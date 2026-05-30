import { formatPrice, products } from '../data.js';
import { escapeAttribute } from '../utils/dom.js';
import { renderFilters } from './Filters.js';

export function renderProductCatalog(state, activeCategory, filteredProducts) {
  return `
    <section class="catalog-section" id="products" aria-labelledby="catalog-title">
      <div class="section-heading">
        <p class="eyebrow">Search and Filter Products</p>
        <h2 id="catalog-title">Product catalog</h2>
      </div>

      ${renderFilters(state, activeCategory)}

      <p class="result-count">Showing ${filteredProducts.length} of ${products.length} products</p>

      <div class="product-grid">
        ${filteredProducts.map(renderProductCard).join('')}
      </div>

      ${
        filteredProducts.length === 0
          ? `
            <div class="empty-state">
              <h3>No products found</h3>
              <p>Try another keyword, category, price range, sort option, or secondary filter.</p>
              <button type="button" data-action="clear">Reset filters</button>
            </div>
          `
          : ''
      }
    </section>
  `;
}

function renderProductCard(product) {
  const primarySpecs = Object.entries(product.specs).slice(0, 3);

  return `
    <article class="product-card">
      <img src="${product.image}" alt="${escapeAttribute(product.name)}" />
      <div class="product-content">
        <div class="card-topline">
          <span>${product.badge}</span>
          <span>${product.stock > 0 ? `${product.stock} in stock` : 'Out of stock'}</span>
        </div>
        <h3>${product.name}</h3>
        <p>${product.category}</p>
        <div class="spec-pills">
          ${primarySpecs.map(([, value]) => `<span>${value}</span>`).join('')}
        </div>
        <div class="product-footer">
          <strong>${formatPrice(product.price)}</strong>
          <button type="button" data-action="details" data-product-id="${product.id}">View details</button>
        </div>
      </div>
    </article>
  `;
}
