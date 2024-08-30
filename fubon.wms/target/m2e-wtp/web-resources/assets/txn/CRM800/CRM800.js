/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM800Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM800Controller";
		$scope.login = projInfoService.getPriID()[0];
			
		
		/** 黃金存摺 **/
		function getGold() {
			let defer = $q.defer();
			$scope.sendRecv("CRM828", "inquire", "com.systex.jbranch.app.server.fps.crm828.CRM828InputVO",
				{cust_id: $scope.custVO.CUST_ID}, function(tota, isError) {
					if (!isError) {
						var result = tota[0].body.resultList;
						if(!result) return;
						var crm828 = {
							SUMBAL : 0,   		//總庫存數量(公克)
							SUMP_VALUE : 0,		//參考總市值
							SUMYIELD_AMT : 0,	//參考總損益金額
							Return : 0,			//參考總報酬率
							SUMINV_AMT : 0,

							resultList : result,
							outputVO :  tota[0].body
						};

						for(var i = 0; i < result.length; i++) {
							var cod = 1;
							//台幣不轉換
							if(result[i].CurCode != 'TWD'){
								//幣值轉換
								for(var j = 0; j < $scope.Currency.length; j++) {
									if(result[i].CurCode == $scope.Currency[j].CUR_COD){
										cod = $scope.Currency[j].BUY_RATE;
									}
								}
							}

							crm828.SUMBAL += (result[i].BAL * cod) ;
							crm828.SUMP_VALUE += (result[i].P_VALUE * cod) ;
							crm828.SUMYIELD_AMT += (result[i].YIELD_AMT * cod) ;
							crm828.SUMINV_AMT += (result[i].INV_AMT * cod) ;
						}
						//參考總報酬率
						if(crm828.SUMYIELD_AMT){
							crm828.Return = (crm828.SUMP_VALUE - crm828.SUMINV_AMT) / crm828.SUMINV_AMT;
							crm828.Return = Number((crm828.Return*100).toFixed(2));
						}
						$scope.investment += crm828.SUMP_VALUE;
						$scope.crm828 = angular.copy(crm828);
						defer.resolve('success');
					}
				});
			return defer.promise;
		}
		
		//最新幣值查詢
		$scope.Currency = [];
		$scope.getCurrency = function() {
			var defer = $q.defer();
			
			$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.Currency = tota[0].body.resultList;
					}	
						
					defer.resolve("success");
			});
			
			return defer.promise;
		}
		
		//查詢保險庫存資料查詢
		$scope.inquire_A = function() {
			var deferred = $q.defer();
			$scope.sendRecv("CRM681", "inquire", "com.systex.jbranch.app.server.fps.crm681.CRM681InputVO", {'cust_id': $scope.custVO.CUST_ID},
    				function(tota, isError) {
						if (!isError) {
							$scope.insurance = 	tota[0].body.insurance;
							deferred.resolve($scope.insurance);
                    	};
    				}
    		);
			return deferred.promise;
		}

		//查詢投資庫存資料查詢
		$scope.inquire_B = function() {
			var deferred = $q.defer();

			//查詢台幣活存(因為台幣活存的帳戶餘額加總會扣除負債，故改由明細查詢)
			//查詢外幣活存、台幣支存、台外幣定存	
			$scope.sendRecv("CRM810", "inquire", "com.systex.jbranch.app.server.fps.crm810.CRM810InputVO", { 'cust_id': $scope.custVO.CUST_ID },
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
					}

					$scope.crm811_amt = tota[0].body.crm811_amt;		//台外幣活存
					$scope.crm812_amt = tota[0].body.crm812_amt;		//台幣支存
					$scope.crm813_amt = tota[0].body.crm813_amt;		//台外幣定存
					$scope.deposit = $scope.crm811_amt + $scope.crm812_amt + $scope.crm813_amt;

					$scope.cur_list = tota[0].body.cur_list;
					var hasTWD = false;
//					if ($scope.cur_list != null) {
//						for ( var key in $scope.cur_list) {
//							if (key == 'TWD') {
//								if ($scope.crm811_amt != null && $scope.crm811_amt != undefined && $scope.crm811_amt > 0) {
//									$scope.cur_list[key] = $scope.crm811_amt + $scope.cur_list[key];
//								}
//								hasTWD = true;
//							}
//						}
//						if (!hasTWD && $scope.crm811_amt != null && $scope.crm811_amt != undefined && $scope.crm811_amt > 0) {
//							$scope.cur_list["TWD"] = $scope.crm811_amt;
//						}
//					} else {
//						$scope.cur_list = {};
//						if ($scope.crm811_amt != null && $scope.crm811_amt != undefined && $scope.crm811_amt > 0) {
//							$scope.cur_list["TWD"] = $scope.crm811_amt;
//						}
//					}
					$scope.no_cur_list = tota[0].body.no_cur_list;
				});
			deferred.resolve($scope.deposit);

			return deferred.promise;
		}
		
		$scope.inquireNano = function() {
			var defer = $q.defer();
			
			$scope.sendRecv("CRM829", "getNanoAsset", "com.systex.jbranch.app.server.fps.crm829.CRM829InputVO", {'cust_id': $scope.custVO.CUST_ID, 'getSumYN': 'Y'}, 
				function(tota, isError) {
					if (!isError) {
						$scope.crm829 = tota[0].body.totalMarketValueTwd;
					}
					
					defer.resolve("success");
			});
			
			return defer.promise;
		}
//		$scope.inquireNano();
		
		//金市海外債
		$scope.inquireGoldBond = function() {
			var defer = $q.defer();
			
			$scope.sendRecv("CRM82A", "getGoldBondAsset", "com.systex.jbranch.app.server.fps.crm82A.CRM82AInputVO", {"cust_id":$scope.custVO.CUST_ID},
				function(tota, isError) {
					if (!isError) {
						$scope.crm82A = tota[0].body.totalMarketValueTwd;
					}
					
					defer.resolve("success");
			});
			
			return defer.promise;
		}
		
		//1913 查詢存款庫存資料查詢
		$scope.inquire_C = function() {
			var deferred = $q.defer();
			$scope.crm826 = 0; 				//外匯雙享利(DCI)
			$scope.crm827 = 0; 				//金錢信託
			$scope.other = 0; 				//其他

			for (var i = 0; i < $scope.assetList.length; i++) {
				switch ($scope.assetList[i].BUSINESS_CODE) {
					case "SD":	//DCD
						$scope.crm826 += $scope.assetList[i].TOTAL_SUM_TWD;
						break;
					case "58":  //金錢信託
						$scope.crm827 += $scope.assetList[i].TOTAL_SUM_TWD;
						break;
					case "60":  //指單-環球固定收益投資
						$scope.other += $scope.assetList[i].TOTAL_SUM_TWD;
						break;
				}
			}

			$scope.investment = $scope.crm821 +
								$scope.crm822 +
								$scope.crm823 +
								$scope.crm824 +
								$scope.crm825 +
								$scope.crm826 +
								$scope.crm827 +
								$scope.crm829 +
								$scope.crm82A +
								$scope.other;

			//畫圖參數設定
			$scope.options = {
				chart: {
					type: 'pieChart',
					height: $scope.chartHeight,
					x: function(d) { return d.label; },
					y: function(d) { return d.value; },
					showLabels: true,
					duration: 300,
					labelThreshold: 0.01,
					labelSunbeamLayout: false,
					labelType: "fubon",
					donutLabelsOutside: true,
					legend: {
						margin: { top: 5, right: 0, bottom: 0, left: 0 },
						align: true
					},
					noData: '無'
				}

			};

			//資產統計圖
			if ($scope.deposit == 0 && $scope.investment == 0 && $scope.insurance == 0) {
				$scope.data = [];
			} else {
				//畫圖資料
				$scope.data = [
					{ "label": "存款", "value": $scope.deposit },
					{ "label": "投資", "value": $scope.investment },
					{ "label": "保險", "value": $scope.insurance }
				];
			}

			deferred.resolve($scope.investment);
			return deferred.promise;
		}
		
		//查詢負債投資
		$scope.getCustAssetInvestData2 = function(){
			var defer = $q.defer();
			
			//信用卡帳務資料及全負債圓餅圖
			$scope.sendRecv("CRM846", "initial", "com.systex.jbranch.app.server.fps.crm846.CRM846InputVO", {'cust_id': $scope.custVO.CUST_ID},
				function(tota, isError) {
					//==============================信用卡資料==============================
					if (!isError) {
						if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
//							return;
		              	}else{
		              		if(tota[0].body.resultList[0].CARD_PPAYD1 != null){
		              			$scope.dataList1 = Number(tota[0].body.resultList[0].CARD_PPAYD1);
		              		}
//		              		if(tota[0].body.resultList[0].CARD_PSTPOS != null){
//		              			$scope.dataList2 = Number(tota[0].body.resultList[0].CARD_PSTPOS);
//		              		}
		              	}
					}
					
					
					$scope.sendRecv("CRM681", "getFu", "com.systex.jbranch.app.server.fps.crm681.CRM681InputVO", {'cust_id': $scope.custVO.CUST_ID},
							function(tota, isError) {
								if (isError) {
									$scope.showErrorMsg(tota[0].body.msgData);
									 return;						
								}	
								
								//分期型房貸
								if(tota[0].body.resultList.length != 0){
//									$scope.setFuList(1,tota[0].body.resultList);
									for(var i = 0;i<tota[0].body.resultList.length;i++){
									tota[0].body.resultList[i].ACT_BAL_NT = tota[0].body.resultList[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号  
									$scope.Fu1 += Number(tota[0].body.resultList[i].ACT_BAL_NT);
									}

								} 
								//循環性貸款
								if(tota[0].body.resultList2.length != 0){
//									$scope.setFuList(2,tota[0].body.resultList2);
									for(var i = 0;i<tota[0].body.resultList2.length;i++){
									tota[0].body.resultList2[i].ACT_BAL_NT = tota[0].body.resultList2[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号  
									$scope.Fu2 += Number(tota[0].body.resultList2[i].ACT_BAL_NT);
									}
									
								} 
								//信貸
								if(tota[0].body.resultList3.length != 0){
//									$scope.setFuList(3,tota[0].body.resultList3);
									for(var i = 0;i<tota[0].body.resultList3.length;i++){
									tota[0].body.resultList3[i].ACT_BAL_NT = tota[0].body.resultList3[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号  
									$scope.Fu3 += Number(tota[0].body.resultList3[i].ACT_BAL_NT);
									}

								} 
								//就學貸款
								if(tota[0].body.resultList4.length != 0){
//									$scope.setFuList(4,tota[0].body.resultList4);
									for(var i = 0;i<tota[0].body.resultList4.length;i++){
									tota[0].body.resultList4[i].ACT_BAL_NT = tota[0].body.resultList4[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号  
									$scope.Fu4 += Number(tota[0].body.resultList4[i].ACT_BAL_NT);
									}

								} 
								//留學貸款
								if(tota[0].body.resultList5.length != 0){
//									$scope.setFuList(5,tota[0].body.resultList5);
									for(var i = 0;i<tota[0].body.resultList5.length;i++){
										tota[0].body.resultList5[i].ACT_BAL_NT = tota[0].body.resultList5[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号  
										$scope.Fu5  += Number(tota[0].body.resultList5[i].ACT_BAL_NT);
									}

								} 
								//存單質借
								if(tota[0].body.resultList6.length != 0){
//									$scope.setFuList(6,tota[0].body.resultList6);
									for(var i = 0;i<tota[0].body.resultList6.length;i++){
									tota[0].body.resultList6[i].ACT_BAL_NT = tota[0].body.resultList6[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号  
									$scope.Fu6 += Number(tota[0].body.resultList6[i].ACT_BAL_NT);
									}

								} 
								//個人週轉金
								if(tota[0].body.resultList7.length != 0){
//									$scope.setFuList(7,tota[0].body.resultList7);
									for(var i = 0;i<tota[0].body.resultList7.length;i++){
									tota[0].body.resultList7[i].ACT_BAL_NT = tota[0].body.resultList7[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号  
									$scope.Fu7 += Number(tota[0].body.resultList7[i].ACT_BAL_NT);
									}

								} 
								//企業貸款
								if(tota[0].body.resultList8.length != 0){
//									$scope.setFuList(8,tota[0].body.resultList8);
									for(var i = 0;i<tota[0].body.resultList8.length;i++){
									tota[0].body.resultList8[i].ACT_BAL_NT = tota[0].body.resultList8[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号  
									$scope.Fu8 += Number(tota[0].body.resultList8[i].ACT_BAL_NT);
									}

								} 

								$scope.FuAll = Number($scope.Fu1)+Number($scope.Fu2)+Number($scope.Fu3)+Number($scope.Fu4)+Number($scope.Fu5)+Number($scope.Fu6)+Number($scope.Fu7)+Number($scope.Fu8)+Number($scope.dataList1)+Number($scope.dataList2);

					//==============================負債統計圖==============================
					//畫圖資料
					if ($scope.Fu1 == 0 && $scope.Fu2 == 0 && $scope.Fu3 == 0 && $scope.Fu4 == 0 &&
						$scope.Fu5 == 0 && $scope.Fu6 == 0 && $scope.Fu7 == 0 && $scope.Fu8 == 0 &&
						$scope.dataList1 == 0 ) {
						$scope.data2 = [];
					} else {
						//企業貸款:沒有金額不顯示,個人周轉金貸款:沒有金額不顯示
						if(Number($scope.Fu8) == 0 && Number($scope.Fu7) == 0){
							$scope.data2 = [
							                {"label": "分期型房貸","value" : Number($scope.Fu1)} , 
							                {"label": "循環型貸款","value" : Number($scope.Fu2)} , 
							                {"label": "信用貸款","value" : Number($scope.Fu3)} ,
							                {"label": "就學貸款","value" : Number($scope.Fu4)} , 
							                {"label": "留學貸款","value" : Number($scope.Fu5)} , 
							                {"label": "存單質借","value" : Number($scope.Fu6)} ,
							                {"label": "信用卡本期帳單剩餘應繳金額","value" : $scope.dataList1} 
//							                {"label": "信用卡未出帳單消費金額","value" : $scope.dataList2}
							               ];
						}else if(Number($scope.Fu8) == 0){
							$scope.data2 = [
							                {"label": "分期型房貸","value" : Number($scope.Fu1)} , 
							                {"label": "循環型貸款","value" : Number($scope.Fu2)} , 
							                {"label": "信用貸款","value" : Number($scope.Fu3)} ,
							                {"label": "就學貸款","value" : Number($scope.Fu4)} , 
							                {"label": "留學貸款","value" : Number($scope.Fu5)} , 
							                {"label": "存單質借","value" : Number($scope.Fu6)} ,
							                {"label": "個人週轉金貸款","value" : Number($scope.Fu7)} ,
							                {"label": "信用卡本期帳單剩餘應繳金額","value" : $scope.dataList1} 
//							                {"label": "信用卡未出帳單消費金額","value" : $scope.dataList2}
							               ];
						}else if(Number($scope.Fu7) == 0){
							$scope.data2 = [
							                {"label": "分期型房貸","value" : Number($scope.Fu1)} , 
							                {"label": "循環型貸款","value" : Number($scope.Fu2)} , 
							                {"label": "信用貸款","value" : Number($scope.Fu3)} ,
							                {"label": "就學貸款","value" : Number($scope.Fu4)} , 
							                {"label": "留學貸款","value" : Number($scope.Fu5)} , 
							                {"label": "存單質借","value" : Number($scope.Fu6)} ,
							                {"label": "企業貸款","value" : Number($scope.Fu8)} , 
							                {"label": "信用卡本期帳單剩餘應繳金額","value" : $scope.dataList1} 
//							                {"label": "信用卡未出帳單消費金額","value" : $scope.dataList2}
							               ];
						}else{
							$scope.data2 = [
							                {"label": "分期型房貸","value" : Number($scope.Fu1)} , 
							                {"label": "循環型貸款","value" : Number($scope.Fu2)} , 
							                {"label": "信用貸款","value" : Number($scope.Fu3)} ,
							                {"label": "就學貸款","value" : Number($scope.Fu4)} , 
							                {"label": "留學貸款","value" : Number($scope.Fu5)} , 
							                {"label": "存單質借","value" : Number($scope.Fu6)} ,
							                {"label": "個人週轉金貸款","value" : Number($scope.Fu7)} ,
							                {"label": "企業貸款","value" : Number($scope.Fu8)} , 
							                {"label": "信用卡本期帳單剩餘應繳金額","value" : $scope.dataList1} 
//							                {"label": "信用卡未出帳單消費金額","value" : $scope.dataList2}
							               ];
						}
					}
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
									margin: {top: 5,right: 0,bottom: 0,left: 0},
									align: true
								},
								noData : '無'
							}
					};
				
					defer.resolve("success");
					});

			});		
			
			return defer.promise;
		};
//		$scope.getCustAssetInvestData2();
		
		
		//負債取值
		$scope.setFuList = function(i,row){
			for(var i=0; i < row.length; i++) {
				//數值0就不必加進去
				if(Number(row[i].ACT_BAL_NT) > 0){
					$scope.FuData += Number(row[i].ACT_BAL_NT);
				}
			}
			$scope.Fu1 = i == 1 ? $scope.FuData:$scope.Fu1;
			$scope.Fu2 = i == 2 ? $scope.FuData:$scope.Fu2;
			$scope.Fu3 = i == 3 ? $scope.FuData:$scope.Fu3;
			$scope.Fu4 = i == 4 ? $scope.FuData:$scope.Fu4;
			$scope.Fu5 = i == 5 ? $scope.FuData:$scope.Fu5;
			$scope.Fu6 = i == 6 ? $scope.FuData:$scope.Fu6;
			$scope.Fu7 = i == 7 ? $scope.FuData:$scope.Fu7;
			$scope.Fu8 = i == 8 ? $scope.FuData:$scope.Fu8;
		}

		
		
		$scope.getCustHomeInclude = function(path) {
			if(path == 'CRM810'){
				$scope.custDepositVO.crm811_amt = $scope.crm811_amt;
				$scope.custDepositVO.crm812_amt = $scope.crm812_amt;
				$scope.custDepositVO.crm813_amt = $scope.crm813_amt;
				$scope.custDepositVO.cur_list = $scope.cur_list;
				$scope.custDepositVO.no_cur_list = $scope.no_cur_list;
				$scope.$emit("CRM610VO", {action:"set", type:"CRM681_CUSTDEPOSIT", data: $scope.custDepositVO});
				$scope.connector("set","CRM681_CUSTDEPOSIT",$scope.custDepositVO);
				
			} else if (path == 'CRM820') {
				$scope.custInvestmentVO.total  = $scope.investment;
				$scope.custInvestmentVO.crm821 = $scope.crm821;
				$scope.custInvestmentVO.crm822 = $scope.crm822;
				$scope.custInvestmentVO.crm823 = $scope.crm823;
				$scope.custInvestmentVO.crm824 = $scope.crm824;
				$scope.custInvestmentVO.crm825 = $scope.crm825;
				$scope.custInvestmentVO.crm826 = $scope.crm826;
				$scope.custInvestmentVO.crm827 = $scope.crm827;
				$scope.custInvestmentVO.crm828 = $scope.crm828.SUMP_VALUE;
				$scope.custInvestmentVO.crm829 = $scope.crm829;
				$scope.custInvestmentVO.crm82A = $scope.crm82A;
				$scope.custInvestmentVO.other  = $scope.other;
				$scope.$emit("CRM610VO", {action:"set", type:"CRM681_Investment", data: $scope.custInvestmentVO});
				$scope.connector("set","CRM681_Investment",$scope.custInvestmentVO);
			} 

    		var url = "assets/txn/"+path+"/"+path+".html";
			$scope.connector("set","CRM610URL",url);	
			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:url});
		};
		
		$scope.getCustHomeInclude1 = function(index) {
    		var index = index;
    		var url = "assets/txn/CRM840/CRM840.html";
			$scope.connector("set","CRM840_tab",index);
			$scope.connector("set","CRM610URL",url);	
			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:url});
		};
		
		//#1913_查詢投資庫存資料查詢
		$scope.getCustAssetInvestData = function(isObu) {
			var defer = $q.defer();
			$scope.sendRecv("CRM681", "getCustAssetInvestData2", "com.systex.jbranch.app.server.fps.crm681.CRM681InputVO", { cust_id: $scope.custVO.CUST_ID, obuFlag: isObu },
				function(tota, isError) {
					if (!isError) {
						$scope.assetList = tota[0].body.assetList;
					}
					defer.resolve("success");
				});
			return defer.promise;
		};

		//#1913_取得基金總資產
		$scope.getFundDeposit = function(isObu) {
			var defer = $q.defer();
			$scope.sendRecv("CRM821", "getFundDeposit", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", { cust_id: $scope.custVO.CUST_ID, isOBU: isObu },
				function(tota, isError) {
					if (!isError) {
						$scope.crm821 = tota[0].body.fundAmount;
					}
					defer.resolve("success");
				});
			return defer.promise;
		};

		//#1913_海外ETF/海外股票總資產
		$scope.getETFStockDeposit = function() {
			var defer = $q.defer();
			$scope.sendRecv("CRM822", "getETFStockDeposit", "com.systex.jbranch.app.server.fps.crm822.CRM822InputVO", { cust_id: $scope.custVO.CUST_ID },
				function(tota, isError) {
					if (!isError) {
						$scope.crm822 = tota[0].body.etfStockAmount;
					}
					defer.resolve("success");
				});
			return defer.promise;
		};

		//#1913_取得 海外債 & SN 總資產
		$scope.getBondSnDeposit = function(isObu) {
			var defer = $q.defer();
			$scope.sendRecv("CRM823", "getBondSnDeposit", "com.systex.jbranch.app.server.fps.crm823.CRM823InputVO", { cust_id: $scope.custVO.CUST_ID, isOBU: isObu },
				function(tota, isError) {
					if (!isError) {
						$scope.crm823 = tota[0].body.bondAmount;
						$scope.crm825 = tota[0].body.snAmount;
					}
					defer.resolve("success");
				});
			return defer.promise;
		};

		//#1913_取得SI總資產
		$scope.getSIDeposit = function() {
			var defer = $q.defer();
			$scope.sendRecv("CRM824", "getSIDeposit", "com.systex.jbranch.app.server.fps.crm824.CRM824InputVO", { "cust_id": $scope.custVO.CUST_ID },
				function(tota, isError) {
					if (!isError) {
						$scope.crm824 = tota[0].body.siAmount;
					}
					defer.resolve("success");
				});
			return defer.promise;
		};
		
		
		//初始化
		$scope.init = function() {
			// 圓餅圖大小調整
			$scope.chartHeight = 400;
			$scope.clientHeight = window.innerHeight;

			if ($scope.clientHeight <= ($scope.chartHeight + 353)) {
				$scope.chartHeight = $scope.clientHeight - 353 - 50;
			}

			$scope.deposit = 0;
			$scope.investment = 0;
			$scope.insurance = 0;
			$scope.custDepositVO = {
				crm811_amt: undefined,
				crm812_amt: undefined,
				crm813_amt: undefined,
				cur_list: [],
				no_cur_list: []
			}
			$scope.custInvestmentVO = {
				crm821: undefined,
				crm822: undefined,
				crm823: undefined,
				crm824: undefined,
				crm825: undefined,
				crm826: undefined,
				crm827: undefined,
				crm828: undefined,
				crm829: undefined,
				crm82A: undefined,
				other: undefined,
				total: undefined
			}
			$scope.FuData = 0;
			$scope.Fu1 = 0;
			$scope.Fu2 = 0;
			$scope.Fu3 = 0;
			$scope.Fu4 = 0;
			$scope.Fu5 = 0;
			$scope.Fu6 = 0;
			$scope.Fu7 = 0;
			$scope.Fu8 = 0;
			$scope.dataList1 = 0;
			$scope.dataList2 = 0;

			//asynchronized避免在電文回傳前就做加總
			console.log("CRM800 starts:" + new Date());
			$scope.getCurrency().then(function(data) {				
				console.log("CRM800 getCurrency finished" + new Date());
			$scope.inquireNano().then(function(data) {
				console.log("CRM800 inquireNano finished" + new Date());
				$scope.getCustAssetInvestData2().then(function(data) {
					console.log("CRM800 getCustAssetInvestData2 finished" + new Date());
					$scope.inquireGoldBond().then(function(data) {
						console.log("CRM800 inquireGoldBond finished" + new Date());
						getGold().then(function(data) {
							console.log("CRM800 getGold finished" + new Date());
							$scope.inquire_A().then(function(data) {
								console.log("CRM800 inquire_A finished" + new Date());
								$scope.inquire_B().then(function(data) {
									console.log("CRM800 inquire_B finished" + new Date());
									$scope.getCustAssetInvestData($scope.inputVO.isOBU).then(function(data) {
										$scope.getFundDeposit($scope.inputVO.isOBU).then(function(data) {
											$scope.getETFStockDeposit().then(function(data) {
												$scope.getBondSnDeposit($scope.inputVO.isOBU).then(function(data) {
													$scope.getSIDeposit().then(function(data) {
														$scope.inquire_C().then(function(data) {
															console.log("CRM800 inquire_C finished" + new Date());
														});
													});
												});
											});
										});
									});
								});
							});
						});
					});
				});
			});
		});
		};
		$scope.init();
		
});
		