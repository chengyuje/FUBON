/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM320_reviewController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, $compile) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "SQM320_reviewController";
	
	$scope.loginID = projInfoService.getUserID();
	$scope.roleID  = projInfoService.getRoleID();
		
	$scope.isSupervisor = false;
	if ($scope.roleID == 'A164' || $scope.roleID == 'A146') {
		$scope.isSupervisor = true;
	} else {
		$scope.isSupervisor = false;
	}
	
	getParameter.XML(["CAM.LEAD_TYPE", "CAM.LEAD_SOURCE", "CAM.VST_REC_CMU_TYPE", "SQM.MEET_RULE_LIST", "SQM.MEET_TYPE"], function(totas) {
		if (totas) {
			$scope.LEAD_TYPE = totas.data[totas.key.indexOf('CAM.LEAD_TYPE')];
			$scope.LEAD_SOURCE = totas.data[totas.key.indexOf('CAM.LEAD_SOURCE')];
			$scope.VST_REC_CMU_TYPE = totas.data[totas.key.indexOf('CAM.VST_REC_CMU_TYPE')];
			$scope.MEET_RULE_LIST = totas.data[totas.key.indexOf('SQM.MEET_RULE_LIST')];
			$scope.MEET_TYPE = totas.data[totas.key.indexOf('SQM.MEET_TYPE')];
		}
	});
	
	/** 日曆 START **/
	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened' + index] = true;
	};
	
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	/** 日曆 END **/
	
    $scope.init = function(){
    	$scope.inputVO.cust_id = $scope.row.CUST_ID;
    	$scope.inputVO.audit_type = $scope.row.AUDIT_TYPE;
    	$scope.inputVO.yearQtr = $scope.row.YEARQTR;
    	$scope.inputVO.ao_code = $scope.row.AO_CODE;
    	$scope.inputVO.sp_cust_yn = $scope.row.SP_CUST_YN;
    	$scope.inputVO.branch_nbr = $scope.row.BRANCH_NBR;
    	$scope.inputVO.emp_id = $scope.row.EMP_ID;
    	$scope.AUDITED = $scope.row.AUDITED;
		$scope.TYPE = $scope.type;
		$scope.AUDITOR = $scope.auditor;
    	$scope.REVIEWER_EMP_ID = $scope.row.REVIEWER_EMP_ID;
    	$scope.REVIEW_DATE = $scope.row.REVIEW_DATE;
    };
    
    $scope.init();
    
	//資料查詢
	$scope.query = function() {	
		$scope.sendRecv("SQM320", "getDtlData", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList = [];
					$scope.totalData = [];
					$scope.outputVO = {};
					$scope.showMsg("ehl_01_common_009");
        			return;
				}
				
				$scope.paramList = tota[0].body.resultList;
				angular.forEach($scope.paramList, function(row, index, objs){
					$scope.CUST_NAME           = row.CUST_NAME;
					$scope.CUST_ID             = row.CUST_ID;
					$scope.GENDER              = row.GENDER;
					$scope.BIRTH_DATE          = row.BIRTH_DATE;
					$scope.SECRET_YN           = row.SECRET_YN;
					// $scope.AVG_AUM_AMT   = row.AVG_AUM_AMT;
					$scope.ROLE_NAME           = row.ROLE_NAME;
					$scope.CUST_AO_CODE        = row.AO_CODE;
					$scope.EMP_NAME            = row.EMP_NAME;
					$scope.PROF_INVESTOR_YN    = row.PROF_INVESTOR_YN;
					$scope.REC_YN              = row.REC_YN;
					$scope.COMM_RS_YN          = row.COMM_RS_YN;
					$scope.COMPLAIN_YN         = row.COMPLAIN_YN;
					$scope.SP_CUST_YN          = row.SP_CUST_YN;
					
//							$scope.MOBILE_NO     = row.MOBILE_NO;
//							$scope.TEL_NO        = row.TEL_NO;
//							$scope.EXT_NO        = row.EXT_NO;
					
					//2018-12-24 by Jacky CR WMSCR-20181112-03 提高KYC承作商品
					$scope.UP_KYC_YN            = row.UP_KYC_YN;
					$scope.EBILL_PICK_YN        = row.EBILL_PICK_YN;
					$scope.CUST_E_NOREC_FLAG    = row.CUST_E_NOREC_FLAG;
					$scope.AST_LOSS_NOREC_FLAG  = row.AST_LOSS_NOREC_FLAG;
					
					row.MEET_DATE = $rootScope.toJsDate(row.MEET_DATE);
					
					if (!(row.DOC_NAME == null || row.DOC_NAME == undefined || row.DOC_NAME == '')) {
						$scope.inputVO.fileRealName = row.DOC_NAME;
					}
				});	
				$scope.totalData = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;		
				return;
			}						
		});
		
	};
	
	$scope.query();
	
	$scope.checkKYC = function(row) {
		if(row.MEET_TM_RULE == 'Y'){
			var msg = "訪談記錄若屬「業務推廣」者(如非主動撥打電話給客戶，或撥打電話給客戶係屬關懷、維繫客戶關係等未涉及推廣商品或服務爭取商易機會之行為，則排除適用)，每位個金RM至少抽查一戶，查核內容至少應包含：";
			var msg1 = " (1).是否告知本行名稱、姓名及電訪目的。";
			var	msg2 = " (2).若有涉及商品推介時，是否告知商品名稱、各項費用或優惠活動內容(含限定時間優惠內容)。";
			var	msg3 = " (3).客戶如表明不願意接受電話行銷後，是否不再以電話行銷方式推廣並應立即在相關系統註記禁止電話行銷。";
			$confirm(
				{text: msg ,text1: msg1 ,text2: msg2 ,text3: msg3 , title: '提醒', hideCancel: true}
				, {size: '120px'}).then(function() {}
			);
		}
	}
	
	//資料儲存
	$scope.save = function(type) {	
		var saveList = [];
		var keepGoing = true;
		
		angular.forEach($scope.paramList, function(row, index, objs) {
			//#6444 : 主管若未訪查「KYC調高且投資金額大於100萬」即選擇儲存，應進行阻擋
			if (keepGoing) {
				if (row.CAMPAIGN_ID == 'KYCUPANDAMT100' || row.CUST_MEMO_SEQ == '0000000000') {
					if (row.MEET_RULE != null       && row.MEET_RULE != undefined       && row.MEET_RULE != '' && 
						row.MEET_RULE_CUST1 != null && row.MEET_RULE_CUST1 != undefined && row.MEET_RULE_CUST1 != '' && 
						row.MEET_RULE_CUST2 != null && row.MEET_RULE_CUST2 != undefined && row.MEET_RULE_CUST2 != '' && 
						row.MEET_RULE_CUST3 != null && row.MEET_RULE_CUST3 != undefined && row.MEET_RULE_CUST3 != '' && 
						row.MEET_RULE_CUST4 != null && row.MEET_RULE_CUST4 != undefined && row.MEET_RULE_CUST4 != '' && 
						row.AUDIT_REMARK != null    && row.AUDIT_REMARK != undefined    && row.AUDIT_REMARK != '' && 
						row.MEET_DATE != null       && row.MEET_DATE != undefined       && row.MEET_DATE != '' &&
						row.MEET_TYPE != null       && row.MEET_TYPE != undefined       && row.MEET_TYPE != '' ) {
						
						row.MEET_DATE = Date.parse(row.MEET_DATE);
						saveList.push($scope.paramList[index]);	
					} else {		
						$scope.showErrorMsg('「KYC調高且投資金額大於100萬」為必查名單');
						//$scope.showErrorMsg('"查核項目-系統查核"，"查核項目-與客戶確認"及"查核意見"為必要輸入欄位');
						keepGoing = false;
					}
				} 
				
				if (row.MEET_TYPE == 'P' && (!row.RECORD_SEQ || (row.RECORD_SEQ && row.RECORD_SEQ.length != 12))) {
					$scope.showErrorMsg('查該方式為「電訪」時，電訪錄音序號為必填、不得少於12碼');
					keepGoing = false;
				} else if (row.MEET_TYPE == 'E' && ($scope.inputVO.fileName == undefined || $scope.inputVO.fileName == null || $scope.inputVO.fileName == '') && row.FILE_FLAG == 'N') {
					$scope.showErrorMsg('查該方式為「Email」時，必需上傳Email訪查紀錄');
					keepGoing = false;
				} else if (row.MEET_RULE && 
						   row.MEET_RULE_CUST1 && 
						   row.MEET_RULE_CUST2 && 
						   row.MEET_RULE_CUST3 && 
						   row.MEET_RULE_CUST4 && 
						   row.AUDIT_REMARK && 
						   row.MEET_DATE &&
						   row.MEET_TYPE) {
					row.MEET_DATE = Date.parse(row.MEET_DATE);
					
					if (row.MEET_TYPE == 'E' && row.FILE_FLAG == 'Y' && !($scope.inputVO.fileName == undefined || $scope.inputVO.fileName == null || $scope.inputVO.fileName == '')) {
				    	$scope.inputVO.fileRealName = row.DOC_NAME;
					}
					
					saveList.push($scope.paramList[index]);	
				}
			}
		});
		
		if (keepGoing) {
			if (saveList.length != 0) {
				$scope.inputVO.save_type = type;
				$scope.inputVO.dtlList = saveList;
				$scope.sendRecv("SQM320", "save", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota, isError){
		        	if (isError) {
		        		$scope.showErrorMsg(tota[0].body.msgData);
		        	}
		        	if (tota.length > 0) {
		        		$scope.showSuccessMsg('ehl_01_common_001');
		        		$scope.closeThisDialog('successful');
		        	};
				});
			} else {
				$scope.showErrorMsg('"查核項目-訪談記錄內容查核"、"1.確認客戶清楚投資損益狀況及理財往來情形，且個金RM未以其私人Email寄發本行相關商品或業務資訊予客戶"、"2.個金RM無銷售非屬本行核准之金融商品"、"3.個金RM無自行製作並提供對帳單，亦未以其個人或本行名義私自製作或提供任何文件或證明"、"4.「自取對帳單客戶名單」目前資產餘額確認"、"查核日期"、"查核方式"及"查核意見"為必要輸入欄位');
			}
		};	
	};
	
	//資料覆核
	$scope.review = function() {	
		var reviewData = [{'YEARQTR':$scope.inputVO.yearQtr, 'CUST_ID':$scope.inputVO.cust_id , 'AUDIT_TYPE':$scope.inputVO.audit_type}];
		$scope.inputVO.reviewList = reviewData;
		$scope.sendRecv("SQM320", "reviewData", "com.systex.jbranch.app.server.fps.sqm320.SQM320InputVO", $scope.inputVO, function(tota,isError){
        	if (isError) {
        		$scope.showErrorMsg(tota[0].body.msgData);
        	}
        	if (tota.length > 0) {
        		$scope.showSuccessMsg('ehl_01_common_001');
        		$scope.closeThisDialog('successful');
        	};
		});
	};
	
	$scope.onlyOne = function(meet_rule) {
		if(meet_rule != undefined && meet_rule != ''){
			$scope.meet_rule_disabled = true;
			return;
		}else{
			$scope.meet_rule_disabled = false;
			return;
		}
	}
	
	$scope.checkField = function(row){
		if (row.MEET_RULE && 
			row.MEET_RULE_CUST1 &&
			row.MEET_RULE_CUST2 && 
			row.MEET_RULE_CUST3 && 
			row.MEET_RULE_CUST4 && 
			row.AUDIT_REMARK)  {
			return true;
		}
	}
	
    $scope.custDTL = function(row) {
    	$scope.custVO = {
				CUST_ID :  row.CUST_ID,
				CUST_NAME :row.CUST_NAME	
		}
		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
    	
    	var path = "assets/txn/CRM610/CRM610_MAIN.html";
		var set = $scope.connector("set","CRM610URL",path);
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM610/CRM610.html',
			className: 'CRM610',
			showClose: false
		});
	};

	$scope.inquire_phone = function() {
		$scope.phoneList = [];
//		$scope.sendRecv("CRM621", "inquire_phone", "com.systex.jbranch.app.server.fps.crm621.CRM621InputVO", {'cust_id' : $scope.inputVO.cust_id},
//				function(tota, isError) {
//					if (isError) {
//						$scope.showErrorMsg(tota[0].body.msgData);
//					}
//					if(tota[0].body.phoneList.length > 0) {
//						angular.forEach(tota[0].body.phoneList, function(row) {
//							if(row.CODE == '1') {			//一般電話
//								$scope.TEL_NO        = row.NUMBER;
//								// $scope.EXT_NO        = row.EXT;
//							}else if(row.CODE == '5') {		//行動電話
//								$scope.MOBILE_NO     = row.NUMBER;
//							}
//						});
//						$scope.output_phoneList = tota[0].body;
//					}
//				}
//		);
	}
	$scope.inquire_phone();
	
	$scope.autoAuditRemark = function(row) {
		if (!row.AUDIT_REMARK) {
			if (row.MEET_RULE == 'Y' && 
				row.MEET_RULE_CUST1 == 'Y' && 
				row.MEET_RULE_CUST2 == 'Y' && 
				row.MEET_RULE_CUST3 == 'Y' && 
				row.MEET_RULE_CUST4 == 'Y' &&
				row.MEET_DATE && 
				row.MEET_TYPE) {
				if (row.MEET_TYPE == 'P' && row.RECORD_SEQ && row.RECORD_SEQ.length == 12) {
					row.AUDIT_REMARK = '查核結果均無異常';
				} else if (row.MEET_TYPE == 'P' && row.RECORD_SEQ && row.RECORD_SEQ.length != 12) {
					row.AUDIT_REMARK = '';
				} else if (row.MEET_TYPE == 'P' && !row.RECORD_SEQ) {
					row.AUDIT_REMARK = '';
				} else {
					row.RECORD_SEQ = '';
					row.AUDIT_REMARK = '查核結果均無異常';
				}
			} else {
				row.AUDIT_REMARK = '';
			}
		}
	}
	
	$scope.uploadFinshed = function(name, rname, size, runType, row) {
		switch (runType) {
			case "A":
				var isnext = false;
				
				var filetypes = [".zip", ".pdf",".msg"];
				var fileend = rname.substring(rname.lastIndexOf("."));
	            if (filetypes && filetypes.length > 0) {
	                for(var i = 0; i < filetypes.length; i++){
	                    if (filetypes[i] == fileend) {
	                        isnext = true;
	                        break;
	                    }
	                }
	            }
	            
	            if (!isnext) {
	            	$scope.showErrorMsg('限上傳PDF檔、壓縮檔或Email原始檔');
	            	
	            	$scope.inputVO.fileName = "";
			    	$scope.inputVO.fileRealName = "";
			    	row.FILE_FLAG = 'N';
			    	
	        		angular.element($("#uploadFile")).remove();
	        		angular.element($("#fileBox")).append("<e-upload id=\"uploadFile\" success=\"uploadFinshed(name, rname, size, 'A', row)\" text=\"上傳\" ng-disabled=\"(TYPE == 'search'  || (meet_rule_disabled && (row.MEET_RULE == null || row.MEET_RULE == '')) || row.CHK_DISABLE == 'CLOSE') || row.MEET_TYPE != 'E'\"></e-upload>");
	        		var content = angular.element($("#fileBox"));
	        		var scope = content.scope();
	        		$compile(content.contents())(scope);
	        		
	        		return false;
	            }
	            
	            var filemaxsize = 1024 * 5; //5M
	            var size = size / 1024;
	            if (size > filemaxsize) {
	            	isnext = false;
	            }
	            
	            if (!isnext) {
	            	$scope.showErrorMsg('檔案大小不能超過' + filemaxsize / 1024 + 'Mb。');
	            	
	            	$scope.inputVO.fileName = "";
			    	$scope.inputVO.fileRealName = "";
			    	row.FILE_FLAG = 'N';
			    	
	        		angular.element($("#uploadFile")).remove();
	        		angular.element($("#fileBox")).append("<e-upload id=\"uploadFile\" success=\"uploadFinshed(name, rname, size, 'A', row)\" text=\"上傳\" ng-disabled=\"(TYPE == 'search'  || (meet_rule_disabled && (row.MEET_RULE == null || row.MEET_RULE == '')) || row.CHK_DISABLE == 'CLOSE') || row.MEET_TYPE != 'E'\"></e-upload>");
	        		var content = angular.element($("#fileBox"));
	        		var scope = content.scope();
	        		$compile(content.contents())(scope);
	        		
	        		return false;
	            }
	            
				if (isnext) {
					$scope.inputVO.fileName = name;
			    	$scope.inputVO.fileRealName = rname;
				}
				
				break;
			case "DEL":
				$confirm({text: '是否刪除Email訪查紀錄', title: '提醒'}, {size: '120px'}).then(function() {
					$scope.inputVO.fileName = "";
			    	$scope.inputVO.fileRealName = "";
			    	row.FILE_FLAG = 'N';
			    	$scope.showSuccessMsg("ehl_01_common_003");
				});
				break;
			case "DOWNLOAD":
				$scope.sendRecv("CUS130", "download", "com.systex.jbranch.app.server.fps.cus130.CUS130InputVO", {'fileID': row.DOC_ID, 'fileName': row.DOC_NAME}, function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
				);
				break;
		}
    };
});