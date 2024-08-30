/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM251Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM251Controller";
		
		// combobox
		$scope.sendRecv("CRM251", "getGroups", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", {},
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
                		$scope.mappingSet['Groups'] = [];
                		angular.forEach(totas[0].body.resultList, function(row, index, objs){
            				$scope.mappingSet['Groups'].push({LABEL: row.GROUP_NAME, DATA: row.GROUP_ID});
            			});
                	};
				}
		);
		
		$scope.ao_code = projInfoService.getAoCode();
		$scope.sendRecv("CRM211", "getAOCode", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", {},
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
						$scope.aolist = totas[0].body.resultList;
						$scope.AOCodes = [];
						if ($scope.ao_code != '' && $scope.ao_code != undefined) {		//有AO_CODE
	            			if($scope.ao_code.length > 1){		//有兩個以上AO_CODE的理專
	            				angular.forEach($scope.aolist, function(row, index, objs){
	            					angular.forEach($scope.ao_code, function(row2, index2, objs2){
	            						if(row.AO_CODE == row2){
	            							$scope.AOCodes.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	            						}
	            					});
	                			});
	            			}else if($scope.ao_code.length == 1){		//只有一個AO_CODE的理專
	                    		angular.forEach($scope.aolist, function(row, index, objs){
	                    			$scope.AOCodes.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	                			});
	            			}
	        			}else{		//無AO_CODE
	        				angular.forEach($scope.aolist, function(row, index, objs){
	        					$scope.AOCodes.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	        				});
	        			}
                	};
				}
		);
		
		$scope.init = function(){
			$scope.inputVO = {};
			
			$scope.pri = String(sysInfoService.getPriID());
			
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            if (projInfoService.getAoCode()[0] != "") {
            	$scope.inputVO.ao_code = projInfoService.getAoCode()[0];
    		}
            
            /*
			 * 取得UHRM人員清單(由員工檔+角色檔)
			 */
			$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
					function(tota, isError) {
						if (isError) {
							return;
						}
						if (tota.length > 0) {
							$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
							$scope.inputVO.uEmpID = tota[0].body.uEmpID;
						}
			});
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
			
		}
		$scope.inquireInit();
		
		$scope.inquire = function(){
			$scope.sendRecv("CRM251", "inquire", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
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
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CRM251", "deleteGroup", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", {'group_id': row.GROUP_ID},
                				function(totas, isError) {
                                	if (isError) {
                                		$scope.showErrorMsg(totas[0].body.msgData);
                                	}
                                	if (totas.length > 0) {
                                		$scope.showSuccessMsg('刪除成功');
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
				template: 'assets/txn/CRM251/CRM251_EDIT.html',
				className: 'CRM251_EDIT',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM251/CRM251_DETAIL.html',
				className: 'CRM251_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
				}
			});
		};
		
		$scope.crm211 = function(row) {
			// 原路徑
        	var path = $rootScope.menuItemInfo.txnPath;
			$scope.connector('set', "CRM251_ROW", row);
			$scope.connector('set', "CRM251_PAGE", "CRM251");
			path.push({'MENU_ID':"CRM211",'MENU_NAME':"我的客戶查詢"});
    		$rootScope.GeneratePage({'txnName':"CRM211",'txnId':"CRM211",'txnPath':path});
		};
		
		
		
});
