/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS423_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS423_UPLOADController";
		
		$scope.queryDataDate = function() {	
			$scope.sendRecv("PMS423", "queryDataDate", "com.systex.jbranch.app.server.fps.pms423.PMS423InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.mappingSet['DATE_LIST'] = [];
						angular.forEach(tota[0].body.resultList, function(row) {
							$scope.mappingSet['DATE_LIST'].push({LABEL: row.YYYYMM.substring(0,4)+'/'+row.YYYYMM.substring(4,6), DATA: row.YYYYMM});
	        			});
						return;
					}
			});
		};
        
        $scope.init = function(){
        	$scope.queryDataDate();
			$scope.inputVO = {
					data_date : '',
					fileName  : '',
					realfileName:''
        	};
			$scope.outputVO = [];
			$scope.resultList = [];
		};
		$scope.init();
		
		//下載範例
		$scope.downloadExm = function() {
			$scope.sendRecv("PMS423", "getExample", "com.systex.jbranch.app.server.fps.pms423.PMS423InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg("下載成功");
						return;
					}
			});
		};
		
		/** 查詢資料 **/
		$scope.query = function(active){
			if($scope.inputVO.data_date == ''){
				$scope.showErrorMsg('請選擇資料年月');
				return;
			}
			$scope.sendRecv("PMS423", "queryGoalData", "com.systex.jbranch.app.server.fps.pms423.PMS423InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.resultList = tota[0].body.resultList;
						return;
					}						
			});
		};
		
		/** 刪除資料 **/
		$scope.delete = function(){
			if($scope.inputVO.data_date == ''){
				$scope.showErrorMsg('請選擇資料年月');
				return;
			}
			$scope.sendRecv("PMS423", "deleteGoalData", "com.systex.jbranch.app.server.fps.pms423.PMS423InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg('刪除成功');
						$scope.query();
					}						
			});
		}

		$scope.uploadFinshed = function(name, rname) {
	    	$scope.inputVO.fileName = name;
        	$scope.inputVO.realfileName = rname;
        };
		
		$scope.insertExpt = function(set_name) {
			if($scope.inputVO.fileName == undefined || $scope.inputVO.fileName == ''){
	    		$scope.showErrorMsg('未選擇上傳檔案');
        		return;
        	}
			var data = {
				success : 'success'
			}
			$scope.sendRecv("PMS423", "uploadFile", "com.systex.jbranch.app.server.fps.pms423.PMS423InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg(tota[0].body.successMsg);
//                		$scope.closeThisDialog(data);
						$scope.init();
					}
			});
		};
});
