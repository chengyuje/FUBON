/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG242Controller', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService, $filter) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG242Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
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
	
	$scope.giveValue = function (source) {
		if (source == 'ec') {
			$scope.inputVO.empIDInput = $scope.inputVO.emp_id;
     	} else if (source == 'ip') {
     		if ($scope.inputVO.empIDInput.length == 6) {
         		$scope.inputVO.emp_id = '';
         		angular.forEach($scope.EMP_LIST, function(item, index) {
         		    var tindex = ($filter('uppercase')($scope.inputVO.empIDInput)).indexOf(item.DATA);
         		    if (tindex > -1 && item.DATA != '') {
         		    	$scope.inputVO.emp_id = $filter('uppercase')($scope.inputVO.empIDInput);
         		    }
         		});
         		
         		if ($scope.inputVO.emp_id == '') {
     		    	$scope.inputVO.empIDInput = '';
     		    	$scope.inputVO.emp_id = '';
     		    	
     		    	$scope.showMsg('該人員無法查詢，可能原因：無人持有/權限不足(非轄下)');
             	}
         	}
     	}
     }
	
	$scope.query = function() {
		$scope.totals = {};
		
		$scope.sendRecv("ORG242", "query", "com.systex.jbranch.app.server.fps.org242.ORG242InputVO", $scope.inputVO, function(tota, isError) {
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
		$scope.sendRecv("ORG242", "getExample", "com.systex.jbranch.app.server.fps.org242.ORG242InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					
					if (tota.length > 0) {
					}
		});
	};
	
	$scope.showORG242CONFIRM = function () {
		$scope.ORG242confirm_dialog = ngDialog.open({
			template: 'assets/txn/ORG242/ORG242CONFIRM.html',
			className : 'ORG242',
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
			$scope.sendRecv("ORG242", "checkPoint", "com.systex.jbranch.app.server.fps.org242.ORG242InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.inputVO.TEAM_LST = tota[0].body.resultList;
					$scope.emptyColumnMessage = tota[0].body.emptyColumnMessage;
					$scope.showORG242CONFIRM();
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
		
		$scope.sendRecv("ORG242", "export", "com.systex.jbranch.app.server.fps.org242.ORG242InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
                		if(totas[0].body.resultList && totas[0].body.resultList.length == 0) {
                			$scope.showMsg("ehl_01_common_009");
                			return;
                		}
                	};
				}
		);
	};
	
});
