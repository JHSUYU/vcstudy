package pfl.monitor;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: BlockRpcSvc.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class BlockRpcSvcGrpc {

  private BlockRpcSvcGrpc() {}

  public static final String SERVICE_NAME = "BlockRpcSvc";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pfl.monitor.BlockRpcSvcOuterClass.RPCRequest,
      pfl.monitor.MsgSvcOuterClass.Log> getRequestRpcLogMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestRpcLog",
      requestType = pfl.monitor.BlockRpcSvcOuterClass.RPCRequest.class,
      responseType = pfl.monitor.MsgSvcOuterClass.Log.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pfl.monitor.BlockRpcSvcOuterClass.RPCRequest,
      pfl.monitor.MsgSvcOuterClass.Log> getRequestRpcLogMethod() {
    io.grpc.MethodDescriptor<pfl.monitor.BlockRpcSvcOuterClass.RPCRequest, pfl.monitor.MsgSvcOuterClass.Log> getRequestRpcLogMethod;
    if ((getRequestRpcLogMethod = BlockRpcSvcGrpc.getRequestRpcLogMethod) == null) {
      synchronized (BlockRpcSvcGrpc.class) {
        if ((getRequestRpcLogMethod = BlockRpcSvcGrpc.getRequestRpcLogMethod) == null) {
          BlockRpcSvcGrpc.getRequestRpcLogMethod = getRequestRpcLogMethod =
              io.grpc.MethodDescriptor.<pfl.monitor.BlockRpcSvcOuterClass.RPCRequest, pfl.monitor.MsgSvcOuterClass.Log>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RequestRpcLog"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pfl.monitor.BlockRpcSvcOuterClass.RPCRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pfl.monitor.MsgSvcOuterClass.Log.getDefaultInstance()))
              .setSchemaDescriptor(new BlockRpcSvcMethodDescriptorSupplier("RequestRpcLog"))
              .build();
        }
      }
    }
    return getRequestRpcLogMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock,
      com.google.protobuf.Empty> getBlockRpcMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "BlockRpc",
      requestType = pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock,
      com.google.protobuf.Empty> getBlockRpcMethod() {
    io.grpc.MethodDescriptor<pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock, com.google.protobuf.Empty> getBlockRpcMethod;
    if ((getBlockRpcMethod = BlockRpcSvcGrpc.getBlockRpcMethod) == null) {
      synchronized (BlockRpcSvcGrpc.class) {
        if ((getBlockRpcMethod = BlockRpcSvcGrpc.getBlockRpcMethod) == null) {
          BlockRpcSvcGrpc.getBlockRpcMethod = getBlockRpcMethod =
              io.grpc.MethodDescriptor.<pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "BlockRpc"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new BlockRpcSvcMethodDescriptorSupplier("BlockRpc"))
              .build();
        }
      }
    }
    return getBlockRpcMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BlockRpcSvcStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BlockRpcSvcStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BlockRpcSvcStub>() {
        @java.lang.Override
        public BlockRpcSvcStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BlockRpcSvcStub(channel, callOptions);
        }
      };
    return BlockRpcSvcStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BlockRpcSvcBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BlockRpcSvcBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BlockRpcSvcBlockingStub>() {
        @java.lang.Override
        public BlockRpcSvcBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BlockRpcSvcBlockingStub(channel, callOptions);
        }
      };
    return BlockRpcSvcBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BlockRpcSvcFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BlockRpcSvcFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BlockRpcSvcFutureStub>() {
        @java.lang.Override
        public BlockRpcSvcFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BlockRpcSvcFutureStub(channel, callOptions);
        }
      };
    return BlockRpcSvcFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class BlockRpcSvcImplBase implements io.grpc.BindableService {

    /**
     */
    public void requestRpcLog(pfl.monitor.BlockRpcSvcOuterClass.RPCRequest request,
        io.grpc.stub.StreamObserver<pfl.monitor.MsgSvcOuterClass.Log> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRequestRpcLogMethod(), responseObserver);
    }

    /**
     */
    public void blockRpc(pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBlockRpcMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestRpcLogMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pfl.monitor.BlockRpcSvcOuterClass.RPCRequest,
                pfl.monitor.MsgSvcOuterClass.Log>(
                  this, METHODID_REQUEST_RPC_LOG)))
          .addMethod(
            getBlockRpcMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock,
                com.google.protobuf.Empty>(
                  this, METHODID_BLOCK_RPC)))
          .build();
    }
  }

  /**
   */
  public static final class BlockRpcSvcStub extends io.grpc.stub.AbstractAsyncStub<BlockRpcSvcStub> {
    private BlockRpcSvcStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BlockRpcSvcStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BlockRpcSvcStub(channel, callOptions);
    }

    /**
     */
    public void requestRpcLog(pfl.monitor.BlockRpcSvcOuterClass.RPCRequest request,
        io.grpc.stub.StreamObserver<pfl.monitor.MsgSvcOuterClass.Log> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRequestRpcLogMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void blockRpc(pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBlockRpcMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class BlockRpcSvcBlockingStub extends io.grpc.stub.AbstractBlockingStub<BlockRpcSvcBlockingStub> {
    private BlockRpcSvcBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BlockRpcSvcBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BlockRpcSvcBlockingStub(channel, callOptions);
    }

    /**
     */
    public pfl.monitor.MsgSvcOuterClass.Log requestRpcLog(pfl.monitor.BlockRpcSvcOuterClass.RPCRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRequestRpcLogMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty blockRpc(pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getBlockRpcMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class BlockRpcSvcFutureStub extends io.grpc.stub.AbstractFutureStub<BlockRpcSvcFutureStub> {
    private BlockRpcSvcFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BlockRpcSvcFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BlockRpcSvcFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pfl.monitor.MsgSvcOuterClass.Log> requestRpcLog(
        pfl.monitor.BlockRpcSvcOuterClass.RPCRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRequestRpcLogMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> blockRpc(
        pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBlockRpcMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_RPC_LOG = 0;
  private static final int METHODID_BLOCK_RPC = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BlockRpcSvcImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(BlockRpcSvcImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_RPC_LOG:
          serviceImpl.requestRpcLog((pfl.monitor.BlockRpcSvcOuterClass.RPCRequest) request,
              (io.grpc.stub.StreamObserver<pfl.monitor.MsgSvcOuterClass.Log>) responseObserver);
          break;
        case METHODID_BLOCK_RPC:
          serviceImpl.blockRpc((pfl.monitor.BlockRpcSvcOuterClass.RPCsToBlock) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class BlockRpcSvcBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BlockRpcSvcBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pfl.monitor.BlockRpcSvcOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("BlockRpcSvc");
    }
  }

  private static final class BlockRpcSvcFileDescriptorSupplier
      extends BlockRpcSvcBaseDescriptorSupplier {
    BlockRpcSvcFileDescriptorSupplier() {}
  }

  private static final class BlockRpcSvcMethodDescriptorSupplier
      extends BlockRpcSvcBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    BlockRpcSvcMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (BlockRpcSvcGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BlockRpcSvcFileDescriptorSupplier())
              .addMethod(getRequestRpcLogMethod())
              .addMethod(getBlockRpcMethod())
              .build();
        }
      }
    }
    return result;
  }
}
