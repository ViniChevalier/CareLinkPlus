

// Initialize intl-tel-input
if (window.intlTelInput) {
  const phoneInput = document.querySelector("#phoneNumber");
  window.iti = window.intlTelInput(phoneInput, {
    initialCountry: "ie",
    separateDialCode: true,
    utilsScript: "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js",
  });
} else {
  console.error("intlTelInput not loaded");
}

// Google Maps API check
if (window.google && window.google.maps) {
  const addressInput = document.getElementById("address");
  const autocomplete = new google.maps.places.Autocomplete(addressInput, {
    types: ["address"],
    componentRestrictions: { country: ["ie"] },
    fields: ["address_components", "formatted_address"],
  });

  autocomplete.addListener("place_changed", function () {
    const place = autocomplete.getPlace();
    if (place.formatted_address) {
      addressInput.value = place.formatted_address;
    }

    let city = "";
    let country = "";

    if (place.address_components) {
      place.address_components.forEach((component) => {
        if (component.types.includes("locality") || component.types.includes("postal_town")) {
          city = component.long_name;
        }
        if (component.types.includes("country")) {
          country = component.long_name;
        }
      });
    }

    document.getElementById("city").value = city;
    document.getElementById("country").value = country;
  });
} else {
  console.error("Google Maps API not loaded");
}