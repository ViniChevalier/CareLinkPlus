// assets/js/footer.js
document.addEventListener("DOMContentLoaded", () => {
  const footerHTML = `
  <footer class="footer pt-95 img-bg" style="background-image:url('assets/img/bg/footer-bg.jpg');">
    <div class="container">
      <div class="footer-widget-wrapper">
        <div class="row">
          <div class="col-xl-4 col-lg-5 col-md-7">
            <div class="footer-widget mb-30">
              <a href="index.html" class="logo">
                <img src="assets/img/CareLink_Logo.png" alt="CareLink+ Logo">
              </a>
              <p>Transforming healthcare through intelligent connection. At CareLink+, we empower you to manage your
                health seamlessly, with trusted professionals and advanced technology at your side.</p>
              <div class="footer-social-links">
                <ul>
                  <li><a href="https://facebook.com" target="_blank"><i class="lni lni-facebook-filled"></i></a></li>
                  <li><a href="https://twitter.com" target="_blank"><i class="lni lni-twitter-filled"></i></a></li>
                  <li><a href="https://linkedin.com" target="_blank"><i class="lni lni-linkedin-original"></i></a></li>
                  <li><a href="https://instagram.com" target="_blank"><i class="lni lni-instagram-original"></i></a></li>
                </ul>
              </div>
            </div>
          </div>
          <div class="col-xl-2 col-lg-3 col-md-7 mx-auto">
            <div class="footer-widget mb-30">
              <h4>Quick Links</h4>
              <ul class="footer-links">
                <li><a href="https://www2.hse.ie/">HSE Ireland</a></li>
                <li><a href="index.html">Home</a></li>
                <li><a href="login.html">Login</a></li>
              </ul>
            </div>
          </div>
          <div class="col-xl-3 col-lg-4 col-md-7 ms-auto">
            <div class="footer-widget mb-30">
              <h4>Contact Us</h4>
              <ul class="footer-contact">
                <li>Email: <a href="mailto:support@carelinkplus.com">support@carelinkplus.com</a></li>
                <li>Phone: +353 1 234 5678</li>
                <li>Address: Dublin, Ireland</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div class="footer-bottom text-center pt-20 pb-20">
        <p>&copy; 2025 CareLink+. All rights reserved. Empowering your health journey.</p>
      </div>
    </div>
  </footer>
  `;

  const footerContainer = document.createElement("div");
  footerContainer.innerHTML = footerHTML;
  document.body.appendChild(footerContainer);
});