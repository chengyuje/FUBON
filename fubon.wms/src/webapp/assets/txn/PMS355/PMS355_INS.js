'use strict';
eSoafApp.controller('PMS355_INSController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $timeout, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS355_INSController";
	
	$scope.init = function() {
		$scope.inputVO = {
			sTime : $scope.yearMon
		};
		
		$scope.largeAgrList = [];
	};
	$scope.init();
	    
	//import xls
	$scope.query = function() {	
		$scope.sendRecv("PMS355", "countsInsCust", "com.systex.jbranch.app.server.fps.pms355.PMS355InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {								
				if(tota[0].body.custList.length == 0) {
					$scope.custList = [];
					$scope.showMsg("ehl_01_common_009");							
					return;
				}
				
				$scope.custList = tota[0].body.custList;
			}					
		});
	};
	$scope.query();
	
	$scope.updfile = function(){
		var sTime = $scope.inputVO.sTime;
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS355/PMS355_INS_UPD.html',
			className: 'PMS355_INS_UPD',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.yearMon = sTime;
				$scope.uploadName = 'importCustData';
	        }]
		});
		
		dialog.closePromise.then(function(data) {
			if(data.value == 'cancel'){
			}
		});
		
	}
	
	// 下載
	$scope.downLoad = function() {
		$scope.sendRecv("PMS355", "downLoad", "com.systex.jbranch.app.server.fps.pms355.PMS355OutputVO", {'csvList': $scope.csvList}, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
			}
		});
	}
});