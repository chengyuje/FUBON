/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM661_rel_setController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM661_rel_setController";

		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.cust_id;
			$scope.inputVO.ao_code = $scope.ao_code;
			$scope.inputVO.join_srv_cust_id = '';
			$scope.resultList_rel_set = [];
		}
		$scope.init();
		
		//rel_type歸戶服務
        var vo = {'param_type': 'CRM.REL_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.REL_TYPE']) {
        	$scope.requestComboBox(vo, function(tota) {
        		if (tota[tota.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.REL_TYPE'] = tota[0].body.result;
        			$scope.mappingSet['CRM.REL_TYPE'] = projInfoService.mappingSet['CRM.REL_TYPE'];
        		}
        	});
        } else {
        	$scope.mappingSet['CRM.REL_TYPE'] = projInfoService.mappingSet['CRM.REL_TYPE'];
        }
        
        
		//歸戶服務初始查詢
		$scope.rel_set_inquire = function(){
			$scope.CUST_ID_M = $scope.custVO.CRM661_ID_M;
			$scope.sendRecv("CRM661", "rel_set_inquire", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", {'cust_id':$scope.CUST_ID_M,'ao_code':$scope.inputVO.ao_code},
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					$scope.resultList_rel_set = tota[0].body.resultList_rel_set;
					$scope.inputVO.join_srv_cust_id = $scope.resultList_rel_set[0].JOIN_SRV_CUST_ID;
				});
		}
		$scope.rel_set_inquire();
		
		//歸戶服務設定
		$scope.rel_set = function() {
			var ans = $scope.resultList_rel_set;
			$scope.sendRecv("CRM661", "rel_set", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", {'rel_set_list': ans , 'join_srv_cust_id': $scope.inputVO.join_srv_cust_id} ,
	    			function(tota, isError) {
	        			if (isError) {
	        				$scope.showErrorMsgInDialog(tota.body.msgData);
	        				return;
		                }
	        			
		                if (tota.length > 0) {
		                	$scope.showMsg('儲存成功');
		                	$scope.closeThisDialog('successful');
		                };
			});
		}
	}
);