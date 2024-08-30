/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS707Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS707Controller";

		//初始化
		$scope.init = function(){
			$scope.inputVO = {
					docYearMon:'',
					userId:projInfoService.getUserID()
			};
			$scope.resultList = [];
		}
		$scope.init();
		$scope.ifShow = false;
		
		
		//月份選擇下拉框
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<12; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
    

        
        //查詢功能
        $scope.query = function(){
        	if($scope.inputVO.docYearMon==null||$scope.inputVO.docYearMon==''){
    		$scope.showErrorMsg('欄位檢核錯誤:參數月份必填');
    		return;
        	}
        	$scope.inputVO.yearMon = $scope.inputVO.docYearMon;
			$scope.sendRecv("PMS707", "queryData", "com.systex.jbranch.app.server.fps.pms707.PMS707InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.ifShow = false;
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.ifShow = true;
							$scope.resultList = tota[0].body.resultList[0];
							$scope.setVar();
							return;
						}
			});
        }
        
        
        //轉化為需要的值
        $scope.setVar = function(){
        	//參數設置
        	$scope.ParLimit_State = $scope.resultList.PAR_LIMIT.substring(0,1);
        	$scope.ParLimit_IfAuto = $scope.resultList.PAR_LIMIT.substring(1,2);
        	if($scope.ParLimit_State=='0'){
        		$scope.ParLimit_State='未設定';
        	}else{
        		$scope.ParLimit_State='已設定';
        	}
        	
        	$scope.ParRate_State = $scope.resultList.PAR_RATE.substring(0,1);
        	$scope.ParRate_IfAuto = $scope.resultList.PAR_RATE.substring(1,2);
        	if($scope.ParRate_State=='0'){
        		$scope.ParRate_State='未設定';
        	}else{
        		$scope.ParRate_State='已設定';
        	}
        	
        	$scope.ParBounty_State = $scope.resultList.PAR_BOUNTY.substring(0,1);
        	$scope.ParBounty_IfAuto = $scope.resultList.PAR_BOUNTY.substring(1,2);
        	if($scope.ParBounty_State=='0'){
        		$scope.ParBounty_State='未設定';
        	}else{
        		$scope.ParBounty_State='已設定';
        	}
        	
        	$scope.ParProj_State = $scope.resultList.PAR_PROJ.substring(0,1);
        	$scope.ParProj_IfAuto = $scope.resultList.PAR_PROJ.substring(1,2);
        	if($scope.ParProj_State=='0'){
        		$scope.ParProj_State='未設定';
        	}else{
        		$scope.ParProj_State='已設定';
        	}
        	
        	$scope.ParBonusAdj_State = $scope.resultList.PAR_BONUS_ADJ.substring(0,1);
        	$scope.ParBonusAdj_IfAuto = $scope.resultList.PAR_BONUS_ADJ.substring(1,2);
        	if($scope.ParBonusAdj_State=='0'){
        		$scope.ParBonusAdj_State='未設定';
        	}else{
        		$scope.ParBonusAdj_State='已設定';
        	}
        	
        	$scope.ParPsList_State = $scope.resultList.PAR_PS_LIST.substring(0,1);
        	$scope.ParPsList_IfAuto = $scope.resultList.PAR_PS_LIST.substring(1,2);
        	if($scope.ParPsList_State=='0'){
        		$scope.ParPsList_State='未設定';
        	}else{
        		$scope.ParPsList_State='已設定';
        	}
        }
        
        
        //儲存修改
        $scope.saveChange = function(){
        	$scope.inputVO.yearMon = $scope.inputVO.docYearMon;
        	$scope.inputVO.YEARMON = $scope.inputVO.docYearMon;
        	$scope.inputVO.PAR_LIMIT = $scope.resultList.PAR_LIMIT.substring(0,1) +　$scope.ParLimit_IfAuto;
        	$scope.inputVO.PAR_RATE = $scope.resultList.PAR_RATE.substring(0,1) +　$scope.ParRate_IfAuto;
        	$scope.inputVO.PAR_BOUNTY = $scope.resultList.PAR_BOUNTY.substring(0,1) +　$scope.ParBounty_IfAuto;
        	$scope.inputVO.PAR_PROJ = $scope.resultList.PAR_PROJ.substring(0,1) +　$scope.ParProj_IfAuto;
        	$scope.inputVO.PAR_BONUS_ADJ = $scope.resultList.PAR_BONUS_ADJ.substring(0,1) +　$scope.ParBonusAdj_IfAuto;
        	$scope.inputVO.PAR_PS_LIST = $scope.resultList.PAR_PS_LIST.substring(0,1) +　$scope.ParPsList_IfAuto;
        	
        	var f = 0;
        	for(var i in $scope.resultList){
        		if($scope.resultList[i]!=$scope.inputVO[i]){
        			f = 1;
        		}
        	}
        	if(f==0){
        		return;//不調用saveChange方法
        	}
        	
			$scope.sendRecv("PMS707", "saveChange", "com.systex.jbranch.app.server.fps.pms707.PMS707InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.backResult) {
								$scope.showMsg("ehl_01_common_002");
								$scope.query();
	                		}else{
	                			$scope.showMsg("ehl_01_common_007");
	                		}
						}
			});
        }

		
	
	
        //跳轉到設置頁面
        //參數4
		$scope.setParLimit = function(yearMon) {
			var dialog = ngDialog.open({
	    		template: 'assets/txn/PMS707/PMS707_LimitReward.html',
				className: 'PMS707_LimitReward',
	    		showClose : true,
	    		controller: ['$scope', function($scope) {
					$scope.yearMon = yearMon;
	            }]
	    	});
			//關閉子界面時，刷新主界面
	    	dialog.closePromise.then(function(data) {
	    		if(data.value == 'cancel'){
	    			 $scope.query();
					}
	    	});
		}
      //參數1
        $scope.setParRate = function(yearMon){
            var dialog = ngDialog.open({
                template: 'assets/txn/PMS707/PMS707_DeRate.html',
                className: 'PMS707_DeRate',
                showClose : true,
                controller: ['$scope', function($scope) {
                	$scope.yearMon = yearMon;
                }]
            });
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
      //理專參數3
        $scope.setParBounty = function(yearMon){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS707/PMS707_DedReward.html',
        		className: 'PMS707_DedReward',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.yearMon = yearMon;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //理專參數2
        $scope.setParProj = function(yearMon){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS707/PMS707_ParProj.html',
        		className: 'PMS707_ParProj',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.yearMon = yearMon;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //理專參數5
        $scope.setParBonusAdj = function(yearMon){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS707/PMS707_ParBonusAdj.html',
        		className: 'PMS707_ParBonusAdj',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.yearMon = yearMon;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        
        //理專參數6
        $scope.setParPsList = function(yearMon){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS707/PMS707_ParPsList.html',
        		className: 'PMS707_ParPsList',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.yearMon = yearMon;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
});