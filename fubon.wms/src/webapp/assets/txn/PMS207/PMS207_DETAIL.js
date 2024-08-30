/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS207_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS207_DETAILController";

		$scope.finalDate = $scope.connector('get', 'PMS207_fnlDt_DETAIL');
		var srcYrMn = $scope.connector('get','PMS207_YRMN');
		var srcAoCode =	$scope.connector('get','PMS207_AOCODE');
		var srcEmpId = $scope.connector('get','PMS207_EMPID');
		var srcWkDt = $scope.connector('get','PMS207_WKDT');	
		var srcBrId = $scope.connector('get','PMS207_BRID');
				
		var srcType = $scope.connector('get','PMS207_TYPE_DETAIL');
		if(srcType == 'week')
			$scope.type = '週';
		else
			$scope.type = '日';
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};					
		
		$scope.init = function(){
			$scope.inputVO = {
//				dataMonth = '',
//				aoCode = '',
//				emp_id = '',
//				work_dt = ''
        	};				
		};
	
		$scope.init();
//		$scope.inquireInit = function(){
//			$scope.initLimit();
//			$scope.paramList = [];			
//		}
//		$scope.inquireInit();		
			
		
		$scope.query = function(){						
			$scope.sendRecv("PMS207", "queryDetail", "com.systex.jbranch.app.server.fps.pms207.PMS207InputVO", {'sttType':srcType, 'dataMonth':srcYrMn , 'emp_id':srcEmpId , 'work_dt':srcWkDt, 'ao_code':srcAoCode},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.dfCustList.length == 0 && tota[0].body.dfPrdList1.length == 0) {								
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}							
							if(tota[0].body.dfCustList.length != 0)
								$scope.paramList = tota[0].body.dfCustList;
							if(tota[0].body.dfPrdList1.length != 0){
								$scope.paramList1 = tota[0].body.dfPrdList1;
								$scope.paramList2 = tota[0].body.dfPrdList2;
								$scope.paramList3 = tota[0].body.dfPrdList3;								
							}
							$scope.outputVO = tota[0].body;														
							return;
						}						
			});			
		};
		$scope.query();
		
		$scope.backToMain = function(){
			$scope.connector('set', 'backFlag', '1');
			$scope.connector('set', 'mainType', srcType);
			$scope.connector('set', 'mainYrMn', srcYrMn);
			$scope.connector('set', 'mainEmpId', srcEmpId);
			$scope.connector('set', 'mainBrId', srcBrId);
			
			$scope.connector('set', 'mainaocode', srcAoCode);
			$rootScope.menuItemInfo.url = "assets/txn/PMS207/PMS207.html";
		};
		
		/** 產品類別代碼 --> 產品類別名稱 **/
		var vo = {'param_type': 'PMS.SALE_PLAN_PRD_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['PMS.SALE_PLAN_PRD_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['PMS.SALE_PLAN_PRD_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['PMS.SALE_PLAN_PRD_TYPE'] = projInfoService.mappingSet['PMS.SALE_PLAN_PRD_TYPE'];        		
        		}
        	});
        } else
        	$scope.mappingSet['PMS.SALE_PLAN_PRD_TYPE'] = projInfoService.mappingSet['PMS.SALE_PLAN_PRD_TYPE'];
			
});
