/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CMMGR015_Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "CMMGR015_Controller";

	// init
	$scope.init = function() {
		$scope.inputVO.code = '';
		$scope.inputVO.text = '';
		$scope.inputVO.sqlStr = '';
		$scope.inputVO.sqlResult = '';
	}
	$scope.init();

	// 查詢
	$scope.inquire = function() {
		$scope.sendRecv('CMMGR015', 'inquire', 'com.systex.jbranch.app.server.fps.cmmgr015.CMMGR015InputVO', $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.outputVO = tota[0].body;
				$scope.i18nlist = tota[0].body.resultList;
				$scope.init();
			} else {
				$scope.showMsg('查詢失敗');
			}
		});

	}

	// 查詢
	$scope.inquireSQL = function() {
		$scope.sendRecv('CMMGR015', 'inquireSQL', 'com.systex.jbranch.app.server.fps.cmmgr015.CMMGR015InputVO', $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.outputVO = tota[0].body;
				$scope.inputVO.sqlResult = tota[0].body.sqlResult;
			} else {
				$scope.showMsg('查詢失敗');
			}
		});

	}
	// 新增
	$scope.insert = function() {
		
		$confirm({
			title : '新增',
			text : '是否新增訊息代碼: ' + $scope.inputVO.code + '訊息內容: ' + $scope.inputVO.text +'?'
		}, {
			size : 'sm'
		}).then(function() {
			
			$scope.sendRecv('CMMGR015', 'insert', 'com.systex.jbranch.app.server.fps.cmmgr015.CMMGR015InputVO', $scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.showMsg('新增成功');
					$scope.inquire();
				} else {
					$scope.showMsg('新增失敗');
				}
			});
			
		});
		
	}
	
	// 修改
	$scope.update = function() {
		
		$confirm({
			title : '修改',
			text : '是否將訊息代碼: ' + $scope.choosecode + '訊息內容: ' + $scope.choosetext + 
			'修改成訊息代碼: ' + $scope.inputVO.code + '訊息內容: ' + $scope.inputVO.text +'?'
		}, {
			size : 'sm'
		}).then(function() {
			
			$scope.sendRecv('CMMGR015', 'update', 'com.systex.jbranch.app.server.fps.cmmgr015.CMMGR015InputVO', $scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.showMsg('修改成功');
					$scope.inquire();
				} else {
					$scope.showMsg('修改失敗');
				}
			});
			
			
		});
		
	}
	
	// 修改
	$scope.updateSQL = function() {
		$scope.sendRecv('CMMGR015', 'updateSQL', 'com.systex.jbranch.app.server.fps.cmmgr015.CMMGR015InputVO', $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.inputVO.sqlResult = tota[0].body.sqlResult;
			} else {
				$scope.inputVO.sqlResult = tota[0].body.sqlResult;
			}
		});
	}
	// 刪除
	
	
	$scope.Delete = function() {
		$confirm({
			title : '刪除',
			text : '是否刪除訊息代碼: ' + $scope.inputVO.code + '訊息內容: ' + $scope.inputVO.text +'?'
		}, {
			size : 'sm'
		}).then(function() {
			$scope.sendRecv('CMMGR015', 'delete', 'com.systex.jbranch.app.server.fps.cmmgr015.CMMGR015InputVO', $scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.showMsg('刪除成功');
					$scope.inquire();
				} else {
					$scope.showMsg('刪除失敗');
				}
			});
		});
	
	}
	
	
	//選擇訊息代號或訊息內容，會自動帶入上方欄位供使用者修改或刪除
	$scope.choose = function(row){
		$scope.choosecode = row.CODE;
		$scope.choosetext = row.CODE;
		
		$scope.inputVO.code = row.CODE;
		$scope.inputVO.text = row.TEXT;
	}

});
