import { api } from '../api.js';

export function render() {
  const app = document.getElementById('app');
  app.innerHTML = `
    <div class="login-wrapper">
      <div class="card login-card">
        <h1>Fuchs OMS</h1>
        <div style="text-align:center;margin-bottom:0.5rem">
          <button id="register-theme-toggle" class="theme-toggle-inline" title="Toggle dark mode">${document.documentElement.getAttribute('data-theme') === 'dark' ? '🌙' : '☀️'}</button>
        </div>
        <p class="subtitle">Vendor Registration</p>
        <div id="register-msg"></div>
        <form id="register-form">
          <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" required autocomplete="username" minlength="3" maxlength="50" pattern="^[a-zA-Z0-9_-]+$" title="Letters, numbers, underscores, and hyphens only">
          </div>
          <div class="form-group">
            <label for="vendorName">Vendor Name</label>
            <input type="text" id="vendorName" required minlength="2" maxlength="100">
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" required autocomplete="new-password" minlength="8" maxlength="100">
            <small class="form-hint">At least 8 characters with uppercase, lowercase, and number</small>
          </div>
          <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" required autocomplete="new-password">
          </div>
          <button type="submit" class="btn" style="width:100%">Register</button>
        </form>
        <p class="register-link">Already have an account? <a href="#login">Login here</a></p>
      </div>
    </div>`;

  document.getElementById('register-theme-toggle').addEventListener('click', () => {
    const current = document.documentElement.getAttribute('data-theme');
    const next = current === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', next);
    localStorage.setItem('theme', next);
    document.getElementById('register-theme-toggle').textContent = next === 'dark' ? '🌙' : '☀️';
    const navToggle = document.getElementById('theme-toggle');
    if (navToggle) navToggle.textContent = next === 'dark' ? '🌙' : '☀️';
  });

  document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const msgEl = document.getElementById('register-msg');
    msgEl.innerHTML = '';

    const username = document.getElementById('username').value.trim();
    const vendorName = document.getElementById('vendorName').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    // Client-side validation
    if (password !== confirmPassword) {
      msgEl.innerHTML = `<div class="msg msg-error">Passwords do not match</div>`;
      return;
    }

    // Password strength check
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/;
    if (!passwordRegex.test(password)) {
      msgEl.innerHTML = `<div class="msg msg-error">Password must contain at least one uppercase letter, one lowercase letter, and one number</div>`;
      return;
    }

    try {
      await api('/auth/register', {
        method: 'POST',
        body: JSON.stringify({ username, password, vendorName })
      });
      msgEl.innerHTML = `<div class="msg msg-success">Registration successful! Redirecting to login...</div>`;
      setTimeout(() => {
        window.location.hash = '#login';
      }, 1500);
    } catch (err) {
      msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
    }
  });
}
