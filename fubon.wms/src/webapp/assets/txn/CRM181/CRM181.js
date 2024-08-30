'use strict';
eSoafApp.controller('CRM181Controller', 
    function($rootScope, $scope, sysInfoService, $filter, $controller, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM181Controller";
		$scope.data = '';
		$scope.pri = String(sysInfoService.getPriID());
		$scope.ao_code = String(sysInfoService.getAoCode());
		
		//待覆核權限
		if ($scope.pri == '001' || $scope.pri == '002' || $scope.pri == '003' || $scope.pri == '004' ||
			$scope.pri == '005' || $scope.pri == '014' || $scope.pri == '023' || 
			$scope.pri == 'UHRM002') {
			$scope.pri_type = 'hide';
		} else {			
			$scope.pri_type = 'show';
		}
		
		$scope.inputVO.pri_type = $scope.pri_type;
		
		// get xml
		getParameter.XML(['CRM.FRQ_TYPE'], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.FRQ_TYPE'] = totas.data[totas.key.indexOf('CRM.FRQ_TYPE')];
			}
		});
		
        // init
		$scope.init = function() {
			$scope.inputVO = {
				empId : sysInfoService.getUserID(),
				empNeme : sysInfoService.getUserName(),
				roleId : projInfoService.getRoleID(),
				priId : String(sysInfoService.getPriID()),
				regionId:sysInfoService.getRegionID(),
				roleId : '',
				role_link_yn : '',
				frq_type : '',
				frq_mwd : '',
				display_no : '',
				rpt_name : '',
				rpt_prog_url : '',
				call_func_name : '',
				pass_params : '',
				display_day : 0,
				dataType : '',
				seqNo : '',
				seqM_No : '',
				pri_type : $scope.pri_type,
				memLoginFlag: String(sysInfoService.getMemLoginFlag())
			};
		};
		$scope.init();
		$scope.advanced_1 = false;		

		//畫面載入
		$scope.initial = function() {
			$scope.resultList = [];
			$scope.displayNo1 = 0;
			$scope.displayNo2 = 0;
			$scope.displayNo9 = 0;
			//取得清單筆數
			$scope.reviewCount = 0;
			$scope.sendRecv("CRM181", "getMustDoListCnt", "com.systex.jbranch.app.server.fps.crm181.CRM181InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {						
						//待覆核總數
						$scope.reviewCount = parseInt(tota[0].body.reviewCount);
						
						$scope.cus130Count  = tota[0].body.cus130Count;
						$scope.sqm120Count  = tota[0].body.sqm120Count;
						$scope.sqm320Count  = tota[0].body.sqm320Count;
						$scope.crm8502Count = tota[0].body.crm8502Count;
						
						if (tota[0].body.resultList.length == 0) {
							return;
						} else {
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							
							//個數計算/日期格式轉換
							/** 日期轉換:yyyymmdd = yyyy * 10000 + mm * 100 + dd **/
							$scope.today = new Date();
							$scope.today = ($scope.today.getFullYear()*10000) + (($scope.today.getMonth()+1)*100) + $scope.today.getDate();
							
							angular.forEach($scope.resultList, function(row, index, objs){
								//日期格式轉換
								row.checkDay = ($scope.toJsDate(row.EFFDATE).getFullYear()*10000) + (($scope.toJsDate(row.EFFDATE).getMonth()+1)*100) + $scope.toJsDate(row.EFFDATE).getDate();
								// 內部事件通知
								if(row.DISPLAY_NO.substr(0,1) == '1'){
									$scope.displayNo1 += 1 ;
								}
								
								// 重要提醒報表
								if(row.DISPLAY_NO.substr(0,1) == '2'){
									$scope.displayNo2 += 1 ;
								}
								
								// 其他
								if(row.DISPLAY_NO.substr(0,1) != '1' && row.DISPLAY_NO.substr(1,1) != '2' ) {
									$scope.displayNo9 += 1 ;
								}
							});
						}
					}
			});
			
			// 近三日國外匯入款入帳清單
			if ($scope.pri == '002' || $scope.pri == '009' || $scope.pri == '010' || $scope.pri == '011') {
				$scope.sendRecv("CRM181", "getFetlirW", "com.systex.jbranch.app.server.fps.crm181.CRM181InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {		
								$scope.fetilrListCount = tota[0].body.fetilrListCount;
								$scope.fetilrListW = tota[0].body.fetilrListW;
							}
				});
			}
			
			//取得MGM活動待覆核案件數
			$scope.mgm113Count = 0;
			if($scope.pri < '040' || $scope.pri == '047' || $scope.pri == '048'){
				$scope.sendRecv("MGM113", "reviewCount", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList != undefined && tota[0].body.resultList.length > 0){
									if(tota[0].body.resultList[0].COUNT == null){
										$scope.mgm113Count = 0;
									}else{
										$scope.mgm113Count = parseInt(tota[0].body.resultList[0].COUNT);
									}								
								}
							}
				});				
			}
			
			//2022年調換換手系統管控需求(未輪調) 分行主管異常通報為"正常"，但電子/實體回函有異常，於首頁提醒
			$scope.rotationBRMsg = "";
			if($scope.pri == '009' || $scope.pri == '010' || $scope.pri == '011') {
				$scope.sendRecv("CRM181", "get2022RotationBRMsg", "com.systex.jbranch.app.server.fps.crm181.CRM181InputVO", {}, 
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body && tota[0].body.pms429Msg && tota[0].body.pms429Msg != ""){
								debugger
								$scope.rotationBRMsg = tota[0].body.pms429Msg;				
							}
						}
				});				
			}
		};
		
		$scope.initial();
		
		$scope.openFetilrW = function (fetilrListW) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM181/CRM181_FETILRW.html',
				className: 'CRM181',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.fetilrListW = fetilrListW;
                }]
			});
		};
		
	    $scope.goMAO151 = function(row) {
	    	if($scope.mgm113Count != undefined && $scope.mgm113Count > 0){
	    		$scope.connector('set','mgm113_flag', $scope.mgm113Count);
	    	} else {
	    		$scope.connector('set','mgm113_flag', 0);
	    	}
	    	
	     	$rootScope.menuItemInfo.url = "assets/txn/MAO151/MAO151.html";
	    };
		
	    //展開表格
		$scope.goCRM181_1 = function(data) {
			// 內部事件通知
			if(data == '1' && $scope.displayNo1 != 0) {
				if ($scope.advanced_1 == false) {
					$scope.advanced_1 = true;
			   	} else {
					$scope.advanced_1 = false;
				}	
			}							
		};
		
		$scope.goCUS130 = function () {
			$scope.connector('set','passParams', 'Y');
			$scope.GeneratePage({'txnName':'CUS140','txnId':'CUS140','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'},{'MENU_ID':'CUS140','MENU_NAME':'調查回報計劃執行'}]});
		};
		
		$scope.goSQM120 = function () {
			$scope.connector('set','LinkFlag_SQM120', 'Y');
			$scope.GeneratePage({'txnName':'SQM120','txnId':'SQM120','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'},{'MENU_ID':'SQM120','MENU_NAME':'問卷報告回覆'}]});
		};
		
		$scope.goMGM110 = function () {
			$scope.GeneratePage({'txnName':'MGM110','txnId':'MGM110','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'},{'MENU_ID':'MGM110','MENU_NAME':'MGM活動新增/查詢'}]});
		}
		
		$scope.goSQM320 = function () {
			$scope.connector('set','LinkFlag_SQM320', 'Y');
			$scope.GeneratePage({'txnName':'SQM320','txnId':'SQM320','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'},{'MENU_ID':'SQM320','MENU_NAME':'客戶服務定期查核'}]});
		};
		
		$scope.goCRM990 = function () {
			$scope.GeneratePage({'txnName':'CRM990','txnId':'CRM990','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'},{'MENU_ID':'CRM990','MENU_NAME':'客訴管理'}]});
		}
		
		$scope.goCRM8502 = function () {
			$scope.connector('set','LinkFlag_CRM8502', {linked : "Y", lastDate : $scope.crm8502Count.LAST_DATE});
			$scope.GeneratePage({'txnName':'CRM8502','txnId':'CRM8502','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'},{'MENU_ID':'CRM8502','MENU_NAME':'資況表列印記錄'}]});
		};
		
		$scope.goKYC320 = function () {
			$scope.connector('set','LinkFlag_KYC320', 'Y');
			$scope.GeneratePage({'txnName':'KYC320','txnId':'KYC320','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'},{'MENU_ID':'KYC320','MENU_NAME':'KYC問卷客戶記錄查詢'}]});
		};
		
		$scope.goPMS429 = function () {
			$scope.connector('set','LinkFlag_PMS429', {linked: "Y", rotationBRMsg: $scope.rotationBRMsg});
			$scope.GeneratePage({'txnName':'PMS429','txnId':'PMS429','txnPath':[{'MENU_ID':'HOME','MENU_NAME':'首頁'},{'MENU_ID':'PMS429','MENU_NAME':'帳務確認管理報表(輪調/換手)'}]});
		};
		
		$scope.report = function (row) {
			$scope.inputVO = {
				empId : sysInfoService.getUserID(),
				empNeme : sysInfoService.getUserName(),
				roleId : projInfoService.getRoleID(),
				priId : row.PRIVILEGEID,
				role_link_yn : row.ROLE_LINK_YN,     
				frq_type : row.FRQ_TYPE,
				frq_mwd : row.FRQ_MWD,
				display_no : row.DISPLAY_NO,
				rpt_name : row.RPT_NAME,
				rpt_prog_url : row.RPT_PROG_URL,
				call_func_name : row.CALL_FUNC_NAME,
				pass_params : row.PASS_PARAMS,
				display_day : row.DISPLAY_DAY,
				seqNo : row.SEQ,
				seqM_No : row.SEQ_M,
				memLoginFlag: String(sysInfoService.getMemLoginFlag())
			};
			
			$scope.sendRecv("CRM181", "jumpToRptPage", "com.systex.jbranch.app.server.fps.crm181.CRM181InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (!isError) {
						if(tota.length == 0) {
							$scope.showMsg("ehl_01_common_008");
						}
					}
			});
			
		    $scope.connector('set','passParams', row.PASS_PARAMS);
		    
		    if(row.RPT_PROG_URL == "PMS421"){
				$scope.connector('set','LinkFlag_PMS421', 'Y');
		    }
		    
		    $rootScope.menuItemInfo.url = 'assets/txn/'+row.RPT_PROG_URL+'/'+row.RPT_PROG_URL+'.html';	
		};
		
});