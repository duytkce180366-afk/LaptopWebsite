export function scrollToSection(sectionId) {
  window.setTimeout(() => {
    document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }, 0);
}

export function escapeAttribute(value) {
  return String(value).replace(/"/g, '&quot;');
}
