'use strict'; 
eSoafApp.controller('INS210Controller',
		function($rootScope, $scope, $controller, $confirm, socketService, projInfoService, sysInfoService, ngDialog, $filter, $q, getParameter){
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS210Controller";
		
		getParameter.XML(["INS.STATUS", "INS.UNIT", "INS.PARA_NO4_SUGGEST_TYPE"],function(tota){
			if(tota){
				$scope.TEMP_INS_STATUS = tota.data[tota.key.indexOf('INS.STATUS')];
				angular.forEach($scope.TEMP_INS_STATUS, function(row) {
					if(row.LABEL == '規劃中')
						$scope.INS_STATUS_1 = row.DATA;
					else if(row.LABEL == '已完成規劃')
						$scope.INS_STATUS_2 = row.DATA;
				});
				$scope.INS_UNIT = tota.data[tota.key.indexOf('INS.UNIT')];
				$scope.PARA_NO4_SUGGEST_TYPE = tota.data[tota.key.indexOf('INS.PARA_NO4_SUGGEST_TYPE')];
			}
		});
		
		$scope.callBackParentTTL_FLAG = function(isCheck) {
			$scope.$emit('callBackTTL_FLAG', {TTL_FLAG:isCheck});	
		}
		
// 職業等級 連動不用了		
		$scope.reGetAllList = function() {
			debugger;
			$scope.sendRecv("INS210","inquire","com.systex.jbranch.app.server.fps.ins210.INS210InputVO",
					$scope.inputVO,function(tota,isError){
						if(!isError){
							$scope.inputVO.planList = tota[0].body.planList;
							$scope.inputVO.suggestList = tota[0].body.suggestList;
							$scope.temp_suggestList = angular.copy($scope.inputVO.suggestList);
							
							if($scope.inputVO.planList && $scope.inputVO.PLAN_TYPE == '3') {
								angular.forEach($scope.inputVO.planList, function(row) {
									if(row.IS_HR == 'Y') {
										$scope.callBackParentTTL_FLAG('Y');
										return;
									}
								});
							}
							
//							$scope.connector('set','INS200_CUST_ID',undefined);
							//客戶年齡不為空
							if($scope.inputVO.AGE){
								//因年齡限制導致沒有推薦商品，顯示警告訊息
								if($scope.inputVO.suggestList.length == 0){
									$scope.showWarningMsg('因保險年齡限制，無可投保之保險商品');
								}
							}
						}	
			});
		}
		
//	    $scope.$on('callReGetAllList', function (event, data) {
//	    	debugger
//	    	if('2' != $scope.inputVO.PLAN_TYPE) return;
//	    	console.log(event);
//	    	$scope.inputVO.PROF_GRADE = data.grade;
//	    	$scope.inputVO.PLAN_TYPE = data.planType
//	    	$scope.reGetAllList();
//	    });
		
		$scope.init = function() {
			debugger
			$rootScope.buttonIsClick = false;
			$scope.save_finish = true;
			$scope.inputVO = {
				LIFE_EXPENSE:0,
				LIFE_EXPENSE_YEARS:0,
				LIFE_EXPENSE_AMT:0,
				LOAN_AMT:0,
				EDU_FEE:0,
				PREPARE_AMT:0,
				CAREFEES:0,
				SEARCH_WORK:0,
				PROF_GRADE:'',
				EXTRA_PROTEXT:0,
				PROTECT_GAP1:undefined,		//缺口
				PROTECT_GAP2:undefined,		//住院日額缺口
				PROTECT_GAP3:undefined,		//看護每月缺口
				planList:[],
				PRINT_YN:'N',
				PLAN_TYPE:$rootScope.PLAN_TYPE,		//保險規劃類型  1:壽險規劃	2: 意外險規劃  3: 醫療險規劃   4: 重大疾病(癌症、長期看護)規劃
				PARA_NO: $rootScope.PARA_NO,
				TYPE3_PARA_NO: $rootScope.TYPE3_PARA_NO,
				STATUS:undefined,	//狀態  沒值: 未規劃  1: 規劃中, 2: 已規劃
				PLAN_KEYNO:undefined,
				PLAN_D_KEYNO:undefined,
				TYPE_CANCER:false,		//對應類型-癌症
				TYPE_MAJOR:false,		//對應類型-重大疾病
				TYPE_LT:false,			//對應類型-長期看護
				CARE_WAY:undefined,
				NURSE_FEE_PAY: undefined,		//長期照護keyNO
				MAJOR_DISEASES_PAY: undefined,
				INS200_FROM_INS132: $rootScope.INS200_FROM_INS132
			};
			if($scope.connector('get','INS200_CUST_ID')) {
				$scope.inputVO.CUST_ID = $scope.connector('get','INS200_CUST_ID');
				var temp = $scope.connector('get','INS200_CUST_DATA');
				$scope.inputVO.AGE = temp.AGE;
				$scope.inputVO.CUST_NAME = temp.CUST_NAME;
				console.log($scope.inputVO);
				debugger;
				$scope.reGetAllList();
			}
			
		}
		$scope.init();

		//目前保障內容刪除
		$scope.delete_data = function(row){
			$scope.delete_inputVO = {
					planList:$scope.inputVO.planList,
					select_data:row
			}
			$scope.sendRecv("INS210","delete_temp","com.systex.jbranch.app.server.fps.ins210.INS210InputVO",
					$scope.delete_inputVO,function(tota,isError){
						if(!isError){
							$scope.inputVO.planList = tota[0].body.planList;
							
							switch ($scope.inputVO.PLAN_TYPE) {
							case '1':
								if($rootScope.PROJECT_GAP_ONCE1 != undefined){
									$rootScope.PROJECT_GAP_ONCE1 = undefined;
								}
								break;
							case '2':
								if($rootScope.PROJECT_GAP_ONCE2 != undefined){
									$rootScope.PROJECT_GAP_ONCE2 = undefined;
								}
								break;
							case '3':
								if($rootScope.PROJECT_GAP_ONCE3 != undefined){
									$rootScope.PROJECT_GAP_ONCE3 = undefined;
								}
								break;
							case '4':
								// 2018/2/13 test
								if($rootScope.PROJECT_GAP_ONCE4_1)
									$rootScope.PROJECT_GAP_ONCE4_1 = undefined;
								if($rootScope.PROJECT_GAP_ONCE4_2)
									$rootScope.PROJECT_GAP_ONCE4_2 = undefined;
								if($rootScope.PROJECT_GAP_ONCE4_3)
									$rootScope.PROJECT_GAP_ONCE4_3 = undefined;
								break;
							default:
								break;
							}
							$scope.reLoadSuggestList().then(function(data) {
								$scope.type_4_filter_suggestList();
							});
						}	
			});
			
		}

		//需求分析放入inputVO對應欄位
		$scope.DAPutInputVO = function(){
//			console.log($rootScope.DemandAnalysisinputVO);
			$scope.inputVO.AGE = $rootScope.DemandAnalysisinputVO.AGE;
			$scope.inputVO.LIFE_EXPENSE = $rootScope.DemandAnalysisinputVO.LIFE_EXPENSE;
			$scope.inputVO.LIFE_EXPENSE_YEARS = $rootScope.DemandAnalysisinputVO.LIFE_EXPENSE_YEARS;
			$scope.inputVO.LIFE_EXPENSE_AMT = $rootScope.DemandAnalysisinputVO.LIFE_EXPENSE_AMT;
			$scope.inputVO.LOAN_AMT = $rootScope.DemandAnalysisinputVO.LOAN_AMT;
			$scope.inputVO.EDU_FEE = $rootScope.DemandAnalysisinputVO.EDU_FEE;
			$scope.inputVO.PREPARE_AMT = $rootScope.DemandAnalysisinputVO.PREPARE_AMT;
			$scope.inputVO.CAREFEES = $rootScope.DemandAnalysisinputVO.CAREFEES;
			$scope.inputVO.SEARCH_WORK = $rootScope.DemandAnalysisinputVO.SEARCH_WORK;
			$scope.inputVO.PROF_GRADE = $rootScope.DemandAnalysisinputVO.PROF_GRADE;
			$scope.inputVO.TTL_FLAG = $rootScope.DemandAnalysisinputVO.TTL_FLAG;
			$scope.inputVO.HOSPITAL_TYPE = $rootScope.DemandAnalysisinputVO.HOSPITAL_TYPE;
			$scope.inputVO.WARD_TYPE = $rootScope.DemandAnalysisinputVO.WARD_TYPE;
			$scope.inputVO.TYPE_CANCER = $rootScope.DemandAnalysisinputVO.TYPE_CANCER;//對應類型-癌症
			$scope.inputVO.TYPE_MAJOR = $rootScope.DemandAnalysisinputVO.TYPE_MAJOR;//對應類型-重大疾病
			$scope.inputVO.TYPE_LT = $rootScope.DemandAnalysisinputVO.TYPE_LT;//對應類型-長期看護
			$scope.inputVO.CARE_WAY = $rootScope.DemandAnalysisinputVO.CARE_WAY;//長期看護方式
			$scope.inputVO.MAJOR_DISEASES_PAY = $rootScope.DemandAnalysisinputVO.MAJOR_DISEASES_PAY;//重大疾病一次給付
			$scope.inputVO.NURSE_FEE_PAY = $rootScope.DemandAnalysisinputVO.NURSE_FEE_PAY;//長期看護KeyNO
			
		};
		
		// 2018/2/13 test
		$scope.type_4_filter_suggestList = function() {
			if($scope.inputVO.PLAN_TYPE == '4') {
				$scope.inputVO.suggestList = angular.copy($scope.temp_suggestList);
				// $scope.PARA_NO4_SUGGEST_TYPE 4, A, B
				var temp = [];
				if($scope.inputVO.TYPE_CANCER) temp.push('A');
				if($scope.inputVO.TYPE_MAJOR) temp.push('4');
				if($scope.inputVO.TYPE_LT) temp.push('B');
				if(temp.length > 0) {
					$scope.inputVO.suggestList = _.filter($scope.inputVO.suggestList, function(o) {
						return (temp.indexOf(o.SUGGEST_TYPE) > -1);
					});
				}
			}
		};
		$scope.$on("INS200_Controller.reLoadSuggestList", function(event, row) {
			$scope.save_finish = true;
			$scope.inputVO.TYPE_CANCER = row.TYPE_CANCER;
			$scope.inputVO.TYPE_MAJOR = row.TYPE_MAJOR;
			$scope.inputVO.TYPE_LT = row.TYPE_LT;
			$scope.inputVO.DISEASE = row.DISEASE;
			$scope.type_4_filter_suggestList();
        });
		
		//計算缺口
		$scope.calculaGap = function() {
			$scope.DAPutInputVO();
			// 2018/2/13 test
			if($scope.inputVO.PLAN_TYPE == '4' && (($scope.inputVO.TYPE_CANCER && (!$scope.inputVO.HOSPITAL_TYPE || !$scope.inputVO.WARD_TYPE)) ||
					($scope.inputVO.TYPE_MAJOR && !$scope.inputVO.MAJOR_DISEASES_PAY) ||
					($scope.inputVO.TYPE_LT && !$scope.inputVO.NURSE_FEE_PAY) ||
					!$scope.inputVO.DISEASE || $scope.inputVO.DISEASE.length == 0)) {
				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
			}
			$rootScope.buttonIsClick = true;
			$scope.inputVO.EXTRA_PROTEXT = $scope.inputVO.EXTRA_PROTEXT || 0;
			// 2018/3/13 sa:只有1,2,3, plan_list+的也是coverage
			if($scope.inputVO.PLAN_TYPE != '4' && $scope.inputVO.INS200_FROM_INS132) {
				// 直接用ins132的金額照舊CODE算法算 壽險
				if($scope.inputVO.PLAN_TYPE == '1') {
					$scope.inputVO.SHD_PROTECT1 = $rootScope.INS200_FamilyGap;
					$scope.inputVO.NOW_PROTECT1 = parseFloat($scope.inputVO.EXTRA_PROTEXT);
					angular.forEach($scope.inputVO.planList, function(row) {
						$scope.inputVO.NOW_PROTECT1 += parseFloat(row.coverage) || 0;
					});
					$scope.inputVO.PROTECT_GAP1 = $scope.inputVO.SHD_PROTECT1 - $scope.inputVO.NOW_PROTECT1;
					$scope.inputVO.PROTECT_GAP1 = $scope.inputVO.PROTECT_GAP1 < 0 ? 0 : $scope.inputVO.PROTECT_GAP1;
					$rootScope.PROJECT_GAP_ONCE1 = angular.copy($scope.inputVO.PROTECT_GAP1);
					if($rootScope.PROJECT_GAP_ONCE1!= undefined){
						$scope.showMsg('壽險缺口已計算，修改需求分析會清除壽險及意外險缺口，需重新點選確定計算缺口')
					}
				}
				// 意外險
				else if($scope.inputVO.PLAN_TYPE == '2') {
					$scope.inputVO.SHD_PROTECT1 = $rootScope.INS200_Accident;
					$scope.inputVO.NOW_PROTECT1 = parseFloat($scope.inputVO.EXTRA_PROTEXT);
					angular.forEach($scope.inputVO.planList, function(row) {
						$scope.inputVO.NOW_PROTECT1 += parseFloat(row.coverage) || 0;
					});
					$scope.inputVO.PROTECT_GAP1 = $scope.inputVO.SHD_PROTECT1 - $scope.inputVO.NOW_PROTECT1;
					$scope.inputVO.PROTECT_GAP1 = $scope.inputVO.PROTECT_GAP1 < 0 ? 0 : $scope.inputVO.PROTECT_GAP1;
					$rootScope.PROJECT_GAP_ONCE2 = angular.copy($scope.inputVO.PROTECT_GAP1);
					if($rootScope.PROJECT_GAP_ONCE2!= undefined){
						$scope.showMsg('意外險缺口已計算，修改需求分析會清除壽險及意外險缺口，需重新點選確定計算缺口')
					}
				}
				// 醫療險
				else {
					$scope.inputVO.SHD_PROTECT2 = $rootScope.INS200_Health;
					$scope.inputVO.NOW_PROTECT2 = parseFloat($scope.inputVO.EXTRA_PROTEXT);
					angular.forEach($scope.inputVO.planList, function(row) {
						$scope.inputVO.NOW_PROTECT2 += parseFloat(row.coverage) || 0;
					});
					$scope.inputVO.PROTECT_GAP2 = $scope.inputVO.SHD_PROTECT2 - $scope.inputVO.NOW_PROTECT2;
					$scope.inputVO.PROTECT_GAP2 = $scope.inputVO.PROTECT_GAP2 < 0 ? 0 : $scope.inputVO.PROTECT_GAP2;
					$rootScope.PROJECT_GAP_ONCE3 = angular.copy($scope.inputVO.PROTECT_GAP2);
				}
				
				$scope.save_finish = false;
			} else {
				// old code 算法
				$scope.sendRecv("INS210","calculaGap","com.systex.jbranch.app.server.fps.ins210.INS210InputVO",
						$scope.inputVO,function(tota,isError){
							if(!isError) {
								$rootScope.finishGapinputVO = $scope.inputVO;
								switch ($scope.inputVO.PLAN_TYPE) {
									case '1':
										$scope.inputVO.SHD_PROTECT1 = tota[0].body.SHD_PROTECT1;//應備總金額
										$scope.inputVO.NOW_PROTECT1 = tota[0].body.NOW_PROTECT1;//已備總金額
										
										$rootScope.PROJECT_GAP_ONCE1 = tota[0].body.PROJECT_GAP_ONCE1;//缺口
										$scope.inputVO.PROTECT_GAP1 = tota[0].body.PROJECT_GAP_ONCE1;
										if($rootScope.PROJECT_GAP_ONCE1!= undefined){
											$scope.showMsg('壽險缺口已計算，修改需求分析會清除壽險及意外險缺口，需重新點選確定計算缺口')
										}										
										if($rootScope.PROJECT_GAP_ONCE1 != $scope.temp_PROJECT_GAP_ONCE){
											$scope.reLoadSuggestList();
										}
										$scope.temp_PROJECT_GAP_ONCE = angular.copy($rootScope.PROJECT_GAP_ONCE1);
										break;
									case '2':
										$scope.inputVO.SHD_PROTECT1 = tota[0].body.SHD_PROTECT1;//應備總金額
										$scope.inputVO.NOW_PROTECT1 = tota[0].body.NOW_PROTECT1;//已備總金額
										
										$rootScope.PROJECT_GAP_ONCE2 = tota[0].body.PROJECT_GAP_ONCE2;
										$scope.inputVO.PROTECT_GAP1 = tota[0].body.PROJECT_GAP_ONCE2;
										if($rootScope.PROJECT_GAP_ONCE2!= undefined){
											$scope.showMsg('意外險缺口已計算，修改需求分析會清除壽險及意外險缺口，需重新點選確定計算缺口')
										}
										if($rootScope.PROJECT_GAP_ONCE2 != $scope.temp_PROJECT_GAP_ONCE){
											$scope.reLoadSuggestList();
										}
										$scope.temp_PROJECT_GAP_ONCE = angular.copy($rootScope.PROJECT_GAP_ONCE2);
										break;
									case '3':
										$scope.inputVO.SHD_PROTECT2 = tota[0].body.SHD_PROTECT2;//應備癌症住院日額
										$scope.inputVO.NOW_PROTECT2 = tota[0].body.NOW_PROTECT2;
										
										$rootScope.PROJECT_GAP_ONCE3 = tota[0].body.PROJECT_GAP_ONCE3;
										$scope.inputVO.PROTECT_GAP2 = tota[0].body.PROJECT_GAP_ONCE3;
										$scope.inputVO.SICKROOM_FEE = tota[0].body.SICKROOM_FEE;
										if($rootScope.PROJECT_GAP_ONCE3 != $scope.temp_PROJECT_GAP_ONCE){
											$scope.reLoadSuggestList();
										}
										$scope.temp_PROJECT_GAP_ONCE = angular.copy($rootScope.PROJECT_GAP_ONCE3);
										break;
									case '4':
										// 2018/2/13 test
										// 重大
										if($scope.inputVO.TYPE_MAJOR) {
											$scope.inputVO.SHD_PROTECT1 = parseFloat($scope.inputVO.MAJOR_DISEASES_PAY) * 10000;
											$scope.inputVO.NOW_PROTECT1 = tota[0].body.NOW_PROTECT1;
											$scope.inputVO.PROTECT_GAP1 = $scope.inputVO.SHD_PROTECT1 - $scope.inputVO.NOW_PROTECT1;
											$scope.inputVO.PROTECT_GAP1 = $scope.inputVO.PROTECT_GAP1 < 0 ? 0 : $scope.inputVO.PROTECT_GAP1;
										} else {
											$scope.inputVO.SHD_PROTECT1 = null;
											$scope.inputVO.NOW_PROTECT1 = null;
											$scope.inputVO.PROTECT_GAP1 = null;
										}
										// 癌症
										if($scope.inputVO.TYPE_CANCER) {
											$scope.inputVO.SICKROOM_FEE = tota[0].body.SICKROOM_FEE;
											$scope.inputVO.SHD_PROTECT2 = tota[0].body.HOSPIHALDAYS;
											$scope.inputVO.NOW_PROTECT2 = tota[0].body.NOW_PROTECT2;
											$scope.inputVO.PROTECT_GAP2 = $scope.inputVO.SHD_PROTECT2 - $scope.inputVO.NOW_PROTECT2;
											$scope.inputVO.PROTECT_GAP2 = $scope.inputVO.PROTECT_GAP2 < 0 ? 0 : $scope.inputVO.PROTECT_GAP2;
										} else {
											$scope.inputVO.SICKROOM_FEE = null;
											$scope.inputVO.SHD_PROTECT2 = null;
											$scope.inputVO.NOW_PROTECT2 = null;
											$scope.inputVO.PROTECT_GAP2 = null;
										}
										// 長期
										if($scope.inputVO.TYPE_LT) {
											$scope.inputVO.SHD_PROTECT3 = tota[0].body.MONTH_AMT;
											$scope.inputVO.NOW_PROTECT3 = tota[0].body.NOW_PROTECT3;
											$scope.inputVO.PROTECT_GAP3 = $scope.inputVO.SHD_PROTECT3 - $scope.inputVO.NOW_PROTECT3;
											$scope.inputVO.PROTECT_GAP3 = $scope.inputVO.PROTECT_GAP3 < 0 ? 0 : $scope.inputVO.PROTECT_GAP3;
										} else {
											$scope.inputVO.NURSE_FEE_PAY = null;
											$scope.inputVO.SHD_PROTECT3 = null;
											$scope.inputVO.NOW_PROTECT3 = null;
											$scope.inputVO.PROTECT_GAP3 = null;
										}
										$rootScope.PROJECT_GAP_ONCE4_1 = $scope.inputVO.PROTECT_GAP1;
										$rootScope.PROJECT_GAP_ONCE4_2 = $scope.inputVO.PROTECT_GAP2;
										$rootScope.PROJECT_GAP_ONCE4_3 = $scope.inputVO.PROTECT_GAP3;
										if($rootScope.PROJECT_GAP_ONCE4_1 != $scope.temp_PROJECT_GAP_ONCE4_1
												|| $rootScope.PROJECT_GAP_ONCE4_2 != $scope.temp_PROJECT_GAP_ONCE4_2
												||$rootScope.PROJECT_GAP_ONCE4_3 != $scope.temp_PROJECT_GAP_ONCE4_3) {
											$scope.reLoadSuggestList().then(function(data) {
												$scope.type_4_filter_suggestList();
											});
										}
										$scope.temp_PROJECT_GAP_ONCE4_1 = angular.copy($rootScope.PROJECT_GAP_ONCE4_1);
										$scope.temp_PROJECT_GAP_ONCE4_2 = angular.copy($rootScope.PROJECT_GAP_ONCE4_2);
										$scope.temp_PROJECT_GAP_ONCE4_3 = angular.copy($rootScope.PROJECT_GAP_ONCE4_3);
										break;
									default:
										break;
								}
								
								$scope.temp_inputVO = angular.copy($scope.inputVO);
								$scope.save_finish = false;
							}	
				});
			}
		}
		
		$scope.reLoadSuggestList = function() {
			var deferred = $q.defer();
			console.log($scope.inputVO);
			var temp = $scope.connector('get','INS200_CUST_DATA');
			$scope.sendRecv("INS810","getSuggestPrd","com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO", {paraType:$scope.inputVO.PLAN_TYPE, insAge:temp.AGE},
				function(tota, isError) {
					if (!isError) {
						$scope.inputVO.suggestList = tota[0].body.suggestPrdList;
						$scope.temp_suggestList = angular.copy($scope.inputVO.suggestList);
						deferred.resolve();
					}
			});
			return deferred.promise;
		};
		
		$scope.Caculate_toBack = function(row) {
			console.log(row)
			if(!row.INSPRD_ANNUAL_choic)
				return;
			var temp = $scope.connector('get','INS200_CUST_DATA');
			var policySuggestInputVO = {
				insPrdId:row.PRD_ID,
				annual:row.INSPRD_ANNUAL_choic,
				currCD: row.CURR_CD,
				age: temp.AGE,
				gender: temp.GENDER
			};
			console.log(policySuggestInputVO);
			$scope.sendRecv("INS810","getPremAndExchangeRate","com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO", policySuggestInputVO,
				function(totas, isError) {
                	if (!isError) {
                		var premRate = totas[0].body.premRate;
                		var index = row.insprd_annualList.map(function(e) { return e.DATA; }).indexOf(row.INSPRD_ANNUAL_choic);
            			var RowData = row.insprd_annualList[index];
            			row.KEY_NO = RowData.KEY_NO;
            			// Insured
            			var unit = 1;
            			if(row.PRD_UNIT == '2')
            				unit = 100;
            			else if(row.PRD_UNIT == '3')
            				unit = 1000;
            			else if(row.PRD_UNIT == '4')
            				unit = 10000;
            			if($scope.inputVO.PLAN_TYPE == '1' || $scope.inputVO.PLAN_TYPE == '2')
            				row.Insured = row.Insured || $scope.inputVO.PROTECT_GAP1 / unit / parseFloat(RowData.CVRG_RATIO);
            			else if($scope.inputVO.PLAN_TYPE == '3')
            				row.Insured = row.Insured || $scope.inputVO.PROTECT_GAP2 / unit / parseFloat(RowData.CVRG_RATIO);
            			else {
            				// 2018/2/13 $scope.PARA_NO4_SUGGEST_TYPE 4, A, B
        					// 重大
            				if(row.SUGGEST_TYPE == '4')
            					row.Insured = row.Insured || $scope.inputVO.PROTECT_GAP1 / unit / parseFloat(RowData.CVRG_RATIO);
            				// 癌症
            				else if(row.SUGGEST_TYPE == 'A')
            					row.Insured = row.Insured || $scope.inputVO.PROTECT_GAP2 / unit / parseFloat(RowData.CVRG_RATIO);
            				// 長期
            				else if(row.SUGGEST_TYPE == 'B')
            					row.Insured = row.Insured || $scope.inputVO.PROTECT_GAP3 / unit / parseFloat(RowData.CVRG_RATIO);
            			}
            			// POLICY_AMT_DISTANCE
            			RowData.POLICY_AMT_MIN = parseFloat(RowData.POLICY_AMT_MIN);
            			RowData.POLICY_AMT_MAX = parseFloat(RowData.POLICY_AMT_MAX);
            			RowData.POLICY_AMT_DISTANCE = parseFloat(RowData.POLICY_AMT_DISTANCE);
            			if(row.Insured < RowData.POLICY_AMT_MIN)
            				row.Insured = RowData.POLICY_AMT_MIN;
            			else if(row.Insured > RowData.POLICY_AMT_MAX)
            				row.Insured = RowData.POLICY_AMT_MAX;
            			else
            				row.Insured = RowData.POLICY_AMT_MIN + (Math.ceil((row.Insured - RowData.POLICY_AMT_MIN) / RowData.POLICY_AMT_DISTANCE) * RowData.POLICY_AMT_DISTANCE);
            			// CURR
            			var refmap = totas[0].body.refExcRateMap;
        				row.Insured = row.Insured * (refmap[row.CURR_CD] || 1);
            			row.Premium = row.Insured * premRate;
                	};
				}
    		);
		};
		
		//選擇推薦商品
		$scope.choic_Suggest = function(row) {
			if(row.choic == 'Y'){
				if(row.insprd_annualList.length == 2){
					row.INSPRD_ANNUAL_choic = row.insprd_annualList[1].LABEL;
					$scope.Caculate_toBack(row);
				}else if(row.INSPRD_ANNUAL_choic != undefined && row.INSPRD_ANNUAL_choic != ''){
					$scope.Caculate_toBack(row);
				}
			}else{
				row.INSPRD_ANNUAL_choic = '';
				row.Insured = undefined;
				row.Premium = undefined;
			}
		};
		
		//儲存
		$scope.save = function(status) {
			
			$scope.inputVO.STATUS = status;
			// 2018/3/2 如果舊CODE定義INS210Controller, 這四個TAG就是獨立的東西
			// TBINS_PLAN_MAIN 是共用, TBINS_PLAN_DTL獨立沒錯
			if($scope.connector('get','INS210_PLAN_KEYNO'))
				$scope.inputVO.PLAN_KEYNO = $scope.connector('get','INS210_PLAN_KEYNO');
			//確認商品推薦有無勾選
			$scope.sendRecv("INS210","check_Insured","com.systex.jbranch.app.server.fps.ins210.INS210InputVO",
					$scope.inputVO,function(tota,isError){
						if(!isError){
							if(tota[0].body.sendInsuredList.length>0){
								$rootScope.buttonIsClick = false;
								$scope.inputVO.sendInsuredList = tota[0].body.sendInsuredList;
								$scope.sendRecv("INS210","Save","com.systex.jbranch.app.server.fps.ins210.INS210InputVO",
										$scope.inputVO,function(tota,isError){
											if(!isError){
												if(tota.length > 0) {
													// 2018/3/2 如果舊CODE定義INS210Controller, 這四個TAG就是獨立的東西
													// TBINS_PLAN_MAIN 是共用, TBINS_PLAN_DTL獨立沒錯
													$scope.connector('set','INS210_PLAN_KEYNO', tota[0].body.PLAN_KEYNO);
													$scope.inputVO.PLAN_KEYNO = tota[0].body.PLAN_KEYNO;
													$scope.inputVO.PLAN_D_KEYNO = tota[0].body.PLAN_D_KEYNO;
													//點選投保時轉跳IOT110
													if($scope.inputVO.STATUS == $scope.INS_STATUS_2){
														debugger;
														var dataList = $scope.inputVO.sendInsuredList;
														var CUST_NAME = $scope.inputVO.CUST_NAME;
														var dialog = ngDialog.open({
															template: 'assets/txn/INS/INS_IOT.html',
															className: 'INS_SOT',
															controller: ['$scope', function ($scope) {
														          $scope.FROM210_CUST_ID = dataList[0].CUST_ID;
														          $scope.FROM210_CUST_NAME = CUST_NAME;
																  $scope.FROM210_PRD_ID = dataList[0].PRD_ID;
																  $scope.FROM210_PREMIUM = dataList[0].Premium;
																  $scope.from210 = true;
														        }]
														});
														dialog.closePromise.then(function(data){
															// 關掉就關掉拉
														});
														
//														$scope.connector('set','INS210_recommend',);
//														$rootScope.menuItemInfo.url = "assets/txn/IOT110/IOT110.html";
													}
											
						        					$scope.showSuccessMsg('ehl_01_common_025');//新增成功
						        				}
											}	
								});
							
							}else{
								$scope.showErrorMsg("未選取推薦商品或繳費年期")
							}
						}	
			});
			
		}
		
		//我要投保
		$scope.insured = function(){
			//證照檢核
			$scope.sendRecv("IOT110", "checkLicenses", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", {empId : sysInfoService.getUserID()},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_iot110_004");                			
							} else {
								$scope.save($scope.INS_STATUS_2);	//'P'已完成規劃
							}
						}
			});

		}
		//控制商品推薦disabled
		$rootScope.checkGap = function(){
			switch ($rootScope.PLAN_TYPE) {
			case '1':
				if($rootScope.PROJECT_GAP_ONCE1 == undefined){
					$scope.save_finish = true;
				}else{
					$scope.save_finish = false;
				}
				break;
			case '2':
				if($rootScope.PROJECT_GAP_ONCE2 == undefined){
					$scope.save_finish = true;
				}else{
					$scope.save_finish = false;
				}
				break;
			case '3':
				if($rootScope.PROJECT_GAP_ONCE3 == undefined){
					$scope.save_finish = true;
				}else{
					$scope.save_finish = false;
				}
				break;
			case '4':
				// 2018/2/13 test
				if(($rootScope.TYPE_MAJOR && !$rootScope.PROJECT_GAP_ONCE4_1) || 
						($rootScope.TYPE_CANCER && !$rootScope.PROJECT_GAP_ONCE4_2) || 
						($rootScope.TYPE_LT && !$rootScope.PROJECT_GAP_ONCE4_3))
					$scope.save_finish = true;
				else
					$scope.save_finish = false;
				break;
			default:
				break;
			}
		}
		
		//跳至輔消文件
		$scope.goDownload = function(row) {
			var id = row.PRD_ID;
			var name = row.INSPRD_NAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD120/PRDDocument.html',
				className: 'PRDDocument',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.PRD_ID = id;
                	$scope.PRD_NAME = name;
                	$scope.PTYPE = "INS";
                	$scope.SUBSYSTEM_TYPE = "PRD";
                }]
			});
		};
		
		//前往規劃書列印
		$scope.Insurance_Planning_Print = function(){
			if($scope.inputVO.PLAN_KEYNO != undefined){
				var plankeyno = $scope.inputVO.PLAN_KEYNO;
				var custId = $scope.inputVO.CUST_ID;
				var dialog = ngDialog.open({
					template: 'assets/txn/INS260/INS260.html',
					className: 'INS260',
					showClose: false,
					controller:	['$scope',function($scope){
						$scope.planKeyno = plankeyno;
						$scope.custId = custId;
					}]
				});
				
			}else{
				$scope.showErrorMsg('目前尚未規劃');
			}
			
		}
		
});