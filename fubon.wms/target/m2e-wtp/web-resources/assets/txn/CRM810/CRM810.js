/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM810Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM810Controller";
		$controller('CRM681Controller', {$scope: $scope});
		
		//初始化
		$scope.chkExecute = $scope.chkExecute === true ? $scope.chkExecute : false;
		$scope.init = function(){
			//#0775
			$scope.chartHeight = 400;
			$scope.clientHeight = window.innerHeight;
//			console.log('innerHeight  ' + window.innerHeight);
			if($scope.clientHeight <= ($scope.chartHeight + 353)) {
				$scope.chartHeight = $scope.clientHeight - 353 - 50;
			}
			 

			if($scope.chkExcute)return;
			$scope.chkExecute = true;
			$scope.data = [];
			$scope.data2 = [];
//			$scope.inputVO.cust_id = 'Q121077443'; //test
//			$scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
			$scope.$emit("CRM610VO", {action:"get", type:"CRM681_CUSTDEPOSIT", data: function(d){
				$scope.custDepositVO = d;
			}});
			console.log(`============================[CRM810][init][custDepositVO][202007220228]============================\n ${JSON.stringify($scope.custDepositVO)}\n============================`);
			$scope.connector("set","CRM681_CUSTDEPOSIT",[]);
			
			//===============================資料排序===============================\\
			$scope.crm811_amt = 0;
			$scope.crm812_amt = 0;
			$scope.crm813_amt = 0;
			$scope.crm811_amt += Number($scope.custDepositVO.crm811_amt);
			$scope.crm812_amt += Number($scope.custDepositVO.crm812_amt);
			$scope.crm813_amt += Number($scope.custDepositVO.crm813_amt);
			$scope.sort_amt1 = 0;
			$scope.sort_amt2 = 0;
			$scope.sort_amt3 = 0;
			$scope.sort_name1 = '';
			$scope.sort_name2 = '';
			$scope.sort_name2 = '';
			$scope.sort_no1 = 0;
			$scope.sort_no2 = 0;
			$scope.sort_no3 = 0;
		 
			
			$scope.sort_amt1 = $scope.crm811_amt;
			$scope.sort_name1 = '台外幣活存';
			$scope.sort_amt2 = $scope.crm812_amt;
			$scope.sort_name2 = '台幣支存';
			$scope.sort_amt3 = $scope.crm813_amt;
			$scope.sort_name3 = '台外幣定存';
			
			
			//大小排序1:最大
			/*
			if($scope.crm811_amt >= $scope.crm812_amt && $scope.crm811_amt >= $scope.crm813_amt){
				$scope.sort_amt1 = $scope.crm811_amt;
				$scope.sort_name1 = '台外幣活存';
			}else if($scope.crm812_amt >= $scope.crm811_amt && $scope.crm812_amt >= $scope.crm813_amt){
				$scope.sort_amt1 = $scope.crm812_amt;
				$scope.sort_name1 = '台幣支存';
			}else if($scope.crm813_amt >= $scope.crm811_amt && $scope.crm813_amt >= $scope.crm812_amt){
				$scope.sort_amt1 = $scope.crm813_amt;
				$scope.sort_name1 = '台外幣定存';
			}
			//大小排序3:最小
			if($scope.crm811_amt <= $scope.crm812_amt && $scope.crm811_amt <= $scope.crm813_amt && $scope.sort_name1 != '台外幣活存'){
				$scope.sort_amt3 = $scope.crm811_amt;
				$scope.sort_name3 = '台外幣活存';
			}else if($scope.crm812_amt <= $scope.crm811_amt && $scope.crm812_amt <= $scope.crm813_amt && $scope.sort_name1 != '台幣支存'){
				$scope.sort_amt3 = $scope.crm812_amt;
				$scope.sort_name3 = '台幣支存';
			}else if($scope.crm813_amt <= $scope.crm811_amt && $scope.crm813_amt <= $scope.crm812_amt && $scope.sort_name1 != '台外幣定存'){
				$scope.sort_amt3 = $scope.crm813_amt;
				$scope.sort_name3 = '台外幣定存';
			}
			
			//大小排序2:中間
			if($scope.sort_name1 != '台外幣活存' && $scope.sort_name3 != '台外幣活存'){
				$scope.sort_amt2 = $scope.crm811_amt;
				$scope.sort_name2 = '台外幣活存';
			}else if($scope.sort_name1 != '台幣支存' && $scope.sort_name3 != '台幣支存'){
				$scope.sort_amt2 = $scope.crm812_amt;
				$scope.sort_name2 = '台幣支存';
			}else if($scope.sort_name1 != '台外幣定存' && $scope.sort_name3 != '台外幣定存'){
				$scope.sort_amt2 = $scope.crm813_amt;
				$scope.sort_name2 = '台外幣定存';
			}
			*/
			//====================================================================\\
	
			$scope.cur_list = $scope.custDepositVO.cur_list;
			$scope.no_cur_list = $scope.custDepositVO.no_cur_list;
			
			if ($scope.no_cur_list != null && $scope.no_cur_list.length > 0) {
				$scope.showMsg("以下幣別未有匯率資料，故不計算在內 : " + $scope.no_cur_list);
			}

			//畫圖資料
			if ($scope.crm811_amt == 0 && $scope.crm812_amt == 0 && $scope.crm813_amt == 0) {
				$scope.data = [];
			} else {
				$scope.data = [
				                { 
				                    "label": "台外幣活存",
				                    "color": "#3E8ABE",
				                    "value" : $scope.crm811_amt
				                  } , 
				                  { 
				                    "label": "台幣支存",
				                    "color": "#F497B2",
				                    "value" : $scope.crm812_amt
				                  } , 
				                  { 
				                    "label": "台外幣定存",
				                    "color": "#6BBC6B",
				                    "value" : $scope.crm813_amt
				                  }
				                ];
			}

			//畫圖參數設定
			$scope.options = {
					chart: {
						type: 'pieChart',
						height: $scope.chartHeight,
						x: function(d){return d.label;},
						y: function(d){return d.value;},
						showLabels: true,
						duration: 300,
						labelThreshold: 0.01,
						labelSunbeamLayout: false,
						labelType: "fubon",
						donutLabelsOutside: true,
						legend: {
							margin: {
								top: 5,
								right: 0,
								bottom: 0,
			                	left: 0
							},
							align: true
						},
						noData : '查無資料'
					}
			};
			
			//===============================資料排序===============================\\
			$scope.data2 = [];
			for (var key in $scope.cur_list){
				$scope.data2.push({"label" : key , "value" : $scope.cur_list[key]});			
			}
			$scope.data2 = _.sortBy($scope.data2, ['value']).reverse();
			//===================================================================\\
			
			//畫圖參數設定
			$scope.options2 = {
					chart: {
						type: 'pieChart',
						height: $scope.chartHeight,
						x: function(d){return d.label;},
						y: function(d){return d.value;},
						showLabels: true,
						duration: 300,
						labelThreshold: 0.01,
						labelSunbeamLayout: false,
						labelType: "fubon",
						donutLabelsOutside: true,
						legend: {
							margin: {
								top: 5,
								right: 0,
								bottom: 0,
			                	left: 0
							},
							align: true
						},
						noData : '查無資料'
					}
			};
			return;
		};
		$scope.init();
		
		$scope.detail = function(index){
			var index = index-1 ;
			var set = $scope.connector("set","CRM811_TAB",index);
			var path = "assets/txn/CRM811/CRM811_TAB.html";
			$scope.connector("set","CRM610URL",path);
			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
			
		}
		
});
		