/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS132Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $compile) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS132Controller";
	
	$scope.init = function(){
		$scope.inputVO = {
			FILE_NAME          : '',
			ACTUAL_FILE_NAME   : '',
			dtccc              : '', 
			exportList         : []
		};
	
		$scope.paramList = [];
	};
	$scope.init();
	
	// 查詢-業務處回覆組別明細檔
	$scope.queryData = function(selectType){
		$scope.totals = {};
		
		$scope.paramList = [];
		$scope.outputVO = [];
		$scope.inputVO.selectType = selectType;
		
		$scope.sendRecv("PMS132", "queryData", "com.systex.jbranch.app.server.fps.pms132.PMS132InputVO", $scope.inputVO, function(tota, isError) {
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
	$scope.getExample = function(selectType) {
		
		$scope.inputVO.selectType = selectType;
		
		$scope.sendRecv("PMS132", "getExample", "com.systex.jbranch.app.server.fps.pms132.PMS132InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			
			if (tota.length > 0) {
			}
		});
	};
	
	// 上傳-業務處回覆組別明細檔
	$scope.updGroupBonusCal = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PMS132", "updGroupBonusCal", "com.systex.jbranch.app.server.fps.pms132.PMS132InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");
				}
				$scope.queryData('G');
			});
		});
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='updGroupBonusCal(name, rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	// 匯出-業務處回覆組別明細檔
	$scope.export = function(selectType) {
		$scope.inputVO.selectType = selectType;
		
		angular.copy($scope.paramList, $scope.inputVO.exportList);
		$scope.inputVO.exportList.push($scope.totals);
		
		$scope.sendRecv("PMS132", "export", "com.systex.jbranch.app.server.fps.pms132.PMS132InputVO", $scope.inputVO,
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
	
	// 上傳-個人獎金率
	$scope.updPersonalBonusCal = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("PMS132", "updPersonalBonusCal", "com.systex.jbranch.app.server.fps.pms132.PMS132InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.showMsg("ehl_01_common_010");
				}
				$scope.queryData('P');
			});
		});
		angular.element($("#csvUpload2")).remove();
		angular.element($("#csvBox2")).append("<e-upload id='csvUpload2' success='updPersonalBonusCal(name, rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox2"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
});
