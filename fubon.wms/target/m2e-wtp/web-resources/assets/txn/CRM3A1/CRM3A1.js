/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3A1Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "CRM3A1Controller";
		
		$scope.init = function() {		
			let defaultDate = new Date();
			$scope.inputVO = {
					PRJ_ID 	: undefined,
            		CUST_ID : '',
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
			
			//參數
			$scope.sendRecv("CRM3A1","init","com.systex.jbranch.app.server.fps.crm3a1.CRM3A1InputVO",
        			$scope.inputVO,function(tota,isError){
        				if(!isError){
        					$scope.mappingSet['PRJ_ID'] = tota[0].body.PRJIDList;
        				}
        	});
			
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
            
        	$scope.sendRecv("CRM3A1","query","com.systex.jbranch.app.server.fps.crm3a1.CRM3A1InputVO",
        			$scope.inputVO,function(tota,isError){
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
    	$scope.edit = function(row) {
    		var dialog = ngDialog.open({
    			template : 'assets/txn/CRM3A1/CRM3A1_EDIT.html',
    			className : 'CRM3A1',
    			showClose : false,
    			controller : [ '$scope', function($scope) {
    				$scope.row = row;
    			} ]
    		});
    		dialog.closePromise.then(function(data) {
    			if (data.value === 'successful') {
    				$scope.inquireInit();
    				$scope.query();
    			}
    		});
    	};
    	
    	//下載檔案
		$scope.download = function(row) {
			$scope.inputVO.seq = row.SEQ;		
				$scope.sendRecv("CRM3A1", "download", "com.systex.jbranch.app.server.fps.crm3a1.CRM3A1InputVO", $scope.inputVO,
					function (tota, isError) {
						if (!isError) {
							if (tota[0].body.downloadList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								return;
							}
							$scope.outputVO = tota[0].body.downloadList[0];
							$scope.fileType = tota[0].body.fileType;
							$scope.fileRealName = tota[0].body.fileRealName;
							debugger;
							$scope.file = new Uint8Array($scope.outputVO.DOWNLOAD_FILE);
							var file = new Blob([$scope.file], { type: 'text/' + $scope.fileType });
							debugger;
							saveAs(file, $scope.fileRealName + '.' + $scope.fileType);
							return;
						}
					}
				);
			
		}
        
});