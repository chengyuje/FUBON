'use strict';
eSoafApp.controller('PMS701Controller', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS701Controller";

	$scope.init = function() {
		$scope.inputVO = {
		};
		$scope.largeAgrList = [];
	};
	$scope.init();
	$scope.cleartype = function(){
		$scope.largeAgrList = [];
	}
	$scope.mappingSet['ctype'] = [];
	$scope.mappingSet['ctype'].push(
			{LABEL: 'RM轉介客戶',    DATA: '1'},
			{LABEL: '個金轉介法金客戶', DATA: '2'},
			{LABEL: '加碼專案客戶',    DATA: '3'},
			{LABEL: '特定排除客戶',    DATA: '4'}
	)

	// 报表年月初始化方法
	$scope.yMonth = function(){
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
	$scope.yMonth();

	/** 查詢 * */
	$scope.query = function() {
		if($scope.parameterTypeEditForm.$invalid){
    		$scope.showErrorMsg('欄位檢核錯誤:欄位計績年月和名單類型必選');
    		return;
    	}
		$scope.sendRecv("PMS701", "queryData",
				"com.systex.jbranch.app.server.fps.pms701.PMS701InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						if (tota[0].body.largeAgrList == null
								|| tota[0].body.largeAgrList.length == 0) {
							$scope.largeAgrList = [];
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.largeAgrList = tota[0].body.largeAgrList;
						$scope.outputVO = tota[0].body;
					}
				});
	}
	
	
	/** 上傳檔案窗口彈出 **/
	$scope.adddata = function(inputVO){
		
		if($scope.parameterTypeEditForm.$invalid){
    		$scope.showErrorMsg('欄位檢核錯誤:欄位計績年月和名單類型必選');
    		return;
    	}
	
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS701/PMS701_UPLOAD.html',
			className: 'PMS701_UPLOAD',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.yearMon   = inputVO.yearMon;
				$scope.cust_Type = inputVO.cust_Type;
            }]
		});
		
		//關閉子界面時，刷新主界面
    	dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel1'){
    			 $scope.query();
				}
    	});
	}

});