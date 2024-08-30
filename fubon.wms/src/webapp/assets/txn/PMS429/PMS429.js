/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS429Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter, sysInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "PMS429Controller";
		
        /*** 可示範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	if($scope.inputVO.sCreDate != '' && $scope.inputVO.sCreDate != null && $scope.inputVO.sCreDate != undefined){
        		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	} else {
        		let defaultDate = new Date().setDate(new Date().getDate() - 14);
		        let defaultEndDate = new Date();
        		$scope.inputVO.reportDate = defaultDate;
        	}
        };
        
        // combobox
    	$scope.getParameter = function() {
    		getParameter.XML(["PMS.STATEMENT_SEND_TYPE", "PMS.ROTATION_RTN_STATUS", "PMS.ROTATION_BRMGR_STATUS", 
	    	                  "FUBONSYS.FC_ROLE", "FUBONSYS.BMMGR_ROLE","PMS.ROTATION_PROCESS_STATUS", "PMS.CUST_PROCE_STATUS", "PMS.ROTATION_UHRMMGR"], function(totas) {
	    		if (totas) {
	    			$scope.mappingSet['PMS.STATEMENT_SEND_TYPE'] = totas.data[totas.key.indexOf('PMS.STATEMENT_SEND_TYPE')];
	    			$scope.mappingSet['PMS.ROTATION_RTN_STATUS'] = totas.data[totas.key.indexOf('PMS.ROTATION_RTN_STATUS')];
	    			$scope.mappingSet['PMS.ROTATION_BRMGR_STATUS'] = totas.data[totas.key.indexOf('PMS.ROTATION_BRMGR_STATUS')];
	    			$scope.mappingSet['FUBONSYS.BMMGR_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.BMMGR_ROLE')];
	    			$scope.mappingSet['FUBONSYS.FC_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.FC_ROLE')];
	    			$scope.mappingSet['RTN_STATUS_AST'] = [];
	    			$scope.mappingSet['RTN_STATUS_AST'].push({LABEL: '相符', DATA: 'Y'},{LABEL: '不符', DATA: 'N'},{LABEL: '未填寫', DATA: 'E'});
	    			$scope.mappingSet['RTN_STATUS_NP'] = [];
	    			$scope.mappingSet['RTN_STATUS_NP'].push({LABEL: '有', DATA: 'Y'},{LABEL: '無', DATA: 'N'},{LABEL: '未填寫', DATA: 'E'});
	    			$scope.mappingSet['PMS.ROTATION_PROCESS_STATUS'] = totas.data[totas.key.indexOf('PMS.ROTATION_PROCESS_STATUS')];
	    			$scope.mappingSet['PMS.CUST_PROCE_STATUS'] = totas.data[totas.key.indexOf('PMS.CUST_PROCE_STATUS')];
	    			$scope.mappingSet['PMS.ROTATION_UHRMMGR'] = totas.data[totas.key.indexOf('PMS.ROTATION_UHRMMGR')];
	    			debugger
	    			$scope.isFC = false; //理專權限
	    			angular.forEach($scope.mappingSet['FUBONSYS.FC_ROLE'], function(row, index, objs){
	    				if(sysInfoService.getRoleID() == row.DATA) {
	    				   $scope.isFC = true;
	    				   $scope.inputVO.statement_send_type = '3';
	//    				   $scope.dateCheck = 'Y';
	//    				   $scope.dateChangeByCheckBox();
	    				}
	    			});
	    			
	    			$scope.isBMMGR = false; //分行主管權限
	                angular.forEach($scope.mappingSet['FUBONSYS.BMMGR_ROLE'], function(row){
	                	if(sysInfoService.getRoleID() == row.DATA) {
	                		$scope.isBMMGR = true;
	                	}
	    			});
	                
	                $scope.inputVO.UHRMMGR_FLAG = "N"; //高端主管註記
	                angular.forEach($scope.mappingSet['PMS.ROTATION_UHRMMGR'], function(row){
	                	if(sysInfoService.getRoleID() == row.DATA) {
	                		$scope.inputVO.UHRMMGR_FLAG = "Y"; 
	                		$scope.isBMMGR = true; //高端主管也是分行主管權限
	                	}
	    			});
//	                if($scope.memLoginFlag == "UHRM") $scope.isFC = true; //高端理專不須特別指定，本來就是兼職理專
	    		}
	    	});
    	}
    	
    	//取得所有加強管控專案
    	$scope.getAllPRJ = function() {
			$scope.sendRecv("PMS429", "getAllPRJ", "com.systex.jbranch.app.server.fps.pms429.PMS429InputVO", $scope.inputVO,
					function(tota, isError) {
					   if(!isError){
						   $scope.TRS_PROJNAME = [];
		                	angular.forEach(tota[0].body.resultList, function(row) {
		                		$scope.TRS_PROJNAME.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_ID});
		                	});
					   }
				});
		};
        
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";	
		
		// date picker
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
		};
		$scope.rec_dateOptions = {
				maxDate : new Date(),
				minDate : null
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.openForRow = function($event, row) {
			$event.preventDefault();
			$event.stopPropagation();
			row.isOpen = !row.isOpen;
		};
		
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.endDate || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
		};
		// date picker end
		
		//檢視所有名單(回函/未回函)
		$scope.dateChangeByCheckBox = function(){
			if ($scope.dateCheck === "Y") {
				$scope.inputVO.sCreDate = null;
				$scope.inputVO.endDate = null;
			} else if ($scope.dateCheck === "N") {
				let defaultDate = new Date().setDate(new Date().getDate() - 14);
		        let defaultEndDate = new Date();
				$scope.inputVO.sCreDate = defaultDate;
				$scope.inputVO.endDate = defaultEndDate;
			}
		};
		
		/*** 可示範圍  JACKY共用版  END***/			

		$scope.query = function(){
			if($scope.checkNull($scope.inputVO.PRJ_ID)) {
				$scope.showErrorMsg("請先選擇專案");
				return;
			}
			
			$scope.inputVO.regionList = $scope.REGION_LIST;
			$scope.inputVO.areaList = $scope.AREA_LIST;
            $scope.inputVO.branchList = $scope.BRANCH_LIST;
            $scope.inputVO.aoCodeList = $scope.AO_LIST;
            	
//			alert(JSON.stringify($scope.inputVO));
			$scope.sendRecv("PMS429", "queryData", "com.systex.jbranch.app.server.fps.pms429.PMS429InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							debugger
							$scope.inputVO.rotationBRMsg = ""; //清除首頁來的查詢資料
							
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								$scope.paramList=[];
								return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							
							angular.forEach($scope.paramList, function(row) {
								var custFlag = '';
								if (row.COMPLAIN_YN == 'Y') {
									custFlag = '客訴戶';
								}
								if (row.COMM_NS_YN == 'Y') {
									if (custFlag.length > 0) custFlag += '、'
									custFlag += 'NS禁銷戶';
								}
								if (row.COMM_RS_YN == 'Y') {
									if (custFlag.length > 0) custFlag += '、'
									custFlag += 'RS拒銷戶';
								}
								if (row.DEATH_YN == 'Y') {
									if (custFlag.length > 0) custFlag += '、'
									custFlag += '死亡戶';
								}
								row.CUST_FLAG = custFlag;
							});
							
							return;
						}						
			});
		};
		
		$scope.updateFlag = function (row){
			row.UPDATE_FLAG = 'Y';			
		}

		$scope.save = function(){
			$scope.updateList = [];
			var checkData = true;
			angular.forEach($scope.paramList, function(row, index, objs){
				if(row.UPDATE_FLAG == 'Y') {
					if($scope.isFC) {
						if(!row.AO_CONTACT_YN){
							$scope.showErrorMsg("自取確認單理專是否已連繫必須選擇。");
							checkData = false;
							return;
						};	
					} else if ($scope.isBMMGR) {
						var noData = false;
						
						//若都沒有輸入，不需儲存
						if($scope.checkNull(row.CONTACT_STATUS) && $scope.checkNull(row.REC_SEQ) && 
						   $scope.checkNull(row.REC_DATE) && $scope.checkNull(row.CONTACT_MEMO) && (row.STATEMENT_SEND_TYPE == '3' ? $scope.checkNull(row.AO_CONTACT_YN) : true)) {
							row.UPDATE_FLAG = 'N';
							noData = true;
						} else {						
							if($scope.checkNull(row.AO_CODE) && row.STATEMENT_SEND_TYPE == '3' && $scope.checkNull(row.PROCESS_STATUS)) {
								//自取對帳單若AO為空，可由主管聯繫客戶(PROCESS_STATUS=null，尚未聯繫客戶)
							} else {
								if(!$scope.checkNull(row.CONTACT_STATUS)) {
									if(!(row.CONTACT_STATUS === '1' || row.CONTACT_STATUS === '2' || row.CONTACT_STATUS === '3' || row.CONTACT_STATUS === '4' 
										|| row.CONTACT_STATUS === '5' || row.CONTACT_STATUS === '6') 
											&& ($scope.checkNull(row.REC_SEQ) || $scope.checkNull(row.REC_DATE))) {
										//錄音訪談紀錄若非選擇(無須電話錄音)之選項，錄音日期&錄音序號為必填
										$scope.showErrorMsg("錄音訪談紀錄若非選擇(無須電話錄音)之選項，錄音日期&錄音序號為必填。");
										checkData = false;
										return;
									}
									
									if((row.CONTACT_STATUS === '1' || row.CONTACT_STATUS === '2' || row.CONTACT_STATUS === '3' || row.CONTACT_STATUS === '4') 
											&& $scope.checkNull(row.RECEIVE_DATE)) {
										//回函日期沒有值，錄音訪談紀錄若為1, 2, 3, 4則不得儲存
										$scope.showErrorMsg("沒有回函日期，錄音訪談紀錄不得選擇此選項。");
										checkData = false;
										return;
									}
								} else {
									if(!$scope.checkNull(row.REC_SEQ) || !$scope.checkNull(row.REC_DATE)) {
										//錄音訪談紀錄為空，但有輸入錄音日期及序號，則錯誤訊息：錄音訪談紀錄欄位必須選擇
										$scope.showErrorMsg("錄音訪談紀錄欄位選擇後才可填入錄音日期&錄音序號。");
										checkData = false;
										return;
									}
								};	
							}
						}
					}
					
					if(!noData) {
						$scope.updateList.push(row);
					}
				};
			});
			
			if(checkData && $scope.updateList.length > 0) {
				$scope.sendRecv("PMS429", "save", "com.systex.jbranch.app.server.fps.pms429.PMS429InputVO",{list: $scope.updateList},
					function(tota, isError) {
	                if (!isError) {
	                    $scope.showSuccessMsg('ehl_01_common_025');
	                    $scope.query();
	                }
	            });
			}
        };
        
		// 連至客戶首頁
        $scope.custDTL = function(row) {
        	$scope.custVO = {
    				CUST_ID   : row.CUST_ID,
    				CUST_NAME : row.CUST_NAME	
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
				
		//匯出
		$scope.exportData = function(){
			if($scope.checkNull($scope.inputVO.PRJ_ID)) {
				$scope.showErrorMsg("請先選擇專案");
				return;
			}
			
			$scope.sendRecv("PMS429","export","com.systex.jbranch.app.server.fps.pms429.PMS429InputVO", 
					{'list': $scope.paramList, 'printAllData' : false, 'PRJ_ID': $scope.inputVO.PRJ_ID}, function(tota, isError) {
						if (isError) {							
							return;            		
		            	}
					});			
		};
		
		//匯出全部資料
		$scope.exportAllData = function(){
			if($scope.checkNull($scope.inputVO.PRJ_ID)) {
				$scope.showErrorMsg("請先選擇專案");
				return;
			}
			
			$scope.sendRecv("PMS429","export","com.systex.jbranch.app.server.fps.pms429.PMS429InputVO", 
					{'list': $scope.paramList, 'printAllData' : true, 'PRJ_ID': $scope.inputVO.PRJ_ID}, function(tota, isError) {
						if (isError) {							
							return;            		
		            	}
					});			
		};
		

		
		//檢查異常通報&錄音訪談紀錄的選擇邏輯
		$scope.pairBRN_MGM_YN_CONTACT_STATUS = function(row){
			if($scope.checkNull(row.CONTACT_STATUS) || row.CONTACT_STATUS === '5' || row.CONTACT_STATUS === '6') {
				row.REC_SEQ = "";
				row.REC_DATE = null;
			}
					
			if($scope.checkNull(row.CONTACT_STATUS)) {
				//錄音訪談紀錄為空，也清空異常通報
				row.BRN_MGM_YN = "";
			} else if ((row.CONTACT_STATUS === '2' || row.CONTACT_STATUS === '3' || row.CONTACT_STATUS === '4'
					 || row.CONTACT_STATUS === '8' || row.CONTACT_STATUS === '9' || row.CONTACT_STATUS === '10')) {
				row.BRN_MGM_YN = 'N';
			} else {
				row.BRN_MGM_YN = 'Y';
			}
			
			/** 
			 * CUST_PROCE_STATUS 客戶處理情形：
			 * null    1-客戶已回函但未有訪談(含錄音)紀錄
			 * 1~4     2-客戶已完成錄音訪談紀錄
			 * 5       3-客戶不便來行，主管陪同外訪確認完成
			 * 6       4-客戶臨櫃填寫「資況表申請書」及「客戶指定個金RM自主聲明書」等表單確認完成
			 * 7~10    5-客戶人在國外或特殊原因(專簽經處長同意)，並已完成錄音訪談紀錄
			 * **/
			if ($scope.checkNull(row.CONTACT_STATUS)) {
				row.CUST_PROCE_STATUS = '1';
			} else if (row.CONTACT_STATUS === '1' || row.CONTACT_STATUS === '2' || 
					   row.CONTACT_STATUS === '3' || row.CONTACT_STATUS === '4') {
				row.CUST_PROCE_STATUS = '2';
			} else if (row.CONTACT_STATUS === '5') {
				row.CUST_PROCE_STATUS = '3';
			} else if (row.CONTACT_STATUS === '6') {
				row.CUST_PROCE_STATUS = '4';
			} else {
				row.CUST_PROCE_STATUS = '5';
			}
		};
		
		$scope.checkNull = function(data) {
			if (data == undefined || data == null || data == '') {
				return true;
			}
			return false;
		}
		
		//錄音序號檢核
		$scope.validateRecseq = function(row) {
			if(!$scope.checkNull(row.REC_SEQ) && !$scope.checkNull(row.REC_DATE)) {
				$scope.sendRecv("PMS429","validateRecseq","com.systex.jbranch.app.server.fps.pms429.PMS429InputVO", 
					{'custId': row.CUST_ID, 'recSeq': row.REC_SEQ, 'recDate': row.REC_DATE}, function(tota, isError) {
						if (!isError) {							
							if(tota[0].body.validateRecseqMsg != "") {
								row.REC_SEQ = "";
								row.REC_DATE = null;
								$scope.showErrorMsg(tota[0].body.validateRecseqMsg);
								
								return false;
							} else {
								return true;
							}    		
			           	} else {
			           		row.REC_SEQ = "";
			           		row.REC_DATE = null;
			           		
			           		return false;
			           	}
				});	
			}
		}
		
		$scope.init = function(){
			/** InputVO 初始，預設日期為系統日 **/
            let defaultDate = new Date().setDate(new Date().getDate() - 14);
            let defaultEndDate = new Date();
            $scope.inputVO = {
            		sCreDate 	: null,
            		endDate		: null,
					dataMonth	: '',
					statement_send_type : '',
					memLoginFlag : String(sysInfoService.getMemLoginFlag()).toUpperCase(),
					privilegeId: projInfoService.getPriID()[0], //權限: 043, 044 分行管理科人員
	                /** 連動組織會用到的參數 **/
	                region_center_id	: undefined,
	                branch_area_id		: undefined,
	                branch_nbr			: undefined,
	                ao_code				: undefined,
	                reportDate			: $filter('date')(defaultDate, 'yyyyMM'),
	                custId      : '',
	                PRJ_ID		: ''
            };
            
	        //組織連動
	  	    $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	  	    $scope.RegionController_setName($scope.region);
	  	    $scope.inputVO.ao_code = ""; //先不指定
	  	    
            $scope.dateCheck = "";
            $scope.dateChange();
			$scope.curDate = new Date();
			$scope.outputVO={};
			$scope.paramList = [];
			$scope.inputVO.loginRole = sysInfoService.getRoleID();
            
            //確認如果是連動來的(首頁CRM181)，請自動查詢
    		//2022年調換換手系統管控需求(未輪調) 分行主管異常通報為"正常"，但電子/實體回函有異常
            $scope.LinkedData = $scope.connector('get','LinkFlag_PMS429');
		    if ($scope.LinkedData != undefined) {
			    if ($scope.LinkedData.linked == "Y") {
					$scope.inputVO.rotationBRMsg = $scope.LinkedData.rotationBRMsg;
					$scope.query();
					
					$scope.LinkedData = undefined;
					$scope.connector('set','LinkFlag_PMS429', undefined);
				}
		    }
		    
		    //取得參數
		    $scope.getParameter();
		    //取得加強管控專案
		    $scope.getAllPRJ();
		};
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
});
