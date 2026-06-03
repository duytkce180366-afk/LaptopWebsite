document.querySelectorAll('[data-hover-category]').forEach((categoryLink) => {
  categoryLink.addEventListener('mouseenter', () => showMegaPanel(categoryLink.dataset.hoverCategory));
  categoryLink.addEventListener('focus', () => showMegaPanel(categoryLink.dataset.hoverCategory));
});

function showMegaPanel(categoryId) {
  document.querySelectorAll('.mega-panel').forEach((panel) => {
    panel.classList.remove('visible');
  });

  document.querySelector(`[data-mega-panel="${categoryId}"]`)?.classList.add('visible');

  document.querySelectorAll('[data-hover-category]').forEach((categoryLink) => {
    categoryLink.classList.toggle('active', categoryLink.dataset.hoverCategory === categoryId);
  });
}
