/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS349_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS349_DETAILController";
		
		$scope.s='';
		$scope.csvList2=[];
		// filter
		var vo = {'param_type': 'CRM.CON_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.CON_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.CON_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.CON_DEGREE'] = projInfoService.mappingSet['CRM.CON_DEGREE'];
        		}
        	});
        } else
        	$scope.mappingSet['CRM.CON_DEGREE'] = projInfoService.mappingSet['CRM.CON_DEGREE'];
		
        $scope.inputVO={
				LOSS_CAT:	$scope.row.set,
				EMP_ID:$scope.row.EMP_ID,
				AO_CODE:$scope.row.AO_CODE,
				DATA_DATE:$scope.row.DATA_DATE,
				cust_id:$scope.row.CUST_ID
		}
		
		$scope.export=function(){
			$scope.sendRecv("PMS349", "detailexport",
					"com.systex.jbranch.app.server.fps.pms349.PMS349OutputVO",
					{'list':$scope.csvList2}, function(tota, isError) {
							if (!isError) {
//								$scope.paramList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
								return;
							}
					});	
			
			
		}
		
		$scope.inquire = function(){
			$scope.sendRecv("PMS349", "getDetail", "com.systex.jbranch.app.server.fps.pms349.PMS349InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
//							$scope.pagingList($scope.paramList, tota[0].body.resultList);
							$scope.paramList=tota[0].body.resultList;
							$scope.csvList2=tota[0].body.csvList;
					//		alert(JSON.stringify($scope.mappingSet['CRM.CON_DEGREE']))
							angular.forEach($scope.csvList2, function(row, index, objs){
								row.ROWNUM=index+1;
								row.IDS=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10);    //隱藏身分證 四碼
								
								angular.forEach($scope.mappingSet['CRM.CON_DEGREE'], function(row1, index, objs){
					//				console.log(row.DATA);
									if(row1.DATA==row.CON_DEGREE)
									 row.CON_DEGREE=row1.LABEL;
									
								});	
							
							});	
							
							angular.forEach($scope.paramList, function(row, index, objs){
								row.ROWNUM=index+1;
								row.IDS=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10);    //隱藏身分證 四碼
							});	
							
							
							
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		$scope.inquire();
		// 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        };
        $scope.inquireInit();
        $scope.aa=function(){
        	window.close();	
        }
});



