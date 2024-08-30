/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM121_MODIFYController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, $rootScope) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM121_MODIFYController";
/**==================================================================初始化=====================================================**/
		
		var pageID = $scope.CUS160PAGE; // 從哪前來CAM140
		
		//=== filter
		$scope.mappingSet['CAM.TASK_SOURCE'] = [];
		angular.forEach(projInfoService.mappingSet['CAM.TASK_SOURCE'], function(row){
			if ((row.DATA).substr(0, 1) == '0') {
				$scope.mappingSet['CAM.TASK_SOURCE'].push({LABEL: row.LABEL, DATA: row.DATA});
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
        if ($scope.custID != undefined) {
        	taskTitle = $scope.custID + " " + $scope.custName + " 另訂時間聯繫";
        }
       
		$scope.init = function(){
			if($scope.row)
				$scope.isUpdate = true;
//				$scope.set = $scope.set;
				
			$scope.inputVO = {
				seq:'',
				CUST_ID: '', 
				CUST_NAME: '', 
				TASK_SOURCE: '',
				TASK_STATUS:'',
				TASK_TITLE: '',
				TASK_MEMO: '', 
				TASK_DATE :undefined,
				sHour: '', 
				sMinute: '',
				eHour: '',
				eMinute: '',
				updateType:''
        	};
			if($scope.isUpdate){
				$scope.inputVO.seq = $scope.row.SEQ_NO;
				$scope.inputVO.CUST_ID = $scope.row.CUST_ID;
				$scope.inputVO.CUST_NAME = $scope.row.CUST_NAME;
				$scope.inputVO.TASK_SOURCE = $scope.row.TASK_SOURCE;
				$scope.inputVO.TASK_TITLE = $scope.row.TITLE;
				$scope.inputVO.TASK_MEMO = $scope.row.TASK_MEMO;
				if($scope.row.DATETIME) {
					$scope.inputVO.TASK_DATE = $scope.toJsDate($scope.row.DATETIME);
				}
				if($scope.row.STIME) {
					$scope.inputVO.sHour = $scope.row.STIME.substr(0,2);
					$scope.inputVO.sMinute = $scope.row.STIME.substr(2,4);
				}
				if($scope.row.ETIME) {
					$scope.inputVO.eHour = $scope.row.ETIME.substr(0,2);
					$scope.inputVO.eMinute = $scope.row.ETIME.substr(2,4);
				}
				$scope.inputVO.updateType = $scope.updateType;
				$scope.inputVO.TASK_STATUS = $scope.row.STATUS;
			}
		};
		$scope.init();
		
		// 2017/7/14 copy from cus160
		$scope.queryCustData = function() {
			if($scope.inputVO.CUST_ID) {
				$scope.inputVO.CUST_ID = $scope.inputVO.CUST_ID.toUpperCase();
				$scope.sendRecv("CUS160", "queryCustData", "com.systex.jbranch.app.server.fps.cus160.CUS160InputVO", {'custID': $scope.inputVO.CUST_ID},
						function(tota, isError) {
	    					if (!isError) {
	    						if(tota[0].body.resultList.length == 0) {
									$scope.showMsg("查無此客戶資料");
									$scope.inputVO.CUST_ID = '';
	              	                return;
								} else
									$scope.inputVO.CUST_NAME = tota[0].body.resultList[0].CUST_NAME;
	                    	}
						}
				);
			}
		};
		//
		
		$scope.save = function (){

        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("CRM121", "update", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
	
						if(totas.length > 0) {
							$scope.showSuccessMsg('ehl_01_common_025');
							
							if($scope.inputVO.updateType == 'B'){
								$rootScope.IamInitFunction();//#0001958:由待辦行事曆打開的任何是窗關閉後做行事曆頁面的重新整理
							}
							
							$scope.closeThisDialog('successful');
						}else{
							$scope.showMsg("ehl_01_common_007");
						}
			})
		
		};
		/**輔銷行事曆會議記錄 輔銷行事曆版型與理專行事曆相同，不需要再有刪除按鈕**/
//		$scope.CRM121_del = function(){ 			
//        	$confirm({text:  '是否確定要刪除?'}, {size: '200px'}).then(function() {	
//	        	$scope.sendRecv("CRM121", "delTodo", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", $scope.inputVO,
//	     				function(tota, isError) {
//	        				if (isError) {
//	     						$scope.showErrorMsg(tota[0].body.msgData);
//	     					}   
//	        				
//	     					if(tota.length > 0) {
//	     						$scope.showSuccessMsg('ehl_01_common_003');
//	     					}else{
//	     						$scope.showErrorMsg("ehl_01_common_005");
//	     					}
//	     					$rootScope.IamInitFunction();//#0001958:由待辦行事曆打開的任何是窗關閉後做行事曆頁面的重新整理
//	     					$scope.closeThisDialog('successful');
//	     			 	});
//        	});
//			
//		};
});
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		