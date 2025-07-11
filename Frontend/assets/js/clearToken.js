function clearStoredToken() {
  localStorage.removeItem("token");
  sessionStorage.removeItem("token");
  document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
}

window.addEventListener('DOMContentLoaded', () => {
  clearStoredToken();
});