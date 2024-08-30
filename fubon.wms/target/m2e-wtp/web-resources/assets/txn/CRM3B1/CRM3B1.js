/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3B1Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "CRM3B1Controller";
		
		// combobox
    	getParameter.XML(["COMMON.YES_NO"], function(totas) {
    		if (totas) {
    			$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
    		}
    	});
    	
		$scope.init = function() {		
			let defaultDate = new Date();
			$scope.inputVO = {
            		CUST_ID : '',
            		DEL_YN : 'N', //查詢預設為未刪除資料
	                /** 連動組織會用到的參數 **/
	                region_center_id	: undefined,
	                branch_area_id		: undefined,
	                branch_nbr			: undefined,
	                ao_code             : undefined,
	                reportDate			: $filter('date')(defaultDate, 'yyyyMM')
            };
			
			$scope.outputVO={};
			$scope.paramList = [];
			$scope.inputVO.loginRole = sysInfoService.getRoleID();
			
			//把鎖定清掉
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			
			//登入者可視範圍
			$scope.RegionController_getORG($scope.inputVO);			
		};
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.paramList = [];
			$scope.outputVO = {};
		};
		$scope.inquireInit();
		
		//查詢
        $scope.query = function(){
        	$scope.inputVO.regionList = $scope.REGION_LIST;
			$scope.inputVO.areaList = $scope.AREA_LIST;
            $scope.inputVO.branchList = $scope.BRANCH_LIST;
            $scope.inputVO.aoCodeList = $scope.AO_LIST;
            
        	$scope.sendRecv("CRM3B1","query","com.systex.jbranch.app.server.fps.crm3b1.CRM3B1InputVO", $scope.inputVO,
        		function(tota,isError){
        			if(!isError){
        				if(tota[0].body.resultList.length == 0){
                			$scope.showMsg("ehl_01_common_009");
                   			return;
                		}
                		$scope.paramList = tota[0].body.resultList;
                		$scope.outputVO = tota[0].body;    					
        			}
        	});
        };
        
    	// 修改
    	$scope.add = function() {
    		var dialog = ngDialog.open({
    			template : 'assets/txn/CRM3B1/CRM3B1_EDIT.html',
    			className : 'CRM3B1',
    			showClose : false,
    			controller : [ '$scope', function($scope) {} ]
    		});
    		dialog.closePromise.then(function(data) {
    			$scope.inquireInit();
    			$scope.query();
    		});
    	};    	
        
    	$scope.deleteRow = function(row) {
    		$confirm({text: '是否刪除此筆資料?'}, {size: 'sm'}).then(function() {
	        	$scope.inputVO.SAVE_TYPE = 'DEL';
	        	$scope.inputVO.SEQ_KEY_NO = row.SEQ_KEY_NO;
	        	
	        	$scope.sendRecv("CRM3B1", "edit", "com.systex.jbranch.app.server.fps.crm3b1.CRM3B1InputVO", $scope.inputVO,
						function(tota, isError) {
			        		if (isError) {
			            		$scope.showErrorMsg(tota[0].body.msgData);
			            	} else {
			            		$scope.inquireInit();
			            		$scope.query();
			            	}
				});
    		});
        };
        
        //下載附件
        $scope.download = function(row) {
			$scope.sendRecv("CRM3B1", "download", "com.systex.jbranch.app.server.fps.crm3b1.CRM3B1InputVO", {'SEQ_KEY_NO': row.SEQ_KEY_NO},
				function(totas, isError) {
		        	if (!isError) {
						return;
					}
				}
			);
		};
});