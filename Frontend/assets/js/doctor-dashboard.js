import {
    getProfileById,
    getAppointmentsByDoctor,
    getDoctorAvailability,
    getPatientHistoriesByPatient,
    getMedicalHistoryByPatient
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

// Register modal show event
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

        // Load appointments
        const appointments = await getAppointmentsByDoctor(doctorId);

        // Load patients (extract unique patients from appointments)
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
        if (appointments.length === 0) {
            upcomingDiv.innerHTML = "<p>No upcoming appointments.</p>";
        } else {
            upcomingDiv.innerHTML = "<ul class='list-group'>" +
                appointments.slice(0, 5).map(appt => {
                    const dateObj = new Date(appt.appointmentDateTime || appt.date);
                    const formattedDate = isNaN(dateObj.getTime()) ? "Unknown date" :
                        dateObj.toLocaleString('en-IE', {
                            day: '2-digit',
                            month: '2-digit',
                            year: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit',
                            hour12: false
                        });

                    const patientId = appt.patientId;
                    const patientName = patientsMap?.[patientId] || "Unknown";

                    return `
                      <li class='list-group-item'>
                        ${formattedDate} - ${patientName}
                      </li>
                    `;
                }).join("") +
                "</ul>";
        }

        // Calculate daily and monthly indicators
        const today = new Date().toISOString().split("T")[0];
        let todaysAppointments = 0;
        const monthlyCount = {};

        for (const appt of appointments) {
            if (appt.date && appt.date.startsWith(today)) {
                todaysAppointments++;
            }

            const month = appt.date?.slice(0, 7); // "YYYY-MM"
            if (month) {
                monthlyCount[month] = (monthlyCount[month] || 0) + 1;
            }
        }

        const patientsDiv = document.getElementById("myPatients");
        if (Object.keys(patientsMap).length === 0) {
            patientsDiv.innerHTML = "<p>No patients found.</p>";
        } else {
            patientsDiv.innerHTML = Object.entries(patientsMap).slice(0, 5).map(([id, name]) => `
              <div class="d-grid gap-2 mb-2">
                <button class="btn btn-sm btn-outline-primary" onclick="viewPatientProfile('${id}')">${name}</button>
              </div>
            `).join("");
        }

        // Function to handle patient profile redirection
        window.viewPatientProfile = function(patientId) {
            localStorage.setItem("selectedPatientId", patientId);
            window.location.href = "patient-profile.html";
        };

        // Populate today's indicators
        const indicatorsList = document.getElementById("dailyIndicators");
        indicatorsList.innerHTML = `
            <li class="list-group-item"><strong>Appointments Today:</strong> ${todaysAppointments}</li>
            <li class="list-group-item"><strong>Total Patients:</strong> ${Object.keys(patientsMap).length}</li>
        `;

        // Load availability (only unbooked slots)
        const availability = await getDoctorAvailability(doctorId);
        const availabilityDiv = document.getElementById("availabilityStatus");
        const unbookedAvailability = availability.filter(slot => slot.status === 'AVAILABLE');

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

        // Setup modal content
        const modalBody = document.getElementById("monthlyIndicatorsContent");
        if (modalBody) {
            const sortedMonths = Object.entries(monthlyCount).sort().reverse();
            modalBody.innerHTML = sortedMonths.length
                ? "<ul class='list-group'>" +
                  sortedMonths.map(([month, count]) => `
                      <li class='list-group-item'>
                        <strong>${month}</strong>: ${count} appointments
                      </li>
                  `).join("") +
                  "</ul>"
                : "<p>No monthly data available.</p>";

            // Create canvas for chart
            const canvas = document.createElement("canvas");
            canvas.id = "monthlyChart";
            modalBody.appendChild(canvas);

            const ctx = canvas.getContext("2d");
            const chartData = {
                labels: sortedMonths.map(([month]) => month).reverse(),
                datasets: [{
                    label: 'Appointments per Month',
                    data: sortedMonths.map(([, count]) => count).reverse(),
                    backgroundColor: 'rgba(54, 162, 235, 0.6)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            };

            new Chart(ctx, {
                type: 'bar',
                data: chartData,
                options: {
                    responsive: true,
                    plugins: {
                        legend: { display: false },
                        title: {
                            display: true,
                            text: 'Monthly Appointment Overview'
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            precision: 0
                        }
                    }
                }
            });
        }

    } catch (error) {
        console.error("Error loading dashboard:", error);
        alert("Failed to load dashboard data.");
    }
});