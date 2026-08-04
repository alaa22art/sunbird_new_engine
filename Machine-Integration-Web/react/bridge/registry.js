// Maps the component name the AngularJS `react-mount` directive is given to a lazy
// import of the actual React component. Add one entry per migrated module -- keeping
// this as dynamic imports means each module ships as its own chunk, so pages that
// haven't been migrated yet never pay for the ones that have.

const registry = {
  BridgeSmokeTest: () => import('../components/BridgeSmokeTest/BridgeSmokeTest'),
};

export function resolveComponent(name) {
  const loader = registry[name];
  if (!loader) {
    throw new Error(`react-mount: no component registered under the name "${name}"`);
  }
  return loader().then((mod) => mod.default);
}
