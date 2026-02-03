import { api, getVendorId } from '../api.js';

let enrolledProductIds = new Set();
let allProducts = [];

export function render() {
  const app = document.getElementById('app');
  const vendorId = getVendorId();

  app.innerHTML = `
    <div class="card">
      <div class="card-header">
        <h2>My Products</h2>
        <button class="btn" onclick="window.__showEnrollModal()">Enroll in Product</button>
      </div>
      <div id="dash-msg"></div>
      <table>
        <thead>
          <tr><th>Name</th><th>Code</th><th>Price</th><th>Stock</th><th>Actions</th></tr>
        </thead>
        <tbody id="product-body"><tr><td colspan="5">Loading...</td></tr></tbody>
      </table>
    </div>

    <div id="enroll-modal" class="modal hidden">
      <div class="modal-content card">
        <h2>Enroll in Product</h2>
        <div id="enroll-msg"></div>
        <form id="enroll-form">
          <div class="form-group">
            <label for="enroll-product">Select Product</label>
            <select id="enroll-product" required>
              <option value="">-- Select a product --</option>
            </select>
          </div>
          <div class="form-group">
            <label for="enroll-price">Your Price ($)</label>
            <input type="number" id="enroll-price" step="0.01" min="0.01" required>
          </div>
          <div class="form-group">
            <label for="enroll-stock">Initial Stock</label>
            <input type="number" id="enroll-stock" min="0" required value="0">
          </div>
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" onclick="window.__closeEnrollModal()">Cancel</button>
            <button type="submit" class="btn">Enroll</button>
          </div>
        </form>
      </div>
    </div>`;

  loadProducts(vendorId);
  loadAllProducts();

  document.getElementById('enroll-form').addEventListener('submit', handleEnroll);
}

async function loadProducts(vendorId) {
  const tbody = document.getElementById('product-body');
  const msgEl = document.getElementById('dash-msg');

  try {
    const products = await api(`/vendors/${vendorId}/products`);
    enrolledProductIds = new Set(products.map(p => p.productId));

    if (!products.length) {
      tbody.innerHTML = '<tr><td colspan="5">No products enrolled. Click "Enroll in Product" to get started.</td></tr>';
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
        <td>
          <button class="btn btn-sm btn-danger" onclick="window.__unenroll(${p.productId}, '${esc(p.name).replace(/'/g, "\\'")}')">Unenroll</button>
        </td>
      </tr>`).join('');
  } catch (err) {
    msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
  }
}

async function loadAllProducts() {
  try {
    allProducts = await api('/products');
  } catch (err) {
    console.error('Failed to load products:', err);
  }
}

window.__showEnrollModal = function() {
  const select = document.getElementById('enroll-product');
  select.innerHTML = '<option value="">-- Select a product --</option>';

  // Filter out already enrolled products
  const availableProducts = allProducts.filter(p => !enrolledProductIds.has(p.id));

  if (availableProducts.length === 0) {
    document.getElementById('enroll-msg').innerHTML =
      '<div class="msg msg-error">You are already enrolled in all available products.</div>';
  } else {
    document.getElementById('enroll-msg').innerHTML = '';
    availableProducts.forEach(p => {
      select.innerHTML += `<option value="${p.id}">${esc(p.name)} (${esc(p.productCode)})</option>`;
    });
  }

  document.getElementById('enroll-price').value = '';
  document.getElementById('enroll-stock').value = '0';
  document.getElementById('enroll-modal').classList.remove('hidden');
};

window.__closeEnrollModal = function() {
  document.getElementById('enroll-modal').classList.add('hidden');
};

async function handleEnroll(e) {
  e.preventDefault();
  const vendorId = getVendorId();
  const productId = document.getElementById('enroll-product').value;
  const price = parseFloat(document.getElementById('enroll-price').value);
  const stock = parseInt(document.getElementById('enroll-stock').value);
  const msgEl = document.getElementById('enroll-msg');

  if (!productId) {
    msgEl.innerHTML = '<div class="msg msg-error">Please select a product</div>';
    return;
  }

  try {
    const res = await api(`/vendors/${vendorId}/products`, {
      method: 'POST',
      body: JSON.stringify({ productId: parseInt(productId), price, stock })
    });

    window.__closeEnrollModal();
    document.getElementById('dash-msg').innerHTML =
      `<div class="msg msg-success">Successfully enrolled in "${esc(res.name)}" at $${price.toFixed(2)}</div>`;
    loadProducts(vendorId);
  } catch (err) {
    msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
  }
}

window.__unenroll = async function(productId, productName) {
  if (!confirm(`Are you sure you want to stop supplying "${productName}"?`)) return;

  const vendorId = getVendorId();
  const msgEl = document.getElementById('dash-msg');

  try {
    await api(`/vendors/${vendorId}/products/${productId}`, { method: 'DELETE' });
    msgEl.innerHTML = `<div class="msg msg-success">Successfully unenrolled from "${productName}"</div>`;
    loadProducts(vendorId);
  } catch (err) {
    msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
  }
};

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
  if (!s) return '';
  const d = document.createElement('div');
  d.textContent = s;
  return d.innerHTML;
}
