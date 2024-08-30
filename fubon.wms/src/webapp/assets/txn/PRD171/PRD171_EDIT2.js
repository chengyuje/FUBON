/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD171_EDIT2Controller',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD171_EDIT2Controller";
		
		 //IOT.PRODUCT_TYPE
        var vo = {'param_type': 'IOT.PRODUCT_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['IOT.PRODUCT_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['IOT.PRODUCT_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['IOT.PRODUCT_TYPE'] = projInfoService.mappingSet['IOT.PRODUCT_TYPE'];
        		}
        	});
        } else
        	$scope.mappingSet['IOT.PRODUCT_TYPE'] = projInfoService.mappingSet['IOT.PRODUCT_TYPE'];
		
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
				$scope.title='新增特殊條件設定';
				$scope.inputVO={
						TYPE_TABLE        :'2',
						KEY_NO            : '',    
						INSPRD_NAME       :'',
						INSPRD_TYPE   : ''
					
				}

			}
		
			if($scope.title_type == 'Update'){
				$scope.title='修改特殊條件設定';
				$scope.Q_ID_TYPE=true;
				
				$scope.inputVO={
						TYPE_TABLE:'2',
						KEY_NO                :$scope.row_data.KEY_NO             || '',             
						INSPRD_ID             :$scope.row_data.INSPRD_ID         || '' ,
						INSPRD_TYPE             :$scope.row_data.INSPRD_TYPE         || '' ,
						INSPRD_NAME             :$scope.row_data.INSPRD_NAME         || '' ,
						PAY_TYPE              :$scope.row_data.PAY_TYPE           || '',
						SPECIAL_CONDITION     :$scope.row_data.SPECIAL_CONDITION  || '',
						SEQ                   :$scope.row_data.SEQ                || ''
//						APPROVER              :$scope.row_data.APPROVER           || '',
//						APP_DATE              :$scope.row_data.APP_DATE           
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
//			 if($scope.inputVO.INSPRD_TYPE=='' || $scope.inputVO.INSPRD_NAME=='')
//				 {
//				  $scope.showErrorMsgInDialog('ehl_01_prd171_002');
//				  return;
//				 }
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