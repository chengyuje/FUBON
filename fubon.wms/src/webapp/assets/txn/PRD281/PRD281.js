/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD281Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD281Controller";

		// combobox
		getParameter.XML(["PRD.FCI_CURRENCY"], function(totas) {
			if (totas) {
				$scope.FCI_CURRENCY = totas.data[totas.key.indexOf('PRD.FCI_CURRENCY')];
			}
		});
		
		//時間--小時
		$scope.genHour = function() {
			$scope.mappingSet['hour'] = [];
			for (var i = 8; i <= 20; i++) {
				if (i < 10) {
					$scope.mappingSet['hour'].push({LABEL: "0" + i, DATA: "0" + i});
				} else {
					$scope.mappingSet['hour'].push({LABEL: i, DATA: i});
				}
			}
        };
        $scope.genHour();
        
        //時間--分鐘
        $scope.genMinute = function() {
			$scope.mappingSet['minute'] = [];
			$scope.mappingSet['minute'].push({LABEL: "00", DATA: "00"});
			$scope.mappingSet['minute'].push({LABEL: "15", DATA: "15"});
			$scope.mappingSet['minute'].push({LABEL: "30", DATA: "30"});
			$scope.mappingSet['minute'].push({LABEL: "45", DATA: "45"});
        };
        $scope.genMinute();
        
        //取得報價說明文字&交易時間起迄
		$scope.inquireParam = function() {
			$scope.paramVO = {};
			
			$scope.sendRecv("PRD281", "inquireParam", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", {},
					function(tota, isError) {
						if (!isError) {
							debugger
							$scope.paramVO = tota[0].body;
						}
				});
		}
		
		//Initialization
		$scope.init = function() {
			$scope.inputVO = {};
			
			//報價說明文字&交易時間起迄
			$scope.inquireParam();			
		};
		$scope.init();
		
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
				
		$scope.inquire = function(){
			// toUpperCase
			if($scope.inputVO.CURR_ID) $scope.inputVO.CURR_ID = $scope.inputVO.CURR_ID.toUpperCase();
			
			$scope.sendRecv("PRD281", "inquire", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", {CURR_ID: $scope.inputVO.CURR_ID},
					function(tota, isError) {
						if (!isError) {
							debugger
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							
							angular.forEach($scope.resultList, function(row) {
								row.func = [];
								if(row.EFFECTIVE_YN == "Y") {
									row.func.push({LABEL: "預覽", DATA: "3"});
								} else {
									row.func.push({LABEL: "修改", DATA: "1"});
									row.func.push({LABEL: "刪除", DATA: "2"});
									row.func.push({LABEL: "預覽", DATA: "3"});
								}
							});
						}
				});
		};		
		
		//儲存說明與交易時間起訖
		$scope.saveParams = function() {
			$scope.paramVO.saveType = "3";
			$scope.sendRecv("PRD281", "save", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", $scope.paramVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('ehl_01_common_004');
						$scope.inquireParam();
					}
			});
		}
		
		//編輯明細資料
        $scope.addProd = function() {
        	var dialog = ngDialog.open({
				template: 'assets/txn/PRD281/PRD281_EDIT.html',
				className: 'PRD281_EDIT',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = {};
					$scope.row.SEQ_NO = undefined;
				}]
			});
			dialog.closePromise.then(function(data) {
				$scope.inquire();
			});
        }
        
        //明細資料：檢視或修改
		$scope.doFunc = function(row) {
			if(row.acttype != "1" && row.acttype != "2" && row.acttype != "3")
				return;
			if(row.acttype == "2") {
				//刪除
				$confirm({text: '您確定刪除此筆記錄？'}, {size: 'sm'}).then(function () {
					var param = {};
					param.saveType = "2";
					param.SEQ_NO = row.SEQ_NO;
                    $scope.sendRecv("PRD281", "save", "com.systex.jbranch.app.server.fps.prd281.PRD281InputVO", param,
                    	function (totas, isError) {
                            if (isError) {
                                $scope.showErrorMsg(totas[0].body.msgData);
                            }
                            if (totas.length > 0) {
                                $scope.showSuccessMsg('ehl_01_common_003'); // 刪除成功
                                $scope.inquireInit();
                                $scope.inquire();
                            }
                    });
                });
			} else if(row.acttype == "1") {
				//修改
				var dialog = ngDialog.open({
					template: 'assets/txn/PRD281/PRD281_EDIT.html',
					className: 'PRD281_EDIT',
					showClose: false,
					controller: ['$scope', function($scope) {
						$scope.row = row;
					}]
				});
				dialog.closePromise.then(function(data) {
					$scope.inquire();
				});
			} else if(row.acttype == "3") {
				//預覽
				var dialog = ngDialog.open({
					template: 'assets/txn/PRD281/PRD281_PREVIEW.html',
					className: 'PRD281_PREVIEW',
					showClose: false,
					controller: ['$scope', function($scope) {
						$scope.row = row;
					}]
				});
				dialog.closePromise.then(function(data) {
					$scope.inquire();
				});
			}
		}
		
		$scope.openUpload = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD281/PRD281_UPLOAD.html',
				className: 'PRD281_UPLOAD',
				showClose: false,
				controller: ['$scope', function($scope) {
				}]
			});
			dialog.closePromise.then(function(data) {
//				$scope.inquire();
			});
		}
		
});