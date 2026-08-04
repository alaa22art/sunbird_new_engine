// Maps the component name the AngularJS `react-mount` directive is given to a lazy
// import of the actual React component. Add one entry per migrated module.
//
// NOTE: because vite.config.js builds this as a single UMD file (so it can load as a
// plain <script>, matching how the rest of the app loads scripts), Rollup currently
// inlines every dynamic import into that one bundle rather than emitting real chunks --
// so this doesn't yet get you per-module code splitting, only a name->component lookup.
// Real chunking would mean building as 'amd' (RequireJS is already loaded by the time
// this bundle runs -- see index.html) or 'es' with <script type="module">, and is worth
// revisiting once enough modules are migrated that bundle size actually matters.

const registry = {
  BridgeSmokeTest: () => import('../components/BridgeSmokeTest/BridgeSmokeTest'),
  PasswordResetPage: () => import('../components/PasswordResetPage/PasswordResetPage'),
};

export function resolveComponent(name) {
  const loader = registry[name];
  if (!loader) {
    throw new Error(`react-mount: no component registered under the name "${name}"`);
  }
  return loader().then((mod) => mod.default);
}
