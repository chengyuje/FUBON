/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD171_EDIT5Controller',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD171_EDIT5Controller";
		
		
		$scope.idck= function(){
			//$scope.inputVO.INSPRD_ID
			$scope.sendRecv("PRD171","queryData2","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
        			$scope.inputVO,function(tota,isError){
            	if (isError) {
            		$scope.inputVO.INSPRD_NAME='';
            		$scope.inputVO.INSPRD_TYPE='';
            		$scope.showErrorMsg(tota[0].body.msgData);
            	
            		
            	}
            	if (tota.length > 0) {
            		if(tota[0].body.DILOGList.length>=1)
            			{
	            		$scope.inputVO.INSPRD_NAME=tota[0].body.DILOGList[0].INSPRD_NAME || '';
	            		$scope.inputVO.INSPRD_TYPE=tota[0].body.DILOGList[0].INSPRD_TYPE || '';
            			}else{
            				$scope.inputVO.INSPRD_NAME='';
    	            		$scope.inputVO.INSPRD_TYPE='';
            			}
            	};
			});
		}
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
			$scope.Q_ID_TYPE = false;
			if($scope.title_type == 'Add'){
				$scope.title='新增提存參數設定';
				$scope.inputVO={
						TYPE_TABLE:'5'
							}

			}
		
			if($scope.title_type == 'Update'){
				$scope.title='修改提存參數設定';
				$scope.Q_ID_TYPE=true;
				
				$scope.inputVO={
						TYPE_TABLE:'5',
						KEY_NO                       :$scope.row_data.KEY_NO                 || '',
						PERIOD                       :$scope.row_data.PERIOD                 || '',
						INSPRD_ID                    :$scope.row_data.INSPRD_ID              || '',
						INSPRD_NAME                    :$scope.row_data.INSPRD_NAME              || '',
						INSPRD_ANNUAL                :$scope.row_data.INSPRD_ANNUAL          || '',
						EMP_BONUS_RATE               :$scope.row_data.EMP_BONUS_RATE         || '',
						PRD_BONUS_RATE               :$scope.row_data.PRD_BONUS_RATE         || '',
						OSEA_BONUS_RATE              :$scope.row_data.OSEA_BONUS_RATE        || '',
						HIGH_CPCT_BONUS_RATE         :$scope.row_data.HIGH_CPCT_BONUS_RATE   || '',
						YEAR_END_BONUS               :$scope.row_data.YEAR_END_BONUS         || ''
//						APPROVER                     :$scope.row_data.APPROVER               || '',
//						APP_DATE                     :$scope.row_data.APP_DATE               || ''      
				}
				$scope.idck();
			}
		}
		$scope.init();
		
		
		
		
		
		
		$scope.btnSubmit = function(){
			 if($scope.parameterTypeEditForm.$invalid) {
		          $scope.showErrorMsgInDialog('欄位檢核錯誤:檢視頻率必要輸入欄位');
		          return;
		         }
			if($scope.title_type == 'Add' || $scope.title_type == 'Copy'){
				
					$scope.sendRecv("PRD171","addData","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
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
			if($scope.title_type == 'Update'){
				
					$scope.sendRecv("PRD171","updateData","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
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