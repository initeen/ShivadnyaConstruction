/*(function () {
  const themeToggle = document.getElementById('themeToggle');
  const backToTop = document.getElementById('backToTop');
  const waBtn = document.getElementById('whatsappBtn');
  const year = document.getElementById('year');

  // Set current year if #year exists
  if (year) {
    year.textContent = new Date().getFullYear();
  }

  // === Theme Toggle Logic (using data-bs-theme and localStorage) ===
  const storedTheme = localStorage.getItem('theme');
  if (storedTheme) {
    document.body.setAttribute('data-bs-theme', storedTheme);
    updateThemeLabel(storedTheme);
  }

  themeToggle?.addEventListener('click', () => {
    const current = document.body.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';
    document.body.setAttribute('data-bs-theme', current);
    localStorage.setItem('theme', current);
    updateThemeLabel(current);
  });

  function updateThemeLabel(currentTheme) {
    const isDark = currentTheme === 'dark';
    const icon = themeToggle.querySelector('i');
    const text = themeToggle.querySelector('span');

    icon.className = isDark ? 'fa-solid fa-sun' : 'fa-solid fa-moon';
    text.textContent = isDark
      ? text.getAttribute('data-light') || 'Light'
      : text.getAttribute('data-dark') || 'Dark';
  }

  // === Back to Top Button ===
  window.addEventListener('scroll', () => {
    if (window.scrollY > 300) {
      backToTop?.classList.remove('d-none');
    } else {
      backToTop?.classList.add('d-none');
    }
  });

  backToTop?.addEventListener('click', () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  });

  // === WhatsApp Button (dynamic link from meta tag) ===
  fetch('/actuator/info')
    .catch(() => {})
    .finally(() => {
      const phone = document.querySelector('meta[name="wa-phone"]')?.content || '';
      const msg = encodeURIComponent('Hello Shivaaddhnya, I want to enquire about a project.');
      waBtn.href = `https://wa.me/${phone}?text=${msg}`;
    });

  // === Contact Form Submission ===
  const form = document.getElementById('contactForm');
  const alertBox = document.getElementById('contactAlert');

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(form).entries());

    try {
      const res = await fetch('/api/contact', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });

      if (!res.ok) throw new Error('HTTP ' + res.status);

      showAlert('success');
      form.reset();
    } catch (err) {
      showAlert('danger');
    }
  });

  function showAlert(type) {
    if (!alertBox) return;

    alertBox.className = `alert alert-${type} mt-3`;
    const isMarathi = document.documentElement.lang === 'mr';
    alertBox.textContent =
      type === 'success'
        ? isMarathi
          ? 'धन्यवाद! आपली चौकशी पाठवली गेली आहे.'
          : 'Thank you! Your inquiry has been sent.'
        : isMarathi
        ? 'क्षमस्व, काहीतरी चूक झाली.'
        : 'Sorry, something went wrong.';

    alertBox.classList.remove('d-none');

    setTimeout(() => {
      alertBox.classList.add('d-none');
    }, 5000);
  }
})();

function changeModalImage(src) {
   document.getElementById('modalImage').src = src;
 }*/
 
 /*(function () {
   const themeToggle = document.getElementById('themeToggle');
   const backToTop = document.getElementById('backToTop');
   const waBtn = document.getElementById('whatsappBtn');
   const year = document.getElementById('year');

   // === Current Year in footer ===
   if (year) {
     year.textContent = new Date().getFullYear();
   }

   // === Theme Toggle (dark/light with localStorage) ===
   const storedTheme = localStorage.getItem('theme');
   if (storedTheme) {
     document.body.setAttribute('data-bs-theme', storedTheme);
     updateThemeLabel(storedTheme);
   }

   themeToggle?.addEventListener('click', () => {
     const current = document.body.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';
     document.body.setAttribute('data-bs-theme', current);
     localStorage.setItem('theme', current);
     updateThemeLabel(current);
   });

   function updateThemeLabel(currentTheme) {
     const isDark = currentTheme === 'dark';
     const icon = themeToggle.querySelector('i');
     const text = themeToggle.querySelector('span');

     if (icon) icon.className = isDark ? 'fa-solid fa-sun' : 'fa-solid fa-moon';
     if (text)
       text.textContent = isDark
         ? text.getAttribute('data-light') || 'Light'
         : text.getAttribute('data-dark') || 'Dark';
   }

   // === Back to Top Button ===
   window.addEventListener('scroll', () => {
     if (window.scrollY > 300) {
       backToTop?.classList.remove('d-none');
     } else {
       backToTop?.classList.add('d-none');
     }
   });

   backToTop?.addEventListener('click', () => {
     window.scrollTo({ top: 0, behavior: 'smooth' });
   });

   // === WhatsApp Button (dynamic link) ===
   fetch('/actuator/info')
     .catch(() => {})
     .finally(() => {
       const phone = document.querySelector('meta[name="wa-phone"]')?.content || '';
       const msg = encodeURIComponent('Hello Shivaaddhnya, I want to enquire about a project.');
       if (waBtn) waBtn.href = `https://wa.me/${phone}?text=${msg}`;
     });

   // === Contact Form AJAX Submission ===
   const form = document.getElementById('contactForm');
   const alertBox = document.getElementById('contactAlert');

   form?.addEventListener('submit', async (e) => {
     e.preventDefault();
     const btn = form.querySelector('button[type="submit"]');
     const data = Object.fromEntries(new FormData(form).entries());

     // Client-side validation
     if (!data.name || !data.email || !data.phone || !data.message) {
       showAlert('danger');
       return;
     }

     btn.disabled = true;
     btn.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Sending...';

     try {
       const res = await fetch('/api/contact', {
         method: 'POST',
         headers: { 'Content-Type': 'application/json' },
         body: JSON.stringify(data)
       });

       if (!res.ok) throw new Error('HTTP ' + res.status);

       showAlert('success');
       form.reset();
     } catch (err) {
       showAlert('danger');
     } finally {
       btn.disabled = false;
       btn.textContent = 'Send Inquiry';
     }
   });

   function showAlert(type) {
     if (!alertBox) return;

     alertBox.className = `alert alert-${type} mt-3`;
     const isMarathi = document.documentElement.lang === 'mr';
     alertBox.textContent =
       type === 'success'
         ? isMarathi
           ? 'धन्यवाद! आपली चौकशी पाठवली गेली आहे.'
           : 'Thank you! Your inquiry has been sent.'
         : isMarathi
         ? 'क्षमस्व, काहीतरी चूक झाली.'
         : 'Sorry, something went wrong.';

     alertBox.classList.remove('d-none');

     setTimeout(() => {
       alertBox.classList.add('d-none');
     }, 5000);
   }
 })();

 // === Gallery Modal Image Change with fade effect ===
 function changeModalImage(src) {
   const img = document.getElementById('modalImage');
   if (!img) return;
   img.classList.remove('show');
   img.onload = () => img.classList.add('show');
   img.src = src;
 }
 
 const toggleBtn = document.getElementById("langToggle");
   let currentLang = "en"; // default

   toggleBtn.addEventListener("click", () => {
     if (currentLang === "en") {
       currentLang = "mr";
       toggleBtn.textContent = "मराठी";
       window.location.href = "?lang=mr"; // redirect
     } else {
       currentLang = "en";
       toggleBtn.textContent = "EN";
       window.location.href = "?lang=en"; // redirect
     }
   });
   
   // === Theme Toggle ===
     const themeToggle = document.getElementById('themeToggle');
     const themeLabel = document.getElementById('themeLabel');

     // Load saved theme
     let currentTheme = localStorage.getItem('theme') || 'light';
     document.body.setAttribute('data-bs-theme', currentTheme);
     updateThemeUI(currentTheme);

     themeToggle.addEventListener('click', () => {
       currentTheme = currentTheme === 'dark' ? 'light' : 'dark';
       document.body.setAttribute('data-bs-theme', currentTheme);
       localStorage.setItem('theme', currentTheme);
       updateThemeUI(currentTheme);
     });

     function updateThemeUI(theme) {
       if(theme === 'dark'){
         themeToggle.classList.add('active');
         themeToggle.querySelector('i').className = 'fa-solid fa-sun me-1';
         themeLabel.textContent = 'Light';
       } else {
         themeToggle.classList.remove('active');
         themeToggle.querySelector('i').className = 'fa-solid fa-moon me-1';
         themeLabel.textContent = 'Dark';
       }
     }

     // === Language Toggle ===
     const langBtn = document.getElementById('langToggleBtn');
     const langLabel = document.getElementById('langLabel');

     let lang = new URLSearchParams(window.location.search).get('lang') || 'en';
     updateLangUI(lang);

     langBtn.addEventListener('click', () => {
       lang = lang === 'en' ? 'mr' : 'en';
       window.location.href = '?lang=' + lang;
     });

     function updateLangUI(lang) {
       if(lang === 'en') {
         langBtn.classList.add('active');
         langLabel.textContent = 'EN';
       } else {
         langBtn.classList.add('active');
         langLabel.textContent = 'मराठी';
       }
     }*/
	 
	 (function () {
	   // === Elements ===
	   const themeToggle = document.getElementById('themeToggle');
	   const themeLabel = document.getElementById('themeLabel');
	   const langBtn = document.getElementById('langToggleBtn');
	   const langLabel = document.getElementById('langLabel');
	   const backToTop = document.getElementById('backToTop');
	   const waBtn = document.getElementById('whatsappBtn');
	   const year = document.getElementById('year');
	   const form = document.getElementById('contactForm');
	   const alertBox = document.getElementById('contactAlert');

	   // === Current Year in footer ===
	   if (year) year.textContent = new Date().getFullYear();

	   // === Theme Toggle ===
	   let currentTheme = localStorage.getItem('theme') || 'light';
	   document.body.setAttribute('data-bs-theme', currentTheme);
	   updateThemeUI(currentTheme);

	   themeToggle?.addEventListener('click', () => {
	     currentTheme = currentTheme === 'dark' ? 'light' : 'dark';
	     document.body.setAttribute('data-bs-theme', currentTheme);
	     localStorage.setItem('theme', currentTheme);
	     updateThemeUI(currentTheme);
	   });

	   function updateThemeUI(theme) {
	     if (!themeToggle || !themeLabel) return;
	     if (theme === 'dark') {
	       themeToggle.classList.add('active');
	       themeToggle.querySelector('i').className = 'fa-solid fa-sun me-1';
	       themeLabel.textContent = 'Light';
	     } else {
	       themeToggle.classList.remove('active');
	       themeToggle.querySelector('i').className = 'fa-solid fa-moon me-1';
	       themeLabel.textContent = 'Dark';
	     }
	   }

	   // === Language Toggle ===
	   // LANGUAGE TOGGLE
	   // Get current language from URL
	   let lang = new URLSearchParams(window.location.search).get('lang') || 'en';
	   updateLangUI(lang);

	   langBtn?.addEventListener('click', () => {
	       // Toggle language
	       lang = lang === 'en' ? 'mr' : 'en';
	       window.location.search = '?lang=' + lang; // redirect
	   });

	  /* function updateLangUI(currentLang) {
	       if (!langBtn || !langLabel) return;

	       // Show the language **user will switch to** on button
	       if (currentLang === 'en') {
	           langLabel.textContent = 'मराठी'; // user can click to switch to Marathi
	       } else {
	           langLabel.textContent = 'EN'; // user can click to switch to English
	       }
	   }
*/

function updateLangUI(currentLang) {
  if (!langBtn || !langLabel) return;

  // Update label to show the language user will switch to
  if (currentLang === 'en') {
    langLabel.textContent = 'मराठी'; // Switch to Marathi

    // Update background color to indicate English is active
    langBtn.style.backgroundColor = '#198754'; // Blue for English (or any color you prefer)
    langBtn.style.color = '#fff'; // White text for contrast
  } else {
    langLabel.textContent = 'EN'; // Switch to English

    // Update background color to indicate Marathi is active
    langBtn.style.backgroundColor = '#ffc107'; // Yellow for Marathi (or any color you prefer)
    langBtn.style.color = '#000'; // Black text for contrast
  }
}

	   // === Back to Top Button ===
	   window.addEventListener('scroll', () => {
	     if (window.scrollY > 300) backToTop?.classList.remove('d-none');
	     else backToTop?.classList.add('d-none');
	   });

	   backToTop?.addEventListener('click', () => {
	     window.scrollTo({ top: 0, behavior: 'smooth' });
	   });

	   // === WhatsApp Button ===
	   fetch('/actuator/info')
	     .catch(() => {})
	     .finally(() => {
	       const phone = document.querySelector('meta[name="wa-phone"]')?.content || '';
	       const msg = encodeURIComponent('Hello Shivadnya Construction, I would like to enquire about one of your projects. Could you please provide more information?');
	       if (waBtn) waBtn.href = `https://wa.me/${phone}?text=${msg}`;
	     });

	   // === Contact Form AJAX Submission ===
	   form?.addEventListener('submit', async (e) => {
	     e.preventDefault();
	     const btn = form.querySelector('button[type="submit"]');
	     const data = Object.fromEntries(new FormData(form).entries());

	     // Client-side validation
	     if (!data.name || !data.email || !data.phone || !data.message) {
	       showAlert('danger');
	       return;
	     }

	     btn.disabled = true;
	     btn.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Sending...';

	     try {
	       const res = await fetch('/api/contact', {
	         method: 'POST',
	         headers: { 'Content-Type': 'application/json' },
	         body: JSON.stringify(data)
	       });

	       if (!res.ok) throw new Error('HTTP ' + res.status);

	       showAlert('success');
	       form.reset();
	     } catch (err) {
	       showAlert('danger');
	     } finally {
	       btn.disabled = false;
	       btn.textContent = 'Send Inquiry';
	     }
	   });

	   function showAlert(type) {
	     if (!alertBox) return;
	     alertBox.className = `alert alert-${type} mt-3`;
	     const isMarathi = document.documentElement.lang === 'mr';
	     alertBox.textContent =
	       type === 'success'
	         ? isMarathi
	           ? 'धन्यवाद! आपली चौकशी पाठवली गेली आहे.'
	           : 'Thank you! Your inquiry has been sent.'
	         : isMarathi
	         ? 'क्षमस्व, काहीतरी चूक झाली.'
	         : 'Sorry, something went wrong.';
	     alertBox.classList.remove('d-none');

	     setTimeout(() => {
	       alertBox.classList.add('d-none');
	     }, 5000);
	   }

	 })();

	 // === Gallery Modal Image Change with fade effect ===
	 function changeModalImage(src) {
	   const img = document.getElementById('modalImage');
	   if (!img) return;
	   img.classList.remove('show');
	   img.onload = () => img.classList.add('show');
	   img.src = src;
	 }

	 document.addEventListener('DOMContentLoaded', () => {
	    const navbarCollapse = document.getElementById('navbars');
	    const bsCollapse = new bootstrap.Collapse(navbarCollapse, { toggle: false });

	    let lastScrollTop = 0;

	    window.addEventListener('scroll', () => {
	      const isExpanded = navbarCollapse.classList.contains('show');

	      if (isExpanded) {
	        bsCollapse.hide(); // Close the menu
	      }
	    });
	  });
	  
	  (() => {
	      const form = document.getElementById('contactForm');

	      form.addEventListener('submit', function (event) {
	        if (!form.checkValidity()) {
	          event.preventDefault();
	          event.stopPropagation();
	        }
	        form.classList.add('was-validated');
	      }, false);
	    })();
		
		//estimate generation
		