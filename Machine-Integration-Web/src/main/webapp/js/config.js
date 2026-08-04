define("config", function () {
	'use strict';
	var config = {
		gridPageSizes: [10, 25, 50, 100],
		gridPageButtonCount: 1,
		testSelectionPageSize: 20,
		dateTimeFormat: 'dd/MM/yyyy HH:mm',
		dateFormat: 'dd/MM/yyyy',
		dateMask: '99/99/9999',
		numberFraction: '3',
		kendoPercentageFormat: {
			decimals: 3,
			round: false,
			restrictDecimals: true,
			min: 0,
			max: 100,
			step: 10
		},
		regexpPercent: new RegExp("^[0-9]*([.][0-9]+)?$"),
		regexpNum: new RegExp("^[0-9]*$"),
		lisDir: lisDir, //'dist' for prod, 'js' for dev. This is read from index.html lisDir variable
		versionNo: versionNo,
		//server: 'http://localhost:8055',
		//server: 'http://192.168.21.93:8055',
		//server: 'http://40.113.104.250:8080',
		//server: 'http://192.168.1.34:8079',
		server: window.location.origin,
		
		contextRoot: '/miw/'
	};
	config.api_path = config.contextRoot + 'services/';
	return config;
});