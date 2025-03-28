/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD235Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD235Controller";

		//參數
		$scope.getParameters = function() {
			getParameter.XML(["PRD.OVSPRI_RDM_KEYIN_ROLE", "PRD.OVSPRI_RDM_AUTH_ROLE", "PRD.OVSPRI_RDM_ADJ_STATUS"], function(totas) {
				if (totas) {
					$scope.mappingSet['PRD.OVSPRI_RDM_KEYIN_ROLE'] = totas.data[totas.key.indexOf('PRD.OVSPRI_RDM_KEYIN_ROLE')];
					$scope.mappingSet['PRD.OVSPRI_RDM_AUTH_ROLE'] = totas.data[totas.key.indexOf('PRD.OVSPRI_RDM_AUTH_ROLE')];
					$scope.mappingSet['PRD.OVSPRI_RDM_ADJ_STATUS'] = totas.data[totas.key.indexOf('PRD.OVSPRI_RDM_ADJ_STATUS')];
					debugger
					//權限ID//是否為PM角色
					$scope.privilegeId = sysInfoService.getPriID();
					var findPMRole = $filter('filter')($scope.mappingSet['PRD.OVSPRI_RDM_KEYIN_ROLE'], {DATA: $scope.privilegeId[0]});
					$scope.inputVO.isPMRole = (findPMRole != null && findPMRole != undefined && findPMRole.length > 0) ? true : false;
					//是否為PM主管角色
					var findPMBossRole = $filter('filter')($scope.mappingSet['PRD.OVSPRI_RDM_AUTH_ROLE'], {DATA: $scope.privilegeId[0]});
					$scope.inputVO.isPMBossRole = (findPMBossRole != null && findPMBossRole != undefined && findPMBossRole.length > 0) ? true : false;
				}
			});
		}
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.mappingSet['PRD_CATEGORY'] = [];
			$scope.mappingSet['PRD_CATEGORY'].push({LABEL: "申購", DATA: "1"});
			$scope.mappingSet['PRD_CATEGORY'].push({LABEL: "贖回", DATA: "2"});
			$scope.getParameters();
			
			$scope.inputVO.TRADE_TYPE = "1"; //申購
		};
		$scope.init();
		
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
				
		$scope.inquire = function(){
			// toUpperCase
			if($scope.inputVO.prd_id) $scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
			
			$scope.sendRecv("PRD235", "inquire", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", {PRD_ID: $scope.inputVO.PRD_ID, TRADE_TYPE: $scope.inputVO.TRADE_TYPE},
					function(tota, isError) {
						if (!isError) {
							debugger
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							
							angular.forEach($scope.resultList, function(row) {
								row.func = [];
								row.func.push({LABEL: "修改", DATA: "1"});
								row.func.push({LABEL: "刪除", DATA: "2"});
							});
						}
				});
		};
		
		//編輯明細資料
        $scope.addProd = function() {
        	var dialog = ngDialog.open({
				template: 'assets/txn/PRD235/PRD235_EDIT.html',
				className: 'PRD235_EDIT',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = {};
					$scope.row.PRD_ID = "";
				}]
			});
			dialog.closePromise.then(function(data) {
				$scope.inquire();
			});
        }
        
        //明細資料：檢視或修改
		$scope.doFunc = function(row) {
			if(row.acttype != "1" && row.acttype != "2")
				return;
			if(row.acttype == "2") {
				//刪除
				$confirm({text: '您確定刪除此筆記錄？'}, {size: 'sm'}).then(function () {
					var param = {};
					param.saveType = "2";
					param.SEQ_NO = row.SEQ_NO;
                    $scope.sendRecv("PRD235", "save", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", param,
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
			} else {
				//修改
				var dialog = ngDialog.open({
					template: 'assets/txn/PRD235/PRD235_EDIT.html',
					className: 'PRD235_EDIT',
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
		
		//費用率與報酬率資料設定
        $scope.feeSetup = function() {
        	var dialog = ngDialog.open({
				template: 'assets/txn/PRD235/PRD235_FEE.html',
				className: 'PRD235_FEE',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = {};
				}]
			});
			dialog.closePromise.then(function(data) {
//				$scope.inquire();
			});
        }
        
        //產出贖回總表
        $scope.printReport = function(row) {
        	var param = {};
			param.SEQ_NO = row.SEQ_NO;
        	$scope.sendRecv("PRD235", "printReport", "com.systex.jbranch.app.server.fps.prd235.PRD235InputVO", param,
                function (totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                    } else {
                    	$scope.inquire();
                    }
        	});
        }
        
        //編輯贖回新單位數
        $scope.editUnitNum = function(row) {
        	var isPMRole = $scope.inputVO.isPMRole;
        	var isPMBossRole = $scope.inputVO.isPMBossRole;
        	
        	var dialog = ngDialog.open({
				template: 'assets/txn/PRD235/PRD235_EDITUNIT.html',
				className: 'PRD235_EDITUNIT',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = row;
					$scope.isPMRole = isPMRole;
					$scope.isPMBossRole = isPMBossRole;
				}]
			});
			dialog.closePromise.then(function(data) {
//				$scope.inquire();
			});
        }

        //檢視贖回調整新單位數明細
        $scope.viewAdjDetails = function(row) {
	
        }
        
        //查詢條件:申購/贖回改變
        $scope.tradeTypeChanged = function() {
        	$scope.inquireInit();
        }
        
        $scope.dividendSetup = function() {
        	var dialog = ngDialog.open({
				template: 'assets/txn/PRD235/PRD235_DIVIDEND.html',
				className: 'PRD235_DIVIDEND',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.row = {};
				}]
			});
			dialog.closePromise.then(function(data) {
//				$scope.inquire();
			});
        }
});