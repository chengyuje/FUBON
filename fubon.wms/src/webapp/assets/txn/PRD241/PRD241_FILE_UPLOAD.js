/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD241_FILE_UPLOADController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD241_FILE_UPLOADController";
		
		// combobox
		var vo = {'param_type': 'COMMON.YES_NO', 'desc': false};
        if(!projInfoService.mappingSet['COMMON.YES_NO']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['COMMON.YES_NO'] = totas[0].body.result;
        			$scope.mappingSet['COMMON.YES_NO'] = projInfoService.mappingSet['COMMON.YES_NO'];
        		}
        	});
        } else
        	$scope.mappingSet['COMMON.YES_NO'] = projInfoService.mappingSet['COMMON.YES_NO'];
		//
		
		// init
        $scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.ptype = "STOCK";
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
		
		// inquire
		$scope.inquire = function() {
			// toUpperCase
			if($scope.inputVO.prd_id)
				$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
			$scope.sendRecv("PRD241", "upload_inquire", "com.systex.jbranch.app.server.fps.prd241.PRD241InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.paramList, function(row, index, objs){
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							return;
						}
			});
		};
		
		$scope.download = function(row) {
        	$scope.sendRecv("CUS130", "download", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'fileID': row.DOC_ID,'fileName': row.FILE_NAME},
					function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
			);
        };
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("PRD220", "deleteData", "com.systex.jbranch.app.server.fps.prd220.PRD220InputVO", {'prd_id': row.PRD_ID,'ptype': $scope.inputVO.ptype,'doc_id': row.DOC_ID,'shared': row.SHARED},
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg("ehl_01_common_003");
                                		$scope.inquireInit();
                                		$scope.inquire();
                                	};
                				}
                		);
					});
				} else
					$scope.edit(row);
				row.cmbAction = "";
			}
		};
		
		$scope.edit = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD220/PRD220_EDIT.html',
				className: 'PRD220',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.ptype = 'STOCK';
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
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