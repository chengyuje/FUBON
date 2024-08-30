/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM431Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM431Controller";
		
		$scope.init = function(){
			$scope.data = [];
			$scope.resultList = [];
			$scope.inputVO = {
				cust_id: '',
				auth_date_bgn: undefined,
				auth_date_end: undefined,
				apply_status: '1',
				statusList: []
			}
		};
		$scope.init();
		
		/** MAO151-start **/
	    //客戶ID
	    $scope.MAO151_PARAMS = $scope.connector('get', 'MAO151_PARAMS');
		$scope.connector('set', "MAO151_PARAMS", null);
		if($scope.MAO151_PARAMS) {
			$scope.inputVO.cust_id = $scope.MAO151_PARAMS.CUST_ID;
		}
		//申請日期
		$scope.MAO151_APPLIED_TIME = $scope.connector('get', 'MAO151_APPLIED_TIME');
		$scope.connector('set', "MAO151_APPLIED_TIME", null);
		if($scope.MAO151_APPLIED_TIME) {
			$scope.inputVO.auth_date_bgn = $scope.toJsDate($scope.MAO151_APPLIED_TIME);
			$scope.inputVO.auth_date_end = $scope.toJsDate($scope.MAO151_APPLIED_TIME);
		}
		/** MAO151-end **/
		 
		// filter
		getParameter.XML(["CRM.APPLY_STATUS", "CRM.SINGLE_TYPE", "CRM.AUTH_STATUS"], function(totas) {
			if (totas) {
				$scope.TEMP = totas.data[totas.key.indexOf('CRM.APPLY_STATUS')];
//				$scope.mappingSet['CRM.APPLY_STATUS'] = [];
//				angular.forEach($scope.TEMP, function(row) {
//					if(row.DATA == "1" || row.DATA == "4")
//						$scope.mappingSet['CRM.APPLY_STATUS'].push(row);
//				});
				$scope.mappingSet['CRM.APPLY_STATUS_ALL'] = $scope.TEMP;
				
				$scope.mappingSet['CRM.SINGLE_TYPE'] = totas.data[totas.key.indexOf('CRM.SINGLE_TYPE')];
				$scope.mappingSet['CRM.AUTH_STATUS'] = totas.data[totas.key.indexOf('CRM.AUTH_STATUS')];
			}
		});
		
	    //2017-8-31 by Jacky 修改為
		$scope.APPLY_STATUS = [];
		$scope.APPLY_STATUS.push({LABEL: '待覆核', DATA: '1'},
				               	 {LABEL: '已同意', DATA: '2'},
				               	 {LABEL: '已終止(期間議價)', DATA: '3'},
				               	 {LABEL: '已退回', DATA: '9'});
		
		$scope.mappingSet['CRM.APPLY_STATUS'] = [];
		$scope.mappingSet['CRM.APPLY_STATUS'].push({LABEL: '待覆核', DATA: '1'},
				                                   {LABEL: '已同意', DATA: '2'},
				                                   {LABEL: '已終止(期間議價)', DATA: '3'},
				                                   {LABEL: '終止待覆核', DATA: '4'},
				                                   {LABEL: '已退回', DATA: '9'});
		
    	$scope.mappingSet['CRM.APPLY.CAT'] =[];
    	$scope.mappingSet['CRM.APPLY.CAT'].push({LABEL: '期間議價', DATA: '1'},
    											{LABEL: '單次議價', DATA: '2'});
    	
    	$scope.inputVO.statusList = [];
		$scope.toggleSelection = function toggleSelection(data) {
			var idx = $scope.inputVO.statusList.indexOf(data);
	    	if (idx > -1) {
	    		$scope.inputVO.statusList.splice(idx, 1);
	    	} else {
	    		$scope.inputVO.statusList.push(data);
	    	}
        };
    	
    	$scope.query = function(){
    		$scope.data = [];
			$scope.resultList = [];
			$scope.outputVO = [];
			$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			
    		var ans = $scope.mappingSet['CRM.APPLY_STATUS'].filter(function(obj){
          		return (obj.SELECTED == true);
          	});
    		$scope.ans = ans;
//    		if($scope.ans.length!=0){
//	    		angular.forEach($scope.ans, function(row, index, objs){
//	    			//待覆核 : 1,4
//	    			if(row.DATA=='1'){
//	    				$scope.inputVO.statusList.push('4');	
//	    			}
//	    			$scope.inputVO.statusList.push(row.DATA);
//	    		})   		
//    		}
    		$scope.sendRecv("CRM431", "inquire", "com.systex.jbranch.app.server.fps.crm431.CRM431InputVO", $scope.inputVO,
 					function(totas, isError) {
 						if (isError) {
 							
 						}
 						if (totas[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						if (totas[0].body.resultList.length > 0) {
//							$scope.resultList = totas[0].body.resultList;
							$scope.resultList = _.sortBy(totas[0].body.resultList, ['APPLY_DATE']).reverse();
	                		$scope.outputVO = totas[0].body;
						}
 						
			});
    	}
    	
    	$scope.detail = function(row) {
    		var dialog = ngDialog.open({
    			template: 'assets/txn/CRM431/CRM431_DETAIL.html',
    			className: 'CRM431_DETAIL',
    			showClose: false, 
    			controller: ['$scope', function($scope) {
    				$scope.row = row;
    			}]
    		
    		});
    		dialog.closePromise.then(function(data) {
    			if(data.value === 'successful'){//新增時
    				$scope.init();
    				$scope.query();
    			}
    		});
    	};
    	
    	/*
    	 * 日期
    	 */
        $scope.sDateOptions = {
        		maxDate: $scope.maxDate,
        		minDate: $scope.minDate
        };

        $scope.eDateOptions = {
        		maxDate: $scope.maxDate,
        		minDate: $scope.minDate
        };

		$scope.model = {};
		
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.auth_date_end || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.auth_date_bgn || $scope.minDate;
		};
    	
});