/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT151Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, $interval, getParameter, $timeout) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT151Controller";
		
		// filter
		getParameter.XML(["SOT.CUST_TYPE", "SOT.CERTIFICATE_STATUS", "SOT.CHANGE_TRADE_SUB_TYPE", "SOT.NF_CHANGE_TYPE","SOT.TRUST_CURR_TYPE","SOT.SPEC_CUSTOMER"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.CUST_TYPE'] = totas.data[totas.key.indexOf('SOT.CUST_TYPE')];
				$scope.mappingSet['SOT.CERTIFICATE_STATUS'] = totas.data[totas.key.indexOf('SOT.CERTIFICATE_STATUS')];
				$scope.mappingSet['SOT.CHANGE_TRADE_SUB_TYPE'] = totas.data[totas.key.indexOf('SOT.CHANGE_TRADE_SUB_TYPE')];
				$scope.mappingSet['SOT.NF_CHANGE_TYPE'] = totas.data[totas.key.indexOf('SOT.NF_CHANGE_TYPE')];
				
				//信託幣別
		        $scope.mappingSet['SOT.TRUST_CURR_TYPE'] = totas.data[totas.key.indexOf('SOT.TRUST_CURR_TYPE')];
		        $scope.mappingSet['SOT.SPEC_CUSTOMER'] = totas.data[totas.key.indexOf('SOT.SPEC_CUSTOMER')];//客戶註記
			}
		});
        //
        
        $scope.init = function() {
        	$scope.cartList = [];
        	
			$scope.inputVO = {
					tradeSEQ: '',
					
					custID: '', 								//客戶ID
					custName: '', 
					
					kycLV: '', 									//KYC等級
					kycDueDate: undefined, 						//KYC效期
					profInvestorYN: '', 						//是否為專業投資人
					piDueDate: undefined, 						//專業投資人效期
					custRemarks: '', 							//客戶註記
					isOBU: '', 									//是否為OBU客戶
					isAgreeProdAdv: '', 						//同意投資商品諮詢服務
					bargainDueDate: undefined,					//期間議價效期
					plNotifyWays: '',							//停損停利通知方式
					takeProfitPerc: undefined,					//停利點
					stopLossPerc: undefined,					//停損點
					debitAcct: '', 								//扣款帳號
					trustAcct: '', 								//信託帳號
					creditAcct: '',								//收益入帳帳號
					w8benEffDate: undefined,					//W8ben有效日期
					w8BenEffYN: '',
					fatcaType: '',
					custProType: '',
					noSale: '',
					
					custType: 'CUST', 							//來行人員
					agentID: '', 								//代理人ID
					agentName: '',
					
					narratorID: projInfoService.getUserID(),	//解說人員ID
					narratorName: projInfoService.getUserName(), //解說人員姓名
					goOPDisabled: false							//傳送OP按鈕，避免連續點擊
			};
		};
		$scope.init();
		        
        $scope.inputVO.tradeSEQ = $scope.connector('get', 'SOTTradeSEQ');
        $scope.connector('set', 'SOTTradeSEQ', null);
        
        $scope.noCallCustQuery = function () {
        	var deferred = $q.defer();
        	
			$scope.sendRecv("SOT150", "query", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.mainList.length == 0) {
								$scope.connector('set', "SOTTradeSEQ", null);
								$scope.connector('set', "SOTCarSEQ", null);
								$rootScope.menuItemInfo.url = "assets/txn/SOT150/SOT150.html";
								
								return;
							}
							$scope.inputVO.custID = tota[0].body.mainList[0].CUST_ID;						//客戶ID
							$scope.inputVO.custName = tota[0].body.mainList[0].CUST_NAME;	 

							$scope.inputVO.kycLV = tota[0].body.mainList[0].KYC_LV;	 						//KYC等級
							$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.mainList[0].KYC_DUE_DATE);	 			//KYC效期
							$scope.inputVO.profInvestorYN = tota[0].body.mainList[0].PROF_INVESTOR_YN;	 	//是否為專業投資人
							$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.mainList[0].PI_DUE_DATE);	 			//專業投資人效期
							$scope.inputVO.custRemarks = tota[0].body.mainList[0].CUST_REMARKS;	 			//客戶註記
							$scope.inputVO.isOBU = tota[0].body.mainList[0].IS_OBU;	 						//是否為OBU客戶
							$scope.inputVO.isAgreeProdAdv = tota[0].body.mainList[0].IS_AGREE_PROD_ADV;	 	//同意投資商品諮詢服務
							$scope.inputVO.bargainDueDate = $scope.toJsDate(tota[0].body.mainList[0].BARGAIN_DUE_DATE);		//期間議價效期
							$scope.inputVO.bargainFeeFlag = tota[0].body.mainList[0].BARGAIN_FEE_FLAG;		//議價狀態
							$scope.inputVO.tradeStatus = tota[0].body.mainList[0].TRADE_STATUS;				//交易狀態
							
							$scope.mainList = tota[0].body.mainList;
							$scope.cartList = tota[0].body.carList;
							$scope.outputVO = tota[0].body;
							
							$scope.inputVO.narratorID = tota[0].body.carList[0].NARRATOR_ID;		//解說人員ID
							$scope.inputVO.narratorName = tota[0].body.carList[0].NARRATOR_NAME;	//解說人員姓名
							
							deferred.resolve("success");
						}
			});
			
			return deferred.promise;
		};
		
		$scope.getSOTCustInfoCT = function () {
			$scope.sendRecv("SOT150", "getSOTCustInfoCT", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", {'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':4},
					function(totaCT, isError) {
						if (!isError) {
							$scope.isFirstTrade = (totaCT[0].body.isFirstTrade == "Y" ? $filter('i18n')('ehl_02_SOT_003') : ""); 
							$scope.ageUnder70Flag = (totaCT[0].body.ageUnder70Flag == "N" ? $filter('i18n')('ehl_02_SOT_004') : "");
							$scope.eduJrFlag = (totaCT[0].body.eduJrFlag == "N" ? $filter('i18n')('ehl_02_SOT_005') : ""); ;
							$scope.healthFlag = (totaCT[0].body.healthFlag == "N" ? $filter('i18n')('ehl_02_SOT_006') : ""); 
							
							if ($scope.mainList[0].IS_REC_NEEDED == 'N') {
								$scope.recSeqFlagOrg = 'N';
							} else {
//								$scope.recSeqFlagOrg = (totaCT[0].body.isFirstTrade == "Y" || totaCT[0].body.custRemarks == "Y" ? "Y" : "N");     //是否弱勢或首購客戶 控制<傳送OP交易及列印表單>與<網銀快速下單>按鈕
								$scope.recSeqFlagOrg = 'Y';
							}	
							$scope.recSeqFlag = $scope.recSeqFlagOrg;
							
							return;
						}
			});
		};
		
		$scope.enterPage = function () {
			$scope.noCallCustQuery().then(function(data) {
				$scope.getSOTCustInfoCT();
			});
		};
		$scope.enterPage();
		
		$scope.delCar = function(seqNO) {
			var txtMsg = "";
			if ($scope.cartList.length == 1) {
				txtMsg = $filter('i18n')('ehl_02_common_005');
			} else {
				txtMsg = $filter('i18n')('ehl_02_common_006');
			}
			
			$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
				$scope.sendRecv("SOT150", "delProd", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", {tradeSEQ : $scope.inputVO.tradeSEQ, carSEQ: seqNO},
						function(tota, isError) {
							if (!isError) {
								$scope.noCallCustQuery();
								
								return;
							}
				});
            });
		};
		
		$scope.goPage = function(seqNO) {
			$scope.connector('set', "SOTTradeSEQ", $scope.inputVO.tradeSEQ);
			$scope.connector('set', "SOTCarSEQ", seqNO);
			$rootScope.menuItemInfo.url = "assets/txn/SOT150/SOT150.html";
		};
		
		//TODO   檢核錄音序號 
		$scope.validateRecSeq = function() {
			if($scope.inputVO.recSEQ == ''){
				$scope.recSeqFlag = $scope.recSeqFlagOrg;
				return;
			}
			/*if($scope.inputVO.recSEQ && $scope.inputVO.recSEQ.length<9){
				$scope.showErrorMsg("錄音序號格式不符");
				return;
			}
			*/
			
			/* var SOT712InputVO = {'tellerID': $scope.carList[0].NARRATOR_ID ,'recSeq': $scope.inputVO.recSeq ,'custID': $scope.inputVO.custID ,'branchNbr': 'TODO' ,'prodType': 'NF' ,'prodID': null ,'tradeType': 1 };
			 */
			//錄音檢核除了 SI/SN 一定要給商品 PROD_ID 以外；其他 3 種，不需要給 PROD_ID~
			var sot712InputVO ={'custID':$scope.inputVO.custID,
					'prodType': 'FUND',
					'recSeq':$scope.inputVO.recSEQ
					};
			$scope.sendRecv("SOT712", "validateRecseq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO",sot712InputVO ,
			function(tota, isError) {
				if (!isError) {
					if(!tota[0].body.Recseq){
						$scope.showErrorMsg("ehl_02_common_007");
						$scope.inputVO.recSEQ='';
						$scope.recSeqFlag = $scope.recSeqFlagOrg;
					} else {
						$scope.recSeqFlag = 'N';
					}
					return;
				}
			});
		};
		
		//傳送OP交易，避免按鈕被連續點擊造成錯誤，先disable按鈕並$timeout
		$scope.goOP = function () {
			$scope.inputVO.goOPDisabled = true;
			
			$timeout(function() {
				$scope.do_goOP(); 
				$scope.inputVO.goOPDisabled = false;}
			, 1000);			
		}
		
		$scope.do_goOP = function () {
			
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			if(!$scope.inputVO.recSEQ && $scope.mainList[0].IS_REC_NEEDED == 'Y') {
				$scope.showErrorMsg("ehl_01_common_022");
				return;
			}
			
			$scope.noCallCustQuery();
//			if ($scope.inputVO.bargainFeeFlag == null || $scope.inputVO.bargainFeeFlag == '2') {
				// 檢核手續費優惠申請皆簽核完畢後，才可進行傳送OP交易及列印表單鍵
				$scope.sendRecv("SOT712", "updateBatchSeq", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'tradeType': '4', 
																													   'prodType': 'NF', 
					   																								   'tradeSeq': $scope.inputVO.tradeSEQ},
					function(tota, isError) {
						if (!isError) {
							$scope.sendRecv("SOT150", "goOP", "com.systex.jbranch.app.server.fps.sot150.SOT150InputVO", $scope.inputVO,
									function(tota, isError) {
										if (!isError) {
											if (tota[0].body.errorMsg != '' && tota[0].body.errorMsg != null) {
												$scope.showErrorMsg(tota[0].body.errorMsg);
											} else {
												
												$scope.printReport();//產生變更申請書表單
												
												$scope.showMsg("ehl_02_SOT_002");
												$scope.enterPage();
												
//												var txtMsg = $filter('i18n')('ehl_01_SOT_008');
//												$confirm({text: txtMsg}, {size: 'sm'}).then(function() {
//													$scope.connector('set', "SOTTradeSEQ", null);
//													$scope.connector('set', "SOTCarSEQ", null);
//													//不跳頁 $rootScope.menuItemInfo.url = "assets/txn/SOT150/SOT150.html";
//									            });
												
												return;
											}
										}
							});
						}
				});
//			}
		};
		
		$scope.newTrade = function() {
			$scope.connector('set', "SOTTradeSEQ", null);
			$rootScope.menuItemInfo.url = "assets/txn/SOT150/SOT150.html";
		};
		
		/**儲存格合併、加總**/
		//計算合併列數
		$scope.numGroups = function(input){
			if(input == null)
				return;
            return Object.keys(input).length;
        };
        
		//橫向加總
        $scope.getSum = function(group, key) {
        	var sum = 0;
            for (var i = 0; i < group.length; i++){            	
            	sum += _.sum(group[i][key].split(',').map(Number));
            }  
            return sum;
        };
        
        //直向加總
        $scope.getSumya = function(group, key) {
        	var sum = [];
        	var ha = 0;        	
        	for(var i = 0; i < group[0][key].split(',').map(Number).length; i++){
        		for (var j = 0; j < group.length; j++){
        			ha += group[j][key].split(',').map(Number)[i];
        		}          		
        		sum.push(ha);
        		ha = 0;
        	}        	
            return sum; 
        };
        
        //列印申請書表單
		$scope.printReport = function() {
			var fitVO = {
				caseCode : 		1, 							//case1 下單
				custId   :		$scope.inputVO.custID,		//客戶ID
				prdType  :      1,							//商品類別 : 基金
				tradeSeq : 		$scope.inputVO.tradeSEQ, 	//交易序號
				tradeType:		4							//基金交易類別 : 變更
			}
			
			$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsg(totas[0].body.msgData);
						}
			});
		}
});