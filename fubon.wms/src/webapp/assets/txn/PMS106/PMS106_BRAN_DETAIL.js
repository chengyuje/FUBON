/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS106_BRAN_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS106_BRAN_DETAILController";
	
		$scope.init = function(){
			$scope.inputVO = {
				dataMonth: '',
				br_id: '',
				ao_code: '',
				srchType:''
        	};	
			$scope.strYrMn = ($scope.row.YEARMON).substring(0,4)+'/'+($scope.row.YEARMON).substring(4,6);
        };
        $scope.init();
	    
        /*** 查詢資料 ***/
		$scope.query = function(){
			$scope.inputVO.sCreDate = $scope.row.YEARMON;
			$scope.inputVO.region_center_id = $scope.row.REGION_CENTER_ID;
			$scope.inputVO.branch_area_id = $scope.row.BRANCH_AREA_ID;
			$scope.inputVO.branch_nbr = $scope.row.BRANCH_NBR;		
			$scope.inputVO.ao_code = $scope.row.AO_CODE;
			if($scope.row.BRANCH_NBR==null){
				$scope.inputVO.ao_code='000';
			}
			$scope.inputVO.srchType = '2';
			$scope.sendRecv("PMS106", "queryData", "com.systex.jbranch.app.server.fps.pms106.PMS106InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}														
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;							
							return;
						}						
			});
		};
		$scope.query();
               
		/** 商品種類代碼 --> 商品種類名稱 **/
		var vo = {'param_type': 'FPS.PROD_TYPE', 'desc': false};
        if(!projInfoService.mappingSet['FPS.PROD_TYPE']) {
        	$scope.requestComboBox(vo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['FPS.PROD_TYPE'] = totas[0].body.result;
        			$scope.mappingSet['FPS.PROD_TYPE'] = projInfoService.mappingSet['FPS.PROD_TYPE'];        		
        		}
        	});
        } else
        	$scope.mappingSet['FPS.PROD_TYPE'] = projInfoService.mappingSet['FPS.PROD_TYPE'];
        
        //專員細節查詢
        $scope.showDetail = function(row, type){
        	var templ, clzName = '';
        	if(type == 'FCD'){
        		templ = 'assets/txn/PMS106/PMS106_FCD_DETAIL.html';
        		clzName = 'PMS106_FCD_DETAIL';
        	}
        	else if(type == 'BOND'){
        		templ = 'assets/txn/PMS106/PMS106_BOND_DETAIL.html';
        		clzName = 'PMS106_BOND_DETAIL';
        	}
        	else if(type == 'NEWINS'){
        		templ = 'assets/txn/PMS106/PMS106_NEWINS_DETAIL.html';
        		clzName = 'PMS106_NEWINS_DETAIL';
        	}
        	else if(type == 'ACUMINS'){
        		templ = 'assets/txn/PMS106/PMS106_ACUMINS_DETAIL.html';
        		clzName = 'PMS106_ACUMINS_DETAIL';
        	}
        	else if(type == 'GI'){
        		templ = 'assets/txn/PMS106/PMS106_GI.html';
        		clzName = 'PMS106_GI';
        	}
        	else
        		return;
        	
        	var dialog = ngDialog.open({
				template: templ,
				className: clzName,					
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
            });
        };
        
});
