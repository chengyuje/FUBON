/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD171_EDIT7Controller',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD171_EDIT7Controller";
		
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
			$scope.disabled_else = false;
			$scope.disabled_required = true;
			if($scope.title_type == 'Add'){
				$scope.title='新增分行文件設定';
				$scope.inputVO={
						TYPE_TABLE:'7',
						DOC_TYPE:'1',
						copy:false,
						INSPRD_ID:'',
						copy_insprd_id:'',
						DOCCHKList:[]
				}

			}
		
			if($scope.title_type == 'Update'){
				$scope.title='修改分行文件設定';
				$scope.Q_ID_TYPE=true;
				$scope.inputVO={
						TYPE_TABLE:'7',
						INSPRD_ID:$scope.row_data.INSPRD_ID        || '',
						SEQ:$scope.row_data.KEY_NO                 || '',
						DOC_NAME:$scope.row_data.DOC_NAME          || '',
						DOC_SEQ:$scope.row_data.DOC_SEQ            || '',
						DOC_TYPE:$scope.row_data.DOC_TYPE          || '',
						DOC_LEVEL:$scope.row_data.DOC_LEVEL        || '',
						SIGN_INC:$scope.row_data.SIGN_INC          || '',
						OTH_TYPE:$scope.row_data.OTH_TYPE          || ''
				}
			}
		}
		$scope.init();
		
		
		
		$scope.idck= function(){
			if($scope.inputVO.copy){
				$scope.be_copy(2);
			}else{
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
		}
		
		
		$scope.be_copy = function(type){
			switch (type) {
			case 1:
				$scope.inputVO.YN_Copy = '1';
				$scope.sendRecv("PRD171","copyDOCCHK","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
	        			$scope.inputVO,function(tota,isError){
					if(!isError){
						$scope.inputVO.DOCCHKList = tota[0].body.DOCCHKList;
					}else{
						$scope.inputVO.copy_insprd_id = '';
					}
				});
				break;
			case 2:
				$scope.inputVO.YN_Copy = '2';
				$scope.sendRecv("PRD171","copyDOCCHK","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
	        			$scope.inputVO,function(tota,isError){
					if(isError){
						$scope.inputVO.INSPRD_ID = '';
					}
				});
				break;
			default:
				break;
			}

		}
		
		$scope.copy_check = function(){
			if($scope.inputVO.copy){
				$scope.disabled_else = true;
				$scope.disabled_required = false;
			}else{
				$scope.disabled_else = false;
				$scope.disabled_required = true;
			}
		}
		
		
		
		
		
		
		$scope.btnSubmit = function(){
			 if($scope.parameterTypeEditForm.$invalid) {
		          $scope.showErrorMsgInDialog('欄位檢核錯誤:檢視頻率必要輸入欄位');
		          return;
		         }
			if($scope.title_type == 'Add' || $scope.title_type == 'Copy'){
					if($scope.inputVO.copy){
						$scope.sendRecv("PRD171","DOCCHK_COPY_ADD","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
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