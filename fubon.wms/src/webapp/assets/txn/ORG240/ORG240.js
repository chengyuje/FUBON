/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG240Controller', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG240Controller";
	
	$scope.init = function() {
		$scope.suptSalesTeamLst = [];
		
		$scope.inputVO = {
			SUPT_SALES_TEAM_ID : '',
			FILE_NAME          : '',
			ACTUAL_FILE_NAME   : '',
			SUPT_SALES_TEAM_LST: []
		};
	};
	$scope.init();
	
	$scope.querySuptSalesTeamLst = function() {
		$scope.sendRecv("ORG240", "querySuptSalesTeamLst", "com.systex.jbranch.app.server.fps.org240.ORG240InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.suptSalesTeamLst = tota[0].body.suptSalesTeamLst;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.getSuptSalesTeamLst = function() {
		$scope.sendRecv("ORG240", "getSuptSalesTeamLst", "com.systex.jbranch.app.server.fps.org240.ORG240InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	};
	
	$scope.showORG240CONFIRM = function () {
		$scope.org240confirm_dialog = ngDialog.open({
			template: 'assets/txn/ORG240/ORG240CONFIRM.html',
			className : 'ORG240',
			showClose : false,
			closeByDocument : true,
			scope : $scope
		}).closePromise.then(function (data) {
			if (data.value != 'cancel') {
				$scope.init();
			}
		});
	};
	
	$scope.uploadSuptSalesTeamLst = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG240", "uploadSuptSalesTeamLst", "com.systex.jbranch.app.server.fps.org240.ORG240InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.inputVO.SUPT_SALES_TEAM_LST = tota[0].body.suptSalesTeamLst;
					$scope.showORG240CONFIRM();
				}
			});
		});
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='uploadSuptSalesTeamLst(name,rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
});
