/*
 * #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管
 */
'use strict';
eSoafApp.controller('ORG270Controller', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService, $filter, getParameter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG270Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// combobox
	getParameter.XML(["COMMON.YES_NO"], function(totas) {
		if (totas) {
			$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
		}
	});
	
	
	$scope.init = function() {
		$scope.resultList = [];
		$scope.data = [];
		
		$scope.inputVO = {
			FILE_NAME          : '',
			ACTUAL_FILE_NAME   : '',
			TEAM_LST           : [], 
			exportList         : []
		};
		
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);

	};
	$scope.init();
	
	$scope.query = function() {
		$scope.totals = {};
		
		$scope.sendRecv("ORG270", "query", "com.systex.jbranch.app.server.fps.org270.ORG270InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.getExample = function() {
		$scope.sendRecv("ORG270", "getExample", "com.systex.jbranch.app.server.fps.org270.ORG270InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					
					if (tota.length > 0) {
					}
		});
	};
	
	$scope.showORG270CONFIRM = function () {
		$scope.ORG270confirm_dialog = ngDialog.open({
			template: 'assets/txn/ORG270/ORG270_CONFIRM.html',
			className : 'ORG270',
			showClose : false,
			closeByDocument : true,
			scope : $scope
		}).closePromise.then(function (data) {
			if (data.value != 'cancel') {
				$scope.init();
				$scope.query();
			}
		});
	};
	
	$scope.checkPoint = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG270", "checkPoint", "com.systex.jbranch.app.server.fps.org270.ORG270InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.inputVO.TEAM_LST = tota[0].body.resultList;
					$scope.showORG270CONFIRM();
				}
			});
		});
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='checkPoint(name,rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	$scope.export = function() {
		angular.copy($scope.resultList, $scope.inputVO.exportList);
		$scope.inputVO.exportList.push($scope.totals);
		
		$scope.sendRecv("ORG270", "export", "com.systex.jbranch.app.server.fps.org270.ORG270InputVO", $scope.inputVO, function(totas, isError) {
        	if (isError) {
        		$scope.showErrorMsg(totas[0].body.msgData);
        	}
        	if (totas.length > 0) {
        		if(totas[0].body.resultList && totas[0].body.resultList.length == 0) {
        			$scope.showMsg("ehl_01_common_009");
        			return;
        		}
        	};
		});
	};
	
});
