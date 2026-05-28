import { render, screen } from '@testing-library/react';
import App from './App';

test('renders laptop catalog demo', () => {
  render(<App />);

  expect(screen.getByText(/TechHub/i)).toBeInTheDocument();
  expect(screen.getByRole('heading', { name: /Product catalog/i })).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/Search by product/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /Categories/i })).toBeInTheDocument();
});
