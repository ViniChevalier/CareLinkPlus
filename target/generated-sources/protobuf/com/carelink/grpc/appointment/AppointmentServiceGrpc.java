package com.carelink.grpc.appointment;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service for appointment scheduling and management
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class AppointmentServiceGrpc {

  private AppointmentServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "carelink.appointment.AppointmentService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.carelink.grpc.appointment.ScheduleAppointmentRequest,
      com.carelink.grpc.appointment.ScheduleAppointmentResponse> getScheduleAppointmentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ScheduleAppointment",
      requestType = com.carelink.grpc.appointment.ScheduleAppointmentRequest.class,
      responseType = com.carelink.grpc.appointment.ScheduleAppointmentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.carelink.grpc.appointment.ScheduleAppointmentRequest,
      com.carelink.grpc.appointment.ScheduleAppointmentResponse> getScheduleAppointmentMethod() {
    io.grpc.MethodDescriptor<com.carelink.grpc.appointment.ScheduleAppointmentRequest, com.carelink.grpc.appointment.ScheduleAppointmentResponse> getScheduleAppointmentMethod;
    if ((getScheduleAppointmentMethod = AppointmentServiceGrpc.getScheduleAppointmentMethod) == null) {
      synchronized (AppointmentServiceGrpc.class) {
        if ((getScheduleAppointmentMethod = AppointmentServiceGrpc.getScheduleAppointmentMethod) == null) {
          AppointmentServiceGrpc.getScheduleAppointmentMethod = getScheduleAppointmentMethod =
              io.grpc.MethodDescriptor.<com.carelink.grpc.appointment.ScheduleAppointmentRequest, com.carelink.grpc.appointment.ScheduleAppointmentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ScheduleAppointment"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.appointment.ScheduleAppointmentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.appointment.ScheduleAppointmentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AppointmentServiceMethodDescriptorSupplier("ScheduleAppointment"))
              .build();
        }
      }
    }
    return getScheduleAppointmentMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.carelink.grpc.appointment.GetAppointmentsRequest,
      com.carelink.grpc.appointment.GetAppointmentsResponse> getGetAppointmentsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetAppointments",
      requestType = com.carelink.grpc.appointment.GetAppointmentsRequest.class,
      responseType = com.carelink.grpc.appointment.GetAppointmentsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.carelink.grpc.appointment.GetAppointmentsRequest,
      com.carelink.grpc.appointment.GetAppointmentsResponse> getGetAppointmentsMethod() {
    io.grpc.MethodDescriptor<com.carelink.grpc.appointment.GetAppointmentsRequest, com.carelink.grpc.appointment.GetAppointmentsResponse> getGetAppointmentsMethod;
    if ((getGetAppointmentsMethod = AppointmentServiceGrpc.getGetAppointmentsMethod) == null) {
      synchronized (AppointmentServiceGrpc.class) {
        if ((getGetAppointmentsMethod = AppointmentServiceGrpc.getGetAppointmentsMethod) == null) {
          AppointmentServiceGrpc.getGetAppointmentsMethod = getGetAppointmentsMethod =
              io.grpc.MethodDescriptor.<com.carelink.grpc.appointment.GetAppointmentsRequest, com.carelink.grpc.appointment.GetAppointmentsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetAppointments"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.appointment.GetAppointmentsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.appointment.GetAppointmentsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AppointmentServiceMethodDescriptorSupplier("GetAppointments"))
              .build();
        }
      }
    }
    return getGetAppointmentsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.carelink.grpc.appointment.CancelAppointmentRequest,
      com.carelink.grpc.appointment.CancelAppointmentResponse> getCancelAppointmentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelAppointment",
      requestType = com.carelink.grpc.appointment.CancelAppointmentRequest.class,
      responseType = com.carelink.grpc.appointment.CancelAppointmentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.carelink.grpc.appointment.CancelAppointmentRequest,
      com.carelink.grpc.appointment.CancelAppointmentResponse> getCancelAppointmentMethod() {
    io.grpc.MethodDescriptor<com.carelink.grpc.appointment.CancelAppointmentRequest, com.carelink.grpc.appointment.CancelAppointmentResponse> getCancelAppointmentMethod;
    if ((getCancelAppointmentMethod = AppointmentServiceGrpc.getCancelAppointmentMethod) == null) {
      synchronized (AppointmentServiceGrpc.class) {
        if ((getCancelAppointmentMethod = AppointmentServiceGrpc.getCancelAppointmentMethod) == null) {
          AppointmentServiceGrpc.getCancelAppointmentMethod = getCancelAppointmentMethod =
              io.grpc.MethodDescriptor.<com.carelink.grpc.appointment.CancelAppointmentRequest, com.carelink.grpc.appointment.CancelAppointmentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelAppointment"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.appointment.CancelAppointmentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.appointment.CancelAppointmentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AppointmentServiceMethodDescriptorSupplier("CancelAppointment"))
              .build();
        }
      }
    }
    return getCancelAppointmentMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AppointmentServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AppointmentServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AppointmentServiceStub>() {
        @java.lang.Override
        public AppointmentServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AppointmentServiceStub(channel, callOptions);
        }
      };
    return AppointmentServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static AppointmentServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AppointmentServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AppointmentServiceBlockingV2Stub>() {
        @java.lang.Override
        public AppointmentServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AppointmentServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return AppointmentServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AppointmentServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AppointmentServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AppointmentServiceBlockingStub>() {
        @java.lang.Override
        public AppointmentServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AppointmentServiceBlockingStub(channel, callOptions);
        }
      };
    return AppointmentServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AppointmentServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AppointmentServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AppointmentServiceFutureStub>() {
        @java.lang.Override
        public AppointmentServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AppointmentServiceFutureStub(channel, callOptions);
        }
      };
    return AppointmentServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service for appointment scheduling and management
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Schedules a new appointment between patient and doctor
     * </pre>
     */
    default void scheduleAppointment(com.carelink.grpc.appointment.ScheduleAppointmentRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.appointment.ScheduleAppointmentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getScheduleAppointmentMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves appointments for a given user (patient or doctor)
     * </pre>
     */
    default void getAppointments(com.carelink.grpc.appointment.GetAppointmentsRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.appointment.GetAppointmentsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetAppointmentsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Cancels an existing appointment
     * </pre>
     */
    default void cancelAppointment(com.carelink.grpc.appointment.CancelAppointmentRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.appointment.CancelAppointmentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelAppointmentMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service AppointmentService.
   * <pre>
   * Service for appointment scheduling and management
   * </pre>
   */
  public static abstract class AppointmentServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return AppointmentServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service AppointmentService.
   * <pre>
   * Service for appointment scheduling and management
   * </pre>
   */
  public static final class AppointmentServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AppointmentServiceStub> {
    private AppointmentServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AppointmentServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AppointmentServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Schedules a new appointment between patient and doctor
     * </pre>
     */
    public void scheduleAppointment(com.carelink.grpc.appointment.ScheduleAppointmentRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.appointment.ScheduleAppointmentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getScheduleAppointmentMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieves appointments for a given user (patient or doctor)
     * </pre>
     */
    public void getAppointments(com.carelink.grpc.appointment.GetAppointmentsRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.appointment.GetAppointmentsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetAppointmentsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Cancels an existing appointment
     * </pre>
     */
    public void cancelAppointment(com.carelink.grpc.appointment.CancelAppointmentRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.appointment.CancelAppointmentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelAppointmentMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service AppointmentService.
   * <pre>
   * Service for appointment scheduling and management
   * </pre>
   */
  public static final class AppointmentServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<AppointmentServiceBlockingV2Stub> {
    private AppointmentServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AppointmentServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AppointmentServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Schedules a new appointment between patient and doctor
     * </pre>
     */
    public com.carelink.grpc.appointment.ScheduleAppointmentResponse scheduleAppointment(com.carelink.grpc.appointment.ScheduleAppointmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getScheduleAppointmentMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves appointments for a given user (patient or doctor)
     * </pre>
     */
    public com.carelink.grpc.appointment.GetAppointmentsResponse getAppointments(com.carelink.grpc.appointment.GetAppointmentsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAppointmentsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Cancels an existing appointment
     * </pre>
     */
    public com.carelink.grpc.appointment.CancelAppointmentResponse cancelAppointment(com.carelink.grpc.appointment.CancelAppointmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelAppointmentMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service AppointmentService.
   * <pre>
   * Service for appointment scheduling and management
   * </pre>
   */
  public static final class AppointmentServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AppointmentServiceBlockingStub> {
    private AppointmentServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AppointmentServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AppointmentServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Schedules a new appointment between patient and doctor
     * </pre>
     */
    public com.carelink.grpc.appointment.ScheduleAppointmentResponse scheduleAppointment(com.carelink.grpc.appointment.ScheduleAppointmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getScheduleAppointmentMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves appointments for a given user (patient or doctor)
     * </pre>
     */
    public com.carelink.grpc.appointment.GetAppointmentsResponse getAppointments(com.carelink.grpc.appointment.GetAppointmentsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAppointmentsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Cancels an existing appointment
     * </pre>
     */
    public com.carelink.grpc.appointment.CancelAppointmentResponse cancelAppointment(com.carelink.grpc.appointment.CancelAppointmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelAppointmentMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service AppointmentService.
   * <pre>
   * Service for appointment scheduling and management
   * </pre>
   */
  public static final class AppointmentServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AppointmentServiceFutureStub> {
    private AppointmentServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AppointmentServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AppointmentServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Schedules a new appointment between patient and doctor
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.carelink.grpc.appointment.ScheduleAppointmentResponse> scheduleAppointment(
        com.carelink.grpc.appointment.ScheduleAppointmentRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getScheduleAppointmentMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves appointments for a given user (patient or doctor)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.carelink.grpc.appointment.GetAppointmentsResponse> getAppointments(
        com.carelink.grpc.appointment.GetAppointmentsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetAppointmentsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Cancels an existing appointment
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.carelink.grpc.appointment.CancelAppointmentResponse> cancelAppointment(
        com.carelink.grpc.appointment.CancelAppointmentRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelAppointmentMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SCHEDULE_APPOINTMENT = 0;
  private static final int METHODID_GET_APPOINTMENTS = 1;
  private static final int METHODID_CANCEL_APPOINTMENT = 2;

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
        case METHODID_SCHEDULE_APPOINTMENT:
          serviceImpl.scheduleAppointment((com.carelink.grpc.appointment.ScheduleAppointmentRequest) request,
              (io.grpc.stub.StreamObserver<com.carelink.grpc.appointment.ScheduleAppointmentResponse>) responseObserver);
          break;
        case METHODID_GET_APPOINTMENTS:
          serviceImpl.getAppointments((com.carelink.grpc.appointment.GetAppointmentsRequest) request,
              (io.grpc.stub.StreamObserver<com.carelink.grpc.appointment.GetAppointmentsResponse>) responseObserver);
          break;
        case METHODID_CANCEL_APPOINTMENT:
          serviceImpl.cancelAppointment((com.carelink.grpc.appointment.CancelAppointmentRequest) request,
              (io.grpc.stub.StreamObserver<com.carelink.grpc.appointment.CancelAppointmentResponse>) responseObserver);
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
          getScheduleAppointmentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.carelink.grpc.appointment.ScheduleAppointmentRequest,
              com.carelink.grpc.appointment.ScheduleAppointmentResponse>(
                service, METHODID_SCHEDULE_APPOINTMENT)))
        .addMethod(
          getGetAppointmentsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.carelink.grpc.appointment.GetAppointmentsRequest,
              com.carelink.grpc.appointment.GetAppointmentsResponse>(
                service, METHODID_GET_APPOINTMENTS)))
        .addMethod(
          getCancelAppointmentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.carelink.grpc.appointment.CancelAppointmentRequest,
              com.carelink.grpc.appointment.CancelAppointmentResponse>(
                service, METHODID_CANCEL_APPOINTMENT)))
        .build();
  }

  private static abstract class AppointmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AppointmentServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.carelink.grpc.appointment.AppointmentProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AppointmentService");
    }
  }

  private static final class AppointmentServiceFileDescriptorSupplier
      extends AppointmentServiceBaseDescriptorSupplier {
    AppointmentServiceFileDescriptorSupplier() {}
  }

  private static final class AppointmentServiceMethodDescriptorSupplier
      extends AppointmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    AppointmentServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (AppointmentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AppointmentServiceFileDescriptorSupplier())
              .addMethod(getScheduleAppointmentMethod())
              .addMethod(getGetAppointmentsMethod())
              .addMethod(getCancelAppointmentMethod())
              .build();
        }
      }
    }
    return result;
  }
}
