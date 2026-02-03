import { api } from '../api.js';

let categories = [];
let products = [];
let editingProductId = null;

export function render() {
  const app = document.getElementById('app');

  app.innerHTML = `
    <div class="card">
      <h2>Product Catalog</h2>
      <div id="catalog-msg"></div>
      <div class="catalog-controls">
        <div class="form-group" style="display:inline-block;margin-right:1rem;margin-bottom:0;">
          <label for="category-filter">Filter by Category</label>
          <select id="category-filter">
            <option value="">All Categories</option>
          </select>
        </div>
        <button class="btn" onclick="window.__showAddProduct()">Add Product</button>
      </div>
      <table>
        <thead>
          <tr><th>Name</th><th>Code</th><th>Category</th><th>Description</th><th>Actions</th></tr>
        </thead>
        <tbody id="catalog-body"><tr><td colspan="5">Loading...</td></tr></tbody>
      </table>
    </div>

    <div id="product-modal" class="modal hidden">
      <div class="modal-content card">
        <h2 id="modal-title">Add Product</h2>
        <div id="modal-msg"></div>
        <form id="product-form">
          <div class="form-group">
            <label for="product-name">Product Name</label>
            <input type="text" id="product-name" required maxlength="100">
          </div>
          <div class="form-group">
            <label for="product-code">Product Code</label>
            <input type="text" id="product-code" required maxlength="50">
          </div>
          <div class="form-group">
            <label for="product-category">Category</label>
            <select id="product-category">
              <option value="">No Category</option>
            </select>
          </div>
          <div class="form-group">
            <label for="product-description">Description</label>
            <textarea id="product-description" rows="3" maxlength="500"></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" onclick="window.__closeModal()">Cancel</button>
            <button type="submit" class="btn">Save</button>
          </div>
        </form>
      </div>
    </div>`;

  loadCategories();
  loadProducts();

  document.getElementById('category-filter').addEventListener('change', (e) => {
    filterProducts(e.target.value);
  });

  document.getElementById('product-form').addEventListener('submit', handleProductSubmit);
}

async function loadCategories() {
  try {
    categories = await api('/categories');
    const filterSelect = document.getElementById('category-filter');
    const formSelect = document.getElementById('product-category');

    categories.forEach(c => {
      filterSelect.innerHTML += `<option value="${c.id}">${esc(c.name)}</option>`;
      formSelect.innerHTML += `<option value="${c.id}">${esc(c.name)}</option>`;
    });
  } catch (err) {
    console.error('Failed to load categories:', err);
  }
}

async function loadProducts(categoryId = null) {
  const tbody = document.getElementById('catalog-body');
  const msgEl = document.getElementById('catalog-msg');

  try {
    const url = categoryId ? `/products?categoryId=${categoryId}` : '/products';
    products = await api(url);

    if (!products.length) {
      tbody.innerHTML = '<tr><td colspan="5">No products found.</td></tr>';
      return;
    }

    renderProducts(products);
  } catch (err) {
    msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
  }
}

function renderProducts(productList) {
  const tbody = document.getElementById('catalog-body');

  tbody.innerHTML = productList.map(p => `
    <tr>
      <td>${esc(p.name)}</td>
      <td>${esc(p.productCode)}</td>
      <td>${p.categoryName ? esc(p.categoryName) : '-'}</td>
      <td>${p.description ? esc(p.description) : '-'}</td>
      <td>
        <button class="btn btn-sm" onclick="window.__editProduct(${p.id})">Edit</button>
        <button class="btn btn-sm btn-danger" onclick="window.__deleteProduct(${p.id}, '${esc(p.name).replace(/'/g, "\\'")}')">Delete</button>
      </td>
    </tr>`).join('');
}

function filterProducts(categoryId) {
  loadProducts(categoryId || null);
}

window.__showAddProduct = function() {
  editingProductId = null;
  document.getElementById('modal-title').textContent = 'Add Product';
  document.getElementById('product-form').reset();
  document.getElementById('modal-msg').innerHTML = '';
  document.getElementById('product-modal').classList.remove('hidden');
};

window.__editProduct = function(productId) {
  const product = products.find(p => p.id === productId);
  if (!product) return;

  editingProductId = productId;
  document.getElementById('modal-title').textContent = 'Edit Product';
  document.getElementById('product-name').value = product.name;
  document.getElementById('product-code').value = product.productCode;
  document.getElementById('product-category').value = product.categoryId || '';
  document.getElementById('product-description').value = product.description || '';
  document.getElementById('modal-msg').innerHTML = '';
  document.getElementById('product-modal').classList.remove('hidden');
};

window.__closeModal = function() {
  document.getElementById('product-modal').classList.add('hidden');
  editingProductId = null;
};

window.__deleteProduct = async function(productId, productName) {
  if (!confirm(`Are you sure you want to delete "${productName}"?`)) return;

  const msgEl = document.getElementById('catalog-msg');
  try {
    await api(`/products/${productId}`, { method: 'DELETE' });
    msgEl.innerHTML = `<div class="msg msg-success">Product "${productName}" deleted successfully</div>`;
    loadProducts(document.getElementById('category-filter').value || null);
  } catch (err) {
    msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
  }
};

async function handleProductSubmit(e) {
  e.preventDefault();
  const msgEl = document.getElementById('modal-msg');
  msgEl.innerHTML = '';

  const data = {
    name: document.getElementById('product-name').value.trim(),
    productCode: document.getElementById('product-code').value.trim(),
    description: document.getElementById('product-description').value.trim() || null,
    categoryId: document.getElementById('product-category').value ?
                parseInt(document.getElementById('product-category').value) : null
  };

  try {
    if (editingProductId) {
      await api(`/products/${editingProductId}`, {
        method: 'PUT',
        body: JSON.stringify(data)
      });
      document.getElementById('catalog-msg').innerHTML =
        `<div class="msg msg-success">Product "${data.name}" updated successfully</div>`;
    } else {
      await api('/products', {
        method: 'POST',
        body: JSON.stringify(data)
      });
      document.getElementById('catalog-msg').innerHTML =
        `<div class="msg msg-success">Product "${data.name}" created successfully</div>`;
    }

    window.__closeModal();
    loadProducts(document.getElementById('category-filter').value || null);
  } catch (err) {
    msgEl.innerHTML = `<div class="msg msg-error">${err.message}</div>`;
  }
}

function esc(s) {
  if (!s) return '';
  const d = document.createElement('div');
  d.textContent = s;
  return d.innerHTML;
}
