import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// «npm run build» deja la interfaz compilada en la carpeta static de Spring Boot, que la sirve junto con la API.
export default defineConfig({
  plugins: [react()],
  build: { outDir: '../backend/src/main/resources/static', emptyOutDir: true },
  // En desarrollo («npm run dev») las llamadas a /api se envían al backend Spring Boot.
  server: { proxy: { '/api': 'http://localhost:8080' } },
});
