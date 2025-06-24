package com.carelink.grpc.medicalhistory;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service for managing medical history records
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class MedicalHistoryServiceGrpc {

  private MedicalHistoryServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "carelink.medicalhistory.MedicalHistoryService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.carelink.grpc.medicalhistory.AddMedicalRecordRequest,
      com.carelink.grpc.medicalhistory.AddMedicalRecordResponse> getAddMedicalRecordMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AddMedicalRecord",
      requestType = com.carelink.grpc.medicalhistory.AddMedicalRecordRequest.class,
      responseType = com.carelink.grpc.medicalhistory.AddMedicalRecordResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.carelink.grpc.medicalhistory.AddMedicalRecordRequest,
      com.carelink.grpc.medicalhistory.AddMedicalRecordResponse> getAddMedicalRecordMethod() {
    io.grpc.MethodDescriptor<com.carelink.grpc.medicalhistory.AddMedicalRecordRequest, com.carelink.grpc.medicalhistory.AddMedicalRecordResponse> getAddMedicalRecordMethod;
    if ((getAddMedicalRecordMethod = MedicalHistoryServiceGrpc.getAddMedicalRecordMethod) == null) {
      synchronized (MedicalHistoryServiceGrpc.class) {
        if ((getAddMedicalRecordMethod = MedicalHistoryServiceGrpc.getAddMedicalRecordMethod) == null) {
          MedicalHistoryServiceGrpc.getAddMedicalRecordMethod = getAddMedicalRecordMethod =
              io.grpc.MethodDescriptor.<com.carelink.grpc.medicalhistory.AddMedicalRecordRequest, com.carelink.grpc.medicalhistory.AddMedicalRecordResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AddMedicalRecord"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.medicalhistory.AddMedicalRecordRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.medicalhistory.AddMedicalRecordResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MedicalHistoryServiceMethodDescriptorSupplier("AddMedicalRecord"))
              .build();
        }
      }
    }
    return getAddMedicalRecordMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest,
      com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse> getGetMedicalHistoryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMedicalHistory",
      requestType = com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest.class,
      responseType = com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest,
      com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse> getGetMedicalHistoryMethod() {
    io.grpc.MethodDescriptor<com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest, com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse> getGetMedicalHistoryMethod;
    if ((getGetMedicalHistoryMethod = MedicalHistoryServiceGrpc.getGetMedicalHistoryMethod) == null) {
      synchronized (MedicalHistoryServiceGrpc.class) {
        if ((getGetMedicalHistoryMethod = MedicalHistoryServiceGrpc.getGetMedicalHistoryMethod) == null) {
          MedicalHistoryServiceGrpc.getGetMedicalHistoryMethod = getGetMedicalHistoryMethod =
              io.grpc.MethodDescriptor.<com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest, com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetMedicalHistory"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new MedicalHistoryServiceMethodDescriptorSupplier("GetMedicalHistory"))
              .build();
        }
      }
    }
    return getGetMedicalHistoryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MedicalHistoryServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MedicalHistoryServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MedicalHistoryServiceStub>() {
        @java.lang.Override
        public MedicalHistoryServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MedicalHistoryServiceStub(channel, callOptions);
        }
      };
    return MedicalHistoryServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static MedicalHistoryServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MedicalHistoryServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MedicalHistoryServiceBlockingV2Stub>() {
        @java.lang.Override
        public MedicalHistoryServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MedicalHistoryServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return MedicalHistoryServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MedicalHistoryServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MedicalHistoryServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MedicalHistoryServiceBlockingStub>() {
        @java.lang.Override
        public MedicalHistoryServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MedicalHistoryServiceBlockingStub(channel, callOptions);
        }
      };
    return MedicalHistoryServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MedicalHistoryServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MedicalHistoryServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MedicalHistoryServiceFutureStub>() {
        @java.lang.Override
        public MedicalHistoryServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MedicalHistoryServiceFutureStub(channel, callOptions);
        }
      };
    return MedicalHistoryServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service for managing medical history records
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Adds a new medical record for a patient
     * </pre>
     */
    default void addMedicalRecord(com.carelink.grpc.medicalhistory.AddMedicalRecordRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.medicalhistory.AddMedicalRecordResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAddMedicalRecordMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves full medical history of a patient
     * </pre>
     */
    default void getMedicalHistory(com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMedicalHistoryMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service MedicalHistoryService.
   * <pre>
   * Service for managing medical history records
   * </pre>
   */
  public static abstract class MedicalHistoryServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return MedicalHistoryServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service MedicalHistoryService.
   * <pre>
   * Service for managing medical history records
   * </pre>
   */
  public static final class MedicalHistoryServiceStub
      extends io.grpc.stub.AbstractAsyncStub<MedicalHistoryServiceStub> {
    private MedicalHistoryServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MedicalHistoryServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MedicalHistoryServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Adds a new medical record for a patient
     * </pre>
     */
    public void addMedicalRecord(com.carelink.grpc.medicalhistory.AddMedicalRecordRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.medicalhistory.AddMedicalRecordResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAddMedicalRecordMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieves full medical history of a patient
     * </pre>
     */
    public void getMedicalHistory(com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMedicalHistoryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service MedicalHistoryService.
   * <pre>
   * Service for managing medical history records
   * </pre>
   */
  public static final class MedicalHistoryServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<MedicalHistoryServiceBlockingV2Stub> {
    private MedicalHistoryServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MedicalHistoryServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MedicalHistoryServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Adds a new medical record for a patient
     * </pre>
     */
    public com.carelink.grpc.medicalhistory.AddMedicalRecordResponse addMedicalRecord(com.carelink.grpc.medicalhistory.AddMedicalRecordRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddMedicalRecordMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves full medical history of a patient
     * </pre>
     */
    public com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse getMedicalHistory(com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMedicalHistoryMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service MedicalHistoryService.
   * <pre>
   * Service for managing medical history records
   * </pre>
   */
  public static final class MedicalHistoryServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<MedicalHistoryServiceBlockingStub> {
    private MedicalHistoryServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MedicalHistoryServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MedicalHistoryServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Adds a new medical record for a patient
     * </pre>
     */
    public com.carelink.grpc.medicalhistory.AddMedicalRecordResponse addMedicalRecord(com.carelink.grpc.medicalhistory.AddMedicalRecordRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAddMedicalRecordMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves full medical history of a patient
     * </pre>
     */
    public com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse getMedicalHistory(com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMedicalHistoryMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service MedicalHistoryService.
   * <pre>
   * Service for managing medical history records
   * </pre>
   */
  public static final class MedicalHistoryServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<MedicalHistoryServiceFutureStub> {
    private MedicalHistoryServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MedicalHistoryServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MedicalHistoryServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Adds a new medical record for a patient
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.carelink.grpc.medicalhistory.AddMedicalRecordResponse> addMedicalRecord(
        com.carelink.grpc.medicalhistory.AddMedicalRecordRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAddMedicalRecordMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves full medical history of a patient
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse> getMedicalHistory(
        com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMedicalHistoryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD_MEDICAL_RECORD = 0;
  private static final int METHODID_GET_MEDICAL_HISTORY = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD_MEDICAL_RECORD:
          serviceImpl.addMedicalRecord((com.carelink.grpc.medicalhistory.AddMedicalRecordRequest) request,
              (io.grpc.stub.StreamObserver<com.carelink.grpc.medicalhistory.AddMedicalRecordResponse>) responseObserver);
          break;
        case METHODID_GET_MEDICAL_HISTORY:
          serviceImpl.getMedicalHistory((com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest) request,
              (io.grpc.stub.StreamObserver<com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse>) responseObserver);
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

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getAddMedicalRecordMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.carelink.grpc.medicalhistory.AddMedicalRecordRequest,
              com.carelink.grpc.medicalhistory.AddMedicalRecordResponse>(
                service, METHODID_ADD_MEDICAL_RECORD)))
        .addMethod(
          getGetMedicalHistoryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest,
              com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse>(
                service, METHODID_GET_MEDICAL_HISTORY)))
        .build();
  }

  private static abstract class MedicalHistoryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MedicalHistoryServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.carelink.grpc.medicalhistory.MedicalHistoryProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MedicalHistoryService");
    }
  }

  private static final class MedicalHistoryServiceFileDescriptorSupplier
      extends MedicalHistoryServiceBaseDescriptorSupplier {
    MedicalHistoryServiceFileDescriptorSupplier() {}
  }

  private static final class MedicalHistoryServiceMethodDescriptorSupplier
      extends MedicalHistoryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    MedicalHistoryServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (MedicalHistoryServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MedicalHistoryServiceFileDescriptorSupplier())
              .addMethod(getAddMedicalRecordMethod())
              .addMethod(getGetMedicalHistoryMethod())
              .build();
        }
      }
    }
    return result;
  }
}
