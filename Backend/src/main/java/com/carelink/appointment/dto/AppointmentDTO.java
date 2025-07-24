package com.carelink.appointment.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.carelink.appointment.model.AppointmentEntity;

public class AppointmentDTO {
    private Integer id;
    private LocalDateTime dateTime;
    private String type;
    private String status;
    private String notes;
    private Integer doctorId;
    private String doctorName;
    private String patientName;
    private Integer patientId;
    private Integer availabilityId;
    private String availableDate;
    private String startTime;
    private String endTime;

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
        this.patientName = entity.getPatient() != null
            ? entity.getPatient().getFirstName() + " " + entity.getPatient().getLastName()
            : null;
        this.patientId = entity.getPatient() != null ? entity.getPatient().getUserID() : null;
        this.availabilityId = entity.getAvailability() != null ? entity.getAvailability().getId() : null;
        if (entity.getAvailability() != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            this.availableDate = entity.getAvailability().getAvailableDate().format(dateFormatter);
            this.startTime = entity.getAvailability().getStartTime().format(timeFormatter);
            this.endTime = entity.getAvailability().getEndTime().format(timeFormatter);
        }
    }

    public AppointmentDTO(AppointmentEntity entity, Integer doctorId, String doctorName) {
        this.id = entity.getAppointmentId();
        this.dateTime = entity.getAppointmentDateTime();
        this.type = entity.getReason();
        this.status = entity.getAppointmentStatus();
        this.notes = entity.getReason();
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientName = entity.getPatient() != null
            ? entity.getPatient().getFirstName() + " " + entity.getPatient().getLastName()
            : null;
        this.patientId = entity.getPatient() != null ? entity.getPatient().getUserID() : null;
        this.availabilityId = entity.getAvailability() != null ? entity.getAvailability().getId() : null;
        if (entity.getAvailability() != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            this.availableDate = entity.getAvailability().getAvailableDate().format(dateFormatter);
            this.startTime = entity.getAvailability().getStartTime().format(timeFormatter);
            this.endTime = entity.getAvailability().getEndTime().format(timeFormatter);
        }
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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Integer availabilityId) {
        this.availabilityId = availabilityId;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}