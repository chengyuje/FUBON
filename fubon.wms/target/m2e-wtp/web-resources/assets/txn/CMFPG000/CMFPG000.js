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
				$scope.IPCheck = true;

				/** 20181018 添加驗證碼機制 (mobile) **/
				$scope.caCode = '';

				var currentUrl = window.location.href;
				if(!(currentUrl.indexOf('MobilePlatform.html') >=0)) {
					var url = new URL(currentUrl);
					var isMobile = url.searchParams.get("isMobile");
					if(isMobile) {
						sessionStorage.CMFPG000_MOBILE_LOGIN = isMobile ;
					} else {
						sessionStorage.removeItem('CMFPG000_MOBILE_LOGIN');
					}
				}
			};
			$scope.initData();
			
			$scope.checkIP = function() {
				$scope.sendRecv("CMFPG000", "precheckIP", "com.systex.jbranch.app.server.fps.cmfpg000.LoginPageVO",{},
					    function(tota, isError) {
							if (isError) {

							} else {
								if(tota[0].body) {
									$scope.IPCheck = true;
								} else {
									$scope.IPCheck = false;
									$scope.showMsg('不允許Internet連線');
								}
							 
							}
						}
					);			
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

            // 20181018 add captcha inspection
			$scope.captcha = {
				code : '',
                /** 生成驗證碼 **/
				rand () {
                    var source = '1234567890'.split('');
                    this.code = '';
                    var captchaLength = 4;
                    for(var i = 0; i < captchaLength; i++)
                        this.code += source[Math.floor(Math.random() * source.length)];
                    return this.code;
				},
                /** 干擾線的隨機x坐標值 **/
				lineX() {
                    return Math.floor(Math.random() * 100);
                },
                /** 干擾線的隨機y坐標值 **/
                lineY() {
                    return Math.floor(Math.random() * 50);
                },
				/** 產生圖形 **/
				generate() {
                    var captcha = document.getElementById('loginCaptcha');
                    var cxt = captcha.getContext('2d');
                    captcha.height = captcha.height; // 藉由改變高度來清除畫布
                    /*生成乾擾線20條*/
                    for(var j = 0; j < 20; j++){
                        cxt.strokeStyle = 'rgb(119, 185, 221)';
                        cxt.beginPath();
                        cxt.moveTo(this.lineX(), this.lineY());
                        cxt.lineTo(this.lineX(), this.lineY());
                        cxt.lineWidth = 0.5;
                        cxt.closePath();
                        cxt.stroke();
                    }
                    cxt.fillStyle = 'rgb(45, 122, 168)';
                    cxt.font = 'italic 18px Arial';
                    cxt.fillText(this.rand(), 20, 25); //把rand()生成的隨機數文本填充到canvas中
				},
				/** 初始化 **/
				init() {
					this.generate();
					var refreshBtn = document.getElementById('reCaptcha');
                    refreshBtn.onclick = (e) => {
						e.preventDefault(); //阻止鼠標點擊發生默認的行為
						this.generate();
					};
				},
				/** 驗證是否與驗證碼相符 **/
				isValid(password) {
					return this.code == password;
				}
			}

            /** 當HTML擁有驗證碼等相關HTML元件並且行外登入，才執行驗證碼初始化 **/
            if (document.getElementById('loginCaptcha') &&
                document.getElementById('reCaptcha')) {

            	$scope.sendRecv("CMFPG000", "needCaptcha", "com.systex.jbranch.app.server.fps.cmfpg000.LoginPageVO", {},
                    (tota) => {
                        $scope.needCaptcha = tota[0].body;
						if ($scope.needCaptcha) $scope.captcha.init();
                    }
                );
			}
			
			/** 當HTML擁有檢核IP等相關HTML元件，才執行IP驗證 **/
            if (document.getElementById('needCheckIP')) {
    			$scope.checkIP();
			}

			/** 清除舊驗證碼，產生新驗證碼 **/
			$scope.clearCaptcha = function() {
                $scope.captcha.generate();
                $scope.caCode = '';
			}

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
				// mobile 需要檢核驗證碼
				if ($scope.needCaptcha &&
					!$scope.captcha.isValid($scope.caCode)) {
                    $scope.showErrorMsg('驗證碼錯誤 !');
					$scope.clearCaptcha();
                    return;
				}

				$scope.Show = true;

				// 供socketService.js使用
				$rootScope.TlrID = $scope.loginID;
				$rootScope.BranchID = $scope.branchModel;
				$rootScope.WsId = $scope.wsId;

				getAuthenticate();
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
						console.log('000-tlron tota=' + tota + ', isError=' + isError);
						if (isError) {
							$scope.msgData = tota.body.msgData;
							// mobile 登入失敗須清除驗證碼，並重新產生
                            if ($scope.needCaptcha) $scope.clearCaptcha();
						} else {
							let tempUserInfo = tota[0].body.tempUserInfo;
							sysInfoService.setTempUserInfo(tempUserInfo);

							if(tota[0].body.loginMsg){
								$scope.showWarningMsg(tota[0].body.loginMsg);
							}

							sysInfoService.setCurrentUserId($scope.loginID);

							if((tota[0].body.userList).length === 1){
								sysInfoService.setUserList(tota[0].body.userList);
								$scope.sendRecv("CMFPG000", "tlron", "com.systex.jbranch.app.server.fps.cmfpg000.LoginPageVO",
									{
										'iptAppUsername':tota[0].body.userList[0].EMP_ID,
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
									    'currentUserId': $scope.loginID
									},
								    function(tota, isError) {
										console.log('000-1-tlron tota=' + tota + ', isError=' + isError);
										if (isError) {
											$scope.msgData = tota.body.msgData;
                                            // mobile 登入失敗須清除驗證碼，並重新產生
                                            if ($scope.needCaptcha) $scope.clearCaptcha();
										} else {
											$scope.setSysInfoService(tota[0].body);
										}
									}
								);
							}
							else if((tota[0].body.userList).length > 1) {
								var loginSourceTemp = $scope.loginSourceToken;
								var loginID = $scope.loginID;

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
				console.log("20200525" + JSON.stringify(output));
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
				sysInfoService.setMemLoginFlag(output.memLoginFlag);

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
						window.location = 'MobilePlatform.html';
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
						debugger;
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
				if(!row["LEVEL"+circle+"_MENU_ID"]) return;
				if(circle > 5) return;
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
