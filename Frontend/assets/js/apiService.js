//const BASE_URL = "https://carelinkplus-backend-hbe9c2egbmfgg6h5.francecentral-01.azurewebsites.net";
const BASE_URL = "http://localhost:8080";

function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

// -----------------------------
// Generic request helpers
// -----------------------------
export async function get(endpoint) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: "GET",
        headers: { "Content-Type": "application/json", ...getAuthHeaders() },
    });
    return handleResponse(response);
}

export async function post(endpoint, data) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json", ...getAuthHeaders() },
        body: JSON.stringify(data),
    });
    return handleResponse(response);
}

export async function put(endpoint, data) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json", ...getAuthHeaders() },
        body: JSON.stringify(data),
    });
    return handleResponse(response);
}

export async function del(endpoint) {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json", ...getAuthHeaders() },
    });
    return handleResponse(response);
}


async function handleResponse(response) {
    let data = {};
    try {
        data = await response.json();
    } catch (e) {

    }

    if (!response.ok) {
        const errorMsg = data.message || data.error || "API request failed";
        throw new Error(errorMsg);
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
    formData.append("patientId", patientId);
    formData.append("doctorId", doctorId);
    formData.append("notes", notes || "");
    formData.append("prescriptions", prescriptions || "");
    if (historyId !== null && historyId !== undefined) {
        formData.append("historyId", historyId);
    }
    if (updatedBy !== null && updatedBy !== undefined && updatedBy !== "") {
        formData.append("updatedBy", updatedBy);
    }
    if (fileObj) {
        formData.append("file", fileObj);
    }

    return fetch(`${BASE_URL}/api/medical-history`, {
        method: "POST",
        headers: {
            ...getAuthHeaders()
        },
        body: formData,
    }).then(handleResponse);
}

export function updateMedicalRecord(recordId, data) {
    return put(`/api/medical-history/${recordId}`, data);
}

export function deleteMedicalRecord(recordId) {
    return del(`/api/medical-history/${recordId}`);
}

export function getMedicalHistoryByPatient(patientId) {
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

export function createPatientHistory(patientId, doctorId, diagnosis, description, status, diagnosisDate, fileObj) {
    const formData = new FormData();
    formData.append("patientId", patientId);
    formData.append("doctorId", doctorId);
    formData.append("diagnosis", diagnosis || "");
    formData.append("description", description || "");
    formData.append("status", status || "Active");
    formData.append("diagnosisDate", diagnosisDate || "");
    if (fileObj) {
        formData.append("file", fileObj);
    }

    return fetch(`${BASE_URL}/api/patient-history`, {
        method: "POST",
        headers: { ...getAuthHeaders() },
        body: formData,
    }).then(handleResponse);
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

export function cancelAvailabilitySlot(availabilityId) {
    return put(`/api/availability/${availabilityId}/cancel`);
}

export function getAllAvailability() {
    return get("/api/availability/all");
}

// -----------------------------
// Account APIs
// -----------------------------
export function register(data) {
    return post("/api/account/register", data);
}

async function handleLoginResponse(response) {
    let data = {};
    try {
        data = await response.json();
    } catch (e) { }

    if (!response.ok) {
        if (response.status === 403) {
            throw new Error("Invalid username or password. Please try again.");
        }
        const errorMsg = data.message || "Login failed. Please try again.";
        throw new Error(errorMsg);
    }

    return data;
}

export async function login(data) {
    const response = await fetch(`${BASE_URL}/api/account/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json", ...getAuthHeaders() },
        body: JSON.stringify(data),
    });
    return handleLoginResponse(response);
}

export function getProfile() {
    return get("/api/account/profile");
}

export function getProfileById(userId) {
    return get(`/api/account/profile/${userId}`);
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

export function deactivateUser(id) {
    return put(`/api/account/deactivate/${id}`, {});
}

export function updateUserRole(id, role) {
    return put(`/api/account/update-role/${id}?role=${encodeURIComponent(role)}`, {});
}

export function getAllPatients() {
    return get("/api/account/patients");
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
