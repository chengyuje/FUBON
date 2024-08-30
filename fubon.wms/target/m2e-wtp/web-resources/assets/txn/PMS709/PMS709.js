'use strict';
eSoafApp.controller('PMS709Controller', function($rootScope, $scope,
		$controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS709Controller";

	$scope.init = function() {
		$scope.inputVO = {
			sTime : '',
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
			
			if (mm == 0) {
				mm = 12;
				yr = yr-1;
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

	/** 查詢 * */
	$scope.query = function() {
		if($scope.inputVO.yearMon==''||$scope.inputVO.yearMon==null){
    		$scope.showErrorMsg('欄位檢核錯誤:欄位轉介年月必選');
    		return;
    	}
		$scope.sendRecv("PMS709", "queryData",
				"com.systex.jbranch.app.server.fps.pms709.PMS709InputVO",
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
	
	/** 刪除 * */
	$scope.deletedata = function(index) {
		/*if(!confirm('確定刪除該筆資料？')){
			return;}*/
		$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
		$scope.inputVO.cust_ID = $scope.largeAgrList[index].CUST_ID;
		$scope.largeAgrList.splice(index,1);
		$scope.sendRecv("PMS709", "deleteData", "com.systex.jbranch.app.server.fps.pms709.PMS709InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota[0].body.flag>0) {
						$scope.showMsg("ehl_01_common_003");
						$scope.inputVO.cust_ID = '';
						/*$scope.query();*/
					};
				});
		})
    };
	
	/** 新增數據窗口彈出 **/
	$scope.adddata = function(){
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS709/PMS709_ADDDATA.html',
			className: 'PMS709_ADDDATA',
			showClose: false,
			controller: ['$scope', function($scope) {
            }]
		});
		dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel1'){
    			$scope.inputVO.yearMon = $rootScope.inputVO.yearMon;
    			$scope.query();
				}
    		/*if(data.value == 'cancel2'){
    			$scope.inputVO.yearMon = $rootScope.inputVO.yearMon;
    			$scope.query();
    			$scope.adddata();
				}*/
    	});
	}
	
	/** 編輯數據窗口彈出 **/
	$scope.editdata = function(row){
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS709/PMS709_EDITDATA.html',
			className: 'PMS709_EDITDATA',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.yearMon       = row.FCH_REF_YEARMON;
				$scope.cust_ID       = row.CUST_ID;
				$scope.fch_AO_CODE   = row.FCH_AO_CODE;
				$scope.fch_EMP_name    = row.FCH_EMP_NAME;
				$scope.ref_BRANCH_NO = row.REF_BRANCH_NO;
				$scope.ref_AO_CODE   = row.REF_AO_CODE;
				$scope.ref_EMP_NAME  = row.REF_EMP_NAME;
            }]
		});
		dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel1'){
    			 $scope.query();
				}
    	});
	}
});