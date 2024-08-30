/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD233Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD233Controller";
		
		// combobox
		getParameter.XML('COMMON.YES_NO', function(totas) {
			if (totas) {
				$scope.mappingSet['COMMON.YES_NO'] = totas.data[0];
			}
		});
		//
		
		// init
		$scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.type = 1;
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.totalList = [];
		}
		$scope.inquireInit();
		
		// date picker
		$scope.sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		};
		// date picker end
		
		// inquire
		$scope.inquire = function() {
			// toUpperCase
			if($scope.inputVO.prd_id)
				$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
			$scope.sendRecv("PRD233", "inquire", "com.systex.jbranch.app.server.fps.prd233.PRD233InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.totalList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.totalList, function(row, index, objs) {
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
						$scope.sendRecv("PRD220", "deleteData", "com.systex.jbranch.app.server.fps.prd220.PRD220InputVO", {'prd_id': row.PRD_ID,'ptype': 'MFD','doc_id': row.DOC_ID,'shared': row.SHARED},
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
                	$scope.ptype = 'MFD';
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
        
		$scope.upload = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD233/PRD233_UPLOAD.html',
				className: 'PRD233',
				showClose: false,
                controller: ['$scope', function($scope) {
                	
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