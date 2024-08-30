/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM1241_PRODUCTController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter ,sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM1241_PRODUCTController";
	 //初始
	$scope.init = function() {
		$scope.inputVO = {
				seq : $scope.row.SEQ,
				ptype : '',
				prod_id : '',
				pname : ''
		}
	}
	$scope.init();

	$scope.inquire = function() {
		$scope.sendRecv("CRM1241", "inquire_record", "com.systex.jbranch.app.server.fps.crm1241.CRM1241InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {						
						if(tota[0].body.resultList_record.length == 0) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.resultList_record = tota[0].body.resultList_record;
						$scope.inputVO = {
								seq : $scope.resultList_record[0].SEQ,
								ptype : $scope.resultList_record[0].PTYPE,
								prod_id : $scope.resultList_record[0].PRO_ID,
								pname : $scope.resultList_record[0].PNAME
						}
						return;
					}
		});
    }
	$scope.inquire();
	
	
	//查詢
	$scope.query = function(){
		$scope.inputVO.ptype = $scope.inputVO.EST_PRD;
    	$scope.sendRecv("CRM1241", "inquire_product", "com.systex.jbranch.app.server.fps.crm1241.CRM1241InputVO", $scope.inputVO,
				function(tota, isError) {
			if (!isError) {	
				$scope.mappingSet['QUERY_PRODUCT'] =[];
				if(tota[0].body.resultList_product.length == 0) {
					$scope.result = '2';
					$scope.showMsg("建議商品查無資料，請重新輸入關鍵字或商品類別");				
					return;				
				}else if($scope.inputVO.ptype == "" || $scope.inputVO.ptype == undefined){
					$scope.result = '2';
					$scope.showMsg("請選擇商品類別");
					return;
				}else if(tota[0].body.resultList_product.length >200){
					$scope.result = '2';
					$scope.showMsg("篩選資料過多，請重新輸入關鍵字");
					return;
				}else{		
					$scope.result = '1';
					var prd_list = [];
					prd_list = tota[0].body.resultList_product;		
					angular.forEach(prd_list, function(row, index, objs){	
						for(var i = 0; i < prd_list.length; i++) {
								$scope.mappingSet['QUERY_PRODUCT'].push({LABEL: prd_list[i].PNAME , DATA: prd_list[i].PRD_ID});					
						}
					});				
					return;
				}					
			}
		});
	}
	
	//確定
	$scope.save = function(){
		
		//查詢結果沒選擇就不儲存
		if($scope.inputVO.query_product == undefined || $scope.inputVO.query_product == ""){
		}else{
			$scope.inputVO.prd_id = $scope.inputVO.query_product;
			$scope.sendRecv("CRM1241", "save_prd_id", "com.systex.jbranch.app.server.fps.crm1241.CRM1241InputVO", $scope.inputVO,
					function(tota, isError) {
				if (isError) {
	        		$scope.showErrorMsg(tota[0].body.msgData);
	        	}else{
	        		$scope.showMsg("ehl_01_common_023");
	        		$scope.closeThisDialog('getPrdid');
	        		
	        	}
	    	});
		}	
		
	}	
});
		