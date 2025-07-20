package com.carelink.appointment.dto;

import java.time.LocalDateTime;

import com.carelink.appointment.model.AppointmentEntity;

public class AppointmentDTO {
    private Integer id;
    private LocalDateTime dateTime;
    private String type;
    private String status;
    private String notes;

    public AppointmentDTO(AppointmentEntity entity) {
        this.id = entity.getAppointmentId();
        this.dateTime = entity.getAppointmentDateTime();
        this.type = entity.getReason();
        this.status = entity.getAppointmentStatus();
        this.notes = entity.getReason();
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
}
