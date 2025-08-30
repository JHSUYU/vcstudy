package pfl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import pfl.monitor.MsgSvcOuterClass.CallLog;
import pfl.monitor.MsgSvcOuterClass.RPCMessageProperties;
import pfl.monitor.RpcParamsOuterClass.RpcParams;
import pfl.signatures.RPCBlockSignature;

import java.time.Instant;
import java.util.Optional;

@Aspect
public abstract class AbstractSolrInstrument extends AbstractHBaseInstrument {

    public AbstractSolrInstrument() throws Exception {
        super();
        System.out.println("### Solr Instrumentation Loaded");
    }

    // Override the abstract methods from parent class to handle HTTP differently
    @Override
    @Pointcut
    public abstract void rpcSend();

    @Override
    @Around("rpcSend()")
    public void rpcSend_monitor(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        // Override with Solr-specific implementation
        solrRequest_monitor(thisJoinPoint);
    }

    @Pointcut
    public abstract void rpcReceive_processReq();

    @Override
    @Pointcut
    public abstract void rpcReceive();

    @Override
    @Before("rpcReceive()")
    public void rpcReceive_monitor(JoinPoint thisJoinPoint) throws Exception {
        // Override with Solr-specific implementation
        solrReceive_monitor(thisJoinPoint);
    }

    // Solr-specific HTTP request handling
    @Around("rpcSend()")
    public Object solrRequest_monitor(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        if (TRACE) System.out.println("Solr request intercepted: " + thisJoinPoint);

        Optional<CallLog.Builder> optionalBuilder = getLogBuilderForFuncCall.apply(thisJoinPoint);
        if (!optionalBuilder.isPresent()) {
            return thisJoinPoint.proceed();
        }

        CallLog.Builder logBuilder = optionalBuilder.get();
        logBuilder.setType(CallLog.Type.RPC_SEND);

        Object[] args = thisJoinPoint.getArgs();
        if (args.length >= 1 && args[0] != null) {
            Object solrRequest = args[0];

            // Extract collection name
            String collection = extractCollection(args);

            // Extract request details
            String path = getRequestPath(solrRequest);
            String method = getRequestMethod(solrRequest);
            RpcParams monitorParams = buildSolrRpcParams(solrRequest, collection, method);

            // Get client instance for server URL
            Object httpClient = thisJoinPoint.getThis();
            String serverUrl = extractServerUrl(httpClient);

            // Build RPC properties
            RPCMessageProperties.Builder rpcPropBuilder = RPCMessageProperties.newBuilder();
            rpcPropBuilder.setDirection(RPCMessageProperties.Direction.SEND);
            rpcPropBuilder.setId(String.valueOf(System.nanoTime()));
            rpcPropBuilder.setFrom(getLocalAddress());
            rpcPropBuilder.setTo(serverUrl);
            rpcPropBuilder.setMethod(method + " " + path);
            rpcPropBuilder.setParam(monitorParams);

            logBuilder.setRpcProperty(rpcPropBuilder.build());
            logQueue.offer(logBuilder.build());

            // Check if should block
            RPCBlockSignature signature = new RPCBlockSignature(path, monitorParams);
            if (RPCBlockSignature.fuzzyContains(signature, blockedRpcSignature)) {
                System.out.println("Blocked Solr request: " + path);
                throw new RuntimeException("Request blocked by deadly retry prevention");
            }
        }

        return thisJoinPoint.proceed();
    }

    // Handle Solr server receiving requests
    public void solrReceive_monitor(JoinPoint thisJoinPoint) throws Exception {
        if ((firstRecvSent) && (!useLocalLog)) return;
        if (TRACE) System.out.println("Solr receive: " + thisJoinPoint);

        Optional<CallLog.Builder> optionalBuilder = getLogBuilderForFuncCall.apply(thisJoinPoint);
        if (!optionalBuilder.isPresent()) {
            return;
        }

        CallLog.Builder logBuilder = optionalBuilder.get();
        logBuilder.setType(CallLog.Type.RPC_RECEIVE);

        // Extract HTTP request details from SolrQueryRequest
        Object solrRequest = thisJoinPoint.getArgs()[0];
        Object solrResponse = thisJoinPoint.getArgs()[1];

        if (solrRequest != null) {
            String path = extractPath(solrRequest);
            String method = extractHttpMethod(solrRequest);
            String params = extractParams(solrRequest);

            // Get client/server addresses
            String fromIP = extractClientAddress(solrRequest);
            String toIP = getLocalAddress();

            // Build RPC properties
            RPCMessageProperties.Builder rpcPropBuilder = RPCMessageProperties.newBuilder();
            rpcPropBuilder.setDirection(RPCMessageProperties.Direction.RECEIVE);
            rpcPropBuilder.setId(String.valueOf(System.nanoTime()));
            rpcPropBuilder.setFrom(fromIP);
            rpcPropBuilder.setTo(toIP);
            rpcPropBuilder.setMethod(method + " " + path);

            RpcParams.Builder paramBuilder = RpcParams.newBuilder();
            paramBuilder.setBatchedCallDepth(0);
            paramBuilder.setNonBatchParam(params);
            rpcPropBuilder.setParam(paramBuilder.build());

            logBuilder.setRpcProperty(rpcPropBuilder.build());

            if ((!firstRecvSent) && (!useLocalLog)) {
                pfl.monitor.MsgSvcOuterClass.Log.Builder builder =
                        pfl.monitor.MsgSvcOuterClass.Log.newBuilder();
                builder.setNodeId(nodeId.toString());
                builder.addCalls(logBuilder.build());
                logStub.send(builder.build());
                firstRecvSent = true;
            } else {
                logQueue.offer(logBuilder.build());
            }
        }
    }

    // Helper methods for Solr
    private String extractCollection(Object[] args) {
        if (args.length == 2 && args[1] instanceof String) {
            return (String) args[1];
        } else if (args.length == 3 && args[2] instanceof String) {
            return (String) args[2];
        }
        return null;
    }

    private String getRequestPath(Object solrRequest) {
        try {
            java.lang.reflect.Method getPath = solrRequest.getClass().getMethod("getPath");
            Object path = getPath.invoke(solrRequest);
            return path != null ? path.toString() : "/select";
        } catch (Exception e) {
            return "/select";
        }
    }

    private String getRequestMethod(Object solrRequest) {
        try {
            java.lang.reflect.Method getMethod = solrRequest.getClass().getMethod("getMethod");
            Object method = getMethod.invoke(solrRequest);
            return method != null ? method.toString() : "GET";
        } catch (Exception e) {
            // Default based on request type
            String className = solrRequest.getClass().getSimpleName();
            if (className.contains("Update")) return "POST";
            return "GET";
        }
    }

    private RpcParams buildSolrRpcParams(Object request, String collection, String method) {
        RpcParams.Builder builder = RpcParams.newBuilder();
        builder.setBatchedCallDepth(0);

        try {
            // Get query parameters
            java.lang.reflect.Method getParams = request.getClass().getMethod("getParams");
            Object params = getParams.invoke(request);

            if (params != null) {
                java.lang.reflect.Method toQueryString = params.getClass().getMethod("toQueryString");
                String queryString = (String) toQueryString.invoke(params);
                builder.setNonBatchParam(queryString != null ? queryString : "");
            }
        } catch (Exception e) {
            builder.setNonBatchParam("request:" + request.getClass().getSimpleName());
        }

        if (collection != null) {
            builder.putRemainingParams("collection", collection);
        }
        builder.putRemainingParams("method", method);

        return builder.build();
    }

    private String extractServerUrl(Object httpClient) {
        if (httpClient == null) return "unknown:0";

        try {
            java.lang.reflect.Method getBaseURL = httpClient.getClass().getMethod("getBaseURL");
            String baseUrl = (String) getBaseURL.invoke(httpClient);
            if (baseUrl != null) {
                java.net.URL url = new java.net.URL(baseUrl);
                return url.getHost() + ":" + (url.getPort() > 0 ? url.getPort() : url.getDefaultPort());
            }
        } catch (Exception e) {
            if (TRACE) e.printStackTrace();
        }
        return "unknown:0";
    }

    private String getLocalAddress() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress() + ":8983";
        } catch (Exception e) {
            return "localhost:8983";
        }
    }

    private String extractPath(Object solrRequest) {
        try {
            java.lang.reflect.Method getPath = solrRequest.getClass().getMethod("getPath");
            return (String) getPath.invoke(solrRequest);
        } catch (Exception e) {
            return "/";
        }
    }

    private String extractHttpMethod(Object solrRequest) {
        try {
            java.lang.reflect.Method getContext = solrRequest.getClass().getMethod("getContext");
            Object context = getContext.invoke(solrRequest);
            if (context instanceof java.util.Map) {
                Object method = ((java.util.Map) context).get("httpMethod");
                if (method != null) return method.toString();
            }
        } catch (Exception e) {
            // Continue to default
        }
        return "GET";
    }

    private String extractParams(Object solrRequest) {
        try {
            java.lang.reflect.Method getParams = solrRequest.getClass().getMethod("getParams");
            Object params = getParams.invoke(solrRequest);
            if (params != null) {
                return params.toString();
            }
        } catch (Exception e) {
            // Continue
        }
        return "";
    }

    private String extractClientAddress(Object solrRequest) {
        try {
            java.lang.reflect.Method getHttpServletRequest =
                    solrRequest.getClass().getMethod("getHttpServletRequest");
            Object httpRequest = getHttpServletRequest.invoke(solrRequest);
            if (httpRequest != null) {
                java.lang.reflect.Method getRemoteAddr =
                        httpRequest.getClass().getMethod("getRemoteAddr");
                java.lang.reflect.Method getRemotePort =
                        httpRequest.getClass().getMethod("getRemotePort");
                String addr = (String) getRemoteAddr.invoke(httpRequest);
                int port = (int) getRemotePort.invoke(httpRequest);
                return addr + ":" + port;
            }
        } catch (Exception e) {
            // Continue to default
        }
        return "unknown:0";
    }
}