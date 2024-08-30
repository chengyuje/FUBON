/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD233_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD233_UPLOADController";
		
		$scope.finList = [];
		$scope.inputVO = {};
		
		$scope.downloadSimple = function() {
        	$scope.sendRecv("PRD233", "downloadSimple", "com.systex.jbranch.app.server.fps.prd233.PRD233InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		$scope.uploadFinshed = function(name, rname) {
			$scope.finList = [];
        	$scope.inputVO.fileName = name;
        	$scope.inputVO.fileRealName = rname;
        	$scope.sendRecv("PRD233", "checkID", "com.systex.jbranch.app.server.fps.prd233.PRD233InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.errorList && tota[0].body.errorList.length > 0) {
		            			var wid = [];
	    						angular.forEach(tota[0].body.errorList, function(row) {
	    							wid.push(row);
	    						});
	    						$scope.showErrorMsg('ehl_01_prd210_001',[wid.toString()]);
		            		}
							$scope.finList = tota[0].body.resultList;
							return;
						}
			});
        };
		
        $scope.save = function() {
        	var list = $scope.finList;
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PRD233/PRD233_UPLOAD1.html',
				className: 'PRD233_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.list = list;
                }]
			});
        	dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.closeThisDialog('successful');
				}
			});
        };
		
		
});