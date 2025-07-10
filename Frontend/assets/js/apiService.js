const BASE_URL = "https://carelinkplus-backend-hbe9c2egbmfgg6h5.francecentral-01.azurewebsites.net";

function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

// -----------------------------
// Generic request helpers
// -----------------------------
async function get(endpoint) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: "GET",
        headers: { "Content-Type": "application/json", ...getAuthHeaders() },
    });
    return handleResponse(response);
}

async function post(endpoint, data) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json", ...getAuthHeaders() },
        body: JSON.stringify(data),
    });
    return handleResponse(response);
}

async function put(endpoint, data) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json", ...getAuthHeaders() },
        body: JSON.stringify(data),
    });
    return handleResponse(response);
}

async function del(endpoint) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json", ...getAuthHeaders() },
    });
    return handleResponse(response);
}

async function handleResponse(response) {
    // Always try to parse JSON, even if the status is not ok
    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(data.message || "API request failed");
    }
    return data;
}

// -----------------------------
// Appointment APIs
// -----------------------------
export function getAllAppointments() {
    return get("/api/appointments");
}

export function getAppointmentById(id) {
    return get(`/api/appointments/${id}`);
}

export function createAppointment(data) {
    return post("/api/appointments", data);
}

export function updateAppointment(id, data) {
    return put(`/api/appointments/${id}`, data);
}

export function deleteAppointment(id) {
    return del(`/api/appointments/${id}`);
}

export function getAppointmentsByPatient(patientId) {
    return get(`/api/appointments/patient/${patientId}`);
}

export function getAppointmentsByDoctor(doctorId) {
    return get(`/api/appointments/doctor/${doctorId}`);
}

// -----------------------------
// Medical History APIs
// -----------------------------
export function getAllMedicalRecords() {
    return get("/api/medical-history");
}

export function getMedicalRecordById(recordId) {
    return get(`/api/medical-history/${recordId}`);
}

export function createMedicalRecord(patientId, doctorId, notes, prescriptions, updatedBy, historyId, fileObj) {
    const formData = new FormData();
    if (fileObj) {
        formData.append("file", fileObj);
    }
    return fetch(`${BASE_URL}/api/medical-history?patientId=${patientId}&doctorId=${doctorId}&notes=${encodeURIComponent(notes || "")}&prescriptions=${encodeURIComponent(prescriptions || "")}&updatedBy=${encodeURIComponent(updatedBy || "")}&historyId=${historyId || ""}`, {
        method: "POST",
        headers: { ...getAuthHeaders() },
        body: formData,
    }).then(handleResponse);
}

export function updateMedicalRecord(recordId, data) {
    return put(`/api/medical-history/${recordId}`, data);
}

export function deleteMedicalRecord(recordId) {
    return del(`/api/medical-history/${recordId}`);
}

export function getRecordsByPatient(patientId) {
    return get(`/api/medical-history/patient/${patientId}`);
}

// -----------------------------
// Patient Medical History APIs
// -----------------------------
export function getPatientHistoriesByPatient(patientId) {
    return get(`/api/patient-history/patient/${patientId}`);
}

export function getPatientHistoryById(historyId) {
    return get(`/api/patient-history/${historyId}`);
}

export function createPatientHistory(data) {
    return post("/api/patient-history", data);
}

export function updatePatientHistory(historyId, data) {
    return put(`/api/patient-history/${historyId}`, data);
}

export function deletePatientHistory(historyId) {
    return del(`/api/patient-history/${historyId}`);
}

// -----------------------------
// Notification APIs
// -----------------------------
export function createNotification(data) {
    return post("/api/notifications", data);
}

export function getNotificationById(id) {
    return get(`/api/notifications/${id}`);
}

export function deleteNotification(id) {
    return del(`/api/notifications/${id}`);
}

export function markNotificationAsRead(id) {
    return put(`/api/notifications/${id}/read`);
}

export function getNotificationsByUser(userId, isRead = null) {
    let url = `/api/notifications/user/${userId}`;
    if (isRead !== null) {
        url += `?isRead=${isRead}`;
    }
    return get(url);
}

// -----------------------------
// Availability APIs
// -----------------------------
export function addDoctorAvailability(data) {
    return post("/api/availability", data);
}

export function getDoctorAvailability(doctorId) {
    return get(`/api/availability/doctor/${doctorId}`);
}

export function deleteAvailability(availabilityId) {
    return del(`/api/availability/${availabilityId}`);
}

// -----------------------------
// Account APIs
// -----------------------------
export function register(data) {
    return post("/api/account/register", data);
}

export function login(data) {
    return post("/api/account/login", data);
}

export function getProfile() {
    return get("/api/account/profile");
}

export function updateProfile(data) {
    return put("/api/account/profile", data);
}

export function changePassword(data) {
    return post("/api/account/change-password", data);
}

export function requestPasswordReset(data) {
    return post("/api/account/reset", data);
}

export function updatePassword(data) {
    return put("/api/account/update-password", data);
}

export function adminResetPassword(data) {
    return post("/api/account/admin/reset", data);
}

// -----------------------------
// File Upload
// -----------------------------
export function uploadFile(fileObj) {
    const formData = new FormData();
    formData.append("file", fileObj);
    return fetch(`${BASE_URL}/api/files/upload`, {
        method: "POST",
        headers: { ...getAuthHeaders() },
        body: formData,
    }).then(handleResponse);
}