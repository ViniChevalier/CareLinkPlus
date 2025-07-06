package com.carelink.appointment.grpc;

import com.carelink.appointment.dto.AppointmentRequestDTO;
import com.carelink.appointment.model.AppointmentEntity;
import com.carelink.appointment.service.AppointmentServiceImpl;
import com.carelink.grpc.appointment.Appointment;
import com.carelink.grpc.appointment.AppointmentServiceGrpc;
import com.carelink.grpc.appointment.ScheduleAppointmentRequest;
import com.carelink.grpc.appointment.ScheduleAppointmentResponse;
import com.carelink.grpc.appointment.GetAppointmentsRequest;
import com.carelink.grpc.appointment.GetAppointmentsResponse;
import com.carelink.grpc.appointment.GetAppointmentByIdRequest;
import com.carelink.grpc.appointment.UpdateAppointmentRequest;
import com.carelink.grpc.appointment.UpdateAppointmentResponse;
import com.carelink.grpc.appointment.CancelAppointmentRequest;
import com.carelink.grpc.appointment.CancelAppointmentResponse;
import com.carelink.grpc.appointment.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AppointmentGrpcService extends AppointmentServiceGrpc.AppointmentServiceImplBase {

    private final AppointmentServiceImpl appointmentService;

    public AppointmentGrpcService(AppointmentServiceImpl appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Override
    public void scheduleAppointment(ScheduleAppointmentRequest request, StreamObserver<ScheduleAppointmentResponse> responseObserver) {
        try {
            AppointmentEntity entity = new AppointmentEntity();
            entity.setPatientId((int) Long.parseLong(request.getPatientId()));
            entity.setDoctorId(Integer.parseInt(request.getDoctorId()));
            entity.setAppointmentDateTime(LocalDateTime.parse(request.getAppointmentTime(), DateTimeFormatter.ISO_DATE_TIME));
            entity.setReason(request.getReason());

            appointmentService.createAppointmentFromEntity(entity);

            ScheduleAppointmentResponse response = ScheduleAppointmentResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Appointment scheduled successfully.")
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            ScheduleAppointmentResponse response = ScheduleAppointmentResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to schedule appointment: " + e.getMessage())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getAppointments(GetAppointmentsRequest request, StreamObserver<GetAppointmentsResponse> responseObserver) {
        List<AppointmentEntity> appointments;

        if ("PATIENT".equalsIgnoreCase(request.getRole())) {
            appointments = appointmentService.getAppointmentsByPatient((int) Long.parseLong(request.getUserId()));
        } else if ("DOCTOR".equalsIgnoreCase(request.getRole())) {
            appointments = appointmentService.getAppointmentsByDoctor((int) Long.parseLong(request.getUserId()));
        } else {
            appointments = List.of();
        }

        GetAppointmentsResponse.Builder responseBuilder = GetAppointmentsResponse.newBuilder();
        for (AppointmentEntity entity : appointments) {
            responseBuilder.addAppointments(buildAppointmentMessage(entity));
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAllAppointments(Empty request, StreamObserver<GetAppointmentsResponse> responseObserver) {
        List<AppointmentEntity> appointments = appointmentService.getAllAppointments();

        GetAppointmentsResponse.Builder responseBuilder = GetAppointmentsResponse.newBuilder();
        for (AppointmentEntity entity : appointments) {
            responseBuilder.addAppointments(buildAppointmentMessage(entity));
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAppointmentById(GetAppointmentByIdRequest request, StreamObserver<Appointment> responseObserver) {
        AppointmentEntity entity = appointmentService.getAppointmentById((int) Long.parseLong(request.getAppointmentId()));

        Appointment appointment = buildAppointmentMessage(entity);

        responseObserver.onNext(appointment);
        responseObserver.onCompleted();
    }

    @Override
    public void updateAppointment(UpdateAppointmentRequest request, StreamObserver<UpdateAppointmentResponse> responseObserver) {
        try {
            Integer appointmentId = Integer.parseInt(request.getAppointmentId());
            String reason = request.getReason();

            AppointmentRequestDTO dto = new AppointmentRequestDTO();
            dto.setReason(reason);

            AppointmentEntity updated = appointmentService.updateAppointment(appointmentId, dto);

            UpdateAppointmentResponse response = UpdateAppointmentResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Appointment updated successfully.")
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            UpdateAppointmentResponse response = UpdateAppointmentResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to update appointment: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void cancelAppointment(CancelAppointmentRequest request, StreamObserver<CancelAppointmentResponse> responseObserver) {
        try {
            appointmentService.deleteAppointment((int) Long.parseLong(request.getAppointmentId()));

            CancelAppointmentResponse response = CancelAppointmentResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Appointment canceled successfully.")
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            CancelAppointmentResponse response = CancelAppointmentResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to cancel appointment: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    // Helper method to build Appointment message
    private Appointment buildAppointmentMessage(AppointmentEntity entity) {
        return Appointment.newBuilder()
            .setAppointmentId(entity.getAppointmentId().toString())
            .setPatientId(entity.getPatientId().toString())
            .setDoctorId(entity.getDoctorId().toString())
            .setAppointmentTime(entity.getAppointmentDateTime().format(DateTimeFormatter.ISO_DATE_TIME))
            .setReason(entity.getReason())
            .setStatus("Scheduled") // Ajuste se tiver campo de status real
            .build();
    }
}