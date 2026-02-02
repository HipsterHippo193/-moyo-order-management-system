const routes = {};

function register(hash, renderFn) {
  routes[hash] = renderFn;
}

function navigate() {
  const hash = window.location.hash || '#login';
  const render = routes[hash];
  if (render) {
    render();
  } else {
    window.location.hash = '#login';
  }
}

function init() {
  window.addEventListener('hashchange', navigate);
  navigate();
}

export { register, navigate, init };
