'use strict';
eSoafApp.controller('ORG131_DOWNLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG131_DOWNLOADController";
		
		getParameter.XML(["ORG.EMP_USE_EXAMPLE"], function(totas) {
			if (totas) {
				$scope.EMP_USE_EXAMPLE = totas.data[totas.key.indexOf('ORG.EMP_USE_EXAMPLE')];
			}
		});
		
		$scope.sendRecv("ORG131", "checkUserId", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO", {},
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
			$scope.sendRecv("ORG131", "downloadSimple", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO", {'fileLabel': row.LABEL, 'fileData': row.DATA},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
    				}
    		);
		};
		
		$scope.upload = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/ORG131/ORG131_UPLOAD.html',
				className: 'ORG131_UPLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		
		
		
		
});