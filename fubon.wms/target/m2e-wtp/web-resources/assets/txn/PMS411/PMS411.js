/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS411Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS411Controller";
		$controller('PMSRegionController', {$scope: $scope});
		
		var NowDate = new Date();
//		//***目前提供測試用設定年月***//
//		NowDate.setYear(2017);
//		NowDate.setMonth(6);
//		//***測試用設定年月END***//
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<36; i++){
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
        
     
//        //選取月份下拉選單 --> 重新設定可視範圍
//        $scope.dataMonthChange = function(){
//        	$scope.inputVO.rc_id = '';
//        	$scope.inputVO.op_id = '';
//        	$scope.inputVO.br_id = '';
//        	$scope.inputVO.emp_id = '';
//        };
        
        /*** 可示範圍  JACKY共用版  START ***/
        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//設定回傳時間
        	if($scope.inputVO.sCreDate!=''){
        		$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
        
		$scope.curDate = new Date();
		
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		/*** 可示範圍  JACKY共用版  END***/
           
            
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		
		$scope.init = function(){
			$scope.inputVO = {	
					sCreDate :'',
					dataMonth: '',					
					rc_id: '',
					op_id: '' ,
					br_id: '',
					emp_id: ''
        	};
			$scope.curDate = new Date();
			$scope.outputVO={totalList:[]};
			$scope.paramList = [];
			$scope.csvList=[];
			//把鎖定清掉
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.outputVO={totalList:[{DATA_DATE:'查無資料'}]};
		}
		$scope.inquireInit();
		
		$scope.query = function(){
			
			$scope.sumINVEST_AMT=0;
			$scope.sumNOMATCH_AMT=0;
			$scope.sumAST_AMT=0;
			
			if($scope.inputVO.sCreDate==''||$scope.inputVO.sCreDate==undefined){
				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位(資料統計月份)');
        		return;
        	}
			
			//清空預設值*********
			if($scope.inputVO.sCreDate=='197001')
				$scope.inputVO.sCreDate='';
			

			
			
			$scope.sendRecv("PMS411", "queryData", "com.systex.jbranch.app.server.fps.pms411.PMS411InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								
								$scope.paramList = [];
								$scope.totalData = [];
								$scope.csvList=[];
								$scope.outputVO={totalList:[{DATA_DATE:'查無資料'}]};
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList=tota[0].body.csvList;
							//取得分頁數值   用來做分頁
							$scope.page = tota[0].body.currentPageIndex;
							$scope.pageT = tota[0].body.totalPage;
							$scope.listT = tota[0].body.totalList;
							angular.forEach($scope.paramList, function(row, index, objs){
//								if(row.CUST_ID.trim() != ""){
//									row.CUST_ID=row.CUST_ID.substring(0, 4)+"****"+row.CUST_ID.substring(8, 10);    //隱藏身分證 四碼
//								}
								if(row.CUST_ID=="N"){
									row.CUST_ID   = "";
									row.CUST_NAME = "無資料";
								}
							});	
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;
							
							for(var i= 0;i<$scope.paramList.length;i++){
								$scope.sumINVEST_AMT += $scope.paramList[i].INVEST_AMT;
								$scope.sumNOMATCH_AMT += $scope.paramList[i].NOMATCH_AMT;
								$scope.sumAST_AMT += $scope.paramList[i].AST_AMT;
							}
							//小計
							$scope.getSUM1($scope.paramList);
							//總計
							if($scope.page == $scope.pageT-1 && $scope.paramList.length > 0){
								$scope.getSUM2($scope.listT);
							}
							return;
						}						
			});
			
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

			
		};
		/*0002251 */
		$scope.exportRPT = function(){
			
			$scope.sendRecv("PMS411", "export", "com.systex.jbranch.app.server.fps.pms411.PMS411OutputVO",{'csvList':$scope.csvList} ,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};
		//小計
		$scope.getSUM1 = function(row) {
			$scope.INVEST_AMT = 0 ;			//不適配投資金額
			$scope.NOMATCH_AMT = 0 ;		//投資總金額
			$scope.AST_AMT = 0 ;			//資產總金額
			$scope.NOMATCH_PERCENT = 0 ;	//風險不適配投資%
			
			for(var i = 0; i < row.length; i++) {
				$scope.INVEST_AMT += row[i].INVEST_AMT ;
				$scope.NOMATCH_AMT += row[i].NOMATCH_AMT ;
				$scope.AST_AMT += row[i].AST_AMT ;
			}
			if($scope.NOMATCH_AMT * $scope.INVEST_AMT != 0){
				$scope.NOMATCH_PERCENT = $scope.NOMATCH_AMT / $scope.INVEST_AMT * 100;
			}
		}
		//總計
		$scope.getSUM2 = function(row) {
			$scope.INVEST_AMT2 = 0 ;		//不適配投資金額
			$scope.NOMATCH_AMT2 = 0 ;		//投資總金額
			$scope.AST_AMT2 = 0 ;			//資產總金額
			$scope.NOMATCH_PERCENT2 = 0 ;	//風險不適配投資%
			$scope.COUNT = 0 ;				//個數
			
			for(var i = 0; i < row.length; i++) {
				$scope.INVEST_AMT2 += row[i].INVEST_AMT ;
				$scope.NOMATCH_AMT2 += row[i].NOMATCH_AMT ;
				$scope.AST_AMT2 += row[i].AST_AMT ;
				$scope.COUNT += 1 ;	
			}
			if($scope.NOMATCH_AMT2 * $scope.INVEST_AMT2 != 0){
				$scope.NOMATCH_PERCENT2 = ($scope.NOMATCH_AMT2 / $scope.INVEST_AMT2) * 100;
			}
		}
});
