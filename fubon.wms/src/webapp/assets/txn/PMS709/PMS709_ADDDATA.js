/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS709_ADDDATAController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter,$q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS709_ADDDATAController";
		/*****CHECK 年月  客戶ID*****/
	    $scope.checkData = function() {
	    	$(".redput").css("border-color","");
	    	if($scope.parameterTypeEditForm.$invalid){
		    	$scope.showErrorMsg('欄位檢核錯誤:請重新檢查必填欄位信息');
		    	$(".redput:required:invalid").css("border-color","red");
		    	return;
		    }
	    	if($.trim($scope.inputVO.fch_AO_CODE) == $.trim($scope.inputVO.ref_AO_CODE)){
	    		$(".redput.two").css("border-color","red"); //eq(3)
	    		$(".redput.five").css("border-color","red"); 
		    	$scope.showErrorMsg('FCH AO_CODE不可等於轉介後AO_CODE');
		    	return;
		    }
	    		
	    	$scope.returnFlag="";
	    	var deferred = $q.defer();
	    	$scope.sendRecv("PMS709", "checkData", "com.systex.jbranch.app.server.fps.pms709.PMS709InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
//							return;
						}
						else {
							deferred.resolve();
							if(tota[0].body.errorMessage!=''){
								$scope.returnFlag=tota[0].body.errorMessage;   //關掉訊息可以儲存
								if($scope.returnFlag.indexOf("年月該客戶ID")>-1)
									$(".redput.one").css("border-color","red");   //年月該客戶ID
								if($scope.returnFlag.indexOf("FCH")>-1)
									$(".redput.two").css("border-color","red"); //eq(3)   //FCH_AOCODE
								if($scope.returnFlag.indexOf("轉介後分行代碼")>-1)
									$(".redput.four").css("border-color","red"); //eq(3)  //轉介後分行代碼
								if($scope.returnFlag.indexOf("轉介後 AO_CODE")>-1)
									$(".redput.five").css("border-color","red"); //eq(3)  //轉介後 AO_CODE
								
							}else{
								$scope.inputVO.FCH_BRANCH_NBR=tota[0].body.FCH_BRANCH_NBR;
								$scope.inputVO.FCH_AO_DATE=tota[0].body.FCH_AO_DATE;
								$scope.inputVO.FCH_EMP_ID=tota[0].body.FCH_EMP_ID;   // FCH_EMP_ID
								$scope.inputVO.REF_EMP_ID=tota[0].body.REF_EMP_ID;   // REF_EMP_ID
								console.log($scope.inputVO);
							}
							
						}
	    	});
	    	return deferred.promise;	
	    }
		$scope.init = function() {
			$scope.inputVO = 
			{
					userId:projInfoService.getUserID(),
					cust_ID:'',   //客戶id
					yearMon:''	  //年月
						
			};
			$scope.largeAgrList = [];
			$scope.returnFlag=true;
		};
		$scope.init();
		
	// 报表年月初始化方法
	$scope.yMonth = function(){
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
		var mm = NowDate.getMonth() + 1;
		var strmm = '';
		var xm = '';
		$scope.mappingSet['timeE'] = [];
		for (var i = 0; i < 12; i++) {
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
			mm = mm - 1;
		} 
	};
	$scope.yMonth();
	    
	 //新增數據
	$scope.add = function() {
			
		$scope.checkData().then(function(data)  {
	    	if($scope.returnFlag!=''){
	    		$scope.showErrorMsg($scope.returnFlag);
		    	return; 
	    	}
			$scope.sendRecv("PMS709", "addData", "com.systex.jbranch.app.server.fps.pms709.PMS709InputVO", $scope.inputVO,
				function(tota, isError) {
						if (isError) {
							//$scope.showErrorMsg("ehl_01_common_008");
						return;
						}else{
							$scope.showMsg("ehl_01_common_001");
							$rootScope.inputVO = $scope.inputVO;
							$scope.closeThisDialog('cancel1');
						}
					}
			);
		});
	 };
	    
	 //新增下一筆數據 
	 $scope.addnext = function() {
	    	$scope.checkData().then(function(data)  {
	    		if($scope.returnFlag!=''){
	    			$scope.showErrorMsg($scope.returnFlag);
		    		return; 
	    		}
		    	if($scope.parameterTypeEditForm.$invalid){
		    		$scope.showErrorMsg('欄位檢核錯誤:請重新檢查必填欄位信息');
		    		return;
		    	}
		    	
				$scope.sendRecv("PMS709", "addData", "com.systex.jbranch.app.server.fps.pms709.PMS709InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								//$scope.showErrorMsg("ehl_01_common_008");
								return;
							}else{
								$scope.showMsg("ehl_01_common_001");
								//初始化新增窗口
								/*$rootScope.inputVO = $scope.inputVO;
								$scope.closeThisDialog('cancel2');*/
								$scope.init();
							};
						}
				);
	    	});	
	   };
	    
	 //獲取詳細數據
	    $scope.detail = function() {
	    	$scope.inputVO.yearMon       = $scope.yearMon;
	    	$scope.inputVO.cust_ID       = $scope.cust_ID;
	    	$scope.inputVO.fch_AO_CODE   = $scope.fch_AO_CODE;
	    	$scope.inputVO.fch_EMP_name  = $scope.fch_EMP_name;
	    	$scope.inputVO.ref_BRANCH_NO = $scope.ref_BRANCH_NO;
	    	$scope.inputVO.ref_AO_CODE   = $scope.ref_AO_CODE;
	    	$scope.inputVO.ref_EMP_NAME  = $scope.ref_EMP_NAME;
		};
		$scope.detail();
	    
	 //編輯數據
	    $scope.edit = function() {
	    	$scope.checkData().then(function(data)  {
	    		if($scope.returnFlag!=''){
	    			$scope.showErrorMsg($scope.returnFlag);
		    		return; 
	    		}
	    		if($scope.parameterTypeEditForm.$invalid){
		    		$scope.showErrorMsg('欄位檢核錯誤:請重新檢查必填欄位信息');
		    		return;
		    	}
				$scope.sendRecv("PMS709", "editData", "com.systex.jbranch.app.server.fps.pms709.PMS709InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								//$scope.showErrorMsgInDialog(tota.body.msgData);
								return;
							}
							if (tota[0].body.flag>0) {
								$scope.showMsg("ehl_01_common_006");
								$scope.closeThisDialog('cancel1');
							};
						}
				);
	    	});			
	    };
	    
	   
	    
	    
});