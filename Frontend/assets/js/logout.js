document.getElementById('logout-link').addEventListener('click', function (e) {
    e.preventDefault();
    
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    localStorage.removeItem("role");

    window.location.href = "index.html";
});