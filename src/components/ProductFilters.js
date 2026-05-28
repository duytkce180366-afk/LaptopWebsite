function ProductFilters({
  activeCategory,
  categories,
  filterOptions,
  priceRanges,
  searchTerm,
  secondaryFilters,
  selectedCategoryId,
  selectedPrice,
  sortOrder,
  sortOptions,
  onCategoryChange,
  onClearFilters,
  onPriceChange,
  onSearchChange,
  onSecondaryFilterChange,
  onSortChange,
}) {
  return (
    <div className="filters" aria-label="Product filters">
      <label className="search-field">
        <span>Search</span>
        <input
          type="search"
          placeholder="Search by product, brand, category, or specs"
          value={searchTerm}
          onChange={(event) => onSearchChange(event.target.value)}
        />
      </label>

      <label>
        <span>Category</span>
        <select value={selectedCategoryId} onChange={(event) => onCategoryChange(event.target.value)}>
          <option value="all">All categories</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>
      </label>

      <label>
        <span>Price</span>
        <select value={selectedPrice} onChange={(event) => onPriceChange(event.target.value)}>
          {priceRanges.map((range) => (
            <option key={range.label}>{range.label}</option>
          ))}
        </select>
      </label>

      <label>
        <span>Sort</span>
        <select value={sortOrder} onChange={(event) => onSortChange(event.target.value)}>
          {sortOptions.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </label>

      <button className="clear-button" type="button" onClick={onClearFilters}>
        Clear
      </button>

      {activeCategory && (
        <div className="secondary-filter-panel">
          <div>
            <p className="eyebrow">Secondary filters</p>
            <h3>{activeCategory.name}</h3>
          </div>
          <div className="secondary-filter-grid">
            {activeCategory.filters.map((filter) => (
              <label key={filter.key}>
                <span>{filter.label}</span>
                <select
                  value={secondaryFilters[filter.key] || 'all'}
                  onChange={(event) => onSecondaryFilterChange(filter.key, event.target.value)}
                >
                  <option value="all">All</option>
                  {(filterOptions[filter.key] || []).map((option) => (
                    <option key={option}>{option}</option>
                  ))}
                </select>
              </label>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default ProductFilters;
