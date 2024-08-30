/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT120Controller', function($rootScope, $confirm, $scope, $filter, $controller, socketService, ngDialog, projInfoService, sysInfoService, getParameter, $timeout, $http) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "IOT120Controller";

	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};

	$scope.open_apply_date = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};

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
	getParameter.XML(['IOT.CM_FLAG','IOT.RLT_WITH_INSURED', 'IOT.RELATED_WITH_PROPOSER', 'IOT.PAY_TYPE', 'IOT.MOP2', 'IOT.MORTGAGE_LIFE_LOAN', 'IOT.PAYER_REL_PROPOSER','IOT.NO_CHK_LOAN_INSPRD', 'IOT.INV_PRD_LOAN_KYC'],function(totas){
		if(totas){
			//戶況檢核
			$scope.mappingSet['IOT.CM_FLAG'] = totas.data[totas.key.indexOf('IOT.CM_FLAG')];
			$scope.mappingSet['IOT.RLT_WITH_INSURED'] = totas.data[totas.key.indexOf('IOT.RLT_WITH_INSURED')];
			$scope.mappingSet['IOT.RELATED_WITH_PROPOSER'] = totas.data[totas.key.indexOf('IOT.RELATED_WITH_PROPOSER')];
			$scope.mappingSet['IOT.PAY_TYPE'] = totas.data[totas.key.indexOf('IOT.PAY_TYPE')];
			$scope.mappingSet['IOT.MOP2'] = totas.data[totas.key.indexOf('IOT.MOP2')];
			$scope.mappingSet['IOT.MORTGAGE_LIFE_LOAN'] = totas.data[totas.key.indexOf('IOT.MORTGAGE_LIFE_LOAN')];
			$scope.mappingSet['IOT.PAYER_REL_PROPOSER'] = totas.data[totas.key.indexOf('IOT.PAYER_REL_PROPOSER')];
			$scope.mappingSet['IOT.NO_CHK_LOAN_INSPRD'] = totas.data[totas.key.indexOf('IOT.NO_CHK_LOAN_INSPRD')];
			$scope.mappingSet['IOT.INV_PRD_LOAN_KYC'] = totas.data[totas.key.indexOf('IOT.INV_PRD_LOAN_KYC')];
		}
	});

	$scope.retrieve_data = function(){
		$scope.sendRecv("IOT120","retrieve_data","com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
				$scope.inputVO,function(tota,isError){
			if(!isError){
				$scope.INS_INFORMATION = tota[0].body.INS_INFORMATION;
				$scope.INS_RIDER_DTLList = tota[0].body.INS_RIDER_DTLList;
				$scope.INVESTList = tota[0].body.INVESTList;
				var dateTolong = new Date($scope.INS_INFORMATION[0].KEYIN_DATE);
				$scope.KEYIN_DATE = dateTolong.toLocaleString();
				$scope.branchID = $scope.INS_INFORMATION[0].BRANCH_NBR;
				$scope.branchName = '';
				$scope.inputVO = {
						INS_KEYNO:$scope.INS_INFORMATION[0].INS_KEYNO,
						NEED_MATCH:$scope.INS_INFORMATION[0].NEED_MATCH,
						REG_TYPE:$scope.INS_INFORMATION[0].REG_TYPE,
						INS_ID:$scope.INS_INFORMATION[0].INS_ID,
						CASE_ID:$scope.INS_INFORMATION[0].CASE_ID,
						CNCT_NAME:$scope.INS_INFORMATION[0].INSPRD_NAME,
						RECRUIT_ID:$scope.INS_INFORMATION[0].RECRUIT_ID,
						REF_CON_ID:$scope.INS_INFORMATION[0].REF_CON_ID,
						IS_EMP:$scope.INS_INFORMATION[0].IS_EMP,
						PROPOSER_NAME : $scope.INS_INFORMATION[0].PROPOSER_NAME,
						PROPOSER_BIRTH: $scope.toJsDate($scope.INS_INFORMATION[0].PROPOSER_BIRTH),
						INSURED_BIRTH: $scope.toJsDate($scope.INS_INFORMATION[0].INSURED_BIRTH),
						PAYER_BIRTH: $scope.toJsDate($scope.INS_INFORMATION[0].PAYER_BIRTH),
						CUST_ID:$scope.INS_INFORMATION[0].CUST_ID,
						INSURED_ID:$scope.INS_INFORMATION[0].INSURED_ID,
						INSURED_NAME:$scope.INS_INFORMATION[0].INSURED_NAME,
						INSPRD_ID:$scope.INS_INFORMATION[0].INSPRD_ID,
						BEFORE_INSPRD_ID:$scope.INS_INFORMATION[0].INSPRD_ID,
						APPLY_DATE:new Date($scope.INS_INFORMATION[0].APPLY_DATE),
						PAY_TYPE:$scope.INS_INFORMATION[0].PAY_TYPE,
						MOP2:$scope.INS_INFORMATION[0].MOP2,
						FIRST_PAY_WAY:$scope.INS_INFORMATION[0].FIRST_PAY_WAY,
						RLT_BT_PROREP:$scope.INS_INFORMATION[0].RLT_BT_PROREP,
						WRITE_REASON:$scope.INS_INFORMATION[0].WRITE_REASON,
						WRITE_REASON_OTH:$scope.INS_INFORMATION[0].WRITE_REASON_OTH,
						AO_CODE:$scope.INS_INFORMATION[0].AO_CODE,
						INSURED_CM_FLAG:$scope.INS_INFORMATION[0].INSURED_CM_FLAG,
						PROPOSER_CM_FLAG:$scope.INS_INFORMATION[0].PROPOSER_CM_FLAG,
						REPRESET_ID:$scope.INS_INFORMATION[0].REPRESET_ID,
						REPRESET_CM_FLAG:$scope.INS_INFORMATION[0].REPRESET_CM_FLAG,
						INSPRD_ANNUAL:$scope.INS_INFORMATION[0].INSPRD_ANNUAL,
						REAL_PREMIUM:$scope.INS_INFORMATION[0].REAL_PREMIUM,
						BASE_PREMIUM:$scope.INS_INFORMATION[0].BASE_PREMIUM,
						CURR_CD:$scope.INS_INFORMATION[0].CURR_CD,
						SPECIAL_CONDITION:$scope.INS_INFORMATION[0].SPECIAL_CON_IDX,
						AB_TRANSSEQ:$scope.INS_INFORMATION[0].AB_TRANSSEQ,
						REF_CON_ID:$scope.INS_INFORMATION[0].REF_CON_ID,
						CNCT_INSPRD_KEYNO:$scope.INS_INFORMATION[0].INSPRD_KEYNO,
						PRODUCT_TYPE:$scope.INS_INFORMATION[0].INSPRD_TYPE,
						INSURED_TRANSSEQ:$scope.INS_INFORMATION[0].INSURED_TRANSSEQ,
						PROPOSER_TRANSSEQ:$scope.INS_INFORMATION[0].PROPOSER_TRANSSEQ,
						QC_ADD:$scope.INS_INFORMATION[0].QC_ADD,
						TERMINATED_INC:$scope.INS_INFORMATION[0].TERMINATED_INC,
						QC_ERASER:$scope.INS_INFORMATION[0].QC_ERASER,
						//QC_ANC_DOC:$scope.INS_INFORMATION[0].QC_ANC_DOC,
						QC_STAMP:$scope.INS_INFORMATION[0].QC_STAMP,
						INS_RIDER_DTLList:$scope.INS_RIDER_DTLList,
						DELETE_INS_RIDER_DTLList:[],
						INVESTList:$scope.INVESTList,
						AB_EXCH_RATE:$scope.INS_INFORMATION[0].AB_EXCH_RATE,
						STATUS:'20',	//$scope.INS_INFORMATION[0].STATUS,
						editINSPRD_ID:false,
						LOAN_PRD_YN:$scope.INS_INFORMATION[0].LOAN_PRD_YN,
						QC_IMMI:$scope.INS_INFORMATION[0].QC_IMMI,
						PREMATCH_SEQ:$scope.INS_INFORMATION[0].PREMATCH_SEQ,
						GUILD_RPT_DATE:$scope.toJsDate($scope.INS_INFORMATION[0].GUILD_RPT_DATE),
						NOT_PASS_REASON:$scope.INS_INFORMATION[0].NOT_PASS_REASON,
						PREMIUM_TRANSSEQ:$scope.INS_INFORMATION[0].PREMIUM_TRANSSEQ,
						I_PREMIUM_TRANSSEQ:$scope.INS_INFORMATION[0].I_PREMIUM_TRANSSEQ,
						P_PREMIUM_TRANSSEQ:$scope.INS_INFORMATION[0].P_PREMIUM_TRANSSEQ,
						PAYER_ID:$scope.INS_INFORMATION[0].PAYER_ID,
						PAYER_NAME:$scope.INS_INFORMATION[0].PAYER_NAME,
						RLT_BT_PROPAY:$scope.INS_INFORMATION[0].RLT_BT_PROPAY,
						PAYER_CM_FLAG:$scope.INS_INFORMATION[0].PAYER_CM_FLAG,
						LOAN_SOURCE_YN:$scope.INS_INFORMATION[0].LOAN_SOURCE_YN,
						LOAN_SOURCE2_YN:$scope.INS_INFORMATION[0].LOAN_SOURCE2_YN,
						AML:$scope.INS_INFORMATION[0].AML,
						PRECHECK:$scope.INS_INFORMATION[0].PRECHECK,
						PROPOSER_INCOME1:$scope.INS_INFORMATION[0].PROPOSER_INCOME1,
						C_KYC_INCOME:$scope.INS_INFORMATION[0].C_KYC_INCOME,
						PROPOSER_INCOME3:$scope.INS_INFORMATION[0].PROPOSER_INCOME3,
						INSURED_INCOME1:$scope.INS_INFORMATION[0].INSURED_INCOME1,
						I_KYC_INCOME:$scope.INS_INFORMATION[0].I_KYC_INCOME,
						INSURED_INCOME3:$scope.INS_INFORMATION[0].INSURED_INCOME3,
						LOAN_CHK1_YN:$scope.INS_INFORMATION[0].LOAN_CHK1_YN,
						LOAN_CHK2_YN:$scope.INS_INFORMATION[0].LOAN_CHK2_YN,
						LOAN_CHK3_YN:$scope.INS_INFORMATION[0].LOAN_CHK3_YN,
						CD_CHK_YN:$scope.INS_INFORMATION[0].CD_CHK_YN,
						C_LOAN_CHK1_YN:$scope.INS_INFORMATION[0].C_LOAN_CHK1_YN,
						C_LOAN_CHK2_YN:$scope.INS_INFORMATION[0].C_LOAN_CHK2_YN,
						C_LOAN_CHK3_YN:$scope.INS_INFORMATION[0].C_LOAN_CHK3_YN,
						C_CD_CHK_YN:$scope.INS_INFORMATION[0].C_CD_CHK_YN,
						I_LOAN_CHK1_YN:$scope.INS_INFORMATION[0].I_LOAN_CHK1_YN,
						I_LOAN_CHK2_YN:$scope.INS_INFORMATION[0].I_LOAN_CHK2_YN,
						I_LOAN_CHK3_YN:$scope.INS_INFORMATION[0].I_LOAN_CHK3_YN,
						I_CD_CHK_YN:$scope.INS_INFORMATION[0].I_CD_CHK_YN,
						CUST_RISK: $scope.INS_INFORMATION[0].CUST_RISK,
						KYC_DUE_DATE: $scope.toJsDate($scope.INS_INFORMATION[0].KYC_DUE_DATE),
						CONTRACT_END_YN:$scope.INS_INFORMATION[0].CONTRACT_END_YN,
						S_INFITEM_LOAN_YN:$scope.INS_INFORMATION[0].S_INFITEM_LOAN_YN,
						PROPOSER_WORK:$scope.INS_INFORMATION[0].PROPOSER_WORK,
						INSURED_WORK:$scope.INS_INFORMATION[0].INSURED_WORK,
						C_LOAN_APPLY_DATE:$scope.toJsDate($scope.INS_INFORMATION[0].C_LOAN_APPLY_DATE),
						C_PREM_DATE:$scope.toJsDate($scope.INS_INFORMATION[0].C_PREM_DATE),
						I_LOAN_APPLY_DATE:$scope.toJsDate($scope.INS_INFORMATION[0].I_LOAN_APPLY_DATE),
						P_LOAN_APPLY_DATE:$scope.toJsDate($scope.INS_INFORMATION[0].P_LOAN_APPLY_DATE),
						C_LOAN_APPLY_YN:$scope.INS_INFORMATION[0].C_LOAN_APPLY_YN,
						I_LOAN_APPLY_YN:$scope.INS_INFORMATION[0].I_LOAN_APPLY_YN,
						P_LOAN_APPLY_YN:$scope.INS_INFORMATION[0].P_LOAN_APPLY_YN,
						AB_SENIOR_YN:$scope.INS_INFORMATION[0].AB_SENIOR_YN,
						C_SALE_SENIOR_YN:$scope.INS_INFORMATION[0].C_SALE_SENIOR_YN,
						I_SALE_SENIOR_YN:$scope.INS_INFORMATION[0].I_SALE_SENIOR_YN,
						P_SALE_SENIOR_YN:$scope.INS_INFORMATION[0].P_SALE_SENIOR_YN,
						C_SALE_SENIOR_TRANSSEQ:$scope.INS_INFORMATION[0].C_SALE_SENIOR_TRANSSEQ,
						I_SALE_SENIOR_TRANSSEQ:$scope.INS_INFORMATION[0].I_SALE_SENIOR_TRANSSEQ,
						P_SALE_SENIOR_TRANSSEQ:$scope.INS_INFORMATION[0].P_SALE_SENIOR_TRANSSEQ,
						QC_APEC:$scope.INS_INFORMATION[0].QC_APEC,
						QC_LOAN_CHK:$scope.INS_INFORMATION[0].QC_LOAN_CHK,
						QC_SIGNATURE:$scope.INS_INFORMATION[0].QC_SIGNATURE,
						C_SENIOR_PVAL:$scope.INS_INFORMATION[0].C_SENIOR_PVAL,
						COMPANY_NUM: $scope.INS_INFORMATION[0].COMPANY_NUM,
						INS_COM_NAM: $scope.INS_INFORMATION[0].INS_COM_NAME, //保險公司名稱
						FB_COM_YN: $scope.INS_INFORMATION[0].FB_COM_YN, //富壽:Y 非富壽:N
						CANCEL_CONTRACT_YN: $scope.INS_INFORMATION[0].CANCEL_CONTRACT_YN //契撤案件
					};

				if(tota[0].body.PreMatchList.length>0){
					$scope.inputVO.PRD_RISK = tota[0].body.PreMatchList[0].CUST_RISK;
					$scope.MATCH_DATE = tota[0].body.PreMatchList[0].MATCH_DATE;
				}
				if($scope.inputVO.REAL_PREMIUM != undefined){
					$scope.inputVO.REAL_PREMIUM = $filter('number')($scope.inputVO.REAL_PREMIUM);
				}
				if($scope.inputVO.BASE_PREMIUM != undefined){
					$scope.inputVO.BASE_PREMIUM = $filter('number')($scope.inputVO.BASE_PREMIUM);
				}else{
					$scope.inputVO.BASE_PREMIUM = '';
				}
				if($scope.inputVO.RECRUIT_ID){
					$scope.getInfo('RECRUIT');
				}
				if($scope.inputVO.REPRESET_ID){
					$scope.getInfo('ADDR');
				}
				$scope.getInfo('CUST');
			}
		});
		$scope.connector('set','IOT140',undefined);
	}

	/**initialize**/
	$scope.init = function() {
		$scope.in_outListbutton = true;
		$scope.checkin = false;
		$scope.checkout = false;
		$scope.check_AB_TRANSSEQ = false;
		$scope.backpage = true;
		$scope.noUpdate = false;
		$scope.check_QC_STAMP1 = false;
		$scope.check_QC_STAMP2 = false;
		$scope.mop2lock = true;
		$scope.INSPRD_Lock = false;
		$scope.read = false;
		$scope.yn_show_write = false;
		$scope.InputData = $scope.connector('get','IOT140');
		var date = new Date();
		$scope.KEYIN_DATE = date.toLocaleString();
		$scope.branchID = projInfoService.getBranchID();
		$scope.branchName = projInfoService.getBranchName();
		$scope.SPECIAL_CONList=[];
		$scope.inputVO.INVESTList=[];
		$scope.notBranchCust = false;	//不用檢查，現在在購買檢核檢查
		$scope.RepresetCust = true;		//不用檢查，現在在購買檢核檢查
		$scope.underSevenYear = false;
		$scope.nextData = true;
		$scope.saveData = false;
		$scope.editCASEID = false;
		$scope.editINSID = false;
		$scope.DEPID = undefined;
		$scope.EMP_NAME = undefined;
		$scope.COM_ADDRESS = undefined;
		$scope.INS_ID = undefined;
		$scope.checkField = {
				represet_id:false,
				represet_name:false,
				represet_cust:false
		}

		if($scope.InputData != undefined){
			switch ($scope.InputData.OPR_STATUS) {
			case 'UPDATE':
				$scope.OPR_STATUS = 'UPDATE';
				$scope.in_outListbutton=false;
				$scope.backpage = false;
				$scope.noUpdate = true;
				$scope.INSPRD_Lock = true;
				$scope.notBranchCust = false;
				$scope.inputVO.INS_KEYNO = $scope.InputData.INS_KEYNO;
				$timeout(function(){$scope.retrieve_data();},500);
				break;
			case 'Read':
				$scope.in_outListbutton = false;
				$scope.backpage = false;
				$scope.mop2lock = true;
				$scope.read = true;
				$scope.noupdatedata = true;
				$scope.INSPRD_Lock = true;
				$scope.saveData = true;
				$scope.OPR_STATUS = 'Read';
				$scope.inputVO.INS_KEYNO = $scope.InputData.INS_KEYNO;
				$timeout(function(){$scope.retrieve_data();},500);
				break;
			default:
				break;
			}
		}else{
			$scope.OPR_STATUS = 'new';

			//清除留存文件暫存
    		$scope.connector('set','IOT900inListtemp','');
    		$scope.connector('set','IOT900outListtemp','');

			$scope.inputVO = {
					in_column:'',
					REG_TYPE:'',
					WRITE_REASON:'',
					WRITE_REASON_OTH:undefined,
					CASE_ID : undefined,
					INS_ID : undefined,
					RECRUIT_ID:undefined,
					REF_CON_ID:'',
					REF_CON_EMPID:'',
					IS_EMP:'N',
					UNDER_YN:'',
					PRO_YN:'',
					PROPOSER_NAME : undefined,
					PROPOSER_BIRTH:undefined,
					INSURED_BIRTH:undefined,
					PAYER_BIRTH:undefined,
					PROPOSER_CM_FLAG:'',
					CUST_ID:undefined,
					INSURED_ID:undefined,
					INSURED_CM_FLAG:'',
					INSPRD_ID:undefined,
					APPLY_DATE:undefined,
					PAY_TYPE:'',
					MOP2:'',
					FIRST_PAY_WAY:'',
					PRODUCT_TYPE:'',
					QC_ADD:'',
					TERMINATED_INC:'',
					QC_ERASER:'',
					//QC_ANC_DOC:'',
					QC_STAMP:'',
					RLT_BT_PROREP:'',
					STATUS:'20',
					PRD_RISK:'',
					AO_CODE:'',
					EMP_NAME:'',
					REPRESET_ID:undefined,
					REPRESET_NAME:'',
					REPRESET_CM_FLAG:'',
					INS_RIDER_DTLList:[],
					DELETE_INS_RIDER_DTLList:[],
					INVESTList:[],
					AB_EXCH_RATE:'',
					editINSPRD_ID:true,
					EMP_NAME:'',
					KEYIN_DATE:date,
					PAYER_ID: '',
					PAYER_NAME: '',
					PAYER_CM_FLAG: '',
					RLT_BT_PROPAY: '',
					CUST_RISK: '',
					KYC_DUE_DATE: undefined,
					AML: '',
					PRECHECK: '',
					GUILD_RPT_DATE: undefined,
					LOAN_PRD_YN: '',
					LOAN_SOURCE_YN: '',
					LOAN_SOURCE2_YN: '',
					PROPOSER_INCOME1: '',
					C_KYC_INCOME: '',
					PROPOSER_INCOME3: '',
					INSURED_INCOME1: '',
					I_KYC_INCOME: '',
					INSURED_INCOME3: '',
					LOAN_CHK1_YN: '',
					LOAN_CHK2_YN: '',
					LOAN_CHK3_YN: '',
					CD_CHK_YN: '',
					C_LOAN_CHK1_YN: '',
					C_LOAN_CHK2_YN: '',
					C_LOAN_CHK3_YN: '',
					C_CD_CHK_YN: '',
					I_LOAN_CHK1_YN: '',
					I_LOAN_CHK2_YN: '',
					I_LOAN_CHK3_YN: '',
					I_CD_CHK_YN: '',
					NOT_PASS_REASON: '',
					PREMIUM_TRANSSEQ: '',
					I_PREMIUM_TRANSSEQ: '',
					P_PREMIUM_TRANSSEQ: '',
					PREMATCH_SEQ: '',
					QC_IMMI: '',
					CONTRACT_END_YN: '',
					S_INFITEM_LOAN_YN: '',
					PROPOSER_WORK: '',
					INSURED_WORK: '',
					C_LOAN_APPLY_DATE: null,
					C_PREM_DATE: null,
					I_LOAN_APPLY_DATE: null,
					P_LOAN_APPLY_DATE: null,
					C_LOAN_APPLY_YN: '',
					I_LOAN_APPLY_YN: '',
					P_LOAN_APPLY_YN: '',
					AB_SENIOR_YN: '',
					C_SALE_SENIOR_YN: '',
					I_SALE_SENIOR_YN: '',
					P_SALE_SENIOR_YN: '',
					C_SALE_SENIOR_TRANSSEQ: '',
					I_SALE_SENIOR_TRANSSEQ: '',
					P_SALE_SENIOR_TRANSSEQ: '',
					QC_APEC: '',
					QC_LOAN_CHK: '',
					QC_SIGNATURE: '',
					NEED_MATCH:'',
					C_SENIOR_PVAL:''
				};
		}
	}
	$scope.init();//initialize variable

	//判斷是否顯示使用紙本要保書原因
	$scope.show_write_reason = function(){
		if($scope.OPR_STATUS =='new'){
			$scope.inputVO.in_column = '';
			$scope.inputVO.INS_ID = undefined;
			$scope.inputVO.CASE_ID = undefined;
			$scope.inputVO.WRITE_REASON = '';
			$scope.inputVO.WRITE_REASON_OTH = undefined;
			$scope.inputVO.RECRUIT_ID = undefined;
			$scope.EMP_NAME = '';
			$scope.inputVO.EMP_NAME = '';
			$scope.inputVO.REF_CON_ID = '';
			$scope.inputVO.REF_CON_EMPID = '';
			$scope.inputVO.REF_CON_NAME = '';
			$scope.inputVO.IS_EMP = 'N';
			$scope.inputVO.PROPOSER_NAME  =  undefined;
			$scope.inputVO.PROPOSER_BIRTH = undefined;
			$scope.inputVO.INSURED_BIRTH = undefined;
			$scope.inputVO.PAYER_BIRTH = undefined;
			$scope.inputVO.PROPOSER_CM_FLAG = '';
			$scope.inputVO.CUST_ID = undefined;
			$scope.inputVO.INSURED_ID = undefined;
			$scope.inputVO.INSURED_NAME = undefined;
			$scope.inputVO.INSURED_CM_FLAG = '';
			$scope.inputVO.REPRESET_ID = undefined;
			$scope.inputVO.REPRESET_NAME = undefined;
			$scope.inputVO.RLT_BT_PROREP = '';
			$scope.inputVO.REPRESET_CM_FLAG = '';
			$scope.COM_ADDRESS = '';
			$scope.inputVO.INSPRD_ANNUAL = '';
			$scope.inputVO.REAL_PREMIUM = '';
			$scope.inputVO.BASE_PREMIUM = '';
			$scope.inputVO.INSPRD_ID = undefined;
			$scope.inputVO.APPLY_DATE = undefined;
			$scope.inputVO.PAY_TYPE = '';
			$scope.inputVO.MOP2 = '';
			$scope.inputVO.FIRST_PAY_WAY = '';
			$scope.inputVO.PRODUCT_TYPE = '';
			$scope.inputVO.CURR_CD = '';
			$scope.inputVO.SPECIAL_CONDITION = '';
			$scope.inputVO.AB_TRANSSEQ = undefined;
			$scope.inputVO.INVESTList = [];
			$scope.inputVO.INS_RIDER_DTLList = [];
			$scope.requiredINS_RIDER_DTLList = false;
			$scope.inputVO.DELETE_INS_RIDER_DTLList = [];
			$scope.inputVO.QC_ADD = '';
			$scope.inputVO.TERMINATED_INC = '';
			$scope.inputVO.QC_ERASER = '';
			//$scope.inputVO.QC_ANC_DOC = '';
			$scope.inputVO.QC_STAMP = '';
			$scope.inputVO.PROPOSER_TRANSSEQ = '';
			$scope.inputVO.INSURED_TRANSSEQ = '';
			$scope.inputVO.STATUS = '20';
			$scope.inputVO.PRD_RISK = '';
			$scope.inputVO.AO_CODE = '';
			$scope.inputVO.PAYER_ID = '';
			$scope.inputVO.PAYER_NAME = '';
			$scope.inputVO.PAYER_CM_FLAG = '';
			$scope.inputVO.RLT_BT_PROPAY = '';
			$scope.inputVO.CUST_RISK = '';
			$scope.inputVO.KYC_DUE_DATE = undefined;
			$scope.inputVO.AML = '';
			$scope.inputVO.PRECHECK = '';
			$scope.inputVO.GUILD_RPT_DATE = undefined;
			$scope.inputVO.LOAN_PRD_YN = '';
			$scope.inputVO.LOAN_SOURCE_YN = '';
			$scope.inputVO.LOAN_SOURCE2_YN = '';
			$scope.inputVO.PROPOSER_INCOME1 = '';
			$scope.inputVO.C_KYC_INCOME = '';
			$scope.inputVO.PROPOSER_INCOME3 = '';
			$scope.inputVO.INSURED_INCOME1 = '';
			$scope.inputVO.I_KYC_INCOME = '';
			$scope.inputVO.INSURED_INCOME3 = '';
			$scope.inputVO.LOAN_CHK1_YN = '';
			$scope.inputVO.LOAN_CHK2_YN = '';
			$scope.inputVO.LOAN_CHK3_YN = '';
			$scope.inputVO.CD_CHK_YN = '';
			$scope.inputVO.C_LOAN_CHK1_YN = '';
			$scope.inputVO.C_LOAN_CHK2_YN = '';
			$scope.inputVO.C_LOAN_CHK3_YN = '';
			$scope.inputVO.C_CD_CHK_YN = '';
			$scope.inputVO.I_LOAN_CHK1_YN = '';
			$scope.inputVO.I_LOAN_CHK2_YN = '';
			$scope.inputVO.I_LOAN_CHK3_YN = '';
			$scope.inputVO.I_CD_CHK_YN = '';
			$scope.inputVO.NOT_PASS_REASON = '';
			$scope.inputVO.PREMIUM_TRANSSEQ = '';
			$scope.inputVO.I_PREMIUM_TRANSSEQ = '';
			$scope.inputVO.P_PREMIUM_TRANSSEQ = '';
			$scope.inputVO.PREMATCH_SEQ = '';
			$scope.inputVO.QC_IMMI = '';
			$scope.inputVO.CONTRACT_END_YN = '';
			$scope.inputVO.S_INFITEM_LOAN_YN = '';
			$scope.inputVO.PROPOSER_WORK = '';
			$scope.inputVO.INSURED_WORK = '';
			$scope.inputVO.C_LOAN_APPLY_DATE = null;
			$scope.inputVO.C_PREM_DATE = null;
			$scope.inputVO.I_LOAN_APPLY_DATE = null;
			$scope.inputVO.P_LOAN_APPLY_DATE = null;
			$scope.inputVO.C_LOAN_APPLY_YN = '';
			$scope.inputVO.I_LOAN_APPLY_YN = '';
			$scope.inputVO.P_LOAN_APPLY_YN = '';
			$scope.inputVO.AB_SENIOR_YN = '';
			$scope.inputVO.C_SALE_SENIOR_YN = '';
			$scope.inputVO.I_SALE_SENIOR_YN = '';
			$scope.inputVO.P_SALE_SENIOR_YN = '';
			$scope.inputVO.C_SALE_SENIOR_TRANSSEQ = '';
			$scope.inputVO.I_SALE_SENIOR_TRANSSEQ = '';
			$scope.inputVO.P_SALE_SENIOR_TRANSSEQ = '';
			$scope.inputVO.QC_APEC = '';
			$scope.inputVO.QC_LOAN_CHK = '';
			$scope.inputVO.QC_SIGNATURE = '';
			$scope.inputVO.NEED_MATCH = '';
			$scope.inputVO.C_SENIOR_PVAL = '';
		}
		if($scope.inputVO.REG_TYPE == '2'){
			$scope.yn_show_write = true;
			$scope.editINSID = true;
			$scope.editCASEID = true;
			$scope.requiredCASEID = false;
		}
		if($scope.inputVO.REG_TYPE == '1'){
			$scope.yn_show_write = false;
			$scope.editINSID = true;
			$scope.editCASEID = false;
			if($scope.OPR_STATUS == 'Read'){
				$scope.editINSID = true;
				$scope.editCASEID = true;
			}
		}
	}

	//webService欄位清除
	$scope.webservice_clear = function(){
		$scope.inputVO.RECRUIT_ID = undefined;
		$scope.inputVO.INSURED_ID = undefined;
		$scope.inputVO.INSURED_NAME = undefined;
		$scope.inputVO.CUST_ID = undefined;
		$scope.inputVO.PROPOSER_NAME = undefined;
		//實收保費
		$scope.inputVO.REAL_PREMIUM = undefined;
		//首期保費繳費方式
		$scope.inputVO.FIRST_PAY_WAY = undefined;
		//要保人生日民國年轉西元年再轉日期格式
		$scope.inputVO.PROPOSER_BIRTH = undefined;

		$scope.inputVO.INSPRD_ID = undefined;
		$scope.inputVO.MOP2 = '';
		$scope.inputVO.APPLY_DATE = undefined;
		$scope.inputVO.INS_RIDER_DTLList = [];
		$scope.requiredINS_RIDER_DTLList = false;
		$scope.inputVO.INVESTList = [];
	}

	$scope.CASEPDF = function(){
		if($scope.inputVO.REG_TYPE == '1' && $scope.OPR_STATUS == 'new'){
			if($scope.inputVO.CASE_ID != undefined){
				$scope.sendRecv("IOT120", "Get_PdfInfo", "com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
					$scope.inputVO, function(tota, isError) {
	        			if(!isError) {
							debugger
	        				//console.log('webservicePdf:'+JSON.stringify(tota[0].body.webservicePdfData));
	        				$scope.webservicePdfData = tota[0].body.webservicePdfData;
	                    	if($scope.webservicePdfData.ReturnCode == '100'){
	                    		var msg = $scope.webservicePdfData.Message;
	                    		$scope.showMsg("PDF Success");
	                    	}else if($scope.webservicePdfData.ReturnCode == '200'){
	                    		var errorMsg = $scope.webservicePdfData.Message;
	                    		$scope.showErrorMsg(errorMsg);
	                    		$scope.init();
	                    	}else{
	                    		var errorMsg = $scope.webservicePdfData.Message;
	                    		$scope.showErrorMsg(errorMsg);
	                    		$scope.init();
	                    	}
	                    }else{
							$scope.init();
	                    }
	               	});
			}else{
				$scope.init();
			}
		}
	}

	//電子要保書根據[保險文件編號]帶出資料(未完成)
	$scope.CASEID = function(){
		if($scope.inputVO.REG_TYPE == "" || $scope.inputVO.REG_TYPE == undefined){
			$scope.showMsg('請先選擇要保書類型');
		}
		debugger
		if($scope.inputVO.REG_TYPE == '1'){
			if($scope.inputVO.CASE_ID != undefined && $scope.inputVO.PREMATCH_SEQ != ""){
				$scope.webservice_clear();
				debugger
				$scope.inputVO.CASE_ID = $scope.inputVO.CASE_ID.toUpperCase();
				$scope.sendRecv("IOT120","Get_InsInfo","com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
    					$scope.inputVO,function(tota,isError){
					if(!isError){
						debugger
						$scope.webservice = tota[0].body.webserviceData;
						if($scope.webservice.type == 'RestWS'){
							if($scope.webservice.CODE == '200') {
								var errorMsg = $scope.webservice.Message;
								$scope.showErrorMsg(errorMsg);
								$scope.init();
							}else if($scope.webservice.CODE == '300'){
								var errorMsg = $scope.webservice.Message;
								$scope.showErrorMsg(errorMsg);
								$scope.init();
							}else{
								$scope.parseWebserviceData();
								$scope.CASEPDF();

								//將購買檢核資料放入頁面中
								$scope.sendRecv("IOT120","get_PreMatch","com.systex.jbranch.app.server.fps.iot120.IOT120InputVO", $scope.inputVO,function(tota,isError){
									if(!isError){
										debugger
										$scope.prematchList = tota[0].body.PreMatchList[0];
										$scope.inputVO.INVESTList = tota[0].body.INVESTList;//不由API資料帶入，由購買檢核資料帶入
										//將購買檢核資料放入頁面中
										$scope.putPrematchData();
									} else {
										$scope.init();
									}
								});

								$scope.in_outListbutton=false;
							}
						}else if($scope.webservice.type == 'SoapWS'){
							if($scope.webservice.CODE == '100'){
								$scope.showErrorMsg('已列印未上傳或找不到該保險文件');
								$scope.init();
							}else if($scope.webservice.CODE == '300'){
								$scope.showErrorMsg('傳入格式錯誤');
								$scope.init();
							}else{
								debugger
								$scope.parseSoapWebserviceData();
							}
						}else{
							$scope.showErrorMsg('查無此案件編號資料');
							$scope.init();
						}
					}else{
						$scope.init();
					}
				});//webService end
			}
		}
	}

	$scope.parseWebserviceData = function(){
		debugger
		//附約
		if($scope.webservice.Result.INS_RIDER_DTLList.length > 0){
			$scope.inputVO.INS_RIDER_DTLList = $scope.webservice.Result.INS_RIDER_DTLList;
			$scope.requiredINS_RIDER_DTLList = true;
		}

		//投資標的
		//註解掉，由購買檢核資料帶入
//		if($scope.webservice.Result.INVESTList.length > 0){
//			$scope.inputVO.INVESTList = $scope.webservice.Result.INVESTList;
//		}//投資標的end
	}

	$scope.getPrematchData = function() {
		debugger
		if($scope.inputVO.REG_TYPE == "1") {
			//電子要保書
			$scope.CASEID();
		} else {
			if($scope.inputVO.PREMATCH_SEQ != ''){
				$scope.sendRecv("IOT120","get_PreMatch","com.systex.jbranch.app.server.fps.iot120.IOT120InputVO", $scope.inputVO,function(tota,isError){
					if(!isError){
						debugger
						$scope.prematchList = tota[0].body.PreMatchList[0];
						$scope.inputVO.INVESTList = tota[0].body.INVESTList;
						//將購買檢核資料放入頁面中
						$scope.putPrematchData();

						$scope.in_outListbutton=false;
					}else{
						$scope.init();
					}
				});
			}else{
				$scope.init();
			}
		}
	}

	$scope.putPrematchData = function() {
		//將購買檢核資料放入頁面中
		debugger
//		$scope.inputVO = $scope.prematchList;
		$scope.inputVO.APPLY_DATE = $scope.toJsDate($scope.prematchList.APPLY_DATE);	//要保書填寫申請日
		$scope.inputVO.KYC_DUE_DATE = $scope.toJsDate($scope.prematchList.KYC_DUE_DATE);

		$scope.inputVO.PREMATCH_SEQ = $scope.prematchList.PREMATCH_SEQ;
		$scope.inputVO.INS_ID = $scope.prematchList.INS_ID;
		$scope.inputVO.CASE_ID = $scope.prematchList.CASE_ID;
		$scope.inputVO.RECRUIT_ID = $scope.prematchList.RECRUIT_ID;
		$scope.getInfo('RECRUIT');
		$scope.inputVO.INSURED_ID = $scope.prematchList.INSURED_ID;
		$scope.inputVO.INSURED_NAME = $scope.prematchList.INSURED_NAME;
		$scope.inputVO.INSURED_CM_FLAG = $scope.prematchList.INSURED_CM_FLAG;
		$scope.inputVO.CUST_ID = $scope.prematchList.CUST_ID;
		$scope.getInfo('CUST');
		$scope.inputVO.AO_CODE = $scope.prematchList.AO_CODE;
		$scope.inputVO.PROPOSER_NAME = $scope.prematchList.PROPOSER_NAME;
		$scope.inputVO.PROPOSER_CM_FLAG = $scope.prematchList.PROPOSER_CM_FLAG;
		$scope.inputVO.PROPOSER_BIRTH = $scope.toJsDate($scope.prematchList.PROPOSER_BIRTH);
		$scope.inputVO.INSURED_BIRTH = $scope.toJsDate($scope.prematchList.INSURED_BIRTH);
		$scope.inputVO.PAYER_BIRTH = $scope.toJsDate($scope.prematchList.PAYER_BIRTH);
		$scope.inputVO.PAYER_ID = $scope.prematchList.PAYER_ID;
		$scope.inputVO.PAYER_NAME = $scope.prematchList.PAYER_NAME;
		$scope.inputVO.PAYER_CM_FLAG = $scope.prematchList.PAYER_CM_FLAG;
		$scope.inputVO.REPRESET_ID = $scope.prematchList.REPRESET_ID;
		$scope.inputVO.REPRESET_NAME = $scope.prematchList.REPRESET_NAME;
		$scope.inputVO.REPRESET_CM_FLAG = $scope.prematchList.REPRESET_CM_FLAG;
		$scope.inputVO.RLT_BT_PROREP = $scope.prematchList.RLT_BT_PROREP;
		$scope.inputVO.RLT_BT_PROPAY = $scope.prematchList.RLT_BT_PROPAY;

		$scope.inputVO.CUST_RISK = $scope.prematchList.CUST_RISK;
		$scope.inputVO.AML = $scope.prematchList.AML;
		$scope.inputVO.PRECHECK = $scope.prematchList.PRECHECK;
	//
		//首期保費繳費方式
		$scope.inputVO.FIRST_PAY_WAY = $scope.prematchList.FIRST_PAY_WAY;
		//要保人生日
		$scope.inputVO.PROPOSER_BIRTH = $scope.toJsDate($scope.prematchList.PROPOSER_BIRTH);
		$scope.inputVO.INSPRD_ID = $scope.prematchList.INSPRD_ID;
		$scope.inputVO.MOP2 = $scope.prematchList.MOP2;
		$scope.inputVO.CNCT_INSPRD_KEYNO = $scope.prematchList.INSPRD_KEYNO;
		$scope.inputVO.CNCT_NAME = $scope.prematchList.CNCT_NAME;
		$scope.inputVO.INSPRD_ANNUAL = $scope.prematchList.INSPRD_ANNUAL;
		$scope.inputVO.REAL_PREMIUM = $scope.prematchList.REAL_PREMIUM;
		$scope.inputVO.BASE_PREMIUM = $scope.prematchList.BASE_PREMIUM;
		$scope.inputVO.PAY_TYPE = $scope.prematchList.PAY_TYPE;
		$scope.inputVO.CURR_CD = $scope.prematchList.CURR_CD;
		$scope.inputVO.PRODUCT_TYPE = $scope.prematchList.PRODUCT_TYPE;
		$scope.inputVO.SPECIAL_CONDITION = $scope.prematchList.SPECIAL_CONDITION;
		$scope.inputVO.AB_EXCH_RATE = $scope.prematchList.AB_EXCH_RATE;
		var findPrd = $filter('filter')($scope.mappingSet['IOT.MORTGAGE_LIFE_LOAN'], {DATA: $scope.inputVO.INSPRD_ID});
		$scope.inputVO.LOAN_PRD_YN = (findPrd != null && findPrd.length > 0) ? "Y" : "N";

		$scope.inputVO.LOAN_SOURCE_YN = $scope.prematchList.LOAN_SOURCE_YN;
		$scope.inputVO.LOAN_SOURCE2_YN = $scope.prematchList.LOAN_SOURCE2_YN;
		$scope.inputVO.PROPOSER_INCOME1 = $scope.prematchList.PROPOSER_INCOME1;
		$scope.inputVO.C_KYC_INCOME = $scope.prematchList.C_KYC_INCOME;
		$scope.inputVO.PROPOSER_INCOME3 = $scope.prematchList.PROPOSER_INCOME3;
		$scope.inputVO.INSURED_INCOME1 = $scope.prematchList.INSURED_INCOME1;
		$scope.inputVO.I_KYC_INCOME = $scope.prematchList.I_KYC_INCOME;
		$scope.inputVO.INSURED_INCOME3 = $scope.prematchList.INSURED_INCOME3;
		$scope.inputVO.LOAN_CHK1_YN = $scope.prematchList.LOAN_CHK1_YN;
		$scope.inputVO.LOAN_CHK2_YN = $scope.prematchList.LOAN_CHK2_YN;
		$scope.inputVO.LOAN_CHK3_YN = $scope.prematchList.LOAN_CHK3_YN;
		$scope.inputVO.CD_CHK_YN = $scope.prematchList.CD_CHK_YN;
		$scope.inputVO.C_LOAN_CHK1_YN = $scope.prematchList.C_LOAN_CHK1_YN;
		$scope.inputVO.C_LOAN_CHK2_YN = $scope.prematchList.C_LOAN_CHK2_YN;
		$scope.inputVO.C_LOAN_CHK3_YN = $scope.prematchList.C_LOAN_CHK3_YN;
		$scope.inputVO.C_CD_CHK_YN = $scope.prematchList.C_CD_CHK_YN;
		$scope.inputVO.I_LOAN_CHK1_YN = $scope.prematchList.I_LOAN_CHK1_YN;
		$scope.inputVO.I_LOAN_CHK2_YN = $scope.prematchList.I_LOAN_CHK2_YN;
		$scope.inputVO.I_LOAN_CHK3_YN = $scope.prematchList.I_LOAN_CHK3_YN;
		$scope.inputVO.I_CD_CHK_YN = $scope.prematchList.I_CD_CHK_YN;
		$scope.inputVO.CONTRACT_END_YN = $scope.prematchList.CONTRACT_END_YN;
		$scope.inputVO.S_INFITEM_LOAN_YN = $scope.prematchList.S_INFITEM_LOAN_YN;
		$scope.inputVO.PROPOSER_WORK = $scope.prematchList.PROPOSER_WORK;
		$scope.inputVO.INSURED_WORK = $scope.prematchList.INSURED_WORK;
		$scope.inputVO.C_LOAN_APPLY_DATE = $scope.toJsDate($scope.prematchList.C_LOAN_APPLY_DATE);
		$scope.inputVO.C_PREM_DATE = $scope.toJsDate($scope.prematchList.C_PREM_DATE);
		$scope.inputVO.I_LOAN_APPLY_DATE = $scope.toJsDate($scope.prematchList.I_LOAN_APPLY_DATE);
		$scope.inputVO.P_LOAN_APPLY_DATE = $scope.toJsDate($scope.prematchList.P_LOAN_APPLY_DATE);
		$scope.inputVO.C_LOAN_APPLY_YN = $scope.prematchList.C_LOAN_APPLY_YN;
		$scope.inputVO.I_LOAN_APPLY_YN = $scope.prematchList.I_LOAN_APPLY_YN;
		$scope.inputVO.P_LOAN_APPLY_YN = $scope.prematchList.P_LOAN_APPLY_YN;
		$scope.inputVO.AB_SENIOR_YN = $scope.prematchList.AB_SENIOR_YN;
		$scope.inputVO.C_SALE_SENIOR_YN = $scope.prematchList.C_SALE_SENIOR_YN;
		$scope.inputVO.I_SALE_SENIOR_YN = $scope.prematchList.I_SALE_SENIOR_YN;
		$scope.inputVO.P_SALE_SENIOR_YN = $scope.prematchList.P_SALE_SENIOR_YN;
		$scope.inputVO.C_SALE_SENIOR_TRANSSEQ = $scope.prematchList.C_SALE_SENIOR_TRANSSEQ;
		$scope.inputVO.I_SALE_SENIOR_TRANSSEQ = $scope.prematchList.I_SALE_SENIOR_TRANSSEQ;
		$scope.inputVO.P_SALE_SENIOR_TRANSSEQ = $scope.prematchList.P_SALE_SENIOR_TRANSSEQ;
		$scope.inputVO.PREMIUM_TRANSSEQ = $scope.prematchList.PREMIUM_TRANSSEQ;
		$scope.inputVO.I_PREMIUM_TRANSSEQ = $scope.prematchList.I_PREMIUM_TRANSSEQ;
		$scope.inputVO.P_PREMIUM_TRANSSEQ = $scope.prematchList.P_PREMIUM_TRANSSEQ;
		$scope.inputVO.NEED_MATCH = $scope.prematchList.NEED_MATCH;
		$scope.inputVO.C_SENIOR_PVAL = $scope.prematchList.C_SENIOR_PVAL;
		$scope.inputVO.COMPANY_NUM = $scope.prematchList.COMPANY_NUM;
		$scope.inputVO.INS_COM_NAME = $scope.prematchList.INS_COM_NAME;//保險公司名稱
		$scope.inputVO.FB_COM_YN = $scope.prematchList.FB_COM_YN; //富壽:Y 非富壽:N
		$scope.inputVO.CANCEL_CONTRACT_YN = $scope.prematchList.CANCEL_CONTRACT_YN; //契撤案件
		
		$scope.MATCH_DATE = $scope.toJsDate($scope.prematchList.MATCH_DATE);
		$scope.inputVO.PRD_RISK = $scope.prematchList.CUST_RISK;
	}

	$scope.parseSoapWebserviceData = function(){
		debugger
		$scope.inputVO.INS_ID = $scope.webservice.CASE_DATA.DATA_DISP;
		if($scope.webservice.CASE_DATA.SING_SALE_ID != ''){
			$scope.inputVO.RECRUIT_ID = $scope.webservice.CASE_DATA.SING_SALE_ID;
			$scope.getInfo('RECRUIT');
		}
		$scope.inputVO.INSURED_ID = $scope.webservice.CASE_DATA.I_ID;
		$scope.inputVO.INSURED_NAME = $scope.webservice.CASE_DATA.I_NAME;
		$scope.inputVO.CUST_ID = $scope.webservice.CASE_DATA.A_ID;
		$scope.inputVO.PROPOSER_NAME = $scope.webservice.CASE_DATA.A_NAME;

		//實收保費
		$scope.inputVO.REAL_PREMIUM = $scope.webservice.CASE_DATA.APP_PREMIUM_RIDER;
		//首期保費繳費方式
		$scope.inputVO.FIRST_PAY_WAY = $scope.webservice.CASE_DATA.FPAY_DIND;
		//要保人生日民國年轉西元年再轉日期格式
		$scope.inputVO.PROPOSER_BIRTH = $scope.webserviceDatechange($scope.webservice.CASE_DATA.A_BIRTH);
		$scope.inputVO.INSPRD_ID = $scope.webservice.CASE_DATA.MINS_KIND;

		if($scope.webservice.CASE_DATA.MOP != ''){
			if($scope.webservice.CASE_DATA.MOP != 'D'){
				$scope.inputVO.MOP2 = $scope.webservice.CASE_DATA.MOP;
			}
		}

		//要保書填寫申請日
		$scope.inputVO.APPLY_DATE = $scope.webserviceDatechange($scope.webservice.CASE_DATA.DECLARE_DATE);
		//附約
		if($scope.webservice.CASE_DATA.INS_RIDER_DTLList.length > 0){
			$scope.inputVO.INS_RIDER_DTLList = $scope.webservice.CASE_DATA.INS_RIDER_DTLList;
		}



		//投資標的
		if($scope.webservice.CASE_DATA.FUND_CODE1.length > 0){
			if($scope.inputVO.INVESTList.length <= 0){
				for(var j = 0; j < $scope.webservice.CASE_DATA.FUND_CODE1.length; j++){
					debugger
					$scope.inputVO.INVESTList.push({
						TARGET_ID       : $scope.webservice.CASE_DATA.FUND_CODE1[j].FUND_CODE1,
						ALLOCATION_RATIO: $scope.webservice.CASE_DATA.FUND_CODE1[j].PASS_AMT_UALL_PERT1
					});
				}
			}
		}//投資標的end
	}

	$scope.checkBirthday = function(){
		if($scope.notBranchCust){
			if(getAge($scope.inputVO.PROPOSER_BIRTH)<7){
				$scope.underSevenYear=true;
				$scope.showMsg("ehl_01_iot120_003");
			}else{
				$scope.showMsg("ehl_01_iot120_004");
			}
		}
	}

	//根據欄位帶出分行代碼、招攬人員姓名、要保人姓名、留存分行地址
	$scope.getInfo = function(in_column){
		$scope.inputVO.in_column = in_column;
		if($scope.inputVO.in_column == 'INSURED'){
			if($scope.inputVO.INSURED_ID != undefined){
				$scope.inputVO.INSURED_ID.toUpperCase();
			}
			$scope.inputVO.INSURED_NAME = undefined;
			$scope.inputVO.INSURED_CM_FLAG = '';
		}
		if($scope.inputVO.in_column == 'CUST'){
			if($scope.inputVO.CUST_ID != undefined){
				$scope.inputVO.CUST_ID.toUpperCase();
			}
			$scope.inputVO.PROPOSER_NAME = undefined;
			$scope.inputVO.PROPOSER_BIRTH = undefined;
			$scope.inputVO.AO_CODE = '';
			$scope.CUST_AO_CODE=undefined;
			$scope.inputVO.PROPOSER_CM_FLAG = '';
		}
		if($scope.inputVO.in_column == 'ADDR'){
			if($scope.inputVO.REPRESET_ID != undefined){
				$scope.inputVO.REPRESET_ID.toUpperCase();
			}
			$scope.inputVO.REPRESET_NAME = undefined;
			$scope.inputVO.COM_ADDRESS = '';
			$scope.inputVO.REPRESET_CM_FLAG = '';
		}
		if($scope.inputVO.in_column == 'RECRUIT'){
			$scope.EMP_NAME = '';
			$scope.inputVO.EMP_NAME = '';
		}
		if($scope.inputVO.in_column == 'REF_CON_ID'){
			$scope.inputVO.REF_CON_EMPID = '';
			$scope.inputVO.REF_CON_NAME = '';
		}
		if($scope.inputVO.in_column == 'PAYER'){
			$scope.inputVO.PAYER_NAME = '';
			$scope.inputVO.PAYER_CM_FLAG = '';
		}
		$scope.sendRecv("IOT920","getCUSTInfo","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",
				$scope.inputVO,function(tota,isError){
			if(!isError){
				if(tota[0].body.EMP_NAME != null){
					if(tota[0].body.EMP_NAME.length>0){
						$scope.EMP_NAME = tota[0].body.EMP_NAME[0].EMP_NAME;
						$scope.inputVO.EMP_NAME = tota[0].body.EMP_NAME[0].EMP_NAME;
					}else{
						debugger
						$scope.inputVO.RECRUIT_ID = undefined;
						$scope.showErrorMsg('ehl_01_common_009');
					}
				}
				if(tota[0].body.CUST_NAME != null){
					if(tota[0].body.CUST_NAME.length>0){
						$scope.notBranchCust = false;
						$scope.inputVO.PROPOSER_NAME = tota[0].body.CUST_NAME[0].CUST_NAME;
						var birthday = $scope.toJsDate(tota[0].body.CUST_NAME[0].BIRTHDAY);
						$scope.inputVO.PROPOSER_BIRTH = birthday;

						$scope.CUST_AO_CODE = tota[0].body.CUST_NAME[0].AO_CODE;
						if($scope.CUST_AO_CODE != undefined){
							$scope.inputVO.AO_CODE = $scope.CUST_AO_CODE;
						}else{
							$scope.inputVO.AO_CODE = $scope.REPRESET_AO_CODE;
						}

						$scope.inputVO.PROPOSER_CM_FLAG = tota[0].body.CUST_NAME[0].PROPOSER_CM_FLAG;
						//是否為弱勢戶
						$scope.inputVO.UNDER_YN = tota[0].body.CUST_NAME[0].UNDER_YN;
						//是否為磚投
						$scope.inputVO.PRO_YN = tota[0].body.CUST_NAME[0].PRO_YN;
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
							$scope.REPRESET_AO_CODE = tota[0].body.REPRESETList[0].AO_CODE;
							if($scope.CUST_AO_CODE == undefined){
								$scope.inputVO.AO_CODE = $scope.REPRESET_AO_CODE;
							}
							$scope.RepresetCust = true;
						}
					}
				}
				if(tota[0].body.COM_ADDRESS != null){
					if(tota[0].body.COM_ADDRESS.length>0){
						$scope.COM_ADDRESS = tota[0].body.COM_ADDRESS[0].COM_ADDRESS;
					}
				}
				if(tota[0].body.REFList != null){
//					$scope.inputVO.REF_CON_EMPID = '';
//					$scope.inputVO.REF_CON_NAME = '';
					if(tota[0].body.REFList.length>0){
						$scope.inputVO.REF_CON_EMPID = tota[0].body.REFList[0].SALES_PERSON;
						$scope.inputVO.REF_CON_NAME = tota[0].body.REFList[0].SALES_NAME;
					}
				}
				if(tota[0].body.PAYERList != null){
					if(tota[0].body.PAYERList.length>0){
						if($scope.inputVO.PAYER_ID !=''){
							$scope.inputVO.PAYER_NAME = tota[0].body.PAYERList[0].CUST_NAME;
							$scope.inputVO.PAYER_CM_FLAG = tota[0].body.PAYERList[0].PAYER_CM_FLAG;
							$scope.inputVO.LOAN_CHK1_YN = tota[0].body.PAYERList[0].LOAN_CHK1_YN;
							$scope.inputVO.LOAN_CHK2_YN = tota[0].body.PAYERList[0].LOAN_CHK2_YN;
							$scope.inputVO.LOAN_CHK3_YN = tota[0].body.PAYERList[0].LOAN_CHK3_YN;
							$scope.inputVO.CD_CHK_YN = tota[0].body.PAYERList[0].CD_CHK_YN;
						}
					}
				}

			}else{
				if($scope.inputVO.in_column == 'INSURED'){
					$scope.inputVO.INSURED_ID = '';
				}
				if($scope.inputVO.in_column == 'CUST'){
					$scope.inputVO.CUST_ID = '';
				}
				if($scope.inputVO.in_column == 'ADDR'){
					$scope.inputVO.REPRESET_ID = '';
				}
				if($scope.inputVO.in_column == 'PAYER'){
					$scope.inputVO.PAYER_ID = '';
				}
			}
		});
	}

	//確認險種代號是否與險種名稱一致
	$scope.checkCNCTData = function(){
		if($scope.check_INSPRD_ID != $scope.inputVO.INSPRD_ID){
			$scope.INSPRD_Lock = false;
			$scope.inputVO.CNCT_INSPRD_KEYNO = undefined;
			$scope.inputVO.CNCT_NAME = undefined;
			$scope.inputVO.INSPRD_ANNUAL = undefined;
			$scope.inputVO.PAY_TYPE = undefined;
			$scope.inputVO.PRODUCT_TYPE = undefined;
			$scope.inputVO.CURR_CD = undefined;
			$scope.inputVO.SPECIAL_CONDITION = undefined;
			$scope.in_outListbutton=true;
			$scope.inputVO.INVESTList=[];
			//清除留存文件temp
			$scope.connector('set','IOT900inListtemp','');
			$scope.connector('set','IOT900outListtemp','');
		}
	}

	//險種資料查詢
	$scope.Ins_dtl_query = function(row){
			var INSPRD_ID=row.DTL_INSPRD_ID
			var dialog = ngDialog.open({
				template: 'assets/txn/IOT910/IOT910.html',
				className: 'IOT910',
				controller:['$scope',function($scope){
					$scope.INSPRD_ID = INSPRD_ID;
					if(INSPRD_ID != undefined){
						$scope.INSPRD_ID = INSPRD_ID.toUpperCase();
					}else{
						$scope.INSPRD_ID = INSPRD_ID;
					}
					$scope.INS_RIDER_DLT = true;
				}]
			});
			dialog.closePromise.then(function(data){
				row.INSPRD_KEYNO = data.value.INSPRD_KEYNO;
				row.DTL_INSPRD_ID = data.value.INSPRD_ID;
				row.INSPRD_NAME = data.value.INSPRD_NAME;
				row.INSPRD_ANNUAL = data.value.INSPRD_ANNUAL;
			});
	}

	$scope.check_RIDER_INSPRD_ID = function(row){
		if(row.DTL_INSPRD_ID == undefined || row.DTL_INSPRD_ID == ""){
			row.INSPRD_KEYNO = undefined;
			row.DTL_INSPRD_ID = undefined;
			row.INSPRD_NAME = undefined;
			row.INSPRD_ANNUAL = undefined;
		}
	}

	//要保書申請日期變更
	$scope.applyDateChg = function() {
		//重新取得繳款人保單貸款檢核、行內貸款檢核、定存不打折檢核
		$scope.getInfo("PAYER");
	}

	//表單產生
	$scope.Doc_print = function(in_type){
		$scope.printList = [];
		var date = new Date();
		switch (in_type) {
		case 'NoteRecord':
			if($scope.inputVO.CUST_ID !=undefined){
				$scope.printList.push({'CUST_ID':$scope.inputVO.CUST_ID,'DATE':date.toLocaleDateString("ja-JP"),'PRODUCT_TYPE':$scope.inputVO.PRODUCT_TYPE});
				$scope.sendRecv("IOT110", "printUnuTraRecTeleIntList",
						"com.systex.jbranch.app.server.fps.iot110.IOT110OutputVO",
						{'list':$scope.printList}, function(tota, isError) {
							if (!isError) {
								return;
							}
		   			});
			}else{
				var list = ['要保人ID']
				$scope.showErrorMsg('ehl_02_common_002',list);
			}
			break;
		case 'Recording':

			if($scope.inputVO.CUST_ID !=undefined && $scope.inputVO.INSURED_ID && $scope.inputVO.EMP_NAME != ''){
				if ($scope.inputVO.INSURED_ID.toUpperCase() == $scope.inputVO.CUST_ID.toUpperCase()) {
					$scope.sendRecv("IOT120", "printRecording1",
							"com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
							$scope.inputVO,
							function(totas, isError) {
			                	if (isError) {
			                		$scope.showErrorMsg(totas[0].body.msgData);
			                	}
							}
					);
				} else {
					$scope.sendRecv("IOT120", "printRecording2",
							"com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
							$scope.inputVO,
							function(totas, isError) {
			                	if (isError) {
			                		$scope.showErrorMsg(totas[0].body.msgData);
			                	}
							}
					);
				}
			}else{
	    		$scope.showErrorMsg('請輸入被保人ID、要保人ID、招攬人員員編');
			}
			break;

		//檢視行動投保要保書  -- 未完成
		case 'Insurance':
			if($scope.inputVO.CASE_ID != undefined){
				$scope.sendRecv("IOT120", "showPDF",
						"com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
						$scope.inputVO,
						function(totas, isError) {
		                	if (isError) {
		                		$scope.showErrorMsg(totas[0].body.msgData);
		                	}
						}
				);
			}else{
				$scope.showErrorMsg('請輸入案件編號');
			}
			break;
		default:
			break;
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
				in_REGTYPE:'1',
				INSPRD_ID:$scope.inputVO.INSPRD_ID
		}
		if($scope.OPR_STATUS != 'Read'){
			if($scope.inputVO.BEFORE_INSPRD_ID != $scope.inputVO.INSPRD_ID){
				doc_qcList.OPR_STATUS='new';
				$scope.inputVO.editINSPRD_ID = true;
			}else{
				doc_qcList.OPR_STATUS='UPDATE';
				$scope.inputVO.editINSPRD_ID = false;
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

	//檢查附約欄位是否為空
	$scope.checkINS_RIDER_DTLList = function(){
		debugger
		var count = 0;
		if($scope.inputVO.INS_RIDER_DTLList.length > 0){
			for(var i=0; i<$scope.inputVO.INS_RIDER_DTLList.length; i++){
				if($scope.inputVO.INS_RIDER_DTLList[i].DTL_INSPRD_ID == undefined ||
					$scope.inputVO.INS_RIDER_DTLList[i].DTL_INSPRD_ID == ""        ||
					$scope.inputVO.INS_RIDER_DTLList[i].INSPRD_ANNUAL == undefined ||
					$scope.inputVO.INS_RIDER_DTLList[i].INSPRD_ANNUAL == ""        ||
					$scope.inputVO.INS_RIDER_DTLList[i].INSPRD_NAME   == undefined ||
					$scope.inputVO.INS_RIDER_DTLList[i].INSPRD_NAME   == ""        ||
					$scope.inputVO.INS_RIDER_DTLList[i].INSURED_NAME  == undefined ||
					$scope.inputVO.INS_RIDER_DTLList[i].INSURED_NAME  == ""        ||
					$scope.inputVO.INS_RIDER_DTLList[i].PREMIUM       == undefined ||
					$scope.inputVO.INS_RIDER_DTLList[i].PREMIUM       == ""        ||
					$scope.inputVO.INS_RIDER_DTLList[i].RLT_WITH_INSURED  == undefined ||
					$scope.inputVO.INS_RIDER_DTLList[i].RLT_WITH_INSURED  == ""){
					count++;
					$scope.showMsg("ehl_01_common_022");
					break;
				}
			}
			if(count > 0){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}

	//檢查欄位
	$scope.checkFieldData = function(){
		if($scope.underSevenYear && $scope.notBranchCust){
				$scope.checkField.represet_id=true;
				$scope.checkField.represet_name=true;
				$scope.checkField.represet_cust=true;
		}
		debugger
		var regex = /^[0-9\s]*$/; //僅限數字

		if($scope.inputVO.REG_TYPE == '' ||
				$scope.inputVO.RECRUIT_ID == undefined || $scope.inputVO.IS_EMP == '' ||
				$scope.inputVO.INSURED_ID == undefined || $scope.inputVO.INSURED_NAME == undefined ||
				$scope.inputVO.CUST_ID == undefined || $scope.inputVO.PROPOSER_NAME == undefined ||
				$scope.inputVO.PROPOSER_BIRTH == undefined || $scope.inputVO.INSPRD_ID == undefined ||
				$scope.inputVO.CNCT_INSPRD_KEYNO == undefined || $scope.inputVO.REAL_PREMIUM == undefined ||
				$scope.inputVO.PAY_TYPE == '' ||
				$scope.inputVO.FIRST_PAY_WAY == '' || $scope.inputVO.PRODUCT_TYPE == '' ||
				$scope.inputVO.APPLY_DATE == undefined ||
				$scope.inputVO.QC_ADD == '' || $scope.inputVO.TERMINATED_INC == '' ||
				$scope.inputVO.QC_ERASER == ''||
				$scope.inputVO.QC_STAMP == '' || $scope.inputVO.QC_IMMI == '' ||
				($scope.inputVO.REG_TYPE == '1' && regex.test($scope.inputVO.INS_ID) && $scope.inputVO.QC_APEC == '') ||
				$scope.inputVO.QC_LOAN_CHK == '' || $scope.inputVO.QC_SIGNATURE == '' ||
				($scope.underSevenYear && ($scope.inputVO.REPRESET_ID == undefined || $scope.inputVO.REPRESET_NAME==undefined || $scope.inputVO.RLT_BT_PROREP ==''))
			){
//			$scope.inputVO.STATUS='10';
			$scope.showMsg("ehl_01_common_022");
		}else{
			$scope.inputVO.INSURED_NAME.toUpperCase();
			$scope.inputVO.PROPOSER_NAME.toUpperCase();
			switch ($scope.inputVO.REG_TYPE) {
			case '1':
				if($scope.inputVO.CASE_ID == undefined){
					$scope.showMsg("ehl_01_common_022");
				}else{
					if($scope.inputVO.PAY_TYPE != '1' &&  $scope.inputVO.MOP2 == ''){
						$scope.showMsg("ehl_01_common_022");
					}else{
						if(!$scope.notBranchCust || ($scope.notBranchCust && $scope.RepresetCust)){
							if(($scope.inputVO.QC_STAMP == 'N' && ($scope.inputVO.PROPOSER_TRANSSEQ != undefined || $scope.inputVO.INSURED_TRANSSEQ != undefined)) ||
								$scope.inputVO.QC_STAMP == 'Y'){
								return true;
							}else{
								$scope.showMsg("ehl_01_common_022");
							}
						}else{
							if($scope.underSevenYear){
								$scope.showMsg("ehl_01_iot120_003");
							}else{
								$scope.showMsg("ehl_01_iot120_004");
							}
						}
					}
				}
				break;
			case '2':
				if($scope.inputVO.WRITE_REASON == '' || ($scope.inputVO.WRITE_REASON == '6' && $scope.inputVO.WRITE_REASON_OTH == undefined)){
					$scope.showMsg("ehl_01_common_022");
				}else{
					if($scope.inputVO.PAY_TYPE != '1' &&  $scope.inputVO.MOP2 == ''){
						$scope.showMsg("ehl_01_common_022");
					}else{
						if(!$scope.notBranchCust || ($scope.notBranchCust && $scope.RepresetCust)){
							if(($scope.inputVO.QC_STAMP == 'N' && ($scope.inputVO.PROPOSER_TRANSSEQ != undefined || $scope.inputVO.INSURED_TRANSSEQ != undefined)) ||
								$scope.inputVO.QC_STAMP == 'Y'){
								return true;
							}else{
								$scope.showMsg("ehl_01_common_022");
							}
						}else{
							if($scope.underSevenYear){
								$scope.showMsg("ehl_01_iot120_003");
							}else{
								$scope.showMsg("ehl_01_iot120_004");
							}
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}

	$scope.check_TRANSSEQ = function(){
		if($scope.inputVO.QC_STAMP == 'N'){
			if($scope.inputVO.PROPOSER_TRANSSEQ != undefined){
				$scope.check_QC_STAMP2=false;
			}else{
				$scope.check_QC_STAMP2=true;
			}
			if($scope.inputVO.INSURED_TRANSSEQ != undefined){
				$scope.check_QC_STAMP1=false;
			}else{
				$scope.check_QC_STAMP1=true;
			}
		}else{
			$scope.check_QC_STAMP1=false;
			$scope.check_QC_STAMP2=false;
		}
	}

	//檢查非常態交易錄音序號
	$scope.required_AB_TRANSSEQ = function(){
//		if($scope.inputVO.PRODUCT_TYPE != '1'){
//			$scope.check_AB_TRANSSEQ = true;
//		}else{
//			$scope.check_AB_TRANSSEQ = false;
//		}
	}

	$scope.New_record = function(){
		$scope.init();
	}

	$scope.to_back = function(){
		$scope.inputVO.inList = $scope.connector('get','IOT900inList');
		$scope.inputVO.outList = $scope.connector('get','IOT900outList');
		$scope.inputVO.OPR_STATUS = $scope.OPR_STATUS;
		if($scope.MATCH_DATE != undefined){
			$scope.inputVO.MATCH_DATE = new Date($scope.MATCH_DATE); // 適配日 2017/09/01新增
		}else{
			$scope.inputVO.MATCH_DATE = undefined;
		}

		//$scope.inputVO.webservicePdfData = $scope.webservicePdfData;
		$scope.sendRecv("IOT120","chkData","com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
				$scope.inputVO,function(tota,isError){
			if (isError) {
//	    		$scope.showErrorMsg(tota.body.msgData);
				if(tota.body.msgData == 'ehl_01_iot120_009'){
					$scope.check_AB_TRANSSEQ = true;
					$scope.showErrorMsg(tota.body.msgData);
					return;
				}else{
					$scope.check_AB_TRANSSEQ = false;
					$scope.showErrorMsg(tota.body.msgData);
					return;
				}
	    	}
			debugger
	    	if (tota.length > 0) {
	    		//INS_ID重複跳錯誤訊息
	    		//CASE_ID重複跳錯誤訊息
	    		if($scope.OPR_STATUS == 'new' && tota[0].body.ErrorCASE_ID){
	    			var errorCASE_ID = [tota[0].body.ErrorCASE_ID];
	    			$scope.showErrorMsg('ehl_02_IOT920_001',errorCASE_ID);
	    			return;
	    		}
	    		if($scope.OPR_STATUS == 'new' && tota[0].body.ErrorINS_ID){
	    			var errorINS_ID = [tota[0].body.ErrorINS_ID];
	    			$scope.showErrorMsg('ehl_02_IOT920_001',errorINS_ID);
	    			return;
	    		}
	    		if($scope.OPR_STATUS == 'new' && tota[0].body.ErrorINS_RIDER){
	    			var errorRider = ['附約'];
	    			$scope.showErrorMsg(tota[0].body.ErrorINS_RIDER,errorRider);
	    			return;
	    		}
	    		if(tota[0].body.errorMsg) {
	    			$scope.showErrorMsg(tota[0].body.errorMsg);
	    		}
	    		
	    		//新增成功
	    		if($scope.OPR_STATUS == 'new' && !tota[0].body.ErrorCASE_ID && !tota[0].body.ErrorINS_RIDER){
	    			$scope.inputVO.INS_KEYNO = tota[0].body.INSKEY_NO;
	        		$scope.OPR_STATUS = 'Read';
	        		$scope.showSuccessMsg('ehl_01_common_001');
	        		$scope.read = true;
	        		$scope.nextData = false;
	        		$scope.saveData = true;
	    		}

	    		//清除留存文件暫存
	    		$scope.connector('set','IOT900inListtemp','');
	    		$scope.connector('set','IOT900outListtemp','');

	    		if($scope.OPR_STATUS == 'UPDATE'){
	    			$scope.showSuccessMsg('ehl_01_common_006');
		        	$scope.connector('set','IOT140_updateSubmit',true);
		        	$rootScope.menuItemInfo.url = "assets/txn/IOT140/IOT140.html";
	    		}
	    	}
		});
	}

	//儲存前檢核項目
	$scope.chkItemsBeforeSave = function() {
		var warningMsg = "";
		debugger
		//符合下述條件需輸入【保費來源錄音序號】，若未輸入則為"理專進件"
		// (定存打折檢核為Y或保單貸款檢核Y或行內貸款檢核Y)且主約險種定義需檢核保費來源(險種代號不存在不需要錄音的參數表)
		// 保費支付來源為貸款且保單貸款檢核Y且主約險種定義需檢核保費來源(險種代號不存在不需要錄音的參數表)
		// 保費支付來源為貸款且行內貸款檢核Y且主約險種定義需檢核保費來源(險種代號不存在不需要錄音的參數表)
		// 貸款申請日於要報書申請日93天內需檢核保費來源(險種代號不存在不需要錄音的參數表)
		// 業保書投保前三個月內有辦理解約或業報書投保前三個月有辦理貸款(險種代號不存在不需要錄音的參數表)
		// 65歲以上(險種代號不存在不需要錄音的參數表)
		var findPrd = $filter('filter')($scope.mappingSet['IOT.NO_CHK_LOAN_INSPRD'], {DATA: $scope.inputVO.INSPRD_ID});//不需要錄音的險種參數表
		var isInNoChkLoan = (findPrd != null && findPrd.length > 0) ? true : false;

		//是否需要輸入保費來源錄音序號
		$scope.inputVO.PREMIUM_TRANSSEQ_NEED_YN = "N";
		$scope.inputVO.I_PREMIUM_TRANSSEQ_NEED_YN = "N";
		$scope.inputVO.P_PREMIUM_TRANSSEQ_NEED_YN = "N";
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
			$scope.inputVO.PREMIUM_TRANSSEQ_NEED_YN = "Y";
			//要保人高齡+要保人、被保人、繳款人保費來源=Y，任一成立則要保人需輸入錄音序號
			if($scope.inputVO.PREMIUM_TRANSSEQ == "" || $scope.inputVO.PREMIUM_TRANSSEQ == null || $scope.inputVO.PREMIUM_TRANSSEQ == undefined ||
					!($scope.inputVO.PREMIUM_TRANSSEQ.length == 12 && $scope.inputVO.PREMIUM_TRANSSEQ.substring(6, 8) == "73")) {
				//保費來源錄音序號為空白者或錄音序號檢核不正確
				$scope.inputVO.STATUS = "10";	//理專進件
				$scope.showMsg("■ 要保人高齡/保費來源錄音序號為空白，或有誤請與客服確認錄音序號");
			}
		}
		//被保人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
		if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && ((($scope.inputVO.I_LOAN_CHK1_YN == 'Y' || $scope.inputVO.I_LOAN_CHK2_YN == 'Y' || $scope.inputVO.I_LOAN_CHK3_YN == 'Y' || $scope.inputVO.I_CD_CHK_YN == 'Y'
				|| $scope.inputVO.I_LOAN_APPLY_YN == 'Y' || $scope.inputVO.I_SALE_SENIOR_YN == "Y") && !isInNoChkLoan))) {
			//需要輸入被保人保費來源錄音序號
			$scope.inputVO.I_PREMIUM_TRANSSEQ_NEED_YN = "Y";
			//被保人高齡+保費來源=Y，任一成立則被保人需輸入錄音序號
			if($scope.inputVO.I_PREMIUM_TRANSSEQ == "" || $scope.inputVO.I_PREMIUM_TRANSSEQ == null || $scope.inputVO.I_PREMIUM_TRANSSEQ == undefined ||
					!($scope.inputVO.I_PREMIUM_TRANSSEQ.length == 12 && $scope.inputVO.I_PREMIUM_TRANSSEQ.substring(6, 8) == "73")) {
				//保費來源錄音序號為空白者或錄音序號檢核不正確
				$scope.inputVO.STATUS = "10";	//理專進件
				$scope.showMsg("■ 被保人高齡/保費來源錄音序號為空白，或有誤請與客服確認錄音序號");
			}
		}
		//繳款人高齡/保費來源錄音序號檢核 (契撤案件不須檢核)
		if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && ((($scope.inputVO.LOAN_CHK1_YN == 'Y' || $scope.inputVO.LOAN_CHK2_YN == 'Y' || $scope.inputVO.LOAN_CHK3_YN == 'Y' || $scope.inputVO.CD_CHK_YN == 'Y'
				|| $scope.inputVO.P_LOAN_APPLY_YN == 'Y' || $scope.inputVO.P_SALE_SENIOR_YN == "Y") && !isInNoChkLoan))) {
			//需要輸入繳款人保費來源錄音序號
			$scope.inputVO.P_PREMIUM_TRANSSEQ_NEED_YN = "Y";
			//繳款人高齡+保費來源=Y，任一成立則繳款人需輸入錄音序號
			if($scope.inputVO.P_PREMIUM_TRANSSEQ == "" || $scope.inputVO.P_PREMIUM_TRANSSEQ == null || $scope.inputVO.P_PREMIUM_TRANSSEQ == undefined ||
					!($scope.inputVO.P_PREMIUM_TRANSSEQ.length == 12 && $scope.inputVO.P_PREMIUM_TRANSSEQ.substring(6, 8) == "73")) {
				//保費來源錄音序號為空白者或錄音序號檢核不正確
				$scope.inputVO.STATUS = "10";	//理專進件
				$scope.showMsg("■ 繳款人高齡/保費來源錄音序號為空白，或有誤請與客服確認錄音序號");
			}
		}

		//保費來源拉選為貸款且為投資型商品
	    if($scope.inputVO.LOAN_SOURCE_YN == "Y" && $scope.inputVO.PRODUCT_TYPE != "1") {
	    	var findKyc = $filter('filter')($scope.mappingSet['IOT.INV_PRD_LOAN_KYC'], {LABEL: $scope.inputVO.CUST_RISK});
	    	if(findKyc.length == 0) {
	    		//保費來源為貸款，購買投資型商品KYC 風險等級應為參數中設定(C4)
	    		$scope.inputVO.STATUS = "10";	//理專進件
	    	}
	    }

		//要保人年齡>=64.5歲(要保人生日-要保書申請日)且購買投資型商品，躉繳保費低於100萬、分期繳保費低於30萬，則需檢核非常態錄音序號 (契撤案件不須檢核)
	    if($scope.inputVO.CANCEL_CONTRACT_YN != "Y" && $scope.inputVO.AB_SENIOR_YN == "Y"
	    	&& ($scope.inputVO.AB_TRANSSEQ == "" || $scope.inputVO.AB_TRANSSEQ == null || $scope.inputVO.AB_TRANSSEQ == undefined)) {
	    	$scope.inputVO.STATUS = "10";	//理專進件
	    	$scope.showMsg("■ 要保人年齡>=64.5歲且購買投資型商品，躉繳保費低於100萬、分期繳保費低於30萬，必須有非常態錄音序號");
	    }

        //檢視電子要保書所列示地址、電話、e-mail、手機與套印要保書是否相符
        if($scope.inputVO.QC_APEC == '3') { //不相符，塗改處客戶沒簽名
        	$scope.inputVO.STATUS = "10";	//理專進件
        }

        //檢視業報書前3個月貸款/保單借款、業報書前3個月保單解約之勾選與要保人保險購買檢核報表相符
        if($scope.inputVO.QC_LOAN_CHK == '2') { //不相符
        	$scope.inputVO.STATUS = "10";	//理專進件
        }

        //檢核要保書客戶與業務員之簽名欄位均有簽名
        if($scope.inputVO.QC_SIGNATURE == '2') { //有遺漏
        	$scope.inputVO.STATUS = "10";	//理專進件
        }
	}

	$scope.Submit = function(){
		debugger
		//投資型商品或非投資型但須適配商品，適配日(要保人保險購買檢核的鍵機日)須等於要保書申請日
		if($scope.checkFieldData() && ($scope.inputVO.PRODUCT_TYPE != '1' || $scope.inputVO.NEED_MATCH == 'Y')) {
			$scope.sendRecv("IOT120","getInvPreMatch","com.systex.jbranch.app.server.fps.iot120.IOT120InputVO",
				$scope.inputVO,function(tota,isError){
				debugger
				if(!isError){
					if(tota[0].body.PreMatchList == null || tota[0].body.PreMatchList.length <= 0){
						$scope.showMsg('ehl_01_iot120_006');
						return;
					} else {
						$scope.doSubmit();
					}
				}
			});
		} else {
			$scope.doSubmit();
		}
	}

	$scope.doSubmit = function() {
		//檢核項目
		$scope.chkItemsBeforeSave();

		$scope.sendRecv("IOT120", "chkValid", "com.systex.jbranch.app.server.fps.iot120.IOT120InputVO", {"RECRUIT_ID":$scope.inputVO.RECRUIT_ID, 
																										 "INSPRD_ID":$scope.inputVO.INSPRD_ID, 
																										 "APPLY_DATE":$scope.inputVO.APPLY_DATE},
			function(tota, isError) {
				let validVo = tota[0].body.REFList[0];
				if (validVo.EXPIRED=='Y')	{
					$scope.showMsg("壽險證照已超過法定換證期限無法進行招攬");
					return;
				}

				if (validVo.UNREG=='Y')	{
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
				if($scope.checkINS_RIDER_DTLList() && $scope.checkFieldData()){
					if($scope.checkin && $scope.checkout){
						$scope.to_back();
					}else{
						$scope.showErrorMsg('請確認分行留存文件檢核或保險送件文件檢核');
					}
				}
		});
	}


	//躉繳/分期繳關聯分期繳繳別
	$scope.linkmop2 = function(){
		if($scope.inputVO.PAY_TYPE != '1'){
			if($scope.OPR_STATUS == 'Read'){
				$scope.mop2lock = true;
			}else{
				$scope.mop2lock = false;
			}
		}else{
			$scope.inputVO.MOP2 = null;
			$scope.mop2lock = true;
		}
	}
	//新增附約
	$scope.addRow = function(){
		var value = "";
		angular.forEach($scope.mappingSet['IOT.RLT_WITH_INSURED'],function(row,index){
			row.Display_order = index + 1;
			$scope.requiredINS_RIDER_DTLList = true;
			if(row.LABEL == '本人'){
				value = row.DATA;
			}
		});
		var map = {
			RLT_WITH_INSURED :value
		}
		$scope.inputVO.INS_RIDER_DTLList.push(map);
	}
	//刪除附約
	$scope.deleteRow = function(index,row){
		var txtMsg = $filter('i18n')('ehl_02_common_001');
		$confirm({text: txtMsg},{size: 'sm'}).then(function(){
			$scope.inputVO.DELETE_INS_RIDER_DTLList = [];
			if(row.RIDER_DTL_KEYNO == undefined || row.RIDER_DTL_KEYNO == ""){
				$scope.inputVO.DELETE_INS_RIDER_DTLList.push()
			}else{
				$scope.inputVO.DELETE_INS_RIDER_DTLList.push(row.RIDER_DTL_KEYNO)
			}
			$scope.inputVO.INS_RIDER_DTLList.splice(index,1);
			angular.forEach($scope.inputVO.INS_RIDER_DTLList,function(row,index){
				row.Display_order = index+1;
			})
			if($scope.inputVO.INS_RIDER_DTLList.length > 0){
				$scope.requiredINS_RIDER_DTLList = true;
			}else{
				$scope.requiredINS_RIDER_DTLList = false;
			}
		});
	}
	//檢查驗印結果是否符合規定
	$scope.qc_stamp_check = function(){
		if($scope.inputVO.QC_STAMP == 'N'){
			$scope.check_QC_STAMP1 = true;
			$scope.check_QC_STAMP2 = true;
		}else{
			$scope.check_QC_STAMP1 = false;
			$scope.check_QC_STAMP2 = false;
		}
	}

	$scope.back_page_btn = function(){
		if($scope.OPR_STATUS == 'Read' || $scope.OPR_STATUS == 'UPDATE'){
	    	$scope.connector('set','IOT140_updateSubmit',true);
	    	$rootScope.menuItemInfo.url = "assets/txn/IOT140/IOT140.html";
		}
	}

	//實收保費&&基本保費加千分位
	$scope.chang_number=function(data,type){

		if(data.match(',') !=null){
			var change=data.replace(/,/g, '');
			if(type == 'basePremium'){
				$scope.inputVO.BASE_PREMIUM = $filter('number')(change);
			}
			if(type == 'realPremium'){
				$scope.inputVO.REAL_PREMIUM = $filter('number')(change);
			}
		}else{
			if(type == 'basePremium'){
				$scope.inputVO.BASE_PREMIUM = $filter('number')(data);
			}
			if(type == 'realPremium'){
				$scope.inputVO.REAL_PREMIUM = $filter('number')(data);
			}
		}

	}

	//附約保費轉千分位
	$scope.change_number_ins = function(row){
		row.PREMIUM = $scope.moneyFormat(row.PREMIUM);
	}

	//webservice date民國年月日轉date.getTime
	$scope.webserviceDatechange = function(date){
		var year = parseInt(date.substr(0,3))+1911;
		var month = date.substr(3,2);
		var day = date.substr(5,2);
		var redate = new Date(year+'-'+month+'-'+day).getTime();
		return redate;
	}

});
