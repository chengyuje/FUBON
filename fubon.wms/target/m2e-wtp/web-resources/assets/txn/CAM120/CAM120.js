/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM120Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM120Controller";
		
		// combobox
		getParameter.XML(["CAM.LEAD_SOURCE", "CAM.LEAD_TYPE", "CAM.LEAD_PARA1", "CAM.CHANNEL_CODE"], function(totas) {
			if (totas) {
				$scope.LEAD_SOURCE = totas.data[totas.key.indexOf('CAM.LEAD_SOURCE')];
				$scope.LEAD_TYPE = totas.data[totas.key.indexOf('CAM.LEAD_TYPE')];
				$scope.LEAD_PARA1 = totas.data[totas.key.indexOf('CAM.LEAD_PARA1')];
				$scope.CHANNEL_CODE = totas.data[totas.key.indexOf('CAM.CHANNEL_CODE')];
			}
		});
		
		$scope.init = function(){
			$scope.inputVO = {
        			camId: '',
        			camName: ''
        	};
		};
        $scope.init();
		
        // 初始分頁資訊
        $scope.inquireInit = function(){
			$scope.paramList = [];
		}
//		$scope.inquireInit();
        
        $scope.inquire = function(){
        	console.log('inquire');
			$scope.sendRecv("CAM120", "queryData", "com.systex.jbranch.app.server.fps.cam120.CAM120InputVO", $scope.inputVO,
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
		$scope.inquire();
		
		$scope.action = function(row) {
			if(row.cmbAction) {
				if(row.cmbAction == "D") {
					$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
						$scope.sendRecv("CAM120", "ddlModify", "com.systex.jbranch.app.server.fps.cam120.CAM120InputVO", {'id': row.SFA_PARA_ID},
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
        	// 原路徑
        	var path = $rootScope.menuItemInfo.txnPath;
        	$scope.connector('set','CAM140PAGE', "CAM120");
        	if(row){
        		$scope.connector('set','CAM140', "update");
        		$scope.connector('set','CAM140EDIT', row.SFA_PARA_ID);
        		$scope.connector('set','IMP_SEQNO', undefined);
        		path.push({'MENU_ID':"CAM140",'MENU_NAME':"編輯名單參數"});
        		$rootScope.GeneratePage({'txnName':"CAM140",'txnId':"CAM140",'txnPath':path});
        	} else {
        		$scope.connector('set','CAM140', "insert");
        		path.push({'MENU_ID':"CAM140",'MENU_NAME':"新增名單參數"});
        		$rootScope.GeneratePage({'txnName':"CAM140",'txnId':"CAM140",'txnPath':path});
        	}
		};
		
});
