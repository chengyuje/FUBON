/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM372_REVIEWController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService ) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM372_REVIEWController";
		
		// combobox
		getParameter.XML(["CRM.PRJ_STATUS", "CRM.CHG_TYPE", "CRM.REVIEW_STATUS"], function(totas) {
			if (totas) {
				$scope.prjStatusList = totas.data[totas.key.indexOf('CRM.PRJ_STATUS')];
				$scope.chgTypeList = totas.data[totas.key.indexOf('CRM.CHG_TYPE')];
				$scope.reviewStatusList = totas.data[totas.key.indexOf('CRM.REVIEW_STATUS')];
				$scope.chgTypeList2= [{"DATA":"M","LABEL":"主要"},{"DATA":"R","LABEL":"參考"},{"DATA":"MR","LABEL":"主要"}];
			}
		});
				
		$scope.init = function() {
			$scope.inputVO = {
					prj_code:$scope.row.PRJ_CODE,
					prd_name:$scope.row.PRJ_NAME
			};
			
			//查詢AO_LIST
			$scope.sendRecv("CRM372", "inquireAOList", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
                		$scope.showErrorMsg(tota[0].body.msgData);
                	}
                	if (tota.length > 0) {
                		$scope.inputVO.csvDataList = tota[0].body.resultList;
	        			$scope.outputVO = tota[0].body;
	        			
//	        			angular.forEach($scope.inputVO.csvDataList ,function(row,index){
//	        				if(row.CHG_TYPE2 == 'M' || row.CHG_TYPE2 == 'MR'){
//	        					row.chgTypeList2= [{"DATA":"M","LABEL":"主要"},{"DATA":"R","LABEL":"參考"}];
//	        				} else {
//	        					row.chgTypeList2= [{"DATA":"MR","LABEL":"主要"},{"DATA":"R","LABEL":"參考"}];
//	        				}
//	        			});
                	};
			});
		};
		$scope.init();
        
        $scope.confirm = function(row) {
        	var errorFlag = false;
        	angular.forEach($scope.inputVO.csvDataList ,function(row,index){
        		if(!errorFlag){
            		if(!errorFlag && (row.CHG_TYPE2 == 'M' || row.CHG_TYPE2 == 'MR') && (row.CHG_TYPE == null || row.CHG_TYPE == '')){
            			errorFlag = true;
            			$scope.showErrorMsg('名單內有主要類別理專尚未回報');
                	}
        		};
			});
        	if(errorFlag) return;
        	
			$scope.sendRecv("CRM372", "directorConfirm", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog('successful');
	                	};
			});
        };
		
});