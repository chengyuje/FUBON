/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM435Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter) {
		$controller('BaseController', { $scope: $scope });
		$scope.controllerName = "CRM435Controller";

		/*
		 * 初始化
		 */
		$scope.init = function() {

			//查詢條件VO
			$scope.inputVO = {
				apply_seq: '',  //議價編號
				auth_emp_id: '' //簽核者員工編號
			};
			
			
		}
		$scope.init();

		getParameter.XML(["CRM.APPLY_CAT", "CRM.AUTH_LV", "CRM.AUTH_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['APPLY_CAT'] = totas.data[totas.key.indexOf('CRM.APPLY_CAT')];	//類別 1: 期間議價  2:單次議價
				$scope.mappingSet['AUTH_LV'] = totas.data[totas.key.indexOf('CRM.AUTH_LV')];		//覆核層級 0：理專 1：第一層級主管 2：第二層級主管 3：第三層級主管 4：第四層級主管
				$scope.mappingSet['AUTH_TYPE'] = totas.data[totas.key.indexOf('CRM.AUTH_TYPE')];	//記錄型態 1：同意 2：退回 3：修改
			}
		});


		/*
		 * Function
		 */
		$scope.inquireInit = function() {
			$scope.paramList = undefined;
			$scope.outputVO = undefined;
			$scope.resultList = undefined;
		}



		$scope.inquire = function() {
			$scope.sendRecv("CRM435", "inquire", "com.systex.jbranch.app.server.fps.crm435.CRM435InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						var errMsg = tota[0].body.errMsg;
						if (errMsg) {
							$scope.showErrorMsg(errMsg);
							return;
						}
						if (tota[0].body.resultList.length) {
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
						} else {
							$scope.showMsg("ehl_01_common_009");
						}
					}
				});
			console.log("filter", $filter);
			console.log("filter i18N", $filter("i18n"));
		}

		$scope.print = function() {
			var fitVO = {
				caseCode: 1, 							//case1 下單
				custId: "T220494883",		//客戶ID
				prdType: 1,							//商品類別 : 基金
				tradeSeq: null, 	//交易序號
				tradeType: 1,							//基金交易類別 : 單筆申購
				isbBackend: null,			//是否為後收型基金(Y/N)
				isPrintSOT819: null //是否列印貸款風險預告書
			}
			$scope.sendRecv("CRM435", "print", "com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO", fitVO,
				function(tota, isError) {
					
				});
		}

		$scope.test = function() {
			debugger;
			const age = "18";
			var test = new Map();
			const result = `AGE => ${age}`;
			
			$scope.showMsg();
			
			var filter = $filter("i18n");
			console.log("filter", filter);
			
		}
	});
