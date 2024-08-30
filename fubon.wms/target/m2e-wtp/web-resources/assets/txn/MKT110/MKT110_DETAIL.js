/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MKT110_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, $http) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MKT110_DETAILController";
		
		// combobox
		getParameter.XML(["CAM.BULLETIN_TYPE", "CAM.PRD_TYPE", "MKT.CHANNEL_CODE"], function(totas) {
			if (totas) {
				$scope.BULLETIN_TYPE = totas.data[totas.key.indexOf('CAM.BULLETIN_TYPE')];
				$scope.PRD_TYPE = totas.data[totas.key.indexOf('CAM.PRD_TYPE')];
				$scope.CHANNEL_CODE = totas.data[totas.key.indexOf('MKT.CHANNEL_CODE')];
			}
		});
		//
		
		$scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		$scope.init = function() {
			$scope.sendRecv("MKT110", "inqEdit", "com.systex.jbranch.app.server.fps.mkt110.MKT110InputVO", {seq:$scope.row.SEQ},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_001");
	                			return;
	                		}
							var ans = tota[0].body.resultList[0];
							$scope.bType = ans.BTYPE;
							$scope.pType = ans.PTYPE;
							$scope.subject = ans.SUBJECT;
							$scope.sDate = $scope.toJsDate(ans.S_DATE);
							$scope.eDate = $scope.toJsDate(ans.E_DATE);
							$scope.orgn = ans.DEPT_NAME;
							$scope.contact = ans.CONTACT;
							$scope.chkCode = [];
							var role = ans.ROLE.split(",");
							var idx = role.indexOf('ALL');
							if (idx > -1) {
								$scope.chkCode.push('全部');
							} else {
								angular.forEach(role, function(row2){
									$scope.chkCode.push($filter('mapping')(row2,$scope.CHANNEL_CODE));
								});
							}
							$scope.chkCode = $scope.chkCode.toString();
							$("#contentHtml").html(ans.CONTENT);
							if(ans.PICTURE) {
								// $scope.pictureSrc = "data:image/png;base64,"+btoa(String.fromCharCode.apply(null, new Uint8Array(ans.PICTURE)));
								// String.fromCharCode(null,array) fails if the array buffer gets too big
								var bufView = new Uint8Array(ans.PICTURE);
    							var length = bufView.length;
    						    var result = '';
    						    var addition = Math.pow(2,16)-1;
    						    for(var i = 0; i<length; i += addition) {
    						    	if(i + addition > length)
    						    		addition = length - i;
    						    	result += String.fromCharCode.apply(null, bufView.subarray(i,i+addition));
    						    }
    						    $scope.pictureSrc = "data:image/png;base64,"+btoa(result);
							}
							return;
						}
			});
			$scope.sendRecv("MKT110", "getUpdateData", "com.systex.jbranch.app.server.fps.mkt110.MKT110InputVO", {seq:$scope.row.SEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.fileName = tota[0].body.resultList;
							$scope.web = tota[0].body.resultList2;
						}
			});
		};
		$scope.init();
		
		$scope.downloadByteFile = function(row) {
        	$scope.sendRecv("CUS130", "download", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'fileID': row.DOC_ID,'fileName': row.DOC_NAME},
					function(totas, isError) {
		        		if (!isError) {
							return;
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
		
});