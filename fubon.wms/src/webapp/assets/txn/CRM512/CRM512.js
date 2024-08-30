'use strict';
eSoafApp.controller('CRM512Controller', function ($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $timeout) {
        
	$controller('BaseController', {$scope: $scope});
	
	$scope.controllerName = "CRM512Controller";

    $scope.init = function () {
		$scope.priID = String(sysInfoService.getPriID());
		
		$scope.inputVO = {					
			custID            	: '',
			fromPRD			  	: '',
			qusBankListBefore 	: [],
			qusBankListAfter  	: [], 
			goExpDisabled	  	: false	// 避免連續點擊
    	};
		debugger
		if($scope.fromPRD == "Y") {
			//由商品查詢、下單過來
			$scope.inputVO.fromPRD = $scope.fromPRD;
			$scope.inputVO.custID = $scope.custId;
			$scope.inputVO.isAnsChanged = false;
			$scope.inputVO.initLoad = true;
		}
		
		$scope.qusBankListBefore = [];	
		$scope.qusBankListAfter  = [];	
		$scope.outputVO = [];
	};
	
	$scope.init();
	
	
	$scope.getQusBankList = function () {
		if($scope.inputVO.custID == null || $scope.inputVO.custID == '' || $scope.inputVO.custID == undefined)
			return;
		
		$scope.sendRecv("CRM512", "getQusBankList", "com.systex.jbranch.app.server.fps.crm512.CRM512InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.qusBankList.length == 0) {
					return;
	    		}
				
				$scope.qusBankListAfter = tota[0].body.qusBankList;
				$scope.outputVO = tota[0].body;
				
				angular.forEach($scope.qusBankListAfter, function(row, index, objs){
						row.priList = [];
						if (row.CAN_MODIFY_PRI_LIST != "" && row.CAN_MODIFY_PRI_LIST != undefined) {
							row.priList = row.CAN_MODIFY_PRI_LIST.split(';');
						}
	
						row.ansDisabled = true;
						for (var i = 0; i <= (row.priList).length; i++) {
							if ($scope.priID == row.priList[i]) {
								row.ansDisabled = false;
							}
						}
				});
				
				$scope.qusBankListBefore = angular.copy(tota[0].body.qusBankList);	
				$scope.checkOption();
			}						
		});
	};
	$scope.getQusBankList();
	
	$scope.callSaveFunc = function () {
		$scope.inputVO.qusBankListBefore = $scope.qusBankListBefore;
		$scope.inputVO.qusBankListAfter  = $scope.qusBankListAfter;
		
		$scope.sendRecv("CRM512", "save", "com.systex.jbranch.app.server.fps.crm512.CRM512InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
            	$scope.showErrorMsgInDialog(totas.body.msgData);
                return;
            } else {
            	$scope.showMsg('儲存成功');
            	if($scope.inputVO.fromPRD == "Y") {
            		//適配、下單過來的
            		if($scope.inputVO.statusChanged) {
						//客戶狀態已有變化
						$scope.closeThisDialog('statusChanged');
					} else {
						$scope.closeThisDialog('successful');
					}
            	} else {
            		$scope.getQusBankList();
            	}
            }
		});
	}
	
	$scope.save = function (statusChanged) {
		//客戶狀態已有變化
		$scope.inputVO.statusChanged = statusChanged;
		
		var requiredYN = true;	// 必填項目是否已全填
		
		angular.forEach($scope.qusBankListAfter, function(row, index, objs){
			var ansLength = (row.answer).length;										// 該題答案總數
			var ansNGCheckLength = 0;													// 該題未填總數
			var ansConflictOption = 0;													// 有衝突選項(無上述情形)
			angular.forEach(row.answer, function(ansRow, ansIndex, ansObjs){
				if (ansRow.ANSWER_NGCHECK == 'N') {
					ansNGCheckLength++;
				}
				
				if (ansRow.ANSWER_NGCHECK == 'Y' && ansRow.ANSWER_FLAG == 'Y') {		// 有勾「無上述情形」
					ansConflictOption++;
				}
			});
			
			if (row.REQUIRED_YN == 'Y' && ansLength == ansNGCheckLength) {				// 該題為必填題目，且 該題答案總數 = 該題未填總數 (等於未填答)
				$scope.showErrorMsg("【" + row.QUESTION_NAME_NAME + "】為必填項目");
				requiredYN = false;
				
				return;
			} else if (ansConflictOption == 1 && (ansLength - ansNGCheckLength) > 1){	// 有勾「無上述情形」，且 該題答案總數 - 該題未填總數 > 1 (應只勾無上述情形才對)
				$scope.showErrorMsg("【" + row.QUESTION_NAME_NAME + "】已勾選「無上述情形」，其它選項不得勾選，請重新確認。");
				requiredYN = false;
				
				return;
			}
		});
		
		var doubleCheckYN = false;	// 由其它選項改為「無上述情形」，需主管輸入帳號/密碼進行確認。
		var fisrtReply = false;		// 是否為首次填答，預設=>否
		angular.forEach($scope.qusBankListAfter, function(rowAft, index, objs) {
			angular.forEach($scope.qusBankListBefore, function(rowBef, index, objs) {
				
				// 題目類型為S：單選選單、M：複選答案，進行檢核
				if (rowAft.QST_NO == rowBef.QST_NO && (rowBef.QUESTION_TYPE == 'S' || rowBef.QUESTION_TYPE == 'M') && rowBef.QUESTION_CLASS != '99') {
					
					// 前次
					var answerBefCNT = 0;	// 前次填答「無上述情形」之次數
					var answerBefQstCntY = 0;
					angular.forEach(rowBef.answer, function(rowBefRow, ansIndex, ansObjs){
						if (rowBefRow.NOTHING_FLAG == 'Y' && rowBefRow.ANSWER_NGCHECK == 'Y') {	// 答案為無上述情形
							answerBefCNT++; 
						}
						
						if (rowBefRow.ANSWER_NGCHECK == 'Y') {
							answerBefQstCntY++;
						}
					});
					
					// 本次
					var answerAftCNT = 0;	// 本次填答「無上述情形」之次數
					angular.forEach(rowAft.answer, function(ansAftRow, ansIndex, ansObjs){
						if (ansAftRow.NOTHING_FLAG == 'Y' && ansAftRow.ANSWER_NGCHECK == 'Y') {	// 答案為無上述情形
							answerAftCNT++; 
						}
					});
					
					if (answerBefQstCntY == 0) {
						fisrtReply = true;
					}
					
					// 前次"未"填答「無上述情形」 且 本次"有"填答「無上述情形」
					if (answerBefCNT == 0 && answerAftCNT > 0 && !doubleCheckYN && !fisrtReply) {
						doubleCheckYN = true;
					}
					
				}
			});
		});
		
		if (doubleCheckYN) {
			var qusBankListBefore = $scope.qusBankListBefore;
			var qusBankListAfter  = $scope.qusBankListAfter;
			var custID = $scope.inputVO.custID;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM512/CRM512_BOSS.html',
				className: 'CRM512_BOSS',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.qusBankListBefore = qusBankListBefore;
                	$scope.qusBankListAfter = qusBankListAfter;
                	$scope.custID = custID;
                }]
			})
			.closePromise.then(function (data) {
				switch (data.value) {
					case "successful" :
						$scope.showMsg('儲存成功');
						if($scope.inputVO.fromPRD == "Y") {
		            		//適配、下單過來的
							if($scope.inputVO.statusChanged) {
								//客戶狀態已有變化
								$scope.closeThisDialog('statusChanged');
							} else {
								$scope.closeThisDialog('successful');
							}
		            	} else {
		            		$scope.getQusBankList();
		            	}
		            	break;
					case "cancel" :
						$scope.showMsg('取消主管確認');
		            	break;
				}
			});

		} else if (requiredYN) {
			$scope.callSaveFunc();
		}
	};
	
	$scope.checkOption = function () { 
		if($scope.inputVO.initLoad) {
			//第一次進到頁面取得答案選項資料，不算有變化
			$scope.inputVO.initLoad = false;
		} else {
			$scope.inputVO.isAnsChanged = true; //答案選項已有變化
		}
		
		var RESULT_1 = 0;	
		var RESULT_2 = 0; 	
		var RESULT_3 = 0; 	
		
		var FINACIAL_COGNITION = 0; // 金融認知是否已勾選
		
		// 計算勾選項目
		angular.forEach($scope.qusBankListAfter, function(row, index, objs){
			// 1.無上述情形
			var ansLength = (row.answer).length;										// 該題答案總數
			var ansNGCheckLength = 0;													// 該題未填總數
			var ansConflictOption = 0;													// 有衝突選項(無上述情形)
			angular.forEach(row.answer, function(ansRow, ansIndex, ansObjs){
				if (ansRow.ANSWER_NGCHECK == 'N') {
					ansNGCheckLength++;
				}
				
				if (ansRow.ANSWER_NGCHECK == 'Y' && ansRow.ANSWER_FLAG == 'Y') {		// 有勾「無上述情形」
					ansConflictOption++;
				}
			});
			
			if (ansConflictOption == 1){	// 有勾「無上述情形」，清空其他答案，保留「無上述情形」，並DISABLE其他答案
				angular.forEach(row.answer, function(ansRow, ansIndex, ansObjs){
					if (ansRow.ANSWER_NGCHECK == 'Y' && ansRow.ANSWER_FLAG == 'Y') {	// 有勾「無上述情形」
						ansRow.ansDisabled = false;
					} 
					
					if (ansRow.ANSWER_FLAG != 'Y'){
						ansRow.ANSWER_NGCHECK = 'N';
						ansRow.ansDisabled = true;
					}
				});
			} else {						// 沒勾「無上述情形」，全數開放
				angular.forEach(row.answer, function(ansRow, ansIndex, ansObjs){
					ansRow.ansDisabled = false;
				});
			}
			
			// 2.衝突選項
			angular.forEach(row.answer, function(ansRow, ansIndex, ansObjs){
				if (ansRow.ANSWER_NGCHECK == 'Y' && ansRow.ANSWER_FLAG != 'Y' && ansRow.ANSWER_FLAG != 'N') {	
					angular.forEach(row.answer, function(ansRowTemp, ansIndex, ansObjs){
						if (ansRow.ANSWER_FLAG == ansRowTemp.ANSWER_SEQ) {
							ansRowTemp.ansDisabled = true;
						}
					});
				}
			});
			
			// 3.評估結果
			angular.forEach(row.answer, function(ansRow, ansIndex, andObjs){
				if (ansRow.RESULT_FLAG == 'Y' && ansRow.ANSWER_NGCHECK == 'Y') { // RESULT_FLAG=需與評估結果連動註記；ANSWER_NGCHECK=已勾選
					switch (ansRow.RESULT_ANSWER_SEQ) {
						case "1":
							RESULT_1++;
							break;
						case "2":
							RESULT_2++;
							break;
						case "3":
							RESULT_3++;
							break;
					}

					// 2022/09/15 祐傑提出：評估結果之自動勾選，需以金融認知有勾選為大前題
					if (ansRow.FINACIAL_COGNITION_YN == 'Y') {	// FINACIAL_COGNITION_YN=是否為金融認知
						FINACIAL_COGNITION++;
					}
				}
			});
		});
		
		// 計算完後再run一遍，將評估結果自動勾選
		angular.forEach($scope.qusBankListAfter, function(row, index, objs){
			if (row.QUESTION_CLASS == '99') {
				/* 2022/09/16 祐傑提出：
				 * 評估結果只會有一種，不會出現兩種
				 * 若勾選能力表現第8項及金融認知第4項無上述情形，則屬有生活能力或自主判斷能力之高齡客戶。 評估結果：1 (同時勾選能力表現8 和 金融認知 4 )
				 * 若勾選金融認知第1項 或 能力表現第1項~第7項，則屬需分行主管協同關懷確認，對金融認知無虞，評估仍可受理原因     評估結果：2 
				 * 但如果有還有選到金融認知 2或 3者，評估結果仍是顯示  3  
				 */
				angular.forEach(row.answer, function(ansRow, ansIndex, andObjs){
					switch (ansRow.ANSWER_SEQ) {
						case "1": // 若勾選能力表現第8項 及 金融認知第4項無上述情形，則屬有生活能力或自主判斷能力之高齡客戶。
							ansRow.ANSWER_NGCHECK = RESULT_1 == 2 && FINACIAL_COGNITION > 0 ? 'Y' : 'N';
							break;
						case "2": // 若勾選金融認知第1項 或 能力表現第1項~第7項，則屬需分行主管協同關懷確認，對金融認知無虞，評估仍可受理原因。
							ansRow.ANSWER_NGCHECK = RESULT_3 == 0 && RESULT_2 > 0 && FINACIAL_COGNITION > 0 ? 'Y' : 'N';
							break;
						case "3": // 若勾選金融認知第2項 或 第3項，則屬需加強關懷客戶，並建議請客戶由家人陪同或徵詢見證人服務，並轉客服專人錄音確認其投資意願。
							ansRow.ANSWER_NGCHECK = RESULT_3 > 0 && FINACIAL_COGNITION > 0 ? 'Y' : 'N';
							break;
					}
				});
			}
		});
	};
	
	$scope.getHistory = function (row) {
		var custID = $scope.inputVO.custID;
		
    	var dialog = ngDialog.open({
			template: 'assets/txn/CRM512/CRM512_HIS.html',
			className: 'CRM512',
			showClose: false,
			 controller: ['$scope', function($scope) {
				 $scope.row = row;
				 $scope.custID = custID;
            }]
		});
    };
	
	$scope.toUppercase_data = function(value, type){
		switch (type) {
			case 'custid':
				if(value){
					$scope.inputVO.custID = value.toUpperCase();
				}
				break;
			default:
				break;
		}
	};
	
	$scope.do_goExport = function() {
		$scope.sendRecv("CRM512", "exportPDF", "com.systex.jbranch.app.server.fps.crm512.CRM512InputVO", $scope.inputVO, function(totas, isError) {
            	if (isError) {
            		$scope.showErrorMsg(totas[0].body.msgData);
            	}
			}
		);
	};
	
	$scope.exportPDF = function() {		
		$scope.inputVO.goExpDisabled = true;
		
		$timeout(function() {
			$scope.do_goExport(); 
			$scope.inputVO.goExpDisabled = false;
		}, 1000);
	};
});