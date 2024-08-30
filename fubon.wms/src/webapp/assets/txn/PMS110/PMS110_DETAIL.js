/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller("PMS110_DETAILController", function($rootScope, $scope, $controller, socketService, ngDialog,
    projInfoService, $q, $confirm, $filter, getParameter) {
    $controller('BaseController', {
        $scope: $scope
    });
    $scope.controllerName = "PMS110_DETAILController";
    
    /***XML參數***/
    getParameter.XML(["PMS.PROD.CUST_SOURCE"], function(totas) {
        if (totas) {
            $scope.mappingSet['PMS.PROD.CUST_SOURCE'] = totas.data[totas.key.indexOf('PMS.PROD.CUST_SOURCE')];
        }
    });    
    
    //選單初始化
    $scope.mappingSet['PRD_LIST'] = [];
    $scope.mappingSet['PRD_LIST'].push({LABEL: "房貸", DATA: "1"},{LABEL: "信貸", DATA: "2"});	
	
    var NowDate = new Date();
	var yr = NowDate.getFullYear();
	var mm = NowDate.getMonth()+(13-NowDate.getMonth());
	var strmm='';
	$scope.mappingSet['timeE'] = [];
    for(var i=0; i<10; i++){
      	mm = mm -1;
      	if(mm == 0){
      		mm = 12;
      		yr = yr-1;
      	}
      	if(mm<10){
      		strmm = '0' + mm;
      	}else{
      		strmm = mm; 
      	}
      	$scope.mappingSet['timeE'].push({LABEL: yr+'/'+strmm, DATA: yr +''+ strmm});        
    }
    
	//文字日期加斜線
    $scope.dateType = function(val) {
        if (!val || val.length !== 8) return;
        var date = val.split("");
        date.splice(6, 0, "/");
        date.splice(4, 0, "/");
        return date.join("");
    }
    
    //async_for
	var async_for = function(initObj, cond, step, main) {
		var deferred = $.Deferred();
		initObj._next = function() {
			initObj._defer.resolve();
		};
		initObj._break = function() {
			initObj._defer.reject();
			deferred.resolve(initObj);
		};
		var wrapped_main = function() {
			initObj._defer = $.Deferred();
			main.call(this, this);
			return this._defer.promise();
		};
		var async_start = function() {
			if (cond.call(initObj, initObj)) {
				wrapped_main.call(initObj).then(function() {
					step.call(initObj, initObj);
				}).then(async_start);
			} else {
				deferred.resolve(initObj);
			}
		};
		async_start();
		return deferred.promise();
	};
    	
    //初始
    $scope.init = function() {
    	var now=new Date();
    	now.setMonth(now.getMonth()+1);
    	$scope.paramList = [];
        $scope.inputVO = {
		        plan_seq           :'',      //消金銷售計劃序號
		        plan_yearmon       :$filter('date')(now, 'yyyyMM'),      //消金銷售計劃月份
		        branch_area_id     :'',      //營運區
		        branch_nbr         :'',      //分行代碼
		        ao_code            :'',      //AO CODE
		        emp_id             :'',      //業務人員員工編號
		        emp_name           :'',      //業務人員員工姓名
		        cust_id            :'',      //客戶ID
		        cust_name          :'',      //客戶姓名
		        case_num           :'',      //案件編號
		        est_incom_date     :$filter('date')(new Date(), 'yyyy/MM/dd'),      //預計進件日期
		        act_incom_date     :'',      //實際進件日期
		        est_prd            :'',      //預計承作商品
		        est_amt            :0,      //預計承做金額
		        est_meeting_date   :'',      //預計面談日期
		        act_meeting_date   :'',      //實際面談日期
		        cust_source        :'',      //客戶來源
		        apply_amt          :0,      //申請金額
		        debt_amt           :0,      //撥款金額
		        pro_date           :'',      //實際對保日期
		        ex_date            :$filter('date')(new Date(), 'yyyyMM'),      //預計撥款月份
		        actual_date        :'',      //實際撥款日期
		        close_flag         :'',      //結案
		        close_c_date       :'',      //結案日期
		        keyemp_id          :'',      //鍵機人員員編
        }
        $scope.inputVO = {
            	plan_yearmon: $scope.row.PLAN_YEARMON,
            	plan_yearmon_OLD: $scope.row.PLAN_YEARMON,
                cust_id: $scope.row.CUST_ID,
                cust_id_OLD: $scope.row.CUST_ID,
                cust_name: $scope.row.CUST_NAME,
                cust_source: $scope.row.CUST_SOURCE,
                est_incom_date: $scope.dateType($scope.row.EST_INCOM_DATE),
                act_incom_date: $scope.row.ACT_INCOM_DATE || '', 
                est_prd: $scope.row.EST_PRD,
                est_amt: $scope.row.EST_AMT,
                est_meeting_date: $scope.dateType($scope.row.EST_MEETING_DATE),
                act_meeting_date: $scope.dateType($scope.row.ACT_MEETING_DATE),
                pro_date: $scope.dateType($scope.row.PRO_DATE),
                apply_amt:$scope.row.APPLY_AMT,      //申請金額
                debt_amt :$scope.row.DEBT_AMT,      //撥款金額
                ex_date: $scope.row.EX_DATE,
                case_num: $scope.row.CASE_NUM,
                plan_seq: $scope.row.PLAN_SEQ,
                emp_id: $scope.row.EMP_ID,
                branch_area_id: $scope.row.BRANCH_AREA_ID,
                branch_nbr: $scope.row.BRANCH_NBR,
                ao_code: $scope.row.AO_CODE,
                close_flag         :$scope.row.CLOSE_FLAG,     //結案
    	        close_c_date       :$scope.row.CLOSR_C_DATE,     //結案日期
    	        keyemp_id          :$scope.row.KEYEMP_ID,     //鍵機人員員編
    	        actual_date: $scope.dateType($scope.row.ACTUAL_DATE)
        };
    }
    $scope.init();
    
    //查詢
    $scope.query = function() {
    	if($scope.inputVO.est_prd == ''){
    		$scope.showMsg("請選擇預計承做商品");
    		return;
    	}
    	if($scope.inputVO.case_num == ''){
    		$scope.showMsg("請輸入案件編號");
    		return;
    	}
    	$scope.sendRecv("PMS110", "query_DETAIL", "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.inputVO,
        	function(tota, isError) {
    			$scope.paramList = [];
    			if(tota[0].body.resultList.length == 0){
    				$scope.paramList = [];
    				$scope.showMsg("ehl_01_common_009");
    				return;
    			}
    			$scope.paramList = tota[0].body.resultList;
    			$scope.paramList[0].EST_AMT = $scope.inputVO.est_amt;	//查詢資料皆為0，第一筆放預計承做金額
    	});
    }
    
    //清除
    $scope.claen = function() {
    	$scope.paramList = [];
    	$scope.inputVO.est_prd = '';
    	$scope.inputVO.case_num = '';
    }
    
    //儲存前先檢查案件編號是否已使用過
    $scope.queryCaseNum = function(){
    	$scope.sendRecv("PMS110", "checkCaseNum", "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.inputVO,
            	function(tota, isError) {
        			$scope.caseparamList = [];
        			if(tota[0].body.resultList2.length == 0){
        				$scope.GetDataToSave();
        			}else{
        				$scope.caseparamList = tota[0].body.resultList2;
        				$scope.caseYearmon = $scope.caseparamList[0].PLAN_YEARMON;
        				$scope.showMsg("該案件編號於"+$scope.caseYearmon+"已存在!");
            			return;
        			}
        	});
    }
    
    //儲存前準備
    $scope.GetDataToSave = function() {
        $confirm({
            text: '確定修改資料!!'
        }, {
            size: 'sm'
        }).then(function() {
	    	$scope.inputVO.est_incom_date = Date.parse($scope.inputVO.est_incom_date);
	    	$scope.inputVO.est_meeting_date = Date.parse($scope.inputVO.est_meeting_date);
	    	$scope.inputVO.act_meeting_date = Date.parse($scope.inputVO.act_meeting_date);
	    	$scope.inputVO.pro_date = Date.parse($scope.inputVO.pro_date);
	    	$scope.inputVO.close_c_date = Date.parse($scope.inputVO.close_c_date);
	    	
	    	$scope.Del_Add('D');
        });
    }
    
    //新增資料迴圈
    $scope.saveFor = function() {
    	//async_for 非同步用迴圈
	    async_for(
	    	{i:0},		//i = 0
	    	function(_){
	    		return _.i < $scope.paramList.length;	// i < $scope.paramList.length
	    	}, 
	    	function(_){
	    		_.i++; 	//i++
	    	},
	    	function(_){
	    		$scope.inputVO.cust_id = $scope.paramList[_.i].CUST_ID;
	    		$scope.inputVO.cust_name = $scope.paramList[_.i].CUST_NAME;
	    		$scope.inputVO.est_amt = $scope.paramList[_.i].EST_AMT;
	    		$scope.inputVO.apply_amt = $scope.paramList[_.i].APPLY_AMT;	//申請金額
	    		$scope.inputVO.debt_amt = $scope.paramList[_.i].DRAW_AMT;		//撥款金額
	    		$scope.inputVO.ex_date = $scope.paramList[_.i].EX_DATE;
	    		$scope.inputVO.act_incom_date = $scope.paramList[_.i].CASE_DATE;
	    		$scope.inputVO.actual_date = $scope.paramList[_.i].DRAW_DATE;
	    		$scope.inputVO.case_type = $scope.paramList[_.i].CASE_TYPE;

	    		$scope.Del_Add('A');
	    		setTimeout(function(){_._next();},500);		//稍微等待上方function跑完再繼續迴圈
	    	}
	    ).then(function(_){		//迴圈結束後執行
	    	$scope.showMsg("ehl_01_common_002");
		   	$scope.closeThisDialog('successful');
		});	
	}   
    
    //儲存
    $scope.Del_Add = function(type) {
    	$scope.inputVO.DA_TYPE = type;
    	$scope.sendRecv("PMS110", 'Del_Add', "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.inputVO,
    		function(tota, isError) {
    			if(!isError){
    				if($scope.inputVO.DA_TYPE == 'D'){
    					$scope.saveFor();
    				}
    			}
    	});
	}
    
    //取消
    $scope.cancel = function() {
        $scope.closeThisDialog('cancel');
    }
    
    
});