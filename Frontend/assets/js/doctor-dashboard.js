import {
    getProfileById,
    getAppointmentsByDoctor,
    getDoctorAvailability,
    cancelAvailabilitySlot
} from './apiService.js';

import { loadDoctorName } from './userProfile.js';

export async function loadPatientsInModal() {
    const modalBody = document.getElementById('patientsModalBody');
    modalBody.innerHTML = '<p>Loading patients...</p>';

    try {
        const doctorId = localStorage.getItem("userId");
        if (!doctorId) throw new Error("Doctor not logged in.");

        const appointments = await getAppointmentsByDoctor(doctorId);
        const patientIds = [...new Set(appointments.map(a => a.patientId).filter(Boolean))];
        const patients = [];

        for (const id of patientIds) {
            try {
                const profile = await getProfileById(id);
                const fullName = `${profile.firstName || ''} ${profile.lastName || ''}`.trim();
                patients.push({ id, name: fullName });
            } catch (e) {
                patients.push({ id, name: "Unknown" });
            }
        }

        if (patients.length === 0) {
            modalBody.innerHTML = '<p>No patients with appointments found.</p>';
            return;
        }

        modalBody.innerHTML = '<ul class="list-group">' +
            patients.map(p => `
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    ${p.name}
                    <button class="btn btn-sm btn-outline-primary" onclick="viewPatientProfile('${p.id}')">View Profile</button>
                </li>
            `).join('') +
            '</ul>';
    } catch (err) {
        console.error(err);
        modalBody.innerHTML = '<p class="text-danger">Error loading patients.</p>';
    }
}


document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('patientsModal');
    if (modal) {
        modal.addEventListener('show.bs.modal', () => {
            loadPatientsInModal();
        });
    }
});

document.addEventListener("DOMContentLoaded", async () => {
    loadDoctorName();
    const doctorId = localStorage.getItem("userId");

    if (!doctorId) {
        alert("User not logged in.");
        window.location.href = "login.html";
        return;
    }

    try {
        const patientsMap = {};
        const appointments = await getAppointmentsByDoctor(doctorId);

        for (const appt of appointments) {
            if (appt.patientId && !patientsMap[appt.patientId]) {
                try {
                    const profile = await getProfileById(appt.patientId);
                    const fullName = `${profile.firstName || ''} ${profile.lastName || ''}`.trim();
                    patientsMap[appt.patientId] = fullName;
                } catch (e) {
                    console.error("Error loading patient profile:", e);
                    patientsMap[appt.patientId] = "Unknown";
                }
            }
        }

        const upcomingDiv = document.getElementById("upcomingAppointments");
        if (upcomingDiv) {
            const now = new Date();
            const validStatuses = ['BOOKED', 'SCHEDULED', 'PENDING', 'CONFIRMED', 'ATTENDED'];
            const upcomingAppointments = appointments.filter(appt => {
                const dateObj = new Date(appt.appointmentDateTime || appt.date);
                return !isNaN(dateObj.getTime()) &&
                    dateObj > now &&
                    validStatuses.includes(appt.appointmentStatus);
            });

            if (upcomingAppointments.length === 0) {
                upcomingDiv.innerHTML = "<p>No upcoming appointments.</p>";
            } else {
                upcomingDiv.innerHTML = "<ul class='list-group'>" +
                    upcomingAppointments.slice(0, 5).map(appt => {
                        const dateObj = new Date(appt.appointmentDateTime || appt.date);
                        const formattedDate = dateObj.toLocaleString('en-IE', {
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit',
                            hour12: false
                        });
                        const patientId = appt.patientId;
                        const patientName = patientsMap?.[patientId] || "Unknown";
                        return `<li class='list-group-item'>${formattedDate} - ${patientName}</li>`;
                    }).join("") +
                    "</ul>";
            }
        }

        const today = new Date().toISOString().split("T")[0];
        let todaysAppointments = 0;
        const monthlyCount = {};

        for (const appt of appointments) {
            if (appt.date && appt.date.startsWith(today)) {
                todaysAppointments++;
            }
            const month = appt.date?.slice(0, 7);
            if (month) {
                monthlyCount[month] = (monthlyCount[month] || 0) + 1;
            }
        }

        const patientsDiv = document.getElementById("myPatients");
        if (patientsDiv) {
            if (Object.keys(patientsMap).length === 0) {
                patientsDiv.innerHTML = "<p>No patients found.</p>";
            } else {
                patientsDiv.innerHTML = Object.entries(patientsMap).slice(0, 5).map(([id, name]) => `
                  <div class="d-grid gap-2 mb-2">
                    <button class="btn btn-sm btn-outline-primary" onclick="viewPatientProfile('${id}')">${name}</button>
                  </div>
                `).join("");
            }
        }

        window.viewPatientProfile = function(patientId) {
            localStorage.setItem("selectedPatientId", patientId);
            window.location.href = "patient_profile.html";
        };

        let availability = [];
        try {
            availability = await getDoctorAvailability(doctorId);
        } catch (error) {
            console.error("Error fetching availability:", error);
            availability = [];
        }

        const availabilityDiv = document.getElementById("availabilityStatus");
        const unbookedAvailability = availability.filter(slot => slot.status === 'AVAILABLE');

        if (availabilityDiv) {
            if (unbookedAvailability.length === 0) {
                availabilityDiv.innerHTML = "<p>No available slots at the moment.</p>";
            } else {
                const sortedSlots = unbookedAvailability.sort((a, b) => {
                    const dateTimeA = new Date(`${a.availableDate}T${a.startTime}`);
                    const dateTimeB = new Date(`${b.availableDate}T${b.startTime}`);
                    return dateTimeA - dateTimeB;
                });

                availabilityDiv.innerHTML = "<ul class='list-group'>" +
                    sortedSlots.slice(0, 5).map(slot => {
                        const dateTime = new Date(`${slot.availableDate}T${slot.startTime}`);
                        const endTime = slot.endTime?.substring(0, 5) || "??:??";
                        const formattedStart = dateTime.toLocaleString('en-IE', {
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit',
                            hour12: false
                        });
                        return `<li class='list-group-item'>${formattedStart} - ${endTime}</li>`;
                    }).join("") +
                    "</ul>";
            }
        }

    } catch (error) {
        console.error("Error loading dashboard:", error);
        alert("Failed to load dashboard data.");
    }
});

async function loadAvailabilityInModal() {
    const doctorId = localStorage.getItem("userId");
    const modalBody = document.getElementById("availabilityModalBody");
    modalBody.innerHTML = "<p>Loading availability...</p>";

    try {
        const availability = await getDoctorAvailability(doctorId);
        const availableSlots = availability.filter(slot => slot.status === 'AVAILABLE');

        if (availableSlots.length === 0) {
            modalBody.innerHTML = "<p>No available slots found.</p>";
            return;
        }

        modalBody.innerHTML = "<ul class='list-group'>" +
            availableSlots.map(slot => {
                const dateTime = new Date(`${slot.availableDate}T${slot.startTime}`);
                const endTime = slot.endTime?.substring(0, 5) || "??:??";
                const formattedStart = dateTime.toLocaleString('en-IE', {
                    day: '2-digit',
                    month: '2-digit',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false
                });

                return `
                    <li class='list-group-item d-flex justify-content-between align-items-center'>
                      ${formattedStart} - ${endTime}
                      <button class="btn btn-sm btn-outline-danger" onclick="cancelSlot('${slot.id}')">Cancel</button>
                    </li>
                `;
            }).join("") +
            "</ul>";
    } catch (err) {
        console.error(err);
        modalBody.innerHTML = "<p class='text-danger'>Failed to load availability.</p>";
    }
}

window.cancelSlot = async function(slotId) {
    const doctorId = localStorage.getItem("userId");
    if (!doctorId) {
        alert("User not logged in.");
        return;
    }

    try {
        await cancelAvailabilitySlot(slotId);
        loadAvailabilityInModal();
    } catch (err) {
        console.error("Error cancelling slot:", err);
        alert("Failed to cancel slot.");
    }
};

document.addEventListener("DOMContentLoaded", () => {
    const availabilityModal = document.getElementById("availabilityModal");
    if (availabilityModal) {
        availabilityModal.addEventListener("show.bs.modal", loadAvailabilityInModal);
    }
});