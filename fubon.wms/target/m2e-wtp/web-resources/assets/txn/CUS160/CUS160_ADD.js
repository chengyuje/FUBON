/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CUS160_ADDController',
	function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS160_ADDController";
		
		var pageID = $scope.CUS160PAGE; // 從哪前來CAM140
		
		//=== 提醒類別filter
		getParameter.XML(["CAM.TASK_SOURCE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CAM.TASK_SOURCE'] = [];
				angular.forEach(totas.data[totas.key.indexOf('CAM.TASK_SOURCE')], function(row){
	    			if ((row.DATA).substr(0, 1) == '0') {
	    				$scope.mappingSet['CAM.TASK_SOURCE'].push({LABEL: row.LABEL, DATA: row.DATA});
	    			}
	    		});	
			}
		});
        //===
	
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
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
        
        $scope.genMinute = function() {
			$scope.mappingSet['minute'] = [];
			$scope.mappingSet['minute'].push({LABEL: "00", DATA: "00"});
			$scope.mappingSet['minute'].push({LABEL: "30", DATA: "30"});
        };
        $scope.genMinute();
        
		var taskMemo = "";
		if ($scope.campNameList != undefined) {
			for (var i = 0; i < $scope.campNameList.length; i++) {
				taskMemo = taskMemo + "名單" + (i + 1) +"：" + $scope.campNameList[i] + "-" + $scope.campDescList[i] + " ";
			}
		}
		
        var taskTitle = "";
        var empID = "";
        if ($scope.custID != undefined) {
        	taskTitle = $scope.custID + " " + $scope.custName + " 另訂時間聯繫";
        	empID = (sysInfoService.getUserID()).trim();
        }
        
        if ($scope.connector('get', "CRM122_EMPID") != undefined) {
        	empID = $scope.connector('get', "CRM122_EMPID");
        }
        
		$scope.init = function(){
			$scope.inputVO = {
				custID: $scope.custID, 
				custName: $scope.custName, 
				sDate: $scope.date,
				taskSource: '',
				taskTitle: taskTitle,
				taskMemo: taskMemo, 
				sHour: '', 
				sMinute: '',
				eHour: '',
				eMinute: '', 
				empID: empID
        	};
		};
		$scope.init();
		
		$scope.save = function() {
			if($scope.inputVO.sHour > $scope.inputVO.eHour) {
				$scope.showErrorMsg('欄位檢核錯誤:時間起迄有誤');
        		return;
			}
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			
			$scope.sendRecv("CUS160", "save", "com.systex.jbranch.app.server.fps.cus160.CUS160InputVO", $scope.inputVO,
					function(totas, isError) {
    					if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.showSuccessMsg("ehl_01_common_001");
                    		$scope.closeThisDialog('successful');
                    	};
					}
			);
		}
		
		$scope.queryCustData = function() {
			$scope.inputVO.custID = $scope.inputVO.custID.toUpperCase();
			if($scope.inputVO.custID != null){
				$scope.sendRecv("CUS160", "queryCustData", "com.systex.jbranch.app.server.fps.cus160.CUS160InputVO", $scope.inputVO,
						function(tota, isError) {
	    					if (!isError) {
	    						if(tota[0].body.resultList.length == 0) {
									$scope.showMsg("查無此客戶資料");
									$scope.inputVO.custID = '';
	              	                return;
								} else {	                	
	        		                $scope.inputVO.custName = tota[0].body.resultList[0].CUST_NAME;					 
								}
	                    	}
						}
				);
			}
		}
		
});
