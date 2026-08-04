import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Builds react/main.jsx as a UMD bundle emitted straight into the webapp source tree
// (same pattern the Gulp/Sass pipeline already uses for assets/css), so `mvn package`
// picks it up with no extra wiring. Loaded by index.html as window.ReactBridge -- see
// react/main.jsx and js/modules/shared/directives/reactMount/reactMount.js.
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'src/main/webapp/assets/react',
    emptyOutDir: true,
    lib: {
      entry: 'react/main.jsx',
      name: 'ReactBridge',
      formats: ['umd'],
      fileName: () => 'react-bridge.js',
    },
    rollupOptions: {
      output: {
        // Keep this a single global-scope script (no ES module externals) since it
        // has to load as a plain <script> alongside the RequireJS/AMD AngularJS app.
        globals: {},
      },
    },
  },
});
