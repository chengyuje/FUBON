'use strict';
eSoafApp.controller('PRDDocumentController',
	function($rootScope, $scope, $controller, $confirm,$filter, socketService, ngDialog, projInfoService, getParameter, $q, $http) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRDDocumentController";
		
		// combobox
		getParameter.XML(["SYS.DOC_TYPE"], function(totas) {
			if (totas) {
				$scope.DOC_TYPE = totas.data[totas.key.indexOf('SYS.DOC_TYPE')];
			}
		});
        
        $scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		$scope.sendRecv("PRD120", "getDownload", "com.systex.jbranch.app.server.fps.prd120.PRD120InputVO", {'prd_id':$scope.PRD_ID,'ptype':$scope.PTYPE,'subsystem_type':$scope.SUBSYSTEM_TYPE},
			function(tota, isError) {
				if (!isError) {
					$scope.totalList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
					return;
				}
		});
        
		$scope.download = function(row) {
        	$scope.sendRecv("CUS130", "download", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'fileID': row.DOC_ID,'fileName': row.FILE_NAME},
					function(totas, isError) {
		        		if (!isError) {
							
						}
					}
			);
        };
        $scope.showPDF = function(row) {
        	$scope.sendRecv("CUS130", "showPDF", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'fileID': row.DOC_ID},
					function(totas, isError) {
		        		if (!isError) {
							$http.get(totas[0].body.fileUrl, {responseType: 'arraybuffer'})
						       .success(function (data) {
						           var file = new Blob([data], {type: 'application/pdf'});
						           var fileURL = URL.createObjectURL(file);
//						           $window.open(fileURL);
						           var dialog = ngDialog.open({
				        				template: 'assets/txn/PRD120/PRDDocument_url.html',
				        				className: 'PRDDocument_url',
				        				showClose: false,
				                        controller: ['$scope', function($scope) {
				                        	$scope.isPDF = "PDF";
				                        	$scope.url = fileURL;
				                        }]
				        			});
						       })
					           .error(function (data) {
					        	   $scope.showErrorMsg('GET ' + totas[0].body.fileUrl + " : " + data.status);
						    });
						}
					}
			);
        };
        
        $scope.open = function(row) {
        	var dialog = ngDialog.open({
				template: 'assets/txn/PRD120/PRDDocument_url.html',
				className: 'PRDDocument_url',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.url = row.DOC_URL;
                }]
			});
        };
        
        
		
});