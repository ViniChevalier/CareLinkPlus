package com.carelink.grpc.account;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service responsible for user account management: registration, login, profile
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class AccountServiceGrpc {

  private AccountServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "carelink.account.AccountService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.carelink.grpc.account.RegisterUserRequest,
      com.carelink.grpc.account.RegisterUserResponse> getRegisterUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterUser",
      requestType = com.carelink.grpc.account.RegisterUserRequest.class,
      responseType = com.carelink.grpc.account.RegisterUserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.carelink.grpc.account.RegisterUserRequest,
      com.carelink.grpc.account.RegisterUserResponse> getRegisterUserMethod() {
    io.grpc.MethodDescriptor<com.carelink.grpc.account.RegisterUserRequest, com.carelink.grpc.account.RegisterUserResponse> getRegisterUserMethod;
    if ((getRegisterUserMethod = AccountServiceGrpc.getRegisterUserMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getRegisterUserMethod = AccountServiceGrpc.getRegisterUserMethod) == null) {
          AccountServiceGrpc.getRegisterUserMethod = getRegisterUserMethod =
              io.grpc.MethodDescriptor.<com.carelink.grpc.account.RegisterUserRequest, com.carelink.grpc.account.RegisterUserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.account.RegisterUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.account.RegisterUserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("RegisterUser"))
              .build();
        }
      }
    }
    return getRegisterUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.carelink.grpc.account.LoginRequest,
      com.carelink.grpc.account.LoginResponse> getLoginUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "LoginUser",
      requestType = com.carelink.grpc.account.LoginRequest.class,
      responseType = com.carelink.grpc.account.LoginResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.carelink.grpc.account.LoginRequest,
      com.carelink.grpc.account.LoginResponse> getLoginUserMethod() {
    io.grpc.MethodDescriptor<com.carelink.grpc.account.LoginRequest, com.carelink.grpc.account.LoginResponse> getLoginUserMethod;
    if ((getLoginUserMethod = AccountServiceGrpc.getLoginUserMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getLoginUserMethod = AccountServiceGrpc.getLoginUserMethod) == null) {
          AccountServiceGrpc.getLoginUserMethod = getLoginUserMethod =
              io.grpc.MethodDescriptor.<com.carelink.grpc.account.LoginRequest, com.carelink.grpc.account.LoginResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "LoginUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.account.LoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.account.LoginResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("LoginUser"))
              .build();
        }
      }
    }
    return getLoginUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.carelink.grpc.account.UserProfileRequest,
      com.carelink.grpc.account.UserProfileResponse> getGetUserProfileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserProfile",
      requestType = com.carelink.grpc.account.UserProfileRequest.class,
      responseType = com.carelink.grpc.account.UserProfileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.carelink.grpc.account.UserProfileRequest,
      com.carelink.grpc.account.UserProfileResponse> getGetUserProfileMethod() {
    io.grpc.MethodDescriptor<com.carelink.grpc.account.UserProfileRequest, com.carelink.grpc.account.UserProfileResponse> getGetUserProfileMethod;
    if ((getGetUserProfileMethod = AccountServiceGrpc.getGetUserProfileMethod) == null) {
      synchronized (AccountServiceGrpc.class) {
        if ((getGetUserProfileMethod = AccountServiceGrpc.getGetUserProfileMethod) == null) {
          AccountServiceGrpc.getGetUserProfileMethod = getGetUserProfileMethod =
              io.grpc.MethodDescriptor.<com.carelink.grpc.account.UserProfileRequest, com.carelink.grpc.account.UserProfileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserProfile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.account.UserProfileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.carelink.grpc.account.UserProfileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AccountServiceMethodDescriptorSupplier("GetUserProfile"))
              .build();
        }
      }
    }
    return getGetUserProfileMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AccountServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceStub>() {
        @java.lang.Override
        public AccountServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceStub(channel, callOptions);
        }
      };
    return AccountServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static AccountServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceBlockingV2Stub>() {
        @java.lang.Override
        public AccountServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return AccountServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AccountServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceBlockingStub>() {
        @java.lang.Override
        public AccountServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceBlockingStub(channel, callOptions);
        }
      };
    return AccountServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AccountServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AccountServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AccountServiceFutureStub>() {
        @java.lang.Override
        public AccountServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AccountServiceFutureStub(channel, callOptions);
        }
      };
    return AccountServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service responsible for user account management: registration, login, profile
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Registers a new user in the system
     * </pre>
     */
    default void registerUser(com.carelink.grpc.account.RegisterUserRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.account.RegisterUserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterUserMethod(), responseObserver);
    }

    /**
     * <pre>
     * Authenticates a user and provides a JWT token
     * </pre>
     */
    default void loginUser(com.carelink.grpc.account.LoginRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.account.LoginResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLoginUserMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves user profile details
     * </pre>
     */
    default void getUserProfile(com.carelink.grpc.account.UserProfileRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.account.UserProfileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserProfileMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service AccountService.
   * <pre>
   * Service responsible for user account management: registration, login, profile
   * </pre>
   */
  public static abstract class AccountServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return AccountServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service AccountService.
   * <pre>
   * Service responsible for user account management: registration, login, profile
   * </pre>
   */
  public static final class AccountServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AccountServiceStub> {
    private AccountServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Registers a new user in the system
     * </pre>
     */
    public void registerUser(com.carelink.grpc.account.RegisterUserRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.account.RegisterUserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Authenticates a user and provides a JWT token
     * </pre>
     */
    public void loginUser(com.carelink.grpc.account.LoginRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.account.LoginResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLoginUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieves user profile details
     * </pre>
     */
    public void getUserProfile(com.carelink.grpc.account.UserProfileRequest request,
        io.grpc.stub.StreamObserver<com.carelink.grpc.account.UserProfileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserProfileMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service AccountService.
   * <pre>
   * Service responsible for user account management: registration, login, profile
   * </pre>
   */
  public static final class AccountServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<AccountServiceBlockingV2Stub> {
    private AccountServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Registers a new user in the system
     * </pre>
     */
    public com.carelink.grpc.account.RegisterUserResponse registerUser(com.carelink.grpc.account.RegisterUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Authenticates a user and provides a JWT token
     * </pre>
     */
    public com.carelink.grpc.account.LoginResponse loginUser(com.carelink.grpc.account.LoginRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLoginUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves user profile details
     * </pre>
     */
    public com.carelink.grpc.account.UserProfileResponse getUserProfile(com.carelink.grpc.account.UserProfileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserProfileMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service AccountService.
   * <pre>
   * Service responsible for user account management: registration, login, profile
   * </pre>
   */
  public static final class AccountServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AccountServiceBlockingStub> {
    private AccountServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Registers a new user in the system
     * </pre>
     */
    public com.carelink.grpc.account.RegisterUserResponse registerUser(com.carelink.grpc.account.RegisterUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Authenticates a user and provides a JWT token
     * </pre>
     */
    public com.carelink.grpc.account.LoginResponse loginUser(com.carelink.grpc.account.LoginRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLoginUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves user profile details
     * </pre>
     */
    public com.carelink.grpc.account.UserProfileResponse getUserProfile(com.carelink.grpc.account.UserProfileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserProfileMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service AccountService.
   * <pre>
   * Service responsible for user account management: registration, login, profile
   * </pre>
   */
  public static final class AccountServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AccountServiceFutureStub> {
    private AccountServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AccountServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AccountServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Registers a new user in the system
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.carelink.grpc.account.RegisterUserResponse> registerUser(
        com.carelink.grpc.account.RegisterUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterUserMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Authenticates a user and provides a JWT token
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.carelink.grpc.account.LoginResponse> loginUser(
        com.carelink.grpc.account.LoginRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLoginUserMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves user profile details
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.carelink.grpc.account.UserProfileResponse> getUserProfile(
        com.carelink.grpc.account.UserProfileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserProfileMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_USER = 0;
  private static final int METHODID_LOGIN_USER = 1;
  private static final int METHODID_GET_USER_PROFILE = 2;

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
        case METHODID_REGISTER_USER:
          serviceImpl.registerUser((com.carelink.grpc.account.RegisterUserRequest) request,
              (io.grpc.stub.StreamObserver<com.carelink.grpc.account.RegisterUserResponse>) responseObserver);
          break;
        case METHODID_LOGIN_USER:
          serviceImpl.loginUser((com.carelink.grpc.account.LoginRequest) request,
              (io.grpc.stub.StreamObserver<com.carelink.grpc.account.LoginResponse>) responseObserver);
          break;
        case METHODID_GET_USER_PROFILE:
          serviceImpl.getUserProfile((com.carelink.grpc.account.UserProfileRequest) request,
              (io.grpc.stub.StreamObserver<com.carelink.grpc.account.UserProfileResponse>) responseObserver);
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
          getRegisterUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.carelink.grpc.account.RegisterUserRequest,
              com.carelink.grpc.account.RegisterUserResponse>(
                service, METHODID_REGISTER_USER)))
        .addMethod(
          getLoginUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.carelink.grpc.account.LoginRequest,
              com.carelink.grpc.account.LoginResponse>(
                service, METHODID_LOGIN_USER)))
        .addMethod(
          getGetUserProfileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.carelink.grpc.account.UserProfileRequest,
              com.carelink.grpc.account.UserProfileResponse>(
                service, METHODID_GET_USER_PROFILE)))
        .build();
  }

  private static abstract class AccountServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AccountServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.carelink.grpc.account.AccountProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AccountService");
    }
  }

  private static final class AccountServiceFileDescriptorSupplier
      extends AccountServiceBaseDescriptorSupplier {
    AccountServiceFileDescriptorSupplier() {}
  }

  private static final class AccountServiceMethodDescriptorSupplier
      extends AccountServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    AccountServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (AccountServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AccountServiceFileDescriptorSupplier())
              .addMethod(getRegisterUserMethod())
              .addMethod(getLoginUserMethod())
              .addMethod(getGetUserProfileMethod())
              .build();
        }
      }
    }
    return result;
  }
}
