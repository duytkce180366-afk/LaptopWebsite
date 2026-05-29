export function renderReviews(product) {
  return `
    <section class="reviews-section" id="reviews" aria-labelledby="reviews-title">
      <div class="section-heading">
        <p class="eyebrow">View Reviews</p>
        <h2 id="reviews-title">Customer reviews for ${product.name}</h2>
      </div>
      <div class="reviews-grid">
        ${product.reviews
          .map(
            (review) => `
              <article class="review-card">
                <div>
                  <strong>${review.user}</strong>
                  <span>${review.date}</span>
                </div>
                <p class="stars" aria-label="${review.rating} out of 5 rating">Rating: ${review.rating}/5</p>
                <p>${review.comment}</p>
              </article>
            `
          )
          .join('')}
      </div>
    </section>
  `;
}
