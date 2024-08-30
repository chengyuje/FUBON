/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS226Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS226Controller";	
		$scope.init = function(){
			$scope.inputVO = {
					sTime:'',    //初使年月   inputVO必要加  因可視範圍
					dataMonth : '',
					assignType: '1',
					role    : '',
					rptVersion : '',
					REF_ID : '',
					TP_ID  : ''
			};
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
            $scope.paramList = [];
            
            //判斷理專
            var FCvo = {'param_type': 'FUBONSYS.FC_ROLE', 'desc': false};
        	$scope.requestComboBox(FCvo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['FUBONSYS.FC_ROLE'] = totas[0].body.result;
        			for(var key in projInfoService.mappingSet['FUBONSYS.FC_ROLE']){
        	    		if(projInfoService.mappingSet['FUBONSYS.FC_ROLE'][key].DATA == projInfoService.getRoleID()){
        	    			$scope.FCFlag = 'Y';
        	    		}
        	    	}
        		}
	    	})
	    	
			var FCHvo = {'param_type': 'FUBONSYS.FCH_ROLE', 'desc': false};
        	$scope.requestComboBox(FCHvo, function(totas) {      	
        		if (totas[totas.length - 1].body.result === 'success') {        		
        			projInfoService.mappingSet['FUBONSYS.FCH_ROLE'] = totas[0].body.result;
        			for(var key in projInfoService.mappingSet['FUBONSYS.FCH_ROLE']){
        	    		if(projInfoService.mappingSet['FUBONSYS.FCH_ROLE'][key].DATA == projInfoService.getRoleID()){
        	    			$scope.FCHFlag = 'Y';
        	    		}
        	    	}
        		}
	    	})
	    	
        	if($scope.FCFlag=='Y'||$scope.FCHFlag=='Y'){
        		$scope.inputVO.empHistFlag = 'Y'
        	};
		};
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.AREA_LIST = [];
			$scope.REGION_LIST = [];
			$scope.BRANCH_LIST = [];
			$scope.AO_LIST = [];
		}
		$scope.inquireInit();
		$scope.isq = function(){
	    		var NowDate = new Date();
	    		var yr = NowDate.getFullYear();
	    		var mm = NowDate.getMonth() + 1;
	    		var strmm = '';
	    		var xm = '';
	    		$scope.mappingSet['timeE'] = [];
	    		for (var i = 0; i < 12; i++) {
	    			mm = mm - 1;
	    			if (mm == 0) {
	    				mm = 12;
	    				yr = yr - 1;
	    			}
	    			if (mm < 10)
	    				strmm = '0' + mm;
	    			else
	    				strmm = mm;
	    			$scope.mappingSet['timeE'].push({
	    				LABEL : yr + '/' + strmm,
	    				DATA : yr + '' + strmm
	    			});
	    		} 
	    	};
	    	$scope.isq();
	    	
    	 $scope.dateChange = function(){
    		 $scope.inquireInit();
    		 if($scope.inputVO.sTime==''){
    	    		$scope.init();
    	    		return;
    	    	}
	    	$scope.inputVO.reportDate = $scope.inputVO.sTime;
   	    	$scope.RegionController_getORG($scope.inputVO);
   	    };
   	    
    	/**
		 * 匯出
		 */
    	$scope.exportRPT = function(){
			$scope.sendRecv("PMS226", "export",	"com.systex.jbranch.app.server.fps.pms226.PMS226OutputVO",{'list':$scope.csvList},
					function(tota, isError) {
						if (!isError) {
							$scope.resultList = tota[0].body.resultList;
							$scope.csvList = tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
						
							return;
						}
					});
			};		
		/**
		 * 查詢
		 */
		$scope.inquire = function(){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.inputVO.region = $scope.inputVO.region_center_id;
			$scope.inputVO.op     = $scope.inputVO.branch_area_id;
			$scope.inputVO.branch = $scope.inputVO.branch_nbr;
			$scope.sendRecv("PMS226", "inquire", "com.systex.jbranch.app.server.fps.pms226.PMS226InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = [];
							$scope.csvList = [];
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							return;
						}else{
							$scope.showBtn = 'none';
						}	
			});
		};
		
		 //選擇報表年月人工上傳按鈕才生效
        $scope.checkTime = function(){
        	var check = true;
        	if($scope.inputVO.sTime != ''){
        		check = false;
        	}
        	return check;
        }
	    
		
		/** 上傳窗口彈出 **/
		$scope.upload = function(sTime){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS226/PMS226_UPLOAD.html',
				className: 'PMS226_UPLOAD',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.sTime = sTime;
	            }]
			});
		}
		
		$scope.toDetail = function(yearMon,index) {
			var custId = $scope.paramList[index].CUST_ID;
    		var dialog = ngDialog.open({
    			template: 'assets/txn/PMS226/PMS226_DETAIL.html',
    			className: 'PMS226_DETAIL',
    			showClose: false,
    			controller: ['$scope', function($scope) {
    				$scope.sTime = yearMon,
    				$scope.custId = custId;
                }]
    		});
   	    }
});