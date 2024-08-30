/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD120Controller',
	function($rootScope, $scope, $controller,$filter, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD120Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		// combobox
		getParameter.XML(["FPS.PROD_RISK_LEVEL", "PRD.ETF_CURRENCY", "COMMON.YES_NO", "PRD.ETF_CUSTOMER_LEVEL", "PRD.ETF_PROJECT"], function(totas) {
			if (totas) {
				$scope.PROD_RISK_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
				$scope.ETF_CURRENCY = totas.data[totas.key.indexOf('PRD.ETF_CURRENCY')];
				$scope.COMMONYN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.ETF_CUSTOMER_LEVEL = totas.data[totas.key.indexOf('PRD.ETF_CUSTOMER_LEVEL')];
				$scope.ETF_PROJECT = totas.data[totas.key.indexOf('PRD.ETF_PROJECT')];
			}
		});
		$scope.sendRecv("PRD120", "getCombo", "com.systex.jbranch.app.server.fps.prd120.PRD120InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.countryList = tota[0].body.countryList;
						$scope.tacticsList = tota[0].body.tacticsList;
						$scope.investList = tota[0].body.investList;
						$scope.companyList = tota[0].body.companyList;
						$scope.stockList = tota[0].body.stockList;
						$scope.industryList = tota[0].body.industryList;
						return;
					}
		});
		//
		
		if($scope.cust_id){
			if( $filter('uppercase')($scope.txnId)=='CRM'){
				$scope.funName='議價';
			}else{
				$scope.funName='下單';
			}
		}else{
			$scope.funName='下單';
		}
		
		$scope.setPtype = function(data) {
			if(data == '1')
				$scope.PTYPE = "ETF";
			else if (data == '2')
				$scope.PTYPE = "STOCK";
		};
		$scope.setPtype('1');
		
		// init
		$scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		$scope.inputVO = {};
        $scope.inputVO.type = '1';
        
		$scope.init = function() {
			var oritype = $scope.inputVO ? $scope.inputVO.type : ''; 
			$scope.inputVO = {};
			$scope.inputVO.type = oritype;
			$scope.inputVO.prodType='2';  //2：海外ETF/股票
        	$scope.inputVO.tradeType=''; 
        	$scope.inputVO.seniorAuthType='A'; //高齡評估表授權種類(S:下單、A：適配)
			// 2017/2/18 add
			if($scope.cust_id) {
				$scope.inputVO.cust_id = $scope.cust_id;
				$scope.inputVO.custID = $scope.cust_id; //PRD100.js用
				$scope.setPtype($scope.pType);
			} else if($scope.is240) {
				$scope.inputVO.type = '2';
				$scope.setPtype($scope.pType);
			}
			
			//是否由下單商品搜尋過來
			if($scope.fromSOTProdYN) {
				$scope.inputVO.fromSOTProdYN = $scope.fromSOTProdYN;
				$scope.inputVO.trustTS = $scope.trustTS;
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
		
		// inquire
		$scope.inquire_etf = function() {
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			if($scope.inputVO.etf_code)
				$scope.inputVO.etf_code = $scope.inputVO.etf_code.toUpperCase();
			if($scope.inputVO.etf_name)
				$scope.inputVO.etf_name = $scope.inputVO.etf_name.toUpperCase();
			if($scope.inputVO.stock_code)
				$scope.inputVO.stock_code = $scope.inputVO.stock_code.toUpperCase();
			if($scope.inputVO.stock_name)
				$scope.inputVO.stock_name = $scope.inputVO.stock_name.toUpperCase();
			if($scope.stock_bond_type)
				$scope.inputVO.stock_bond_type = $scope.stock_bond_type;
			$scope.sendRecv("PRD120", "inquire_etf", "com.systex.jbranch.app.server.fps.prd120.PRD120InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.totalList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		
		$scope.inquire_stock = function(){
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			if($scope.inputVO.stock_code)
				$scope.inputVO.stock_code = $scope.inputVO.stock_code.toUpperCase();
			$scope.sendRecv("PRD120", "inquire_stock", "com.systex.jbranch.app.server.fps.prd120.PRD120InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.totalList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		
		// inquire
		$scope.inquire = function(){
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			
			if($scope.inputVO.fromSOTProdYN == "Y") {
				$scope.reallyInquire(); //下單只需要輸入客戶ID時檢核，商品搜尋時不需再次檢核
			} else {
				if($scope.inputVO.cust_id) {
					$scope.inputVO.custID = $scope.inputVO.cust_id; //PRD100.js用
				}
				$scope.validSeniorCustEval(); //先做高齡評估量表檢核
			}
		};
		
		//PRD100.validSeniorCustEval高齡檢核不通過清空客戶ID
		$scope.clearCustInfo = function() {
			$scope.inputVO.cust_id = "";
		};
		
		//PRD100高齡檢核後查詢
		$scope.reallyInquire = function() {
			if($scope.PTYPE == "ETF") {
				$scope.inquire_etf();
			} else {
				$scope.inquire_stock();
			}
		}
		
		$scope.goDownload = function(row) {
			var ptype = $scope.PTYPE;
			var name = "";
			if(ptype == "ETF")
				name = row.ETF_CNAME;
			else
				name = row.STOCK_CNAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD120/PRDDocument.html',
				className: 'PRDDocument',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.PRD_ID = row.PRD_ID;
                	$scope.PRD_NAME = name;
                	$scope.PTYPE = ptype;
                	$scope.SUBSYSTEM_TYPE = "PRD";
//                	$scope.DOC_TYPE = "XXX";
                }]
			});
		};
		
		$scope.jump = function(row){
			if($scope.isPop) {
				$scope.closeThisDialog(row);
			} else {
				$scope.connector('set','SOTCustID', $scope.inputVO.cust_id);
				$scope.connector('set','SOTProd', {'InsuranceNo': row.PRD_ID});
				$rootScope.menuItemInfo.url = "assets/txn/SOT210/SOT210.html";
			}
		};
		
		$scope.save = function(row) {
			$scope.isPrintSOT819 = 'Y';
			if($scope.cust_id && $scope.cust_id.length >= 8 && $scope.cust_id.length < 10) {
				$scope.isPrintSOT819 = 'N';
			}
			//適配資訊
			var fitVO = {
				caseCode  : 2, 					   	 //case2 適配
				custId    : $scope.inputVO.cust_id,  //客戶ID
				prdId     : row.PRD_ID,              //商品代碼
				riskLevel : row.RISKCATE_ID,	     //商品P值
				prdType   : $scope.PTYPE,            //商品類別 : ETF or STOCK
				prdName   : row.ETF_CNAME,			 //商品名稱
				isPrintSOT819   	: $scope.isPrintSOT819  //印貸款風險預告書
			}
			
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
   					fitVO.prdType = '2';
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
});