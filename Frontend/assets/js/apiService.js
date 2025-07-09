export function fetchAppointments() {
  return fetch('/api/appointments/upcoming', {
    headers: {
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  }).then(response => response.json());
}

export function fetchMedications() {
  return fetch('/api/medications/current', {
    headers: {
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  }).then(response => response.json());
}