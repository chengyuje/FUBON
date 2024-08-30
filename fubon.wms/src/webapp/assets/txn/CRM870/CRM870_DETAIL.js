 'use strict';
eSoafApp.controller('CRM870_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM870_DETAILController";
		
		//最新幣值查詢
		$scope.Currency = [];
		$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", {}, function(tota, isError) {
			if (!isError) {
				$scope.Currency = tota[0].body.resultList;
			}	
		});

		$scope.goCRM870HIS =  function() {
			var temp_vo = $scope.custVO;
			var tabSheet = $scope.tabSheet;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM870/CRM870_HIS.html',
				className: 'CRM870',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.custVO = temp_vo;
                	$scope.tabSheet = tabSheet;
                }]
			});
		};
		
		$scope.pagechose = function(tabSheet){
			$scope.tabSheet = tabSheet;
			$scope.data = [];  //查詢用
			$scope.resultList = [];  //查詢用
			$scope.outputVO = [];
			
        	$scope.sendRecv("CRM870", "getAST_INV_SEC", "com.systex.jbranch.app.server.fps.crm870.CRM870InputVO", {"custID": $scope.inputVO.cust_id, "tabSheet" : $scope.tabSheet}, function(tota, isError) {
        		if (!isError) {
        			if(tota[0].body.resultList.length == 0) {
        				$scope.resultList = [];   //若查無資料，就將結果清空。
            			return;
        			}
        			$scope.outputVO = tota[0].body;
        			$scope.resultList = tota[0].body.resultList;

        			return;       			
        		}      		
        	});
		}

		//初始化
		$scope.init = function() {
			$scope.data = [];  			//查詢用
			$scope.resultList = [];  	//查詢用
			
			$scope.tabSheet = 1;
			if($scope.connector('get', 'CRM870_TAB') != undefined){
				$scope.tabSheet = $scope.connector('get','CRM870_TAB');
			}
			
			$scope.pagechose($scope.tabSheet);
			
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
			
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
		};
		
		$scope.init();

		$scope.detail = function(tabSheet, row) {
			switch (tabSheet) {
				case 2: // 海外股票
				case 3:	// 海外債券
					break;
				case 4: // 境外結構型商品
					var dialog = ngDialog.open({
						template: 'assets/txn/PRD312/PRD312_DETAIL.html',
						className: 'PRD312_DETAIL',
						showClose: false,
		                controller: ['$scope', function($scope) {
		                	$scope.row = row;
		                }]
					});
					break;
				case 5: // 境內結構型商品
					var dialog = ngDialog.open({
						template: 'assets/txn/PRD313/PRD313_DETAIL.html',
						className: 'PRD313_DETAIL',
						showClose: false,
		                controller: ['$scope', function($scope) {
		                	$scope.row = row;
		                }]
					});
					break;
			}
		};
});