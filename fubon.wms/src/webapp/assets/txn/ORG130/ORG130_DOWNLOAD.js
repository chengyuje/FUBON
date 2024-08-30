'use strict';
eSoafApp.controller('ORG130_DOWNLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG130_DOWNLOADController";
		
		getParameter.XML(["ORG.EMP_USE_EXAMPLE"], function(totas) {
			if (totas) {
				$scope.EMP_USE_EXAMPLE = totas.data[totas.key.indexOf('ORG.EMP_USE_EXAMPLE')];
			}
		});
		
		$scope.sendRecv("ORG130", "checkUserId", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO", {},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0)
							$scope.canUpload = false;
						else
							$scope.canUpload = true;
						return;
					}
		});
		
		$scope.download = function(row) {
			$scope.sendRecv("ORG130", "downloadSimple", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO", {'fileLabel': row.LABEL, 'fileData': row.DATA},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
    				}
    		);
		};
		
		$scope.upload = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/ORG130/ORG130_UPLOAD.html',
				className: 'ORG130_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		
		
		
		
});