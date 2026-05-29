import { categories, products } from '../data.js';

export function renderCategorySection(state) {
  return `
    <section class="section-heading" id="categories" aria-labelledby="featured-title">
      <p class="eyebrow">Featured categories</p>
      <h2 id="featured-title">Computer store departments</h2>
      <div class="category-grid category-grid-wide">
        ${categories
          .map(
            (category) => `
              <button
                class="${category.id === state.selectedCategoryId ? 'category-card active' : 'category-card'}"
                type="button"
                data-action="category"
                data-category-id="${category.id}"
              >
                <span>${category.name}</span>
                <small>${products.filter((product) => product.categoryId === category.id).length} products</small>
              </button>
            `
          )
          .join('')}
      </div>
    </section>
  `;
}
