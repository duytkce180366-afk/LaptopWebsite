import { categories, products } from '../data.js';

export function renderHero() {
  return `
    <section class="hero" id="home">
      <div class="hero-copy">
        <p class="eyebrow">Guest shopping flow</p>
        <h1>Find laptops, PC parts, and accessories with category-specific filters.</h1>
        <p>
          This demo implements the assigned browsing features with an expanded product
          catalog: search, category menu, filters, sorting, product detail pages, and
          customer reviews.
        </p>
        <div class="hero-actions">
          <a class="primary-action" href="#products">Browse products</a>
          <a class="secondary-action" href="#categories">View categories</a>
        </div>
      </div>

      <div class="hero-panel" aria-label="Featured promotion">
        <span class="promo-label">Demo catalog</span>
        <h2>Computer store products in one searchable page</h2>
        <p>Compare specs, price, stock status, warranty, and real customer feedback.</p>
        <div class="promo-stats">
          <span><strong>${products.length}</strong>Products</span>
          <span><strong>${categories.length}</strong>Groups</span>
          <span><strong>3s</strong>Search target</span>
        </div>
      </div>
    </section>
  `;
}
