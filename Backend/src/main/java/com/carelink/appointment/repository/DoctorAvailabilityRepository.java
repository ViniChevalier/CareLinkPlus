package com.carelink.appointment.repository;

import com.carelink.appointment.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

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

    @Modifying
    @Transactional
    @org.springframework.data.jpa.repository.Query("UPDATE DoctorAvailability a SET a.status = 'AVAILABLE', a.isBooked = false WHERE a.id = :slotId AND a.doctorId = :doctorId")
    int cancelSlot(@Param("slotId") Integer slotId, @Param("doctorId") Integer doctorId);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM DoctorAvailability a WHERE a.doctor.id = :doctorId")
    List<DoctorAvailability> getSlotDTOsByDoctor(@Param("doctorId") Integer doctorId);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM DoctorAvailability a")
    List<DoctorAvailability> getAllSlotDTOs();

    @org.springframework.data.jpa.repository.Query("SELECT a FROM DoctorAvailability a WHERE a.id = :availabilityId")
    DoctorAvailability getSlotDTOById(@Param("availabilityId") Integer availabilityId);

    List<DoctorAvailability> findByIsBookedFalseAndStatus(com.carelink.appointment.model.AvailabilityStatus status);
}