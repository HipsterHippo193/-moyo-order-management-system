import { isLoggedIn, clearAuth, getUsername } from './api.js';
import { register, init } from './router.js';
import { render as loginRender } from './pages/login.js';
import { render as registerRender } from './pages/register.js';
import { render as dashboardRender } from './pages/dashboard.js';
import { render as catalogRender } from './pages/catalog.js';
import { render as ordersRender } from './pages/orders.js';

const navbar = document.getElementById('navbar');

// Theme toggle
const themeToggle = document.getElementById('theme-toggle');
const savedTheme = localStorage.getItem('theme') || 'light';
document.documentElement.setAttribute('data-theme', savedTheme);
themeToggle.textContent = savedTheme === 'dark' ? '🌙' : '☀️';

themeToggle.addEventListener('click', () => {
  const current = document.documentElement.getAttribute('data-theme');
  const next = current === 'dark' ? 'light' : 'dark';
  document.documentElement.setAttribute('data-theme', next);
  localStorage.setItem('theme', next);
  themeToggle.textContent = next === 'dark' ? '🌙' : '☀️';
});

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

register('#register', () => {
  if (isLoggedIn()) { window.location.hash = '#dashboard'; return; }
  navbar.classList.add('hidden');
  registerRender();
});

register('#dashboard', guard(dashboardRender));
register('#catalog', guard(catalogRender));
register('#orders', guard(ordersRender));

document.getElementById('logout-btn').addEventListener('click', () => {
  clearAuth();
  window.location.hash = '#login';
});

init();
