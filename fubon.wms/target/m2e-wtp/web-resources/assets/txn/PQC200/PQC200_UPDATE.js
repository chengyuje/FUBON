'use strict';
eSoafApp.controller('PQC200_UPDATEController', function(getParameter, $rootScope, $scope, $controller, validateService, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
    $scope.controllerName = "PQC200_UPDATEController";

    $scope.inputVO.row = $scope.row;
    $scope.inputVO.custId = $scope.inputVO.row.APPLY_CUST_ID;
    $scope.inputVO.custName = $scope.inputVO.row.APPLY_CUST_NAME;
    $scope.inputVO.applyQuota = $scope.inputVO.row.APPLY_QUOTA;
    $scope.inputVO.tempApplyQuota = $scope.inputVO.row.APPLY_QUOTA;	// 原申請額度
    
    $scope.getActivePrd = function() {
    	$scope.mappingSet['ACTIVE_PRD_LIST'] = [];
    	
    	$scope.sendRecv("PQC100", "getActivePrd", "com.systex.jbranch.app.server.fps.pqc100.PQC100InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				return;
			}
			if (tota.length > 0) {
				$scope.mappingSet['ACTIVE_PRD_LIST'] = tota[0].body.activePrdList;
			}
    	});
    };

    $scope.getLaveQuota = function() {
    	$scope.sendRecv("PQC200", "getLaveQuota", "com.systex.jbranch.app.server.fps.pqc200.PQC200InputVO", {'searchPrdType': $scope.inputVO.row.APPLY_PRD_TYPE, 'searchPrdID': $scope.inputVO.row.APPLY_PRD_ID}, function(tota, isError) {
			if (!isError) {
				$scope.inputVO.SHOW_START_DATE = tota[0].body.showStartDate;
				$scope.inputVO.SHOW_END_DATE = tota[0].body.showEndDate;
				
				$scope.inputVO.MIN_QUOTA = tota[0].body.MIN_QUOTA;
				$scope.inputVO.MAX_QUOTA = tota[0].body.MAX_QUOTA;
				$scope.inputVO.LAVE_QUOTA = tota[0].body.LAVE_QUOTA;
				$scope.inputVO.addStartDate = tota[0].body.START_DATE;
				$scope.inputVO.addEndDate = tota[0].body.END_DATE;
				$scope.inputVO.TOTAL_QUOTA_TYPE = tota[0].body.TOTAL_QUOTA_TYPE;
				$scope.inputVO.TOTAL_QUOTA = tota[0].body.TOTAL_QUOTA;
			}
    	});
    }
    $scope.getLaveQuota();
    
	$scope.cancel = function() {
		$scope.closeThisDialog('cancel');
	}
	
	$scope.save = function() {
		var checkData = true;
		
		if (Number($scope.inputVO.applyQuota) % 10000) {
			$scope.showErrorMsg("申請額度需以10,000為倍數輸入");
			checkData = false;
		} else if (Number($scope.inputVO.applyQuota) < 10000) {
			$scope.showErrorMsg("申請額度需大於等於10,000");
			checkData = false;
		} else if (Number($scope.inputVO.applyQuota) < Number($scope.inputVO.MIN_QUOTA)) {
			$scope.showErrorMsg("申請額度需大於個金RM最低申購額度");
			checkData = false;
		} else if (Number($scope.inputVO.applyQuota) > Number($scope.inputVO.MAX_QUOTA)) {
			$scope.showErrorMsg("申請額度需小於個金RM最高申購額度");
			checkData = false;
		} else if (Number($scope.inputVO.applyQuota) > (Number($scope.inputVO.LAVE_QUOTA) + Number($scope.inputVO.tempApplyQuota))) {
			$scope.showErrorMsg("申請額度需小於等於商品/全行/處剩餘額度");
			checkData = false;
		}  else if (Number($scope.inputVO.applyQuota) > Number($scope.inputVO.tempApplyQuota)) {
			$scope.showErrorMsg("申請額度僅可調降，不可增加");
			checkData = false;
		} 
		
		if (!checkData) {
			return;
		}
		
		$scope.inputVO.SEQNO = $scope.inputVO.row.SEQNO;
		
		$scope.sendRecv("PQC200", "upd", "com.systex.jbranch.app.server.fps.pqc200.PQC200InputVO", $scope.inputVO, function(totas, isError) {
			if (!isError) {
				$scope.showSuccessMsg('ehl_01_common_025');
				$scope.closeThisDialog('cancel');
			}
		});
	}
});