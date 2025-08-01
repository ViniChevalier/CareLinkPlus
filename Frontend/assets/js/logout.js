document.addEventListener("DOMContentLoaded", function () {
  const logoutLink = document.getElementById('logout-link');
  if (logoutLink) {
    logoutLink.addEventListener('click', function (e) {
      e.preventDefault();
      localStorage.clear();
      window.location.href = "login.html";
    });
  }
});