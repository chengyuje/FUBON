/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MTC110_MAINTAINController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, sysInfoService, getParameter, $q, $filter) {
	$controller('BaseController', {$scope: $scope});
	$controller('RegionController', {$scope: $scope});
	
	$scope.controllerName = "MTC110_MAINTAINController";
	
	// filter
	getParameter.XML(["MTC.FOREIGN_CURR", "MTC.LEG_AGENT_REL"], function(totas) {
		if (totas) {
			$scope.currList1 = totas.data[totas.key.indexOf('MTC.FOREIGN_CURR')];
//			$scope.LEG_AGENT_REL = totas.data[totas.key.indexOf('MTC.LEG_AGENT_REL')];
		}
	});
	
	// 取得他行下拉
	$scope.sendRecv("MTC110", "getOtrBank", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", {}, 
		function(tota, isError) {
			if (!isError) {
				$scope.otrBankList = [];
				angular.forEach(tota[0].body.resultList, function(row) {
					$scope.otrBankList.push({LABEL: row.LNAME, DATA: row.BANKID});
				});
			}
	});
	
	// 標準收費
	$scope.SIGNING_FEE_NTD = 3000;
	$scope.MODIFY_FEE_NTD = 1000;
	$scope.SIGNING_FEE_USD = 100;
	$scope.MODIFY_FEE_USD = 50;
	$scope.MNG_FEE_RATE1 = 0.5;
	$scope.MNG_FEE_RATE2 = 0.4;
	$scope.MNG_FEE_RATE3 = 0.3;
	$scope.MNG_FEE_MIN_NTD = 1000;
	$scope.MNG_FEE_MIN_USD = 30;
	
	// date picker
//	$scope.Birth_DateOptions = {
//		maxDate: new Date()
//	};
//	$scope.model = {};
//	$scope.open = function($event, elementOpened) {
//		$event.preventDefault();
//		$event.stopPropagation();
//		$scope.model[elementOpened] = !$scope.model[elementOpened];
//	};
	
	//
	$scope.initAllFee = function() {
		if ($scope.inputVO.CON_CURR == 'T') {
			// 台幣
			$scope.inputVO.SIGNING_FEE 	= $scope.SIGNING_FEE_NTD;
			$scope.inputVO.MODIFY_FEE 	= $scope.MODIFY_FEE_NTD;
			$scope.inputVO.MNG_FEE_MIN	= $scope.MNG_FEE_MIN_NTD;
		} else {
			// 外幣
			$scope.inputVO.SIGNING_FEE 	= $scope.SIGNING_FEE_USD;
			$scope.inputVO.MODIFY_FEE 	= $scope.MODIFY_FEE_USD;
			$scope.inputVO.MNG_FEE_MIN	= $scope.MNG_FEE_MIN_USD;
		}
		$scope.inputVO.MNG_FEE_RATE1 = $scope.MNG_FEE_RATE1;
		$scope.inputVO.MNG_FEE_RATE2 = $scope.MNG_FEE_RATE2;
		$scope.inputVO.MNG_FEE_RATE3 = $scope.MNG_FEE_RATE3;
	}
	
	// 推派監察人下拉選項 (只會有 監察人1 與 監察人2)
	$scope.initDiscList = function() {
		$scope.discList = [];
//		for (var i = 2; i <= 5; i++) {
		for (var i = 2; i <= 3; i++) {
			if ($scope.inputVO['custList' + i] != undefined && $scope.inputVO['custList' + i].CUST_ID != undefined && $scope.inputVO['custList' + i].CUST_ID != '') {
				$scope.discList.push({LABEL: $scope.inputVO['custList' + i].CUST_ID , DATA: $scope.inputVO['custList' + i].CUST_ID});
			}
		}
	}
	
	//初始化
	$scope.init = function() {
		$scope.saveFlag = false;
		$scope.otrBnkFlag = undefined;
		$scope.legAgent = undefined;
		$scope.lock = false;
		$scope.unchangeable = false;
		$scope.unchangeable_c = false;
		$scope.oneLegAge = undefined;
		
		for (var i = 1; i <= 5; i++) {
			$scope['accList' + i] = [];
			$scope.inputVO['custList' + i] = {};
		}
		
		// 編輯 or 查詢
		if ($scope.row != undefined) {
			$scope.unchangeable = true;
			$scope.unchangeable_c = true;
			if ($scope.type == 'read') {
				$scope.lock = true;
			}
			$scope.inputVO = $scope.row;
			
			// 依約定月份給付_每年幾月：1~12（逗號分隔，代表1~12月）
			if ($scope.row.A_MONTHS != undefined && $scope.row.A_MONTHS.length >= 1) {
				var aMonArray = $scope.row.A_MONTHS.split(',');
				$scope.inputVO.list1 = [];
				angular.forEach(aMonArray, function(row) {
					$scope['A' + row + '_1'] = true;
					$scope.inputVO.list1.push(row + '');
				});
			}
			
			if ($scope.row.CON_CURR == 'F') {
				$scope.currList = [];	// for後面選項連動
				$scope.currList2 = [];
				$scope.currList3 = [];
				if($scope.inputVO.CURR1 != undefined){
					$scope.currList.push({LABEL: $scope.inputVO.CURR1, DATA: $scope.inputVO.CURR1});				
				}
				if($scope.inputVO.CURR2 != undefined){
					$scope.currList.push({LABEL: $scope.inputVO.CURR2, DATA: $scope.inputVO.CURR2});					
				}
				if($scope.inputVO.CURR3 != undefined){
					$scope.currList.push({LABEL: $scope.inputVO.CURR3, DATA: $scope.inputVO.CURR3});					
				}
				angular.forEach($scope.currList1, function(row, index) {
					if(row.DATA != $scope.inputVO.CURR1 && row.DATA != '')
						$scope.currList2.push(row);
				});
				angular.forEach($scope.currList2, function(row, index) {
					if(row.DATA != $scope.inputVO.CURR2 && row.DATA != '')
						$scope.currList3.push(row);
				});
			}
			
			$scope.sendRecv("MTC110", "getDetail", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", {'CON_NO': $scope.inputVO.CON_NO},
				function(tota, isError) {
					if (!isError) {
						$scope.legNameList = [];
						if(tota[0].body.resultList.length > 0) {
							angular.forEach(tota[0].body.resultList, function(row) {
								var rel_type = row.REL_TYPE;
								$scope.inputVO['custList' + rel_type] = row;
								
								// 委託人
								if (rel_type == '1') {
//									$scope.getAcct(rel_type, '1');	// 取得"委託人"行內帳號
//									if (row.BIRTH_DATE != undefined) {
//										$scope.inputVO.BIRTH_DATE = $scope.toJsDate(row.BIRTH_DATE);										
//									}
									if (row.BIR_ROC != undefined) {
										if (row.BIR_ROC.charAt(0) == '0') {
											row.BIR_ROC = row.BIR_ROC.substring(1);
										}
										$scope.inputVO.BIRTH_DATE = row.BIR_ROC;
									}
									if (row.LEG_AGENT_NAME1 != undefined) {
										$scope.legNameList.push({LABEL: row.LEG_AGENT_NAME1, DATA: row.LEG_AGENT_NAME1});
									}
									if (row.LEG_AGENT_NAME2 != undefined) {
										$scope.legNameList.push({LABEL: row.LEG_AGENT_NAME2, DATA: row.LEG_AGENT_NAME2});
									}
								} 
//								else {
//									if ($scope.inputVO.CON_CURR == 'T') {
//										$scope.getAcct(rel_type, '1');	// 取得"監察人"行內帳號
//									}
//								}
								
								$scope.getAcct(rel_type, '1');	// 取得"委託人"/"監察人"行內帳號
								
								if (!$scope.checkNull(row.AGR_ACC))
									$scope.inputVO['custList' + rel_type].AGR_ACC = row.AGR_ACC + '-' + row.AGR_BRA_NBR;
								
								// for:比較"報酬及給付幣別"有無異動
								$scope.inputVO['custList' + rel_type].PAY_CURR_ORI = angular.copy(row.PAY_CURR);
								
								// 報酬及給付頻率 M：每月　S：每季（1. 4. 7. 10月當月）　H：每半年（1. 7月當月）　1～12（逗號分隔，代表1~12月）
								if (row.PAY_FREQ != undefined && row.PAY_FREQ != 'M' && row.PAY_FREQ != 'S' && row.PAY_FREQ != 'H') {
//									var monArray = row.PAY_FREQ.split(',');
//									$scope.inputVO['list' + rel_type] = [];
//									angular.forEach(monArray, function(aRow) {
//										$scope['A' + aRow + '_' + rel_type] = true;
//										$scope.inputVO['list' + rel_type].push(aRow + '');
//									});
									$scope.inputVO['custList' + rel_type].PAY_FREQ_A = row.PAY_FREQ;
									$scope.inputVO['custList' + rel_type].PAY_FREQ = 'A';
								}
							});
							$scope.initDiscList();
							
							if($scope.inputVO.custList1 != undefined && $scope.inputVO.custList1.CUST_ID != undefined && $scope.inputVO.custList1.CUST_ID != '' && 
							 (($scope.inputVO.custList2 != undefined && ($scope.inputVO.custList2.CUST_ID == undefined || $scope.inputVO.custList2.CUST_ID == '')) || 
							   $scope.inputVO.custList2 == undefined)
							) {
								$scope.radioLock = true;	
							}
							$scope.showApp();
							
							// 複製後交付幣別不變，保留原本所有內容(含交付幣別、金額、監察人 報酬、特別給付、其他給付項目)
							if ($scope.type == 'copy') {
								$scope.inputVO.CON_NO = undefined;	
								$scope.unchangeable = false;
								$scope.unchangeable_c = true;
							}
                		}
					}
			});
		} else {
			// 新增
			$scope.legNameList = [];
			$scope.inputVO.CON_CURR = 'T';
			$scope.radioLock = false;
			$scope.showApp_sup = false;
			$scope.discList = [];
			$scope.inputVO.list1 = [];
//			for (var i = 1; i <= 5; i++) {
////				$scope.inputVO['list' + i] = [];
//				$scope.inputVO['custList' + i] = {};
//			}
			$scope.currList2 = [];
			$scope.currList3 = [];
			$scope.initAllFee();	// 預帶標準收費，可修改。
			$scope.checkRadio();
		}
	}
	
	$scope.memberOriBnk = function(nbr) {
		$scope.otrBnkFlag = $scope.inputVO['custList' + nbr].OTR_BANK;
	}
	
	// 取得他行分行下拉
	$scope.getOtrBranch = function(nbr, bankID) {
		$scope.sendRecv("MTC110", "getOtrBranch", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", {'bank_id': bankID}, 
			function(tota, isError) {
				if (!isError) {
					$scope['otrBranchList' + nbr] = [];
					
					if ($scope.otrBnkFlag != undefined && $scope.otrBnkFlag != bankID) {
						$scope.inputVO['custList' + nbr].OTR_BRANCH = undefined;
						$scope.inputVO['custList' + nbr].OTR_ACC = undefined;						
					}
					
					angular.forEach(tota[0].body.resultList, function(row) {
						$scope['otrBranchList' + nbr].push({LABEL: row.LNAME, DATA: row.BRANCHID});
					});
				}
		});
	}
	
	// 變更（外幣）交易幣別
	$scope.changeCurr = function(nbr) {
		$scope.inputVO['AMT' + nbr] = undefined;
		$scope.currList = [];	// for後面選項連動
		switch (nbr) {
		case 1:
			$scope.currList2 = [];
			$scope.currList3 = [];
			if ($scope.inputVO.CURR2 != undefined) {
				$scope.inputVO.CURR2 = undefined;
				$scope.inputVO.AMT2 = undefined;
			}
			if ($scope.inputVO.CURR3 != undefined) {
				$scope.inputVO.CURR3 = undefined;				
				$scope.inputVO.AMT3 = undefined;
			}
			if($scope.inputVO.CURR1 != undefined && $scope.inputVO.CURR1 != ''){
				$scope.currList.push({LABEL: $scope.inputVO.CURR1, DATA: $scope.inputVO.CURR1});				
			} else {
				$scope.inputVO.AMT1 = undefined;
			}
			angular.forEach($scope.currList1, function(row, index) {
				if(row.DATA != $scope.inputVO.CURR1 && row.DATA != '')
					$scope.currList2.push(row);
			});
			break;
		case 2:
			$scope.currList3 = [];
			if ($scope.inputVO.CURR3 != undefined) {
				$scope.inputVO.CURR3 = undefined;
				$scope.inputVO.AMT3 = undefined;
			}
			if($scope.inputVO.CURR1 != undefined){
				$scope.currList.push({LABEL: $scope.inputVO.CURR1, DATA: $scope.inputVO.CURR1});				
			}
			if($scope.inputVO.CURR2 != undefined){
				$scope.currList.push({LABEL: $scope.inputVO.CURR2, DATA: $scope.inputVO.CURR2});					
			}
			angular.forEach($scope.currList2, function(row, index) {
				if(row.DATA != $scope.inputVO.CURR2 && row.DATA != '')
					$scope.currList3.push(row);
			});
			break;
		case 3:
			if($scope.inputVO.CURR1 != undefined){
				$scope.currList.push({LABEL: $scope.inputVO.CURR1, DATA: $scope.inputVO.CURR1});				
			}
			if($scope.inputVO.CURR2 != undefined){
				$scope.currList.push({LABEL: $scope.inputVO.CURR2, DATA: $scope.inputVO.CURR2});					
			}
			if($scope.inputVO.CURR3 != undefined){
				$scope.currList.push({LABEL: $scope.inputVO.CURR3, DATA: $scope.inputVO.CURR3});					
			}
			break;
		}
	}
	
	$scope.checkRadio = function() {
		if($scope.inputVO.custList1 != undefined && $scope.inputVO.custList1.CUST_ID != undefined && $scope.inputVO.custList1.CUST_ID != '' && 
		 (($scope.inputVO.custList2 != undefined && ($scope.inputVO.custList2.CUST_ID == undefined || $scope.inputVO.custList2.CUST_ID == '')) || 
		   $scope.inputVO.custList2 == undefined)
		) {
			$scope.inputVO.TERM_CON = 'A';
			$scope.inputVO.MODI_CON = 'A';
			$scope.inputVO.DISC_CON = 'A';
			$scope.inputVO.APL_CON  = 'A';
			if ($scope.inputVO.APL_DOC_N == true)
				$scope.inputVO.UNLIMIT_DOC_TYPE = 'A';
			if ($scope.inputVO.APL_DOC_Y == true)
				$scope.inputVO.LIMIT_DOC_TYPE = 'A';
			$scope.radioLock = true;	
		} else {
//			$scope.inputVO.TERM_CON = undefined;
//			$scope.inputVO.MODI_CON = undefined;
//			$scope.inputVO.DISC_CON = undefined;
//			$scope.inputVO.APL_CON  = undefined;
//			$scope.inputVO.UNLIMIT_DOC_TYPE = undefined;
//			$scope.inputVO.LIMIT_DOC_TYPE = undefined;
			$scope.radioLock = false;
			$scope.showApp_sup = false;
			$scope.showApp();
		}
	}
	
	$scope.checkID = function(step) {
		var custID = undefined;
		if ($scope.inputVO['custList' + step] != undefined && 
		   ($scope.inputVO['custList' + step].CUST_ID == undefined || 
		    $scope.inputVO['custList' + step].CUST_ID == '')) {
			$scope.inputVO['custList' + step] = {};
			$scope['BRA_NAME' + step] = "";
		} else {
			//只有ID有修改，相關資料都清掉；但ID需保留重新取資料
			var custidTemp =  $scope.inputVO['custList' + step].CUST_ID;
			$scope.inputVO['custList' + step] = {};
			$scope['BRA_NAME' + step] = "";
			$scope.inputVO['custList' + step].CUST_ID = custidTemp;
		}
		
		if($scope.inputVO['custList' + step] != undefined && 
		   $scope.inputVO['custList' + step].CUST_ID != undefined && 
		   $scope.inputVO['custList' + step].CUST_ID.replace(/\s*/g,"") != ''){
			// 去除字串內所有的空格 & 轉大寫
			$scope.inputVO['custList' + step].CUST_ID = $scope.inputVO['custList' + step].CUST_ID.toUpperCase().replace(/\s*/g,"");	
			custID = $scope.inputVO['custList' + step].CUST_ID;
		}
		
		var showErrMsg = false;
		if (step != undefined) {
			if (step != '1') {
				if ($scope.inputVO.custList1.CUST_ID == custID) {
					showErrMsg = true;
				}
				
				// 1.監察人(最多 2 位，亦可 1 位或無)，如監察人 2 位都刪除，次順位、第三順位 監察人請由系統自動刪除。
				// 2.次順位監察人刪除後，第三順位監察人請由系統自動刪除。
				// 3.監察人 1 刪除，監察人 2、次順位、第三順位監察人請由系統自動刪除。
				var clean_flag = false;
				if (step != 3) {
					for (var i = 2; i <= 5; i++) {
						if ($scope.inputVO['custList' + i] == undefined || $scope.inputVO['custList' + i].CUST_ID == undefined || $scope.inputVO['custList' + i].CUST_ID == '') {
							if (i != 3) {
								clean_flag = true;
								continue;								
							}
						}
						if (clean_flag == true && $scope.inputVO['custList' + i] != undefined && $scope.inputVO['custList' + i].CUST_ID != undefined) {
							$scope.inputVO['custList' + i] = {};						
						}
					}
				}
				
			} else {
				for (var i = 2; i <= 5; i++) {
					if ($scope.inputVO['custList' + i].CUST_ID != undefined && custID == $scope.inputVO['custList' + i].CUST_ID) {
						showErrMsg = true;
						break;
					}
				}
			}
			if (showErrMsg == true) {
				$scope.inputVO['custList' + step].CUST_ID = undefined;
				custID = undefined; 
				$scope.showErrorMsg("委託人ID 不得與監察人相同。");
			}
		}
		
		if (step != '1') {
			if (custID != undefined) {
				for (var i = 2; i <= 5; i ++) {
					if (i != step && 
						$scope.inputVO['custList' + i] != undefined && 
						custID == $scope.inputVO['custList' + i].CUST_ID) {
						
						custID = undefined;
						
						for (var c = step; c <= 5; c++) {
							if ($scope.inputVO['custList' + c] != undefined)
								$scope.inputVO['custList' + c] = {};
						}
						$scope.showErrorMsg("監察人ID 不得與次順位/第三順位監察人相同。");
					}
				}
			}			
		}
		
		// 更新：推派決定權下拉選單
		$scope.initDiscList();
		if (step == '2' || step == '3') {
			$scope.inputVO.DISC_ID = undefined;
		}
		
		// 判斷是否顯示『委託人＋全體監察人』
		$scope.checkRadio();
		
		// 取得客戶基本資料
		if (custID != undefined) {
			$scope.sendRecv("MTC110", "getCustInfo", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", 
				{'cust_id': custID}, function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length > 0) {
							var custInfo = tota[0].body.resultList[0];
							if (custInfo.CUST_YN == 'Y') {
								$scope.inputVO['custList' + step].CUST_NAME 	= custInfo.CUST_NAME;
								$scope.inputVO['custList' + step].TEL 			= custInfo.TEL;
								$scope.inputVO['custList' + step].MOBILE_NO 	= custInfo.MOBILE_NO;
								$scope.inputVO['custList' + step].EMAIL 		= custInfo.EMAIL;
								$scope.inputVO['custList' + step].COM_ZIP_CODE 	= custInfo.COM_ZIP_CODE;
								$scope.inputVO['custList' + step].COM_ADDRESS 	= custInfo.COM_ADDRESS;
								$scope.inputVO['custList' + step].CUST_YN 		= "Y";
								
								if (step == '1') {
									//$scope.inputVO.BIRTH_DATE = $scope.toJsDate(custInfo.BIRTH_DATE);
									if (custInfo.BIR_ROC != undefined) {
										if (custInfo.BIR_ROC.charAt(0) == '0') {
											custInfo.BIR_ROC = custInfo.BIR_ROC.substring(1);
										}
									}
									$scope.inputVO.BIRTH_DATE = custInfo.BIR_ROC;
									$scope.inputVO['custList' + step].CEN_ZIP_CODE 	= custInfo.CEN_ZIP_CODE;
									$scope.inputVO['custList' + step].CEN_ADDRESS 	= custInfo.CEN_ADDRESS;
								} 
//								else {									
//									if ($scope.inputVO.CON_CURR == 'T') {
//										$scope.getAcct(step, '2');
//									}
//								}
								// 取得客戶行內帳號
								$scope.getAcct(step, '2');
							} else {
								if (step == '1') {
									// 委託人非本行客戶
									$scope.showMsg("委託人須為本行存款客戶。");
									$scope.inputVO.custList1 = {};
								} else {
									$scope.showMsg("監察人非為本行客戶，請自行登打基本資料。");
									$scope.inputVO['custList' + step].CUST_NAME 	= undefined;
									$scope.inputVO['custList' + step].TEL 			= undefined;
									$scope.inputVO['custList' + step].MOBILE_NO 	= undefined;
									$scope.inputVO['custList' + step].EMAIL 		= undefined;
									$scope.inputVO['custList' + step].COM_ZIP_CODE 	= undefined;
									$scope.inputVO['custList' + step].COM_ADDRESS 	= undefined;
									$scope.inputVO['custList' + step].CUST_YN 		= "N";
								}
							}
						}
					} else {
						$scope.inputVO['custList' + step] = {};
					}
			});
		}
	}
	
	//給付方式，清除
	$scope.changePayType = function(type) {
		if(type == "1" && $scope.inputVO.PAY_TYPE1 == false) {
			$scope.inputVO.SIP_TYPE1 = false;
			$scope.inputVO.SIP_TYPE2 = false;
			$scope.changeSipType("1");
			$scope.changeSipType("2");
		} else if(type == "2" && $scope.inputVO.PAY_TYPE2 == false) {
			$scope.inputVO.APL_DOC_N = false;
			$scope.inputVO.APL_DOC_Y = false;
			$scope.changeAplDoc();
		}
	}
	
	//定期定額給付-固定給付，清除
	$scope.changeSipType = function(type) {
		if(type == "1" && $scope.inputVO.SIP_TYPE1 == false) {
			$scope.inputVO.BEN_AGE_YN1 = false;
			$scope.inputVO.BEN_AGE_YN2 = false;
			$scope.inputVO.BEN_AGE_YN3 = false;
			$scope.clickAge(1);
			$scope.clickAge(2);
			$scope.clickAge(3);
		} else if(type == "2" && $scope.inputVO.SIP_TYPE2 == false) {
			$scope.inputVO.AGR_MON_TYPE_M = false;
			$scope.inputVO.AGR_MON_TYPE_A = false;
			$scope.clickAgrMon('M');
			$scope.clickAgrMon('A');
		}
	}
		
	//是否需提供佐證單據
	$scope.changeAplDoc = function() {
		if($scope.inputVO.custList1 != undefined && $scope.inputVO.custList1.CUST_ID != undefined && $scope.inputVO.custList1.CUST_ID != '' && 
		 (($scope.inputVO.custList2 != undefined && ($scope.inputVO.custList2.CUST_ID == undefined || $scope.inputVO.custList2.CUST_ID == '')) || 
		   $scope.inputVO.custList2 == undefined)
		){
			if ($scope.inputVO.APL_DOC_N == true)
				$scope.inputVO.UNLIMIT_DOC_TYPE = 'A';
			if ($scope.inputVO.APL_DOC_Y == true)
				$scope.inputVO.LIMIT_DOC_TYPE = 'A';
		} else {
			$scope.inputVO.UNLIMIT_DOC_TYPE = undefined;
			$scope.inputVO.LIMIT_DOC_TYPE = undefined;
		}
		
		if ($scope.inputVO.APL_DOC_Y == false) {
			$scope.inputVO.EDU_YN = false;
			$scope.inputVO.MED_YN = false;
			$scope.inputVO.MED_PAY_FOR_A = false;
			$scope.inputVO.MED_PAY_FOR_B = false;
			$scope.inputVO.NUR_YN = false;
			$scope.inputVO.NUR_PAY_FOR_A = false;
			$scope.inputVO.NUR_PAY_FOR_B = false;
			$scope.inputVO.OTR_YN = false;
			$scope.changeOtrYN();
		}
	}
	
	//其他給付
	$scope.changeOtrYN = function() {
		if ($scope.inputVO.OTR_YN == false) {
			if($scope.inputVO.CON_CURR == 'F') {	
				//外幣信託
				$scope.inputVO.MAR_CURR = undefined;
				$scope.inputVO.BIR_CURR = undefined;
				$scope.inputVO.HOS_CURR = undefined;
				$scope.inputVO.OTR_CURR = undefined;
			}
			
			$scope.inputVO.MAR_AMT = undefined;
			$scope.inputVO.BIR_AMT = undefined;
			$scope.inputVO.HOS_AMT = undefined;
			$scope.inputVO.OTR_AMT = undefined;		
			$scope.inputVO.OTR_ITEM = undefined;	
		}
	}
	
	// 取得客戶帳號資訊
	$scope.getAcct = function(step, flag) {
		var cust_id = $scope.inputVO['custList' + step].CUST_ID;
		if (cust_id != undefined && cust_id != '') {
			var con_curr = $scope.inputVO.CON_CURR;
			if (flag == 2) {
				$scope.inputVO['custList' + step].AGR_ACC = undefined;		
			}
			var pay_curr = undefined;
			if (con_curr == 'F' && step != 1) {
				pay_curr = 'USD';
			}
			$scope.sendRecv("MTC110", "getAcct", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", 
				{'cust_id' : $scope.inputVO['custList' + step].CUST_ID, 
				 'CON_CURR': con_curr, 
				 'PAY_CURR': pay_curr}, function(tota, isError) {
					if (!isError) {
						$scope['accList' + step] = [];
						angular.forEach(tota[0].body.resultList, function(row) {
							$scope['accList' + step].push({LABEL: row.AGR_ACC, DATA: row.AGR_ACC + '-' + row.AGR_BRA_NBR});		
//							if ($scope.inputVO.CON_CURR == 'T') {
//							} else {
//								$scope['accList' + step].push({LABEL: row.AGR_ACC + '_' + row.CURR, DATA: row.AGR_ACC + '-' + row.AGR_BRA_NBR});
//							}
						});
					}
			});
		}
	}
	
	// 取得帳號分行名稱
	$scope.getBraName = function(acc, nbr) {
		if (acc != undefined && acc != '') {
			var bra_nbr = acc.split("-")[1];
			if (bra_nbr != undefined) {
				$scope.sendRecv("MTC110", "getBraName", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", {'BRA_NBR' : bra_nbr}, 
						function(tota, isError) {
							if (!isError) {
								$scope['BRA_NAME' + nbr] = tota[0].body.resultList[0].DEPT_NAME;
							}
					});
			}
		}
	}
	
	// 取得客戶帳號資訊 (by幣別) ==> 交付幣別為"外幣"時才需要
//	$scope.getAccByCurr = function(step) {
//		if ($scope.inputVO.CON_CURR == 'F') {
//			if ($scope.inputVO['custList' + step] != undefined && 
//				$scope.inputVO['custList' + step].CUST_ID != undefined &&
//				$scope.inputVO['custList' + step].PAY_CURR != undefined
//			) {
//				if ($scope.inputVO['custList' + step].PAY_CURR_ORI !== $scope.inputVO['custList' + step].PAY_CURR) {
//					$scope.inputVO['custList' + step].AGR_ACC = undefined;
//				}
//				$scope.sendRecv("MTC110", "getAcct", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", 
//				   {'cust_id' : $scope.inputVO['custList' + step].CUST_ID, 
//					'CON_CURR': $scope.inputVO.CON_CURR, 
//					'PAY_CURR': $scope.inputVO['custList' + step].PAY_CURR},
//					function(tota, isError) {
//						if (!isError) {
//							$scope['accList' + step] = [];
//							angular.forEach(tota[0].body.resultList, function(row) {
//								$scope['accList' + step].push({LABEL: row.AGR_ACC, DATA: row.AGR_ACC + '-' + row.AGR_BRA_NBR});
//							});
//							if (tota[0].body.resultList.length == 0) {
//								$scope.inputVO['custList' + step].AGR_ACC = undefined;
//							}
//						}
//					});
//			}
//		}
//	}
	
	// 判斷是否顯示『委任監察人』(監察人需兩位以上)
	$scope.showApp = function() {
		if($scope.inputVO.custList1 != undefined && $scope.inputVO.custList1.CUST_ID != undefined && $scope.inputVO.custList1.CUST_ID != '' && 
		   $scope.inputVO.custList2 != undefined && $scope.inputVO.custList2.CUST_ID != undefined && $scope.inputVO.custList2.CUST_ID != '' && (
		  ($scope.inputVO.custList3 != undefined && $scope.inputVO.custList3.CUST_ID != undefined && $scope.inputVO.custList3.CUST_ID != '') ||
		  ($scope.inputVO.custList4 != undefined && $scope.inputVO.custList4.CUST_ID != undefined && $scope.inputVO.custList4.CUST_ID != '') ||
		  ($scope.inputVO.custList5 != undefined && $scope.inputVO.custList5.CUST_ID != undefined && $scope.inputVO.custList5.CUST_ID != ''))) {
			$scope.showApp_sup = true;
		}
	}
	
	$scope.clickAgrMon = function(type) {
		// 依約定月份給付類別（複選）M：每月　A：每年某幾月
		if (type == 'M') {
			if ($scope.inputVO.AGR_MON_TYPE_M != true)
				$scope.inputVO.M_AMT = undefined;
		} else {
			if ($scope.inputVO.AGR_MON_TYPE_A != true) {
				for (var i = 1; i <= 12; i++) {
					$scope['A' + i + '_1'] = false;
				}
				$scope.inputVO.A_AMT = undefined;				
			}
		}
	}
	
	$scope.checkMon = function(type) {
		$scope.inputVO['list' + type] = [];
		for (var i = 1; i <= 12; i++) {
			if ($scope['A' + i + '_' + type] == true) {
				$scope.inputVO['list' + type].push(i + '');
			}
			// 最多選 11 筆，若有 12 筆則為『每月』
			if ($scope.inputVO['list' + type].length == 12) {
				$scope.inputVO['list' + type] = [];
				
				for (var m = 1; m <= 12; m++) {
					$scope['A' + m + '_' + type] = false;
				}
				
//				switch (type) {
//					case 1:
//						$scope.inputVO.AGR_MON_TYPE = 'M';
//						$scope.inputVO.A_AMT = undefined;
//						break;
//					case 2:
//						$scope.inputVO.custList2.PAY_FREQ = 'M';
//						break;
//					case 3:
//						$scope.inputVO.custList3.PAY_FREQ = 'M';
//						break;
//					case 4:
//						$scope.inputVO.custList4.PAY_FREQ = 'M';
//						break;
//					case 5:
//						$scope.inputVO.custList5.PAY_FREQ = 'M';
//						break;	
//				}
			}
		}		
	}
	
	$scope.changeDisc = function() {
		if ($scope.inputVO.DISC_CON != 'C') {
			$scope.inputVO.APP_SUP = undefined;
			$scope.inputVO.DISC_ID = undefined;
		}
	}
	
	$scope.changeApp = function() {
		if ($scope.inputVO.APP_SUP != 'C2') {
			$scope.inputVO.DISC_ID = undefined;
		}
	}
	
	$scope.checkRate = function() {
		var showErrMsg = false;
		if ($scope.inputVO.MNG_FEE_RATE1 > $scope.MNG_FEE_RATE1) {
			$scope.inputVO.MNG_FEE_RATE1 = $scope.MNG_FEE_RATE1;
			showErrMsg = true;
		}
		if ($scope.inputVO.MNG_FEE_RATE2 > $scope.MNG_FEE_RATE2) {
			$scope.inputVO.MNG_FEE_RATE2 = $scope.MNG_FEE_RATE2;
			showErrMsg = true;
		}
		if ($scope.inputVO.MNG_FEE_RATE3 > $scope.MNG_FEE_RATE3) {
			$scope.inputVO.MNG_FEE_RATE3 = $scope.MNG_FEE_RATE3;
			showErrMsg = true;
		}
		if (showErrMsg == true) {
			$scope.showErrorMsg("金額/費率不得高於標準收費");
		}
		
		if ($scope.inputVO.MNG_FEE_RATE1 != undefined &&  $scope.inputVO.MNG_FEE_RATE2 != undefined && 
			$scope.inputVO.MNG_FEE_RATE1 < $scope.inputVO.MNG_FEE_RATE2) {
			$scope.inputVO.MNG_FEE_RATE2 = undefined;
			$scope.inputVO.MNG_FEE_RATE3 = undefined;
			$scope.showErrorMsg("費率需按級距降低或相同。");
		}
		if ($scope.inputVO.MNG_FEE_RATE2 != undefined &&  $scope.inputVO.MNG_FEE_RATE3 != undefined && 
		    $scope.inputVO.MNG_FEE_RATE2 < $scope.inputVO.MNG_FEE_RATE3) {
			$scope.inputVO.MNG_FEE_RATE3 = undefined;
			$scope.showErrorMsg("費率需按級距降低或相同。");
		}
	}
	
	$scope.checkFee = function(nbr) {
		var curr = $scope.inputVO.CON_CURR;
		var showErrMsg = false;
		switch (nbr) {
			case 1:
				// 簽約費
				if (curr == 'T') {
					if ($scope.inputVO.SIGNING_FEE > $scope.SIGNING_FEE_NTD) {
						$scope.inputVO.SIGNING_FEE = $scope.SIGNING_FEE_NTD;
						showErrMsg = true;
					}
				} else {
					if ($scope.inputVO.SIGNING_FEE > $scope.SIGNING_FEE_USD) {
						$scope.inputVO.SIGNING_FEE = $scope.SIGNING_FEE_USD;
						showErrMsg = true;
					}
				}
				break;
			case 2:;
				// 修約費
				if (curr == 'T') {
					if ($scope.inputVO.MODIFY_FEE > $scope.MODIFY_FEE_NTD) {
						$scope.inputVO.MODIFY_FEE = $scope.MODIFY_FEE_NTD;
						showErrMsg = true;
					}
				} else {
					if ($scope.inputVO.MODIFY_FEE > $scope.MODIFY_FEE_USD) {
						$scope.inputVO.MODIFY_FEE = $scope.MODIFY_FEE_USD;
						showErrMsg = true;
					}
				}
				break;
			case 3:
				// 最低管理費
				if (curr == 'T') {
					if ($scope.inputVO.MNG_FEE_MIN > $scope.MNG_FEE_MIN_NTD) {
						$scope.inputVO.MNG_FEE_MIN = $scope.MNG_FEE_MIN_NTD;
						showErrMsg = true;
					}
				} else {
					if ($scope.inputVO.MNG_FEE_MIN > $scope.MNG_FEE_MIN_USD) {
						$scope.inputVO.MNG_FEE_MIN = $scope.MNG_FEE_MIN_USD;
						showErrMsg = true;
					}
				}
				break;
		}
		if (showErrMsg == true) {
			$scope.showErrorMsg("金額/費率不得高於標準收費");
		}
	}
	
	//儲存
	$scope.save = function(send_yn) {
		//「新增契約」儲存前檢核必輸欄位: 1.交付幣別 2.委託人即受益人 ID
		if ($scope.inputVO.custList1.CUST_ID == undefined || $scope.inputVO.custList1.CUST_ID == '') {
			$scope.showMsg("儲存未成功，請先輸入交付幣別、委託人 ID");
			return;
		}
		
		$scope.sendRecv("MTC110", "save", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.saveFlag = true;
					$scope.inputVO.CON_NO = tota[0].body.CON_NO;
					$scope.unchangeable = true;		// "交付幣別"在儲存之後，即不可改變。
					$scope.unchangeable_c = true;	// "委託人即受益人ID"在儲存之後，即不可改變。
					if (send_yn) {
						$confirm({text: '確定送出此次編輯?'}, {size: 'sm'}).then(function() {
							// 檢核
							if ($scope.sendCheck()) {
								$scope.sendRecv("MTC110", "updateStatus", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", 
									{'CON_NO': $scope.inputVO.CON_NO, 'status': 'C'},
									function(tota, isError) {
										if (!isError) {
											$scope.showSuccessMsg('ehl_01_common_023'); // 執行成功
											$scope.closeThisDialog('successful');									
										}
								});								
							}
						});
					} else {
						$scope.showSuccessMsg('ehl_01_common_025'); // 儲存成功	
					}
					return;
				}
		});
	}
	
	// 檢核『固定給付』
	$scope.checkFixedPay = function() {
		for (var i = 1; i <= 3; i++) {
			if ($scope.inputVO['BEN_AGE_YN' + i] == true) {
				if ($scope.inputVO.CON_CURR == 'F') {
					// 外幣
					if ($scope.inputVO['BEN_CURR' + i] == undefined) {
						$scope.showErrorMsg("『固定給付』已勾選項目『幣別』為必要欄位。");	
						return false;
					}
				}
				if ($scope.inputVO['BEN_AMT' + i] == undefined) {
					$scope.showErrorMsg("『固定給付』已勾選項目『金額』為必要欄位。");	
					return false;
				}
			}
		}
	}
	
	// 送出檢核
	$scope.sendCheck = function() {
		//
		if ($scope.inputVO.CON_CURR == 'T') {
			// 台幣
			if ($scope.checkNull($scope.inputVO.AMT1) || $scope.inputVO.AMT1 == '0'){
				$scope.showErrorMsg("『交付金額』為必要欄位，且需大於0。");	
				return false;
			}
			
		} else {
			// 外幣
			if ($scope.checkNull($scope.inputVO.AMT1) || $scope.inputVO.CURR1 == undefined || $scope.inputVO.AMT1 == '0') {
				$scope.showErrorMsg("『交付金額』至少填一項，且金額需大於0。");	
				return false;
			}
			if ((!$scope.checkNull($scope.inputVO.CURR1) && ($scope.inputVO.AMT1 == undefined || $scope.inputVO.AMT1 == '0' || $scope.inputVO.AMT1 == '')) ||
				(!$scope.checkNull($scope.inputVO.CURR2) && ($scope.inputVO.AMT2 == undefined || $scope.inputVO.AMT2 == '0' || $scope.inputVO.AMT2 == '')) ||	
				(!$scope.checkNull($scope.inputVO.CURR3) && ($scope.inputVO.AMT3 == undefined || $scope.inputVO.AMT3 == '0' || $scope.inputVO.AMT3 == ''))
			) {
				$scope.showErrorMsg("『交付金額』若已選擇幣別，則金額為必要欄位，且需大於0。");	
				return false;
			}
		}
		debugger
		//
		if (($scope.checkNull($scope.inputVO.SIGNING_FEE) && $scope.inputVO.SIGNING_FEE != '0') || 
			($scope.checkNull($scope.inputVO.MODIFY_FEE) && $scope.inputVO.MODIFY_FEE != '0') ||
			($scope.checkNull($scope.inputVO.MNG_FEE_MIN) && $scope.inputVO.MNG_FEE_MIN != '0') ||
			$scope.checkNull($scope.inputVO.MNG_FEE_RATE1) || $scope.checkNull($scope.inputVO.MNG_FEE_RATE2) ||
			$scope.checkNull($scope.inputVO.MNG_FEE_RATE3)) {
			$scope.showErrorMsg("『信託報酬』皆為必填欄位。");
			return false;
		}
		//
		if ($scope.inputVO.custList1 == undefined || $scope.checkNull($scope.inputVO.custList1.CUST_ID)) {
			$scope.showErrorMsg("『委託人即受益人ID』皆為必填欄位。");
			return false;
		}
		//
		if ($scope.checkNull($scope.inputVO.END_YEARS) && $scope.checkNull($scope.inputVO.END_AMT_LIMIT)) {
			$scope.showErrorMsg("『終止條件』至少擇一填寫。");
			return false;
		}
		//
		if ($scope.checkNull($scope.inputVO.TERM_CON)) {
			$scope.showErrorMsg("『提前終止』為必要欄位。");
			return false;
		}
		//
		if ($scope.checkNull($scope.inputVO.MODI_CON)) {
			$scope.showErrorMsg("『變更契約』為必要欄位。");
			return false;
		}
		//
		if ($scope.checkNull($scope.inputVO.DISC_CON)) {
			$scope.showErrorMsg("『運用決定權』為必要欄位。");
			return false;
		}
		//
		if ($scope.inputVO.DISC_CON == 'C' && $scope.showApp_sup == true && $scope.checkNull($scope.inputVO.APP_SUP)) {
			$scope.showErrorMsg("『委任監察人』需二擇一。");
			return false;
		}
		//
		if ($scope.inputVO.APP_SUP == 'C2' && $scope.checkNull($scope.inputVO.DISC_ID)) {
			$scope.showErrorMsg("『委任監察人』需選擇推派人員。");
			return false;
		}
		//
		if (($scope.inputVO.PAY_TYPE1 == undefined || $scope.inputVO.PAY_TYPE1 == false) && 
			($scope.inputVO.PAY_TYPE2 == undefined || $scope.inputVO.PAY_TYPE2 == false)) {
			$scope.showErrorMsg("『客戶規劃資產給付方式』至少擇一，可複選。");
			return false;
		}
		//
		if ($scope.inputVO.PAY_TYPE1 == true) {
			if($scope.checkNull($scope.inputVO.SIP_TYPE1) && $scope.checkNull($scope.inputVO.SIP_TYPE2)) {
				$scope.showErrorMsg("『定期定額給付方式』至少擇一，可複選。");
				return false;
			}
			
			//依受益人年紀給付
			if($scope.inputVO.SIP_TYPE1 == true) {
				if($scope.checkNull($scope.inputVO.BEN_AGE_YN1) && $scope.checkNull($scope.inputVO.BEN_AGE_YN2) && $scope.checkNull($scope.inputVO.BEN_AGE_YN3)) {
					$scope.showErrorMsg("固定給付『依受益人年紀給付』至少擇一，可複選。");
					return false;
				}
				
				if($scope.inputVO.BEN_AGE_YN1 == true) {
					if($scope.checkNull($scope.inputVO.BEN_AGE1) || $scope.checkNull($scope.inputVO.BEN_AMT1)){
						$scope.showErrorMsg("固定給付『依受益人年紀給付』請輸入年紀及金額。");
						return false;
					}
					if($scope.inputVO.CON_CURR == 'F' && $scope.checkNull($scope.inputVO.BEN_CURR1)){
						$scope.showErrorMsg("固定給付『依受益人年紀給付』請輸入幣別。");
						return false;
					}
				}
				
				if($scope.inputVO.BEN_AGE_YN2 == true) {
					if($scope.checkNull($scope.inputVO.BEN_AGE2) || $scope.checkNull($scope.inputVO.BEN_AGE3) || $scope.checkNull($scope.inputVO.BEN_AMT2)){
						$scope.showErrorMsg("固定給付『依受益人年紀給付』請輸入年紀及金額。");
						return false;
					}
					if($scope.inputVO.CON_CURR == 'F' && $scope.checkNull($scope.inputVO.BEN_CURR2)){
						$scope.showErrorMsg("固定給付『依受益人年紀給付』請輸入幣別。");
						return false;
					}
				}
				
				if($scope.inputVO.BEN_AGE_YN3 == true) {
					if($scope.checkNull($scope.inputVO.BEN_AGE4) || $scope.checkNull($scope.inputVO.BEN_AMT3)){
						$scope.showErrorMsg("固定給付『依受益人年紀給付』請輸入年紀及金額。");
						return false;
					}
					if($scope.inputVO.CON_CURR == 'F' && $scope.checkNull($scope.inputVO.BEN_CURR3)){
						$scope.showErrorMsg("固定給付『依受益人年紀給付』請輸入幣別。");
						return false;
					}
				}
			}
			
			//依約定月份給付
			if($scope.inputVO.SIP_TYPE2 == true) {
				if($scope.checkNull($scope.inputVO.AGR_MON_TYPE_M) && $scope.checkNull($scope.inputVO.AGR_MON_TYPE_A)) {
					$scope.showErrorMsg("固定給付『依約定月份給付』至少擇一，可複選。");
					return false;
				}
				
				if($scope.inputVO.AGR_MON_TYPE_M == true) {
					if($scope.checkNull($scope.inputVO.M_AMT)){
						$scope.showErrorMsg("固定給付『依約定月份給付』請輸入金額。");
						return false;
					}
					if(($scope.inputVO.CON_CURR == 'F' && $scope.checkNull($scope.inputVO.M_CURR))){
						$scope.showErrorMsg("固定給付『依約定月份給付』請輸入幣別。");
						return false;
					}
				}
				
				if($scope.inputVO.AGR_MON_TYPE_A == true) {
					if($scope.checkNull($scope.inputVO.A_AMT) || $scope.inputVO['list1'] == null || $scope.inputVO['list1'] == undefined || $scope.inputVO['list1'].length == 0){
						$scope.showErrorMsg("固定給付『依約定月份給付』請輸入月份及金額。");
						return false;
					}
					if($scope.inputVO.CON_CURR == 'F' && $scope.checkNull($scope.inputVO.A_CURR)){
						$scope.showErrorMsg("固定給付『依約定月份給付』請輸入幣別。");
						return false;
					}
				}
			}
		}
		//
		if ($scope.inputVO.PAY_TYPE2 == true) {			
//			if ($scope.inputVO.APL_CON == undefined) {
//				$scope.showErrorMsg("『有權申請人』需擇一。");
//				return false;
//			}
			
			if($scope.checkNull($scope.inputVO.APL_DOC_N) && $scope.checkNull($scope.inputVO.APL_DOC_Y)) {
				$scope.showErrorMsg("『申請時是否需提供佐證單據』至少擇一輸入。");
				return false;
			}
		}
		//免附佐證單據
		if ($scope.inputVO.APL_DOC_N == true && $scope.checkNull($scope.inputVO.UNLIMIT_DOC_TYPE)) {
			$scope.showErrorMsg("『不限用途、免付單據』為必要欄位。");
			return false;
		}
		//有提供佐證單據
		if ($scope.inputVO.APL_DOC_Y == true) {
			if($scope.checkNull($scope.inputVO.LIMIT_DOC_TYPE)) {
				$scope.showErrorMsg("『限制用途、檢附單據』為必要欄位。");
				return false;
			}
			
			if(($scope.inputVO.MED_YN == undefined || $scope.inputVO.MED_YN == false) 
					&& ($scope.inputVO.NUR_YN == undefined || $scope.inputVO.NUR_YN == false) 
					&& ($scope.inputVO.EDU_YN == undefined || $scope.inputVO.EDU_YN == false) 
					&& ($scope.inputVO.OTR_YN == undefined || $scope.inputVO.OTR_YN == false)) {
				$scope.showErrorMsg("『限制用途、檢附單據』為必要欄位。");
				return false;
			}
			
			if($scope.inputVO.MED_YN == true && $scope.radioLock != true) {
				if(($scope.inputVO.MED_PAY_FOR_A == undefined || $scope.inputVO.MED_PAY_FOR_A == false)
						&& ($scope.inputVO.MED_PAY_FOR_B == undefined || $scope.inputVO.MED_PAY_FOR_B == false)) {
					$scope.showErrorMsg("『限制用途、檢附單據_醫療費』至少擇一輸入。");
					return false;
				}
			}
			
			if($scope.inputVO.NUR_YN == true) {
				if(($scope.inputVO.NUR_PAY_FOR_A == undefined || $scope.inputVO.NUR_PAY_FOR_A == false)
						&& ($scope.inputVO.NUR_PAY_FOR_B == undefined || $scope.inputVO.NUR_PAY_FOR_B == false)) {
					$scope.showErrorMsg("『限制用途、檢附單據_安養護機構及看護支出』至少擇一輸入。");
					return false;
				}
			}
			
			//其他給付
			if ($scope.inputVO.OTR_YN == true) {
				if ($scope.inputVO.CON_CURR == 'F') {
					// 外幣
					if (($scope.checkNull($scope.inputVO.MAR_CURR) || $scope.checkNull($scope.inputVO.MAR_AMT)) && 
						($scope.checkNull($scope.inputVO.BIR_CURR) || $scope.checkNull($scope.inputVO.BIR_AMT)) &&
						($scope.checkNull($scope.inputVO.HOS_CURR) || $scope.checkNull($scope.inputVO.HOS_AMT)) &&
						($scope.checkNull($scope.inputVO.OTR_CURR) || $scope.checkNull($scope.inputVO.OTR_AMT))
					) {
						$scope.showErrorMsg("『限制用途、檢附單據』若勾選『其他』至少需完整填入一項。");
						return false;
					}
					
					// 外幣（選擇幣別後一定要輸入金額才能確認送出。）
					if (!$scope.checkNull($scope.inputVO.MAR_CURR) && $scope.checkNull($scope.inputVO.MAR_AMT)) {
						$scope.showErrorMsg("『限制用途、檢附單據_其他_結婚』若已選擇幣別，則金額為必要欄位。");
						return false;
					}
					if (!$scope.checkNull($scope.inputVO.BIR_CURR) && $scope.checkNull($scope.inputVO.BIR_AMT)) {
						$scope.showErrorMsg("『限制用途、檢附單據_其他_生育』若已選擇幣別，則金額為必要欄位。");
						return false;
					}
					if (!$scope.checkNull($scope.inputVO.HOS_CURR) && $scope.checkNull($scope.inputVO.HOS_AMT)) {
						$scope.showErrorMsg("『限制用途、檢附單據_其他_購屋』若已選擇幣別，則金額為必要欄位。");
						return false;
					}
					if (!$scope.checkNull($scope.inputVO.OTR_CURR) && $scope.checkNull($scope.inputVO.OTR_AMT)) {
						$scope.showErrorMsg("『限制用途、檢附單據_其他_其他』若已選擇幣別，則金額為必要欄位。");
						return false;
					}
					
					if ($scope.checkNull($scope.inputVO.MAR_CURR) && !$scope.checkNull($scope.inputVO.MAR_AMT)) {
						$scope.showErrorMsg("『限制用途、檢附單據_其他_結婚』請選擇幣別。");
						return false;
					}
					if ($scope.checkNull($scope.inputVO.BIR_CURR) && !$scope.checkNull($scope.inputVO.BIR_AMT)) {
						$scope.showErrorMsg("『限制用途、檢附單據_其他_生育』請已選擇幣別。");
						return false;
					}
					if ($scope.checkNull($scope.inputVO.HOS_CURR) && !$scope.checkNull($scope.inputVO.HOS_AMT)) {
						$scope.showErrorMsg("『限制用途、檢附單據_其他_購屋』請已選擇幣別。");
						return false;
					}
					if ($scope.checkNull($scope.inputVO.OTR_CURR) && !$scope.checkNull($scope.inputVO.OTR_AMT)) {
						$scope.showErrorMsg("『限制用途、檢附單據_其他_其他』請已選擇幣別。");
						return false;
					}
				} else {
					// 台幣
					if ($scope.checkNull($scope.inputVO.MAR_AMT) && $scope.checkNull($scope.inputVO.BIR_AMT) 
							&& $scope.checkNull($scope.inputVO.HOS_AMT) && $scope.checkNull($scope.inputVO.OTR_AMT)) {
						$scope.showErrorMsg("『限制用途、檢附單據』若勾選『其他』至少需填寫一項。");
						return false;
					}
				}
							
				if(!$scope.checkNull($scope.inputVO.OTR_ITEM) && $scope.checkNull($scope.inputVO.OTR_AMT)) {
					$scope.showErrorMsg("『限制用途、檢附單據_其他_其他』若已輸入其他給付項目，則金額為必要欄位。");
					return false;
				}
				
				if($scope.checkNull($scope.inputVO.OTR_ITEM) && !$scope.checkNull($scope.inputVO.OTR_AMT)) {
					$scope.showErrorMsg("『限制用途、檢附單據_其他_其他』請輸入其他給付項目。");
					return false;
				}
			}
		}
		//檢核『固定給付』
		$scope.checkFixedPay();		
		
		//關係人基本資料檢核
		for (var i = 1; i <= 5; i++) {
			if ($scope.inputVO['custList' + i] != undefined && !$scope.checkNull($scope.inputVO['custList' + i].CUST_ID)) {
				//行外客戶姓名為必輸
				if ($scope.checkNull($scope.inputVO['custList' + i].CUST_NAME)) {
					$scope.showErrMsg(i, "姓名為必要欄位。");
					return false;
				}
				//
				if ($scope.checkNull($scope.inputVO['custList' + i].TEL) && $scope.checkNull($scope.inputVO['custList' + i].MOBILE_NO)) {
					$scope.showErrMsg(i, "電話或手機至少擇一填寫。");
					return false;
				}
				//通訊地址為必輸條件
				if ($scope.checkNull($scope.inputVO['custList' + i].COM_ZIP_CODE) || $scope.checkNull($scope.inputVO['custList' + i].COM_ADDRESS)) {
					$scope.showErrMsg(i, "通訊地址及郵遞區號為必要欄位。");
					return false;
				}				
				//
				if (i != 1 && ($scope.inputVO['custList' + i].PAY_YN == undefined || $scope.inputVO['custList' + i].PAY_YN == null)) {
					$scope.showErrMsg(i, "需選擇有無『報酬及給付』。");
					return false;
				}
				//
				if (i != 1 && $scope.inputVO['custList' + i].PAY_YN == 'Y') {
					//
					if ($scope.checkNull($scope.inputVO['custList' + i].PAY_AMT) ||
						$scope.checkNull($scope.inputVO['custList' + i].PAY_FREQ) ||
					   ($scope.inputVO['custList' + i].PAY_FREQ == 'A' && 
					    $scope.checkNull($scope.inputVO['custList' + i].PAY_FREQ_A))
					) {
						$scope.showErrMsg(i, "報酬及給付皆為必要欄位。");
						return false;
					}
					//
					if(i <= 3) {	//次順位與第三順位監察人不需輸入約定給付帳號
						if ($scope.checkNull($scope.inputVO['custList' + i].AGR_ACC_TYPE)) {
							$scope.showErrMsg(i, "約定給付帳號為必要欄位。");
							return false;
						} else {
							if ($scope.inputVO['custList' + i].AGR_ACC_TYPE == 'A') {
								//本行帳號
								if($scope.inputVO['custList' + i].AGR_ACC == 'null-null' || 
										$scope.checkNull($scope.inputVO['custList' + i].AGR_ACC)) {
									$scope.showErrMsg(i, "約定給付帳號需選擇本行帳號。");
									return false; 
								}
							   
								//若資料中的帳號不在電文回的帳號LIST中，則錯誤
								var findAcct = $filter('filter')($scope['accList' + i], {DATA: $scope.inputVO['custList' + i].AGR_ACC});
								if(findAcct == null || findAcct.length == 0) {
									$scope.showErrMsg(i, "約定給付帳號需選擇本行帳號。");
									return false; 
								}
							}
							
							if ($scope.inputVO['custList' + i].AGR_ACC_TYPE == 'B' && (
								$scope.checkNull($scope.inputVO['custList' + i].OTR_BANK) ||
								$scope.checkNull($scope.inputVO['custList' + i].OTR_BRANCH) ||
								$scope.checkNull($scope.inputVO['custList' + i].OTR_ACC)					
							)) {
								$scope.showErrMsg(i, "約定給付帳號需輸入他行銀行、分行、帳號。");
								return false;
							}
						}
					}
				}
				//
				if (i == 1) {
					if ($scope.checkNull($scope.inputVO.custList1.AGR_ACC_TYPE)) {
						$scope.showErrMsg(i, "約定給付帳號為必要欄位。");
						return false;
					} else {
						if ($scope.inputVO.custList1.AGR_ACC_TYPE == 'A') {
							if($scope.inputVO.custList1.AGR_ACC == 'null-null' || 
									$scope.checkNull($scope.inputVO['custList' + i].AGR_ACC)) {
								$scope.showErrMsg(i, "約定給付帳號需選擇本行帳號。");
								return false; 
							}
							
							//若資料中的帳號不在電文回的帳號LIST中，則錯誤
							var findAcct = $filter('filter')($scope['accList' + i], {DATA: $scope.inputVO['custList' + i].AGR_ACC});
							if(findAcct == null || findAcct.length == 0) {
								$scope.showErrMsg(i, "約定給付帳號需選擇本行帳號。");
								return false; 
							}
						}
						if ($scope.inputVO.custList1.AGR_ACC_TYPE == 'B' && (
							$scope.checkNull($scope.inputVO.custList1.OTR_BANK) ||
							$scope.checkNull($scope.inputVO.custList1.OTR_BRANCH) ||
							$scope.checkNull($scope.inputVO.custList1.OTR_ACC)					
						)) {
							$scope.showErrMsg(i, "約定給付帳號需輸入他行銀行、分行、帳號。");
							return false;
						}
					}
					
					if ($scope.inputVO.custList1.MINOR_YN == 'Y' && (
						$scope.inputVO.custList1.LEG_AGENT_ID1 == undefined || 
						$scope.inputVO.custList1.LEG_AGENT_ID1 == '')) {
						$scope.showErrMsg(i, "若為未成年，則法定代理人至少輸入一人。");
						return false;
					}
				}
				//
				if (i == 2) {
					if ($scope.inputVO.custList2.DIS_TYPE == undefined) {
						$scope.showErrorMsg("監察人解任方式為必要欄位。");
						return false;
					}
				}
			}
		}
		if ($scope.inputVO.custList1.MINOR_YN == 'Y') {
			for (var i = 1; i <= 2; i++) {
				if ($scope.inputVO.custList1['LEG_AGENT_ID' + i] != undefined && $scope.inputVO.custList1['LEG_AGENT_ID' + i] != '' && 
				   ($scope.inputVO.custList1['LEG_AGENT_NAME' + i] == undefined || $scope.inputVO.custList1['LEG_AGENT_NAME' + i] == '')) {
					$scope.showErrorMsg("未留存法定代理人姓名。");
					return false;								
				}
				if ($scope.inputVO.custList1['LEG_AGENT_ID' + i] != undefined && $scope.inputVO.custList1['LEG_AGENT_ID' + i] != '' && 
				   ($scope.inputVO.custList1['LEG_AGENT_TEL' + i] == undefined || $scope.inputVO.custList1['LEG_AGENT_TEL' + i] == '')) {
					$scope.showErrorMsg("未留存法定代理人電話。");
					return false;								
				}
				if ($scope.inputVO.custList1['LEG_AGENT_ID' + i] != undefined && $scope.inputVO.custList1['LEG_AGENT_ID' + i] != '' && 
				   ($scope.inputVO.custList1['LEG_AGENT_REL' + i] == undefined || $scope.inputVO.custList1['LEG_AGENT_REL' + i] == '')) {
					$scope.showErrorMsg("未勾選法定代理人關係。");
						return false;								
				}
			}
			if ($scope.inputVO.custList1.SEAL_RETENTION_MTD == undefined) {
				$scope.showErrorMsg("請選擇印鑑留存方式。");
				return false;
			} else {
				if ($scope.inputVO.custList1.SEAL_RETENTION_MTD == '1') {
					// 未滿 7 歲
					if ($scope.inputVO.custList1.SEAL_UNDER7 == undefined) {
						$scope.showErrorMsg("請選擇印鑑併留方式。");
						return false;
					} else {
						if ($scope.inputVO.custList1.SEAL_UNDER7 == '2' && 
						   ($scope.inputVO.custList1.SEAL_UNDER7_NAME == undefined || 
							$scope.inputVO.custList1.SEAL_UNDER7_NAME == '')) {
							// 僅併留____印鑑
							$scope.showErrorMsg("請選擇僅併留哪位法定代理人。");
							return false;
						}
					}
				} else if ($scope.inputVO.custList1.SEAL_RETENTION_MTD == '2') {
					// 滿 7 歲未滿 20 歲
					if ($scope.inputVO.custList1.SEAL_UNDER20_1 == undefined) {
						$scope.showErrorMsg("請選擇是否併留印鑑。");
						return false;
					} else {
						if ($scope.inputVO.custList1.SEAL_UNDER20_1 == '2') {
							// 併留印鑑(續填，二擇一)
							if ($scope.inputVO.custList1.SEAL_UNDER20_2 == undefined) {
								$scope.showErrorMsg("請選擇印鑑併留方式。");
								return false;								
							} else {
								if ($scope.inputVO.custList1.SEAL_UNDER20_2 == '2' && 
								   ($scope.inputVO.custList1.SEAL_UNDER20_NAME == undefined ||
									$scope.inputVO.custList1.SEAL_UNDER20_NAME == '')) {
									// 僅併留____印鑑
									$scope.showErrorMsg("請選擇僅併留哪位法定代理人。");
									return false;
								}
							}
						}
					}
				}
			}
		}
		//
		return true;
	}
	
	$scope.showErrMsg = function(nbr, msg) {
		switch (nbr) {
			case 1:
				$scope.showErrorMsg("『委託人』" + msg);
				break;
			case 2:
			case 3:
				$scope.showErrorMsg("『監察人" + (nbr - 1) + "』" + msg);
				break;
			case 4:
				$scope.showErrorMsg("『次順位監察人』" + msg);
				break;
			case 5:
				$scope.showErrorMsg("『第三順位監察人』" + msg);
				break;
		}
	}
	
	$scope.checkNull = function(data) {
		if (data == undefined || data == null || data == '') {
			return true;
		}
		return false;
	}
	
	$scope.focusLeg = function(id) {
		$scope.legAgent = id;
	}
	
	$scope.cleanLegAgent = function(nbr) {
		$scope.inputVO.custList1['LEG_AGENT_ID' + nbr] = undefined;
		$scope.inputVO.custList1['LEG_AGENT_NAME' + nbr] = undefined;
		$scope.inputVO.custList1['LEG_AGENT_TEL' + nbr]  = undefined;
		$scope.inputVO.custList1['LEG_AGENT_REL' + nbr]  = undefined;
		$scope.inputVO.custList1['LEG_AGENT_OTR' + nbr]  = undefined;
		if (nbr == '1') {
			$scope.oneLegAge = undefined;
			$scope.inputVO.custList1.LEG_AGENT_ID2	 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_NAME2 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_TEL2  = undefined;
			$scope.inputVO.custList1.LEG_AGENT_REL2  = undefined;
			$scope.inputVO.custList1.LEG_AGENT_OTR2  = undefined;
		}
		$scope.addLegName();
	}
	
	$scope.addLegName = function() {
		$scope.legNameList = [];
		$scope.inputVO.custList1.SEAL_UNDER7_NAME = undefined;
		var name1 = $scope.inputVO.custList1.LEG_AGENT_NAME1;
		var name2 = $scope.inputVO.custList1.LEG_AGENT_NAME2;
		if (name1 != undefined && name1 != '') {
			$scope.legNameList.push({LABEL: name1, DATA: name1});
		} else {
			$scope.legNameList = [];
		}
		if (name2 != undefined && name2 != '') {
			$scope.legNameList.push({LABEL: name2, DATA: name2});
		}
		
		// 僅有一位法定代理人/監護人，直 接勾選□僅併留 XXX 印鑑
		if ($scope.inputVO.custList1.LEG_AGENT_ID1 != undefined && $scope.inputVO.custList1.LEG_AGENT_ID1 != '' &&
		   ($scope.inputVO.custList1.LEG_AGENT_ID2 == undefined || $scope.inputVO.custList1.LEG_AGENT_ID2 == '')) {
			$scope.oneLegAge = true;
			// 未滿 7 歲
			if ($scope.inputVO.custList1.SEAL_RETENTION_MTD == '1') {
				$scope.inputVO.custList1.SEAL_UNDER7 = '2';
				if ($scope.inputVO.custList1.LEG_AGENT_NAME1 != undefined && $scope.inputVO.custList1.LEG_AGENT_NAME1 != '')
					$scope.inputVO.custList1.SEAL_UNDER7_NAME = $scope.inputVO.custList1.LEG_AGENT_NAME1;							
			}
			// 滿 7 歲未滿 20 歲 && 併留印鑑
			if ($scope.inputVO.custList1.SEAL_RETENTION_MTD == '2' && $scope.inputVO.custList1.SEAL_UNDER20_1 == '2') {
				$scope.inputVO.custList1.SEAL_UNDER20_2 = '2';
				if ($scope.inputVO.custList1.LEG_AGENT_NAME1 != undefined && $scope.inputVO.custList1.LEG_AGENT_NAME1 != '')
					$scope.inputVO.custList1.SEAL_UNDER20_NAME = $scope.inputVO.custList1.LEG_AGENT_NAME1;							
			}
		} else if ($scope.inputVO.custList1.LEG_AGENT_ID1 != undefined && $scope.inputVO.custList1.LEG_AGENT_ID1 != '' &&
				   $scope.inputVO.custList1.LEG_AGENT_ID2 != undefined && $scope.inputVO.custList1.LEG_AGENT_ID2 != '') {
			$scope.oneLegAge = false;
		}
	}
	
	// 取得法定代理人資訊
	$scope.getLegInfo = function(nbr, cust_id) {
		if (cust_id == $scope.legAgent) return;
		if (cust_id != undefined && cust_id != '') {
			// 兩位法代/監護人ID/姓名不得相同，出訊息後清空欄位；出錯誤訊息:兩位法定代理人不得相同。
			var showErrMsg = false;
//			var cust_id = undefined;
			if (nbr == '1') {
				$scope.inputVO.custList1.LEG_AGENT_ID1 = $scope.inputVO.custList1.LEG_AGENT_ID1.toUpperCase().replace(/\s*/g,"");
				cust_id = $scope.inputVO.custList1.LEG_AGENT_ID1;
				if (cust_id == $scope.inputVO.custList1.LEG_AGENT_ID2) {
					$scope.inputVO.custList1.LEG_AGENT_ID1 = undefined;
					showErrMsg = true;
				}
			} else if (nbr == '2') {
				$scope.inputVO.custList1.LEG_AGENT_ID2 = $scope.inputVO.custList1.LEG_AGENT_ID2.toUpperCase().replace(/\s*/g,"");
				cust_id = $scope.inputVO.custList1.LEG_AGENT_ID2;
				if (cust_id == $scope.inputVO.custList1.LEG_AGENT_ID1) {
					$scope.inputVO.custList1.LEG_AGENT_ID2 = undefined;
					showErrMsg = true;
				}
			}
			if (showErrMsg == true) {
				$scope.showErrorMsg("兩位法定代理人不得相同。");
				$scope.cleanLegAgent(nbr);
				return;
			}
			var errFlag = false;
			// 檢核客戶ID合理性
			$scope.sendRecv("MTC110", "getLegInfo", "com.systex.jbranch.app.server.fps.mtc110.MTC110InputVO", {'cust_id': cust_id},
					function(tota, isError) {
						if (!isError) {	
							if (tota[0].body.resultList.length > 0) {
								$scope.inputVO.custList1['LEG_AGENT_NAME' + nbr] = tota[0].body.resultList[0].CUST_NAME;
								$scope.inputVO.custList1['LEG_AGENT_TEL' + nbr]  = tota[0].body.resultList[0].MOBILE_NO;
							} else {
								//行外客戶也可為法定代理人，不須清除
//								$scope.cleanLegAgent(nbr);
							}
							$scope.addLegName();
						} else {
							$scope.cleanLegAgent(nbr);
						}
			});						
		} else {
			$scope.cleanLegAgent(nbr);
		}
	}
	
	// 選取本行，則清除他行資訊
	$scope.clickFbnBank = function(nbr) {
		$scope.inputVO['custList' + nbr].OTR_BANK = undefined;
		$scope.inputVO['custList' + nbr].OTR_BRANCH = undefined;
		$scope.inputVO['custList' + nbr].OTR_ACC = undefined;
	}
	
	// 如客戶非留存本行帳號，增加提醒說明如下
	$scope.clickOtrBank = function(nbr) {
		// 清除本行約定給付帳號
		$scope.inputVO['custList' + nbr].AGR_ACC = undefined; 
		$scope['BRA_NAME' + nbr] = undefined;
		
		var mbr = "";
		if (nbr == 1)
			mbr = "委託人";
		if (nbr == 2)
			mbr = "監察人1";
		if (nbr == 3)
			mbr = "監察人2";
		if (nbr == 4)
			mbr = "次順位監察人";
		if (nbr == 5)
			mbr = "第三順位監察人";
		
		
		var cur = $scope.inputVO.CON_CURR == 'T' ? '台幣' : '外幣';
		var warn = '1.約定給付帳號需為' + mbr + '本人之「' + cur + '」帳號。	2.給付時將依本行匯款收費標準進行收費。'
//		$confirm({text: warn}, {size: 'sm'}).then(function() {
//		});
		$scope.showMsg(warn);
	}
	
	$scope.clickAge = function(nbr) {
		if ($scope.inputVO['BEN_AGE_YN' + nbr] != true) {
			if (nbr == '1') {
				$scope.inputVO.BEN_AGE1 = undefined;
				$scope.inputVO.BEN_CURR1 = undefined;
				$scope.inputVO.BEN_AMT1 = undefined;
			} else if (nbr == '2') {
				$scope.inputVO.BEN_AGE2 = undefined;
				$scope.inputVO.BEN_AGE3 = undefined;
				$scope.inputVO.BEN_CURR2 = undefined;
				$scope.inputVO.BEN_AMT2 = undefined;
			} else if (nbr == '3') {
				$scope.inputVO.BEN_AGE4 = undefined;
				$scope.inputVO.BEN_CURR3 = undefined;
				$scope.inputVO.BEN_AMT3 = undefined;
			}
		}
	}
	
	// 檢核固定給付年紀：歲數需遞增/相同(規則如右)，登打後出現錯誤訊息(歲數應遞增/相同)，並自動清空已登打數字。
	$scope.checkBenAge = function(nbr) {
		var age1 = $scope.inputVO.BEN_AGE1 == undefined || $scope.inputVO.BEN_AGE1 == '' ? undefined : $scope.inputVO.BEN_AGE1;
		var age2 = $scope.inputVO.BEN_AGE2 == undefined || $scope.inputVO.BEN_AGE2 == '' ? undefined : $scope.inputVO.BEN_AGE2;
		var age3 = $scope.inputVO.BEN_AGE3 == undefined || $scope.inputVO.BEN_AGE3 == '' ? undefined : $scope.inputVO.BEN_AGE3;
		var age4 = $scope.inputVO.BEN_AGE4 == undefined || $scope.inputVO.BEN_AGE4 == '' ? undefined : $scope.inputVO.BEN_AGE4;
		
		if ((age1 != undefined && age2 != undefined && (age1 > age2)) ||
			(age4 != undefined && age3 != undefined && (age4 < age3))) {
			$scope.showErrorMsg("歲數應遞增/相同");
			$scope.inputVO['BEN_AGE' + nbr] = undefined;
			
		} else if (age3 != undefined && age2 != undefined && (age3 <= age2)) {
			if (nbr == '2') {
				$scope.showErrorMsg("歲數應小於" + age3 + "歲");				
			} else if (nbr == '3') {
				$scope.showErrorMsg("歲數應大於" + age2 + "歲");
			}
			$scope.inputVO['BEN_AGE' + nbr] = undefined;
		}
	}
	
	$scope.clickMed = function() {
		if ($scope.radioLock == true && $scope.inputVO.MED_YN == true) {
			$scope.inputVO.MED_PAY_FOR_A = true;	// 無監察人時，醫療費限入受益人帳戶。
		}
		
		if ($scope.inputVO.MED_YN == false) {
			$scope.inputVO.MED_PAY_FOR_A = false;
			$scope.inputVO.MED_PAY_FOR_B = false;
		}
	}
	
	$scope.clickNur = function() {
		if ($scope.inputVO.NUR_YN != true) {
			$scope.inputVO.NUR_PAY_FOR = undefined;
		}
		
		if ($scope.inputVO.NUR_YN == false) {
			$scope.inputVO.NUR_PAY_FOR_A = false;
			$scope.inputVO.NUR_PAY_FOR_B = false;
		}
	}
	
	$scope.clickPayFreq = function(nbr) {
		if ($scope.inputVO['custList' + nbr].PAY_FREQ != 'A') {
			$scope.inputVO['custList' + nbr].PAY_FREQ_A	= undefined;	
		}
	}
	
	$scope.clickPayN = function(nbr) {
		$scope.inputVO['custList' + nbr].PAY_YN = 'N';
		$scope.inputVO['custList' + nbr].PAY_CURR = undefined;
		$scope.inputVO['custList' + nbr].PAY_AMT = undefined;
		$scope.inputVO['custList' + nbr].PAY_FREQ = undefined;
		$scope.inputVO['custList' + nbr].PAY_FREQ_A = undefined;
		$scope.inputVO['custList' + nbr].AGR_ACC_TYPE = undefined;
		$scope.inputVO['custList' + nbr].AGR_ACC = undefined;
		$scope['BRA_NAME' + nbr] = undefined;
		$scope.inputVO['custList' + nbr].OTR_BANK = undefined;
		$scope.inputVO['custList' + nbr].OTR_BRANCH = undefined;
		$scope.inputVO['custList' + nbr].OTR_ACC = undefined;
	}
	
	$scope.clickMinor = function() {
		if ($scope.inputVO.custList1.MINOR_YN == 'N') {
			$scope.legNameList = [];
			$scope.inputVO.custList1.LEG_AGENT_ID1 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_ID2 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_NAME1 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_NAME2 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_TEL1 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_TEL2 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_REL1 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_OTR1 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_REL2 = undefined;
			$scope.inputVO.custList1.LEG_AGENT_OTR2 = undefined;
			$scope.inputVO.custList1.SEAL_RETENTION_MTD = undefined;
			$scope.inputVO.custList1.SEAL_UNDER7 = undefined;
			$scope.inputVO.custList1.SEAL_UNDER7_NAME = undefined;
			$scope.inputVO.custList1.SEAL_UNDER20_1 = undefined;
			$scope.inputVO.custList1.SEAL_UNDER20_2 = undefined;
			$scope.inputVO.custList1.SEAL_UNDER20_NAME = undefined;
		}
	}
	
//	$scope.clickRel = function(nbr) {
//		if ($scope.inputVO.custList1['LEG_AGENT_REL' + nbr] != '3') {
//			$scope.inputVO.custList1['LEG_AGENT_OTR' + nbr] = undefined;
//		}
//	}
	
	$scope.clickSealMtd = function() {
		if ($scope.inputVO.custList1.SEAL_RETENTION_MTD == '1') {
			// 選：未滿 7 歲
			$scope.inputVO.custList1.SEAL_UNDER20_1 = undefined;
			$scope.inputVO.custList1.SEAL_UNDER20_2	= undefined;
			$scope.inputVO.custList1.SEAL_UNDER20_NAME = undefined;
			
			if ($scope.oneLegAge == true) {
				$scope.inputVO.custList1.SEAL_UNDER7 = '2';
				if ($scope.inputVO.custList1.LEG_AGENT_NAME1 != undefined && $scope.inputVO.custList1.LEG_AGENT_NAME1 != '')
					$scope.inputVO.custList1.SEAL_UNDER7_NAME = $scope.inputVO.custList1.LEG_AGENT_NAME1;				
			}
		} else if ($scope.inputVO.custList1.SEAL_RETENTION_MTD == '2') {
			// 選：滿 7 歲未滿 20 歲
			$scope.inputVO.custList1.SEAL_UNDER7 = undefined;
			$scope.inputVO.custList1.SEAL_UNDER7_NAME = undefined;
		}
	}
	
	$scope.clickUnder7 = function() {
		if ($scope.inputVO.custList1.SEAL_UNDER7 == '1')
			$scope.inputVO.custList1.SEAL_UNDER7_NAME = undefined;
	}
	
	$scope.clickUnder20 = function() {
		if ($scope.inputVO.custList1.SEAL_UNDER20_1 == '1') {
			$scope.inputVO.custList1.SEAL_UNDER20_2 = undefined;
			$scope.inputVO.custList1.SEAL_UNDER20_NAME = undefined;
		} else if ($scope.inputVO.custList1.SEAL_UNDER20_1 == '2') {
			if ($scope.oneLegAge == true) {
				$scope.inputVO.custList1.SEAL_UNDER20_2 = '2';
				if ($scope.inputVO.custList1.LEG_AGENT_NAME1 != undefined && $scope.inputVO.custList1.LEG_AGENT_NAME1 != '')
					$scope.inputVO.custList1.SEAL_UNDER20_NAME = $scope.inputVO.custList1.LEG_AGENT_NAME1;
			}
		}
	}
	
	$scope.checkZip = function(zip_code, nbr) {
		if (zip_code.length < 3) {
			$scope.showErrorMsg("郵遞區號：數字最少 3 碼。");
			
			if (nbr == '1_1') {
				$scope.inputVO.custList1.CEN_ZIP_CODE = undefined;
			} else if (nbr == '1_2') {
				$scope.inputVO.custList1.COM_ZIP_CODE = undefined;
			} else {
				$scope.inputVO['custList' + nbr].COM_ZIP_CODE = undefined;
			}
		}
	}
	
	$scope.checkAmount = function(amt, type) {
		if (type == '1') {
			if (amt == 0 || amt.includes('.')) {
				$scope.showErrorMsg("金額不得為 0，需為整數。");
				return false;
			} else {
				return true;
			}			
		} else if (type == '2') {
			if (amt.includes('.')) {
				$scope.showErrorMsg("金額需為整數。");
				return false;
			} else {
				return true;
			}	
		}
	}
	
	// 檢核固定給付金額：金額不可為 0，給付金額不得大於交付金額
	$scope.checkGiveAmt = function(type, nbr) {
		var amt = undefined;
		var curr = undefined;
		switch (type) {
			case 1:
				amt = $scope.inputVO['BEN_AMT' + nbr];
				if($scope.inputVO.CON_CURR == 'F') curr = $scope.inputVO['BEN_CURR' + nbr];
				break;
			case 2:
			case 3:	
				amt = $scope.inputVO[nbr + '_AMT'];
				if($scope.inputVO.CON_CURR == 'F') curr = $scope.inputVO[nbr + '_CURR'];
				break;
		}
		
		var cleanVal = false;
		if (!$scope.checkAmount(amt, '1')) {
			cleanVal = true;
		}
		
		//由幣別取得交付金額
		var giveAmt = 0;
		if(($scope.inputVO.CON_CURR == 'F' && !$scope.checkNull(curr)) || $scope.inputVO.CON_CURR == 'T') {			
			if(curr == $scope.inputVO.CURR1 || $scope.inputVO.CON_CURR == 'T')
				giveAmt = $scope.inputVO.AMT1;
			else if(curr == $scope.inputVO.CURR2)
				giveAmt = $scope.inputVO.AMT2;
			else if(curr == $scope.inputVO.CURR3)
				giveAmt = $scope.inputVO.AMT3;
		
			// 給付金額不得大於交付金額
			if (parseInt(amt) > parseInt(giveAmt)) {
				cleanVal = true;
				$scope.showErrorMsg("給付金額不得大於交付金額");
			}
		} else {
			//外幣信託，沒有選幣別
			cleanVal = true;
			$scope.showErrorMsg("請先輸入給付金額幣別");
		}		
		
		if (cleanVal == true) {
			if (type == '1') {
				$scope.inputVO['BEN_AMT' + nbr] = undefined;
			} else {
				$scope.inputVO[nbr + '_AMT'] = undefined;
			}
		}
	}
	
	// 檢核報酬給付金額
	$scope.checkPayAmt = function(nbr) {
		var cleanPayAmt = false;
		var amt = $scope.inputVO['custList' + nbr].PAY_AMT;
		if (!$scope.checkAmount(amt, '1')) {
			cleanPayAmt = true;
		}
//		alert(parseInt(amt));
//		alert(parseInt($scope.inputVO.AMT1));
//		alert(parseInt(amt) > parseInt($scope.inputVO.AMT1));
		// 報酬給付金額不得大於交付金額
		if (parseInt(amt) > parseInt($scope.inputVO.AMT1)) {
			cleanPayAmt = true;
			$scope.showErrorMsg("報酬給付金額不得大於交付金額");
		}
		if (cleanPayAmt == true) {
			$scope.inputVO['custList' + nbr].PAY_AMT = undefined;
		}
	}
	
	// 檢核交付金額
	$scope.checkAmt = function(nbr) {
		var amt = $scope.inputVO['AMT' + nbr];
		
		if (nbr == 1) {
			// 報酬給付金額不得大於交付金額
			for (var i = 2; i <= 5; i++) {
				var mbr = "";
				if (i == 2)
					mbr = "『監察人1』";
				if (i == 3)
					mbr = "『監察人2』";
				if (i == 4)
					mbr = "『次順位監察人』";
				if (i == 5)
					mbr = "『第三順位監察人』";
				
				if ($scope.inputVO['custList' + i] != undefined && 
					$scope.inputVO['custList' + i].PAY_AMT != undefined &&
					amt < $scope.inputVO['custList' + i].PAY_AMT
				) {
					$scope.showErrorMsg("報酬給付金額不得大於交付金額，故清除" + mbr + "報酬給付金額。");
					$scope.inputVO['custList' + i].PAY_AMT = undefined;
				}
			}
		}
		// 金額不得為 0，需為整數。
		if (!$scope.checkAmount(amt, '1')) {
			$scope.inputVO['AMT' + nbr] = undefined;
		}
	}
	
	// 檢核終止金額：可為 0，須為整數，不得大於交付金額
	$scope.checkEndAmtLimit = function() {
		var amt = $scope.inputVO.END_AMT_LIMIT;
		if (!$scope.checkAmount(amt, '2')) {
			$scope.inputVO.END_AMT_LIMIT = undefined;
		}
		if (amt > $scope.inputVO.AMT1) {
			$scope.inputVO.END_AMT_LIMIT = undefined;
			$scope.showErrorMsg("終止金額不得大於交付金額");
		}
	}
	
	// 檢核終止年：年份不可為 0，且須為整數。
	$scope.checkEndYear = function() {
		if ($scope.inputVO.END_YEARS == 0) {
			$scope.showErrorMsg("年份不可為 0，且須為整數。");
			$scope.inputVO.END_YEARS = undefined;
		}
	}
	
	// 變更交付幣別
	$scope.$watch('inputVO.CON_CURR', function(newValue, oldValue) {
		if (newValue != oldValue) {
			if ($scope.inputVO.CON_NO == undefined) {
				$scope.initAllFee();	// 預帶標準收費，可修改。
			}
			//外幣：交付金額第一欄預設呈現 USD，不可刪除; 向下可再選擇其他幣別。
			if (newValue == 'F') {
				$scope.inputVO.CURR1 = 'USD';
				$scope.changeCurr(1);
			} else {
				$scope.inputVO.CURR1 = 'TWD';
				$scope.currList = [];
			}
			
			for (var i = 1; i <= 5; i++) {
				if ($scope.inputVO['custList' + i] != undefined && 
					$scope.inputVO['custList' + i].CUST_ID != undefined &&
					$scope.inputVO['custList' + i].CUST_ID != ''
				) {
					$scope.clickPayN(i);
					$scope.getAcct(i, '2');
					
//					if (i == 1) {
//						$scope.getAcct(i, '2');
//					} else {
//						if (newValue == 'T') {
//							// 台幣
//							$scope.getAcct(i, '2');
//						} else if (newValue == 'F') {
//							// 外幣
//							if ($scope.inputVO['custList' + i].PAY_CURR != undefined) {
//								$scope.getAccByCurr(i);
//							}
//						}
//					}
				}
			}
			
			// 交付幣別改變，涉及幣別向下需填入之金額應一併清空
			for (var i = 1; i <= 3; i++) {
				if (i != 1) {
					$scope.inputVO['CURR' + i] = undefined;
				}
				$scope.inputVO['AMT' + i] = undefined;
				$scope.inputVO['BEN_CURR1' + i] = undefined;
				$scope.inputVO['BEN_AMT' + i] = undefined;
			}
			$scope.inputVO.M_CURR = undefined;
			$scope.inputVO.M_AMT = undefined;
			$scope.inputVO.A_CURR = undefined;
			$scope.inputVO.A_AMT = undefined;
			$scope.inputVO.MAR_CURR = undefined;
			$scope.inputVO.MAR_AMT = undefined;
			$scope.inputVO.BIR_CURR = undefined;
			$scope.inputVO.BIR_AMT = undefined;
			$scope.inputVO.HOS_CURR = undefined;
			$scope.inputVO.HOS_AMT = undefined;
			$scope.inputVO.OTR_CURR = undefined;
			$scope.inputVO.OTR_AMT = undefined;
			
			$scope.inputVO.END_AMT_LIMIT = undefined;
		}
	});
	
	$scope.clickClose = function() {
		if ($scope.type != 'read' && $scope.saveFlag == false) {
			$confirm({text: '尚未進行存檔作業，請確定是否需存檔?'}, {size: 'sm'}).then(function() {
				// 確認
				$scope.save(false);
				$scope.closeThisDialog('successful');	
			},function() {
				// 取消
				$scope.closeThisDialog('cancel');
			});
		} else {
			$scope.closeThisDialog('cancel');
		}
	}
	
	$scope.init();
});