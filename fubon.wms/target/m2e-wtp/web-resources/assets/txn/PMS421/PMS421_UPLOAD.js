/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS421_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS421_UPLOADController";
		
		$scope.mappingSet['month'] = [];
		for(var i = 2;i<13;i++){
			if(i==4 || i==7 || i==10){
				continue;
			}else{
				$scope.mappingSet['month'].push({LABEL: i+'月', DATA:i});
			}
		}
        
        
        $scope.init = function(){			
			$scope.inputVO = {
					PABTH_NAME: 'BTPMS421_AO7Y',
					DATA_DATE : '',
					MONTH     : '',
					EXE_TIME  : undefined,
					EXE_DATE  : '',
					fileName  : '',
					realfileName:''
        	};
			$scope.outputVO = [];
			$scope.ServiceList = [];
		};
	
		$scope.init();
		
		$scope.queryServiceDate = function() {	
			$scope.sendRecv("PMS421", "queryServiceDate", "com.systex.jbranch.app.server.fps.pms421.PMS421InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.mappingSet['DATE_LIST']=[];
						angular.forEach(tota[0].body.DATE_LIST, function(row) {
							$scope.mappingSet['DATE_LIST'].push({LABEL: row.YYYYMM.substring(0,4)+'/'+row.YYYYMM.substring(4,6), DATA: row.YYYYMM});
	        			});
						
						$scope.test = tota[0].body.EXECUTE_TIME[0].CRONEXPRESSION.split(' ');
						
						var date = new Date();
						date.setHours($scope.test[2]);
						date.setMinutes($scope.test[1]);
						date.setSeconds(0);

						$scope.inputVO.EXE_TIME = date;
						$scope.inputVO.EXE_DATE = $scope.test[3];
						
						$scope.inputVO.MONTH = $scope.test[4];
						return;
					}
			});
		};
		$scope.queryServiceDate();
				
		
		//設定批次時間
		$scope.setting = function(){
			if($scope.inputVO.MONTH == '' || $scope.inputVO.MONTH == undefined){
				$scope.showErrorMsg('請選擇執行月份');
				return;
			}
			$scope.inputVO.EXE_DATE = parseInt($scope.inputVO.EXE_DATE).toString();
			$scope.sendRecv("PMS421", "setting", "com.systex.jbranch.app.server.fps.pms421.PMS421InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg("設定成功");
							return;
						}
			});
		};
		
		//下載範例
		$scope.downloadExm = function() {
			$scope.sendRecv("PMS421", "getExample", "com.systex.jbranch.app.server.fps.pms421.PMS421InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg("下載成功");
						return;
					}
			});
		};
		
		/** 查詢資料 **/
		$scope.query = function(active){
			if($scope.inputVO.DATA_DATE == ''){
				$scope.showErrorMsg('請選擇資料年月');
				return;
			}
			$scope.sendRecv("PMS421", "queryServiceData", "com.systex.jbranch.app.server.fps.pms421.PMS421InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.ServiceList = tota[0].body.SERVICE_LIST;
							return;
						}						
			});
		};
		
		/** 刪除資料 **/
		$scope.delete = function(){
			if($scope.inputVO.DATA_DATE == ''){
				$scope.showErrorMsg('請選擇資料年月');
				return;
			}
			$scope.sendRecv("PMS421", "deleteServiceData", "com.systex.jbranch.app.server.fps.pms421.PMS421InputVO", $scope.inputVO,
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
			
			$scope.sendRecv("PMS421", "uploadFile", "com.systex.jbranch.app.server.fps.pms421.PMS421InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.showSuccessMsg(tota[0].body.successMsg);
//                		$scope.closeThisDialog(data);
						$scope.init();
						$scope.queryServiceDate();
					}
			});
		};
});
