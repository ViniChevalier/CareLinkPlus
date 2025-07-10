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