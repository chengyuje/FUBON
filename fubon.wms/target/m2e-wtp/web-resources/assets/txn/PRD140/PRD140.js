/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PRD140Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, $q, validateService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PRD140Controller";
		//繼承PRD100高齡檢核
		$controller('PRD100Controller', {$scope: $scope});
		
		// combobox
		getParameter.XML(["PRD.BOND_CURRENCY", "FPS.PROD_RISK_LEVEL", "COMMON.YES_NO", "OBU.YES_NO", "PRD.SN_TYPE", "PRD.SN_PROJECT"], function(totas) {
			if (totas) {
				$scope.mappingSet['PRD.BOND_CURRENCY'] = totas.data[totas.key.indexOf('PRD.BOND_CURRENCY')];
				$scope.mappingSet['FPS.PROD_RISK_LEVEL'] = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
				$scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['OBU.YES_NO'] = totas.data[totas.key.indexOf('OBU.YES_NO')];
				$scope.mappingSet['PRD.SN_TYPE'] = totas.data[totas.key.indexOf('PRD.SN_TYPE')];
				$scope.mappingSet['PRD.SN_PROJECT'] = totas.data[totas.key.indexOf('PRD.SN_PROJECT')];
			}
		});
		
		// init
		$scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		$scope.inputVO = {};
        $scope.inputVO.type = '1';
		$scope.init = function() {
			var oritype = $scope.inputVO ? $scope.inputVO.type : ''; 
			$scope.inputVO = {};
			$scope.inputVO.type = oritype;
			$scope.inputVO.prodType='5';  //5：SN
        	$scope.inputVO.tradeType=''; 
        	$scope.inputVO.seniorAuthType='A'; //高齡評估表授權種類(S:下單、A：適配)
			// 2017/2/20 add
			if($scope.cust_id) {
				$scope.inputVO.cust_id = $scope.cust_id;
				$scope.inputVO.custID = $scope.cust_id; //PRD100.js用
			} else if($scope.is270)
				$scope.inputVO.type = '2';
			
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
			if($scope.inputVO.sn_id) {
				$scope.inputVO.sn_id = $scope.inputVO.sn_id.toUpperCase();
				$scope.sendRecv("PRD140", "getSnName", "com.systex.jbranch.app.server.fps.prd140.PRD140InputVO", {'sn_id':$scope.inputVO.sn_id},
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.sn_name) {
									$scope.inputVO.sn_name = tota[0].body.sn_name;
								}
								deferred.resolve();
							}
				});
			} else
				deferred.resolve();
			return deferred.promise;
		};
		
		// inquire
		$scope.inquire = function(){
			if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			// toUpperCase
			if($scope.inputVO.cust_id) {
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
				$scope.inputVO.custID = $scope.inputVO.cust_id; //PRD100.js用
			}
			if($scope.inputVO.sn_name)
				$scope.inputVO.sn_name = $scope.inputVO.sn_name.toUpperCase();
			if($scope.inputVO.sn_id) {
				$scope.inputVO.sn_id = $scope.inputVO.sn_id.toUpperCase();
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
			$scope.sendRecv("PRD140", "inquire", "com.systex.jbranch.app.server.fps.prd140.PRD140InputVO", $scope.inputVO,
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
		
		$scope.detail = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD140/PRD140_DETAIL.html',
				className: 'PRD140_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		//判斷是否為彈跳頁面
		$scope.jump = function(row) {
			if($scope.is270)
				$scope.closeThisDialog(row);
			if($scope.isPop)
				$scope.closeThisDialog(row.PRD_ID);
			else {
				$scope.connector('set','SOTCustID', $scope.inputVO.cust_id);
				$scope.connector('set','SOTProd', {'InsuranceNo': row.PRD_ID});
				$rootScope.menuItemInfo.url = "assets/txn/SOT510/SOT510.html";
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
				caseCode 	: 2, 					    //case2 適配
				custId		: $scope.inputVO.cust_id,	//客戶ID
				prdId      	: row.PRD_ID,             	//商品代碼
				riskLevel 	: row.RISKCATE_ID,	     	//商品P值
				prdType  	: 'SN',					 	//商品類別 : SN
				prdName	  	: row.SN_CNAME,				//商品名稱
				hnwcBuy	    : row.HNWC_BUY,			 	//限高資產申購註記
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
   					fitVO.prdType = '5';
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