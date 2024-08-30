/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM511_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM511_EDITController";
		
		// combobox
		$scope.mappingSet['Layer'] = [];
		$scope.mappingSet['Layer'].push({LABEL : '第一層顯示',DATA : '1'},{LABEL : '第二層顯示',DATA : '2'});
		$scope.mappingSet['Type'] = [];
		$scope.mappingSet['Type'].push({LABEL : '客戶經營-KYC',DATA : '1'},{LABEL : 'Advisory-KYC',DATA : '2'});
		// date picker
		// 有效起始日期
		$scope.bgn_sDateOptions = {};
		// 有效截止日期
		$scope.end_sDateOptions = {};
		// config
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.end_sDate;
			$scope.end_sDateOptions.minDate = $scope.inputVO.bgn_sDate;
		};
		// date picker end
		// 排序用
		$scope.sortableOptions = {
				stop: function(e, ui) {
					angular.forEach(ui.item.sortable.droptargetModel, function(row, index){
						row.DISPLAY_ORDER = index + 1;
					});
				}
		};
		
		$scope.init = function() {
        	if($scope.row)
        		$scope.isUpdate = true;
            $scope.row = $scope.row || {};
            $scope.inputVO = {
            		display_layer: $scope.row.DISPLAY_LAYER,
            		display_order: $scope.row.DISPLAY_ORDER || 0,
            		qstn_content: $scope.row.QSTN_CONTENT,
            		word_surgery: $scope.row.WORD_SURGERY,
            		qstn_type: $scope.row.QSTN_TYPE,
            		qstn_format: $scope.row.QSTN_FORMAT,
            		opt_yn: $scope.row.OTH_OPT_YN,
            		memo_yn: $scope.row.EXT_MEMO_YN
            };
            if(!$scope.isUpdate) {
            	$scope.sendRecv("CRM511", "getSN", "com.systex.jbranch.app.server.fps.crm511.CRM511InputVO", {},
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.inputVO.qstn_id = totas[0].body.qstn_id;
    	                	};
    					}
    			);
            	$scope.inputVO.au_list = [];
            } else {
            	$scope.inputVO.qstn_id = $scope.row.QSTN_ID;
            	// TBCRM_DKYC_ANS_SET
        		$scope.inputVO.au_list = [];
    			$scope.sendRecv("CRM511", "getAU", "com.systex.jbranch.app.server.fps.crm511.CRM511InputVO", {'qstn_id':$scope.inputVO.qstn_id},
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.inputVO.au_list = totas[0].body.resultList;
    	                		// 日期
    	                		if($scope.row.QSTN_FORMAT == 'D') {
    	                			angular.forEach($scope.inputVO.au_list, function(row) {
    	            					if(row.ANS_VALUE)
    	            						row.ANS_VALUE = new Date(row.ANS_VALUE);
    	                			});
    	                		}
    	                	};
    					}
    			);
            }
            if($scope.row.VALID_BGN_DATE)
            	$scope.inputVO.bgn_sDate = $scope.toJsDate($scope.row.VALID_BGN_DATE);
            if($scope.row.VALID_END_DATE)
            	$scope.inputVO.end_sDate = $scope.toJsDate($scope.row.VALID_END_DATE);
            // date picker
            $scope.bgn_sDateOptions.maxDate = $scope.inputVO.end_sDate || $scope.maxDate;
			$scope.end_sDateOptions.minDate = $scope.inputVO.bgn_sDate || $scope.minDate;
			//
            if($scope.row.VIP_DEGREE)
            	$scope.inputVO.vip_degree = $scope.row.VIP_DEGREE.split(",");
            else
            	$scope.inputVO.vip_degree = [];
            if($scope.row.AUM_DEGREE)
            	$scope.inputVO.aum_degree = $scope.row.AUM_DEGREE.split(",");
            else
            	$scope.inputVO.aum_degree = [];
        };
        $scope.init();
        
        //for checkbox
        var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        		}
        	});
        } else
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        var vo = {'param_type': 'CRM.CON_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.CON_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.CON_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.CON_DEGREE'] = projInfoService.mappingSet['CRM.CON_DEGREE'];
        		}
        	});
        } else
        	$scope.mappingSet['CRM.CON_DEGREE'] = projInfoService.mappingSet['CRM.CON_DEGREE'];
        // checkbox
		$scope.toggleSelection = function toggleSelection(data) {
        	var idx = $scope.inputVO.vip_degree.indexOf(data);
        	if (idx > -1) {
        		$scope.inputVO.vip_degree.splice(idx, 1);
        	} else {
        		$scope.inputVO.vip_degree.push(data);
        	}
        };
        $scope.toggleSelection2 = function toggleSelection(data) {
        	var idx = $scope.inputVO.aum_degree.indexOf(data);
        	if (idx > -1) {
        		$scope.inputVO.aum_degree.splice(idx, 1);
        	} else {
        		$scope.inputVO.aum_degree.push(data);
        	}
        };
		//
		
        $scope.addAU = function() {
        	$scope.inputVO.au_list.push({});
        	angular.forEach($scope.inputVO.au_list, function(row) {
				row.DISPLAY_ORDER = index + 1;
			});
		};
		$scope.removeAU = function(index) {
			$scope.inputVO.au_list.splice(index,1);
			angular.forEach($scope.inputVO.au_list, function(row) {
				row.DISPLAY_ORDER = index + 1;
			});
        };
        
        $scope.clearAns = function() {
        	angular.forEach($scope.inputVO.au_list, function(row) {
				row.ANS_VALUE = "";
			});
        	// check use
        	if(($scope.inputVO.qstn_format == 'C' || $scope.inputVO.qstn_format == 'M') && $scope.inputVO.au_list.length == 0)
        		$scope.check = true;
        	else
        		$scope.check = false;
		};
        
        $scope.save = function() {
        	if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	// TBCRM_DKYC_ANS_SET
        	$scope.check = false;
        	if(($scope.inputVO.qstn_format == 'C' || $scope.inputVO.qstn_format == 'M') && $scope.inputVO.au_list.length == 0)
        		$scope.check = true;
        	angular.forEach($scope.inputVO.au_list, function(row){
				if(!row.ANS_CONTENT || !row.ANS_VALUE)
					$scope.check = true;
				// format
				if($scope.inputVO.qstn_format == 'D')
					row.ANS_VALUE = $filter('date')(row.ANS_VALUE, "yyyy/MM/dd");
			});
	    	if($scope.check) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
	    		return;
	    	}
	    	//
        	if($scope.isUpdate) {
        		$scope.sendRecv("CRM511", "updateKYC", "com.systex.jbranch.app.server.fps.crm511.CRM511InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
        	} else {
        		$scope.sendRecv("CRM511", "addKYC", "com.systex.jbranch.app.server.fps.crm511.CRM511InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
        	}
        };
		
});
