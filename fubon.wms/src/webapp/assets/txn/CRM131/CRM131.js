
'use strict';
eSoafApp.controller('CRM131Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM131Controller";
	
	$scope.init = function () {
		$scope.inputVO = {
			mroleid : '',
			pri_id: String(sysInfoService.getPriID()), 
			memLoginFlag: String(sysInfoService.getMemLoginFlag())
		}
		
		$scope.roleId = sysInfoService.getRoleID();
	};
	$scope.init();

	$scope.loginRole = function (){
		$scope.sendRecv("CRM131", "login", "com.systex.jbranch.app.server.fps.crm131.CRM131InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if (tota[0].body.privilege.length == 0) {
        			return;
        		} else {
					$scope.privilege = tota[0].body.privilege;
					$scope.inputVO.mroleid = $scope.privilege[0].COUNTS;
					$scope.data();
        		}
			}
		})
	};
	
	$scope.loginRole();
	
	$scope.data = function () { 
		$scope.resultList = [];
		if ($scope.inputVO.mroleid == '0' || $scope.inputVO.mroleid == '4') { // 20210420 add by ocean => 君榮 => || $scope.inputVO.mroleid=='1'|| $scope.inputVO.mroleid=='2'
			//聯繫名單-理專、消金PS
			$scope.sendRecv("CRM131", "initial", "com.systex.jbranch.app.server.fps.crm131.CRM131InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					if (tota[0].body.resultList.length == 0) {
						$scope.inputVO.TOT_CONTROL_CUST    = 0;
						$scope.inputVO.TOT_NEC_NOTIFY_CUST = 0;
						$scope.inputVO.TOT_MARKETING_CUST  = 0;
						$scope.inputVO.TOT_LEAVE_INFO_CUST = 0;
						$scope.inputVO.TOT_REFER_INFO_CUST = 0;
						$scope.inputVO.TOT_OTHER_CUST 	   = 0;
						
//						$scope.inputVO.CONTACT_CUST = 0; 
//            			$scope.inputVO.TOTAL_CONTROL_CUST = 0;
//            			$scope.inputVO.CONTACTED_CUST = 0;
//            			$scope.inputVO.CMPLT_R = 0;
						return;
            		} else {
            			$scope.resultList = tota[0].body.resultList;
            			
            			$scope.inputVO.TOT_CONTROL_CUST    = $scope.resultList[0].TOT_CONTROL_CUST;
						$scope.inputVO.TOT_NEC_NOTIFY_CUST = $scope.resultList[0].TOT_NEC_NOTIFY_CUST;
						$scope.inputVO.TOT_MARKETING_CUST  = $scope.resultList[0].TOT_MARKETING_CUST;
						$scope.inputVO.TOT_LEAVE_INFO_CUST = $scope.resultList[0].TOT_LEAVE_INFO_CUST;
						$scope.inputVO.TOT_REFER_INFO_CUST = $scope.resultList[0].TOT_REFER_INFO_CUST;
						$scope.inputVO.TOT_OTHER_CUST 	   = $scope.resultList[0].TOT_OTHER_CUST;
						
//            			$scope.inputVO.CONTACT_CUST = $scope.resultList[0].CONTACT_CUST; 
//            			$scope.inputVO.TOTAL_CONTROL_CUST = $scope.resultList[0].TOTAL_CONTROL_CUST;
//            			$scope.inputVO.CONTACTED_CUST = $scope.resultList[0].CONTACTED_CUST;
//            			$scope.inputVO.CMPLT_R = $scope.resultList[0].CMPLT_R;
//            			
//            			$scope.inputVO.NEAR_UNDERSERV_CNT = $scope.resultList[0].NEAR_UNDERSERV_CNT;
//            			$scope.inputVO.BELLOW_UNDERSERV_CNT = $scope.resultList[0].BELLOW_UNDERSERV_CNT;
            			
            			// 2018/4/3 撈出理專/PS "本日有線上留資名單需處理"
            			if ($scope.resultList[0].LEAVE_INFO_YN == 'Y') {
            				$confirm({text12: '今天有<span style="color: red;">『線上留資名單』</span>，快跟客戶聯絡哦！', title: '提醒', hideCancel: true}, {size: 'sm'}).then(function() {
            					
            				});
            			}
            		}
				}
			});
		}
		
		//聯絡名單-主管
		if(($scope.inputVO.mroleid == '3' || $scope.inputVO.mroleid == '5') && $scope.inputVO.pri_id != '013' && $scope.inputVO.pri_id != '012') { // 20210420 add by ocean => 君榮=> || $scope.inputVO.mroleid == '2' || $scope.inputVO.mroleid == '1'
			
			$scope.sendRecv("CRM131", "inquire", "com.systex.jbranch.app.server.fps.crm131.CRM131InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					if (tota[0].body.resultList.length == 0) {
						$scope.inputVO.RM_TOT_CONTROL_CUST = 0;
						$scope.inputVO.RM_TOT_NEC_NOTIFY_CUST = 0;
						$scope.inputVO.RM_TOT_MARKETING_CUST = 0;
						$scope.inputVO.RM_TOT_LEAVE_INFO_CUST = 0;
						$scope.inputVO.RM_TOT_REFER_INFO_CUST = 0;
						$scope.inputVO.RM_TOT_OTHER_CUST = 0;
						
						$scope.inputVO.M_CONTACT_CUST = 0;
            			$scope.inputVO.WAIT_DISPATCH_LEADS = 0;
            			
            			$scope.inputVO.NRM_TOT_CONTROL_CUST = 0;
            			$scope.inputVO.NRM_TOT_NEC_NOTIFY_CUST = 0;
            			$scope.inputVO.NRM_TOT_MARKETING_CUST = 0;
            			$scope.inputVO.NRM_TOT_LEAVE_INFO_CUST = 0;
            			$scope.inputVO.NRM_TOT_REFER_INFO_CUST = 0;
            			$scope.inputVO.NRM_TOT_OTHER_CUST = 0;
						
//            			$scope.inputVO.RM_CONTACT_CUST = 0; 
//            			$scope.inputVO.RM_TOT_CONTROL_CUST = 0;
//            			$scope.inputVO.RM_CONTACTED_CUST = 0;
//            			$scope.inputVO.RM_CMPLT_R = 0;
//            			$scope.inputVO.M_CONTACT_CUST = 0;
//            			$scope.inputVO.WAIT_DISPATCH_LEADS = 0;
//            			
//            			$scope.inputVO.NRM_CONTACT_CUST = 0;
//            			$scope.inputVO.NRM_TOT_CONTROL_CUST = 0;
//            			$scope.inputVO.NRM_CONTACTED_CUST = 0;
//            			$scope.inputVO.NRM_CMPLT_R = 0;
//            			
//            			$scope.inputVO.NEAR_UNDERSERV_CNT = 0;
//            			$scope.inputVO.BELLOW_UNDERSERV_CNT = 0;
            			
            			return;
            		} else {
            			$scope.resultList = tota[0].body.resultList;
            			
            			$scope.inputVO.RM_TOT_CONTROL_CUST = $scope.resultList[0].RM_TOT_CONTROL_CUST;
						$scope.inputVO.RM_TOT_NEC_NOTIFY_CUST = $scope.resultList[0].RM_TOT_NEC_NOTIFY_CUST;
						$scope.inputVO.RM_TOT_MARKETING_CUST = $scope.resultList[0].RM_TOT_MARKETING_CUST;
						$scope.inputVO.RM_TOT_LEAVE_INFO_CUST = $scope.resultList[0].RM_TOT_LEAVE_INFO_CUST;
						$scope.inputVO.RM_TOT_REFER_INFO_CUST = $scope.resultList[0].RM_TOT_REFER_INFO_CUST;
						$scope.inputVO.RM_TOT_OTHER_CUST = $scope.resultList[0].RM_TOT_OTHER_CUST;
						
            			$scope.inputVO.M_CONTACT_CUST = $scope.resultList[0].M_CONTACT_CUST;
            			$scope.inputVO.WAIT_DISPATCH_LEADS = $scope.resultList[0].WAIT_DISPATCH_LEADS;
            			
            			$scope.inputVO.NRM_TOT_CONTROL_CUST = $scope.resultList[0].NRM_TOT_CONTROL_CUST;
            			$scope.inputVO.NRM_TOT_NEC_NOTIFY_CUST = $scope.resultList[0].NRM_TOT_NEC_NOTIFY_CUST;
            			$scope.inputVO.NRM_TOT_MARKETING_CUST = $scope.resultList[0].NRM_TOT_MARKETING_CUST;
            			$scope.inputVO.NRM_TOT_LEAVE_INFO_CUST = $scope.resultList[0].NRM_TOT_LEAVE_INFO_CUST;
            			$scope.inputVO.NRM_TOT_REFER_INFO_CUST = $scope.resultList[0].NRM_TOT_REFER_INFO_CUST;
            			$scope.inputVO.NRM_TOT_OTHER_CUST = $scope.resultList[0].NRM_TOT_OTHER_CUST;
            			
//            			$scope.inputVO.RM_CONTACT_CUST = $scope.resultList[0].RM_CONTACT_CUST; 
//            			$scope.inputVO.RM_TOT_CONTROL_CUST = $scope.resultList[0].RM_TOT_CONTROL_CUST;
//            			$scope.inputVO.RM_CONTACTED_CUST = $scope.resultList[0].RM_CONTACTED_CUST;
//            			$scope.inputVO.RM_CMPLT_R = $scope.resultList[0].RM_CMPLT_R;
//            			$scope.inputVO.M_CONTACT_CUST = $scope.resultList[0].M_CONTACT_CUST;
//            			$scope.inputVO.WAIT_DISPATCH_LEADS = $scope.resultList[0].WAIT_DISPATCH_LEADS;
//            			
//            			$scope.inputVO.NRM_CONTACT_CUST = $scope.resultList[0].NRM_CONTACT_CUST;
//            			$scope.inputVO.NRM_TOT_CONTROL_CUST = $scope.resultList[0].NRM_TOT_CONTROL_CUST;
//            			$scope.inputVO.NRM_CONTACTED_CUST = $scope.resultList[0].NRM_CONTACTED_CUST;
//            			$scope.inputVO.NRM_CMPLT_R = $scope.resultList[0].NRM_CMPLT_R;
//            			
//            			$scope.inputVO.NEAR_UNDERSERV_CNT = $scope.resultList[0].NEAR_UNDERSERV_CNT;
//            			$scope.inputVO.BELLOW_UNDERSERV_CNT = $scope.resultList[0].BELLOW_UNDERSERV_CNT;
            			
            			// 2018/4/3 撈出主管 "本日有線上留資名單需分派"
    					if ($scope.inputVO.pri_id == '009' || $scope.inputVO.pri_id == '010' || $scope.inputVO.pri_id == '011' || $scope.inputVO.pri_id == '012') {
    						if ($scope.resultList[0].LEAVE_INFO_YN == 'Y') {
                				$confirm({text12: '本日有<span style="color: red;">線上留資名單</span>需分派', title: '提醒', hideCancel: true}, {size: 'sm'}).then(function() {
                					
                				});
                			}
    					}
            		}
				}
			});
			
			if(projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["CAM220"])
				$scope.CanCAM220 = projInfoService.getAuthorities().MODULEID[projInfoService.$currentModule].ITEMID["CAM220"].FUNCTIONID["maintenance"];
			else
				$scope.CanCAM220 = false;
		}
	};
	
	$scope.detail = function(set, tabType, leadTypeFlag, campType) {
		var set =  set.toString();
		
		switch (set) {
		case '1':
			// 畫面跳轉至：行銷活動管理 >> 名單執行 >> 名單查詢 >> 自訂查詢
//			$scope.connector('set', 'tab', 'tab1');
			$scope.connector('set', 'tab', 'tab5');
			$rootScope.menuItemInfo.url = "assets/txn/CAM190/CAM190.html";
			
			break;
		case '2':
			// 畫面跳轉至：行銷活動管理 >> 分行名單管理 >> 分行全部名單(含改派/作廢) >> 轄下理專名單執行現狀(帶入：名單分類)
			$scope.connector('set', 'tab', tabType);
			$scope.connector('set', 'tabNumber', (tabType + 1 + "") );
			$scope.connector('set', 'leadType', leadTypeFlag);
			$scope.connector('set', 'campType', campType);
			$rootScope.menuItemInfo.url = "assets/txn/CAM210/CAM210.html";
			
			break;
		case '3':
			// 畫面跳轉至：行銷活動管理 >> 分行名單管理 >> 分行名單分派
			$rootScope.menuItemInfo.url = "assets/txn/CAM220/CAM220.html";

			break;
		case '4':
			$scope.connector('set', 'tab', tabType);
			$rootScope.menuItemInfo.url = "assets/txn/CRM131/CRM131_USRPT.html";

			break;
		case '9':
			// 畫面跳轉至：行銷活動管理 >> 名單執行 >> 名單查詢 >> 自訂查詢(帶入：名單分類)
			$scope.connector('set', 'tab', 'tab5');
			$scope.connector('set', 'leadType', leadTypeFlag);
			$scope.connector('set', 'campType', campType);
			$rootScope.menuItemInfo.url = "assets/txn/CAM190/CAM190.html";

			break;
		case '5':
			$scope.connector('set', 'tab', tabType);
			$scope.connector('set', 'tabNumber', (tabType + 1 + "") );
			$scope.connector('set', 'leadType', leadTypeFlag);
			$rootScope.menuItemInfo.url = "assets/txn/CAM211/CAM211.html";
			
			break;
		}
	};
});