import { getProfile } from './apiService.js';

export function loadPatientName() {
  getProfile()
    .then(data => {
      const name = data.firstName || 'Patient';
      const greeting = getGreeting();
      document.getElementById('patient-name').innerText = `${greeting}, ${name}!`;
    })
    .catch(error => {
      console.error('Error loading patient name:', error);
      document.getElementById('patient-name').innerText = 'Hello, Patient!';
    });
}

export function loadDoctorName() {
  getProfile()
    .then(data => {
      const name = data.firstName || 'Doctor';
      const greeting = getGreeting();
      document.getElementById('doctor-name').innerText = `${greeting}, Dr. ${name}!`;
    })
    .catch(error => {
      console.error('Error loading doctor name:', error);
      document.getElementById('doctor-name').innerText = 'Hello, Doctor!';
    });
}

export function loadSecretaryName() {
  getProfile()
    .then(data => {
      const name = data.firstName || 'Secretary';
      const greeting = getGreeting();
      document.getElementById('secretary-name').innerText = `${greeting}, ${name}!`;
    })
    .catch(error => {
      console.error('Error loading secretary name:', error);
      document.getElementById('secretary-name').innerText = 'Hello, Secretary!';
    });
}

export function loadAdminName() {
  getProfile()
    .then(data => {
      const name = data.firstName || 'Admin';
      const greeting = getGreeting();
      document.getElementById('admin-name').innerText = `${greeting}, ${name}!`;
    })
    .catch(error => {
      console.error('Error loading admin name:', error);
      document.getElementById('admin-name').innerText = 'Hello, Admin!';
    });
}

function getGreeting() {
  const hour = new Date().getHours();

  if (hour >= 5 && hour < 12) {
    return 'Good morning';
  } else if (hour >= 12 && hour < 18) {
    return 'Good afternoon';
  } else {
    return 'Good evening';
  }
}