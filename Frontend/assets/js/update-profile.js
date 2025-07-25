import { getProfile, updateProfile, getGoogleMapsApiKey } from './apiService.js';

window.addEventListener("load", async () => {
  // Initialize intlTelInput
  if (window.intlTelInput) {
    const phoneInput = document.getElementById("phoneNumber");
    window.iti = window.intlTelInput(phoneInput, {
      initialCountry: "ie",
      separateDialCode: true,
      utilsScript: "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js",
    });
  } else {
    console.error("intlTelInput not loaded");
  }

  // Load Google Maps for address autocomplete
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

  loadGoogleMaps(() => {
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
  });

  const form = document.getElementById("updateProfileForm");
  const messageDiv = document.getElementById("message");

  try {
    const profile = await getProfile();
    document.getElementById("firstName").value = profile.firstName || "";
    document.getElementById("lastName").value = profile.lastName || "";
    document.getElementById("email").value = profile.email || "";
    if (profile.dateOfBirth) {
      const parts = profile.dateOfBirth.split('/');
      if (parts.length === 3) {
        const formattedDate = `${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}`;
        document.getElementById("dateOfBirth").value = formattedDate;
      } else {
        console.error("Invalid date format received:", profile.dateOfBirth);
        document.getElementById("dateOfBirth").value = "";
      }
    } else {
      document.getElementById("dateOfBirth").value = "";
    }
    document.getElementById("gender").value = profile.gender || "";
    document.getElementById("address").value = profile.address || "";
    document.getElementById("city").value = profile.city || "";
    document.getElementById("country").value = profile.country || "";

    if (window.iti && profile.phoneNumber) {
      window.iti.setNumber(profile.phoneNumber);
    }

    const prefs = profile.notificationPreference ? profile.notificationPreference.split(",") : [];
    document.getElementById("prefEmail").checked = prefs.includes("Email");
    document.getElementById("prefCall").checked = prefs.includes("Call");
    document.getElementById("prefSMS").checked = prefs.includes("SMS");
    document.getElementById("prefLetter").checked = prefs.includes("Letter");
  } catch (error) {
    console.error("Failed to load profile:", error);
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const preferences = [];
    if (document.getElementById("prefEmail").checked) preferences.push("Email");
    if (document.getElementById("prefCall").checked) preferences.push("Call");
    if (document.getElementById("prefSMS").checked) preferences.push("SMS");
    if (document.getElementById("prefLetter").checked) preferences.push("Letter");

    const data = {
      firstName: document.getElementById("firstName").value,
      lastName: document.getElementById("lastName").value,
      email: document.getElementById("email").value,
      phoneNumber: window.iti ? window.iti.getNumber() : "",
      dateOfBirth: document.getElementById("dateOfBirth").value,
      gender: document.getElementById("gender").value,
      address: document.getElementById("address").value,
      city: document.getElementById("city").value,
      country: document.getElementById("country").value,
      notificationPreference: preferences.join(","),
    };

    const originalButtonHTML = form.querySelector("button[type='submit']").innerHTML;
    const submitButton = form.querySelector("button[type='submit']");
    submitButton.disabled = true;
    submitButton.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Updating...`;

    try {
      await updateProfile(data);
      messageDiv.className = "alert alert-success text-center";
      messageDiv.textContent = "Profile updated successfully!";
      messageDiv.classList.remove("d-none");
      submitButton.disabled = false;
      submitButton.innerHTML = originalButtonHTML;
      setTimeout(() => {
        messageDiv.classList.add("d-none");
      }, 5000);
    } catch (error) {
      messageDiv.className = "alert alert-danger text-center";
      messageDiv.textContent = error.message || "Failed to update profile.";
      messageDiv.classList.remove("d-none");
      submitButton.disabled = false;
      submitButton.innerHTML = originalButtonHTML;
    }
  });
});