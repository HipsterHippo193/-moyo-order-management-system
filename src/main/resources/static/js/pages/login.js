import { api, saveAuth } from '../api.js';

export function render() {
  const app = document.getElementById('app');
  app.innerHTML = `
    <div class="login-wrapper">
      <div class="card login-card">
        <h1>Moyo OMS</h1>
        <div style="text-align:center;margin-bottom:0.5rem">
          <button id="login-theme-toggle" class="theme-toggle-inline" title="Toggle dark mode">${document.documentElement.getAttribute('data-theme') === 'dark' ? '🌙' : '☀️'}</button>
        </div>
        <p class="subtitle">Vendor Login</p>
        <div id="login-msg"></div>
        <form id="login-form">
          <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" required autocomplete="username">
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" required autocomplete="current-password">
          </div>
          <button type="submit" class="btn" style="width:100%">Login</button>
        </form>
        <p class="register-link">Don't have an account? <a href="#register">Register here</a></p>
      </div>
    </div>`;

  document.getElementById('login-theme-toggle').addEventListener('click', () => {
    const current = document.documentElement.getAttribute('data-theme');
    const next = current === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', next);
    localStorage.setItem('theme', next);
    document.getElementById('login-theme-toggle').textContent = next === 'dark' ? '🌙' : '☀️';
    const navToggle = document.getElementById('theme-toggle');
    if (navToggle) navToggle.textContent = next === 'dark' ? '🌙' : '☀️';
  });

  document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const msgEl = document.getElementById('login-msg');
    msgEl.innerHTML = '';

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    try {
      const data = await api('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password })
      });
      saveAuth(data.token);
      window.location.hash = '#dashboard';
    } catch (err) {
      msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
    }
  });
}
