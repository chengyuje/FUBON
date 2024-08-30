/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS358Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS358Controller";
		$scope.mappingSet['ReportType'] = [];
		$scope.BAL_NAME = "台定餘額";
		$scope.BAL_NAME_TYPE = "月";  //預設為月
        //選取週報下拉選單 --> 重新設定可視範圍
        $scope.dateChange_week = function(){
//        	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	$scope.RegionController_getORG($scope.inputVO);
        };
        
        //選取月報下拉選單 --> 重新設定可視範圍
        $scope.dateChange_month = function(){
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;    //目前2月1日無資料  須設定為   1970220   否則預設為   +"01"
        	//可視範圍  觸發 
        	if($scope.inputVO.sCreDate!=''){
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };    
        /***ORG COMBOBOX END***/
        
        //報表類型
        $scope.mappingSet['ReportType'].push({LABEL: "週報", DATA: "week"},{LABEL: "月報", DATA: "month"});
 
        //週月報轉換
        $scope.ChangeReport = function(){
        	$scope.inputVO.sCreDate = '';
        	$scope.inputVO.reportType = '';
        }
        
		//月報選單
        var NowDate = new Date();
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
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		
		$scope.curDate = new Date();
		
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		$scope.init1=function(){
			  var deferred=$q.defer();
			  $scope.paramList2=[];
			  $scope.sendRecv("PMS358", "date_query", "com.systex.jbranch.app.server.fps.pms358.PMS358InputVO",{},
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.resultList.length == 0) {
									$scope.showMsg("ehl_01_common_009");
		                			return;
		                		}
								$scope.paramList2 = tota[0].body.resultList;
								deferred.resolve();
								return;
							}						
				});
			 return deferred.promise;  
		  }	
		var getMonthWeek = function (a, b, c) { 
		    var date = new Date(a, parseInt(b) - 1, c), w = date.getDay(), d = date.getDate(); 
		    return Math.ceil( (d + 6 - w) / 7 ); 
		};
		   
		$scope.init = function(){
			$scope.ReportType =  '';
			$scope.inputVO = {										
					sCreDate: '',
					rc_id: '',
					op_id: '' ,
					br_id: '',
					ao_code: '',
					roleType: rp,
					reportType: ''
        	};					
			$scope.sumFlag = false;
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			$scope.inputVO2 = $scope.inputVO;
			$scope.outputVO2= $scope.outputVO;
			
			
			$scope.mappingSet['timeW']=[];
			  angular.forEach($scope.paramList2,function(row, index, objs){
				  var sp=[]; 
				  sp[0]=row.DATA_DATE.substring(0,4);
				  sp[1]=row.DATA_DATE.substring(4,6);
				  sp[2]=row.DATA_DATE.substring(6,8);
				 var e=getMonthWeek(sp[0],sp[1],sp[2]);
				 var time=new Date();
				 time.setFullYear(sp[0],sp[1]-1,sp[2]);
				 var WeekFirstDay=new Date(time-(time.getDay()-1)*86400000);
				 var WeekLastDay=new Date((WeekFirstDay/1000+6*86400)*1000);
				 var days=WeekFirstDay.getDate();
				 days=(days<sp[2])?sp[2]:days;
				
				 $scope.mappingSet['timeW'].push(
						   { 
							   LABEL:sp[0]+'/'+sp[1]+'/'+sp[2], 
							   DATA:row.DATA_DATE
						   });   	
			   });
			  $scope.paramList=[];
				$scope.paramList1=[];
				$scope.outputVO={}
				$scope.CREATE_DATE='';
			  console.log($scope.mappingSet['timeW']);
		};
		$scope.init1().then(function(){$scope.init();});
		
		$scope.inquireInit = function(){
			$scope.paramList = [];	
		}
		$scope.inquireInit();	
		
		$scope.mappingSet['rType'] = [];
		$scope.mappingSet['rType'].push({LABEL:'理專', DATA:'AO'},{LABEL:'分行主管', DATA:'BR'},{LABEL:'業務處主管', DATA:'RC'});
		
		
		// date picker
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
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
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
		};
		// date picker end
		
		        
		$scope.query = function(){	
			$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
			//資料日期沒選 = undefined
			if($scope.inputVO.sCreDate == undefined || $scope.inputVO.sCreDate == '') {
				if($scope.ReportType=='month')
					$scope.showErrorMsg('欄位檢核錯誤:月份必要輸入欄位');
				if($scope.ReportType=='week')
					$scope.showErrorMsg('欄位檢核錯誤:日期必要輸入欄位');
				
				return;
        	}
			if($scope.ReportType == '' || $scope.ReportType == undefined) {
	    		$scope.showErrorMsg('欄位檢核錯誤:報表類型必要輸入欄位');
        		return;
        	}
			$scope.inputVO.reportType = $scope.ReportType;
			$scope.inputVO2.reportType = $scope.ReportType;
			$scope.sumFlag = false;
			$scope.sumWEEK_BAL = 0;
			$scope.sumLMON_BAL = 0;
			$scope.sumCD_DIFF = 0;
			
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

			
			$scope.sendRecv("PMS358", "queryData", "com.systex.jbranch.app.server.fps.pms358.PMS358InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.paramList1=[];
								$scope.outputVO={}
								$scope.CREATE_DATE='';
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.sumFlag = true;
							$scope.paramList = tota[0].body.resultList;
							for(var i = 0; i < $scope.paramList.length; i++){
								$scope.sumWEEK_BAL += $scope.paramList[i].WEEK_BAL;
								$scope.sumLMON_BAL += $scope.paramList[i].LMON_BAL;
								$scope.sumCD_DIFF += $scope.paramList[i].CD_DIFF;
							}	
							
							if(tota[0].body.resultList.length == 0) {

							}else{
								//日期組字串 週報:YY/MM/DD
							
								if($scope.ReportType == 'week'){
										$scope.CREATE_DATE = tota[0].body.resultList[0].CREATDATE;				
										$scope.CREATE_DATE_YEAR = $scope.CREATE_DATE.substr(0,4);
										$scope.CREATE_DATE_MONTH = $scope.CREATE_DATE.substr(5,2);
										$scope.CREATE_DATE_DAY = $scope	.CREATE_DATE.substr(8,2);
										$scope.CREATE_DATE = $scope.CREATE_DATE_YEAR + "/" + $scope.CREATE_DATE_MONTH + "/" + $scope.CREATE_DATE_DAY;
										$scope.TypeName = "週報";
										$scope.BAL_NAME = "本週台定餘額";
										$scope.BAL_NAME_TYPE = "週";  //2017/05/20新增加"周"別
										
										angular.forEach($scope.paramList, function(row, index, objs){
											row.DATA_DATE = row.DATA_DATE.substring(0, 4) + "/" + row.DATA_DATE.substring(4,6) + "/" + row.DATA_DATE.substring(6,8); //日期加斜線
										});	
										
								}
								//日期組字串 月報:YY/MM
								else if($scope.ReportType == 'month'){		
									$scope.CREATE_DATE =tota[0].body.resultList[0].CREATDATE;	
									$scope.CREATE_DATE_YEAR = $scope.CREATE_DATE.substr(0,4);
									$scope.CREATE_DATE_MONTH = $scope.CREATE_DATE.substr(5,2);
									$scope.CREATE_DATE_DAY = $scope	.CREATE_DATE.substr(8,2);
									$scope.CREATE_DATE = $scope.CREATE_DATE_YEAR + "/" + $scope.CREATE_DATE_MONTH + "/" + $scope.CREATE_DATE_DAY;
									$scope.CREATE_DATE_YEAR = $scope.inputVO.sCreDate.substr(0,4);
									$scope.CREATE_DATE_MONTH = $scope.inputVO.sCreDate.substr(4,2);
									$scope.CREATE_DATES = $scope.CREATE_DATE_YEAR + "/" + $scope.CREATE_DATE_MONTH;
									$scope.TypeName = "月報";
									$scope.BAL_NAME = "本月台定餘額";
									$scope.BAL_NAME_TYPE = "月";  //2017/05/20新增加"月"別
									
									angular.forEach($scope.paramList, function(row, index, objs){
										row.DATA_DATE = $scope.CREATE_DATES;
									});			
								//該月沒建表才有可能發生的例外狀況
								}else{			
									$scope.CREATE_DATE ='';
								}										
							}
//							
//							//組產出日字串
//							$scope.sendRecv("PMS358", "queryCDate", "com.systex.jbranch.app.server.fps.pms358.PMS358InputVO", $scope.inputVO,
//									function(tota, isError) {
//						
//							});	

							
							if($scope.inputVO.roleType == 'BR'){		
								angular.forEach($scope.paramList, function(row, index, objs){
									//空AO_CODE : MASS戶
									if(row.AO_CODE == null || row.AO_CODE == '' || row.AO_CODE == '000'){
										row.AO_CODE = '000';
										row.EMP_NAME = 'MASS戶'
									//AO_CODE+EMP_NAME
									}
								});		
							}
							
							
//							//身分證遮蔽:只有理專會使用到
//							if($scope.inputVO.roleType == 'AO'){
//								angular.forEach($scope.paramList, function(row, index, objs){
//									row.CUST_ID = row.CUST_ID.substring(0, 4)+"***"+row.CUST_ID.substring(7, 10); //隱藏身分證  三碼
//								});	
//							}
							
							$scope.outputVO = tota[0].body;	
							$scope.inputVO.roleType = tota[0].body.roleType;
							return;
						}						
			});
		};
		
		$scope.showDetail = function(row, type, showType){ 
        	var dialog = ngDialog.open({
				template: 'assets/txn/PMS358/PMS358_DETAIL.html',
				className: 'PMS358_DETAIL',					
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.type = type;
                	$scope.rowtype = row.CUST_TYPE;
                	$scope.showType = showType;
                	
                }]
            });
        };
		
});
