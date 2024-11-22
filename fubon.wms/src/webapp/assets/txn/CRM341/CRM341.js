/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM341Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $q, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM341Controller";
		
		// combobox
		$scope.pri_id = projInfoService.getPriID()[0];
		getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE", "CRM.AO_JOB_RANK", "CRM.TRS_APL_REASON", "ORG.AOCODE_TYPE", "CRM.2022CMDT_BACKORGBRH_REASON", "CRM.CMDT_REG_BACKORGRM_REASON"], function(totas) {
			if (totas) {
				$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.AO_JOB_RANK = totas.data[totas.key.indexOf('CRM.AO_JOB_RANK')];
				$scope.TRS_APL_REASON = totas.data[totas.key.indexOf('CRM.TRS_APL_REASON')];
				$scope.AOCODE_TYPE = totas.data[totas.key.indexOf('ORG.AOCODE_TYPE')];
				
				// 理專取PARAM_CODE為A開頭的選項；主管則取全部
				$scope.TRS_APL_REASON = [];
				if($scope.pri_id == '002' || $scope.pri_id == '003') {
					angular.forEach(totas.data[totas.key.indexOf('CRM.TRS_APL_REASON')], function(row) {
						
						if (row.DATA.substring(0, 1) == 'A' || row.DATA.substring(0, 2) == '99')
							$scope.TRS_APL_REASON.push(row);
					});

				} else {
					$scope.TRS_APL_REASON = totas.data[totas.key.indexOf('CRM.TRS_APL_REASON')];
				}
				
				angular.forEach(totas.data[totas.key.indexOf('CRM.2022CMDT_BACKORGBRH_REASON')], function(row) {
					$scope.CMDT_BACKORGBRH_REASON2022 = row.LABEL;
				});
				
				angular.forEach(totas.data[totas.key.indexOf('CRM.CMDT_REG_BACKORGRM_REASON')], function(row) {
					$scope.CMDT_REG_BACKORGRM_REASON = row.LABEL;
				});
			}
		});
		//
		
		$scope.clearUpload = function() {
	    	$scope.inputVO.fileName = "";
        	$scope.inputVO.realfileName = "";
        	$scope.inputVO.fileName2 = "";
        	$scope.inputVO.realfileName2 = "";
        	$scope.inputVO.agmt_seq = "";
	    };
	    
		// 初始化
	    $scope.ao_code = projInfoService.getAoCode();
//	    alert(JSON.stringify($scope.ao_code));
	    $scope.inputVO = {};
		$scope.init = function() {
			var oriId = $scope.inputVO.cust_id;
			$scope.inputVO = {};
			$scope.inputVO.cust_id = oriId;
			$scope.clearUpload();
			if (projInfoService.getAoCode()[0] != "")
				$scope.inputVO.new_ao_code = projInfoService.getAoCode()[0];
			$scope.inputVO.pri_id = projInfoService.getPriID()[0];
		};
		$scope.init();
		$scope.inquireInit = function() {
			var deferred = $q.defer();
			$scope.resultList = {};
			$scope.isExist = "";
			$scope.isCMDTCust = false;
			$scope.inputVO.is2022CMDTCust3 = false;
			$scope.inputVO.is2023CMDTCust4 = false;
			$scope.addConfirm = false;
			$scope.clearUpload();
			deferred.resolve("success");
			return deferred.promise;
		};
		$scope.inquireInit();
		
		$scope.clear = function() {
			$scope.inputVO.cust_id='';
			$scope.init();
			$scope.inquireInit();
			$scope.changeNewAO();
		}
		
		$scope.initial = function() {
			$scope.sendRecv("CRM341", "initial", "com.systex.jbranch.app.server.fps.crm341.CRM341InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.AO_LIST = [];
						angular.forEach(tota[0].body.ao_list, function(row) {
    						if(row.TYPE == '1')
    							$scope.AO_LIST.push({'LABEL': (row.EMP_NAME+'(主Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
    						else if(row.TYPE == '2')
    							$scope.AO_LIST.push({'LABEL': (row.EMP_NAME+'(副Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
    						// mantis 3901 請移除下拉選單有(維護) CODE的選項 //#5649加回來，可移入任何CODE
    						else if(row.TYPE == '3')
    							$scope.AO_LIST.push({'LABEL': (row.EMP_NAME+'(維護Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
						});
						
						$scope.AO_LIST = $filter('orderBy')($scope.AO_LIST, 'TYPE', false);
					}
				});
	    };
	    $scope.initial();
		
		//查詢
		$scope.inquire = function() {
			$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase().trim();
			// 給同意書用
			$scope.inputVO.oldVOList = {};
			$scope.sendRecv("CRM341", "inquire", "com.systex.jbranch.app.server.fps.crm341.CRM341InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
	                		return;
	                	}
						$scope.resultList = tota[0].body.resultList[0];
						if ($scope.resultList.UEMP_ID != null) {
							$scope.showMsg("此為UHRM客戶，不可進行客戶移入申請");
						}
						if($scope.resultList.AO_CODE == $scope.inputVO.new_ao_code){
							$scope.showMsg('此客戶已經是您的客戶，不需再申請移入');
							$scope.inputVO.cust_id = "";
							$scope.init();
							$scope.inquireInit();
							$scope.changeNewAO();
							return;
						}
						
						if(tota[0].body.CMDT2022CUST1_YN == 'Y' || tota[0].body.CMDT2023CUST3_YN == 'Y'){
							$scope.showErrorMsg('RM輪調後，帶走核心客戶，一年內不得再帶走該RM轄下原分行客戶');		
							$scope.inputVO.cust_id = "";
							$scope.init();
							$scope.inquireInit();
							$scope.changeNewAO();
							return;
						}
						
						$scope.inputVO.apl_reason = "";
						$scope.inputVO.apl_oth_reason = "";
						$scope.inputVO.reasonDisabled = false;
						
						//2023必輪調：非區域分行，輪調後帶至新分行之核心客戶，依客戶意願欲轉回原分行(限RM移入功能)，經由一階主管同意，再經由處主管同意後生效，客戶即可轉回原分行
						$scope.inputVO.is2023CMDTCust5 = tota[0].body.CMDT2023CUST5_YN == "Y" ? true : false;
						if(tota[0].body.CMDT2022CUST2_YN == 'Y' || $scope.inputVO.is2023CMDTCust5){
							$scope.showMsg($scope.CMDT_BACKORGBRH_REASON2022);	//輪調專案客戶移回原分行
							$scope.inputVO.apl_reason = "99";
							$scope.inputVO.apl_oth_reason = $scope.CMDT_BACKORGBRH_REASON2022;
							$scope.inputVO.reasonDisabled = true;
						}
						
						if(tota[0].body.CMDT2023CUST1_YN == 'Y' || tota[0].body.CMDT2023CUST2_YN == 'Y'){
							$scope.showErrorMsg('輪調RM名單上傳後，執行前名單中RM/客戶不可做移入申請');		
							$scope.inputVO.cust_id = "";
							$scope.init();
							$scope.inquireInit();
							$scope.changeNewAO();
							return;
						}
						
						//WMS-CR-20200214-01_新增理專輪調暨客戶換手經營資料產出及後續控管  Jacky added
						$scope.inputVO.isCMDTCust = tota[0].body.isCMDTCust;
						if(tota[0].body.isCMDTCust && !$scope.addConfirm){
							$scope.showMsg('此客戶輪調換手未滿6個月,要移入此客戶請填寫客戶指定聲明書');							
						}
						
						//是否為2022換手名單6個月內要移回原理專的客戶
						$scope.inputVO.is2022CMDTCust3 = tota[0].body.CMDT2022CUST3_YN == "Y" ? true : false;
						if($scope.inputVO.is2022CMDTCust3 && !$scope.addConfirm){
							$scope.showMsg('此客戶換手經營未滿六個月，要移入此客戶請客戶簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」並上傳表單。');							
						}
							
						//2023必輪調：區域分行非核心客戶一年內移回原理專，依客戶意願欲轉回原RM，經由一階主管同意，再經由處主管同意後生效，客戶即可轉回原RM
						$scope.inputVO.is2023CMDTCust4 = tota[0].body.CMDT2023CUST4_YN == "Y" ? true : false;
						if($scope.inputVO.is2023CMDTCust4 && !$scope.addConfirm){
							$scope.showMsg($scope.CMDT_REG_BACKORGRM_REASON);	//輪調專案客戶換手移回原RM
							$scope.inputVO.apl_reason = "99";
							$scope.inputVO.apl_oth_reason = $scope.CMDT_REG_BACKORGRM_REASON;
							$scope.inputVO.reasonDisabled = true;
						}
						
						$scope.isExist = tota[0].body.resultList2;
						$scope.inputVO.oldVOList = tota[0].body.resultList[0];
						return;
					}else{
						//清空查詢條件及結果
						$scope.inputVO.cust_id = "";						
					}
				});
	    };
	    
	    $scope.changeNewAO = function(colseType) {
	    	if (colseType == 'N') {
	    		$scope.clearUpload();
	    	}
	    	// 給同意書用
			$scope.inputVO.newVOList = {};
	    	$scope.resultList2 = {};
	    	if($scope.inputVO.new_ao_code) {
	    		var msg = $filter('i18n')('ehl_02_common_009'); //維護CODE的客戶將不計績，請再次確認是否認養或移入哦！
	    			    		
	    		if($scope.AO_LIST && $filter('filter')($scope.AO_LIST, {'DATA': $scope.inputVO.new_ao_code})[0].TYPE == '3') {
        			$confirm({text: msg , title: '提醒', hideCancel: true}, {size: 'sm'}).then(function() {
    					
    				});
        		}
	    		$scope.sendRecv("CRM341", "changeNewAO", "com.systex.jbranch.app.server.fps.crm341.CRM341InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length == 0) {
									$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.resultList2 = tota[0].body.resultList[0];
								$scope.inputVO.newVOList = tota[0].body.resultList[0];
								return;
							}
				});
	    	}
	    };
	    $scope.changeNewAO();
	    
	    $scope.clearOTH = function() {
	    	if($scope.inputVO.apl_reason != "99")
	    		$scope.inputVO.apl_oth_reason = "";
	    };
	    
	    //下載客戶同意書
    	$scope.download = function() {
    		if(!$scope.inputVO.new_ao_code) {
    			$scope.showErrorMsg('請先選擇移入AO CODE');
	    		return;
    		}
    		// 下載前先檢核：主CODE客戶不可移轉至維護CODE
    		if($scope.resultList.TYPE == '1' && $scope.resultList2.NEW_AO_TYPE == '3') {
    			$scope.showErrorMsg('主CODE客戶不可移轉至維護CODE');
	    		return;
        	} else {
	    		$scope.sendRecv("CRM341", "download", "com.systex.jbranch.app.server.fps.crm341.CRM341InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.showMsg('ehl_01_common_004');
							return;
						}
				});
        	}
		};
	    
	    //上傳客戶同意書
	    $scope.uploadFinshed = function(name, rname , file) {
	    	if(file == 'file1'){
	    		$scope.inputVO.fileName = name;
	        	$scope.inputVO.realfileName = rname;
	    	}
	    	if(file == 'file2'){
	    		$scope.inputVO.fileName2 = name;
	        	$scope.inputVO.realfileName2 = rname;
	    	}
        };
        
        $scope.checkupload = function() {
        	var deferred = $q.defer();
        	$scope.sendRecv("CRM341", "upload", "com.systex.jbranch.app.server.fps.crm341.CRM341InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							deferred.resolve(tota[0].body.resultList);
						} else
							deferred.reject();
			});
        	return deferred.promise;
        };
        
        $scope.checkadd = function() {
        	$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase().trim();

        	//2022換手名單：已換手經營客戶未滿6個月移轉回原個金RM，需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」
        	if($scope.inputVO.is2022CMDTCust3) {
        		if(!$scope.inputVO.fileName) { 
        			$scope.showErrorMsg('此客戶換手經營未滿六個月，要移入此客戶請客戶簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」並上傳表單。');
        			return;
        		}
        		if(!$scope.inputVO.fileName2) { //十保流程
        			$scope.showErrorMsg('此客戶換手經營未滿六個月，要移入此客戶請客戶簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」並上傳表單。');
        			return;
        		}
    		}
        	
        	//2023必輪調：區域分行非核心客戶一年內移回原理專，需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」
        	if($scope.inputVO.is2023CMDTCust4) {
        		if(!$scope.inputVO.fileName) { 
        			$scope.showErrorMsg('輪調專案客戶移回原RM，要移入此客戶請客戶簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」並上傳表單。');
        			return;
        		}
        		if(!$scope.inputVO.fileName2) { //十保流程
        			$scope.showErrorMsg('輪調專案客戶移回原RM，要移入此客戶請客戶簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」並上傳表單。');
        			return;
        		}
    		}
    		
    		
        	if(!$scope.resultList.CUST_ID || !$scope.inputVO.apl_reason || !$scope.resultList2.NEW_AO_CODE) {
        		$scope.showErrorMsg('請查詢客戶或選取AO CODE和申請移入原因');
	    		return;
        	}
        	
        	// 申請他行空CODE移入維護CODE
        	if(!$scope.resultList.AO_CODE && $scope.resultList.BRA_NBR != $scope.resultList2.NEW_BRA_NBR && $scope.resultList2.NEW_AO_TYPE == 3 && !$scope.inputVO.realfileName) {
    			$scope.showErrorMsg('需經客戶簽署「主要往來方行異動申請書」，上傳後才可申請移入');
	    		return;
        	}
        	
        	// 主CODE客戶不可移轉至維護CODE
        	if($scope.resultList.TYPE == '1' && $scope.resultList2.NEW_AO_TYPE == '3') {
    			$scope.showErrorMsg('主CODE客戶不可移轉至維護CODE');
	    		return;
        	}
        	
        	// 跨分行移轉須上傳文件者
        	if($scope.resultList.BRA_NBR != $scope.resultList2.NEW_BRA_NBR && $scope.resultList.AO_CODE) {
        		if(!$scope.inputVO.isCMDTCust){ //原來流程
        			if(!$scope.inputVO.fileName) {
            			$scope.showErrorMsg('跨分行移轉需上傳客戶異動申請書');
        	    		return;
            		}
        		}else{
        			if(!$scope.inputVO.fileName) {
            			$scope.showErrorMsg('跨分行移轉需上傳客戶異動暨資況表申請書');
        	    		return;
            		}
        			if(!$scope.inputVO.fileName2) {
            			$scope.showErrorMsg('需上傳客戶指定理專服務申請書');
        	    		return;
            		}
        		}	
        	}else{
        		if($scope.inputVO.isCMDTCust && !$scope.inputVO.fileName){ //十保流程
        			$scope.showErrorMsg('此客戶為十保監控客戶,同分行移入需上傳客戶資況表申請書');
        			return;
        		}
        		if($scope.inputVO.isCMDTCust && !$scope.inputVO.fileName2){ //十保流程
        			$scope.showErrorMsg('此客戶為十保監控客戶,同分行移入需上傳客戶指定理專服務申請書');
        			return;
        		}
        	}
        	
        	// 移入前和移入後相同Code,應不可申請移入
        	if($scope.resultList.AO_CODE == $scope.inputVO.new_ao_code) {
        		$scope.showErrorMsg('移入前和移入後相同Code，應不可申請移入');
	    		return;
        	}
        	
        	// 法金法人戶空CODE登陸無法由系統登錄，需由人工登錄
        	if(!$scope.resultList.AO_CODE && $scope.resultList.CO_ACCT_YN == 'Y' && $scope.resultList.CUST_ID.length < 10) {
        		$scope.showErrorMsg('法金法人戶空CODE登錄無法由系統登錄，需由人工登錄');
	    		return;
        	}
        	
        	// 超過30天申請書的申請序號將失效
        	if($scope.inputVO.fileName && !$scope.inputVO.isCMDTCust && !$scope.inputVO.is2022CMDTCust3 && !$scope.inputVO.is2023CMDTCust4) { //同行十保客戶不需填寫異動單編號
        		if(!$scope.inputVO.agmt_seq) {
        			$scope.showErrorMsg('未填寫異動單編號');
        			return;
        		}
            	$scope.checkupload().then(function(data) {
            		if(data.length == 0) {
            			$scope.showErrorMsg('異動單編號檢核有誤，請重新輸入');
            			return;
            		}
            		// apl_seq 只會有一筆
            		var temp = data[0];
            		if(temp.VALID_DAY > 30) {
            			$scope.showErrorMsg('異動單申請書編號錯誤或超過30天期限');
            			return;
            		}
            		
//            		var existIndex = $scope.ao_code.indexOf(temp.NEW_AO_CODE);
//            		if(existIndex <= -1)
            		if(temp.NEW_AO_CODE != $scope.inputVO.new_ao_code) {
            			$scope.showErrorMsg('同意書下載的理專和申請移入理專不同人，不可進行移入申請');
            			return;
            		}
            		if(temp.CUST_ID != $scope.inputVO.cust_id) {
            			$scope.showErrorMsg('同意書下載的客戶ID和申請移入的客戶ID，不同人不可進行移入申請');
            			return;
            		}
            		reallydo();
            	},function(reason) {
            		$scope.showErrorMsg('檢核上傳失敗');
            	});
        	}
        	else
        		reallydo();
        };
        
        function reallydo() {
        	
        	$scope.sendRecv("CRM341", "checkadd", "com.systex.jbranch.app.server.fps.crm341.CRM341InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.resultList2 == 'ERR1') {
								$scope.showErrorMsg("目前無設定"+ $scope.resultList2.NEW_ROLE_NAME + "的總客戶數上限");
							}
							else if (tota[0].body.resultList2 == 'ERR2') {
								var cctext = "超過"+ $scope.resultList2.NEW_ROLE_NAME + "的總客戶數上限，是否繼續?";
								$confirm({text: cctext}, {size: 'sm'}).then(function() {
									$scope.sendRecv("CRM341", "addconfirm", "com.systex.jbranch.app.server.fps.crm341.CRM341InputVO", $scope.inputVO,
											function(totas, isError) {
							    				if (isError) {
							    					$scope.showErrorMsgInDialog(totas.body.msgData);
							    				return;
							    				}
							    				if (totas.length > 0) {
							    					$scope.showSuccessMsg('ehl_01_common_004');
							    					$scope.inquireInit();
							    					//2020/11/25 蓁蓁說不需要再顯示查詢後的結果.
							    					//$scope.inquire();
							    				};
									});
								});
							}
							// sen issue 4131 20180111
							else if (tota[0].body.resultList2 == 'ERR7') {
								$scope.showErrorMsg("原理專離職，跨分行申請，必須填寫申請單");
							}
							else if (tota[0].body.resultList2 == 'ERR8') {
								$scope.showErrorMsg("客戶在ON CODE排除名單中，不可做移入申請");
							}
							else if (tota[0].body.resultList2 == 'ERR3') {
								$scope.showErrorMsg("客戶已在待覆核流程中");
							}
							else if (tota[0].body.resultList2 == 'ERR4') {
								$scope.showErrorMsg("FC客戶不可移入FCH");
							}
							else if (tota[0].body.resultList2 == 'ERR5') {
								$scope.showErrorMsg("FCH計績 Code不可轉入FCH維護Code");
							}
							else if (tota[0].body.resultList2 == 'ERR6') {
								$scope.showErrorMsg("公司戶與公司負責人掛Code需一致");
							}
							else {
								$scope.sendRecv("CRM341", "addconfirm", "com.systex.jbranch.app.server.fps.crm341.CRM341InputVO", $scope.inputVO,
										function(totas, isError) {
						    				if (isError) {
						    					$scope.showErrorMsgInDialog(totas.body.msgData);
						    				return;
						    				}
						    				if (totas.length > 0) {						    					
						    					$scope.showSuccessMsg('ehl_01_common_004');
						    					$scope.inquireInit().then(function(){
						    						$scope.addConfirm = true;
						    					});
						    					
						    					//2020/11/25 蓁蓁說不需要再顯示查詢後的結果.
						    					//$scope.inquire();
						    					
						    				};
								});
							}
						}
			});
        }
        
		$scope.isHighAge = function(){		
			$scope.sendRecv("CRM512", "isHighAge", "com.systex.jbranch.app.server.fps.crm512.CRM512InputVO", {custID : $scope.inputVO.cust_id},
					function(tota, isError) {
					if (!isError) {
						if(tota[0].body.isHighAge){
							$scope.showMsg("ehl_02_CRM512_001");
						}
					}					
				});
		}; 
		
		$scope.checkUHRM = function () {
			if ($scope.inputVO.cust_id != undefined || $scope.inputVO.cust_id != '' || $scope.inputVO.cust_id != null) {
				$scope.inputVO.custID = $scope.inputVO.cust_id;
				$scope.sendRecv("REF110", "queryCustProfile", "com.systex.jbranch.app.server.fps.ref110.REF110InputVO", $scope.inputVO, function(tota, isError) {
					if (!isError) {
						// 高端_P3
						if (tota[0].body.uhrmYN == 'Y') { //此為高端客戶，請洽詢個人高端RM
							$scope.showErrorMsg("此為高端客戶，請洽詢個人高端RM");
							$scope.inputVO.cust_id = '';
							$scope.inputVO.custID = '';
							return;
						} 
					}
				});
			}
		}
        
   
});
		