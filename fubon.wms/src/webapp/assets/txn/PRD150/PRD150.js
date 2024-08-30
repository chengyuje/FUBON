/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD150Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, $q, validateService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD150Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		// combobox
		getParameter.XML(["PRD.BOND_CURRENCY","FPS.PROD_RISK_LEVEL","PRD.SI_RATE_GUARANTEEPAY", "COMMON.YES_NO", "OBU.YES_NO","PRD.SI_PROJECT","PRD.SI_CUSTOMER_LEVEL"], function(totas) {
			if (totas) {
				$scope.BOND_CURRENCY = totas.data[totas.key.indexOf('PRD.BOND_CURRENCY')];
				$scope.PROD_RISK_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
				$scope.SI_RATE_GUARANTEEPAY = totas.data[totas.key.indexOf('PRD.SI_RATE_GUARANTEEPAY')];
				$scope.COMM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.OBU_YN = totas.data[totas.key.indexOf('OBU.YES_NO')];
				$scope.PRDSI_PROJECT = totas.data[totas.key.indexOf('PRD.SI_PROJECT')];
				$scope.PRDSI_CUSTOMER_LEVEL = totas.data[totas.key.indexOf('PRD.SI_CUSTOMER_LEVEL')];
			}
		});
        //
		
		// init
		$scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
        $scope.inputVO = {};
        $scope.inputVO.type = '1';
		$scope.init = function() {
			var oritype = $scope.inputVO ? $scope.inputVO.type : ''; 
			$scope.inputVO = {};
			$scope.inputVO.type = oritype;
			$scope.inputVO.prodType='4';  //4：SI
        	$scope.inputVO.tradeType=''; 
        	$scope.inputVO.seniorAuthType='A'; //高齡評估表授權種類(S:下單、A：適配)
			// 2017/2/20 add
			if($scope.cust_id) {
				$scope.inputVO.cust_id = $scope.cust_id;
				$scope.inputVO.custID = $scope.cust_id; //PRD100.js用
			} else if($scope.is280)
				$scope.inputVO.type = '2';
			
			//是否由下單商品搜尋過來
			if($scope.fromSOTProdYN) {
				$scope.inputVO.fromSOTProdYN = $scope.fromSOTProdYN;
			} else {
				$scope.inputVO.fromSOTProdYN = "N";
			}
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		//checkCustID
		$scope.checkCustID = function() {
			$scope.inputVO.cust_id = $filter('uppercase')($scope.inputVO.cust_id);
			var validCustID = validateService.checkCustID($scope.inputVO.cust_id); //自然人和法人檢查
			if(validCustID==false){
				$scope.inputVO.cust_id='';
				return;
			}
		}
		
		$scope.getName = function() {
			var deferred = $q.defer();
			if($scope.inputVO.si_id) {
				$scope.inputVO.si_id = $scope.inputVO.si_id.toUpperCase();
				$scope.sendRecv("PRD150", "getSiName", "com.systex.jbranch.app.server.fps.prd150.PRD150InputVO", {'si_id':$scope.inputVO.si_id},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.si_name) {
									$scope.inputVO.si_name = tota[0].body.si_name;
								}
								deferred.resolve();
							}
				});
			} else
				deferred.resolve();
			return deferred.promise;
		};
		
		// inquire
		$scope.inquire = function() {
			debugger;
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			// toUpperCase
			if($scope.inputVO.cust_id) {
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
				$scope.inputVO.custID = $scope.inputVO.cust_id; //PRD100.js用
			}
			if($scope.inputVO.si_name)
				$scope.inputVO.si_name = $scope.inputVO.si_name.toUpperCase();
			if($scope.inputVO.si_id) {
				$scope.inputVO.si_id = $scope.inputVO.si_id.toUpperCase();
				// 2017/6/27 非同步
				$scope.getName().then(function(data) {
					if($scope.inputVO.fromSOTProdYN == "Y") {
						$scope.reallyInquire(); //下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
					} else {
						$scope.validSeniorCustEval(); //高齡評估量表檢核
					}
				});
			} else {
				if($scope.inputVO.fromSOTProdYN == "Y") {
					$scope.reallyInquire(); //下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
				} else {
					$scope.validSeniorCustEval(); //高齡評估量表檢核
				}
			}
		};

		//PRD100.validSeniorCustEval高齡檢核不通過清空客戶ID
		$scope.clearCustInfo = function() {
			$scope.inputVO.cust_id = "";
		};
		
		//PRD100高齡檢核後查詢
		$scope.reallyInquire = function() {
			$scope.sendRecv("PRD150", "inquire", "com.systex.jbranch.app.server.fps.prd150.PRD150InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}

	                		debugger
							$scope.totalList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;

							if($scope.inputVO.type != 1 && $scope.inputVO.type != 4){
							    return;
                            }

							$scope.inputVO.profInvestorYN = tota[0].body.fp032675DataVO.custProFlag;	//專業投資人
							$scope.inputVO.custRemarks = tota[0].body.fp032675DataVO.custRemarks;		//弱勢客戶
							$scope.inputVO.proCorpInv = tota[0].body.fp032675DataVO.proCorpInv;			//專業機構投資人
							$scope.inputVO.highYieldCorp = tota[0].body.fp032675DataVO.highYieldCorp;	//高淨值法人
							$scope.inputVO.siProCorp = tota[0].body.fp032675DataVO.siProCorp;			//衍商資格專業法人
							$scope.inputVO.pro3000 = tota[0].body.fp032675DataVO.pro3000;				//專業自然人提供3000萬財力證明(L1,J1)
							$scope.inputVO.pro1500 = tota[0].body.fp032675DataVO.pro1500;				//專業自然人提供1500萬財力證明(L2,J2)
							
							$scope.inputVO.isPrintSot816 = "N";	//是否列印結構型商品交易自主聲明書
							$scope.inputVO.isPrintSot817 = "N";	//是否列印結構型商品推介終止同意書
							
							
							//*** 推介同意簽署機制
							if ($scope.inputVO.proCorpInv == "Y" || $scope.inputVO.highYieldCorp == "Y" || $scope.inputVO.siProCorp == "Y" || $scope.inputVO.pro3000 == "Y") {
								//專業機構投資人//高淨值法人//專業法人//專業自然人提供3000萬財力證明，不檢核也不套印
							} else {							
								if (($scope.inputVO.pro1500 == "Y" && $scope.inputVO.custRemarks == "Y") ||
										($scope.inputVO.profInvestorYN != "Y" && $scope.inputVO.custRemarks == "Y")) {
									//一般客戶為特定客群，需簽署結構型商品交易自主聲明書
									$scope.showMsg("ehl_02_sot410_002");
									$scope.inputVO.isPrintSot816 = "Y";
								} else if (($scope.inputVO.pro1500 == "Y" && $scope.inputVO.custRemarks != "Y") ||
										   ($scope.inputVO.profInvestorYN != "Y" && $scope.inputVO.custRemarks != "Y") ||
										   ($scope.inputVO.siProCorp != "Y"))	
								{ 
									//查詢結構型商品推介同意註記
									$scope.sendRecv("SOT701", "getSDACTQ8Data", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {'custID':$scope.inputVO.cust_id},
										function(totaCT, isError) {
											if (!isError) {
												$scope.inputVO.isSign = totaCT[0].body.siPromDataVO.isSign;
												$scope.inputVO.signStatus = totaCT[0].body.siPromDataVO.status;
												
												debugger
												if($scope.inputVO.isSign != "Y" || $scope.inputVO.signStatus != "Y") {
													$scope.inputVO.isPrintSot816 = "Y";	//是否列印結構型商品交易自主聲明書
													$scope.inputVO.isPrintSot817 = "Y";	//是否列印結構型商品推介終止同意書
												}
												
												if($scope.inputVO.isSign == "N") {													
													$scope.showMsg("ehl_02_sot410_003");			//一般客戶未簽署結構型商品推介同意書，需簽署結構型商品交易自主聲明書												
												} else if($scope.inputVO.isSign == "Y") {
													if($scope.inputVO.signStatus == "C") {
														$scope.showMsg("ehl_02_sot410_004");		//一般客戶簽署結構型商品推介同意書已終止，需簽署結構型商品交易自主聲明書
													} else if($scope.inputVO.signStatus == "E") {
														$scope.showMsg("ehl_02_sot410_005");		//一般客戶簽署結構型商品推介同意書已失效，需簽署結構型商品交易自主聲明書
													}
												}
												
											}
									});
								}
							}
																				
							return;
						}
			});
		};
		
		$scope.detail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD150/PRD150_DETAIL.html',
				className: 'PRD150_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		//判斷是否為彈跳頁面
		$scope.jump = function(row) {
			var fitVO = {
					custId 	   		: $scope.inputVO.cust_id,  		//客戶ID
				}
			if($scope.inputVO.type == 1 ){
				$scope.sendRecv("SOT712", "identifyKYCDateAndRecord", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'custID':$scope.inputVO.cust_id},
						function(totas, isError) {
							var KYCResult = totas[0].body.KYCResult;
				 	
							if(KYCResult == 'N'){
								$scope.showErrorMsg("ehl_02_KYC310_021");
								return;
								
							}else{
								if($scope.is280)
									$scope.closeThisDialog(row);
								if($scope.isPop)
									$scope.closeThisDialog(row.PRD_ID);
								else {
									$scope.connector('set','SOTCustID', $scope.inputVO.cust_id);
									$scope.connector('set','SOTProd', {'InsuranceNo': row.PRD_ID});
									$rootScope.menuItemInfo.url = "assets/txn/SOT410/SOT410.html";
								}
							}
						});
			}else{
				if($scope.is280)
					$scope.closeThisDialog(row);
				if($scope.isPop)
					$scope.closeThisDialog(row.PRD_ID);
				else {
					$scope.connector('set','SOTProd', {'InsuranceNo': row.PRD_ID});
					$rootScope.menuItemInfo.url = "assets/txn/SOT410/SOT410.html";
				}
			}
			
		};
		
		$scope.save = function(row) {
			if (row.warningMsg) {
				//適配有警告訊息
				var dialog = ngDialog.open({
					template: 'assets/txn/CONFIRM/CONFIRM.html',
					className: 'CONFIRM',
					showClose: false,
					scope : $scope,
					controller: ['$scope', function($scope) {
						$scope.dialogLabel = row.warningMsg;
		            }]
				}).closePromise.then(function (data) {
					if (data.value === 'successful') {
						$scope.saveData(row);
					} else {
						return;
					}
				});
			} else {
				$scope.saveData(row);
			}			
		};
		
		$scope.saveData = function(row) {
			$scope.isPrintSOT819 = 'Y';
			if($scope.cust_id && $scope.cust_id.length >= 8 && $scope.cust_id.length < 10) {
				$scope.isPrintSOT819 = 'N';
			}
			//適配資訊
			var fitVO = {
				caseCode   		: 2, 					  		//case2 適配
				custId 	   		: $scope.inputVO.cust_id,  		//客戶ID
				prdId      		: row.PRD_ID,              		//商品代碼
				riskLevel  		: row.RISKCATE_ID,	      		//商品P值
				prdType    		: 'SI',					  		//商品類別 : SI
				prdName	   		: row.SI_CNAME,			  		//商品名稱
				isPrintSot816	: $scope.inputVO.isPrintSot816,	//是否列印結構型商品交易自主聲明書
				isPrintSot817	: $scope.inputVO.isPrintSot817,	//是否列印結構型商品推介終止同意書
				hnwcBuy	  		: row.HNWC_BUY,			 		//限高資產申購註記
				isPrintSOT819   	: $scope.isPrintSOT819  //印貸款風險預告書
			}
			
			$scope.sendRecv("SOT712", "identifyKYCDateAndRecord", "com.systex.jbranch.app.server.fps.sot712.SOT712InputVO", {'custID':$scope.inputVO.cust_id},
					function(totas, isError) {
						
					 	var KYCResult = totas[0].body.KYCResult;
					 	
						if(KYCResult == 'N'){

							$scope.showErrorMsg("ehl_02_KYC310_021");
							return;
							
						}else{
							$scope.sendRecv("SOT712", "saveFitInfo", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
									function(totas, isError) {
										//取得回傳的錯誤訊息
										var errMsg = totas[0].body;
									
										if (errMsg) {
											$scope.showErrorMsg(errMsg);
											return;
										} else {
											$scope.showSuccessMsg('ehl_01_common_025'); //儲存成功
										}
					   					
					   					//fitToGetPDF
										//配合下單模組，將產品類別轉為數字 ，SI不套表
					   					fitVO.prdType = '4';
						   				$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
						   					function(totas, isError) {
						   						if (isError) {
						   							$scope.showErrorMsgInDialog(totas.body.msgData);
						   							return;
						   						} 
						   				});
								});
						}
				});
			
		};
});