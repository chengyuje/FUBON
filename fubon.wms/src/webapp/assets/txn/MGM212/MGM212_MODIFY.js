/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM212_MODIFYController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM212_MODIFYController";

		//檢核案件序號、查詢案件點數
		$scope.getPoints = function(){
			if($scope.inputVO.seq != undefined && $scope.inputVO.seq != ''){
				$scope.sendRecv("MGM212", "checkSeq", "com.systex.jbranch.app.server.fps.mgm212.MGM212InputVO", 
					{'act_seq' : $scope.act_seq, 'seq' : $scope.inputVO.seq},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length > 0) {
									$scope.seq = $scope.inputVO.seq;
									$scope.mgm_cust_id = tota[0].body.resultList[0].MGM_CUST_ID;
									$scope.be_edit_points = tota[0].body.resultList[0].BE_EDIT_POINTS;
								} else {
									$scope.inputVO.seq = undefined;
									$scope.seq = '';
									$scope.mgm_cust_id = '';
									$scope.be_edit_points = '';
									$scope.showMsg("ehl_01_common_009");		//查無資料
								}
							} else {
								$scope.inputVO.seq = undefined;
								$scope.seq = '';
								$scope.mgm_cust_id = '';
								$scope.be_edit_points = '';
							}
				});				
			} else {
				$scope.showErrorMsg("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
				return;
			}
		}
		
		//init
		$scope.init = function(){
			$scope.inputVO.seq = undefined;
			$scope.inputVO.appr_points = undefined;
			if($scope.mgm212_seq != undefined){
				$scope.inputVO.seq = $scope.mgm212_seq;	
				$scope.getPoints();
			}
		};
		$scope.init();
		
		//修改憑證上傳
        $scope.upload = function(name, rname) {
        	if(name){
        		$scope.inputVO.evidence_name = name;
            	$scope.inputVO.real_evidence_name = rname;
        	}
        };
		
		$scope.saveModify = function(){
			if($scope.inputVO.seq != undefined && $scope.inputVO.seq != '' && 
			   $scope.inputVO.appr_points != undefined && $scope.inputVO.appr_points != '' && 
			   $scope.inputVO.modify_reason != undefined && $scope.inputVO.modify_reason != ''){
				
				$scope.sendRecv("MGM212", "saveModify", "com.systex.jbranch.app.server.fps.mgm212.MGM212InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('ehl_01_common_025'); 	//儲存成功
							$scope.closeThisDialog('successful');
						}
				});	
			} else {
				$scope.showErrorMsg("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
				return;
			}
		}
		
});
