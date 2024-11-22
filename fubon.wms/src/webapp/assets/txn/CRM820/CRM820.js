/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM820Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM820Controller";
		
		//最新幣值查詢
		$scope.Currency = [];
		$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.Currency = tota[0].body.resultList;
					}	
		});

		//初始化
		$scope.chkExecute = $scope.chkExecute === true ? $scope.chkExecute : false;
		$scope.init = function() {
			if($scope.chkExcute)return;
			$scope.chkExecute = true;
			$scope.$emit("CRM610VO", {action:"get", type:"CRM681_Investment", data: function(d){
				$scope.investment = d;
			}});
//			$scope.investment = $scope.connector("get", "CRM681_Investment");
			$scope.connector("set", "CRM681_Investment", []);
//			$scope.investment = $scope.$parent.$parent.CRM610VO.CRM681_Investment; // 取代connector, 避免多開下互相影響.
			
			$scope.crm821 = 0; //"基金"
			$scope.crm822 = 0; //"海外ETF/海外股票"
			$scope.crm823 = 0; //"海外債"
			$scope.crm824 = 0; //"組合式商品(SI)"
			$scope.crm825 = 0; //境外結構型商品(SN)
			$scope.crm826 = 0; //外匯雙享利(DCI)
			$scope.crm827 = 0; //金錢信託
			$scope.crm828 = 0; //黃金存摺
			$scope.crm829 = 0; //奈米投
			$scope.crm82A = 0; //金市海外債
			$scope.crm82B = 0; //金市SN債
			$scope.other = 0;  //其他
			$scope.total = 0;  //投資總計(約當台幣)
			
			if(!isNaN($scope.investment.crm821)) $scope.crm821 = $scope.investment.crm821;
			if(!isNaN($scope.investment.crm822)) $scope.crm822 = $scope.investment.crm822;
			if(!isNaN($scope.investment.crm823)) $scope.crm823 = $scope.investment.crm823;
			if(!isNaN($scope.investment.crm824)) $scope.crm824 = $scope.investment.crm824;
			if(!isNaN($scope.investment.crm825)) $scope.crm825 = $scope.investment.crm825;
			if(!isNaN($scope.investment.crm826)) $scope.crm826 = $scope.investment.crm826;
			if(!isNaN($scope.investment.crm827)) $scope.crm827 = $scope.investment.crm827;
			if(!isNaN($scope.investment.crm828)) $scope.crm828 = $scope.investment.crm828;
			if(!isNaN($scope.investment.crm829)) $scope.crm829 = $scope.investment.crm829;
			if(!isNaN($scope.investment.crm82A)) $scope.crm82A = $scope.investment.crm82A;
			if(!isNaN($scope.investment.crm82B)) $scope.crm82B = $scope.investment.crm82B;
			if(!isNaN($scope.investment.other))  $scope.other  = $scope.investment.other;
			if(!isNaN($scope.investment.total)) $scope.total  = $scope.investment.total;
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
		
		if ($scope.crm821 == 0 && $scope.crm822 == 0 && $scope.crm823 == 0 && $scope.crm824 == 0 && $scope.crm825 == 0 
				&& $scope.crm826 == 0 && $scope.crm827 == 0 && $scope.crm828 == 0 && $scope.crm829 == 0 && $scope.crm82A == 0 && $scope.crm82B == 0 
				&& $scope.other == 0) {
			$scope.data = [];
		} else {
			//繪圖資料
			$scope.data = [
	                         {
	                             key: "基金",
	                             y: $scope.crm821
	                         },
	                         {
	                             key: "海外ETF/海外股票",
	                             y: $scope.crm822
	                         },
	                         {
	                             key: "海外債",
	                             y: $scope.crm823
	                         },
	                         {
	                             key: "海外債-金市",
	                             y: $scope.crm82A
	                         },
	                         {
	                             key: "組合式商品(SI)",
	                             y: $scope.crm824
	                         },
	                         {
	                             key: "境外結構型商品(SN)",
	                             y: $scope.crm825
	                         },
	                         {
	                             key: "境外結構型債券-金市",
	                             y: $scope.crm82B
	                         },
	                         {
	                             key: "外匯雙享利(DCI)",
	                             y: $scope.crm826
	                         },
	                         {
	                             key: "金錢信託",
	                             y: $scope.crm827
	                         },
	                         {
	                             key: "黃金存摺",
	                             y: $scope.crm828
	                         },
	                         {
	                             key: "奈米投",
	                             y: $scope.crm829
	                         },
	                         {
	                             key: "其它",
	                             y: $scope.other
	                         }
	                     ];
		}
			
		$scope.detail = function(index){
			var index = index-1 ;
			var set = $scope.connector("set","CRM820_TAB",index);
			var path = "assets/txn/CRM820/CRM820_DETAIL.html";
			$scope.connector("set","CRM610URL",path);
			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
		}
		
		// 2017/11/2
		$scope.goCRM860 = function() {
			var temp_vo = $scope.custVO;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM860/CRM860.html',
				className: 'CRM860',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.custVO = temp_vo;
                }]
			});
		};

});