	/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM825Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM825Controller";
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
			$scope.inputVO.prodType = '1';
			$scope.inputVO.isOBU = '';
			$scope.inputVO.prod_id_list = [];
		};
		$scope.init();
		
		//最新幣值查詢
		$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.Currency = tota[0].body.resultList;
					}
		});
		
		//先判斷客戶是否為OBU
		$scope.sendRecv("SOT701", "getFP032675Data", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {"custID":$scope.inputVO.cust_id},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.fp032675DataVO.obuFlag == 'Y'){
							$scope.inputVO.isOBU = 'Y';							
						}else{
							$scope.inputVO.isOBU = 'N';
						}
						
						$scope.sendRecv("SOT707", "getNewCustAssetBondData", "com.systex.jbranch.app.server.fps.sot707.SOT707InputVO", {"custId":$scope.inputVO.cust_id, "prodType":$scope.inputVO.prodType, "isOBU":$scope.inputVO.isOBU},
								function(tota, isError) {
									if (!isError) {
										if(tota[0].body.custAssetBondList == null || tota[0].body.custAssetBondList.length == 0) {
//											$scope.showMsg("ehl_01_common_009");
				                			return;
				                		}
										$scope.resultList = _.sortBy(tota[0].body.custAssetBondList, ['TrustNo']);
										$scope.outputVO = tota[0].body;
										
										//交易日、發行日
										$scope.resultList.filter(function(obj){
											$scope.inputVO.prod_id_list.push(obj.BondNo.trim());
								    		return obj.BondNo;
								    	});
										$scope.sendRecv("CRM825", "inquire_DB", "com.systex.jbranch.app.server.fps.crm825.CRM825InputVO", $scope.inputVO,
												function(tota, isError) {
													if (!isError) {
														if(tota[0].body.sninfoList == null || tota[0].body.sninfoList.length == 0) {
														} else {
								                			//TBPRD_SNINFO DB資料加進電文LIST
								                			angular.forEach($scope.resultList, function(row){				
								                				angular.forEach(tota[0].body.sninfoList, function(row2){				
										    						if (row.BondNo == row2.PRD_ID) {
										    							row.sninfoList = row2;
										    						}
									                			});
								                			});
								                		}
													}
										});
										//已付前手息
										$scope.sendRecv("CRM823", "queryPRE_INT", "com.systex.jbranch.app.server.fps.crm823.CRM823InputVO", $scope.inputVO,
											function(tota, isError) {
												if (!isError) {									
													$scope.preList = tota[0].body.resultList;
													angular.forEach($scope.resultList, function(row){
														//已付前手息	將結果放入對應的$scope.resultList中
														row.PRE_INT = 0 ;										
														for(var j = 0; j < $scope.preList.length; j++) {	
															if($scope.preList[j].BOND_NBR == row.BondNo){
																if(Number($scope.preList[j].CERT_NBR) == Number(row.TrustNo)){
																	row.PRE_INT = $scope.preList[j].PRE_INT;
																}
															}
														}										
														
														//含息報酬率
														if(row.TrustAmt == null || row.TrustAmt == 0){
															row.IRROR = 0 ;
														}else{
															if(row.TrustVal == null || row.TrustVal == 0 || row.RefPrice == null || row.RefPrice == 0){
																row.TR = 0 ;
															}else{
																row.TR = row.TrustVal * row.RefPrice / 100 ;
															}
														}
														row.IRROR = ((row.TR + row.AccuInterest  + row.PayableFee )/(row.TrustAmt + row.PRE_INT)) - 1 ;
														row.IRROR = Number((row.IRROR*100).toFixed(4));
													});
													$scope.getSUM($scope.resultList);
													console.log($scope.resultList);
													return;
												}						
										});
									}
						});
					}
				}
		);
		
		
		$scope.detail = function (row) {
			var cust_id = $scope.inputVO.cust_id;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM825/CRM825_DETAIL.html',
				className: 'CRM825_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.cust_id = cust_id;
                }]
			});
		}
		
		$scope.goPrdDetail = function(data) {
			var row = {};
			row.PRD_ID = data.BondNo;
			row.SN_CNAME = data.BondName;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD140/PRD140_DETAIL.html',
				className: 'PRD140_DETAIL',
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
		};
		
		$scope.getSUM = function(row) {
			
			$scope.SUMPayableFee = 0 ;		//每單位前手息率
			$scope.SUMTrustVal = 0 ;		//庫存面額
			$scope.SUMAccuInterest = 0 ;	//累積現金配息
			$scope.SUMPRE_INT = 0 ;			//已付前手息
			$scope.SUMTrustAmt = 0 ;		//原始信託本金
			
			$scope.PT1 = 0 ;				//應收前手息 	PayableFeeRate x TrustVal / 100 = PayableFee
			$scope.TR = 0 ;					//			TrustVal x RefPrice
			$scope.SUMIRROR = 0 ;			//含息報酬
			$scope.Return = 0 ;				//含息報酬率	(TrustVal x RefPrice + AccuInterest - 已付前手息 + 應收前手息)/ TrustAmt - 1
			
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
				
				$scope.SUMPayableFee += (row[i].PayableFee * $scope.cod) ;
				$scope.SUMTrustVal += (row[i].TrustVal * $scope.cod) ;
				$scope.SUMAccuInterest += (row[i].AccuInterest * $scope.cod) ;
				$scope.SUMTrustAmt += (row[i].TrustAmt * $scope.cod) ;
				$scope.SUMPRE_INT += (row[i].PRE_INT * $scope.cod) ;
				
				$scope.COUNT += 1 ;	
				
				$scope.TR  += (row[i].TrustVal * $scope.cod * row[i].RefPrice / 100);
				//含息報酬:(庫存面額X參考贖回報價+累積現金配息-已付前手息+應收前手息)
				$scope.SUMIRROR += ((row[i].TrustVal * $scope.cod * row[i].RefPrice / 100) +	//庫存面額X參考贖回報價X匯率
									(row[i].AccuInterest * $scope.cod) + 	// + 累積現金配息X匯率
									(row[i].PayableFee * $scope.cod  / $scope.COUNT)) ;			// + 應收前手息X匯率
				
			}	
			//應收前手息
			$scope.PT1 = $scope.SUMPayableFee  / $scope.COUNT ;
			
			//含息報酬率:含息報酬加總(約當台幣) / 庫存金額加總 (約當台幣)
			$scope.Return = ($scope.SUMIRROR / ($scope.SUMTrustVal + $scope.SUMPRE_INT)) - 1
			$scope.Return = Number(($scope.Return*100).toFixed(4));
		}
		
});