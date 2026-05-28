import {
  categories,
  formatPrice,
  getAverageRating,
  getCategoryById,
  priceRanges,
  products,
  secondaryFilterOptions,
  sortOptions,
} from './data.js';

const state = {
  page: 'home',
  hoveredCategoryId: categories[0].id,
  searchTerm: '',
  selectedCategoryId: 'all',
  selectedPrice: priceRanges[0].label,
  sortOrder: sortOptions[0].value,
  secondaryFilters: {},
  selectedProductId: products[0].id,
  isCategoryMenuOpen: false,
};

const app = document.getElementById('app');

function render() {
  app.innerHTML = state.page === 'product' ? renderProductPage() : renderHomePage();
  bindEvents();
}

function renderHomePage() {
  const activeCategory = state.selectedCategoryId === 'all' ? null : getCategoryById(state.selectedCategoryId);
  const filteredProducts = getFilteredProducts();

  return `
    ${renderNavbar(state.selectedCategoryId)}
    ${renderHero()}
    ${renderCategorySection()}
    ${renderProductCatalog(activeCategory, filteredProducts)}
  `;
}

function renderProductPage() {
  const product = getSelectedProduct();

  return `
    ${renderNavbar(product.categoryId)}
    <div class="product-page">
      <button class="back-button" type="button" data-action="home">Back to products</button>
      ${renderProductDetails(product)}
      ${renderReviews(product)}
    </div>
  `;
}

function renderNavbar(activeCategoryId) {
  const visibleCategoryId = state.hoveredCategoryId === 'all' ? categories[0].id : state.hoveredCategoryId;

  return `
    <nav class="topbar" aria-label="Main navigation">
      <button class="brand-button" type="button" data-action="home">
        <span class="brand-mark">TechHub</span>
        <span class="brand-subtitle">Computer store demo</span>
      </button>

      <div class="${state.isCategoryMenuOpen ? 'category-menu open' : 'category-menu'}">
        <button class="category-menu-button" type="button" data-action="toggle-category-menu" aria-expanded="${
          state.isCategoryMenuOpen ? 'true' : 'false'
        }">
          Categories
          <span aria-hidden="true">v</span>
        </button>
        <div class="mega-menu">
          <div class="mega-list">
            ${categories
              .map(
                (category) => `
                  <button
                    class="${category.id === activeCategoryId ? 'mega-category active' : 'mega-category'}"
                    type="button"
                    data-action="category"
                    data-category-id="${category.id}"
                    data-hover-category="${category.id}"
                  >
                    ${category.name}
                    <span aria-hidden="true">&gt;</span>
                  </button>
                `
              )
              .join('')}
          </div>

          ${categories
            .map(
              (category) => `
                <div
                  class="${category.id === visibleCategoryId ? 'mega-panel visible' : 'mega-panel'}"
                  data-mega-panel="${category.id}"
                >
                  ${category.menuGroups
                    .map(
                      (group) => `
                        <section>
                          <h3>${group.title}</h3>
                          <div class="mega-tags">
                            ${group.options
                              .map(
                                (option) => `
                                  <button
                                    type="button"
                                    data-action="menu-option"
                                    data-category-id="${category.id}"
                                    data-group-title="${group.title}"
                                    data-option="${option}"
                                  >
                                    ${option}
                                  </button>
                                `
                              )
                              .join('')}
                          </div>
                        </section>
                      `
                    )
                    .join('')}
                </div>
              `
            )
            .join('')}
        </div>
      </div>

      <div class="nav-links">
        <button type="button" data-action="home">Home</button>
        <button type="button" data-action="products">Products</button>
      </div>
    </nav>
  `;
}

function renderHero() {
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

function renderCategorySection() {
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

function renderProductCatalog(activeCategory, filteredProducts) {
  return `
    <section class="catalog-section" id="products" aria-labelledby="catalog-title">
      <div class="section-heading">
        <p class="eyebrow">Search and Filter Products</p>
        <h2 id="catalog-title">Product catalog</h2>
      </div>

      ${renderFilters(activeCategory)}

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

function renderFilters(activeCategory) {
  return `
    <div class="filters" aria-label="Product filters">
      <label class="search-field">
        <span>Search</span>
        <input
          type="search"
          placeholder="Search by product, brand, category, or specs"
          value="${escapeAttribute(state.searchTerm)}"
          data-filter="search"
        />
      </label>

      <label>
        <span>Category</span>
        <select data-filter="category">
          <option value="all">All categories</option>
          ${categories
            .map(
              (category) => `
                <option value="${category.id}" ${category.id === state.selectedCategoryId ? 'selected' : ''}>
                  ${category.name}
                </option>
              `
            )
            .join('')}
        </select>
      </label>

      <label>
        <span>Price</span>
        <select data-filter="price">
          ${priceRanges
            .map((range) => `<option ${range.label === state.selectedPrice ? 'selected' : ''}>${range.label}</option>`)
            .join('')}
        </select>
      </label>

      <label>
        <span>Sort</span>
        <select data-filter="sort">
          ${sortOptions
            .map(
              (option) => `
                <option value="${option.value}" ${option.value === state.sortOrder ? 'selected' : ''}>
                  ${option.label}
                </option>
              `
            )
            .join('')}
        </select>
      </label>

      <button class="clear-button" type="button" data-action="clear">Clear</button>

      ${activeCategory ? renderSecondaryFilters(activeCategory) : ''}
    </div>
  `;
}

function renderSecondaryFilters(activeCategory) {
  return `
    <div class="secondary-filter-panel">
      <div>
        <p class="eyebrow">Secondary filters</p>
        <h3>${activeCategory.name}</h3>
      </div>
      <div class="secondary-filter-grid">
        ${activeCategory.filters
          .map((filter) => {
            const options = secondaryFilterOptions[activeCategory.id]?.[filter.key] || [];
            return `
              <label>
                <span>${filter.label}</span>
                <select data-secondary-filter="${filter.key}">
                  <option value="all">All</option>
                  ${options
                    .map(
                      (option) => `
                        <option value="${escapeAttribute(option)}" ${
                        state.secondaryFilters[filter.key] === option ? 'selected' : ''
                      }>
                          ${option}
                        </option>
                      `
                    )
                    .join('')}
                </select>
              </label>
            `;
          })
          .join('')}
      </div>
    </div>
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

function renderProductDetails(product) {
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

function renderReviews(product) {
  return `
    <section class="reviews-section" id="reviews" aria-labelledby="reviews-title">
      <div class="section-heading">
        <p class="eyebrow">View Reviews</p>
        <h2 id="reviews-title">Customer reviews for ${product.name}</h2>
      </div>
      <div class="reviews-grid">
        ${product.reviews
          .map(
            (review) => `
              <article class="review-card">
                <div>
                  <strong>${review.user}</strong>
                  <span>${review.date}</span>
                </div>
                <p class="stars" aria-label="${review.rating} out of 5 rating">Rating: ${review.rating}/5</p>
                <p>${review.comment}</p>
              </article>
            `
          )
          .join('')}
      </div>
    </section>
  `;
}

function bindEvents() {
  document.querySelectorAll('[data-hover-category]').forEach((button) => {
    button.addEventListener('mouseenter', () => {
      showMegaPanel(button.dataset.hoverCategory);
    });
    button.addEventListener('focus', () => {
      showMegaPanel(button.dataset.hoverCategory);
    });
  });

  document.addEventListener('click', handleDocumentClick);
  document.addEventListener('keydown', handleDocumentKeydown);

  document.querySelectorAll('[data-action]').forEach((element) => {
    element.addEventListener('click', handleAction);
  });

  document.querySelector('[data-filter="search"]')?.addEventListener('input', (event) => {
    state.searchTerm = event.target.value;
    render();
  });

  document.querySelector('[data-filter="category"]')?.addEventListener('change', (event) => {
    handleCategorySelect(event.target.value);
  });

  document.querySelector('[data-filter="price"]')?.addEventListener('change', (event) => {
    state.selectedPrice = event.target.value;
    render();
  });

  document.querySelector('[data-filter="sort"]')?.addEventListener('change', (event) => {
    state.sortOrder = event.target.value;
    render();
  });

  document.querySelectorAll('[data-secondary-filter]').forEach((select) => {
    select.addEventListener('change', (event) => {
      state.secondaryFilters[event.target.dataset.secondaryFilter] = event.target.value;
      render();
    });
  });
}

function handleAction(event) {
  const target = event.currentTarget;
  const action = target.dataset.action;

  if (action === 'home') {
    state.isCategoryMenuOpen = false;
    state.page = 'home';
    render();
    scrollToSection('home');
  }

  if (action === 'products') {
    state.isCategoryMenuOpen = false;
    state.page = 'home';
    render();
    scrollToSection('products');
  }

  if (action === 'toggle-category-menu') {
    event.stopPropagation();
    state.isCategoryMenuOpen = !state.isCategoryMenuOpen;
    render();
  }

  if (action === 'category') {
    handleCategorySelect(target.dataset.categoryId);
  }

  if (action === 'menu-option') {
    handleMenuOptionSelect(target.dataset.categoryId, target.dataset.groupTitle, target.dataset.option);
  }

  if (action === 'details') {
    state.selectedProductId = Number(target.dataset.productId);
    state.page = 'product';
    render();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  if (action === 'clear') {
    clearFilters();
  }
}

function handleCategorySelect(categoryId) {
  state.page = 'home';
  state.selectedCategoryId = categoryId;
  state.secondaryFilters = {};
  state.searchTerm = '';
  state.hoveredCategoryId = categoryId === 'all' ? categories[0].id : categoryId;
  state.isCategoryMenuOpen = false;
  render();
  scrollToSection('products');
}

function handleMenuOptionSelect(categoryId, groupTitle, option) {
  const selectedCategory = getCategoryById(categoryId);
  const filterKey = getFilterKeyFromMenuGroup(groupTitle);

  state.page = 'home';
  state.selectedCategoryId = categoryId;
  state.secondaryFilters = {};
  state.searchTerm = '';
  state.hoveredCategoryId = categoryId;
  state.isCategoryMenuOpen = false;

  if (groupTitle === 'Prices') {
    state.selectedPrice = option;
    render();
    scrollToSection('products');
    return;
  }

  state.selectedPrice = priceRanges[0].label;

  if (filterKey && selectedCategory) {
    const hasConfiguredOption = secondaryFilterOptions[categoryId]?.[filterKey]?.includes(option);

    if (hasConfiguredOption) {
      state.secondaryFilters = { [filterKey]: option };
    } else {
      state.searchTerm = option;
    }
  } else {
    state.searchTerm = option;
  }

  render();
  scrollToSection('products');
}

function clearFilters() {
  state.searchTerm = '';
  state.selectedCategoryId = 'all';
  state.selectedPrice = priceRanges[0].label;
  state.sortOrder = sortOptions[0].value;
  state.secondaryFilters = {};
  state.isCategoryMenuOpen = false;
  render();
}

function showMegaPanel(categoryId) {
  state.hoveredCategoryId = categoryId;

  document.querySelectorAll('.mega-panel').forEach((panel) => {
    panel.classList.remove('visible');
  });

  document.querySelector(`[data-mega-panel="${categoryId}"]`)?.classList.add('visible');
}

function handleDocumentClick(event) {
  if (!state.isCategoryMenuOpen || event.target.closest('.category-menu')) {
    return;
  }

  state.isCategoryMenuOpen = false;
  render();
}

function handleDocumentKeydown(event) {
  if (event.key !== 'Escape' || !state.isCategoryMenuOpen) {
    return;
  }

  state.isCategoryMenuOpen = false;
  render();
}

function getFilteredProducts() {
  const categoryProducts =
    state.selectedCategoryId === 'all'
      ? products
      : products.filter((product) => product.categoryId === state.selectedCategoryId);
  const priceRange = priceRanges.find((range) => range.label === state.selectedPrice) || priceRanges[0];
  const query = state.searchTerm.trim().toLowerCase();

  const results = categoryProducts.filter((product) => {
    const searchableText = [product.name, product.brand, product.category, product.badge, ...Object.values(product.specs)]
      .join(' ')
      .toLowerCase();
    const matchesSearch = searchableText.includes(query);
    const matchesPrice = product.price >= priceRange.min && product.price < priceRange.max;
    const matchesSecondaryFilters = Object.entries(state.secondaryFilters).every(([key, value]) => {
      if (!value || value === 'all') {
        return true;
      }

      return key === 'brand' ? product.brand === value : product.specs[key] === value;
    });

    return matchesSearch && matchesPrice && matchesSecondaryFilters;
  });

  if (state.sortOrder === 'price-asc') {
    return [...results].sort((first, second) => first.price - second.price);
  }

  if (state.sortOrder === 'price-desc') {
    return [...results].sort((first, second) => second.price - first.price);
  }

  return results;
}

function getSelectedProduct() {
  return products.find((product) => product.id === state.selectedProductId) || products[0];
}

function getFilterKeyFromMenuGroup(groupTitle) {
  const groupMap = {
    Brands: 'brand',
    Purpose: 'purpose',
    CPU: 'cpu',
    GPU: 'gpu',
    Screen: 'display',
    Sensor: 'sensor',
    Connection: 'connection',
    Switch: 'switchType',
    Layout: 'layout',
    Resolution: 'resolution',
    'Refresh rate': 'refreshRate',
    Capacity: 'capacity',
    Interface: 'interfaceType',
    Type: 'memoryType',
    'Bus RAM': 'bus',
    Socket: 'socket',
    Cores: 'cores',
    Chipset: 'chipset',
    VRAM: 'vram',
    Motherboard: 'motherboardSupport',
    Color: 'color',
    Size: 'size',
  };

  return groupMap[groupTitle];
}

function formatSpecLabel(key) {
  return key
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, (letter) => letter.toUpperCase())
    .replace('Gpu', 'GPU')
    .replace('Cpu', 'CPU')
    .replace('Dpi', 'DPI')
    .replace('Tdp', 'TDP')
    .replace('Vram', 'VRAM');
}

function scrollToSection(sectionId) {
  window.setTimeout(() => {
    document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }, 0);
}

function escapeAttribute(value) {
  return String(value).replace(/"/g, '&quot;');
}

render();
