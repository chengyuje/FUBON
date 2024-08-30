
'use strict';
eSoafApp.controller('CRM651Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM651Controller";

	// combobox
	getParameter.XML(["KYC.EDUCATION", "KYC.CAREER", "KYC.MARRAGE",'KYC.CHILD_NO'], function(totas) {
		if (totas) {
			$scope.EDUCATION = totas.data[totas.key.indexOf('KYC.EDUCATION')];
			$scope.CAREER = totas.data[totas.key.indexOf('KYC.CAREER')];
			$scope.MARRAGE = totas.data[totas.key.indexOf('KYC.MARRAGE')];
			$scope.CHILDNO = totas.data[totas.key.indexOf('KYC.CHILD_NO')];

		}
	});
	//
	
	$scope.cust_id = $scope.custVO.CUST_ID;

	function queryFirstSubject() {
		$scope.sendRecv("CRM651", "queryFirstSubject", "com.systex.jbranch.app.server.fps.crm651.CRM651InputVO", {'cust_id': $scope.cust_id, 
																												  'data067050_067101_2': crm610Data.data067050_067101_2, 
																												  'data067050_067000': crm610Data.data067050_067000, 
																												  'data067050_067112': crm610Data.data067050_067112 }, function (tota, isError) {
			if (!isError) {
				$scope.data = [];
				if (tota[0].body.resultList != null && tota[0].body.resultList.length > 0) {
					$scope.data = tota[0].body.resultList;
					// 2017/5/12
					angular.forEach($scope.data, function (row) {
						if (row.QSTN_FORMAT == 'D' && row.ANS_CONTENT)
							row.ANS_CONTENT = $filter('date')(new Date(parseFloat(row.ANS_CONTENT)), 'yyyy-MM-dd');
					});
				}
				// 2017/6/5
				$scope.crm651_esbData = tota[0].body.fp032151OutputVO;
				$scope.connector('set', 'CRM651_esbData', $scope.crm651_esbData);
			}
		});
	}

	function init() {
		queryFirstSubject();
	}

	// CRM651 如果是由客戶首頁 include 的，則監聽來自於客戶首頁的廣播，以取得已載入的共用資料
	let crm610Data = {}
	if ($scope.fromCRM610) {
		$scope.$on('CRM610_DATA', function(event, data) {
			console.log('【深度KYC】已收到【客戶首頁】的廣播...');
			crm610Data = data;
			init()
		});
	} else {
		init();
	}

	$scope.jump = function() {
		$scope.connector('set','CRM610_tab', 9);
		var path = "assets/txn/CRM610/CRM610_DETAIL.html"; //連至客戶首頁詳細資料內
		$scope.connector("set","CRM610URL",path);
		$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
    };
	

});
