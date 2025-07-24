import { getGoogleMapsApiKey, registerPatient } from './apiService.js';

document.getElementById('registerPatientForm').addEventListener('submit', async function (e) {
  e.preventDefault();

  const registerBtn = document.getElementById("registerBtn");
  const spinner = document.getElementById("spinner");
  const toastEl = document.getElementById("successToast");

  registerBtn.disabled = true;
  spinner.classList.remove("d-none");

  const patientData = {
    firstName: document.getElementById('firstName').value.trim(),
    lastName: document.getElementById('lastName').value.trim(),
    email: document.getElementById('email').value.trim(),
    phoneNumber: document.getElementById('phoneNumber').value.trim(),
    dateOfBirth: document.getElementById('dateOfBirth').value,
    gender: document.getElementById('gender').value,
    address: document.getElementById('address').value.trim(),
    city: document.getElementById('city').value.trim(),
    country: document.getElementById('country').value.trim(),
    notificationPreference: document.getElementById('notificationPreference').value
  };

  try {
    const response = await registerPatient(patientData);
    document.getElementById('registerPatientForm').reset();
    registerBtn.disabled = false;
    spinner.classList.add("d-none");
    toastEl.querySelector('.toast-body').textContent = `Patient ${response.firstName} ${response.lastName} registered successfully.`;
    new bootstrap.Toast(toastEl).show();
  } catch (error) {
    registerBtn.disabled = false;
    spinner.classList.add("d-none");
    console.error('Registration failed:', error);
    alert('Failed to register patient. Please try again.');
  }
});

function loadGoogleMaps(callback) {
  getGoogleMapsApiKey()
    .then(key => {
      const script = document.createElement("script");
      script.src = `https://maps.googleapis.com/maps/api/js?key=${key}&libraries=places`;
      script.defer = true;
      script.onload = callback;
      document.head.appendChild(script);
    })
    .catch(err => console.error("Failed to load Google Maps key:", err));
}

function initAutocomplete() {
  const addressInput = document.getElementById("address");
  const autocomplete = new google.maps.places.Autocomplete(addressInput, {
    types: ["address"],
    componentRestrictions: { country: ["ie"] },
    fields: ["address_components", "formatted_address"],
  });

  autocomplete.addListener("place_changed", function () {
    const place = autocomplete.getPlace();
    if (place.formatted_address) {
      addressInput.value = place.formatted_address;
    }

    let city = "";
    let country = "";

    if (place.address_components) {
      place.address_components.forEach((component) => {
        if (component.types.includes("locality") || component.types.includes("postal_town")) {
          city = component.long_name;
        }
        if (component.types.includes("country")) {
          country = component.long_name;
        }
      });
    }

    document.getElementById("city").value = city;
    document.getElementById("country").value = country;
  });
}

if (window.intlTelInput) {
  const phoneInput = document.querySelector("#phoneNumber");
  window.iti = window.intlTelInput(phoneInput, {
    initialCountry: "ie",
    separateDialCode: true,
    utilsScript: "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js",
  });
} else {
  console.error("intlTelInput not loaded");
}

loadGoogleMaps(initAutocomplete);

function validateFormFields() {
  const requiredFields = [
    'firstName',
    'lastName',
    'email',
    'phoneNumber',
    'dateOfBirth',
    'gender',
    'address',
    'city',
    'country',
    'notificationPreference'
  ];

  const allFilled = requiredFields.every(id => {
    const el = document.getElementById(id);
    return el && el.value.trim() !== "";
  });

  document.getElementById("registerBtn").disabled = !allFilled;
}

document.querySelectorAll("#registerPatientForm input, #registerPatientForm select, #registerPatientForm textarea").forEach(input => {
  input.addEventListener("input", validateFormFields);
});