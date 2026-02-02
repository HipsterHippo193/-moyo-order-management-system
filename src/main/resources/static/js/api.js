const API_BASE = '/api';

function getToken() {
  return localStorage.getItem('token');
}

function getVendorId() {
  return localStorage.getItem('vendorId');
}

function getUsername() {
  return localStorage.getItem('username');
}

function saveAuth(token) {
  localStorage.setItem('token', token);
  // Decode JWT payload to extract vendorId and username
  const payload = JSON.parse(atob(token.split('.')[1]));
  localStorage.setItem('vendorId', payload.vendorId);
  localStorage.setItem('username', payload.sub);
}

function clearAuth() {
  localStorage.removeItem('token');
  localStorage.removeItem('vendorId');
  localStorage.removeItem('username');
}

function isLoggedIn() {
  return !!getToken();
}

async function api(path, options = {}) {
  const headers = { 'Content-Type': 'application/json' };
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 401) {
    clearAuth();
    window.location.hash = '#login';
    throw new Error('Unauthorized');
  }

  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || body.error || `Request failed (${res.status})`);
  }

  if (res.status === 204) return null;
  return res.json();
}

export { api, getToken, getVendorId, getUsername, saveAuth, clearAuth, isLoggedIn };
