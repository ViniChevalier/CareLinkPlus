const loginForm = document.getElementById('loginForm');
loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const username = document.getElementById('username').value;
  const password = document.getElementById('password').value;
  const message = document.getElementById('message');
  message.textContent = 'Logging in...';

  try {
    const response = await fetch('http://localhost:8080/api/account/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem('token', data.token);
      message.textContent = 'Login successful! Redirecting...';
      message.style.color = 'green';

      setTimeout(() => {
        window.location.href = 'index.html';
      }, 1500);
    } else {
      const errorData = await response.json();
      message.textContent = errorData.message || 'Invalid credentials';
      message.style.color = 'red';
    }
  } catch (error) {
    message.textContent = 'Error connecting to server';
    message.style.color = 'red';
  }
});