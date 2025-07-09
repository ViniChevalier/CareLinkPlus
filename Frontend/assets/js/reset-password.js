document.getElementById("resetPasswordForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const newPassword = document.getElementById("newPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;
  const messageDiv = document.getElementById("message");

  // Pega token da URL
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get("token");

  if (!token) {
    messageDiv.innerHTML = `<span class="text-danger">Invalid or missing token.</span>`;
    return;
  }

  if (newPassword !== confirmPassword) {
    messageDiv.innerHTML = `<span class="text-danger">Passwords do not match.</span>`;
    return;
  }

  fetch("http://localhost:8080/api/account/update-password", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      token: token,
      newPassword: newPassword
    })
  })
    .then(response => {
      if (response.ok) {
        return response.text();
      } else {
        throw new Error("Failed to reset password. Please try again.");
      }
    })
    .then(data => {
      messageDiv.innerHTML = `<span class="text-success">Password reset successfully! You can now log in.</span>`;
    })
    .catch(error => {
      messageDiv.innerHTML = `<span class="text-danger">${error.message}</span>`;
    });
});