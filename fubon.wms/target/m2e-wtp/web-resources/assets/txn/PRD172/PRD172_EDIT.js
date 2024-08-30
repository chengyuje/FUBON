/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD172_EDITController',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD172_EDITController";
		
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
		
		

		
		  $scope.mappingSet['type']=[];
          $scope.mappingSet['type'].push({
				LABEL : '是',
				DATA :'1'
  		},{
				LABEL : '否',
				DATA :'2'
  		});
		
		$scope.init = function(){
			$scope.REG_TYPEList = $scope.REG_TYPE;
			$scope.Q_ID_TYPE = false;
			if($scope.title_type == 'Add'){
				$scope.title='新增檢核文件';
				$scope.inputVO.REG_TYPE='';
			}
		
			if($scope.title_type == 'Update'){
				$scope.title='修改檢核文件';
				$scope.Q_ID_TYPE=true;
				$scope.inputVO={
						SEQ:$scope.row_data.KEY_NO,
						DOC_NAME:$scope.row_data.DOC_NAME,
						DOC_SEQ:$scope.row_data.DOC_SEQ,
						DOC_TYPE:$scope.row_data.DOC_TYPE,
						DOC_LEVEL:$scope.row_data.DOC_LEVEL,
						SIGN_INC:$scope.row_data.SIGN_INC,
						OTH_TYPE:$scope.row_data.OTH_TYPE,
						REG_TYPE:$scope.row_data.REG_TYPE
				}
			}
		}
		$scope.init();
		
		// 如果先選"登錄種類"，再選"文件登錄種類"，則重製"登錄種類"
		$scope.REG_TYPEController = function(){
			if($scope.inputVO.OTH_TYPE != '' && $scope.inputVO.REG_TYPE != '3'){
				$scope.inputVO.REG_TYPE = "3";
			}
		}
		// 登錄種類如果為"其他文件登錄"，則"文件登錄種類為必填"
		$scope.OTH_TYPEController = function(){
			if($scope.inputVO.REG_TYPE == '3'){
				return true;
			}
			if($scope.inputVO.REG_TYPE == '4'){
				$scope.inputVO.OTH_TYPE = '';
				return false;
			}
		}
		$scope.chk_REG = function(){
			if($scope.inputVO.REG_TYPE == '' || $scope.inputVO.REG_TYPE == '4'){
				return true;
			}
			else{
				return false;
			}
		}
		$scope.chk_REG();
		
		$scope.btnSubmit = function(){
			 if($scope.parameterTypeEditForm.$invalid) {
		          $scope.showErrorMsg('欄位檢核錯誤:檢視頻率必要輸入欄位');
		          return;
		         }
			if($scope.title_type == 'Add' || $scope.title_type == 'Copy'){
					$scope.sendRecv("PRD172","addData","com.systex.jbranch.app.server.fps.prd172.PRD172EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_001');
	                		$scope.closeThisDialog($scope.inputVO.REG_TYPE , 'successful');
	                	}
					});
				
			}
			if($scope.title_type == 'Update'){
				
					$scope.sendRecv("PRD172","updateData","com.systex.jbranch.app.server.fps.prd172.PRD172EDITInputVO",
		        			$scope.inputVO,function(tota,isError){
	                	if (isError) {
	                		$scope.showErrorMsg(tota[0].body.msgData);
	                	}
	                	if (tota.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_006');
	                		$scope.closeThisDialog('successful');
	                	}
	                	});
					}else{	}
			}
		
		
	}
);