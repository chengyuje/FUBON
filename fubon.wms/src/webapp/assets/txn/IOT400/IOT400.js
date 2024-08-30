/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT400Controller',
	function($scope, $controller, $confirm, getParameter, ngDialog) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT400Controller";
		
		getParameter.XML(["IOT.PAY_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['IOT.PAY_TYPE'] = totas.data[totas.key.indexOf('IOT.PAY_TYPE')];
				
			}
		});
		
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.getVisitTime = function(type) {
			if ($scope.inputVO[type + '_TIME'] != undefined) {
				var time = $scope.inputVO[type + '_TIME'];
				for (var i = 0; i < 4; i++) {
					var flag = 'N';
					var index = i + 1;
					if (time.substr(i, 1) == '1') {
						flag = 'Y';
					}
					$scope.inputVO[type + '_TIME' + index] = flag;
				}
			}
		}
		
		$scope.init = function() {
//			alert($scope.review);
			$scope.resultList = [];
			$scope.editable = false;
			$scope.btnCtrl = false;
			$scope.showTip = false;
			$scope.cancleBtnCtrl = false;
			$scope.showWarningMsg = false;
			$scope.j9 = false;
			
			$scope.sendRecv("IOT400","queryData","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", {'PREMATCH_SEQ': $scope.inputVO.PREMATCH_SEQ},
			function(tota,isError){
				if (isError) {
            		$scope.showErrorMsg(tota[0].body.resultList);
            		return;
            	}
				$scope.resultList = tota[0].body.fileList;
				$scope.inputVO.SEQ = tota[0].body.SEQ;
//				alert($scope.inputVO.SEQ);
				
				$scope.inputVO = tota[0].body.resultList[0];
				if ($scope.inputVO.length <= 0) {
					$scope.showErrorMsg("資料有誤，請洽保險商品處。");
					return;
				}
				
				// 案件編號為J9開頭或為空時，電子要保書按鈕為「反灰無法點選」
				if ($scope.inputVO.CASE_ID == undefined || $scope.inputVO.CASE_ID.substr(0, 2) == 'J9') {
					$scope.j9 = true;
				}
				
				// 若該案有多筆同案件編號且電訪記錄之電訪狀態有「電訪疑義」「取消投保」之記錄，需產生提醒訊息
				$scope.showWarningMsg = tota[0].body.showMsg;
				
				// 若案件電訪狀態=1.未申請 或無案件電訪狀態時，才可執行電訪預約/取消作業。
				if (($scope.inputVO.STATUS == undefined || $scope.inputVO.STATUS == '1') && $scope.review != true) {
					$scope.editable = true;
				}
				
				/**
				 * 儲存/送出預約/清除/上傳檔案 ==> 電訪狀態為 1(未申請) 或無電訪狀態時，才可執行
				 * 狀態：4、7、8 時，反灰不能點選
				 * 狀態：2、3、5、6 時，不能點選，產生toolTip提醒訊息
				 * **/
				if ($scope.inputVO.STATUS == undefined || $scope.inputVO.STATUS == '1') {
					$scope.btnCtrl = false;
				} else {
					$scope.btnCtrl = true;
					
					if ($scope.inputVO.STATUS == '2' || $scope.inputVO.STATUS == '3' || $scope.inputVO.STATUS == '5' || $scope.inputVO.STATUS == '6') {
						$scope.showTip = true;						
					}
				}
				
				/**
				 * 取消電訪
				 * 狀態：undefined、1、4、7、8 時，反灰不能點選
				 * 狀態：2、3、5、6 時，可以點選，產生提醒訊息
				 * **/
				if ($scope.inputVO.STATUS == undefined || $scope.inputVO.STATUS == '1' || 
					$scope.inputVO.STATUS == '4' || $scope.inputVO.STATUS == '7' || $scope.inputVO.STATUS == '8') {
					$scope.cancleBtnCtrl = true;
				}
				// 要、被、繳生日
				$scope.inputVO.PROPOSER_BIRTH 	= $scope.toJsDate($scope.inputVO.PROPOSER_BIRTH);
				$scope.inputVO.INSURED_BIRTH  	= $scope.toJsDate($scope.inputVO.INSURED_BIRTH);
				$scope.inputVO.PAYER_BIRTH    	= $scope.toJsDate($scope.inputVO.PAYER_BIRTH);
				// 要、被、繳法代生日 
				$scope.inputVO.REPRESET_BIRTH 	= $scope.toJsDate($scope.inputVO.REPRESET_BIRTH);
				$scope.inputVO.I_REPRESET_BIRTH = $scope.toJsDate($scope.inputVO.I_REPRESET_BIRTH);
				$scope.inputVO.P_REPRESET_BIRTH = $scope.toJsDate($scope.inputVO.P_REPRESET_BIRTH);
				
				if ($scope.inputVO.STATUS != undefined) {
					$scope.getVisitTime('C');
					$scope.getVisitTime('I');
					$scope.getVisitTime('P');
				} else {
					$scope.callTime('C');
					$scope.callTime('I');
					$scope.callTime('P');
				}
				
				/**
				 * 電訪類型(錄音樣態)
				 * **/
//				$scope.inputVO.CALL_TYPE  	   = "0000";	// 電訪類型
				$scope.inputVO.CALL_TYPE_LABEL = "";		// 電訪類型
				$scope.inputVO.C_CALL_TYPE 	   = "";		// 要保人電訪類型
				$scope.inputVO.I_CALL_TYPE 	   = "";		// 被保人電訪類型
				$scope.inputVO.P_CALL_TYPE 	   = "";		// 繳款人電訪類型
				
				/** 
				 * 1. 解約：
				 * 1-1. 業報書保費來源為解約=Y（S01.LOAN_SOURCE2_YN）或
				 * 1-2. 業報書投保前三個月內有辦理解約=Y（S01.CONTRACT_END_YN）或
				 * 1-3. 行內保單解約檢核(任一)=Y
				 * 			S01.C_LOAN_CHK3_YN	(要保人)
				 * 			S01.I_LOAN_CHK3_YN	(被保人)
				 * 			S01.LOAN_CHK3_YN	(繳款人)
				 * **/
				var label1 = "";
				var label2 = "";
				var label3 = "";
				var label4 = "";
				
				if ($scope.inputVO.LOAN_SOURCE2_YN == 'Y' || $scope.inputVO.CONTRACT_END_YN == '1' || $scope.inputVO.C_LOAN_CHK3_YN == 'Y') {
					$scope.inputVO.C_CALL_TYPE += "1";
					label1 = "解約";
				} else {
					$scope.inputVO.C_CALL_TYPE += "0";
				}
				
				var iFlag1 = $scope.inputVO.I_LOAN_CHK3_YN == 'Y' ? "1" : "0";
				var pFlag1 = $scope.inputVO.LOAN_CHK3_YN   == 'Y' ? "1" : "0";
				$scope.inputVO.I_CALL_TYPE += iFlag1;
				$scope.inputVO.P_CALL_TYPE += pFlag1;
				
				if (iFlag1 == "1" || pFlag1 == "1") {
					label1 = "解約";
				}
				
				/**  
				 * 2. 貸款或保單借款：
				 * 2-1. 業報書保費來源為貸款/保單借款=Y（S01.LOAN_SOURCE_YN）或
				 * 2-2. 業報書投保前三個月內有辦理貸款或保險單借款=Y（S01.S_INFITEM_LOAN_YN）或
				 * 2-3. 行內貸款檢核(任一)=Y 或
				 * 			S01.C_LOAN_CHK2_YN	(要保人)
				 * 			S01.I_LOAN_CHK2_YN	(被保人)
				 * 			S01.LOAN_CHK2_YN	(繳款人)
				 * 2-4. 保單借款檢核(任一)=Y 或
				 * 			S01.C_LOAN_CHK1_YN	(要保人)
				 * 			S01.I_LOAN_CHK1_YN	(被保人)
				 * 			S01.LOAN_CHK1_YN	(繳款人)
				 * 2-5. 行內貸款申請日<>空白
				 * 			S01.C_LOAN_APPLY_DATE	(要保人)
				 * 			S01.I_LOAN_APPLY_DATE	(被保人)
				 * 			S01.P_LOAN_APPLY_DATE	(繳款人)
				 * **/
				if ($scope.inputVO.LOAN_SOURCE_YN == 'Y' || $scope.inputVO.S_INFITEM_LOAN_YN == '1' || 
					$scope.inputVO.C_LOAN_CHK2_YN == 'Y' || $scope.inputVO.C_LOAN_CHK1_YN == 'Y' || $scope.inputVO.C_LOAN_APPLY_DATE != undefined) {
					$scope.inputVO.C_CALL_TYPE += "1";
					label2 = "貸款或保單借款";
				} else {
					$scope.inputVO.C_CALL_TYPE += "0";
				}
				
				var iFlag2 = ($scope.inputVO.I_LOAN_CHK2_YN == 'Y' || $scope.inputVO.I_LOAN_CHK1_YN == 'Y' || $scope.inputVO.I_LOAN_APPLY_DATE != undefined) ? "1" : "0";
				var pFlag2 = ($scope.inputVO.LOAN_CHK2_YN   == 'Y' || $scope.inputVO.LOAN_CHK1_YN   == 'Y' || $scope.inputVO.P_LOAN_APPLY_DATE != undefined) ? "1" : "0";
				$scope.inputVO.I_CALL_TYPE += iFlag2;
				$scope.inputVO.P_CALL_TYPE += pFlag2;
				
				if (iFlag2 == "1" || pFlag2 == "1") {
					label2 = "貸款或保單借款";
				}
				
				/**  
				 * 3-1. 定存不打折檢核(任一)=Y
				 * 			S01.C_CD_CHK_YN		(要保人)
				 * 			S01.I_CD_CHK_YN		(被保人)
				 * 			S01.CD_CHK_YN		(繳款人)
				 * **/
				var cFlag3 = $scope.inputVO.C_CD_CHK_YN == 'Y' ? "1" : "0";
				var iFlag3 = $scope.inputVO.I_CD_CHK_YN == 'Y' ? "1" : "0";
				var pFlag3 = $scope.inputVO.CD_CHK_YN   == 'Y' ? "1" : "0";
				$scope.inputVO.C_CALL_TYPE += cFlag3;
				$scope.inputVO.I_CALL_TYPE += iFlag3;
				$scope.inputVO.P_CALL_TYPE += pFlag3;
				
				if (cFlag3 == "1" || iFlag3 == "1" || pFlag3 == "1") {
					label3 = "定存解約不打折";
				}
				
				/** 
				 * 4-1. 要保人、被保人、繳款人(任一)生日>=64.5歲
				 * 			S01.C_SALE_SENIOR_YN	(要保人)
				 * 			S01.I_SALE_SENIOR_YN	(被保人)
				 * 			S01.P_SALE_SENIOR_YN	(繳款人)
				 * **/
				var cFlag4 = $scope.inputVO.C_SALE_SENIOR_YN == 'Y' ? "1" : "0";
				var iFlag4 = $scope.inputVO.I_SALE_SENIOR_YN == 'Y' ? "1" : "0";
				var pFlag4 = $scope.inputVO.P_SALE_SENIOR_YN == 'Y' ? "1" : "0";
				$scope.inputVO.C_CALL_TYPE += cFlag4;
				$scope.inputVO.I_CALL_TYPE += iFlag4;
				$scope.inputVO.P_CALL_TYPE += pFlag4;
				
				if (cFlag4 == "1" || iFlag4 == "1" || pFlag4 == "1") {
					label4 = "高齡關懷";
				}

				$scope.inputVO.CALL_TYPE_LABEL += label1;
				if (label1.length > 0 && label2.length > 0) $scope.inputVO.CALL_TYPE_LABEL += "、";
				$scope.inputVO.CALL_TYPE_LABEL += label2;
				if ((label1.length > 0 || label2.length > 0) && label3.length > 0) $scope.inputVO.CALL_TYPE_LABEL += "、";
				$scope.inputVO.CALL_TYPE_LABEL += label3;
				if ((label1.length > 0 || label2.length > 0 || label3.length > 0) && label4.length > 0) $scope.inputVO.CALL_TYPE_LABEL += "、";
				$scope.inputVO.CALL_TYPE_LABEL += label4;
				
//				alert($scope.inputVO.C_CALL_TYPE);
//				alert($scope.inputVO.I_CALL_TYPE);
//				alert($scope.inputVO.P_CALL_TYPE);
				
				/** 電訪對象(若三者ID均相同，則帶要保人) **/
				var cFlag = $scope.inputVO.C_CALL_TYPE.includes("1") ? "Y" : "N";	// 要保人
				var iFlag = $scope.inputVO.I_CALL_TYPE.includes("1") ? "Y" : "N";	// 被保人
				var pFlag = $scope.inputVO.P_CALL_TYPE.includes("1") ? "Y" : "N";	// 繳款人
				
				$scope.inputVO.C_NEED_CALL_YN = cFlag;
				$scope.inputVO.I_NEED_CALL_YN = iFlag;
				$scope.inputVO.P_NEED_CALL_YN = pFlag;
				
				if (cFlag == 'Y') {
					$scope.inputVO.CALL_C_YN = "Y";
				}
				if (iFlag == 'Y' && ($scope.inputVO.INSURED_ID != $scope.inputVO.CUST_ID)) {
					$scope.inputVO.CALL_I_YN = "Y";
				}
				if (pFlag == 'Y' && ($scope.inputVO.PAYER_ID != $scope.inputVO.CUST_ID) && (($scope.inputVO.PAYER_ID != $scope.inputVO.INSURED_ID))) {
					$scope.inputVO.CALL_P_YN = "Y";
				}
			});
		}
		
		$scope.upload = function(name, rname) {
			$scope.inputVO.fileName = name;
			$scope.inputVO.fileRealName = rname;
			
			if ($scope.resultList.length == 0) {
				$scope.inputVO.SEQ = 1;		
			}
			
			$scope.sendRecv("IOT400","upload","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.resultList = tota[0].body.fileList;
					$scope.inputVO.SEQ = tota[0].body.SEQ;
//					alert($scope.inputVO.SEQ);
				}
			});
		}
		
		$scope.deleteRow = function(row) {
			$scope.sendRecv("IOT400","delete","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", 
			{'PREMATCH_SEQ': row.PREMATCH_SEQ, 'SEQ': row.SEQ},
			function(tota, isError) {
				if (!isError) {
					$scope.resultList = tota[0].body.fileList;
					$scope.inputVO.SEQ = tota[0].body.SEQ;
//					alert($scope.inputVO.SEQ);
				}
			});
		}
		
		$scope.download = function(row) {
			$scope.sendRecv("IOT400","download","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", 
			{'PREMATCH_SEQ': row.PREMATCH_SEQ, 'SEQ': row.SEQ},
			function(totas, isError) {
        		if (!isError) {
					return;
				}
			});
        };
		
		// 要保人變更_客戶保險購買檢核表
		$scope.printChgCustCheckList = function() {
			$scope.sendRecv("IOT110", "printChgCustCheckList", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", 
			{'PREMATCH_SEQ':$scope.inputVO.PREMATCH_SEQ},
			function(tota, isError) {
				if (!isError) {
					return;
				}
   			});
		};
		
		// 檢視行動投保要保書
		$scope.getPDF = function() {
			if ($scope.inputVO.CASE_ID != undefined && $scope.inputVO.CASE_ID != null && $scope.inputVO.CASE_ID != "") {
				$scope.sendRecv("IOT120", "Get_PdfInfo", "com.systex.jbranch.app.server.fps.iot120.IOT120InputVO", 
				{'CASE_ID':$scope.inputVO.CASE_ID, 'fromIOT110': 'Y'},
				function(totas, isError) {
	               	if (isError) {
	               		$scope.showErrorMsg(totas[0].body.msgData);
	               	}
				});
			} else {
				$scope.showErrorMsg('無案件編號，無法檢視行動投保要保書。');
			}
			
//			$scope.sendRecv("IOT400","getPDF","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", $scope.inputVO,
//			function(tota, isError) {
//				if (!isError) {
//					return;
//				}
//			});
		};
		
		$scope.click = function(flag) {
			$scope.openFlag = flag;
		};	
		
		// 選擇方便聯絡時間(複選)
		$scope.callTime = function(type) {
			$scope.inputVO[type + '_TIME'] = '';
			for (var i = 1; i <= 4; i++ ) {
				var check = $scope.inputVO[type + '_TIME' + i] == 'Y' ? '1' : '0';
				
				$scope.inputVO[type + '_TIME'] += check;
			}
//			alert($scope.inputVO[type + '_TIME']);
		}
		
		$scope.save = function() {
			if ($scope.checkStatus()) {
				$scope.sendRecv("IOT400","save","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.editable = true;
						$scope.showSuccessMsg('ehl_01_common_025'); // 儲存成功
						
					}
				});				
			}
		}
		
//		$scope.getAge = function() {
//			$scope.sendRecv("IOT400","getAge","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", $scope.inputVO,
//			function(tota, isError) {
//				if (!isError) {
//					$scope.inputVO.AGE = tota[0].body.resultList[0].AGE;
//					alert($scope.inputVO.AGE);
//				}
//			});
//		}
		
		$scope.cancle = function() {
			if ($scope.checkStatus('cancle')) {
				// 無需處理....
			}
		}
		
		$scope.close = function() {
			if ($scope.inputVO.STATUS == '1' && $scope.editable) {
				$confirm({text: '未完成電訪預約，請確認是否關閉'}, {size: 'sm'}).then(function() {
					$scope.closeThisDialog('cancel');
				});				
			} else {
				$scope.closeThisDialog('cancel');
			}
		}
		
		// 送出預約
		$scope.reserve = function() {
			if ($scope.checkStatus()) {
				// 案件編號為：空值或J9開頭，將顯示2個按鈕保險購買檢核表、上傳檔案；請檢核若上傳檔案為空檔，則無法點選送出預約。
				// 提示訊息：要保書文件未上傳，無法申請電訪預約!
				if ($scope.inputVO.CASE_ID == undefined || $scope.inputVO.CASE_ID.substr(0, 2) == 'J9') {
					if ($scope.resultList.length == 0) {
						$scope.showErrorMsg("要保書文件未上傳，無法申請電訪預約!");
	    				return;
					}
				}
				
				// 若要保人為未成年，法代姓名及ID資訊由保險購買檢核帶入；生日欄位呈現且必填
				if ($scope.inputVO.C_AGE < 18 && (
					$scope.inputVO.REPRESET_NAME == undefined ||
					$scope.inputVO.REPRESET_ID == undefined || 
					$scope.inputVO.REPRESET_BIRTH == undefined) && $scope.inputVO.CALL_C_YN == 'Y') {
					$scope.showErrorMsg("若要保人為未成年，法代姓名/ID/生日欄位必填");
					return;
				}
				if ($scope.inputVO.I_AGE < 18 && (
						$scope.inputVO.I_REPRESET_NAME == undefined ||
						$scope.inputVO.I_REPRESET_ID == undefined || 
						$scope.inputVO.I_REPRESET_BIRTH == undefined) && $scope.inputVO.CALL_I_YN == 'Y') {
					$scope.showErrorMsg("若被保人為未成年，法代姓名/ID/生日欄位必填");
					return;
				}
				if ($scope.inputVO.P_AGE < 18 && (
						$scope.inputVO.P_REPRESET_NAME == undefined ||
						$scope.inputVO.P_REPRESET_ID == undefined || 
						$scope.inputVO.P_REPRESET_BIRTH == undefined) && $scope.inputVO.CALL_P_YN == 'Y') {
					$scope.showErrorMsg("若繳款人為未成年，法代姓名/ID/生日欄位必填");
					return;
				}
				
				// 檢核欄位及應上傳檔案是否填寫齊全。若不齊全(EX：未勾選方便連絡時間)，未滿足條件之欄位產生紅框，無法預約成功。
				// 提示訊息：必要欄位未齊全，無法申請電訪預約!
				if (($scope.inputVO.CALL_C_YN == 'Y' && $scope.inputVO.C_TIME == '0000') ||
					($scope.inputVO.CALL_I_YN == 'Y' && $scope.inputVO.I_TIME == '0000') ||
					($scope.inputVO.CALL_P_YN == 'Y' && $scope.inputVO.P_TIME == '0000')) {
					$scope.showErrorMsg("必要欄位未齊全，無法申請電訪預約！請勾選方便聯絡時間");
					return;
				}
				
				// 通訊電話及行動電話需控則一必填
				if (($scope.inputVO.CALL_C_YN == 'Y' && $scope.inputVO.C_TEL_NO == undefined && $scope.inputVO.C_MOBILE == undefined) ||
					($scope.inputVO.CALL_I_YN == 'Y' && $scope.inputVO.I_TEL_NO == undefined && $scope.inputVO.I_MOBILE == undefined) ||
					($scope.inputVO.CALL_P_YN == 'Y' && $scope.inputVO.P_TEL_NO == undefined && $scope.inputVO.P_MOBILE == undefined)) {
					$scope.showErrorMsg("『通訊電話』及『手機號碼』至少需擇一輸入。");
					return;
				}

				
				$scope.sendRecv("IOT400","save","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.sendRecv("IOT400","reserve","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.showSuccessMsg('送出預約成功');
								$scope.closeThisDialog('successful');
							}
						});
					}
				});
			}
		}
		
		// 清除：點選後，清空「方便連絡時間」、「特別注意事項」之欄位值。
		$scope.clean = function() {
			if ($scope.checkStatus()) {
				$scope.inputVO.C_TIME = '0000';
				$scope.inputVO.I_TIME = '0000';
				$scope.inputVO.P_TIME = '0000';
				$scope.getVisitTime('C');
				$scope.getVisitTime('I');
				$scope.getVisitTime('P');
				
				$scope.inputVO.SPECIAL_MEMO = undefined;
			}
		}
		
		// 檢視完成（若檢視狀態已回壓「檢視完成」，仍可再執行檢視完成及退回按鈕）
		$scope.reviewOK = function() {
			$scope.sendRecv("IOT400","reviewOK","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", {'PREMATCH_SEQ': $scope.inputVO.PREMATCH_SEQ},
			function(tota, isError) {
				if (!isError) {
					$scope.showSuccessMsg('檢視完成');
					$scope.closeThisDialog('successful');
				}
			});
		}
		
		// 退回
		$scope.reject = function() {
			var prematch_seq = $scope.inputVO.PREMATCH_SEQ;
			
			var dialog = ngDialog.open({
				template: 'assets/txn/IOT400/IOT400_REJECT.html',
				className: 'IOT400',
				controller:['$scope',function($scope){
					$scope.prematch_seq = prematch_seq;
				}]
			});
			dialog.closePromise.then(function(data){
				if (data.value === 'successful') {
					$scope.closeThisDialog('successful');
				}
			});
		}
		
		$scope.checkStatus = function(type) {
			if ($scope.inputVO.STATUS == '2' || $scope.inputVO.STATUS == '3' || $scope.inputVO.STATUS == '5' || $scope.inputVO.STATUS == '6') {
				if (type == 'cancle') {
					$scope.showMsg("電訪處理中，若欲取消電訪，請通知電訪人員退回電訪狀態為1.未申請，方可操作!");
				} else {
					$scope.showMsg("電訪處理中，若欲調整，請通知電訪人員退回電訪狀態為1.未申請，方可異動!");
				}
				return false;
			}
			return true;
		}
		
		// 行動電話驗證
		$scope.checkMobile = function(type) {
			var mobile = $scope.inputVO[type + '_MOBILE'];
			var MobileReg = /^(09)[0-9]{8}$/;
			if (!mobile.match(MobileReg)) {
				$scope.inputVO[type + '_MOBILE'] = undefined;
				$scope.showErrorMsg("手機號碼格式不正確，請重新輸入")
			}
		}
		
		$scope.init();
	}
);