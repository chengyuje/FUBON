/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG260Controller', function($scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG260Controller";
	
	/*
	 * 取得UHRM人員清單(由員工檔+角色檔)
	 */
	$scope.getUHRMList = function() {
		$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
						$scope.inputVO.uEmpID = tota[0].body.uEmpID;
					}
		});
	};
	
	$scope.init = function() {
		$scope.resultList = [];
		$scope.data = [];
		
		$scope.inputVO = {
			uEmpID : '',
			fileName : '',
			actualFileName : '', 
			exportList : []
		};

		$scope.getUHRMList();
	};
	$scope.init();
	
	$scope.query = function() {
		$scope.totals = {};
		$scope.resultList = [];
		$scope.outputVO = [];
		$scope.sendRecv("ORG260", "query", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
						return;
					}
					if (tota.length > 0) {
						if (tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						} else {
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
						}
					}
		});
	};
	
	$scope.showResultDialog = function (prjID) {
		$scope.resultDialog = ngDialog.open({
    		template: 'assets/txn/ORG260/ORG260_RESULT.html',
    		className: 'ORG260',
    		showClose : false,
			closeByDocument : true,
    		controller: ['$scope',function($scope){
    			$scope.prjID = prjID;
    		}]
    	}).closePromise.then(function (data) {
			$scope.init();
			$scope.query();
    	});
	};
	
	/*
	 * 上傳UHRM與客戶對應檔
	 */
	$scope.updUHRMList = function(name, rname) {
		$scope.inputVO.fileName = name;
		$scope.inputVO.actualFileName = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG260", "updUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (tota.length > 0) {
							$scope.showResultDialog(tota[0].body.prjID);
						}		
			});
		});
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='updUHRMList(name, rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	$scope.getExample = function() {
		$scope.sendRecv("ORG260", "getExample", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {
					}
		});
	};
	
	$scope.export = function() {
		angular.copy($scope.resultList, $scope.inputVO.exportList);
		$scope.inputVO.exportList.push($scope.totals);
		
		$scope.sendRecv("ORG260", "export", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO,
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
