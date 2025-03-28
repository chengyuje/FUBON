/**================================================================================================
@program: MenuBarController.js
@description: Menu Controller
@version: 1.0.20170823
=================================================================================================*/
eSoafApp.controller("MenuBarController", ['$rootScope', '$scope', '$http', '$cookies', 'socketService', 'sysInfoService', '$timeout', 'withLazyModule', '$q', 'getParameter',
	function($rootScope, $scope, $http, $cookies, socketService, sysInfoService, $timeout, withLazyModule, $q, getParameter)
		{
	
			/** initialize variables **/
			$scope.priID    = sysInfoService.getPriID()[0];
			$scope.UserName = sysInfoService.getUserName();
			$scope.RoleName = sysInfoService.getRoleName();
			$scope.BranchName = sysInfoService.getAvailBranch().length > 1 ? '' : sysInfoService.getBranchName() == 'null' ? '' : $scope.priID == '004AO' ? '' : sysInfoService.getBranchName();

			$scope.isLocal = (sysInfoService.Exclude.IP.indexOf(document.location.hostname)>-1);
			
			/** API **/
			// menu
			$rootScope.GENERATE_MENU = function() {
				$rootScope.MenuBar = angular.copy(sysInfoService.getMenu());
				var IsMobile = sysInfoService.getLoginSourceToken() == 'mobile';
				var pri_id = sysInfoService.getPriID()[0];
				// menu auth
				function mobileauth() {
					var deferred = $q.defer();
					$scope.sendRecv("CMMGR020", "getMobileAUTH", "com.systex.jbranch.app.server.fps.cmmgr020.CMMGR020ItemVO", {},
						function(tota, isError) {
							if (!isError) {
								deferred.resolve(tota[0].body.data);
							}
					});
					return deferred.promise;
				};
				// normal auth
				function checkauth(menus, mobile_list) {
					for (var i = menus.length - 1; i >= 0; i--) {
						if(menus[i].MENU_TYPE == "P") {
							if(!sysInfoService.getAuthorities().MODULEID[sysInfoService.$currentModule].ITEMID[menus[i].MENU_ID]) {
								menus.splice(i, 1);
							} else {
								// 2017/6/7 mobile
								if(IsMobile) {
									if(_.findIndex(mobile_list, function(o) { return o.ITEMID == menus[i].MENU_ID && o.PRIVILEGEID == pri_id; }) == -1)
										menus.splice(i, 1);
								}
							}
			    		}
						else {
							if(menus[i].SUBITEM) {
								checkauth(menus[i].SUBITEM, mobile_list);
								if(menus[i].SUBITEM.length == 0)
									menus.splice(i, 1);
							}
						}
					}
				};
				mobileauth().then(function(data) {
					checkauth($rootScope.MenuBar, data);
				});
//				if(!IsMobile) {
					// 常用連結
					$scope.sendRecv("CRM911", "initial", "com.systex.jbranch.app.server.fps.crm911.CRM911InputVO", {'emp_id':sysInfoService.getUserID()},
						function(tota, isError) {
							if (!isError) {
								var team = [];
								angular.forEach(tota[0].body.resultList, function(row) {
									team.push({"MENU_ID":row.LINK_URL,"MENU_NAME":row.LINK_NAME,"MENU_TYPE":"U"});
					        	});
								$rootScope.MenuBar.push({"MENU_ID":"TEST","MENU_NAME":"常用連結","MENU_TYPE":"F","SEQ_NUM":7,
								    "SUBITEM":[{"MENU_ID":"TEST","MENU_NAME":"常用連結","MENU_TYPE":"F","SEQ_NUM":1,"SUBITEM":team}]});
							}
					});
//				}
			};
			//initialize function
			function platformInitial() {
				$rootScope.ApplicationID = sysInfoService.getApplicationID();
				$rootScope.BranchID = sysInfoService.getBranchID();
				$rootScope.WsID = sysInfoService.getWsID();
				if (sysInfoService.getUserID() == undefined) {
					alert("無法取得使用者帳號");
					return;						
				}
				$rootScope.TlrID = sysInfoService.getUserID();
				loadMenu({'txnName':'首頁','txnId':'HOME','txnPath': [{'MENU_ID':'HOME','MENU_NAME':'首頁'}]});
			}						
			// menu controlls
			$scope.menuVisible = function(action, idx) {
				document.getElementsByClassName("menuBar")[idx].style.opacity = action?"1":"0";
				document.getElementsByClassName("menuBar")[idx].style.visibility = action?"visible":"hidden";
			}
			$scope.menuNon = function() {
				$(".menuBar").each(function(i){
					$(this).css("visibility","hidden");
				});
			}
			//transform Page
			$scope.GeneratePage = function(data) {
				data.txnId = data.txnId.toUpperCase().trim();
				if(data.txnId == "HOME")
					loadMenu(data);
				
				//---- 2016/09/05 walala新增 : 檢核登入者為離職、異動分行、留停者才可使用該功能
				else if(data.txnId == "CRM351") {
					$scope.sendRecv("CRM351", "check", "com.systex.jbranch.app.server.fps.crm351.CRM351InputVO", {'pri_id': sysInfoService.getPriID()[0]},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.checkList.length == 0) {
									$scope.showMsg("組織人員管理功能中有註記離職、異動分行、留停者，才會開放此交接功能");
									return;
								}
								withLazyModule(
										'assets/txn/'+data.txnId+'/'+data.txnId,
							            function() {
											loadMenu(data);
							            },
							            function(err){
							            	$scope.showErrorMsg(err);
							            }
								);
							}
					});

				} else {
					withLazyModule(
							'assets/txn/'+data.txnId+'/'+data.txnId,
				            function() {
								loadMenu(data);
				            },
				            function(err){
				            	$scope.showErrorMsg(err);
				            }
					);
				}
			};
			// load menu
			function loadMenu(data) {
				sysInfoService.$currentTxn = data.txnId;
				// // 右鍵
				// var unSecurity = true;
	            // if(sysInfoService.getAuthorities() && sysInfoService.getAuthorities().MODULEID[sysInfoService.$currentModule].ITEMID[sysInfoService.$currentTxn] && sysInfoService.getAuthorities().MODULEID[sysInfoService.$currentModule].ITEMID[sysInfoService.$currentTxn].FUNCTIONID['security']){
	            // 	unSecurity = false;
	            // }
				// 禁用右鍵、拖曳與選中功能
			    document.oncontextmenu = new Function("return false ");
			    document.ondragstart = new Function("return false ");
			    document.onselectstart = new Function("return false ");
			    
			    // 2025/01/08 Steven:以下為控制PrintScreen、非焦點視窗的PrintScreen的寫法，目前先註解。Start
			    // 狀態變數模擬黑屏控制
//			    let showBlackScreen = false;
//
//			    // 黑屏 DOM 元素
//			    let blackScreenElement = document.getElementById("black-screen");
//			    if (!blackScreenElement) {
//			        blackScreenElement = document.createElement("div");
//			        blackScreenElement.id = "black-screen";
//			        blackScreenElement.style.cssText = `
//			            width: 100vw; height: 100vh; background-color: black; color: white;
//			            display: none; justify-content: center; align-items: center; font-size: 24px; 
//			            font-weight: bold; position: fixed; top: 0; left: 0; z-index: 9999;
//			        `;
//			        blackScreenElement.innerText = "禁止截圖";
//			        document.body.appendChild(blackScreenElement);
//			    }
//
//			    // 顯示或隱藏黑屏的函數
//			    function toggleBlackScreen(show) {
//			        showBlackScreen = show;
//			        blackScreenElement.style.display = show ? "flex" : "none";
//			    }
//
//			    // 鍵盤事件處理
//			    const keysPressed = { ShiftLeft: false, MetaLeft: false };
//
//			    document.onkeydown = function (event) {
//			        if (event.code in keysPressed) {
//			            keysPressed[event.code] = true;
//			            if (keysPressed.ShiftLeft && keysPressed.MetaLeft) {
//			                toggleBlackScreen(true);
//			                console.log("禁止截圖");
//			                return false;
//			            }
//			        }
//			    };
//
//			    document.onkeyup = function (event) {
//			        if (event.key === "PrintScreen") {
//			            navigator.clipboard.writeText(" ");
//			            toggleBlackScreen(true);
//			            console.log("禁止截圖");
//			            setTimeout(() => toggleBlackScreen(false), 4000);
//			        }
//			        if (event.code in keysPressed) {
//			            keysPressed[event.code] = false;
//			            setTimeout(() => toggleBlackScreen(false), 4000);
//			        }
//			    };
//
//			    // 視窗焦點事件處理
//			    window.onfocus = function () {
//			        console.log("視窗獲得焦點");
//			        toggleBlackScreen(false);
//			    };
//
//			    window.onblur = function () {
//			        console.log("視窗失去焦點");
//			        toggleBlackScreen(true);
//			    };
			    // 2025/01/08 Steven:以上為控制PrintScreen、非焦點視窗的PrintScreen的寫法，目前先註解。End
			    
	            // CTRL+C, PRINT
	            var screenSecurity = false;
	            if(sysInfoService.getAuthorities() && sysInfoService.getAuthorities().MODULEID[sysInfoService.$currentModule].ITEMID[sysInfoService.$currentTxn] && sysInfoService.getAuthorities().MODULEID[sysInfoService.$currentModule].ITEMID[sysInfoService.$currentTxn].FUNCTIONID['screen'])
	            	screenSecurity = true;
	            var ctrlDown = false;
	            $(document).on("keydown", function(e) {
	                if (e.keyCode == 17 || e.keyCode == 91) ctrlDown = true;
	            }).on("keyup", function(e) {
	                if (e.keyCode == 17 || e.keyCode == 91) ctrlDown = false;
	            });
	            $(document).on("keydown", function(e) {
	                if (ctrlDown && e.keyCode == 67) {
	                	var aux = document.createElement("input");
	                	aux.setAttribute("value", "");
	            		document.body.appendChild(aux);
	            		aux.select();
	            		document.execCommand("copy");
	            		document.body.removeChild(aux);
	                }
	            }).on("keyup", function(e) {
	            	if(screenSecurity && e.keyCode == 44) {
	                	var aux = document.createElement("input");
	            		aux.setAttribute("value", "");
	            		document.body.appendChild(aux);
	            		aux.select();
	            		document.execCommand("copy");
	            		document.body.removeChild(aux);
	                }
	            });
	            // watermark
	            document.getElementById('watermark').className = "hidden";
	            if(sysInfoService.getAuthorities() && sysInfoService.getAuthorities().MODULEID[sysInfoService.$currentModule].ITEMID[sysInfoService.$currentTxn] && sysInfoService.getAuthorities().MODULEID[sysInfoService.$currentModule].ITEMID[sysInfoService.$currentTxn].FUNCTIONID['watermark']){
	            	document.getElementById('watermark').className = "watermark";
	            }
	            if(data.txnId != "HOME")
					$scope.txnPath = data.txnPath;	// Add: 2016/02/17 ArthurKO
				else
					$scope.txnPath = undefined;
	            $rootScope.menuItemInfo = {url:'', txnId: data.txnId, txnName:data.txnName};
	            $timeout(function() {$rootScope.menuItemInfo = {url:'assets/txn/'+data.txnId+'/'+data.txnId+'.html', txnId: data.txnId, txnName:data.txnName, txnPath:$scope.txnPath};}).then(function(){
	            	$scope.menuNon();
	            });
			}
			// local query
			$scope.customEnter = function(txnCode) {
				txnCode = txnCode.toUpperCase();
				$scope.GeneratePage({'txnName':txnCode,'txnId':txnCode,'txnPath':[]});
			};			
			// Path row
			$rootScope.MenuBarLink = function(data,index) {
				// 原路徑 2016/12/26
	        	var oldPath = $rootScope.menuItemInfo.txnPath;
				//Select Case by HOME || TXN.
				switch(index){
					case 0:
						$scope.GeneratePage({'txnName':'HOME','txnId':'HOME','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'}]});
						break;
					case 1:
						for (var i=0; i<$rootScope.MenuBar.length; i+=1) {
							//Level 1 : Check LEVEL1 (Type: P)
							if ($rootScope.MenuBar[i].MENU_ID == data.MENU_ID && $rootScope.MenuBar[i].MENU_TYPE == "P") {
								var delIndex = oldPath.map(function(e) { return e.MENU_ID; }).indexOf(data.MENU_ID);
								oldPath.splice(-1, oldPath.length - 1 - delIndex);
								$scope.GeneratePage({'txnName':$rootScope.MenuBar[i].MENU_NAME,'txnId':$rootScope.MenuBar[i].MENU_ID,'txnPath':oldPath});
								break;
							}
							//Level 2 : Check LEVEL1 (Type: F) -> LEVEL2 (Type: P)
							if ($rootScope.MenuBar[i].MENU_TYPE == "F") {
								for (var a=0; a<$rootScope.MenuBar[i].SUBITEM.length; a+=1) {
									if ($rootScope.MenuBar[i].SUBITEM[a].MENU_ID == data.MENU_ID && $rootScope.MenuBar[i].SUBITEM[a].MENU_TYPE == "P") {
										var delIndex = oldPath.map(function(e) { return e.MENU_ID; }).indexOf(data.MENU_ID);
										oldPath.splice(-1, oldPath.length - 1 - delIndex);
										$scope.GeneratePage({'txnName':$rootScope.MenuBar[i].SUBITEM[a].MENU_NAME,'txnId':$rootScope.MenuBar[i].SUBITEM[a].MENU_ID,'txnPath':oldPath});
										break;
									}
									//Level 3 : Check LEVEL2 (Type: F) -> LEVEL3 (Type: P)
									if ($rootScope.MenuBar[i].SUBITEM[a].MENU_TYPE == "F") {
										for (var b=0; b<$rootScope.MenuBar[i].SUBITEM[a].SUBITEM.length; b+=1) {
											if ($rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].MENU_ID == data.MENU_ID && $rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].MENU_TYPE == "P") {
												var delIndex = oldPath.map(function(e) { return e.MENU_ID; }).indexOf(data.MENU_ID);
												oldPath.splice(-1, oldPath.length - 1 - delIndex);
												$scope.GeneratePage({'txnName':$rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].MENU_NAME,'txnId':$rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].MENU_ID,'txnPath':oldPath});
												break;
											}
											if ($rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].MENU_TYPE == "F") {
												for (var c=0; c<$rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].SUBITEM.length; c+=1) {
													if ($rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].SUBITEM[c].MENU_ID == data.MENU_ID && $rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].SUBITEM[c].MENU_TYPE == "P") {
														var delIndex = oldPath.map(function(e) { return e.MENU_ID; }).indexOf(data.MENU_ID);
														oldPath.splice(-1, oldPath.length - 1 - delIndex);
														$rootScope.GeneratePage({'txnName':$rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].SUBITEM[c].MENU_NAME,'txnId':$rootScope.MenuBar[i].SUBITEM[a].SUBITEM[b].SUBITEM[c].MENU_ID,'txnPath':oldPath});
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						break;
				} //switch END
			}; //[Link] END
			
			/** render **/
			$rootScope.GENERATE_MENU();
			platformInitial();
			$rootScope.GeneratePage = $scope.GeneratePage;	// Add: 2016/05/10 ArthurKO
			
			/** logger **/
//			console.log('MenuList='+JSON.stringify($rootScope.MenuBar));
//			console.log('refreshAuthorities='+JSON.stringify(sysInfoService.getAuthorities()));
//			console.log('AvailBranchList='+JSON.stringify(sysInfoService.getAvailBranch()));
					
}]);