'use strict';
eSoafApp.controller('PMS501Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $compile) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS501Controller";
	
	$scope.init = function(){
		$scope.inputVO = {
			FILE_NAME          : '',
			ACTUAL_FILE_NAME   : '',
			exportList         : []
		};
	
		$scope.paramList = [];
	};
	$scope.init();
	
	// 查詢
	$scope.query = function(){
		$scope.totals = {};
		
		$scope.paramList = [];
		$scope.outputVO = [];
		
		$scope.sendRecv("PMS501", "query", "com.systex.jbranch.app.server.fps.pms501.PMS501InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.paramList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;

				return;
			}
		});
	};
	$scope.query();
	
	// 取得範例
	$scope.getExample = function() {
		$scope.sendRecv("PMS501", "getExample", "com.systex.jbranch.app.server.fps.pms501.PMS501InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
			}
		});
	};
	
	// 上傳
	$scope.updFile = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PMS501", "updFile", "com.systex.jbranch.app.server.fps.pms501.PMS501InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				} else {
					$scope.showMsg("ehl_01_common_010");
					$scope.query();
				}
			});
		});
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='updFile(name, rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	// 匯出
	$scope.export = function() {
		angular.copy($scope.paramList, $scope.inputVO.exportList);
		$scope.inputVO.exportList.push($scope.totals);
		
		$scope.sendRecv("PMS501", "export", "com.systex.jbranch.app.server.fps.pms501.PMS501InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
                		if(totas[0].body.paramList && totas[0].body.paramList.length == 0) {
                			$scope.showMsg("ehl_01_common_009");
                			return;
                		}
                	};
				}
		);
	};
	
});
