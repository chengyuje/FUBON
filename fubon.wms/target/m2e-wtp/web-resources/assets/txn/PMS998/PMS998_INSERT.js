'use strict';
eSoafApp.controller('PMS998_INSERTController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService, $q) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS998_INSERTController";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	$scope.priID = String(sysInfoService.getPriID());
	$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag());
	
	$scope.init= function(){
		$scope.inputVO = {
			branchNbr    : '',
			empID        : '',
			custID       : '',
			custName     : '',
			c2eRelation  : '',
			prove        : '',
			note         : ''
		};

        $scope.mappingSet['UHRM_LIST'] = [];
        if (($scope.memLoginFlag).toLowerCase().startsWith("uhrm")) {
    		$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, function(tota, isError) {
    			if (!isError) { 
    				if (null != tota[0].body.uhrmList) {
        				$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
        			} else {
        				$scope.mappingSet['UHRM_LIST'] = [];
        			}
    			} 
    		});
        } else {
    		//組織連動
            $scope.region = ['N', $scope.inputVO, "regionCenterID", "REGION_LIST", "branchAreaID", "AREA_LIST", "branchNbr", "BRANCH_LIST", "aoCode", "AO_LIST", "empID", "EMP_LIST"];
            $scope.RegionController_setName($scope.region);
        }
	};
	
	$scope.uhrmDept = function () {
		angular.forEach($scope.mappingSet['UHRM_LIST'], function(row, index, objs){
			if ($scope.inputVO.empID == row.value) {
				$scope.inputVO.branchNbr = row.BRANCH_NBR;
				$scope.inputVO.branchName = row.BRANCH_NAME;
			}
		});
	}
	
	$scope.init();
	
	$scope.toUppercase_data = function(value, type){
		switch (type) {
			case 'custid':
				if(value){
					$scope.inputVO.custID = value.toUpperCase();
				}
				break;
			default:
				break;
		}
	};
	
	$scope.getCustName = function () {
		$scope.inputVO.actionType = 'SC';

		$scope.sendRecv("PMS998", "action", "com.systex.jbranch.app.server.fps.pms998.PMS998InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			} else {
				$scope.inputVO.custName = tota[0].body.custName;
			}
		});
	};
	
	$scope.add = function () {
		if (!$scope.inputVO.branchNbr ||
			!$scope.inputVO.empID ||
			!$scope.inputVO.custID || 
			!$scope.inputVO.custName ||
			!$scope.inputVO.c2eRelation ||
			!$scope.inputVO.prove ||
			($scope.inputVO.prove == '03' && !$scope.inputVO.note)
			) {
			
			$scope.showErrorMsg("分行、行員員編、客戶ID、與行員關係、確認佐證，都必須輸入。(若確認佐證為「其他」，請補充說明)");
			return;
		}

		$scope.inputVO.actionType = 'A';
		
		$scope.sendRecv("PMS998", "action", "com.systex.jbranch.app.server.fps.pms998.PMS998InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.showSuccessMsg('ehl_01_common_025');
				$scope.closeThisDialog('successful');
			}
		});
	};
});