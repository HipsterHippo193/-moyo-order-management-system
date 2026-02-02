import { isLoggedIn, clearAuth, getUsername } from './api.js';
import { register, init } from './router.js';
import { render as loginRender } from './pages/login.js';
import { render as dashboardRender } from './pages/dashboard.js';
import { render as ordersRender } from './pages/orders.js';

const navbar = document.getElementById('navbar');

function guard(renderFn) {
  return () => {
    if (!isLoggedIn()) {
      window.location.hash = '#login';
      return;
    }
    navbar.classList.remove('hidden');
    document.getElementById('vendor-name').textContent = getUsername();
    // Highlight active nav link
    document.querySelectorAll('.nav-link').forEach(a => {
      a.classList.toggle('active', a.getAttribute('href') === window.location.hash);
    });
    renderFn();
  };
}

register('#login', () => {
  if (isLoggedIn()) { window.location.hash = '#dashboard'; return; }
  navbar.classList.add('hidden');
  loginRender();
});

register('#dashboard', guard(dashboardRender));
register('#orders', guard(ordersRender));

document.getElementById('logout-btn').addEventListener('click', () => {
  clearAuth();
  window.location.hash = '#login';
});

init();
