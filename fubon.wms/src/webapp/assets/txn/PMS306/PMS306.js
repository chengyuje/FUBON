/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS306Controller', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS306Controller";
	
	getParameter.XML(['CMMGR005.STATUS','CMMGR005.RESULT'], function(totas) {
		if(len(totas)>0){
			$scope.mappingSet['CMMGR005.STATUS'] = totas.data[totas.key.indexOf('CMMGR005.STATUS')];
			$scope.mappingSet['CMMGR005.RESULT'] = totas.data[totas.key.indexOf('CMMGR005.RESULT')];			
		}
	});
    
    
	
	/******時間控制******/

	// date picker
	$scope.ivgStartDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	$scope.ivgEndDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	// config
	$scope.model = {};
	
	$scope.open = function($event, elementOpened) {		
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.ivgStartDateOptions.maxDate = $scope.inputVO.EDate || $scope.maxDate;
		$scope.ivgEndDateOptions.minDate = $scope.inputVO.SDate || $scope.minDate;
	};
	// date picker end
	
	
	$scope.reset = function(){
		$scope.sendRecv("PMS306", "reset", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);   //顯示body抓到錯誤訊息
				return;
			}
			
			$scope.showMsg("已重算");
			return;	
		});
	}
	
	$scope.reset_base = function(){
		$scope.sendRecv("PMS306", "resetBase", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);   //顯示body抓到錯誤訊息
				return;
			}
			
			$scope.showMsg("已重算");
			return;	
		});
	}
	
	
	
	
	/***********初始init*************/
	$scope.init = function() {
		$scope.inputVO = {
				SDate:undefined,
				EDate:undefined,
				
		};
		$scope.outputVO={};
	};
	$scope.init();
	
	$scope.inquire = function() {
		$scope.sendRecv("PMS306", "query", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);   //顯示body抓到錯誤訊息
				return;
			}
			if (tota.length > 0) {
				if(tota[0].body.pislist.length == 0) {
					$scope.pislist = [];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				$scope.pislist = tota[0].body.pislist;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	
		
	
	
	
});
