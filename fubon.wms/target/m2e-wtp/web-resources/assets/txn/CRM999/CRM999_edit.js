/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM999editController',
		function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "CRM999editController";

	/**
	 * 字數長度計算
	 */
	$scope.checkLenght = function(type, cloum, limit) {

		var text = $scope.inputVO[type];
		if (!text) {
			$scope[cloum] = limit;
		} else {
			var len = text.length;
			$scope[cloum] = parseInt(limit)-parseInt(len);
		}
	}

	$scope.disabledByLevel = function(inputElem, selectElem){
		$(':radio:not(:checked)').attr('disabled', true);
		$(inputElem).attr("readonly",true);
		$(inputElem).each(function(){
			$(this).focus(function(){
				$(this).blur();
			});
		});
	}


	/*--------------------------------------------------------------------------------------------
	 * 初始化
	 * --------------------------------------------------------------------------------------------
	 */

	/*
	 * 載入初始資訊
	 */
	$scope.start = function() {
		$scope.inputVO = angular.copy($scope.preInputVO);

		//textarea字數判斷
		$scope.checkLenght('handleCondition1', "lenght1", "800");
		$scope.checkLenght('handleCondition2', "lenght2", "500");
		$scope.checkLenght('handleCondition3', "lenght3", "1500");
		$scope.checkLenght('problemDescribe', "lenght4", "800");
		$scope.checkLenght('custDescribe',    "lenght5", "500");
		$scope.checkLenght('complainSummary', "lenght6", "150");

		//轉時間
		$scope.inputVO.fmt_create_date = $scope.toJsDate($scope.inputVO.fmt_create_date);
		$scope.inputVO.fmt_end_date = $scope.toJsDate($scope.inputVO.fmt_end_date);
		$scope.inputVO.birthday = $scope.toJsDate($scope.inputVO.birthday);
		$scope.inputVO.openAccountDate = $scope.toJsDate($scope.inputVO.openAccountDate);
		$scope.inputVO.buyDate = $scope.toJsDate($scope.inputVO.buyDate);
		$scope.inputVO.startDate = $scope.toJsDate($scope.inputVO.startDate);
		$scope.inputVO.endDate = $scope.toJsDate($scope.inputVO.endDate);

		try{ //總行人員才有
			if('Y' == $scope.inputVO.isA01) { 

				//設定a01 comboSource
				//客訴類型
				$scope.mappingSet['complainTypeList']=[];
				$scope.inputVO.complainTypeList.map((e)=>{
					$scope.mappingSet['complainTypeList'].push({LABEL: e.NAME, DATA: e.SEQ});
				})

				//客訴型態 
				$scope.mappingSet['complainModeList']=[];
				$scope.inputVO.complainModeList.map((e)=>{
					$scope.mappingSet['complainModeList'].push({LABEL: e.NAME, DATA: e.SEQ});
				})

				//摘要訴求
				$scope.mappingSet['complainRequestList']=[];
				$scope.inputVO.complainRequestList.map((e)=>{
					$scope.mappingSet['complainRequestList'].push({LABEL: e.NAME, DATA: e.SEQ});
				})

				//處理情形
				$scope.mappingSet['handleStatusList']=[];
				$scope.inputVO.handleStatusList.map((e)=>{
					$scope.mappingSet['handleStatusList'].push({LABEL: e.NAME, DATA: e.SEQ});
				})
			}
		}catch(e) {
			$scope.showErrorMsg('系統發生錯誤，請洽系統管理員!');
			return;
		}

	}	

	$scope.init = function() {
		$scope.lenght1 = 500;
		$scope.lenght2 = 500;
		$scope.lenght3 = 500;
		$scope.lenght4 = 800;
		$scope.lenght5 = 500;
		$scope.lenght6 = 150;
		$scope.start(); //載入初始資訊
		
		//權限管控
		var isA01 = $scope.inputVO.isA01 == 'Y';
		var handleStep = $scope.inputVO.handleStep;
		var grade = $scope.inputVO.grade;
		
		
		//myCase判斷
		$scope.myCaseFlag = 'Y' == $scope.inputVO.myCase;
		$scope.myCaseOtherFlag = false;
		if (isA01) {
			$scope.myCaseOtherFlag = true;
		} else if ("1"== $scope.inputVO.jobLv) {
			$scope.myCaseOtherFlag = "Y"== $scope.inputVO.myCase;
			//myCaseOtherFlag 給值後，需要將jobLv 設為undefined。
			//走add流程時，會給值為"1"。
			$scope.inputVO.jobLv = undefined;
		}

		$scope.saveFlag = false;
		if(isA01 == 'Y') {
			$scope.saveFlag = true;
		} else {
			if(!grade){
				$scope.saveFlag = true;
			}else if (grade=='1' &&  handleStep=='1') {
				$scope.saveFlag = true;
			}else if (grade=='2' && handleStep!='3') {
				$scope.saveFlag = true;
			}else if (grade=='3') {
				$scope.saveFlag = true;
			}
		}


		$scope.inputVO.region_center_id = ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
		$scope.inputVO.branch_area_id = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());

		//2015-07-02邏輯處理，只有新增人員可以修改創建資料========================================
		var selectElem = 'select';
		var inputElem = '';
		if("Y" == isA01){ //總行
			//不限制
		}else if("1" == handleStep && "1" == grade){
			//不限制
		}else if("1" == handleStep && "2" == grade){
			//不限制
		}else if("1" == handleStep && "3" == grade){
			//不限制
		}else if("2" == handleStep && "1" == grade){
			inputElem = ':input';
		}else if("2" == handleStep && "2" == grade){
			inputElem = ':input:not(#handleCondition2)';
		}else if("2" == handleStep && "3" == grade){
			inputElem = ':input:not(#handleCondition2)';
		}else if("3" == handleStep && "1" == grade){
			inputElem = ':input';
		}else if("3" == handleStep && "2" == grade){
			inputElem = ':input';
		}else if("3" == handleStep && "3" == grade){
			inputElem = ':input:not(#handleCondition3)';
		}

		if('' != inputElem){
			$scope.disabledByLevel(inputElem, selectElem);
		}

	};
	$scope.init();



	/*--------------------------------------------------------------------------------------------
	 *  combo box datasource
	 * 
	 * --------------------------------------------------------------------------------------------
	 * */
	$scope.mappingSet['gradeSource'] = [];
	$scope.mappingSet['gradeSource'].push(
			{LABEL : '1',DATA : '1'},
			{LABEL : '2',DATA : '2'},
			{LABEL : '3',DATA : '3'}
	);

	//客訴來源
	$scope.mappingSet['complainSource'] = [];
	$scope.mappingSet['complainSource'].push(
			{LABEL : '金管會-平台電話',DATA : '1'},
			{LABEL : '金管會-平台非電話',DATA : '2'},
			{LABEL : '金管會來函',DATA : '0'},
			{LABEL : '其他主管機關',DATA : '3'},
			{LABEL : '存證信函',DATA : '4'},
			{LABEL : '律師/陳情/書函',DATA : '5'},
			{LABEL : '臨櫃',DATA : '6'},
			{LABEL : '電話',DATA : '7'},
			{LABEL : '客服部',DATA : '8'},
			{LABEL : '評議中心-電話',DATA : '9'},
			{LABEL : '評議中心-書函',DATA : '10'},
			{LABEL : '評議中心-評議',DATA : '11'},
			{LABEL : '公關信箱',DATA : '12'}
	);

	//客訴類型
	$scope.mappingSet['complainTypeSel'] = [];
	$scope.mappingSet['complainTypeSel'].push(
			{LABEL : '保本境外結構型商品', DATA : '1'},
			{LABEL : '不保本境外結構型商品', DATA : '2'},
			{LABEL : '基金', DATA : '3'},
			{LABEL : '海外債', DATA : '24'},
			{LABEL : '信貸', DATA : '25'},
			{LABEL : '房貸', DATA : '26'},
			{LABEL : 'ETF/海外股票', DATA : '27'},
			{LABEL : '保險', DATA : '4'},
			{LABEL : '服務', DATA : '5'},
			{LABEL : '金控子公司', DATA : '6'},
			{LABEL : '其他', DATA : '7'},
			//{LABEL : '組合式商品SN', DATA : '8'}
			{LABEL : '組合式商品SI', DATA : '8'},
			{LABEL : '組合式商品DCI', DATA : '9'},
			{LABEL : '個資保護', DATA : '10'},
			{LABEL : '作業規範與流程', DATA : '11'},
			{LABEL : '共同行銷', DATA : '12'},
			{LABEL : '開戶/警示戶/靜止戶', DATA : '13'},
			{LABEL : '手續費/違約金爭議', DATA : '14'},
			{LABEL : '服務衍生爭議', DATA : '15'},
			{LABEL : '爭議帳款', DATA : '16'},
			{LABEL : '存匯作業', DATA : '17'},
			{LABEL : '資訊系統', DATA : '18'},
			{LABEL : '自動化設備', DATA : '19'},
			{LABEL : '關懷提問', DATA : '20'},
			{LABEL : '金融卡', DATA : '21'},
			{LABEL : '保管箱', DATA : '22'},
			{LABEL : '非屬本行服務範圍', DATA : '23'}
	);

	//是否寄送對帳單
	$scope.mappingSet['checkSheet'] = [];
	$scope.mappingSet['checkSheet'].push(
			{LABEL : '信託', DATA : '1'},
			{LABEL : '白金對帳單', DATA : '2'},
			{LABEL : '電子', DATA : '3'},
			{LABEL : '自取或不寄送', DATA : '4'}
	);

	// date picker
	$scope.sDateOptions = {}; //發生日期
	$scope.eDateOptions = {}; //結案日期

	$scope.birthdayOptions = {};//出生年月日
	$scope.openAccountDateOptions = {};//開戶日期
	$scope.buyDateOptions = {};//申購日期

	$scope.model = {};
	$scope.open = function($event, elementOpened) {

		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};

	$scope.limitDate = function() {
		$scope.sDateOptions.maxDate = $scope.inputVO.fmt_end_date;
		$scope.eDateOptions.minDate = $scope.inputVO.fmt_create_date;
	};


	/*
	 * --------------------------------------------------------------------------------------------
	 * function
	 * 
	 * --------------------------------------------------------------------------------------------
	 * 
	 * */

	/**
	 * 
	 * 上傳檔案
	 */
	$scope.uploadFinshed = function(name, rname) {
		$scope.inputVO.fileName = name;
		$scope.inputVO.realfileName = rname;
	};

	/**
	 * 儲存此次記錄並更新狀態
	 */	
	$scope.nextStep = function() {

		var grade = $scope.inputVO.grade;  //等級
		var isA01 = "Y" == $scope.inputVO.isA01;  //非UI
		var branchId = $scope.inputVO.branchId;	//分行別
		var addByA01 = $scope.inputVO.addByA01;	//非UI
		var step  = $scope.inputVO.handleStep;     //非UI
		var territory = $scope.inputVO.territoryId;//非UI
		var title = $scope.inputVO.jobTitleId;		//非UI

		var hasNextStep = false;
		if($scope.check()){
			/* 確定送出此次編輯-並更新處理狀態  */
			$confirm({text: '確定送出此次編輯-並更新處理狀態'}, {size: 'sm'}).then(function() {
				$scope.nextStepDisabledFlag = true; //鎖鍵
				if (isA01) {
					if (addByA01 == "Y"){
						if( "3" == grade){
							hasNextStep = true;
						}else{
							hasNextStep = false;
						}
					} else {
						if("1" == step){ hasNextStep = true; }
						if("2" == step){ hasNextStep = true; }
						if("3" == step){
							if("1" == grade || "2" == grade){
								hasNextStep = false;								
							}else{
								hasNextStep = true;	
							}
						}
						if("A" == step){ hasNextStep = true; }
						if("A1" == step){ hasNextStep = true; }
						if("A2" == step){ hasNextStep = false; }
					}
				}else{
					if ( (territory == "806" && branchId != "806") || (branchId == "998" || branchId == "806")) {
						if(step == "3"){
							hasNextStep = false;
						}else{
							hasNextStep = true;
						}
					}else if (grade == 3) { //三級客訴需總行結按
						hasNextStep = true;
					} else { // 1 or 2 
						if ("3" == step) {
							hasNextStep = false;
						} else {
							hasNextStep = true;
						}
					}
				}

				//總行新增或是尚有下一流程才需跳出視窗
				if (hasNextStep) {
					$scope.sendRecv("CRM999", "next", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
							function(tota, isError) {
						if (!isError) {
							var input = angular.copy(tota[0].body.paramVO);  //取得回傳資訊
							if(input) {
								var dialog = ngDialog.open({
									template: 'assets/txn/CRM999/CRM999_nextComplainFlow.html',
									className: 'CRM999_nextComplainFlow',
									showClose: false,
									controller: ['$scope', function($scope) {

										$scope.preInputVO = input;
									}]
								});
								dialog.closePromise.then(function (data) {
									if(data.value === 'successful'){
										$scope.closeThisDialog('successful');
									}
								});
							}
						}
					});

				}else {
					$scope.inputVO.nextEmp = "sys";
					$scope.saveSubmit();
				}
			});
		}
	}
	
	$scope.check = function() {
		var checkResult = false;//儲存檢查欄位結果
		var grade = $scope.inputVO.grade;
		var handleStep = $scope.inputVO.handleStep;
		//Ray	201310040162-00	客訴來源及客訴類型改為必填欄位
		var complainSource = $scope.inputVO.complainSource; //客訴來源
		var complainTypeSel = $scope.inputVO.complainTypeSel; //客訴類型
		var fmt_create_date =  $scope.inputVO.fmt_create_date; //發生日期
		var isA01 = $scope.inputVO.isA01; //非UI

		if (!complainSource) {
			$scope.showErrorMsg('客訴來源必須輸入');
			return false;
		}
		if (!complainTypeSel) {
			$scope.showErrorMsg('客訴類型必須輸入');
			return false;
		}
		if (isNaN($scope.inputVO.branchId) || ($scope.inputVO.branchId || "").toString().length != 3) {
			$scope.showErrorMsg('分行別資料有誤');
			return false;
		}
		if(isA01 == "Y" && !fmt_create_date){
			$scope.showErrorMsg('發生日期必須輸入');
			return false;
		}
		if (!$scope.inputVO.grade) {
			$scope.showErrorMsg('請輸入客訴等級');
			return false;
		}
		if(!handleStep || handleStep == "1"){
			checkResult = $scope.checkHandleStep1();
		}
		if(handleStep=="2" && (grade == "2" || grade == "3")){
			checkResult = $scope.checkHandleStep2();
		}else if(handleStep=="2" && grade == "1"){
			checkResult = true;
		}

		if(handleStep=="3" && grade == "3"){
			checkResult = $scope.checkHandleStep3();	
		}else if(handleStep=="3" && (grade == "2" || grade=="1")) {
			checkResult = true;
		}
		if( "A" == handleStep || "A1" == handleStep || "A2" == handleStep) {
			if(checkResult = $scope.checkHandleStep1()) {
				checkResult = $scope.checkHandleStep3();
			}
		}

		if("E" == handleStep){
			checkResult = true;
		}

		return checkResult;
	}

	/**
	 * 檢查處理狀態1所需輸入欄位
	 */
	$scope.checkHandleStep1 = function() {
		var jobLv = $scope.inputVO.jobLevel;//判斷處理等級1=金服主管、2=區督導、3=總行
		var custId = $scope.inputVO.custId || ""; //客戶統編
		var complainSummary = $scope.inputVO.complainSummary; //客戶摘要
		var custName = $scope.inputVO.custName; //客戶姓名
		var personId = $scope.inputVO.personId; //專員員編
		var complainProduct = $scope.inputVO.complainProduct; //客訴商品 
		var problemDescribe = $scope.inputVO.problemDescribe; //問題實況
		var custDescribe = $scope.inputVO.custDescribe; 		//客戶訴求
		var handleCondition1 = $scope.inputVO.handleCondition1;//第一級處理情形
		var handleCondition2 = $scope.inputVO.handleCondition2;//第二級處理情形
		var handleCondition3 = $scope.inputVO.handleCondition3;//第三級處理情形
		var openAccountDate = $scope.inputVOopenAccountDate;//開戶日期
		var birthday = $scope.inputVO.birthday; //出生年月日(西元)
		var buyDate = $scope.inputVO.buyDate; //申購日期
		var checkSheet = $scope.inputVO.checkSheet; //是否寄送對帳單
		var date = new Date();//今天的日期

		if (!$scope.inputVO.branchId){
			$scope.showErrorMsg('請輸入分行別');
			return false;
		}

		if ($scope.inputVO.branchId != "998"
			&& !$scope.inputVO.flowType){
			$scope.showErrorMsg('請輸入案件分流');
			return false;
		}

		if (!checkSheet){
			$scope.showErrorMsg('請選擇是否寄送對帳單');
			return false;
		}

		//檢查客戶統編
		var custIdLen = custId.length;
		if(custIdLen!=8 && custIdLen!=10 && custIdLen>0){
			$scope.showErrorMsg("輸入客戶統編長度不正確!");
			document.getElementById("custId").focus();
			return ;			
		}

		if(openAccountDate > date){
			$scope.showErrorMsg("開戶日期不得大於今天日期!");
			document.getElementById("openAccountDate").focus();
			return false;
		}
		if(birthday > date){
			$scope.showErrorMsg("出生年月日(西元) 不得大於今天日期!");
			document.getElementById("birthday").focus();
			return false;
		}
		if(buyDate > date){
			$scope.showErrorMsg("申購日期不得大於今天日期!");
			document.getElementById("buyDate").focus();
			return false;
		}

		if(!complainSummary){
			$scope.showErrorMsg('請輸入：'+'客訴摘要');
			document.getElementById("complainSummary").focus();
			return false;
		}
		if(!custName){
			$scope.showErrorMsg('請輸入：' + '客戶姓名')
			document.getElementById("custName").focus();
			return false;
		}

		if(!complainProduct){
			$scope.showErrorMsg('請輸入：' + '客訴商品')
			document.getElementById("complainProduct").focus();
			return false;
		}
		if(!problemDescribe){
			$scope.showErrorMsg('請輸入：' + '問題實況');
			document.getElementById("problemDescribe").focus();
			return false;
		}
		if(!custDescribe){
			$scope.showErrorMsg('請輸入：' + '客戶訴求');
			document.getElementById("custDescribe").focus();
			return false;
		}
		if(jobLv == "1" && !handleCondition1) {
			$scope.showErrorMsg('請輸入：' + "第一級處理情形");
			document.getElementById("handleCondition1").focus();
			return false;
		}
		if(jobLv == "2" && !handleCondition2) {
			$scope.showErrorMsg('請輸入：' + "第二級處理情形");
			document.getElementById("handleCondition2").focus();
			return false;
		}
		if (jobLv == "3" && !handleCondition3) {
			$scope.showErrorMsg('請輸入：' + "第三級處理情形");
			document.getElementById("handleCondition3").focus();
			return false;
		}
		return true;
	}

	/**
	 * 檢查處理狀態2(區督導)所需輸入欄位
	 */
	$scope.checkHandleStep2 = function() {
		var handleCondition2 = $scope.inputVO.handleCondition2;
		if(!handleCondition2){
			$scope.showErrorMsg('請輸入：' + "第二級處理情形");
			document.getElementById("handleCondition2").focus();
			return false;
		}
		return true;
	}

	/**
	 * 檢查處理狀態3(總行)所需輸入欄位
	 */
	$scope.checkHandleStep3 = function() {

		var handleCondition3 = $scope.inputVO.handleCondition3;

		if(!handleCondition3){
			$scope.showErrorMsg('請輸入：' + "第三級處理情形");
			document.getElementById("handleCondition3").focus();
			return false;
		}
		return true;
	}

	/**
	 * 儲存此次記錄並更新狀態
	 */	
	$scope.saveSubmit = function() {
		$scope.saveSubmitDisabledFlag = true; //鎖鍵
		$scope.inputVO.status = 1 ;//0表示只儲存  1表示儲存更新handleStep欄位
		
		$scope.sendRecv("CRM999", "save", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
				function(tota, isError) {
			if (!isError) {
				if(tota[0].body.msg == 'Y') {
					$scope.showMsg('客訴資料表建立成功！');
					$scope.closeThisDialog('successful');
				} else {
					//其他錯誤訊息
					if(tota[0].body.errorMsg) {
						$scope.showErrorMsg(tota[0].body.errorMsg);
					}

					if(tota[0].body.msg == 'N') {
						$scope.showErrorMsg('客訴資料表建立失敗！');
					}
				}
			}
		});
	}

	/**
	 * 儲存此次記錄
	 */	  
	$scope.save = function() {
//		var jobLv = 'A';//判斷處理等級1=金服主管、2=區督導、3=總行
		if($scope.check()){
			/* 確定只儲存此次編輯-但不更新處理狀態  */
			$confirm({text: '確定只儲存此次編輯-但不更新處理狀態'}, {size: 'sm'}).then(function() {
				$scope.inputVO.status = 0 ;//0表示只儲存  1表示儲存更新handleStep欄位

				$scope.sendRecv("CRM999", "save", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO", $scope.inputVO,
						function(tota, isError) {
					if (!isError) {
						if(tota[0].body.msg == 'Y') {
							$scope.showMsg('客訴資料表建立成功！');
							$scope.closeThisDialog('successful');
						} else {
							//其他錯誤訊息
							if(tota[0].body.errorMsg) {
								$scope.showErrorMsg(tota[0].body.errorMsg);
							}

							if(tota[0].body.msg == 'N') {
								$scope.showErrorMsg('客訴資料表建立失敗！');
							}
						}
					}
				});
			});	
		}
	}


	/**
	 * 退件按鈕動作【INIT】
	 */
	$scope.backStep = function(listId) {

		$scope.sendRecv("CRM999", "backStep", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO",
				{complainListId : $scope.inputVO.complainListId,
			flowType : $scope.inputVO.flowType}, 
			function(tota, isError) {
				if (!isError) {

					var paramVO = angular.copy(tota[0].body.paramVO);  //取得回傳資訊
					if (paramVO) {
						//開啟退件dialog
						var dialog = ngDialog.open({
							template: 'assets/txn/CRM999/CRM999_backComplainFlow.html',
							className: 'CRM999_backComplainFlow',
							showClose: false,
							controller: ['$scope', function($scope) {

								$scope.preInputVO = paramVO;
							}]
						});
						dialog.closePromise.then(function (data) {
							if(data.value === 'successful'){
								$scope.closeThisDialog('successful');
							}
						});
					}
				}
			});
	}

	/**
	 * 刪除按鈕動作.
	 */
	$scope.del = function(listId) {
		$confirm({text : '確定要刪除此客訴表'}, {size : 'sm'}).then(function() {
			$scope.sendRecv("CRM999", "del", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO",
					{complainListId : $scope.inputVO.complainListId}, 
					function(tota, isError) {
						if (!isError) {
							var msg = tota[0].body.msg;  //取得回傳訊息
							if (msg == 'Y') {
								$scope.showMsg("客訴資料表刪除成功！");
								$scope.closeThisDialog('successful');	
							} else {
								$scope.showErrorMsg("客訴資料表刪除失敗！");
							}
						}
					});
		});
	}

	/*
	 * 檔案名稱設置
	 * */
	$scope.uploadFinshed = function(name, rname) {

		$scope.inputVO.realfileName = rname;
		$scope.inputVO.uploadFileName = name;
	};

	$scope.downloadFile = function() {

		$scope.sendRecv("CRM999", "getUploadFile", "com.systex.jbranch.app.server.fps.crm999.CRM999InputVO",
				{complainListId : $scope.inputVO.complainListId}, 
				function(tota, isError) {
					if (!isError) {
					}
				});
	}
	
	/*
	 * 客訴來源combobox選擇邏輯
	 */
	$scope.selcomplainSource = function() {
		let value = $scope.inputVO.complainSource;  //客訴來源
		if (value == 3 || value == 5) { //"其他主管機關"、"律師/陳情/書函"，須出現otherSource欄位
			$scope.complainSourceFlag = true;
		} else {
			$scope.complainSourceFlag = false;
			$scope.inputVO.otherSource = '';
		}
	}
	$scope.selcomplainSource();
});