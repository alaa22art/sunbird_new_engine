define(['app'], function (app) {
    'use strict';
    // Bridges a single React component into the AngularJS view tree.
    //   <react-mount component="'BridgeSmokeTest'" props="{ foo: bar }"></react-mount>
    // `component` is the name used in react/bridge/registry.js. `props` is optional
    // and re-evaluated (and re-rendered) whenever it changes.
    // Requires assets/react/react-bridge.js (built from react/main.jsx by Vite) to
    // have loaded first so window.ReactBridge exists -- see index.html.
    app.directive('reactMount', function () {
        return {
            restrict: 'E',
            scope: {
                component: '<',
                props: '<'
            },
            link: function (scope, element) {
                var node = element[0];

                function render() {
                    if (!window.ReactBridge) {
                        console.error('react-mount: window.ReactBridge is not loaded yet (check assets/react/react-bridge.js in index.html)');
                        return;
                    }
                    window.ReactBridge.mount(node, scope.component, scope.props || {});
                }

                render();
                scope.$watch('props', render, true);

                scope.$on('$destroy', function () {
                    if (window.ReactBridge) {
                        window.ReactBridge.unmount(node);
                    }
                });
            }
        };
    });
});
