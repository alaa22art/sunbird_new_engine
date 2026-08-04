// Entry point for the React bridge bundle. Built by Vite in library/UMD mode (see
// vite.config.js) and loaded by src/main/webapp/index.html as a plain <script>, so it
// must expose itself as a global -- there is no module loader shared with the
// RequireJS/AMD AngularJS app.

import { mount, unmount } from './bridge/mount';

window.ReactBridge = { mount, unmount };
