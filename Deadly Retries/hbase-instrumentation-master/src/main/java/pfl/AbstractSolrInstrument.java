package pfl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import pfl.signatures.RPCBlockSignature;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
public abstract class AbstractSolrInstrument extends AbstractHBaseInstrument {

    @Around("rpcSend()")
    public Object solrSend_monitor(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        if (TRACE) System.out.println("Solr request intercepted: " + thisJoinPoint);

        Object[] args = thisJoinPoint.getArgs();

        // 使用反射检查第一个参数是否是 SolrRequest
        if (args.length > 0 && isSolrRequest(args[0])) {
            Object solrRequest = args[0];
            String collection = args.length > 1 ? (String) args[1] : null;

            // 构建监控参数
            RpcParams monitorParams = buildSolrRpcParams(solrRequest, collection);

            // 获取请求路径
            String path = getRequestPath(solrRequest);

            // 检查是否应该阻止
            RPCBlockSignature signature = new RPCBlockSignature(path, monitorParams);
            if (RPCBlockSignature.fuzzyContains(signature, blockedRpcSignature)) {
                System.out.println("Blocked Solr request: " + path);
                // 不能直接抛出 SolrServerException，使用反射创建
                throw createSolrException("Request blocked by deadly retry prevention");
            }

            // 记录请求
            recordSolrRequest(solrRequest, collection, monitorParams);
        }

        return thisJoinPoint.proceed();
    }

    private boolean isSolrRequest(Object obj) {
        if (obj == null) return false;
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            if (clazz.getName().equals("org.apache.solr.client.solrj.SolrRequest")) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    private String getRequestPath(Object solrRequest) {
        try {
            Method getPath = solrRequest.getClass().getMethod("getPath");
            Object path = getPath.invoke(solrRequest);
            return path != null ? path.toString() : "/select";
        } catch (Exception e) {
            if (TRACE) e.printStackTrace();
            return "/unknown";
        }
    }

    private RpcParams buildSolrRpcParams(Object request, String collection) throws Exception {
        RpcParams.Builder builder = RpcParams.newBuilder();

        // 检查是否是 UpdateRequest
        if (isUpdateRequest(request)) {
            handleUpdateRequest(request, builder);
        } else {
            // 处理查询请求
            handleQueryRequest(request, builder);
        }

        // 添加 collection 信息
        if (collection != null) {
            builder.putRemainingParams("collection", collection);
        }

        return builder.build();
    }

    private boolean isUpdateRequest(Object request) {
        return request.getClass().getName().contains("UpdateRequest");
    }

    private void handleUpdateRequest(Object updateReq, RpcParams.Builder builder) throws Exception {
        try {
            // 尝试获取文档列表
            Method getDocuments = updateReq.getClass().getMethod("getDocuments");
            Object docs = getDocuments.invoke(updateReq);

            if (docs instanceof List) {
                List<?> docList = (List<?>) docs;
                if (docList.size() > 1) {
                    // 批量请求
                    builder.setBatchedCallDepth(1);
                    RepeatedParam.Builder rpBuilder = RepeatedParam.newBuilder();

                    // 只记录文档数量和大小信息，避免记录完整内容
                    for (Object doc : docList) {
                        String docInfo = "doc_size:" + estimateObjectSize(doc);
                        rpBuilder.addParams(docInfo);
                    }
                    builder.putBatchedParams("docs", rpBuilder.build());
                } else if (docList.size() == 1) {
                    // 单个文档
                    builder.setBatchedCallDepth(0);
                    builder.setNonBatchParam("single_doc_size:" + estimateObjectSize(docList.get(0)));
                }
            }
        } catch (Exception e) {
            // 如果反射失败，记录基本信息
            builder.setBatchedCallDepth(0);
            builder.setNonBatchParam("update_request:" + request.getClass().getSimpleName());
        }
    }

    private void handleQueryRequest(Object request, RpcParams.Builder builder) throws Exception {
        builder.setBatchedCallDepth(0);

        try {
            // 获取查询参数
            Method getParams = request.getClass().getMethod("getParams");
            Object params = getParams.invoke(request);

            if (params != null) {
                // 获取查询字符串
                Method toQueryString = params.getClass().getMethod("toQueryString");
                String queryString = (String) toQueryString.invoke(params);
                builder.setNonBatchParam(queryString);
            }
        } catch (Exception e) {
            builder.setNonBatchParam("query_request:" + request.getClass().getSimpleName());
        }
    }

    private int estimateObjectSize(Object obj) {
        // 简单估算，使用 toString 的长度
        return obj.toString().length();
    }

    private void recordSolrRequest(Object solrRequest, String collection, RpcParams params) {
        // 构建 CallLog
        CallLog.Builder logBuilder = CallLog.newBuilder();
        logBuilder.setType(CallLog.Type.RPC_SEND);
        logBuilder.setNodeId(nodeId.toString());

        // 设置时间戳
        Instant time = Instant.now();
        logBuilder.setTimestamp(Timestamp.newBuilder()
                .setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build());

        // 构建 RPC 属性
        RPCMessageProperties.Builder rpcPropBuilder = RPCMessageProperties.newBuilder();
        rpcPropBuilder.setDirection(RPCMessageProperties.Direction.SEND);
        rpcPropBuilder.setMethod(getRequestPath(solrRequest));
        rpcPropBuilder.setParam(params);

        // 获取目标地址（如果可能）
        String targetUrl = getTargetUrl(solrRequest);
        if (targetUrl != null) {
            rpcPropBuilder.setTo(targetUrl);
        }

        logBuilder.setRpcProperty(rpcPropBuilder.build());
        logQueue.offer(logBuilder.build());
    }

    private String getTargetUrl(Object solrRequest) {
        try {
            Method getBasePath = solrRequest.getClass().getMethod("getBasePath");
            Object basePath = getBasePath.invoke(solrRequest);
            return basePath != null ? basePath.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Exception createSolrException(String message) {
        try {
            Class<?> exceptionClass = Class.forName("org.apache.solr.client.solrj.SolrServerException");
            return (Exception) exceptionClass.getConstructor(String.class).newInstance(message);
        } catch (Exception e) {
            // 如果无法创建 SolrServerException，返回普通异常
            return new RuntimeException(message);
        }
    }
}