package com.carelink.appointment.service;

import com.carelink.appointment.model.AppointmentEntity;
import com.carelink.appointment.repository.AppointmentRepository;
import com.carelink.grpc.appointment.Appointment;
import com.carelink.grpc.appointment.ScheduleAppointmentRequest;
import com.carelink.grpc.appointment.ScheduleAppointmentResponse;
import com.carelink.grpc.appointment.GetAppointmentsRequest;
import com.carelink.grpc.appointment.GetAppointmentsResponse;
import com.carelink.grpc.appointment.CancelAppointmentRequest;
import com.carelink.grpc.appointment.CancelAppointmentResponse;
import com.carelink.grpc.appointment.AppointmentServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentServiceImpl extends AppointmentServiceGrpc.AppointmentServiceImplBase {

        private final AppointmentRepository repository;

        public AppointmentServiceImpl(AppointmentRepository repository) {
                this.repository = repository;
        }

        @Override
        public void scheduleAppointment(ScheduleAppointmentRequest request,
                        StreamObserver<ScheduleAppointmentResponse> responseObserver) {
                try {
                        Integer patientId = Integer.valueOf(request.getPatientId());
                        Integer doctorId = Integer.valueOf(request.getDoctorId());
                        LocalDateTime appointmentDateTime = LocalDateTime.parse(request.getAppointmentTime());

                        AppointmentEntity entity = new AppointmentEntity(
                                        null,
                                        patientId,
                                        doctorId,
                                        appointmentDateTime,
                                        "Scheduled",
                                        request.getReason());

                        repository.save(entity);

                        ScheduleAppointmentResponse response = ScheduleAppointmentResponse.newBuilder()
                                        .setSuccess(true)
                                        .setMessage("Appointment scheduled and saved successfully.")
                                        .build();

                        responseObserver.onNext(response);
                        responseObserver.onCompleted();

                } catch (Exception e) {
                        ScheduleAppointmentResponse response = ScheduleAppointmentResponse.newBuilder()
                                        .setSuccess(false)
                                        .setMessage("Error scheduling appointment: " + e.getMessage())
                                        .build();

                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                }
        }

        @Override
        public void getAppointments(GetAppointmentsRequest request,
                        StreamObserver<GetAppointmentsResponse> responseObserver) {
                try {
                        Integer userId = Integer.valueOf(request.getUserId());

                        List<AppointmentEntity> found = repository.findByPatientIdOrDoctorId(userId, userId);

                        List<Appointment> grpcAppointments = found.stream().map(entity -> Appointment.newBuilder()
                                        .setAppointmentId(String.valueOf(entity.getAppointmentId()))
                                        .setPatientId(String.valueOf(entity.getPatientId()))
                                        .setDoctorId(String.valueOf(entity.getDoctorId()))
                                        .setAppointmentTime(entity.getAppointmentDateTime().toString())
                                        .setReason(entity.getReason() == null ? "" : entity.getReason())
                                        .setStatus(entity.getAppointmentStatus())
                                        .build()).collect(Collectors.toList());

                        GetAppointmentsResponse response = GetAppointmentsResponse.newBuilder()
                                        .addAllAppointments(grpcAppointments)
                                        .build();

                        responseObserver.onNext(response);
                        responseObserver.onCompleted();

                } catch (Exception e) {
                        GetAppointmentsResponse response = GetAppointmentsResponse.newBuilder().build();
                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                }
        }

        @Override
        public void cancelAppointment(CancelAppointmentRequest request,
                        StreamObserver<CancelAppointmentResponse> responseObserver) {
                try {
                        Integer appointmentId = Integer.valueOf(request.getAppointmentId());
                        Optional<AppointmentEntity> optional = repository.findById(appointmentId);
                        boolean success = false;
                        String message;

                        if (optional.isPresent() && !"Cancelled".equals(optional.get().getAppointmentStatus())) {
                                AppointmentEntity entity = optional.get();
                                entity.setAppointmentStatus("Cancelled");
                                repository.save(entity);
                                success = true;
                                message = "Appointment cancelled and updated successfully.";
                        } else {
                                message = "Appointment not found or already cancelled.";
                        }

                        CancelAppointmentResponse response = CancelAppointmentResponse.newBuilder()
                                        .setSuccess(success)
                                        .setMessage(message)
                                        .build();

                        responseObserver.onNext(response);
                        responseObserver.onCompleted();

                } catch (Exception e) {
                        CancelAppointmentResponse response = CancelAppointmentResponse.newBuilder()
                                        .setSuccess(false)
                                        .setMessage("Error cancelling appointment: " + e.getMessage())
                                        .build();

                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                }
        }
}