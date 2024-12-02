/**
 *
 */
'use strict';
eSoafApp.controller('IOT110Controller',
    function($rootScope,$scope,$controller, $q , $confirm, sysInfoService,socketService, ngDialog, projInfoService,getParameter,$filter, validateService,$timeout) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IOT110Controller";

        $scope.open_apply_date = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};

		//取得富壽保險公司資料
		$scope.getCompanyData = function() {
			$scope.mappingSet['COMPANY_FB'] = [];
			$scope.mappingSet['COMPANY_NFB'] = [];

	        $scope.sendRecv("PRD176", "inquire", "com.systex.jbranch.app.server.fps.prd176.PRD176InputVO", {},
	            function (tota, isError) {
	                if (!isError) {
	                	//富壽保險公司資料
	                    $scope.mappingSet['COMPANY_FB'] = _.chain(tota[0].body.resultList)
	                        .map(each => ({LABEL: each.CNAME, DATA: each.SERIALNUM + ''}))
	                        .filter(com => com.DATA === '82') // 富壽
	                        .value();

	                    //非富壽保險公司資料
	                    $scope.mappingSet['COMPANY_NFB'] = _.chain(tota[0].body.resultList)
	                    .map(each => ({LABEL: each.CNAME, DATA: each.SERIALNUM + ''}))
	                    .filter(com => com.DATA !== '82') // 非富壽排除「富邦」
	                    .value();
	                }
	            });
	    }
		$scope.getCompanyData();

		//富壽保險公司下拉選單
		$scope.inquireCompanyFB = function() {
			$scope.mappingSet['COMPANY'] = [];
			$scope.mappingSet['COMPANY'] = $scope.mappingSet['COMPANY_FB'];
	    }

		//非富壽保險公司下拉選單
		$scope.inquireCompanyNFB = function() {
			$scope.mappingSet['COMPANY'] = [];
			$scope.mappingSet['COMPANY'] = $scope.mappingSet['COMPANY_NFB'];
	    }

        //計算年齡
        function getAge(dateString) {
            var today = new Date();
            var birthDate = new Date(dateString);
            var age = today.getFullYear() - birthDate.getFullYear();
            var m = today.getMonth() - birthDate.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            return age;
        }

        $scope.date = new Date();
        $scope.hideTable='';
        $scope.prevRegType = '';

		//*** 取得購買檢核資料 ***//
		$scope.query = function() {
			if($scope.inputVO.PREMATCH_SEQ == null || $scope.inputVO.PREMATCH_SEQ == undefined || $scope.inputVO.PREMATCH_SEQ == '') {
				$scope.inputVO.PREMATCH_SEQ = "";
				return;
			}

			//有適合度檢核編號，取得資料
			$scope.sendRecv("IOT110", "queryData", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.hideTable = "true";
								$scope.showMsg("ehl_01_iot110_004");
							} else {
								$scope.rltBtPropayOnChange = false;
								debugger;
								$scope.inputVO = tota[0].body.resultList[0];
								//保險公司下拉選單
								if($scope.inputVO.FB_COM_YN == "N") {
									$timeout(function(){$scope.inquireCompanyNFB();},700);
								} else {
									$timeout(function(){$scope.inquireCompanyFB();},700);
								}
								$scope.inputVO.PROPOSER_BIRTH = $scope.toJsDate($scope.inputVO.PROPOSER_BIRTH);
								$scope.inputVO.INSURED_BIRTH = $scope.toJsDate($scope.inputVO.INSURED_BIRTH);
								$scope.inputVO.PAYER_BIRTH = $scope.toJsDate($scope.inputVO.PAYER_BIRTH);
								$scope.inputVO.APPLY_DATE = $scope.toJsDate($scope.inputVO.APPLY_DATE);
								$scope.inputVO.CUST_RISK_DUE = $scope.toJsDate($scope.inputVO.CUST_RISK_DUE);
								$scope.inputVO.MATCH_DATE = $scope.toJsDate($scope.inputVO.MATCH_DATE);
								$scope.inputVO.CHG_PROPOSER_BIRTH = $scope.toJsDate($scope.inputVO.CHG_PROPOSER_BIRTH);
								$scope.inputVO.C_LOAN_APPLY_DATE = $scope.toJsDate($scope.inputVO.C_LOAN_APPLY_DATE);
								$scope.inputVO.C_PREM_DATE = $scope.toJsDate($scope.inputVO.C_PREM_DATE);
								$scope.inputVO.I_LOAN_APPLY_DATE = $scope.toJsDate($scope.inputVO.I_LOAN_APPLY_DATE);
								$scope.inputVO.P_LOAN_APPLY_DATE = $scope.toJsDate($scope.inputVO.P_LOAN_APPLY_DATE);
								$scope.inputVO.C_LOAN_CHK2_DATE = $scope.toJsDate($scope.inputVO.C_LOAN_CHK2_DATE);
								$scope.inputVO.I_LOAN_CHK2_DATE = $scope.toJsDate($scope.inputVO.I_LOAN_CHK2_DATE);
								$scope.inputVO.LOAN_CHK2_DATE = $scope.toJsDate($scope.inputVO.LOAN_CHK2_DATE);
								$scope.inputVO.C_CD_DUE_DATE = $scope.toJsDate($scope.inputVO.C_CD_DUE_DATE);
								$scope.inputVO.I_CD_DUE_DATE = $scope.toJsDate($scope.inputVO.I_CD_DUE_DATE);
								$scope.inputVO.P_CD_DUE_DATE = $scope.toJsDate($scope.inputVO.P_CD_DUE_DATE);

								//要被繳有任一需要高齡/保費來源錄音序號
								$scope.inputVO.NEED_PREMIUM_TRANSSEQ_YN = "N";
					    		if($scope.inputVO.C_PREMIUM_TRANSSEQ_YN == "Y" || $scope.inputVO.I_PREMIUM_TRANSSEQ_YN == "Y" || $scope.inputVO.P_PREMIUM_TRANSSEQ_YN == "Y") {
					    			$scope.inputVO.NEED_PREMIUM_TRANSSEQ_YN = "Y";
					    		}
					    		
								$scope.inputVO.hnwcData = tota[0].body.hnwcData; //高資產客戶註記
								$scope.inputVO.INVESTList = tota[0].body.INVESTList; //險種標的清單

								//尚未送覆核案件須重讀要保人、被保人、繳款人資料，以及招攬人員資料(公平待客、高齡完訓資格)
								if($scope.inputVO.REG_TYPE == "1" && ($scope.inputVO.STATUS == '1' || $scope.inputVO.STATUS == '4')) {
									if($scope.inputVO.CUST_ID) $scope.getInfo('CUST');
									if($scope.inputVO.INSURED_ID) $scope.getInfo('INSURED');
									if($scope.inputVO.PAYER_ID) $scope.getInfo('PAYER');
									if($scope.inputVO.RECRUIT_ID) $scope.getInfo('RECRUIT');
								}

								var findPrd = $filter('filter')($scope.mappingSet['IOT.NO_CHK_LOAN_INSPRD'], {DATA: $scope.inputVO.INSPRD_ID});
								$scope.isInNoChkLoan = (findPrd != null && findPrd.length > 0) ? true : false;

								$scope.noUpdate = true;
								//refresh edit/viewonly status
								$scope.checkStatus();
								
								//人壽資料共享案件，重新取得電子要保書資料
								if($scope.inputVO.DATA_SHR_YN == "Y" && ($scope.inputVO.CUST_ID == "" || $scope.inputVO.CUST_ID == null || $scope.inputVO.CUST_ID == undefined)) {
									$scope.noUpdate = false; //人壽資料共享案件, 第一次進來可以修改資料
									$scope.getCaseIDInfo(); 
									$scope.getMAPPVideoChkType();
								}
							}
						}
			});
		}

		//refresh edit/viewonly status
		$scope.checkStatus = function() {
			if($scope.privilegeGroup != "FC" || $scope.inputVO.STATUS == "2" || $scope.inputVO.STATUS == "3") {
				//只有FC(有可鍵機Privilege)，狀態不為"覆核中"以及"已核可"才能修改
				$scope.viewOnly = true;
			} else {
				$scope.viewOnly = false;
			}

			debugger
			//目前狀態為待覆核
			$scope.brhmgrAuthing = false;
			if($scope.inputVO.STATUS == "2") {
				//是否為主管的可視分行
				var findBranch = $filter('filter')($scope.AvailBranchList, {BRANCH_NBR: $scope.inputVO.BRANCH_NBR});

				if($scope.inputVO.AML == "高" || $scope.inputVO.PRECHECK == "高" || (($scope.inputVO.AML == "" || $scope.inputVO.AML == null) && $scope.inputVO.PRECHECK == "NONE")) {
					//AML或PRECHECK為高或未開戶，則需分行主管以上層級才可覆核(分行主管、營運督導、處主管)
					if($scope.isAMLAuthRole && $scope.inputVO.UHRM_CASE == "Y" && $scope.memLoginFlag == "UHRMMGR") {
						//UHRM案件且為UHRM主管登入，狀態為主管覆核中，顯示"核可"、"退回" Button
						$scope.brhmgrAuthing = true;
					} else if($scope.isAMLAuthRole && $scope.inputVO.UHRM_CASE == "N" && (findBranch != null && findBranch != undefined && findBranch.length > 0)) {
						//非UHRM案件，主管登入，狀態為主管覆核中，顯示"核可"、"退回" Button
						$scope.brhmgrAuthing = true;
					}
				} else if($scope.privilegeGroup == "BRHMGR") {
					if($scope.inputVO.UHRM_CASE == "Y" && ($scope.isHeadMgr || $scope.memLoginFlag == "UHRMMGR")) {
						//UHRM案件且為總行或UHRM主管登入，狀態為主管覆核中，顯示"核可"、"退回" Button
						$scope.brhmgrAuthing = true;
					} else if($scope.inputVO.UHRM_CASE == "N" && ($scope.isHeadMgr || (findBranch != null && findBranch != undefined && findBranch.length > 0))) {
						//非UHRM案件，總行主管或鍵機理專的主管登入，狀態為主管覆核中，顯示"核可"、"退回" Button
						$scope.brhmgrAuthing = true;
					}
				}				
			}

			//目前狀態為已覆核
			$scope.brhmgrAllowDeny = false;
			if($scope.inputVO.STATUS == "3") {
				var findBranch = $filter('filter')($scope.AvailBranchList, {BRANCH_NBR: $scope.inputVO.BRANCH_NBR});

				if($scope.inputVO.AML == "高" || $scope.inputVO.PRECHECK == "高" || (($scope.inputVO.AML == "" || $scope.inputVO.AML == null) && $scope.inputVO.PRECHECK == "NONE")) {
					//AML或PRECHECK為高或未開戶，則需分行主管以上層級才可覆核、退回(分行主管、營運督導、處主管)
					if($scope.isAMLAuthRole && $scope.inputVO.UHRM_CASE == "Y" && $scope.memLoginFlag == "UHRMMGR") {
						//UHRM案件且為UHRM主管登入，狀態為已覆核，主管仍可退回
						$scope.brhmgrAllowDeny = true;
					} else if($scope.isAMLAuthRole && $scope.inputVO.UHRM_CASE == "N" && (findBranch != null && findBranch != undefined && findBranch.length > 0)) {
						//總行主管或鍵機理專的主管登入，狀態為已覆核，主管仍可退回
						$scope.brhmgrAllowDeny = true;
					}
				} else if($scope.privilegeGroup == "BRHMGR") {
					if($scope.inputVO.UHRM_CASE == "Y" && ($scope.isHeadMgr || $scope.memLoginFlag == "UHRMMGR")) {
						//UHRM案件且為總行或UHRM主管登入，狀態為已覆核，主管仍可退回
						$scope.brhmgrAllowDeny = true;
					} else if($scope.inputVO.UHRM_CASE == "N" && ($scope.isHeadMgr || (findBranch != null && findBranch != undefined && findBranch.length > 0))) {
						//總行主管或鍵機理專的主管登入，狀態為已覆核，主管仍可退回
						$scope.brhmgrAllowDeny = true;
					}
				}
			}

			//取得視訊投保檢核項目類型(覆核層級)
			$scope.getMAPPVideoChkType();

			$scope.regType3AuthChange();
		}

		$scope.regType3AuthChange = function() {
			debugger
			if($scope.inputVO.REG_TYPE == "3" && $scope.inputVO.CHG_CUST_ID != "") {
				if(!$scope.branch_cust || $scope.inputVO.AML == "高" || $scope.inputVO.PRECHECK == "高" || (($scope.inputVO.AML == "" || $scope.inputVO.AML == null) && $scope.inputVO.PRECHECK == "NONE")) {
					//若變更後要保人未開戶、AML或PRECHECK為高或未開戶，需覆核
					$scope.regType3AuthDisabled = false;
				} else {
					//AML與PRECHECK不為高也不為未開戶，不須覆核
					$scope.regType3AuthDisabled = true;
				}
			}
		}

		$scope.getMAPPVideoChkType = function() {
			//取得視訊投保檢核項目類型(覆核層級)
			if($scope.privilegeGroup == "FC" && ($scope.inputVO.STATUS == "" || $scope.inputVO.STATUS == "1" || $scope.inputVO.STATUS == "4")) {
				//理專層級
				$scope.inputVO.MAPP_CHKLIST_TYPE = "1";
			} else if($scope.brhmgrAuthing && $scope.inputVO.STATUS == "2") {
				//主管覆核層級
				$scope.inputVO.MAPP_CHKLIST_TYPE = "2";
			} else {
				$scope.inputVO.MAPP_CHKLIST_TYPE = ""; //viewOnly
			}
		}

		//將要保人ID與姓名帶入被保人ID與姓名
		$scope.toSameCustId = function(){
			if($scope.chkSameCustId){
				$scope.inputVO.INSURED_ID = $scope.inputVO.CUST_ID;
				$scope.inputVO.INSURED_NAME = $scope.inputVO.PROPOSER_NAME;
				$scope.inputVO.INSURED_BIRTH = $scope.inputVO.PROPOSER_BIRTH;
				$scope.inputVO.INSURED_CM_FLAG = $scope.inputVO.PROPOSER_CM_FLAG;
				$scope.inputVO.INSURED_INCOME3 = $scope.inputVO.PROPOSER_INCOME3;
				$scope.inputVO.I_LOAN_CHK1_YN = $scope.inputVO.C_LOAN_CHK1_YN;
				$scope.inputVO.I_LOAN_CHK2_YN = $scope.inputVO.C_LOAN_CHK2_YN;
				$scope.inputVO.I_LOAN_CHK2_DATE = $scope.inputVO.C_LOAN_CHK2_DATE;
				$scope.inputVO.I_LOAN_CHK3_YN = $scope.inputVO.C_LOAN_CHK3_YN;
				$scope.inputVO.I_LOAN_CHK4_YN = $scope.inputVO.C_LOAN_CHK4_YN;
				$scope.inputVO.I_REVOLVING_LOAN_YN = $scope.inputVO.C_REVOLVING_LOAN_YN;
				$scope.inputVO.I_CD_CHK_YN = $scope.inputVO.C_CD_CHK_YN;
				$scope.inputVO.I_CD_DUE_DATE = $scope.inputVO.C_CD_DUE_DATE;
				$scope.inputVO.I_LOAN_APPLY_DATE = $scope.inputVO.C_LOAN_APPLY_DATE;
				$scope.inputVO.I_LOAN_APPLY_YN = $scope.inputVO.C_LOAN_APPLY_YN;
				$scope.inputVO.I_KYC_INCOME = $scope.inputVO.C_KYC_INCOME;
				$scope.inputVO.INSURED_DEBIT = $scope.inputVO.CUST_DEBIT;
			}else{
				$scope.inputVO.INSURED_ID = '';
				$scope.inputVO.INSURED_NAME = '';
				$scope.inputVO.INSURED_BIRTH = null;
				$scope.inputVO.INSURED_CM_FLAG = '';
				$scope.inputVO.INSURED_INCOME3 = undefined;
				$scope.inputVO.I_LOAN_CHK1_YN = '';
				$scope.inputVO.I_LOAN_CHK2_YN = '';
				$scope.inputVO.I_LOAN_CHK2_DATE = null;
				$scope.inputVO.I_LOAN_CHK3_YN = '';
				$scope.inputVO.I_LOAN_CHK4_YN = '';
				$scope.inputVO.I_REVOLVING_LOAN_YN = '';
				$scope.inputVO.I_CD_CHK_YN = '';
				$scope.inputVO.I_CD_DUE_DATE = null;
				$scope.inputVO.I_LOAN_APPLY_DATE = null;
				$scope.inputVO.I_LOAN_APPLY_YN = '';
				$scope.inputVO.I_KYC_INCOME = '';
				$scope.inputVO.INSURED_DEBIT = undefined;
			}

			//計算要保人/被保人工作年收入(業報)/(財告)
			$scope.calInCome();
		}

		//要保人變更：將被保人ID與姓名帶入變更後要保人ID與姓名
		$scope.toSameInsuredId = function(){
			if($scope.chkSameInsuredId){
				$scope.inputVO.CHG_CUST_ID = $scope.inputVO.INSURED_ID;
				$scope.getInfo("CHG_CUST");
			}else{
				$scope.inputVO.CHG_CUST_ID = '';
				$scope.inputVO.CHG_PROPOSER_NAME = '';
				$scope.inputVO.CHG_PROPOSER_BIRTH = undefined;
				$scope.inputVO.AML = "";
				$scope.inputVO.PRECHECK = "";
				$scope.inputVO.BUSINESS_REL = "";
			}
		}

		//*****日曆的使用*******//
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};

		// 設定時間
		$scope.altInputFormats = ['M!/d!/yyyy'];

		$scope.timesys = new Date();
		$scope.endDateOptions = {
				maxDate : $scope.maxDate,
				minDate : $scope.timesys
		};
		/** *設定時間結束** */


        $scope.allItems = function () {
			 var dialog = ngDialog.open({
				  template: 'assets/txn/IOT110/AllItems.html',
				  className: 'AllItems',
		          showClose: true
		     });
		};

		 $scope.serviceNetwork = function () {
			 var dialog = ngDialog.open({
				  template: 'assets/txn/IOT110/ServiceNetwork.html',
				  className: 'ServiceNetwork',
		          showClose: true
		     });
		};

		 $scope.insAdministratForm = function () {
			 var dialog = ngDialog.open({
				  template: 'assets/txn/IOT110/InsAdministratForm.html',
				  className: 'InsAdministratForm',
		          showClose: true
		     });
		};

		// 檢核招攬人員證照
		$scope.checkLicenses = function() {
			let defer = $q.defer();
			$scope.sendRecv("IOT110", "checkLicenses", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.hideTable = "true";
								$scope.showMsg("ehl_01_iot110_004");
							} else {
								$scope.inputVO.EMP_NAME = tota[0].body.resultList[0].EMP_NAME;
								defer.resolve("success");
							}
						}
			})
			return defer.promise;
		};

		$scope.checkLicensesValid = function() {
			let defer = $q.defer();
			$scope.sendRecv("IOT110", "checkLicensesValid", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
								let validVo	=	tota[0].body.resultList[0];
								if (validVo.EXPIRED=='Y')	{
									$scope.showMsg("壽險證照已超過法定換證期限無法進行招攬");
									$scope.invalid = true;
								}
								if (validVo.UNREG=='Y')	{
									$scope.showMsg("壽險證照已註銷無法進行招攬");
									$scope.invalid = true;
								}
							}
							defer.resolve("success");
			});
			return defer.promise;
		};
		$scope.checkLicenses().then(function(){
			$scope.checkLicensesValid();
		});

		// 顯示Fatca
		$scope.queryCustData = function() {
			$scope.sendRecv("IOT110", "checkCustData", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.fatca != "X") {
								$scope.checkFatca = "否";
							} else {
								$scope.checkFatca = "是";
							}
						}
			});
		};

		//檢查是否小於7歲
		$scope.checkyear = function(){
			if(!$scope.branch_cust){
				if(getAge($scope.inputVO.birthday)<7){
					$scope.underSeverYear = true;
					$scope.checkseveryear = true;
					$scope.showMsg("ehl_01_iot120_003");
				}else{
					$scope.underSeverYear = false;
					$scope.checkseveryear = false;
					$scope.showMsg("ehl_01_iot120_004");
				}
			}
		}

		//檢查是否配置比例相加為100
		$scope.checkPercentage = function() {
			if($scope.inputVO.INVESTList.length > 0) {
				var sum = 0;
				for(var a = 0; a<$scope.inputVO.INVESTList.length; a++){
					if($scope.inputVO.INVESTList[a].ALLOCATION_RATIO != undefined
							&& $scope.inputVO.INVESTList[a].ALLOCATION_RATIO != null
							&& $scope.inputVO.INVESTList[a].ALLOCATION_RATIO != '') {
						if($scope.inputVO.INVESTList[a].ALLOCATION_RATIO != '') {
							if($scope.inputVO.INVESTList[a].ALLOCATION_RATIO < 5) {
								return false
								break;
							} else {
								sum += Number($scope.inputVO.INVESTList[a].ALLOCATION_RATIO);
							}
						}
					}
				}
				if(sum != 100) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}

		//壽險新契約儲存
		$scope.saveDataRegType1 = function(chkStatus) {
			var currentStatus = $scope.inputVO.STATUS;
			$scope.inputVO.STATUS = chkStatus;

			$scope.sendRecv("IOT110", "saveData", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
				function(tota, isError) {
					if(isError){
						$scope.inputVO.STATUS = currentStatus;
						return;
					} else {
						if(tota[0].body.savepass){
	    					$scope.savePass = true;
	    					$scope.caseIdDisabled = true;	//存檔後CASE_ID不可修改

	    					if($scope.inputVO.PRODUCT_TYPE != '1'){
	    						$scope.Investment=true;
	    					}
	    					//購買檢核或送出覆核時，投資屬性分數不同顯示訊息
//	    					if((chkStatus == "1" || chkStatus == "2") && tota[0].body.invScoreChk == "2") {
//	    						$scope.showMsg("近期送件，投資屬性問卷分數不同，請說明。");
//	    					}

	    					$scope.printList = tota[0].body.KYCList;
							$scope.KYCList = tota[0].body.KYCList;
							$scope.checkCost=tota[0].body.printAb;
							$scope.inputVO.PREMATCH_SEQ = tota[0].body.prematchSeq;
							$scope.inputVO.CALLOUT_YN = tota[0].body.CALLOUT_YN; //是否有已申請的電訪紀錄
//							$scope.inputVO.C_SENIOR_PVAL = tota[0].body.C_SENIOR_PVAL;
							//refresh edit/viewonly status
							$scope.checkStatus();

							var findPrd = $filter('filter')($scope.mappingSet['IOT.NO_CHK_LOAN_INSPRD'], {DATA: $scope.inputVO.INSPRD_ID});
							$scope.isInNoChkLoan = (findPrd != null && findPrd.length > 0) ? true : false;

							$scope.noUpdate = true;
	    					$scope.showMsg("ehl_01_common_001");
	    				}
					}
	   			});
		}

		//契約變更-基金標的異動儲存
		$scope.saveDataRegType2 = function(chkStatus) {
			var currentStatus = $scope.inputVO.STATUS;
			$scope.inputVO.STATUS = chkStatus;
			debugger
			$scope.sendRecv("IOT110", "saveData", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
				function(tota, isError) {
					if(isError){
						$scope.inputVO.STATUS = currentStatus;
						return;
					} else {
						if(tota[0].body.savepass){
							$scope.savePass = true;
							if($scope.inputVO.PRODUCT_TYPE != '1'){
								$scope.Investment=true;
							}
							$scope.printList = tota[0].body.KYCList;
							$scope.KYCList = tota[0].body.KYCList;
							$scope.inputVO.PREMATCH_SEQ = tota[0].body.prematchSeq;
//							$scope.inputVO.C_SENIOR_PVAL = tota[0].body.C_SENIOR_PVAL;
//							$scope.pass=true;
							$scope.noUpdate = true;
							$scope.showMsg("ehl_01_common_001");
						}
					}
			});
		}

		//適合度檢核
		$scope.checkFit = function(chkStatus) {
			//WMS-CR-20180305-01_新系統新增設控保險業務員資格超過法定換證期限未換證，不允許招攬保險之檢核
			if ($scope.invalid)
				return;

			switch ($scope.inputVO.REG_TYPE) {
			//壽險新契約
			case '1':
				debugger
				//欄位空值檢核
				if(chkStatus != '4' &&
					($scope.inputVO.CUST_ID == '' || $scope.inputVO.CUST_ID == null || $scope.inputVO.CUST_ID == undefined ||
					 $scope.inputVO.PROPOSER_NAME == '' || $scope.inputVO.PROPOSER_BIRTH == undefined ||
					 $scope.inputVO.INSURED_ID == '' || $scope.inputVO.INSURED_ID == null || $scope.inputVO.INSURED_ID == undefined || $scope.inputVO.INSURED_NAME == '' ||
					 $scope.inputVO.INSPRD_ID == '' || $scope.inputVO.PAY_TYPE == '' || $scope.inputVO.REAL_PREMIUM == undefined ||
					 ($scope.inputVO.PAY_TYPE != '1' && $scope.inputVO.MOP2 == '') || $scope.inputVO.INSPRD_KEYNO == undefined ||
					 $scope.inputVO.LOAN_SOURCE_YN == '' || $scope.inputVO.LOAN_SOURCE2_YN == '' ||
					 $scope.inputVO.PAYER_ID == '' || $scope.inputVO.PAYER_ID == null || $scope.inputVO.PAYER_ID == undefined ||
					 $scope.inputVO.PAYER_NAME == '' || $scope.inputVO.RLT_BT_PROPAY == '' ||
					 $scope.inputVO.RECRUIT_ID == null || $scope.inputVO.RECRUIT_ID == undefined || $scope.inputVO.RECRUIT_ID == '' ||
					 $scope.inputVO.PROPOSER_INCOME1 == null || $scope.inputVO.PROPOSER_INCOME1 == undefined ||
					 $scope.inputVO.INSURED_INCOME1 == null || $scope.inputVO.INSURED_INCOME1 == undefined ||
					 $scope.inputVO.CONTRACT_END_YN == '' || $scope.inputVO.S_INFITEM_LOAN_YN == '' ||
					 $scope.inputVO.PROPOSER_WORK == '' || $scope.inputVO.INSURED_WORK == '' ||
					 $scope.inputVO.INSURED_BIRTH == undefined || $scope.inputVO.PAYER_BIRTH == undefined)) {
					//必輸欄位
					$scope.showErrorMsgInDialog("ehl_01_common_022");
				} else {
					//檢核證照
					$scope.sendRecv("IOT120", "chkValid", "com.systex.jbranch.app.server.fps.iot120.IOT120InputVO", {"RECRUIT_ID":$scope.inputVO.RECRUIT_ID,
									 																				 "INSPRD_ID":$scope.inputVO.INSPRD_ID,
									 																				 "APPLY_DATE":$scope.inputVO.APPLY_DATE},
						function(tota, isError) {
							let validVo = tota[0].body.REFList[0];
							if (validVo.EXPIRED == 'Y')	{
								$scope.showMsg("壽險證照已超過法定換證期限無法進行招攬");
								return;
							}

							if (validVo.UNREG == 'Y')	{
								$scope.showMsg("壽險證照已註銷無法進行招攬");
								return;
							}

							// #0543: WMS-CR-20190905-01_新增理專保險新商品教育訓練檢核
							let tmsVO = tota[0].body.TMSList[0];
							if (tmsVO.STUDY_COMPLETED == 'N')	{
								$scope.showMsg("主約商品教育訓練尚未完成，無法通過檢核");
								return;
							}
							if (tmsVO.OVERDUE == 'Y' && $scope.inputVO.CANCEL_CONTRACT_YN != "Y")	{
								$scope.showMsg("要保書申請日不得小於商品訓練完訓日期");
								return;
							}
							debugger
							if(chkStatus == '4') { //主管退回不須檢核
								//壽險新契約儲存
								$scope.saveDataRegType1(chkStatus);
							} else {
								//適合度檢核
								if($scope.isFitValidate(chkStatus)) {
									//高齡檢核(1:適合度檢核或2:送出覆核)，契撤不須做高齡檢核
									if($scope.inputVO.isOver65 && (chkStatus == "1" || chkStatus == "2")) {
										if($scope.inputVO.CANCEL_CONTRACT_YN == "Y") {
											//契撤
											$scope.inputVO.SENIOR_AUTH_OPT = "3"; //契約撤銷件，無須驗證錄音	
											//壽險新契約儲存
											$scope.saveDataRegType1(chkStatus);
										} else {
											//非契撤才做高齡檢核
											$scope.sendRecv("IOT110", "validSeniorCustEvlRM", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
									        		function(tota, isError) {
									            		debugger
									    				if (!isError) {
									    					if(tota[0].body.invalidSeniorCustEvlRM == "Y") {
									    						//高齡評估作業異動日期"非"介於要保書申請日-3個工作天～要保書申請日區間
									    						$scope.showErrorMsgInDialog("高齡客戶應先完成本行高齡評估作業，確保客戶有辨識購買保險商品的能力");
									    						return;
									    					} else {
									    						debugger
									    						//請檢核招攬人員針對高齡客戶是否有維護行內高齡客戶資訊觀察表
										    					$scope.inputVO.SENIOR_AUTH_OPT2 = "1"; //有維護
									    						//請檢核客戶資訊觀察表高齡客戶能力表現問項為8.無上述情形且金融認知問項為4.無上述情形之選項
									    						//invalidSeniorCustEvlBossB: Y:invalid 金融認知結果<>4沒有上述情形
									    						//invalidSeniorCustEvlBossC: Y:invalid 能力表現填答<>8.無上述情形
									    						//invalidSeniorCustEvlBossD: Y:invalid 金融認知填答第一選項
									    						//invalidSeniorCustEvlBossE: Y:invalid 金融認知填答第二或第三個選項
									    						if(tota[0].body.invalidSeniorCustEvlBossB != "Y" && tota[0].body.invalidSeniorCustEvlBossC != "Y") {
									    							//金融認知結果=4沒有上述情形 且 能力表現填答=8.無上述情形
									    							$scope.inputVO.SENIOR_AUTH_OPT3 = "2"; //2:沒有上述情形
									    							$scope.inputVO.SENIOR_AUTH_REMARKS = "高齡客戶符合風險管理規範";
									    						} else if(tota[0].body.invalidSeniorCustEvlBossE == "Y") {
									    							//金融認知填答第二或第三個選項
									    							$scope.inputVO.SENIOR_AUTH_OPT3 = "1"; //1:有上述情形-主管協同關懷確認不可投保 
									    							$scope.inputVO.SENIOR_AUTH_REMARKS = "高齡客戶不符合風險管理規範";
									    						} else if(tota[0].body.invalidSeniorCustEvlBossC == "Y" || tota[0].body.invalidSeniorCustEvlBossD == "Y") {
									    							//能力表現填答1~7 或 金融認知填答第一選項
									    							$scope.inputVO.SENIOR_AUTH_OPT3 = "3"; //3:有上述情形-主管協同關懷確認可投保
									    						}
									    						
									    						//投資型商品，開啟基金適配作業頁面做確認再儲存
																if($scope.inputVO.PRODUCT_TYPE != '1' && $scope.inputVO.PREMATCH_SEQ == '') {
																		//先取得高齡P值
																		$scope.sendRecv("IOT110", "getSeniorPVal", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
																			function(tota, isError) {
																				if(!isError){
																					$scope.inputVO.C_SENIOR_PVAL = tota[0].body.C_SENIOR_PVAL;
																					$scope.openFundLinkDialog('1', chkStatus);
																				}
																		});	
																} else {
																	//投資型商品檢查是否配置比例相加為100
																	if($scope.inputVO.PRODUCT_TYPE != '1' && !$scope.checkPercentage()) {
																		$scope.showErrorMsg("基金適配配置比例合計必須等於100");
																		return;
																	}
																	//壽險新契約儲存
																	$scope.saveDataRegType1(chkStatus);
																}
									    					}
									    				}
									    	   	});
										}
									} else {
										//非高齡
										//投資型商品，開啟基金適配作業頁面做確認再儲存
										if($scope.inputVO.PRODUCT_TYPE != '1' && $scope.inputVO.PREMATCH_SEQ == '') {
											//先取得高齡P值
											$scope.sendRecv("IOT110", "getSeniorPVal", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
												function(tota, isError) {
													if(!isError){
														$scope.inputVO.C_SENIOR_PVAL = tota[0].body.C_SENIOR_PVAL;
														$scope.openFundLinkDialog('1', chkStatus);
													}
											});	
										} else {
											//投資型商品檢查是否配置比例相加為100
											if($scope.inputVO.PRODUCT_TYPE != '1' && !$scope.checkPercentage()) {
												$scope.showErrorMsg("基金適配配置比例合計必須等於100");
												return;
											}
											//壽險新契約儲存
											$scope.saveDataRegType1(chkStatus);
										}
									}
								}
							}
					});
				}
				break;
			//契約變更-基金標的異動
			case '2':
				//欄位空值檢核
				if($scope.inputVO.CUST_ID == '' || $scope.inputVO.PROPOSER_NAME == '' || $scope.inputVO.INSPRD_KEYNO == undefined ||
					$scope.inputVO.PROPOSER_BIRTH == undefined || $scope.inputVO.INSPRD_ID == '' || $scope.inputVO.COMPANY_NUM == ''){
					$scope.showErrorMsgInDialog("ehl_01_common_022");
				}else{
					if($scope.isFitValidate2(chkStatus)) {
						//投資型商品，開啟基金適配作業頁面做確認再儲存
						if($scope.inputVO.PRODUCT_TYPE != '1' && $scope.inputVO.PREMATCH_SEQ == '') {
							//高齡須檢核投資型商品P值
							var today = new Date();
							var systoday = new Date(today.getFullYear(), today.getMonth(), today.getDate());//取當日日期
							var birthDate = new Date($scope.inputVO.PROPOSER_BIRTH);//要保人生日
				            var chkDate65C = new Date(birthDate.getFullYear() + 64, birthDate.getMonth() + 6, birthDate.getDate());
				            $scope.inputVO.C_SALE_SENIOR_YN = (systoday >= chkDate65C ? "Y" : "N"); //要保人是否64.5歲以上
							//先取得高齡P值
							$scope.sendRecv("IOT110", "getSeniorPVal", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO, 
								function(tota, isError) {
									if(!isError) {
										$scope.inputVO.C_SENIOR_PVAL = tota[0].body.C_SENIOR_PVAL;
										$scope.openFundLinkDialog('2', chkStatus);
									}
							});	
						} else {
							//投資型商品檢查是否配置比例相加為100
							if($scope.inputVO.PRODUCT_TYPE != '1' && !$scope.checkPercentage()) {
								$scope.showErrorMsg("基金適配配置比例合計必須等於100");
								return;
							}

							//契約變更-基金標的異動儲存
							$scope.saveDataRegType2(chkStatus);
						}
					}
				}
				break;
			//契約變更-要保人變更
			case '3':
				debugger
				//欄位空值檢核
				if($scope.inputVO.POLICY_NO1 == '' || $scope.inputVO.CHG_CUST_ID == '' || $scope.inputVO.CHG_PROPOSER_NAME == '' ||
						$scope.inputVO.CHG_PROPOSER_BIRTH == undefined || $scope.inputVO.COMPANY_NUM == ''){
					$scope.showErrorMsgInDialog("ehl_01_common_022");
				}else{
					var currentStatus = $scope.inputVO.STATUS;
					//AML風險等級、Pre-check 結果都不為高風險且其中一個不是空白，就不須覆核
					if(chkStatus == "1") {
						if(!$scope.branch_cust || $scope.inputVO.AML == "高" || $scope.inputVO.PRECHECK == "高" || (($scope.inputVO.AML == "" || $scope.inputVO.AML == null) && $scope.inputVO.PRECHECK == "NONE")) {
							//若變更後要保人未開戶、AML風險等級、Pre-check 結果任一為高風險或客戶未開戶，則需點選送出覆核，由主管進行覆核後，才完成作業
							$scope.inputVO.STATUS = chkStatus;
						} else {
							//不須覆核，直接將狀態壓為"核可"
							$scope.inputVO.STATUS = "3";
							//直接核可但還是要列印購買檢核表
							$scope.chgCustPrintCheck = true;
						}
					} else {
						//若AML風險等級、Pre-check 結果任一為高風險或兩個均為空白(客戶未開戶)，則需點選送出覆核，由主管進行覆核後，才完成作業
						$scope.inputVO.STATUS = chkStatus;
					}

					$scope.sendRecv("IOT110", "saveData", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
    					function(tota, isError) {
							if(isError){
	    						$scope.inputVO.STATUS = currentStatus;
	    						return;
	    					} else {
	    						if(tota[0].body.savepass) {
	    							$scope.savePass = true;
	    							$scope.inputVO.PREMATCH_SEQ = tota[0].body.prematchSeq;
	    							//refresh edit/viewonly status
									$scope.checkStatus();

									$scope.noUpdate = true;
	    							$scope.showMsg("ehl_01_common_001");
	    						}
							}
					});
				}
				break;
			default:
				$scope.showErrorMsgInDialog("ehl_01_common_022");
				break;
			}
		};

		//壽險新契約檢核
		$scope.isFitValidate = function(chkStatus) {
			//高齡覆核時...非契撤才檢核
			if(chkStatus == '3' && $scope.inputVO.CANCEL_CONTRACT_YN != "Y" && 
					($scope.inputVO.C_SALE_SENIOR_YN == "Y" || $scope.inputVO.I_SALE_SENIOR_YN == "Y" || $scope.inputVO.P_SALE_SENIOR_YN == "Y")) {
				if(!$scope.isInNoChkLoan && ($scope.inputVO.SENIOR_AUTH_OPT == undefined || $scope.inputVO.SENIOR_AUTH_OPT == null || $scope.inputVO.SENIOR_AUTH_OPT == '')) {
					$scope.showErrorMsgInDialog("高齡銷售過程錄音覆核選項為必輸");
					return false;
				}

				if($scope.inputVO.SENIOR_AUTH_OPT4 == undefined || $scope.inputVO.SENIOR_AUTH_OPT4 == null || $scope.inputVO.SENIOR_AUTH_OPT4 == '') {
					$scope.showErrorMsgInDialog("高齡客戶投保評估量表覆核選項為必輸");
					return false;
				}

				if($scope.inputVO.SENIOR_AUTH_OPT == '2') {
					$scope.showErrorMsgInDialog("高齡銷售過程錄音驗證有誤，無法核可");
			    	return false;
				}
				
				if($scope.inputVO.SENIOR_AUTH_OPT2 == '2') {
					$scope.showErrorMsgInDialog("高齡客戶應先完成本行高齡評估作業，確保客戶有辨識購買保險商品的能力");
			    	return false;
				}

				//1:有上述情形：金融認知2或3
				if($scope.inputVO.SENIOR_AUTH_OPT3 == '1') {
					if($scope.inputVO.CANCEL_CONTRACT_YN == "Y" && $scope.inputVO.SENIOR_AUTH_ID == "") {
						//契撤案件須輸入關懷主管才可通過檢核
						$scope.showErrorMsgInDialog("請輸入高齡關懷主管員編");
				    	return false;
					}
					if($scope.inputVO.CANCEL_CONTRACT_YN != "Y") {
						//非契撤案件不可通過檢核
						$scope.showErrorMsgInDialog("高齡客戶評估結果不符合可申購條件");
				    	return false;
					}
				} else if($scope.inputVO.SENIOR_AUTH_OPT3 == '2') {
					//2:沒有上述情形
					//高齡客戶關懷主管員編帶入登入主管員編
					$scope.inputVO.SENIOR_AUTH_ID = $scope.loginID; 
				} else if($scope.inputVO.SENIOR_AUTH_OPT3 == '3') {
					//3:有上述情形：能力表現1-7 或金融認知1
					if($scope.inputVO.SENIOR_AUTH_REMARKS == undefined || $scope.inputVO.SENIOR_AUTH_REMARKS == null || $scope.inputVO.SENIOR_AUTH_REMARKS == "" 
							|| $scope.inputVO.SENIOR_AUTH_ID == undefined || $scope.inputVO.SENIOR_AUTH_ID == null || $scope.inputVO.SENIOR_AUTH_ID == "") {
						$scope.showErrorMsgInDialog('請輸入高齡客戶投保關懷說明以及高齡關懷主管員編');
			    		return false;
					} else if($scope.inputVO.SENIOR_AUTH_REMARKS.length < 10) {
						$scope.showErrorMsgInDialog('高齡客戶投保關懷說明至少要輸入10個字以上');
			    		return false;
					}
				}				
				
				if($scope.inputVO.SENIOR_AUTH_OPT4 == '2') {
					$scope.showErrorMsgInDialog("客戶欠缺辨識有不利其投保權益情形請確認是否適合購買該保險商品");
			    	return false;
				}
			}

			//KYC: 投資型險種或外幣商品或非投資型險種但需視配商品，須檢查KYC
			if(($scope.inputVO.PRODUCT_TYPE != "1" || $scope.inputVO.CURR_CD != "TWD" || ($scope.inputVO.PRODUCT_TYPE == "1" && $scope.inputVO.NEED_MATCH == "Y")) &&
					($scope.inputVO.CUST_RISK == undefined || $scope.inputVO.CUST_RISK == null || $scope.inputVO.CUST_RISK == '')) {
				$scope.showErrorMsgInDialog("要保人KYC日期為空白");
		    	return false;
			}
			debugger
			$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
		    if (($scope.inputVO.PRODUCT_TYPE != "1" || $scope.inputVO.CURR_CD != "TWD" || ($scope.inputVO.PRODUCT_TYPE == "1" && $scope.inputVO.NEED_MATCH == "Y")) &&
		    		($scope.inputVO.CUST_RISK_DUE < $scope.toJsDate($scope.toDay))) { //KYC過期
		    	$scope.showErrorMsgInDialog("要保人KYC日期已過期");
		    	return false;
		    }

		    //投資型商品適配日(要保人保險購買檢核的鍵機日)須等於要保書申請日
		    if($scope.inputVO.PRODUCT_TYPE != "1" || $scope.inputVO.NEED_MATCH == "Y") {
		    	var applydate = $filter('date')($scope.inputVO.APPLY_DATE,'yyyy-MM-dd');
		    	var matchdate = $filter('date')($scope.inputVO.MATCH_DATE,'yyyy-MM-dd');
		    	if(applydate != matchdate) {
		    		$scope.showMsg("適合度檢核日與要保書填寫日不符");
		    	}
		    }

		    if($scope.inputVO.OTH_TYPE == '1' && $scope.inputVO.CASE_ID.substring(0, 1) != 'J') {
		    	$scope.showErrorMsgInDialog('請輸入"J"開頭之10碼同意書編號或WEB上傳號碼');
		    	return false;
		    }

		    //若保費來源拉選不為貸款或解約，但定存打折檢核、保單貸款檢核、行內貸款檢核、行內保單解約檢核、貸款申請日檢核任一檢核為Y，理專須說明客戶保費來源
		    //若檢核有額度式貸款，客戶保費來源說明一定要填寫
		    if((($scope.inputVO.LOAN_SOURCE_YN != 'Y' || $scope.inputVO.LOAN_SOURCE2_YN != 'Y') &&
		    		($scope.inputVO.C_CD_CHK_YN == 'Y' || $scope.inputVO.C_LOAN_CHK1_YN == 'Y' || $scope.inputVO.C_LOAN_CHK2_YN == 'Y' || $scope.inputVO.C_LOAN_CHK3_YN == 'Y' ||
		    		 $scope.inputVO.I_CD_CHK_YN == 'Y' || $scope.inputVO.I_LOAN_CHK1_YN == 'Y' || $scope.inputVO.I_LOAN_CHK2_YN == 'Y' || $scope.inputVO.I_LOAN_CHK3_YN == 'Y' ||
		    		 $scope.inputVO.CD_CHK_YN == 'Y' || $scope.inputVO.LOAN_CHK1_YN == 'Y' || $scope.inputVO.LOAN_CHK2_YN == 'Y' || $scope.inputVO.LOAN_CHK3_YN == 'Y' ||
		    		 $scope.inputVO.C_LOAN_APPLY_YN == 'Y' || $scope.inputVO.I_LOAN_APPLY_YN == 'Y' || $scope.inputVO.P_LOAN_APPLY_YN == 'Y')) ||
		    	$scope.inputVO.LOAN_CHK4_YN == 'Y' || $scope.inputVO.C_LOAN_CHK4_YN == 'Y' || $scope.inputVO.I_LOAN_CHK4_YN == 'Y') {
		    	if($scope.inputVO.LOAN_SOURCE_REMARK == null || $scope.inputVO.LOAN_SOURCE_REMARK == undefined || $scope.inputVO.LOAN_SOURCE_REMARK == '') {
		    		$scope.showErrorMsgInDialog('保費來源說明不能為空白');
		    		return false;
		    	} else if($scope.inputVO.LOAN_SOURCE_REMARK.length < 5) {
		    		$scope.showErrorMsgInDialog('保費來源說明至少要輸入4個字以上');
		    		return false;
		    	}
		    }

		    //保費來源拉選為貸款且為投資型商品
		    if($scope.inputVO.LOAN_SOURCE_YN == "Y" && $scope.inputVO.PRODUCT_TYPE != "1") {
		    	var findKyc = $filter('filter')($scope.mappingSet['IOT.INV_PRD_LOAN_KYC'], {LABEL: $scope.inputVO.CUST_RISK});
		    	if(findKyc == null || findKyc.length == 0) {
		    		$scope.showErrorMsgInDialog('保費來源為貸款，購買投資型商品KYC 風險等級應為' + $scope.mappingSet['IOT.INV_PRD_LOAN_KYC'][0].LABEL);
			    	return false;
		    	}
		    }

		    //因應回覆金檢修改事項，要保人/被保人若要保書申請日一年內有貸款撥款，只需於畫面及報表上提示訊息，不須卡控業報書收入需等於徵審收入需一致
		    if(($scope.inputVO.C_LOAN_CHK2_DATE != undefined && $scope.inputVO.C_LOAN_CHK2_DATE != null && $scope.inputVO.C_LOAN_CHK2_DATE != '') ||
		       ($scope.inputVO.I_LOAN_CHK2_DATE != undefined && $scope.inputVO.I_LOAN_CHK2_DATE != null && $scope.inputVO.I_LOAN_CHK2_DATE != '')) {
		    	$scope.showMsg("客戶近一年有貸款撥貸，請檢視授信收入與業報書收入(工作收入+其他收入)是否一致，不一致必須於業報書補充說明欄詳細說明原因。");
		    }

			if(($scope.inputVO.LOAN_CHK3_YN == 'Y' || $scope.inputVO.C_LOAN_CHK3_YN == 'Y' || $scope.inputVO.I_LOAN_CHK3_YN == 'Y') && $scope.inputVO.CONTRACT_END_YN == '2') {
				$scope.showErrorMsgInDialog('客戶最近3個月內有辦理解約，業報書投保前三個月內是否有辦理契約終止(解約)欄位應勾選是');
				return false;
			}

			if(($scope.inputVO.LOAN_CHK1_YN == 'Y' || $scope.inputVO.C_LOAN_CHK1_YN == 'Y' || $scope.inputVO.I_LOAN_CHK1_YN == 'Y' ||
				$scope.inputVO.LOAN_CHK2_YN == 'Y' || $scope.inputVO.C_LOAN_CHK2_YN == 'Y' || $scope.inputVO.I_LOAN_CHK2_YN == 'Y' ||
				$scope.inputVO.C_LOAN_APPLY_YN == 'Y' || $scope.inputVO.I_LOAN_APPLY_YN == 'Y' || $scope.inputVO.P_LOAN_APPLY_YN == 'Y') && $scope.inputVO.S_INFITEM_LOAN_YN == '2') {
				$scope.showErrorMsgInDialog('客戶最近3個月內有辦理保單借款或貸款，業報書投保前三個月內是否有辦理貸款或保險單借款欄位應勾選是');
				return false;
			}

			//保單貸款檢核、行內貸款檢核、行內保單解約檢核註記為Y者，彈跳訊息
			if($scope.inputVO.LOAN_CHK1_YN == 'Y' || $scope.inputVO.LOAN_CHK2_YN == 'Y' || $scope.inputVO.LOAN_CHK3_YN == 'Y'
					|| $scope.inputVO.C_LOAN_CHK1_YN == 'Y' || $scope.inputVO.C_LOAN_CHK2_YN == 'Y' || $scope.inputVO.C_LOAN_CHK3_YN == 'Y'
					|| $scope.inputVO.I_LOAN_CHK1_YN == 'Y' || $scope.inputVO.I_LOAN_CHK2_YN == 'Y' || $scope.inputVO.I_LOAN_CHK3_YN == 'Y') {
				$confirm({text: "客戶最近3個月有貸款或解約，業報書第6項與第7項需勾選為是。"}, {size: 'sm'});
			}

			//要保人AML為空白或高風險
			if($scope.inputVO.AML == "高" || $scope.inputVO.PRECHECK == "高" || (($scope.inputVO.AML == "" || $scope.inputVO.AML == null) && $scope.inputVO.PRECHECK == "NONE")) {
				$scope.showMsg("要保人AML 風險值須個金分行主管(BM)才能進行覆核。");
			}
		    debugger
		    $scope.validSeniorRec = false;	//高齡非常態錄音檢核
			var applyDate = new Date($scope.inputVO.APPLY_DATE);	//要保書申請日
            var birthDate = new Date($scope.inputVO.PROPOSER_BIRTH);//要保人生日
            var birthDateI = new Date($scope.inputVO.INSURED_BIRTH);//被保人生日
            var birthDateP = new Date($scope.inputVO.PAYER_BIRTH);//繳款人生日
            //要保人, 被保人, 繳款人64.5歲以上
            var chkDate65C = new Date(birthDate.getFullYear() + 64, birthDate.getMonth() + 6, birthDate.getDate());
            var chkDate65I = new Date(birthDateI.getFullYear() + 64, birthDateI.getMonth() + 6, birthDateI.getDate());
            var chkDate65P = new Date(birthDateP.getFullYear() + 64, birthDateP.getMonth() + 6, birthDateP.getDate());
            //要保人
            var chkDate18 = new Date(birthDate.getFullYear() + 18, birthDate.getMonth(), birthDate.getDate());
            var chkDate64 = new Date(birthDate.getFullYear() + 64, birthDate.getMonth() + 6, birthDate.getDate());
            var chkDate69 = new Date(birthDate.getFullYear() + 69, birthDate.getMonth() + 6, birthDate.getDate());
            //要保人70歲以上
            var chkDate70C = new Date(birthDate.getFullYear() + 70, birthDate.getMonth(), birthDate.getDate());
            
            var exRate = ($scope.inputVO.AB_EXCH_RATE == null || $scope.inputVO.AB_EXCH_RATE == undefined) ? 1 : $scope.inputVO.AB_EXCH_RATE;//非常態交易匯率
            $scope.inputVO.AB_SENIOR_YN = 'N';//高齡非常態錄音檢核
            //取消高齡非常態錄音檢核
            //要保人年齡>=64.5歲(要保人生日-要保書申請日)且購買投資型商品，躉繳保費低於100萬、分期繳保費低於30萬，則需產出非常態錄音照會單
//            if(applyDate > chkDate64 && $scope.inputVO.PRODUCT_TYPE != "1") {
//            	if(($scope.inputVO.PAY_TYPE == '1' && ($scope.inputVO.REAL_PREMIUM*exRate) < 1000000) || ($scope.inputVO.PAY_TYPE != '1' && ($scope.inputVO.REAL_PREMIUM*exRate) < 300000)) {
//            		$scope.validSeniorRec = true;
//            		$scope.inputVO.AB_SENIOR_YN = 'Y';//高齡非常態錄音檢核
//            	}
//            }

            //檢查要保人或被保人或繳款人是否65歲以上
            $scope.inputVO.isOver65 = false;
            if($scope.inputVO.REG_TYPE != "1" || $scope.inputVO.OTH_TYPE != "1") { //非電子要保書
            	$scope.inputVO.C_SALE_SENIOR_YN = "N";//要保人高齡銷售過程錄音檢核
            }
            $scope.inputVO.I_SALE_SENIOR_YN = "N";//被保人高齡銷售過程錄音檢核
            $scope.inputVO.P_SALE_SENIOR_YN = "N";//繳款人高齡銷售過程錄音檢核

            if(applyDate >= chkDate65C || applyDate >= chkDate65I || applyDate >= chkDate65P) {
            	if(applyDate >= chkDate65C) {
            		$scope.inputVO.C_SALE_SENIOR_YN = "Y";
            	} else { //若電子要保書為回"Y"，實際還是以業管計算為主
            		$scope.inputVO.C_SALE_SENIOR_YN = "N";
            	}
            	if(applyDate >= chkDate65I) $scope.inputVO.I_SALE_SENIOR_YN = "Y";
            	if(applyDate >= chkDate65P) $scope.inputVO.P_SALE_SENIOR_YN = "Y";
            }

            if($scope.inputVO.C_SALE_SENIOR_YN == "Y" || $scope.inputVO.I_SALE_SENIOR_YN == "Y" || $scope.inputVO.P_SALE_SENIOR_YN == "Y") {
            	$scope.inputVO.isOver65 = true;
            }
            //要保人>=70歲
            $scope.inputVO.CUST_OVER_70 = (applyDate >= chkDate70C ? "Y" : "N");
//            //要保人>=64.5歲&<70歲
//            $scope.inputVO.CUST_65_TO_70 = (applyDate >= chkDate65C && applyDate < chkDate70C ? "Y" : "N");
            
            //要保人或被保人或繳款人只要其中一人為高齡，且商品代號不在不須檢核貸款險種參數檔中，且不為契撤案件，須執行高齡銷售過程錄音
            if($scope.inputVO.isOver65 && !$scope.isInNoChkLoan && $scope.inputVO.CANCEL_CONTRACT_YN != "Y") {
            	//送出覆核時若為高齡需檢核是否填寫高齡錄音序號
        		if(chkStatus == '2') {
        			//只要其中一人為高齡，要保人一定要填寫高齡錄音序號
        			if($scope.inputVO.C_SALE_SENIOR_TRANSSEQ == null
        					|| $scope.inputVO.C_SALE_SENIOR_TRANSSEQ == undefined
        					|| $scope.inputVO.C_SALE_SENIOR_TRANSSEQ == "") {
	        			$scope.showErrorMsgInDialog('要保人或被保人或繳款人保險年齡達65歲，請填寫要保人高齡銷售過程錄音序號(6～8碼)才能送出覆核');
	        			return false;
        			} else if($scope.inputVO.C_SALE_SENIOR_TRANSSEQ.length < 6 || $scope.inputVO.C_SALE_SENIOR_TRANSSEQ.length > 8) {
        				$scope.showErrorMsgInDialog('要保人或被保人或繳款人保險年齡達65歲，請填寫要保人高齡銷售過程錄音序號(6～8碼)才能送出覆核');
	        			return false;
        			}

        			//若被保人為高齡
        			if($scope.inputVO.I_SALE_SENIOR_YN == "Y") {
        				//若被保人不為要保人
        				if($scope.inputVO.CUST_ID != $scope.inputVO.INSURED_ID) {
        					//錄音序號不可為空
        					if($scope.inputVO.I_SALE_SENIOR_TRANSSEQ == "" || $scope.inputVO.I_SALE_SENIOR_TRANSSEQ == null || $scope.inputVO.I_SALE_SENIOR_TRANSSEQ == undefined) {
        						$scope.showErrorMsgInDialog('被保人保險年齡達65歲，請填寫被保人高齡銷售過程錄音序號(6～8碼)才能送出覆核');
        	        			return false;
        					} else if($scope.inputVO.I_SALE_SENIOR_TRANSSEQ.length < 6	|| $scope.inputVO.I_SALE_SENIOR_TRANSSEQ.length > 8) {
        						$scope.showErrorMsgInDialog('被保人保險年齡達65歲，請填寫被保人高齡銷售過程錄音序號(6～8碼)才能送出覆核');
        	        			return false;
        					}
        				//若被保人為要保人且錄音序號不為空，就需檢核是否為6-8碼
        				} else if(!($scope.inputVO.I_SALE_SENIOR_TRANSSEQ == "" || $scope.inputVO.I_SALE_SENIOR_TRANSSEQ == null || $scope.inputVO.I_SALE_SENIOR_TRANSSEQ == undefined)) {
        					if($scope.inputVO.I_SALE_SENIOR_TRANSSEQ.length < 6	|| $scope.inputVO.I_SALE_SENIOR_TRANSSEQ.length > 8) {
        						$scope.showErrorMsgInDialog('被保人保險年齡達65歲，請填寫被保人高齡銷售過程錄音序號(6～8碼)才能送出覆核');
        	        			return false;
        					}
        				}
        			}

        			//若繳款人為高齡
        			if($scope.inputVO.P_SALE_SENIOR_YN == "Y") {
        				//若繳款人不為要保人
        				if($scope.inputVO.CUST_ID != $scope.inputVO.PAYER_ID) {
        					if($scope.inputVO.P_SALE_SENIOR_TRANSSEQ == "" || $scope.inputVO.P_SALE_SENIOR_TRANSSEQ == null || $scope.inputVO.P_SALE_SENIOR_TRANSSEQ == undefined) {
        						$scope.showErrorMsgInDialog('繳款人保險年齡達65歲，請填寫繳款人高齡銷售過程錄音序號(6～8碼)才能送出覆核');
        						return false;
        					} else if($scope.inputVO.P_SALE_SENIOR_TRANSSEQ.length < 6 || $scope.inputVO.P_SALE_SENIOR_TRANSSEQ.length > 8) {
        						$scope.showErrorMsgInDialog('繳款人保險年齡達65歲，請填寫繳款人高齡銷售過程錄音序號(6～8碼)才能送出覆核');
        						return false;
        					}
        				//若繳款人為要保人且錄音序號不為空，就需檢核是否為6-8碼
        				} else if(!($scope.inputVO.P_SALE_SENIOR_TRANSSEQ == "" || $scope.inputVO.P_SALE_SENIOR_TRANSSEQ == null || $scope.inputVO.P_SALE_SENIOR_TRANSSEQ == undefined)) {
        					if($scope.inputVO.P_SALE_SENIOR_TRANSSEQ.length < 6 || $scope.inputVO.P_SALE_SENIOR_TRANSSEQ.length > 8) {
        						$scope.showErrorMsgInDialog('繳款人保險年齡達65歲，請填寫繳款人高齡銷售過程錄音序號(6～8碼)才能送出覆核');
        						return false;
        					}
        				}
        			}
        		} else {
        			$scope.showMsg("要保人及達65歲之被保人或繳款人需進行高齡銷售過程錄音。");
        		}
            }

            //若要保人或被保人或繳款人保險年齡>=65歲，於點選『適合度檢核』時，提示警語
            if($scope.inputVO.isOver65 && chkStatus == '1' && $scope.inputVO.CANCEL_CONTRACT_YN != "Y") {
            	$scope.showMsg("高齡客戶應檢附行內『高齡客戶資訊觀察表』，並請留意業務人員報告書附表_高齡客戶投保評估量表是否具有合理性。");
            }

            //若要保人年齡<18歲(原來是20歲，參數名稱不改)，顯示訊息
            $scope.inputVO.AGE_UNDER20_YN = 'N';
            if(applyDate < chkDate18) {
            	$scope.inputVO.AGE_UNDER20_YN = 'Y';//要保人年齡<18歲
            	$scope.showMsg("要保人未成年請注意投資型屬性問卷須以要保人角度填寫，投保目的勾選必須具合理性。");
				
				if($scope.isStringEmpty($scope.inputVO.REPRESET_ID) || $scope.isStringEmpty($scope.inputVO.REPRESET_NAME) || $scope.isStringEmpty($scope.inputVO.RLT_BT_PROREP)) {
            		$scope.showErrorMsgInDialog('要保人<18歲，請填寫法定代理人相關欄位');
					return false;
            	}
            }

            //若購買投資型商品，檢核時投資屬性問卷分數欄位為必填
//            if($scope.inputVO.PRODUCT_TYPE != "1" && (chkStatus == '1' || chkStatus == '2') &&
//            		($scope.inputVO.INV_SCORE == null || $scope.inputVO.INV_SCORE == undefined || $scope.inputVO.INV_SCORE == '')) {
//            	$scope.showErrorMsgInDialog('投資型商品，「投資屬性問卷分數」為必填欄位');
//            	return false;
//            }

            //是否有產生"保費來源確認及高齡客戶關懷錄音電訪單"
            $scope.inputVO.printSourceRecListYN = "N";
            if(($scope.inputVO.LOAN_SOURCE_YN == 'Y' || $scope.inputVO.LOAN_SOURCE2_YN == 'Y'
					|| $scope.inputVO.LOAN_CHK1_YN == 'Y' || $scope.inputVO.LOAN_CHK2_YN == 'Y' || $scope.inputVO.LOAN_CHK3_YN == 'Y' || $scope.inputVO.CD_CHK_YN == 'Y'
					|| $scope.inputVO.C_LOAN_CHK1_YN == 'Y' || $scope.inputVO.C_LOAN_CHK2_YN == 'Y' || $scope.inputVO.C_LOAN_CHK3_YN == 'Y' || $scope.inputVO.C_CD_CHK_YN == 'Y'
					|| $scope.inputVO.I_LOAN_CHK1_YN == 'Y' || $scope.inputVO.I_LOAN_CHK2_YN == 'Y' || $scope.inputVO.I_LOAN_CHK3_YN == 'Y' || $scope.inputVO.I_CD_CHK_YN == 'Y'
					|| $scope.inputVO.C_LOAN_APPLY_YN == 'Y' || $scope.inputVO.I_LOAN_APPLY_YN == 'Y' || $scope.inputVO.P_LOAN_APPLY_YN == 'Y'
					|| $scope.inputVO.CONTRACT_END_YN == '1' || $scope.inputVO.S_INFITEM_LOAN_YN == '1' || $scope.inputVO.isOver65) && !$scope.isInNoChkLoan) {
            	$scope.inputVO.printSourceRecListYN = "Y";
            }
            
            //預約電訪判斷是否需要錄音
            //是否需要輸入保費來源錄音序號
            var findPrd = $filter('filter')($scope.mappingSet['IOT.NO_CHK_LOAN_INSPRD'], {DATA: $scope.inputVO.INSPRD_ID});//不需要錄音的險種參數表
    		var isInNoChkLoan = (findPrd != null && findPrd.length > 0) ? true : false;
    		
    		$scope.inputVO.C_PREMIUM_TRANSSEQ_YN = "N";
    		$scope.inputVO.I_PREMIUM_TRANSSEQ_YN = "N";
    		$scope.inputVO.P_PREMIUM_TRANSSEQ_YN = "N";
    		$scope.inputVO.NEED_PREMIUM_TRANSSEQ_YN = "N";
    		//要保人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
    		if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && (($scope.inputVO.LOAN_SOURCE_YN == 'Y' && !isInNoChkLoan)
    				|| ($scope.inputVO.LOAN_SOURCE2_YN == 'Y' && !isInNoChkLoan)
    				|| (($scope.inputVO.LOAN_CHK1_YN == 'Y' || $scope.inputVO.LOAN_CHK2_YN == 'Y' || $scope.inputVO.LOAN_CHK3_YN == 'Y' || $scope.inputVO.CD_CHK_YN == 'Y'
    					|| $scope.inputVO.C_LOAN_CHK1_YN == 'Y' || $scope.inputVO.C_LOAN_CHK2_YN == 'Y' || $scope.inputVO.C_LOAN_CHK3_YN == 'Y' || $scope.inputVO.C_CD_CHK_YN == 'Y'
    					|| $scope.inputVO.I_LOAN_CHK1_YN == 'Y' || $scope.inputVO.I_LOAN_CHK2_YN == 'Y' || $scope.inputVO.I_LOAN_CHK3_YN == 'Y' || $scope.inputVO.I_CD_CHK_YN == 'Y'
    					|| $scope.inputVO.C_LOAN_APPLY_YN == 'Y' || $scope.inputVO.I_LOAN_APPLY_YN == 'Y' || $scope.inputVO.P_LOAN_APPLY_YN == 'Y'
    					|| $scope.inputVO.CONTRACT_END_YN == '1' || $scope.inputVO.S_INFITEM_LOAN_YN == '1'
    					|| $scope.inputVO.C_SALE_SENIOR_YN == "Y") && !isInNoChkLoan))) {
    			//需要輸入要保人保費來源錄音序號
    			$scope.inputVO.C_PREMIUM_TRANSSEQ_YN = "Y";
    		}
    		//被保人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
    		if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && ((($scope.inputVO.I_LOAN_CHK1_YN == 'Y' || $scope.inputVO.I_LOAN_CHK2_YN == 'Y' || $scope.inputVO.I_LOAN_CHK3_YN == 'Y' || $scope.inputVO.I_CD_CHK_YN == 'Y'
    				|| $scope.inputVO.I_LOAN_APPLY_YN == 'Y' || $scope.inputVO.I_SALE_SENIOR_YN == "Y") && !isInNoChkLoan))) {
    			//需要輸入被保人保費來源錄音序號
    			$scope.inputVO.I_PREMIUM_TRANSSEQ_YN = "Y";
    		}
    		//繳款人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
    		if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && ((($scope.inputVO.LOAN_CHK1_YN == 'Y' || $scope.inputVO.LOAN_CHK2_YN == 'Y' || $scope.inputVO.LOAN_CHK3_YN == 'Y' || $scope.inputVO.CD_CHK_YN == 'Y'
    				|| $scope.inputVO.P_LOAN_APPLY_YN == 'Y' || $scope.inputVO.P_SALE_SENIOR_YN == "Y") && !isInNoChkLoan))) {
    			//需要輸入繳款人保費來源錄音序號
    			$scope.inputVO.P_PREMIUM_TRANSSEQ_YN = "Y";
    		}
    		//要被繳有任一需要高齡/保費來源錄音序號
    		if($scope.inputVO.C_PREMIUM_TRANSSEQ_YN == "Y" || $scope.inputVO.I_PREMIUM_TRANSSEQ_YN == "Y" || $scope.inputVO.P_PREMIUM_TRANSSEQ_YN == "Y") {
    			$scope.inputVO.NEED_PREMIUM_TRANSSEQ_YN = "Y";
    		}
    		
    		//非契撤，分紅商品，招攬人員是否有分紅證照
			var applydate = $filter('date')($scope.inputVO.APPLY_DATE,'yyyy-MM-dd');
    		if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && $scope.inputVO.PROD_DIVIDEND_YN && $scope.inputVO.PROD_DIVIDEND_YN == "Y") {
    			if($scope.inputVO.empDividendCertYN == "N") {
	    			$scope.showErrorMsgInDialog("輸入的招攬人員必須具有(銀行)人身保險業分紅保險商品教育訓練完訓資格");
					return false;
    			}
    			if($scope.toJsDate($scope.inputVO.empDividendEndDate) > $scope.toJsDate(applydate)) {
    				$scope.showErrorMsgInDialog("要保書申請日不得小於分紅完訓日期");
					return false;
    			}
    		}
    		
    		//非契撤，高齡完訓資格檢核
    		if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && $scope.inputVO.isOver65) {
    			var findEmp = $filter('filter')($scope.mappingSet['IOT.NO_SENIOR_CERTIFICATE_EMP'], {LABEL: $scope.inputVO.RECRUIT_ID}); //未完成高齡完訓資格員編
        		var isInNoCertEmp = (findEmp != null && findEmp.length > 0) ? true : false;
        		if(isInNoCertEmp || $scope.inputVO.empSeniorCertYN == "N") {
	    			$scope.showErrorMsgInDialog("輸入的招攬人員必須具有高齡完訓資格");
					return false;
        		}
    		}
    		
    		//非契撤，公平待客完訓資格檢核
    		if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && $scope.inputVO.empFairCertYN == "N") {
    			$scope.showErrorMsgInDialog("要保書申請日不得小於公平待客訓練完訓日期");
				return false;
    		}
    		
    		//非契撤，要保人/被保人/繳款人戶況檢核
    		if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" &&
    				(RegExp('[4678]').test($scope.inputVO.PROPOSER_CM_FLAG) ||
    				 RegExp('[4678]').test($scope.inputVO.INSURED_CM_FLAG) ||
    				 RegExp('[4678]').test($scope.inputVO.PAYER_CM_FLAG))) {
    			if($scope.inputVO.PROPOSER_CM_FLAG == "4" || $scope.inputVO.INSURED_CM_FLAG == "4" || 
    					$scope.inputVO.PAYER_CM_FLAG == "4") {
    				$scope.showErrorMsgInDialog("客戶為NS禁銷戶，不得送出覆核。");
    				return false;
    			}
    			if($scope.inputVO.PROPOSER_CM_FLAG == "6" || $scope.inputVO.INSURED_CM_FLAG == "6" || 
    					$scope.inputVO.PAYER_CM_FLAG == "6") {
    				$scope.showErrorMsgInDialog("客戶為客訴+NS禁銷戶，不得送出覆核。");
    				return false;
    			}
    			if($scope.inputVO.PROPOSER_CM_FLAG == "7" || $scope.inputVO.INSURED_CM_FLAG == "7" || 
    					$scope.inputVO.PAYER_CM_FLAG == "7") {
    				$scope.showErrorMsgInDialog("客戶為拒銷+NS禁銷戶，不得送出覆核。");
    				return false;
    			}
    			if($scope.inputVO.PROPOSER_CM_FLAG == "8" || $scope.inputVO.INSURED_CM_FLAG == "8" || 
    					$scope.inputVO.PAYER_CM_FLAG == "8") {
    				$scope.showErrorMsgInDialog("客戶為客訴+拒銷+NS禁銷戶，不得送出覆核。");
    				return false;
    			}
    		}
    		
            return true;
		}

		//契約變更-基金標的異動檢核
		$scope.isFitValidate2 = function(chkStatus) {
			$scope.toDay = $filter('date')(new Date(),'yyyy-MM-dd 00:00:00');//取當日日期
		    if (($scope.inputVO.PRODUCT_TYPE != "1" || $scope.inputVO.CURR_CD != "TWD" || ($scope.inputVO.PRODUCT_TYPE == "1" && $scope.inputVO.NEED_MATCH == "Y")) &&
		    		($scope.inputVO.CUST_RISK_DUE < $scope.toJsDate($scope.toDay))) { //KYC過期
		    	$scope.showErrorMsgInDialog("要保人KYC已過期，請要保人重作KYC後才能進行基金適配作業");
		    	return false;
		    }

		    //若購買投資型商品，檢核時投資屬性問卷分數欄位為必填
//            if($scope.inputVO.PRODUCT_TYPE != "1" && ($scope.inputVO.INV_SCORE == null || $scope.inputVO.INV_SCORE == undefined || $scope.inputVO.INV_SCORE == '')) {
//            	$scope.showErrorMsgInDialog('投資型商品，「投資屬性問卷分數」為必填欄位');
//            	return false;
//            }

		    return true;
		}

		//保險重要權益宣告書列印
		$scope.printInsVitIntDecBook = function() {
			$scope.printList = [];
			var date = new Date();
			if($scope.inputVO.INSURED_ID != undefined){
				var Temp_INSURED_ID = $scope.inputVO.INSURED_ID.toUpperCase();
				$scope.inputVO.INSURED_ID = Temp_INSURED_ID;
			}
			$scope.printList.push({
				CUST_ID:$scope.inputVO.CUST_ID.toUpperCase(),
				CUST_NAME : $scope.inputVO.PROPOSER_NAME,
				INSURED_ID : $scope.inputVO.INSURED_ID,
				INSURED_NAME : $scope.inputVO.INSURED_NAME,
				CURR_CD : $scope.inputVO.CURR_CD,
				INSPRD_ID : $scope.inputVO.INSPRD_ID,
				INSPRD_NAME:$scope.inputVO.INSPRD_NAME,
				REAL_PREMIUM : $scope.inputVO.REAL_PREMIUM,
				INSPRD_ANNUAL : $scope.inputVO.INSPRD_ANNUAL,
				DATE : date.toISOString()
				});
			$scope.sendRecv("IOT110", "printInsVitIntDecBook",
					"com.systex.jbranch.app.server.fps.iot110.IOT110OutputVO",
					{'list':$scope.printList}, function(tota, isError) {
						if (!isError) {
							return;
						}
	   			});
		};

		//投資型商品基金適配清單
		$scope.printInvOriAdaFunList = function() {
			$scope.printList = [];
			var date = new Date();
			$scope.printList.push({
				INSTYPE : $scope.inputVO.REG_TYPE,
				CUST_ID : $scope.inputVO.CUST_ID.toUpperCase(),
				CUST_NAME : $scope.inputVO.PROPOSER_NAME,
				EMP_ID : $scope.inputVO.RECRUIT_ID.toUpperCase(),
				INSPRD_ID : $scope.inputVO.INSPRD_ID,
				INSPRD_NAME : $scope.inputVO.INSPRD_NAME,
				DATE : date.toLocaleDateString("ja-JP"),
				POLICY_NO1 : $scope.inputVO.POLICY_NO1 ? $scope.inputVO.POLICY_NO1.toUpperCase() : "",
				POLICY_NO2 : $scope.inputVO.POLICY_NO2 ,
				POLICY_NO3 : $scope.inputVO.POLICY_NO3 ,
//				KYCList : $scope.KYCList,
				KYCList : $scope.inputVO.INVESTList,
				PRO_YN : $scope.inputVO.PRO_YN,
				C_SENIOR_PVAL : $scope.inputVO.C_SENIOR_PVAL,
				CUST_RISK_DUE : $scope.inputVO.CUST_RISK_DUE.toLocaleDateString("ja-JP"),
//				INV_SCORE : $scope.inputVO.INV_SCORE,
				REG_TYPE : $scope.inputVO.REG_TYPE,
				CUST_RISK : $scope.inputVO.CUST_RISK,
				//高資產越級適配
				SENIOR_OVER_PVAL: $scope.inputVO.SENIOR_OVER_PVAL,
				validHnwcYN: $scope.inputVO.hnwcData.validHnwcYN,
				hnwcService: $scope.inputVO.hnwcData.hnwcService,
				hnwcDueDate: $scope.inputVO.hnwcData.dueDate,
				REAL_PREMIUM: $scope.inputVO.REAL_PREMIUM,
				CURR_CD: $scope.inputVO.CURR_CD,
				PROD_CURR_RATE: $scope.inputVO.PROD_CURR_RATE,
				CUST_REMARKS: $scope.inputVO.CUST_REMARKS,
				OVER_PVAL_AMT: $scope.inputVO.OVER_PVAL_AMT,
				OVER_PVAL_MAX_AMT: $scope.inputVO.OVER_PVAL_MAX_AMT
			});
			debugger
			$scope.sendRecv("IOT110", "printInvOriAdaFunList",
					"com.systex.jbranch.app.server.fps.iot110.IOT110OutputVO",
					{'list':$scope.printList}, function(tota, isError) {
						if (!isError) {
							return;
						}
	   			});
		};

		//非常態交易錄音電訪單
		$scope.printUnuTraRecTeleIntList = function() {
			$scope.printList = [];
			var date = new Date();
			$scope.printList.push({
				CUST_ID : $scope.inputVO.CUST_ID.toUpperCase(),
				PRODUCT_TYPE : $scope.inputVO.PRODUCT_TYPE,
				DATE : date.toISOString()
			});
			$scope.sendRecv("IOT110", "printUnuTraRecTeleIntList",
					"com.systex.jbranch.app.server.fps.iot110.IOT110OutputVO",
					{'list':$scope.printList}, function(tota, isError) {
						if (!isError) {
							return;
						}
	   			});
		};

		//新契約_客戶保險購買檢核表
		$scope.printCheckList = function() {
			$scope.sendRecv("IOT110", "printCheckList", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", {'PREMATCH_SEQ':$scope.inputVO.PREMATCH_SEQ},
				function(tota, isError) {
					if (!isError) {
						return;
					}
	   			});
		};

		//要保人變更_客戶保險購買檢核表
		$scope.printChgCustCheckList = function() {
			$scope.sendRecv("IOT110", "printChgCustCheckList", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", {'PREMATCH_SEQ':$scope.inputVO.PREMATCH_SEQ},
				function(tota, isError) {
					if (!isError) {
						return;
					}
	   			});
		};

		//保費來源確認錄音照會單
		$scope.printSourceRecList = function() {
			$scope.printList = [];
			//取得錄音樣態
			var recType = $scope.getRecordType();
			
			$scope.printList.push({
				CUST_ID : $scope.inputVO.C_PREMIUM_TRANSSEQ_YN == "Y" ? $scope.inputVO.CUST_ID.toUpperCase() : "",
				PROPOSER_NAME : $scope.inputVO.C_PREMIUM_TRANSSEQ_YN == "Y" ? $scope.inputVO.PROPOSER_NAME : "",
				INSURED_ID : $scope.inputVO.I_PREMIUM_TRANSSEQ_YN == "Y" ? $scope.inputVO.INSURED_ID.toUpperCase() : "",
				INSURED_NAME : $scope.inputVO.I_PREMIUM_TRANSSEQ_YN == "Y" ? $scope.inputVO.INSURED_NAME : "",
				PAYER_ID : $scope.inputVO.P_PREMIUM_TRANSSEQ_YN == "Y" ? $scope.inputVO.PAYER_ID.toUpperCase() : "",
				PAYER_NAME : $scope.inputVO.P_PREMIUM_TRANSSEQ_YN == "Y" ? $scope.inputVO.PAYER_NAME : "",
				PROD_ID : $scope.inputVO.INSPRD_ID.toUpperCase(),
				PROD_NAME : $scope.inputVO.INSPRD_NAME,
				REC_TYPE : recType,
				PROD_CURR : $scope.inputVO.CURR_CD
			});
			debugger
			$scope.sendRecv("IOT110", "printSourceRecList", "com.systex.jbranch.app.server.fps.iot110.IOT110OutputVO", {'list':$scope.printList},
				function(tota, isError) {
						if (!isError) {
							return;
						}
	   			});
		};

		//取得錄音樣態
		$scope.getRecordType = function() {
			var recType = "";
			
			if($scope.inputVO.LOAN_SOURCE2_YN == 'Y' || $scope.inputVO.CONTRACT_END_YN == '1' || 
					$scope.inputVO.C_LOAN_CHK3_YN == 'Y' || $scope.inputVO.I_LOAN_CHK3_YN == 'Y' || $scope.inputVO.LOAN_CHK3_YN == 'Y') {
				recType = "解約";
			}
			
			if($scope.inputVO.LOAN_SOURCE_YN == 'Y' || $scope.inputVO.S_INFITEM_LOAN_YN == '1' || 
					$scope.inputVO.C_LOAN_CHK2_YN == 'Y' || $scope.inputVO.C_LOAN_CHK1_YN == 'Y' || 
					$scope.inputVO.I_LOAN_CHK2_YN == 'Y' || $scope.inputVO.I_LOAN_CHK1_YN == 'Y' ||
					$scope.inputVO.LOAN_CHK2_YN   == 'Y' || $scope.inputVO.LOAN_CHK1_YN   == 'Y') {
				if(recType != "") recType = recType + "/";
				recType = recType + "貸款或保單借款";
			}
			if($scope.inputVO.C_CD_CHK_YN == 'Y' || $scope.inputVO.I_CD_CHK_YN == 'Y' || $scope.inputVO.CD_CHK_YN == 'Y') {
				if(recType != "") recType = recType + "/";
				recType = recType + "定存解約不打折";
			}
			if($scope.inputVO.C_SALE_SENIOR_YN == 'Y' || $scope.inputVO.I_SALE_SENIOR_YN == 'Y' || $scope.inputVO.P_SALE_SENIOR_YN == 'Y') {
				if(recType != "") recType = recType + "/";
				recType = recType + "高齡關懷";
			}
			
			return recType;
		}
		
		//主約險種資料查詢(待PM專區完成後才能開發)
		$scope.Ins_query = function(){
			debugger
			if($scope.inputVO.COMPANY_NUM == "") {
				$scope.showErrorMsg('請先選擇保險公司');
				return;
			}

			var INSPRD_ID = $scope.inputVO.INSPRD_ID;
			var FB_COM_YN = $scope.inputVO.FB_COM_YN;
			var COMPANY_NUM = $scope.inputVO.COMPANY_NUM;

			var dialog = ngDialog.open({
				template: 'assets/txn/IOT910/IOT910.html',
				className: 'IOT910',
				controller:['$scope',function($scope){
					$scope.FB_COM_YN = FB_COM_YN;
					$scope.COMPANY_NUM = COMPANY_NUM;
					$scope.INSPRD_ID = (INSPRD_ID != undefined) ? INSPRD_ID.toUpperCase() : INSPRD_ID;
				}]
			});
			dialog.closePromise.then(function(data) {
//				$scope.cnctdataList = $scope.connector('get','IOT910');
				if(data.value != undefined){
					if(data.value != 'cancel'){
						//證照類型
						$scope.inputVO.CERT_TYPE = data.value.CERT_TYPE;
						//教育訓練
						$scope.inputVO.TRAINING_TYPE = data.value.TRAINING_TYPE;
						//招攬人員員編
						$scope.inputVO.EMP_ID = $scope.inputVO.RECRUIT_ID;
						$scope.sendRecv("IOT920","chk_CertTraining","com.systex.jbranch.app.server.fps.iot920.chk_CTInputVO",
								$scope.inputVO,function(tota,isError){
									if(!isError){
										if($scope.inputVO.REG_TYPE == "1") {
											//壽險新契約，檢核招攬人員分紅證照
											$scope.inputVO.empDividendCertYN = tota[0].body.empDividendCert.EMP_DIVIDEND_YN;
											$scope.inputVO.empDividendEndDate = tota[0].body.empDividendCert.EMP_DIVIDEND_END_DATE;
											var applydate = $filter('date')($scope.inputVO.APPLY_DATE,'yyyy-MM-dd');
											//分紅商品，招攬人員是否有分紅證照
											if(data.value.DIVIDEND_YN && data.value.DIVIDEND_YN == "Y") {
												if($scope.inputVO.empDividendCertYN == "N") {
													$scope.showMsg("輸入的招攬人員必須具有(銀行)人身保險業分紅保險商品教育訓練完訓資格");
												} else if($scope.toJsDate($scope.inputVO.empDividendEndDate) > $scope.toJsDate(applydate)) {
													$scope.showMsg("要保書申請日不得小於分紅完訓日期");
												}
											}
										}
										if(tota[0].body.Chk_Pass == 'Y'){
											$scope.INSPRD_Lock = true;
//											$scope.inputVO.CNCT_INSPRD_KEYNO = $scope.cnctdataList.INSPRD_KEYNO;
											$scope.inputVO.INSPRD_KEYNO = data.value.INSPRD_KEYNO;
											$scope.inputVO.INSPRD_ID = data.value.INSPRD_ID;
											$scope.check_INSPRD_ID = data.value.INSPRD_ID;
											$scope.inputVO.INSPRD_NAME = data.value.INSPRD_NAME;
											$scope.inputVO.CURR_CD = data.value.CURR_CD;
											$scope.inputVO.INSPRD_ANNUAL = data.value.INSPRD_ANNUAL;
											$scope.inputVO.PAY_TYPE = data.value.PAY_TYPE;
											$scope.inputVO.EXCH_RATE = data.value.EXCH_RATE;
											$scope.inputVO.PRD_RATE = data.value.PRD_RATE;
											$scope.inputVO.CNR_RATE = data.value.CNR_RATE;
											$scope.inputVO.AB_EXCH_RATE = data.value.AB_EXCH_RATE;//非常態交易參考匯率
											$scope.inputVO.PRODUCT_TYPE = data.value.INSPRD_TYPE;//產品類型
											$scope.inputVO.NEED_MATCH = data.value.NEED_MATCH;//是否需要適配
											$scope.inputVO.PRD_RISK = data.value.PRD_RISK;//商品風險值
											$scope.inputVO.COMPANY_NUM = data.value.COMPANY_NUM;//保險公司代碼
											$scope.inputVO.INS_COM_NAME = data.value.INS_COM_NAME;//保險公司名稱
											$scope.inputVO.PROD_DIVIDEND_YN = data.value.DIVIDEND_YN; //是否為分紅商品
											$scope.inputVO.INVESTList = [];
											debugger
											var findPrd = $filter('filter')($scope.mappingSet['IOT.NO_CHK_LOAN_INSPRD'], {DATA: $scope.inputVO.INSPRD_ID});
											$scope.isInNoChkLoan = (findPrd != null && findPrd.length > 0) ? true : false;
//											$scope.connector('set','IOT910','');

											if($scope.inputVO.CUST_RISK == "C1" && $scope.inputVO.PRODUCT_TYPE != "" && $scope.inputVO.PRODUCT_TYPE != "1") {
												$confirm({text: "客戶風險等級為C1，只能購買風險性質P1商品，請檢視適配清單有無可購買標的，若是KYC冷靜期，請解除後重新受理，產生新的要保人適合度檢核編碼。"}, {size: 'sm'});
											}
										}else{
											$scope.INSPRD_Lock = false;
											$scope.inputVO.INSPRD_ID = '';
											$scope.inputVO.INSPRD_NAME = '';
											$scope.inputVO.CURR_CD = '';
											$scope.inputVO.INSPRD_ANNUAL = undefined;
											$scope.inputVO.PAY_TYPE = '';
											$scope.inputVO.EXCH_RATE = undefined;
											$scope.inputVO.PRD_RATE = undefined;
											$scope.inputVO.CNR_RATE = undefined;
											$scope.inputVO.AB_EXCH_RATE = undefined;
											$scope.inputVO.PRODUCT_TYPE = '';
											$scope.isInNoChkLoan = false;
											$scope.inputVO.NEED_MATCH = '';//是否需要適配
											$scope.inputVO.PRD_RISK = '';//商品風險值
											$scope.inputVO.COMPANY_NUM = '';//保險公司代碼
											$scope.inputVO.INS_COM_NAME = '';//保險公司名稱
											$scope.inputVO.PROD_DIVIDEND_YN = "";
											$scope.showErrorMsg('ehl_01_iot120_002')
										}
									}
							});
					}
				}
			});
		}

		//確認險種代號是否與險種名稱一致
		$scope.checkCNCTData = function(){
			if($scope.check_INSPRD_ID != $scope.inputVO.INSPRD_ID){
				$scope.INSPRD_Lock = false;
				$scope.inputVO.INSPRD_NAME = '';
				$scope.inputVO.CURR_CD = '';
				$scope.inputVO.INSPRD_ANNUAL = '';
				$scope.inputVO.PAY_TYPE = '';
				$scope.inputVO.EXCH_RATE = undefined;
				$scope.inputVO.PRD_RATE = undefined;
				$scope.inputVO.CNR_RATE = undefined;
				$scope.inputVO.AB_EXCH_RATE = undefined;
				$scope.inputVO.PRODUCT_TYPE = '';
			}
		}

		//根據欄位帶出分行代碼、招攬人員姓名、要保人姓名、留存分行地址
		$scope.getInfo = function(in_column){

			$scope.inputVO.in_column = in_column;
			if($scope.inputVO.in_column == 'INSURED'){
				$scope.inputVO.INSURED_ID = $scope.inputVO.INSURED_ID ? $scope.inputVO.INSURED_ID.toUpperCase() : "";
				$scope.inputVO.INSURED_NAME = '';
				$scope.inputVO.INSURED_CM_FLAG = '';
				$scope.inputVO.INSURED_INCOME3 = undefined;

				if($scope.inputVO.RLT_BT_PROPAY == "2") {	//繳款人= 被保人
					$scope.inputVO.PAYER_ID = '';
					$scope.inputVO.PAYER_NAME = '';
					$scope.inputVO.PAYER_CM_FLAG = '';
					$scope.inputVO.LOAN_CHK1_YN = '';
					$scope.inputVO.LOAN_CHK2_YN = '';
					$scope.inputVO.LOAN_CHK2_DATE = null;
					$scope.inputVO.LOAN_CHK3_YN = '';
					$scope.inputVO.LOAN_CHK4_YN = '';
					$scope.inputVO.P_REVOLVING_LOAN_YN = '';
					$scope.inputVO.CD_CHK_YN = '';
					$scope.inputVO.P_CD_DUE_DATE = null;
					$scope.inputVO.P_LOAN_APPLY_DATE = null;
					$scope.inputVO.P_LOAN_APPLY_YN = '';

				}
			}
			if($scope.inputVO.in_column == 'CUST'){
				$scope.inputVO.CUST_ID = $scope.inputVO.CUST_ID ? $scope.inputVO.CUST_ID.toUpperCase() : "";
				$scope.inputVO.PROPOSER_NAME = '';
				$scope.inputVO.birthday = undefined;
				$scope.inputVO.PROPOSER_CM_FLAG = '';
				$scope.inputVO.AML = "";
				$scope.inputVO.PRECHECK = "";
				$scope.inputVO.BUSINESS_REL = "";
				$scope.inputVO.PROPOSER_INCOME3 = undefined;
				$scope.inputVO.hnwcData = undefined;
//				$scope.inputVO.INVESTList = [];

				if($scope.chkSameCustId == true){	//要保人=被保人
					$scope.inputVO.INSURED_ID = '';
					$scope.inputVO.INSURED_NAME = '';
					$scope.inputVO.INSURED_BIRTH = null;
					$scope.inputVO.INSURED_INCOME3 = undefined;
					$scope.inputVO.I_LOAN_CHK1_YN = '';
					$scope.inputVO.I_LOAN_CHK2_YN = '';
					$scope.inputVO.I_LOAN_CHK2_DATE = null;
					$scope.inputVO.I_LOAN_CHK3_YN = '';
					$scope.inputVO.I_LOAN_CHK4_YN = '';
					$scope.inputVO.I_REVOLVING_LOAN_YN = '';
					$scope.inputVO.I_CD_CHK_YN = '';
					$scope.inputVO.I_CD_DUE_DATE = null;
					$scope.inputVO.I_LOAN_APPLY_DATE = null;
					$scope.inputVO.I_LOAN_APPLY_YN = '';
					$scope.inputVO.I_KYC_INCOME = '';
					$scope.inputVO.INSURE_DEBIT = undefined;
				}

				if($scope.inputVO.RLT_BT_PROPAY == "1") {	//繳款人=要保人
					$scope.inputVO.PAYER_ID = '';
					$scope.inputVO.PAYER_NAME = '';
					$scope.inputVO.PAYER_BIRTH = null;
					$scope.inputVO.PAYER_CM_FLAG = '';
					$scope.inputVO.LOAN_CHK1_YN = '';
					$scope.inputVO.LOAN_CHK2_YN = '';
					$scope.inputVO.LOAN_CHK2_DATE = null;
					$scope.inputVO.LOAN_CHK3_YN = '';
					$scope.inputVO.LOAN_CHK4_YN = '';
					$scope.inputVO.P_REVOLVING_LOAN_YN = '';
					$scope.inputVO.CD_CHK_YN = '';
					$scope.inputVO.P_CD_DUE_DATE = null;
					$scope.inputVO.P_LOAN_APPLY_DATE = null;
					$scope.inputVO.P_LOAN_APPLY_YN = '';
				}
			}
			if($scope.inputVO.in_column == 'REPRESET'){
				$scope.inputVO.REPRESET_ID = $scope.inputVO.REPRESET_ID ? $scope.inputVO.REPRESET_ID.toUpperCase() : "";
				$scope.inputVO.REPRESET_NAME = '';
				$scope.inputVO.REPRESET_CM_FLAG = '';
			}

			if($scope.inputVO.in_column == 'PAYER'){
				if($scope.inputVO.RLT_BT_PROPAY == "1") {	//繳款人=要保人
					$scope.inputVO.PAYER_ID = $scope.inputVO.CUST_ID;
				} else if($scope.inputVO.RLT_BT_PROPAY == "2") {	//繳款人=被保人
					$scope.inputVO.PAYER_ID = $scope.inputVO.INSURED_ID;
				} else {
					$scope.inputVO.PAYER_ID = $scope.inputVO.PAYER_ID ? $scope.inputVO.PAYER_ID.toUpperCase() : "";
				}
				$scope.inputVO.PAYER_NAME = '';
				$scope.inputVO.PAYER_CM_FLAG = '';
			}

			if($scope.inputVO.in_column == 'CHG_CUST'){
				$scope.inputVO.CHG_CUST_ID = $scope.inputVO.CHG_CUST_ID ? $scope.inputVO.CHG_CUST_ID.toUpperCase() :"";
				$scope.inputVO.CHG_PROPOSER_NAME = '';
				$scope.inputVO.CHG_PROPOSER_BIRTH = undefined;
				$scope.inputVO.AML = "";
				$scope.inputVO.PRECHECK = "";
				$scope.inputVO.BUSINESS_REL = "";
				$scope.inputVO.hnwcData = undefined;
			}

			if($scope.inputVO.in_column == 'RECRUIT') {
				$scope.inputVO.RECRUIT_ID = $scope.inputVO.RECRUIT_ID ? $scope.inputVO.RECRUIT_ID.toUpperCase() : "";
				$scope.EMP_NAME = '';
				$scope.inputVO.EMP_NAME = '';
				$scope.inputVO.empDividendCertYN = "N";
				$scope.inputVO.empDividendEndDate = undefined;
				$scope.inputVO.empFairCertYN = "N"; //招攬人員是否有公平待客完訓資格
				$scope.inputVO.empSeniorCertYN = "N"; //招攬人員是否有高齡完訓資格
			}

			$scope.sendRecv("IOT920","getCUSTInfo","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",
					$scope.inputVO,function(tota,isError){
				if(!isError){
					debugger
					//$scope.inputVO.in_column == 'RECRUIT'
					if(tota[0].body.EMP_NAME != null){
						if(tota[0].body.EMP_NAME.length>0){
							$scope.EMP_NAME = tota[0].body.EMP_NAME[0].EMP_NAME;
							$scope.inputVO.EMP_NAME = tota[0].body.EMP_NAME[0].EMP_NAME;
							$scope.inputVO.empDividendCertYN = tota[0].body.EMP_NAME[0].EMP_DIVIDEND_CERT.EMP_DIVIDEND_YN; //招攬人員是否有分紅商品證照
							$scope.inputVO.empDividendEndDate = tota[0].body.EMP_NAME[0].EMP_DIVIDEND_CERT.EMP_DIVIDEND_END_DATE; //招攬人員分紅商品證照完訓日
							$scope.inputVO.empFairCertYN = tota[0].body.EMP_NAME[0].EMP_FAIR_CERT; //招攬人員是否有公平待客完訓資格
							$scope.inputVO.empSeniorCertYN = tota[0].body.EMP_NAME[0].EMP_SENIOR_CERT; //招攬人員是否有高齡完訓資格
						} else {
							$scope.inputVO.RECRUIT_ID = "";
						}
					}

					if(tota[0].body.CUST_NAME != null){
						if($scope.inputVO.in_column == 'CHG_CUST') {
							$scope.inputVO.CHG_PROPOSER_NAME = tota[0].body.CUST_NAME[0].CUST_NAME;
							$scope.inputVO.CHG_PROPOSER_BIRTH = $scope.toJsDate(tota[0].body.CUST_NAME[0].BIRTHDAY);
							if(tota[0].body.CUST_NAME[0].CUST_NAME){
								$scope.branch_cust = true;
							}else{
								$scope.branch_cust = false;
							}
						} else {
							if(tota[0].body.CUST_NAME.length>0){
//								$scope.notBranchCust = false;
								console.log(tota[0].body);

								$scope.inputVO.PROPOSER_NAME = tota[0].body.CUST_NAME[0].CUST_NAME;
								$scope.inputVO.PROPOSER_BIRTH = $scope.toJsDate(tota[0].body.CUST_NAME[0].BIRTHDAY);
								$scope.inputVO.PROPOSER_CM_FLAG = tota[0].body.CUST_NAME[0].PROPOSER_CM_FLAG;
								$scope.inputVO.PROPOSER_INCOME3 = tota[0].body.INCOME3;
								$scope.inputVO.C_LOAN_CHK1_YN = tota[0].body.CUST_NAME[0].C_LOAN_CHK1_YN;
								$scope.inputVO.C_LOAN_CHK2_YN = tota[0].body.CUST_NAME[0].C_LOAN_CHK2_YN;
								$scope.inputVO.C_LOAN_CHK2_DATE = $scope.toJsDate(tota[0].body.CUST_NAME[0].C_LOAN_CHK2_DATE);
								$scope.inputVO.C_LOAN_CHK3_YN = tota[0].body.CUST_NAME[0].C_LOAN_CHK3_YN;
								$scope.inputVO.C_LOAN_CHK4_YN = tota[0].body.CUST_NAME[0].C_LOAN_CHK4_YN;
								$scope.inputVO.C_REVOLVING_LOAN_YN = tota[0].body.CUST_NAME[0].C_REVOLVING_LOAN_YN;
								$scope.inputVO.C_CD_CHK_YN = tota[0].body.CUST_NAME[0].C_CD_CHK_YN;
								$scope.inputVO.C_CD_DUE_DATE = $scope.toJsDate(tota[0].body.CUST_NAME[0].C_CD_DUE_DATE);
								$scope.inputVO.C_LOAN_APPLY_DATE = $scope.toJsDate(tota[0].body.CUST_NAME[0].C_LOAN_APPLY_DATE);
								$scope.inputVO.C_PREM_DATE = $scope.toJsDate(tota[0].body.CUST_NAME[0].C_PREM_DATE);
								$scope.inputVO.C_LOAN_APPLY_YN = tota[0].body.CUST_NAME[0].C_LOAN_APPLY_YN;
								$scope.inputVO.C_KYC_INCOME = tota[0].body.CUST_NAME[0].C_KYC_INCOME;
								$scope.inputVO.CUST_DEBIT = tota[0].body.CUST_NAME[0].CUST_DEBIT;

								//要保人非本行客戶不可新增
								if($scope.inputVO.PROPOSER_CM_FLAG == '10') {
									$scope.showErrorMsgInDialog('要保人未開戶，不可進行要保人保險購買檢核');
									$scope.inputVO.CUST_ID = "";
									return;
								}

								if($scope.chkSameCustId == true){	//要保人=被保人
									$scope.inputVO.INSURED_ID = $scope.inputVO.CUST_ID;
									$scope.inputVO.INSURED_NAME = $scope.inputVO.PROPOSER_NAME;
									$scope.inputVO.INSURED_BIRTH = $scope.inputVO.PROPOSER_BIRTH;
									$scope.inputVO.INSURED_CM_FLAG = $scope.inputVO.PROPOSER_CM_FLAG;
									$scope.inputVO.INSURED_INCOME3 = $scope.inputVO.PROPOSER_INCOME3;
									$scope.inputVO.I_LOAN_CHK1_YN = $scope.inputVO.C_LOAN_CHK1_YN;
									$scope.inputVO.I_LOAN_CHK2_YN = $scope.inputVO.C_LOAN_CHK2_YN;
									$scope.inputVO.I_LOAN_CHK2_DATE = $scope.inputVO.C_LOAN_CHK2_DATE;
									$scope.inputVO.I_LOAN_CHK3_YN = $scope.inputVO.C_LOAN_CHK3_YN;
									$scope.inputVO.I_LOAN_CHK4_YN = $scope.inputVO.C_LOAN_CHK4_YN;
									$scope.inputVO.I_REVOLVING_LOAN_YN = $scope.inputVO.C_REVOLVING_LOAN_YN;
									$scope.inputVO.I_CD_CHK_YN = $scope.inputVO.C_CD_CHK_YN;
									$scope.inputVO.I_CD_DUE_DATE = $scope.inputVO.C_CD_DUE_DATE;
									$scope.inputVO.I_LOAN_APPLY_DATE = $scope.inputVO.C_LOAN_APPLY_DATE;
									$scope.inputVO.I_LOAN_APPLY_YN = $scope.inputVO.C_LOAN_APPLY_YN;
									$scope.inputVO.I_KYC_INCOME = $scope.inputVO.C_KYC_INCOME;
									$scope.inputVO.INSURED_DEBIT = $scope.inputVO.CUST_DEBIT;
								}
								if(tota[0].body.CUST_NAME[0].CUST_NAME){
									$scope.branch_cust = true;
								}else{
									$scope.branch_cust = false;
								}
								debugger
								$scope.inputVO.AO_CODE = tota[0].body.CUST_NAME[0].AO_CODE;
								$scope.inputVO.CUST_RISK = tota[0].body.CUST_NAME[0].CUST_RISK_ATR;
								$scope.inputVO.CUST_RISK_DUE = $scope.toJsDate(tota[0].body.CUST_NAME[0].KYC_DUE_DATE);
								// 是否為弱勢戶
								$scope.inputVO.UNDER_YN = tota[0].body.CUST_NAME[0].UNDER_YN;
								// 是否為磚投
								$scope.inputVO.PRO_YN = tota[0].body.CUST_NAME[0].PRO_YN;
								// 是否為特定客戶
								$scope.inputVO.CUST_REMARKS = tota[0].body.CUST_NAME[0].CUST_REMARKS;
								// 顯示Fatca
								$scope.queryCustData();
								// 是否大於七歲
								$scope.checkyear();

								if($scope.inputVO.CUST_RISK == "C1" && $scope.inputVO.PRODUCT_TYPE != "" && $scope.inputVO.PRODUCT_TYPE != "1") {
									$confirm({text: "客戶風險等級為C1，只能購買風險性質P1商品，請檢視適配清單有無可購買標的，若是KYC冷靜期，請解除後重新受理，產生新的要保人適合度檢核編碼。"}, {size: 'sm'});

								}

								debugger
								if($scope.inputVO.RLT_BT_PROPAY == "1") {	//繳款人=要保人
									$scope.inputVO.PAYER_ID = $scope.inputVO.CUST_ID;
									//重新取得繳款人資料
									$scope.getInfo("PAYER");
								}
							}else{
								if($scope.chkSameCustId == true){
									$scope.inputVO.INSURED_ID = $scope.inputVO.CUST_ID;
									$scope.inputVO.INSURED_NAME = $scope.inputVO.PROPOSER_NAME;
									$scope.inputVO.INSURED_BIRTH = $scope.inputVO.PROPOSER_BIRTH;
								}
							}
						}
						debugger
						if(tota[0].body.AML != null) $scope.inputVO.AML = tota[0].body.AML;
						if(tota[0].body.PRECHECK != null) $scope.inputVO.PRECHECK = tota[0].body.PRECHECK;
						if(tota[0].body.hnwcData != null) $scope.inputVO.hnwcData = tota[0].body.hnwcData; //高資產客戶註記
						$scope.inputVO.insAsset = 0;
						if(tota[0].body.INS_ASSET != null) $scope.inputVO.insAsset = tota[0].body.INS_ASSET;
						//新增業務關係
						if($scope.inputVO.AML == undefined || $scope.inputVO.AML == null || $scope.inputVO.AML == "" || $scope.inputVO.AML == "高") {
							$scope.inputVO.BUSINESS_REL = $scope.inputVO.insAsset != 0 ? "N" : "Y";
						} else {
							$scope.inputVO.BUSINESS_REL = "";
						}
					}
					
					if(tota[0].body.INSURED_NAME != null){
						if(tota[0].body.INSURED_NAME.length>0){
							if($scope.inputVO.INSURED_ID !=''){
								$scope.inputVO.INSURED_NAME = tota[0].body.INSURED_NAME[0].CUST_NAME;
								$scope.inputVO.INSURED_CM_FLAG = tota[0].body.INSURED_NAME[0].INSURED_CM_FLAG;
								$scope.inputVO.INSURED_BIRTH = $scope.toJsDate(tota[0].body.INSURED_NAME[0].BIRTHDAY);
								$scope.inputVO.INSURED_INCOME3 = tota[0].body.INCOME3;
								$scope.inputVO.I_LOAN_CHK1_YN = tota[0].body.INSURED_NAME[0].I_LOAN_CHK1_YN;
								$scope.inputVO.I_LOAN_CHK2_YN = tota[0].body.INSURED_NAME[0].I_LOAN_CHK2_YN;
								$scope.inputVO.I_LOAN_CHK2_DATE = $scope.toJsDate(tota[0].body.INSURED_NAME[0].I_LOAN_CHK2_DATE);
								$scope.inputVO.I_LOAN_CHK3_YN = tota[0].body.INSURED_NAME[0].I_LOAN_CHK3_YN;
								$scope.inputVO.I_LOAN_CHK4_YN = tota[0].body.INSURED_NAME[0].I_LOAN_CHK4_YN;
								$scope.inputVO.I_REVOLVING_LOAN_YN = tota[0].body.INSURED_NAME[0].I_REVOLVING_LOAN_YN;
								$scope.inputVO.I_CD_CHK_YN = tota[0].body.INSURED_NAME[0].I_CD_CHK_YN;
								$scope.inputVO.I_CD_DUE_DATE = $scope.toJsDate(tota[0].body.INSURED_NAME[0].I_CD_DUE_DATE);
								$scope.inputVO.I_LOAN_APPLY_DATE = $scope.toJsDate(tota[0].body.INSURED_NAME[0].I_LOAN_APPLY_DATE);
								$scope.inputVO.I_LOAN_APPLY_YN = tota[0].body.INSURED_NAME[0].I_LOAN_APPLY_YN;
								$scope.inputVO.I_KYC_INCOME = tota[0].body.INSURED_NAME[0].I_KYC_INCOME;
								$scope.inputVO.INSURED_DEBIT = tota[0].body.INSURED_NAME[0].INSURED_DEBIT;

								if($scope.chkSameInsuredId) {
									$scope.inputVO.CHG_CUST_ID = $scope.inputVO.INSURED_ID;
									$scope.getInfo("CHG_CUST");
								}

								if($scope.inputVO.RLT_BT_PROPAY == "2") {	//繳款人=被保人
									$scope.inputVO.PAYER_ID = $scope.inputVO.INSURED_ID;
									//重新取得繳款人資料
									$scope.getInfo("PAYER");
								}
							}
						}
					}
					if(tota[0].body.REPRESETList != null){
						if(tota[0].body.REPRESETList.length>0){
							if($scope.inputVO.REPRESETList !=''){
								$scope.inputVO.REPRESET_NAME = tota[0].body.REPRESETList[0].CUST_NAME;
								if(tota[0].body.REPRESETList[0].CUST_NAME){
									$scope.branch_represet = true;
								}else{
									$scope.branch_represet = false;
								}
								$scope.inputVO.REPRESET_CM_FLAG = tota[0].body.REPRESETList[0].REPRESET_CM_FLAG;
//								$scope.RepresetCust = true;
							}
						}
					}
					if(tota[0].body.COM_ADDRESS != null){
						if(tota[0].body.COM_ADDRESS.length>0){
							$scope.COM_ADDRESS = tota[0].body.COM_ADDRESS[0].COM_ADDRESS;
						}
					}
					if(tota[0].body.REFList != null){
						$scope.inputVO.REF_CON_EMPID = '';
						$scope.inputVO.REF_CON_NAME = '';
						if(tota[0].body.REFList.length>0){
							$scope.inputVO.REF_CON_EMPID = tota[0].body.REFList[0].SALES_PERSON;
							$scope.inputVO.REF_CON_NAME = tota[0].body.REFList[0].SALES_NAME;
						}
					}
					if(tota[0].body.PAYERList != null){
						debugger
						if(tota[0].body.PAYERList.length>0){
							if($scope.inputVO.PAYER_ID !=''){
								$scope.inputVO.PAYER_NAME = tota[0].body.PAYERList[0].CUST_NAME;
								$scope.inputVO.PAYER_CM_FLAG = tota[0].body.PAYERList[0].PAYER_CM_FLAG;
								$scope.inputVO.PAYER_BIRTH = $scope.toJsDate(tota[0].body.PAYERList[0].BIRTHDAY);
								$scope.inputVO.LOAN_CHK1_YN = tota[0].body.PAYERList[0].LOAN_CHK1_YN;
								$scope.inputVO.LOAN_CHK2_YN = tota[0].body.PAYERList[0].LOAN_CHK2_YN;
								$scope.inputVO.LOAN_CHK2_DATE = $scope.toJsDate(tota[0].body.PAYERList[0].LOAN_CHK2_DATE);
								$scope.inputVO.LOAN_CHK3_YN = tota[0].body.PAYERList[0].LOAN_CHK3_YN;
								$scope.inputVO.LOAN_CHK4_YN = tota[0].body.PAYERList[0].LOAN_CHK4_YN;
								$scope.inputVO.P_REVOLVING_LOAN_YN = tota[0].body.PAYERList[0].P_REVOLVING_LOAN_YN;
								$scope.inputVO.CD_CHK_YN = tota[0].body.PAYERList[0].CD_CHK_YN;
								$scope.inputVO.P_CD_DUE_DATE = $scope.toJsDate(tota[0].body.PAYERList[0].P_CD_DUE_DATE);
								$scope.inputVO.P_LOAN_APPLY_DATE = $scope.toJsDate(tota[0].body.PAYERList[0].P_LOAN_APPLY_DATE);
								$scope.inputVO.P_LOAN_APPLY_YN = tota[0].body.PAYERList[0].P_LOAN_APPLY_YN;
							}
						}
					}

					if($scope.inputVO.in_column == 'CHG_CUST') {
						$scope.regType3AuthChange();
					}
				}else{
					if($scope.inputVO.in_column == 'INSURED'){
						$scope.inputVO.INSURED_ID = '';
					}
					if($scope.inputVO.in_column == 'CUST'){
						$scope.inputVO.CUST_ID = '';
					}
					if($scope.inputVO.in_column == 'REPRESET'){
						$scope.inputVO.REPRESET_ID = '';
					}
					if($scope.inputVO.in_column == 'PAYER'){
						$scope.inputVO.PAYER_ID = '';
					}
					if($scope.inputVO.in_column == 'CHG_CUST'){
						$scope.inputVO.CHG_CUST_ID = '';
					}
				}
				debugger
				//要保人有最近一年內撥貸日者，才需要顯示授信收入，若無最近撥貸日，就不用顯示授信收入
				if($scope.inputVO.C_LOAN_CHK2_DATE == undefined || $scope.inputVO.C_LOAN_CHK2_DATE == null || $scope.inputVO.C_LOAN_CHK2_DATE == '') {
					//不用顯示授信收入
					$scope.inputVO.PROPOSER_INCOME3 = 0;
				}
				//被保人有最近一年內撥貸日者，才需要顯示授信收入，若無最近撥貸日，就不用顯示授信收入
				if($scope.inputVO.I_LOAN_CHK2_DATE == undefined || $scope.inputVO.I_LOAN_CHK2_DATE == null || $scope.inputVO.I_LOAN_CHK2_DATE == '') {
					//不用顯示授信收入
					$scope.inputVO.INSURED_INCOME3 = 0;
				}
			});
		}

		//躉繳/分期繳關聯分期繳繳別
		$scope.linkmop2 = function(){
			if($scope.inputVO.PAY_TYPE != '1'){
					$scope.mop2lock = false;
			}else{
				$scope.mop2lock = true;
			}
		}

		//FPS導頁進入
		$scope.isOpenDialog = function(){
			if ($scope.fromFPS){
				console.log($scope.FPSData);
				$scope.inputVO.CUST_ID = $scope.FPSData.custID;
				$scope.inputVO.INSPRD_ID= $scope.FPSData.prdID;//商品代號
				$scope.getInfo('CUST');
			}
			else if($scope.from450) {
				$scope.inputVO.CUST_ID = $scope.FROM450_CUST_ID;
				$scope.inputVO.PROPOSER_NAME = $scope.FROM450_CUST_NAME;
				$scope.inputVO.INSPRD_ID = $scope.FROM450_PRD_ID;
				var birthday = $scope.FROM450_BIRTHDAY ? $scope.toJsDate($scope.FROM450_BIRTHDAY) : undefined;
				$scope.inputVO.PROPOSER_BIRTH = birthday;
				$scope.inputVO.REAL_PREMIUM = $filter('number')($scope.FROM450_POLICYFEE ? Number($scope.FROM450_POLICYFEE) : undefined);
			}
			else if($scope.from210) {
				debugger
				$scope.inputVO.INSURED_ID = $scope.FROM210_CUST_ID;
				$scope.inputVO.INSURED_NAME = $scope.FROM210_CUST_NAME;
				$scope.inputVO.INSPRD_ID = $scope.FROM210_PRD_ID;
				$scope.inputVO.REAL_PREMIUM = $filter('number')($scope.FROM210_PREMIUM ? Number($scope.FROM210_PREMIUM) : undefined);
			}
		}
		$scope.isOpenDialog();


		//電子要保書根據[保險文件編號]帶出資料
		$scope.getCaseIDInfo = function(){
			if($scope.inputVO.CASE_ID != undefined && $scope.inputVO.CASE_ID != null && $scope.inputVO.CASE_ID != ""){
				debugger
				$scope.inputVO.CASE_ID = $scope.inputVO.CASE_ID.toUpperCase();

				if($scope.inputVO.CASE_ID.substring(0, 1) != "J" || $scope.inputVO.CASE_ID.length != 10) {
					$scope.showErrorMsg('請輸入”J”開頭之10碼同意書編號或WEB上傳號碼');
					$scope.inputVO.CASE_ID = "";
					return;
				}

				$scope.sendRecv("IOT120","Get_InsInfo","com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
    					$scope.inputVO,function(tota,isError){
					if(!isError){
						$scope.webservice = tota[0].body.webserviceData;
						if($scope.webservice.type == 'RestWS'){
							if($scope.webservice.CODE == '200') {
								var errorMsg = $scope.webservice.Message;
								$scope.showErrorMsg(errorMsg);
								$scope.inputVO.CASE_ID = "";
							}else if($scope.webservice.CODE == '300'){
								var errorMsg = $scope.webservice.Message;
								$scope.showErrorMsg(errorMsg);
								$scope.inputVO.CASE_ID = "";
							}else{
								$scope.parseWebserviceData();
								$scope.Doc_print("N");
							}
						}else{
							$scope.showErrorMsg('查無此案件編號資料');
							$scope.inputVO.CASE_ID = "";
						}
					}else{
						$scope.inputVO.CASE_ID = "";
					}
				});//webService end
			}else{
				$scope.inputVO.CASE_ID = "";
			}
		}

		$scope.parseWebserviceData = function(){
			debugger
			$scope.inputVO.AO_ID  = $scope.webservice.Result.AO_ID;
			$scope.inputVO.INS_ID = $scope.webservice.Result.DATA_DISP;
			if($scope.webservice.Result.SING_SALE_ID != ''){
				$scope.inputVO.RECRUIT_ID = $scope.webservice.Result.SING_SALE_ID;
				$scope.getInfo('RECRUIT');
			}
			$scope.inputVO.INSURED_ID = $scope.webservice.Result.I_ID;
			if($scope.webservice.Result.I_NAME != undefined){
				$scope.inputVO.INSURED_NAME = $scope.webservice.Result.I_NAME.replace(/\s/g,'');
				$scope.getInfo('INSURED');
			}else{
				$scope.inputVO.INSURED_NAME = $scope.webservice.Result.I_NAME;
			}
			$scope.inputVO.CUST_ID = $scope.webservice.Result.A_ID;
			$scope.inputVO.PROPOSER_NAME = $scope.webservice.Result.A_NAME;
			if($scope.webservice.Result.A_NAME != undefined){
				$scope.inputVO.PROPOSER_NAME = $scope.webservice.Result.A_NAME.replace(/\s/g,'');
			}else{
				$scope.inputVO.PROPOSER_NAME = $scope.webservice.Result.A_NAME;
			}

			if($scope.inputVO.CUST_ID){
				$scope.getInfo('CUST');
			}
			//實收保費
			$scope.inputVO.REAL_PREMIUM = $scope.webservice.Result.MINS_PREMIUM;
			//首期保費繳費方式
			$scope.inputVO.FIRST_PAY_WAY = $scope.webservice.Result.FPAY_KIND;
			//要保人生日民國年轉西元年再轉日期格式
			$scope.inputVO.PROPOSER_BIRTH = $scope.webserviceDatechange($scope.webservice.Result.A_BIRTH);

			$scope.inputVO.INSPRD_ID = $scope.webservice.Result.MINS_KIND;

			if($scope.webservice.Result.MOP != ''){
				var MOP = $scope.webservice.Result.MOP.substr(0,1);
				if(MOP != 'D'){
					$scope.inputVO.MOP2 = $scope.webservice.Result.MOP;
				}else{
					$scope.inputVO.MOP2 = null;
				}
			}

			//要保書填寫申請日
			$scope.inputVO.APPLY_DATE = $scope.webserviceDatechange($scope.webservice.Result.DECLARE_DATE);
			//附約
			if($scope.webservice.Result.INS_RIDER_DTLList.length > 0){
				$scope.inputVO.INS_RIDER_DTLList = $scope.webservice.Result.INS_RIDER_DTLList;
				$scope.requiredINS_RIDER_DTLList = true;
			}

			//業報書投保前三個月內有辦理解約
			$scope.inputVO.CONTRACT_END_YN = $scope.webservice.Result.CONTRACT_END;
			//業報書投保前三個月內有辦理貸款或保險單借款
			$scope.inputVO.S_INFITEM_LOAN_YN = $scope.webservice.Result.S_INFITEM_LOAN;
			//要保人工作內容
			$scope.inputVO.PROPOSER_WORK = $scope.webservice.Result.A_WORK;
			//被保人工作內容
			$scope.inputVO.INSURED_WORK = $scope.webservice.Result.I_WORK;
//			//高齡投保錄音檢核
			$scope.inputVO.RECORDING_OLD = $scope.webservice.Result.RECORDING_OLD;
			$scope.inputVO.C_SALE_SENIOR_YN = $scope.inputVO.RECORDING_OLD;//高齡銷售過程錄音檢核
			//是否為視訊投
			$scope.inputVO.MAPPVIDEO_YN = ($scope.webservice.Result.DistanceSignFlag == "Y" ? "Y" : "N");
			//投資屬性問卷分數
			$scope.inputVO.INV_SCORE = $scope.webservice.Result.SCORES;
			//理專補充說明：視訊投保不通過原因
			$scope.inputVO.MAPPVIDEO_AGENTMEMO = $scope.webservice.Result.AgentMemo;
			//Y:線上簽署行動投保服務同意書案件
			$scope.inputVO.DIGITAL_AGREESIGN_YN = $scope.webservice.Result.DigitalAgreeSignFlag;

			//投資標的
			//不由人壽提供
//			if($scope.webservice.Result.INVESTList.length > 0){
//				$scope.inputVO.INVESTList = $scope.webservice.Result.INVESTList;
//			}//投資標的end

			debugger
			//保費支出來源是否為貸款
			if($scope.webservice.Result.S_INFITEM_PREM_RES != null && $scope.webservice.Result.S_INFITEM_PREM_RES != undefined && $scope.webservice.Result.S_INFITEM_PREM_RES != "") {
				if($scope.webservice.Result.S_INFITEM_PREM_RES.indexOf("7") >= 0 || $scope.webservice.Result.S_INFITEM_PREM_RES.indexOf("9") >= 0) {
					$scope.inputVO.LOAN_SOURCE_YN = "Y";
				} else {
					$scope.inputVO.LOAN_SOURCE_YN = "N";
				}

				if($scope.webservice.Result.S_INFITEM_PREM_RES.indexOf("B") >= 0 || $scope.webservice.Result.S_INFITEM_PREM_RES.indexOf("C") >= 0) {
					$scope.inputVO.LOAN_SOURCE2_YN = "Y";
				} else {
					$scope.inputVO.LOAN_SOURCE2_YN = "N";
				}
			} else {
				$scope.inputVO.LOAN_SOURCE_YN = "N";
				$scope.inputVO.LOAN_SOURCE2_YN = "N";
			}

			//計算要保人/被保人工作年收入(業報)/(財告)
			$scope.calInCome();
		}

		//計算要保人/被保人工作年收入(業報)/(財告)
		$scope.calInCome = function() {
			if(!$scope.webservice || !$scope.webservice.Result)
				return;

			debugger
			var S_INFITEM_08 = ($scope.webservice.Result.S_INFITEM_08 == null || $scope.webservice.Result.S_INFITEM_08 == undefined || $scope.webservice.Result.S_INFITEM_08 == '') ? 0 : $scope.webservice.Result.S_INFITEM_08;
			var S_INFITEM_20 = ($scope.webservice.Result.S_INFITEM_20 == null || $scope.webservice.Result.S_INFITEM_20 == undefined || $scope.webservice.Result.S_INFITEM_20 == '') ? 0 : $scope.webservice.Result.S_INFITEM_20;
			var D000069_S_INFITEM_08 = ($scope.webservice.Result.D000069_S_INFITEM_08 == null || $scope.webservice.Result.D000069_S_INFITEM_08 == undefined || $scope.webservice.Result.D000069_S_INFITEM_08 == '') ? 0 : $scope.webservice.Result.D000069_S_INFITEM_08;
			var D000069_S_INFITEM_20 = ($scope.webservice.Result.D000069_S_INFITEM_20 == null || $scope.webservice.Result.D000069_S_INFITEM_20 == undefined || $scope.webservice.Result.D000069_S_INFITEM_20 == '') ? 0 : $scope.webservice.Result.D000069_S_INFITEM_20;
			var S_INFITEM_07 = ($scope.webservice.Result.S_INFITEM_07 == null || $scope.webservice.Result.S_INFITEM_07 == undefined || $scope.webservice.Result.S_INFITEM_07 == '') ? 0 : $scope.webservice.Result.S_INFITEM_07;
			var AINS_OTHERINCOME = ($scope.webservice.Result.AINS_OTHERINCOME == null || $scope.webservice.Result.AINS_OTHERINCOME == undefined || $scope.webservice.Result.AINS_OTHERINCOME == '') ? 0 : $scope.webservice.Result.AINS_OTHERINCOME;
			var D000069_S_INFITEM_07 = ($scope.webservice.Result.D000069_S_INFITEM_07 == null || $scope.webservice.Result.D000069_S_INFITEM_07 == undefined || $scope.webservice.Result.D000069_S_INFITEM_07 == '') ? 0 : $scope.webservice.Result.D000069_S_INFITEM_07;
			var D000069_AINS_OTHERINCOME = ($scope.webservice.Result.D000069_AINS_OTHERINCOME == null || $scope.webservice.Result.D000069_AINS_OTHERINCOME == undefined || $scope.webservice.Result.D000069_AINS_OTHERINCOME == '') ? 0 : $scope.webservice.Result.D000069_AINS_OTHERINCOME;

			//要保人工作年收入(業報)/(財告)
			if($scope.inputVO.CUST_ID == $scope.webservice.Result.A_ID) {	//要保人同電子要保書回傳資料才計算
				if($scope.inputVO.CUST_ID == $scope.inputVO.INSURED_ID) {
					$scope.inputVO.PROPOSER_INCOME1	= (parseFloat(S_INFITEM_08) + parseFloat(S_INFITEM_07));	//業報
//					$scope.inputVO.PROPOSER_INCOME2	= (parseFloat(D000069_S_INFITEM_08) * 10000 + parseFloat(D000069_S_INFITEM_07) * 10000).toFixed();	//財告
				} else {
					$scope.inputVO.PROPOSER_INCOME1	= (parseFloat(S_INFITEM_20) + parseFloat(AINS_OTHERINCOME));	//業報
//					$scope.inputVO.PROPOSER_INCOME2	= (parseFloat(D000069_S_INFITEM_20) * 10000 + parseFloat(D000069_AINS_OTHERINCOME) * 10000).toFixed();	//財告
				}
			}

			//被保人工作年收入(業報)/(財告)
			if($scope.inputVO.INSURED_ID == $scope.webservice.Result.I_ID) {	//被保人同電子要保書回傳資料才計算
				$scope.inputVO.INSURED_INCOME1	= (parseFloat(S_INFITEM_08) + parseFloat(S_INFITEM_07));	//業報
//				$scope.inputVO.INSURED_INCOME2	= (parseFloat(D000069_S_INFITEM_08) * 10000 + parseFloat(D000069_S_INFITEM_07) * 10000).toFixed();	//財告
			}
		}

		//webservice date民國年月日轉date.getTime
		$scope.webserviceDatechange = function(date){
			var year = parseInt(date.substr(0,3))+1911;
			var month = date.substr(3,2);
			var day = date.substr(5,2);
			var redate = new Date(year+'-'+month+'-'+day).getTime();
			return redate;
		}

		//由其他保險文件編號複製說明(改為輸入購買檢核編號，複製說明)
		$scope.copyInsIdData = function() {
			debugger
			if($scope.chkCopyInsId && $scope.copyInsId != null && $scope.copyInsId != undefined && $scope.copyInsId != "") {
				$scope.inputVO.copyInsId = $scope.copyInsId;
				$scope.sendRecv("IOT110", "getCopyInsData", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
    				function(tota, isError) {
    					if (!isError) {
    						if(tota[0].body.resultList == null || tota[0].body.resultList.length == 0) {
							} else {
								$scope.inputVO.INCOME_REMARK = tota[0].body.resultList[0].INCOME_REMARK;
								$scope.inputVO.LOAN_SOURCE_REMARK = tota[0].body.resultList[0].LOAN_SOURCE_REMARK;
							}
    					}
				});
			}
		}

		//要保書申請日期變更
		$scope.applyDateChg = function() {
			//重新取得要保人、被保人、繳款人保單貸款檢核、行內貸款檢核、定存不打折檢核
			//以及招攬人員資料(公平待客、高齡完訓資格)
			debugger;
			$scope.getInfo("CUST");
			$scope.getInfo("INSURED");
			$scope.getInfo("PAYER");
			$scope.getInfo("RECRUIT");
		}

		$scope.chgOthType = function() {
			if($scope.inputVO.OTH_TYPE != '1') {
				$scope.inputVO.CASE_ID = '';
				$scope.inputVO.INS_ID = '';
			}
		}

		$scope.chgRltBtPropay = function() {
			if($scope.rltBtPropayOnChange && $scope.inputVO.RLT_BT_PROPAY == "1") {	//繳款人=要保人
				debugger
				$scope.inputVO.PAYER_ID = $scope.inputVO.CUST_ID;
				//重新取得繳款人保單貸款檢核、行內貸款檢核、定存不打折檢核
				$scope.getInfo("PAYER");
			} else if($scope.rltBtPropayOnChange && $scope.inputVO.RLT_BT_PROPAY == "2") {	//繳款人=被保人
				$scope.inputVO.PAYER_ID = $scope.inputVO.INSURED_ID;
				//重新取得繳款人保單貸款檢核、行內貸款檢核、定存不打折檢核
				$scope.getInfo("PAYER");
			} else {
				$scope.rltBtPropayOnChange = true;
			}
		}

		//保單號碼
		$scope.POLICY_NO_Query = function(){
			debugger
			if($scope.inputVO.POLICY_NO1 != ''){
				$scope.inputVO.POLICY_NO1 = $scope.inputVO.POLICY_NO1.toUpperCase();
			}
			if($scope.inputVO.POLICY_NO2 != ''){
				$scope.inputVO.POLICY_NO2 = $scope.inputVO.POLICY_NO2.toUpperCase();
			}
			if($scope.inputVO.POLICY_NO3 != ''){
				$scope.inputVO.POLICY_NO3 = $scope.inputVO.POLICY_NO3.toUpperCase();
			}
			if($scope.inputVO.POLICY_NO1 != '' && $scope.inputVO.POLICY_NO2 != '') {
				$scope.sendRecv("IOT130", "POLICY_NO_Query", "com.systex.jbranch.app.server.fps.iot130.IOT130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.POLICY_Data.length>0){
								$scope.inputVO.CUST_ID = tota[0].body.POLICY_Data[0].CUST_ID;
								$scope.inputVO.INSURED_ID = tota[0].body.POLICY_Data[0].INS_ID;
								$scope.inputVO.INSPRD_ID = tota[0].body.POLICY_Data[0].INS_TYPE;
								$scope.inputVO.INSPRD_NAME = tota[0].body.POLICY_Data[0].POLICY_FULL_NAME;
								$scope.inputVO.PRODUCT_TYPE = tota[0].body.POLICY_Data[0].INSPRD_TYPE;
								$scope.inputVO.INS_COM_NAME = tota[0].body.POLICY_Data[0].INS_COM_NAME;
								$scope.getInfo('INSURED');
								if($scope.inputVO.REG_TYPE == '2') $scope.getInfo('CUST'); //契約變更_基金標的異動：才需有要保人資料

							}
						}
			   		});
			}
		}


		// init
		$scope.init = function() {
			$scope.viewOnly = false;
			$scope.noUpdate = false;
			$scope.caseIdDisabled = false;
			$scope.brhmgrAuthing = false;
			$scope.brhmgrAllowDeny = false;
			$scope.branch_cust = false;
			$scope.branch_represet = false;
			$scope.underSeverYear = false;
			$scope.checkseveryear = false;
			$scope.Investment = false;
			$scope.invalid = false;		//WMS-CR-20180305-01_新系統新增設控保險業務員資格超過法定換證期限未換證，不允許招攬保險之檢核
			$scope.regType3AuthDisabled = false;

			$scope.chkSameCustId = false;
			$scope.chkCopyInsId = false;
			$scope.copyInsId = undefined;
			$scope.chkSameInsuredId = false;
//			$scope.empId = sysInfoService.getUserID();
//			$scope.loginBranchId = sysInfoService.getBranchID();
			$scope.loginID = sysInfoService.getUserID();
			$scope.AvailBranchList = sysInfoService.getAvailBranch();
			$scope.RoleID = sysInfoService.getRoleID();
			$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();
			$scope.checkFatca = '';
			$scope.checkCost = false;
			$scope.savePass = false;
			$scope.isInNoChkLoan = false;
			$scope.rltBtPropayOnChange = true;
			$scope.fromIOT111 = false;
			$scope.chgCustPrintCheck = false;
			$scope.validSeniorRec = false;	//高齡非常態錄音檢核

			//權限ID
			$scope.privilegeId = sysInfoService.getPriID();
			debugger
			var findKeyinRole = $filter('filter')($scope.mappingSet['IOT.PREMATCH_KEYIN_ROLE'], {DATA: $scope.privilegeId[0]});
			var findAuthRole = $filter('filter')($scope.mappingSet['IOT.PREMATCH_AUTH_ROLE'], {DATA: $scope.privilegeId[0]}); //包含UHRM主管角色
			$scope.privilegeGroup = (findKeyinRole != null && findKeyinRole != undefined && findKeyinRole.length > 0) ? "FC" :
										((findAuthRole != null && findAuthRole != undefined && findAuthRole.length > 0) ? "BRHMGR" : "OTH");

			var findAMLAuthRole = $filter('filter')($scope.mappingSet['IOT.PREMATCH_AML_AUTH_ROLE'], {DATA: $scope.privilegeId[0]}); //包含UHRM主管角色
			$scope.isAMLAuthRole = (findAMLAuthRole != null && findAMLAuthRole != undefined && findAMLAuthRole.length > 0) ? true : false;

			//是否為總行人員角色
			var findHeadMgr = $filter('filter')($scope.mappingSet['FUBONSYS.HEADMGR_ROLE'], {DATA: $scope.RoleID});
			$scope.isHeadMgr = (findHeadMgr != null && findHeadMgr != undefined && findHeadMgr.length > 0) ? true : false;
			
			$scope.inputVO = {
				REG_TYPE: '',
				OTH_TYPE: '',
				PREMATCH_SEQ: '',
				CASE_ID: '',
				CUST_ID : '',
				BRANCH_NBR: '',
				PROPOSER_NAME : '',
				PROPOSER_BIRTH: undefined,
				PROPOSER_CM_FLAG: '',
				REPRESET_ID: '',
				REPRESET_NAME: '',
				REPRESET_CM_FLAG: '',
				CUST_RISK: '',
				CUST_RISK_DUE: undefined,
				RLT_BT_PROREP: '',
				INSURED_ID: '',
				INSURED_NAME: '',
				INSURED_CM_FLAG: '',
				INSURED_BIRTHDAY: undefined,
				PAYER_ID: '',
				PAYER_NAME: '',
				PAYER_CM_FLAG: '',
				RLT_BT_PROPAY: '',
				INSPRD_KEYNO: undefined,
				INSPRD_ID: '',
				INSPRD_NAME: '',
				PAY_TYPE: '',
				INSPRD_ANNUAL: '',
				REAL_PREMIUM: undefined,
				BASE_PREMIUM: undefined,
				MOP2: '',
				PRD_RATE: undefined,
				CNR_REATE: undefined,
				EXCH_RATE: undefined,
				AB_EXCH_RATE: undefined,
				CURR_CD: undefined,
				PRODUCT_TYPE: '',
				LOAN_SOURCE_YN: '',
				LOAN_SOURCE2_YN: '',
				APPLY_DATE: new Date(),
				CONTRACT_END_YN: '',
				S_INFITEM_LOAN_YN: '',
				PROPOSER_WORK: '',
				INSURED_WORK: '',
				RECORDING_OLD: '',
				C_LOAN_APPLY_DATE: null,
				C_PREM_DATE: null,
				I_LOAN_APPLY_DATE: null,
				P_LOAN_APPLY_DATE: null,
				C_LOAN_APPLY_YN: '',
				I_LOAN_APPLY_YN: '',
				P_LOAN_APPLY_YN: '',
				POLICY_NO1:'',
				POLICY_NO2:'',
				POLICY_NO3:'',
				AML: '',
				PRECHECK: '',
				hnwcData: undefined,
				PROPOSER_INCOME1: undefined,
//				PROPOSER_INCOME2: undefined,
				PROPOSER_INCOME3: undefined,
				INSURED_INCOME1: undefined,
//				INSURED_INCOME2: undefined,
				INSURED_INCOME3: undefined,
				LOAN_CHK1_YN: '',
				LOAN_CHK2_YN: '',
				LOAN_CHK2_DATE: null, //繳款人行內貸款最近撥貸日
				LOAN_CHK3_YN: '',
				LOAN_CHK4_YN: '', //額度式貸款檢核
				P_REVOLVING_LOAN_YN: '',
				CD_CHK_YN: '',
				P_CD_DUE_DATE: null,
				C_LOAN_CHK1_YN: '',
				C_LOAN_CHK2_YN: '',
				C_LOAN_CHK2_DATE: null, //要保人行內貸款最近撥貸日
				C_LOAN_CHK3_YN: '',
				C_LOAN_CHK4_YN: '', //額度式貸款檢核
				C_REVOLVING_LOAN_YN: '',
				C_CD_CHK_YN: '',
				C_CD_DUE_DATE: null,
				I_LOAN_CHK1_YN: '',
				I_LOAN_CHK2_YN: '',
				I_LOAN_CHK2_DATE: null, //被保人行內貸款最近撥貸日
				I_LOAN_CHK3_YN: '',
				I_LOAN_CHK4_YN: '', //額度式貸款檢核
				I_REVOLVING_LOAN_YN: '',
				I_CD_CHK_YN: '',
				I_CD_DUE_DATE: null,
				INCOME_REMARK: '',
				LOAN_SOURCE_REMARK: '',
				CHG_CUST_ID: '',
				CHG_PROPOSER_NAME: '',
				CHG_PROPOSER_BIRTH: undefined,
				AB_SENIOR_YN: '',
				C_SALE_SENIOR_YN: '',
				I_SALE_SENIOR_YN: '',
				P_SALE_SENIOR_YN: '',
				AGE_UNDER20_YN: '',
				STATUS: '',	//1:暫存 2:覆核中 3:核可 4:退件
				MATCH_DATE: new Date(),
				RECRUIT_ID: sysInfoService.getUserID(),
				empId: sysInfoService.getUserID(),
				MAPPVIDEO_YN: 'N', 	//是否為電子要保書視訊投保(Y/N)
				MAPP_CHKLIST_TYPE: "",	//視訊投保檢核項目類型(覆核層級)
				INV_SCORE: "",			//投資屬性問卷分數
				C_KYC_INCOME: "",		//要保人行內KYC收入
				I_KYC_INCOME: "",		//被保人行內KYC收入
				CUST_DEBIT: undefined,		//要保人行內負債(不含信用卡)
				INSURED_DEBIT: undefined,	//被保人行內負債(不含信用卡)
				SENIOR_AUTH_OPT: '', //高齡銷售過程錄音覆核選項
				SENIOR_AUTH_OPT2: '',
				SENIOR_AUTH_OPT3: '',
				SENIOR_AUTH_OPT4: '',
				SENIOR_AUTH_REMARKS: '',
				SENIOR_AUTH_ID: '',
				C_SALE_SENIOR_TRANSSEQ: '', //要保人高齡銷售過程錄音序號
				MAPPVIDEO_AGENTMEMO: '',	//理專補充說明：視訊投保不通過原因
				DIGITAL_AGREESIGN_YN: '',	//Y:線上簽署行動投保服務同意書案件
				INVESTList: [],
				FB_COM_YN: 'Y', //預設為富壽
				COMPANY_NUM:'82',
				CANCEL_CONTRACT_YN: 'N', //契撤案件
				DATA_SHR_YN: 'N', 	//人壽資料分享案件
				UHRM_CASE: 'N', 		//UHRM人員鍵機或UHRM為招攬人員的案件
				CUST_INS_ASSET: undefined, //要保人保險庫存金額
				BUSINESS_REL: "" //新增業務關係
			};

			$scope.getInfo('RECRUIT');
			
			debugger
			if($scope.connector('get', 'IOT_PREMATCH_SEQ')){
				$scope.inputVO.PREMATCH_SEQ = $scope.connector('get', 'IOT_PREMATCH_SEQ');
				$scope.connector('set', 'IOT_PREMATCH_SEQ', null);
				$scope.fromIOT111 = true;
			}

			if($scope.inputVO.PREMATCH_SEQ == null || $scope.inputVO.PREMATCH_SEQ == undefined || $scope.inputVO.PREMATCH_SEQ == '') {
				$scope.inputVO.PREMATCH_SEQ = "";
				$timeout(function(){$scope.inquireCompanyFB();},700);
			} else {
				$scope.query();
			}
		};


		$scope.back_page_btn = function(){
			$scope.connector('set','IOT111_updateSubmit',true);
		    $rootScope.menuItemInfo.url = "assets/txn/IOT111/IOT111.html";
		};

		//XML參數
		getParameter.XML(["IOT.CM_FLAG", "IOT.NO_CHK_LOAN_INSPRD", "IOT.INV_PRD_LOAN_KYC", "IOT.PREMATCH_KEYIN_ROLE", "IOT.PREMATCH_AUTH_ROLE",
		                  "FUBONSYS.HEADMGR_ROLE","IOT.PREMATCH_AML_AUTH_ROLE", "IOT.MAPPVIDEO_CHK_STEP", "IOT.MAPPVIDEO_CHK_CODE", "IOT.NO_SENIOR_CERTIFICATE_EMP"],function(totas){
			if(totas){
				$scope.mappingSet['IOT.CM_FLAG'] = totas.data[totas.key.indexOf('IOT.CM_FLAG')];						//戶況檢核
				$scope.mappingSet['IOT.NO_CHK_LOAN_INSPRD'] = totas.data[totas.key.indexOf('IOT.NO_CHK_LOAN_INSPRD')];	//不須檢核貸款險種代號
				$scope.mappingSet['IOT.INV_PRD_LOAN_KYC'] = totas.data[totas.key.indexOf('IOT.INV_PRD_LOAN_KYC')];		//投資型貸款風險等級參數
				$scope.mappingSet['IOT.PREMATCH_KEYIN_ROLE'] = totas.data[totas.key.indexOf('IOT.PREMATCH_KEYIN_ROLE')];//保險進件購買檢核可鍵機角色Privilege
				$scope.mappingSet['IOT.PREMATCH_AUTH_ROLE'] = totas.data[totas.key.indexOf('IOT.PREMATCH_AUTH_ROLE')];	//保險進件購買檢核可覆核角色Privilege
				$scope.mappingSet['IOT.PREMATCH_AML_AUTH_ROLE'] = totas.data[totas.key.indexOf('IOT.PREMATCH_AML_AUTH_ROLE')];	//保險進件購買檢核AML高可覆核角色Privilege
				$scope.mappingSet['FUBONSYS.HEADMGR_ROLE'] = totas.data[totas.key.indexOf('FUBONSYS.HEADMGR_ROLE')];	//總行權限角色
				$scope.mappingSet['IOT.MAPPVIDEO_CHK_STEP'] = totas.data[totas.key.indexOf('IOT.MAPPVIDEO_CHK_STEP')];
				$scope.mappingSet['IOT.MAPPVIDEO_CHK_CODE'] = totas.data[totas.key.indexOf('IOT.MAPPVIDEO_CHK_CODE')];
				$scope.mappingSet['IOT.NO_SENIOR_CERTIFICATE_EMP'] = totas.data[totas.key.indexOf('IOT.NO_SENIOR_CERTIFICATE_EMP')];

				$scope.init();
			}
		});

		$scope.openMappVideoList = function() {
//			if(!$scope.inputVO.CASE_ID) {
//				$scope.showErrorMsgInDialog("請輸入案件編號");
//				return;
//			}
			var caseid = $scope.inputVO.CASE_ID;	//J600000071

			var dialog = ngDialog.open({
				template: 'assets/txn/IOT930/IOT930.html',
				className: 'IOT930',
				controller:['$scope',function($scope){
					$scope.CASE_ID = caseid;
				}]
			});
			dialog.closePromise.then(function(data){
				//
			});
		}

		//影像品質檢核項目
		$scope.openMappVideoChkItems = function() {
			debugger
//			$scope.inputVO.PREMATCH_SEQ = "2202150391";

			//新案件若無購買檢核編碼，則須先取得
			if($scope.inputVO.PREMATCH_SEQ == undefined || $scope.inputVO.PREMATCH_SEQ == null || $scope.inputVO.PREMATCH_SEQ == '') {
				$scope.sendRecv("IOT110", "getPrematchSeq", "com.systex.jbranch.app.server.fps.iot110.IOT110InputVO", $scope.inputVO,
					function(tota, isError) {
					debugger
						if (!isError) {
							$scope.inputVO.PREMATCH_SEQ = tota[0].body.prematchSeq;

							$scope.openMVCItemDialog();
						}
				});
			} else {
				$scope.openMVCItemDialog();
			}
		}

		//影像品質檢核項目
		$scope.openMVCItemDialog = function() {
			debugger
			var pseq = $scope.inputVO.PREMATCH_SEQ;
			var custId = $scope.inputVO.CUST_ID;
			var insId = $scope.inputVO.INS_ID;
			var agentMemo = $scope.inputVO.MAPPVIDEO_AGENTMEMO;
			var chkType = $scope.inputVO.MAPP_CHKLIST_TYPE; //總行質檢
			var stepList = $scope.mappingSet['IOT.MAPPVIDEO_CHK_STEP'];
			var codeList = $scope.mappingSet['IOT.MAPPVIDEO_CHK_CODE'];
			var ansList = [];

			for (var i = 0; i < stepList.length; i++) {
				for (var j = 0; j < codeList.length; j++) {
					var ans = [];
					ans.DATA = stepList[i].DATA+codeList[j].DATA; // ["11", "12", "13", "21", "22", "23", "31", ....]
					ans.LABEL = stepList[i].LABEL+codeList[j].LABEL;
					ansList.push(ans);
				}
			}

			var dialog = ngDialog.open({
				template: 'assets/txn/IOT940/IOT940.html',
				className: 'IOT940',
				controller:['$scope',function($scope){
					$scope.PREMATCH_SEQ = pseq;
					$scope.CUST_ID = custId;
					$scope.INS_ID = insId;
					$scope.CHKLIST_TYPE = chkType;
					$scope.CHK_STEP_LIST = stepList;
					$scope.CHK_CODE_LIST = codeList;
					$scope.CHK_ANS_LIST = ansList;
					$scope.AGENT_MEMO = agentMemo;
				}]
			});
			dialog.closePromise.then(function(data){
				//
			});
		}

		//基金適配作業頁面
		$scope.openFundLinkDialog = function(funCode, chkStatus) {
			debugger
			if($scope.inputVO.REG_TYPE == "1" && $scope.inputVO.CUST_OVER_70 != "Y" && 
					$scope.inputVO.hnwcData && $scope.inputVO.hnwcData.validHnwcYN == "Y" && 
					$scope.inputVO.hnwcData.hnwcService == "Y" && $scope.inputVO.hnwcData.spFlag != "Y") {
				//新契約且非高齡(70歲以上)且為高資產客戶且非高資產特定客戶
				//不須檢核高齡64.5歲以上有無投資經驗
				$scope.inputVO.C_SENIOR_PVAL = "";	
				
				if($scope.inputVO.CUST_RISK == "C4") {
					//C4等級(不用越級)，開啟一般適配視窗
					$scope.openNormalFundLinkDialog(funCode, chkStatus);
				} else {
					//非C4等級，可越級適配
					$scope.openHNWCFundLinkDialog(funCode, chkStatus);
				}
			} else {
				//其他，開啟一般適配視窗
				$scope.openNormalFundLinkDialog(funCode, chkStatus);
			}
		}
		
		//一般(非高資產)基金適配作業頁面
		$scope.openNormalFundLinkDialog = function(funCode, chkStatus) {
			debugger
			//列出投資型連結標的清單
			if($scope.inputVO.PRODUCT_TYPE && $scope.inputVO.PRODUCT_TYPE != '1' && $scope.inputVO.CUST_ID && $scope.inputVO.INSPRD_ID) {
				//
				if(!$scope.inputVO.INVESTList || $scope.inputVO.INVESTList.length <= 0) {
	    			$scope.inputVO.custID = $scope.inputVO.CUST_ID;//因為IOT920inpuVO的custid名稱與inputVO.CUST_ID名稱不同
					$scope.sendRecv("IOT920","Get_List","com.systex.jbranch.app.server.fps.iot920.InsFundListInputVO",
						$scope.inputVO,function(tota,isError){
							if(!isError) {
								$scope.inputVO.INVESTList = tota[0].body.INVESTList;
								$scope.openIOT950(funCode, chkStatus);
							}
					});
				} else {
					$scope.openIOT950(funCode, chkStatus);
				}
    		}
		}
		
		//一般(非高資產)基金適配清單
		$scope.openIOT950 = function(funCode, chkStatus) {
			$scope.inputVO.FROM_ID = 'IOT110';
			$scope.inputVO.privilegeGroup = $scope.privilegeGroup;
			var inputVO = $scope.inputVO;

			var dialog = ngDialog.open({
				template: 'assets/txn/IOT950/IOT950.html',
				className: 'IOT950',
				controller:['$scope',function($scope){
					$scope.inputVO = inputVO;
				}]
			});
			dialog.closePromise.then(function(data){
				debugger
				if(data.value != undefined && data.value != 'cancel') {
					if(funCode == '1') {
						debugger
						$scope.inputVO.INVESTList = data.value;
						//壽險新契約儲存
						$scope.saveDataRegType1(chkStatus);
					} else if(funCode == '2') {
						debugger
						$scope.inputVO.INVESTList = data.value;
						//契約變更-基金標的異動儲存
						$scope.saveDataRegType2(chkStatus);
					}
				}
			});
		}

		//高資產投組適配作業
		$scope.openHNWCFundLinkDialog = function(funCode, chkStatus) {
			//列出投資型連結標的清單
			if($scope.inputVO.PRODUCT_TYPE && $scope.inputVO.PRODUCT_TYPE != '1' && $scope.inputVO.CUST_ID && $scope.inputVO.INSPRD_ID) {
				$scope.openIOT960(funCode, chkStatus);
			}
		}

		//高資產投組適配清單
		$scope.openIOT960 = function(funCode, chkStatus) {
//			funCode = "1";
//			chkStatus = "1";
			$scope.inputVO.FROM_ID = 'IOT110';
			$scope.inputVO.privilegeGroup = $scope.privilegeGroup;
			var inputVO = $scope.inputVO;

			var dialog = ngDialog.open({
				template: 'assets/txn/IOT960/IOT960.html',
				className: 'IOT960',
				controller:['$scope',function($scope){
					$scope.inputVO = inputVO;
				}]
			});
			dialog.closePromise.then(function(data){
				debugger
				if(data.value != undefined && data.value != 'cancel') {
					if(funCode == '1') { //新契約
						debugger
						$scope.inputVO.INVESTList = data.value.INVESTList;
						$scope.inputVO.SENIOR_OVER_PVAL = data.value.SENIOR_OVER_PVAL;
						$scope.inputVO.WMSHAIA_SEQ = data.value.WMSHAIA_SEQ;
						$scope.inputVO.PROD_CURR_RATE = data.value.PROD_CURR_RATE;
						//越級適配資料，for高資產越級適配清單用
						$scope.inputVO.OVER_PVAL_AMT = data.value.OVER_PVAL_AMT;
						$scope.inputVO.OVER_PVAL_MAX_AMT = data.value.OVER_PVAL_MAX_AMT;
						//壽險新契約儲存
						$scope.saveDataRegType1(chkStatus);
					}
				}
			});
		}
		
		//檢視行動投保要保書
		//from110參數：
		//		Y:檢視電子要保書PDF 
		//		N:同IOT120做法，將PDF寫入TBIOT_MAPP_PDF中
		$scope.Doc_print = function(from110){
			if($scope.inputVO.CASE_ID != undefined && $scope.inputVO.CASE_ID != null && $scope.inputVO.CASE_ID != ""){
				$scope.inputVO.fromIOT110 = from110;
				$scope.sendRecv("IOT120", "Get_PdfInfo", "com.systex.jbranch.app.server.fps.iot120.IOT120InputVO", $scope.inputVO,
					function(totas, isError) {
		               	if (isError) {
		               		$scope.showErrorMsg(totas[0].body.msgData);
		               	}
					}
				);
			}else{
				$scope.showErrorMsg('請輸入案件編號');
			}
		}

		//進件來源變更
		$scope.fnComYnChange = function() {
			debugger
			//險種資料清空
			$scope.inputVO.INSPRD_ID = '';
			$scope.inputVO.INSPRD_NAME = '';
			$scope.inputVO.INSPRD_KEYNO = undefined;

			if($scope.inputVO.FB_COM_YN == 'Y') {
				$scope.inputVO.COMPANY_NUM = "82";
				$scope.inquireCompanyFB();
			} else {
				$scope.inputVO.COMPANY_NUM = "";
				$scope.inquireCompanyNFB();
			}
		}

		//保險公司變更
		$scope.companyChange = function() {
			//險種資料清空
			$scope.inputVO.INSPRD_ID = '';
			$scope.inputVO.INSPRD_NAME = '';
			$scope.inputVO.INSPRD_KEYNO = undefined;
		}
		
		//高齡銷售過程錄音覆核選項變更
		$scope.validateSeniorAuthOPT = function() {
			if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && $scope.inputVO.SENIOR_AUTH_OPT == "3") {
				$scope.showErrorMsgInDialog("契約撤銷件，才可點選");
				$scope.inputVO.SENIOR_AUTH_OPT = "";
			}
		}
		
		$scope.openCallReport = function() {
			var inputVO = [];
			inputVO.PREMATCH_SEQ = $scope.inputVO.PREMATCH_SEQ;
			
			if ($scope.inputVO.CASE_ID != undefined) {
				$scope.sendRecv("IOT400","checkCaseID","com.systex.jbranch.app.server.fps.iot400.IOT400InputVO", 
				{'CASE_ID': $scope.inputVO.CASE_ID, 'PREMATCH_SEQ': $scope.inputVO.PREMATCH_SEQ},
				function(tota, isError) {
					if (!isError) {
						debugger
						if (tota[0].body.resultList.length != 0) {
							$scope.showErrorMsg('相同案件編號案件已有電訪記錄，請確認!');
							return;
						} else {
							var dialog = ngDialog.open({
								template: 'assets/txn/IOT400/IOT400.html',
								className: 'IOT400',
								controller:['$scope',function($scope){
									$scope.inputVO = inputVO;
								}]
							});
							dialog.closePromise.then(function(data){
							});
						}
					}
				});
			} else {
				var dialog = ngDialog.open({
					template: 'assets/txn/IOT400/IOT400.html',
					className: 'IOT400',
					controller:['$scope',function($scope){
						$scope.inputVO = inputVO;
					}]
				});
				dialog.closePromise.then(function(data){
				});
			}
		}
		
		$scope.isStringEmpty = function(str) {
			if(str == undefined || str == null || str == "")
				return true;
			else
				false;
		}		
	});
