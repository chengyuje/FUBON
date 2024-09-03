/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD110Controller',
	function($rootScope, $scope, $controller, $confirm,$filter, socketService, ngDialog, projInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD110Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		// combobox
		getParameter.XML(["FPS.PROD_RISK_LEVEL", "FPS.CURRENCY", "FPS.FUND_TYPE", "FPS.DIVIDEND_FREQUENCY", "PRD.FUND_HW_TYPE", "PRD.FUND_SUBJECT", "PRD.FUND_PROJECT","PRD.FUND_CUSTOMER_LEVEL"], function(totas) {
			if (totas) {
				$scope.PROD_RISK_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
				$scope.CURRENCY = totas.data[totas.key.indexOf('FPS.CURRENCY')];
				$scope.FUND_TYPE = totas.data[totas.key.indexOf('FPS.FUND_TYPE')];
				$scope.DIVIDEND_FREQUENCY = totas.data[totas.key.indexOf('FPS.DIVIDEND_FREQUENCY')];
				$scope.FUND_HW_TYPE = totas.data[totas.key.indexOf('PRD.FUND_HW_TYPE')];
				$scope.FUND_SUBJECT = totas.data[totas.key.indexOf('PRD.FUND_SUBJECT')];
				$scope.FUND_PROJECT = totas.data[totas.key.indexOf('PRD.FUND_PROJECT')];
				$scope.FUND_CUSTOMER_LEVEL = totas.data[totas.key.indexOf('PRD.FUND_CUSTOMER_LEVEL')];
			}
		});
        
        $scope.sendRecv("PRD110", "getCompany", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.TRUST_COM = [];
						angular.forEach(tota[0].body.resultList, function(row) {
							$scope.TRUST_COM.push({LABEL: row.FUND_COMPANY_NAME, DATA: row.FUND_COMPANY_ID});
	        			});
					}
		});
        //
		
		// init
        $scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
        $scope.inputVO = {};
        $scope.inputVO.type = '1';
		$scope.init = function() {
			var oritype = $scope.inputVO.type; 
			$scope.inputVO = {};
			$scope.inputVO.type = oritype;
			$scope.inputVO.prodType='1';  //1：基金
        	$scope.inputVO.tradeType=''; 
        	$scope.inputVO.seniorAuthType='A'; //高齡評估表授權種類(S:下單、A：適配)
			// 2017/2/18 add
			if($scope.cust_id) {
				// for ocean
				$scope.inputVO.cust_id = $scope.cust_id;
				$scope.inputVO.custID = $scope.cust_id; //PRD100.js用
				$scope.inputVO.tradeType = $scope.tradeType || '1';
				$scope.inputVO.main_prd = $scope.main_prd || '';
				$scope.inputVO.sameSerialYN = $scope.sameSerialYN;
				$scope.inputVO.sameSerialProdId = $scope.sameSerialProdId;
				$scope.inputVO.dynamicProdCurrM = $scope.dynamicProdCurrM;
				$scope.inputVO.dynamicType = $scope.dynamicType;
				if($scope.isBackend != undefined)
					$scope.inputVO.isBackend = $scope.isBackend;
			} else if($scope.is910){
				$scope.inputVO.type = '2';
				$scope.inputVO.stock_bond_type = $scope.stock_bond_type;				
			}
			
			$scope.mappingSet['OVSPRI_PRD_CATEGORY'] = [];
			$scope.mappingSet['OVSPRI_PRD_CATEGORY'].push({LABEL: "申購", DATA: "1"});
			$scope.mappingSet['OVSPRI_PRD_CATEGORY'].push({LABEL: "贖回", DATA: "2"});
			
			//是否由下單商品搜尋過來
			if($scope.fromSOTProdYN) {
				$scope.inputVO.fromSOTProdYN = $scope.fromSOTProdYN;
				$scope.inputVO.trustTS = $scope.trustTS;
			} else {
				$scope.inputVO.fromSOTProdYN = "N";
			}
			$scope.inputVO.fromPRD111YN = $scope.fromPRD111YN; //是否由動態鎖利適配過來
			$scope.inputVO.sortType = "1"; //商品代碼排序 1:ASC 2:DESC
			$scope.inputVO.column = "";
		};
		$scope.init();
		
		$scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.totalList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		// sot
		$scope.inputVO.showOvsPri = true;
		if($scope.cust_id) {
			if( $filter('uppercase')($scope.txnId)=='CRM'){
				$scope.funName='議價';
				$scope.inputVO.showOvsPri = false;
			}else{
				$scope.funName='下單';
			}
		}else{
			$scope.funName='下單';
		}
			
		
		$scope.getName = function() {
			var deferred = $q.defer();
			if($scope.inputVO.fund_id) {
				$scope.inputVO.fund_id = $scope.inputVO.fund_id.toUpperCase();
				$scope.sendRecv("PRD110", "getFundName", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {'fund_id':$scope.inputVO.fund_id},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.fund_name) {
									$scope.inputVO.fund_name = tota[0].body.fund_name;
								}
								deferred.resolve();
							}
				});
			} else
				deferred.resolve();
			return deferred.promise;
		};
		
		$scope.getArea = function() {
			if($scope.inputVO.fund_type) {
				$scope.inputVO.inv_target = "";
				$scope.mappingSet['INV_TARGET'] = [];
				$scope.sendRecv("PRD110", "getArea", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['INV_AREA'] = [];
								angular.forEach(tota[0].body.resultList, function(row) {
									$scope.mappingSet['INV_AREA'].push({LABEL: row.PARAM_NAME, DATA: row.NEXT_TIER_VALUE});
			        			});
								$scope.inputVO.inv_area = "";
								return;
							}
				});
			} else {
				$scope.inputVO.inv_area = "";
				$scope.inputVO.inv_target = "";
				$scope.mappingSet['INV_AREA'] = [];
				$scope.mappingSet['INV_TARGET'] = [];
			}
		};
		
		$scope.getTarget = function() {
			if($scope.inputVO.inv_area) {
				$scope.sendRecv("PRD110", "getTarget", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.mappingSet['INV_TARGET'] = [];
								angular.forEach(tota[0].body.resultList, function(row) {
									$scope.mappingSet['INV_TARGET'].push({LABEL: row.PARAM_NAME, DATA: row.NEXT_TIER_VALUE});
			        			});
								$scope.inputVO.inv_target = "";
								return;
							}
				});
			} else {
				$scope.inputVO.inv_target = "";
				$scope.mappingSet['INV_TARGET'] = [];
			}
		};
		
		$scope.clearRoi = function(){
			if(!$scope.inputVO.roi_dt)
				$scope.inputVO.roi = '';
		};
		
		// inquire
		$scope.inquire = function() {
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			// toUpperCase
			if($scope.inputVO.cust_id) {
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
				$scope.inputVO.custID = $scope.inputVO.cust_id; //PRD100.js用
			}
			if($scope.inputVO.fund_name)
				$scope.inputVO.fund_name = $scope.inputVO.fund_name.toUpperCase();
			if($scope.inputVO.fund_id) {
				$scope.inputVO.fund_id = $scope.inputVO.fund_id.toUpperCase();
				$scope.getName().then(function(data) {
					if($scope.inputVO.fromSOTProdYN == "Y") {
						$scope.reallyInquire("N"); //下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
					} else {
						$scope.validSeniorCustEval(); //高齡評估量表檢核
					}
				});
			} else {
				if($scope.inputVO.fromSOTProdYN == "Y") {
					$scope.reallyInquire("N"); //下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
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
		$scope.reallyInquire = function(sortYN) {
			debugger
			$scope.inquireInit();
			$scope.inputVO.column = "";
			$scope.sendRecv("PRD110", "inquire", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.totalList = tota[0].body.resultList;
							if(sortYN == "Y") {
								//以商品代碼排序
								if($scope.inputVO.sortType == "1") { //ASC
									$scope.totalList.sort(function (a, b) {
										return (a.PRD_ID < b.PRD_ID) ? -1 : 1;
									});
									$scope.inputVO.sortType = "2"; //下一次 DESC
								} else {
									$scope.totalList.sort(function (a, b) {
										return (a.PRD_ID > b.PRD_ID) ? -1 : 1;
									});
									$scope.inputVO.sortType = "1"; //下一次 ASC
								}
							}
							$scope.outputVO = tota[0].body;
							
							return;
						}
			});
		};
		
		$scope.detail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD110/PRD110_DETAIL.html',
				className: 'PRD110_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		$scope.openOvsPri = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD110/PRD110_OVSPRI.html',
				className: 'PRD110_OVSPRI',
				showClose: false,
                controller: ['$scope', function($scope) {
                }]
			});
		}
		
		$scope.jump = function(row) {
			// for ocean
			if($scope.isPop) {
				$scope.closeThisDialog(row);
			} else {
				$scope.connector('set','SOTCustID', $scope.inputVO.cust_id);
				$scope.connector('set','SOTProd', {'FundNO': row.PRD_ID});
				$rootScope.menuItemInfo.url = "assets/txn/SOT110/SOT110.html";
			}
		};
		
		$scope.save = function(row) {
			debugger
			//適配資訊
			var fitVO = {
				caseCode  : 2, 					     //case2 適配
				custId    : $scope.inputVO.cust_id,  //客戶ID
				prdId     : row.PRD_ID,              //商品代碼
				riskLevel : row.RISKCATE_ID,	     //商品P值
				prdType	  : "1",                     //商品類別 : 1:基金
				prdName	  : row.BOND_CNAME,			 //商品名稱
				hnwcBuy	  : row.OVS_PRIVATE_YN,		 //境外私募基金註記
				isPrintSOT819   	: $scope.isPrintSOT819  //印貸款風險預告書
			}
			
			$scope.inputVO.hmshacrDataVO = null;
			if(row.OVS_PRIVATE_YN == "Y") { //境外私募基金註記
				//先檢核集中度
				$scope.sendRecv("SOT712", "getCentRateData", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
					function(totas, isError) {
						if (!isError) {
							debugger
							if(totas[0].body.hmshacrDataVO) {
								$scope.inputVO.hmshacrDataVO = totas[0].body.hmshacrDataVO;
								
								if(totas[0].body.hmshacrDataVO.VALIDATE_YN == "N") {
									$scope.showErrorMsg("客戶高風險商品集中度比例已超過上限");
									return;
								} else if(totas[0].body.hmshacrDataVO.VALIDATE_YN == "W") {
									var dialog = ngDialog.open({
										template: 'assets/txn/CONFIRM/CONFIRM.html',
										className: 'CONFIRM',
										showClose: false,
										scope : $scope,
										controller: ['$scope', function($scope) {
											$scope.dialogLabel = "客戶高風險商品集中度已超過通知門檻比例，請取得客戶同意，若為弱勢客戶，須請處(副)主管核准\n\n是否繼續";
							            }]
									}).closePromise.then(function (data) {
										if (data.value === 'successful') {
											$scope.doSave(row);
										}
									});
								} else { //totas[0].body.hmshacrDataVO.VALIDATE_YN == "Y"
									$scope.doSave(row);
								}
							} else {
								$scope.doSave(row);
							}
						}
				});
			} else { //商品沒有境外私募基金註記
				$scope.doSave(row);
			}
		}
		
		$scope.doSave = function(row) {
			$scope.isPrintSOT819 = 'Y';
			if($scope.cust_id && $scope.cust_id.length >= 8 && $scope.cust_id.length < 10) {
				$scope.isPrintSOT819 = 'N';
			}
			//適配資訊
			var fitVO = {
				caseCode 	: 2, 					   //case2 適配
				custId		: $scope.inputVO.cust_id,  //客戶ID
				prdId  	  	: row.PRD_ID,              //商品代碼
				riskLevel 	: row.RISKCATE_ID,	       //商品P值
				prdType  	: 'MFD',   				   //商品類型 : MFD
				prdName  	: row.FUND_CNAME,		   //商品名稱
				currency   	: $scope.CURRENCY.filter((e)=>e.DATA == row.CURRENCY_STD_ID)[0].DATA,  //幣別
				isPrintSOT819   	: $scope.isPrintSOT819,  //印貸款風險預告書
				hnwcBuy	  : row.OVS_PRIVATE_YN,		 //境外私募基金註記
				hmshacrDataVO : $scope.inputVO.hmshacrDataVO, //集中度資訊
			}
			
			$scope.getCustInfo(row.PRD_ID).then(function() {
				if ($scope.msgObj != null) {
					$scope.openMSG();
				}
			});
			
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
					
   					//配合下單模組，將產品類別轉為數字
   					fitVO.prdType = '1';
   					//fitToGetPDF
   					$scope.sendRecv("SOT712", "fitToGetPDF", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
   						function(totas, isError) {
   				    		if (isError) {
   				    			$scope.showErrorMsgInDialog(totas.body.msgData);
   			   					return;
   			   				} 
   					});
			});
		} 
		
		//#1865_商品主檔篩選錄音提醒
		$scope.msgObj = null;
	
		$scope.getCustInfo = function(prdId) {
			var deferred = $q.defer();
			$scope.sendRecv("PRD110", "getCustInfo", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", { cust_id: $scope.inputVO.cust_id, prod_type: $scope.inputVO.prodType, fund_id: prdId },
				function(totas, isError) {
					if (!isError) {
						var recNeeded = totas[0].body.isRecNeeded;
						if (recNeeded) {
							$scope.msgObj = {
								isFirstTrade: totas[0].body.isFirstTrade,
								specialCust: totas[0].body.specialCust
							};
						}
						deferred.resolve("success");
					}
				});
			return deferred.promise;
		}
		
		$scope.openMSG = function() {
			var obj = $scope.msgObj;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD110/PRD110_MSG.html',
				className: 'PRD110',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.msgObj = obj;
				}]
			}).closePromise.then(function(data) {

			});
		}
});