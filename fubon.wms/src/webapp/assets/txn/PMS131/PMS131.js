'use strict';
eSoafApp.controller('PMS131Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $compile, $q) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS131Controller";
	
	
	$scope.getPersonalBonusCal = function () {
		// 取得個人獎金率
		$scope.sendRecv("PMS131", "getPersonalBonusCal", "com.systex.jbranch.app.server.fps.pms131.PMS131InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				$scope.bonusRateList = tota[0].body.bonusRateList;
			}
		});
	}
	
	$scope.init = function() {
		$scope.inputVO = {
			dtccc : '', 
		};

		$scope.bonusRateList = [];
	
		$scope.clear();
		
		$scope.getPersonalBonusCal();
	};
	
	$scope.clear = function() {
		// DT 團隊整體 
		$scope.PDBBDT = 0; 		// 生產力基期(%)
		$scope.GCPOR_DT = 0; 	// DT獎金率(%)
		$scope.GCPORUP1_DT = 0; // DT獎金率加碼(%)
		$scope.GCPORUP2_DT = 0; // 無違規/客訴加碼(%)
		
		///
		$scope.ARBDT = 0;		// 總收益(元)
		$scope.PDNBDT = 0;		// 生產力
		$scope.GCBDT = 0;		// 團隊獎金
		$scope.TWPCNBDT = 0; 	// 總成本
		
		///
		$scope.GCBDT = 0;		// 團隊獎金

		$scope.paramList = [];
	}
	
	$scope.init();
	
	// 查詢-業務處回覆組別明細檔
	$scope.queryData = function(){
		
		if($scope.inputVO.dtccc == undefined || $scope.inputVO.dtccc == '' || $scope.inputVO.dtccc == null){
    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(DT 查詢碼)');
    		return;
    	}
		
		$scope.totals = {};
		$scope.outputVO = [];
		$scope.clear();
		
		$scope.inputVO.selectType = 'G';
		
		$scope.sendRecv("PMS132", "queryData", "com.systex.jbranch.app.server.fps.pms132.PMS132InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.originalList = angular.copy(tota[0].body.resultList);
				$scope.paramList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;

				// DT 團隊整體 - 生產力基期
				$scope.PDBBDT = tota[0].body.resultList[0].PDBB_DT;		 		// 生產力基期(%)
				$scope.GCPOR_DT = tota[0].body.resultList[0].GCPOR_DT 	 		// DT獎金率(%)
				$scope.GCPORUP1_DT = tota[0].body.resultList[0].GCPORUP1_DT; 	// DT獎金率加碼(%)
				$scope.GCPORUP2_DT = tota[0].body.resultList[0].GCPORUP2_DT; 	// 無違規/客訴加碼(%)

				angular.forEach($scope.paramList, function(row){
					row.TWPCNBP = row.WANBP + 0;
			    });
				
				return;
			}
		});
	};

	
	$scope.calculate = function (row) {
		
		if (row.ARBRM == '' || row.ARBRM == undefined || row.ARBRM == 0) {
			$scope.showMsg("當月收益(元)至少需大於1以上，不可為0或負值");
			
			return;
		}
		
		angular.forEach($scope.bonusRateList, function(porRow) {
			if (porRow.JOB_TITLE_NAME == row.JOB_TITLE_NAME && porRow.INCOME_MIN <= row.ARBRM && porRow.INCOME_MAX >= row.ARBRM) {
				row.POR = porRow.BONUS_RATE;
				
				row.QUAL = row.WANBP * row.MUL;
				
				// 個人 財務指標得分 金額 = (當月收益(元)(推估) – (成本1 × 倍數)) × 個人獎金率POR(%) × 60% × 財務指標得分 ÷ 100
				row.FS_AMT = (row.ARBRM - row.QUAL > 0) ? ((row.ARBRM - row.QUAL) * (row.POR / 100) * (60 / 100) * row.FS / 100) : 0;
				
				// 個人 行為指標扣減 金額 = (當月收益(元)(推估) – (成本1 × 倍數)) × 個人獎金率POR% × 40% - 行為指標扣減金額
				row.BP_AMT = (row.ARBRM - row.QUAL > 0) ? ((row.ARBRM - row.QUAL) * (row.POR / 100) * (40 / 100) - row.BP) : 0;
				
				// 成本2(PCNBP) = 財務指標得分 金額 + 行為指標扣減 金額
				row.PCNBP = row.FS_AMT + row.BP_AMT 
				
				// 個別組員成本 = 成本1 + 成本2
				row.TWPCNBP = row.WANBP + row.PCNBP; 
				
				// 生產力推估 = 當月收益(元)(推估) ÷ 個別組員成本
				row.PDNBP = row.ARBRM / row.TWPCNBP; 
				
				// 生產力是否大於基期(Y/N)
				row.PD_YN = (row.PDNBP > row.PDBBP ? 'Y' : 'N'); 
			}
	    });
	};
	
	$scope.calculateDT = function () {
		$scope.ARBDT = 0;		// 總收益(元)
		$scope.PDNBDT = 0;		// 生產力
		$scope.GCBDT = 0;		// 團隊獎金
		
		$scope.TWPCNBDT = 0; 	// 總成本
		
		// DT團隊整體
		angular.forEach($scope.paramList, function(row) {
			// 總收益(元)
			if (row.ARBRM != undefined && row.ARBRM != '') {
				$scope.ARBDT = $scope.ARBDT + parseFloat(row.ARBRM);
			}
			
			// 成本
			if (row.TWPCNBP != undefined && row.TWPCNBP != '') {
				$scope.TWPCNBDT += parseFloat(row.TWPCNBP);
			}
	    });
		
		// 生產力
		$scope.PDNBDT = $scope.ARBDT / $scope.TWPCNBDT;
		
		if ($scope.PDNBDT >= $scope.PDBBDT) {
			// (當月收益(元)(推估) - 總成本) * DT獎金率(%) * (1 + DT獎金率加碼) * 無違規/客訴加碼(%)
			$scope.GCBDT = ($scope.ARBDT - $scope.TWPCNBDT) * ($scope.GCPOR_DT / 100) * (1 + $scope.GCPORUP1_DT / 100) * ($scope.GCPORUP2_DT / 100);
		}
	};
	
	$scope.checkColumn = function (row, type) {
		switch(type) {
			case "FS":
				if (row.FS == 0 || row.FS > 100 || row.FS == '') {
					angular.forEach($scope.originalList, function (originalRow, originalIndex, originalObjs) {
						if (row.EMP_ID == originalRow.EMP_ID) {
							row.FS = originalRow.FS;
						}
					});
					$scope.showMsg("財務指標分數需大於1~100，不可為0或負值");
				}
				break;
			case "ARBRM":
				if (row.ARBRM == 0 || row.ARBRM == '') {
					angular.forEach($scope.originalList, function (originalRow, originalIndex, originalObjs) {
						if (row.EMP_ID == originalRow.EMP_ID) {
							row.ARBRM = undefined;
							row.PD_YN = undefined;
						}
					});
					$scope.showMsg("當月收益(元)至少需大於1以上，不可為0或負值");
				}
				break;
		}
	};
});
