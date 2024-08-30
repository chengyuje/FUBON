/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD174_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD174_UPLOADController";
		
		$scope.inputVO = {
			P_TYPE: $scope.type
		};
		
		$scope.downloadSimple = function() {
        	$scope.sendRecv("PRD174", "downloadSimple", "com.systex.jbranch.app.server.fps.prd174.PRD174InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		$scope.uploadFinshed = function(name, rname) {
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        };
		
        $scope.save = function() {
        	$scope.sendRecv("PRD174", "upload", "com.systex.jbranch.app.server.fps.prd174.PRD174InputVO", $scope.inputVO,
					function(tota, isError) {
		        		if (isError) {
		            		$scope.showErrorMsg(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		if(tota[0].body.errorList && tota[0].body.errorList.length > 0) {
		            			var wid = "";
	    						angular.forEach(tota[0].body.errorList, function(row) {
	    							wid += row + ", ";
	    						});
	    						$scope.showErrorMsg('上傳, '+wid+'error');
		            		}
		            		$scope.showSuccessMsg('ehl_01_common_004');
		            		$scope.closeThisDialog('successful');
		            	};
			});
        };
		
		
});