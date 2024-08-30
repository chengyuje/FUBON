/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CMMGR014_EditController', function($scope, $controller, socketService, alerts, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "CMMGR014_EditController";

	// 增加FTP Server ID下拉選項
	$scope.iniRoles = function() {
		socketService.sendRecv("CMMGR014", "init", "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO", {}).then(function(oResp) {
			$scope.mappingSet['hostId'] = [];
			angular.forEach(oResp[0].body.hostIdList, function(row, index, objs) {
				$scope.mappingSet['hostId'].push({
					LABEL : row.IP,
					DATA : row.HOSTID
				});
			});
			projInfoService.mappingSet['hostId'] = $scope.mappingSet['hostId'];
		}, function(oErr) {
			$scope.showErrorMsg(oErr);
		});
	};

	$scope.iniRoles();

	$scope.init = function() {
		if ($scope.row) {
			$scope.isUpdate = true
		}
		$scope.row = $scope.row || {};

		$scope.inputVO = {
			tipFtpSettingId : $scope.row.FTPSETTINGID,
			tipSrcDirectory : $scope.row.SRCDIRECTORY,
			tipSrcFileName : $scope.row.SRCFILENAME,
			tipDesDirectory : $scope.row.DESDIRECTORY,
			tipDesFileName : $scope.row.DESFILENAME,
			cmbHostId : $scope.row.HOSTID,
			tipCheckFile : $scope.row.CHECKFILE,
			tipRepeat : $scope.row.REPEAT,
			tipRepeatInterval : $scope.row.REPEATINTERVAL,
			srcDelete : $scope.row.SRCDELETE,
			encryptKey : '',
			operKeyType : ''
		};
		console.log('inputVO=' + JSON.stringify($scope.inputVO));

		$scope.encryptReject = true;
		$scope.encryptAgree = false;
		$scope.ifDeleteEntryptKey = 'no';
	};
	$scope.init();

	$scope.save = function() {

		$scope.saveEntryptKey();
		
		if ($scope.parameterTypeEditForm.$invalid) {
			console.log('form=' + JSON.stringify($scope.parameterTypeEditForm));
			return;
		}
		console.log('save=' + $scope.row.FTPSETTINGID);
		$scope.inputVO.operType = 'Create';
		if ($scope.row.FTPSETTINGID != undefined) {
			$scope.inputVO.operType = 'Update';
		}
		$scope.sendRecv("CMMGR014", 'operation', "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}

			if (totas.length > 0) {
				$scope.closeThisDialog('successful');
			}
			;
		});
	}

	$scope.del = function() {
		$scope.deleteEntryptKey();
		
		$scope.inputVO.operType = 'Delete';
		$scope.sendRecv("CMMGR014", "operation", "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO", $scope.inputVO, function(totas, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(totas.body.msgData);
				return;
			}

			if (totas.length > 0) {
				$scope.showMsg('刪除成功');
				$scope.closeThisDialog('successful');
			}
			;
		});
	}

	// 是否解密切換
	$scope.encryptChange = function(value) {

		if (value == 1) {
			$scope.encryptReject = false;
			$scope.encryptAgree = true;
		}
		if (value == 2) {
			$scope.encryptReject = true;
			$scope.encryptAgree = false;
			$scope.inputVO.encryptKey = '';
			$scope.ifDeleteEntryptKey = 'yes';
		}
	}

	// 撈現有全部加密KEY
	$scope.getEncryptKey = function() {
		$scope.inputVO.operKeyType = 'inquire';
		$scope.sendRecv("CMMGR014", "inquireEntryptKey", "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.existEncryptKey = [];
				$scope.inputVO.operKeyType = 'Create';
				angular.forEach(tota[0].body.keyList, function(row, index, objs) {
					$scope.existEncryptKey.push({
						LABEL : row.KEY,
						DATA : row.KEY
					});
				});

				if (tota[0].body.encryptKey != "null") {
					$scope.encryptChange(1);
					$scope.inputVO.encryptKey = tota[0].body.encryptKey;
					$scope.inputVO.operKeyType = 'Update';

				}
			}
		});
	}
	$scope.getEncryptKey();

	$scope.saveEntryptKey = function() {
		if ($scope.encryptAgree === true) {
			$scope.sendRecv("CMMGR014", "operationEncryptKey", "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO", $scope.inputVO, function(tota, isError) {
			});
		} else if ($scope.row.FTPSETTINGID != undefined) {
			$scope.inputVO.operKeyType = 'Delete';
			$scope.sendRecv("CMMGR014", "operationEncryptKey", "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO", $scope.inputVO, function(tota, isError) {
			});
		} 
	}

	$scope.deleteEntryptKey = function() {
		$scope.inputVO.operKeyType = 'Delete';
		if ($scope.inputVO.encryptKey != "" || $scope.ifDeleteEntryptKey == 'yes') {
			$scope.sendRecv("CMMGR014", "operationEncryptKey", "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO", $scope.inputVO, function(tota, isError) {
			});
		}
	}

});
