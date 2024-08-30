/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM121Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM121Controller";
		
		//===filter
        var vo = {'param_type': 'COMMON.YES_NO', 'desc': false};
        if(!projInfoService.mappingSet['COMMON.YES_NO']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['COMMON.YES_NO'] = totas[0].body.result;
        			$scope.mappingSet['COMMON.YES_NO'] = projInfoService.mappingSet['COMMON.YES_NO'];
        		}
        	});
        } else {
        	$scope.mappingSet['COMMON.YES_NO'] = projInfoService.mappingSet['COMMON.YES_NO'];
        }
        
        var vo = {'param_type': 'SYS.QUESTION_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['SYS.QUESTION_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['SYS.QUESTION_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['SYS.QUESTION_TYPE'] = projInfoService.mappingSet['SYS.QUESTION_TYPE'];
        		}
        	});
        } else {
        	$scope.mappingSet['SYS.QUESTION_TYPE'] = projInfoService.mappingSet['SYS.QUESTION_TYPE'];
        }
        //===
        
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.eDate || $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.sDate || $scope.minDate
		};
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.endDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		};
		
		$scope.init = function(){
			$scope.data = [];
			$scope.questionList = [];
			
			$scope.inputVO = {
					questionDesc: '',
					sDate: undefined,
					eDate: undefined
        	};

			$scope.limitDate();
		};
        $scope.init();
        
        // 初始分頁資訊
        $scope.inquireInit = function(){
			$scope.questionList = [];
		}
		$scope.inquireInit();
        
        $scope.inquire = function() {
        	$scope.sendRecv("CAM121", "query", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", $scope.inputVO,
        			function(tota, isError) {
						if (!isError) {
							if(tota[0].body.questionList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}
							$scope.questionList = tota[0].body.questionList;
							$scope.outputVO = tota[0].body;
							angular.forEach($scope.questionList, function(row, index, objs){
								row.set = [];
								row.set.push({LABEL: "修改", DATA: "U"});
								row.set.push({LABEL: "刪除", DATA: "D"});
							});
							return;
						}
					}
			);
        }
        
        $scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CAM121", "delQandA", "com.systex.jbranch.app.server.fps.cam121.CAM121InputVO", {questionVersion: row.QUESTION_VERSION},
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
        	if(row){
        		var dialog = ngDialog.open({
					template: 'assets/txn/CAM121/CAM121_MAINTAIN.html',
					className: 'CAM121_MAINTAIN',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.questionVersion = row.QUESTION_VERSION;
	                	$scope.actionType = 'update';
	                }]
				}).closePromise.then(function (data) {
					 $scope.inquire();
				});
        	} else {
        		var dialog = ngDialog.open({
					template: 'assets/txn/CAM121/CAM121_MAINTAIN.html',
					className: 'CAM121_MAINTAIN',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.actionType = 'insert';
	                }]
				}).closePromise.then(function (data) {
					 $scope.inquire();
				});
        	}
		};
});
