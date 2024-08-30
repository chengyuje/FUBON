'use strict';
eSoafApp.controller('CRM8101Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM8101Controller";
	
	// filter
	getParameter.XML(["COMMON.YES_NO"], function(totas) {
		if (totas) {
			$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
		}
	});
	// ===
	
	//畫圖參數設定
	$scope.options = {
		chart: {
			type: 'pieChart',
			height: 650,
			width : 650,
			x: function(d){return d.key;},
			y: function(d){return d.y;},
			showLabels: true,
			duration: 300,
			labelThreshold: 0.01,
			labelSunbeamLayout: false,
			labelType: "fubon",
			donutLabelsOutside: true,
			legend: {
				margin: {
					top: 4,
					right: 0,
					bottom: 0,
                	left: 0 
                },
				align: true
			},
			noData : '查無資料'
		}
	};
	
	$scope.init = function() {
		$scope.inputVO = {
			custID : ''
		};
		
		$scope.openFlag = 'F';
		$scope.custList = [];
		$scope.prdCurrList = [];
		$scope.prdList = [];
		$scope.incomeList = [];
	};
	
	$scope.init();

	$scope.query = function() {
		$scope.custList = [];
		$scope.prdCurrList = [];
		$scope.prdList = [];
		$scope.incomeList = [];
		
		$scope.sendRecv("CRM8101", "query", "com.systex.jbranch.app.server.fps.crm8101.CRM8101InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.custList = tota[0].body.custList;
				$scope.prdCurrList = tota[0].body.prdCurrList;
				$scope.prdList = tota[0].body.prdList;
				$scope.incomeList = tota[0].body.incomeList;

				if ($scope.prdList == null) {
					$scope.data = [];
				} else {
					$scope.data = [];
					
					//繪圖資料
					angular.forEach($scope.prdList, function(row, index, objs){
						$scope.data.push({key: row.PROD_TYPE, y: row.PROD_PR});
					}); 
				}
			}	
		});
	}

	$scope.open = function(flag) {
		$scope.openFlag = flag;
	};	
});