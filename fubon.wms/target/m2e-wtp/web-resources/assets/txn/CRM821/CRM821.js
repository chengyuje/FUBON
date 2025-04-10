/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM821Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, getParameter, sotService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM821Controller";
		$scope.mappingSet['AssetType'] = [];
		$scope.mappingSet['AssetType'].push({LABEL: '單筆', DATA: '0001'},
											{LABEL: '定期定額', DATA: '0002'},
											{LABEL: '定期不定額', DATA: '0003'},
											{LABEL: '定存轉基金', DATA: '0004'},
											{LABEL: '基金套餐', DATA: '0005'});
			
		getParameter.XML(["PRD.FUND_VIGILANT"], function(tota) {
			if (tota) {
				$scope.mappingSet['PRD.FUND_VIGILANT'] = tota.data[tota.key.indexOf('PRD.FUND_VIGILANT')];
			}
		});
		
		$scope.Currency = [];
        $scope.pri = projInfoService.getPriID()[0];
        $scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
//			$scope.inputVO.isOBU = '';
		};
		$scope.init();
		
		//最新幣值查詢
		$scope.sendRecv("CRM821", "inquire_Currency", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.Currency = tota[0].body.resultList;
					}
			
		});
		
		
		$scope.sendRecv("CRM821", "getFundDeposit", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", { cust_id: $scope.inputVO.cust_id, isOBU: $scope.inputVO.isOBU },
			function(tota, isError) {
				if (!isError) {
					$scope.SUMCurBal = tota[0].body.fundAmount; //參考總市值
					$scope.sendRecv("CRM821", "getNFVIPA", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", { "cust_id": $scope.inputVO.cust_id, "isOBU": $scope.inputVO.isOBU },
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
//									$scope.showMsg("ehl_01_common_009");
//									return;
								}
								debugger
								$scope.redeemResultList = tota[0].body.redeemList; //贖回在途資料
								$scope.redeemOutputVO = tota[0].body.redeemList;
								
								$scope.resultList = tota[0].body.resultList; //庫存資料
								console.log("$scope.resultList", $scope.resultList);

								angular.forEach($scope.resultList, function(row, index, objs) {
									row.set = [];
									row.set.push({ LABEL: "單筆申購", DATA: "SOT110" });
									row.set.push({ LABEL: "定期(不)定額申購", DATA: "SOT120" });
									row.set.push({ LABEL: "轉換", DATA: "SOT140" });
									row.set.push({ LABEL: "贖回/贖回再申購", DATA: "SOT130" });
									row.set.push({ LABEL: "約定條件變更", DATA: "SOT150" });
									row.setDyna = [];
									row.setDyna.push({ LABEL: "單筆申購", DATA: "SOT1610" });
									row.setDyna.push({ LABEL: "母基金加碼", DATA: "SOT1620" });
									row.setDyna.push({ LABEL: "贖回", DATA: "SOT1630" });
									row.setDyna.push({ LABEL: "轉換", DATA: "SOT1640" });
									row.setDyna.push({ LABEL: "約定條件變更", DATA: "SOT1650" });

									//動態鎖利
									if (row.Dynamic && (row.Dynamic == "1" || row.Dynamic == "2")) {
										if (row.Dynamic == "1") row.DynamicType = "動態鎖利_母";
										if (row.Dynamic == "2") row.DynamicType = "動態鎖利_子";

										//約定申購子基金日
										var dateArray = [];
										if (row.SatelliteBuyDate1) dateArray.push(row.SatelliteBuyDate1);
										if (row.SatelliteBuyDate2) dateArray.push(row.SatelliteBuyDate2);
										if (row.SatelliteBuyDate3) dateArray.push(row.SatelliteBuyDate3);
										if (row.SatelliteBuyDate4) dateArray.push(row.SatelliteBuyDate4);
										if (row.SatelliteBuyDate5) dateArray.push(row.SatelliteBuyDate5);
										if (row.SatelliteBuyDate6) dateArray.push(row.SatelliteBuyDate6);
										dateArray.sort();
										row.SatelliteBuyDates = dateArray.join(", ");

										//約定停利報酬率
										var sRate = row.BenefitReturnRate1 + "%";
										if (row.BenefitReturnRate2) sRate = sRate + ", " + row.BenefitReturnRate2 + "%";
										if (row.BenefitReturnRate3) sRate = sRate + ", " + row.BenefitReturnRate3 + "%";
										row.BenefitReturnRates = sRate;

									} else {
										row.DynamicType = "一般";
										row.SatelliteBuyDates = "";
										row.BenefitReturnRates = "";
									}

									//查詢
									if (row.FundNO != undefined && row.FundNO != null && row.AssetType != '0006') {
										$scope.sendRecv("CRM821", "inquire_fund", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", { "prod_id": row.FundNO },
											function(tota, isError) {
												if (tota[0].body.resultList[0].FUS20 == 'C') {
													row.FUS20 = '國內基金';
												} else {
													row.FUS20 = '海外基金';
												}

												if (tota[0].body.resultList[0].VIGILANT != undefined && tota[0].body.resultList[0].VIGILANT != null) {
													row.VigCheck = 'V';
													row.Vigilant = tota[0].body.resultList[0].VIGILANT;
												} else {
													row.VigCheck = '';
													row.Vigilant = '';
												}
											});
									} else {
										row.FUS20 = '國內代理基金';
									}

									if (row.SignDigit == '-' && row.ProfitAndLoss != 0) {
										row.ProfitAndLoss = row.ProfitAndLoss * (-1);
									}
									if (row.SignDigit == '-' && row.Return != 0) {
										row.Return = row.Return * (-1);
									}
									if (row.RewRateDigit == '-' && row.AccAllocateRewRate != 0) {
										row.AccAllocateRewRate = row.AccAllocateRewRate * (-1);
									}
									if (row.RewRateDigitN == '-' && row.AccAllocateRewRateN != 0) {
										row.AccAllocateRewRateN = row.AccAllocateRewRateN * (-1);
									}
									
									//判斷交易型態
									if (row.AssetType == "0001") {
										row.TxType = "單筆";
										row.assetTradeSubTypeD = '1';
									} else if (row.AssetType == "0002") {
										if (row.TxType == "Y") {
											row.TxType = "定期定額FUND久久";
											row.assetTradeSubTypeD = '4';
										} else if (row.TxType == "A") {
											row.TxType = "定期定額FUND心投";
											row.assetTradeSubTypeD = '8';
										} else {
											row.TxType = "定期定額";
											row.assetTradeSubTypeD = '2';
										}
									} else if (row.AssetType == "0003") {
										if (row.TxType == "Y") {
											row.TxType = "定期不定額FUND久久";
											row.assetTradeSubTypeD = '5';
										} else if (row.TxType == "A") {
											row.TxType = "定期不定額FUND心投";
											row.assetTradeSubTypeD = '9';
										} else {
											row.TxType = "定期不定額";
											row.assetTradeSubTypeD = '3';
										}
									}
								});
								
								//排序：憑證編號、母/子基金(母基金在前)
								$scope.resultList.sort(function (a, b) {
									if(a.EviNum == b.EviNum) {
										return (a.Dynamic < b.Dynamic) ? -1 : 1;
									} else {
										return (a.EviNum < b.EviNum) ? -1 : 1;
									}
								});
								
								$scope.outputVO = tota[0].body;
								$scope.getSUM($scope.resultList);
							}
						});
				}
			});
		

		// 贖回在途資料
//		$scope.sendRecv("CRM821", "getRedeem", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", { "cust_id": $scope.inputVO.cust_id, "isOBU": $scope.inputVO.isOBU },
//			function(tota, isError) {
//				if (!isError) {
//					$scope.redeemResultList = tota[0].body.resultList;
//					$scope.redeemOutputVO = tota[0].body;
//					return;
//				}
//			});

		
		$scope.goCharge = function() {
			var row = $filter('filter')($scope.resultList, function (r) {
				return r.AssetType == '0002' || r.AssetType == '0003';
			});
			var outputVO_CHARGE = {};
			outputVO_CHARGE = {'data': row };
			var cust_id = $scope.inputVO.cust_id;
			
			var dialog = ngDialog.open({
		        template: 'assets/txn/CRM821/CRM821_CHARGE.html',
		        className: 'CRM821_CHARGE',
		        controller: ['$scope', function($scope) {
		      	  $scope.row = row;
		      	  $scope.outputVO_CHARGE = outputVO_CHARGE;
		      	  $scope.cust_id = cust_id;
		      	  debugger;
		        }]
			});
		};
		
		$scope.goCoupon = function() {
			var row = $scope.resultList;
			var cust_id = $scope.inputVO.cust_id;
			
			var dialog = ngDialog.open({
		        template: 'assets/txn/CRM821/CRM821_COUPON.html',
		        className: 'CRM821_COUPON',
		        controller: ['$scope', function($scope) {
		      	  $scope.row = row;
		      	  $scope.cust_id = cust_id;
		        }]
			});
		};
		
		$scope.goDetail = function(row) {
			 var dialog = ngDialog.open({
	                template: 'assets/txn/CRM821/CRM821_DETAIL.html',
	                className: 'CRM821_DETAIL',
	                controller: ['$scope', function($scope) {
	              	  $scope.row = row;
	                }]
	            });
		};
		
		$scope.goPrdDetail = function(data) {
			var row = {};
			row.PRD_ID = data.FundNO;
			row.FUND_CNAME = data.FundName;
			var dialog = ngDialog.open({
				template: 'assets/txn/PRD110/PRD110_DETAIL.html',
				className: 'PRD110_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		//一般基金
		$scope.action = function(row) {
			if(row.cmbAction) {
				$scope.connector('set','SOTCustID',$scope.inputVO.cust_id);
				$scope.connector('set','SOTProd',row);
				var menuName = "";
				if(row.cmbAction == "SOT110") menuName = "基金單筆申購";
				if(row.cmbAction == "SOT120") menuName = "基金定期(不)定額申購";
				if(row.cmbAction == "SOT130") menuName = "基金贖回/贖回再申購";
				if(row.cmbAction == "SOT140") menuName = "基金轉換";
				if(row.cmbAction == "SOT150") menuName = "基金約定事項變更";				
				debugger
				var path = [{'MENU_ID':'HOME','MENU_NAME':'首頁'}];
				path.push({'MENU_ID':row.cmbAction,'MENU_NAME':menuName});
	    		$rootScope.GeneratePage({'txnName':row.cmbAction,'txnId':row.cmbAction,'txnPath':path});
	    		
//				$rootScope.menuItemInfo.url = "assets/txn/"+row.cmbAction+"/"+row.cmbAction+".html";
				$scope.closeThisDialog('cancel');
//				row.cmbAction = "";
			}
		};
		
		//動態鎖利
		$scope.actionDyna = function(row) {
			if(row.cmbAction) {
				$scope.connector('set','SOTCustID',$scope.inputVO.cust_id);				
				var menuName = "";
				if(row.cmbAction == "SOT1610") {
					menuName = "動態鎖利單筆申購";
					$scope.connector('set','SOTProd', row.FundNO);
				} else {
					debugger
					var dynaData = $scope.getDynaData(row);
					$scope.connector('set','SOTProdM', dynaData.prodM);
					$scope.connector('set','SOTProdC1', dynaData.prodC1);
					$scope.connector('set','SOTProdC2', dynaData.prodC2);
					$scope.connector('set','SOTProdC3', dynaData.prodC3);
					$scope.connector('set','SOTProdC4', dynaData.prodC4);
					$scope.connector('set','SOTProdC5', dynaData.prodC5);
					
					if(row.cmbAction == "SOT1620") menuName = "動態鎖利母基金加碼";
					if(row.cmbAction == "SOT1630") menuName = "動態鎖利贖回";
					if(row.cmbAction == "SOT1640") menuName = "動態鎖利轉換";
					if(row.cmbAction == "SOT1650") menuName = "動態鎖利事件變更";	
				}
				
				var path = [{'MENU_ID':'HOME','MENU_NAME':'首頁'}];
				path.push({'MENU_ID':row.cmbAction,'MENU_NAME':menuName});
	    		$rootScope.GeneratePage({'txnName':row.cmbAction,'txnId':row.cmbAction,'txnPath':path});
				
//				$rootScope.menuItemInfo.url = "assets/txn/"+row.cmbAction+"/"+row.cmbAction+".html";
				$scope.closeThisDialog('cancel');
//				row.cmbAction = "";
			}
		};
		
		//回傳動態鎖利庫存資料
		$scope.getDynaData = function(row) {
			var rtnData = [];
			var cIndex = 1;
			for (var i = 0; i < $scope.resultList.length; i++) {
				//相同憑證編號
				if($scope.resultList[i].EviNum == row.EviNum) {
					if($scope.resultList[i].Dynamic == "1") rtnData.prodM = $scope.resultList[i]; //母基金
					if($scope.resultList[i].Dynamic == "2") { //子基金(舊憑證最多5筆，新憑證最多3筆)
						if(cIndex == 1) rtnData.prodC1 = $scope.resultList[i];
						if(cIndex == 2) rtnData.prodC2 = $scope.resultList[i];
						if(cIndex == 3) rtnData.prodC3 = $scope.resultList[i];
						if(cIndex == 4) rtnData.prodC4 = $scope.resultList[i];
						if(cIndex == 5) rtnData.prodC5 = $scope.resultList[i];
						cIndex++;
					}
				}
			}
			//回傳該憑證編號的母子基金資料
			return rtnData;
		}
		
		$scope.getSUM = function(row) {
//			$scope.SUMCurBalNT = 0 ;		//參考總市值(約當台幣)
			$scope.SUMProfitAndLoss = 0 ;	//參考總損益金額
			$scope.SUMCurAmt = 0 ;			//參考總投資金額
			$scope.Return = 0 ;				//參考總報酬率(不含配息)
			$scope.FundType_1 = 0 ;			//股票
			$scope.FundType_3 = 0 ;			//貨幣
			$scope.FundType_4 = 0 ;			//債券
			$scope.FundType_5 = 0 ;			//平衡
			$scope.SUMAccAllocateRew = 0;   //調整後累積現金配息
			$scope.SUMAccAllocateRewN = 0; 	// 調整後累積現金配息(含轉換前息)

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
				
				if(row[i].FundType == '1' || row[i].FundType == '01'){		//股票
					$scope.FundType_1 += (row[i].CurBal * $scope.cod);
				}
				if(row[i].FundType == '3' || row[i].FundType == '03'){		//貨幣
					$scope.FundType_3 += (row[i].CurBal * $scope.cod);
				}
				if(row[i].FundType == '4' || row[i].FundType == '04'){		//債券
					$scope.FundType_4 += (row[i].CurBal * $scope.cod);
				}
				if(row[i].FundType == '5' || row[i].FundType == '05'){		//平衡
					$scope.FundType_5 += (row[i].CurBal * $scope.cod);
				}
				
//				$scope.SUMCurBalNT += row[i].CurBalNT;
				$scope.SUMCurAmt += (row[i].CurAmt * $scope.cod);
				$scope.SUMAccAllocateRew += (row[i].AccAllocateRew * $scope.cod);
				$scope.SUMAccAllocateRewN += (row[i].AccAllocateRewN * $scope.cod)
				
			}
			
			//四捨五入 參考總報酬率(不含配息)
			$scope.SUMCurAmt = _.round($scope.SUMCurAmt);
			$scope.SUMProfitAndLoss = _.round($scope.SUMCurBal-$scope.SUMCurAmt);
			$scope.Return = Number(($scope.SUMProfitAndLoss * 100 / $scope.SUMCurAmt).toFixed(2));
			
			//參考總含息報酬率(不含轉換前配息)
			$scope.SUMAccAllocateRew = _.round($scope.SUMAccAllocateRew);
			$scope.Return_int = Number((($scope.SUMProfitAndLoss + $scope.SUMAccAllocateRew) * 100 / $scope.SUMCurAmt).toFixed(2));
			
			console.log("//參考總含息報酬率(不含轉換前配息)");
			console.log("$scope.SUMProfitAndLoss", $scope.SUMProfitAndLoss);
			console.log("$scope.SUMAccAllocateRew", $scope.SUMAccAllocateRew);
			console.log("$scope.SUMCurAmt", $scope.SUMCurAmt);
			
			//參考總含息報酬率(含轉換前配息)
			$scope.SUMAccAllocateRewN = _.round($scope.SUMAccAllocateRewN);
			$scope.Return_int2 = Number((($scope.SUMProfitAndLoss + $scope.SUMAccAllocateRewN) * 100 / $scope.SUMCurAmt).toFixed(2));
			console.log("//參考總含息報酬率(含轉換前配息)");
			console.log("$scope.SUMAccAllocateRewN",  $scope.SUMAccAllocateRewN);
		}
		
		$scope.reverse = '';
		$scope.propertyName = '';
		$scope.sortBy = function(propertyName){
			if($scope.propertyName == propertyName){
				$scope.propertyName = propertyName;
				if($scope.reverse == true){
					$scope.reverse = false;
				}else{
					$scope.reverse = true;
				}
			}else{
				$scope.reverse = true;
				$scope.propertyName = propertyName;
			}
			if($scope.reverse == true){
				$scope.resultList = _.sortBy($scope.resultList, [propertyName]).reverse();         //遞減	
				$scope.outputVO = {'data':$scope.resultList};
				return;
			}else{
				$scope.resultList = _.sortBy($scope.resultList, [propertyName]);         //遞增		
				$scope.outputVO =  {'data':$scope.resultList};
				return;
			}
		}
		
		$scope.redeemReverse = '';
		$scope.redeemPropertyName = '';
		$scope.redeemSortBy = function(colName){
			if ($scope.redeemPropertyName == colName) {
				$scope.redeemPropertyName = colName;
				if ($scope.redeemReverse == true) {
					$scope.redeemReverse = false;
				} else {
					$scope.redeemReverse = true;
				}
			} else {
				$scope.redeemReverse = true;
				$scope.redeemPropertyName = colName;
			}
			if ($scope.redeemReverse == true) {
				//遞減
				$scope.redeemResultList = _.sortBy($scope.redeemResultList, [colName]).reverse();
				$scope.redeemOutputVO = {'data':$scope.redeemResultList};
				return;
			}else{
				//遞增
				$scope.redeemResultList = _.sortBy($scope.redeemResultList, [colName]);		
				$scope.redeemOutputVO =  {'data':$scope.redeemResultList};
				return;
			}
		}
});
