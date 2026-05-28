import { useState } from 'react';

function Navbar({
  categories,
  activeCategoryId,
  onCategorySelect,
  onHomeClick,
  onMenuOptionSelect,
  onProductsClick,
}) {
  const [hoveredCategoryId, setHoveredCategoryId] = useState(activeCategoryId || categories[0].id);
  const visibleCategoryId = hoveredCategoryId === 'all' ? categories[0].id : hoveredCategoryId;

  return (
    <nav className="topbar" aria-label="Main navigation">
      <button className="brand-button" type="button" onClick={onHomeClick}>
        <span className="brand-mark">TechHub</span>
        <span className="brand-subtitle">Computer store demo</span>
      </button>

      <div className="category-menu">
        <button className="category-menu-button" type="button">
          Categories
          <span aria-hidden="true">v</span>
        </button>
        <div className="mega-menu">
          <div className="mega-list">
            {categories.map((category) => (
              <button
                className={category.id === activeCategoryId ? 'mega-category active' : 'mega-category'}
                key={category.id}
                type="button"
                onMouseEnter={() => setHoveredCategoryId(category.id)}
                onFocus={() => setHoveredCategoryId(category.id)}
                onClick={() => onCategorySelect(category.id)}
              >
                {category.name}
                <span aria-hidden="true">&gt;</span>
              </button>
            ))}
          </div>

          {categories.map((category) => (
            <div
              className={category.id === visibleCategoryId ? 'mega-panel visible' : 'mega-panel'}
              data-category={category.id}
              key={category.id}
            >
              {category.menuGroups.map((group) => (
                <section key={group.title}>
                  <h3>{group.title}</h3>
                  <div className="mega-tags">
                    {group.options.map((option) => (
                      <button
                        key={option}
                        type="button"
                        onClick={() => onMenuOptionSelect(category.id, group.title, option)}
                      >
                        {option}
                      </button>
                    ))}
                  </div>
                </section>
              ))}
            </div>
          ))}
        </div>
      </div>

      <div className="nav-links">
        <button type="button" onClick={onHomeClick}>
          Home
        </button>
        <button type="button" onClick={onProductsClick}>
          Products
        </button>
      </div>
    </nav>
  );
}

export default Navbar;
