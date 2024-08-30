'use strict';
eSoafApp.controller('PMS711Controller', function($rootScope, $scope,
		$controller, $confirm, $filter,socketService, ngDialog, projInfoService) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS711Controller";
	//初始化
	$scope.init = function(){
		$scope.inputVO = {
				date_year  : '',
				personType : '',
				SDate      : undefined,
				EDate      : undefined,
				fullScore  : '',
				branchs    : '',
				selectFlag : '',
				examStats  : '',
				upRate     : '',
				kpiScore   : '',
				statRate   : '',
				scoreValue : ''
				
		};
		$scope.resultList = [];
		$scope.statsList = [];
		$scope.showList = [];
		$scope.showList1 = [];
	}
	$scope.init();
	$scope.ifShow = false;
	$scope.ifShowES1 = false;
	$scope.inquireInit = function(){
		$scope.resultList = [];
		$scope.statsList = [];
		$scope.showList = [];
		$scope.showList1 = [];

	}
	var vo = {'param_type': 'PMS.PERSON_TYPE', 'desc': false};
	$scope.requestComboBox(vo, function(totas) {      	
		if (totas[totas.length - 1].body.result === 'success') {        		
			projInfoService.mappingSet['PMS.PERSON_TYPE'] = totas[0].body.result;
			$scope.mappingSet['personType'] = projInfoService.mappingSet['PMS.PERSON_TYPE'];
		}
	});
	$scope.isq = function(){
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
		$scope.mappingSet['date_year'] = [];
		for (var i = 0; i < 12; i++) {
			$scope.mappingSet['date_year'].push({
				LABEL : yr ,
				DATA : yr
			});
			yr = yr - 1; 
		} 
	};
	$scope.isq();
	
	//FC,FCH,PS,分行個金主管整批上傳
	$scope.batchUpload = function(date_year) {
		var personType = $scope.inputVO.personType;
		var subProjectSeqId = $scope.inputVO.subProjectSeqId;
		if($scope.inputVO.date_year == null || $scope.inputVO.date_year == '' || $scope.inputVO.personType == null || $scope.inputVO.personType == '')
    	{
    		$scope.showErrorMsg('欄位檢核錯誤:KPI年度與人員類別必填');
    		return;
    	}
		var dialog = ngDialog.open({
			template : 'assets/txn/PMS711/PMS711_batchUpload.html',
			className : 'PMS711_batchUpload',
			showClose : false,
			controller : [ '$scope', function($scope) {
				$scope.date_year = date_year,
				$scope.personType = personType;
				
			} ]
		});
		dialog.closePromise.then(function(data) {
			if (data.value == 'cancel') {
				$scope.query(personType);
			}
		});
	}
	
	//非系統計算人工上傳項目整批上傳
	$scope.batchUpload2 = function(date_year) {
		var personType = $scope.inputVO.personType;
		var subProjectSeqId = $scope.inputVO.subProjectSeqId;
		if($scope.inputVO.date_year == null || $scope.inputVO.date_year == '' || $scope.inputVO.personType == null || $scope.inputVO.personType == '')
    	{
    		$scope.showErrorMsg('欄位檢核錯誤:KPI年度與人員類別必填');
    		return;
    	}
		var dialog = ngDialog.open({
			template : 'assets/txn/PMS711/PMS711_batchUpload2.html',
			className : 'PMS711_batchUpload2',
			showClose : false,
			controller : [ '$scope', function($scope) {
				$scope.date_year = date_year,
				$scope.personType = personType;
			} ]
		});
		dialog.closePromise.then(function(data) {
			if (data.value == 'cancel') {
				$scope.queryAdd();
			}
		});
	}
	
	//堆疊關係設定
	$scope.batchUpload3 = function(date_year) {
		var personType = $scope.inputVO.personType;
		var subProjectSeqId = $scope.inputVO.subProjectSeqId;
		if($scope.inputVO.date_year == null || $scope.inputVO.date_year == '' || $scope.inputVO.personType == null || $scope.inputVO.personType == '')
    	{
    		$scope.showErrorMsg('欄位檢核錯誤:KPI年度與人員類別必填');
    		return;
    	}
		var dialog = ngDialog.open({
			template : 'assets/txn/PMS711/PMS711_batchUpload3.html',
			className : 'PMS711_batchUpload3',
			showClose : false,
			controller : [ '$scope', function($scope) {
				$scope.date_year = date_year,
				$scope.personType = personType;
				
			} ]
		});
		dialog.closePromise.then(function(data) {
			if (data.value == 'cancel') {
				$scope.query(personType);
			}
		});
	}
		
	 //查詢功能
    $scope.query = function(personType){
    	$scope.inputVO.SDate=undefined;
    	$scope.inputVO.EDate=undefined;
    	$scope.inputVO.isSpecial = '0';
    	$scope.perType = personType;
    	
    	if($scope.inputVO.date_year == null || $scope.inputVO.date_year == '')
    	{
    		$scope.showErrorMsg('欄位檢核錯誤:參數月份必填');
    		return;
    	}
    	if($scope.inputVO.personType == null || $scope.inputVO.personType == '')
    	{
    		$scope.showErrorMsg('欄位檢核錯誤:人員類別必填');
    		return;
    	}
    	
    	$scope.changeToCommon();
    	
    	//查詢KPI期間設定
		$scope.sendRecv("PMS711", "queryData", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.showList.length == 0) {
							$scope.ifShow = false;
                			return;
                		}
						$scope.ifShow = true;
						$scope.showList = tota[0].body.showList;
						$scope.personTypeName = $scope.showList[0].PERSON_TYPE_NAME;
						
						return;
					}
		});
		
		$scope.queryExamStats();
		$scope.queryAdd();
		
		//查詢KPI期間設定計績期間初始值
		$scope.sendRecv("PMS711", "queryKpiDay", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
		        		$scope.showErrorMsg(tota[0].body.msgData);
		        	}
					$scope.inputVO.fullScore = '';
					$scope.inputVO.SDate = '';
					$scope.inputVO.EDate = '';
					
		        	if (tota[0].body.kpiDayList.length > 0 && tota[0].body.kpiDayList[0].START_DAY != undefined 
		        			&& tota[0].body.kpiDayList[0].END_DAY != undefined) {
						$scope.kpiDayList = tota[0].body.kpiDayList;
						var	SDate = $scope.kpiDayList[0].START_DAY.substring(0,4)
							+"/"+$scope.kpiDayList[0].START_DAY.substring(4,6)
							+"/"+$scope.kpiDayList[0].START_DAY.substring(6,8);
						var EDate = $scope.kpiDayList[0].END_DAY.substring(0,4)
							+"/"+$scope.kpiDayList[0].END_DAY.substring(4,6)
							+"/"+$scope.kpiDayList[0].END_DAY.substring(6,8);
						
						var fullScore = $scope.kpiDayList[0].FULL_SCORE;
						
						$scope.inputVO.SDate = new Date(SDate);
						$scope.inputVO.EDate = new Date(EDate);
						$scope.inputVO.fullScore = fullScore;
						
		    			}
		        	$scope.setTotalMon();
					return;
		});				
    }     
    
    //調整至人員類別維護界面
    $scope.maintainPT = function(){
    	var dialog = ngDialog.open({
    		template: 'assets/txn/PMS711/PMS711_maintainPT.html',
			className: 'PMS711_maintainPT',
    		showClose : true,
    		controller: ['$scope', function($scope) {
            }]
    	});
		//關閉子界面時，刷新主界面
    	dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    			$scope.sendRecv("PMS711", "queryPerson", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (!isError) {
    	                		if(totas[0].body.personTypeList.length == 0) {
    	                			return;
    	                		}
    	                		$scope.mappingSet['personType'] = [];
    	                		$scope.inputVO.personType = '';
    		                	angular.forEach(totas[0].body.personTypeList, function(row, index, objs){
    	        					$scope.mappingSet['personType'].push({LABEL: row.PERSON_TYPE_NAME,DATA: row.PERSON_TYPE});
    	                		});
    	                	}
    					});
				}
    	});
    }
	
    //查詢KPI項目
    $scope.queryExamStats = function() {
    	$scope.sendRecv("PMS711", "queryExamStats", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.statsList.length == 0) {
							$scope.ifShowES = false;
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.ifShowES = true;
						$scope.statsList = tota[0].body.statsList;
						//分數秀出來
						angular.forEach($scope.statsList, function(row, index, objs){
							if(row.SELECT_FLAG == '1')
								$scope.inputVO.scoreValue = $scope.accAdd($scope.inputVO.scoreValue,row.KPI_SCORE);
						});						
						$scope.inputVO.branchs = tota[0].body.statsList[0].BRANCHS;
						return;
					}
		});    	
    }

    $scope.setTotalMon = function() {
    	var SDate = new Date($scope.inputVO.SDate);
    	var EDate = new Date($scope.inputVO.EDate);
    	$scope.syyyy = $filter('date')(SDate,'yyyy');
    	$scope.eyyyy = $filter('date')(EDate,'yyyy');
    	$scope.smm = $filter('date')(SDate,'MM');
    	$scope.emm = $filter('date')(EDate,'MM');
    	$scope.sdd = $filter('date')(SDate,'dd');
    	$scope.edd = $filter('date')(EDate,'dd');
    	
    	var diffyear = $scope.eyyyy-$scope.syyyy;
    	var diffmonth = $scope.emm - $scope.smm + diffyear*12;
    	var diffday = $scope.edd - $scope.sdd;
    	var EDateAdd1 = new Date(EDate.getTime() + 24 * 3600 * 1000);
    	if(EDateAdd1.getMonth() != EDate.getMonth()){
    		diffmonth = diffmonth + 1;
    		
    	}
    	$scope.totalMon = diffmonth;
    	$scope.ifNaN = isNaN($scope.totalMon);
    	var yyyy = $scope.syyyy;
    	var mm = Number($scope.smm);
    	$scope.mappingSet['timeE'] = [];
    	for(var i=1; i<=$scope.totalMon; i++){
    		 var tempMon = mm;
    		if(mm < 10)
    			tempMon = '0' + mm;
    		else if(mm > 12){
				yyyy++;
				mm = 1;
				tempMon = '01';
    		}
    		mm++;
    		$scope.mappingSet['timeE'].push({
				LABEL : yyyy +''+ tempMon,
				DATA :	'MONTH'+i
			});
    	}
    	$scope.showMonthList = new Object();
    	for(var col in $scope.mappingSet['timeE']){
    		if ($scope.mappingSet['timeE'].hasOwnProperty(col)){
    			$scope.showMonthList[$scope.mappingSet['timeE'][col].DATA] = $scope.mappingSet['timeE'][col].LABEL;
    		}    		
    	}
	};
	
	$scope.changeCheck = function(event,index){
		if(null==$scope.statsList[index].KPI_SCORE || String($scope.statsList[index].KPI_SCORE)==''){
			$scope.showMsg("分數不能為空");
			$(event.target).removeAttr("checked");
		}
		
		if($(event.target).is(":checked")){
			$scope.statsList[index].SELECT_FLAG='1'
		}else{
			$scope.statsList[index].SELECT_FLAG='0'
		}
	}
	$scope.changeSe = function(index){
		$scope.connector('set','PMS_711TYPE', $scope.statsList[index].SUB_PROJECT_SEQ_NAME);
	}
	
	$scope.changeAddCheck = function(event,index){
		if(null==$scope.addToList[index].KPI_SCORE || String($scope.addToList[index].KPI_SCORE)==''){
			$scope.showMsg("分數不能為空");
			$(event.target).removeAttr("checked");
		}
		
		if($(event.target).is(":checked")){
			$scope.addToList[index].SELECT_FLAG='1'
		}else{
			$scope.addToList[index].SELECT_FLAG='0'
		}
	}
	
	//查詢非系統計算人工上傳項目
	$scope.queryAdd = function() {
    	$scope.sendRecv("PMS711", "queryAdd", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.addToList = tota[0].body.addToList;
						
						angular.forEach($scope.addToList, function(row, index, objs){
							if(row.SELECT_FLAG == '1')
								$scope.inputVO.scoreValue = $scope.accAdd($scope.inputVO.scoreValue,row.KPI_SCORE);
						});
						
						return;
					}
		});
    }
	//將輸入框中的內容加入addToList中
	$scope.toList = function() {
		var KPI_PROJECT = $scope.inputVO.KPI_PROJECT;
		var SUB_PROJECT_SEQ_NAME = $scope.inputVO.SUB_PROJECT_SEQ_NAME;
		var SUB_PROJECT_SEQ_ID = 27;
		var flagA = 0;
		if($scope.addToList != null){
			for(var i = 0; i < $scope.addToList.length; i++){				
				var flag = 0;
				for(var row in $scope.addToList){
					var id = $scope.addToList[row].SUB_PROJECT_SEQ_ID;
					if(id == SUB_PROJECT_SEQ_ID + i){
						flag = 1;
						break;
					}
				}
				if(flag ==0){
					SUB_PROJECT_SEQ_ID = SUB_PROJECT_SEQ_ID + i;
					flagA = 1;
					break;
				}
			}
			if(flagA == 0){
				SUB_PROJECT_SEQ_ID = SUB_PROJECT_SEQ_ID + $scope.addToList.length;
			}
		}
		if (KPI_PROJECT == null || KPI_PROJECT == ""
				|| SUB_PROJECT_SEQ_NAME == null  || SUB_PROJECT_SEQ_NAME == ""){
			$scope.showMsg("ehl_01_common_022");  //欄位檢核錯誤：必要輸入欄位
			return;
		}
		for (var i = 0; i < $scope.addToList.length; i++) {
			if ($scope.addToList[i].SUB_PROJECT_SEQ_NAME == SUB_PROJECT_SEQ_NAME) {
				$scope.showMsg("ehl_01_common_016");  //資料已存在
				return;
			}
		}
		var a = new Object();
		a.KPI_PROJECT = KPI_PROJECT;
		a.SUB_PROJECT_SEQ_NAME = SUB_PROJECT_SEQ_NAME;
		a.EXAM_STATS = 0;
		a.SUB_PROJECT_SEQ_ID = SUB_PROJECT_SEQ_ID;
		$scope.subPro = SUB_PROJECT_SEQ_ID;
		a.STAT_FLAG = 0;
		a.UP_RATE = 100;
		a.SELECT_FLAG = 0;
		$scope.addToList.push(a);
		$scope.inputVO.KPI_PROJECT = null;
		$scope.inputVO.SUB_PROJECT_SEQ_NAME = null;
	}
	
	//將表單數據插入或更新到數據庫
	$scope.save = function() {
		$scope.inputVO.addToList = $scope.addToList;
		$scope.sendRecv(
			"PMS711","addKpi","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
			$scope.inputVO,
			function(tota, isError) {
				if(!isError){
					$scope.showMsg("ehl_01_common_002"); //成功
					$scope.queryAdd();
				}else{
					$scope.showMsg("ehl_01_common_024"); //執行失敗
				}
				return;
			});
	}
	
	$scope.accAdd = function(num1,num2){  		
	   var r1,r2,m;  
       try{  
           r1 = num1.toString().split('.')[1].length;  
       }catch(e){  
           r1 = 0;  
       }  
       try{  
           r2=num2.toString().split(".")[1].length;  
       }catch(e){  
           r2=0;  
       }  
       m=Math.pow(10,Math.max(r1,r2)); 
       return Math.round(num1*m+num2*m)/m;         
	 }  
	
	$scope.saveChange = function() {
		$scope.inputVO.scoreValue = 0;
		var selectCnt = 0;
		for(var i in $scope.statsList){
			if($scope.statsList[i].SELECT_FLAG == '1'){
				if(null==$scope.statsList[i].UP_RATE || String($scope.statsList[i].UP_RATE)==''){
					$scope.statsList[i].UP_RATE = 100;
				}
				if( null==$scope.statsList[i].KPI_SCORE || String($scope.statsList[i].KPI_SCORE)==''){
					$scope.showMsg("分數不能為空");
					return;
				}else{
					$scope.inputVO.scoreValue = $scope.accAdd($scope.inputVO.scoreValue,$scope.statsList[i].KPI_SCORE);
					
				}
				selectCnt +=1;
			}
		}
		
		for(var i in $scope.addToList){
			if($scope.addToList[i].SELECT_FLAG == '1'){
				if(null==$scope.addToList[i].UP_RATE || String($scope.addToList[i].UP_RATE)==''){
					$scope.addToList[i].UP_RATE = 100;
				}
				if( null==$scope.addToList[i].KPI_SCORE || String($scope.addToList[i].KPI_SCORE)==''){
					$scope.showMsg("分數不能為空");
					return;
				}else{
					$scope.inputVO.scoreValue = $scope.accAdd($scope.inputVO.scoreValue,$scope.addToList[i].KPI_SCORE);
				}
				selectCnt +=1;
			}
		}
		$scope.inputVO.statsList = $scope.statsList;
		$scope.inputVO.addToList = $scope.addToList;
		$scope.inputVO.showList = $scope.showList;
		var SDate = new Date($scope.inputVO.SDate);
    	var EDate = new Date($scope.inputVO.EDate);
    	$scope.inputVO.SDate1 = $filter('date')(SDate,'yyyyMMdd');
    	$scope.inputVO.EDate1 = $filter('date')(EDate,'yyyyMMdd');
    	if(($scope.showList[0].START_DAY != $filter('date')($scope.inputVO.SDate,'yyyyMMdd') )
    			|| ($scope.showList[0].END_DAY != $filter('date')($scope.inputVO.EDate,'yyyyMMdd'))){
    		$confirm({text: "計績區間改變將影響其他人員類別，確定儲存嗎？"}, {size: 'sm'}).then(function() {
    			$scope.saveKpi();
    		});
    	}else{
    		$scope.saveKpi();
    	}
		if($scope.inputVO.scoreValue != 100 && selectCnt != 0){
			$scope.showMsg("勾選項目分數總和不為100");
		}
		
	};
	$scope.saveKpi = function(){
		$scope.sendRecv("PMS711", "addKpi", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.sendRecv("PMS711", "saveChange", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
								function(tota, isError) {
									if (!isError) {
										$scope.showMsg("ehl_01_common_002");
									}else
									{
										$scope.showMsg("ehl_01_common_007");
									}
							});
					}else
					{
						$scope.showMsg("ehl_01_common_007");
					}
			});
	};
	/******一般分行與特殊分行轉換******/
    $scope.changeToCommon = function () {
    	$scope.inputVO.scoreValue=0;
    	$scope.inputVO.isSpecial = '0';
    	$(".common").addClass('table-span2');
    	$(".special").removeClass('table-span2');
    }
    $scope.changeToSpecial = function () {
    	$scope.inputVO.scoreValue=0;
    	$scope.inputVO.isSpecial = '1';
    	$(".special").addClass('table-span2');
    	$(".common").removeClass('table-span2');
    } 
    // config
    
	/******時間控制******/
	// date picker
	$scope.ivgStartDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	$scope.ivgEndDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	// config
	$scope.model = {};
	
	$scope.open = function($event, elementOpened) {		
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.ivgStartDateOptions.maxDate = $scope.inputVO.EDate || $scope.maxDate;
		$scope.ivgEndDateOptions.minDate = $scope.inputVO.SDate || $scope.minDate;
	};
	
	//打開對應的子項目
    $scope.setKpi = function(personType,statsList,index) {
		var date_year = $scope.inputVO.date_year;
    	var personType = $scope.inputVO.personType;
    	var isSpecial = $scope.inputVO.isSpecial;    	
    	$scope.setTotalMon();
    	var showMonthList = $scope.showMonthList;
    	var eyyyy = $scope.eyyyy;
    	var emm = $scope.emm;
    	var totalMon = $scope.totalMon;
    	var endDate = $filter('date')($scope.inputVO.EDate,'yyyyMMdd');
    	var flag = $scope.flag;
    	
    	//公共模块的打开方式
    	if(statsList[index].URL==null){
    		var dialog = ngDialog.open({
        		template: 'assets/txn/PMS711/PMS711_fcSub.html',
    			className: 'PMS711_fcSub',
        		showClose : true,
        		controller: ['$scope', function($scope) {
        			$scope.endDate = endDate,
    				$scope.date_year = date_year,
    				$scope.personType = personType,
    				$scope.isSpecial = isSpecial,
    				$scope.showMonthList = showMonthList,
    				$scope.subName = statsList[index].SUB_PROJECT_SEQ_NAME,
    				$scope.subProjectSeqId = statsList[index].SUB_PROJECT_SEQ_ID;
                }]
        	});
    		//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			 $scope.queryIndex(personType,index);
    				}
        	});
    	}else{
    		var dialog = ngDialog.open({
        		template: statsList[index].URL,
    			className: statsList[index].CLASS_NAME,
        		showClose : true,
        		controller: ['$scope', function($scope) {
    				$scope.date_year = date_year,
    				$scope.personType = personType,
    				$scope.showMonthList = showMonthList,
    				$scope.isSpecial = isSpecial,
    				$scope.subId = statsList[index].SUB_PROJECT_SEQ_ID,
        			$scope.endDate = endDate,
    				$scope.subProjectSeqId = statsList[index].SUB_PROJECT_SEQ_ID
    				$scope.subName = statsList[index].SUB_PROJECT_SEQ_NAME;
                }]
        	});
    		//關閉子界面時，刷新主界面
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel'){
        			$scope.queryIndex(personType,index);
    				}
        	});
    	}
    }
    
    //獲取最新的子項目信息
    $scope.queryIndex = function(personType,index){
    	$scope.sendRecv("PMS711", "queryExamStats", "com.systex.jbranch.app.server.fps.pms711.PMS711InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.statsList.length == 0) {
							$scope.ifShowES = false;
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						$scope.statsList[index].EXAM_STATS = tota[0].body.statsList[index].EXAM_STATS;
						return;
					}
		});
    }
    
    //非系統計算人工上傳項目子項目頁面
    $scope.queryAddKpi = function(addToList,index) {
    	if(addToList[index].EXAM_STATS == '0')
			return;
    	var date_year = $scope.inputVO.date_year;
    	var personType = $scope.inputVO.personType;
    	var isSpecial = $scope.inputVO.isSpecial;
		var dialog = ngDialog.open({
    		template: 'assets/txn/PMS711/PMS711_addSub.html',
			className: 'PMS711_addSub',
    		showClose : true,
    		controller: ['$scope', function($scope) {
				$scope.date_year = date_year,
				$scope.personType = personType,
				$scope.isSpecial = isSpecial,
				$scope.subName = addToList[index].SUB_PROJECT_SEQ_NAME,
				$scope.subId = addToList[index].SUB_PROJECT_SEQ_ID;
            }]
    	});
		//關閉子界面時，刷新主界面
    	dialog.closePromise.then(function(data) {
    		if(data.value == 'cancel'){
    			 $scope.queryExamStats();
				}
    	});
    	
    }
    
  //非系統計算刪除子項目
    $scope.deleteAddKpi = function(addToList,subProjectSeqId,index) {
    	$scope.inputVO.subProjectSeqId = subProjectSeqId;
    	$confirm({text: '是否刪除此筆資料!!對應的上傳數據也會一併刪除'}, {size: 'sm'}).then(function(){
			$scope.sendRecv("PMS711", "deleteAddKpi",
					"com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",
					$scope.inputVO, function(tota, isError) {
						if (!isError) {
							$scope.showMsg("ehl_01_common_003");
						}else{
							$scope.showErrorMsg("失敗");
						}
					});
			$scope.addToList.splice(index,1);
			})    	
    }
});