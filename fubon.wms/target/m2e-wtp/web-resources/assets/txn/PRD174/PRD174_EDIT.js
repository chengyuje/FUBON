/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD174_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD174_EDITController";
		
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
			$scope.Q_ID_TYPE = false;
			if($scope.title_type == 'Add'){
				$scope.title='新增題目';
//				$scope.inputVO={
//						Q_ID:'',
//						Q_NAME:'',
//						Q_TYPE:'',
//						EFFECT_DATE:undefined,
//						EXPIRY_DATE:undefined
//				}
			}
			if($scope.title_type == 'Copy'){
				$scope.title='複製題目';
				$scope.inputVO={
						Q_NAME:$scope.row_data.Q_NAME,
						Q_TYPE:$scope.row_data.Q_TYPE,
						EFFECT_DATE:$scope.row_data.EFFECT_DATE_CHANGE,
						EXPIRY_DATE:$scope.row_data.EXPIRY_DATE_CHANGE,
						TEXT_STYLE_B:$scope.row_data.TEXT_STYLE_B,
						TEXT_STYLE_I:$scope.row_data.TEXT_STYLE_I,
						TEXT_STYLE_U:$scope.row_data.TEXT_STYLE_U,
						TEXT_STYLE_A:$scope.row_data.TEXT_STYLE_A
				}
			}
			if($scope.title_type == 'Update'){
				$scope.title='修改題目';
				$scope.Q_ID_TYPE=true;
				$scope.inputVO={
						Q_ID:$scope.row_data.Q_ID,
						Q_NAME:$scope.row_data.Q_NAME,
						Q_TYPE:$scope.row_data.Q_TYPE,
						EFFECT_DATE:$scope.row_data.EFFECT_DATE_CHANGE,
						EXPIRY_DATE:$scope.row_data.EXPIRY_DATE_CHANGE,
						TEXT_STYLE_B:$scope.row_data.TEXT_STYLE_B,
						TEXT_STYLE_I:$scope.row_data.TEXT_STYLE_I,
						TEXT_STYLE_U:$scope.row_data.TEXT_STYLE_U,
						TEXT_STYLE_A:$scope.row_data.TEXT_STYLE_A
				}
			}
		}
		$scope.init();
		
		$scope.btnSubmit = function(){
			if($scope.title_type == 'Add' || $scope.title_type == 'Copy'){
				if($scope.inputVO.Q_ID !=undefined && $scope.inputVO.EFFECT_DATE != undefined && $scope.inputVO.EXPIRY_DATE != undefined){
					$scope.sendRecv("PRD174","addData","com.systex.jbranch.app.server.fps.prd174.PRD174EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog('successful');
	                	};
					});
				}else{
					if($scope.inputVO.Q_ID == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['題目代號']);
					}
					if($scope.inputVO.EFFECT_DATE == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['啟用日']);
					}					
					if($scope.inputVO.EXPIRY_DATE == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['停用日']);
					}	
				}
			}
			if($scope.title_type == 'Update'){
				if($scope.inputVO.Q_ID !=undefined && $scope.inputVO.EFFECT_DATE != undefined && $scope.inputVO.EXPIRY_DATE != undefined){
					$scope.sendRecv("PRD174","updateData","com.systex.jbranch.app.server.fps.prd174.PRD174EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_006');
	                		$scope.closeThisDialog('successful');
	                	};
					});
				}else{
					if($scope.inputVO.Q_ID == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['題目代號']);
					}
					if($scope.inputVO.EFFECT_DATE == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['啟用日']);
					}					
					if($scope.inputVO.EXPIRY_DATE == undefined){
						$scope.showErrorMsg('ehl_02_common_002',['停用日']);
					}	
				}
			}
		}
		
	}
);