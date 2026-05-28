function Hero({ productCount, categoryCount }) {
  return (
    <section className="hero" id="home">
      <div className="hero-copy">
        <p className="eyebrow">Guest shopping flow</p>
        <h1>Find laptops, PC parts, and accessories with category-specific filters.</h1>
        <p>
          This demo implements the assigned browsing features with an expanded product
          catalog: search, category menu, filters, sorting, product detail pages, and
          customer reviews.
        </p>
        <div className="hero-actions">
          <a className="primary-action" href="#products">
            Browse products
          </a>
          <a className="secondary-action" href="#categories">
            View categories
          </a>
        </div>
      </div>

      <div className="hero-panel" aria-label="Featured promotion">
        <span className="promo-label">Demo catalog</span>
        <h2>Computer store products in one searchable page</h2>
        <p>Compare specs, price, stock status, warranty, and real customer feedback.</p>
        <div className="promo-stats">
          <span>
            <strong>{productCount}</strong>
            Products
          </span>
          <span>
            <strong>{categoryCount}</strong>
            Groups
          </span>
          <span>
            <strong>3s</strong>
            Search target
          </span>
        </div>
      </div>
    </section>
  );
}

export default Hero;
