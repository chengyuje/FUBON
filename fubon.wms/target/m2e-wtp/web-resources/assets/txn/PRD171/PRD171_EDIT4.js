/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD171_EDIT4Controller',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD171_EDIT4Controller";



		$scope.idck= function(){
			//$scope.inputVO.INSPRD_ID
			$scope.sendRecv("PRD171","queryData2","com.systex.jbranch.app.server.fps.prd171.PRD171EDITInputVO",
        			$scope.inputVO,function(tota,isError){
            	if (isError) {
            		$scope.inputVO.LIPPER_ID='';
            		$scope.inputVO.LINKED_NAME='';
            		$scope.inputVO.PRD_RISK='';
            		$scope.showErrorMsg(tota[0].body.msgData);


            	}
            	if (tota.length > 0) {
            		if(tota[0].body.DILOGList.length>=1)
        			{
            		$scope.inputVO.LIPPER_ID=tota[0].body.DILOGList[0].LIPPER_ID || '';
            		$scope.inputVO.LINKED_NAME=tota[0].body.DILOGList[0].LINKED_NAME || '';
            		$scope.inputVO.PRD_RISK=tota[0].body.DILOGList[0].PRD_RISK || '';
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
		  $scope.mappingSet['COMPANY'] = $scope.companies;

		$scope.init = function(){
			$scope.Q_ID_TYPE = false;
			if($scope.title_type == 'Add'){
				$scope.title='新增連結標的設定';
				$scope.inputVO={
						TYPE_TABLE:'4'
				}

			}

			if($scope.title_type == 'Update'){
				$scope.title='修改連結標的設定';
				$scope.Q_ID_TYPE=true;

				$scope.inputVO={
						TYPE_TABLE:'4',
						KEY_NO           :$scope.row_data.KEY_NO          || '',
						INSPRD_ID        :$scope.row_data.INSPRD_ID       || '',
						TARGET_ID        :$scope.row_data.TARGET_ID       || '',
						FUND_ID          :$scope.row_data.FUND_ID         || '',
						LIPPER_ID        :$scope.row_data.LIPPER_ID       || '',
						LINKED_NAME      :$scope.row_data.LINKED_NAME     || '',
						PRD_RISK         :$scope.row_data.PRD_RISK        || '',
						TRAINING_TYPE    :$scope.row_data.TRAINING_TYPE   || '',
//						APPROVER         :$scope.row_data.APPROVER        || '',
//						APP_DATE         :$scope.row_data.APP_DATE        || ''

						COMPANY_NUM		 :$scope.row_data.COMPANY_NUM		   ,
						KYC_SCORE		 :$scope.row_data.KYC_SCORE		  || 0 ,
						TARGET_CURR		 :$scope.row_data.TARGET_CURR		   ,
						INT_TYPE		 :$scope.row_data.INT_TYPE			   ,
						TRANSFER_FLG     :$scope.row_data.TRANSFER_FLG
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
//			 if($scope.inputVO.LINKED_NAME=='' || $scope.inputVO.LIPPER_ID=='')
//			 {
//			  $scope.showErrorMsgInDialog('ehl_01_prd171_001');
//			  return;
//			 }

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
