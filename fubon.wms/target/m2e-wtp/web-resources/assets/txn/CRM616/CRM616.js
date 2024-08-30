/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM616Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM616Controller";

		//DATE OPTION
//		var today=new Date();
//		var currentDateTime = today.getFullYear();
		
		$scope.getData_year = function() {
//			$scope.mappingSet['data_year'] = [{LABEL: currentDateTime + "年", DATA: currentDateTime},
//			                                  {LABEL: currentDateTime-1 + "年" , DATA: currentDateTime-1},
//			                                  {LABEL: currentDateTime-2 + "年" , DATA: currentDateTime-2}
//			                                  ];
			
			//顯示近三年"有資料"的資料年度(#4138)
			$scope.sendRecv("CRM616", "getDataYear", "com.systex.jbranch.app.server.fps.crm616.CRM616InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.mappingSet['data_year'] = [];
							angular.forEach(tota[0].body.resultList, function(row){
								$scope.mappingSet['data_year'].push({LABEL: row.DATA_YEAR, DATA: row.DATA_YEAR});
							});
						}
			});
        };
//      $scope.getData_year();
        
		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		//vip_degree
        var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        		}
        	});
        } else {
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        }
		
		//初始化
		$scope.init = function(){
//			$scope.inputVO.cust_id =  $scope.connector('get','CRM110_CUST_ID');
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
			$scope.getData_year();
		};
		$scope.init();
		
		//初始化
		$scope.inquireInit = function(){
			$scope.resultList = [];
			$scope.resultList2 = [];
		}
		$scope.inquireInit();
		
		//初始查詢
		$scope.initial = function(){
    	
		$scope.sendRecv("CRM616", "initial", "com.systex.jbranch.app.server.fps.crm616.CRM616InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					}
					if(tota[0].body.resultList != null && tota[0].body.resultList.length >0) {
						
						$scope.resultList = tota[0].body.resultList;
					}
				}
		)};
		$scope.initial();
		
		//查詢
		$scope.inquire = function(){
    	
		$scope.sendRecv("CRM616", "inquire", "com.systex.jbranch.app.server.fps.crm616.CRM616InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					}
					if(tota[0].body.resultList2 && tota[0].body.resultList2.length == 0) {
						$scope.showMsg("ehl_01_common_009");
                		return;
					}
					$scope.resultList2 = tota[0].body.resultList2;
				}
		)};
		
});
		