/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM82BController',
	function($rootScope, $scope, $controller, ngDialog) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM82BController";
		
		
		// 境外結構型債券-金市資料
		$scope.getGoldSnAsset = function() {
			$scope.sendRecv("CRM82B", "getGoldSnAsset", "com.systex.jbranch.app.server.fps.crm82B.CRM82BInputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.errorMsg && tota[0].body.errorMsg != "") {
							$scope.showErrorMsg(tota[0].body.errorMsg);
						}
						if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
	            			return;
	            		}
						$scope.SUMTrustVal = tota[0].body.SUMTrustVal;
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
					}
			});
		}
		
		$scope.goPrdDetail = function(data) {
			debugger
			var row = {};
			row.PRD_ID = data.PROD_ID;
			row.BOND_CNAME = data.PROD_NAME;
			row.CUST_ID = data.CUST_ID;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD141/PRD141_DETAIL.html',
				className: 'PRD141_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		$scope.detail = function (row) {
			var cust_id = $scope.inputVO.cust_id;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM82B/CRM82B_DETAIL.html',
				className: 'CRM82B_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.cust_id = cust_id;
                }]
			});
		}
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
			$scope.getGoldSnAsset();
		};
		
		$scope.init();
});
