'use strict';
eSoafApp.controller('SOT515Controller',
    function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "SOT515Controller";

        // 特金 SN 繼承
        $controller('SOT510Controller', {$scope: $scope});

        // 參數取得
        $scope.getParam = function () {
            var deferred = $q.defer();

            getParameter.XML(['SOT.TRUST_ACCT'], function(totas) {
                if(len(totas) > 0) {
                    $scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'] = totas.data[totas.key.indexOf('SOT.TRUST_ACCT')];

                    deferred.resolve("success");
                } else {
                    deferred.resolve("");
                }
            });

            return deferred.promise;
        };

        // 信託帳號
        $scope.setTrustAcct = function () {
            $scope.inputVO.trustAcct = $scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'][1].DATA;
        }

        // 扣款帳號 + 收益入帳帳號
        $scope.getACC = function(enterTime) {

            if (enterTime == 'default' || (enterTime == 'change' && $scope.interFlag)) {
                $scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];
                $scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];

                if ($scope.inputVO.contractID != '') {
                    angular.forEach($scope.mappingSet['SOT.CONTRACT_LIST'], function(contractRow){
                        if ($scope.inputVO.contractID == contractRow.DATA) {
                        	$scope.needclear = false;
                        	if(enterTime == 'change' || ($scope.lockFlagByContractID == true && enterTime == 'default')) {
                        		//0000275: 金錢信託受監護受輔助宣告交易控管調整
                                if(contractRow.GUARDIANSHIP_FLAG == '1'){
                                    $scope.showErrorMsg("ehl_02_SOT_014");
                                    $scope.needclear = true;
                                }
                                
                                //#0864: 金錢信託不受理55歲以上受益人申購SN 該契約編號只有一筆的情況
                                if(contractRow.CREDIT_FLAG == 'Y'){
                                    $scope.showErrorMsg("ehl_01_SOT515_001");
                                    $scope.needclear = true;
                                }
                                
                                // #1324: 金錢信託高齡套表規則新增
    							if(contractRow.CONTRACT_SPE_FLAG === '03' && contractRow.CONTRACT_P_TYPE === '58'){
    								$scope.showErrorMsg("ehl_01_SOT_M_001");
    								$scope.needclear = true;
    							}
    							
    							if($scope.needclear) {
                                    $scope.clearCustForM();
                                    $scope.inputVO.custID = "";
    							}
                        	}
                            
                          

                            if(contractRow.CONTRACT_SPE_FLAG.substring(0,2) == '01' || contractRow.LABEL.substring(0,9) == '250100000'){
                                $scope.showMsg("ehl_02_SOT_998");
                            }

                            $scope.inputVO.GUARDIANSHIP_FLAG = contractRow.GUARDIANSHIP_FLAG;

                            $scope.acctCurrencyByM = contractRow.CUR;
                            if ($scope.checkAccCurrency($scope.acctCurrencyByM)) {
                                $scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'].push({LABEL: contractRow.ACC, DATA: contractRow.ACC});
                                $scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'].push({LABEL: contractRow.ACC, DATA: contractRow.ACC});

                                $scope.inputVO.trustPeopNum = contractRow.FLAG;

                                if ($scope.inputVO.trustPeopNum == 'Y') {
                                    var txtMsg = $filter('i18n')('ehl_02_SOT_999');

                                    $confirm({text: txtMsg, ok: '繼續', cancel: '結束'})
                                        .then(function (e) {
                                        }).catch(function (e) {
                                        $scope.inputVO.custID = '';
                                        $scope.clearCustForM();

                                        $scope.inputVO.prodID = '';
                                        $scope.prodClear();
                                        return;
                                    });
                                }
                            } else {
                                $scope.inputVO.contractID = '';
                            }
                        }
                    });

                    if($scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'].length == 1){ // 只有一筆不能勾選
                        $scope.inputVO.debitAcct = $scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'][0].DATA;
                        $scope.cmbDebitAcctByTrust = true;
                    }

                    if($scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'].length == 1){ // 只有一筆不能勾選
                        $scope.inputVO.creditAcct = $scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'][0].DATA;
                        $scope.cmbCreditAcctByTrust = true;
                    }
                }
            }

            // 只有一筆不能勾選，協助設定預設值時，會觸發ng-change導致confirm跳兩次，以interFlag判斷是否執行
            if (enterTime == 'change' && !$scope.interFlag) {
                $scope.interFlag = true;
            }
            
            $scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
			//比較系統日與契約迄日
			if ($scope.inputVO.contractID != '') {
				angular.forEach($scope.mappingSet['SOT.CONTRACT_LIST'], function(contractRow){ 
					if ($scope.inputVO.contractID == contractRow.DATA) {
							if($scope.toJsDate(contractRow.CONTRACT_END_DAY) < $scope.toJsDate($scope.toDay)){
								$scope.showErrorMsgInDialog("契約迄日已過期");
					        	$scope.inputVO.contractID = '';  		//清空契約編號
					        	return false;
							}
						}
					});
				}
        };

        $scope.noCallCustQueryByM = function() {
            var deferred = $q.defer();
            debugger

            // 取得契約
            $scope.inputVO.trustPeopNum = 'N';								// 是否為多委託人契約 Y=是/N=否
            $scope.mappingSet['SOT.CONTRACT_LIST'] = [];
            $scope.sendRecv("SOT315", "getCustANDContractList", "com.systex.jbranch.app.server.fps.sot315.SOT315InputVO", {'custID':$scope.inputVO.custID}, function(tota, isError) {
                if (!isError) {
                    $scope.mappingSet['SOT.CONTRACT_LIST'] = tota[0].body.contractList;
                    $scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];
                    $scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];

//                    if($scope.mappingSet['SOT.CONTRACT_LIST'].length == 1){ //只有一筆不能勾選
//                        $scope.inputVO.contractID = $scope.mappingSet['SOT.CONTRACT_LIST'][0].DATA;
//                        $scope.lockFlagByContractID = true;
//                    } else if ($scope.mappingSet['SOT.CONTRACT_LIST'].length < 1){ //只有一筆不能勾選
//                        $scope.lockFlagByContractID = true;
//                    } else {
//                        $scope.interFlag = true;
//                        $scope.lockFlagByContractID = false;
//                    }

                    // 取消自動選擇契約的邏輯，允許契約皆可選
                    //避免契約迄日到期直接帶入
		            $scope.lockFlagByContractID = false; 
		            $scope.interFlag = $scope.mappingSet['SOT.CONTRACT_LIST'].length > 0; 
		            
		            
                    deferred.resolve("success");
                }
            });

            return deferred.promise;
        }

        // 新的交易
        $scope.getCustANDContractList = function() {				// 若為新交易，要取得客戶契約編號時...
            initialize();

            $scope.getParam().then(function() {							// 取得參數
                $scope.getSOTCustInfo().then(function(data) {		    // STEP 1. 取得客戶基本資料
                    $scope.interFlag = false;
                    $scope.noCallCustQueryByM().then(function() { 		// STEP 2. 取得契約編號
                        $scope.getACC('default');				// STEP 3. 設定扣款帳號/收益入帳帳號
                        $scope.setTrustAcct();							// STEP 4. 設定信託帳號
                    });
                });
            });
        };

        function initialize() {
            $scope.mappingSet['SOT.CONTRACT_LIST'] = [];				// 契約編號列表
            $scope.mappingSet['SOT.DEBIT_ACCT_LIST#TRUST'] = [];		// 扣款帳號
            $scope.mappingSet['SOT.CREDIT_ACCT_LIST#TRUST'] = [];		// 收益入帳帳號
            $scope.mappingSet['SOT.TRUST_ACCT_LIST#TRUST'] = [];		// 信託帳號

            $scope.cmbDebitAcctByTrust = true;
            $scope.cmbCreditAcctByTrust = true;
            $scope.inputVO.trustTS = 'M';								// S=特金/M=金錢信託
            $scope.inputVO.trustPeopNum = 'N';							// 是否為多委託人契約 Y=是/N=否
            $scope.lockFlagByContractID = true;
        }

        // 進入畫面時...
        $scope.SOT515init = function() {
            initialize();

            debugger;
            if ($scope.inputVO.tradeSEQ) { 								// 若由SOT610整合交易進入畫面時...
                $scope.getParam().then(function() {						// 取得參數
                    debugger;
                    $scope.noCallCustQuery().then(function() { 			// STEP 1. 取得客戶基本資料
                        debugger;
                        $scope.interFlag = false;
                        $scope.noCallCustQueryByM().then(function() { 	// STEP 2. 取得契約編號
                            $scope.getACC('default');			// STEP 3. 設定扣款帳號/收益入帳帳號
                            $scope.setTrustAcct();						// STEP 4. 設定信託帳號
                        });
                    });
                });
            }
        }

        $scope.SOT515init();
        
        //金錢信託的客戶資訊清空
        $scope.clearCustForM = function(){
        	$scope.clearCust();
        	$scope.mappingSet['SOT.CONTRACT_LIST'] = [];				// 契約編號列表
        	$scope.inputVO.contractID = ''; //契約編號
        	
        };
    });