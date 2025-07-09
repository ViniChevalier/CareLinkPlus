document.getElementById("forgotPasswordForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const messageDiv = document.getElementById("message");

  fetch("http://localhost:8080/api/account/reset", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ username: username })
  })
    .then(response => {
      if (response.ok) {
        return response.text();
      } else {
        throw new Error("Failed to send reset link. Please try again.");
      }
    })
    .then(data => {
      messageDiv.innerHTML = `<span class="text-success">Reset link sent! Please check your email.</span>`;
    })
    .catch(error => {
      messageDiv.innerHTML = `<span class="text-danger">${error.message}</span>`;
    });
});