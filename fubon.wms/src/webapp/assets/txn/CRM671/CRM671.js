/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM671Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM671Controller";
		
		$scope.init = function() {
			$scope.inputVO.pri_id = '';
			$scope.inputVO.cust_id = '';
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
			$scope.inputVO.pri_id = String(sysInfoService.getPriID());	
		}
		$scope.init();
		
		//xml
		 getParameter.XML(['CAM.VST_REC_CMU_TYPE'], function(totas) {
				if(len(totas)>0) {
					$scope.mappingSet['CAM.VST_REC_CMU_TYPE'] = totas.data[totas.key.indexOf('CAM.VST_REC_CMU_TYPE')];
				}
				 
			});
		
		//初始查詢-近期互動
		$scope.inquire = function(){
		$scope.sendRecv("CRM671", "inquire", "com.systex.jbranch.app.server.fps.crm671.CRM671InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
						return;
					}
					if(tota[0].body.resultList2 != null && tota[0].body.resultList2.length > 0) {
						
						$scope.resultList2 = tota[0].body.resultList2;
					}
				}
		)};
		$scope.inquire();
		
		//初始查詢-交辦事項
		$scope.inquire2 = function(){
    		$scope.sendRecv("CRM671", "inquire2", "com.systex.jbranch.app.server.fps.crm671.CRM671InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
						return;
					}
					if(tota[0].body.resultList3 != null && tota[0].body.resultList3.length > 0) {
						
						$scope.resultList3 = tota[0].body.resultList3;
						$scope.outputVO3 = tota[0].body;
					}
				});
		};
		$scope.inquire2();
		
		$scope.detail = function () {
//        	$scope.connector('set','CRM671ID', $scope.inputVO.cust_id);
        	$scope.connector('set','CRM610_tab', 2);
        	var path = "assets/txn/CRM610/CRM610_DETAIL.html";
			$scope.connector("set","CRM610URL",path);
        	$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
//			$rootScope.menuItemInfo.url = "assets/txn/CRM610/CRM610.html";
    		
        };
		

});
		