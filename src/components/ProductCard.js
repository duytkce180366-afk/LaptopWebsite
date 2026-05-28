import { formatPrice } from '../Data';

function ProductCard({ product, onViewDetails }) {
  const primarySpecs = Object.entries(product.specs).slice(0, 3);

  return (
    <article className="product-card">
      <img src={product.image} alt={product.name} />
      <div className="product-content">
        <div className="card-topline">
          <span>{product.badge}</span>
          <span>{product.stock > 0 ? `${product.stock} in stock` : 'Out of stock'}</span>
        </div>
        <h3>{product.name}</h3>
        <p>{product.category}</p>
        <div className="spec-pills">
          {primarySpecs.map(([key, value]) => (
            <span key={key}>{value}</span>
          ))}
        </div>
        <div className="product-footer">
          <strong>{formatPrice(product.price)}</strong>
          <button type="button" onClick={() => onViewDetails(product.id)}>
            View details
          </button>
        </div>
      </div>
    </article>
  );
}

export default ProductCard;
