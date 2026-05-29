import { categories, getCategoryById, priceRanges, secondaryFilterOptions, sortOptions } from './data.js';
import { state } from './AppState.js';
import { renderHomePage, renderProductPage } from './components/Pages.js';
import { getFilterKeyFromMenuGroup } from './services/catalogService.js';
import { scrollToSection } from './utils/dom.js';

const app = document.getElementById('app');
if (app?.dataset.page === 'product') {
  state.page = 'product';
  state.selectedProductId = Number(app.dataset.productId) || state.selectedProductId;
}

function render() {
  app.innerHTML = state.page === 'product' ? renderProductPage(state) : renderHomePage(state);
  bindEvents();
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

render();
