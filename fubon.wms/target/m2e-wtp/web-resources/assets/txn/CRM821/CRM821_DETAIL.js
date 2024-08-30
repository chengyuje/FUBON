/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM821_DETAILController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM821_DETAILController";
		
		$scope.mappingSet['AssetType'] = [];
		$scope.mappingSet['AssetType'].push({LABEL: '單筆', DATA: '0001'},
											{LABEL: '定期定額', DATA: '0002'},
											{LABEL: '定期不定額', DATA: '0003'},
											{LABEL: '定存轉基金', DATA: '0004'},
											{LABEL: '基金套餐', DATA: '0005'},
											{LABEL: '國內代理基金', DATA: '0006'});
		$scope.mappingSet['Status'] = [];
		$scope.mappingSet['Status'].push({LABEL: '正常', DATA: '0'},
										 {LABEL: '暫停', DATA: '1'},
										 {LABEL: '非主標的', DATA: '2'});
		
		$scope.inputVO.prod_id = $scope.row.FundNO;
		

		$scope.NetValueDate = '';
		$scope.Strdate = '';
		var strY = '' , strM = '' , strD = '';			
		//淨值日期	
		strY = Number(1911) + Number($scope.row.NetValueDate.substring(0, 3));
		strM = $scope.row.NetValueDate.substring(3, 5);
		strD = $scope.row.NetValueDate.substring(5, 7);					
		if(strY == 1911){
			strY = "0000";
		}
		$scope.NetValueDate =  strY + "/" + strM + "/" + strD;	
		

		//投資起日
		strY = Number(1911) + Number($scope.row.Strdate.substring(0, 3));
		strM = $scope.row.Strdate.substring(3, 5);
		strD = $scope.row.Strdate.substring(5, 7);
		if(strY == 1911){
			strY = "0000";
		}
		$scope.Strdate =  strY + "/" + strM + "/" + strD;	
	
		//查SQL
		$scope.sendRecv("CRM821", "inquire", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO",$scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
            			return;
            		}
					$scope.row.table = tota[0].body.resultList;
					return;
				}
		});
		
		//最新幣值查詢
		$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.Currency = tota[0].body.resultList;
						$scope.cod = 1;
						//台幣不轉換
						if($scope.row.CurCode != 'TWD'){
							//幣值轉換
							for(var j = 0; j < $scope.Currency.length; j++) {
								if($scope.row.CurCode == $scope.Currency[j].CUR_COD){
									$scope.cod = $scope.Currency[j].BUY_RATE;
								}
							}
						}
					}
			
		});
		
});
