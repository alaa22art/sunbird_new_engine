define(['app', 'config'], function(app, config) {
    'use strict';

    /*
     * LocalStorage Service
     * The LocalStorage service provides an easy way to handle data in browser localstorage
     */
    app.factory('localStorage', ['$window', function($window) {

        /*
         *  getAll
         *  No parameters required.
         *  return all values in local storage
         */
        var getAll = function() {
            var obj = JSON.parse(JSON.stringify($window.localStorage)); //To clone the object
            
            for (var i = 0; i < Object.keys(obj).length; i++) { //Use for instead of forEach to support older android versions (4.1, 4.3)
                var key = Object.keys(obj)[i];

                try {
                    obj[key] = JSON.parse(obj[key]); //If able to parse then its an stringified object
                } catch (e){
                    obj[key] = obj[key]; //If not return plain text value
                }
            }

            return obj;
        };

        /*
         *  get
         *  @prop; string
         *  return certain value in local storage
         */
        var get = function(prop) {
            var item = $window.localStorage.getItem(prop);
            try {
                return JSON.parse(item); //If able to parse then its an stringified object
            } catch (e){
                return item; //If not return plain text value
            }
        };

        /*
         *  setAll
         *  @obj; object
         *  set the value of the local storage to the values in an object clearing all sat before.
         */
        var setAll = function(obj) {

            try {
                $window.localStorage.clear();

                for (var i = 0; i < Object.keys(obj).length; i++) {
                    var key = Object.keys(obj)[i];
                    //if value is an object stringify it
                    obj[key] = typeof(obj[key]) === 'object' ? JSON.stringify(obj[key]) : obj[key];
                    $window.localStorage[key] = obj[key];
                }
                return getAll();
            } catch (e) { // might be a full storage
                console.warn(e);
                return false;
            }
        };

        /*
         *  set
         *  @obj; object
         *  add set of values to the local storage.
         */
        var set = function(obj) {

            try {
                for (var i = 0; i < Object.keys(obj).length; i++) {
                    var key = Object.keys(obj)[i];
                    //if value is an object stringify it
                    obj[key] = typeof(obj[key]) === 'object' ? JSON.stringify(obj[key]) : obj[key];
                    $window.localStorage[key] = obj[key];
                }
                return getAll();
            } catch (e) { // might be a full storage
                console.warn(e);
                return false;
            }
        };

        /*
         *  clearAll
         *  No parameters required.
         *  Clear's all properties from local storage
         */
        var clearAll = function() {
            $window.localStorage.clear();
            return null;
        };

        /*
         *  clear
         *  @prop; string
         *  set the value of one property from local storage to empty
         */
        var clear = function(prop) {
            $window.localStorage.removeItem(prop);
            return getAll();
        };

        return {
            getAll: getAll,
            get: get,
            setAll: setAll,
            set: set,
            clearAll: clearAll,
            clear: clear
        };

    }]);
});