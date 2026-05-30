import { formatPrice, getAverageRating } from '../data.js';
import { escapeAttribute } from '../utils/dom.js';
import { formatSpecLabel } from '../utils/formatters.js';

export function renderProductDetails(product) {
  return `
    <section class="details-section" id="details" aria-labelledby="details-title">
      <div class="section-heading">
        <p class="eyebrow">View Product Details</p>
        <h2 id="details-title">${product.name}</h2>
      </div>

      <div class="details-layout">
        <img src="${product.image}" alt="${escapeAttribute(product.name)} product view" />
        <div class="details-content">
          <div class="detail-summary">
            <span>${product.brand}</span>
            <span>${product.category}</span>
            <span>${getAverageRating(product)} / 5 rating</span>
          </div>
          <p>${product.description}</p>
          <h3>${formatPrice(product.price)}</h3>
          <dl class="spec-table">
            ${Object.entries(product.specs)
              .map(
                ([key, value]) => `
                  <div>
                    <dt>${formatSpecLabel(key)}</dt>
                    <dd>${value}</dd>
                  </div>
                `
              )
              .join('')}
            <div>
              <dt>Warranty</dt>
              <dd>${product.warranty}</dd>
            </div>
            <div>
              <dt>Status</dt>
              <dd>${product.stock > 0 ? `${product.stock} in stock` : 'Out of Stock'}</dd>
            </div>
          </dl>
        </div>
      </div>
    </section>
  `;
}
