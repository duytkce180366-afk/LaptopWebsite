function CategorySection({ categories, products, activeCategoryId, onCategorySelect }) {
  return (
    <section className="section-heading" id="categories" aria-labelledby="featured-title">
      <p className="eyebrow">Featured categories</p>
      <h2 id="featured-title">Computer store departments</h2>
      <div className="category-grid category-grid-wide">
        {categories.map((category) => (
          <button
            className={category.id === activeCategoryId ? 'category-card active' : 'category-card'}
            key={category.id}
            type="button"
            onClick={() => onCategorySelect(category.id)}
          >
            <span>{category.name}</span>
            <small>
              {products.filter((product) => product.categoryId === category.id).length} products
            </small>
          </button>
        ))}
      </div>
    </section>
  );
}

export default CategorySection;
