export function formatSpecLabel(key) {
  return key
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, (letter) => letter.toUpperCase())
    .replace('Gpu', 'GPU')
    .replace('Cpu', 'CPU')
    .replace('Dpi', 'DPI')
    .replace('Tdp', 'TDP')
    .replace('Vram', 'VRAM');
}
