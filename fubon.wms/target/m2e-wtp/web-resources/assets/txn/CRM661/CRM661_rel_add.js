/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM661_rel_addController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,$rootScope ) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM661_rel_addController";

		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		//vip_degree
        var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        		}
        	});
        } else {
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        }
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id_m = $scope.cust_id_m;
			$scope.inputVO.cust_id ='';
			$scope.inputVO.cust_name ='';
			$scope.inputVO.ao_code = $scope.ao_code;
			$scope.inputVO.emp_id = sysInfoService.getUserID();
			$scope.inputVO.choose = {};
		}
		$scope.init();
		
		//輸出欄初始化
		$scope.inquireInit = function(){
			$scope.resultList_rel_add = [];
		}
		$scope.inquireInit();
		
		//抓東西用
		$scope.sendRecv("CRM661", "initial", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", $scope.inputVO,
			function(tota, isError) {
				$scope.resultList = tota[0].body.resultList;
		});
		
		
		$scope.rel_inquire = function(){
			//檢核
			if($scope.inputVO.cust_id == '' && $scope.inputVO.cust_name == ''){
				$scope.showErrorMsgInDialog("請輸入查詢條件")
				return;
			}
			if($scope.inputVO.cust_id != ''){
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.replace(/[ ]/g,"");
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			}
			if($scope.inputVO.cust_name != ''){
				$scope.inputVO.cust_name = $scope.inputVO.cust_name.replace(/[ ]/g,"");
			}
//			$scope.inputVO.cust_id = $scope.inputVO.cust_id.replace(/[ ]/g,"");
//			$scope.inputVO.cust_name = $scope.inputVO.cust_name.replace(/[ ]/g,"");
			$scope.sendRecv("CRM661", "rel_inquire", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}
					if(tota[0].body.resultList_rel_add.length == 0) {
						$scope.showMsg("ehl_01_common_009");
	               		return;
					}
					$scope.resultList_rel_add = tota[0].body.resultList_rel_add;
			});
		}
		
		//radio 檢查
		$scope.rel_add = function(){
			//原寫法不知道甚麼原因只能選第一筆，沒有選一定沒有值，因此修正為抓取Object的其中一個值
//			if(document.getElementById('check').checked) {
			if($scope.inputVO.choose["CUST_ID"] != null || $scope.inputVO.choose["CUST_ID"] != undefined) {	
				$scope.closeThisDialog($scope.inputVO.choose);
			} else  {
				$scope.showMsg("尚未選擇關係戶客戶");
        		return;
			}
		}

		
	}
);