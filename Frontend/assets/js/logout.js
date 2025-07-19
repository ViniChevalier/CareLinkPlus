document.getElementById('logout-link').addEventListener('click', function (e) {
    e.preventDefault();
    
    localStorage.clear();
    
    window.location.href = "index.html";
});