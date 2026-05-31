const themeToggle = document.querySelector('[data-theme-toggle]');
const root = document.documentElement;

function setTheme(theme) {
  if (theme === 'dark') {
    root.dataset.theme = 'dark';
    localStorage.setItem('techstore-theme', 'dark');
    themeToggle?.setAttribute('aria-pressed', 'true');
    themeToggle?.setAttribute('aria-label', 'Switch to light mode');
    return;
  }

  delete root.dataset.theme;
  localStorage.setItem('techstore-theme', 'light');
  themeToggle?.setAttribute('aria-pressed', 'false');
  themeToggle?.setAttribute('aria-label', 'Switch to dark mode');
}

setTheme(localStorage.getItem('techstore-theme') === 'dark' ? 'dark' : 'light');

themeToggle?.addEventListener('click', () => {
  setTheme(root.dataset.theme === 'dark' ? 'light' : 'dark');
});
