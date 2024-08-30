/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS302_QUERYController', function($rootScope, $scope, $controller, projInfoService, socketService, ngDialog) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS302_QUERYController";

	$scope.paramList = [];
	$scope.totalList = [];
	$scope.inputVO = {};
	$scope.inputVO.sCreDate = $scope.sCreDate;
	$scope.inputVO.ao_code = $scope.ao_code;
	$scope.inputVO.srchType = $scope.srchType;
	$scope.inputVO.tx_ym = $scope.tx_ym;
	$scope.inputVO.branch_nbr = $scope.branch_nbr;
	$scope.sendRecv("PMS302", "queryDetail", "com.systex.jbranch.app.server.fps.pms302.PMS302InputdetailVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if (tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
						return;
					}
					$scope.totalList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
				}
	});

    /** 查詢客戶銷量 */
	$scope.questCustDetail = function(custId) {
		var vo = angular.copy($scope.inputVO);
		vo.cust_id = custId;

		var now = new Date();
		if (vo.srchType == 'NOW'){
			var Today = new Date();
			vo.tx_ym = Today.yyyymmdd();
		}else{
			var Today = new Date();
            vo.tx_ym = $scope.inputVO.tx_ym;
		}
			
         
		ngDialog.open({
			template:'assets/txn/PMS302/PMS302_custDetail.html',
			className:'PMS302Cust',
			controller:['$scope',function($scope) {
				$scope.inputVO = vo;
			}]
		});
	}
	
	Date.prototype.yyyymmdd = function() {
		var mm = this.getMonth() + 1; // getMonth() is zero-based
		var dd = this.getDate();
		return [ this.getFullYear(), (mm > 9 ? '' : '0') + mm, (dd > 9 ? '' : '0') + dd ].join('');
	};
	
});