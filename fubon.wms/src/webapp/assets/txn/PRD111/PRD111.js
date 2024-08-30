/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD111Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q , validateService, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD111Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		        		
		$scope.custClear = function() {
			$scope.inputVO.custName = '';
			$scope.inputVO.kycLV = '';									//KYC等級
			
			$scope.inputVO.kycDueDate = undefined;						//KYC效期
			$scope.inputVO.profInvestorYN = '';							//是否為專業投資人
			$scope.inputVO.piDueDate = undefined;						//專業投資人效期
			$scope.inputVO.custRemarks = '';							//客戶註記
			$scope.inputVO.isOBU = '';									//是否為OBU客戶
			$scope.inputVO.isAgreeProdAdv = '';							//同意投資商品諮詢服務
			$scope.inputVO.piRemark = '';                               //專業投資人註記
			$scope.inputVO.custProType = '';                            //專業投資人類型1：大專投 2：小專投
			$scope.inputVO.isFirstTrade = '';                            //是否首購 
			$scope.inputVO.hnwcYN = '';
			$scope.inputVO.hnwcServiceYN = '';
        };
        
      //母基金資料
		$scope.setProdDataM = function(data) {
			$scope.inputVO.prodId = data.prodDTL[0].PRD_ID;	//母基金只有商品代碼不一樣，其他帶一般基金商品變數名稱
			$scope.inputVO.prodName = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurr = data.prodDTL[0].CURRENCY_STD_ID;					//計價幣別
			$scope.inputVO.prodRiskLv = data.prodDTL[0].RISKCATE_ID;
			$scope.showFitnessMessage(data.fitnessMessage);
		}
		
		//子基金資料1
		$scope.setProdDataC1 = function(data) {
			$scope.inputVO.prodIdC1 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC1 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC1 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC1 = data.prodDTL[0].RISKCATE_ID;
		}
		
		//子基金資料2
		$scope.setProdDataC2 = function(data) {
			$scope.inputVO.prodIdC2 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC2 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC2 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC2 = data.prodDTL[0].RISKCATE_ID;	
		}
		
		//子基金資料3
		$scope.setProdDataC3 = function(data) {
			$scope.inputVO.prodIdC3 = data.prodDTL[0].PRD_ID;
			$scope.inputVO.prodNameC3 = data.prodDTL[0].FUND_CNAME;						//商品名稱
			$scope.inputVO.prodCurrC3 = data.prodDTL[0].CURRENCY_STD_ID;				//計價幣別
			$scope.inputVO.prodRiskLvC3 = data.prodDTL[0].RISKCATE_ID;
		}
		
		//清空商品資料
		$scope.prodClearD = function(type, idx, isClearPID) {
			if(type == "M") {
				$scope.prodClearM(isClearPID);
			} else {
				if(idx == "1") $scope.prodClearC1(isClearPID);
				if(idx == "2") $scope.prodClearC2(isClearPID);
				if(idx == "3") $scope.prodClearC3(isClearPID);
			}
		}
		
		//清空母基金商品資料
		$scope.prodClearM = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodId = ''; //也要清空產品編號
			}
	       	$scope.inputVO.prodName = '';     //基金名稱
			$scope.inputVO.prodCurr = '';     //計價幣別
			$scope.inputVO.prodRiskLv = '';   //產品風險等級
			
			//清空子基金
			$scope.prodClearC1(true);
			$scope.prodClearC2(true);
			$scope.prodClearC3(true);
		}
		
		//清空子基金1商品資料
		$scope.prodClearC1 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodIdC1 = ''; //也要清空產品編號
			}
			$scope.inputVO.prodNameC1 = '';     //基金名稱
			$scope.inputVO.prodCurrC1 = '';     //計價幣別
			$scope.inputVO.prodRiskLvC1 = '';   //產品風險等級
		}
		
		//清空子基金2商品資料
		$scope.prodClearC2 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodIdC2 = ''; //也要清空產品編號
			}
			$scope.inputVO.prodNameC2 = '';     //基金名稱
			$scope.inputVO.prodCurrC2 = '';     //計價幣別
			$scope.inputVO.prodRiskLvC2 = '';   //產品風險等級
		}
		
		//清空子基金3商品資料
		$scope.prodClearC3 = function(isClearProdId) {
			if (isClearProdId) {
				$scope.inputVO.prodIdC3 = ''; //也要清空產品編號
			}
			$scope.inputVO.prodNameC3 = '';     //基金名稱
			$scope.inputVO.prodCurrC3 = '';     //計價幣別
			$scope.inputVO.prodRiskLvC3 = '';   //產品風險等級
		}
		
		$scope.validateSeniorCust = function() {
			if(!$scope.inputVO.custID) return;
			$scope.inputVO.type = "1";
			$scope.inputVO.cust_id = $scope.inputVO.custID;
			$scope.validSeniorCustEval(); //PRD100.validSeniorCustEval高齡檢核
		}
		
		//PRD100.validSeniorCustEval高齡檢核不通過清空客戶ID
		$scope.clearCustInfo = function() {
			debugger
			$scope.inputVO.custID = "";
			$scope.custClear();
		};
		
		//PRD100.validSeniorCustEval高齡檢核通過後查詢
		$scope.reallyInquire = function() {
			$scope.getSOTCustInfo(false,true);
		};
		
		// SOT701-客戶電文
		$scope.getSOTCustInfo = function(loadEdit,input) {
			debugger
			var deferred = $q.defer();
			$scope.inputVO.custID = $filter('uppercase')($scope.inputVO.custID);
			var validCustID = validateService.checkCustID($scope.inputVO.custID); //自然人和法人檢查
			if(!validCustID) $scope.inputVO.custID='';			
			if(!validCustID || input) { 
				$scope.custClear();
				$scope.prodClearM(true);
			}
			if(validCustID) {
				//動態鎖利與一般基金的取得客戶資料方式相同，prodType先不變
				$scope.sendRecv("SOT110", "getSOTCustInfo", "com.systex.jbranch.app.server.fps.sot110.SOT110InputVO", {'custID':$scope.inputVO.custID, 'prodType':1, 'tradeType':1, 'trustTS':"S"},
						function(tota, isError) {
							debugger
							if (!isError) { 
									$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
									//FOR CBS TEST日期
//								    if ($scope.toJsDate(tota[0].body.kycDueDate) < $scope.toJsDate($scope.toDay)) { //若KYC過期，需有訊息提示客戶，並清空客戶ID。
									if(tota[0].body.isKycDueDateUseful){
										 var kycDueDate = $scope.toJsDate(tota[0].body.kycDueDate);
										 var msgParam = "";
										 if(kycDueDate==null) {
											msgParam='未承作';
										 }else{
											msgParam = kycDueDate.getFullYear() + "/" +(kycDueDate.getMonth() + 1) + "/" + kycDueDate.getDate();
										 }
										 var txtMsg = $filter('i18n')('ehl_01_sot310_001') + "(" + msgParam + ")";
										 $scope.showErrorMsg(txtMsg);
										 $scope.custClear();
										 $scope.inputVO.custID = "";
										 return;
									}
								    
								    $scope.isGetSOTCustInfo = undefined;
									$scope.inputVO.custName = tota[0].body.custName;
									$scope.inputVO.kycLV = tota[0].body.kycLV;								//KYC等級
									$scope.inputVO.kycDueDate = $scope.toJsDate(tota[0].body.kycDueDate);	//KYC效期
									$scope.inputVO.profInvestorYN = tota[0].body.profInvestorYN;			//是否為專業投資人
									$scope.inputVO.piDueDate = $scope.toJsDate(tota[0].body.piDueDate);		//專業投資人效期
									$scope.inputVO.custRemarks = tota[0].body.custRemarks;					//客戶註記
									$scope.inputVO.isOBU = tota[0].body.isOBU;								//是否為OBU客戶
									$scope.inputVO.isAgreeProdAdv = tota[0].body.isAgreeProdAdv;			//同意投資商品諮詢服務
									$scope.inputVO.piRemark = tota[0].body.piRemark;			            //專業投資人註記
									$scope.inputVO.custProType = tota[0].body.custProType;                  // 專業投資人類型
									$scope.inputVO.hnwcYN = tota[0].body.hnwcYN;
									$scope.inputVO.hnwcServiceYN = tota[0].body.hnwcServiceYN;
									
									deferred.resolve("success");
									return deferred.promise;
							} else {
								//查無客戶 需清空資料
								$scope.inputVO.custID='';
								$scope.custClear();
								$scope.prodClearM(true);//清除產品
							}
				});
			}
			return deferred.promise;
		};
				
		$scope.notEmptyValue = function(value) {
			if(value == undefined || value == null || value == "")
				return false;
			else
				return true;
		}
		
		//適配結果代碼以逗號分隔
		$scope.showFitnessMessage = function (fitnessMessage) {
			if(fitnessMessage && fitnessMessage!=''){
				$scope.connector('set','fitnessMessageList', fitnessMessage); 
				ngDialog.closeAll();
	        	var dialog = ngDialog.open({
	        		template: 'assets/txn/SOT712/FitnessMessageDialog.html',
	        		className: '',
	        		controller: ['$scope',function($scope){ 
	        		}]
	        	});
	        	
	        	dialog.closePromise.then(function(data){});
			}
		};
		
		//取得動態鎖利商品資料
		$scope.getProdDTL = function(type, idx) {
			var deferred = $q.defer();
			
			//子基金檢核
			if(type == "C") {
				if($scope.inputVO.prodId == "") {
					$scope.showErrorMsg("請先選擇母基金");
					$scope.prodClearD(type, idx, true);
					return;
				} else if(idx == "2") {
					if($scope.inputVO.prodIdC1 == "") {
						$scope.showErrorMsg("請先選擇子基金1");
						$scope.prodClearD(type, idx, true);
						return;
					}
				} else if(idx == "3") {
					if($scope.inputVO.prodIdC1 == "" || $scope.inputVO.prodIdC2 == "") {
						$scope.showErrorMsg("請先選擇子基金1以及子基金2");
						$scope.prodClearD(type, idx, true);
						return;
					}
				}
			}
			
			//清空選擇的商品資料
			$scope.prodClearD(type, idx, false);
			
			var obj = eval("$scope.inputVO.prodId" + (type == "M" ? "" : type) + idx);
			//將要搜尋的母基金或子基金放到PROD_ID
			var prodId = $filter('uppercase')(obj);
		    
			debugger
			if(prodId) {
				$scope.sendRecv("PRD111", "getProdDTL", "com.systex.jbranch.app.server.fps.prd111.PRD111InputVO", 
						{"dynamicType":type, "prodId":prodId, "custID":$scope.inputVO.custID, "prodIdM":$scope.inputVO.prodId, "dynamicProdCurrM":$scope.inputVO.prodCurr},
					function(tota, isError) {
							debugger
						if (!isError) {
							if (tota[0].body.errorMsg != null && tota[0].body.errorMsg != "") {
								//適配有錯誤訊息
								$scope.showErrorMsg(tota[0].body.errorMsg);
								$scope.prodClearD(type, idx, true);
							} else if (tota[0].body.prodDTL != null) {
								if (tota[0].body.warningMsg != null && tota[0].body.warningMsg != "") {
										//適配有警告訊息
										var dialog = ngDialog.open({
										template: 'assets/txn/CONFIRM/CONFIRM.html',
										className: 'CONFIRM',
										showClose: false,
										scope : $scope,
										controller: ['$scope', function($scope) {
											$scope.dialogLabel = tota[0].body.warningMsg;
							            }]
									}).closePromise.then(function (data) {
										if (data.value === 'successful') {
										} else {
											$scope.prodClearD(type, idx, true);
											deferred.resolve("");
										}
									});
								}		
								
								if (tota[0].body.prodDTL[0]) {
									if(type == "M") {
										//母基金商品資料
										$scope.setProdDataM(tota[0].body);
									} else {
										//子基金商品資料
										if(idx == "1") $scope.setProdDataC1(tota[0].body);
										if(idx == "2") $scope.setProdDataC2(tota[0].body);
										if(idx == "3") $scope.setProdDataC3(tota[0].body);
									}
								}
								
								return;
							} else {
								$scope.showErrorMsg("ehl_01_common_009");					
								$scope.prodClearD(type, idx, true);
							}
						}
						//查無商品 需清除商品ID
						$scope.prodClearD(type, idx, true);
				});
			}
			return deferred.promise;
		};
		
		//動態鎖利商品查詢
		$scope.goPRD110D = function(type, idx) {
			var custID = $scope.inputVO.custID;
			var trustTS = $scope.inputVO.trustTS;
			var prodIdM = $scope.inputVO.prodId;
			var dynamicProdCurrM = $scope.inputVO.prodCurr;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/SOT110/SOT110_ROUTE.html',
				className: 'PRD110',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					if(type == "M") {
						$scope.txnName = "搜尋母基金";
					}else{
						$scope.txnName = "搜尋子基金";
					}
					$scope.isPop = true;
	        		$scope.routeURL = 'assets/txn/PRD110/PRD110.html';
	        		$scope.tradeType = "1";
	        		$scope.cust_id = custID;
	        		$scope.trustTS = "S";
	        		$scope.dynamicType = type; //動態鎖利類別 M:母基金 C:子基金
	        		if(type == "C") {
	        			debugger
	        			//子基金需要與母基金是同系列基金且相同幣別
	        			$scope.sameSerialYN = "Y"; 
	        			$scope.sameSerialProdId = prodIdM;
	        			$scope.dynamicProdCurrM = dynamicProdCurrM;
	        		}
	        		$scope.fromSOTProdYN = "Y"; //下單商品搜尋//下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
	        		$scope.fromPRD111YN = "Y";   //動態鎖利適配
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
					if(type == "M") {
						$scope.inputVO.prodId = data.value.PRD_ID;
					} else {
						if(idx == "1") $scope.inputVO.prodIdC1 = data.value.PRD_ID;
						if(idx == "2") $scope.inputVO.prodIdC2 = data.value.PRD_ID;
						if(idx == "3") $scope.inputVO.prodIdC3 = data.value.PRD_ID;
					}
					$scope.getProdDTL(type, idx);
				}
			});
		};	

		/**
		 * 確認基金註記 => 此交易檢查是否停止申購
		 */
		//確認基金註記 => 此交易檢查是否停止申購
		$scope.checkFundStatusM = () => $scope.sendRecv("SOT703", "qryFundMemo",
			"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.prodId},
			(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getProdDTL("M", ""));
		$scope.checkFundStatusC1 = () => $scope.sendRecv("SOT703", "qryFundMemo",
				"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.prodIdC1},
				(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getProdDTL("C", "1"));
		$scope.checkFundStatusC2 = () => $scope.sendRecv("SOT703", "qryFundMemo",
				"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.prodIdC2},
				(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getProdDTL("C", "2"));
		$scope.checkFundStatusC3 = () => $scope.sendRecv("SOT703", "qryFundMemo",
				"com.systex.jbranch.app.server.fps.sot703.SOT703InputVO", {prodId: $scope.inputVO.prodIdC3},
				(tota) => tota[0].body[0] && tota[0].body[0].NO_E_PURCHASE ? $scope.showErrorMsg(tota[0].body[0].NO_E_PURCHASE) : $scope.getProdDTL("C", "3"));

		//儲存適配
		$scope.saveFit = function() {
			debugger
			//必輸欄位檢核
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg("ehl_01_common_022");
	    		return;
	    	}
			
			$scope.inputVO.isPrintSOT819 = 'Y';
			if($scope.inputVO.custID && $scope.inputVO.custID.length >= 8 && $scope.inputVO.custID.length < 10) {
				$scope.inputVO.isPrintSOT819 = 'N';
			}
			
			//儲存適配資訊
			$scope.sendRecv("PRD111", "save", "com.systex.jbranch.app.server.fps.prd111.PRD111InputVO", $scope.inputVO,
				function(totas, isError) {
				    if (!isError) {
				    	debugger
				    	$scope.showSuccessMsg('ehl_01_common_025'); //儲存成功
						
						//適配印表
						var fitVO = {
							caseCode : 		2, 								//case2 適配
							custId   :		$scope.inputVO.custID,			//客戶ID
							custName :		$scope.inputVO.custName,
							prdId	 :		$scope.inputVO.prodId,
							prdIdC1	 :		$scope.inputVO.prodIdC1,
							prdIdC2	 :		$scope.inputVO.prodIdC2,
							prdIdC3	 :		$scope.inputVO.prodIdC3,
							prdName  :		$scope.inputVO.prodName,
							prdNameC1:		$scope.inputVO.prodNameC1,
							prdNameC2:		$scope.inputVO.prodNameC2,
							prdNameC3:		$scope.inputVO.prodNameC3,
							prdType  :      8,								//商品類別 : 8:基金動態鎖利
							isbBackend:		"N",							//是否為後收型基金(Y/N)
							isPrintSOT819:	$scope.inputVO.isPrintSOT819 	//是否列印貸款風險預告書
						}
							
						$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
								function(tota, isError) {
									if (isError) {
										$scope.showErrorMsg(totas[0].body.msgData);
									}
						});
			   		} 
			});
		}
		
		// init
        $scope.init = function() {
        	$scope.inputVO.dynamicYN = "Y";
        	$scope.inputVO.prodType='8';  //8：基金動態鎖利
        	$scope.inputVO.tradeType=''; 
        	$scope.inputVO.seniorAuthType='A'; //高齡評估表授權種類(S:下單、A：適配)
			$scope.inputVO.custID = ''; //客戶ID
			$scope.inputVO.custName = '';
			$scope.custClear(); 
			$scope.prodClearM(true);
			$scope.inputVO.hnwcYN = ''; //是否為高資產客戶 Y/N 
			$scope.inputVO.hnwcServiceYN = ''; //可提供高資產商品或服務 Y/N 
		};
		$scope.init();
		
});