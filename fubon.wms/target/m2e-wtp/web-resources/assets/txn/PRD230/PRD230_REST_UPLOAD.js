/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD230_REST_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD230_REST_UPLOADController";
		
		$scope.inputVO = {
			prdType:'FUND'
		};
				
		$scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        };
		
        $scope.save = function() {
        	$scope.sendRecv("PRD280", "uploadREST", "com.systex.jbranch.app.server.fps.prd280.PRD280InputVO", $scope.inputVO,
					function(tota, isError) {
        				if (!isError) {
        					if(tota[0].body.errorList && tota[0].body.errorList.length > 0)
		            			$scope.showErrorMsg('ehl_01_prd210_004',[tota[0].body.errorList.toString()]);
        					if(tota[0].body.errorList2 && tota[0].body.errorList2.length > 0)
		            			$scope.showErrorMsg('ehl_01_prd210_002',[tota[0].body.errorList2.toString()]);
        					$scope.showSuccessMsg('ehl_01_common_004');
		            		$scope.closeThisDialog('successful');
		            	};
			});
        };		
});