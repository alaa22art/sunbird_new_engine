define("config", function () {
	'use strict';
	var config = {
		gridPageSizes: [10, 25, 50, 100],
		gridPageButtonCount: 1,
		dateTimeFormat: 'dd/MM/yyyy HH:mm',
		dateFormat: 'dd/MM/yyyy',
		timeFormat: 'HH:mm',
		dateMask: '##/##/####',
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
		regexpIP: new RegExp("^([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})[.]([0-9]{1,3})$"),
		regexpPort: new RegExp("^(?:[0-9]{4}|[0-9]{5})$"),
		lisDir: lisDir, //'dist' for prod, 'js' for dev. This is read from index.html lisDir variable
		versionNo: versionNo,
		server: 'http://192.168.21.114:8055',
		//server: 'http://localhost:8050',
		contextRoot: '/miw/'
	};

	config["api_path"] = config.contextRoot + 'services/';

	return config;
});