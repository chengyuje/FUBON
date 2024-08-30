/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG130_SASController',
function($rootScope, $scope, $controller, $confirm,  socketService, ngDialog, projInfoService, sysInfoService, getParameter,$http) {

	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "ORG130_SASController";
		
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["ORG.FINANCIAL_EXP", "ORG.PRE_FIN_INST", "ORG.REQ_CERTIFICATE", "ORG.MEET_JOB"], function(totas) {
		if (totas) {
			$scope.mappingSet['ORG.FINANCIAL_EXP'] = totas.data[totas.key.indexOf('ORG.FINANCIAL_EXP')];
			$scope.mappingSet['ORG.PRE_FIN_INST'] = totas.data[totas.key.indexOf('ORG.PRE_FIN_INST')];
			$scope.mappingSet['ORG.REQ_CERTIFICATE'] = totas.data[totas.key.indexOf('ORG.REQ_CERTIFICATE')];
			$scope.mappingSet['ORG.MEET_JOB'] = totas.data[totas.key.indexOf('ORG.MEET_JOB')];
		}
	});
	
    //config
	$scope.model = {};
		
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.priID = sysInfoService.getPriID();
	$scope.inputVO.bookedOnboardDate = new Date();
    // generatePDF
    $scope.go = function (where, type) {
   		$('#showSpan').hide();
//   		$('input').css("border","0");
//   		$('textarea').css("border","0");
        $scope.inputVO.downloadBtn = true;
	    if (where == 'generatePDF') {
	       // show DN & creapy man
            var dialog = ngDialog.open({
                template: '<div><img style="display:block; margin:auto;vertical-align:middle;" width="150px" height="150px" src="./resource/loading.gif"></div>',
                plain: true,
                showClose: false,
                closeByDocument: false,
                closeByEscape: false,
                className: 'loading-Img'
            });

	        html2Pdf.getPdf(['*[print]'], 'blob', null, {
	            pageFormat: 'a4',
	            pdfName: '保險規劃書.pdf',
	        })
	        .then(function (data) {
	        	uploadPdf('test.pdf', data);
	    		$('#showSpan').show();
	       		$('input').css("border","black 1");
	       		$('textarea').css("border","black 1");
	            dialog.close();
	        })
	        .catch(function (e) {
	        	console.log(e);
	        	dialog.close();
	        });;
	    }
	};

	    // upload pdf
	var uploadPdf = function (fileName, data) {

		if (!data) return false;

		var formData = new FormData();
		var uploadFileName = uuid();
		var fileSize = data.size;
		var mimeType = '.pdf';
		var tempFileName = uploadFileName + mimeType;
		var file = new File([data], fileName, {
			type: 'application/pdf ',
			name: tempFileName
		});
		formData.append(tempFileName, file);

		$http.post('FileUpload', formData, {
			transformRequest: angular.identity,
			headers: {
				'Content-Type': undefined
			}
		})
		.then(function (jqXHR) {
			if (jqXHR.status === 200) {
				var url = window.URL.createObjectURL(data);
				window.open(url);
				
			}
		}, function (error) {
			console.log(error);
		});
	};


	$scope.clear = function () {
		$scope.inputVO.req_certificate 		= [];
		$scope.inputVO.financial_exp		= "";		//相關金融工作資歷(參數維護)
		
		$scope.inputVO.pre_fin_inst 		= "";		//前職金融機構(參數維護1～8)
		$scope.inputVO.other_fi 			= "";		//前職金融機構 (7: 其他金融機構)
		$scope.inputVO.other_pre_fin_inst 	= "";		//前職金融機構(8.其他分行名稱)
			
		$scope.inputVO.cur_m_goal			= undefined;//業績目標
		$scope.inputVO.actual_accomplish	= undefined;//實際達成(前職績效表現)
		$scope.inputVO.cur_aum				= undefined;//管理AUM
		$scope.inputVO.cust_cnt				= undefined;//管理客戶數(前職績效表現)

		$scope.inputVO.resign_reason		= "";		//離職原因
			
		$scope.inputVO.cust_satisfaction	= "";		//客戶紛爭/滿意度
			
		$scope.inputVO.communication		= "";		//溝通協調性
		$scope.inputVO.problem_solving_skill= "";		//解決問題能力
		$scope.inputVO.evDate   =   "";//面談評估日期
		$scope.inputVO.honest   =   "";//誠信
		$scope.inputVO.kindly   =   "";//
		$scope.inputVO.prof =   "";
		$scope.inputVo.innov    =   "";
		$scope.inputVO.organize =   "";
		$scope.inputVO.selfselfImprove  =   "";
		$scope.inputVO.resign_reason =   "";
		$scope.inputVO.expectation  =   "";
        $scope.inputVO.bookedOnboardDate    =   "";
        $scope.inputVO.expSalary    =   undefined;
        $scope.inputVO.intv_sup_remark  =   "";
        $scope.inputVO.preMonthGoal =   undefined;
        $scope.inputVO.pre6MAccomplish  =   undefined;
        $scope.inputVO.exp6MAbility =   undefined;
        $scope.inputVO.exp1YAbility =   undefined;
        $scope.inputVO.expPropertyLoan  =   undefined;
        $scope.inputVO.expCreditLoan    =   undefined;
        $scope.inputVO.suggest_job  =   "";
        $scope.inputVO.hire_status  =   "";
        $scope.inputVO.hireStatusTransRemark    =   "";
        $scope.inputVO.prefType =   "";
        $scope.inputVO.buLayer1 =   "";
        $scope.inputVO.buLayer2 =   "";
        $scope.inputVO.buLayer3 =   "";
        $scope.inputVO.buLayer4 =   "";
        $scope.inputVO.buLayer5 =   "";
        $scope.inputVO.buLayer6 =   "";
        $scope.inputVO.buLayer7 =   "";
        $scope.inputVO.buLayer8 =   "";
        $scope.inputVO.crosBuLayer1 =   "";
        $scope.inputVO.crosBuLayer2 =   "";
        $scope.inputVO.crosBuLayer3 =   "";
        $scope.inputVO.crosBuLayer4 =   "";
        $scope.inputVO.crosBuLayer5 =   "";
        $scope.inputVO.crosBuLayer6 =   "";
        $scope.inputVO.crosBuLayer7 =   "";
        $scope.inputVO.crosBuLayer8 =   "";
        
	};
		
	$scope.setValue = function () {
		$scope.inputVO.seq                      = $scope.row.SEQ;
		$scope.inputVO.branch_nbr				= $scope.row.BRANCH_NBR;
		$scope.inputVO.cust_id 					= $scope.row.CUST_ID;				//應徵者身份證編號
		$scope.inputVO.status 					= $scope.row.STATUS;				//目前狀態
		$scope.inputVO.review_status 			= $scope.row.REVIEW_STATUS;			//主管覆核狀態：Y-已覆核/W-待覆核/N-退回
		$scope.inputVO.hire_status 				= $scope.row.HIRE_STATUS;			//進用狀態：0-無/1-通過/2-不通過/3-轉介至他行/4-暫存
		$scope.inputVO.emp_name					= $scope.row.EMP_NAME;				//應徵者姓名
		$scope.inputVO.rc_id 					= $scope.row.REGION_CENTER_ID;		//區域中心代碼
		$scope.inputVO.op_id 					= $scope.row.BRANCH_AREA_ID;		//營運區代碼
		$scope.inputVO.br_id 					= $scope.row.BRANCH_NBR;			//分行代碼
		$scope.inputVO.recommender_emp_id 		= $scope.row.RECOMMENDER_EMP_ID;	//推薦人
		$scope.inputVO.recommender_emp_name 	= $scope.row.RECOMMENDER_EMP_NAME;	//推薦人
		$scope.inputVO.ao_job_rank				= $scope.row.AO_JOB_RANK;			//職務
		$scope.inputVO.financial_exp			= $scope.row.FINANCIAL_EXP;			//相關金融工作資歷(參數維護)
		$scope.inputVO.prev_job 				= $scope.row.PREV_JOB;				//前職
		$scope.inputVO.cur_m_goal				= $scope.row.CUR_MONTHLY_GOAL;		//業績目標
		$scope.inputVO.actual_accomplish		= $scope.row.ACTUAL_ACCOMPLISH;		//實際達成(前職績效表現)
		$scope.inputVO.cur_aum					= $scope.row.CUR_AUM;				//管理AUM
		$scope.inputVO.cust_cnt					= $scope.row.CUST_CNT;				//管理客戶數(前職績效表現)
		$scope.inputVO.cust_satisfaction		= $scope.row.CUST_SATISFACTION;		//客戶紛爭/滿意度
		$scope.inputVO.resign_reason			= $scope.row.RESIGN_REASON;			//離職原因
		$scope.inputVO.achievement				= $scope.row.ACHIEVEMENT;			//成就導向
		$scope.inputVO.sales_skill 				= $scope.row.SALES_SKILL;			//銷售技巧
		$scope.inputVO.active					= $scope.row.ACTIVE;				//積極主動性
		$scope.inputVO.pressure_manage			= $scope.row.PRESSURE_MANAGE;		//抗壓性
		$scope.inputVO.communication			= $scope.row.COMMUNICATION;			//溝通協調性
	//			$scope.inputVO.problem_solving_skill	= $scope.row.PROBLEM_SOLVING_SKILL;	//解決問題能力
		$scope.inputVO.intv_sup_remark 			= $scope.row.INTV_SUP_REMARK;		//面談主管評語
		$scope.inputVO.pre_fin_inst 			= $scope.row.PRE_FIN_INST;			//前職金融機構(參數維護1～8)
		$scope.inputVO.other_fi 				= $scope.row.OTHER_FI;				//前職金融機構 (7: 其他金融機構)
		$scope.inputVO.other_pre_fin_inst 		= $scope.row.OTHER_PRE_FIN_INST;	//前職金融機構(8.其他分行名稱)
		$scope.inputVO.fee_6m_ability			= $scope.row.FEE_6M_ABILITY;		//評估未來6M手收能力
		$scope.inputVO.fee_1y_ability			= $scope.row.FEE_1Y_ABILITY;		//評估未來1Y手收能力
		$scope.inputVO.suggest_job				= $scope.row.SUGGEST_JOB;			//建議職等職稱
	//			$scope.inputVO.suggest_salary			= $scope.row.SUGGEST_SALARY;		//建議薪資(整包月薪)
	
		$scope.inputVO.seqno =   $scope.row.SEQNO;
		$scope.inputVO.communication		= $scope.row.COMMUNICATION;		//溝通協調性
		$scope.inputVO.problem_solving_skill= $scope.row.PROBLEM_SOLVING_SKILL;		//解決問題能力
	    $scope.inputVO.evDate   =   $scope.row.EV_DATE == null?new Date():new Date($scope.row.EV_DATE);//面談評估日期
	    $scope.inputVO.honest   =   $scope.row.HONEST;//誠信
	    $scope.inputVO.kindly   =   $scope.row.KINDLY;//
	    $scope.inputVO.prof =   $scope.row.PROFESSION;
	    $scope.inputVO.innov    =   $scope.row.INNOVATION;
	    $scope.inputVO.organize =   $scope.row.ORGANIZE;
	    $scope.inputVO.selfImprove  =   $scope.row.SELF_IMPROVE;
		$scope.inputVO.resign_reason =   $scope.row.RESIGN_REASON;
	    $scope.inputVO.expectation  =   $scope.row.EXPECTATION;
	    $scope.inputVO.bookedOnboardDate    =   new Date($scope.row.BOOKED_ONBOARD_DATE);
	    $scope.inputVO.expSalary    =   $scope.row.EXP_SALARY;
	    $scope.inputVO.intv_sup_remark  =   $scope.row.INTV_SUP_REMARK;
	    $scope.inputVO.preMonthGoal =   $scope.row.PRE_MONTHLY_GOAL;
	    $scope.inputVO.pre6MAccomplish  =   $scope.row.PRE_6M_ACCOMPLISH;
	    $scope.inputVO.exp6MAbility =   $scope.row.EXP_6M_ABILITY;
	    $scope.inputVO.exp1YAbility =   $scope.row.EXP_1Y_ABILITY;
	    $scope.inputVO.expPropertyLoan  =   $scope.row.EXP_PROPERTY_LOAN;
	    $scope.inputVO.expCreditLoan    =   $scope.row.EXP_CREDIT_LOAN;
	    $scope.inputVO.suggest_job  =   $scope.row.SUGGEST_JOB;
	    $scope.inputVO.hire_status  =   $scope.row.HIRE_STATUS;
	    $scope.inputVO.hireStatusTransRemark    =   $scope.row.HIRE_STATUS_TRANS_REMARK;
	    $scope.inputVO.prefType =   $scope.row.PERF_TYPE;
	    $scope.inputVO.buLayer1 =   $scope.row.BU_LAYER1;
	    $scope.inputVO.buLayer2 =   $scope.row.BU_LAYER2;
	    $scope.inputVO.buLayer3 =   $scope.row.BU_LAYER3;
	    $scope.inputVO.buLayer4 =   $scope.row.BU_LAYER4;
	    $scope.inputVO.buLayer5 =   $scope.row.BU_LAYER5;
	    $scope.inputVO.buLayer6 =   $scope.row.BU_LAYER6;
	    $scope.inputVO.buLayer7 =   $scope.row.BU_LAYER7;
	    $scope.inputVO.buLayer8 =   $scope.row.BU_LAYER8;
	    $scope.inputVO.crosBuLayer1 =   $scope.row.CROS_BU_LAYER1;
	    $scope.inputVO.crosBuLayer2 =   $scope.row.CROS_BU_LAYER2;
	    $scope.inputVO.crosBuLayer3 =   $scope.row.CROS_BU_LAYER3;
	    $scope.inputVO.crosBuLayer4 =   $scope.row.CROS_BU_LAYER4;
	    $scope.inputVO.crosBuLayer5 =   $scope.row.CROS_BU_LAYER5;
	    $scope.inputVO.crosBuLayer6 =   $scope.row.CROS_BU_LAYER6;
	    $scope.inputVO.crosBuLayer7 =   $scope.row.CROS_BU_LAYER7;
	    $scope.inputVO.crosBuLayer8 =   $scope.row.CROS_BU_LAYER8;
	    
	    $scope.inputVO.credit_status = $scope.row.CREDIT_STATUS; // add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
	    $scope.inputVO.credit_dtl = $scope.row.CREDIT_DTL; // add by ocean : WMS-CR-20190710-01_金控版面談評估表格式內容調整
	    
	    $scope.inputVO.fhcSeq =   $scope.row.FHC_SEQ;
		if ($scope.inputVO.ao_job_rank == "FC1" || 
			$scope.inputVO.ao_job_rank == "FC2" || 
			$scope.inputVO.ao_job_rank == "FC3" || 
			$scope.inputVO.ao_job_rank == "FC4" || 
			$scope.inputVO.ao_job_rank == "FC5" || 
			$scope.inputVO.ao_job_rank == "FCH"){
		} else {
			$scope.inputVO.type = 'text';
			$scope.inputVO.ao_job_rank1 = $scope.inputVO.ao_job_rank;
		}
		
		if($scope.row.REQ_CERTIFICATE){
	    	$scope.inputVO.req_certificate = $scope.row.REQ_CERTIFICATE.split(",");
		}else{
			$scope.inputVO.req_certificate = [];
		}
		
		$scope.inputVO.roleID =  sysInfoService.getRoleID();
		$scope.sendRecv("ORG130", "login", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO",$scope.inputVO,
			function(tota, isError) {
		 		if(!isError){
				   $scope.inputVO.mroleID = tota[0].body.roleID[0].COUNTS;
				}
	   });
	};
		
	$scope.init = function() {
		if($scope.row)
			$scope.isUpdate = true;
		
		$scope.inputVO = {
		}
		var tmp	=	{
			SEQ                  : $scope.row.SEQ,
			FHC_SEQ              : $scope.row.FHC_SEQ,
			BRANCH_NBR			 : $scope.row.BRANCH_NBR,
			CUST_ID 			 : $scope.row.CUST_ID,				//應徵者身份證編號
			STATUS 				 : $scope.row.STATUS,				//目前狀態
			REVIEW_STATUS		 : $scope.row.REVIEW_STATUS,		//主管覆核狀態：Y-已覆核/W-待覆核/N-退回
			EMP_NAME			 : $scope.row.EMP_NAME,				//應徵者姓名
			REGION_CENTER_ID	 : $scope.row.REGION_CENTER_ID,		//區域中心代碼
			BRANCH_AREA_ID 		 : $scope.row.BRANCH_AREA_ID,		//營運區代碼
			BRANCH_NBR 			 : $scope.row.BRANCH_NBR,			//分行代碼
			RECOMMENDER_EMP_ID	 : $scope.row.RECOMMENDER_EMP_ID,	//推薦人
			RECOMMENDER_EMP_NAME : $scope.row.RECOMMENDER_EMP_NAME,	//推薦人
			AO_JOB_RANK			 : $scope.row.AO_JOB_RANK,			//職務
			EV_DATE				 : new Date(),
			HONEST               : $scope.row.HONEST,
			KINDLY               : $scope.row.KINDLY,
			PROFESSION           : $scope.row.PROFESSION,
			INNOVATION           : $scope.row.INNOVATION,
			COMMUNICATION        : $scope.row.COMM,
			PROBLEM_SOLVING_SKILL: $scope.row.PSS,
			ORGANIZE             : $scope.row.ORG,
			SELF_IMPROVE         : $scope.row.SI,
			RESIGN_REASON        : $scope.row.RR,
			EXPECTATION          : $scope.row.EXPECTATION,
			BOOKED_ONBOARD_DATE  : $scope.row.BOD == null ? new Date() : $scope.row.BOD,
			EXP_SALARY           : $scope.row.EXP_SALARY,
			INTV_SUP_REMARK      : $scope.row.ISR,
			PRE_MONTHLY_GOAL     : $scope.row.PMG,
			PRE_6M_ACCOMPLISH    : $scope.row.P6A,
			EXP_6M_ABILITY       : $scope.row.E6A,
			EXP_1Y_ABILITY       : $scope.row.E1A,
			EXP_PROPERTY_LOAN    : $scope.row.EPL,
			EXP_CREDIT_LOAN      : $scope.row.ECL,
			SUGGEST_JOB          : $scope.row.SJ,
			HIRE_STATUS          : $scope.row.HS,
			HIRE_STATUS_TRANS_REMARK:$scope.row.HSTR,
			PERF_TYPE			 : $scope.row.PT,
			BU_LAYER1			 : $scope.row.BL1,
			BU_LAYER2			 : $scope.row.BL2,
			BU_LAYER3			 : $scope.row.BL3,
			BU_LAYER4			 : $scope.row.BL4,
			BU_LAYER5			 : $scope.row.BL5,
			BU_LAYER6			 : $scope.row.BL6,
			BU_LAYER7			 : $scope.row.BL7,
			BU_LAYER8			 : $scope.row.BL8,
			CROS_BU_LAYER1		 : $scope.row.CBL1,
			CROS_BU_LAYER2		 : $scope.row.CBL2,
			CROS_BU_LAYER3		 : $scope.row.CBL3,
			CROS_BU_LAYER4		 : $scope.row.CBL4,
			CROS_BU_LAYER5		 : $scope.row.CBL5,
			CROS_BU_LAYER6		 : $scope.row.CBL6,
			CROS_BU_LAYER7		 : $scope.row.CBL7,
			CROS_BU_LAYER8		 : $scope.row.CBL8, 
			CREDIT_STATUS		 : $scope.row.CREDIT_STATUS, 
			CREDIT_DTL		     : $scope.row.CREDIT_DTL
		};
        $scope.showSpan = true;//面談主管評估限制5字紅字顯示
		$scope.row = tmp;
		$scope.setValue();

		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
	};
	$scope.init();
	$scope.inputVO.login_id = sysInfoService.getUserID();

	//複選
    $scope.toggleSelection = function toggleSelection(data) {
    	var idx = $scope.inputVO.req_certificate.indexOf(data);
    	if (idx > -1) {
    		$scope.inputVO.req_certificate.splice(idx, 1);      //若已存在，就將它移除
    	} else {
    		$scope.inputVO.req_certificate.push(data);          //若不存在，就將它加入
    	}
    };
	
    $scope.changepre = function(){
    	if($scope.inputVO.pre_fin_inst=='8'){
    		$scope.inputVO.other_pre_fin_inst = $scope.inputVO.other_pre_fin_inst;
    	}else{
    		$scope.inputVO.other_pre_fin_inst = '';
    	}   
    	if($scope.inputVO.pre_fin_inst=='7'){
    		$scope.inputVO.other_fi = $scope.inputVO.other_fi;
    	}else{
    		$scope.inputVO.other_fi = '';  
    	}
		   
    };
	
	$scope.save = function(row) {
		if($scope.parameterTypeEditForm.$invalid){
    		$scope.showErrorMsg('欄位檢核錯誤:必輸條件(*)必須輸入!!');
    		return;
    	}	
		
		// 期望待遇NT$
//		if (typeof($scope.inputVO.expSalary)) {
//			if ($scope.inputVO.expSalary == '') {
//				$scope.inputVO.expSalary = 0;
//			} else {
//				$scope.showErrorMsg('欄位檢核錯誤:『期望待遇』請輸入數字!!');
//				return;
//			}
//		}
		
		if ($scope.inputVO.hire_status != "4")
            $scope.inputVO.hireStatusTransRemark = "";
		
		$confirm({text: '是否儲存：' + $scope.row.CUST_ID + '-' + $scope.row.EMP_NAME + '面談評估表?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG130", "saveInterviewHolding", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO",$scope.inputVO,
					function(tota, isError) {
					if(!isError){
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
							$scope.showMsg("ehl_01_common_007");
							return;
						}else{
							if(tota.length > 0) {
								$scope.showMsg("ehl_01_common_006");
								$scope.closeThisDialog('successful');
				        		
							}
						}
					}
		 			}
			);
		});
	};
	
	$scope.avoidNumberError = function(tag) {
		debugger;
		switch(tag) {
		case 1:
			if($scope.inputVO.preMonthGoal == "") $scope.inputVO.preMonthGoal =   undefined;							
			break;
		case 2:
			if($scope.inputVO.pre6MAccomplish == "") $scope.inputVO.pre6MAccomplish  =   undefined;
			break;
		case 3:
			if($scope.inputVO.exp6MAbility == "") $scope.inputVO.exp6MAbility =   undefined;
			break;
		case 4:
			if($scope.inputVO.exp1YAbility == "") $scope.inputVO.exp1YAbility =   undefined;
			break;
		case 5:
			if($scope.inputVO.expPropertyLoan == "") $scope.inputVO.expPropertyLoan  =   undefined;
			break;
		case 6:
			if($scope.inputVO.expCreditLoan == "") $scope.inputVO.expCreditLoan    =   undefined;
			break;	
		case 7:
			if($scope.inputVO.expSalary == "") $scope.inputVO.expSalary    =   undefined;
			break;	
		}	
	};
});