package com.carelink.appointment.dto;

import java.time.LocalDateTime;

import com.carelink.appointment.model.AppointmentEntity;

public class AppointmentDTO {
    private Integer id;
    private LocalDateTime dateTime;
    private String type;
    private String status;
    private String notes;
    private Integer doctorId;
    private String doctorName;

    public AppointmentDTO(AppointmentEntity entity) {
        this.id = entity.getAppointmentId();
        this.dateTime = entity.getAppointmentDateTime();
        this.type = entity.getReason();
        this.status = entity.getAppointmentStatus();
        this.notes = entity.getReason();
        this.doctorId = entity.getDoctor() != null ? entity.getDoctor().getUserID() : null;
        this.doctorName = entity.getDoctor() != null
            ? entity.getDoctor().getFirstName() + " " + entity.getDoctor().getLastName()
            : null;
    }

    public AppointmentDTO(AppointmentEntity entity, Integer doctorId, String doctorName) {
        this.id = entity.getAppointmentId();
        this.dateTime = entity.getAppointmentDateTime();
        this.type = entity.getReason();
        this.status = entity.getAppointmentStatus();
        this.notes = entity.getReason();
        this.doctorId = doctorId;
        this.doctorName = doctorName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}