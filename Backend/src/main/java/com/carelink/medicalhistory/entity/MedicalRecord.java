package com.carelink.medicalhistory.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "medicalrecords")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecordID")
    private Integer recordId;

    @Column(name = "PatientID", nullable = false)
    private Integer patientId;

    @Column(name = "DoctorID", nullable = false)
    private Integer doctorId;

    @Column(name = "recordDate", insertable = false, updatable = false)
    private Timestamp recordDate;

    @Lob
    @Column(name = "notes", columnDefinition = "LONGTEXT")
    private String notes;

    @Lob
    @Column(name = "prescriptions", columnDefinition = "LONGTEXT")
    private String prescriptions;

    @Column(name = "attachment_name")
    private String attachmentName;

    @Column(name = "LastUpdated", insertable = false, updatable = false)
    private Timestamp lastUpdated;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "HistoryID")
    private Integer historyId;

    // Getters and setters

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Timestamp getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Timestamp recordDate) {
        this.recordDate = recordDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(String prescriptions) {
        this.prescriptions = prescriptions;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }
}