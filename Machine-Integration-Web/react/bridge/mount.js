import { createElement } from 'react';
import { createRoot } from 'react-dom/client';
import { resolveComponent } from './registry';
import '../styles/tokens.css';

// One React root per DOM node the AngularJS `react-mount` directive owns, keyed by
// that node itself so unmount() can find it again without any extra bookkeeping on
// the AngularJS side.
const rootsByNode = new WeakMap();

export async function mount(node, componentName, props) {
  const Component = await resolveComponent(componentName);
  let root = rootsByNode.get(node);
  if (!root) {
    root = createRoot(node);
    rootsByNode.set(node, root);
  }
  root.render(createElement(Component, props));
}

export function unmount(node) {
  const root = rootsByNode.get(node);
  if (root) {
    root.unmount();
    rootsByNode.delete(node);
  }
}
