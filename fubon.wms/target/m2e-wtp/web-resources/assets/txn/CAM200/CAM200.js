/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CAM200Controller',
	function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CAM200Controller";
		
		$scope.loginType = projInfoService.getRoleID();
		$scope.tabType = $scope.connector('get','tab');
		
		/*
		 * 取得UHRM人員清單(由員工檔+角色檔)
		 */
		$scope.getPSAOSalesList = function(row) {
			$scope.sendRecv("CAM200", "getPSAOSalesList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", {sfaLeadIDList: $scope.inputVO.sfaLeadIDList, leadType: row.LEAD_TYPE}, 
					function(tota, isError) {
						if (isError) {
							return;
						}
						$scope.PASO_LIST= [];
						if (tota.length > 0) {
							$scope.PASO_LIST = tota[0].body.psaoSalesList;
						}
			});
		};
		
//		Mark: 2016/12/19 modify connect to cust homepage  by Stella		
		if($scope.connector('get','custID') != undefined && $scope.connector('get','custID') != ""){
			$scope.custID = $scope.connector('get','custID');
			$scope.custName = $scope.connector('get','custName');
			$scope.connector('set','custID', undefined);
			$scope.connector('set','custName', undefined);
		}else{
			$scope.custID = $scope.custVO.CUST_ID;
			$scope.custName = $scope.custVO.CUST_NAME;
		}
		
		$scope.custVO = {
				CUST_ID :  $scope.custID,
				CUST_NAME :$scope.custName	
		};
		
		// 點選詳細資料
		$scope.connector('set','openDialog', '1'); 
		$scope.connector('set','CRM_CUSTVO', $scope.custVO);
//		Mark: 2016/12/19 modify connect to cust homepage  by Stella	 END 
		
		$scope.pmsButton = (sysInfoService.getPriID() == '002' || sysInfoService.getPriID() == '003' ? false : true);

        $scope.init = function(){
        	var nowDate = new Date();
			var defDate = new Date(nowDate.getFullYear(), nowDate.getMonth()+1, nowDate.getDate(), nowDate.getHours(), nowDate.getMinutes());
			
			$scope.inputVO = {
					custID: $scope.custID, 
					custName: $scope.custName, 
					sfaLeadIDList: [],
					leadStatusList: [], 
					campNameList: [],
					campDescList: [],
					leadTypeList: [],
					
					cmuType: '',
					vstRecTextFormat: '', 
					visitMemo: '', 
					closedCmuType: '',
					closedVstRecTextFormat: '',
					closedVisitMemo: '', 
					
					leadType: '', 
					
					visitDate: nowDate,
					visitTime: defDate,
					visitCreply:''
        	};
			
			$scope.leadCheckLock = false; 				//多選是否LOCK
			$scope.selectedIndex = -1;
			$scope.haveChangeCount = 0; 				//可編輯筆數
			
			//通知客戶內容
			$scope.disableTextFormatStatus = true; 		//禁用字串使用狀態
	        $scope.tempDisableText = ""; 				//使用哪個禁用字串
	        
	        //客戶回應內容
			$scope.disableTextFormatStatusBYcrp = true; //禁用字串使用狀態
	        $scope.tempDisableTextBYcrp = ""; 			//使用哪個禁用字串
	        
	        $scope.tempDisable = true; //TAB3是否開啟編 輯
	        $scope.beContactDetailList = [];
	        $scope.checkVO = {};
		};
        $scope.init();
        
        // dateTime ===
		$scope.visit_date_DateOptions = {
       		maxDate: $scope.maxDate
   	    };
		$scope.model = {};
		
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.visit_date_DateOptions.maxDate =  $scope.maxDate;
		};
		// dateTime ===
		
        //===filter
		getParameter.XML(['COMMON.YES_NO', 'CAM.DISABLE_TEXT_FORMAT', 'CAM.VST_REC_TEXT_FORMAT', 'PMS.PIPELINE_PRD'], function(tota) {
			if(tota){
				$scope.mappingSet['COMMON.YES_NO'] = tota.data[tota.key.indexOf('COMMON.YES_NO')];
				$scope.mappingSet['CAM.DISABLE_TEXT_FORMAT'] = tota.data[tota.key.indexOf('CAM.DISABLE_TEXT_FORMAT')];
				$scope.mappingSet['CAM.VST_REC_TEXT_FORMAT'] = tota.data[tota.key.indexOf('CAM.VST_REC_TEXT_FORMAT')];
				$scope.mappingSet['PMS.PIPELINE_PRD'] = tota.data[tota.key.indexOf('PMS.PIPELINE_PRD')];
			}
		});   	
    	//===
    	
        // 取得待聯繫名單
        $scope.beContactTempList = [];
        $scope.getBeContactList = function() {
			$scope.sendRecv("CAM200", "getBeContactList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
        			function(tota, isError) {
        				if (!isError) {
        					if(tota[0].body.beContactList.length == 0) {
        						$scope.beContactList = [];
//        						$scope.showMsg("ehl_01_common_009");
        						return;
        					}
        					$scope.beContactList = tota[0].body.beContactList;
        					angular.copy(tota[0].body.beContactList, $scope.beContactTempList);
    						$scope.outputVO = tota[0].body;
    						
    						var haveChangeCount = 0;
    						angular.forEach($scope.beContactList, function(row, index, objs){
    							row.OLD_LEAD_STATUS = angular.copy(row.LEAD_STATUS);

    							if (row.LEAD_TYPE != '04') 
    								haveChangeCount++;
    								
    							$scope.sendRecv("CAM200", "getResList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", {campID: row.CAMPAIGN_ID, leadType: row.LEAD_TYPE, responseCode: row.LEAD_RESPONSE_CODE},
    				        			function(tota1, isError) {
	    									if (!isError) {
	    										row.set = [];
	    										angular.forEach(tota1[0].body.resultList, function(resRow, index, objs) {
	    											row.set.push({LABEL: resRow.RESPONSE_NAME, DATA: resRow.LEAD_STATUS});
	    										});
	    									}
    									}
    							);
							});
    						$scope.haveChangeCount = haveChangeCount;
    						
//    						// 2017/8/14 russle 預設第一筆
//    						$scope.showDetail($scope.beContactList[0], 0);
    						return;
        				}
        			}
			);
        };
        $scope.checkConrow = function() {
        	if ($scope.checkVO.clickConAll) {
        		angular.forEach($scope.beContactList, function(row, index) {
        			if (row.LEAD_TYPE != '99' && row.LEAD_TYPE != '05' && row.LEAD_TYPE != '06' && row.LEAD_TYPE != 'H1' && row.LEAD_TYPE != 'H2' && row.LEAD_TYPE != 'UX') {
        				row.CHECK = true;
            			$scope.showDetail(row, index);
        			}
    			});
        	} else {
        		$scope.inputVO.cmuType = "";
            	$scope.inputVO.vstRecTextFormat = "";
            	$scope.inputVO.visitMemo = "";
        		
        		$scope.beContactDetailList = [];
        		$scope.inputVO.sfaLeadIDList = [];
            	$scope.inputVO.leadStatusList = [];
            	$scope.inputVO.campNameList = [];
            	$scope.inputVO.campDescList = [];
            	$scope.inputVO.leadTypeList = [];
            	$scope.questionStatus = [];
        		
        		angular.forEach($scope.beContactList, function(row) {
        			row.CHECK = false;
    			});

        		$scope.leadCheckLock = 'release';
        		$scope.controlLockID = '';
        		$scope.controlLockType = '';
        	}
        };
        
        // 取得已過期未聯繫名單
        $scope.expiredTempList = [];
        $scope.getExpiredList = function() {
			$scope.sendRecv("CAM200", "getExpiredList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
        			function(tota, isError) {
        				if (!isError) {
        					if(tota[0].body.expiredList.length == 0) {
//        						$scope.showMsg("ehl_01_common_009");
        						return;
        					}
        					$scope.expiredList = tota[0].body.expiredList;
        					angular.copy(tota[0].body.expiredList, $scope.expiredTempList);
    						$scope.expiredListOutputVO = tota[0].body;
    						
    						angular.forEach($scope.expiredList, function(row, index, objs){
    							row.OLD_LEAD_STATUS = angular.copy(row.LEAD_STATUS);
    							$scope.sendRecv("CAM200", "getResList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", {campID: row.CAMPAIGN_ID, leadType: row.LEAD_TYPE, responseCode: row.LEAD_RESPONSE_CODE},
    				        			function(tota1, isError) {
	    									if (!isError) {
	    										row.set = [];
	    										angular.forEach(tota1[0].body.resultList, function(resRow, index, objs) {
	    											row.set.push({LABEL: resRow.RESPONSE_NAME, DATA: resRow.LEAD_STATUS});
	    										});
	    									}
    									}
    							);
							});
    						
    						// 2017/8/14 russle 預設第一筆
//    						$scope.rowDTL($scope.expiredList[0], $scope.expiredTempList[0], 0, $scope.tabType);
    						return;
        				}
        			}
			);
        };
        
        //取得已結案名單
        $scope.closedTempList = [];
        $scope.getClosedList = function() {
			$scope.sendRecv("CAM200", "getClosedList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
        			function(tota, isError) {
        				if (!isError) {
        					if(tota[0].body.closedList.length == 0) {
//        						$scope.showMsg("ehl_01_common_009");
        						return;
        					}
        					$scope.closedList = tota[0].body.closedList;
        					angular.copy(tota[0].body.closedList, $scope.closedTempList);
    						$scope.closedListVO = tota[0].body;
    						
    						var haveChangeCount = 0;
    						angular.forEach($scope.closedList, function(row, index, objs){
    							if (row.LEAD_TYPE != '04' && row.LEAD_STATUS == '03C') 
    								haveChangeCount++;
    							
    							$scope.sendRecv("CAM200", "getResList", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", {campID: row.CAMPAIGN_ID, responseCode: row.LEAD_RESPONSE_CODE, leadType: row.LEAD_TYPE},
    				        			function(tota1, isError) {
	    									if (!isError) {
	    										row.set = [];
	    										angular.forEach(tota1[0].body.resultList, function(resRow, index, objs) {
	    											row.set.push({LABEL: resRow.RESPONSE_NAME, DATA: resRow.LEAD_STATUS});
	    										});
	    									}
    									}
    							);
							});
    						$scope.haveChangeCount = haveChangeCount;

    						// 2017/8/14 russle 預設第一筆
    						$scope.rowDTL($scope.closedList[0], $scope.closedTempList[0], 0, $scope.tabType);
    						return;
        				}
        			}
			);
        };
        
        $scope.getFileList = function(row) {
        	var dialog = ngDialog.open({
				template: 'assets/txn/CAM200/CAM200_FILES.html',
				className: 'CAM200_FILES',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.campID = row.CAMPAIGN_ID;
                	$scope.stepID = row.STEP_ID;
                	$scope.campName = row.CAMPAIGN_NAME;
                }]
			});
        }
        
        $scope.questionStatus = [];
        $scope.changeStatus = function(row, tabType) {
        	if (tabType == "tab1") {
	        	angular.forEach($scope.beContactTempList, function(rowBASE){
	        		var idx = $scope.inputVO.sfaLeadIDList.indexOf(row.SFA_LEAD_ID);
					if (rowBASE.SFA_LEAD_ID == row.SFA_LEAD_ID && 
						rowBASE.LEAD_STATUS != row.LEAD_STATUS) {
			        	if (idx > -1) {
			        		$scope.inputVO.sfaLeadIDList.splice(idx, 1);
			        		$scope.inputVO.leadStatusList.splice(idx, 1);
			        		$scope.inputVO.campNameList.splice(idx, 1);
			        		$scope.inputVO.campDescList.splice(idx, 1);
			        		$scope.inputVO.leadTypeList.splice(idx, 1);
			        		$scope.questionStatus.splice(idx, 1);
			        	} 
			        	
			        	if (row.LEAD_STATUS == '01') {
			        		$scope.showErrorMsg("ehl_01_cam140_002");
			        	} else {
			        		$scope.inputVO.sfaLeadIDList.push(row.SFA_LEAD_ID);
			        		$scope.inputVO.leadStatusList.push(row.LEAD_STATUS);
			        		$scope.inputVO.campNameList.push(row.CAMPAIGN_NAME);
			        		$scope.inputVO.campDescList.push(row.CAMPAIGN_DESC);
			        		$scope.inputVO.leadTypeList.push(row.LEAD_TYPE);
			        		$scope.questionStatus.push(row.EXAMRECORD);
			        	}
					} else if (rowBASE.SFA_LEAD_ID == row.SFA_LEAD_ID && 
							   rowBASE.LEAD_STATUS == row.LEAD_STATUS) {
						if (idx > -1) {
			        		$scope.inputVO.sfaLeadIDList.splice(idx, 1);
			        		$scope.inputVO.leadStatusList.splice(idx, 1);
			        		$scope.inputVO.campNameList.splice(idx, 1);
			        		$scope.inputVO.campDescList.splice(idx, 1);
			        		$scope.inputVO.leadTypeList.splice(idx, 1);
			        		$scope.questionStatus.splice(idx, 1);
			        	} 
					}
				});
        	} else if (tabType == "tab2" || tabType == "tab3") {
        		$scope.inputVO.sfaLeadIDList = [];
        		$scope.inputVO.leadStatusList = [];
        		
        		$scope.tempList = [];
        		if (tabType == "tab2") {
        			$scope.tempList = $scope.expiredTempList;
        		} else {
        			$scope.tempList = $scope.closedTempList;
        		}

        		angular.forEach($scope.tempList, function(rowBASE){
	        		var idx = $scope.inputVO.sfaLeadIDList.indexOf(row.SFA_LEAD_ID);
					if (rowBASE.SFA_LEAD_ID == row.SFA_LEAD_ID && 
						rowBASE.LEAD_STATUS != row.LEAD_STATUS) {

			        	if (idx > -1) {
			        		$scope.inputVO.sfaLeadIDList.splice(idx, 1);
			        		$scope.inputVO.leadStatusList.splice(idx, 1);
			        	} 
			        	
		        		$scope.inputVO.sfaLeadIDList.push(row.SFA_LEAD_ID);
		        		$scope.inputVO.leadStatusList.push(row.LEAD_STATUS);
					} else if (rowBASE.SFA_LEAD_ID == row.SFA_LEAD_ID && 
							   rowBASE.LEAD_STATUS == row.LEAD_STATUS) {
						if (idx > -1) {
			        		$scope.inputVO.sfaLeadIDList.splice(idx, 1);
			        		$scope.inputVO.leadStatusList.splice(idx, 1);
			        	} 
					}
				});
        	}
        }
        
        $scope.write = function(word, tabType) {
        	if (tabType != 'tab3') {
            	$scope.inputVO.visitMemo = ($scope.inputVO.visitMemo ? $scope.inputVO.visitMemo+";" : "") + " " + word + " ";
        	} else {
            	$scope.inputVO.closedVisitMemo = ($scope.inputVO.closedVisitMemo ? $scope.inputVO.closedVisitMemo+";" : "") + " " + word + " ";

        	}
        }
        
        $scope.checkDisableText = function(word, type) {
        	if (type == 'visitMemo') $scope.disableTextFormatStatus = true;
        	if (type == 'visitCreply') $scope.disableTextFormatStatusBYcrp = true;
        	
        	if (undefined != word) {
        		$scope.esbData = $scope.connector('get','CRM611_esbData'); //FP032675Data
        		//客戶為專業投資人或有簽信託推介客戶，則不阻擋禁用文字
        		if($scope.esbData && $scope.esbData.custTxFlag == 'N' && $scope.esbData.custProFlag == 'N') {        			
	            	angular.forEach($scope.mappingSet['CAM.DISABLE_TEXT_FORMAT'], function(value) {
	            		if (word.indexOf(value.LABEL) != -1) {
	            			if (type == 'visitMemo') {
	            				$scope.tempDisableText = "：" + value.LABEL;
	            				$scope.disableTextFormatStatus = false;
	            			} else if (type == 'visitCreply') {
	            				$scope.tempDisableTextBYcrp = "：" + value.LABEL;
	            				$scope.disableTextFormatStatusBYcrp = false;
	            			}
	            		}
	            	});
        		}
        	}
        }
        
        $scope.save = function(tabType, saveType) {
        	if (tabType == 'tab1' || tabType == 'tab2') {
        		var wrongColumns = 0;
    			var notCloseColumns = 0;
    			var canCloseColumns = 0;
    			var close03CColumns = 0;
    			
        		angular.forEach($scope.inputVO.leadStatusList, function(value) {
        			var val = value.substring(0, 2);
        			
        			// 2018/11/5 LEAD_STATUS == '01' check here
        			if(value == '01')
        				wrongColumns++;
        			
        			if (value == '03C') {
        				close03CColumns++;
        			}
        			
        			if (val == '03') {
        				canCloseColumns++;
        			} else {
        				notCloseColumns++;
        			}
            	});
        		
        		if(wrongColumns > 0) {
        			$scope.showErrorMsg("ehl_01_cam140_002");
        		} else if (canCloseColumns > 0 && notCloseColumns > 0) {
        			$scope.showErrorMsg("ehl_01_cam200_001");
        		} else if (close03CColumns > 0 && (close03CColumns != (canCloseColumns + notCloseColumns))) {
        			$scope.showErrorMsg("ehl_01_cam200_003");
        		} else {
        			if (saveType == "pipeline") { // 存檔(並導入pipeline)
        				$confirm({text: '是否確認存檔並導入Pipeline？'}, {size: 'sm'}).then(function() {

            				$scope.sendRecv("CAM200", "saveDTL", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
        		    				function(totas, isError) {
        		                    	if (isError) {
        		                    		$scope.showErrorMsg(totas[0].body.msgData);
        		                    	}
        		                    	if (totas.length > 0) {
        		                    		$scope.showSuccessMsg("ehl_01_common_023");
        		                    		$scope.init();
        		                    		if (tabType == 'tab1') {
    				                    		$scope.getBeContactList();
    			                    		} else if (tabType == 'tab2') {
    			                    			$scope.getExpiredList();
    			                    		}
        		                    	};
        		    				}
        		    		);
        	            });
        			} else if (saveType == "other") { // 存檔(並開自建提醒)
            			$scope.inputVO.custID = $scope.custID;
            			
                		if (($scope.inputVO.leadStatusList).indexOf('03C') == 0) { // 結案-暫時聯絡不上客戶，另訂時間聯繫
                			var custID = $scope.custID;
                			var custName = $scope.inputVO.custName;
                			var campNameList = $scope.inputVO.campNameList;
                			var campDescList = $scope.inputVO.campDescList;
                			var dialog = ngDialog.open({
        						template: 'assets/txn/CUS160/CUS160_ADD.html',
        						className: 'CAM160_ADD',
        						showClose: false,
        		                controller: ['$scope', function($scope) {
        		                	$scope.custID = custID; 
        		                	$scope.custName = custName;
        		                	$scope.campNameList = campNameList;
        		                	$scope.campDescList = campDescList;
        		                	$scope.CUS160PAGE = "CAM200";
        		                }]
        					}).closePromise.then(function (data) { // 新增自建提醒後，編輯名單狀態與訪談內容
        						if(data.value === 'successful'){
        							$scope.sendRecv("CAM200", "saveDTL", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
        				    				function(totas, isError) {
        				                    	if (isError) {
        				                    		$scope.showErrorMsg(totas[0].body.msgData);
        				                    	}
        				                    	if (totas.length > 0) {
        				                    		$scope.showSuccessMsg("ehl_01_common_023");
        				                    		$scope.init();
        				                    		if (tabType == 'tab1') {
            				                    		$scope.getBeContactList();
        				                    		} else if (tabType == 'tab2') {
        				                    			$scope.getExpiredList();
        				                    		}
        				                    	};
        				    				}
        				    		);
        						}
        					});
                		} else {
        					$scope.sendRecv("CAM200", "saveDTL", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
        		    				function(totas, isError) {
        		                    	if (isError) {
        		                    		$scope.showErrorMsg(totas[0].body.msgData);
        		                    	}
        		                    	if (totas.length > 0) {
        		                    		$scope.showSuccessMsg("ehl_01_common_023");
        		                    		$scope.init();
        		                    		if (tabType == 'tab1') {
    				                    		$scope.getBeContactList();
				                    		} else if (tabType == 'tab2') {
				                    			$scope.getExpiredList();
				                    		}
        		                    	};
        		    				}
        		    		);
                		}
        			} else { // 銷售計畫
        				var cust_id = $scope.custID;
        				var dialog = ngDialog.open({
        					template: 'assets/txn/PMS109/PMS109.html',
        					className: 'PMS109',
        					controller: ['$scope', function($scope) {
        						$scope.cust_id = cust_id;
        						$scope.SRC_TYPE = '5';
        						$scope.emp_id = sysInfoService.getUserID();
        					}]
        				}).closePromise.then(function (data) {
        					if(data.value === 'successful'){
        						$scope.sendRecv("CAM200", "saveDTL", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
    				    				function(totas, isError) {
    				                    	if (isError) {
    				                    		$scope.showErrorMsg(totas[0].body.msgData);
    				                    	}
    				                    	if (totas.length > 0) {
    				                    		$scope.showSuccessMsg("ehl_01_common_023");
    				                    		$scope.init();
    				                    		$scope.getBeContactList();
    				                    	};
    				    				}
    				    		);
        					}
        				});
        			}
        		}
        	} else { //tab3
        		var notCloseColumns = 0;
    			var canCloseColumns = 0;
    			
        		angular.forEach($scope.inputVO.leadStatusList, function(value) {
        			var val = value.substring(0, 2);
        			
        			if (val == '03') {
        				canCloseColumns++;
        			} else {
        				notCloseColumns++;
        			}
            	});
        		
        		if (notCloseColumns > 0 && !($scope.inputVO.leadType == '05' || $scope.inputVO.leadType == '06' || $scope.inputVO.leadType == '07' || $scope.inputVO.leadType == '08')) {
        			$scope.showErrorMsg("ehl_01_cam200_002");
        		} else {
        			if (saveType == "other") {
	        			$scope.sendRecv("CAM200", "updDTL", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
	    	    				function(totas, isError) {
	    	                    	if (isError) {
	    	                    		$scope.showErrorMsg(totas[0].body.msgData);
	    	                    	}
	    	                    	if (totas.length > 0) {
	    	                    		$scope.showSuccessMsg("ehl_01_common_023");
	    	                    		$scope.init();
	    	                    		$scope.getClosedList();
	    	                    	};
	    	    				}
	    	    		);
        			} else { // 銷售計畫
        				
        				var cust_id = $scope.custID;
        				var dialog = ngDialog.open({
        					template: 'assets/txn/PMS109/PMS109.html',
        					className: 'PMS109',
        					controller: ['$scope', function($scope) {
        						$scope.cust_id = cust_id;
        						$scope.SRC_TYPE = '5';
        						$scope.emp_id = sysInfoService.getUserID();
        					}]
        				}).closePromise.then(function (data) {
        					if(data.value === 'successful'){
        						$scope.sendRecv("CAM200", "saveDTL", "com.systex.jbranch.app.server.fps.cam200.CAM200InputVO", $scope.inputVO,
    				    				function(totas, isError) {
    				                    	if (isError) {
    				                    		$scope.showErrorMsg(totas[0].body.msgData);
    				                    	}
    				                    	if (totas.length > 0) {
    				                    		$scope.showSuccessMsg("ehl_01_common_023");
    				                    		$scope.init();
    				                    		$scope.getBeContactList();
    				                    	};
    				    				}
    				    		);
        					}
        				});
        			}
        		}
        	}
        };
        
        $scope.showDetail = function(row, $index) {	

        	var nowDate = new Date();
			var defDate = new Date(nowDate.getFullYear(), nowDate.getMonth()+1, nowDate.getDate(), nowDate.getHours(), nowDate.getMinutes());

        	$scope.INFO_Y = 'Y';
        	$scope.inputVO.cmuType = "";
        	$scope.inputVO.vstRecTextFormat = "";
        	$scope.inputVO.visitMemo = "";
        	$scope.inputVO.visitDate = nowDate;
        	$scope.inputVO.visitTime = defDate;
        	$scope.inputVO.visitCreply = "";
        	
        	var idx = $scope.inputVO.sfaLeadIDList.indexOf(row.SFA_LEAD_ID);
        	if (idx > -1) {
        		$scope.inputVO.sfaLeadIDList.splice(idx, 1);
        		$scope.inputVO.leadStatusList.splice(idx, 1);
        		$scope.inputVO.campNameList.splice(idx, 1);
        		$scope.inputVO.campDescList.splice(idx, 1);
        		$scope.inputVO.leadTypeList.splice(idx, 1);
        		$scope.questionStatus.splice(idx, 1);
        		
        		if (row.LEAD_TYPE == '99' || row.LEAD_TYPE == '05' || row.LEAD_TYPE == '06' || row.LEAD_TYPE == 'H1' || row.LEAD_TYPE == 'H2' || row.LEAD_TYPE == 'UX') {
            		$scope.leadCheckLock = 'release';
            		$scope.controlLockID = '';
            		$scope.controlLockType = '';
        		} else if ($scope.inputVO.sfaLeadIDList.length == 0) {
        			$scope.leadCheckLock = 'release';
            		$scope.controlLockID = '';
            		$scope.controlLockType = '';
        		}
        	} else {
        		$scope.leadCheckLock = 'lock';
        		if (row.LEAD_TYPE == '99' || row.LEAD_TYPE == '05' || row.LEAD_TYPE == '06' || row.LEAD_TYPE == 'H1' || row.LEAD_TYPE == 'H2' || row.LEAD_TYPE == 'UX') {
            		$scope.controlLockID = row.SFA_LEAD_ID;
            		$scope.controlLockType = '';
            	} else {
            		$scope.controlLockID = '';
            		$scope.controlLockType = '99';
            	}
        	}

        	if(row.CHECK) {
            	var exiIndex = $scope.beContactDetailList.map(function(e) { return e.SFA_LEAD_ID; }).indexOf(row.SFA_LEAD_ID);
        		if(exiIndex > -1)
        			$scope.beContactDetailList.splice(exiIndex, 1);
            	
        		if (null != row.VISIT_DATE) {
        			var checkNowDate = new Date(row.VISIT_DATE);
        			var checkDefDate = new Date(checkNowDate.getFullYear(), checkNowDate.getMonth()+1, checkNowDate.getDate(), checkNowDate.getHours(), checkNowDate.getMinutes());
        			
            		$scope.inputVO.cmuType = row.CMU_TYPE;
                	$scope.inputVO.visitMemo = row.VISIT_MEMO;
                	$scope.inputVO.visitDate = checkNowDate;
    	        	$scope.inputVO.visitTime = checkDefDate;
    	        	$scope.inputVO.visitCreply = row.VISIT_CREPLY;
        		}
        		
        		if(row.INFO == 'Y')
        			$scope.beContactDetailList.push(row);
    			
    			$scope.inputVO.sfaLeadIDList.push(row.SFA_LEAD_ID);
        		$scope.inputVO.leadStatusList.push(row.LEAD_STATUS);
        		$scope.inputVO.campNameList.push(row.CAMPAIGN_NAME);
        		$scope.inputVO.campDescList.push(row.CAMPAIGN_DESC);
        		$scope.inputVO.leadTypeList.push(row.LEAD_TYPE);
        		$scope.questionStatus.push(row.EXAMRECORD);
        		
        		if ($scope.inputVO.leadStatusList[0] == '03B') {
        			$scope.PASO_LIST = [];
            		$scope.getPSAOSalesList(row);
        		}
        	} else {
        		exiIndex = $scope.beContactDetailList.map(function(e) { return e.SFA_LEAD_ID; }).indexOf(row.SFA_LEAD_ID);
        		if (exiIndex > -1) {
        			$scope.beContactDetailList.splice(exiIndex, 1);
        		} else {
        			if(row.INFO == 'Y')
            			$scope.beContactDetailList.push(row);
        			
        		}
        		exiIndex = $scope.beContactDetailList.map(function(e) { return e.SFA_LEAD_ID; }).indexOf(row.SFA_LEAD_ID);	
        	}
        };
        
        $scope.changeStatusTypeOne = function(row) {
        	$scope.inputVO.sfaLeadIDList = [];
        	$scope.inputVO.leadStatusList = [];
        	$scope.inputVO.campNameList = [];
        	$scope.inputVO.campDescList = [];
        	$scope.inputVO.leadTypeList = [];
        	$scope.questionStatus = [];
        	
        	var ans = $scope.beContactList.filter(function(obj) {
	    		return (obj.CHECK == true);
	    	});
        	angular.forEach(ans, function(row) {
    			$scope.inputVO.sfaLeadIDList.push(row.SFA_LEAD_ID);
        		$scope.inputVO.leadStatusList.push(row.LEAD_STATUS);
        		$scope.inputVO.campNameList.push(row.CAMPAIGN_NAME);
        		$scope.inputVO.campDescList.push(row.CAMPAIGN_DESC);
        		$scope.inputVO.leadTypeList.push(row.LEAD_TYPE);
        		$scope.questionStatus.push(row.EXAMRECORD);
			});

        	if ($scope.inputVO.sfaLeadIDList.length > 0 && (row.LEAD_TYPE == '05' || row.LEAD_TYPE == '06' || row.LEAD_TYPE == 'H1' || row.LEAD_TYPE== 'H2' || row.LEAD_TYPE == 'UX') && row.LEAD_STATUS == '03B') {
        		$scope.PASO_LIST = [];
        		$scope.getPSAOSalesList(row);
        	}
        };
        
        $scope.rowDTL = function(row, rowTemp, $index, tabType) {
        	
        	$scope.selectedIndex = $index;
        	$scope.INFO_Y = row.INFO;
        	
        	var nowDate;
        	var defDate;
        	if (null != row.VISIT_DATE) {
            	nowDate = new Date(row.VISIT_DATE);
    			defDate = new Date(nowDate.getFullYear(), nowDate.getMonth()+1, nowDate.getDate(), nowDate.getHours(), nowDate.getMinutes());
        	} else {
        		nowDate = new Date();
    			defDate = new Date(nowDate.getFullYear(), nowDate.getMonth()+1, nowDate.getDate(), nowDate.getHours(), nowDate.getMinutes());
        	}

			if (tabType != 'tab3') {
	        	$scope.inputVO.cmuType = row.CMU_TYPE;
	        	$scope.inputVO.visitMemo = row.VISIT_MEMO;
	        	$scope.inputVO.visitDate = nowDate;
	        	$scope.inputVO.visitTime = defDate;
	        	$scope.inputVO.visitCreply = row.VISIT_CREPLY;
	        	
	        	$scope.inputVO.leadType = row.LEAD_TYPE;
	        	$scope.inputVO.leadStatus = row.LEAD_STATUS;
	        	
	        	if (tabType == 'tab2') {
	        		$scope.inputVO.sfaLeadIDList = [];
	        		$scope.inputVO.sfaLeadIDList.push(rowTemp.SFA_LEAD_ID);
	        		$scope.inputVO.leadStatusList = [];
	        		$scope.inputVO.leadStatusList.push(row.LEAD_STATUS);
	        		$scope.inputVO.leadTypeList = [];
	        		$scope.inputVO.leadTypeList.push(row.LEAD_TYPE);
	        		
	        		if (row.LEAD_TYPE == '99' || row.LEAD_TYPE == '05' || row.LEAD_TYPE == '06' || row.LEAD_TYPE == 'H1' || row.LEAD_TYPE == 'H2' || row.LEAD_TYPE == 'UX') {
	        			$scope.tempDisableByEXP = true;
	        		} else {
	        			$scope.tempDisableByEXP = false;
	        		}
	        	}
        	} else {
        		if (rowTemp.LEAD_STATUS == '03C' || (rowTemp.LEAD_TYPE == '07' || rowTemp.LEAD_TYPE == '08')) {
        			$scope.tempDisable = false;
        		} else if ((rowTemp.LEAD_TYPE == '05' || rowTemp.LEAD_TYPE == '06' || rowTemp.LEAD_TYPE == 'H1' || rowTemp.LEAD_TYPE == 'UX') && rowTemp.LEAD_STATUS != '03B') {
        			$scope.tempDisable = false;
        		} else {
        			$scope.tempDisable = true;
        		}
        		
        		$scope.leadCheckLock = true; 							//多選是否LOCK
        		
        		$scope.inputVO.sfaLeadIDList = [];
        		$scope.inputVO.sfaLeadIDList.push(rowTemp.SFA_LEAD_ID);
        		$scope.inputVO.leadStatusList = [];
        		$scope.inputVO.leadStatusList.push(row.LEAD_STATUS);
        		$scope.inputVO.leadTypeList = [];
        		$scope.inputVO.leadTypeList.push(row.LEAD_TYPE);
        		
        		$scope.inputVO.leadType = row.LEAD_TYPE;
        		$scope.inputVO.leasStatus = row.LEAD_STATUS;

        		$scope.inputVO.closedCmuType = row.CMU_TYPE;
	        	$scope.inputVO.closedVisitMemo = row.VISIT_MEMO;
	        	$scope.inputVO.closedVisitDate = nowDate;
	        	$scope.inputVO.closedVisitTime = defDate;
	        	$scope.inputVO.closedVisitCreply = row.VISIT_CREPLY;
	        	
	    		//== 20201127 ADD BY OCEAN => 0000415: WMS-CR-20200908-01_自動產製線上留資名單報表及留資客戶承作消金商品導入消金Pipeline功能
	        	$scope.inputVO.closedEstPrd = row.EST_PRD;
	        	$scope.inputVO.closedEstAmt = row.EST_AMT;
	        	$scope.inputVO.closedPlanEmpID = row.PLAN_EMP_ID;
        	}
        }

        $scope.goAction = function(type) {
        	$scope.tabType = (typeof(type) !== 'undefined' ? type : $scope.tabType);
        	if ($scope.tabType == "tab1") {
        		$scope.getBeContactList();
        	} else if ($scope.tabType == "tab2") {
        		$scope.getExpiredList();
        	} else if ($scope.tabType == "tab3") {
        		$scope.getClosedList();
        	}
        }
        $scope.goAction();
        
        $scope.writeQuestion = function(row, actionType) {
        	var examVersion = row.EXAM_ID;
        	var custID = $scope.custID;
        	var custName = $scope.custName;
	    	var dialog = ngDialog.open({
				template: 'assets/txn/CAM200/CAM200_QUS.html',
				className: 'CAM200_QUS',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.examVersion = examVersion;
                	$scope.custID = custID;
                	$scope.custName = custName;
                	$scope.actionType = actionType;
                }]
			}).closePromise.then(function (data) {
				if(data.value === 'successful'){
					row.EXAMRECORD = 'N';
					row.EXAMRECORD_STATUS = 'Y';
				}
			});
        }
        
        $scope.goCAM200Phone = function () {
        	var custID = $scope.custID;
        	var dialog = ngDialog.open({
				template: 'assets/txn/CAM200/CAM200_PHONE.html',
				className: 'CRM521',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.custID = custID;
					$scope.routeURL = 'assets/txn/CRM621/CRM621_phone.html';
					$scope.sourcePage = "CAM200";
					$scope.txnName = "電話錯誤註記";
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
				}
			});
        }
        
		$scope.goCRM651 = function() {
			$scope.connector('set','CRM610_tab', 9);
			var path = "assets/txn/CRM610/CRM610_DETAIL.html";
			var set = $scope.connector("set", "CRM610URL", path);
			$scope.connector('set', 'CRM_CUSTVO', $scope.custVO);
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			}).closePromise.then(function (data) {
				$scope.goAction();
			});
        };
        
        $scope.custom_split = function(text) {
        	if(text)
        		return text.split('\\n');
        	else
        		return [];
        };
        
});