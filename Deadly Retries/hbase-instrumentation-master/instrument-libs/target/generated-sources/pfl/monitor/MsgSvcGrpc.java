package pfl.monitor;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.44.0)",
    comments = "Source: MsgSvc.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class MsgSvcGrpc {

  private MsgSvcGrpc() {}

  public static final String SERVICE_NAME = "MsgSvc";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<pfl.monitor.MsgSvcOuterClass.Log,
      com.google.protobuf.Empty> getSendMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Send",
      requestType = pfl.monitor.MsgSvcOuterClass.Log.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pfl.monitor.MsgSvcOuterClass.Log,
      com.google.protobuf.Empty> getSendMethod() {
    io.grpc.MethodDescriptor<pfl.monitor.MsgSvcOuterClass.Log, com.google.protobuf.Empty> getSendMethod;
    if ((getSendMethod = MsgSvcGrpc.getSendMethod) == null) {
      synchronized (MsgSvcGrpc.class) {
        if ((getSendMethod = MsgSvcGrpc.getSendMethod) == null) {
          MsgSvcGrpc.getSendMethod = getSendMethod =
              io.grpc.MethodDescriptor.<pfl.monitor.MsgSvcOuterClass.Log, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Send"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pfl.monitor.MsgSvcOuterClass.Log.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new MsgSvcMethodDescriptorSupplier("Send"))
              .build();
        }
      }
    }
    return getSendMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pfl.monitor.MsgSvcOuterClass.Log4jEvent,
      com.google.protobuf.Empty> getSysFatalLogMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SysFatalLog",
      requestType = pfl.monitor.MsgSvcOuterClass.Log4jEvent.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pfl.monitor.MsgSvcOuterClass.Log4jEvent,
      com.google.protobuf.Empty> getSysFatalLogMethod() {
    io.grpc.MethodDescriptor<pfl.monitor.MsgSvcOuterClass.Log4jEvent, com.google.protobuf.Empty> getSysFatalLogMethod;
    if ((getSysFatalLogMethod = MsgSvcGrpc.getSysFatalLogMethod) == null) {
      synchronized (MsgSvcGrpc.class) {
        if ((getSysFatalLogMethod = MsgSvcGrpc.getSysFatalLogMethod) == null) {
          MsgSvcGrpc.getSysFatalLogMethod = getSysFatalLogMethod =
              io.grpc.MethodDescriptor.<pfl.monitor.MsgSvcOuterClass.Log4jEvent, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SysFatalLog"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pfl.monitor.MsgSvcOuterClass.Log4jEvent.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new MsgSvcMethodDescriptorSupplier("SysFatalLog"))
              .build();
        }
      }
    }
    return getSysFatalLogMethod;
  }

  private static volatile io.grpc.MethodDescriptor<pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo,
      com.google.protobuf.Empty> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Register",
      requestType = pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo,
      com.google.protobuf.Empty> getRegisterMethod() {
    io.grpc.MethodDescriptor<pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo, com.google.protobuf.Empty> getRegisterMethod;
    if ((getRegisterMethod = MsgSvcGrpc.getRegisterMethod) == null) {
      synchronized (MsgSvcGrpc.class) {
        if ((getRegisterMethod = MsgSvcGrpc.getRegisterMethod) == null) {
          MsgSvcGrpc.getRegisterMethod = getRegisterMethod =
              io.grpc.MethodDescriptor.<pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new MsgSvcMethodDescriptorSupplier("Register"))
              .build();
        }
      }
    }
    return getRegisterMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MsgSvcStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MsgSvcStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MsgSvcStub>() {
        @java.lang.Override
        public MsgSvcStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MsgSvcStub(channel, callOptions);
        }
      };
    return MsgSvcStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MsgSvcBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MsgSvcBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MsgSvcBlockingStub>() {
        @java.lang.Override
        public MsgSvcBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MsgSvcBlockingStub(channel, callOptions);
        }
      };
    return MsgSvcBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MsgSvcFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MsgSvcFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MsgSvcFutureStub>() {
        @java.lang.Override
        public MsgSvcFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MsgSvcFutureStub(channel, callOptions);
        }
      };
    return MsgSvcFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class MsgSvcImplBase implements io.grpc.BindableService {

    /**
     */
    public void send(pfl.monitor.MsgSvcOuterClass.Log request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendMethod(), responseObserver);
    }

    /**
     */
    public void sysFatalLog(pfl.monitor.MsgSvcOuterClass.Log4jEvent request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSysFatalLogMethod(), responseObserver);
    }

    /**
     */
    public void register(pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pfl.monitor.MsgSvcOuterClass.Log,
                com.google.protobuf.Empty>(
                  this, METHODID_SEND)))
          .addMethod(
            getSysFatalLogMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pfl.monitor.MsgSvcOuterClass.Log4jEvent,
                com.google.protobuf.Empty>(
                  this, METHODID_SYS_FATAL_LOG)))
          .addMethod(
            getRegisterMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo,
                com.google.protobuf.Empty>(
                  this, METHODID_REGISTER)))
          .build();
    }
  }

  /**
   */
  public static final class MsgSvcStub extends io.grpc.stub.AbstractAsyncStub<MsgSvcStub> {
    private MsgSvcStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MsgSvcStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MsgSvcStub(channel, callOptions);
    }

    /**
     */
    public void send(pfl.monitor.MsgSvcOuterClass.Log request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sysFatalLog(pfl.monitor.MsgSvcOuterClass.Log4jEvent request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSysFatalLogMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void register(pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class MsgSvcBlockingStub extends io.grpc.stub.AbstractBlockingStub<MsgSvcBlockingStub> {
    private MsgSvcBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MsgSvcBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MsgSvcBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty send(pfl.monitor.MsgSvcOuterClass.Log request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty sysFatalLog(pfl.monitor.MsgSvcOuterClass.Log4jEvent request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSysFatalLogMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty register(pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class MsgSvcFutureStub extends io.grpc.stub.AbstractFutureStub<MsgSvcFutureStub> {
    private MsgSvcFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MsgSvcFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MsgSvcFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> send(
        pfl.monitor.MsgSvcOuterClass.Log request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> sysFatalLog(
        pfl.monitor.MsgSvcOuterClass.Log4jEvent request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSysFatalLogMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> register(
        pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND = 0;
  private static final int METHODID_SYS_FATAL_LOG = 1;
  private static final int METHODID_REGISTER = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MsgSvcImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MsgSvcImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND:
          serviceImpl.send((pfl.monitor.MsgSvcOuterClass.Log) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_SYS_FATAL_LOG:
          serviceImpl.sysFatalLog((pfl.monitor.MsgSvcOuterClass.Log4jEvent) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_REGISTER:
          serviceImpl.register((pfl.monitor.MsgSvcOuterClass.ClientServerBindInfo) request,
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

  private static abstract class MsgSvcBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MsgSvcBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pfl.monitor.MsgSvcOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MsgSvc");
    }
  }

  private static final class MsgSvcFileDescriptorSupplier
      extends MsgSvcBaseDescriptorSupplier {
    MsgSvcFileDescriptorSupplier() {}
  }

  private static final class MsgSvcMethodDescriptorSupplier
      extends MsgSvcBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    MsgSvcMethodDescriptorSupplier(String methodName) {
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
      synchronized (MsgSvcGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MsgSvcFileDescriptorSupplier())
              .addMethod(getSendMethod())
              .addMethod(getSysFatalLogMethod())
              .addMethod(getRegisterMethod())
              .build();
        }
      }
    }
    return result;
  }
}
