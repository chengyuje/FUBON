/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM824Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM824Controller";
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
		};
		$scope.init();
		
		//最新幣值查詢
		$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.Currency = tota[0].body.resultList;
					}
		});
		
		$scope.sendRecv("SOT708", "getCustAssetSIData", "com.systex.jbranch.app.server.fps.sot708.SOT708InputVO", {"custId":$scope.inputVO.cust_id},
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.custAssetSIList == null || tota[0].body.custAssetSIList.length == 0) {
//						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					$scope.resultList = tota[0].body.custAssetSIList;
					$scope.COUNT = 0 ;
					angular.forEach($scope.resultList, function(row){
						$scope.COUNT += 1 ;
						//市值
						row.MVAULE = Number(row.IVAMT2 * row.SDAMT3 / 100);
        			});
					$scope.outputVO = tota[0].body;
					$scope.getSUM($scope.resultList);
					return;
				}
		});
		
		
		$scope.detail = function (row) {
			var custID =  $scope.custVO.CUST_ID;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM824/CRM824_DETAIL.html',
				className: 'CRM824_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.cust_id = custID;
                }]
			});
		}
		
		$scope.goPrdDetail = function(data) {
			var row = {};
			row.SI_CNAME = data.PRDCNM;
			row.PRD_ID = data.SDPRD;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD150/PRD150_DETAIL.html',
				className: 'PRD150_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		$scope.getSum = function(list, key) {
			if(list == null) 
				return;
			
			var sum = 0;
			for (var i = 0; i < list.length; i++ ) {
				sum += Number(list[i][key]);
			}
			return Number(sum);
		}
		
		$scope.getSUM = function(row) {
			
			$scope.SUMIVAMT2 = 0 ;		//庫存面額
			$scope.SUMVAULE = 0 ;		//市值
			$scope.SUMIRROR = 0 ;		//含息報酬
			$scope.Return = 0 ;			//含息報酬率
				
			//---------------------------------------------------
			
			for(var i = 0; i < row.length; i++) {
				//台幣不轉換
				if(row[i].IVCUCY != 'TWD'){
					//幣值轉換
					for(var j = 0; j < $scope.Currency.length; j++) {
						if(row[i].IVCUCY == $scope.Currency[j].CUR_COD){
							$scope.cod = $scope.Currency[j].BUY_RATE;
						}
					}
				}
				
				$scope.SUMIVAMT2 += (row[i].IVAMT2 * $scope.cod) ;	//面額*匯率
				$scope.SUMVAULE += (row[i].MVAULE * $scope.cod) ;	//市值*匯率
				var INTROR = row[i].INTROR;
				if(row[i].INTROR_S == '-'){
					INTROR = row[i].INTROR * -1;			
				}
				$scope.SUMIRROR += (INTROR * row[i].IVAMT2 * $scope.cod / 100) ;
				
			}
			//含息報酬率(約當台幣) = SUM(含息報酬率*庫存面額*匯率) / SUM(庫存面額*匯率)
			$scope.Return = $scope.SUMIRROR/$scope.SUMIVAMT2;
			$scope.Return = Number(($scope.Return*100).toFixed(4));
			
		}
});
