/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM621_detailController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM621_detailController";

		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
   
        
        
        // VALUE_TYPE
		$scope.mappingSet['VALID_TYPE'] = [];
		$scope.mappingSet['VALID_TYPE'].push({LABEL : '永久有效',DATA : 'F'},{LABEL : '截至有效',DATA : 'D'},{LABEL : '區間有效',DATA : 'B'});
        
        
        
		// 初始化
		$scope.init = function(){
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
			$scope.inputVO.bra_nbr = $scope.custVO.CUST_BRANCH;

			$scope.TEST = '8';
			$scope.valid_bgn_DateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
					};
			$scope.valid_end_DateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
					};
		}
		$scope.init();
		
		// 輸出欄初始化
		$scope.inquireInit = function(){
// $scope.resultList = [];
			$scope.data_B = [];
			$scope.data_C = [];
			$scope.data_D = [];
			$scope.resultList_A = [];
			$scope.outputVO_A = {};
			$scope.resultList_B = [];
			$scope.resultList_C = []; // 通訊電話
			$scope.outputVO_C = {};
			$scope.resultList_D = [];
			$scope.outputVO_D = {};
		}
		$scope.inquireInit();
		
		// date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		}; 
		
		$scope.limitDate = function() {
			$scope.valid_bgn_DateOptions.maxDate = $scope.inputVO.valid_end_Date || $scope.maxDate;
			$scope.valid_end_DateOptions.minDate = $scope.inputVO.valid_bgn_Date || $scope.minDate;
		};
		
		$scope.check = function() {
// if ($scope.inputVO.valid_type == 'B') {
// $scope.showdateB = true;
// $scope.showdateD = true;
// }
// else if ($scope.inputVO.valid_type == 'D') {
// $scope.showdateB = true;
// $scope.showdateD = false;
// }
// else {
// $scope.showdateB = false;
// $scope.showdateD = false;
// }
		}
		
		
		// A查詢 特殊聯絡方式
		$scope.inquire_A = function() {
			$scope.inputVO.detail_YN = 'Y';
			$scope.sendRecv("CRM621", "inquire_A", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.resultList_A = tota[0].body.resultList_A;
						$scope.outputVO_A = tota[0].body;
					}
			});
		};
		$scope.inquire_A();
		
		// A新增 特殊聯絡方式
		$scope.add = function(){
        	$scope.sendRecv("CRM621", "add", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO", $scope.inputVO,
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('ehl_01_common_001');
	                	$scope.inputVO.valid_type = null;
	                	$scope.inputVO.content = '';
	                	$scope.inquire_A();
	                };
	            }
        	);
        };
		

		// A刪除資料 特殊聯絡方式
		$scope.delete = function(row) {
			$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
				$scope.inputVO.sp_contact_id = row.SP_CONTACT_ID;
				$scope.sendRecv("CRM621", "delete", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO", $scope.inputVO,
                	function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.showSuccessMsg('刪除成功');
                    		$scope.resultList_A = [];
                    		$scope.inquire_A();
                    	};
                	}
                );
			});
		};
		
	
				
				$scope.sendRecv("CRM621", "inquire_message", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
						}
						if(tota[0].body !=null ) {						
							$scope.resultList_B = tota[0].body.addrList;
							$scope.resultList_D = tota[0].body.mailList;
							$scope.outputVO_B = tota[0].body;
							$scope.outputVO_D = tota[0].body;	
							
				
						}
		    	});
		
		
		
		$scope.mappingSet['PHN_TYPE'] = [{LABEL: "一般", DATA: "1"},
										{LABEL: "行動", DATA: "2"},
										{LABEL: "BB CALL", DATA: "3"}
										];
		
		$scope.mappingSet['YN'] = [{LABEL : "是" , DATA: "Y" },{LABEL : "否" , DATA: "N" }];
		
});
		