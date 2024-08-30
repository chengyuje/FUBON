/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM82AController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM82AController";
		
		$scope.goPrdDetail = function(data, type) {
			debugger
			var row = {};
			row.PRD_ID = data.M_SE_CODE;
			row.BOND_CNAME = data.M_ISSUER_NM;
			row.CUST_ID = data.M_TP_CNTRP;
			row.TYPE = type;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD131/PRD131_DETAIL.html',
				className: 'PRD131_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		//金市海外債庫存資料
		$scope.getGoldBondAsset = function() {
			$scope.sendRecv("CRM82A", "getGoldBondAsset", "com.systex.jbranch.app.server.fps.crm82A.CRM82AInputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.errorMsg && tota[0].body.errorMsg != "") {
							$scope.showErrorMsg(tota[0].body.errorMsg);
						}
						if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
	            			return;
	            		}
						$scope.totalInvestmentTwd = tota[0].body.totalInvestmentTwd;
						$scope.totalMarketValueTwd = tota[0].body.totalMarketValueTwd;
						$scope.totalROI = tota[0].body.totalROI;
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;

					}
			});
		}
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
			$scope.getGoldBondAsset();
		};
		$scope.init();
				
});
