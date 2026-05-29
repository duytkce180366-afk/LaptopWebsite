import { categories, priceRanges, products, sortOptions } from './data.js';

export const state = {
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
