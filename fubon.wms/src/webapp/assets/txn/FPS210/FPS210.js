/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS210Controller',
  function ($rootScope, $scope, $controller, socketService, ngDialog, $timeout, projInfoService, getParameter, $filter, $q, $confirm) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS210Controller';
    $scope.dragOptions = {
      label: {
        front: '0%',
        end: '100%',
        unit: '%',
      },
      drag: {
        interval: 1
      },
      showTip: true,
      showLabel: true,
      showBtn: true,
    };
    

    /* parameter */
    var planID = $scope.connector('get', 'planID') || undefined;
    var custID = $scope.connector('get', 'custID') || undefined;
    $scope.kycInvalid = $scope.connector('get', 'kycInvalid') !== false ? true : false;
    $scope.hasIns = false; // 有無投保
    // var hasPopHint = 0;
    $scope.modelFlag = '1';
    var suggestPct = [];
    var minPriceAmt = -1;
    var cntCash = 0;
    var cntCashOther = 0;
    var originLoan = {};
    var originIns = {};
    var signText = {
      valid: '提醒您：此客戶尚未填寫推介同意書。建議請客戶填寫完成後，本行即可提供完整之商品投資標的規劃。若客戶未簽署推介同意書，則本行所提供之規畫文件將不包含個別商品標的。是否列印推介同意書?',
      notValid: '提醒您，此客戶無簽署推介同意書，本行所提供給客戶之規劃文件將不包含個別商品標的。是否繼續規劃?'
    };

    /* init */
    $scope.init = function () {
      $scope.newFlag = '';
      $scope.PBList = [];
      $scope.FPList = [];
      $scope.inputVO = {
        step: '1',
        liquidAmt: 0,
        deposit: 0,

        // hasIns
        mfdProd: 0,
        etfProd: 0,
        insProd: 0,
        bondProd: 0,
        snProd: 0,
        siProd:0,
        nanoProd:0,   //2020.1.20 新增奈米投
        
        //TODO 現金準備 之後抓參數 
    	percent:10,
        
        sowAmt: 0,
        cash: 0,
        insPolicyAmt: 0,
        insSavAmt: 0,
        ins: 0
      };
      $scope.fromES = false; // 電文		來的資料
      $scope.fromDB_insPolicyAmt = false; // 資料庫	來的資料
      $scope.fromDB_insSavAmt = false; // 資料庫	來的資料
      $scope.step1List = [];
      $scope.connector('set', 'STEP1VO', undefined);
      $scope.connector('set', 'STEP2VO', undefined);
      $scope.connector('set', 'STEP3VO', undefined);
      $scope.connector('set', 'cashVO', undefined);
    };

    // getXML
    var param = function () {
      var deferred = $q.defer();
      $scope.sendRecv('FPS210', 'getAvailableParam', 'com.systex.jbranch.app.server.fps.fps210.FPS210InputVO', {},
        function (tota, isError) {
          if (!isError) {
            minPriceAmt = tota[0].body || -1;
            console.log(minPriceAmt);
            deferred.resolve('success');
            return true;
          }
          deferred.reject(tota);
          return false;
        });
      return deferred.promise;
    };
    
    // getPct
    var getPct = function () {
        var deferred = $q.defer();
        $scope.sendRecv('FPS210', 'getSuggestPct', 'java.util.Map', {custID:$scope.connector('get', 'custID'), hasIns:$scope.hasIns,riskType:$scope.connector('get', 'custInfo').KYC_LEVEL},
          function (tota, isError) {
            if (!isError) {
              suggestPct.push(tota[0].body[0]);
              suggestPct.push(tota[0].body[1]);
              
              if($scope.hasIns) {
            	  suggestPct.push(tota[0].body[2]);
                  suggestPct.push(tota[0].body[3]);
              }
              deferred.resolve('success');
              return true;
            }
            deferred.reject(tota);
            return false;
          });
        return deferred.promise;
      };

    // main function

    var getCustData = function (custID, planID) {
      var defer = $q.defer();
      if (custID) {
        $scope.sendRecv('FPS210', 'inquire', 'com.systex.jbranch.app.server.fps.fps210.FPS210InputVO', {
            custID: custID,
            planID: planID
          },
          function (tota, isError) {
            if (!isError) {
              console.log(tota[0].body);
              var output = tota[0].body;
              if (output.outputList.length > 0) {
                $scope.newFlag = output.newFlag;
                $scope.custList = output.custList[0];
                // if new: get item from cust | cash data = 0
                // else: get item from fps_inv (get cash datas)
                $scope.paramList = output.outputList[0];
                
                $scope.cashPreparePct = output.cashPreparePct;
                $scope.inputVO.percent = output.cashPreparePct;
                
                $scope.inputVO.planID = $scope.paramList.PLAN_ID || '';
                $scope.inputVO.action = $scope.paramList.PLAN_ID ? 'update' : 'create';

                $scope.inputVO.deposit = $scope.paramList.DEPOSIT_AMT || 0;
                $scope.inputVO.sowAmt = $scope.paramList.SOW_AMT || 0;
                
                // hasIns
                $scope.inputVO.mfdProd = $scope.paramList.MFD_PROD_AMT || 0;
                $scope.inputVO.etfProd = $scope.paramList.ETF_PROD_AMT || 0;
                $scope.inputVO.insProd = 0;
                $scope.inputVO.bondProd = $scope.paramList.BOND_PROD_AMT || 0;
                $scope.inputVO.snProd = $scope.paramList.SN_PROD_AMT || 0;
                $scope.inputVO.siProd = $scope.paramList.SI_PROD_AMT || 0;
                
                //2020-1-30 by Jacky 新增奈米投
                $scope.inputVO.nanoProd = $scope.paramList.NANO_PROD_AMT || 0;
                
//                $scope.inputVO.annuityProd = $scope.paramList.INS_1_AMT || $scope.paramList.ANNUITY_INS_AMT || 0;
//                $scope.inputVO.fixedProd = $scope.paramList.FIXED_INCOME_AMT || 0;
//                $scope.inputVO.fundProd = $scope.paramList.FUND_AMT || 0;
                var sum1 = $scope.inputVO.mfdProd + $scope.inputVO.etfProd + $scope.inputVO.insProd + $scope.inputVO.nanoProd; //2020-1-30 by Jacky 新增奈米投
                var sum2 = $scope.inputVO.bondProd + $scope.inputVO.snProd + $scope.inputVO.siProd;
                $scope.inputVO.liquidAmt = ($scope.inputVO.deposit + sum1 + sum2) || 0;
                
                $scope.prevBusiDay = tota[0].body.prevBusiDay;

                // 歷史有更改過的 cash 參數
                $scope.cashList = (tota[0].body.cashList && tota[0].body.cashList.length > 0) ? tota[0].body.cashList[0] : {};
//                debugger;
                // 有購買過商品
                $scope.hasIns = ((sum1 + sum2) || 0) > 0;

                // INS_YEAR_AMT_1(new) INS_POLICY_AMT(inv)
                $scope.inputVO.insPolicyAmt = ($scope.paramList.INS_POLICY_AMT || $scope.paramList.INS_YEAR_AMT_1) || 0;
                // INS_YEAR_AMT_2(new) INS_SAV_AMT(inv)
                $scope.inputVO.insSavAmt = ($scope.paramList.INS_SAV_AMT || $scope.paramList.INS_YEAR_AMT_2) || 0;
                $scope.inputVO.ins = $scope.paramList.INS_AMT || 0;
                
                $scope.cashVO = {
//                  essCash: $scope.paramList.LIVE_YEAR_AMT || $scope.cashList.LIVE_YEAR_AMT || 0,
//                  emeCsh: $scope.paramList.PREPARE_YEAR_AMT || $scope.cashList.PREPARE_YEAR_AMT || 0,
                  // LN_YEAR_AMT_1(new) LN_HOUSE_AMT(inv)
                  houseLoan: ($scope.custList.LN_YEAR_AMT_1 || $scope.paramList.LN_HOUSE_AMT || $scope.cashList.LN_HOUSE_AMT) || 0,
                  // LN_YEAR_AMT_2(new) LN_CREDIT_AMT(inv)
                  creditLoan: ($scope.custList.LN_YEAR_AMT_2 || $scope.paramList.LN_CREDIT_AMT || $scope.cashList.LN_CREDIT_AMT) || 0,
                  // LN_YEAR_AMT_3(new) LN_EDCUATION_AMT(inv)
                  stdLoan: ($scope.custList.LN_YEAR_AMT_3 || $scope.paramList.LN_EDCUATION_AMT || $scope.cashList.LN_EDCUATION_AMT) || 0,
                  payForHouse: $scope.paramList.BUY_HOUSE_AMT || $scope.cashList.BUY_HOUSE_AMT || 0,
                  payForCar: $scope.paramList.BUY_CAR_AMT || $scope.cashList.BUY_CAR_AMT || 0,
                  study: $scope.paramList.OVERSEA_EDUCATION_AMT || $scope.cashList.OVERSEA_EDUCATION_AMT || 0,
                  travel: $scope.paramList.TRAVEL_AMT || $scope.cashList.TRAVEL_AMT || 0,
                  other: $scope.paramList.OTHER_AMT || $scope.cashList.OTHER_AMT || 0,
                };
                
                // 情況一：貸款電文有資料 or 保費資料庫有資料時
                // >理專只可輸入比貸款電文或保費資料庫高的數字，輸入比較低的數字要跳提醒文字並擋掉
                // >每次新規劃都會帶入最新的電文或資料庫數字
                // >S1欄位與?欄位都要帶入貸款電文或保費資料庫數字

                // 情況二：貸款電文有資料 or 保費資料庫無資料時，但理專之前有輸入欄位資料
                // >直接帶入前次理專輸入的金額(包含S1欄位與?欄位)，這邊沒有邏輯控管
                //
                // 情況三：貸款電文有資料 or 保費資料庫有資料時且理專之前有輸入欄位資料
                // >依電文或資料庫資料文主，邏輯比照情況一

                $scope.cashVO.AmtTotal_1 = $scope.cashVO.houseLoan + $scope.cashVO.creditLoan + $scope.cashVO.stdLoan;
                
            	$scope.cashVO.AmtTotal_2 = $scope.cashVO.payForHouse + $scope.cashVO.payForCar + $scope.cashVO.study + $scope.cashVO.travel + $scope.cashVO.other;

                // 生活必要支出
                $scope.inputVO.needExpenses = $scope.paramList.LIVE_YEAR_AMT || $scope.cashList.LIVE_YEAR_AMT || 0;
                
                
                if($scope.inputVO.action === 'create') {
                	$scope.inputVO.cashPrepare = Math.round($scope.inputVO.percent * $scope.inputVO.deposit / 100); 
                	// 貸款支出
                    compareLoanExpenses();
                    
                	// 其他支出
                    $scope.inputVO.otherExpenses = $scope.cashVO.payForHouse + $scope.cashVO.payForCar + $scope.cashVO.study + $scope.cashVO.travel + $scope.cashVO.other;
                    if($scope.cashList.OTHER_EXPENSES > $scope.inputVO.otherExpenses) {
                    	$scope.inputVO.otherExpenses = $scope.cashList.OTHER_EXPENSES;
                    	$scope.cashVO.payForHouse = 0; 
                    	$scope.cashVO.payForCar = 0;
                    	$scope.cashVO.study = 0; 
                    	$scope.cashVO.travel = 0; 
                    	$scope.cashVO.other = 0;
                    }
                    
                    // 保障型保費
                    if($scope.paramList.INS_POLICY_AMT || $scope.paramList.INS_YEAR_AMT_1) {
                    	$scope.fromDB_insPolicyAmt = true;
                    }
                    $scope.inputVO.insPolicyAmt = ($scope.paramList.INS_POLICY_AMT || $scope.paramList.INS_YEAR_AMT_1 || $scope.cashList.INS_POLICY_AMT) || 0;

                    // 儲蓄型保費
                    if($scope.paramList.INS_SAV_AMT || $scope.paramList.INS_YEAR_AMT_2) {
                    	$scope.fromDB_insSavAmt = true;
                    }
                    $scope.inputVO.insSavAmt = ($scope.paramList.INS_SAV_AMT || $scope.paramList.INS_YEAR_AMT_2 || $scope.cashList.INS_SAV_AMT) || 0;
                } else {
                	$scope.inputVO.loanExpenses = $scope.paramList.LOAN_EXPENSES || 0;
                	$scope.cashVO.houseLoan = $scope.paramList.LN_HOUSE_AMT || 0;
                 	$scope.cashVO.creditLoan = $scope.paramList.LN_CREDIT_AMT || 0;
                 	$scope.cashVO.stdLoan = $scope.paramList.LN_EDCUATION_AMT || 0;
            		$scope.inputVO.otherExpenses = $scope.paramList.OTHER_EXPENSES || 0;
                	$scope.inputVO.cashPrepare = $scope.paramList.CASH_PREPARE || 0;
                	$scope.changeCashPrepare();
                }
                
                originLoan = {
                  houseLoan: $scope.cashVO.houseLoan || 0,
                  creditLoan: $scope.cashVO.creditLoan || 0,
                  stdLoan: $scope.cashVO.stdLoan || 0,
                };

                originIns = {
        		  loanExpenses: $scope.inputVO.loanExpenses,
        		  otherExpenses: $scope.inputVO.otherExpenses,
        		  needExpenses: $scope.inputVO.needExpenses,
                  insPolicyAmt: $scope.custList.INS_YEAR_AMT_1,
                  insSavAmt: $scope.custList.INS_YEAR_AMT_2,
                };

                countCashOther();
                countCash();

                $scope.inputVO.cash = $scope.paramList.CASH_YEAR_AMT || $scope.cashList.CASH_YEAR_AMT || cntCash || 0;
                defer.resolve(true);
                return true;
              }
            }
            defer.reject(tota);
            return false;
          });
      } else {
        $timeout(function () {
          defer.reject(false);
        });
      }

      return defer.promise;
    };
    
    // 處理電文資料
    var compareLoanExpenses = function() {
    	 var tempLoanExpensesMapping = {
         	'1': ['LN_YEAR_AMT_1', 'LN_YEAR_AMT_2', 'LN_YEAR_AMT_3'],
         	'3': ['LN_HOUSE_AMT', 'LN_CREDIT_AMT', 'LN_EDCUATION_AMT'],
         }
         
         var tempLoanExpensesList = {
         	'1': $scope.custList,
         	'3': $scope.cashList
         }
         
         // 每次 $scope.custList 同電文過來的
         var tempCashVOAmtTotal_1 = setTempLoanExpenses(tempLoanExpensesMapping, tempLoanExpensesList, '1');; // 貸款 
         
         // 每次 $scope.cashList 歷史規劃終日期最近的的
         var tempCashVOAmtTotal_3 = tempLoanExpensesList['3'] ? (tempLoanExpensesList['3']['LOAN_EXPENSES'] || 0) : 0; // 貸款 
         
         // 三個 temp 中最大的
         var max = '1';
         if(tempCashVOAmtTotal_1 != 0) {
        	 max = '1';
        	 $scope.fromES = true;
         } else {
        	 max = '3';
         }
         
        $scope.cashVO.houseLoan = tempLoanExpensesList[max][tempLoanExpensesMapping[max][0]] || 0;
     	$scope.cashVO.creditLoan = tempLoanExpensesList[max][tempLoanExpensesMapping[max][1]] || 0;
     	$scope.cashVO.stdLoan = tempLoanExpensesList[max][tempLoanExpensesMapping[max][2]] || 0;
     	
     	$scope.inputVO.loanExpenses = (max === '1' ? tempCashVOAmtTotal_1 : 
     		(tempLoanExpensesList[max]['LOAN_EXPENSES'] ? tempLoanExpensesList[max]['LOAN_EXPENSES'] : 0));
    }
    
    var setTempLoanExpenses = function(tempLoanExpensesMapping, tempLoanExpensesList, type) {
    	var total = 0;
    	tempLoanExpensesMapping[type].forEach(function(column) {
    		total += tempLoanExpensesList[type][column] || 0;
    	});
    	return total;
    }

    $scope.setItem = function () {
      var recordCash = angular.copy($scope.cashVO);
      var recordInputCash = $scope.inputVO.cash;
      var scope = $scope;
      
      if($scope.cashVO) {
    	  if($scope.inputVO.loanExpenses != $scope.cashVO.AmtTotal_1) {
    		  $scope.cashVO.houseLoan = 0;
    		  $scope.cashVO.creditLoan = 0;
    		  $scope.cashVO.stdLoan = 0;
    		  $scope.cashVO.AmtTotal_1 = 0;
    	  } 
    	  
    	  if($scope.inputVO.otherExpenses != $scope.cashVO.AmtTotal_2){
    		  $scope.cashVO.payForHouse = 0;
    		  $scope.cashVO.payForCar = 0;
    		  $scope.cashVO.study = 0;
    		  $scope.cashVO.travel = 0;
    		  $scope.cashVO.other = 0;
    		  $scope.cashVO.AmtTotal_2 = 0;
    	  }
      }
      
      var dialog = ngDialog.open({
        template: 'assets/txn/FPS210/FPS210_CASH.html',
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.cashVO = scope.cashVO;
          $scope.originLoan = originLoan;
        }]
      });
      dialog.closePromise.then(function (data) {
        if (data.value === 'cancel' || !data.value) {
          $scope.cashVO = recordCash;
          $scope.inputVO.cash = recordInputCash;
        } else {
          $scope.cashVO = data.value;
          $scope.inputVO.loanExpenses = $scope.cashVO.AmtTotal_1;
          $scope.inputVO.otherExpenses = $scope.cashVO.AmtTotal_2;
          countCashOther();
          countCash();
          $scope.chgAmt();
        }
      });
    };

    // update amt on blur
    $scope.chgAmt = function (key) {
      var insPolicyAmtBoolean = (key === 'insPolicyAmt' && $scope.fromDB_insPolicyAmt);
      var insSavAmtBoolean = (key === 'insSavAmt' && $scope.fromDB_insSavAmt);
      var loanExpensesBoolean = (key === 'loanExpenses' && $scope.fromES);
      
      if ((insPolicyAmtBoolean || insSavAmtBoolean|| loanExpensesBoolean) && $scope.inputVO[key] < originIns[key]) {
    	  var msg = '';
	      if(key === 'insPolicyAmt') {msg = '保障型保險分期繳保費';} 
	      else if(key === 'insSavAmt') {msg = '儲蓄型保險分期繳保費';} 
	      else if(key === 'loanExpenses') {msg = '應繳貸款費用';}
	      
	      $scope.showErrorMsg('填入的金額小於系統預設之「未來一年預期' + msg + $filter('number')(originIns[key]) + '」，請重新填寫。');
	      $scope.inputVO[key] = originIns[key];
	      return false;
      }

      $scope.inputVO[key] = parseInt(Number($scope.inputVO[key] || 0), 10);
      // hasPopHint = 0;
      $scope.inputVO.cash = Number($scope.inputVO.needExpenses) + Number($scope.inputVO.loanExpenses) + Number($scope.inputVO.otherExpenses);
      $scope.inputVO.ins = Number($scope.inputVO.insPolicyAmt) + Number($scope.inputVO.insSavAmt);
      var sum1 = Number($scope.inputVO.mfdProd) + Number($scope.inputVO.etfProd) + Number($scope.inputVO.insProd) + Number($scope.inputVO.nanoProd); //2020-1-30 by Jacky 新增奈米投
      var sum2 = Number($scope.inputVO.bondProd) + Number($scope.inputVO.snProd)+ Number($scope.inputVO.siProd);
      $scope.inputVO.price = Number($scope.inputVO.deposit) + Number($scope.inputVO.sowAmt) + sum1 + sum2; //(hasIns);
      $scope.cost = Number($scope.inputVO.cash) + Number($scope.inputVO.ins) + Number($scope.inputVO.cashPrepare);
      $scope.total = Number($scope.inputVO.price) - $scope.cost;
      return true;
    };

    // check cash if it < min limit
    $scope.checkCashAmt = function (fn, key) {
      if ($scope.inputVO.cash !== Number($scope.inputVO.needExpenses) + Number($scope.inputVO.loanExpenses) + Number($scope.inputVO.otherExpenses)) {
    	var temp = {
    			houseLoan:$scope.cashVO.houseLoan,
    			creditLoan:$scope.cashVO.creditLoan,
    			stdLoan:$scope.cashVO.stdLoan,	
    			payForHouse:$scope.cashVO.payForHouse,
    			payForCar:$scope.cashVO.payForCar,
				study:$scope.cashVO.study,
				travel:$scope.cashVO.travel,
				other:$scope.cashVO.other,
				loanExpenses:$scope.inputVO.cash - ($scope.inputVO.needExpenses + $scope.inputVO.otherExpenses),
				otherExpenses:$scope.inputVO.cash - ( $scope.inputVO.needExpenses + $scope.inputVO.loanExpenses)
    	}  
    	if(fn(key) && key !== 'needExpenses') {
    		var isConfirm = $confirm({
    			text: '修改後，現金部位相對應欄位資料將會被刪除'
    		})
    		.then(function () {
//    			if (!checkLoanAmt()) {
//    				return false;
//    			}
    			
    			if(key == 'loanExpenses') {
    				$scope.cashVO.houseLoan = 0;
    				$scope.cashVO.creditLoan = 0;
    				$scope.cashVO.stdLoan = 0;            	
    			}
    			
    			if(key == 'otherExpenses') {
    				$scope.cashVO.payForHouse = 0;
    				$scope.cashVO.payForCar = 0;
    				$scope.cashVO.study = 0;
    				$scope.cashVO.travel = 0;
    				$scope.cashVO.other = 0;            	
    			}
    			$scope.inputVO.cash = Number($scope.inputVO.needExpenses) + Number($scope.inputVO.loanExpenses) + Number($scope.inputVO.otherExpenses);
    			
//            $scope.inputVO.needExpenses = 0;
//            $scope.inputVO.loanExpenses = 0;
//            $scope.inputVO.otherExpenses = 0;
//            $scope.cashVO.stdLoan = 0;
//            cntCashOther = 0;
    			countCashOther();
    		}, function () {
    			if(key == 'loanExpenses') {
    				$scope.cashVO.houseLoan = temp.houseLoan;
    				$scope.cashVO.creditLoan = temp.creditLoan;
    				$scope.cashVO.stdLoan = temp.stdLoan; 
    				$scope.inputVO.loanExpenses = temp.loanExpenses;            	
    			}
    			
    			if(key == 'otherExpenses') {
    				$scope.cashVO.payForHouse = temp.payForHouse;
    				$scope.cashVO.payForCar = temp.payForCar;
    				$scope.cashVO.study = temp.study;
    				$scope.cashVO.travel = temp.travel;
    				$scope.cashVO.other = temp.other;  
    				$scope.inputVO.otherExpenses = temp.otherExpenses;           	
    			}
    			$scope.inputVO.cash = Number($scope.inputVO.needExpenses) + Number($scope.inputVO.loanExpenses) + Number($scope.inputVO.otherExpenses);
    		});
    	}
      } else {
        if (!checkLoanAmt()) {
          return false;
        }
        fn(key);
      }
    };

    // check loan
    var checkLoanAmt = function () {
      if (originLoan.houseLoan + originLoan.creditLoan + originLoan.stdLoan > $scope.inputVO.cash) {
        // if ($scope.cashVO.houseLoan + $scope.cashVO.creditLoan + $scope.cashVO.stdLoan > $scope.inputVO.cash) {
        $scope.showErrorMsg('填入的金額小於貸款支出(年)，請提高填寫金額');
        $scope.inputVO.cash = cntCash;
        return false;
      }
      return true;
    };

    var save = function (step) {
      $scope.inputVO.planAmt = $scope.total;
      // $scope.inputVO.cash = Number($scope.inputVO.cash);
      // $scope.inputVO.ins = Number($scope.inputVO.insPolicyAmt) + Number($scope.inputVO.insSavAmt);
      // $scope.inputVO.price = Number($scope.inputVO.deposit) + Number($scope.inputVO.sowAmt) +
      //   // hasIns
      //   Number($scope.inputVO.annuityProd) + Number($scope.inputVO.fixedProd) + Number($scope.inputVO.fundProd);

      $scope.inputVO.newFlag = $scope.newFlag;
      $scope.inputVO.newPlan = $scope.newPlan;
      $scope.inputVO.custID = $scope.connector('get', 'custID');

      // 未來一年所需現金 cash
      $scope.inputVO.essCash = $scope.inputVO.needExpenses;
      $scope.inputVO.houseLoan = $scope.cashVO.houseLoan;
      $scope.inputVO.creditLoan = $scope.cashVO.creditLoan;
      $scope.inputVO.stdLoan = $scope.cashVO.stdLoan;
      
      $scope.inputVO.payForHouse = $scope.cashVO.payForHouse;
      $scope.inputVO.payForCar = $scope.cashVO.payForCar;
      $scope.inputVO.study = $scope.cashVO.study;
      $scope.inputVO.travel = $scope.cashVO.travel;
      $scope.inputVO.other = $scope.cashVO.other;
      $scope.inputVO.custRisk = $scope.connector('get', 'custInfo').KYC_LEVEL;
      
      // 調整後金額 STEP1同 建議(首作) or 調整前(再投資)
      $scope.inputVO.portfolio2Ratio = suggestPct[0];
      $scope.inputVO.portfolio3Ratio = suggestPct[1];
      $scope.inputVO.portfolio2Amt = Math.round($scope.inputVO.planAmt * suggestPct[0] /100) || 0;
      $scope.inputVO.portfolio3Amt = Math.round($scope.inputVO.planAmt * suggestPct[1] /100) || 0;

      console.log($scope.inputVO);

      $scope.inputVO.step = step;
      var defer = $q.defer();
      if (!$scope.FPS210Form.$invalid || $scope.FPS210Form.$dirty || !$scope.inputVO.planID || $scope.inputVO.step) {
        $scope.inputVO.isDirty = checkB4save();
        console.log($scope.inputVO.isDirty);
        // step
        $scope.sendRecv('FPS210', 'save', 'com.systex.jbranch.app.server.fps.fps210.FPS210InputVO',
          $scope.inputVO,
          function (tota, isError) {
            if (!isError) {
              $scope.inputVO.planID = tota[0].body;
              $scope.inputVO.action = 'update';
              defer.resolve(tota[0].body);
              return true;
            }
            defer.reject(tota);
          });
      } else {
        $timeout(function () {
          defer.resolve(planID);
        });
      }
      return defer.promise;
    };

    var checkB4save = function () {
      var isDirty =
        $scope.inputVO.deposit !== ($scope.paramList.DEPOSIT_AMT || 0) ||
        $scope.inputVO.sowAmt !== ($scope.paramList.SOW_AMT || 0) ||
        // INS_YEAR_AMT_1(new) INS_POLICY_AMT(inv)
        $scope.inputVO.insPolicyAmt !== ($scope.paramList.INS_POLICY_AMT || 0) ||
        // INS_YEAR_AMT_2(new) INS_SAV_AMT(inv)
        $scope.inputVO.insSavAmt !== ($scope.paramList.INS_SAV_AMT || 0) ||
        $scope.inputVO.cash !== ($scope.paramList.CASH_YEAR_AMT || 0);
      return isDirty;
    };

    $scope.go = function (where) {
      switch (where) {
        case 'save':
          save().then(function () {
            $scope.showSuccessMsg('ehl_01_common_025');
          });
          break;
      }
    };

    $scope.changeStep = function (hasGoThroughSign) {
      // 檢核
      if ($scope.inputVO.price - $scope.cost <= 0) {
        $scope.showErrorMsg('客戶無可供規劃金額，請提醒客戶匯入行外資金方可執行理財規劃。');
        return false;
      }

      if ($scope.inputVO.price - $scope.cost < minPriceAmt * 10000) {
        $scope.showMsg('客戶可供規劃金額低於新台幣' + $filter('number')(minPriceAmt) + '萬元，建議客戶匯入行外資金，當可供規劃金額大於新台幣' + minPriceAmt + '萬元時，即可執行理財規劃。');
        return false;
      }

      // if ($scope.hasIns && hasPopHint <= 0) {
      if ($scope.hasIns &&
        (Number($scope.inputVO.deposit) + Number($scope.inputVO.sowAmt) - $scope.cost < 0)) {
        $scope.showMsg('客戶現金部位可能無法因應未來一年比較支出，建議請客戶匯入更多資金或是情況減持獲利商品');
        $scope.showMsg('客戶可供規劃金額偏低，建議請客戶匯入更多資金');
        return false;
      }

      // kyc
      if ($scope.kycInvalid) {
        $scope.showErrorMsg('客戶無有效KYC，無法進行理財規劃！');
        return false;
      }

      // 推介同意書
      // 檢查客戶是否填寫過推介同意書，若無，則依客戶是否符合填寫推介同意書資格，出示訊息提醒理專並提供理專列印推介同意書功能
  	  // (填寫推介同意書資格：非專投且未簽推介同意書) ==> 所以只需要檢查：“是否符合填寫推介同意書資格”
      if (!hasGoThroughSign && $scope.custInfo.CUST_PRO_FLAG !== 'Y' && $scope.custInfo.SIGN_AGMT_YN !== 'Y') {
    	  // '提醒您：此客戶尚未填寫推介同意書。建議請客戶填寫完成後，本行即可提供完整之商品投資標的規劃。
    	  // 若客戶未簽署推介同意書，則本行所提供之規畫文件將不包含個別商品標的。是否列印推介同意書?'
    	  $confirm({text: signText.valid}, {size: 'sm'}).then( function () {
    		  // 產生〝推介同意書〞PDF檔案
              printSignAgmt().then(function () {
            	  $scope.changeStep(true);
              }, function () {
            	  $scope.showErrorMsg('無法產生推介同意書');
            	  $scope.changeStep(true);
              });
    	  }, function () {
    		  $scope.changeStep(true);
          });
      } else if (!hasGoThroughSign && $scope.custInfo.CUST_PRO_FLAG === 'Y' && $scope.custInfo.SIGN_AGMT_YN !== 'Y') {
    	  // notValid: '提醒您，此客戶無簽署推介同意書，本行所提供給客戶之規劃文件將不包含個別商品標的。是否繼續規劃?'
    	  $confirm({text: signText.notValid}, {size: 'sm'}).then( function () {
    		  $scope.changeStep(true);
    	  }, function () {
    		  return false;
          });
      }
      
      save('3')
        .then(function (planID) {
          var custInfo = $scope.connector('get', 'custInfo');
          $scope.connector('set', 'planID', planID);
          $scope.connector('set', 'STEP1VO', {
            CUST_RISK: custInfo.KYC_LEVEL,
            PLAN_ID: $scope.inputVO.planID,
            PLAN_AMT: $scope.inputVO.planAmt,
            CUST_ID: $scope.inputVO.custID,
            SIGN_AGMT_YN: custInfo.SIGN_AGMT_YN || 'N'
          });
          $scope.connector('set', 'STEP2VO',{
            MODEL_FLAG: $scope.modelFlag,
            // 調整前的
            PORTFOLIO2_AMT_AFTER_ORIGIN: Math.round($scope.hasIns ? suggestPct[2] : ($scope.inputVO.planAmt * suggestPct[0] /100)) || 0,
            PORTFOLIO3_AMT_AFTER_ORIGIN: Math.round($scope.hasIns ? suggestPct[3] : ($scope.inputVO.planAmt * suggestPct[1] /100)) || 0,
            PORTFOLIO2_RATIO_AFTER_ORIGIN: suggestPct[0], // 債券
            PORTFOLIO3_RATIO_AFTER_ORIGIN: suggestPct[1],  // 股票
          	// 調整後的 從 STEP1過去 目前長一樣
          	PORTFOLIO2_AMT_AFTER: Math.round($scope.inputVO.planAmt * suggestPct[0] /100) || 0,
          	PORTFOLIO3_AMT_AFTER: Math.round($scope.inputVO.planAmt * suggestPct[1] /100) || 0,
          	PORTFOLIO2_RATIO_AFTER: suggestPct[0], // 債券
          	PORTFOLIO3_RATIO_AFTER: suggestPct[1]  // 股票
          });
          
          $scope.connector('set', 'STEP', 'STEP3');
        }, function (err) {
          console.log(err);
        });
    };

    var countCashOther = function () {
      cntCashOther =
        Number($scope.inputVO.needExpenses) +
        (Number($scope.cashVO.houseLoan) - Number(originLoan.houseLoan)) +
        (Number($scope.cashVO.creditLoan) - Number(originLoan.creditLoan)) +
        (Number($scope.cashVO.stdLoan) - Number(originLoan.stdLoan)) +
        Number($scope.cashVO.payForHouse) +
        Number($scope.cashVO.payForCar) +
        Number($scope.cashVO.study) +
        Number($scope.cashVO.travel) +
        Number($scope.cashVO.other);
    };

    var countCash = function () {
      cntCash =
        Number($scope.inputVO.needExpenses) +
        Number($scope.cashVO.houseLoan) +
        Number($scope.cashVO.creditLoan) +
        Number($scope.cashVO.stdLoan) +
        Number($scope.cashVO.payForHouse) +
        Number($scope.cashVO.payForCar) +
        Number($scope.cashVO.study) +
        Number($scope.cashVO.travel) +
        Number($scope.cashVO.other);
    };

    var printSignAgmt = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPS210', 'printSignAgmt', 'com.systex.jbranch.app.server.fps.fps210.FPS210InputVO', {
          custID: custID
        },
        function (tota, isError) {
          if (!isError) {
            defer.resolve(true);
            return true;
          }
          defer.reject(false);
          return false;
        });
      return defer.promise;
    }
    
    // 拖拉Event
    $scope.dragBarOnDrag = function (newPct) {
      var pct = 100;
      if(newPct) {
    	  pct = Number(newPct);
    	  if (!pct && pct !== 0) return false;
    	  $scope.inputVO.cashPrepare = Math.round(pct * $scope.inputVO.deposit / 100);
      }	else {
    	  if (!$scope.inputVO.cashPrepare && $scope.inputVO.cashPrepare !== 0) return false;
    	  if($scope.inputVO.cashPrepare >$scope.inputVO.deposit) $scope.inputVO.cashPrepare = $scope.inputVO.deposit;
    	  pct = Math.round($scope.inputVO.cashPrepare / $scope.inputVO.deposit * 100);
      }
      $scope.inputVO.percent = pct;
      $scope.chgAmt();
    };

    /* watcher */
    $scope.$watch('connector(\'get\', \'custID\')',
      function (newValue, oldValue) {
        console.info('fps210 newValue---=' + newValue);
        if (newValue !== undefined) {
          $scope.init();
          getCustData(newValue, planID)
            .then(() => {
              $scope.chgAmt();
              // test();
            });
        }
      });

    /** main progress */
    $scope.init();
    param()
      .then(function () {
        getCustData(custID, planID)
          .then(() => {
        	getPct();
            $scope.chgAmt();
          });
      }, function (err) {
        $scope.showErrorMsg('參數錯誤');
      });

    $scope.changeCashPrepare = function() {
    	$scope.dragBarOnDrag();
    }
  });
