/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM190Controller',
	function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM190Controller";
		
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		
		$scope.tabType = $scope.connector('get','tab');
		if (typeof($scope.tabType) === 'undefined') {
			$scope.tabType = 'tab3';
		}

        getParameter.XML(['CRM.CON_DEGREE', 'CAM.LEAD_TYPE', 'CAM.CAMP_PURPOSE'], function(tota) {
			if(tota){
				$scope.mappingSet['CRM.CON_DEGREE'] = tota.data[tota.key.indexOf('CRM.CON_DEGREE')];
				$scope.mappingSet['CAM.LEAD_TYPE'] = tota.data[tota.key.indexOf('CAM.LEAD_TYPE')];
				$scope.mappingSet['CAM.CAMP_PURPOSE'] = tota.data[tota.key.indexOf('CAM.CAMP_PURPOSE')];
			}
		});
        // ===
        
        // 取得參數：CAM.CAMP_PURPOSE
 		$scope.getCampPurpose = function(){
 			$scope.CAMP_PURPOSE = [];
 			$scope.sendRecv("CAM140", "getCampPurpose", "com.systex.jbranch.app.server.fps.cam140.CAM140InputVO", {},
 			function(tota, isError) {
 				if (!isError) {
 					$scope.CAMP_PURPOSE = tota[0].body.resultList;
 				}
 			});
 		}
 		$scope.getCampPurpose();
 		
 		// 依據所選『名單類型』變更『名單目的』下拉選項
 		$scope.getPurpose = function(){
 			$scope.inputVO.camp_purpose = undefined;
 			$scope.PURPOSE = [];
 			if ($scope.inputVO.leadType != undefined) {
				angular.forEach($scope.CAMP_PURPOSE, function(row) {
					if (row.PARAM_DESC.indexOf($scope.inputVO.leadType) >= 0) {
						$scope.PURPOSE.push({LABEL:row.PARAM_NAME, DATA: row.PARAM_CODE});
					}
				});
			}
 			if ($scope.PURPOSE.length == 0) {
 				$scope.PURPOSE = $scope.mappingSet['CAM.CAMP_PURPOSE'];
 			}
 		}
		
		$scope.init = function() {
			$scope.inputVO = {};
		};
		$scope.init();

		$scope.getList = function(flag, type) {
			debugger;
			if (typeof(type) !== 'undefined') {
				$scope.customTabType = type ? type : $scope.customTabType;
				$scope.objForTab.secTab = $scope.customTabType == 'tab97' ? 97 : ($scope.customTabType == 'tab98' ? 98 : 99);
				$scope.customList = [];
				
				// 2017/6/20 add upper
				if($scope.inputVO.custID)
					$scope.inputVO.custID = $scope.inputVO.custID.toUpperCase();
				if (flag) {
					$scope.inputVO.customTabType = $scope.customTabType;
		    		$scope.sendRecv("CAM190", "getList", "com.systex.jbranch.app.server.fps.cam190.CAM190InputVO", $scope.inputVO,
							function(tota, isError) {
		    					$scope.customList = [];
		    				
								if (!isError) {
									if(tota[0].body.customList.length == 0) {
//										$scope.showMsg("ehl_01_common_009");
				            			return;
				            		}
									$scope.customList = tota[0].body.customList;

//									$scope.inputVO.aoCode = $scope.customList[0].AO_CODE;
									$scope.customVO = tota[0].body;
				
									return;
								}
					});
				}
			}
		};
		
		$scope.customQuery = function(type) {
			debugger;
			$scope.searchFlag = true;
			$scope.customTabType = (typeof(type) !== 'undefined' ? type : $scope.customTabType);
			$scope.getList($scope.searchFlag, $scope.customTabType);
		}
		
		$scope.campNameTemp = $scope.connector('get', 'campName');
		$scope.leadTypeTemp = $scope.connector('get', 'leadType');
		$scope.campType 	= $scope.connector('get', 'campType');
		$scope.connector('set', 'campName', '');
		$scope.connector('set', 'leadType', '');
		$scope.connector('set', 'campType', '');
		
		$scope.initCustom = function(){
			$scope.customList = [];
			$scope.objForTab = {};
//			$scope.getAoCode();
			$scope.inputVO = {
					regionID: '',
					opID:  '',
					branchID: '',
					custID: '',
					custName: '',
					aoCode: '', //(projInfoService.getAoCode().length > 0 ? $scope.mappingSet['aoCodeList'][0].DATA : sysInfoService.getUserID()),
					campName: $scope.campNameTemp,
					leadStatus: '',
					vipDegree: '',
					leadDateRange: '',
					leadType: $scope.leadTypeTemp,
					conDegree: '', 
					campType: $scope.campType,
					
					customTabType: $scope.customTabType
			};
			
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "regionID", "REGION_LIST", "opID", "AREA_LIST", "branchID", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region).then(function(data) {
	        	if($scope.AVAIL_REGION.length == 1)
	        		$scope.inputVO.regionID = $scope.AVAIL_REGION[0].REGION_CENTER_ID; 
	        });
	        
	        $scope.pri_id = String(sysInfoService.getPriID());
	        
			$scope.beContactList = [];
			$scope.getExpiredList = [];
			$scope.getClosedList = [];
			$scope.inputVO.aoCode = '';
			
			if (!(typeof($scope.leadTypeTemp) === 'undefined')) {

				if ($scope.pri_id == '004AO' || $scope.pri_id == '004PS' ) {
					if ($scope.AVAIL_REGION.length > 1) {
						$scope.inputVO.regionID = '';
					}
					
					if ($scope.AVAIL_AREA.length > 1) {
						$scope.inputVO.opID = '';
					}
					
					if ($scope.AVAIL_BRANCH.length > 1) {
						$scope.inputVO.branchID = '';
					}
				}
				
				$scope.customQuery($scope.customTabType);
			}
		};
		$scope.initCustom();
		
		// 2017/9/25
		$scope.exportCustom = function() {
			if($scope.customList.length == 0)
				return;
			$scope.sendRecv("CAM190", "exportCust", "com.systex.jbranch.app.server.fps.cam190.CAM190InputVO", {'custom_list': $scope.customList, 'customTabType': $scope.customTabType},
				function(tota, isError) {
					if (!isError) {
						
					}
			});
		};
		
		// 若變更搜尋條件，則需按下「查詢」才可以撈資料
        $scope.changeSearchFlag = function (status) {
        	$scope.searchFlag = status;
        }

		$scope.customTabType = "tab97"; // 預設TAB5開啟「待聯繫名單」TAB
		if (typeof($scope.campNameTemp) === 'undefined' || typeof($scope.leadTypeTemp) === 'undefined') {
			$scope.searchFlag = false; //TAB5的搜尋FLAG，標記是否已按查詢
		} else {
			$scope.searchFlag = true;
			$scope.getList($scope.searchFlag, $scope.customTabType);
		}
		
        $scope.clearList = function () {
        	$scope.custNExpiredList = [];
        	$scope.custNExpiredVO = {};
        	
        	$scope.custExpiredList = [];
        	$scope.custExpiredVO = {};
        	
        	$scope.campNExpiredList = [];
        	$scope.campNExpiredVO = {};
        	
        	$scope.campExpiredList = [];
        	$scope.campExpiredVO = {};
        }
        
        $scope.getCampExpiredList = function() {
        	$scope.sendRecv("CAM190", "getCampExpiredList", "com.systex.jbranch.app.server.fps.cam190.CAM190InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.campExpiredList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}
							$scope.campExpiredList = tota[0].body.campExpiredList;
							$scope.campExpiredVO = tota[0].body;
		
							return;
						}
			});
        }
        
        $scope.getCampNExpiredList = function() {
        	$scope.sendRecv("CAM190", "getCampNExpiredList", "com.systex.jbranch.app.server.fps.cam190.CAM190InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.campNExpiredList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}
							$scope.campNExpiredList = tota[0].body.campNExpiredList;
							$scope.campNExpiredVO = tota[0].body;
		
							return;
						}
			});
        }
        
        $scope.getCustExpiredList = function() {
        	$scope.sendRecv("CAM190", "getCustExpiredList", "com.systex.jbranch.app.server.fps.cam190.CAM190InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.custExpiredList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}
							$scope.custExpiredList = tota[0].body.custExpiredList;
							$scope.custExpiredVO = tota[0].body;
		
							return;
						}
			});
        }
        
        $scope.getCustNExpiredList = function() {
        	$scope.sendRecv("CAM190", "getCustNExpiredList", "com.systex.jbranch.app.server.fps.cam190.CAM190InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.custNExpiredList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
		            			return;
		            		}
							$scope.custNExpiredList = tota[0].body.custNExpiredList;
							$scope.custNExpiredVO = tota[0].body;
							return;
						}
			});
        }

		$scope.goAction = function(type, row, goTab) {
			$scope.tabType = type ? type : $scope.tabType;
        	$scope.mainTab = $scope.tabType == "tab1" ? 0 : $scope.tabType == "tab2" ? 1 : $scope.tabType == "tab3" ? 2 : $scope.tabType == "tab4" ? 3 : 4;
        	if ($scope.tabType == "tab1") {
        		$scope.getCustNExpiredList();
        	} else if ($scope.tabType == "tab2") {
        		$scope.getCustExpiredList();
        	} else if ($scope.tabType == "tab3") {
        		$scope.getCampNExpiredList();
        	} else if ($scope.tabType == "tab4") {
        		$scope.getCampExpiredList();
        	} else if ($scope.tabType == "tab5") {
//        		$scope.getAoCode();
        		$scope.initCustom();
        		
        		if (row != null) {
        			$scope.inputVO.leadType = row.LEAD_TYPE;
        			$scope.inputVO.campName = row.CAMPAIGN_NAME;
        			$scope.inputVO.leadDateRange = '';
        			if (goTab != null) {
        				$scope.customTabType = goTab;
        			} else {
            			$scope.customTabType = "tab97";
        			}
        			
        			if ($scope.pri_id == '004AO') {
        				if ($scope.AVAIL_REGION.length > 1) {
        					$scope.inputVO.regionID = '';
        				}
        				
        				if ($scope.AVAIL_AREA.length > 1) {
        					$scope.inputVO.opID = '';
        				}
        				
        				if ($scope.AVAIL_BRANCH.length > 1) {
        					$scope.inputVO.branchID = '';
        				}
        			}
        			
        			$scope.searchFlag = true;
    				$scope.getList($scope.searchFlag, $scope.customTabType);
    				
    				$timeout(function(){ $scope.searchFlag=true; });
        		}
        	}
        }
        $scope.goAction();
        
        // 連至名單執行紀錄
//        $scope.custConnectData = function(row, tabType) {
//        	$scope.connector('set','custID', row.CUST_ID);
//        	$scope.connector('set','custName', row.CUST_NAME);
//            $scope.connector('set','tab', tabType);
//    		$rootScope.menuItemInfo.url = "assets/txn/CAM200/CAM200.html";
//        }
        
        $scope.custConnectData = function(row, tabType) {
        	var customTabType = $scope.inputVO.customTabType;
        	if(customTabType == "tab97" || customTabType == "tab98" || customTabType == "tab99"){
        		$scope.sendRecv("CAM190", "updateContactInfo", "com.systex.jbranch.app.server.fps.cam190.CAM190InputVO", {custID: row.CUST_ID, sfaLeadID: row.SFA_LEAD_ID},
						function(tota, isError) { 
	        				if(!isError) {
	        					//1740 自訂查詢，按聯絡時，更新成功與失敗訊息
	        					//目前先不用顯示訊息。
//        						var rowUpdate = tota[0].body.rowUpdate;
//            					if(rowUpdate != 1){
//            						$scope.showMsg("更新失敗")
//            					}else {
//            						$scope.showMsg("更新成功")
//            					}
	        				}
				});
    		}
        	var dialog = ngDialog.open({
				template: 'assets/txn/CAM190/CAM190_ROUTE.html',
				className: 'CAM200',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.txnName = "名單執行紀錄";
	        		$scope.routeURL = 'assets/txn/CAM200/CAM200.html';
	        		$scope.connector('set','custID', row.CUST_ID);
	            	$scope.connector('set','custName', row.CUST_NAME);
	                $scope.connector('set','tab', tabType);
	            }]
			});
		};
        
        //Mark: 2016/12/19 modify 連至客戶首頁   BY Stella 
        // 連至客戶首頁
        $scope.custDTL = function(row) {
        	$scope.custVO = {
    				CUST_ID :  row.CUST_ID,
    				CUST_NAME :row.CUST_NAME	
    		}
    		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
        	
        	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			var set = $scope.connector("set","CRM610URL",path);
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
		}
        
});