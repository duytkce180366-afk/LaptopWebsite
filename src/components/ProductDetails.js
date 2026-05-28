import { formatPrice, getAverageRating } from '../Data';

function ProductDetails({ product }) {
  return (
    <section className="details-section" id="details" aria-labelledby="details-title">
      <div className="section-heading">
        <p className="eyebrow">View Product Details</p>
        <h2 id="details-title">{product.name}</h2>
      </div>

      <div className="details-layout">
        <img src={product.image} alt={`${product.name} product view`} />
        <div className="details-content">
          <div className="detail-summary">
            <span>{product.brand}</span>
            <span>{product.category}</span>
            <span>{getAverageRating(product)} / 5 rating</span>
          </div>
          <p>{product.description}</p>
          <h3>{formatPrice(product.price)}</h3>
          <dl className="spec-table">
            {Object.entries(product.specs).map(([key, value]) => (
              <div key={key}>
                <dt>{formatSpecLabel(key)}</dt>
                <dd>{value}</dd>
              </div>
            ))}
            <div>
              <dt>Warranty</dt>
              <dd>{product.warranty}</dd>
            </div>
            <div>
              <dt>Status</dt>
              <dd>{product.stock > 0 ? `${product.stock} in stock` : 'Out of Stock'}</dd>
            </div>
          </dl>
        </div>
      </div>
    </section>
  );
}

function formatSpecLabel(key) {
  return key
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, (letter) => letter.toUpperCase())
    .replace('Gpu', 'GPU')
    .replace('Cpu', 'CPU')
    .replace('Dpi', 'DPI')
    .replace('Tdp', 'TDP')
    .replace('Vram', 'VRAM');
}

export default ProductDetails;
