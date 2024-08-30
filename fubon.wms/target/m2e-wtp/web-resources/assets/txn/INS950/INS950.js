'use strict';
eSoafApp.controller('INS950Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS950Controller";
		
		// combobox
		getParameter.XML(["INS.PLAN_TYPE", "INS.PARA_HEADER_STATUS", "INS.UNIT"], function(totas) {
			if (totas) {
				$scope.PLAN_TYPE = totas.data[totas.key.indexOf('INS.PLAN_TYPE')];
				$scope.PARA_HEADER_STATUS = totas.data[totas.key.indexOf('INS.PARA_HEADER_STATUS')];
				$scope.INS_UNIT = totas.data[totas.key.indexOf('INS.UNIT')];
			}
		});
		
//		// 判斷主管直接根據有無覆核權限
//		if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["INS910"])
//			$scope.CanConfirm = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["INS910"].FUNCTIONID["confirm"];
//		else
//			$scope.CanConfirm = false;
		
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.sendRecv("INS950", "initial", "com.systex.jbranch.app.server.fps.ins950.INS950InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		$scope.reportList = totas[0].body.reportList;
                	};
				}
    		);
		};
		$scope.init();
		
		$scope.downloadDoc = function(row) {
			$scope.sendRecv("INS930", "downloadDoc", "com.systex.jbranch.app.server.fps.ins930.INS930InputVO", {'para_no': row.KEYNO},
				function(totas, isError) {
                	if (!isError) {
                		
                	};
				}
    		);
		};
		$scope.uploadT = function (row) {
			$scope.inputVO.para_type = row.PARA_TYPE;
			var btn = angular.element(document.getElementById('fileup'));
			btn.trigger('click');
        };
        $scope.finishUpload = function (name, rname) {
            if(!name) {
            	$scope.showErrorMsg('欄位檢核錯誤：請選擇上傳檔案');
            	return;
    		}
            $scope.inputVO.file_seq = null;
            $scope.inputVO.fileName = name;
            $scope.inputVO.fileRealName = rname;
            $scope.REPORT_FILE_NAME = rname;
            $scope.goReview();
        };
		
        //儲存PDF
        $scope.goReview = function() {
        	$scope.sendRecv("INS950", "goReview", "com.systex.jbranch.app.server.fps.ins950.INS950InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (!isError) {
                		$scope.showSuccessMsg('ehl_01_common_006');
                		$scope.init();
                	};
				}
    		);
        };
        		
});