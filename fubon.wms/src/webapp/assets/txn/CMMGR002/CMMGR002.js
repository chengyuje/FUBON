/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CMMGR002_Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CMMGR002_Controller";
		
		$scope.init = function() {
			$scope.inputVO = {};
			$scope.adgRole = [];
			$scope.adgExistRole = [];
		};
		$scope.init();
		
		$scope.initInquire = function() {
			var deferred = $q.defer();
	    	$scope.sendRecv("CMMGR002", "query", "com.systex.jbranch.app.server.fps.cmmgr002.CMMGR002InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.adgGroupList = tota[0].body.groupList;
							$scope.groupVO = tota[0].body;
							$scope.adgRoleList = tota[0].body.roleList;
//							$scope.queryMember($scope.adgGroupList[0].PRIVILEGEID);
							deferred.resolve("success");
						}
			});
	    	return deferred.promise;
	    };
	    $scope.initInquire();
		
		$scope.queryMember = function(priID) {
			$scope.clickAll = false;
			$scope.clickAll2 = false;
			// clone
			$.extend($scope.adgRole, $scope.adgRoleList);
			$scope.roleVO = {'data':$scope.adgRole};
			$scope.adgExistRole = [];
			$scope.exRoleVO = {};
			$scope.inputVO.priID = priID;
			$scope.sendRecv("CMMGR002", "inquire",
					"com.systex.jbranch.app.server.fps.cmmgr002.CMMGR002InputVO",
					$scope.inputVO, function(tota, isError) {
						if (!isError) {
							$scope.adgExistRole = tota[0].body.resultList;
							$scope.exRoleVO = {'data':$scope.adgExistRole};
							for (var i = $scope.adgExistRole.length - 1; i >= 0; i--) {
								var k = getIndexByRoleID($scope.adgRole,
										$scope.adgExistRole[i].ROLEID);
								if (k != -1) {
									$scope.adgRole.splice(k, 1);
								}
							}
							$scope.roleVO = {'data':$scope.adgRole};
							return;
						}
					});
		};
		
		function getIndexByRoleID(adgRole, roleID) {
			var ret = -1;
			if (adgRole == null) {
				return ret;
			}
			var index = 0;
			for (var i = 0; i < adgRole.length; i++) {
				if (adgRole[i].ROLEID == roleID) {
					ret = index;
				} else {
					index++;
				}
			}
			return ret;
		};
		
		function getIndexByRoleID(adgRole, roleID) {
			var ret = -1;
			if (adgRole == null) {
				return ret;
			}
			var index = 0;
			for (var i = 0; i < adgRole.length; i++) {
				if (adgRole[i].ROLEID == roleID) {
					ret = index;
				} else {
					index++;
				}
			}
			return ret;
		};
		
		$scope.checkrow = function(click, to) {
			if (click) {
				for (var i = 0; i < to.length; i++) {
					to[i].SELECTED = true;
				}
			} else {
				for (var i = 0; i < to.length; i++) {
					to[i].SELECTED = false;
				}
			}
		};
		
		$scope.btnAddFun = function() {
//			$scope.clickAll = false;
//			$scope.clickAll2 = false;
			for (var i = $scope.adgRole.length - 1; i >= 0; i--) {
				if ($scope.adgRole[i].SELECTED) {
					$scope.adgRole[i].SELECTED = false;
					$scope.adgExistRole.push($scope.adgRole[i]);
					$scope.adgRole.splice(i, 1);
				}
			}
			$scope.exRoleVO = {'data':$scope.adgExistRole};
			$scope.roleVO = {'data':$scope.adgRole};
		};

		$scope.btnDelFun = function() {
//			$scope.clickAll = false;
//			$scope.clickAll2 = false;
			for (var i = $scope.adgExistRole.length - 1; i >= 0; i--) {
				if ($scope.adgExistRole[i].SELECTED) {
					$scope.adgExistRole[i].SELECTED = false;
					$scope.adgRole.push($scope.adgExistRole[i]);
					$scope.adgExistRole.splice(i, 1);
				}
			}
			$scope.exRoleVO = {'data':$scope.adgExistRole};
			$scope.roleVO = {'data':$scope.adgRole};
		};

		$scope.save = function() {
			if ($scope.inputVO.groupid == '') {
				return;
			}
			$scope.inputVO.roleList = $scope.adgExistRole;
			$scope.sendRecv("CMMGR002", "save","com.systex.jbranch.app.server.fps.cmmgr002.CMMGR002InputVO",
				$scope.inputVO, function(tota, isError) {
					if (!isError) {
						$scope.initInquire().then(function(data) {
							$scope.queryMember($scope.inputVO.priID);
						});
						return;
					}
			});
		};

		$scope.edit = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CMMGR002/CMMGR002_EDIT.html',
				className: 'CMMGR002',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function(data) {
				if (data.value === 'successful') {
					$scope.init();
				}
			});
		};
		
		
	}
);