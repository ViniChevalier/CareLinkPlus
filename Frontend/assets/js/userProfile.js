export function loadPatientName() {
  fetch('/api/user/profile', {
    headers: {
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  })
    .then(response => {
      if (!response.ok) {
        throw new Error(`Failed to fetch profile: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      const name = data.name || 'Patient';
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