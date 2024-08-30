'use strict';
eSoafApp.controller('PMS142Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $compile) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS142Controller";
	
	$scope.selectType = 'PROD_YIELD';
	$scope.init = function(){
		
		$scope.inputVO = {
			FILE_NAME          : '',
			ACTUAL_FILE_NAME   : ''
		};
	
		$scope.paramList = [];
	};
	$scope.init();
	
	// 查詢-業務處回覆組別明細檔
	$scope.queryData = function(type){
		
		$scope.totals = {};
		
		$scope.paramList = [];
		$scope.outputVO = [];
		
		$scope.selectType = type;
		$scope.inputVO.selectType = $scope.selectType;
		
		$scope.selectType = $scope.inputVO.selectType;
		$scope.sendRecv("PMS142", "queryData", "com.systex.jbranch.app.server.fps.pms142.PMS142InputVO", $scope.inputVO, function(tota, isError) {
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
	
	// 取得範例-業務處回覆組別明細檔
	$scope.getExample = function() {
		
		$scope.inputVO.selectType = $scope.selectType;
		
		$scope.sendRecv("PMS142", "getExample", "com.systex.jbranch.app.server.fps.pms142.PMS142InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
			}
		});
	};
	
	// 上傳-業務處回覆組別明細檔
	$scope.updFile = function(name, rname) {
		
		$scope.inputVO.selectType = $scope.selectType;
		
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PMS142", "updFile", "com.systex.jbranch.app.server.fps.pms142.PMS142InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				
				if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");
				}
				
				$scope.queryData($scope.inputVO.selectType);
			});
		});
		
		switch ($scope.inputVO.selectType) {
			case "PROD_YIELD":
				angular.element($("#csvUpload_PROD_YIELD")).remove();
				angular.element($("#csvBox_PROD_YIELD")).append("<e-upload id='csvUpload_PROD_YIELD' success='updFile(name, rname)' text='上傳' accept='.csv'></e-upload>");
				var content = angular.element($("#csvBox_PROD_YIELD"));
				var scope = content.scope();
				$compile(content.contents())(scope);
				
				break;
			case "BASIC_THRESHHOLD":
				angular.element($("#csvUpload_BASIC_THRESHHOLD")).remove();
				angular.element($("#csvBox_BASIC_THRESHHOLD")).append("<e-upload id='csvUpload_BASIC_THRESHHOLD' success='updFile(name, rname)' text='上傳' accept='.csv'></e-upload>");
				var content = angular.element($("#csvBox_BASIC_THRESHHOLD"));
				var scope = content.scope();
				$compile(content.contents())(scope);
				
				break;
			case "NUM_NEW_APPOINTMENTS":
				angular.element($("#csvUpload_NUM_NEW_APPOINTMENTS")).remove();
				angular.element($("#csvBox_NUM_NEW_APPOINTMENTS")).append("<e-upload id='csvUpload_NUM_NEW_APPOINTMENTS' success='updFile(name, rname)' text='上傳' accept='.csv'></e-upload>");
				var content = angular.element($("#csvBox_NUM_NEW_APPOINTMENTS"));
				var scope = content.scope();
				$compile(content.contents())(scope);
				
				break;
			case "BONUS_RATE":
				angular.element($("#csvUpload_BONUS_RATE")).remove();
				angular.element($("#csvBox_BONUS_RATE")).append("<e-upload id='csvUpload_BONUS_RATE' success='updFile(name, rname)' text='上傳' accept='.csv'></e-upload>");
				var content = angular.element($("#csvBox_BONUS_RATE"));
				var scope = content.scope();
				$compile(content.contents())(scope);
				
				break;
			case "FINANCIAL_INDICATOR":
				angular.element($("#csvUpload_FINANCIAL_INDICATOR")).remove();
				angular.element($("#csvBox_FINANCIAL_INDICATOR")).append("<e-upload id='csvUpload_FINANCIAL_INDICATOR' success='updFile(name, rname)' text='上傳' accept='.csv'></e-upload>");
				var content = angular.element($("#csvBox_FINANCIAL_INDICATOR"));
				var scope = content.scope();
				$compile(content.contents())(scope);
				
				break;
			case "BEHAVIORAL_INDICATOR":
				angular.element($("#csvUpload_BEHAVIORAL_INDICATOR")).remove();
				angular.element($("#csvBox_BEHAVIORAL_INDICATOR")).append("<e-upload id='csvUpload_BEHAVIORAL_INDICATOR' success='updFile(name, rname)' text='上傳' accept='.csv'></e-upload>");
				var content = angular.element($("#csvBox_BEHAVIORAL_INDICATOR"));
				var scope = content.scope();
				$compile(content.contents())(scope);
				
				break;
		}

	};
	
	// 匯出-業務處回覆組別明細檔
	$scope.export = function() {

		$scope.inputVO.selectType = $scope.selectType;
		$scope.inputVO.exportList = $scope.paramList;
		
		$scope.sendRecv("PMS142", "export", "com.systex.jbranch.app.server.fps.pms142.PMS142InputVO", $scope.inputVO, function(totas, isError) {
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
