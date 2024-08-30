/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS315Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS315Controller";
		
		/*** 可視範圍  JACKY共用版  START ***/
		$controller('PMSRegionController', {$scope: $scope});
		$scope.dateChange = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	$scope.RegionController_getORG($scope.inputVO);
		};
		
		//月報選單
        // var NowDate = new Date();
        // if(NowDate.getMonth()<10){
        // 	var ym = NowDate.getFullYear() + '0' + NowDate.getMonth();
        // }else if(NowDate.getMonth() >= 10){
        // 	var ym = NowDate.getFullYear() + '' + NowDate.getMonth();
        // }
        
        
        /********************
         * 使用日期資訊inputVO參數
         * PAID_DATES 起  
         * PAID_DATEE 迄  
         * 欄位名稱: 應繳日期    
         ********************/
        // date picker
   		$scope.bgn_sDateOptionsPAID = {
   			maxDate: $scope.maxDate,
   			minDate: $scope.minDate
   		};
   		$scope.bgn_eDateOptionsPAID = {
   				maxDate: $scope.maxDate,
   				minDate: $scope.minDate
   		};
   		// config
   		$scope.model = {};
   		$scope.open = function($event, elementOpened) {
   			$event.preventDefault();
   			$event.stopPropagation();
   			$scope.model[elementOpened] = !$scope.model[elementOpened];
   		};
   		$scope.limitDatePAID = function() {
   			$scope.bgn_sDateOptionsPAID.maxDate = $scope.inputVO.PAID_DATEE || $scope.maxDate;
   			$scope.bgn_eDateOptionsPAID.minDate = $scope.inputVO.PAID_DATES || $scope.minDate;
   		};
   		
   		/********************
         * 使用日期資訊inputVO參數
         * FST_ACT_DTS 起   
         * FST_ACT_DTE 迄  
         * 欄位名稱: 預計第一次扣款日   
         *********************/
        // date picker
   		$scope.bgn_sDateOptionsFST = {
   			maxDate: $scope.maxDateFST,
   			minDate: $scope.minDateFST
   		};
   		$scope.bgn_eDateOptionsFST = {
   				maxDate: $scope.maxDateFST,
   				minDate: $scope.minDateFST
   		};
   		// config   	
   		$scope.limitDateFST = function() {
   			$scope.bgn_sDateOptionsFST.maxDate = $scope.inputVO.FST_ACT_DTE || $scope.maxDate;
   			$scope.bgn_eDateOptionsFST.minDate = $scope.inputVO.FST_ACT_DTS || $scope.minDate;
   		};
   		
        
        
        
//        $scope.initLoad = function(){
//			// $scope.sendRecv("PMS315", "initialQuery", "com.systex.jbranch.app.server.fps.pms315.PMS315InputVO", {},
//			// 		   function(totas, isError) {
//			// 	             	if (totas.length > 0) {
//			// 	               		$scope.ymList = totas[0].body.ymList;
//			// 	               		$scope.inputVO.sCreDate = $scope.ymList[0].DATA;
//			// 	               	};
//			// 		   }
//			// );
//
//
//				debugger
//				// 單號 5829 : 	商品相關管理報表中保費續期通知月報一點選執行時間非常久才能操作,請調整功能效能,留存近12個月資訊即可
//				// 改由前端撈取
//				var NowDate = new Date();
//			    NowDate.setMonth(NowDate.getMonth()+1); 
//			    var strMon = '';
//				$scope.mappingSet['timeE'] = [];
//				
//			    //資料日期區間限制為1年內資料
//			    for(var i = 0; i < 12; i++){
//			    	
//			    	strMon = NowDate.getMonth()+1;
//			    	//10月以下做文字處理，+0 在前面
//			    	if(strMon < 10 ){
//			    		strMon = '0' + strMon;
//			    	}
//			    	
//			    	$scope.mappingSet['timeE'] .push({
//			    		LABEL: NowDate.getFullYear() + '/' + strMon,
//			    		DATA : NowDate.getFullYear() + '' + strMon
//			    	}); 
//			    	//每一筆減一個月，倒回去取一年內日期區間
//			    	NowDate.setMonth(NowDate.getMonth()-1);
//			    }
//			    $scope.inputVO.sCreDate = $scope.mappingSet['timeE'][0].DATA;			
//		}
//		
//		$scope.initLoad();
		
		
		
		/***參數先設定在此      要改用DB paramtype 再改 CR問題單:0003703***/
		$scope.mappingSet['PAID_STAT']=[];
        $scope.mappingSet['PAID_STAT'].push(
        		{LABEL : '已繳費',DATA :'已繳費'},
        		{LABEL : '扣款失敗',DATA :'扣款失敗'},
        		{LABEL : '尚未繳費',DATA :'尚未繳費'}
        );
        $scope.mappingSet['MOP_T']=[];
        $scope.mappingSet['MOP_T'].push(
        		{LABEL : '月繳',DATA :'月繳'},
        		{LABEL : '季繳',DATA :'季繳'},
        		{LABEL : '年繳',DATA :'年繳'},
        		{LABEL : '半年繳',DATA :'半年繳'}
        );
        
		
		$scope.init = function(){
			
			//KOTODO 單號 5829 : 清除-->出現for input string: "" / console-->java.lang.NumberFormatException
			var NowDate = new Date();
		    NowDate.setMonth(NowDate.getMonth()+1); 
		    var strMon = '';
			$scope.mappingSet['timeE'] = [];
			
		    //資料日期區間限制為1年內資料
		    for(var i = 0; i < 12; i++){
		    	
		    	strMon = NowDate.getMonth()+1;
		    	//10月以下做文字處理，+0 在前面
		    	if(strMon < 10 ){
		    		strMon = '0' + strMon;
		    	}
		    	
		    	$scope.mappingSet['timeE'] .push({
		    		LABEL: NowDate.getFullYear() + '/' + strMon,
		    		DATA : NowDate.getFullYear() + '' + strMon
		    	}); 
		    	//每一筆減一個月，倒回去取一年內日期區間
		    	NowDate.setMonth(NowDate.getMonth()-1);
		    }	
			
			
			$scope.inputVO = {
					sCreDate : $scope.mappingSet['timeE'][0].DATA,
					region_center_id : '',
					branch_area_id : '',
					branch_nbr : '',
					ao_code : '',
					emp_id : '',
					CUST_ID : '',       //要保人ID
					CUST_NAME : '',     //要保人姓名
					POLICY_NO : '',     //保單號碼
					POLICY_SEQ : '',    //保單序號
					PAID_STAT  : '',    //繳費狀態
					PAID_DATES : undefined,    //應繳日期起
					PAID_DATEE : undefined,    //應繳日期訖
					FST_ACT_DTS : undefined,   //預計第一次扣款日起
					FST_ACT_DTE : undefined,   //預計第一次扣款日迄
					MOP_T    : '',      //繳別
			};
			
			$scope.dateChange();
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
            $scope.paramList = [];  
		};
		$scope.init();
		
		/*** 可視範圍  JACKY共用版  END***/
		
		$scope.inquire = function(){
			if($scope.inputVO.sCreDate == ''){
				$scope.showMsg("欄位檢核錯誤：報表月份為必要欄位");
				return;
			}
			$scope.sendRecv("PMS315", "inquire", "com.systex.jbranch.app.server.fps.pms315.PMS315InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.csvList=[];
								$scope.outputVO = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		
		$scope.save = function(){
			$scope.sendRecv("PMS315", "update", "com.systex.jbranch.app.server.fps.pms315.PMS315InputVO",{'list':$scope.paramList},
					function(tota, isError) {
						if (!isError) {
							if (isError) {
    	                		$scope.showErrorMsg(tota[0].body.msgData);
    	                	}
    	                	if (tota.length > 0) {
    	                		$scope.showSuccessMsg('新增成功');
    	                	};
							return;
						}
			});
		};

});