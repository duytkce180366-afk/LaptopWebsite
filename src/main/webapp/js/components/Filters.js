import { categories, priceRanges, secondaryFilterOptions, sortOptions } from '../data.js';
import { escapeAttribute } from '../utils/dom.js';

export function renderFilters(state, activeCategory) {
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

      ${activeCategory ? renderSecondaryFilters(state, activeCategory) : ''}
    </div>
  `;
}

function renderSecondaryFilters(state, activeCategory) {
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
