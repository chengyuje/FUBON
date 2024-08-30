'use strict';
eSoafApp.controller('INS200_LoaderController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter){
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "INS200_LoaderController";
	
	//filter
	getParameter.XML(["COMMON.YES_NO","INS.HOSPITAL_TYPE","INS.WARD_TYPE"],function(tota){
		if(tota){
			$scope.mappingSet['COMMON.YES_NO'] = tota.data[tota.key.indexOf('COMMON.YES_NO')];
			$scope.mappingSet['INS.HOSPITAL_TYPE'] = tota.data[tota.key.indexOf('INS.HOSPITAL_TYPE')];	//醫院類型
			$scope.mappingSet['INS.WARD_TYPE'] = tota.data[tota.key.indexOf('INS.WARD_TYPE')];			//病房類型
		}
	});
	// 2018/2/13 我覺得初始化就能撈了阿, 不用等點了才撈
	$scope.sendRecv("INS200", "getLtcare", "com.systex.jbranch.app.server.fps.ins200.INS200InputVO", $scope.inputVO,
		function(totas, isError) {
        	if (!isError) {
        		debugger
        		$scope.CARE_WAY = totas[0].body.ltcareList;
        	};
		}
	);
	
	//預設壽險
	$scope.ACTIVE = 0;
	$scope.init = function() {
		$scope.page = 'assets/txn/INS210/INS210.html';
		$scope.temp_LIFE_EXPENSE = 0;
		// inputVO
		$scope.ltcareList = [];
		$scope.inputVO = {
			LIFE_EXPENSE:0,			//月生活準備金
			LIFE_EXPENSE_YEARS:0,	//年
			LIFE_EXPENSE_AMT:0,		//生活準備金加總
			LOAN_AMT:0,				//負債
			EDU_FEE:0,				//子女準備金
			PREPARE_AMT:0,			//已備金額
			CAREFEES:0,				//看護費
			SEARCH_WORK:0,			//尋找勝任工作時間(年)
			PROF_GRADE:'1',			//職業等級
			TTL_FLAG:'N'	,		//實支實付
			HOSPITAL_TYPE:'',		//醫院類型
			WARD_TYPE:''	,		//病房類型
			DISEASE:[],				//最擔心疾病參數
			DIS_DESCList:[],		//疾病警語
			TYPE_CANCER_INT:0,
			TYPE_CANCER:false,		//對應類型-癌症
			TYPE_MAJOR_INT:0,
			TYPE_MAJOR:false,		//對應類型-重大疾病
			TYPE_LT_INT:0,
			TYPE_LT:false,			//對應類型-長期看護
			CARE_WAY:undefined,		//照護方式
			NURSE_FEE_PAY: undefined,		//照護名稱KeyNO
			MAJOR_DISEASES_PAY: 0	//既有保障-重大疾病一次給付
		};
		$rootScope.TYPE_CANCER = false;
		$rootScope.TYPE_MAJOR = false;
		$rootScope.TYPE_LT = false;
		$rootScope.DemandAnalysisinputVO = $scope.inputVO;
		$rootScope.buttonIsClick = false;
		
		if($scope.connector('get', "loader_active")) {
			$scope.ACTIVE = $scope.connector('get', "loader_active");
			$scope.clicktab(($scope.ACTIVE + 1).toString());
			$scope.connector('set', "loader_active", undefined);
		}
		// 2018/3/13 test
		$rootScope.INS200_FROM_INS132 = false;
		if($scope.connector('get', "INS132_INPUT_VO")) {
			$rootScope.INS200_FROM_INS132 = true;
			var temp = $scope.connector('get', "INS132_INPUT_VO");
			debugger
			$scope.showFamilyGap = (temp.numInsFamilyGap > 10000 ? parseInt(temp.numInsFamilyGap / 10000, 10).toString() : (temp.numInsFamilyGap).toString()).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '萬元';
			$scope.showAccident = (temp.numInsItemAccident > 10000 ? parseInt(temp.numInsItemAccident / 10000, 10).toString() : (temp.numInsItemAccident).toString()).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '萬元';
			$scope.showHealth = (temp.numInsItemHealth).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '元';
			$rootScope.INS200_FamilyGap = temp.numInsFamilyGap;
			$rootScope.INS200_Accident = temp.numInsItemAccident;
			$rootScope.INS200_Health = temp.numInsItemHealth;
			$scope.connector('set', 'INS132_INPUT_VO', null);
		}
		
		//預設壽險頁面
		if($scope.ACTIVE == 0) {
			$scope.sendRecv("INS200","vaildParameter","com.systex.jbranch.app.server.fps.ins200.INS200InputVO",
					{PARA_TYPE:'1'},function(tota,isError){
						if(!isError){
							$scope.validparameter = tota[0].body.vaildparameter;
							$rootScope.PARA_NO = tota[0].body.para_no;
							$rootScope.CAL_DESC = tota[0].body.cal_desc;
							if($scope.validparameter){
								$rootScope.PLAN_TYPE = '1';
								$scope.page0 = $scope.page;
							}else{
								$scope.showErrorMsg('無參數設定')
							}
						}
			});
		};
	}
	
	$scope.clicktab = function(PLAN_TYPE) {
		$rootScope.buttonIsClick = false;
		$scope.sendRecv("INS200","vaildParameter","com.systex.jbranch.app.server.fps.ins200.INS200InputVO",
				{PARA_TYPE:PLAN_TYPE},function(tota,isError){
					if(!isError) {
						$scope.validparameter = tota[0].body.vaildparameter;
						$rootScope.PARA_NO = tota[0].body.para_no;
						$rootScope.CAL_DESC = tota[0].body.cal_desc;

						debugger
						// 說明:type3_para_no 用途為計算 3-醫療險或 4-重大疾病缺口用
						if(PLAN_TYPE == '3') {
							$scope.inputVO.TYPE3_PARA_NO = tota[0].body.para_no; // type 為 3 正常取得
						}
						
						if(PLAN_TYPE == '4') {
							$scope.inputVO.TYPE3_PARA_NO = tota[0].body.type3_para_no; // type 為 4 要額外取得
						}
						$rootScope.TYPE3_PARA_NO = $scope.inputVO.TYPE3_PARA_NO;
						if($scope.validparameter) {
							$rootScope.PLAN_TYPE = PLAN_TYPE;
							$scope.inputVO.PLAN_TYPE = PLAN_TYPE;
							switch (PLAN_TYPE) {
								case '1':
									$scope.page0 = $scope.page;
									break;
								case '2':
									$scope.page1 = $scope.page;
									break;
								case '3':
									$scope.page2 = $scope.page;
									break;
								case '4':
									if(!$scope.DISEASE_LIST)
										$scope.DISEASE_LIST = tota[0].body.diseaseList;
									// 2018/2/13 因為他TAG3會共用, 這裡只好重算
									if($scope.inputVO.TYPE_CANCER && $scope.inputVO.HOSPITAL_TYPE && $scope.inputVO.WARD_TYPE)
										$scope.getTempDisplayCount();
									$scope.page3 = $scope.page;
									break;
								default:
									break;
							}
//							$rootScope.checkGap();
						}else{
							$scope.showErrorMsg('無參數設定')
						}
					}
		});
	}
	
	//最擔心的疾病
	$scope.selectDisease = function(row) {
		if(row.CHOOSE=='Y'){
			if($scope.inputVO.DISEASE.length >= 3){
				row.CHOOSE = 'N'
				$scope.showErrorMsg('最多選擇三項');
			}else{
				$scope.inputVO.DISEASE.push(row.DATA);
				$scope.inputVO.DIS_DESCList.push(row.DIS_DESC);
				$scope.CSDT(row, true);
			}
		}else if(row.CHOOSE=='N'){
			$scope.CSDT(row, false);
			if($scope.inputVO.DISEASE.length > 0) {
				var delIndex = $scope.inputVO.DISEASE.indexOf(row.DATA);
				$scope.inputVO.DISEASE.splice(delIndex, 1);
				$scope.inputVO.DIS_DESCList.splice(delIndex, 1);
			}
		}
		// 2018/2/13 test
		// 因為他TAG3會共用, 這裡只好重算
		if($scope.inputVO.TYPE_CANCER && $scope.inputVO.HOSPITAL_TYPE && $scope.inputVO.WARD_TYPE)
			$scope.getTempDisplayCount();
		$scope.$broadcast("INS200_Controller.reLoadSuggestList", $scope.inputVO);
	}
	
	//選擇最擔心疾病後開顯示的區塊
	$scope.CSDT = function(row,status){
		if(row.TYPE_CANCER == 'Y') {
			if(status == true){
				$scope.inputVO.TYPE_CANCER_INT=$scope.inputVO.TYPE_CANCER_INT + 1;
				$scope.inputVO.TYPE_CANCER = true;
				$rootScope.TYPE_CANCER = true;
			} else if(status == false) {
				$scope.inputVO.TYPE_CANCER_INT=$scope.inputVO.TYPE_CANCER_INT - 1;
				if($scope.inputVO.TYPE_CANCER_INT == 0) {
					$scope.inputVO.TYPE_CANCER = false;
					$rootScope.TYPE_CANCER = false;
				}
			}
		}
		if(row.TYPE_MAJOR == 'Y'){
			if(status == true) {
				$scope.inputVO.TYPE_MAJOR_INT=$scope.inputVO.TYPE_MAJOR_INT + 1;
				$scope.inputVO.TYPE_MAJOR = true;
				$rootScope.TYPE_MAJOR = true;
			} else if(status == false) {
				$scope.inputVO.TYPE_MAJOR_INT=$scope.inputVO.TYPE_MAJOR_INT - 1;
				if($scope.inputVO.TYPE_MAJOR_INT == 0) {
					$scope.inputVO.TYPE_MAJOR = false;
					$rootScope.TYPE_MAJOR = false;
				}
			}
		}
		if(row.TYPE_LT == 'Y') {
			if(status == true) {
				$scope.inputVO.TYPE_LT_INT=$scope.inputVO.TYPE_LT_INT + 1;
				$scope.inputVO.TYPE_LT = true;
				$rootScope.TYPE_LT = true;
			} else if(status == false) {
				$scope.inputVO.TYPE_LT_INT=$scope.inputVO.TYPE_LT_INT - 1;
				if($scope.inputVO.TYPE_LT_INT == 0){
					$scope.inputVO.TYPE_LT = false;
					$rootScope.TYPE_LT = false;
				}
			}
		}
	}
	
	
	
	//家庭生活準備金計算
	$scope.life_expense_amt = function(){
		$scope.inputVO.LIFE_EXPENSE = $scope.moneyFormat($scope.inputVO.LIFE_EXPENSE);
		$scope.inputVO.LIFE_EXPENSE_YEARS = $scope.moneyFormat($scope.inputVO.LIFE_EXPENSE_YEARS);
		if($scope.inputVO.LIFE_EXPENSE != '' && $scope.inputVO.LIFE_EXPENSE_YEARS != ''){
			$scope.inputVO.LIFE_EXPENSE_AMT = ($scope.inputVO.LIFE_EXPENSE.replace(/\,/g,'')*12)*$scope.inputVO.LIFE_EXPENSE_YEARS.replace(/\,/g,'');
			if($rootScope.finishGapinputVO != undefined){
				if($scope.inputVO.LIFE_EXPENSE.replace(/\,/g,'') != $rootScope.finishGapinputVO.LIFE_EXPENSE.replace(/\,/g,'') || 
					$scope.inputVO.LIFE_EXPENSE_YEARS.replace(/\,/g,'') != $rootScope.finishGapinputVO.LIFE_EXPENSE_YEARS.replace(/\,/g,'')){
					$rootScope.PROJECT_GAP_ONCE1 = undefined;
					$rootScope.PROJECT_GAP_ONCE2 = undefined;
				}
			}
			
		}else{
			if($scope.inputVO.LIFE_EXPENSE == ''){
				$scope.inputVO.LIFE_EXPENSE = 0;
				$rootScope.PROJECT_GAP_ONCE1 = undefined;
				$rootScope.PROJECT_GAP_ONCE2 = undefined;
			}
			if($scope.inputVO.LIFE_EXPENSE_YEARS == ''){
				$scope.inputVO.LIFE_EXPENSE_YEARS = 0;
				$rootScope.PROJECT_GAP_ONCE1 = undefined;
				$rootScope.PROJECT_GAP_ONCE2 = undefined;
			}
			$scope.inputVO.LIFE_EXPENSE_AMT = 0;
		}
		$rootScope.DemandAnalysisinputVO = $scope.inputVO;
	}

	
	//數字加千分位
	$scope.add_Thousands = function(name){
		if(name != undefined && name != ''){
			switch (name) {
			case 'LOAN_AMT':
				if($scope.inputVO.LOAN_AMT != ''){
					if($rootScope.finishGapinputVO != undefined){
						if($scope.inputVO.LOAN_AMT != $rootScope.finishGapinputVO.LOAN_AMT){
							$rootScope.PROJECT_GAP_ONCE1 = undefined;
							$rootScope.PROJECT_GAP_ONCE2 = undefined;
						}
					}
					$scope.inputVO.LOAN_AMT = $scope.moneyFormat($scope.inputVO.LOAN_AMT);
				}else{
					$scope.inputVO.LOAN_AMT = 0;
					$rootScope.PROJECT_GAP_ONCE1 = undefined;
					$rootScope.PROJECT_GAP_ONCE2 = undefined;
				}
				$rootScope.DemandAnalysisinputVO = $scope.inputVO;
				break;
			case 'EDU_FEE':
				if($scope.inputVO.EDU_FEE != ''){
					if($rootScope.finishGapinputVO != undefined){
						if($scope.inputVO.EDU_FEE != $rootScope.finishGapinputVO.EDU_FEE){
							$rootScope.PROJECT_GAP_ONCE1 = undefined;
							$rootScope.PROJECT_GAP_ONCE2 = undefined;
						}
					}
					$scope.inputVO.EDU_FEE = $scope.moneyFormat($scope.inputVO.EDU_FEE);
				}else{
					$scope.inputVO.EDU_FEE = 0;
					$rootScope.PROJECT_GAP_ONCE1 = undefined;
					$rootScope.PROJECT_GAP_ONCE2 = undefined;
				}
				$rootScope.DemandAnalysisinputVO = $scope.inputVO;
				break;
			case 'PREPARE_AMT':
				if($scope.inputVO.PREPARE_AMT != ''){
					if($rootScope.finishGapinputVO != undefined){
						if($scope.inputVO.PREPARE_AMT != $rootScope.finishGapinputVO.PREPARE_AMT){
							$rootScope.PROJECT_GAP_ONCE1 = undefined;
							$rootScope.PROJECT_GAP_ONCE2 = undefined;
						}
					}
					$scope.inputVO.PREPARE_AMT = $scope.moneyFormat($scope.inputVO.PREPARE_AMT);
				}else{
					$scope.inputVO.PREPARE_AMT = 0;
					$rootScope.PROJECT_GAP_ONCE1 = undefined;
					$rootScope.PROJECT_GAP_ONCE2 = undefined;
				}
				$rootScope.DemandAnalysisinputVO = $scope.inputVO;
				break;
			case 'CAREFEES':
				if($scope.inputVO.CAREFEES != ''){
					if($rootScope.finishGapinputVO != undefined){
						if($scope.inputVO.CAREFEES != $rootScope.finishGapinputVO.CAREFEES){
							$rootScope.PROJECT_GAP_ONCE2 = undefined;
						}
					}
					$scope.inputVO.CAREFEES = $scope.moneyFormat($scope.inputVO.CAREFEES);
				}else{
					$scope.inputVO.CAREFEES = 0;
					$rootScope.PROJECT_GAP_ONCE2 = undefined;
				}
				$rootScope.DemandAnalysisinputVO = $scope.inputVO;
				break;
			case 'SEARCH_WORK':
				if($scope.inputVO.SEARCH_WORK != ''){
					if($rootScope.finishGapinputVO != undefined){
						if($scope.inputVO.SEARCH_WORK != $rootScope.finishGapinputVO.SEARCH_WORK){
							$rootScope.PROJECT_GAP_ONCE2 = undefined;
						}
					}
					$scope.inputVO.SEARCH_WORK = $scope.moneyFormat($scope.inputVO.SEARCH_WORK);
				}else{
					$scope.inputVO.SEARCH_WORK = 0;
					$rootScope.PROJECT_GAP_ONCE2 = undefined;
				}
				$rootScope.DemandAnalysisinputVO = $scope.inputVO;
				break;
			default:
				break;
			}
		}
		$rootScope.checkGap();
	}
	
	 $scope.$on('callBackTTL_FLAG', function (event, data) {
    	debugger
    	if($scope.ACTIVE != 2) return;
    	$scope.inputVO.TTL_FLAG = data.TTL_FLAG;
    });
	
// 職業等級 連動不用了
//	$scope.changeAllList = function() {
//		$scope.$broadcast('callReGetAllList', {grade:$scope.inputVO.PROF_GRADE,planType:$scope.inputVO.PLAN_TYPE});	
//		$rootScope.DemandAnalysisinputVO = $scope.inputVO;
//		$rootScope.checkGap();
//	}
	
	$scope.sendDA = function(){
		if(($scope.ACTIVE == 2 || $scope.ACTIVE == 3) && $rootScope.finishGapinputVO != undefined){
			if($scope.inputVO.TTL_FLAG != $rootScope.finishGapinputVO.TTL_FLAG || $scope.inputVO.HOSPITAL_TYPE != $rootScope.finishGapinputVO.HOSPITAL_TYPE
					|| $scope.inputVO.WARD_TYPE != $rootScope.finishGapinputVO.WARD_TYPE){
				// ins132 沒法選
				if(!$rootScope.INS200_FROM_INS132)
					$rootScope.PROJECT_GAP_ONCE3 = undefined;
				$rootScope.PROJECT_GAP_ONCE4_2 = undefined;
			}
		}
		$rootScope.DemandAnalysisinputVO = $scope.inputVO;
		$rootScope.checkGap();
	};
	// 2018/2/13
	$scope.getTempDisplayCount = function() {
		$scope.sendRecv("INS210", "getTempDisplayCount", "com.systex.jbranch.app.server.fps.ins210.INS210InputVO", $scope.inputVO,
			function(totas, isError) {
            	if (!isError) {
            		$scope.HOSPIHALDAYS = totas[0].body.HOSPIHALDAYS;
            		$scope.MONTH_AMT = totas[0].body.MONTH_AMT;
            	};
			}
		);
	};
	
	$scope.clearMajor = function() {
		if(!$scope.inputVO.MAJOR_DISEASES_PAY)
			$scope.inputVO.MAJOR_DISEASES_PAY = 0;
		if($rootScope.finishGapinputVO && $rootScope.finishGapinputVO.MAJOR_DISEASES_PAY != $scope.inputVO.MAJOR_DISEASES_PAY)
			$rootScope.PROJECT_GAP_ONCE4_1 = undefined;
		$rootScope.checkGap();
	};
	
	$scope.clearLtcare = function() {
		angular.forEach($scope.CARE_WAY, function(row) {
			row.NURSE_FEE_PAY = undefined;
		});
		$rootScope.PROJECT_GAP_ONCE4_3 = undefined;
		$rootScope.checkGap();
	};
	$scope.selectData = function(row) {
		$scope.inputVO.NURSE_FEE_PAY = parseFloat(row.NURSE_FEE_PAY);
		$rootScope.DemandAnalysisinputVO = $scope.inputVO;
		$scope.getTempDisplayCount();
		$rootScope.PROJECT_GAP_ONCE4_3 = undefined;
		$rootScope.checkGap();
	};
	
	$scope.init();
});