/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM131_USRPTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $q, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "CRM131_USRPTController";

		getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE"], function(totas) {
			if (totas) {
				$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
			}
		});
		// init
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.pri_id = projInfoService.getPriID()[0];
			$scope.priList = ['012','013','033','045']; //營運督導、業務處處長、管銷科助理與總行限制一定要選擇分行別條件，避免查詢過久
			$scope.regionOBJ = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "ORI_AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.regionOBJ);
		};
		$scope.init();
						
		$scope.inquire = function() {
			
			$scope.resultList = [];
			
			$scope.tabType = $scope.connector('get','tab');
			if (typeof($scope.tabType) === 'undefined') {
				$scope.tabType = '1';
			}
			if($scope.tabType == '1'){
				$scope.inputVO.TAKE_CARE_DUE_YN = 'Y';
				$scope.inputVO.TAKE_CARE_MATCH_YN = '';
			}else{
				$scope.inputVO.TAKE_CARE_DUE_YN = '';
			}
			if($scope.tabType == '2'){
				$scope.inputVO.TAKE_CARE_DUE_YN = '';
				$scope.inputVO.TAKE_CARE_MATCH_YN = 'Y';
			}else{
				$scope.inputVO.TAKE_CARE_MATCH_YN = '';
			}			
			
			$scope.sendRecv("CRM131", "getUnderservRpt", "com.systex.jbranch.app.server.fps.crm131.CRM131InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
//						debugger
						$scope.resultList = tota[0].body.resultList5;
						$scope.outputVO = tota[0].body;
//						console.log("tota="+JSON.stringify(tota[0].body.resultList5));
					}
			});
		};
		$scope.inquire();
		
		$scope.getAolist = function() {
			$scope.sendRecv("CRM131", "ao_inquire", "com.systex.jbranch.app.server.fps.crm131.CRM131InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.AO_LIST = [];
							$scope.inputVO.ao_code = '';
							angular.forEach(tota[0].body.ao_list, function(row) {
								if(row.TYPE == '1')
	    							$scope.AO_LIST.push({'LABEL': (row.EMP_NAME+'(主Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE, 'BRANCH': row.BRANCH_NBR, 'AREA': row.BRANCH_AREA_ID, 'CENTER': row.REGION_CENTER_ID});
	    						else if(row.TYPE == '2')
	    							$scope.AO_LIST.push({'LABEL': (row.EMP_NAME+'(副Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE, 'BRANCH': row.BRANCH_NBR, 'AREA': row.BRANCH_AREA_ID, 'CENTER': row.REGION_CENTER_ID});
	    						// mantis 3901 請移除下拉選單有(維護) CODE的選項 //#5649加回來，可移入任何CODE
	    						else if(row.TYPE == '3')
	    							$scope.AO_LIST.push({'LABEL': (row.EMP_NAME+'(維護Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE, 'BRANCH': row.BRANCH_NBR, 'AREA': row.BRANCH_AREA_ID, 'CENTER': row.REGION_CENTER_ID});
							});
							
							$scope.AO_LIST = $filter('orderBy')($scope.AO_LIST, 'TYPE', false);
						}
					}
				);
		};
		$scope.getAolist();
			
		//字串分成[]
	    $scope.comma_split = function(value) {
	    	return value.split('@');
	    };
	    
	    $scope.custConnectData = function(row, tabType) {
        	var dialog = ngDialog.open({
				template: 'assets/txn/CAM190/CAM190_ROUTE.html',
				className: 'CAM200',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.txnName = "名單執行紀錄";
	        		$scope.routeURL = 'assets/txn/CAM200/CAM200.html';
	        		$scope.connector('set','custID', row.CUST_ID);
	            	$scope.connector('set','custName', row.CUST_NAME);
	                $scope.connector('set','tab', tabType);
	            }]
			});
		};
	    
		 // 連至客戶首頁
        $scope.custDTL = function(row) {
        	$scope.custVO = {
    				CUST_ID :  row.CUST_ID,
    				CUST_NAME :row.CUST_NAME	
    		}
    		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
        	
        	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			var set = $scope.connector("set","CRM610URL",path);
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
		}
       
        //下載清單
        $scope.exportData = function() {
			if($scope.resultList.length == 0){
				$scope.showMsg("無資料可以匯出哦~");
				return;
        	}
			$scope.sendRecv("CRM131", "export", "com.systex.jbranch.app.server.fps.crm131.CRM131InputVO", {'resultList': $scope.resultList},
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
});