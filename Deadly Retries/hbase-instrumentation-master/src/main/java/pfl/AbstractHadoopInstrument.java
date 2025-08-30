package pfl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Optional;

import pfl.monitor.MsgSvcOuterClass.CallLog;
import pfl.monitor.MsgSvcOuterClass.RPCMessageProperties;
import pfl.monitor.RpcParamsOuterClass.RpcParams;
import pfl.signatures.RPCBlockSignature;

@Aspect
public abstract class AbstractHadoopInstrument extends AbstractHBaseInstrument {

    public AbstractHadoopInstrument() throws Exception {
        super();
        System.out.println("### Hadoop 3.0 Instrumentation Loaded");
    }

    @Override
    @Around("rpcSend()")
    public void rpcSend_monitor(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        if (TRACE) System.out.println(thisJoinPoint);

        Optional<CallLog.Builder> optionalBuilder = getLogBuilderForFuncCall.apply(thisJoinPoint);
        if (!optionalBuilder.isPresent()) {
            thisJoinPoint.proceed();
            return;
        }

        CallLog.Builder logBuilder = optionalBuilder.get();
        logBuilder.setType(CallLog.Type.RPC_SEND);

        // Client.Call object
        Object callObj = thisJoinPoint.getArgs()[0];
        if (callObj == null) {
            thisJoinPoint.proceed();
            return;
        }

        // Get connection from the context (Client.Connection)
        Object connectionObj = thisJoinPoint.getThis();

        String fromIP = "localhost:0";
        String toIP = "unknown:0";

        // Get server address from Connection
        InetSocketAddress server = (InetSocketAddress) Utils.getObjectField(connectionObj, "server");
        if (server != null) {
            toIP = server.getAddress().getHostAddress() + ":" + server.getPort();
        }

        // Get socket for local address
        Socket socket = (Socket) Utils.getObjectField(connectionObj, "socket");
        if (socket != null && socket.isConnected()) {
            fromIP = socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort();
            if (server == null) {
                toIP = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
            }
        }

        // Get RPC details from Call
        Integer callId = (Integer) Utils.getObjectField(callObj, "id");
        Integer retry = (Integer) Utils.getObjectField(callObj, "retry");
        Object rpcRequest = Utils.getObjectField(callObj, "rpcRequest");
        Object rpcKind = Utils.getObjectField(callObj, "rpcKind");

        String methodName = extractMethodName(rpcRequest);
        RpcParams monitorParams = buildHadoopRpcParams(rpcRequest, rpcKind);

        // Build RPC properties
        RPCMessageProperties.Builder rpcPropBuilder = RPCMessageProperties.newBuilder();
        rpcPropBuilder.setDirection(RPCMessageProperties.Direction.SEND);
        rpcPropBuilder.setId(callId != null ? callId.toString() : "0");
        rpcPropBuilder.setFrom(fromIP);
        rpcPropBuilder.setTo(toIP);
        rpcPropBuilder.setMethod(methodName);
        rpcPropBuilder.setParam(monitorParams);

        logBuilder.setRpcProperty(rpcPropBuilder.build());
        logQueue.offer(logBuilder.build());

        // Check for blocked calls
        RPCBlockSignature signature = new RPCBlockSignature(methodName, monitorParams);
        if (RPCBlockSignature.fuzzyContains(signature, blockedRpcSignature)) {
            System.out.println("Blocked Hadoop RPC: " + methodName);
            return; // Don't proceed
        }

        thisJoinPoint.proceed();
    }

    @Override
    @Before("rpcReceive()")
    public void rpcReceive_monitor(JoinPoint thisJoinPoint) throws Exception {
        if ((firstRecvSent) && (!useLocalLog)) return;
        if (TRACE) System.out.println(thisJoinPoint);

        Optional<CallLog.Builder> optionalBuilder = getLogBuilderForFuncCall.apply(thisJoinPoint);
        if (!optionalBuilder.isPresent()) {
            return;
        }

        CallLog.Builder logBuilder = optionalBuilder.get();
        logBuilder.setType(CallLog.Type.RPC_RECEIVE);

        // Server.RpcCall constructor arguments
        Object[] args = thisJoinPoint.getArgs();

        // RpcCall constructor signature:
        // RpcCall(Connection connection, int id, int retryCount,
        //         Writable param, RPC.RpcKind kind, byte[] clientId,
        //         TraceScope traceScope, CallerContext context)

        // Connection is the first argument (after outer class reference if nested)
        Object connectionObj = args[0 + instConfig.RECV_ARG_OFFSET];
        Integer callId = (Integer) args[1 + instConfig.RECV_ARG_OFFSET];
        Integer retryCount = (Integer) args[2 + instConfig.RECV_ARG_OFFSET];
        Object rpcRequest = args[3 + instConfig.RECV_ARG_OFFSET];
        Object rpcKind = args[4 + instConfig.RECV_ARG_OFFSET];

        String fromIP = "unknown:0";
        String toIP = "localhost:0";

        // Extract addresses from connection
        if (connectionObj != null) {
            Socket socket = (Socket) Utils.getObjectField(connectionObj, "socket");
            if (socket != null) {
                fromIP = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                toIP = socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort();
            }
        }

        String methodName = extractMethodName(rpcRequest);
        RpcParams monitorParams = buildHadoopRpcParams(rpcRequest, rpcKind);

        // Build RPC properties
        RPCMessageProperties.Builder rpcPropBuilder = RPCMessageProperties.newBuilder();
        rpcPropBuilder.setDirection(RPCMessageProperties.Direction.RECEIVE);
        rpcPropBuilder.setId(callId != null ? callId.toString() : "0");
        rpcPropBuilder.setFrom(fromIP);
        rpcPropBuilder.setTo(toIP);
        rpcPropBuilder.setMethod(methodName);
        rpcPropBuilder.setParam(monitorParams);

        logBuilder.setRpcProperty(rpcPropBuilder.build());

        if ((!firstRecvSent) && (!useLocalLog)) {
            pfl.monitor.MsgSvcOuterClass.Log.Builder builder = pfl.monitor.MsgSvcOuterClass.Log.newBuilder();
            builder.setNodeId(nodeId.toString());
            builder.addCalls(logBuilder.build());
            logStub.send(builder.build());
            firstRecvSent = true;
        } else {
            logQueue.offer(logBuilder.build());
        }
    }

    private String extractMethodName(Object rpcRequest) {
        if (rpcRequest == null) {
            return "UNKNOWN";
        }

        // For ProtoBuf-based RPC
        String className = rpcRequest.getClass().getSimpleName();

        // Try to extract method name from the request
        try {
            // Many Hadoop RPC requests have getMethodName()
            java.lang.reflect.Method getMethodName = rpcRequest.getClass().getMethod("getMethodName");
            String methodName = (String) getMethodName.invoke(rpcRequest);
            if (methodName != null) return methodName;
        } catch (Exception e) {
            // Not all requests have this method
        }

        // Fallback to class name
        return className.replace("RequestProto", "");
    }

    private RpcParams buildHadoopRpcParams(Object rpcRequest, Object rpcKind) {
        RpcParams.Builder builder = RpcParams.newBuilder();
        builder.setBatchedCallDepth(0);

        if (rpcRequest == null) {
            builder.setNonBatchParam("null");
            return builder.build();
        }

        String requestClass = rpcRequest.getClass().getSimpleName();
        builder.setNonBatchParam(requestClass);

        // Add RPC kind
        if (rpcKind != null) {
            builder.putRemainingParams("rpcKind", rpcKind.toString());
        }

        // Try to determine service type based on request class name
        if (requestClass.contains("Block") || requestClass.contains("Datanode") ||
                requestClass.contains("Namenode")) {
            builder.putRemainingParams("service", "HDFS");
        } else if (requestClass.contains("Container") || requestClass.contains("Application") ||
                requestClass.contains("Resource")) {
            builder.putRemainingParams("service", "YARN");
        } else if (requestClass.contains("Job") || requestClass.contains("Task")) {
            builder.putRemainingParams("service", "MapReduce");
        }

        return builder.build();
    }
}
