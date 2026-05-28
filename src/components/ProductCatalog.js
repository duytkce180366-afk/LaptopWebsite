import ProductCard from './ProductCard';
import ProductFilters from './ProductFilters';

function ProductCatalog({
  activeCategory,
  categories,
  filterOptions,
  filteredProducts,
  priceRanges,
  products,
  searchTerm,
  secondaryFilters,
  selectedCategoryId,
  selectedPrice,
  sortOrder,
  sortOptions,
  onCategoryChange,
  onClearFilters,
  onPriceChange,
  onProductSelect,
  onSearchChange,
  onSecondaryFilterChange,
  onSortChange,
}) {
  return (
    <section className="catalog-section" id="products" aria-labelledby="catalog-title">
      <div className="section-heading">
        <p className="eyebrow">Search and Filter Products</p>
        <h2 id="catalog-title">Product catalog</h2>
      </div>

      <ProductFilters
        activeCategory={activeCategory}
        categories={categories}
        filterOptions={filterOptions}
        priceRanges={priceRanges}
        searchTerm={searchTerm}
        secondaryFilters={secondaryFilters}
        selectedCategoryId={selectedCategoryId}
        selectedPrice={selectedPrice}
        sortOrder={sortOrder}
        sortOptions={sortOptions}
        onCategoryChange={onCategoryChange}
        onClearFilters={onClearFilters}
        onPriceChange={onPriceChange}
        onSearchChange={onSearchChange}
        onSecondaryFilterChange={onSecondaryFilterChange}
        onSortChange={onSortChange}
      />

      <p className="result-count">
        Showing {filteredProducts.length} of {products.length} products
      </p>

      <div className="product-grid">
        {filteredProducts.map((product) => (
          <ProductCard key={product.id} product={product} onViewDetails={onProductSelect} />
        ))}
      </div>

      {filteredProducts.length === 0 && (
        <div className="empty-state">
          <h3>No products found</h3>
          <p>Try another keyword, category, price range, sort option, or secondary filter.</p>
          <button type="button" onClick={onClearFilters}>
            Reset filters
          </button>
        </div>
      )}
    </section>
  );
}

export default ProductCatalog;
