/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM1241_RECORDController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM1241_RECORDController";
		
		// SELL_OUT_YN
		$scope.mappingSet['SELL_OUT_YN'] = [];
		$scope.mappingSet['SELL_OUT_YN'].push({LABEL: '是', DATA: 'Y'},{LABEL: '否', DATA: 'N'});
		
		var vo = {'param_type': 'PMS.VISIT_PURPOSE', 'desc': false};
        if(!projInfoService.mappingSet['PMS.VISIT_PURPOSE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['PMS.VISIT_PURPOSE'] = totas[0].body.result;
        			$scope.mappingSet['PMS.VISIT_PURPOSE'] = projInfoService.mappingSet['PMS.VISIT_PURPOSE'];
        		}
        	});
        } else {
        	$scope.mappingSet['PMS.VISIT_PURPOSE'] = projInfoService.mappingSet['PMS.VISIT_PURPOSE'];
        }
        //初始
		$scope.init = function() {
			$scope.inputVO = {
					seq : $scope.row.SEQ,
					branch_id : '',
					branch_name : '',
					as_date : '',
					as_datetime_bgn : '',
					as_datetime_end : '',
					cust_id : '',
					cust_name : '',
					ao_code : '',
					ao_name : '',
					ao_job_rank : '',
					visit_purpose : '',
					visit_purpose_other : '',
					ptype : '',
					prod_id : '',
					pname : '',
					key_issue : '',
					sell_out_yn : '',
					visit_memo_as : '',
					visit_memo_ao : ''
			}
			$scope.type = '';
			if (projInfoService.getRoleID() == "FA9" || projInfoService.getRoleID() == "IA9"){
				$scope.type = 1;
			//輔銷人員頁面	
			}else if(projInfoService.getRoleID() == "FA" || projInfoService.getRoleID() == "IA"){
				$scope.type = 2;
			}
		}
		$scope.init();
	    
		
		//查詢
		$scope.inquire = function() {
			$scope.sendRecv("CRM1241", "inquire_record", "com.systex.jbranch.app.server.fps.crm1241.CRM1241InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList_record.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							console.log(tota[0].body.resultList_record)
							$scope.resultList_record = tota[0].body.resultList_record;
							$scope.inputVO = {
									seq : $scope.resultList_record[0].SEQ,
									branch_id : $scope.resultList_record[0].BRANCH_NBR,
									branch_name : $scope.resultList_record[0].BRANCH_NAME,
									as_date : $scope.resultList_record[0].AS_DATE,
									as_datetime_bgn : $scope.resultList_record[0].AS_DATETIME_BGN,
									as_datetime_end : $scope.resultList_record[0].AS_DATETIME_END,
									cust_id : $scope.resultList_record[0].CUST_ID,
									cust_name : $scope.resultList_record[0].CUST_NAME,
									ao_code : $scope.resultList_record[0].AO_CODE,
									ao_name : $scope.resultList_record[0].EMP_NAME,
									ao_job_rank : $scope.resultList_record[0].AO_JOB_RANK,
									visit_purpose : $scope.resultList_record[0].VISIT_PURPOSE,
									visit_purpose_other : $scope.resultList_record[0].VISIT_PURPOSE_OTHER,
									ptype : $scope.resultList_record[0].PTYPE,
									prod_id : $scope.resultList_record[0].PRO_ID,
									pname : $scope.resultList_record[0].PNAME,
									key_issue : $scope.resultList_record[0].KEY_ISSUE,
									sell_out_yn : $scope.resultList_record[0].SELL_OUT_YN,
									visit_memo_as : $scope.resultList_record[0].VISIT_MEMO_AS,
									visit_memo_ao : $scope.resultList_record[0].VISIT_MEMO_AO,
									complete_yn : $scope.resultList_record[0].COMPLETE_YN,
									status : $scope.resultList_record[0].STATUS
							}	
							if($scope.inputVO.prod_id != null){
								$scope.inputVO.product_name = $scope.inputVO.pname;
							}
							return;
						}
						
			});
	    }
		$scope.inquire();
		
		
        //查詢建議商品
		$scope.query_product = function(row) {			
        	var dialog = ngDialog.open({
				template: 'assets/txn/CRM1241/CRM1241_PRODUCT.html',
				className: 'CRM1241_PRODUCT',
				showClose: false,
				controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
        	dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.init();
				}
				if(data.value == 'getPrdid'){
					$scope.init_saleplan();					 
				}		
			});
	    }
		
		//關鍵商品重新查詢
		$scope.init_saleplan = function(){
			$scope.sendRecv("CRM1241", "inquire_record", "com.systex.jbranch.app.server.fps.crm1241.CRM1241InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList_record.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}							
							$scope.resultList_record = tota[0].body.resultList_record;
							$scope.inputVO.ptype = $scope.resultList_record[0].PTYPE,
							$scope.inputVO.prod_id = $scope.resultList_record[0].PRO_ID,
							$scope.inputVO.pname = $scope.resultList_record[0].PNAME		
							if($scope.inputVO.prod_id != null){
								$scope.inputVO.product_name = $scope.inputVO.pname;
							}
							return;
						}			
			});
		}
		//儲存
		$scope.save_record = function(){
			$scope.sendRecv("CRM1241", "save_record", "com.systex.jbranch.app.server.fps.crm1241.CRM1241InputVO", $scope.inputVO,
					function(tota, isError) {
				if (isError) {
            		$scope.showErrorMsg(tota[0].body.msgData);
            	}
            	if (tota.length > 0) {
            		$scope.showSuccessMsg('ehl_01_common_004');
            		$scope.closeThisDialog('successful');
            	};				
	    	});
		}
});
		