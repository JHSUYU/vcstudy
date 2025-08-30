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
public abstract class AbstractCassandraInstrument extends AbstractHBaseInstrument {

    public AbstractCassandraInstrument() throws Exception {
        super();
    }

    @Pointcut
    public abstract void rpcSend();

    @Pointcut
    public abstract void rpcReceive();

    @Around("rpcSend()")
    public void rpcSend_monitor(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        if (TRACE) System.out.println(thisJoinPoint);

        Object[] args = thisJoinPoint.getArgs();
        if (args.length < 2) {
            thisJoinPoint.proceed();
            return;
        }

        Object message = args[0];
        Object to = args[1];

        // Extract message header and payload
        Object header = Utils.getObjectField(message, "header");
        if (header == null) {
            thisJoinPoint.proceed();
            return;
        }

        // Get message details
        Long id = (Long) Utils.getObjectField(header, "id");
        Object verb = Utils.getObjectField(header, "verb");
        Object payload = Utils.getObjectField(message, "payload");

        String verbName = verb != null ? verb.toString() : "UNKNOWN";
        RpcParams monitorParams = buildCassandraRpcParams(verbName, payload);

        // Check if should block
        RPCBlockSignature signature = new RPCBlockSignature(verbName, monitorParams);
        if (RPCBlockSignature.fuzzyContains(signature, blockedRpcSignature)) {
            System.out.println("Blocked Cassandra RPC: " + verbName);
            return; // Block the message
        }

        // Record the message
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
        rpcPropBuilder.setId(id != null ? id.toString() : "0");
        rpcPropBuilder.setMethod(verbName);
        rpcPropBuilder.setParam(monitorParams);

        // Set addresses
        String fromAddr = getLocalAddress();
        String toAddr = extractAddress(to);
        rpcPropBuilder.setFrom(fromAddr);
        rpcPropBuilder.setTo(toAddr);

        logBuilder.setRpcProperty(rpcPropBuilder.build());
        logQueue.offer(logBuilder.build());

        thisJoinPoint.proceed();
    }

    @Around("rpcReceive()")
    public Object rpcReceive_monitor(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        if (TRACE) System.out.println(thisJoinPoint);

        Object[] args = thisJoinPoint.getArgs();
        if (args.length < 1) {
            return thisJoinPoint.proceed();
        }

        Object message = args[0];
        Object header = Utils.getObjectField(message, "header");
        if (header == null) {
            return thisJoinPoint.proceed();
        }

        // Get message details
        Long id = (Long) Utils.getObjectField(header, "id");
        Object verb = Utils.getObjectField(header, "verb");
        Object from = Utils.getObjectField(header, "from");
        Object payload = Utils.getObjectField(message, "payload");

        String verbName = verb != null ? verb.toString() : "UNKNOWN";

        // Record the message

        CallLog.Builder logBuilder = CallLog.newBuilder();
        logBuilder.setType(CallLog.Type.RPC_RECEIVE);
        logBuilder.setNodeId(nodeId.toString());
        logBuilder.setTid(Thread.currentThread().getId());

        Instant time = Instant.now();
        logBuilder.setTimestamp(pfl.shaded.com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build());

        RPCMessageProperties.Builder rpcPropBuilder = RPCMessageProperties.newBuilder();
        rpcPropBuilder.setDirection(RPCMessageProperties.Direction.RECEIVE);
        rpcPropBuilder.setId(id != null ? id.toString() : "0");
        rpcPropBuilder.setMethod(verbName);
        rpcPropBuilder.setParam(buildCassandraRpcParams(verbName, payload));

        // Set addresses
        String fromAddr = extractAddress(from);
        String toAddr = getLocalAddress();
        rpcPropBuilder.setFrom(fromAddr);
        rpcPropBuilder.setTo(toAddr);

        logBuilder.setRpcProperty(rpcPropBuilder.build());
        logQueue.offer(logBuilder.build());

        return thisJoinPoint.proceed();
    }

    private RpcParams buildCassandraRpcParams(String verbName, Object payload) {
        RpcParams.Builder builder = RpcParams.newBuilder();
        builder.setBatchedCallDepth(0);

        if (payload != null) {
            String payloadClass = payload.getClass().getSimpleName();
            builder.setNonBatchParam(payloadClass);

            // Check if it's a batch operation
            if (payloadClass.contains("Batch") || verbName.contains("BATCH")) {
                builder.setBatchedCallDepth(1);
            }
        } else {
            builder.setNonBatchParam("NoPayload");
        }

        return builder.build();
    }

    private String getLocalAddress() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress() + ":7000";
        } catch (Exception e) {
            return "localhost:7000";
        }
    }

    private String extractAddress(Object endpoint) {
        if (endpoint == null) {
            return "unknown:7000";
        }

        try {
            // Try to get address field (InetAddressAndPort)
            Object address = Utils.getObjectField(endpoint, "address");
            Integer port = (Integer) Utils.getObjectField(endpoint, "port");

            if (address != null) {
                String addr = address.toString();
                if (port != null) {
                    return addr + ":" + port;
                }
                return addr + ":7000";
            }
        } catch (Exception e) {
            // Fall through
        }

        // Fallback to toString
        String str = endpoint.toString();
        if (!str.contains(":")) {
            str = str + ":7000";
        }
        return str;
    }
}