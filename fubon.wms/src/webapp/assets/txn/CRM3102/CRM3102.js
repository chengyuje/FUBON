/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3102Controller',
	function($rootScope, $scope, $controller, $confirm, ngDialog, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3102Controller";
		
		// combobox
		getParameter.XML(["CRM.TRS_PRJ_STATUS", "CRM.TRS_PRJ_TYPE", "CMMGR005.STATUS", "CMMGR005.RESULT"], function(totas) {
			if (totas) {
				$scope.TRS_PRJ_STATUS = totas.data[totas.key.indexOf('CRM.TRS_PRJ_STATUS')];
				$scope.TRS_PRJ_TYPE = totas.data[totas.key.indexOf('CRM.TRS_PRJ_TYPE')];
				$scope.mappingSet['CMMGR005.STATUS'] = totas.data[totas.key.indexOf('CMMGR005.STATUS')];
				$scope.mappingSet['CMMGR005.RESULT'] = totas.data[totas.key.indexOf('CMMGR005.RESULT')];
			}
		});
		
		$scope.getAllPRJ = function() {
			$scope.sendRecv("CRM3102", "getAllPRJ", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO",$scope.inputVO,
					function(tota, isError) {
					   if(!isError){
						   $scope.TRS_PROJNAME = [];
		                	angular.forEach(tota[0].body.allPRJ, function(row) {
		                		$scope.TRS_PROJNAME.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_ID});
		                	});
					   }
				});
		};
		$scope.getAllPRJ();
		//
		
		// date picker
		$scope.sDateOptions = {};
		$scope.eDateOptions = {};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.edate;
			$scope.eDateOptions.minDate = $scope.inputVO.sdate;
		};
		//
		
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.data = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inquireInit();
		};
		$scope.init();
		
		//查詢
		$scope.inquire = function() {
			$scope.sendRecv("CRM3102", "inquire", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO",$scope.inputVO,
					function(tota, isError) {
						if(!isError) {
							if (tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
							}
							else {
								$scope.resultList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
								angular.forEach($scope.resultList, function(row) {
									row.set = [];
									row.set.push({LABEL: "修改", DATA: "1"});
									row.set.push({LABEL: "查看匯入成功名單", DATA: "2"});
									row.set.push({LABEL: "查看匯入失敗名單", DATA: "3"});
									row.set.push({LABEL: "刪除整個專案", DATA: "4"});
									if(row.PRJ_TYPE == '2') {
										row.set.push({LABEL: "匯出客戶名單", DATA: "5"});
									}
									
								});
		                	}
					   }
				});
		};
		
		//新增專案名稱
		$scope.newPrj = function(prjId) {
			debugger
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM3102/CRM3102_UPLOAD.html',
				className: 'CRM3102_UPLOAD',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.PRJ_ID = prjId;
				}]
			});
			dialog.closePromise.then(function(data) {
				if(data.value === 'successful') {
					$scope.getAllPRJ();
					$scope.inquireInit();
					$scope.inquire();
	  			}
			});
		};
		
		//查詢結果:功能
		$scope.doFunc = function(row) {
			if(row.acttype) {
				//修改專案資料
				if (row.acttype == '1'){
					debugger
					$scope.newPrj(row.PRJ_ID);
				//查看移轉成功名單
				} else if (row.acttype == '2'){	  
					var dialog = ngDialog.open({
						template: 'assets/txn/CRM3102/CRM3102_SCSLST.html',
						className: 'CRM3102_SCSLST',
						showClose: false,
						controller: ['$scope', function($scope) {
							$scope.row = row;
							$scope.act = '2';
						}]
					});
				//查看移轉失敗名單
				} else if (row.acttype == '3'){   
					var dialog = ngDialog.open({
						template: 'assets/txn/CRM3102/CRM3102_ERRLST.html',
						className: 'CRM3201_ERRLST',
						showClose: false,
						controller: ['$scope', function($scope) {
							$scope.row = row;
							$scope.act = '3';
						}]
					});
				//刪除整個專案
				} else if(row.acttype == '4') {
					$confirm({text: '確定執行 : 刪除整個專案?', text1:'(上傳名單將一併刪除)'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CRM3102", "doFunction", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO", {'PRJ_ID': row.PRJ_ID, 'acttype': row.acttype},
							function(tota, isError) {
							   if(!isError) {
								   $scope.showMsg("刪除整個專案成功");
							   }

							   $scope.getAllPRJ();
							   $scope.inquireInit();
							   $scope.inquire();
						});
					});
				//換手；匯出客戶名單
				} else if(row.acttype == '5') {
					$scope.sendRecv("CRM3102", "doFunction", "com.systex.jbranch.app.server.fps.crm3102.CRM3102InputVO", {'PRJ_ID': row.PRJ_ID, 'acttype': '9', 'PRJ_NAME': row.PRJ_NAME},
						function(tota, isError) {
						   if(!isError) {
						   }
					});
				}
			}
		};
		
});