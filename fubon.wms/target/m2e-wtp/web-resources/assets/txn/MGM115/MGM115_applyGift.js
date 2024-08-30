/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('MGM115_applyGiftController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService, $q, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "MGM115_applyGiftController";
		
		// filter
		getParameter.XML(["MGM.GIFT_KIND"], function(totas) {
			if (totas) {
				$scope.mappingSet['MGM.GIFT_KIND'] = totas.data[totas.key.indexOf('MGM.GIFT_KIND')];
			}
		});
		
		//初始化
		$scope.init = function() {
			
			$scope.resultList = [];
			$scope.totalGiftList = [];
			$scope.giftList =  [];
			$scope.outputVO = [];
			$scope.receiverInfo = {};
			
			if($scope.connector('get', 'MGM110_inquireVO') != null){
				//初始化$scope.inputVO
				$scope.inputVO = $scope.connector('get', 'MGM110_inquireVO');
				if($scope.inputVO.act_seq != undefined && $scope.inputVO.act_seq != null && $scope.inputVO.act_seq != ''){
					$scope.inputVO.gift_kind = '1';
					$scope.inputVO.cust_id = $scope.mgm115.CUST_ID;
					$scope.inputVO.remain = $scope.mgm115.REMAIN;
					
					//查詢活動名稱&活動適用贈品
					$scope.sendRecv("MGM115", "getActInfo", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", 
						{'act_seq': $scope.inputVO.act_seq, 'cust_id' : $scope.inputVO.cust_id},
							function(tota, isError) {
								if (!isError) {
									$scope.act_name = tota[0].body.resultList[0].ACT_NAME;
									
									$scope.totalGiftList = tota[0].body.giftList;
									$scope.outputVO = tota[0].body;
									
									if($scope.inputVO.gift_kind != undefined ){
										angular.forEach(tota[0].body.giftList, function(row){
											if(row.GIFT_KIND == $scope.inputVO.gift_kind){
												$scope.giftList.push(row);
											}
										});
									}else{
										$scope.giftList = tota[0].body.giftList;
									}
									
									return;
								}
					});
					
					//查詢客戶基本資訊
					$scope.sendRecv("MGM115", "getCustInfo", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'cust_id' : $scope.inputVO.cust_id},
							function(tota, isError) {
								if (!isError) {
									$scope.bra_nbr = tota[0].body.resultList[0].BRA_NBR;
									$scope.ao_code = tota[0].body.resultList[0].AO_CODE;
									$scope.emp_name = tota[0].body.resultList[0].EMP_NAME;
									$scope.cust_name = tota[0].body.resultList[0].CUST_NAME;
									return;
								}
					});
					
//					//查詢收件人資訊
//					$scope.sendRecv("MGM115", "getReceiverInfo", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", {'cust_id' : $scope.inputVO.cust_id},
//							function(tota, isError) {
//								if (!isError) {
//									$scope.receiverInfo = tota[0].body.resultList[0];
//									$scope.changeRecInfo();
//									return;
//								}
//					});
					
				}
			}
		}
		
		$scope.init();
		
//		//領用方式連動贈品選項、收件人資訊
//		$scope.changeGetWay = function() {
//			if($scope.inputVO.gift_get_way != undefined ){
//				$scope.giftList =  [];
//				$scope.outputVO = [];
//				angular.forEach($scope.totalGiftList, function(row){
//					if(row.GIFT_KIND == $scope.inputVO.gift_kind){
//						$scope.giftList.push(row);
//					}
//				});
////				$scope.changeRecInfo();
//			}
//		}
		
		//領用方式連動贈品選項
		$scope.changeGiftKind = function() {
			if($scope.inputVO.gift_kind != undefined ){
				$scope.giftList =  [];
				$scope.outputVO = [];
				angular.forEach($scope.totalGiftList, function(row){
					if(row.GIFT_KIND == $scope.inputVO.gift_kind){
						$scope.giftList.push(row);
					}
				});
				
//				$scope.changeRecInfo();
			}
		}
		//領用方式連動收件人資訊
//		$scope.changeRecInfo = function() {
//			if($scope.inputVO.gift_get_way != undefined ){
//				if($scope.inputVO.gift_get_way == '1') {			//分行親領
//					
//					if($scope.ao_code == null){						//空Code客戶
//						$scope.inputVO.rec_name = $scope.receiverInfo.SPV_EMP_NAME;
//						
//					} else {										//有Code客戶
//						$scope.inputVO.rec_name = $scope.emp_name;
//					}
//					
//					$scope.inputVO.rec_tel_no = $scope.receiverInfo.TEL_NO_MAIN;
//					$scope.inputVO.rec_mobile_no = null;
//					$scope.inputVO.address = '(' + $scope.receiverInfo.ZIP_COD + ')'
//												+ $scope.receiverInfo.CHIN_ADDR
//												+ '【' + $scope.receiverInfo.CHIN_FL_NAME + '】';
//					
//					
//				} else if ($scope.inputVO.gift_get_way == '2') {	//郵寄
//					$scope.inputVO.rec_name = $scope.receiverInfo.CUST_NAME;
//					$scope.inputVO.rec_tel_no = $scope.receiverInfo.DAY_COD;
//					$scope.inputVO.rec_mobile_no = $scope.receiverInfo.TEL_NO;
//					$scope.inputVO.address = $scope.receiverInfo.CUST_ADDR;
//					
//				} else if ($scope.inputVO.gift_get_way == '3') {	//電子券號/刷卡金
//					$scope.inputVO.rec_name = $scope.receiverInfo.CUST_NAME;
//					$scope.inputVO.rec_tel_no = null;
//					if($scope.receiverInfo.TEL_NO != null && $scope.receiverInfo.TEL_NO != ''){
//						$scope.inputVO.rec_mobile_no = $scope.receiverInfo.TEL_NO;						
//					} else {
//						$scope.showErrorMsg("推薦人未於系統內留存國內可收簡訊之手機門號，如欲兌換電子序號，請先至新端末系統內維護或致電客服經客戶確認留資後更新！");
//					}
//					$scope.inputVO.address = null;
//				}											
//			}
//		}
		
		//選取欲兌換贈品
		$scope.addGiftList = function() {
			$scope.errFlag = false;
			$scope.inputVO.exchange_points = null;
			$scope.inputVO.exchange_reward = null;
			$scope.inputVO.exchange = null;
			$scope.inputVO.apply_gift_list = [];
			
	    	angular.forEach($scope.giftList, function(row){
	    		if (row.ADD == true) {
	    			if(row.APPLY_NUMBER == '' || row.APPLY_NUMBER == undefined){
	    				$scope.inputVO.apply_gift_list = [];
	    				$scope.errFlag = true;
	    				$scope.showErrorMsg("請輸入兌換數量。");
	    				return;
	    			}
	    			
	    			$scope.inputVO.exchange += (row.GIFT_EXC_UNI * row.APPLY_NUMBER);
	    			$scope.inputVO.apply_gift_list.push(row);
	    		}
	    	});
	    	
	    	if($scope.inputVO.exchange > $scope.inputVO.remain){
	    		$scope.inputVO.apply_gift_list = [];
				$scope.errFlag = true;
	    		if($scope.inputVO.act_type == 'M'){
	    			$scope.showErrorMsg("兌換點數超過可兌換點數，請重新輸入。");
    				return;
	    		} else if ($scope.inputVO.act_type == 'V') {
	    			$scope.showErrorMsg("兌換金額超過可兌換金額，請重新輸入。");
    				return;
	    		}
	    	}
		}
		
		//確認兌換
		$scope.apply = function() {
			//檢核兌換贈品
			$scope.addGiftList();
			
			if($scope.inputVO.apply_gift_list.length > 0){
//				var warning = '';
//				if($scope.inputVO.gift_get_way == '1'){
//					warning = '請務必另約客戶時間致贈並回收客戶親簽之贈品簽收單。';
//				} else if ($scope.inputVO.gift_get_way == '2'){
//					warning = '請務必確認推薦人同意本案贈品廠商於贈品配送時以客戶留存本行之聯繫方式(包括電話/手機與通訊地址)進行配送事宜。另請務必另約客戶時間請客戶親簽贈品簽收單並回收，以保障雙方權益。';
//				} else if ($scope.inputVO.gift_get_way == '3'){
//					warning = '請務必確認推薦人於本行留存之聯絡手機門號為國內可收簡訊之門號，電子序號一經發送不可退回！';
//				}
//				
//				$confirm({text: warning}, {size: 'sm' }).then(function () {
//					$scope.sendRecv("MGM115", "apply", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
//							function(tota, isError) {
//								if (!isError) {
//									$scope.showSuccessMsg("ehl_01_common_023");		//執行成功
//									$scope.closeThisDialog('successful');
//								}
//					});	
//				});
				
				//若選擇兌換[刷卡金]，需檢核(電文：CCM7818)客戶是否持有有效信用卡。
				if($scope.inputVO.gift_kind == '1'){
					$scope.sendRecv("CRM846", "initial", "com.systex.jbranch.app.server.fps.crm846.CRM846InputVO", 
						{'cust_id': $scope.inputVO.cust_id},
							function(tota, isError) {
								if (!isError) {
									if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
										$scope.showErrorMsg("此客戶未持有有效信用卡。");
			                			return;
			                		}
									if(tota[0].body.resultList.length > 0 && tota[0].body.resultList[0].CARD_NUMBER_S_TOTAL > 0) {
//										alert("持有有效信用卡!");
//										alert(tota[0].body.resultList[0].CARD_NUMBER_S_TOTAL > 0);
//										alert(JSON.stringify(tota[0].body.resultList));
										$scope.sendRecv("MGM115", "apply", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
												function(tota, isError) {
											if (!isError) {
												$scope.showSuccessMsg("ehl_01_common_023");		//執行成功
												$scope.closeThisDialog('successful');
											}
										});
			                		} else {
			                			$scope.showErrorMsg("此客戶未持有有效信用卡。");
			                			return;
			                		}
								}
					});		
				} else {
					$scope.sendRecv("MGM115", "apply", "com.systex.jbranch.app.server.fps.mgm110.MGM110InputVO", $scope.inputVO,
							function(tota, isError) {
						if (!isError) {
							$scope.showSuccessMsg("ehl_01_common_023");		//執行成功
							$scope.closeThisDialog('successful');
						}
					});						
				}
			} else {
				if(!$scope.errFlag) {
					$scope.showErrorMsg("請選擇兌換贈品。");
					return;					
				}
			}
		}
});
