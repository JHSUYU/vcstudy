package pfl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import pfl.monitor.RpcParamsOuterClass.RpcParams;
import pfl.monitor.RpcParamsOuterClass.RepeatedParam;
import pfl.signatures.RPCBlockSignature;

import java.time.Instant;

@Aspect
public abstract class AbstractSolrInstrument extends AbstractHBaseInstrument {

    @Pointcut
    public abstract void rpcSend();

    @Around("rpcSend()")
    public Object solrSend_monitor(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        if (TRACE) System.out.println("Solr request intercepted: " + thisJoinPoint);

        Object[] args = thisJoinPoint.getArgs();

        // First arg is SolrRequest, second is collection name
        if (args.length >= 1) {
            Object solrRequest = args[0];
            String collection = args.length > 1 ? (String) args[1] : null;

            // Get the path from the request
            String path = getRequestPath(solrRequest);

            // Build monitoring parameters
            RpcParams monitorParams = buildSolrRpcParams(solrRequest, collection);

            // Check if should block
            RPCBlockSignature signature = new RPCBlockSignature(path, monitorParams);
            if (RPCBlockSignature.fuzzyContains(signature, blockedRpcSignature)) {
                System.out.println("Blocked Solr request: " + path);
                throw new RuntimeException("Request blocked by deadly retry prevention");
            }

            // Record the request
            recordSolrRequest(solrRequest, collection, monitorParams, thisJoinPoint.getTarget());
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

    private RpcParams buildSolrRpcParams(Object request, String collection) throws Exception {
        RpcParams.Builder builder = RpcParams.newBuilder();

        // Get request parameters
        try {
            java.lang.reflect.Method getParams = request.getClass().getMethod("getParams");
            Object params = getParams.invoke(request);

            if (params != null) {
                // Convert SolrParams to string representation
                java.lang.reflect.Method toQueryString = params.getClass().getMethod("toQueryString");
                String queryString = (String) toQueryString.invoke(params);
                builder.setBatchedCallDepth(0);
                builder.setNonBatchParam(queryString);
            }
        } catch (Exception e) {
            builder.setBatchedCallDepth(0);
            builder.setNonBatchParam("request:" + request.getClass().getSimpleName());
        }

        // Add collection info if present
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

        // Set timestamp
        Instant time = Instant.now();
        logBuilder.setTimestamp(pfl.shaded.com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build());

        // Build RPC properties
        RPCMessageProperties.Builder rpcPropBuilder = RPCMessageProperties.newBuilder();
        rpcPropBuilder.setDirection(RPCMessageProperties.Direction.SEND);
        rpcPropBuilder.setMethod(getRequestPath(solrRequest));
        rpcPropBuilder.setParam(params);

        // Try to get the base URL from HttpSolrClient
        if (httpClient != null) {
            try {
                java.lang.reflect.Method getBaseURL = httpClient.getClass().getMethod("getBaseURL");
                String baseUrl = (String) getBaseURL.invoke(httpClient);
                if (baseUrl != null) {
                    rpcPropBuilder.setTo(baseUrl);
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        logBuilder.setRpcProperty(rpcPropBuilder.build());
        logQueue.offer(logBuilder.build());
    }
}