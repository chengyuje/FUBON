/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT620Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT620Controller";
		
		getParameter.XML(["SOT.CENTRATE_PROD_TYPE", "SOT.CENTRATE_TRANS_TYPE", "SOT.CENTRATE_SI_STATUS", "SOT.CENTRATE_SI_TRNAS_TYPE", "SOT.CENTRATE_OVSPRI_STATUS"], function(totas) {
			if (totas) {
				//商品種類
			    $scope.mappingSet['SOT.CENTRATE_PROD_TYPE'] = totas.data[totas.key.indexOf('SOT.CENTRATE_PROD_TYPE')];
			    //交易類型
			    $scope.mappingSet['SOT.CENTRATE_TRANS_TYPE'] = totas.data[totas.key.indexOf('SOT.CENTRATE_TRANS_TYPE')];
			    //SI狀態
			    $scope.mappingSet['SOT.CENTRATE_SI_STATUS'] = totas.data[totas.key.indexOf('SOT.CENTRATE_SI_STATUS')];
			    //SI交易類型
			    $scope.mappingSet['SOT.CENTRATE_SI_TRNAS_TYPE'] = totas.data[totas.key.indexOf('SOT.CENTRATE_SI_TRNAS_TYPE')];
			    //境外私募基金狀態
			    $scope.mappingSet['SOT.CENTRATE_OVSPRI_STATUS'] = totas.data[totas.key.indexOf('SOT.CENTRATE_OVSPRI_STATUS')];
			}
		});
      	
        $scope.init = function(){
        	$scope.inputVO = {
					custID: ''
        	};
		};
		$scope.init();
        
		$scope.inquireInit = function() {
			$scope.curCRateData = {};
			$scope.centInvDataList = {};
			$scope.trialCalData = null;
			$scope.trialCalData_ORI = null;
			$scope.outputVO = {};
			$scope.addProdData = null;
		};
		$scope.inquireInit();	
		
		//檢核客戶須為高資產客戶
		//取得客戶目前集中度結果
		//取得客戶已委託高風險商品庫存
		$scope.getCustData = function() {
			$scope.inquireInit();
			
			if ($scope.inputVO.custID != undefined && $scope.inputVO.custID != null && $scope.inputVO.custID != '') {
				$scope.inputVO.custID = $scope.inputVO.custID.toUpperCase();
				$scope.sendRecv("SOT620", "getCustData", "com.systex.jbranch.app.server.fps.sot620.SOT620InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.currRateData == null && tota[0].body.centInvList == null) {
								$scope.showMsg("ehl_01_common_009");
		                		return;
		                	}
							
							//取得客戶目前集中度結果
							if(tota[0].body.currRateData != null ) {
								debugger
								var rData = tota[0].body.currRateData;
								$scope.curCRateData.DataDate = new Date();
//								$scope.curCRateData.PERCENTAGE_1 = rData.PERCENTAGE_1.toString() + "%"; //(移除)不保本境內外結構型商品
								$scope.curCRateData.PERCENTAGE_2 = rData.PERCENTAGE_2.toString() + "%"; //低信評/無信評海外債券
								$scope.curCRateData.PERCENTAGE_3 = rData.PERCENTAGE_3.toString() + "%"; //未具證投信基金性質境外基金
//								$scope.curCRateData.PERCENTAGE_4 = rData.PERCENTAGE_4.toString() + "%"; //高風險商品總計
								$scope.curCRateData.LIMIT_PERC_2 = rData.LIMIT_PERC_2.toString() + "%"; //海外債券上限比例基準百分比
								$scope.curCRateData.LIMIT_PERC_3 = rData.LIMIT_PERC_3.toString() + "%"; //未具證投信基金性質境外基金上限比例基準百分比
//								$scope.curCRateData.remark1 = "上限" + rData.BASE_PERC_1.toString() + "%，" + ((rData.PERCENTAGE_1 > rData.BASE_PERC_1) ? "已超限" : "未超限");
								$scope.curCRateData.remark2 = (rData.PERCENTAGE_2 > rData.BASE_PERC_2) ? "超過通知門檻比例" : "通知門檻比例" + rData.BASE_PERC_2.toString() + "%，未超限";
								$scope.curCRateData.remark3 = (rData.PERCENTAGE_3 > rData.BASE_PERC_3) ? "超過通知門檻比例" : "通知門檻比例" + rData.BASE_PERC_3.toString() + "%，未超限";
//								$scope.curCRateData.remark4 = "通知門檻比例" + rData.BASE_PERC_4.toString() + "%，" + ((rData.PERCENTAGE_4 > rData.BASE_PERC_4) ? "已超限" : "未超限");
//								$scope.curCRateData.remark2 = ((rData.PERCENTAGE_2 > rData.LIMIT_PERC_2) ? "超過上限比例" : ((rData.PERCENTAGE_2 > rData.BASE_PERC_2) ? "超過通知門檻比例" : "未超限"));
//								$scope.curCRateData.remark3 = ((rData.PERCENTAGE_3 > rData.LIMIT_PERC_3) ? "超過上限比例" : ((rData.PERCENTAGE_3 > rData.BASE_PERC_3) ? "超過通知門檻比例" : "未超限"));
//								$scope.curCRateData.remark4 = ((rData.PERCENTAGE_4 > rData.BASE_PERC_4) ? "超過通知門檻比例" : "未超限");
								
//								$scope.curCRateData.color1 = (rData.PERCENTAGE_1 > rData.BASE_PERC_1) ? "color:red" : "color:black";
								$scope.curCRateData.color2 = (rData.PERCENTAGE_2 > rData.BASE_PERC_2) ? "color:red" : "color:black";
								$scope.curCRateData.color3 = (rData.PERCENTAGE_3 > rData.BASE_PERC_3) ? "color:red" : "color:black";
//								$scope.curCRateData.color4 = (rData.PERCENTAGE_4 > rData.BASE_PERC_4) ? "color:red" : "color:black";
							}
							
							//取得客戶已委託高風險商品庫存
							if(tota[0].body.centInvList != null ) {
								$scope.centInvDataList = tota[0].body.centInvList;
								$scope.outputVO = tota[0].body;
							}
							
							return;
						} else {
							$scope.inputVO.custID == "";
						}
				});
			} else {
//				$scope.showErrorMsg("請輸入客戶ID");
			}
		}
		
		//新增風險商品資料
        $scope.addProd = function(row) {
        	if ($scope.inputVO.custID == undefined || $scope.inputVO.custID == null || $scope.inputVO.custID == '') {
        		$scope.showErrorMsg("請輸入客戶ID");
        		return;
        	}
        	
        	var custid = $scope.inputVO.custID;
        	var dialog = ngDialog.open({
				template: 'assets/txn/SOT620/SOT620_ADDPROD.html',
				className: 'SOT620_ADDPROD',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.custID = custid;
				}]
			});
        	dialog.closePromise.then(function(data) {
				if(data.value != undefined){
					if(data.value != 'cancel'){
						debugger;
						$scope.addProdData = null;
//						var pTypeName = ($filter('filter')($scope.mappingSet['SOT.CENTRATE_PROD_TYPE'], {DATA: data.value.prodType}))[0].LABEL;
						$scope.addProdData = {	"BUY_DATE": "", //委託日期
												"STATUS": "試算", //委託狀態
												"PROD_CAT": (data.value.prodType == '7' ? '1' : data.value.prodType), //商品類型；「其他結構型商品」= 金市SI
												"TRANS_TYPE": "1", //交易類型
												"PROD_ID": data.value.prodID, //商品代號
												"PROD_NAME": data.value.prodName, //商品名稱
												"CURR_ID": data.value.prodCurr, //計價幣別
												"AMT_ORG": data.value.trustAmt, //申購金額
												"AMT_TWD": data.value.trustAmtTWD //台幣申購金額
										  	 } 
					}
				}
			});
        }
        
        //集中度試算
        $scope.trialCalculate = function() {
        	$scope.trialCalData = null;
        	$scope.trialCalData_ORI = null;
        	debugger
        	if($scope.addProdData == null) {
        		$scope.showErrorMsg("請先新增風險商品");
        		return;
        	}
        	
        	$scope.sendRecv("SOT620", "getCurrentCRate", "com.systex.jbranch.app.server.fps.sot620.SOT620InputVO", 
        			{"custID": $scope.inputVO.custID, "addProdData":$scope.addProdData},
        		function(tota, isError) {
					if (!isError) {
						if(tota[0].body.currRateData == null) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
	                	}
						
						//取得客戶目前集中度結果
						if(tota[0].body.currRateData != null ) {
							$scope.trialCalData = {};
							$scope.trialCalData_ORI = {};
							debugger
							$scope.trialCalData_ORI = tota[0].body.currRateData;
							let rData = tota[0].body.currRateData;
							$scope.trialCalData.DataDate = new Date();
//							$scope.trialCalData.PERCENTAGE_1 = rData.PERCENTAGE_1.toString() + "%";
							$scope.trialCalData.PERCENTAGE_2 = rData.PERCENTAGE_2.toString() + "%";
							$scope.trialCalData.PERCENTAGE_3 = rData.PERCENTAGE_3.toString() + "%";
//							$scope.trialCalData.PERCENTAGE_4 = rData.PERCENTAGE_4.toString() + "%";
							$scope.trialCalData.LIMIT_PERC_2 = rData.LIMIT_PERC_2.toString() + "%"; //海外債券上限比例基準百分比
							$scope.trialCalData.LIMIT_PERC_3 = rData.LIMIT_PERC_3.toString() + "%"; //未具證投信基金性質境外基金上限比例基準百分比
//							$scope.trialCalData.remark1 = "上限" + rData.BASE_PERC_1.toString() + "%，" + ((rData.PERCENTAGE_1 > rData.BASE_PERC_1) ? "已超限" : "未超限");
							$scope.trialCalData.remark2 = (rData.PERCENTAGE_2 > rData.BASE_PERC_2) ? "超過通知門檻比例" : "通知門檻比例" + rData.BASE_PERC_2.toString() + "%，未超限";
							$scope.trialCalData.remark3 = (rData.PERCENTAGE_3 > rData.BASE_PERC_3) ? "超過通知門檻比例" : "通知門檻比例" + rData.BASE_PERC_3.toString() + "%，未超限";
//							$scope.trialCalData.remark4 = "通知門檻比例" + rData.BASE_PERC_4.toString() + "%，" + ((rData.PERCENTAGE_4 > rData.BASE_PERC_4) ? "已超限" : "未超限");
//							$scope.trialCalData.remark2 = ((rData.PERCENTAGE_2 > rData.LIMIT_PERC_2) ? "超過上限比例" : ((rData.PERCENTAGE_2 > rData.BASE_PERC_2) ? "超過通知門檻比例" : "未超限"));
//							$scope.trialCalData.remark3 = ((rData.PERCENTAGE_3 > rData.LIMIT_PERC_3) ? "超過上限比例" : ((rData.PERCENTAGE_3 > rData.BASE_PERC_3) ? "超過通知門檻比例" : "未超限"));
//							$scope.trialCalData.remark4 = ((rData.PERCENTAGE_4 > rData.BASE_PERC_4) ? "超過通知門檻比例" : "未超限");
							
//							$scope.trialCalData.color1 = (rData.PERCENTAGE_1 > rData.BASE_PERC_1) ? "color:red" : "color:black";
							$scope.trialCalData.color2 = (rData.PERCENTAGE_2 > rData.BASE_PERC_2) ? "color:red" : "color:black";
							$scope.trialCalData.color3 = (rData.PERCENTAGE_3 > rData.BASE_PERC_3) ? "color:red" : "color:black";
//							$scope.trialCalData.color4 = (rData.PERCENTAGE_4 > rData.BASE_PERC_4) ? "color:red" : "color:black";
						}
						
						return;
					}
        	});
        }
        
        //列印高資產客戶投資產品集中度聲明書
        $scope.printDoc = function() {
        	if($scope.trialCalData == null) {
        		$scope.showErrorMsg("請先進行集中度試算");
        		return;
        	}
        	
        	$scope.sendRecv("SOT620", "printReport", "com.systex.jbranch.app.server.fps.sot620.SOT620InputVO", 
        			{"trialRateData": $scope.trialCalData_ORI, "addProdData": $scope.addProdData},
        		function(totas, isError) {
    				if (isError) {
    					$scope.showErrorMsgInDialog(totas.body.msgData);
    					return;
    				} 
    		});
        }
});
