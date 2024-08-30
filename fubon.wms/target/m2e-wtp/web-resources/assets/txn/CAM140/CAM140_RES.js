'use strict';
eSoafApp.controller('CAM140_RESController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM140_RESController";
		
		// combobox
		getParameter.XML(["CAM.RESPONSE_MEAN", "CAM.RESPONSE_MEAN_2"], function(totas) {
			if (totas) {
				$scope.RESPONSE_MEAN = totas.data[totas.key.indexOf('CAM.RESPONSE_MEAN')];
				$scope.RESPONSE_MEAN_2 = totas.data[totas.key.indexOf('CAM.RESPONSE_MEAN_2')];
			}
		});
		
        // edit data
		$scope.sendRecv("CAM140", "getRes", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {'camp_id': $scope.camp_id, 'responseCode': $scope.responseCode},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_001");
                			return;
                		}
						$scope.paramList = tota[0].body.resultList;
						angular.forEach($scope.paramList, function(row) {
							row.editDis = true;
							row.delDis = false;
							// temp
							row.tempENABLE = row.RESPONSE_ENABLE;
							row.tempMEAN = row.RESPONSE_MEAN;
							row.tempNAME = row.RESPONSE_NAME;
						});
						
						// 補到滿用
						var check = ['03A','03B','03C','03D','03E','03F','03G','03H','03I','03J','03K','03L','03N','03M','03O','03P','03Q','03R','03S','03T'];
						angular.forEach(check, function(row) {
							if($scope.paramList.map(function(e) { return e.LEAD_STATUS; }).indexOf(row) == -1) {
								if ($scope.paramList.map(function(e) { return e.CAMPAIGN_ID; }).indexOf("0000000001") == 0 ||
									$scope.paramList.map(function(e) { return e.CAMPAIGN_ID; }).indexOf("0000000002") == 0 ||
									$scope.paramList.map(function(e) { return e.CAMPAIGN_ID; }).indexOf("0000000003") == 0) {
									
								} else {
									$scope.paramList.push({'CAMPAIGN_ID': $scope.camp_id,
														   'editDis': true,
														   'delDis': true,
														   'LEAD_STATUS': row,
														   'tempENABLE': 'Y',
														   'RESPONSE_ENABLE': 'Y',
														   'tempMEAN': '',
														   'tempNAME': ''});
								}
							}
						});
					}
		});
		
		$scope.edit = function(row) {
        	row.editDis = false;
        };
		
		$scope.del = function(row) {
			$confirm({text: '請確定是否刪除此筆資料？'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("CAM140", "delRes", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {'camp_id': row.CAMPAIGN_ID, 'lead':row.LEAD_STATUS},
						function(tota, isError) {
							if (!isError) {
								$scope.showSuccessMsg('ehl_01_common_003');
								row.RESPONSE_ENABLE = 'Y';
								row.RESPONSE_MEAN = '';
								row.RESPONSE_NAME = '';
								row.tempENABLE = 'Y';
								row.tempMEAN = '';
								row.tempNAME = '';
					        	row.editDis = true;
					        	row.delDis = true;
							}
				});
			});
        }
		
		$scope.confirm = function(row) {
			$scope.sendRecv("CAM140", "updateRES", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {'camp_id':row.CAMPAIGN_ID, 
																											  'enable':row.tempENABLE,
																											  'lead':row.LEAD_STATUS,
																											  'mean':row.tempMEAN,
																											  'name':row.tempNAME},
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg('ehl_01_common_006');
				        	row.editDis = true;
				        	row.delDis = false;
						}
			});
        }
		
		$scope.cancel = function(row) {
			row.tempENABLE = row.RESPONSE_ENABLE;
			row.tempMEAN = row.RESPONSE_MEAN;
			row.tempNAME = row.RESPONSE_NAME;
        	row.editDis = true;
        }
		
		/*
		 * 計算此次是否有新增回應選項
		 * 
		 */
		$scope.closeAfertCountRow = function () {
			$scope.sendRecv("CAM140", "getResponseCountRow", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {'camp_id': $scope.camp_id},
					function(totas, isError) {
		                if (isError) {
		                	$scope.showErrorMsgInDialog(totas.body.msgData);
		                    return;
		                }
		                if (totas.length > 0) {
		                	$scope.closeThisDialog(totas[0].body.responseCounts);
		                }
		            }
			);
		}
		
		
});
