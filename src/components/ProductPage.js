import ProductDetails from './ProductDetails';
import Reviews from './Reviews';

function ProductPage({ product, onBack }) {
  return (
    <div className="product-page">
      <button className="back-button" type="button" onClick={onBack}>
        Back to products
      </button>
      <ProductDetails product={product} />
      <Reviews product={product} />
    </div>
  );
}

export default ProductPage;
