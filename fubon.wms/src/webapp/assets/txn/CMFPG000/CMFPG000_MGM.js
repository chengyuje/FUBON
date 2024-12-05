/*
  首頁
 */
'use strict';
eSoafApp.config(['$locationProvider', function($locationProvider) {  
	$locationProvider.html5Mode(true);  
}]);
eSoafApp.controller('CMFPG000Controller', ["$rootScope", "$scope", "$location", "sysInfoService", "projInfoService", "$controller", "ngDialog", "$cookies", "$q", "$window", "$timeout", "getParameter", "$filter",
		function($rootScope, $scope, $location, sysInfoService, projInfoService, $controller, ngDialog, $cookies, $q, $window, $timeout, getParameter, $filter) {
			$controller('BaseController', { $scope : $scope	});
			$scope.controllerName = "CMFPG000Controller";

			$scope.initData = function() {
				$scope.authuid = '';
				$scope.loginID = '';
				$scope.loginCD = '';
				$scope.wsId = '00';
				$scope.branchModel = '0000'; //[William] 登入不檢核分行與工作站，故分別以0000與00代替
//				$scope.mask = $rootScope.navigator==false?(($location.search().isMobile && $location.search().isMobile == 'mobile')?false:true):false;
				$scope.mask = false;
//				$scope.tmpstr = unfaker('%20%20%20%20%20%20%20p%3F%3F%3F%3F%3Fa%3F%3F%3F%3F%3F%3Fs%3F%3F%3F%3F%3F%3F%3Fs%23%23w%21o%20%20%20%20%20%20rd');
//				if($scope.mask)$("button[class*=e-login-btn-],input[class*=e-login-input]").attr('title', '系統不支援Chrome以外的瀏覽器');
			};
			$scope.initData();

			$scope.blurThenTlron = function($event) {
				if ($event.keyCode == 13) {
					$event.target.blur();
					$scope.tlron();
				}
			};

			$scope.wson = function() {
				var query = window.location.search.substring(1);
				var vars = query.split("&");

				for (var i = 0; i < vars.length; i++) {
					var pair = vars[i].split("=");
					if (pair[0] == "authuid") {
						$scope.authuid = pair[1];
					}
				}

				if ($scope.authuid) {
					console.log('query: ' + window.location.search);
					getAuthenticate();
				}
			}

			$scope.tlron = function() {
				$scope.Show = true;

				// 供socketService.js使用
				$rootScope.TlrID = $scope.loginID;
				$rootScope.BranchID = $scope.branchModel;
				$rootScope.WsId = $scope.wsId;

				getAuthenticate();

			}

			//2017/6/9
			if(sessionStorage.CMFPG000_MOBILE_LOGIN)
				$scope.loginSourceToken = 'mobile';
			else {
				if($location.search().isMobile && $location.search().isMobile == 'mobile') {
					$scope.loginSourceToken = 'mobile';
					sessionStorage.CMFPG000_MOBILE_LOGIN = 'mobile';
				}
			}

			//
			function getAuthenticate() {
				var inputVO = {
					"authuid" : $scope.authuid,
					"iptAppUsername" : $scope.loginID,
					"iptAppUserPassword" : $scope.loginCD,
					"iptRACFUser_ID" : "888803",
					"iptRACF_Password" : "",
					"ddlBranchNo" : $scope.branchModel,
					"kickOut" : true
				};

				$scope.sendRecv("CMFPG000", "authenticate", "com.systex.jbranch.app.server.fps.cmfpg000.LoginPageVO", inputVO,
						function(tota, isError) {
							console.log('MGM-tlron tota=' + tota + ', isError=' + isError);
							if (isError) {
								$scope.msgData = tota.body.msgData;
							} else {
								if(tota[0].body.loginMsg){
									$scope.showWarningMsg(tota[0].body.loginMsg);
								}

								sysInfoService.setCurrentUserId($scope.loginID);

								if((tota[0].body.userList).length === 1){
									console.log('#' +  	$scope.loginSourceToken);
									sysInfoService.setUserList(tota[0].body.userList);
									$scope.sendRecv("CMFPG000", "tlron", "com.systex.jbranch.app.server.fps.cmfpg000.LoginPageVO",
											{'iptAppUsername':tota[0].body.userList[0].EMP_ID,
										     'iptAppUserRole':tota[0].body.userList[0].ROLE_ID,
										     'iptAppUserDeptID':tota[0].body.userList[0].DEPT_ID,
										     'iptAppUserRegioinCenterID':tota[0].body.userList[0].REGION_CENTER_ID,
										     'iptAppUserRegioinCenterName':tota[0].body.userList[0].REGION_CENTER_NAME,
										     'iptAppUserBranchAreaID':tota[0].body.userList[0].BRANCH_AREA_ID,
										     'iptAppUserBranchAreaName':tota[0].body.userList[0].BRANCH_AREA_NAME,
										     'iptAppUserBranchID':tota[0].body.userList[0].BRANCH_NBR,
										     'iptAppUserBranchName':tota[0].body.userList[0].BRANCH_NAME,
										     'iptAppUserIsPrimaryRole':tota[0].body.userList[0].IS_PRIMARY_ROLE,
										     'loginSourceToken':$scope.loginSourceToken,
										     'currentUserId':$scope.loginID
										     }, function(tota, isError) {
												console.log('MGM-1-tlron tota=' + tota + ', isError=' + isError);
												if (isError) {
													$scope.msgData = tota.body.msgData;
												} else {
													$scope.setSysInfoService(tota[0].body);
												}
											}
										);
								}
								else if((tota[0].body.userList).length > 1) {
									debugger;
									var loginSourceTemp = $scope.loginSourceToken;
									sysInfoService.setUserList(tota[0].body.userList);

									var dialog = ngDialog.open({
						        		template: 'assets/txn/CMFPG000/CMFPG000_AGENT.html',
						        		className: 'CMFPG000',
						        		showClose: false,
						        		controller: ['$scope', function($scope) {
						        			$scope.userList = tota[0].body.userList;
						        			$scope.loginSourceToken = loginSourceTemp;
						        		}]
						        	});
									dialog.closePromise.then(function (data) {
						        		if(data.value === 'successful'){
						        			$scope.setSysInfoService(projInfoService.LoginInfo);
						        		}
						        	});
								}
								else{
									return;
								}
							}
						}
				);
			}

			$scope.setSysInfoService = function(output) {
				$cookies.branchId = output.loginBrh;
				$cookies.UserId = output.loginID;
				sysInfoService.setApServerName(output.apServerName);
				sysInfoService.setUserID(output.loginID);
				sysInfoService.setUserName(output.loginName);
				sysInfoService.setApplicationID($scope.ApplicationID);
				sysInfoService.setBranchID(output.loginBrh);
				sysInfoService.setAreaID(output.loginArea);
				sysInfoService.setRegionID(output.loginRegion);
				sysInfoService.setBranchName(output.loginBrhName);
				sysInfoService.setWsID($scope.wsId);
				sysInfoService.setRoleID(output.loginRole);
				sysInfoService.setPriID(output.priID);
				sysInfoService.setRoleName(output.loginRoleName);
				sysInfoService.setAvailBranch(output.availBranchList);
				sysInfoService.setAvailRegion(output.availRegionList);
				sysInfoService.setAvailArea(output.availAreaList);
				sysInfoService.setUser(output.userInfo);
				sysInfoService.setAoCode(output.userInfo.AoCode);
				sysInfoService.setLoginSourceToken(output.loginSourceToken);
				sysInfoService.setIsUHRM(output.isUHRM);

				$scope.UserID = sysInfoService.getUserID();
				$scope.ApplicationID = sysInfoService.getApplicationID();
				$scope.BranchID = sysInfoService.getBranchID();
				$scope.WsID = sysInfoService.getWsID();
				$scope.RoleID = sysInfoService.getRoleID();

				$q.all([
					loadMenu().then(function(data) {
						$scope.finMenu = true;
					}),

					loadAuthorities(sysInfoService.getRoleID())
					.then(function(data) {
						$scope.finAuthorities = true;
					}),

					 i18nLoad().then(function(data) {
					 $scope.finI18n = true;
					 }),
					loadWatermark().then(function(data) {
						$scope.finWatermark = true;
					})
				])
				.then(function() {
					getParameter.XML('MGM.URL', function(totas) {
						if (totas) {
							var param = totas.data[totas.key.indexOf('MGM.URL')];
							if (param && param.length) {
								var url = param[0].LABEL;
								if (url) {
									window.open(url.replace('{userId}', $cookies.UserId).replace('{branchId}', $cookies.branchId));
									window.close();
								}
							}
						}
					});
				});
			};

			$rootScope.ApplicationID = uuid();

			function loadWatermark() {
				var deferred = $q.defer();
				getParameter.XML('WATERMARK', function(totas) {
					if (totas) {
						sysInfoService.setWATERMARK(totas.data[0]);
						deferred.resolve(sysInfoService.getWATERMARK());
					}
				});
				return deferred.promise;
			}
			function i18nLoad() {
				var language = $window.navigator.language
						|| $window.navigator.userLanguage;
				language = language.toLocaleLowerCase();
				var keys = null;
				if (language === 'en') {
					keys = {
						'pf_sessionmanager_error_001' : 'workstation timeout',
						'pf_sessionmanager_error_002' : 'login dupicate',
						'pf_sessionmanager_error_003' : 'system error',
						'pf_sessionmanager_error_004' : 'Server shutdown logout',
						'pf_sessionmanager_error_005' : 'workstaion logout',
						'pf_sessionmanager_error_006' : 'Business day changed',
						'pf_sessionmanager_error_007' : 'please login',
						'ehl_01_cmfpg000_008' : 'connect failed:{0}-{1}'
					};
				} else if (language === 'zh-tw') {
					keys = {
						'pf_sessionmanager_error_001' : '工作站逾時',
						'pf_sessionmanager_error_002' : '重複登入',
						'pf_sessionmanager_error_003' : '系統異常',
						'pf_sessionmanager_error_004' : 'Server關機登出',
						'pf_sessionmanager_error_005' : '工作站已登出',
						'pf_sessionmanager_error_006' : '營業日期已被改變',
						'pf_sessionmanager_error_007' : '請重新登入',
						'ehl_01_cmfpg000_008' : '連線失敗:{0}-{1}'
					};
				}
				return i18nRemoteLoad(language, keys);
			}
			function i18nRemoteLoad(language, keys) {
				var deferred = $q.defer();
				$scope.sendRecv("I18N", "inquireAll", "com.systex.jbranch.app.server.fps.i18n.I18NInputVO", {'locale' : language},
					function(totas, isError) {
						if (isError) {
							$scope.showErrorMsg(totas[0].body.msgData);
							deferred.reject(keys);
						}
						if (totas.length > 0) {
							angular.forEach(totas[0].body.resultList,
											function(row, index, objs) {
												keys[row.CODE] = row.TEXT;
											});
							sysInfoService.setI18N(keys);
							deferred.resolve(sysInfoService.getI18N());
						};
					}
				);
				return deferred.promise;
			}

			function loadAuthorities(roles) {
				var deferred = $q.defer();
				if (roles) {
					roles = roles.split(',');
					$scope.sendRecv("CMFPG000", "getAuthorities", "com.systex.jbranch.app.server.fps.cmfpg000.AuthoritiesInputVO", {roles : roles},
						function(totas, isError) {
							if (isError) {
								$scope.showErrorMsg(totas[0].body.msgData);
								deferred.reject();
							}
							if (totas.length > 0) {
								// sysInfoService.mappingSet['authorities'].MODULEID['WMS'].ITEMID['CMFPG000'].FUNCTIONID['query']
								// sysInfoService.mappingSet['authorities']
								// = ;
								sysInfoService.setAuthorities(refreshAuthorities(totas[0].body.authorities));
								deferred.resolve(sysInfoService.getAuthorities());
							};
						}
					);
				}
				return deferred.promise;
			}
			function refreshAuthorities(authorities) {
				var ans = {};
				ans["MODULEID"] = {};
				angular.forEach(authorities,
						function(row, index, objs) {
							ans.MODULEID[row.MODULEID] = {};
							ans.MODULEID[row.MODULEID]["ITEMID"] = {};
						});
				angular.forEach(authorities,
								function(row, index, objs) {
									ans.MODULEID[row.MODULEID].ITEMID[row.ITEMID] = {};
									ans.MODULEID[row.MODULEID].ITEMID[row.ITEMID]["FUNCTIONID"] = {};
								});
				angular.forEach(authorities,
								function(row, index, objs) {
									ans.MODULEID[row.MODULEID].ITEMID[row.ITEMID].FUNCTIONID[row.FUNCTIONID] = true;
								});
				return ans;
			}

			function loadMenu() {
				var deferred = $q.defer();
				$scope.sendRecv("CMMGR020", "getMENUTree", "com.systex.jbranch.app.server.fps.cmmgr020.CMMGR020DataVO", {},
					function(totas, isError) {
						if (isError) {
							$scope.showErrorMsg(totas[0].body.msgData);
							deferred.reject();
						}
						if (totas.length > 0) {
							sysInfoService.setMenu(refreshMenus(totas[0].body.meunTreeLS));
							deferred.resolve(sysInfoService.getMenu());
						};
					}
				);
				return deferred.promise;
			}
			function refreshMenus(menus) {
				var ans = [];
				for (var i = 0; i < menus.length; i++) {
					generateMenu(ans, menus[i], 1);
				}
				return ans;
			}
			function generateMenu(ansRow, row, circle) {
				var obj = {}, exist = false;
				for (var i = 0; i < ansRow.length; i++) {
					if (row["LEVEL" + circle + "_MENU_ID"] == ansRow[i]["MENU_ID"]) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					obj["MENU_ID"] = row["LEVEL" + circle + "_MENU_ID"];
					obj["MENU_NAME"] = row["LEVEL" + circle + "_MENU_NAME"];
					obj["MENU_TYPE"] = row["LEVEL" + circle + "_MENU_TYPE"];
					obj["SEQ_NUM"] = row["LEVEL" + circle + "_SEQ_NUM"];
					if (row["LEVEL" + circle + "_MENU_TYPE"] == "P") {
						obj["ITEM_ID"] = row["LEVEL" + circle + "_ITEM_ID"];
						ansRow.push(obj);
						return;
					}
					obj["SUBITEM"] = [];
					ansRow.push(obj);
					generateMenu(obj["SUBITEM"], row, circle + 1);
					return;
				}
				var old = ansRow.slice(-1).pop();
				generateMenu(old["SUBITEM"], row, circle + 1);
			}
			function settle () {
				var tmp = [{"LABEL":"SINGLE","DATA":1},{"LABEL":"TOTAL","DATA":10}];
				var gator = navigator.userAgent.match("Safari") ? true : false;

				sysInfoService.setDialogCounts(tmp);
				//Dialog
				getParameter.XML('MAX_DIALOG_COUNT', function(totas) {
					if (totas) {
						tmp = (totas.data[0].length !=0) ? totas.data[0] : tmp;
						sysInfoService.setDialogCounts(tmp);
					}
				});
				//Navigator
				sysInfoService.setNavigator(gator);
			}

			settle();
			$scope.wson();
			
}]);
