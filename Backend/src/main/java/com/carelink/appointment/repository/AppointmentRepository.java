package com.carelink.appointment.repository;

import com.carelink.appointment.model.AppointmentEntity;
import com.carelink.appointment.dto.AppointmentWithPatientDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Integer> {
       List<AppointmentEntity> findByPatientId(Integer patientId);

       List<AppointmentEntity> findByDoctorId(Integer doctorId);

       @Query("SELECT new com.carelink.appointment.dto.AppointmentWithPatientDTO(" +
                     "a.id, a.patientId, a.doctorId, a.appointmentDateTime, a.appointmentStatus, a.reason, " +
                     "a.createdAt, a.availability.id, u.firstName, u.lastName) " +
                     "FROM AppointmentEntity a JOIN a.patient u WHERE a.doctorId = :doctorId")
       List<AppointmentWithPatientDTO> findAppointmentsWithPatientNameByDoctorId(@Param("doctorId") Integer doctorId);

       @Query("SELECT a FROM AppointmentEntity a JOIN FETCH a.patient")
       List<AppointmentEntity> findAllWithPatient();

       @Query("SELECT a FROM AppointmentEntity a " +
                     "WHERE a.appointmentStatus = 'ATTENDED' " +
                     "AND FUNCTION('DATE', a.appointmentDateTime) = :yesterday")
       List<AppointmentEntity> findAttendedAppointmentsFromYesterday(@Param("yesterday") java.time.LocalDate yesterday);

       @Query("SELECT a FROM AppointmentEntity a WHERE DATE(a.appointmentDateTime) = :targetDate AND LOWER(a.appointmentStatus) <> 'cancelled'")
       List<AppointmentEntity> findUpcomingAppointmentsForReminder(@Param("targetDate") java.time.LocalDate targetDate);
}