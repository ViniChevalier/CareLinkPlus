
import { getProfile, updateProfile } from './apiService.js';
import { logout } from './logout.js';

window.addEventListener("load", async () => {
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

    try {
      await updateProfile(data);
      messageDiv.className = "alert alert-success text-center";
      messageDiv.textContent = "Profile updated successfully!";
      messageDiv.classList.remove("d-none");
    } catch (error) {
      messageDiv.className = "alert alert-danger text-center";
      messageDiv.textContent = error.message || "Failed to update profile.";
      messageDiv.classList.remove("d-none");
    }
  });
});

document.getElementById("logout-link").addEventListener("click", function (e) {
  e.preventDefault();
  logout();
});