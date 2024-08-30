/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM826Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM826Controller";
		
		$scope.getPara = function(){
	        getParameter.XML(["CRM.DCI_CRCY_CHGE_STATUS"], function(totas) {
				if(len(totas)>0){
					$scope.mappingSet['CRM.DCI_CRCY_CHGE_STATUS'] = totas.data[totas.key.indexOf('CRM.DCI_CRCY_CHGE_STATUS')];
				}
				 
			});
        }
		var vo = {'param_type': 'FPS.CURRENCY', 'desc': false};
		if(!projInfoService.mappingSet['FPS.CURRENCY']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['FPS.CURRENCY'] = totas[0].body.result;
	    			$scope.mappingSet['FPS.CURRENCY'] = projInfoService.mappingSet['FPS.CURRENCY'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['FPS.CURRENCY'] = projInfoService.mappingSet['FPS.CURRENCY'];
	    }
        $scope.getPara();
        
		//初始化
		$scope.init = function(){
//			$scope.inputVO.cust_id = 'A100122119';
//			$scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
		};
		$scope.init();
		
		$scope.sendRecv("CRM826", "inquire", "com.systex.jbranch.app.server.fps.crm826.CRM826InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
//							$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					$scope.resultList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
					return;
				}
		});
		
		$scope.detail = function (row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM826/CRM826_DETAIL.html',
				className: 'CRM826_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		}
		
		
});
