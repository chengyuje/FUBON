/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS716Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS716Controller";

		//初始化
		$scope.init = function(){
			$scope.inputVO = {
					sTime:''
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
        
        $scope.queryInsure = function() {
        	$scope.sendRecv("PMS716", "queryInsureDetail", "com.systex.jbranch.app.server.fps.pms716.PMS716InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.len1 = false;
	                			return;
	                		}
							$scope.len1 = true;
							$scope.resultList = tota[0].body.resultList;
							$scope.flag1 = $scope.resultList[0].UPDATE_DATE.substring(0,10);
							return;
						}
			});
			$scope.sendRecv("PMS716", "queryInsureYe", "com.systex.jbranch.app.server.fps.pms716.PMS716InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.len2 = false;
	                			return;
	                		}
							$scope.len2 = true;
							$scope.resultList = tota[0].body.resultList;
							$scope.flag2 = $scope.resultList[0].UPDATE_DATE.substring(0,10);
							return;
						}
			});
        }
        
        $scope.queryInsure();
        
        //查詢功能
        $scope.query = function(){
        	if($scope.inputVO.sTime==null||$scope.inputVO.sTime==''){
    		$scope.showErrorMsg('欄位檢核錯誤:參數月份必填');
    		return;
        	}
			$scope.sendRecv("PMS716", "queryData", "com.systex.jbranch.app.server.fps.pms716.PMS716InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.ifShow = false;
		            			return;
		            		}
							$scope.ifShow = true;
							$scope.resultList = tota[0].body.resultList;
							$scope.setVar();
							return;
						}
			});
			$scope.queryInsure();
        }
        
        //轉化為需要的值
        $scope.setVar = function(){
        	//參數設置
        	$scope.minorEntEmp = $scope.resultList[0].MINOR_ENT_EMP;
        	$scope.minorEntBranch = $scope.resultList[0].MINOR_ENT_BRANCH;
        	$scope.dcEmp = $scope.resultList[0].DC_EMP;
        	$scope.dcBranch = $scope.resultList[0].DC_BRANCH;
        	$scope.rests = $scope.resultList[0].RESTS;
        	if($scope.minorEntEmp=='0'){
        		$scope.minorEntEmp='未設定';
        	}else{
        		$scope.minorEntEmp='已設定';
        	}
        	if($scope.minorEntBranch=='0'){
        		$scope.minorEntBranch='未設定';
        	}else{
        		$scope.minorEntBranch='已設定';
        	}
        	if($scope.dcEmp=='0'){
        		$scope.dcEmp='未設定';
        	}else{
        		$scope.dcEmp='已設定';
        	}
        	if($scope.dcBranch=='0'){
        		$scope.dcBranch='未設定';
        	}else{
        		$scope.dcBranch='已設定';
        	}
        	if($scope.rests=='0'){
        		$scope.rests='未設定';
        	}else{
        		$scope.rests='已設定';
        	}
        }
        //跳轉到設置頁面
        $scope.setInsureDetail = function(){
            var dialog = ngDialog.open({
                template: 'assets/txn/PMS716/PMS716_insureDetail.html',
                className: 'PMS716_insureDetail',
                showClose : true
            });
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.queryInsure();
 				}
        	});
        };
        $scope.setInsureYe = function(){
            var dialog = ngDialog.open({
                template: 'assets/txn/PMS716/PMS716_insureYe.html',
                className: 'PMS716_insureYe',
                showClose : true
            });
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.queryInsure();
 				}
        	});
        };
        //參數1
        $scope.setMinEmp = function(sTime){
            var dialog = ngDialog.open({
                template: 'assets/txn/PMS716/PMS716_minEmp.html',
                className: 'PMS716_minEmp',
                showClose : true,
                controller: ['$scope', function($scope) {
                	$scope.sTime = sTime;
                }]
            });
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
      //參數2
        $scope.setMinBran = function(sTime){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS716/PMS716_minBran.html',
        		className: 'PMS716_minBran',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.sTime = sTime;
        		}]
        	});
			//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.query();
 				}
        	});
        };
        //參數3
		$scope.setDcEmp = function(sTime) {
			var dialog = ngDialog.open({
	    		template: 'assets/txn/PMS716/PMS716_dcEmp.html',
				className: 'PMS716_dcEmp',
	    		showClose : true,
	    		controller: ['$scope', function($scope) {
					$scope.sTime = sTime;
	            }]
	    	});
			//關閉子界面時，刷新主界面
	    	dialog.closePromise.then(function(data) {
	    		if(data.value == 'cancel'){
	    			 $scope.query();
					}
	    	});
		}
        
        //理專參數4
        $scope.setDcBran = function(sTime){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS716/PMS716_dcBran.html',
        		className: 'PMS716_dcBran',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.sTime = sTime;
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
        $scope.setRests = function(sTime){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PMS716/PMS716_rests.html',
        		className: 'PMS716_rests',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.sTime = sTime;
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