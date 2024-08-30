'use strict';
eSoafApp.controller('PMS708Controller', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS708Controller";

	$scope.init = function() {
		$scope.inputVO = {
			sTime : '',
			modelName : ''
		};
		$scope.largeAgrList = [];
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
		$scope.inputVO.yearMon = $scope.inputVO.sTime;
		if($scope.parameterTypeEditForm.$invalid){
    		$scope.showErrorMsg('欄位檢核錯誤:欄位查詢年月必選');
    		return;
    	}
		$scope.sendRecv("PMS708", "queryData",
				"com.systex.jbranch.app.server.fps.pms708.PMS708InputVO",
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
	
	/** 上傳窗口彈出 **/
	$scope.upload = function(sTime){
		
		/*if($scope.parameterTypeEditForm.$invalid){
    		$scope.showErrorMsg('請選擇正確上傳檔案的年月');
    		return;
    	}*/
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS708/PMS708_UPLOAD.html',
			className: 'PMS708_UPLOAD',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.yearMon = sTime;
            }]
		});
		
		//關閉子界面時，刷新主界面
    	dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel1'){
    			 $scope.query();
				}
    	});
	}
	/**
	 * 下載
	 */
	$scope.downLoad = function() {
		$scope.sendRecv("PMS708", "downLoad", "com.systex.jbranch.app.server.fps.pms708.PMS708OutputVO", {
			 'csvList': $scope.csvList}, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	}
});