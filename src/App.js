import { useMemo, useState } from 'react';
import './App.css';
import CategorySection from './components/CategorySection';
import Hero from './components/Hero';
import Navbar from './components/Navbar';
import ProductCatalog from './components/ProductCatalog';
import ProductPage from './components/ProductPage';
import {
  categories,
  getCategoryById,
  priceRanges,
  products,
  secondaryFilterOptions,
  sortOptions,
} from './Data';

function App() {
  const [page, setPage] = useState('home');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategoryId, setSelectedCategoryId] = useState('all');
  const [selectedPrice, setSelectedPrice] = useState(priceRanges[0].label);
  const [sortOrder, setSortOrder] = useState(sortOptions[0].value);
  const [secondaryFilters, setSecondaryFilters] = useState({});
  const [selectedProductId, setSelectedProductId] = useState(products[0].id);

  const activeCategory = selectedCategoryId === 'all' ? null : getCategoryById(selectedCategoryId);
  const selectedProduct = products.find((product) => product.id === selectedProductId) || products[0];

  const categoryProducts = useMemo(() => {
    if (selectedCategoryId === 'all') {
      return products;
    }

    return products.filter((product) => product.categoryId === selectedCategoryId);
  }, [selectedCategoryId]);

  const filterOptions = useMemo(() => {
    if (!activeCategory) {
      return {};
    }

    return activeCategory.filters.reduce((options, filter) => {
      return {
        ...options,
        [filter.key]: secondaryFilterOptions[activeCategory.id]?.[filter.key] || [],
      };
    }, {});
  }, [activeCategory]);

  const filteredProducts = useMemo(() => {
    const priceRange = priceRanges.find((range) => range.label === selectedPrice);
    const query = searchTerm.trim().toLowerCase();

    const results = categoryProducts.filter((product) => {
      const searchableText = [
        product.name,
        product.brand,
        product.category,
        product.badge,
        ...Object.values(product.specs),
      ]
        .join(' ')
        .toLowerCase();

      const matchesSearch = searchableText.includes(query);
      const matchesPrice = product.price >= priceRange.min && product.price < priceRange.max;
      const matchesSecondaryFilters = Object.entries(secondaryFilters).every(([key, value]) => {
        if (!value || value === 'all') {
          return true;
        }

        return key === 'brand' ? product.brand === value : product.specs[key] === value;
      });

      return matchesSearch && matchesPrice && matchesSecondaryFilters;
    });

    if (sortOrder === 'price-asc') {
      return [...results].sort((first, second) => first.price - second.price);
    }

    if (sortOrder === 'price-desc') {
      return [...results].sort((first, second) => second.price - first.price);
    }

    return results;
  }, [categoryProducts, searchTerm, secondaryFilters, selectedPrice, sortOrder]);

  function scrollToSection(sectionId) {
    window.setTimeout(() => {
      document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 0);
  }

  function handleHomeClick() {
    setPage('home');
    scrollToSection('home');
  }

  function handleProductsClick() {
    setPage('home');
    scrollToSection('products');
  }

  function handleCategorySelect(categoryId) {
    setPage('home');
    setSelectedCategoryId(categoryId);
    setSecondaryFilters({});
    setSearchTerm('');
    scrollToSection('products');
  }

  function handleMenuOptionSelect(categoryId, groupTitle, option) {
    const selectedCategory = getCategoryById(categoryId);
    const filterKey = getFilterKeyFromMenuGroup(groupTitle);

    setPage('home');
    setSelectedCategoryId(categoryId);
    setSecondaryFilters({});
    setSearchTerm('');

    if (groupTitle === 'Prices') {
      setSelectedPrice(option);
      scrollToSection('products');
      return;
    }

    setSelectedPrice(priceRanges[0].label);

    if (filterKey && selectedCategory) {
      const hasExactFilterValue = products
        .filter((product) => product.categoryId === categoryId)
        .some((product) => {
          const value = filterKey === 'brand' ? product.brand : product.specs[filterKey];
          return value === option;
        });

      if (hasExactFilterValue) {
        setSecondaryFilters({ [filterKey]: option });
      } else {
        setSearchTerm(option);
      }
    } else {
      setSearchTerm(option);
    }

    scrollToSection('products');
  }

  function handleSecondaryFilterChange(key, value) {
    setSecondaryFilters((currentFilters) => ({
      ...currentFilters,
      [key]: value,
    }));
  }

  function handleProductSelect(productId) {
    setSelectedProductId(productId);
    setPage('product');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function clearFilters() {
    setSearchTerm('');
    setSelectedCategoryId('all');
    setSelectedPrice(priceRanges[0].label);
    setSortOrder(sortOptions[0].value);
    setSecondaryFilters({});
  }

  if (page === 'product') {
    return (
      <main className="app-shell">
        <Navbar
          categories={categories}
          activeCategoryId={selectedProduct.categoryId}
          onCategorySelect={handleCategorySelect}
          onHomeClick={handleHomeClick}
          onMenuOptionSelect={handleMenuOptionSelect}
          onProductsClick={handleProductsClick}
        />
        <ProductPage product={selectedProduct} onBack={handleHomeClick} />
      </main>
    );
  }

  return (
    <main className="app-shell">
      <Navbar
        categories={categories}
        activeCategoryId={selectedCategoryId}
        onCategorySelect={handleCategorySelect}
        onHomeClick={handleHomeClick}
        onMenuOptionSelect={handleMenuOptionSelect}
        onProductsClick={handleProductsClick}
      />
      <Hero productCount={products.length} categoryCount={categories.length} />
      <CategorySection
        categories={categories}
        products={products}
        activeCategoryId={selectedCategoryId}
        onCategorySelect={handleCategorySelect}
      />
      <ProductCatalog
        activeCategory={activeCategory}
        categories={categories}
        filterOptions={filterOptions}
        filteredProducts={filteredProducts}
        priceRanges={priceRanges}
        products={products}
        searchTerm={searchTerm}
        secondaryFilters={secondaryFilters}
        selectedCategoryId={selectedCategoryId}
        selectedPrice={selectedPrice}
        sortOrder={sortOrder}
        sortOptions={sortOptions}
        onCategoryChange={handleCategorySelect}
        onClearFilters={clearFilters}
        onPriceChange={setSelectedPrice}
        onProductSelect={handleProductSelect}
        onSearchChange={setSearchTerm}
        onSecondaryFilterChange={handleSecondaryFilterChange}
        onSortChange={setSortOrder}
      />
    </main>
  );
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

export default App;
