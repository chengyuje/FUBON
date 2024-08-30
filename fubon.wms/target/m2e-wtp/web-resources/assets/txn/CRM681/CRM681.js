/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM681Controller',
    function ($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $q, $timeout) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CRM681Controller";
        $scope.login = String(sysInfoService.getPriID());

        $('#CRM681HIDDENFORTUNA').hide();
        
        $('#CRM681FORTUNA').hover(
            function () {
                $('#CRM681HOVERTABLE').css("opacity", "0.5");
                $('#CRM681FORTUNA').hide();
                $('#CRM681HIDDENFORTUNA').show();
            }
        );
        
        $('#CRM681HIDDENFORTUNA').hover(
            function () {

            }, function () {
                $('#CRM681HOVERTABLE').css("opacity", "1");
                $('#CRM681FORTUNA').show();
                $('#CRM681HIDDENFORTUNA').hide();
            }
        );

        $scope.inputVO.cust_id = $scope.custVO.CUST_ID;
		

        //信用卡帳務資料
        function getCustCreditData() {
            var defer = $q.defer();

            $scope.sendRecv("CRM846", "initial", "com.systex.jbranch.app.server.fps.crm846.CRM846InputVO", {'cust_id': $scope.custVO.CUST_ID}, function (tota, isError) {
                if (!isError) {
                    if (tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
                        defer.resolve("success");
                        return defer.promise;
                    }
                    //信用卡本期帳單剩餘應繳金額
                    $scope.dataList1 = tota[0].body.resultList[0].CARD_PPAYD1;
                    //信用卡未出帳單消費金額
                    $scope.dataList2 = tota[0].body.resultList[0].CARD_PSTPOS;
                }
                
                defer.resolve($scope.dataList1);
            });
            
            return defer.promise;
        }

        function getFu(debt) {
            //分期型房貸
            if (debt.resultList.length != 0) {
                for (var i = 0; i < debt.resultList.length; i++) {
                    debt.resultList[i].ACT_BAL_NT = debt.resultList[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号
                    $scope.Fu1 += Number(debt.resultList[i].ACT_BAL_NT);
                }
            }

            //循環性貸款
            if (debt.resultList2.length != 0) {
                for (var i = 0; i < debt.resultList2.length; i++) {
                    debt.resultList2[i].ACT_BAL_NT = debt.resultList2[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号
                    $scope.Fu2 += Number(debt.resultList2[i].ACT_BAL_NT);
                }
            }

            //信貸
            if (debt.resultList3.length != 0) {
                for (var i = 0; i < debt.resultList3.length; i++) {
                    debt.resultList3[i].ACT_BAL_NT = debt.resultList3[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号
                    $scope.Fu3 += Number(debt.resultList3[i].ACT_BAL_NT);
                }

            }
            
            //就學貸款
            if (debt.resultList4.length != 0) {
                for (var i = 0; i < debt.resultList4.length; i++) {
                    debt.resultList4[i].ACT_BAL_NT = debt.resultList4[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号
                    $scope.Fu4 += Number(debt.resultList4[i].ACT_BAL_NT);
                }
            }

            //留學貸款
            if (debt.resultList5.length != 0) {
                for (var i = 0; i < debt.resultList5.length; i++) {
                    debt.resultList5[i].ACT_BAL_NT = debt.resultList5[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号
                    $scope.Fu5 += Number(debt.resultList5[i].ACT_BAL_NT);
                }
            }

            //存單質借
            if (debt.resultList6.length != 0) {
                for (var i = 0; i < debt.resultList6.length; i++) {
                    debt.resultList6[i].ACT_BAL_NT = debt.resultList6[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号
                    $scope.Fu6 += Number(debt.resultList6[i].ACT_BAL_NT);
                }
            }

            //個人週轉金
            if (debt.resultList7.length != 0) {
                for (var i = 0; i < debt.resultList7.length; i++) {
                    debt.resultList7[i].ACT_BAL_NT = debt.resultList7[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号
                    $scope.Fu7 += Number(debt.resultList7[i].ACT_BAL_NT);
                }
            }

            //企業貸款
            if (debt.resultList8.length != 0) {
                for (var i = 0; i < debt.resultList8.length; i++) {
                    debt.resultList8[i].ACT_BAL_NT = debt.resultList8[i].ACT_BAL_NT.replace(/,/g, "");//取消字符串中出现的所有逗号
                    $scope.Fu8 += Number(debt.resultList8[i].ACT_BAL_NT);
                }
            }

            $scope.FuAll = Number($scope.Fu1) + 
            			   Number($scope.Fu2) + 
            			   Number($scope.Fu3) + 
            			   Number($scope.Fu4) + 
            			   Number($scope.Fu5) + 
            			   Number($scope.Fu6) + 
            			   Number($scope.Fu7) + 
            			   Number($scope.Fu8) + 
            			   Number($scope.dataList1) + 
            			   Number($scope.dataList2);
        }

        //查詢負債投資
        function getCustAssetInvestData2(acctList) {
            var defer = $q.defer();

            $scope.sendRecv("CRM681", "getFu", "com.systex.jbranch.app.server.fps.crm681.CRM681InputVO", {'cust_id': $scope.custVO.CUST_ID, acctData: acctList}, function (tota, isError) {
                if (isError) {
                    $scope.showErrorMsg(tota[0].body.msgData);
                    defer.resolve();
                } else {
                    defer.resolve(tota[0].body);
                }
            });
            
            return defer.promise;
        }

        //負債取值
        $scope.setFuList = function (i, row) {
            $scope.Fu1 = i == 1 ? $scope.FuData : $scope.Fu1;
            $scope.Fu2 = i == 2 ? $scope.FuData : $scope.Fu2;
            $scope.Fu3 = i == 3 ? $scope.FuData : $scope.Fu3;
            $scope.Fu4 = i == 4 ? $scope.FuData : $scope.Fu4;
            $scope.Fu5 = i == 5 ? $scope.FuData : $scope.Fu5;
            $scope.Fu6 = i == 6 ? $scope.FuData : $scope.Fu6;
            $scope.Fu7 = i == 7 ? $scope.FuData : $scope.Fu7;
            $scope.Fu8 = i == 8 ? $scope.FuData : $scope.Fu8;
        }

        //抓取保險庫存餘額
        function inquire() {
            var defer = $q.defer();
            $scope.sendRecv("CRM681", "inquire", "com.systex.jbranch.app.server.fps.crm681.CRM681InputVO", {'cust_id': $scope.custVO.CUST_ID},
                function (tota, isError) {
                    if (!isError) {
                        $scope.insurance = tota[0].body.insurance;
                    }
                    defer.resolve($scope.insurance);
                }
            )
            return defer.promise;
        }

        function inquireGold() {
            var defer = $q.defer();
            $scope.crm828 = 0;		//黃金存摺
            $scope.sendRecv("CRM828", "inquire", "com.systex.jbranch.app.server.fps.crm828.CRM828InputVO", {'cust_id': $scope.custVO.CUST_ID},
                function (tota, isError) {
                    if (!isError) {
                        angular.forEach(tota[0].body.resultList, function (row) {
                            $scope.crm828 += row.P_VALUE;
                        });
                    }
                    defer.resolve($scope.crm828);
                }
            )
            return defer.promise;
        }

        //查詢存款庫存資料查詢
        function getCustAssetDepositData(acctList) {
            var defer = $q.defer();
            //查詢外幣活存、台幣支存、台外幣定存
            $scope.sendRecv("CRM810", "inquire", "com.systex.jbranch.app.server.fps.crm810.CRM810InputVO", {'cust_id': $scope.custVO.CUST_ID, acctData: acctList}, function (tota, isError) {
                if (isError) {
                    $scope.showErrorMsg(tota[0].body.msgData);
                    defer.resolve();
                } else {
                    $scope.crm811_amt = tota[0].body.crm811_amt;		//台外幣活存
                    $scope.crm812_amt = tota[0].body.crm812_amt;		//台幣支存
                    $scope.crm813_amt = tota[0].body.crm813_amt;		//台外幣定存
                    $scope.deposit = $scope.crm811_amt + $scope.crm812_amt + $scope.crm813_amt;
                    $scope.cur_list = tota[0].body.cur_list;
                    $scope.no_cur_list = tota[0].body.no_cur_list;
                    defer.resolve(tota[0].body);
                }
            });
            return defer.promise;
        }

        //查詢奈米投庫存
        function getNanoAsset() {
            var defer = $q.defer();
            $scope.sendRecv("CRM829", "getNanoAsset", "com.systex.jbranch.app.server.fps.crm829.CRM829InputVO", {'cust_id': $scope.custVO.CUST_ID, 'getSumYN': 'Y'}, function (tota, isError) {
	            if (!isError) {
	                $scope.crm829 = tota[0].body.totalMarketValueTwd;
	                defer.resolve($scope.crm829);
	            } else {
	                defer.resolve();
	            }
	        });
            return defer.promise;
        }

        function getSUM(result) {
            $scope.crm821 = result.fundDeposit; 		      // 基金
			$scope.crm822 = result.etfStockDeposit; 		  // 海外ETF/海外股票
            $scope.crm823 = result.bondSnDeposit.bondDeposit; // 海外債
            $scope.crm824 = result.siDeposit; 		          // 組合式商品(SI)
            $scope.crm825 = result.bondSnDeposit.snDeposit;   // 境外結構型商品(SN)
            $scope.crm826 = 0; 		// 外匯雙享利(DCI)
            $scope.crm827 = 0; 		// 金錢信託
//			$scope.crm828 = 0; 		// 黃金存摺
            $scope.other = 0;  		// 其他
            
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
			
			
            for (var i = 0; i < $scope.assetList.length; i++) {
                switch ($scope.assetList[i].BUSINESS_CODE) {
                    case "SD":	// DCD
                        $scope.crm826 += $scope.assetList[i].TOTAL_SUM_TWD;
                        break;
                    case "58":  // 金錢信託
                        $scope.crm827 += $scope.assetList[i].TOTAL_SUM_TWD;
                        break;
                    case "40":  // 有價證券信託
                        $scope.crm683 += $scope.assetList[i].TOTAL_SUM_TWD;
                        break;
                    case "51":  // 指單-計劃性投資組合
                    case "52":  // 全權委託
                    case "59":  // 指單-環球動態投資
                    case "60":  // 指單-環球固定收益投資
                        $scope.other += $scope.assetList[i].TOTAL_SUM_TWD;
                        break;
                    case "F10": // 複委託股票
                    	$scope.crm871AUM += $scope.assetList[i].TOTAL_SUM_TWD;
                    	$scope.crm871INVEST_AUM += $scope.assetList[i].TOTAL_INVEST_TWD;
                    	$scope.crm871BENEFIT_AMT1 += $scope.assetList[i].TOTAL_BENEFIT_AMT1_TWD;
                    	$scope.crm871BENEFIT_AMT2 += $scope.assetList[i].TOTAL_BENEFIT_AMT2_TWD;
                        break;
                    case "F30": // 複委託債券
                    	$scope.crm872AUM += $scope.assetList[i].TOTAL_SUM_TWD;
                    	$scope.crm872INVEST_AUM += $scope.assetList[i].TOTAL_INVEST_TWD;
                    	$scope.crm872BENEFIT_AMT1 += $scope.assetList[i].TOTAL_BENEFIT_AMT1_TWD;
                    	$scope.crm872BENEFIT_AMT2 += $scope.assetList[i].TOTAL_BENEFIT_AMT2_TWD;
                        break;
                    case "F40": // 複委託境外結構
                    	$scope.crm873AUM += $scope.assetList[i].TOTAL_SUM_TWD;
                    	$scope.crm873INVEST_AUM += $scope.assetList[i].TOTAL_INVEST_TWD;
                    	$scope.crm873BENEFIT_AMT1 += $scope.assetList[i].TOTAL_BENEFIT_AMT1_TWD;
                    	$scope.crm873BENEFIT_AMT2 += $scope.assetList[i].TOTAL_BENEFIT_AMT2_TWD;
                        break;
                    case "DSN": // 複委託境內結構
                    	$scope.crm874AUM += $scope.assetList[i].TOTAL_SUM_TWD;
                    	$scope.crm874INVEST_AUM += $scope.assetList[i].TOTAL_INVEST_TWD;
                    	$scope.crm874BENEFIT_AMT1 += $scope.assetList[i].TOTAL_BENEFIT_AMT1_TWD;
                    	$scope.crm874BENEFIT_AMT2 += $scope.assetList[i].TOTAL_BENEFIT_AMT2_TWD;
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
            					$scope.crm828 + 
            					$scope.crm829 + 
            					$scope.crm82A + 
            					$scope.other;

            $scope.sec = $scope.crm871AUM + $scope.crm872AUM + $scope.crm873AUM + $scope.crm874AUM;
        }

        //金市海外債
        function getGoldBondAsset() {
            var defer = $q.defer();
            $scope.sendRecv("CRM82A", "getGoldBondAsset", "com.systex.jbranch.app.server.fps.crm82A.CRM82AInputVO", {"cust_id": $scope.custVO.CUST_ID}, function (tota, isError) {
                if (!isError) {
                    $scope.crm82A = tota[0].body.totalMarketValueTwd;
                }
                defer.resolve($scope.crm82A);
            });
            return defer.promise;
        }
		
        //#1913_查詢投資庫存資料查詢
        function getCustAssetInvestData(isObu) {
            var defer = $q.defer();
			$scope.sendRecv("CRM681", "getCustAssetInvestData2", "com.systex.jbranch.app.server.fps.crm681.CRM681InputVO", { cust_id: $scope.custVO.CUST_ID, obuFlag: isObu }, function(tota, isError) {
				if (!isError) {
					$scope.assetList = tota[0].body.assetList;
					$scope.secCustCrossSelling = tota[0].body.secCustCrossSelling;
				}
				defer.resolve("");
			});
            return defer.promise;
        };

        $scope.getCustHomeInclude = function (path) {
        	switch (path) {
	        	case "CRM810" :
	                $scope.custDepositVO.crm811_amt = $scope.crm811_amt;
	                $scope.custDepositVO.crm812_amt = $scope.crm812_amt;
	                $scope.custDepositVO.crm813_amt = $scope.crm813_amt;
	                $scope.custDepositVO.cur_list = $scope.cur_list;
	                $scope.custDepositVO.no_cur_list = $scope.no_cur_list;
	                $scope.$emit("CRM610VO", {action: "set", type: "CRM681_CUSTDEPOSIT", data: $scope.custDepositVO});
	                $scope.connector("set", "CRM681_CUSTDEPOSIT", $scope.custDepositVO);
	        		break;
	        	case "CRM820" :
	                $scope.custInvestmentVO.total = $scope.investment;
	                $scope.custInvestmentVO.crm821 = $scope.crm821;
	                $scope.custInvestmentVO.crm822 = $scope.crm822;
	                $scope.custInvestmentVO.crm823 = $scope.crm823;
	                $scope.custInvestmentVO.crm824 = $scope.crm824;
	                $scope.custInvestmentVO.crm825 = $scope.crm825;
	                $scope.custInvestmentVO.crm826 = $scope.crm826;
	                $scope.custInvestmentVO.crm827 = $scope.crm827;
	                $scope.custInvestmentVO.crm828 = $scope.crm828;
	                $scope.custInvestmentVO.crm829 = $scope.crm829;
	                $scope.custInvestmentVO.crm82A = $scope.crm82A;
	                $scope.custInvestmentVO.other = $scope.other;
	                $scope.$emit("CRM610VO", {action: "set", type: "CRM681_Investment", data: $scope.custInvestmentVO});
	                $scope.connector("set", "CRM681_Investment", $scope.custInvestmentVO);
	        		break;
	        	case "CRM870" :
	        		$scope.custSecVO.total = $scope.sec;
	                $scope.custSecVO.crm871AUM 			= $scope.crm871AUM;
	                $scope.custSecVO.crm871INVEST_AUM 	= $scope.crm871INVEST_AUM;
	                $scope.custSecVO.crm871BENEFIT_AMT1 = $scope.crm871BENEFIT_AMT1;
	                $scope.custSecVO.crm871BENEFIT_AMT2 = $scope.crm871BENEFIT_AMT2;
	                $scope.custSecVO.crm872AUM 			= $scope.crm872AUM;
	                $scope.custSecVO.crm872INVEST_AUM 	= $scope.crm872INVEST_AUM;
	                $scope.custSecVO.crm872BENEFIT_AMT1 = $scope.crm872BENEFIT_AMT1;
	                $scope.custSecVO.crm872BENEFIT_AMT2 = $scope.crm872BENEFIT_AMT2;
	                $scope.custSecVO.crm873AUM 			= $scope.crm873AUM;
	                $scope.custSecVO.crm873INVEST_AUM 	= $scope.crm873INVEST_AUM;
	                $scope.custSecVO.crm873BENEFIT_AMT1 = $scope.crm873BENEFIT_AMT1;
	                $scope.custSecVO.crm873BENEFIT_AMT2 = $scope.crm873BENEFIT_AMT2;
	                $scope.custSecVO.crm874AUM 			= $scope.crm874AUM;
	                $scope.custSecVO.crm874INVEST_AUM 	= $scope.crm874INVEST_AUM;
	                $scope.custSecVO.crm874BENEFIT_AMT1 = $scope.crm874BENEFIT_AMT1;
	                $scope.custSecVO.crm874BENEFIT_AMT2 = $scope.crm874BENEFIT_AMT2;
 	                $scope.$emit("CRM610VO", {action: "set", type: "CRM681_Sec", data: $scope.custSecVO});
	                $scope.connector("set", "CRM681_Sec", $scope.custSecVO);
	        		break;
        	}
            var url = "assets/txn/" + path + "/" + path + ".html";
            $scope.connector("set", "CRM610URL", url); // 這裡待處理,避免多開情況互相影響.
            $scope.$emit("CRM610VO", {action: "set", type: "URL", data: url});
        };

        $scope.getCustHomeInclude1 = function (index) {
            var index = index;
            var url = "assets/txn/CRM840/CRM840.html";
            $scope.connector("set", "CRM840_tab", index);
            $scope.connector("set", "CRM610URL", url);
            $scope.$emit("CRM610VO", {action: "set", type: "URL", data: url});
        };

        $scope.loanIncrementInfo = function () {
            var url = "assets/txn/CRM682/CRM682.html";
            $scope.connector("set", "CRM610URL", url);
            $scope.$emit("CRM610VO", {action: "set", type: "URL", data: url});
        }

        $scope.goCRM683 = function () {
            var url = "assets/txn/CRM683/CRM683.html";
            $scope.connector("set", "CRM610URL", url);
            $scope.$emit("CRM610VO", {action: "set", type: "URL", data: url});
        }

        $scope.FORTUNA_GO = function (DataRow, IFTYPE) {
            if (IFTYPE) {
                if (IFTYPE == '1') {
                    projInfoService.fromFPS814 = true;
                    $scope.connector('set', 'FPS300', {'custID': $scope.custVO.CUST_ID});
//					$scope.connector('set','CRM681_PLAN_ID', DataRow.PLAN_ID_1);
                    // 原路徑 2018/5/11
                    var oldPath = $rootScope.menuItemInfo.txnPath;
                    $scope.GeneratePage({'txnName': "FPS300", 'txnId': "FPS300", 'txnPath': oldPath});
                } else if (IFTYPE == '2') {
                    $scope.connector('set', 'CRM121_CUST_PLAN', {'custID': $scope.custVO.CUST_ID});
//					$scope.connector('set','CRM121_CUST_PLAN', DataRow.PLAN_ID_2);
                    // 原路徑 2018/5/11
                    var oldPath = $rootScope.menuItemInfo.txnPath;
                    $scope.GeneratePage({'txnName': "FPS200", 'txnId': "FPS200", 'txnPath': oldPath});
                } else if (IFTYPE == '3') {
                    projInfoService.fromFPS814 = true;
                    $scope.connector('set', 'FPS300', {'custID': $scope.custVO.CUST_ID});
                    // 原路徑 2018/5/11
                    var oldPath = $rootScope.menuItemInfo.txnPath;
                    $scope.GeneratePage({'txnName': "FPS300", 'txnId': "FPS300", 'txnPath': oldPath});
                } else if (IFTYPE == '4') {
                    $scope.connector('set', 'INS400', {'custID': $scope.custVO.CUST_ID});
                    var oldPath = $rootScope.menuItemInfo.txnPath;
                    $scope.GeneratePage({'txnName': "INS400", 'txnId': "INS400", 'txnPath': oldPath});
                }
            } else {
                if (DataRow.TYPE == 'MONEY_BIG' || DataRow.TYPE == 'MONEY_LOW') {
                    $scope.connector('set', 'CRM121_CUST_PLAN', {'custID': $scope.custVO.CUST_ID});
                    // 原路徑 2018/5/11
                    var oldPath = $rootScope.menuItemInfo.txnPath;
                    $scope.GeneratePage({'txnName': "FPS200", 'txnId': "FPS200", 'txnPath': oldPath});
                } else if (DataRow.TYPE == 'SPP_GO') {
                    projInfoService.fromFPS814 = true;
                    $scope.connector('set', 'FPS300', {'custID': $scope.custVO.CUST_ID});
                    // 原路徑 2018/5/11
                    var oldPath = $rootScope.menuItemInfo.txnPath;
                    $scope.GeneratePage({'txnName': "FPS300", 'txnId': "FPS300", 'txnPath': oldPath});
                } else if (DataRow.TYPE == 'PORT_GO') {
                    projInfoService.fromFPS814 = true;
                    $scope.connector('set', 'FPS300', {'custID': $scope.custVO.CUST_ID});
                    // 原路徑 2018/5/11
                    var oldPath = $rootScope.menuItemInfo.txnPath;
                    $scope.GeneratePage({'txnName': "FPS300", 'txnId': "FPS300", 'txnPath': oldPath});
                }
            }
            
            $scope.closeThisDialog('successful');
        };

        function statistics() {
        	//
            $scope.custDepositVO.crm811_amt = $scope.crm811_amt + $scope.crm811_fs_amt;
            $scope.custDepositVO.crm812_amt = $scope.crm812_amt;
            $scope.custDepositVO.crm813_amt = $scope.crm813_amt;
            $scope.custDepositVO.cur_list = $scope.cur_list;
            $scope.custDepositVO.no_cur_list = $scope.no_cur_list;
            
            $scope.$emit("CRM610VO", {action: "set", type: "CRM681_CUSTDEPOSIT", data: $scope.custDepositVO});

            //
            $scope.custInvestmentVO.total = $scope.investment;
            $scope.custInvestmentVO.crm821 = $scope.crm821;
            $scope.custInvestmentVO.crm822 = $scope.crm822;
            $scope.custInvestmentVO.crm823 = $scope.crm823;
            $scope.custInvestmentVO.crm824 = $scope.crm824;
            $scope.custInvestmentVO.crm825 = $scope.crm825;
            $scope.custInvestmentVO.crm826 = $scope.crm826;
            $scope.custInvestmentVO.crm827 = $scope.crm827;
            $scope.custInvestmentVO.crm828 = $scope.crm828;
            $scope.custInvestmentVO.crm829 = $scope.crm829;
            $scope.custInvestmentVO.crm82A = $scope.crm82A;
            $scope.custInvestmentVO.other = $scope.other;
            
            $scope.$emit("CRM610VO", {action: "set", type: "CRM681_Investment", data: $scope.custInvestmentVO});
            
            //
            $scope.custSecVO.total = $scope.sec;
            
            $scope.custSecVO.crm871AUM = $scope.crm871AUM;
            $scope.custSecVO.crm871INVEST_AUM = $scope.crm871INVEST_AUM;
            $scope.custSecVO.crm871BENEFIT_AMT1 = $scope.crm871BENEFIT_AMT1;
            $scope.custSecVO.crm871BENEFIT_AMT2 = $scope.crm871BENEFIT_AMT2;
            
            $scope.custSecVO.crm872AUM = $scope.crm872AUM;
            $scope.custSecVO.crm872INVEST_AUM = $scope.crm872INVEST_AUM;
            $scope.custSecVO.crm872BENEFIT_AMT1 = $scope.crm872BENEFIT_AMT1;
            $scope.custSecVO.crm872BENEFIT_AMT2 = $scope.crm872BENEFIT_AMT2;
            
            $scope.custSecVO.crm873AUM = $scope.crm873AUM;
            $scope.custSecVO.crm873INVEST_AUM = $scope.crm873INVEST_AUM;
            $scope.custSecVO.crm873BENEFIT_AMT1 = $scope.crm873BENEFIT_AMT1;
            $scope.custSecVO.crm873BENEFIT_AMT2 = $scope.crm873BENEFIT_AMT2;
            
            $scope.custSecVO.crm874AUM = $scope.crm874AUM;
            $scope.custSecVO.crm874INVEST_AUM = $scope.crm874INVEST_AUM;
            $scope.custSecVO.crm874BENEFIT_AMT1 = $scope.crm874BENEFIT_AMT1;
            $scope.custSecVO.crm874BENEFIT_AMT2 = $scope.crm874BENEFIT_AMT2;
            
            $scope.$emit("CRM610VO", {action: "set", type: "CRM681_Sec", data: $scope.custSecVO});
        }
        
		//#1913_取得基金總資產
		function getFundDeposit(isObu) {
			var defer = $q.defer();
			$scope.sendRecv("CRM821", "getFundDeposit", "com.systex.jbranch.app.server.fps.crm821.CRM821InputVO", { cust_id: $scope.custVO.CUST_ID, isOBU: isObu },
				function(tota, isError) {
					let fundDeposit = 0;
					if (!isError) {
						fundDeposit = tota[0].body.fundAmount;
					}
					defer.resolve(fundDeposit);
				});
			return defer.promise;
		};

		//#1913_海外ETF/海外股票總資產
		function getETFStockDeposit() {
			var defer = $q.defer();
			$scope.sendRecv("CRM822", "getETFStockDeposit", "com.systex.jbranch.app.server.fps.crm822.CRM822InputVO", { cust_id: $scope.custVO.CUST_ID },
				function(tota, isError) {
					let etfStockDeposit = 0;
					if (!isError) {
						etfStockDeposit = tota[0].body.etfStockAmount;
					}
					defer.resolve(etfStockDeposit);
				});
			return defer.promise;
		};

		//#1913_取得 海外債 & SN 總資產
		function getBondSnDeposit(isObu) {
			var defer = $q.defer();
			$scope.sendRecv("CRM823", "getBondSnDeposit", "com.systex.jbranch.app.server.fps.crm823.CRM823InputVO", { cust_id: $scope.custVO.CUST_ID, isOBU: isObu },
				function(tota, isError) {
					let obj = { bondDeposit: 0, snDeposit: 0 };
					if (!isError) {
						obj = { bondDeposit: tota[0].body.bondAmount, snDeposit: tota[0].body.snAmount };
					}
					defer.resolve(obj);
				});
			return defer.promise;
		};

		//#1913_取得SI總資產
		function getSIDeposit() {
			var defer = $q.defer();
			$scope.sendRecv("CRM824", "getSIDeposit", "com.systex.jbranch.app.server.fps.crm824.CRM824InputVO", { "cust_id": $scope.custVO.CUST_ID },
				function(tota, isError) {
					let siDeposit = 0;
					if (!isError) {
						siDeposit = tota[0].body.siAmount;
					}
					defer.resolve(siDeposit);
				});
			return defer.promise;
		};

        //初始化
        function init() {
            /* 投資
             * $scope.crm821 =>
			 * $scope.crm822 =>
			 * $scope.crm823 => D1/D2/U1/U2/U3/UJ 國內代理基金/國內債券型代理基金/海外基金/國內基金/國內債券基金/海外債(海外商品)
			 * $scope.crm824 => F1/T1 外幣組合式商品/台幣組合式商品
			 * $scope.crm825 => SN
			 * $scope.crm826 => SD(DCD)
			 * $scope.crm827 => 58 金錢信託
			 * $scope.crm828 => 
			 * $scope.crm829 => 
			 * $scope.crm82A => 
		 	 * $scope.other  => 51/52/59/60
             */
            $scope.investment = 0;	
            
            $scope.inputVO.isOBU = crm610Data.isObu;
            
            $scope.custInvestmentVO = {
                total: undefined,
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
                other: undefined
            }
            
            /* 存款 */
            $scope.deposit = undefined;
            
            $scope.custDepositVO = {
                crm811_amt: undefined,
                crm812_amt: undefined,
                crm813_amt: undefined,
                cur_list: [],
                no_cur_list: []
            }
            
            /* 保險 */
            $scope.insurance = undefined;

            /* 有價證券信託
             * $scope.crm683 => 40 
             */
            $scope.crm683 = 0; 		//有價證券信託
            
            /* 證券往來 */
            $scope.sec = 0; 		//證券往來
            
            $scope.custSecVO = {
            	total: undefined,
            	crm871AUM: undefined,
                crm871INVEST_AUM: undefined,
                crm871BENEFIT_AMT1: undefined,
                crm871BENEFIT_AMT2: undefined,
                crm872AUM: undefined,
                crm872INVEST_AUM: undefined,
                crm872BENEFIT_AMT1: undefined,
                crm872BENEFIT_AMT2: undefined,
                crm873AUM: undefined,
                crm873INVEST_AUM: undefined,
                crm873BENEFIT_AMT1: undefined,
                crm873BENEFIT_AMT2: undefined, 
                crm874AUM: undefined,
                crm874INVEST_AUM: undefined,
                crm874BENEFIT_AMT1: undefined,
                crm874BENEFIT_AMT2: undefined
            }

            /* 負債 */
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
            $scope.FuAll = 0;
            
            $scope.resultList = [];

            console.log("CRM681 $scope.inputVO.cust_id:" + $scope.inputVO.cust_id);
            console.log("CRM681 $scope.custVO.CUST_ID:" + $scope.custVO.CUST_ID);
            console.log("CRM681 starts:" + new Date());
            
            $q.all({
                credit: getCustCreditData(),
                insurance: inquire(),
                nano: getNanoAsset(),
                gold: inquireGold(),
                goldBond: getGoldBondAsset(),

                debt: getCustAssetInvestData2(crm610Data.acctData),
                deposit: getCustAssetDepositData(crm610Data.acctData),
				
				//#1913
                getAsset: getCustAssetInvestData($scope.inputVO.isOBU),
				fundDeposit: getFundDeposit($scope.inputVO.isOBU),			//基金 總資產
				etfStockDeposit: getETFStockDeposit(),				    	//海外ETF/海外股票 總資產
				bondSnDeposit: getBondSnDeposit($scope.inputVO.isOBU),	    //海外債 && SN 總資產
				siDeposit: getSIDeposit()									//SI 總資產
            }).then(function (result) {
                getFu(result.debt);
                getSUM(result);
                statistics();
            });
        };

        // CRM681 如果是由客戶首頁 include 的，則監聽來自於客戶首頁的廣播，以取得已載入的共用資料
        let crm610Data = {}
        if ($scope.fromCRM610) {
            $scope.$on('CRM610_DATA', function (event, data) {
                console.log('【資產負債總覽】已收到【客戶首頁】的廣播...');
                crm610Data = data;
                init();
            });
        } else {
            init();
        }
    }
);