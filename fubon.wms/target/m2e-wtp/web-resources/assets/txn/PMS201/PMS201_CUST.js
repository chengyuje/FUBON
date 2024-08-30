/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS201_CUSTController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS201_CUSTController";
		
		// filter
		getParameter.XML(["PMS.PRDCIVT3_TITLE_NAME", "CRM.CON_DEGREE"], function(totas) {
			if (totas) {
				$scope.mappingSet['PMS.PRDCIVT3_TITLE_NAME'] = totas.data[totas.key.indexOf('PMS.PRDCIVT3_TITLE_NAME')];
				$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.mappingSet['CRM.CON_DEGREE'].push({LABEL:'合計', DATA:'TOT'});
			}
		});
	    //
		
		$scope.init = function(){
			$scope.inputVO = {
				eTime: $scope.eTime,
				ao_code: $scope.ao_code, 
				conDegree: $scope.vipType, 
				targetType: ($scope.row.COLUMN_NAME == 'NON_CNTCT_CNT' ? '1' : '2')
        	};
		};
        $scope.init();
        
        $scope.initialList = function(){
			$scope.data = [];
			$scope.prdCtvtMngmDtl = [];
		};
		$scope.initialList();
        
        $scope.sendRecv("PMS201", "getPrdCtvtMngmDtl", "com.systex.jbranch.app.server.fps.pms201.PMS201InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                		return;
                	}
                	if (totas.length > 0) {
                		$scope.prdCtvtMngmDtl = totas[0].body.prdCtvtMngmDtl;

						$scope.paramListOutputVO = totas[0].body;
                	};
				}
		);
        
});
