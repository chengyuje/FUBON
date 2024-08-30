/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS419Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS419Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		getParameter.XML(['CRM.TR_TYPE'], function(tota) {
			if(tota){
				$scope.mappingSet['CRM.TR_TYPE'] = tota.data[tota.key.indexOf('CRM.TR_TYPE')];
			}
		});
		
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableEmpCombo = false;
			var today = new Date();
			var date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();
			$scope.inputVO.reportDate = date;
			$scope.RegionController_getORG($scope.inputVO);
		};
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.paramList1 = [];	
			$scope.paramList2 = [];	
			$scope.paramList3 = [];	
			$scope.paramList4 = [];	
			$scope.totalData1 = [];
			$scope.totalData2 = [];
			$scope.totalData3 = [];
			$scope.totalData4 = [];
			$scope.outputVO = {};
			$scope.outputVO2 = {};
			$scope.outputVO3 = {};
			$scope.outputVO4 = {};
		}
		$scope.inquireInit();	
		
		
		$scope.query = function(){
			
			$scope.sendRecv("PMS419", "queryData", "com.systex.jbranch.app.server.fps.pms419.PMS419InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList_1.length == 0 && tota[0].body.resultList_2.length == 0 && tota[0].body.resultList_3.length == 0 && tota[0].body.resultList_4.length == 0 ) {
								$scope.totalData1 = [];
								$scope.paramList1 = [];
								$scope.totalData2 = [];
								$scope.paramList2 = [];
								$scope.totalData3 = [];
								$scope.paramList3 = [];
								$scope.totalData4 = [];
								$scope.paramList4 = [];
								$scope.outputVO = {};
								$scope.outputVO2 = {};
								$scope.outputVO3 = {};
								$scope.outputVO4 = {};
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.totalData1 = tota[0].body.resultList_1;
							$scope.paramList1 = tota[0].body.resultList_1;	
							$scope.totalData2 = tota[0].body.resultList_2;
							$scope.paramList2 = tota[0].body.resultList_2;
							$scope.totalData3 = tota[0].body.resultList_3;
							$scope.paramList3 = tota[0].body.resultList_3;
							$scope.totalData4 = tota[0].body.resultList_4;
							$scope.paramList4 = tota[0].body.resultList_4;
							$scope.outputVO = tota[0].body;
							$scope.outputVO2 = angular.copy(tota[0].body);
							$scope.outputVO3 = angular.copy(tota[0].body);
							$scope.outputVO4 = angular.copy(tota[0].body);
							return;
						}						
			});
		};
		
		// 連至客戶首頁
        $scope.custDTL = function(row) {
        	$scope.custVO = {
    				CUST_ID   : row.PERSON_ID,
    				CUST_NAME : row.CUST_NAME	
    		}
    		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
        	
        	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			var set = $scope.connector("set","CRM610URL",path);
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
		};
        

});