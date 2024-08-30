/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS500Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "INS500Controller";
		
		// 讀取參數檔
		getParameter.XML(["INS.UNIT"],function(tota){
			if(tota){
				$scope.INS_UNIT = tota.data[tota.key.indexOf('INS.UNIT')];
			}
		});
		
		// 主推 & 推薦商品相關列表初始化
		$scope.tableListInitial = function(PLAN_TYPE) {
			// 主推還本
			if(!PLAN_TYPE || PLAN_TYPE==8) {
				$scope.inputVO.reimbursementList = [];
				$scope.reimbursementOutputVO = [];
			}

			// 推薦保障
			if(!PLAN_TYPE || PLAN_TYPE==9) {
				$scope.inputVO.protectionList = [];
				$scope.protectionOutputVO = [];
			}
		}
		
		// 觸發清除
		$scope.clear = function(value) {
			if('N' != value) {
				$scope.inputVO.custId = undefined;
				$scope.inputVO.custName = undefined;				
				$scope.inputVO.age = undefined;
			}
			debugger
			console.log($scope.inputVO.age);
			$scope.inputVO.curAliveFee = undefined;
			$scope.tableListInitial(undefined);
			$scope.savingsOutputVO = [];
			$scope.inputVO.savingsList = [];
			$scope.initialProdSuggest();
		}
		
		// 作業整體初始化
		$scope.initial = function(){
			$scope.isDisabledPrint = true;
			$scope.selectProd = [];
//			$scope.tableListInitial();
			$scope.clear();
		}
		
		// 主推 & 推薦 init
		$scope.initialProdSuggest = function(){
			// 非同步
			$scope.getSuggest(8).then(function(){
				$scope.getSuggest(9); // 推薦保障型商品				
			}); // 主推還本型商品
			$scope.currentSelectRow = undefined;
		}
		
		// 取得使用者資訊	
		$scope.getUserInfo = function(in_column){
			var defer = $q.defer();
			$scope.inputVO.in_column = in_column;
			$scope.inputVO.INSURED_ID = $scope.inputVO.custId;
			$scope.sendRecv("IOT920","getCUSTInfo","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",
				$scope.inputVO,function(tota,isError){
				if(!isError){
					if(tota[0].body.INSURED_NAME != null){
						if(tota[0].body.INSURED_NAME.length>0){
							if($scope.inputVO.custId !=''){
								$scope.inputVO.custName = tota[0].body.INSURED_NAME[0].CUST_NAME;
								$scope.inputVO.gender = tota[0].body.INSURED_NAME[0].GENDER;
								debugger
								$scope.inputVO.birth = new Date(tota[0].body.INSURED_NAME[0].BIRTH_DATE);
							}
						} else {
							$scope.showErrorMsg("ehl_02_SOT_001");
							$scope.clear();
						}
					}
					defer.resolve("success");
				}else{
					if($scope.inputVO.in_column == 'CUST'){
						$scope.clear();
					}
					defer.reject();
				}
			});
			return defer.promise;
		}
		
		// 查詢還本 & 保障型商品
		$scope.getSuggest = function(PLAN_TYPE) {
			var defer = $q.defer();
			$scope.sendRecv("INS810","getSuggestPrd","com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO",
					{paraType:PLAN_TYPE, insAge:$scope.inputVO.age},function(tota,isError){
				if(!isError){
					debugger
					if(tota[0].body.suggestPrdList!=null && tota[0].body.suggestPrdList.length>0) {
						switch(PLAN_TYPE) {
							case 8: 
								$scope.inputVO.reimbursementList = tota[0].body.suggestPrdList;
								$scope.reimbursementOutputVO = tota[0].body.suggestPrdList;
								$scope.dataListFormat($scope.inputVO.reimbursementList, 2);
								break;
							case 9: 
								$scope.inputVO.protectionList = tota[0].body.suggestPrdList;
								$scope.protectionOutputVO = tota[0].body.suggestPrdList;
								$scope.dataListFormat($scope.inputVO.protectionList, 3);
								break;
							default :
								$scope.tableListInitial(PLAN_TYPE);
								break;
						}
					} else {
						$scope.tableListInitial(PLAN_TYPE);
					}
					defer.resolve("success");
				}else{
					$scope.showErrorMsg("主推還本 + 推薦保障 商品取得失敗!");
					$scope.tableListInitial(PLAN_TYPE);
					defer.reject();
				}
			});
	    	return defer.promise;
		}
		
		// 觸發查詢既有商品
		$scope.doQuery = function() {
			debugger
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
		    }
			$scope.sendRecv("INS500","query","com.systex.jbranch.app.server.fps.ins500.INS500InputVO",
					$scope.inputVO,function(tota,isError){
				if(!isError){
					if(tota[0].body.lstLogTableList) {
						$scope.showErrorMsg(JSON.stringify(tota[0].body.lstLogTableList));
					} else {
						$scope.inputVO.savingsList = tota[0].body.savingsList;
						$scope.savingsOutputVO = tota[0].body.savingsList;
						var whichTb = 1;
						angular.forEach($scope.inputVO.savingsList, function(row, index) {
							row.map = {};
							row['checkData'] = 'Y'; // 載入全部勾選
							row['prdId'+ whichTb] = row.PROD_ID; // 險種代碼
							row['prdName' + whichTb] = row.PROD_NAME; // 險種名稱
							row['insPrdAnnual' + whichTb] = row.PREMTERM_NAME // 繳費年期
							row['curPolicyYear' + whichTb] =row.YEAR; // 保險年度
							row['returnFee' + whichTb] =row.PREMIUM;// 保費 calAssureAmt()
							row['assureAmt' + whichTb] =row.QUANTITY + " " + row.COVERUNIT; // 保額
							row['aliveFee' + whichTb] =row.REPAY ? row.REPAY : 0; // 第一年度生存金  
						})
					}
				}else{
					$scope.showErrorMsg(tota.body.msgData);
				}
			});
			
		}
		
		// 觸發查詢
		$scope.query = function() {
			$scope.getUserInfo('INSURED').then(function(){
				$scope.getPolicyAge().then(function(){
					$scope.doQuery();
				});
			});
		}
		
		$scope.getPolicyAge = function() {
			var defer = $q.defer();
			// 取得當前客戶保險年齡
			$scope.sendRecv("INS500","calCustPolicyAge","com.systex.jbranch.app.server.fps.ins500.INS500InputVO",
				$scope.inputVO,function(tota2,isError2){
					if(!isError2){
						$scope.inputVO.age = tota2[0].body;
						$scope.clear('N');
						defer.resolve("success");
					} else {
						$scope.clear();
						defer.reject();
					}
				});
			return defer.promise;
		}
		
		
		
		// 勾選主推商品後
		$scope.checkReimbursementRow = function(row) {
			if(!$scope.inputVO.custName) {
//				row.checkData = false;
				$scope.showErrorMsg("請先確認客戶資訊");
				return;
			}
			if('N' == row.checkData) {
				row.insPrdAnnualModel2 = row.insPrdAnnual2[0].DATA;
				row.assureAmt2 = undefined;
				row.aliveFee2 = undefined;
				$scope.selectProd.splice($scope.selectProd.indexOf(row.prdId2),1);
			} else {
				$scope.selectProd.push(row.prdId2);
				// do only one default first
				$scope.onlyOneDefaultFirst(row, 2);
			}
			$scope.inputVO.curAliveFee = undefined;
			
			$scope.resetCurrentSelectRow();
			
			debugger
			if($scope.selectProd && $scope.selectProd.length > 0) {
				$scope.isDisabledPrint = false;
			} else {
				$scope.isDisabledPrint = true;
			}
		}
		
		// 勾選推薦商品後
		$scope.checkProtectionRow = function(row) {
			// 先判斷有無計算當年度可領回生存金 再確認當前操作
			if($scope.inputVO.curAliveFee != null && $scope.inputVO.curAliveFee != undefined) {
				if($scope.inputVO.curAliveFee == 0) {
					row.checkData = false;
					row.isDisabled = true;
					row.policyFee3 = '';
					row.policyFee3_tmp = '';
					row.insPrdAnnualModel3 = row.insPrdAnnual3[0].DATA;
					$scope.resetCurrentSelectRow();
					$scope.showErrorMsg("ehl_01_INS500_001");
				} else {
					$scope.resetCurrentSelectRow(row);
					$scope.currentSelectRow = row;
					row.isDisabled = false;
					row.policyFee3 = $scope.inputVO.curAliveFee;
					// do only one default first
					$scope.onlyOneDefaultFirst(row, 3);
				}
			} else {
				row.checkData = false;
				row.isDisabled = true;
				row.policyFee3 = '';
				row.insPrdAnnualModel3 = row.insPrdAnnual3[0].DATA;
				$scope.resetCurrentSelectRow();
				$scope.showErrorMsg("請先計算 當年度可領回生存金");
			}
			
			if($scope.selectProd && $scope.selectProd.length > 0) {
				$scope.isDisabledPrint = false;
			} else {
				$scope.isDisabledPrint = true;
			}

//			if(row.insPrdAnnualModel3 && row.ref3 && row.rate3 && row.assureAmt3 && row.policyAmtMin3) {
//				var value = row.policyFee3 / row.rate3 / row.ref3;
//				if(value >= row.policyAmtMax3) {
//					row.assureAmt3 = row.policyAmtMax3;
//					row.policyFee3 = row.assureAmt3 * row.rate3 * row.ref3;
//				} else if(value <= row.policyAmtMin3) {
//					row.assureAmt3 = row.policyAmtMin3;
//					row.policyFee3 = row.assureAmt3 * row.rate3 * row.ref3;
//				} else {
//					var assureAmt = ((value-row.policyAmtMin3) / row.policyAmtDistance3) * row.policyAmtDistance3 + row.policyAmtMin3;
//					row.assureAmt3 = Math.round(assureAmt);								
//				}
//			}
		}
		
		// 當前推薦商品 reset
		var doReset = false;
		$scope.resetCurrentSelectRow = function(row) {
			debugger
			doReset = true;
			if($scope.currentSelectRow) {
				var index = $scope.selectProd.indexOf($scope.currentSelectRow.prdId3);
				$scope.selectProd.splice(index,index >= 0 ? 1 :0);
				if(row) $scope.selectProd.push(row.prdId3);
				if((row && !(row.prdId3 == $scope.currentSelectRow.prdId3)) || !row) {
					$scope.currentSelectRow.checkData = false;
					$scope.currentSelectRow.isDisabled = true;
					$scope.currentSelectRow.policyFee3 = '';
					$scope.currentSelectRow.policyFee3_tmp = '';
					$scope.currentSelectRow.insPrdAnnualModel3 = $scope.currentSelectRow.insPrdAnnual3[0].DATA;
				}
			} else {
				if(row) $scope.selectProd.push(row.prdId3);
			}
		}
		
		// 按下確定
		$scope.confirm = function() {
			debugger
			var aliveFeeTotal = 0;
			angular.forEach($scope.inputVO.savingsList, function(row, index) {
				if(row.checkData == 'Y') {
					aliveFeeTotal += row.aliveFee1;
				}
			})
			angular.forEach($scope.inputVO.reimbursementList, function(row, index) {
				if(row.checkData == 'Y') {
					aliveFeeTotal += row.aliveFee2 ? row.aliveFee2 : 0;
				}
			})
			$scope.inputVO.curAliveFee = aliveFeeTotal;

			$scope.resetCurrentSelectRow();
			
			$scope.currentSelectRow = undefined;
		}
		
		// 第一年度生存金
		$scope.currentRepay = function(row) {
			// 當使用者資訊確認後取得主推商品的第一年度生存金 & 當前客戶保險年齡
			debugger
			$scope.inputVO.suggestList = [row];
			$scope.sendRecv("INS500","getFirstRepay","com.systex.jbranch.app.server.fps.ins500.INS500InputVO",
				$scope.inputVO,function(tota2,isError2){
					if(!isError2){
						if(!$scope.inputVO.age) {
							$scope.inputVO.age = tota2[0].body[0];
						}
						if(tota2[0].body[1].reimbursementList[0].ERROR) {
							$scope.showErrorMsg(tota2[0].body[1].reimbursementList[0].ERROR);
						} else {
							row.aliveFee2 = tota2[0].body[1].reimbursementList[0].REPAY;
						}
					} 
				});
		}
		
		// 取得費率
		$scope.getRefRate = function(row) {
			
			debugger
			
			if(row.insPrdAnnualModel2) {
				var exIndex = row.insPrdAnnual2.map(function(e) { return e.DATA; }).indexOf(row.insPrdAnnualModel2);
				var obj = row.insPrdAnnual2[exIndex];
				row.cvrgRatio2 = obj.CVRG_RATIO;
				row.keyNo2 = obj.KEY_NO;
				row.prodKeyno2 = obj.INSDATA_KEYNO;
					// 保額相關
				row.policyAmtMin2 = obj.POLICY_AMT_MIN; // 保額下限
				row.policyAmtMax2 = obj.POLICY_AMT_MAX; // 保額上限
				row.policyAmtDistance2 = obj.POLICY_AMT_DISTANCE; // 保額累加級距
			}
			var policySuggestInputVO = {
				insPrdId:row.prdId2,
				annual:row.insPrdAnnualModel2,
				currCD: row.currCD2,
				age: $scope.inputVO.age,
				gender: $scope.inputVO.gender
			};
			if(!(policySuggestInputVO.insPrdId && policySuggestInputVO.annual && policySuggestInputVO.currCD 
					&& policySuggestInputVO.age && policySuggestInputVO.gender)) {
				row['ref2'] = undefined;
				row['rate2'] = undefined;
				row.policyFee2 = 0;
				return;
			}
			
			$scope.sendRecv("INS810","getPremAndExchangeRate","com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO",
					policySuggestInputVO, function(totas,isError){
				if(!isError) {
					row['ref2'] = row.currCD2 == 'TWD' ? 1 : totas[0].body.refExcRateMap[row.currCD2]; // 匯率
					row['rate2'] = totas[0].body.premRate; // 費率 
					if(row.assureAmt2) {
						row.policyFee2 = row.assureAmt2 * row['ref2'] * row['rate2'];
						$scope.currentRepay(row);
					}
				}
			});
		}
		
		// 保額計算
		$scope.getAssureAmt = function(row) {
			if(!row.insPrdAnnualModel2) {
				$scope.showErrorMsg("請先選擇 繳費年期");
				row.assureAmt2 = undefined;
				return;
			}
			
			if(row.assureAmt2) {
				debugger
    			// 轉換成數值
				var assureAmt = Number(row.assureAmt2);
				var POLICY_AMT_MIN = Number(row.policyAmtMin2);
				var POLICY_AMT_MAX = Number(row.policyAmtMax2);
				var POLICY_AMT_DISTANCE = Number(row.policyAmtDistance2);
				if(assureAmt <= POLICY_AMT_MIN)
					row.assureAmt2 = POLICY_AMT_MIN;
				else if(assureAmt >= POLICY_AMT_MAX)
					row.assureAmt2 = POLICY_AMT_MAX;
				else
					row.assureAmt2 = POLICY_AMT_MIN + (Math.ceil((assureAmt - POLICY_AMT_MIN) / POLICY_AMT_DISTANCE) * POLICY_AMT_DISTANCE);
				if(row.ref2 && row.rate2) {
					row.policyFee2 = row.assureAmt2 * row['ref2'] * row['rate2'];
					$scope.currentRepay(row);
				}
				
			}
		}
		
		// 保額計算 (由保費反推)
//		[推薦保障型商品] 清單，將根據參數設定顯示。用當年度領回生存金作為保費，搭配人壽提供的費率表算出保額。
//
//		再用當年度可領回生存金(=該年度可繳保費) / 費率 (ex.117184 / 580 = 202.04)
//		比對保額上下限 (6, 500) 與 保額累加級距 (4) 確認正確保額 = 取整數[(202.04-6)/4] * 4 + 6 = 202
//
//		保額欄位 顯示 202
//		保障年期 顯示 人壽提供的商品保障年期 = 99
		$scope.getAssureAmtAndAnnual = function(row, kind) {
			debugger
			if(kind == 'blur') {
				if(!row.insPrdAnnualModel3) {
					$scope.showErrorMsg("請先選擇 繳費年期");
					row.assureAmt3 = undefined;
					row.guaranteeAnnual = undefined;
				}
				
				if(row.policyFee3_tmp == row.policyFee3){
					return;
				}
			} else {
				row.policyFee3 = $scope.inputVO.curAliveFee;
			}
			
			if(row.insPrdAnnualModel3) {
				debugger
				var exIndex = row.insPrdAnnual3.map(function(e) { return e.DATA; }).indexOf(row.insPrdAnnualModel3);
				var obj = row.insPrdAnnual3[exIndex];
				row.cvrgRatio3 = obj.CVRG_RATIO;
				row.keyNo3 = obj.KEY_NO;
				row.prodKeyno3 = obj.INSDATA_KEYNO;
					// 保額相關
				row.policyAmtMin3 = obj.POLICY_AMT_MIN; // 保額下限
				row.policyAmtMax3 = obj.POLICY_AMT_MAX; // 保額上限
				row.policyAmtDistance3 = obj.POLICY_AMT_DISTANCE; // 保額累加級距
				
				row.earnedYear = obj.EARNED_YEAR; // 滿期金計算類型
				row.earnedRatio = obj.EARNED_RATIO; // 滿期金比
				row.earnedCalWay = obj.EARNED_CAL_WAY; // 滿期金年度
				console.log('保額下限' + row.policyAmtMin3);
				console.log('保額上限' + row.policyAmtMax3);
				console.log('保額累加級距' + row.policyAmtDistance3);
				if(row.policyFee3) {
					var policySuggestInputVO = {
						insPrdId:row.prdId3,
						annual:row.insPrdAnnualModel3,
						currCD: row.currCD3,
						age: $scope.inputVO.age,
						gender: $scope.inputVO.gender
					};
					if(!(policySuggestInputVO.insPrdId && policySuggestInputVO.annual && policySuggestInputVO.currCD 
							&& policySuggestInputVO.age && policySuggestInputVO.gender)) {
						row['ref3'] = undefined;
						row['rate3'] = undefined;
						return;
					}
					
					$scope.sendRecv("INS810","getPremAndExchangeRate","com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO",
							policySuggestInputVO, function(totas,isError){
						if(!isError) {
							row['ref3'] = row.currCD3 == 'TWD' ? 1 : totas[0].body.refExcRateMap[row.currCD3]; // 匯率
							row['rate3'] = totas[0].body.premRate; // 費率 
							var value = row.policyFee3 / row['rate3'] / row['ref3'];
							
							if(value >= Number(row.policyAmtMax3)) {
								row.assureAmt3 = row.policyAmtMax3;
								row.policyFee3 = row.assureAmt3 * row['rate3'] * row['ref3'];
							} else if(value <= Number(row.policyAmtMin3)) {
								row.assureAmt3 = row.policyAmtMin3;
								row.policyFee3 = row.assureAmt3 * row['rate3'] * row['ref3'];
							} else {
								var assureAmt = ((value-Number(row.policyAmtMin3)) / Number(row.policyAmtDistance3)) * Number(row.policyAmtDistance3) + Number(row.policyAmtMin3);
								row.assureAmt3 = Math.floor(assureAmt);		
								row.policyFee3 = row['ref3'] * row['rate3'] * row.assureAmt3;
							}
							
							row.policyFee3_tmp = row.policyFee3;
						}
					});
					row.guaranteeAnnual = obj.GUARANTEE_ANNUAL;
					
//					$scope.sendRecv("INS500","getCoverageSel","com.systex.jbranch.app.server.fps.ins500.INS500InputVO",
//							{prdID:row.prdId3, annual:row.insPrdAnnualModel3}, function(totas,isError){
//						if(!isError) {
//							debugger
//						}
//					});
				} else {
					row.assureAmt3 = undefined;
					row.guaranteeAnnual = undefined;
				}
			} else {
				row.assureAmt3 = undefined;
				row.guaranteeAnnual = undefined;
				if(!doReset) {
					row.policyFee3 = '';
					row.policyFee3_tmp = '';
				} 
//				else {
//					row.policyFee3 = $scope.inputVO.curAliveFee;
//					row.policyFee3_tmp = row.policyFee3;
//				}
			}
			doReset = false;
		}
		
		// 列印建議書
		$scope.print = function() {
			debugger;
			// 取得有勾選的既有商品
			$scope.inputVO.checkFirstTablePrd = [];
			angular.forEach($scope.inputVO.savingsList, function(row, index) {
				if(row.checkData == 'Y') {
					$scope.inputVO.checkFirstTablePrd.push(row.prdId1);
				}
			})
			
			// 取得有勾選的主推商品
			$scope.inputVO.checkSecondTablePrd = [];
			angular.forEach($scope.inputVO.reimbursementList, function(row, index) {
				if(row.checkData == 'Y') {
					$scope.inputVO.checkSecondTablePrd.push(row);
				}
			})
			
			// 取得有勾選的推薦商品
			$scope.inputVO.checkThirdTablePrd = [];
			if($scope.currentSelectRow) {
				$scope.inputVO.checkThirdTablePrd.push($scope.currentSelectRow);				
			}
			
			$scope.inputVO.selectPrdDatas = [];
			$scope.inputVO.selectPrdDatas.push($scope.inputVO.checkFirstTablePrd);
			$scope.inputVO.selectPrdDatas.push($scope.inputVO.checkSecondTablePrd);
			$scope.inputVO.selectPrdDatas.push($scope.inputVO.checkThirdTablePrd);
			
			$scope.sendRecv("INS500","printSuggestReport","com.systex.jbranch.app.server.fps.ins500.INS500InputVO",
					$scope.inputVO, function(totas,isError){
				if(isError) {
					$scope.showErrorMsg("保險商品組合建議書列印失敗");
				}
			});
		}
		
//		// 暫存
//		$scope.save = function() {
//			$scope.sendRecv("INS500","temporarySave","com.systex.jbranch.app.server.fps.ins500.INS500InputVO",
//				$scope.inputVO, function(totas,isError){
//					debugger
//					if(isError) {
//						$scope.showErrorMsg("保險商品組合建議暫存失敗");
//					} else {
//						$scope.showSuccessMsg("保險商品組合規劃暫存成功");
//					}
//				});
//		}
		
		// 資料統一格式化
		$scope.dataListFormat = function(dataList, whichTb) {
			angular.forEach(dataList, function(row, index) {
//				row.map = {};
//				row.cvrgRatio = row.CVRG_RATIO.split(',');
//				row.keyNo = row.KEY_NO.split(',');
				
				row['prdId'+ whichTb] = row.PRD_ID; // 險種代碼
				row['currCD'+ whichTb] = row.CURR_CD;
				row['prdName' + whichTb] = row.INSPRD_NAME; // 險種名稱
//				row['insPrdAnnual' + whichTb] = row.INSPRD_ANNUAL.split(',')
//				.map(function(item, _index){ 
//					row.map[item] = {
//							annual: item,
//							cvrgRatio: row.cvrgRatio[_index],
//							keyNo: row.keyNo[_index]
//					}
//					return { LABEL: item, DATA: item}
//					}); // 繳費年期
				row['insPrdAnnual' + whichTb] = row.insprd_annualList;
				row['insPrdAnnualModel' + whichTb] = '';
				row['curPolicyYear' + whichTb] ='1'; // 保險年度
				row['policyFee' + whichTb] =''; // 保費 calAssureAmt()
				row['assureAmt' + whichTb] =''; // 保額
				row['prdUnit' + whichTb] = row.PRD_UNIT; // 單位
				row['currCD' + whichTb] = row.CURR_CD; // 幣別
				row['prodKeyno' + whichTb] = '';
//				debugger
//				
//				if($scope.inputVO.age && $scope.inputVO.gender) {
//					if(row.insprd_annualList.length == 1) {
//						row['insPrdAnnualModel' + whichTb] = row.insprd_annualList[0].DATA;
//						row['cvrgRatio' + whichTb] = row.insprd_annualList[0].CVRG_RATIO;
//						row['keyNo' + whichTb] = row.insprd_annualList[0].KEY_NO;
//						row['policyAmtMin' + whichTb] = row.insprd_annualList[0].POLICY_AMT_MIN; // 保額下限
//						row['policyAmtMax' + whichTb] = row.insprd_annualList[0].POLICY_AMT_MAX; // 保額上限
//						row['policyAmtDistance' + whichTb] = row.insprd_annualList[0].POLICY_AMT_DISTANCE; // 保額累加級距
//						
//						if(whichTb == 3) {
//							row.earnedYear = row.insprd_annualList[0].EARNED_YEAR; // 滿期金計算類型
//							row.earnedRatio = row.insprd_annualList[0].EARNED_RATIO; // 滿期金比
//							row.earnedCalWay = row.insprd_annualList[0].EARNED_CAL_WAY; // 滿期金年度
//						}
//						
//						var policySuggestInputVO = {
//							insPrdId:row['prdId' + whichTb],
//							annual:row['insPrdAnnualModel' + whichTb],
//							currCD: row['currCD' + whichTb],
//							age: $scope.inputVO.age,
//							gender: $scope.inputVO.gender
//						};
//						if((policySuggestInputVO.insPrdId && policySuggestInputVO.annual && policySuggestInputVO.currCD 
//								&& policySuggestInputVO.age && policySuggestInputVO.gender)) {
//						
//							$scope.sendRecv("INS810","getPremAndExchangeRate","com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO",
//									policySuggestInputVO, function(totas,isError){
//								if(!isError) {
//									row['ref' + whichTb] = row['currCD' + whichTb] == 'TWD' ? 1 : totas[0].body.refExcRateMap[row['currCD' + whichTb]]; // 匯率
//									row['rate' + whichTb] = totas[0].body.premRate; // 費率 
//								}
//							});
//						}
//					}
//				}
				
				
				// 保額相關
//				row['policyAmtMin' + whichTb] = row.POLICY_AMT_MIN; // 保額下限
//				row['policyAmtMax' + whichTb] = row.POLICY_AMT_MAX; // 保額上限
//				row['policyAmtDistance' + whichTb] = row.POLICY_AMT_DISTANCE; // 保額累加級距
				if(whichTb == 2) {
					row['INSDATA_KEYNO'] = row.INSDATA_KEYNO; // PRD_KEYNO
					row['aliveFee' + whichTb] =row.REPAY; // 第一年度生存金 
					row['checkData'] = false;
				} else {
					row['checkData'] = false;
					row['isDisabled'] = true;
				}
			})
		}
		
		// row 當前勾選的 index 列表 1 or 2
		$scope.onlyOneDefaultFirst = function(row, index) {
			debugger;
			if(row['insPrdAnnual' + index]) {
				if(index == 2 && row['insPrdAnnual' + index].length == 2) {
					row.insPrdAnnualModel2 = row.insPrdAnnual2[1].DATA;
					$scope.getRefRate(row)
				} else {
					row.insPrdAnnualModel3 = row.insPrdAnnual3[1].DATA;
					$scope.getAssureAmtAndAnnual(row, 'change');
				}				
			}
		}

		$scope.initial();
});
