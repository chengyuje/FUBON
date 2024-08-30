/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM661_rel_rpyController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM661_rel_rpyController";
		
		//rel_type
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
		
        //rel_status
        $scope.mappingSet['REL_STATUS'] = [];
		$scope.mappingSet['REL_STATUS'].push({LABEL : '新增關係戶',DATA : 'RAN'},{LABEL : '刪除關係戶',DATA : 'RAD'},
											 {LABEL : '歸戶申請',DATA : 'RAJ'},{LABEL : '歸戶申請取消',DATA : 'RAC'});
		
		
		
		$scope.init = function() {
			$scope.inputVO.rel_status = '';
			$scope.inputVO.rel_rpy_type = '';
			$scope.inputVO.seq ='';
			$scope.inputVO.cust_id_s = '';
			$scope.inputVO.join_srv_cust_id = '';
		}
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.resultList_rel_rpy = [];
		}
		$scope.inquireInit();
		
		
		//關係戶覆核初始查詢
		$scope.rel_rpy_inquire = function(){
		$scope.sendRecv("CRM661", "rel_rpy_inquire", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					if(tota[0].body.resultList_rel_rpy != null && tota[0].body.resultList_rel_rpy.length > 0 ){
						$scope.resultList_rel_rpy = tota[0].body.resultList_rel_rpy;
					}
				}
		)};
		$scope.rel_rpy_inquire();
		
		
		$scope.rel_rpy = function(row, rel_rpy_type) {
			$scope.inputVO.rel_rpy_type = rel_rpy_type;
			$scope.inputVO.seq = row.SEQ;
			$scope.inputVO.rel_status = row.REL_STATUS;
			$scope.inputVO.cust_id = row.CUST_ID_S;
			$scope.inputVO.join_srv_cust_id = row.JOIN_SRV_CUST_ID;
			$scope.inputVO.cust_id_m = row.CUST_ID_M;
			$scope.sendRecv("CRM661", "rel_rpy", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", $scope.inputVO,
    			function(tota, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(tota.body.msgData);
        				return;
	                }
	                if (tota.length > 0) {
	                	$scope.showMsg('覆核成功');
	                	$scope.init();
	                	$scope.inquireInit();
	                	$scope.rel_rpy_inquire();
	                };
			});
		}
});