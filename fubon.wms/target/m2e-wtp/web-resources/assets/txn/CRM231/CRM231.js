/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM231Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('CRM230Controller', {$scope: $scope});
		
		$scope.controllerName = "CRM231Controller";

		getParameter.XML(["CAM.PRD_TYPE" , "CRM.CRM230_YN"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.CRM230_YN'] = totas.data[totas.key.indexOf('CRM.CRM230_YN')];
				
				$scope.mappingSet['PRD_TYPE'] = [];
				angular.forEach(totas.data[0], function(row, index, objs){
					if(index < 7) {
						$scope.mappingSet['PRD_TYPE'].push({LABEL: row.LABEL, DATA: row.DATA});
					}			
				});
			}
		});
		
		//是否持有有效信用卡連動
		$scope.checkCardFlg = function(){
			if('Y' == $scope.inputVO.hold_card_flg){
				$scope.inputVO.spec_date = null;
				$scope.inputVO.opp_ms_type =  null;
				$scope.inputVO.spec_check_yn = 'N';
				$scope.inputVO.ms_type = '0';
			}
			else{
				$scope.inputVO.ms_type = null;
				$scope.inputVO.spec_check_yn = 'Y';
				$scope.inputVO.opp_ms_type = '0';
				$scope.check();
			}
		}

		
		$scope.checkProd = function() {
			
			$scope.inputVO.prod_flag = false;
			
			if(($scope.inputVO.prod_type != undefined && $scope.inputVO.prod_type != '') ||
			   ($scope.inputVO.rtn_rate_wd_bgn != undefined && $scope.inputVO.rtn_rate_wd_bgn != '') ||
			   ($scope.inputVO.rtn_rate_wd_end != undefined && $scope.inputVO.rtn_rate_wd_end != '') ||
			   ($scope.inputVO.prod_id != undefined && $scope.inputVO.prod_id != '') ||
			   ($scope.inputVO.now_amt_twd_bgn != undefined && $scope.inputVO.now_amt_twd_bgn != '') ||
			   ($scope.inputVO.now_amt_twd_end != undefined && $scope.inputVO.now_amt_twd_end != '') ||
			   ($scope.inputVO.prod_name != undefined && $scope.inputVO.prod_name != '') ||
			   ($scope.inputVO.cur_id != undefined && $scope.inputVO.cur_id != '') ||
			   ($scope.inputVO.take_prft_pt_bgn != undefined && $scope.inputVO.take_prft_pt_bgn != '') ||
			   ($scope.inputVO.take_prft_pt_end != undefined && $scope.inputVO.take_prft_pt_end != '') ||
			   ($scope.inputVO.stop_loss_pt_bgn != undefined && $scope.inputVO.stop_loss_pt_bgn != '') ||
			   ($scope.inputVO.stop_loss_pt_end != undefined && $scope.inputVO.stop_loss_pt_end != '')){
				
				$scope.inputVO.prod_flag = true;
			}
		}
		
	    $scope.inquire = function() {
//	    	$scope.inputVO.aolist = $scope.aolist;
	    	$scope.checkProd();
			$scope.sendRecv("CRM231", "inquire", "com.systex.jbranch.app.server.fps.crm231.CRM231InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							
							$scope.resultList = [];
							if($scope.inputVO.prod_flag){
								//去除沒有投資資訊的客戶								
								angular.forEach(tota[0].body.resultList, function(row) {
									if(row.PROD_ID != null){
										if(row.PROD_ID.substring(0, 4) == "WMBB"){
											row.isWMBB = true;
										}else{
											row.isWMBB = false;
										}
										$scope.resultList.push(row);
									}
								});
								$scope.outputVO = {'data':$scope.resultList};								
							}
								
							//客戶資訊(一個客戶只呈現一筆)
							$scope.custList = [];
							angular.forEach(tota[0].body.resultList, function(row) {
								var temp = $scope.custList.map(function(e) { return e.CUST_ID; }).indexOf(row.CUST_ID) > -1;
								if (!temp)
		            				$scope.custList.push(row);
							});
							$scope.custOutputVO = {'data':$scope.custList};
							return;
						}
			});
	    };
	    
	    $scope.check = function() {
	    	//日期設限checkbox為勾選時不帶，為從未辦卡者
	    	if($scope.inputVO.spec_check_yn == 'N'){
	    		$scope.inputVO.spec_date = undefined;
	    	}
	    	//如果有勾選為系統日的前180天，卡片預設帶正卡
	    	else{
	    		$scope.inputVO.spec_date = new Date().setDate(new Date().getDate() - 180);
	    	}
	    	
	    }
	    
	    $scope.clearAll = function(){
	    	$scope.custList = [];
	    	$scope.custOutputVO = {};
	    	$scope.resultList = [];
	    	$scope.inputVO = {};
	    	$scope.outputVO = {};
	    }
	    
		$scope.initQuery = function(){
			$scope.sendRecv("CRM211", "initQuery", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", $scope.inputVO,
				function(tota, isError) {
				if (!isError) {
					if(tota[0].body.showMsg){
						$scope.showErrorMsg(tota[0].body.errorMsg);
					}
					
				}					
			});
		};
		$scope.initQuery();
	
});

