/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS203_SET_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMS203Controller', { $scope: $scope});
		$scope.controllerName = "PMS203_SET_EDITController";		
		
		$scope.init = function(){
			if($scope.row)
				$scope.isUpdate = true;
			$scope.inputVO = {
					reportDate: $scope.ym,
					levelDist: 0,
					olTitle: '',
					jobTitleId: '',
					prodGoals: 0,
					keepGoals: 0,
					advanGoals: 0,
					proStrLine: 0,
					proHorLine: 0,
					proSlaLine: 0,
					demStrLine: 0,
					demHorLine: 0,
					demSlaLine: 0,
					fixSal: 0,
					sCreDate:$scope.ym
        	};
            $scope.inputVO.reportDate = $scope.inputVO.sCreDate;
    		if($scope.isUpdate){
    			$scope.inputVO.reportDate = $scope.row.DATA_YEARMON;
    			$scope.inputVO.levelDist = $scope.row.LEVEL_DISTANCE;
    			$scope.inputVO.olTitle = $scope.row.OL_TITLE;
    			$scope.inputVO.jobTitleId = $scope.row.JOB_TITLE_ID;
    			$scope.inputVO.prodGoals = $scope.row.PRODUCT_GOALS;
    			$scope.inputVO.keepGoals = $scope.row.KEEP_GOALS;
    			$scope.inputVO.advanGoals =$scope.row.ADVAN_GOALS;   			
    			$scope.inputVO.proStrLine = $scope.row.PRO_STR_LINE;    			
    			$scope.inputVO.proHorLine = $scope.row.PRO_HOR_LINE;
    			$scope.inputVO.proSlaLine = $scope.row.PRO_SLA_LINE;    			
    			$scope.inputVO.demStrLine = $scope.row.DEM_STR_LINE;
    			$scope.inputVO.demHorLine = $scope.row.DEM_HOR_LINE;
    			$scope.inputVO.demSlaLine = $scope.row.DEM_SLA_LINE;
    			$scope.inputVO.fixSal = $scope.row.FIX_SAL;    			    			
    		}
      		
        };
        $scope.init();

        $scope.save = function() {
        	//檢核條件
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(紅色＊)');
        		return;
        	}       	
        	if($scope.isUpdate) {        		
        		$scope.sendRecv("PMS203", "updateSET", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
    					function(totas, isError) {
        		
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
        	} else {   
        		$scope.sendRecv("PMS203", "insertSET", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg('ehl_01_common_016');
    	                	}else{
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	}
    					}
    			);        		
        	}   
        };
                	
});
