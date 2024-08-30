'use strict';
eSoafApp.controller('PMS408_DETAILController', function($rootScope, $scope, $controller) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "PMS408_DETAILController";

	/** 查詢明細 **/
    $scope.queryCompare = function() {
    	$scope.sendRecv("PMS408", "queryCompare", "com.systex.jbranch.app.server.fps.pms408.PMS408InputVO", {compareDataDate:$scope.row.DATA_DATE, compareCustID:$scope.row.CUST_ID, compareDataType:$scope.row.DATA_TYPE_O}, function(tota, isError) {
    		if (!isError) {
				$scope.compareDtlList = tota[0].body.compareDtlList;
				$scope.outputVO = tota[0].body;
				
				if (!$scope.compareDtlList.length)
					$scope.showMsg("ehl_01_common_009");
			}						
    	});
    }; 
    
	$scope.queryCompare();

    $scope.comma_split = function(value) {
    	if (null == value) {
    		return '';
    	} else {
    		return value.split('|');
    	}
    	
    }
    
    $scope.exportRPT = function(rptVersion){
    	$scope.inputVO.rptVersion = rptVersion;
		$scope.inputVO.compareDtlList = $scope.compareDtlList;
		
		$scope.sendRecv("PMS408", "export", "com.systex.jbranch.app.server.fps.pms408.PMS408InputVO", $scope.inputVO, function(tota, isError) {						
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
        	}
			
			if (tota.length > 0) {
        		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
        			$scope.showMsg("ehl_01_common_009");
        			return;
	        	}
	        }
		});
	};
});