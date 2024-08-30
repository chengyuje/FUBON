/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM828Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM828Controller";
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
		};
		$scope.init();
		
		//最新幣值查詢
		$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.Currency = tota[0].body.resultList;
					}
		});
		
		$scope.sendRecv("CRM828", "inquire", "com.systex.jbranch.app.server.fps.crm828.CRM828InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
            			return;
            		}
					$scope.resultList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
					console.log($scope.resultList)
					$scope.getSUM($scope.resultList);
					return;
				}
		});
		
		$scope.getSum = function(list, key) {
			if(list == null) 
				return;
			
			var sum = 0;
			for (var i = 0; i < list.length; i++ ) {
				sum += Number(list[i][key]);
			}
			return sum;
		}
		
		$scope.getSUM = function(row) {
			
			$scope.SUMBAL = 0 ;				//總庫存數量(公克)
			$scope.SUMP_VALUE = 0 ;			//參考總市值
			$scope.SUMYIELD_AMT = 0 ;		//參考總損益金額
			$scope.Return = 0 ;			//參考總報酬率
			$scope.SUMINV_AMT = 0 ;	
			
			$scope.COUNT = 0 ;	
			//---------------------------------------------------
			
			for(var i = 0; i < row.length; i++) {
				$scope.cod = 1;
				//台幣不轉換
				if(row[i].CurCode != 'TWD'){
					//幣值轉換
					for(var j = 0; j < $scope.Currency.length; j++) {
						if(row[i].CurCode == $scope.Currency[j].CUR_COD){
							$scope.cod = $scope.Currency[j].BUY_RATE;
						}
					}
				}

				$scope.SUMBAL += (row[i].BAL * $scope.cod) ;
				$scope.SUMP_VALUE += (row[i].P_VALUE * $scope.cod) ;
				$scope.SUMYIELD_AMT += (row[i].YIELD_AMT * $scope.cod) ;
				$scope.SUMINV_AMT += (row[i].INV_AMT * $scope.cod) ;
				
			}
			//參考總報酬率
			if($scope.SUMYIELD_AMT == null || $scope.SUMYIELD_AMT == 0){
				$scope.Return = 0;
			}else{
				$scope.Return = ($scope.SUMP_VALUE - $scope.SUMINV_AMT) / $scope.SUMINV_AMT;
			}
			
			$scope.Return = Number(($scope.Return*100).toFixed(2));
		}
		
});
