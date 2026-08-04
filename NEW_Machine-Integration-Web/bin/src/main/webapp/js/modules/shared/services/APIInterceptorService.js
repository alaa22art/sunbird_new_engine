define(['app', 'util'], function (app, util) {
	'use strict';
	app.service('APIInterceptor', ['$rootScope', '$state', '$q', function ($rootScope, $state, $q) {
		var serverRequests = [];
		this.getServerRequests = function () {
			return serverRequests;
		};
		this.insertServerRequest = function (request) {
			serverRequests.push(request)
		};

		function httpEnableClick(responseId) {
			if (!responseId) {
				return;
			}
			$rootScope.$broadcast('httpEnableClick', responseId);
		}

		this.request = function (config) {
			if (config.method === "POST" && util.token && !config.url.endsWith("oauth/token") && !config.url.endsWith(".pub.srvc")) {
				config.headers['Authorization'] = "Bearer " + util.token;
			}
			return config;
		};

		this.requestError = function (config) {
			httpEnableClick(config.id);
			return config;
		};

		this.response = function (response) {
			httpEnableClick(response.config.id);
			return response;
		};

		var apisAwaitingRefresh = [];
		var refreshInProgress = false;

		this.responseError = function (responseError) {
			if (responseError.config.method !== "POST") {
				return;
			}
			httpEnableClick(responseError.config.id);
			if (responseError.status === 401) {
				if ('error_description' in responseError.data) {
					if (responseError.data.error_description.startsWith("Access token expired") ||
						responseError.data.error_description.startsWith("accessTokenExpired")) {
						responseError.data.isVisible = false;
						if (!responseError.config.url.endsWith("/oauth/token")) {//any failed request which isn't a login or refresh_token request
							var deferred = $q.defer();
							apisAwaitingRefresh.push({
								responseError: responseError,
								deferred: deferred
							});
							if (!refreshInProgress) {
								refreshInProgress = true;
								util.doRefreshToken().then(function (refreshResponse) {
									var isRememberMe = util.getItemFromStorage("local", "token") ? true : false;
									util.setUserData(refreshResponse.data, isRememberMe);
									for (var i = 0; i < apisAwaitingRefresh.length; i++) {
										util.$http(apisAwaitingRefresh[i].responseError.config)
											.then(apisAwaitingRefresh[i].deferred.resolve, apisAwaitingRefresh[i].deferred.reject);
									}
									apisAwaitingRefresh = [];
									refreshInProgress = false;
								}, function () {
									//this error is thrown for expired refresh token
									for (var i = 0; i < apisAwaitingRefresh.length; i++) {
										apisAwaitingRefresh[i].deferred.reject(responseError);
									}
									apisAwaitingRefresh = [];
									refreshInProgress = false;
									util.createToast(util.systemMessages.invalidToken, "error");
									util.clearUtilData();
									$state.go('login');
								});
							}
							return deferred.promise;
						}
					} else {
						util.clearUtilData();
						$state.go('login');
					}
				} else {
					util.clearUtilData();
					$state.go('login');
				}
			}
			errorHandler(responseError.data);
			throw responseError;
		};

		function errorHandler(data) {
			if (data == null) { // fallback
				util.createToast(util.systemMessages.somethingWrong, "error");
				return;
			}
			if (util.isJsonString(data)) {
				data = JSON.parse(data);
			}
			//handle token filtration  errors
			if (data.authenticationFilterError) {
				//handling expired token
				if (data.errorCode == "tokenExpired") {
					util.clearUtilData();
					$state.go("home");
					return;
				}
			}

			var errorCode;
			if (data.error_description != null) {
				errorCode = data.error_description;
			} else {
				errorCode = data.errorCode;
			}

			var parameters = data.parameters;
			var translatedError;
			if (util.systemMessages.hasOwnProperty(errorCode)) { // systemMessages are set             
				translatedError = util.systemMessages[errorCode];
				if (parameters) {
					for (var key in parameters) {
						translatedError = translatedError.replace(key, parameters[key]);
					}
				}
			} else {
				translatedError = util.systemMessages.somethingWrong;
			}
			util.createToast(translatedError || "Something wrong happened", data.errorSeverity || "error");
		}

	}]);

});