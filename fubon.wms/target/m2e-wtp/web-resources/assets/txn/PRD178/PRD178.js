/**
 * 
 */
'use strict';
eSoafApp.controller('PRD178Controller', function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, $filter, getParameter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PRD178Controller";

	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened' + index] = true;
	};

	// date picker
	$scope.bgn_sDateOptions = {
		maxDate : $scope.maxDate,
		minDate : $scope.minDate
	};
	$scope.bgn_eDateOptions = {
		maxDate : $scope.maxDate,
		minDate : $scope.minDate
	};
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.endDate || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
	};
	// date picker end

	// init
	$scope.init = function() {
		$scope.inputVO = {
			CNAME : '',
			PROJECTNAME : '',
			PROJECTID : '',
			sCreDate :null,
			endDate : null
		};

	};
	$scope.init();
	$scope.inquireInit = function() {
		$scope.resultList = [];
	}
	$scope.inquireInit();

	// 查詢
	$scope.inquire = function() {
		$scope.sendRecv("PRD178", "inquire", "com.systex.jbranch.app.server.fps.prd178.PRD178InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
					return;
				}
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
			}
		});
	};

	// 整批上傳
	$scope.upload = function() {
		var dialog = ngDialog.open({
			template : 'assets/txn/PRD178/PRD178_UPLOAD.html',
			className : 'PRD178',
			showClose : false,
			controller : [ '$scope', function($scope) {

			} ]
		});
		dialog.closePromise.then(function(data) {
			if (data.value === 'successful') {
				if ($scope.inputVO.prd_id && $scope.inputVO.prd_id.length > 1) {
					$scope.inquireInit();
					$scope.inquire();
				}
			}
		});
	};

	// 下載CSV
	$scope.download = function(row) {
		$scope.sendRecv("PRD178", "downloadCSV", "com.systex.jbranch.app.server.fps.prd178.PRD178InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsg(totas[0].body.msgData);
			}
			if (totas.length > 0) {
				if (totas[0].body.resultList && totas[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
					return;
				}
			}
			;
		});
	};

	// 修改
	$scope.edit = function(row) {
		var dialog = ngDialog.open({
			template : 'assets/txn/PRD178/PRD178_EDIT.html',
			className : 'PRD178',
			showClose : false,
			controller : [ '$scope', function($scope) {
				$scope.row = row;
			} ]
		});
		dialog.closePromise.then(function(data) {
			if (data.value === 'successful') {
				$scope.inquireInit();
				$scope.inquire();
			}
		});
	};

	// 刪除
	$scope.deleteData = function(row) {
		var txtMsg = "確定刪除此筆資料?";
		$confirm({
			text : txtMsg
		}, {
			size : 'sm'
		}).then(function() {
			$scope.inputVO.deleteProjectID = row.PROJECTID;
			$scope.sendRecv("PRD178", "delete", "com.systex.jbranch.app.server.fps.prd178.PRD178InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					$scope.showSuccessMsg('ehl_01_common_003');
					$scope.inquireInit()
					$scope.inquire();
					return;
				}
			});
		});
	};

});