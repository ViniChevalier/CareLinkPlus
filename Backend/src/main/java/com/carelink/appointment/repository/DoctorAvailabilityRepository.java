package com.carelink.appointment.repository;

import com.carelink.appointment.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Integer> {
    List<DoctorAvailability> findByDoctorId(Integer doctorId);

    boolean existsByDoctorIdAndAvailableDateAndStartTimeLessThanAndEndTimeGreaterThan(
        Integer doctorId,
        java.time.LocalDate availableDate,
        java.time.LocalTime endTime,
        java.time.LocalTime startTime
    );

    @org.springframework.data.jpa.repository.Query("SELECT a FROM DoctorAvailability a WHERE FUNCTION('TIMESTAMP', a.availableDate, a.startTime) < CURRENT_TIMESTAMP AND a.isBooked = false AND a.status = 'AVAILABLE'")
    List<DoctorAvailability> findExpiredSlots();
}