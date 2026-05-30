import { priceRanges, products } from '../data.js';

export function getFilteredProducts(state) {
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

export function getSelectedProduct(state) {
  return products.find((product) => product.id === state.selectedProductId) || products[0];
}

export function getFilterKeyFromMenuGroup(groupTitle) {
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
