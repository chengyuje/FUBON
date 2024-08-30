/**
 *
 */
'use strict';
eSoafApp.controller('IOT150Controller',
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,getParameter,$filter,$timeout , $window) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "IOT150Controller";


     // date picker
		// 有效起始日期
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_applysDateOptions = {
				maxDate:$scope.applymaxDate,
				minDate:$scope.applyminDate
		};
		$scope.bgn_applyeDateOptions = {
				maxDate:$scope.applymaxDate,
				minDate:$scope.applyminDate
		}
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.KEYIN_DATE_T || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.KEYIN_DATE_F || $scope.minDate;
			if($scope.inputVO.KEYIN_DATE_F != undefined && $scope.inputVO.KEYIN_DATE_T != undefined){
				var math_day = ($scope.inputVO.KEYIN_DATE_T-$scope.inputVO.KEYIN_DATE_F)/(1000 * 60 * 60 * 24)
				if(math_day >31){
					$scope.showErrorMsg('ehl_01_IOT150_001');
					$scope.inputVO.KEYIN_DATE_F = undefined;
					$scope.inputVO.KEYIN_DATE_T = undefined;
				}
			}
		};
		$scope.applylimitDate = function(){
			$scope.bgn_applysDateOptions.maxDate = $scope.inputVO.APPLY_DATE_T || $scope.applymaxDate;
			$scope.bgn_applyeDateOptions.minDate = $scope.inputVO.APPLY_DATE_F || $scope.applyminDate;
			if($scope.inputVO.APPLY_DATE_F != undefined && $scope.inputVO.APPLY_DATE_T != undefined){
				var math_day = ($scope.inputVO.APPLY_DATE_T-$scope.inputVO.APPLY_DATE_F)/(1000 * 60 * 60 * 24)
				if(math_day >31){
					$scope.showErrorMsg('ehl_01_IOT150_001');
					$scope.inputVO.APPLY_DATE_F = undefined;
					$scope.inputVO.APPLY_DATE_T = undefined;
				}
			}
		}
		//初始化
        $scope.init = function(){
        	$scope.hasRegType = false;
    		$scope.showReasonList = false;
        	$scope.othertable = false;
        	$scope.othertext=false;
        	$scope.print = true;
        	$scope.exchangeShow = false;
        	$scope.newShow = false;
        	$scope.newDataShow = false;
        	$scope.otherShow = false;
        	$scope.otherDataShow = false;
        	$scope.hasRegType_text = '';
        	$scope.signViewFileShow = false;
        	$scope.inputVO = {
        		OP_BATCH_NO:undefined,
        		REG_TYPE:'',
        		STATUS:'',
        		BRANCH_NBR:'',
        		INS_ID:'',
        		B_OVER_DAYS:'',
        		H_OVER_DAYS:'',
        		CUST_ID:'',
        		KEYIN_DATE_F:undefined,
        		KEYIN_DATE_T:undefined,
        		APPLY_DATE_F:undefined,
        		APPLY_DATE_T:undefined,
        		POLICY_NO1:'',
        		POLICY_NO2:'',
        		POLICY_NO3:'',
        		PROD_NAME:'',
        		PROD_ALL:'',
        		BEF_SIGN_OPRID:'',
        		BEF_SIGN_DATE:undefined,
        		SIGN_OPRID:'',
        		SIGN_DATE:undefined,
        		AFT_SIGN_OPRID:'',
        		AFT_SIGN_DATE:undefined,
        		BATCH_SETUP_EMPID:'',
        		BATCH_SETUP_DATE: undefined
        	}
        };
        $scope.init();

        //查詢欄位清空
        $scope.cleartext = function(){
    		$scope.inputVO.OP_BATCH_NO=undefined;
    		$scope.inputVO.STATUS='';
    		$scope.inputVO.BRANCH_NBR='';
    		$scope.inputVO.INS_ID='';
    		$scope.inputVO.B_OVER_DAYS='';
    		$scope.inputVO.H_OVER_DAYS='';
    		$scope.inputVO.CUST_ID='';
    		$scope.inputVO.KEYIN_DATE_F=undefined;
    		$scope.inputVO.KEYIN_DATE_T=undefined;
    		$scope.inputVO.APPLY_DATE_F=undefined;
    		$scope.inputVO.APPLY_DATE_T=undefined;
    		$scope.inputVO.POLICY_NO1='';
    		$scope.inputVO.POLICY_NO2='';
    		$scope.inputVO.POLICY_NO3='';
    		$scope.inputVO.PROD_NAME='';
    		$scope.inputVO.PROD_ALL='';
    		$scope.inputVO.BEF_SIGN_OPRID='';
    		$scope.inputVO.BEF_SIGN_DATE=undefined;
    		$scope.inputVO.SIGN_OPRID='';
    		$scope.inputVO.SIGN_DATE=undefined;
    		$scope.inputVO.AFT_SIGN_OPRID='';
    		$scope.inputVO.AFT_SIGN_DATE=undefined;
    		$scope.IOT_MAINList=[];
    		$scope.outputVO=[];
        }

        $scope.touppcase_OP_BATCH_NO = function(OP_BATCH_NO){
        	if(OP_BATCH_NO != undefined){
        		$scope.inputVO.OP_BATCH_NO = OP_BATCH_NO.toUpperCase();
        	}
        }


        $scope.showSearchCondition = function(){
        	if($scope.inputVO.REG_TYPE != ''){
        		$scope.hasRegType = true;
        	}else{
        		$scope.hasRegType = false;
        	}
        	switch ($scope.inputVO.REG_TYPE) {
			case '1':
				$scope.newShow = true;
	        	$scope.exchangeShow = false;
	        	$scope.otherShow = false;
				$scope.othertext=false;
				$scope.othertable=false;
				$scope.hasRegType_text = '，鍵機日與要保書填寫申請日擇一必填';
				$scope.cleartext();
				break;
			case '2':
				$scope.otherShow = true;
	        	$scope.newShow = false;
	        	$scope.exchangeShow = false;
				$scope.othertext=false;
				$scope.othertable=false;
				$scope.hasRegType_text = '';
				$scope.cleartext();
				break;
			case '3':
				$scope.othertable=true;
				$scope.othertext=true;
				$scope.exchangeShow = true;
	        	$scope.newShow = true;
	        	$scope.otherShow = false;
				$scope.hasRegType_text = '，鍵機日與約定書申請日擇一必填';
	        	$scope.cleartext();
				break;
			default:
				$scope.exchangeShow = false;
        		$scope.newShow = false;
        		$scope.otherShow = false;
				$scope.othertext=false;
				$scope.othertable=false;
				$scope.hasRegType_text = '';
				$scope.cleartext();
				break;
			}
        }


        //clear
        $scope.btnClear = function(){
        	$scope.init();
        }

        //分頁初始化
//        $scope.inquireInit = function(){
//        	$scope.questionList = [];
//        };
//        $scope.inquireInit();

			getParameter.XML(["IOT.MAIN_STATUS","IOT.PAY_TYPE","IOT.REG4_STATUS","IOT.SUBMIT_WAY","IOT.BATCH_SEQ","IOT.OTH_TYPE","IOT.MAPPVIDEO_CHK_STEP","IOT.MAPPVIDEO_CHK_CODE","COMMON.YES_NO"], function(totas) {
			   if (totas) {
				//狀態
			    $scope.mappingSet['IOT.MAIN_STATUS'] = totas.data[totas.key.indexOf('IOT.MAIN_STATUS')];
			    $scope.mappingSet['IOT.PAY_TYPE'] = totas.data[totas.key.indexOf('IOT.PAY_TYPE')];
			    $scope.mappingSet['IOT.REG4_STATUS'] = totas.data[totas.key.indexOf('IOT.REG4_STATUS')];
			    $scope.mappingSet['IOT.SUBMIT_WAY'] = totas.data[totas.key.indexOf('IOT.SUBMIT_WAY')];
			    $scope.mappingSet['IOT.BATCH_SEQ'] = totas.data[totas.key.indexOf('IOT.BATCH_SEQ')];
			    $scope.mappingSet['IOT.OTH_TYPE'] = totas.data[totas.key.indexOf('IOT.OTH_TYPE')];
			    $scope.mappingSet['IOT.MAPPVIDEO_CHK_STEP'] = totas.data[totas.key.indexOf('IOT.MAPPVIDEO_CHK_STEP')];
			    $scope.mappingSet['IOT.MAPPVIDEO_CHK_CODE'] = totas.data[totas.key.indexOf('IOT.MAPPVIDEO_CHK_CODE')];
			    $scope.mappingSet['COMMON.YES_NO'] = totas.data[totas.key.indexOf('COMMON.YES_NO')];
			   }
			  });

		$scope.checkqueryField = function(){
			if($scope.inputVO.INS_ID != '' || $scope.inputVO.OP_BATCH_NO !=undefined || $scope.inputVO.REG_TYPE != ''){
				if($scope.inputVO.REG_TYPE !='' && $scope.inputVO.OP_BATCH_NO == undefined && $scope.inputVO.INS_ID == ''){
					//鍵機日 OR 要保書填寫申請日必填
					if($scope.inputVO.KEYIN_DATE_F != undefined || $scope.inputVO.KEYIN_DATE_T != undefined//鍵機日
							|| $scope.inputVO.APPLY_DATE_F != undefined || $scope.inputVO.APPLY_DATE_T != undefined//約定書申請日
							|| $scope.inputVO.BEF_SIGN_DATE != undefined || $scope.inputVO.BATCH_SETUP_DATE != undefined){ //點收日期(簽署前) 設定批次日期
						return true;
					}else{
						return false;
					}
				}else{
					return true;
				}
			}else{
				return false;
			}
		}

        //查詢
        $scope.queryData = function(){
        	if($scope.inputVO.STATUS == '60'){
        		$scope.print = false;
        	}

        	if($scope.checkqueryField()){
        		$scope.signViewFileShow = false;
               	$scope.sendRecv("IOT150","queryData","com.systex.jbranch.app.server.fps.iot150.IOT150InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			if(tota[0].body.IOT_MAIN.length <=0){
		               				$scope.IOT_MAINList = tota[0].body.IOT_MAIN;
			                   		$scope.outputVO = tota[0].body;
			                   		$scope.csvList = tota[0].body.IOT_MAIN;
		               				$scope.showMsg("ehl_01_common_009");
		               			}
		               			else{
			                   		$scope.IOT_MAINList = tota[0].body.IOT_MAIN;
			                   		$scope.outputVO = tota[0].body;
			                   		$scope.csvList = tota[0].body.IOT_MAIN;
			                   		if($scope.IOT_MAINList.length>0){
				                   		switch ($scope.IOT_MAINList[0].REG_TYPE) {
										case 1:
											$scope.newDataShow = true;
											$scope.otherDataShow = false;
											break;
										case 2:
											$scope.newDataShow = false;
											$scope.otherDataShow = true;
											break;
										default:
											break;
										}
			                   		}
		               			}

		               			var isEtop = false;

		               			for(var i = 0 ; i < $scope.IOT_MAINList.length ; i++){
		               				var opBatchNo = $scope.IOT_MAINList[i].OP_BATCH_NO;
		               				opBatchNo == undefined ? '' : opBatchNo;
		               				$scope.IOT_MAINList[i].isOpBatchNonewEtopShow = RegExp('^[EN].*').test(opBatchNo);
		               				$scope.IOT_MAINList[i].isOpBatchS = RegExp('^S.*').test(opBatchNo);
		               				//TODO 測試案例要還原
		               				//$scope.IOT_MAINList[i].isOpBatchNonewEtopShow = RegExp('^P.*').test(opBatchNo);
		               				//$scope.IOT_MAINList[i].STATUS = 40;
		               				isEtop = isEtop || $scope.IOT_MAINList[i].isOpBatchNonewEtopShow || $scope.IOT_MAINList[i].isOpBatchS;
		               			}

		               			$scope.signViewFileShow = isEtop;
		               		}
            	});
        	}else{
        		$scope.showErrorMsg("ehl_01_common_022");
        	}
        }

        $scope.setBatch = function(){
			var dialog = ngDialog.open({
				template: 'assets/txn/IOT160/IOT160.html',
				className: 'IOT160',
				showClose: false,
                controller: ['$scope', function($scope) {
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful') {
//					$scope.inquire();
				}
			});
        }

        $scope.selectall = function(){
        	debugger;
        	if($scope.inputVO.selectPass_all == 'Y'){
        		for(var a = 0;a<$scope.IOT_MAINList.length;a++){
        			$scope.IOT_MAINList[a].pass = 'Y';
        			$scope.IOT_MAINList[a].nopass = 'N';
        			$scope.chkXsetStatus($scope.IOT_MAINList[a], 'PASS');
        		}
        	}else{
        		for(var a = 0;a<$scope.IOT_MAINList.length;a++){
        			$scope.IOT_MAINList[a].pass = 'N';
        			$scope.IOT_MAINList[a].next_status = '';
        		}
        	}
        	if($scope.inputVO.selectNoPass_all == 'Y'){
        		for(var a = 0;a<$scope.IOT_MAINList.length;a++){
        			$scope.IOT_MAINList[a].nopass = 'Y';
        			$scope.IOT_MAINList[a].pass = 'N';
        			$scope.chkXsetStatus($scope.IOT_MAINList[a], 'REJECT');
        		}
        	}else{
        		for(var a = 0;a<$scope.IOT_MAINList.length;a++){
        			$scope.IOT_MAINList[a].nopass = 'N';
        			$scope.IOT_MAINList[a].next_status = '';
        		}
        	}
        }

        $scope.chkXsetStatus = function(row,in_opr){
        	debugger;
        	if(row.pass == 'Y'){
        		if($scope.inputVO.selectNoPass_all != undefined){
        			if($scope.inputVO.selectNoPass_all == 'Y'){
        				$scope.inputVO.selectNoPass_all = 'N';
        			}
        		}

        		$scope.IOT920inputVO = {
        				in_opr:in_opr,
        				curr_status:row.STATUS,
        				SIGN_INC:row.SIGN_INC,
        				INS_KIND:row.INS_KIND,
        				REG_TYPE:row.REG_TYPE,
        				MAPPVIDEO_YN:row.MAPPVIDEO_YN,
        				PREMATCH_SEQ:row.PREMATCH_SEQ,
        				MAPP_CHKLIST_TYPE:'3' //總行檢核視訊投保
        		}
               	$scope.sendRecv("IOT920","chkXsetStatus","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",$scope.IOT920inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			var message = tota[0].body.Message;
		               			if(message != null && message != undefined && message != "") {
		               				row.pass='N';
		               				$scope.showErrorMsg(message);
		               			} else {
			               			$scope.next_statusList = tota[0].body.next_statusList;
			               			if($scope.next_statusList.length>0){
				               			row.next_status = $scope.next_statusList[0].NEXT_STATUS;
			               			}else{
			               				row.pass='N';
			               				$scope.showErrorMsg('ehl_02_IOT_003');
			               			}
		               			}
		               		}
            	});
        	}else{
        		row.next_status = '';
        	}
        	if(row.nopass == 'Y'){
        		$scope.showReasonList = true;
        		row.nopassreason = '';
        		row.REJ_REASON_OTH = '';
        		if($scope.inputVO.selectPass_all != undefined){
        			if($scope.inputVO.selectPass_all == 'Y'){
        				$scope.inputVO.selectPass_all = 'N';
        			}
        		}
        		$scope.IOT920inputVO = {
        				in_opr:in_opr,
        				curr_status:row.STATUS,
        				SIGN_INC:row.SIGN_INC,
        				INS_KIND:row.INS_KIND,
        				REG_TYPE:row.REG_TYPE
        		}
               	$scope.sendRecv("IOT920","chkXsetStatus","com.systex.jbranch.app.server.fps.iot920.IOT920InputVO",$scope.IOT920inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			$scope.next_statusList = tota[0].body.next_statusList;
		               			if($scope.next_statusList.length>0){
			               			row.next_status = $scope.next_statusList[0].NEXT_STATUS;
		               			}else{
		               				row.nopass='N';
		               				if($scope.inputVO.selectNoPass_all == 'Y'){
		               					$scope.inputVO.selectNoPass_all='N'
		               				}
		               				$scope.showErrorMsg('ehl_02_IOT_003');
		               			}
		               		}
            	});
        	}else{
        		row.next_status = '';
        	}
        }

        $scope.submit = function(){
        	if($scope.IOT_MAINList != undefined){
        		$scope.inputVO.IOT_MAINList = $scope.IOT_MAINList;
               	$scope.sendRecv("IOT150","submit","com.systex.jbranch.app.server.fps.iot150.IOT150InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){
		               			if(tota[0].body){
		               				switch ($scope.inputVO.REG_TYPE) {
									case '3'://匯利專案
			               				$scope.queryData();
			               				if($scope.inputVO.selectNoPass_all == 'Y'){
			               					$scope.inputVO.selectNoPass_all='N'
			               				}
			               				if($scope.inputVO.selectPass_all == 'Y'){
			               					$scope.inputVO.selectPass_all='N'
			               				}
										break;
									default:
										$scope.queryData();
//		               					$timeout(function(){$scope.Report();},500);
		               					if($scope.inputVO.selectNoPass_all == 'Y'){
			               					$scope.inputVO.selectNoPass_all='N'
			               				}
			               				if($scope.inputVO.selectPass_all == 'Y'){
			               					$scope.inputVO.selectPass_all='N'
			               				}
										break;
									}

		               			}
		               		}
            	});
        	}

        }

        $scope.Report = function(){
        	if($scope.IOT_MAINList != undefined){
        		$scope.inputVO.IOT_MAINList = $scope.IOT_MAINList;
               	$scope.sendRecv("IOT150","Report","com.systex.jbranch.app.server.fps.iot150.IOT150InputVO",$scope.inputVO,
            			function(tota,isError){
		               		if(!isError){

		               		}
            	});
        	}

        }

        $scope.export = function(){
        	if($scope.IOT_MAINList != undefined){
	        	$scope.inputVO.IOT_MAINList = $scope.IOT_MAINList;
	        	$scope.sendRecv("IOT150","export","com.systex.jbranch.app.server.fps.iot150.IOT150InputVO",$scope.inputVO,function(tota,isError){
	        		if (!isError) {
//	        			$scope.paramList = tota[0].body.resultList;
//	        			$scope.outputVO = tota[0].body;
//	        			return;
	        		}
	        	});
        	}
        }

        $scope.chkOpBatchNoTop = function(opBatchNo , topChr){
        	var regexpObj = new RegExp('^' + topChr + '.*');
        	var result = false;
        	console.log(topChr);
        	console.log(opBatchNo);
        	console.log(result = regexpObj.test(opBatchNo));
        	return result;
        }

        //簽署系統
        $scope.signView = function(type){
        	$scope.sendRecv("WmsSsoService" , "tokenGeneration" , "java.util.HashMap" , {SYS_CODE : 'SIG'} , function(tota,isError){
        		if (!isError) {
        			$window.open(tota[0].body);
        			return;
        		}
        	});
        }

        //人壽簽署系統(視訊投保)
        $scope.signINSView = function(){
        	$scope.sendRecv("SSO002" , "takeToken" , "java.util.HashMap" , {id : sysInfoService.getUserID()} ,
        		function(tota,isError){
	        		if (!isError) {
	        			if(tota[0].body.errMsg) {
	        				$scope.showErrorMsg(tota[0].body.errCode + ":" + tota[0].body.errMsg);
	        			} else {
	        				$window.open(tota[0].body.INSSIG_URL);
	        			}
	        			return;
	        		}
        	});
        }

        //檢視文件
        $scope.pdfFile = function(ins_id){
           	$scope.sendRecv("IOT150" , "getPdfFile" , "java.util.HashMap" , {INS_ID : ins_id} , function(tota , isError){
           		if(!isError){}
        	});
        }

        //開啟視訊簽單影像品質檢核項目視窗
        $scope.openMAPPVideoDialog = function(row) {
        	debugger
			var pseq = row.PREMATCH_SEQ;
			var custId = row.CUST_ID;
			var insId = row.INS_ID;
			var chkType = (row.STATUS != 40 && row.STATUS != 62) ? "3" : "";
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
				}]
			});
			dialog.closePromise.then(function(data){
				//
			});
        }

      //開啟視訊簽單影像視窗
        $scope.openMAPPVideo = function(row) {
        	var caseid = row.CASE_ID;	//J600000071

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

        //人壽待簽署管理報表
        $scope.insSigRpt = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/IOT150/IOT150_INSSIGRPT.html',
				className: 'IOT150_INSSIGRPT',
				showClose: false,
                controller: ['$scope', function($scope) {

                }]
			});
		};


		// （Popup 開啟）非富壽_送件明細表產生暨補印
		$scope.notFbGenPrint = function() {
			const dialog = ngDialog.open({
				template: 'assets/txn/IOT150/IOT150_notFbGenPrint.html',
				className: 'IOT150_notFbGenPrint',
				controller:['$scope',function($scope){

				}]
			});
			dialog.closePromise.then(function(data){
			});
		}

		// （Popup 開啟）非富壽_送件明細表保險公司簽收回覆
		$scope.notFbSigReview = function() {
			const dialog = ngDialog.open({
				template: 'assets/txn/IOT150/IOT150_notFbSigReview.html',
				className: 'IOT150_notFbSigReview',
				controller:['$scope',function($scope){

				}]
			});
			dialog.closePromise.then(function(data){
			});
		}
		
		//保險資金電訪報表
		$scope.exportRecordYN = function(){
        	$scope.sendRecv("IOT150","exportRecordYN","com.systex.jbranch.app.server.fps.iot150.IOT150InputVO",$scope.inputVO,function(tota,isError){
	        	if (!isError) {
	        	}
	        });
        }
	});
