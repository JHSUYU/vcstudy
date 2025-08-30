package pfl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import pfl.monitor.MsgSvcOuterClass.CallLog;
import pfl.monitor.MsgSvcOuterClass.RPCMessageProperties;
import pfl.monitor.RpcParamsOuterClass.RpcParams;
import pfl.signatures.RPCBlockSignature;

import java.time.Instant;

@Aspect
public abstract class AbstractSolrInstrument extends AbstractHBaseInstrument {

    public AbstractSolrInstrument() throws Exception {
        super();
    }

    @Pointcut
    public abstract void rpcSend();

    @Around("rpcSend()")
    public Object solrSend_monitor(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        if (TRACE) System.out.println("Solr request intercepted: " + thisJoinPoint);

        Object[] args = thisJoinPoint.getArgs();

        if (args.length >= 1 && args[0] != null) {
            Object solrRequest = args[0];

            // 根据参数数量和类型确定 collection
            String collection = null;
            Object responseParser = null;

            if (args.length == 2) {
                // request(SolrRequest, String) 或 request(SolrRequest, ResponseParser)
                if (args[1] instanceof String) {
                    collection = (String) args[1];
                } else {
                    responseParser = args[1];
                }
            } else if (args.length == 3) {
                // request(SolrRequest, ResponseParser, String)
                responseParser = args[1];
                collection = (String) args[2];
            }

            String path = getRequestPath(solrRequest);
            RpcParams monitorParams = buildSolrRpcParams(solrRequest, collection);

            // 检查是否应该阻止
            RPCBlockSignature signature = new RPCBlockSignature(path, monitorParams);
            if (RPCBlockSignature.fuzzyContains(signature, blockedRpcSignature)) {
                System.out.println("Blocked Solr request: " + path);
                throw new RuntimeException("Request blocked by deadly retry prevention");
            }

            // 记录请求 - thisJoinPoint.getThis() 是 HttpSolrClient 实例
            recordSolrRequest(solrRequest, collection, monitorParams, thisJoinPoint.getThis());
        }

        return thisJoinPoint.proceed();
    }

    private String getRequestPath(Object solrRequest) {
        try {
            java.lang.reflect.Method getPath = solrRequest.getClass().getMethod("getPath");
            Object path = getPath.invoke(solrRequest);
            return path != null ? path.toString() : "/select";
        } catch (Exception e) {
            if (TRACE) e.printStackTrace();
            return "/select";
        }
    }

    private RpcParams buildSolrRpcParams(Object request, String collection) {
        RpcParams.Builder builder = RpcParams.newBuilder();

        try {
            java.lang.reflect.Method getParams = request.getClass().getMethod("getParams");
            Object params = getParams.invoke(request);

            if (params != null) {
                java.lang.reflect.Method toQueryString = params.getClass().getMethod("toQueryString");
                String queryString = (String) toQueryString.invoke(params);
                builder.setBatchedCallDepth(0);
                builder.setNonBatchParam(queryString != null ? queryString : "");
            }
        } catch (Exception e) {
            builder.setBatchedCallDepth(0);
            builder.setNonBatchParam("request:" + request.getClass().getSimpleName());
        }

        if (collection != null) {
            builder.putRemainingParams("collection", collection);
        }

        return builder.build();
    }

    private void recordSolrRequest(Object solrRequest, String collection, RpcParams params, Object httpClient) {
        CallLog.Builder logBuilder = CallLog.newBuilder();
        logBuilder.setType(CallLog.Type.RPC_SEND);
        logBuilder.setNodeId(nodeId.toString());
        logBuilder.setTid(Thread.currentThread().getId());

        Instant time = Instant.now();
        logBuilder.setTimestamp(pfl.shaded.com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build());

        RPCMessageProperties.Builder rpcPropBuilder = RPCMessageProperties.newBuilder();
        rpcPropBuilder.setDirection(RPCMessageProperties.Direction.SEND);
        rpcPropBuilder.setMethod(getRequestPath(solrRequest));
        rpcPropBuilder.setParam(params);

        // 设置唯一 ID
        String requestId = String.valueOf(System.nanoTime());
        rpcPropBuilder.setId(requestId);

        // 设置 from 地址
        try {
            String fromIP = java.net.InetAddress.getLocalHost().getHostAddress() + ":0";
            rpcPropBuilder.setFrom(fromIP);
        } catch (Exception e) {
            rpcPropBuilder.setFrom("localhost:0");
        }

        // 设置 to 地址 - 从 HttpSolrClient 获取 baseUrl
        String toAddr = "unknown:0";
        if (httpClient != null) {
            try {
                java.lang.reflect.Method getBaseURL = httpClient.getClass().getMethod("getBaseURL");
                String baseUrl = (String) getBaseURL.invoke(httpClient);
                if (baseUrl != null) {
                    java.net.URL url = new java.net.URL(baseUrl);
                    toAddr = url.getHost() + ":" + (url.getPort() > 0 ? url.getPort() : url.getDefaultPort());
                }
            } catch (Exception e) {
                if (TRACE) e.printStackTrace();
            }
        }
        rpcPropBuilder.setTo(toAddr);

        logBuilder.setRpcProperty(rpcPropBuilder.build());
        logQueue.offer(logBuilder.build());
    }
}