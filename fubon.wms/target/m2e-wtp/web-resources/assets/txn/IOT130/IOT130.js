/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT130Controller', function($rootScope, $scope,$filter, $controller, socketService, ngDialog, projInfoService,sysInfoService,getParameter,$timeout) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "IOT130Controller";

	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};

//	$scope.open_apply_date = function($event, elementOpened) {
//		$event.preventDefault();
//		$event.stopPropagation();
//		$scope.model[elementOpened] = !$scope.model[elementOpened];
//	};

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

    //要保書類型/戶況檢核
	getParameter.XML(["IOT.CM_FLAG", "IOT.PAYER_REL_PROPOSER", "IOT.NO_CHK_LOAN_INSPRD"],function(totas){
		if(totas){
			//戶況檢核
			$scope.mappingSet['IOT.CM_FLAG'] = totas.data[totas.key.indexOf('IOT.CM_FLAG')];
			$scope.mappingSet['IOT.PAYER_REL_PROPOSER'] = totas.data[totas.key.indexOf('IOT.PAYER_REL_PROPOSER')];
			$scope.mappingSet['IOT.NO_CHK_LOAN_INSPRD'] = totas.data[totas.key.indexOf('IOT.NO_CHK_LOAN_INSPRD')];
		}
	});

	//根據欄位帶出分行代碼、招攬人員姓名、要保人姓名、留存分行地址
	$scope.getInfo = function(in_column){
		debugger
		$scope.inputVO.in_column = in_column;
		if($scope.inputVO.in_column == 'RECRUIT'){
			if($scope.inputVO.RECRUIT_ID !=undefined){
				$scope.inputVO.RECRUIT_ID.toUpperCase();
			}
			$scope.EMP_NAME = '';
		}
		if($scope.inputVO.in_column == 'CUST'){
			if($scope.inputVO.CUST_ID != undefined){
				$scope.inputVO.CUST_ID = $scope.inputVO.CUST_ID.toUpperCase();
			}
			$scope.inputVO.PROPOSER_NAME = '';
			$scope.inputVO.PROPOSER_BIRDAY = undefined;
			$scope.inputVO.PROPOSER_CM_FLAG = '';
			if($scope.inputVO.reg_type == '4'){
				if($scope.inputVO.EX_CUST_ID != undefined){
					$scope.inputVO.EX_CUST_ID = $scope.inputVO.EX_CUST_ID.toUpperCase();
				}
				$scope.inputVO.EX_PROPOSER_NAME = '';
			}
		}
		if($scope.inputVO.in_column == 'INSURED'){
			if($scope.inputVO.INSURED_ID != undefined){
				$scope.inputVO.INSURED_ID = $scope.inputVO.INSURED_ID.toUpperCase();
			}
			$scope.inputVO.INSURED_NAME = '';
			$scope.inputVO.INSURED_CM_FLAG = '';
		}
		if($scope.inputVO.in_column == 'REPRESET'){
			if($scope.inputVO.REPRESET_ID != undefined){
				$scope.inputVO.REPRESET_ID = $scope.inputVO.REPRESET_ID.toUpperCase();
			}
			$scope.inputVO.REPRESET_NAME = '';
			$scope.inputVO.REPRESET_CM_FLAG = '';
		}
		if($scope.inputVO.in_column == 'PAYER'){
			$scope.inputVO.PAYER_NAME = '';
			$scope.inputVO.PAYER_CM_FLAG = '';
			$scope.inputVO.LOAN_CHK1_YN = '';
			$scope.inputVO.LOAN_CHK2_YN = '';
			$scope.inputVO.CD_CHK_YN = '';
		}

		if($scope.inputVO.reg_type == '4'){
			$scope.inputVO.CUST_ID = $scope.inputVO.EX_CUST_ID;
			//立約人帳號檢核
			if($scope.inputVO.REMARK_INS != ''){
				$scope.checkREMARK_INS();
			}
		}
		$scope.sendRecv("IOT920","getCUSTInfo","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",
				$scope.inputVO,function(tota,isError){
			debugger
			if(!isError){
				if(tota[0].body.EMP_NAME != null){
					if(tota[0].body.EMP_NAME.length>0){
						$scope.EMP_NAME = tota[0].body.EMP_NAME[0].EMP_NAME;
					}else{
						$scope.EMP_NAME = undefined;
					}
				}
				if(tota[0].body.CUST_NAME != null){
					if(tota[0].body.CUST_NAME.length>0){
						if($scope.inputVO.CUST_ID != ''){
							if($scope.inputVO.reg_type == '4'){
								$scope.inputVO.EX_PROPOSER_NAME = tota[0].body.CUST_NAME[0].CUST_NAME;
							}else{
								$scope.inputVO.PROPOSER_NAME = tota[0].body.CUST_NAME[0].CUST_NAME;
							}
							var birthday = $scope.toJsDate(tota[0].body.CUST_NAME[0].BIRTHDAY);
							$scope.inputVO.PROPOSER_BIRDAY = birthday;
							$scope.inputVO.AO_CODE = tota[0].body.CUST_NAME[0].AO_CODE;
							$scope.inputVO.PROPOSER_CM_FLAG = tota[0].body.CUST_NAME[0].PROPOSER_CM_FLAG;
							if($scope.inputVO.reg_type == '4')
								$scope.checkREMARK_INS();
						}
					}else{
						$scope.notBranchCust=true;
					}
				}
				if(tota[0].body.INSURED_NAME != null){
					if(tota[0].body.INSURED_NAME.length>0){
						if($scope.inputVO.INSURED_ID !=''){
							$scope.inputVO.INSURED_NAME = tota[0].body.INSURED_NAME[0].CUST_NAME;
							$scope.inputVO.INSURED_CM_FLAG = tota[0].body.INSURED_NAME[0].INSURED_CM_FLAG;
						}
					}
				}
				if(tota[0].body.REPRESETList != null){
					if(tota[0].body.REPRESETList.length>0){
						if($scope.inputVO.REPRESETList !=''){
							$scope.inputVO.REPRESET_NAME = tota[0].body.REPRESETList[0].CUST_NAME;
							$scope.inputVO.REPRESET_CM_FLAG = tota[0].body.REPRESETList[0].REPRESET_CM_FLAG;
							$scope.RepresetCust = true;
						}
					}
				}
				if(tota[0].body.COM_ADDRESS != null){
					if(tota[0].body.COM_ADDRESS.length>0){
						$scope.COM_ADDRESS = tota[0].body.COM_ADDRESS[0].COM_ADDRESS;
					}
				}
				if(tota[0].body.PAYERList != null){
					if(tota[0].body.PAYERList.length>0){
						if($scope.inputVO.PAYER_ID !=''){
							$scope.inputVO.PAYER_NAME = tota[0].body.PAYERList[0].CUST_NAME;
							$scope.inputVO.PAYER_CM_FLAG = tota[0].body.PAYERList[0].PAYER_CM_FLAG;
							$scope.inputVO.LOAN_CHK1_YN = tota[0].body.PAYERList[0].LOAN_CHK1_YN;
							$scope.inputVO.LOAN_CHK2_YN = tota[0].body.PAYERList[0].LOAN_CHK2_YN;
							$scope.inputVO.CD_CHK_YN = tota[0].body.PAYERList[0].CD_CHK_YN;
						}
					}
				}
			}else{
				if($scope.inputVO.in_column == 'CUST'){
					$scope.inputVO.CUST_ID = undefined;
					if($scope.inputVO.reg_type == '4'){
						$scope.inputVO.EX_CUST_ID = undefined;
					}
				}
				if($scope.inputVO.in_column == 'INSURED'){
					$scope.inputVO.INSURED_ID = undefined;
				}
				if($scope.inputVO.in_column == 'REPRESET'){
					$scope.inputVO.REPRESET_ID = undefined;
				}
			}
		});


	}

	//查詢修改資料
	$scope.retrieve_data = function(){
		$scope.sendRecv("IOT130","retrieve_data","com.systex.jbranch.app.server.fps.iot130.IOT130InputVO",
				$scope.inputVO,function(tota,isError){
					if(!isError){
						debugger
						$scope.INS_INFORMATION = tota[0].body.INS_INFORMATION;
						$scope.inputVO.reg_type = $scope.INS_INFORMATION[0].REG_TYPE;
						$scope.branchID = $scope.INS_INFORMATION[0].BRANCH_NBR;
						$scope.branchName = '';
						switch ($scope.inputVO.reg_type) {
						case '3':
							$scope.inputVO.FB_COM_YN = $scope.INS_INFORMATION[0].FB_COM_YN; //富壽:Y 非富壽:N
							//保險公司下拉選單
							if($scope.inputVO.FB_COM_YN == "N") {
								$timeout(function(){$scope.inquireCompanyNFB();},700);
							} else {
								$timeout(function(){$scope.inquireCompanyFB();},700);
							}
							$scope.inputVO.COMPANY_NUM = $scope.INS_INFORMATION[0].COMPANY_NUM; //保險公司代碼
							$scope.inputVO.INS_COM_NAME =$scope.INS_INFORMATION[0].INS_COM_NAME; //保險公司名稱
							$scope.elsefile=true;
							$scope.exchangefile=false;
							var dateTolong = new Date($scope.INS_INFORMATION[0].KEYIN_DATE);
							$scope.KEYIN_DATE = dateTolong.toLocaleString();
							$scope.inputVO.OTH_TYPE = $scope.INS_INFORMATION[0].OTH_TYPE;
							$scope.inputVO.temp_OTH_TYPE = $scope.INS_INFORMATION[0].OTH_TYPE;
							$scope.inputVO.DOC_KEYIN_DATE = $scope.toJsDate($scope.INS_INFORMATION[0].DOC_KEYIN_DATE);
							$scope.inputVO.POLICY_NO1 = $scope.INS_INFORMATION[0].POLICY_NO1;
							$scope.inputVO.POLICY_NO2 = $scope.INS_INFORMATION[0].POLICY_NO2;
							$scope.inputVO.POLICY_NO3 = $scope.INS_INFORMATION[0].POLICY_NO3;
							$scope.inputVO.CUST_ID = $scope.INS_INFORMATION[0].CUST_ID;
							$scope.inputVO.PROPOSER_NAME = $scope.INS_INFORMATION[0].PROPOSER_NAME;
							$scope.inputVO.PROPOSER_BIRDAY = $scope.toJsDate($scope.INS_INFORMATION[0].PROPOSER_BIRTH);
							$scope.inputVO.PROPOSER_CM_FLAG = $scope.INS_INFORMATION[0].PROPOSER_CM_FLAG;
							$scope.inputVO.INSURED_ID = $scope.INS_INFORMATION[0].INSURED_ID;
							$scope.inputVO.INSURED_NAME = $scope.INS_INFORMATION[0].INSURED_NAME;
							$scope.inputVO.INSURED_CM_FLAG = $scope.INS_INFORMATION[0].INSURED_CM_FLAG;
							$scope.inputVO.REPRESET_CM_FLAG = $scope.INS_INFORMATION[0].REPRESET_CM_FLAG;
							$scope.inputVO.REPRESET_ID = $scope.INS_INFORMATION[0].REPRESET_ID;
							$scope.inputVO.REPRESET_CUST = $scope.INS_INFORMATION[0].RLT_BT_PROREP;
							$scope.inputVO.INS_ID = $scope.INS_INFORMATION[0].INS_ID;
							$scope.inputVO.RECRUIT_ID = $scope.INS_INFORMATION[0].RECRUIT_ID;
							$scope.inputVO.AO_CODE = $scope.INS_INFORMATION[0].AO_CODE;
							$scope.inputVO.TERMINATED_INC = $scope.INS_INFORMATION[0].TERMINATED_INC;
							$scope.inputVO.INSPRD_ID = $scope.INS_INFORMATION[0].INSPRD_ID;
							$scope.inputVO.INSPRD_NAME = $scope.INS_INFORMATION[0].INSPRD_NAME;
							$scope.inputVO.STATUS = $scope.INS_INFORMATION[0].STATUS;
							$scope.inputVO.CNCT_NAME = $scope.INS_INFORMATION[0].INSPRD_NAME;
							$scope.inputVO.INSPRD_KEYNO = $scope.INS_INFORMATION[0].INSPRD_KEYNO;
							$scope.inputVO.editOTH_TYPE = false;
							$scope.inputVO.LOAN_PRD_YN = $scope.INS_INFORMATION[0].LOAN_PRD_YN;
							$scope.inputVO.QC_IMMI = $scope.INS_INFORMATION[0].QC_IMMI;
							$scope.inputVO.PREMATCH_SEQ = $scope.INS_INFORMATION[0].PREMATCH_SEQ;
							$scope.inputVO.GUILD_RPT_DATE = $scope.INS_INFORMATION[0].GUILD_RPT_DATE;
							$scope.inputVO.PREMIUM_TRANSSEQ = $scope.INS_INFORMATION[0].PREMIUM_TRANSSEQ;
							$scope.inputVO.QC_PROPOSER_CHG = $scope.INS_INFORMATION[0].QC_PROPOSER_CHG;
							$scope.inputVO.PREMIUM_USAGE = $scope.INS_INFORMATION[0].PREMIUM_USAGE;
							$scope.inputVO.PAY_WAY = $scope.INS_INFORMATION[0].PAY_WAY;
							$scope.inputVO.PAYER_ID = $scope.INS_INFORMATION[0].PAYER_ID;
							$scope.inputVO.RLT_BT_PROPAY = $scope.INS_INFORMATION[0].RLT_BT_PROPAY;
							$scope.inputVO.LOAN_SOURCE_YN = $scope.INS_INFORMATION[0].LOAN_SOURCE_YN;
							$scope.inputVO.APPLY_DATE = $scope.toJsDate($scope.INS_INFORMATION[0].APPLY_DATE);
							$scope.inputVO.AML = $scope.INS_INFORMATION[0].AML;
							$scope.inputVO.PRECHECK = $scope.INS_INFORMATION[0].PRECHECK;
							$scope.inputVO.LOAN_CHK1_YN = $scope.INS_INFORMATION[0].LOAN_CHK1_YN;
							$scope.inputVO.LOAN_CHK2_YN = $scope.INS_INFORMATION[0].LOAN_CHK2_YN;
							$scope.inputVO.CD_CHK_YN = $scope.INS_INFORMATION[0].CD_CHK_YN;
							$scope.inputVO.VALID_CHG_CUST_YN = $scope.INS_INFORMATION[0].VALID_CHG_CUST_YN;
							$scope.inputVO.CHG_CUST_ID = $scope.INS_INFORMATION[0].CHG_CUST_ID;
							$scope.inputVO.INVESTList = tota[0].body.INVESTList;
							$scope.inputVO.REVISE_CONFIRM_YN = $scope.INS_INFORMATION[0].REVISE_CONFIRM_YN;

							if($scope.INS_INFORMATION[0].INSPRD_TYPE != 1){
								$scope.MatchButton=false;
							}
							$scope.getInfo('RECRUIT');
							$scope.getInfo('PAYER');
							break;
						case '4':
							$scope.elsefile=false;
							$scope.exchangefile=true;
							var dateTolong = new Date($scope.INS_INFORMATION[0].KEYIN_DATE);
							$scope.KEYIN_DATE = dateTolong.toLocaleString();
							$scope.inputVO.INS_ID = $scope.INS_INFORMATION[0].INS_ID;
							$scope.inputVO.RECRUIT_ID = $scope.INS_INFORMATION[0].RECRUIT_ID;
							$scope.inputVO.EX_CUST_ID = $scope.INS_INFORMATION[0].CUST_ID;
							$scope.inputVO.EX_PROPOSER_NAME = $scope.INS_INFORMATION[0].PROPOSER_NAME;
							$scope.inputVO.CONTRAT_DATE = $scope.toJsDate($scope.INS_INFORMATION[0].APPLY_DATE);
							$scope.inputVO.FX_PROD = $scope.INS_INFORMATION[0].FXD_KEYNO;
							$scope.inputVO.tempFX_PROD = $scope.INS_INFORMATION[0].FXD_KEYNO;
							$scope.inputVO.PROD_PERIOD = $scope.INS_INFORMATION[0].FX_PROD;
							$scope.inputVO.REMARK_INS = $scope.INS_INFORMATION[0].REMARK_INS;
							$scope.inputVO.AO_CODE = $scope.INS_INFORMATION[0].AO_CODE;
							$scope.inputVO.STATUS = $scope.INS_INFORMATION[0].STATUS;
							$scope.getInfo('RECRUIT');
							break;
						default:
							break;
						}
					}
		});
		$scope.connector('set','IOT140',undefined);
	}

	//欄位清除
	$scope.clear_cloum = function(){
		$scope.inputVO.INS_ID = '';
		$scope.inputVO.RECRUIT_ID = undefined;
		$scope.inputVO.OTH_TYPE = '';
		$scope.inputVO.DOC_KEYIN_DATE = undefined;
		$scope.inputVO.CUST_ID = undefined;
		$scope.inputVO.PROPOSER_NAME = '';
		$scope.inputVO.PROPOSER_BIRDAY = undefined;
		$scope.inputVO.PROPOSER_CM_FLAG = '';
		$scope.inputVO.INSURED_ID = undefined;
		$scope.inputVO.INSURED_NAME = '';
		$scope.inputVO.INSURED_CM_FLAG = '';
		$scope.inputVO.RECRUIT_ID = undefined;
		$scope.inputVO.REPRESET_ID = undefined;
		$scope.inputVO.REPRESET_NAME = '';
		$scope.inputVO.REPRESET_CUST = '';
		$scope.inputVO.REPRESET_CM_FLAG = '';
		$scope.inputVO.AO_CODE = '';
		$scope.inputVO.POLICY_NO1 = '';
		$scope.inputVO.POLICY_NO2 = '';
		$scope.inputVO.POLICY_NO3 = '';
		$scope.inputVO.INSPRD_KEYNO = '';
		$scope.inputVO.INSPRD_ID = undefined;
		$scope.inputVO.CNCT_NAME = '';
		$scope.inputVO.TERMINATED_INC = '';
		$scope.inputVO.STATUS = '20';
		$scope.inputVO.FX_PROD = '';
		$scope.inputVO.EX_CUST_ID = undefined;
		$scope.inputVO.EX_PROPOSER_NAME = '';
		$scope.inputVO.CONTRAT_DATE = undefined;
		$scope.inputVO.PROD_PERIOD = '';
		$scope.inputVO.REMARK_INS = '';
		$scope.inputVO.PROPOSER_RISK = '';
		$scope.inputVO.MATCH_DATE = undefined;
		$scope.inputVO.C_SENIOR_PVAL = '';
		$scope.inputVO.temp_OTH_TYPE = undefined;
		$scope.inputVO.tempFX_PROD = undefined;
		$scope.inputVO.FB_COM_YN = 'Y';

//		$scope.initCheckItems();
	}

	//保單號碼
	$scope.POLICY_NO_Query = function(){
		if($scope.inputVO.POLICY_NO1 != ''){
			$scope.inputVO.POLICY_NO1 = $scope.inputVO.POLICY_NO1.toUpperCase();
		}
		if($scope.inputVO.POLICY_NO2 != ''){
			$scope.inputVO.POLICY_NO2 = $scope.inputVO.POLICY_NO2.toUpperCase();
		}
		if($scope.inputVO.POLICY_NO3 != ''){
			$scope.inputVO.POLICY_NO3 = $scope.inputVO.POLICY_NO3.toUpperCase();
		}

		if($scope.inputVO.POLICY_NO1 != '' && $scope.inputVO.POLICY_NO2 != ''){
			if($scope.OPR_STATUS == 'new'){
				$scope.sendRecv("IOT130", "POLICY_NO_Query", "com.systex.jbranch.app.server.fps.iot130.IOT130InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.POLICY_Data.length>0){
									$scope.inputVO.CUST_ID = tota[0].body.POLICY_Data[0].CUST_ID;
									$scope.inputVO.PROPOSER_NAME = tota[0].body.POLICY_Data[0].APPL_NAME;
									$scope.inputVO.INSURED_ID = tota[0].body.POLICY_Data[0].INS_ID;
									$scope.inputVO.INSURED_NAME = tota[0].body.POLICY_Data[0].INS_NAME;
									$scope.getInfo('CUST');
									$scope.getInfo('INSURED');
									$scope.inputVO.INSPRD_ID = tota[0].body.POLICY_Data[0].INS_TYPE;
									$scope.inputVO.INS_COM_NAME = tota[0].body.POLICY_Data[0].INS_COM_NAME;
								}
							}
		   			});
			}
		}
	}

	//確認要保人小於7歲
	$scope.checkBirthday = function(){
		//僅有文件種類為契變-投資型轉換時才需要要保人id是否有開戶&確認要保人小於7歲
		if($scope.inputVO.OTH_TYPE == '5'){
			if($scope.notBranchCust){
				if(getAge($scope.inputVO.PROPOSER_BIRDAY)<7){
					$scope.underSevenYear=true;
					//法定代理人檢核欄位
					$scope.checkField.represet_id=true;
					$scope.checkField.represet_name=true;
					$scope.checkField.represet_cust=true;
					//小於7歲錯誤訊息
					$scope.showMsg("ehl_01_iot120_003");
				}else{
					$scope.underSevenYear=false;

					$scope.checkField.represet_id=false;
					$scope.checkField.represet_name=false;
					$scope.checkField.represet_cust=false;

					$scope.showMsg("ehl_01_iot120_004");
				}
			}
		}
	}

	//文件檢核清單
	$scope.Doc_qc = function(in_type){
		var in_type_data = '';
		if(in_type == 'in'){
			in_type_data='1';
		}
		if(in_type == 'out'){
			in_type_data='2'
		}
		var doc_qcList={
				in_type:in_type_data,
				INS_KEYNO:$scope.inputVO.INS_KEYNO,
				OPR_STATUS:$scope.OPR_STATUS,
				in_REGTYPE:$scope.in_REGTYPE,
				oth_type:$scope.inputVO.OTH_TYPE,
				INSPRD_ID:$scope.inputVO.INSPRD_ID
		}

		if($scope.OPR_STATUS != 'Read' && $scope.OPR_STATUS != 'new'){
			switch ($scope.inputVO.reg_type) {
			case '3':

				if($scope.inputVO.temp_OTH_TYPE != $scope.inputVO.OTH_TYPE){
					doc_qcList.OPR_STATUS='new';
					$scope.inputVO.editOTH_TYPE = true;
				}else{
					doc_qcList.OPR_STATUS='UPDATE';
					$scope.inputVO.editOTH_TYPE = false;
				}
				break;
			case '4':

				if($scope.inputVO.tempFX_PROD != $scope.inputVO.FX_PROD){
					doc_qcList.OPR_STATUS='new';
					$scope.inputVO.editOTH_TYPE = true;
				}else{
					doc_qcList.OPR_STATUS='UPDATE';
					$scope.inputVO.editOTH_TYPE = false;
				}

				break;
			default:
				break;
			}

		}

		var dialog = ngDialog.open({
			template: 'assets/txn/IOT900/IOT900.html',
			className: 'IOT900',
			controller:['$scope',function($scope){
				$scope.doc_qcList=doc_qcList;
			}]
		});

		dialog.closePromise.then(function(data){
			if(data.value == 'in'){
				$scope.checkin = true;
			}
			if(data.value == 'out'){
				$scope.checkout = true;
			}
		});
	}

	//基金變更異動
	$scope.openIOT950 = function() {
		$scope.inputVO.FROM_ID = 'IOT130';
		var inputVO = $scope.inputVO;

		var dialog = ngDialog.open({
			template: 'assets/txn/IOT950/IOT950.html',
			className: 'IOT950',
			controller:['$scope',function($scope){
				$scope.inputVO = inputVO;
			}]
		});
		dialog.closePromise.then(function(data){

		});
	}

	//文件種類選項重選清空險種
	$scope.check_othType = function(){
		$scope.inputVO.INSPRD_KEYNO = '';
		$scope.inputVO.INSPRD_ID = '';
		$scope.checkINSPRD_ID = '';
		$scope.inputVO.CNCT_NAME = '';
		$scope.MatchButton=true;
		//留存文件及送件文件
		$scope.checkin = false;
		$scope.checkout = false;
		//清除留存文件暫存
		$scope.connector('set','IOT900inListtemp','');
		$scope.connector('set','IOT900outListtemp','');
		if($scope.inputVO.OTH_TYPE == '5'){
			$scope.notCNCT = true;
		}else{
			$scope.notCNCT = false;
		}
		if($scope.inputVO.OTH_TYPE == '2'){
			$scope.inputVO.TERMINATED_INC = 'Y';
			$scope.inputVO.REVISE_CONFIRM_YN = "N";
			$scope.inputVO.notOthType2 = false;
		}else{
			$scope.inputVO.TERMINATED_INC = 'N';
			$scope.inputVO.REVISE_CONFIRM_YN = "N";
			$scope.inputVO.notOthType2 = true;
		}

		$scope.premiumUsageChange();
	}

	//查詢險種
	$scope.Ins_query = function(){
		if($scope.inputVO.RECRUIT_ID != undefined){
			if($scope.inputVO.CUST_ID != undefined && $scope.inputVO.DOC_KEYIN_DATE != undefined){
				if($scope.inputVO.COMPANY_NUM == "") {
					$scope.showErrorMsg('請先選擇保險公司');
					return;
				}

				var INSPRD_ID=$scope.inputVO.INSPRD_ID;
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
				dialog.closePromise.then(function(data){
					if(data.value != undefined){
						if(data.value != 'cancel'){
							//證照類型
							$scope.inputVO.CERT_TYPE = data.value.CERT_TYPE;
							//教育訓練
							$scope.inputVO.TRAINING_TYPE = data.value.TRAINING_TYPE;
							//送件人員員編
							$scope.inputVO.EMP_ID = $scope.inputVO.RECRUIT_ID;
							$scope.sendRecv("IOT920","chk_CertTraining","com.systex.jbranch.app.server.fps.iot920.chk_CTInputVO",
									$scope.inputVO,function(tota,isError){
										if(!isError){
											if(tota[0].body.Chk_Pass == 'Y'){
												debugger
//												$scope.cnctdataList = $scope.connector('get','IOT910');
												$scope.inputVO.INSPRD_KEYNO = data.value.INSPRD_KEYNO;
												$scope.inputVO.INSPRD_ID = data.value.INSPRD_ID;
												$scope.checkINSPRD_ID = data.value.INSPRD_ID;
												$scope.inputVO.CNCT_NAME = data.value.INSPRD_NAME;
//												$scope.inputVO.INS_COM_NAME = data.value.INS_COM_NAME;//保險公司名稱
//												$scope.inputVO.COMPANY_NUM = data.value.COMPANY_NUM;//保險公司代碼(若為非富壽，查詢時未帶入保險公司代碼需回壓)
												if(data.value.INSPRD_TYPE != '1'){
													//如果文件種類為5-契變
													if($scope.inputVO.OTH_TYPE == '5'){
														$scope.MatchButton=false;
														if($scope.inputVO.CUST_ID != undefined && $scope.inputVO.INSPRD_ID != undefined){
															$scope.sendRecv("IOT120","getInvPreMatch","com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
																	$scope.inputVO,function(tota,isError){
																	if(!isError){
																		if(tota[0].body.PreMatchList.length>0){
																			$scope.inputVO.PROPOSER_RISK = tota[0].body.PreMatchList[0].CUST_RISK;
																			$scope.inputVO.MATCH_DATE = tota[0].body.PreMatchList[0].MATCH_DATE;
																			$scope.inputVO.C_SENIOR_PVAL = tota[0].body.PreMatchList[0].C_SENIOR_PVAL;
																			$scope.inputVO.PREMATCH_SEQ = tota[0].body.PreMatchList[0].PREMATCH_SEQ;
																			$scope.inputVO.INVESTList = tota[0].body.INVESTList;
																		}else{
																			$scope.inputVO.INSPRD_KEYNO = '';
																			$scope.inputVO.INSPRD_ID = '';
																			$scope.checkINSPRD_ID = '';
																			$scope.inputVO.CNCT_NAME = '';
																			$scope.MatchButton=true;
																			$scope.inputVO.INVESTList = [];
																			$scope.showErrorMsg('ehl_01_iot120_006');
																		}
																	}
															});
														}
													}
												}else{
													$scope.MatchButton=true;
												}
//																			$scope.connector('set','IOT910','');
											}else{
												$scope.inputVO.INSPRD_KEYNO = '';
												$scope.inputVO.INSPRD_ID = '';
												$scope.checkINSPRD_ID = '';
												$scope.inputVO.CNCT_NAME = '';
												$scope.showErrorMsg('ehl_01_iot120_002')
											}
										}
								});
						}else{
							//查無險種或險種下市清空險種代碼
							 if($scope.inputVO.CNCT_NAME=='' &&  $scope.inputVO.INSPRD_KEYNO==''){
								 $scope.inputVO.INSPRD_ID = '';
							 }
						}
					}
				});
			}else{
				$scope.showErrorMsg('要保人ID或文件填寫申請日未填寫');
			}
		}else{
			$scope.showErrorMsg('請輸入送件人員員編');
		}
	}

	//確認險種代號是否與險種名稱一致
	$scope.checkCNCTData = function(){
		if($scope.checkINSPRD_ID != $scope.inputVO.INSPRD_ID){
			$scope.inputVO.INSPRD_KEYNO='';
			$scope.inputVO.CNCT_NAME='';
		}
	}

	//欄位檢核
	$scope.checkFieldData = function(){
		if($scope.inputVO.OTH_TYPE == '5'){
			if($scope.inputVO.INSPRD_ID=='' ||  $scope.inputVO.CNCT_NAME=='' ||  $scope.inputVO.INSPRD_KEYNO==''){
				$scope.notCNCT=true;
			}else{
				$scope.notCNCT=false;
			}
		}
		debugger
		if($scope.inputVO.OTH_TYPE == '2'){
			//2：契變-解約/縮小保額/部分提領(終止)
			if($scope.inputVO.REVISE_CONFIRM_YN != "Y") {
				$scope.showMsg("欄位檢核錯誤：*為必要輸入欄位,請輸入後重試");
				return false;
			}
		}
		if($scope.inputVO.RECRUIT_ID == undefined || $scope.inputVO.OTH_TYPE == '' || $scope.inputVO.DOC_KEYIN_DATE == undefined || $scope.inputVO.CUST_ID=='' ||
				$scope.inputVO.PROPOSER_NAME=='' || $scope.inputVO.INSURED_NAME == '' || $scope.inputVO.INSURED_NAME == undefined ||
				($scope.underSevenYear && ($scope.inputVO.REPRESET_ID=='' || $scope.inputVO.REPRESET_NAME=='' || $scope.inputVO.REPRESET_CUST=='')) ||
				$scope.notCNCT || ($scope.inputVO.OTH_TYPE == '2' && $scope.inputVO.TERMINATED_INC=='') ||
				$scope.inputVO.QC_PROPOSER_CHG == '' || $scope.inputVO.PREMIUM_USAGE == '' || $scope.inputVO.COMPANY_NUM == '' ||
				($scope.inputVO.PREMIUM_USAGE == "1" && ($scope.inputVO.PAY_WAY == "" || $scope.inputVO.APPLY_DATE == undefined ||
														 $scope.inputVO.LOAN_SOURCE_YN == "" || $scope.inputVO.PAYER_ID == "" || $scope.inputVO.RLT_BT_PROPAY == ""))) {
			$scope.showMsg("ehl_01_common_022");
			return false;
		}else{
			//僅有文件種類為契變-投資型轉換時才需要要保人id是否有開戶及7歲檢核
			if($scope.inputVO.OTH_TYPE == '5'){
				if(!$scope.notBranchCust || ($scope.notBranchCust && $scope.RepresetCust)){
//					return true;
				}else{
					if($scope.underSevenYear){
						$scope.showMsg("ehl_01_iot120_003");
						return false;
					}else{
						$scope.showMsg("ehl_01_iot120_004");
						return false;
					}
				}
			}
		}

		//有辦理要保人變更
		if($scope.inputVO.QC_PROPOSER_CHG == "Y") {
			//未輸入要保人適合度檢核編碼
			if($scope.inputVO.PREMATCH_SEQ == null || $scope.inputVO.PREMATCH_SEQ == undefined || $scope.inputVO.PREMATCH_SEQ == "") {
				$scope.showMsg("ehl_02_IOT130_002");
				return false;
			} else {
				if($scope.inputVO.VALID_CHG_CUST_YN != "Y") {
					$scope.showMsg("請確認變更申請書變更後要保人與適合度編碼變更後要保人是否一致");
					return false;
				}
			}
		}

		//有辦理要保人變更，購買檢核需主管覆核完成
		if($scope.inputVO.QC_PROPOSER_CHG == "Y" && $scope.inputVO.AUTH_YN != "Y") {
			$scope.showMsg("ehl_02_IOT130_001");
			return false;
		}

		//勾選•單筆增額/彈性繳費/保單還款
		if($scope.inputVO.PREMIUM_USAGE == "2") {
			//符合下述條件需輸入【保費來源錄音序號】，若未輸入則為"理專進件"
			// 定存打折檢核為Y且主約險種定義需檢核保費來源(險種代號不存在不需要錄音的參數表)
			// 保費支付來源為貸款且保單貸款檢核Y且主約險種定義需檢核保費來源(險種代號不存在不需要錄音的參數表)
			// 保費支付來源為貸款且行內貸款檢核Y且主約險種定義需檢核保費來源(險種代號不存在不需要錄音的參數表)
			var findPrd = $filter('filter')($scope.mappingSet['IOT.NO_CHK_LOAN_INSPRD'], {DATA: $scope.inputVO.INSPRD_ID});//不需要錄音的險種參數表
			var isInNoChkLoan = (findPrd != null && findPrd.length > 0) ? true : false;

			if(($scope.inputVO.CD_CHK_YN == 'Y' && !isInNoChkLoan)
						|| ($scope.inputVO.LOAN_SOURCE_YN == 'Y' && $scope.inputVO.LOAN_CHK1_YN == 'Y' && !isInNoChkLoan)
						|| ($scope.inputVO.LOAN_SOURCE_YN == 'Y' && $scope.inputVO.LOAN_CHK2_YN == 'Y' && !isInNoChkLoan)) {
				if($scope.inputVO.PREMIUM_TRANSSEQ == "") {
					$scope.showMsg("請輸入保費來源錄音序號");
					return false;
				} else if(!($scope.inputVO.PREMIUM_TRANSSEQ.length >= 8 && $scope.inputVO.PREMIUM_TRANSSEQ.substring(6, 8) == "73")) {
					$scope.showMsg("保費來源錄音序號有誤請與客服確認錄音序號");
					return false;
				}
			}
		}

		return true;
	}


	//基金變更異動檢核
	$scope.checkMatchList = function(){
		if($scope.inputVO.MatchList != undefined){
			if($scope.inputVO.MatchList.length<=0){
				$scope.inputVO.STATUS='10';
				$scope.showMsg("ehl_01_iot120_008");
			}
		}
	}

	//建機人員檢核
	$scope.checkData = function(){
		$scope.sendRecv("IOT130","chkData","com.systex.jbranch.app.server.fps.iot130.IOT130InputVO",$scope.inputVO,
				function(tota,isError){
			if(!isError){
				if(tota[0].body.chkData.length<=0){
					$scope.inputVO.STATUS='10';
					$scope.showMsg('ehl_01_iot120_002');
				}
			}
		});
	}

	$scope.New_record = function(){
		$scope.init();
		$scope.elsefile=true;
	}

	//商品檔期
	$scope.prdSchedule = function(){
		angular.forEach($scope.ExchangeRateList,function(row,index,objs){
			if($scope.inputVO.FX_PROD == row.DATA){
				$scope.inputVO.PROD_PERIOD = row.PROD_PERIOD;
			}
		});
	}

	//保險送件文件檢核 至少都要選擇一份文件
	$scope.checkInOutList = function() {
		var outCount = 0;

		angular.forEach($scope.inputVO.outList,function(row){
			if(row.DOC_CHK == "Y"){
				outCount++;
			}
		});

		return (outCount == 0 ? false : true);
	}

	$scope.Submit = function(){
		debugger
		$scope.inputVO.inList = $scope.connector('get','IOT900inList');
		$scope.inputVO.outList = $scope.connector('get','IOT900outList');
		if($scope.checkin && $scope.checkout && $scope.checkInOutList()){
			switch ($scope.inputVO.reg_type) {
			case '3':
					if($scope.checkFieldData()){
						if($scope.inputVO.OTH_TYPE == '5'){
							$scope.inputVO.MatchList = $scope.connector('get','IOT131');
							$scope.checkMatchList();
						}
						if($scope.inputVO.INSPRD_ID != "" && $scope.inputVO.INSPRD_KEYNO != ''){
							$scope.checkData();
						}
						$scope.inputVO.OPR_STATUS = $scope.OPR_STATUS;
						$scope.sendRecv("IOT130","submit","com.systex.jbranch.app.server.fps.iot130.IOT130InputVO",$scope.inputVO,
								function(tota,isError){
									if (isError) {
				                		$scope.showErrorMsg(tota[0].body.msgData);
				                	}
				                	if (tota.length > 0) {
				                		if($scope.OPR_STATUS == 'new'){
					                		$scope.nextData=false;
					                		$scope.saveData=true;
					                		$scope.OPR_STATUS='Read';
					                		$scope.read=true;
					                		$scope.inputVO.INS_KEYNO=tota[0].body.INSKEY_NO;
					                		$scope.inputVO.INS_ID = tota[0].body.INS_ID;
				                		}
				                		//清除留存文件暫存
				                		$scope.connector('set','IOT900inListtemp','');
				                		$scope.connector('set','IOT900outListtemp','');

				                		//清除基金變更異動
				                		$scope.connector('set','IOT131',undefined);

				                		if($scope.OPR_STATUS == 'UPDATE'){
					                		$scope.showSuccessMsg('ehl_01_common_002');
				            	        	$scope.connector('set','IOT140_updateSubmit',true);
				            	        	$rootScope.menuItemInfo.url = "assets/txn/IOT140/IOT140.html";
				                		}else{
					                		$scope.showSuccessMsg('ehl_01_common_001');
				                		}
				                	}
						});
					}
				break;
			case '4':
				$scope.inputVO.OPR_STATUS = $scope.OPR_STATUS;
				if($scope.inputVO.FX_PROD =='' || $scope.inputVO.EX_CUST_ID == undefined || $scope.inputVO.EX_PROPOSER_NAME == '' ||
						$scope.inputVO.CONTRAT_DATE == undefined || $scope.inputVO.REMARK_INS == ''){
					$scope.showErrorMsg('ehl_01_common_022');
				}else{
					if($scope.inputVO.REMARK_INS != ''){
						$scope.inputVO.REMARK_INS = $scope.inputVO.REMARK_INS.toUpperCase();
					}
					$scope.sendRecv("IOT130","EX_submit","com.systex.jbranch.app.server.fps.iot130.IOT130InputVO",$scope.inputVO,
							function(tota,isError){
								if (isError) {
			                		$scope.showErrorMsg(tota[0].body.msgData);
			                	}
			                	if (tota.length > 0) {
			                		if($scope.OPR_STATUS == 'new'){
				                		$scope.nextData=false;
				                		$scope.saveData=true;
				                		$scope.OPR_STATUS='Read';
				                		$scope.read=true;
				                		$scope.inputVO.INS_KEYNO=tota[0].body.INSKEY_NO;
				                		$scope.inputVO.INS_ID = tota[0].body.INS_ID;

			                		}
			                		//清除留存文件暫存
			                		$scope.connector('set','IOT900inListtemp','');
			                		$scope.connector('set','IOT900outListtemp','');
			                		if($scope.OPR_STATUS == 'UPDATE'){
				                		$scope.showSuccessMsg('ehl_01_common_002');
			            	        	$scope.connector('set','IOT140_updateSubmit',true);
			            	        	$rootScope.menuItemInfo.url = "assets/txn/IOT140/IOT140.html";
			                		}else{
				                		$scope.showSuccessMsg('ehl_01_common_001');
			                		}
			                	}
					});
				}
				break;
			default:
				break;
			}
		}else{
			$scope.showErrorMsg('請確認分行留存文件檢核或保險送件文件檢核');
		}

	}

	$scope.back_page_btn = function(){
		if($scope.OPR_STATUS == 'Read' || $scope.OPR_STATUS == 'UPDATE'){
        	$scope.connector('set','IOT140_updateSubmit',true);
        	$rootScope.menuItemInfo.url = "assets/txn/IOT140/IOT140.html";
		}
	}

	$scope.checkREMARK_INS = function(){
		$scope.sendRecv("IOT130","checkREMARK_INS","com.systex.jbranch.app.server.fps.iot130.IOT130InputVO",$scope.inputVO,
				function(tota,isError){
			if(isError){
				$scope.remark_insList = [];
			}
			if(tota[0].body.checkREMARK_INS){
				$scope.remark_insList = tota[0].body.checkREMARK_INS;
			}
			if(!tota[0].body.checkREMARK_INS){
				$scope.remark_insList = [];
				$scope.inputVO.REMARK_INS = '';
				$scope.showErrorMsg('ehl_01_IOT_003');
			}
		});
	}

	$scope.getPrematchData = function() {
		if($scope.inputVO.QC_PROPOSER_CHG == "Y" && $scope.inputVO.PREMATCH_SEQ != null && $scope.inputVO.PREMATCH_SEQ != undefined && $scope.inputVO.PREMATCH_SEQ != ''){
			$scope.sendRecv("IOT130","getPreMatchData","com.systex.jbranch.app.server.fps.iot130.IOT130InputVO", $scope.inputVO,function(tota,isError){
				if(!isError){
					debugger
					if(tota[0].body.PreMatchList[0] == null || tota[0].body.PreMatchList[0].length == 0) {
						$scope.inputVO.PREMATCH_SEQ = "";
						$scope.showErrorMsg('ehl_01_common_009');
						return;
					}

					$scope.prematchList = tota[0].body.PreMatchList[0];
					//將購買檢核資料放入頁面中
					$scope.putPrematchData();
				}else{
					$scope.initCheckItems();
				}
			});
		}
	}

	$scope.putPrematchData = function() {
		//將購買檢核資料放入頁面中
		$scope.inputVO.APPLY_DATE = $scope.toJsDate($scope.prematchList.APPLY_DATE);	//要保書填寫申請日

//		$scope.inputVO.PAYER_ID = $scope.prematchList.PAYER_ID;
//		$scope.getInfo('PAYER');
//		$scope.inputVO.RLT_BT_PROPAY = $scope.prematchList.RLT_BT_PROPAY;
//
		$scope.inputVO.AML = $scope.prematchList.AML;
		$scope.inputVO.PRECHECK = $scope.prematchList.PRECHECK;

		$scope.inputVO.LOAN_SOURCE_YN = $scope.prematchList.LOAN_SOURCE_YN;
		$scope.inputVO.LOAN_CHK1_YN = $scope.prematchList.LOAN_CHK1_YN;
		$scope.inputVO.LOAN_CHK2_YN = $scope.prematchList.LOAN_CHK2_YN;
		$scope.inputVO.CD_CHK_YN = $scope.prematchList.CD_CHK_YN;
		$scope.inputVO.AUTH_YN = $scope.prematchList.AUTH_YN;
		$scope.inputVO.CHG_CUST_ID = $scope.prematchList.CHG_CUST_ID;
	}

	//保險文件編號
	$scope.policyNo1Blur = function() {
		$scope.inputVO.POLICY_NO1 = $scope.inputVO.POLICY_NO1.toUpperCase();
	}

	//文件填寫申請日期變更
	$scope.docKeyinDateChg = function() {
		if($scope.inputVO.APPLY_DATE == null || $scope.inputVO.APPLY_DATE == undefined || $scope.inputVO.APPLY_DATE == "") {
			$scope.inputVO.APPLY_DATE = $scope.inputVO.DOC_KEYIN_DATE;
		}
	}

	//要保書申請日期變更
	$scope.applyDateChg = function() {
		//重新取得繳款人保單貸款檢核、行內貸款檢核、定存不打折檢核
		$scope.getInfo("PAYER");
	}

	$scope.premiumUsageChange = function() {
		//先清空繳費管道
		$scope.inputVO.PAY_WAY = '';
		//點選非首期保險費，則繳費管道就不用點選、繳款人ID、保費來源是否為貸款也不用拉選
		if($scope.inputVO.PREMIUM_USAGE == "3") {
			$scope.inputVO.PAY_WAY = "";
			$scope.inputVO.PAYER_ID = "";
			$scope.inputVO.PAYER_NAME = "";
			$scope.inputVO.PAYER_CM_FLAG = "";
			$scope.inputVO.RLT_BT_PROPAY = "";
			$scope.inputVO.LOAN_CHK1_YN = "";
			$scope.inputVO.LOAN_CHK2_YN = "";
			$scope.inputVO.CD_CHK_YN = "";
			$scope.inputVO.LOAN_SOURCE_YN = "";

			$scope.chgCustNotFirstPremium = true;
		} else {
			$scope.chgCustNotFirstPremium = false;
		}
	}

	$scope.initCheckItems = function() {
		$scope.inputVO.PREMATCH_SEQ = '';
		$scope.inputVO.AML = '';
		$scope.inputVO.PRECHECK = '';
		$scope.inputVO.AUTH_YN = '';
		$scope.inputVO.PREMIUM_USAGE = '3';
		$scope.inputVO.PAY_WAY = '';
		$scope.inputVO.PAYER_ID = '';
		$scope.inputVO.APPLY_DATE = undefined;
		$scope.inputVO.LOAN_SOURCE_YN = '';
		$scope.inputVO.RLT_BT_PROPAY = '';
		$scope.inputVO.LOAN_CHK1_YN = '';
		$scope.inputVO.LOAN_CHK2_YN = '';
		$scope.inputVO.CD_CHK_YN = '';
		$scope.inputVO.PREMIUM_TRANSSEQ = '';
		$scope.inputVO.LOAN_PRD_YN = '';
		$scope.inputVO.QC_IMMI = '';
		$scope.inputVO.QC_PROPOSER_CHG = '';
		$scope.inputVO.CHG_CUST_ID = '';
		$scope.inputVO.VALID_CHG_CUST_YN = 'N';

		$scope.premiumUsageChange();
	}

	/**initialize**/
	$scope.init = function() {
		debugger
		$scope.checkin = false;
		$scope.checkout = false;
		$scope.backpage = true;
		$scope.read = false;
		$scope.InputData = $scope.connector('get','IOT140');
		$scope.branchID = projInfoService.getBranchID();
		$scope.branchName = projInfoService.getBranchName();
		$scope.nextData = true;
		$scope.saveData = false;
		$scope.MatchButton = true;
		$scope.notCNCT = false;
		$scope.notBranchCust = false;
		$scope.RepresetCust = false;
		$scope.underSevenYear = false;
		$scope.elsefile = false;
		$scope.exchangefile = false;
		$scope.cnctdataList={};
		var date = new Date();
		$scope.KEYIN_DATE = date.toLocaleString();
		$scope.chgCustNotFirstPremium = false;

		$scope.checkField = {
				oth_type:false,
				apply_date:false,
				cust_id:false,
				proposer_name:false,
				proposer_birday:false,
				insured_id:false,
				represet_id:false,
				represet_name:false,
				represet_cust:false,
				insprd_id:false,
				terminated_inc:false
		}


		if($scope.InputData != undefined){
			$scope.control_reg_type = true;
			switch ($scope.InputData.OPR_STATUS) {
			case 'UPDATE':
				$scope.OPR_STATUS = 'UPDATE';
				$scope.backpage = false;
				$scope.inputVO.INS_KEYNO = $scope.InputData.INS_KEYNO;
				$scope.inputVO.reg_type = $scope.InputData.REG_TYPE;
				$timeout(function(){$scope.retrieve_data();},500);
				break;
			case 'Read':
				$scope.backpage = false;
				$scope.saveData = true;
				$scope.read = true;
				$scope.OPR_STATUS = 'Read';
				$scope.inputVO.INS_KEYNO = $scope.InputData.INS_KEYNO;
				$scope.inputVO.reg_type = $scope.InputData.REG_TYPE;
				$timeout(function(){$scope.retrieve_data();},500);
				break;
			default:
				break;
			}
		}else{
			$scope.control_reg_type = false;
			$scope.OPR_STATUS = 'new';

			//清除留存文件暫存
    		$scope.connector('set','IOT900inListtemp','');
    		$scope.connector('set','IOT900outListtemp','');

			$scope.inputVO = {
					reg_type:'3',
					INS_ID:'',
					RECRUIT_ID:undefined,
					OTH_TYPE:'',
					DOC_KEYIN_DATE: undefined,
					CUST_ID:undefined,
					PROPOSER_NAME:'',
					PROPOSER_BIRDAY:undefined,
					PROPOSER_CM_FLAG:'',
					INSURED_ID:undefined,
					INSURED_NAME:'',
					INSURED_CM_FLAG:'',
					RECRUIT_ID:undefined,
					REPRESET_ID:undefined,
					REPRESET_NAME:'',
					REPRESET_CUST:'',
					REPRESET_CM_FLAG:'',
					AO_CODE:'',
					POLICY_NO1:'',
					POLICY_NO2:'',
					POLICY_NO3:'',
					INSPRD_KEYNO:'',
					INSPRD_ID:undefined,
					CNCT_NAME:'',
					TERMINATED_INC:'',
					STATUS:'20',
					FX_PROD:'',
					EX_CUST_ID:undefined,
					EX_PROPOSER_NAME:'',
					CONTRAT_DATE:undefined,
					PROD_PERIOD:'',
					REMARK_INS:'',
					PROPOSER_RISK:'',
					MATCH_DATE:undefined,
					C_SENIOR_PVAL:'',
					temp_OTH_TYPE:undefined,
					tempFX_PROD:undefined,
					editOTH_TYPE:false,
					FB_COM_YN:'Y',
					INVESTList:[],
					COMPANY_NUM:'82',
					REVISE_CONFIRM_YN: "N",
					notOthType2: true
			}
			$scope.initCheckItems();
			$timeout(function(){$scope.inquireCompanyFB();},700);
		}
	}
	$scope.init();//initialize variable

	//登入類型變化
	$scope.selectRegType = function(){
		//新增時切換登陸類型清除欄位
		if($scope.InputData == undefined){
			$scope.clear_cloum();
		}
		if($scope.inputVO.reg_type == '3'){
			$scope.elsefile=true;
			$scope.exchangefile=false;
			$scope.in_REGTYPE='3'
		}
		if($scope.inputVO.reg_type == '4'){
			$scope.elsefile=false;
			$scope.exchangefile=true;
			$scope.in_REGTYPE='4'
			$scope.sendRecv("IOT130", "getExchangeRate", "com.systex.jbranch.app.server.fps.iot130.IOT130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.ExchangeRateList = tota[0].body.ExchangeRateList
						}
	   			});
		}
	}
	$scope.selectRegType();

	//進件來源變更
	$scope.fnComYnChange = function() {
		debugger
		//險種資料清空
		$scope.inputVO.INSPRD_ID = '';
		$scope.inputVO.CNCT_NAME = '';
		$scope.inputVO.INSPRD_KEYNO = '';

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
		$scope.inputVO.CNCT_NAME = '';
		$scope.inputVO.INSPRD_KEYNO = '';
	}
//end
});
