/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD173_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD173_EDITController";
		
	     // date picker
		// 有效起始日期
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.EXPIRY_DATE || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.EFFECT_DATE || $scope.minDate;
		};		
		
		$scope.init = function(){
			if($scope.title_type == 'Add'){
				$scope.title='新增匯利專案';
			}

			if($scope.title_type == 'Update'){
				$scope.title='修改匯利專案';
				$scope.inputVO={
						FXD_KEYNO:$scope.row_data.FXD_KEYNO,
						PROD_NAME:$scope.row_data.PROD_NAME,
						PROD_PERIOD:$scope.row_data.PROD_PERIOD,
						EFFECT_DATE:new Date($scope.row_data.EFFECT_DATE_CHANGE),
						EXPIRY_DATE:new Date($scope.row_data.EXPIRY_DATE_CHANGE)
				}
				$scope.limitDate();
			}
		}
		$scope.init();
		

		
		$scope.btnSubmit = function(){
			if($scope.title_type == 'Add'){
				if($scope.add_prod.$invalid){
					if($scope.inputVO.PROD_NAME == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['專案名稱']);
					}
					if($scope.inputVO.PROD_PERIOD == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['商品檔期']);
					}
					if($scope.inputVO.EFFECT_DATE == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['啟用日']);
					}					
					if($scope.inputVO.EXPIRY_DATE == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['停用日']);
					}	
				}else{
					$scope.sendRecv("PRD173","addData","com.systex.jbranch.app.server.fps.prd173.PRD173EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog('successful');
	                	};
					});
				}
			}
			if($scope.title_type == 'Update'){
				if($scope.add_prod.$invalid){
					if($scope.inputVO.PROD_NAME == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['專案名稱']);
					}
					if($scope.inputVO.PROD_PERIOD == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['商品檔期']);
					}
					if($scope.inputVO.EFFECT_DATE == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['啟用日']);
					}					
					if($scope.inputVO.EXPIRY_DATE == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['停用日']);
					}	
				}else{
					$scope.sendRecv("PRD173","updateData","com.systex.jbranch.app.server.fps.prd173.PRD173EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_006');
	                		$scope.closeThisDialog('successful');
	                	};
					});
				}
			}
		}
		
	}
);