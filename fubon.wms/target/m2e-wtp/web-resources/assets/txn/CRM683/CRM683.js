/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM683Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM683Controller";
		
		getParameter.XML(["CRM.NMVIPA_ARR03"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.NMVIPA_ARR03']  = totas.data[totas.key.indexOf('CRM.NMVIPA_ARR03')];
			}
		});
		
		//=== 信託管理運用方式 filter
		var vo = {'param_type': 'CRM.NMVIPA_ARR14', 'desc': false};
		if(!projInfoService.mappingSet['CRM.NMVIPA_ARR14']) {
	    	$scope.requestComboBox(vo, function(totas) {
	    		if (totas[totas.length - 1].body.result === 'success') {
	    			projInfoService.mappingSet['CRM.NMVIPA_ARR14'] = totas[0].body.result;
	    			$scope.mappingSet['CRM.NMVIPA_ARR14'] = projInfoService.mappingSet['CRM.NMVIPA_ARR14'];
	    		}
	    	});
	    } else {
	    	$scope.mappingSet['CRM.NMVIPA_ARR14'] = projInfoService.mappingSet['CRM.NMVIPA_ARR14'];
	    }
		
		//初始化
		$scope.init = function(){
//			$scope.inputVO.cust_id = 'A201262543';
//			$scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
		};
		$scope.init();
		
		
		$scope.sendRecv("CRM683", "inquire", "com.systex.jbranch.app.server.fps.crm683.CRM683InputVO", $scope.inputVO,
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
				template: 'assets/txn/CRM683/CRM683_DETAIL.html',
				className: 'CRM683_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		}
		
		
});
