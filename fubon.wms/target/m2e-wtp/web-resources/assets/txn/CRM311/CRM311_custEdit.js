/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM311custEditController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM311custEditController";
		
		// 2017/2/15
		$scope.sendRecv("CRM311", "getAo", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.AO_JOB_RANK = tota[0].body.resultList1;
						return;
					}
		});
		//
				
		//修改初始畫面
		$scope.init = function(){
			$scope.row = $scope.row || {};
			$scope.inputVO = {
				 aum_limit_up:$scope.row.AUM_LIMIT_UP,
				 ttl_cust_no_limit_up:$scope.row.TTL_CUST_NO_LIMIT_UP,
				 cust_ao_job_rank:$scope.row.AO_JOB_RANK,
				 vip_degree:$scope.row.VIP_DEGREE,
				 cust_no_flex_prcnt:$scope.row.CUST_NO_FLEX_PRCNT,
				 cust_no_limit_up:$scope.row.CUST_NO_LIMIT_UP
			};
			console.log('inputVO='+JSON.stringify($scope.inputVO));
			
		};
		$scope.init();
		
		//修改
		$scope.custmodify = function(){
			$scope.inputVO.type = 'Update';
        	$scope.sendRecv("CRM311", 'custmodify', "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
        			function(totas, isError) {
    	                if (isError) {
    	                	$scope.showErrorMsgInDialog(totas.body.msgData);
    	                    return;
    	                }else {
    	                	$scope.showMsg('修改成功');
    		       			$scope.closeThisDialog('successful');
    	                }
    	            }
            	);
 		}
		
});