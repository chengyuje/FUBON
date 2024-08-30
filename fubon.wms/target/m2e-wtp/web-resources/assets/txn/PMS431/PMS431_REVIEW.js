/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS431_REVIEWController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
	
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS431_REVIEWController";
	
	getParameter.XML(["PMS.HIGH_RISK_INV_TYPE", "PMS.HIGH_RISK_INV_DRCR"], function(totas) {
		if (totas) {
			$scope.HIGH_RISK_INV_TYPE = totas.data[totas.key.indexOf('PMS.HIGH_RISK_INV_TYPE')];
			$scope.HIGH_RISK_INV_DRCR = totas.data[totas.key.indexOf('PMS.HIGH_RISK_INV_DRCR')];
		}
	});
	
	$scope.init = function(){
    	$scope.inputVO.snapYYYYMM = $scope.row.SNAP_YYYYMM;
    	$scope.inputVO.aoBranchNBR = $scope.row.BRANCH_NBR;
    	$scope.inputVO.aoEmpID = $scope.row.AO_EMP_CID;
    	$scope.inputVO.kindType = $scope.row.KIND_TYPE;
    	
    	$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'PMS431'}, function(tota, isError) {
			if (!isError) {
				$scope.chkMaintenance = tota[0].body.isMaintenancePRI == 'Y' ? true : false;

				return;
			}						
		});
    };
    
    $scope.init();
    
    $scope.updateFlagR = function (row) {
		row.UPDATE_FLAG_R = 'Y';			
	}
    
    $scope.allUpd = function (type){
		angular.forEach($scope.paramList, function(row) {
			row.HR_ATTR = type;
			row.UPDATE_FLAG_R = 'Y';
			
			if (!row.NOTE && type == "N") {
				row.NOTE = "無異常";
			} else {
				row.NOTE = "";
			}
		});
		
	}
    
	//資料查詢
	$scope.queryR = function() {	
		$scope.sendRecv("PMS431", "queryDetail", "com.systex.jbranch.app.server.fps.pms431.PMS431InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.paramList = tota[0].body.dtlList;
				$scope.totalData = tota[0].body.dtlList;
				$scope.outputVO = tota[0].body;		
				return;
			}						
		});
	};
	
	$scope.queryR();
	
	//資料儲存
	$scope.save = function() {	
		
		$scope.updateListR = [];
		var checkData = true;

		angular.forEach($scope.paramList, function(row, index, objs){
			if(row.UPDATE_FLAG_R == 'Y') {
				if((row.HR_ATTR == 'Y' && !row.NOTE) || !row.HR_ATTR){
					checkData = false; 
					return;
				};
				
				$scope.updateListR.push(row);
			};
		});
		
		if(!checkData){
			$scope.showErrorMsg("已勾選的初判異常轉法遵部調查、備註(如有異常必填入說明)，都必須輸入。");
    		return;
		};
		
        $scope.sendRecv("PMS431", "save", "com.systex.jbranch.app.server.fps.pms431.PMS431InputVO", {editedList: $scope.updateListR}, (_, isError) => {
            if (!isError) {
                $scope.showSuccessMsg('ehl_01_common_025');
                $scope.queryR();
            }
        });
	};
	
});