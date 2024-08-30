'use strict'; 
eSoafApp.controller('INS260Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS260Controller";
		
		getParameter.XML(["INS.STATUS"],function(tota){
			if(tota){
				$scope.INS_STATUS = tota.data[tota.key.indexOf('INS.STATUS')];
			}
		});
		
//		var custId = $scope.connector('get','INS200_CUST_DATA').CUST_ID;
		$scope.inputVO = {
			'ins200_from_ins132': $rootScope.INS200_FROM_INS132,
			'planKeyno': $scope.planKeyno,
			'chkType': [],
			'custId' : $scope.custId
		};
		if($rootScope.INS200_FROM_INS132) {
			$scope.inputVO.INS200_FamilyGap = $rootScope.INS200_FamilyGap;
			$scope.inputVO.INS200_Accident = $rootScope.INS200_Accident;
			$scope.inputVO.INS200_Health = $rootScope.INS200_Health;
		}
		$scope.type1Enable = false; $scope.type2Enable = false;
		$scope.type3Enable = false; $scope.type4Enable = false;
		$scope.sendRecv("INS260", "inquire", "com.systex.jbranch.app.server.fps.ins260.INS260InputVO", $scope.inputVO,
			function(totas, isError) {
            	if (!isError) {
            		if(totas[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
            		angular.forEach(totas[0].body.resultList, function(row) {
						if(row.PLAN_TYPE == '1') {
							$scope.inputVO.chkType.push('1');
							$scope.type1Enable = true;
							$scope.type1Text = $filter('mapping')(row.STATUS, $scope.INS_STATUS) || "未規劃";
						}
						else if(row.PLAN_TYPE == '2') {
							$scope.inputVO.chkType.push('2');
							$scope.type2Enable = true;
							$scope.type2Text = $filter('mapping')(row.STATUS, $scope.INS_STATUS) || "未規劃";
						}
						else if(row.PLAN_TYPE == '3') {
							$scope.inputVO.chkType.push('3');
							$scope.type3Enable = true;
							$scope.type3Text = $filter('mapping')(row.STATUS, $scope.INS_STATUS) || "未規劃";
						}
						else if(row.PLAN_TYPE == '4') {
							$scope.inputVO.chkType.push('4');
							$scope.type4Enable = true;
							$scope.type4Text = $filter('mapping')(row.STATUS, $scope.INS_STATUS) || "未規劃";
						}
					});
            	};
			}
		);
		
		$scope.toggleSelection = function toggleSelection(data) {
        	var idx = $scope.inputVO.chkType.indexOf(data);
        	if (idx > -1) {
        		$scope.inputVO.chkType.splice(idx, 1);
        	} else {
        		$scope.inputVO.chkType.push(data);
        	}
        };
        
        $scope.printReport = function() {
        	if($scope.inputVO.chkType.length == 0) {
        		$scope.showErrorMsg('請至少選一個');
        		return;
        	}
        	        	
        	$scope.sendRecv("INS260", "printReport", "com.systex.jbranch.app.server.fps.ins260.INS260InputVO", $scope.inputVO,
    			function(totas, isError) {
                	if (!isError) {
                		$scope.closeThisDialog('successful');
                	};
    			}
    		);
        };
		
		
		
});