package com.carelink.appointment.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import com.carelink.account.model.User;

@Entity
@Table(name = "doctoravailability")
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AvailabilityID")
    private Integer id;

    @Column(name = "DoctorID", nullable = false)
    private Integer doctorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DoctorID", insertable = false, updatable = false)
    private User doctor;

    @Column(name = "AvailableDate", nullable = false)
    private LocalDate availableDate;

    @Column(name = "StartTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "EndTime", nullable = false)
    private LocalTime endTime;

    @Column(name = "IsBooked")
    private Boolean isBooked = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private AvailabilityStatus status = AvailabilityStatus.AVAILABLE;

    // Getters e setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(Boolean isBooked) {
        this.isBooked = isBooked;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public void setStatus(AvailabilityStatus status) {
        this.status = status;
    }
    @Transient
    public String getDoctorName() {
        if (doctor != null) {
            return doctor.getFirstName() + " " + doctor.getLastName();
        }
        return "";
    }
}