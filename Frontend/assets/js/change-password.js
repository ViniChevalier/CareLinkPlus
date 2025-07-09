document.getElementById('changePasswordForm').addEventListener('submit', async function (e) {
  e.preventDefault();

  const currentPassword = document.getElementById('currentPassword').value;
  const newPassword = document.getElementById('newPassword').value;
  const confirmPassword = document.getElementById('confirmPassword').value;
  const messageDiv = document.getElementById('message');

  if (newPassword !== confirmPassword) {
    messageDiv.innerHTML = '<span style="color:red;">Passwords do not match.</span>';
    return;
  }

  const token = localStorage.getItem('token');
  if (!token) {
    messageDiv.innerHTML = '<span style="color:red;">You are not logged in.</span>';
    return;
  }

  try {
    const response = await fetch('/api/account/change-password', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
      },
      body: JSON.stringify({
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
      })
    });

    if (response.ok) {
      messageDiv.innerHTML = '<span style="color:green;">Password changed successfully.</span>';
      document.getElementById('changePasswordForm').reset();
    } else {
      const data = await response.json();
      messageDiv.innerHTML = `<span style="color:red;">${data.message || 'Error changing password.'}</span>`;
    }
  } catch (error) {
    messageDiv.innerHTML = '<span style="color:red;">Server error. Please try again later.</span>';
  }
});