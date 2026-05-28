function Reviews({ product }) {
  return (
    <section className="reviews-section" id="reviews" aria-labelledby="reviews-title">
      <div className="section-heading">
        <p className="eyebrow">View Reviews</p>
        <h2 id="reviews-title">Customer reviews for {product.name}</h2>
      </div>
      <div className="reviews-grid">
        {product.reviews.map((review) => (
          <article className="review-card" key={`${review.user}-${review.date}`}>
            <div>
              <strong>{review.user}</strong>
              <span>{review.date}</span>
            </div>
            <p className="stars" aria-label={`${review.rating} out of 5 rating`}>
              Rating: {review.rating}/5
            </p>
            <p>{review.comment}</p>
          </article>
        ))}
      </div>
    </section>
  );
}

export default Reviews;
