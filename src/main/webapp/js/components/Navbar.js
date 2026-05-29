import { categories } from '../data.js';

export function renderNavbar(state, activeCategoryId) {
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
