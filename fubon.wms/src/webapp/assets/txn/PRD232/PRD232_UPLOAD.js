/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD232_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD232_UPLOADController";
		
		$scope.inputVO = {};
		
		$scope.downloadSimple = function() {
        	$scope.sendRecv("PRD232", "downloadSimple", "com.systex.jbranch.app.server.fps.prd232.PRD232InputVO", {},
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
        	$scope.sendRecv("PRD232", "upload", "com.systex.jbranch.app.server.fps.prd232.PRD232InputVO", $scope.inputVO,
                function(tota, isError) {
                    if (isError) {
                        $scope.showErrorMsg(tota[0].body.msgData);
                    }

                    var body = tota[0].body;
                    if(body.errorList2.length)
                        $scope.showErrorMsg('ehl_01_prd210_003',[body.errorList2.toString()]);
                    if(body.errorList3.length)
                        $scope.showErrorMsg('ehl_01_prd210_004',[body.errorList3.toString()]);
                    if(body.errorList4.length)
                        $scope.showErrorMsg('ehl_01_prd210_005',[body.errorList4.toString()]);
                    if(body.errorList5.length)
                        $scope.showErrorMsg('ehl_01_prd210_002',[body.errorList5.toString()]);


                    if (!(body.errorList2.length || body.errorList3.length || body.errorList4.length || body.errorList5.length)) {
                        $scope.showSuccessMsg('ehl_01_common_004');
                        $scope.closeThisDialog('successful');
                    }
			});
        };
		
		
});