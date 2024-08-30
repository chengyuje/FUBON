/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM870Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM870Controller";
	
	//最新幣值查詢
	$scope.Currency = [];
	$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", {}, function(tota, isError) {
		if (!isError) {
			$scope.Currency = tota[0].body.resultList;
		}	
	});
	
	$scope.setValue = function () {
		angular.forEach($scope.mappingSet['secCustData'], function(row, index, objs){
			if(row.ACCT_NBR == $scope.inputVO.ACCT_NBR){
	        	$scope.inputVO.RISK_LEVEL        = row.RISK_LEVEL;
	        	$scope.inputVO.RISK_UPD_DATE     = row.RISK_UPD_DATE;
	        	$scope.inputVO.PI_IND            = row.PI_IND;
	        	$scope.inputVO.PI_EXPIRY_DATE    = row.PI_EXPIRY_DATE;
	        	$scope.inputVO.TO_BANK_ACCT_NBR  = row.TO_BANK_ACCT_NBR;
	        	$scope.inputVO.FO_BANK_ACCT_NBR  = row.FO_BANK_ACCT_NBR;
	        	$scope.inputVO.BANK_ACCT_NBR     = row.BANK_ACCT_NBR;
	        	$scope.inputVO.CROSS_SELLING_IND = row.CROSS_SELLING_IND;
			}
		});
	}
	
	$scope.setValueDSN = function () {
		angular.forEach($scope.mappingSet['secCustDataDSN'], function(row, index, objs){
			if(row.ACCT_NBR_DSN == $scope.inputVO.ACCT_NBR_DSN){
	        	$scope.inputVO.KYC_M_DATE       = row.KYC_M_DATE;
	        	$scope.inputVO.SALES_BRANCH_NBR = row.SALES_BRANCH_NBR;
	        	$scope.inputVO.SALES_BRANCH_NAME= row.SALES_BRANCH_NAME;
			}
		});
	}
	
	$scope.getSecCustData = function () {
		$scope.sendRecv("CRM681", "getSecCustData", "com.systex.jbranch.app.server.fps.crm681.CRM681InputVO", {'cust_id': $scope.custVO.CUST_ID}, function(tota, isError) {
			if (isError) {
				return;
			} else {
				if (tota.length > 0) {
					$scope.mappingSet['secCustData'] = tota[0].body.secCustData;
					$scope.mappingSet['secCustDataDSN'] = tota[0].body.secCustDataDSN;
					
					if (tota[0].body.secCustData.length >= 1) {
						$scope.inputVO.ACCT_NBR = tota[0].body.secCustData[0].ACCT_NBR;
					}
					
					if (tota[0].body.secCustDataDSN.length >= 1) {
						$scope.inputVO.ACCT_NBR_DSN = tota[0].body.secCustDataDSN[0].ACCT_NBR_DSN;
					}
					
					if (tota[0].body.secCustData.length == 1) {
						$scope.disabledFlag = true;
					}
					
					if (tota[0].body.secCustDataDSN.length == 1) {
						$scope.disabledFlagDSN = true;
					}
					
					if (tota[0].body.secCustDataDSN.length == 0) {
						$scope.comboboxFlag = true;
					}
				}
			}	
		});
	}

	//初始化
	$scope.init = function() {
		$scope.$emit("CRM610VO", {action:"get", type:"CRM681_Sec", data: function(d){
			$scope.sec = d;
		}});
		$scope.connector("set", "CRM681_Sec", []);
		
    	// 證券-海外股票
    	$scope.crm871AUM = 0;
    	$scope.crm871INVEST_AUM = 0;
    	$scope.crm871BENEFIT_AMT1 = 0;
    	$scope.crm871BENEFIT_AMT2 = 0;
    	
    	// 證券-海外債
    	$scope.crm872AUM = 0;
    	$scope.crm872INVEST_AUM = 0;
    	$scope.crm872BENEFIT_AMT1 = 0;
    	$scope.crm872BENEFIT_AMT2 = 0;
    	
    	// 證券-境外結構型商品
    	$scope.crm873AUM = 0;
    	$scope.crm873INVEST_AUM = 0;
    	$scope.crm873BENEFIT_AMT1 = 0;
    	$scope.crm873BENEFIT_AMT2 = 0;
    	
    	// 證券-境內結構型商品
    	$scope.crm874AUM = 0;
    	$scope.crm874INVEST_AUM = 0;
    	$scope.crm874BENEFIT_AMT1 = 0;
    	$scope.crm874BENEFIT_AMT2 = 0;
    	
    	// 總計(約當台幣)
		$scope.total = 0;  
		
		if(!isNaN($scope.sec.crm871AUM)) 			$scope.crm871AUM 			= $scope.sec.crm871AUM;
		if(!isNaN($scope.sec.crm871INVEST_AUM)) 	$scope.crm871INVEST_AUM 	= $scope.sec.crm871INVEST_AUM;
		if(!isNaN($scope.sec.crm871BENEFIT_AMT1)) 	$scope.crm871BENEFIT_AMT1 	= $scope.sec.crm871BENEFIT_AMT1;
		if(!isNaN($scope.sec.crm871BENEFIT_AMT2)) 	$scope.crm871BENEFIT_AMT2 	= $scope.sec.crm871BENEFIT_AMT2;
		
		if(!isNaN($scope.sec.crm872AUM)) 			$scope.crm872AUM 			= $scope.sec.crm872AUM;
		if(!isNaN($scope.sec.crm872INVEST_AUM)) 	$scope.crm872INVEST_AUM 	= $scope.sec.crm872INVEST_AUM;
		if(!isNaN($scope.sec.crm872BENEFIT_AMT1)) 	$scope.crm872BENEFIT_AMT1 	= $scope.sec.crm872BENEFIT_AMT1;
		if(!isNaN($scope.sec.crm872BENEFIT_AMT2)) 	$scope.crm872BENEFIT_AMT2 	= $scope.sec.crm872BENEFIT_AMT2;
		
		if(!isNaN($scope.sec.crm873AUM)) 			$scope.crm873AUM 			= $scope.sec.crm873AUM;
		if(!isNaN($scope.sec.crm873INVEST_AUM)) 	$scope.crm873INVEST_AUM 	= $scope.sec.crm873INVEST_AUM;
		if(!isNaN($scope.sec.crm873BENEFIT_AMT1)) 	$scope.crm873BENEFIT_AMT1 	= $scope.sec.crm873BENEFIT_AMT1;
		if(!isNaN($scope.sec.crm873BENEFIT_AMT2)) 	$scope.crm873BENEFIT_AMT2 	= $scope.sec.crm873BENEFIT_AMT2;
		
		if(!isNaN($scope.sec.crm874AUM)) 			$scope.crm874AUM 			= $scope.sec.crm874AUM;
		if(!isNaN($scope.sec.crm874INVEST_AUM)) 	$scope.crm874INVEST_AUM 	= $scope.sec.crm874INVEST_AUM;
		if(!isNaN($scope.sec.crm874BENEFIT_AMT1)) 	$scope.crm874BENEFIT_AMT1 	= $scope.sec.crm874BENEFIT_AMT1;
		if(!isNaN($scope.sec.crm874BENEFIT_AMT2)) 	$scope.crm874BENEFIT_AMT2 	= $scope.sec.crm874BENEFIT_AMT2;
		
		if(!isNaN($scope.sec.total)) $scope.total  = $scope.sec.total;
		
		$scope.disabledFlag = false;
		$scope.disabledFlagDSN = false;
		$scope.comboboxFlagDSN = false;
		$scope.mappingSet['secCustData'] = [];
		$scope.mappingSet['secCustDataDSN'] = [];
		
		$scope.inputVO = {
			ACCT_NBR : '',
			RISK_LEVEL : '',
			RISK_UPD_DATE : '',
			PI_IND : '',
			PI_EXPIRY_DATE : '',
			TO_BANK_ACCT_NBR : '',
			FO_BANK_ACCT_NBR : '',
			BANK_ACCT_NBR : '',
			CROSS_SELLING_IND : '', 
			
			ACCT_NBR_DSN : '',
			KYC_M_DATE : '',
			SALES_BRANCH_NBR : '',
			SALES_BRANCH_NAME : ''
		}
		
		$scope.getSecCustData();
	};
	
	$scope.init();
	
	//畫圖參數設定
	$scope.options = {
		chart: {
			type: 'pieChart',
			height: 450,
			width : 450,
			x: function(d){return d.key;},
			y: function(d){return d.y;},
			showLabels: true,
			duration: 300,
			labelThreshold: 0.01,
			labelSunbeamLayout: false,
			labelType: "fubon",
			donutLabelsOutside: true,
			legend: {
				margin: {
					top: 4,
					right: 0,
					bottom: 0,
                	left: 0 
                },
				align: true
			},
			noData : '查無資料'
		}
	};
	
	if ($scope.crm871AUM == 0 && $scope.crm872AUM == 0 && $scope.crm873AUM == 0 && $scope.crm874AUM == 0) {
		$scope.data = [];
	} else {
		//繪圖資料
		$scope.data = [{key: "海外股票", y: $scope.crm871AUM}, 
                       {key: "海外債券", y: $scope.crm872AUM},
                       {key: "境外結構型", y: $scope.crm873AUM}, 
                       {key: "境內結構型", y: $scope.crm874AUM}];
	}
		
	$scope.detail = function(index){
		// SET PATH
		var path = "assets/txn/CRM870/CRM870_DETAIL.html";
		$scope.connector("set", "CRM610URL", path);
		$scope.$emit("CRM610VO", {action: "set", type: "URL", data: path});
		
		// SET INDEX
		$scope.connector("set", "CRM870_TAB", index);
		
		// SET SOMETHING
		$scope.$emit("CRM610VO", {action: "set", type: "CRM681_Sec", data: $scope.sec});
        $scope.connector("set", "CRM681_Sec", $scope.sec);
	}
		
});