/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM124_EDIT_ONSITEController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM124_EDIT_ONSITEController";
		
		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		//CHG_REASON
		$scope.mappingSet['CHG_REASON'] = [];
		$scope.mappingSet['CHG_REASON'].push(
											{LABEL: '教育訓練', DATA: '1'},
											{LABEL: '客戶陪訪', DATA: '2'},
											{LABEL: '業務檢討', DATA: '3'},
											{LABEL: '其他', DATA: '4'}
											);

		//NEW_ONSITE_PERIOD
		$scope.mappingSet['NEW_ONSITE_PERIOD'] = [];
		$scope.mappingSet['NEW_ONSITE_PERIOD'].push(
													{LABEL: '上午', DATA: '1'},
													{LABEL: '下午', DATA: '2'}
													);
		
		// init
		$scope.init = function(){
			$scope.period = '';
			if ($scope.row.ONSITE_PERIOD == '1') {
				$scope.period = '上午';
			} else {
				$scope.period = '下午';
			};
			$scope.on_date = $scope.row.ONSITE_DATE;
			$scope.onsite_brh = $filter('mapping')($scope.row.ONSITE_BRH, $scope.mappingSet['branchsDesc']);
           
            $scope.inputVO = {
            		seq: $scope.row.SEQ, 
            		emp: $scope.emp_id, 
            		onsite_brh: $scope.row.ONSITE_BRH,
            		on_date: $scope.toJsDate($scope.row.ONSITE_DATE),
            		onsite_period: $scope.row.ONSITE_PERIOD,
            		new_onsite_brh: '',
            		new_on_date: undefined,
            		new_onsite_period: '',
            		chg_reason: '',
            		chg_reason_oth: ''	
            };
        };
        $scope.init();        
        // date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		// date picker end		
        $scope.save = function() {
        	
        	if($scope.inputVO.new_onsite_brh == undefined || $scope.inputVO.new_onsite_brh == '' ||
        			$scope.inputVO.chg_reason == undefined || $scope.inputVO.chg_reason == '' ||
        			$scope.inputVO.new_on_date == undefined || $scope.inputVO.new_on_date == '' ||
        			$scope.inputVO.new_onsite_period == undefined || $scope.inputVO.new_onsite_period == ''){
    	    			$scope.showErrorMsg("ehl_01_common_022");		//欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
    	    			return;
    	    	}
        	
        	if($scope.inputVO.onsite_brh == $scope.inputVO.new_onsite_brh && $scope.inputVO.on_date == $scope.inputVO.new_on_date && $scope.inputVO.on_date == $scope.inputVO.new_on_date) {
    			$scope.closeThisDialog('successful');
    			return;
        	}	
    		$scope.sendRecv("CRM124", "edit_onsite", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
                    		$scope.closeThisDialog('successful');
	                	};
					}
			);
        };
});
