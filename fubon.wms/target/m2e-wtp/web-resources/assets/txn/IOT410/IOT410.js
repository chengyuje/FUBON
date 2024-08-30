/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT410Controller', function($scope, $controller, getParameter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "IOT410Controller";
	
	//XML參數
	getParameter.XML(["CALLOUT_STATUS", "CALLOUT.FAIL_REASON", "CALLOUT.FAIL_TYPE"],function(totas){
		if(totas){
			$scope.mappingSet['CALLOUT_STATUS'] = totas.data[totas.key.indexOf('CALLOUT_STATUS')];				// 電訪狀態
			$scope.mappingSet['CALLOUT.FAIL_REASON'] = totas.data[totas.key.indexOf('CALLOUT.FAIL_REASON')];	// 未成功原因
			$scope.mappingSet['CALLOUT.FAIL_TYPE'] = totas.data[totas.key.indexOf('CALLOUT.FAIL_TYPE')];		// 失敗異常分類
		}
	});
	
	$scope.init = function(){
		$scope.mappingSet['IOT.CALLOUT_STATUS'] = [];
		$scope.mappingSet['IOT.CALLOUT_STATUS'].push({DATA: '1', LABEL: '未申請'},
													 {DATA: '3', LABEL: '電訪處理中'},
													 {DATA: '4', LABEL: '電訪成功'},
													 {DATA: '5', LABEL: '電訪未成功'},
													 {DATA: '6', LABEL: '電訪疑義'},
													 {DATA: '7', LABEL: '取消電訪'},
													 {DATA: '8', LABEL: '退件處理-契撤'});
		
		$scope.rejectList = [];
		$scope.showWarningMsg = false;
		$scope.sendRecv("IOT410","getCallOut","com.systex.jbranch.app.server.fps.iot410.IOT410InputVO", $scope.inputVO,
		function(tota, isError) {
			if (!isError) {
				if (tota[0].body.resultList == undefined || tota[0].body.resultList.length == 0) {
					$scope.closeThisDialog('cancel');
					$scope.showMsg("資料有誤，請洽保險商品處。");
					return;
				}
				$scope.inputVO = tota[0].body.resultList[0];
				
				// 判斷是否需輸入錄音序號 S01.CUST_ID, S01.INSURED_ID, S01.PAYER_ID,
				$scope.cFlag = false;
				$scope.iFlag = false;
				$scope.pFlag = false;
				if ($scope.inputVO.C_PREMIUM_TRANSSEQ_YN == undefined || $scope.inputVO.C_PREMIUM_TRANSSEQ_YN != 'Y') {
					$scope.cFlag = true;
				}
				if ($scope.inputVO.I_PREMIUM_TRANSSEQ_YN == undefined || $scope.inputVO.I_PREMIUM_TRANSSEQ_YN != 'Y' || 
					$scope.inputVO.INSURED_ID == $scope.inputVO.CUST_ID) {
					$scope.iFlag = true;
				}
				if ($scope.inputVO.P_PREMIUM_TRANSSEQ_YN == undefined || $scope.inputVO.P_PREMIUM_TRANSSEQ_YN != 'Y' || 
					$scope.inputVO.PAYER_ID == $scope.inputVO.CUST_ID || $scope.inputVO.PAYER_ID == $scope.inputVO.INSURED_ID ) {
					$scope.pFlag = true;
				}
				
				// 若該案有多筆同案件編號且電訪記錄之電訪狀態有「電訪疑義」「取消投保」之記錄，需產生提醒訊息
				$scope.showWarningMsg = tota[0].body.showMsg;
				$scope.rejectList = tota[0].body.rejectList;
			}
		});
	}
	
	$scope.keySEQ = function(type){
		if (type == 'C') {
			if ($scope.inputVO.I_PREMIUM_TRANSSEQ_YN == 'Y' && $scope.inputVO.CUST_ID == $scope.inputVO.INSURED_ID) {
				$scope.inputVO.I_PREMIUM_TRANSSEQ = $scope.inputVO.C_PREMIUM_TRANSSEQ;
			}
			if ($scope.inputVO.P_PREMIUM_TRANSSEQ_YN == 'Y' && $scope.inputVO.CUST_ID == $scope.inputVO.PAYER_ID) {
				$scope.inputVO.P_PREMIUM_TRANSSEQ = $scope.inputVO.C_PREMIUM_TRANSSEQ;
			}
		} else if (type == 'I') {
			if ($scope.inputVO.P_PREMIUM_TRANSSEQ_YN == 'Y' && $scope.inputVO.INSURED_ID == $scope.inputVO.PAYER_ID) {
				$scope.inputVO.P_PREMIUM_TRANSSEQ = $scope.inputVO.I_PREMIUM_TRANSSEQ;
			}
		}
	}
	
	// 若有輸入錄音序號，檢核錄音序號第7、8碼數字是否為73，若≠73，則產生提醒訊息：「請確認錄音序號是否正確」
	$scope.checkSeq = function(type){
		var seq = $scope.inputVO[type + '_PREMIUM_TRANSSEQ'];
		if (seq != undefined) {
			if (seq.length < 11) {
				$scope.showErrorMsg('請確認錄音序號是否正確');
			} else {
				var flag = transSeq.substring(6, 8);
				if (flag !== '73') {
					$scope.showErrorMsg('請確認錄音序號是否正確');
				}
			}
		}
	}
	
	// 若有輸入錄音序號，檢核錄音序號第7、8碼數字是否為73，若≠73，則產生提醒訊息：「請確認錄音序號是否正確」
	$scope.checkSave = function(){
		var flag = true;
		// 拉選「電訪狀態=電訪成功」，需判斷應電訪對象之錄音序號是否有值，若無值則產生提醒訊息：「請登打錄音序號」
		if ($scope.inputVO.STATUS == '4') {
			var types = ['C', 'I', 'P'];
			for (var i = 0; i < types.length; i++) {
				var transSeq = $scope.inputVO[types[i] + '_PREMIUM_TRANSSEQ'];
//				if ($scope.inputVO[types[i] + '_NEED_CALL_YN'] == 'Y' && (transSeq == undefined || transSeq == '')) {
				if ($scope.inputVO[types[i] + '_PREMIUM_TRANSSEQ_YN'] == 'Y' && (transSeq == undefined || transSeq == '')) {
					$scope.showErrorMsg('請登打錄音序號');
					flag = false;
					break;
				}
			}
			
			if (flag) {
				for (var i = 0; i < types.length; i++) {
					var transSeq = $scope.inputVO[types[i] + '_PREMIUM_TRANSSEQ'];
					if (transSeq != undefined) {
						if (transSeq.length < 12) {
							$scope.showErrorMsg('請確認錄音序號是否正確');
							flag = false;
							break;
						} else {
							var flag = transSeq.substring(6, 8);
							if (flag !== '73') {
								$scope.showErrorMsg('請確認錄音序號是否正確');
								flag = false;
								break;
							}
						}
					}
				}
				return flag;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	$scope.save = function(){
		if ($scope.checkSave()) {
			$scope.sendRecv("IOT410","save","com.systex.jbranch.app.server.fps.iot410.IOT410InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.showSuccessMsg('ehl_01_common_025'); 	//儲存成功
					
					// 拉選「電訪狀態=未申請」，一併產生提醒訊息：「案件退回RM/OP，請通知RM/OP人員」
					if ($scope.inputVO.STATUS == '1') {
						$scope.showMsg('案件退回RM/OP，請通知RM/OP人員');
					}
					
					$scope.closeThisDialog('successful');
				}
			});
		}
	}
	
	$scope.init();
});