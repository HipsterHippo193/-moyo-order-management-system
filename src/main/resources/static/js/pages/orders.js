import { api, getVendorId } from '../api.js';

export function render() {
  const app = document.getElementById('app');

  app.innerHTML = `
    <div class="card">
      <h2>Create Order</h2>
      <div id="order-msg"></div>
      <form id="order-form">
        <div class="form-group">
          <label for="order-product">Product</label>
          <select id="order-product" required>
            <option value="">Loading...</option>
          </select>
        </div>
        <div class="form-group">
          <label for="order-qty">Quantity</label>
          <input type="number" id="order-qty" min="1" value="1" required>
        </div>
        <button type="submit" class="btn">Place Order</button>
      </form>
    </div>
    <div class="card">
      <h2>Order History</h2>
      <table>
        <thead>
          <tr><th>ID</th><th>Product</th><th>Qty</th><th>Price</th><th>Total</th><th>Vendor</th><th>Status</th><th>Date</th></tr>
        </thead>
        <tbody id="orders-body"><tr><td colspan="8">Loading...</td></tr></tbody>
      </table>
    </div>`;

  loadProducts();
  loadOrders();

  document.getElementById('order-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const msgEl = document.getElementById('order-msg');
    const productId = document.getElementById('order-product').value;
    const quantity = parseInt(document.getElementById('order-qty').value);

    if (!productId) return;

    try {
      const res = await api('/orders', {
        method: 'POST',
        body: JSON.stringify({ productId: parseInt(productId), quantity })
      });
      msgEl.innerHTML = `<div class="msg msg-success">Order #${res.orderId} placed — allocated to ${esc(res.allocatedVendorName)} at $${Number(res.price).toFixed(2)}/unit (Total: $${Number(res.totalPrice).toFixed(2)})</div>`;
      loadOrders();
    } catch (err) {
      msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
    }
  });
}

async function loadProducts() {
  const select = document.getElementById('order-product');
  try {
    const vendorId = getVendorId();
    const products = await api(`/vendors/${vendorId}/products`);
    select.innerHTML = '<option value="">Select a product</option>' +
      products.map(p => `<option value="${p.productId}">${esc(p.name)} (${esc(p.productCode)})</option>`).join('');
  } catch {
    select.innerHTML = '<option value="">Failed to load</option>';
  }
}

async function loadOrders() {
  const tbody = document.getElementById('orders-body');
  try {
    const orders = await api('/orders');
    if (!orders.length) {
      tbody.innerHTML = '<tr><td colspan="8">No orders yet.</td></tr>';
      return;
    }
    tbody.innerHTML = orders.map(o => `
      <tr>
        <td>${o.orderId}</td>
        <td>${esc(o.productName)}</td>
        <td>${o.quantity}</td>
        <td>$${Number(o.price).toFixed(2)}</td>
        <td>$${Number(o.totalPrice).toFixed(2)}</td>
        <td>${esc(o.allocatedVendorName)}</td>
        <td>${esc(o.status)}</td>
        <td>${new Date(o.createdAt).toLocaleDateString()}</td>
      </tr>`).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="8">${err.message}</td></tr>`;
  }
}

function esc(s) {
  const d = document.createElement('div');
  d.textContent = s;
  return d.innerHTML;
}
