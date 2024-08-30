'use strict';
eSoafApp.controller('PMS141Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $compile, $q, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS141Controller";
	
	getParameter.XML(["COMMON.YES_NO", "PMS.JOB_TITLE_NAME", "PMS.NUM_NEW_APPOINTMENTS", "PMS.VIOLATION_TYPE", "PMS.CUST_COMPLAINT_TYPE"], function(totas) {
		if (totas) {
	        $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
	        $scope.mappingSet['PMS.JOB_TITLE_NAME'] = totas.data[totas.key.indexOf('PMS.JOB_TITLE_NAME')];
	        $scope.mappingSet['PMS.NUM_NEW_APPOINTMENTS'] = totas.data[totas.key.indexOf('PMS.NUM_NEW_APPOINTMENTS')];
	        $scope.mappingSet['PMS.VIOLATION_TYPE'] = totas.data[totas.key.indexOf('PMS.VIOLATION_TYPE')];
	        $scope.mappingSet['PMS.CUST_COMPLAINT_TYPE'] = totas.data[totas.key.indexOf('PMS.CUST_COMPLAINT_TYPE')];
		}
	});
	
	$scope.init = function(){
		$scope.inputVO = {
			prod:  [],
			sales: [],
			income : []
		};
	
		$scope.POR = 0;
	};
	$scope.init();
	
	$scope.getBasic = function () {				
		var types = ['PROD_YIELD', 'BASIC_THRESHHOLD', 'NUM_NEW_APPOINTMENTS', 'BONUS_RATE', 'FINANCIAL_INDICATOR', 'BEHAVIORAL_INDICATOR', 'BUSI_DAYS'];
		
		$scope.PROD_YIELD_List = [];
		$scope.BASIC_THRESHHOLD_List = [];
		$scope.NUM_NEW_APPOINTMENTS_List = [];
		$scope.BONUS_RATE_List = [];
		$scope.FINANCIAL_INDICATOR_List = [];
		$scope.BEHAVIORAL_INDICATOR_List = [];
		$scope.BUSI_DAYS_List = [];
		
		angular.forEach(types, function(item, index, objs){
			$scope.sendRecv("PMS142", "queryData", "com.systex.jbranch.app.server.fps.pms142.PMS142InputVO", {'selectType': item}, function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
						return;
					}
					
					switch (item) {
						case "PROD_YIELD":
							$scope.PROD_YIELD_List = [];
							$scope.PROD_YIELD_List = tota[0].body.resultList;
							$scope.mappingSet['PROD_YIELD_0'] = tota[0].body.resultList;
							break;
						case "BASIC_THRESHHOLD":
							$scope.BASIC_THRESHHOLD_List = [];
							$scope.BASIC_THRESHHOLD_List = tota[0].body.resultList;
							break;
						case "NUM_NEW_APPOINTMENTS":
							$scope.NUM_NEW_APPOINTMENTS_List = [];
							$scope.NUM_NEW_APPOINTMENTS_List = tota[0].body.resultList;
							break;
						case "BONUS_RATE":
							$scope.BONUS_RATE_List = [];
							$scope.BONUS_RATE_List = tota[0].body.resultList;
							break;
						case "FINANCIAL_INDICATOR":
							$scope.FINANCIAL_INDICATOR_List = [];
							$scope.FINANCIAL_INDICATOR_List = tota[0].body.resultList;
							break;
						case "BEHAVIORAL_INDICATOR":
							$scope.BEHAVIORAL_INDICATOR_List = [];
							$scope.BEHAVIORAL_INDICATOR_List = tota[0].body.resultList;
							break;
						case "BUSI_DAYS":
							$scope.BUSI_DAYS_List = [];
							
							angular.forEach(tota[0].body.resultList, function(row, index, objs){
								$scope.BUSI_DAYS_List.push({LABEL: row.YYYYMM, DATA: row.BUSI_DAYS + ''});
							});
							
							break;
					}
					
					return;
				}
			});
		});
	}
	$scope.getBasic();
	
	$scope.changePROD_YIELD_List = function (columnFlag) {
		var columnFlagNext = columnFlag + 1;
		$scope.mappingSet['PROD_YIELD_' + columnFlagNext] = $scope.PROD_YIELD_List;
		
//		for (var i = 0; i <= columnFlagNext; i++) {
//			angular.forEach($scope.PROD_YIELD_List, function(row, index, objs){
//				if (row.LABEL == $scope.inputVO.prod[columnFlag]) {
//					$scope.mappingSet['PROD_YIELD_' + columnFlagNext].splice(index, 1);
//				}
//			});
//    	}
	}
	
	$scope.calculate_P = function (type) {
		if ($scope.inputVO.sales[type] == undefined || $scope.inputVO.sales[type] == '') {
			$scope.inputVO.income[type] = '';
			return;
		} else {
			angular.forEach($scope.PROD_YIELD_List, function(row, index, array){
				if ($scope.inputVO.prod[type] == row.PROD) {
					$scope.inputVO.income[type] = Math.round($scope.inputVO.sales[type] * row.YIELD / 100);
				}
			});
		}
	}
	
	$scope.toNumber = function (type) {
		$scope.inputVO.income[type] = Math.round($scope.inputVO.income[type]);
	}
	
	$scope.getPersonalBonusCal = function () {
		var defer = $q.defer();
		
		if ($scope.inputVO.rmType == undefined || $scope.inputVO.rmType == '' || $scope.inputVO.rmType == null || 
			$scope.inputVO.allIncome == undefined || $scope.inputVO.allIncome == '' || $scope.inputVO.allIncome == null) {
			$scope.showErrorMsg('欄位檢核錯誤：職級、當月收入試算');
		} else {
			// 取得個人獎金率
			$scope.sendRecv("PMS141", "getPersonalBonusCal", "com.systex.jbranch.app.server.fps.pms141.PMS141InputVO", {'JOB_TITLE_NAME': $scope.inputVO.rmType, 'ARBRM': $scope.inputVO.allIncome}, function(tota, isError) {
				if (!isError) {
					$scope.POR = tota[0].body.BONUS_RATE;

					defer.resolve("success");
				}
			});
		}
		
		return defer.promise;
	}
	
	$scope.calculate = function () {
		// 本薪倍數：若 職級 = RM1 且 是否已過試用期 = N，則為3；若條件皆不符合則依各職級查詢「個獎試算參數維護ー基本門檻」
		if ($scope.inputVO.rmType == 'RM1' && $scope.inputVO.isProbation == 'N') {
			$scope.inputVO.salaryMultiple = 3;
		} else {
			angular.forEach($scope.BASIC_THRESHHOLD_List, function(row, index, array){
				if ($scope.inputVO.rmType == row.JOB_TITLE_NAME) {
					$scope.inputVO.salaryMultiple = row.SALARY_MULTIPLE;
				}
			});
		}
		
		// (%)新任成數：
		//   若 職級 = RM1 且 是否為新任RM = Y，則依各職級+新任月數 查詢「個獎試算參數維護ー新任成數」；
		//   若 職級 ≠ RM1 且 是否為新任RM = Y，則依各職級+新任月數 查詢「個獎試算參數維護ー新任成數」；
		//   若條件皆不符合，則為100%
		$scope.inputVO.num_new_appointment = 100;
		if ($scope.inputVO.isNewRM == 'Y') {
			angular.forEach($scope.NUM_NEW_APPOINTMENTS_List, function(row, index, array){
				if ($scope.inputVO.rmType == row.JOB_TITLE_NAME && $scope.inputVO.newMonth == row.MONTHS) {
					$scope.inputVO.num_new_appointment = row.NUM_APPONTMENTS;
				}
			});
		}
		
		// (%)休假折數：若 當月休假天數 >= 5，則當月休假天數 ÷ 當月總工作日 %；若條件皆不符合，則為100%
		$scope.inputVO.vacationFold = 100;
		if ($scope.inputVO.vacationDays >= 5) {
			$scope.inputVO.vacationFold = $scope.inputVO.vacationDays / $scope.inputVO.busiDays * 100;
		}
		
		// 基本門檻：當月本薪 × 本薪倍數 × 新任成數 × 休假折數
		$scope.inputVO.basicThreshold = Math.round($scope.inputVO.monthlySalary * $scope.inputVO.salaryMultiple * ($scope.inputVO.num_new_appointment / 100) * ($scope.inputVO.vacationFold / 100));
		
		// 獎金率成數：若違規態樣 = 理專十誡七項重大，則為50%；若條件皆不符合，則為100%
		$scope.inputVO.bonusRateFold = 100;
		if ($scope.inputVO.violationType == '理專十誡七項重大') {
			$scope.inputVO.bonusRateFold = 50;
		}
		
		// 獎金率：依各職級+當月預估收益查詢「個獎試算參數維護ー獎金率」
		$scope.getPersonalBonusCal().then(function(){
			// 獎金率：獎金率×獎金率成數
			$scope.inputVO.bonusRate = $scope.POR * $scope.inputVO.bonusRateFold / 100;
			
			// === 財務指標得分 START ===
			/* 財務指標得分 
			 * 存款AuM & 投資及保險AuM
			 *   達成率
			 *     存款AuM的預估達成率 >= 「個獎試算參數維護ー財務指標」, 則取「個獎試算參數維護ー財務指標」，否則取存款AuM的預估達成率；
			 *     投資及保險AuM的預估達成率 >= 「個獎試算參數維護ー財務指標」，則取「個獎試算參數維護ー財務指標」，否則取投資及保險AuM的預估達成率
			 *   得分上限
			 *     若存款AuM 達成率×權重 + 投資及保險AuM 達成率×權重 >= 50%，則為 50%；
			 *     若 < 50%，則為存款AuM 達成率×權重 + 投資及保險AuM 達成率×權重
			 * 商品活躍度 & 投保商品配置
			 *   達成率
			 *     商品活躍度的預估達成率 >= 「個獎試算參數維護ー財務指標」，則取「個獎試算參數維護ー財務指標」，否則取商品活躍度的預估達成率；
			 *     投保商品配置的預估達成率 >= 「個獎試算參數維護ー財務指標」，則取「個獎試算參數維護ー財務指標」，否則取投保商品配置的預估達成率
			 *   得分上限
			 *     若商品活躍度 達成率×權重 + 投保商品配置 達成率×權重 >= 50%，則為 50%；
			 *     若 < 50%，則為商品活躍度 達成率×權重 + 投保商品配置 達成率×權重
			 * 財務指標得分 ：存款AuM&投資及保險AuM 的得分上限 + 商品活躍度&投保商品配置 的得分上限
			 */
			angular.forEach($scope.FINANCIAL_INDICATOR_List, function(row, index, array){
				switch (row.INDICATOR_TYPE) {
					case '存款AuM':
						// 達成率
						if ($scope.inputVO.depositAUM >= row.TARGET_MAX) {
							$scope.inputVO.depositAUM_fillRate = row.TARGET_MAX;
						} else {
							$scope.inputVO.depositAUM_fillRate = $scope.inputVO.depositAUM;
						}
						
						// 得分%
						$scope.inputVO.depositAUM_score = ($scope.inputVO.depositAUM_fillRate / 100) * (row.WEIGHTS / 100) * 100;
						
						break;
					case '投資及保險AuM':
						// 達成率
						if ($scope.inputVO.INV_INS_AUM >= row.TARGET_MAX) {
							$scope.inputVO.INV_INS_AUM_fillRate = row.TARGET_MAX;
						} else {
							$scope.inputVO.INV_INS_AUM_fillRate = $scope.inputVO.INV_INS_AUM;
						}
						
						// 得分%
						$scope.inputVO.INV_INS_AUM_score = ($scope.inputVO.INV_INS_AUM_fillRate / 100) * (row.WEIGHTS / 100) * 100;
		
						break;
					case '商品活躍度':
						// 達成率
						if ($scope.inputVO.prodActivity >= row.TARGET_MAX) {
							$scope.inputVO.prodActivity_fillRate = row.TARGET_MAX;
						} else {
							$scope.inputVO.prodActivity_fillRate = $scope.inputVO.prodActivity;
						}
						
						// 得分%
						$scope.inputVO.prodActivity_score = ($scope.inputVO.prodActivity_fillRate / 100) * (row.WEIGHTS / 100) * 100;
		
						break;
					case '投保商品配置':
						// 達成率
						if ($scope.inputVO.INS_prodConfiguration >= row.TARGET_MAX) {
							$scope.inputVO.INS_prodConfiguration_fillRate = row.TARGET_MAX;
						} else {
							$scope.inputVO.INS_prodConfiguration_fillRate = $scope.inputVO.INS_prodConfiguration;
						}
						
						// 得分%
						$scope.inputVO.INS_prodConfiguration_score = ($scope.inputVO.INS_prodConfiguration_fillRate / 100) * (row.WEIGHTS / 100) * 100;
		
						break;
				}
			});
			// 存款AuM & 投資及保險AuM-得分上限
			$scope.inputVO.aumUpperLimit = ($scope.inputVO.depositAUM_score + $scope.inputVO.INV_INS_AUM_score) > 50 ? 50 : ($scope.inputVO.depositAUM_score + $scope.inputVO.INV_INS_AUM_score);
			
			// 商品活躍度 & 投保商品配置-得分上限
			$scope.inputVO.prodUpperLimit = ($scope.inputVO.prodActivity_score + $scope.inputVO.INS_prodConfiguration_score) > 50 ? 50 : ($scope.inputVO.prodActivity_score + $scope.inputVO.INS_prodConfiguration_score);

			// 財務指標得分
			$scope.inputVO.upperLimit = $scope.inputVO.aumUpperLimit + $scope.inputVO.prodUpperLimit;
			// === 財務指標得分 END ===
			
			// === 行為指標 START ===
			// 行為指標-違規：以 違規態樣 至 「個獎試算參數維護ー行為指標」取得 扣減比例；若無符合，則為0%
			$scope.inputVO.violationRate = 0;
			if ($scope.inputVO.isViolation == 'Y' && ($scope.inputVO.violationType != undefined || $scope.inputVO.violationType != '')) {
				angular.forEach($scope.BEHAVIORAL_INDICATOR_List, function(row, index, array){
					if ($scope.inputVO.violationType == row.INDICATOR_TYPE) {
						$scope.inputVO.violationRate = row.DEDUCTION_RATIO;
					}
				});
			}
			
			// 行為指標-客訴(不滿意)：若是否為客訴=Y，則為1%，否則為0%
			$scope.inputVO.dissatisfiedRate = ($scope.inputVO.isCustComplaint == 'Y' ? 1 : 0);
			
			// 行為指標-客訴(可歸責)：以 客訴類型 至「個獎試算參數維護ー行為指標」取得 扣減比例；若無符合，則為0%
			$scope.inputVO.custComplaintRate = 0;
			if ($scope.inputVO.isImputation == 'Y' && ($scope.inputVO.custComplaintType != undefined || $scope.inputVO.custComplaintType != '')) {
				angular.forEach($scope.BEHAVIORAL_INDICATOR_List, function(row, index, array){
					if ($scope.inputVO.custComplaintType == row.INDICATOR_TYPE) {
						$scope.inputVO.custComplaintRate = row.DEDUCTION_RATIO;
					}
				});
			}
			// ==== 行為指標 END ===

			// === final calculate start ===
			// 1.領獎門檻-$scope.inputVO.basicThreshold = 當月本薪 × 本薪倍數 × 新任成數 × 休假折數
			// 2.獎金率-$scope.inputVO.bonusRate = 依「當月預估收益」對照「個獎試算參數維護ー獎金率」
			// 3.財務指標考核(得分)-$scope.inputVO.upperLimit：參考上述
			
			// 4.行為指標考核(扣減金額)：參考上述 合併下述公式
			//   行為指標 = 行為指標-違規 與 行為指標-客訴(不滿意+可歸責) 取得 最大值
			//     若 是否違規 = Y 或 是否有客訴 = Y，且前六個月平均月獎金×行為指標 > 1000，則扣減金額為前六個月平均月獎金×行為指標；若 前六個月平均月獎金×行為指標<=1000，則為1000
			//     若 是否違規或是否有客訴=N，則為0
			// 2022-08-01: 把不滿意(dissatisfiedRate)拿掉
			$scope.inputVO.deductionAmt = 0;
			$scope.inputVO.behavioralIndicators = ($scope.inputVO.violationRate > $scope.inputVO.custComplaintRate) ? $scope.inputVO.violationRate : $scope.inputVO.custComplaintRate;
			if ($scope.inputVO.isViolation == 'Y' || $scope.inputVO.isImputation == 'Y') {
				if ($scope.inputVO.avgBonus * ($scope.inputVO.behavioralIndicators / 100) > 1000) {
					$scope.inputVO.deductionAmt = $scope.inputVO.avgBonus * ($scope.inputVO.behavioralIndicators / 100);
				} else {
					$scope.inputVO.deductionAmt = 1000;
				}
			}
			
			// 5.當月個人獎金
			//   1.若收入 >= 領獎門檻，則該欄位顯示(收入 – 領獎門檻) × 獎金率 × 60% × 財務指標考核(得分) + (收入 – 領獎門檻) × 獎金率 × 40% – 行為指標考核(扣減金額)
			//   2.若條件皆不符合，則該欄位顯示0
			// 20220804:陳麗敏提出
			// 個人獎金欄位
			// 目前：判斷收入是否大於成本， Y→計算個人獎金； N→個人獎金欄位直接為0。
			// 調整：判斷收入是否大於成本， Y→計算個人獎金； N→個人獎金的計算改為「0-行為指標扣減金額」。

			$scope.inputVO.personalBonus = 0;
			if ($scope.inputVO.allIncome >= $scope.inputVO.basicThreshold) {
				$scope.inputVO.personalBonus = ($scope.inputVO.allIncome - $scope.inputVO.basicThreshold) * ($scope.inputVO.bonusRate / 100) * (60 / 100) * ($scope.inputVO.upperLimit / 100) + 
											   ($scope.inputVO.allIncome - $scope.inputVO.basicThreshold) * ($scope.inputVO.bonusRate / 100) * (40 / 100) -
											   $scope.inputVO.deductionAmt;
			} else {
				$scope.inputVO.personalBonus = $scope.inputVO.personalBonus - $scope.inputVO.deductionAmt;
			}

			// 6.當月生產力倍數：收入 ÷ (當月本薪 + 當月個人獎金)
			// 20220804:陳麗敏提出
			// 生產力倍數欄位
			// 目前：收入/(本薪+個人獎金)
			// 調整：收入/(本薪+個人獎金)，但個人獎金如果為負值，以0取代。

			$scope.inputVO.productivityMultiplier = 0;
			if ($scope.inputVO.personalBonus < 0) {
				$scope.inputVO.productivityMultiplier = $scope.inputVO.allIncome / (Math.round($scope.inputVO.monthlySalary));
			} else if (!($scope.inputVO.allIncome == 0 && (Math.round($scope.inputVO.monthlySalary) + $scope.inputVO.personalBonus == 0))) {
				$scope.inputVO.productivityMultiplier = $scope.inputVO.allIncome / (Math.round($scope.inputVO.monthlySalary) + $scope.inputVO.personalBonus);
			}
			// === final calculate end ===
		});
	};
	
	$scope.checkColumn = function (type, columnFlag) {
		switch(type) {
			case "SALES":
				if ($scope.inputVO.sales[columnFlag] == 0 || $scope.inputVO.sales[columnFlag] == '') {
					$scope.inputVO.sales[columnFlag] = '';
					$scope.showMsg("銷量至少需大於1以上，不可為0或負值");
				}
				break;
			case "MONTHLY_SALARY":
				if ($scope.inputVO.monthlySalary == 0 || $scope.inputVO.monthlySalary == '') {
					$scope.inputVO.monthlySalary = '';
					$scope.showMsg("當月本薪至少需大於1以上，不可為0或負值");
				}
				break;
			case "FINANCIAL_INDICATOR":
				angular.forEach($scope.FINANCIAL_INDICATOR_List, function(row, index, array){
					if (columnFlag == row.INDICATOR_TYPE) {
						switch (columnFlag) {
							case "存款AuM":
								if ($scope.inputVO.depositAUM > row.TARGET_MAX) {
									$scope.inputVO.depositAUM = row.TARGET_MAX;
									$scope.showMsg(columnFlag + "可為0至參數上限，目前上限值為：" + row.TARGET_MAX);
								}
								break;
							case "投資及保險AuM":
								if ($scope.inputVO.INV_INS_AUM > row.TARGET_MAX) {
									$scope.inputVO.INV_INS_AUM = row.TARGET_MAX;
									$scope.showMsg(columnFlag + "可為0至參數上限，目前上限值為：" + row.TARGET_MAX);
								}
								break;
							case "商品活躍度":
								if ($scope.inputVO.prodActivity > row.TARGET_MAX) {
									$scope.inputVO.prodActivity = row.TARGET_MAX;
									$scope.showMsg(columnFlag + "可為0至參數上限，目前上限值為：" + row.TARGET_MAX);
								}
								break;
							case "投保商品配置":
								if ($scope.inputVO.INS_prodConfiguration > row.TARGET_MAX) {
									$scope.inputVO.INS_prodConfiguration = row.TARGET_MAX;
									$scope.showMsg(columnFlag + "可為0至參數上限，目前上限值為：" + row.TARGET_MAX);
								}
								break;
						}
					}
				});
				break;
		}
	};
});
