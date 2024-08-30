/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD300_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD300_EDITController";
		
		// combobox
		getParameter.XML(["FPS.PROD_RISK_LEVEL","FPS.CURRENCY", "FPS.PROD_INV_LEVEL"], function(totas) {
			if (totas) {
				$scope.PROD_RISK_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
				$scope.CURRENCY = totas.data[totas.key.indexOf('FPS.CURRENCY')];
				$scope.PROD_INV_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_INV_LEVEL')];
			}
		});
				
		$scope.checkID = function() {
			if($scope.inputVO.prd_id) {
				$scope.sendRecv("PRD300", "checkID", "com.systex.jbranch.app.server.fps.prd300.PRD300InputVO", {'prd_id':$scope.inputVO.prd_id},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.name){
									$scope.showErrorMsg('欄位檢核錯誤:此商品代碼已存在');
									$scope.showPrdIDError = true;		
									$scope.showPrdIDOk = false;
								}else{
									$scope.showPrdIDError = false;		
									$scope.showPrdIDOk = true;
								}	
							}
				});
			}
		};
		
				
		$scope.init = function() {
			debugger;
			if($scope.row) {
        		$scope.isUpdate = true;
        		$scope.inputVO = {
        			prd_id: $scope.row.PRD_ID,
        			prd_name: $scope.row.PRD_NAME,
        			riskcate_id: $scope.row.RISKCATE_ID,
        			stock_bond_type: $scope.row.STOCK_BOND_TYPE,
        			core_type: $scope.row.CORE_TYPE,
        			currency_std_id: $scope.row.CURRENCY_STD_ID,
        			is_sale: $scope.row.IS_SALE,
        			inv_level: $scope.row.INV_LEVEL
        			
        		};
        	} else {
        		$scope.inputVO = {
        			prd_id: '',
            		prd_name: '',
            		riskcate_id: '',
            		stock_bond_type: '',
            		core_type: '',
            		currency_std_id: '',
            		is_sale: '',
            		inv_level:''
        		};
        	}
		};
		$scope.init();
		
				
        $scope.save = function() {
//        	if($scope.parameterTypeEditForm.$invalid) {
//	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
//        		return;
//        	}
        	if(!$scope.inputVO.prd_id){
        		$scope.showErrorMsg('欄位檢核錯誤:商品代碼必要輸入欄位');
        		return;
        	}
        	if(!$scope.inputVO.prd_name){
        		$scope.showErrorMsg('欄位檢核錯誤:商品名稱必要輸入欄位');
        		return;
        	}
        	if(!$scope.inputVO.riskcate_id){
        		$scope.showErrorMsg('欄位檢核錯誤:風險屬性必要輸入欄位');
        		return;
        	}
        	if(!$scope.inputVO.stock_bond_type){
        		$scope.showErrorMsg('欄位檢核錯誤:類股票/類債券必要輸入欄位');
        		return;
        	}
        	if(!$scope.inputVO.currency_std_id){
        		$scope.showErrorMsg('欄位檢核錯誤:計價幣別必要輸入欄位');
        		return;
        	}
        	if(!$scope.inputVO.is_sale){
        		$scope.showErrorMsg('欄位檢核錯誤:是否可銷售必要輸入欄位');
        		return;
        	}
			if($scope.isUpdate) {
				$scope.sendRecv("PRD300", "update", "com.systex.jbranch.app.server.fps.prd300.PRD300InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showSuccessMsg('ehl_01_common_002');
		                		$scope.closeThisDialog('successful');
		                	};
				});
			} else {
				$scope.sendRecv("PRD300", "add", "com.systex.jbranch.app.server.fps.prd300.PRD300InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                	}
		                	if (tota.length > 0) {
		                		$scope.showSuccessMsg('ehl_01_common_001');
		                		$scope.closeThisDialog('successful');
		                	};
				});
			}
        };
		
		
		
		
});