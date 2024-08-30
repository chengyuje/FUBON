/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS101Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService,$filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS101Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$controller('PPAPController', {$scope: $scope});
		
		/**===========================================================**
			queryType : 點擊查詢按鈕時==>給予Y，點擊清除按鈕時==>給予N
			用途 : 點擊已轉、未轉銷售計劃頁籤時也會查詢，此時只有queryType = Y才會做查詢
		**===========================================================**/
		
		 /***主查詢功能  必須放在最上方 Init要使用****/
		$scope.inquire = function(){
			$scope.quertType = "Y";
			$scope.query();
		}
		
		/***參數先設定在此      要改用DB paramtype 再改 CR問題單:0003703***/
		$scope.mappingSet['PRD_TYPE']=[];
        $scope.mappingSet['PRD_TYPE'].push(
        		{LABEL : 'SI',DATA :'SI'},
        		{LABEL : '海外債',DATA :'海外債'},
        		{LABEL : '外定',DATA :'外定'},
        		{LABEL : 'SN',DATA :'SN'},
        		{LABEL : '基金',DATA :'基金'},
        		{LABEL : '保險',DATA :'保險'},
        		{LABEL : '台定',DATA :'台定'}
        );
        
        $scope.query = function(){
        	
//    		轉換已規劃下拉選單
        	$scope.typeFlag=$scope.inputVO.TYPE;
        	/*以下內容是總計各個欄位*/
        	$scope.sumExpTot = 0;
        	$scope.sumExpPlan = 0;
        	$scope.sumExpBal = 0;
        	$scope.sumRecTot = 0;
        	$scope.sumRecTxn = 0;
        	$scope.sumRecPlan = 0;
        	$scope.sumRecBal = 0;
        	$scope.sumRecDep = 0;
        	       	
        	$scope.sumExpToty = 0;
        	$scope.sumExpPlany = 0;
        	$scope.sumExpBaly = 0;
        	$scope.sumRecToty = 0;
        	$scope.sumRecTxny = 0;
        	$scope.sumRecPlany = 0;
        	$scope.sumRecBaly = 0;
        	$scope.sumRecDepy = 0;

        	//查詢檢核
        	if($scope.connector('get','CRM171_AO_CODE') == undefined){
	        	if($scope.parameterTypeEditForm.$invalid){				
		    		$scope.showErrorMsg('欄位檢核錯誤:年月為必輸入欄位!!!');
		    		$scope.connector('set','CRM171_AO_CODE', undefined);
	        		return;
	        	}	
        	}
        	
        	//***合計function***//
        	$scope.getSum = function(group, key) {
                  var sum = 0;
                  if(group!=undefined){
                	  for (var i = 0; i < group.length; i++){
                          sum += group[i][key];
                         }    
                  }
                  return sum;
             }
        	/****查詢資料METHOD****/
        	if($scope.shows == 'Y' && $scope.quertType == "Y"){
        		$scope.sendRecv("PMS101", "queryDataY", "com.systex.jbranch.app.server.fps.pms101.PMS101InputVO", $scope.inputVO,
        				function(tota, isError) {
        					if (!isError) {	

        						$scope.alllist=tota[0].body.allList;
        						//已轉銷售陣列				
        						$scope.paramListy =[];
        						$scope.paramListy =tota[0].body.allList.listy.resultList;
        						$scope.outputVOy = angular.copy(tota[0].body);
        						//迴圈給定最後一頁值 已轉銷售
        						angular.forEach($scope.paramListy, function(row, index, objs){
        							row.PLAN_YEARMON=$scope.inputVO.sCreDate;  //增加年月資訊
        							if(index==$scope.paramListy.length-1)
        								row.AP='true';
        						});
        						
        						/*******暫存年月*******/
        						$scope.sCreDate= angular.copy($scope.inputVO.sCreDate);
        						//合計function paramListy
        						for(var i = 0; i < $scope.paramListy.length; i++){
        							$scope.sumExpToty += $scope.paramListy[i].EXP_CF_TOTAL;
        							$scope.sumExpPlany += $scope.paramListy[i].EXP_CF_PLAN;
        					       	$scope.sumExpBaly += $scope.paramListy[i].EXP_CF_BAL;
        							$scope.sumRecToty += $scope.paramListy[i].REC_CF_TOTAL;
        							$scope.sumRecTxny += $scope.paramListy[i].REC_CF_TXN;
        							$scope.sumRecPlany += $scope.paramListy[i].REC_CF_PLAN;
        					        $scope.sumRecBaly += $scope.paramListy[i].REC_CF_BAL;
        							$scope.sumRecDepy += $scope.paramListy[i].REC_CF_YTD_DEP;
        						}
        						return;
        					}
        				});
        	}
        	
        	if($scope.shows == 'N' && $scope.quertType == "Y"){
        		$scope.sendRecv("PMS101", "queryDataN", "com.systex.jbranch.app.server.fps.pms101.PMS101InputVO", $scope.inputVO,
    					function(tota, isError) {
    						if (!isError) {	

    							$scope.alllist=tota[0].body.allList;
    							$scope.outputVOy = angular.copy(tota[0].body);
    							//未轉銷售陣列
    							$scope.paramList =[];
    							$scope.paramList =tota[0].body.allList.listn.resultList;
    							$scope.outputVOn = angular.copy(tota[0].body);
    							
    							//迴圈給定最後一頁值 未轉銷售
    							angular.forEach($scope.paramList, function(row, index, objs){
    								row.PLAN_YEARMON=$scope.inputVO.sCreDate;   //增加年月資訊
    								if(index==$scope.paramList.length-1)     
    									row.AP='true';
    							});
    							/*******暫存年月*******/
    							$scope.sCreDate= angular.copy($scope.inputVO.sCreDate);
    							//合計function paramList
    							for(var i = 0; i < $scope.paramList.length; i++){
    								$scope.sumExpTot += $scope.paramList[i].EXP_CF_TOTAL;
    								$scope.sumExpPlan += $scope.paramList[i].EXP_CF_PLAN;
    						       	$scope.sumExpBal += $scope.paramList[i].EXP_CF_BAL;
    								$scope.sumRecTot += $scope.paramList[i].REC_CF_TOTAL;
    								$scope.sumRecTxn += $scope.paramList[i].REC_CF_TXN;
    								$scope.sumRecPlan += $scope.paramList[i].REC_CF_PLAN;
    					        	$scope.sumRecBal += $scope.paramList[i].REC_CF_BAL;
    								$scope.sumRecDep += $scope.paramList[i].REC_CF_YTD_DEP;
    							}
    							return;
    						}
    					});
        	}
        	
		};
		
		
		//設定月份
		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+2;        
        var strmm='';        
        $scope.mappingSet['timeE'] = [];
        //#0000375: 報表留存時間 四個月
        for(var i=0; i<=3; i++){   //年月增加一個月
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
        
        
    	// combobox:CRM.REL_TYPE 關係名稱
		getParameter.XML(["CRM.REL_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CRM.REL_TYPE']  = totas.data[totas.key.indexOf('CRM.REL_TYPE')];		
			}
		});
               
        $scope.nowDate = new Date();
        /*** 可示範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate   ;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"       	
        	//可視範圍  觸發 
        	if($scope.inputVO.sCreDate!=''){
        		$scope.RegionController_getORG($scope.inputVO);
        		$scope.inputVO.branch_nbr = $scope.connector('get','CRM171_BRANCH') || '';
        	}
        };      
		$scope.curDate = new Date();
		
		/*** 可示範圍  JACKY共用版  END***/
           
       

		/****初始設定未轉銷售/已轉銷售*****/
		    $scope.mappingSet['type']=[];
            $scope.mappingSet['type'].push({LABEL : '已入帳未聯繫',DATA :'Y'},{LABEL : '已入帳且流失',DATA :'N'});
            
         /****換圖片FUNCTION******/
        $scope.ale=  function(data){
        	if(data==1)
        	return '<img src="assets/images/icon/u1210.jpg" >'
        }
        
		/******初始化*******/	
	    $scope.init = function(){
	    	var dt = new Date();
	    	dt.setMonth(dt.getMonth());
			$scope.inputVO = {
				CUST_ID:'',
				PROD_TYPE:'',
				TYPE:'',      //已規劃/未規劃
				branch_area_id: '',   //營運區
				branch_nbr: '',    //分行
				ao_code: '',	  //理專
				sCreDate: $filter('date')(dt, 'yyyyMM'),	//年月	
				emp_id: ''	  //理專員編
	        };
			$scope.showSum = false;
			/*******暫存年月*******/
			$scope.sCreDate=$filter('date')(dt, 'yyyyMM');
			
			/*****本日時間******/
			$scope.curDate = new Date();
			$scope.inputVO.sCreDate=  $scope.connector('get','CRM171_lastMonthYN') || ''; //時間
//			$scope.inputVO.TYPE= $scope.connector('get','CRM171_TYPE') || '';  //未轉銷
			$scope.inputVO.CUST_ID= $scope.connector('get','CRM171_CUSTID') || '';   //客戶ID
			$scope.inputVO.ao_code= $scope.connector('get','CRM171_AO_CODE') || '';    //員工ID
			//測試
			if($scope.inputVO.sCreDate!=''){
				$scope.dateChange();
			}
			if($scope.connector('get','CRM171_AO_CODE')!=undefined){
				$scope.inquire();
			}
			
			$scope.paramList=[];
			$scope.paramListy=[];
			$scope.datan=[];
			$scope.datay=[];
			$scope.outputVOy={};
			$scope.outputVOn={};
			
			//預設為未規劃
			if($scope.inputVO.sCreDate!=''){
				if($scope.inputVO.TYPE==undefined){
					$scope.inputVO.TYPE='N';
				}
			}
			$scope.shows='N';
			$scope.quertType = "N";
	    }  
	    $scope.init();  
	    
		//未轉銷售計劃/已轉銷售計劃頁面切換
		$scope.pinse = function(TYPE){
			$scope.shows = TYPE;
			if($scope.quertType == "Y"){
				$scope.query();
			}
		}
		
	    /*****新增資料'呼叫PMS109*****/
		$scope.edit = function (row) {
			row.PLAN_YEARMON = $scope.inputVO.sTime;
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS109/PMS109.html',
				className: 'PMS109',
				controller: ['$scope', function($scope) {
					$scope.cust_id = row.CUST_ID;
					$scope.src_type = 'PMS101';
				}]
			});
			dialog.closePromise.then(function (data) {
				$scope.inquire();
			});
		};
		
        /*****以下是清單畫面'*****/
		$scope.edit2 = function (row,DATES) { 	 
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS101/PMS101_LEADS.html',
				className: 'PMS101_LEADS',
				controller: ['$scope', function($scope) {
					$scope.row = row;
					$scope.DATA_DATE=DATES;
				}]
			});
		};
		
		
		  /****查詢****/
        $scope.UpDate = function(inde){ 
        	$scope.inputVO2={
        			CUST_ID:$scope.paramListy[inde].CUST_ID,   //客戶ID
//        			PROD_TYPE:'', //商品類型 
//        			TYPE:'',      //狀態
        			SEQ:$scope.paramListy[inde].SEQ,       //流水編號
        			sTime:$scope.paramListy[inde].PLAN_YEARMON,     //年月  (有疑問)
        			ao_code:$scope.paramListy[inde].AO_CODE,   //AO_CODE
        			branch_nbr:$scope.paramListy[inde].BRANCH_NBR //分行
        	}
      
			$scope.sendRecv("PMS103", "queryData", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", $scope.inputVO2,
					function(tota, isError) {
						if (!isError) {
//							$scope.paramList = [];
							if(tota[0].body.resultList.length == 0) {						
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}							
							$scope.paramList_One = tota[0].body.resultList;  //結果
							$scope.ppap($scope.paramList_One[0].SEQ, $scope.paramList_One[0].CUST_ID, $scope.paramList_One[0].CUST_NAME, '1', 'upd');						
							
							return;
						}
			});	
        }
		
});
