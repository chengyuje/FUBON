/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM662_prv_rpyController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM662_prv_rpyController";
		
		//prv_type
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
		
        //prv_status
        $scope.mappingSet['PRV_APL_TYPE'] = [];
		$scope.mappingSet['PRV_APL_TYPE'].push({LABEL : '新增家庭會員', DATA : '1'},
											   {LABEL : '刪除家庭會員', DATA : '2'},
											   {LABEL : '新增家庭會員', DATA : '3'},
											   {LABEL : '刪除家庭會員', DATA : '4'},
											   {LABEL : '變更排序', DATA : '5'},
											   {LABEL : '新增家庭會員', DATA : '6'}
		);
		
		
		
		$scope.init = function() {
			$scope.inputVO.cust_id_m = '';
			$scope.inputVO.cust_id_s = '';
			$scope.inputVO.prv_apl_type = '';
			$scope.inputVO.prv_rpy_type = '';
			$scope.inputVO.seq ='';
			$scope.inputVO.prv_mbr_no_new = '';
			$scope.inputVO.prv_mbr_no = '';
			$scope.inputVO.prv_status = '';
			
			
		}
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.resultList_prv_rpy = [];
		}
		$scope.inquireInit();
		
		
		//關係戶覆核初始查詢
		$scope.prv_rpy_inquire = function(){
		$scope.sendRecv("CRM662", "prv_rpy_inquire", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					$scope.resultList_prv_rpy = tota[0].body.resultList_prv_rpy;
	                console.log("覆核的");
	                console.log(JSON.stringify($scope.resultList_prv_rpy.length));
				}
		)};
		$scope.prv_rpy_inquire();
		
		
		$scope.prv_rpy = function(row, prv_rpy_type) {
			$scope.inputVO.cust_id_m = row.CUST_ID_M;
			$scope.inputVO.cust_id_s = row.CUST_ID_S;
			$scope.inputVO.prv_apl_type = row.PRV_APL_TYPE;
			$scope.inputVO.prv_rpy_type = prv_rpy_type;
			$scope.inputVO.prv_mbr_no = row.PRV_MBR_NO;
			$scope.inputVO.prv_mbr_no_new = row.PRV_MBR_NO_NEW;
			$scope.inputVO.prv_status = row.PRV_STATUS;
			
			$scope.inputVO.seq = row.SEQ;
			$scope.sendRecv("CRM662", "prv_rpy", "com.systex.jbranch.app.server.fps.crm662.CRM662InputVO", $scope.inputVO,
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('覆核成功');
	                	$scope.init();
	                	$scope.inquireInit();
	                	$scope.prv_rpy_inquire();
	                };
			});
		}
});