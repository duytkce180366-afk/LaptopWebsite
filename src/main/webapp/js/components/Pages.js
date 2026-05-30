import { getCategoryById } from '../data.js';
import { getFilteredProducts, getSelectedProduct } from '../services/catalogService.js';
import { renderCategorySection } from './CategorySection.js';
import { renderHero } from './Hero.js';
import { renderNavbar } from './Navbar.js';
import { renderProductCatalog } from './ProductCatalog.js';
import { renderProductDetails } from './ProductDetails.js';
import { renderReviews } from './Reviews.js';

export function renderHomePage(state) {
  const activeCategory = state.selectedCategoryId === 'all' ? null : getCategoryById(state.selectedCategoryId);
  const filteredProducts = getFilteredProducts(state);

  return `
    ${renderNavbar(state, state.selectedCategoryId)}
    ${renderHero()}
    ${renderCategorySection(state)}
    ${renderProductCatalog(state, activeCategory, filteredProducts)}
  `;
}

export function renderProductPage(state) {
  const product = getSelectedProduct(state);

  return `
    ${renderNavbar(state, product.categoryId)}
    <div class="product-page">
      <button class="back-button" type="button" data-action="home">Back to products</button>
      ${renderProductDetails(product)}
      ${renderReviews(product)}
    </div>
  `;
}
