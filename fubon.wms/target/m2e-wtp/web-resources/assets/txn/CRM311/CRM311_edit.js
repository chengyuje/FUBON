/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM311editController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM311editController";
		
		// 2017/2/15
		$scope.sendRecv("CRM311", "getAo", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.AO_JOB_RANK = tota[0].body.resultList1;
						return;
					}
		});
		//
		/*自訂是否控管AUM上限下拉式選單*/
		$scope.aum_yn = function() {
			$scope.mappingSet['limit_by_aum_yn']=[];
			$scope.mappingSet['limit_by_aum_yn'].push({LABEL:'是',DATA:'Y'},{LABEL:'否',DATA:'N'});
		 };
		
		$scope.aum_yn();
		
		//修改初始畫面
		$scope.init = function(){
			$scope.row = $scope.row || {};
			$scope.inputVO = {
				 ao_ao_job_rank:$scope.row.AO_JOB_RANK,
				 limit_by_aum_yn:$scope.row.LIMIT_BY_AUM_YN,
				 aum_limit_up:$scope.row.AUM_LIMIT_UP,
				 ttl_cust_no_limit_up:$scope.row.TTL_CUST_NO_LIMIT_UP,
			};
			console.log('inputVO='+JSON.stringify($scope.inputVO));
		};
		$scope.init();
		
		//修改
		$scope.modify = function(){
			if($scope.inputVO.limit_by_aum_yn != '') {
				$scope.inputVO.type = 'Update';
	        	$scope.sendRecv("CRM311", 'modify', "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
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
			} else {
				$scope.showErrorMsg("請選擇是否控管AUM上限");
			}
 		}
		
});