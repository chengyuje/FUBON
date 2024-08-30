'use strict';
eSoafApp.controller("PMS110_INSERTController", function(getParameter, $rootScope, $scope, $controller, validateService, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
    
	$controller('BaseController', {$scope: $scope});
    $scope.controllerName = "PMS110_INSERTController";

	/** 繼承共用的組織連動選單 **/
	$controller('PMSRegionController', {$scope: $scope});

    /***XML參數***/
    getParameter.XML(['PMS.PIPELINE_PRD', 
                      'PMS.PIPELINE_SOURCE', 
                      'PMS.PIPELINE_REFUSE', 
                      'PMS.PIPELINE_VIEW_RESULT', 
                      'PMS.PIPELINE_STATUS', 
                      'PMS.PIPELINE_PRD_ITEM'
                      ], (totas) => {
        if (totas) {
            $scope.mappingSet['PMS.PIPELINE_PRD'] = totas.data[totas.key.indexOf('PMS.PIPELINE_PRD')];					// 預計承作商品(大項)
            $scope.mappingSet['PMS.PIPELINE_SOURCE'] = totas.data[totas.key.indexOf('PMS.PIPELINE_SOURCE')];			// 案件來源
            $scope.mappingSet['PMS.PIPELINE_REFUSE'] = totas.data[totas.key.indexOf('PMS.PIPELINE_REFUSE')];			// 已核不撥原因
            $scope.mappingSet['PMS.PIPELINE_VIEW_RESULT'] = totas.data[totas.key.indexOf('PMS.PIPELINE_VIEW_RESULT')];	// 面談結果
            $scope.mappingSet['PMS.PIPELINE_STATUS'] = totas.data[totas.key.indexOf('PMS.PIPELINE_STATUS')];			// 案件狀態
            $scope.mappingSet['PMS.PIPELINE_PRD_ITEM'] = totas.data[totas.key.indexOf('PMS.PIPELINE_PRD_ITEM')];			// 實際承作商品(小項)
        }
    });

    /** 設置日曆 **/
    $scope.model = {};
    $scope.open = function($event, elementOpened) {
        $scope.model[elementOpened] = !$scope.model[elementOpened];
    };


    /** 如果是更新 Pipeline 銷售計劃，將 row 更新到 InputVO 上 **/
    $scope.loadDataExistRow = () => {    	
    	if ($scope.row != undefined) {
    		//key
    		$scope.inputVO.planSeq = $scope.row.PLAN_SEQ; 										// 銷售計劃序號
    		$scope.inputVO.planYearmon = $scope.row.PLAN_YEARMON; 								// 銷售計劃月份
    		$scope.inputVO.region_center_id = $scope.row.CENTER_ID; 							// 業務處
    		$scope.inputVO.branch_area_id = $scope.row.AREA_ID; 								// 營運區
    		
    		//other
    		$scope.inputVO.custSource = $scope.row.CUST_SOURCE;									// 客戶來源
    		$scope.inputVO.case_num = $filter('uppercase')($scope.row.CASE_NUM);				// 進件編號
    		$scope.inputVO.estPrdItem = $scope.row.PRD_ITEM;									// 實際承作商品
    		
        	$scope.sendRecv("PMS110", "getPipelineDTL", "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					// KEY
					$scope.inputVO.planSeq = tota[0].body.pipelineDtl[0].PLAN_SEQ;											// 銷售計畫序號
					$scope.inputVO.planYearmon = tota[0].body.pipelineDtl[0].PLAN_YEARMON;									// 銷售計畫月份
					$scope.inputVO.region_center_id = tota[0].body.pipelineDtl[0].CENTER_ID;								// 業務處
					$scope.inputVO.branch_area_id = tota[0].body.pipelineDtl[0].AREA_ID;									// 營運區

					// OTHER - 區塊1
					$scope.inputVO.meetingDate = $scope.toJsDate(tota[0].body.pipelineDtl[0].MEETING_DATE, 'yyyy/MM/dd');	// 面談日期
					$scope.inputVO.custId = tota[0].body.pipelineDtl[0].CUST_ID;											// 客戶ID
					$scope.inputVO.custName = tota[0].body.pipelineDtl[0].CUST_NAME;										// 客戶姓名
					$scope.inputVO.leadVarPhone = tota[0].body.pipelineDtl[0].LEAD_VAR_PHONE;								// 名單-客戶連絡電話
					$scope.inputVO.leadVarCTime = tota[0].body.pipelineDtl[0].LEAD_VAR_C_TIME;								// 名單-客戶方便聯絡時間
					$scope.inputVO.leadVarRtnEName = tota[0].body.pipelineDtl[0].LEAD_VAR_RTN_E_NAME;						// 名單-客服或電銷轉介人員姓名
					$scope.inputVO.leadVarRtnEID = tota[0].body.pipelineDtl[0].LEAD_VAR_RTN_E_ID;							// 名單-客服或電銷轉介人員員編
					$scope.inputVO.estPrd = tota[0].body.pipelineDtl[0].EST_PRD;											// 預計承作商品大項
					$scope.inputVO.estAmt = tota[0].body.pipelineDtl[0].EST_AMT;											// 預作承作金額
					$scope.inputVO.estDrawDate = $scope.toJsDate(tota[0].body.pipelineDtl[0].EST_DRAW_DATE, 'yyyy/MM/dd');	// 預計撥款日期
					$scope.inputVO.meetingResult = tota[0].body.pipelineDtl[0].MEETING_RESULT;								// 面談結果
					$scope.inputVO.branch_nbr = tota[0].body.pipelineDtl[0].BRA_NBR;										// 分行別
					$scope.inputVO.memo = tota[0].body.pipelineDtl[0].MEMO;													// 備註
		    		$scope.inputVO.caseSource = $scope.row.CASE_SOURCE;														// 案件來源
		    		
		    		// OTHER - 區塊2
					$scope.inputVO.case_num = $filter('uppercase')(tota[0].body.pipelineDtl[0].CASE_NUM);											// 案件編號
					$scope.inputVO.loanCustID = tota[0].body.pipelineDtl[0].LOAN_CUST_ID;									// 借款人身份證字號
					$scope.inputVO.loanCustName = tota[0].body.pipelineDtl[0].LOAN_CUST_NAME;								// 借款人姓名
					$scope.inputVO.loanAmt = tota[0].body.pipelineDtl[0].LOAN_AMT;											// 借款金額
					
					// OTHER - 區塊3
					$scope.inputVO.pipelineStatus = tota[0].body.pipelineDtl[0].PIPELINE_STATUS;							// 案件狀態
					$scope.inputVO.refuseReason = tota[0].body.pipelineDtl[0].REFUSE_REASON;								// 已核不撥原因
					$scope.inputVO.estPrdItem = tota[0].body.pipelineDtl[0].PIPELINE_PRD_ITEM;								// 承作商品類型(小項)
					$scope.inputVO.chkLoanDate =  $scope.toJsDate(tota[0].body.pipelineDtl[0].CHK_LOAN_DATE, 'yyyy/MM/dd');	// 對保日期
					if (null != tota[0].body.pipelineDtl[0].ALLOW_DATE) {
						$scope.inputVO.adDate = tota[0].body.pipelineDtl[0].ALLOW_DATE;										// 核准日期
						$scope.inputVO.adAmt = tota[0].body.pipelineDtl[0].ALLOW_AMT;										// 核准金額
					} else {
						$scope.inputVO.adDate = tota[0].body.pipelineDtl[0].DECLINE_DATE;									// 婉拒日期
						$scope.inputVO.adAmt = tota[0].body.pipelineDtl[0].DECLINE_AMT;										// 婉拒金額
					}
					$scope.inputVO.refEmp = tota[0].body.pipelineDtl[0].REF_EMP;											// 轉介人員姓名
					$scope.inputVO.refEmpCID = tota[0].body.pipelineDtl[0].REP_EMP_CID;										// 轉介人員身份證字號
				}
			});
    	} else {
    		$scope.inputVO.custSource = 'C';
    	}
    }


    /** 初始化 **/
    $scope.init = () => {
    	/** 如果是更新 Pipeline 銷售計劃，將 row 更新到 InputVO 上 **/
        $scope.loadDataExistRow();			

        /** 新增前 **/
        $scope.beforeAdd = !$scope.inputVO.planSeq;
        
        /** 進件前 **/
        $scope.beforeCase = !$scope.inputVO.case_num;
        
        /** 銷售計劃月份（往前半年、包含當月再往後三個月，總計 10 個月） **/
        var LastDate = new Date();
    	LastDate.setMonth(LastDate.getMonth() + 3, 1);
	    var strLastMon='';
	    $scope.mappingSet['timeE'] = [];
	    
	    //資料日期區間限制為兩年內資料
	    for (var i = 0; i <= 10; i++) {
	    	strLastMon = LastDate.getMonth();
	    	strLastMon++;
	    	if (strLastMon < 10 ) {
	    		strLastMon = '0' + strLastMon;
	    	} else {
	    		strLastMon = '' + strLastMon;
	    	}

			$scope.mappingSet['timeE'].push({
	    		LABEL: LastDate.getFullYear() + '/' + strLastMon,
	    		DATA: LastDate.getFullYear() + '' + strLastMon
	    	}); 
	    	
	    	//每一筆減一個月，倒回去取前六個月內日期區間
	    	LastDate.setMonth(LastDate.getMonth() - 1);
	    }
    }
    
    $scope.init();
    

    /** 點選儲存鍵 **/
    $scope.save = isInsert => {
        
        if (($scope.inputVO.custId == '' || $scope.inputVO.custId == undefined) &&
        	($scope.inputVO.custName == '' || $scope.inputVO.custName == undefined)){
        	$scope.showSuccessMsg('客戶ID/客戶姓名請擇一輸入');
        	return;
        } 
        
        if ($scope.inputVO.custId && !validateService.checkCustID($scope.inputVO.custId)) 
        	return;
        
        if (isInsert) 
        	$scope.saveLogic('insert', 'ehl_01_common_001');
        else 
        	$scope.saveLogic('update', 'ehl_01_common_002');
    }

    /** 儲存邏輯 **/
    $scope.saveLogic = (action, msg) => {
        $scope.sendRecv("PMS110", action, "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.inputVO, function(tota, isError) {
        	if (!isError) {
        		$scope.showSuccessMsg(msg);
        		$scope.closeThisDialog("successful");
        	}
        });
    }

    /** 關閉 Dialog **/
    $scope.cancel = () => {
        $scope.closeThisDialog("cancel");
    }

});