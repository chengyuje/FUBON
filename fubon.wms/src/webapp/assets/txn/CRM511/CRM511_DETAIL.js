/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM511_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM511_DETAILController";
		
		// combobox
		$scope.mappingSet['Layer'] = [];
		$scope.mappingSet['Layer'].push({LABEL : '第一層顯示',DATA : '1'},{LABEL : '第二層顯示',DATA : '2'});
		$scope.mappingSet['Type'] = [];
		$scope.mappingSet['Type'].push({LABEL : '客戶經營-KYC',DATA : '1'},{LABEL : 'Advisory-KYC',DATA : '2'});
		getParameter.XML(["CRM.DKYC_QSTN_FORMAT", "CRM.VIP_DEGREE", "CRM.CON_DEGREE"], function(totas) {
			if (totas) {
				$scope.DKYC_QSTN_FORMAT = totas.data[totas.key.indexOf('CRM.DKYC_QSTN_FORMAT')];
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
			}
		});
        //
		
		$scope.init = function(){
        	$scope.inputVO = {
        			qstn_id: $scope.row.QSTN_ID,
            		display_layer: $scope.row.DISPLAY_LAYER,
            		display_order: $scope.row.DISPLAY_ORDER,
            		qstn_content: $scope.row.QSTN_CONTENT,
            		word_surgery: $scope.row.WORD_SURGERY,
            		qstn_type: $scope.row.QSTN_TYPE,
            		qstn_format: $scope.row.QSTN_FORMAT,
            		opt_yn: $scope.row.OTH_OPT_YN,
            		memo_yn: $scope.row.EXT_MEMO_YN
            };
        	$scope.sendRecv("CRM511", "getAU", "com.systex.jbranch.app.server.fps.crm511.CRM511InputVO", {'qstn_id':$scope.inputVO.qstn_id},
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.inputVO.au_list = totas[0].body.resultList;
	                	};
					}
			);
            if($scope.row.VALID_BGN_DATE)
            	$scope.inputVO.bgn_sDate = $scope.toJsDate($scope.row.VALID_BGN_DATE);
            if($scope.row.VALID_END_DATE)
            	$scope.inputVO.end_sDate = $scope.toJsDate($scope.row.VALID_END_DATE);
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
        
        
});
