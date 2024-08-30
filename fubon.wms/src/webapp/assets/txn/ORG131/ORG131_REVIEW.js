/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG131_REVIEWController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG131_REVIEWController";
		
		$scope.init = function(){
			$scope.inputVO = {
					seqno : $scope.seqno, 
					cust_id : $scope.cust_id, 
					branch_nbr : $scope.branch_nbr,
					seq : $scope.seq, 
					emp_id : $scope.emp_id, 
					result: $scope.result, 
        	};
		};
        $scope.init();
		
        $scope.removeLead = function() {
			$confirm({text: '是否退回：' + $scope.inputVO.cust_id +'-'+ $scope.inputVO.emp_id + '進用資料?'}, {size: '200px'}).then(function() {
				$scope.sendRecv("ORG131", "reback",	"com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
						}
						if (tota.length > 0) {
                    		$scope.showSuccessMsg("退回成功");
                    		$scope.closeThisDialog('successful');
                    	};
					});
			});
        }
});
