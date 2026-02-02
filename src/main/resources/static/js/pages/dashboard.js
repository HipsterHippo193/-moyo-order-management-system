import { api, getVendorId } from '../api.js';

export function render() {
  const app = document.getElementById('app');
  const vendorId = getVendorId();

  app.innerHTML = `
    <div class="card">
      <h2>Products</h2>
      <div id="dash-msg"></div>
      <table>
        <thead>
          <tr><th>Name</th><th>Code</th><th>Price</th><th>Stock</th></tr>
        </thead>
        <tbody id="product-body"><tr><td colspan="4">Loading...</td></tr></tbody>
      </table>
    </div>`;

  loadProducts(vendorId);
}

async function loadProducts(vendorId) {
  const tbody = document.getElementById('product-body');
  const msgEl = document.getElementById('dash-msg');

  try {
    const products = await api(`/vendors/${vendorId}/products`);
    if (!products.length) {
      tbody.innerHTML = '<tr><td colspan="4">No products found.</td></tr>';
      return;
    }
    tbody.innerHTML = products.map(p => `
      <tr>
        <td>${esc(p.name)}</td>
        <td>${esc(p.productCode)}</td>
        <td id="price-cell-${p.productId}">
          <span>$${Number(p.price).toFixed(2)}</span>
          <button class="btn btn-sm" onclick="window.__editPrice(${p.productId})">Edit</button>
        </td>
        <td id="stock-cell-${p.productId}">
          <span>${p.stock}</span>
          <button class="btn btn-sm" onclick="window.__editStock(${p.productId})">Edit</button>
        </td>
      </tr>`).join('');
  } catch (err) {
    msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
  }
}

window.__editPrice = function(productId) {
  const cell = document.getElementById(`price-cell-${productId}`);
  const current = cell.querySelector('span').textContent.replace('$', '');
  cell.innerHTML = `
    <div class="inline-edit">
      <input type="number" step="0.01" min="0.01" value="${current}" id="price-input-${productId}">
      <button class="btn btn-sm" onclick="window.__savePrice(${productId})">Save</button>
    </div>`;
};

window.__editStock = function(productId) {
  const cell = document.getElementById(`stock-cell-${productId}`);
  const current = cell.querySelector('span').textContent;
  cell.innerHTML = `
    <div class="inline-edit">
      <input type="number" min="0" value="${current}" id="stock-input-${productId}">
      <button class="btn btn-sm" onclick="window.__saveStock(${productId})">Save</button>
    </div>`;
};

window.__savePrice = async function(productId) {
  const vendorId = getVendorId();
  const price = document.getElementById(`price-input-${productId}`).value;
  const msgEl = document.getElementById('dash-msg');
  try {
    const res = await api(`/vendors/${vendorId}/products/${productId}/price`, {
      method: 'PUT',
      body: JSON.stringify({ price: parseFloat(price) })
    });
    msgEl.innerHTML = `<div class="msg msg-success">Price updated: $${Number(res.oldPrice).toFixed(2)} → $${Number(res.newPrice).toFixed(2)}</div>`;
    loadProducts(vendorId);
  } catch (err) {
    msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
  }
};

window.__saveStock = async function(productId) {
  const vendorId = getVendorId();
  const stock = document.getElementById(`stock-input-${productId}`).value;
  const msgEl = document.getElementById('dash-msg');
  try {
    const res = await api(`/vendors/${vendorId}/products/${productId}/stock`, {
      method: 'PUT',
      body: JSON.stringify({ stock: parseInt(stock) })
    });
    msgEl.innerHTML = `<div class="msg msg-success">Stock updated: ${res.oldStock} → ${res.newStock}</div>`;
    loadProducts(vendorId);
  } catch (err) {
    msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
  }
};

function esc(s) {
  const d = document.createElement('div');
  d.textContent = s;
  return d.innerHTML;
}
