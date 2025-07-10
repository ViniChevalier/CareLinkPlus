import { updatePassword } from './apiService.js';

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
    await updatePassword({
      oldPassword: currentPassword,
      newPassword: newPassword
    });

    messageDiv.innerHTML = '<span style="color:green;">Password changed successfully.</span>';
    document.getElementById('changePasswordForm').reset();
  } catch (error) {
    messageDiv.innerHTML = `<span style="color:red;">${error.message || 'Error changing password.'}</span>`;
  }
});