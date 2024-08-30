/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SQM130Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "SQM130Controller";
    	
    	$scope.sendRecv("PMS000", "getLastYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
		    function(totas, isError) {
	             	if (totas.length > 0) {
	               		$scope.ymList = totas[0].body.ymList;
	               	};
		    }
		);	
    	
		$scope.REPORT_TYPE = 
			[{'LABEL':'貢獻度', 'DATA': '1'},{'LABEL':'客群', 'DATA': '2'},{'LABEL':'員編', 'DATA': '3'},{'LABEL':'服務別', 'DATA': '4'},{'LABEL':'問卷別', 'DATA': '5'}];				
		
		// XML
		getParameter.XML([ "CRM.VIP_DEGREE_HIS","SQM.QTN_TYPE" ], function(totas) {
			if (totas) {
				$scope.VIP_DEGREE_XML = totas.data[totas.key.indexOf('CRM.VIP_DEGREE_HIS')];
				$scope.QTN_LIST = totas.data[totas.key.indexOf('SQM.QTN_TYPE')];
			}
		});
		
		$scope.init = function(){
			$scope.inputVO = {
					trade_date_ys 	:	"",	//起始年
					trade_date_ms 	:	"",	//起始月
					trade_date_ye 	:	 "",	//迄年
					trade_date_me 	:	"",	//迄月
					region_center_id  :	'',	//區域中心
					branch_area_id	:	'',	//營運區
					branch_nbr	:	'',			//分行
					report_type	:	"",	//報表類型
					emp_id	:	""	//員編
        	};
			$scope.paramList =[];
			$scope.totalData = [];
			$scope.outputVO={totalList:[]};
//			$scope.disableRegionCombo = false;
//			$scope.disableAreaCombo = false;
//			$scope.disableBranchCombo = false;
			$scope.disableEmpId = true;
			$scope.regionJson = {};
			$scope.regionSize = {};
			$scope.areaSize = {};
			$scope.lastBranch = [];
			$scope.lastBranchArea = [];
			$scope.regionArray = [];
			var thisYear = ((new Date()).getYear()) + 1900;
			$scope.YEAR_LIST = [];
			for (var i =0 ; i<=5 ; i++) {
				if(thisYear >= 2018){
					$scope.YEAR_LIST.push({'LABEL':thisYear , 'DATA':thisYear});
				}
				thisYear --;
			}
			
			$scope.dateChange();
		}
		
		 //選取月份 --> 重新設定可視範圍
		$scope.dateChange = function(){
			$scope.MONTH_LIST = [];
		    $scope.MONTH_LIST = 
				[{'LABEL':'01', 'DATA': '01'},{'LABEL':'02', 'DATA': '02'},{'LABEL':'03', 'DATA': '03'},{'LABEL':'04', 'DATA': '04'},
				 {'LABEL':'05', 'DATA': '05'},{'LABEL':'06', 'DATA': '06'},{'LABEL':'07', 'DATA': '07'},{'LABEL':'08', 'DATA': '08'},
				 {'LABEL':'09', 'DATA': '09'},{'LABEL':'10', 'DATA': '10'},{'LABEL':'11', 'DATA': '11'},{'LABEL':'12', 'DATA': '12'}];				
		  
		    //201809上線，之前無資料，無法選擇
	    	if($scope.inputVO.trade_date_ys=='2018') { 
	    	  $scope.MONTH_LIST = 
	    		[{'LABEL':'09', 'DATA': '09'},{'LABEL':'10', 'DATA': '10'},{'LABEL':'11', 'DATA': '11'},{'LABEL':'12', 'DATA': '12'}];				
	    	}	
		    
//        	$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
			$scope.inputVO.reportDate = $filter('date')(new Date(),'yyyyMMdd');
        	$scope.RegionController_getORG($scope.inputVO);
		};
        
		$scope.init();
		$scope.changeReportType = function(){
			if ($scope.inputVO.report_type == '3') {
				$scope.disableEmpId = false;
			} else 
				$scope.disableEmpId = true;
		}

		$scope.query = function(){
			$scope.regionJson = {};
			$scope.regionSize = {};
			$scope.areaSize = {};
			$scope.regionArray =[];
			if($scope.parameterTypeEditForm.$invalid){
				var yearMonS = $scope.inputVO.trade_date_ys + $scope.inputVO.trade_date_ms;
				var yearMonE = $scope.inputVO.trade_date_ye + $scope.inputVO.trade_date_me;
				//起始日必輸
				if($scope.inputVO.trade_date_ys== "" || $scope.inputVO.trade_date_ms == ""){
		    		$scope.showMsg('資料年月未選取');
	        		return;
	        	}
				//起迄日檢核
				if (yearMonE	!=	""	&&	yearMonS > yearMonE) {
					$scope.showMsg("交易年月起值大於迄值");
	        		return;
				}
				//報表類型檢核
				if ($scope.inputVO.report_type	==	"") {
					$scope.showMsg("報表類型未選取");
	        		return;
				}
			}
			$scope.report_type_flag = $scope.inputVO.report_type;
			
			$scope.sendRecv("SQM130", "inquire", "com.systex.jbranch.app.server.fps.sqm130.SQM130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.totalData = [];
								$scope.regionSumList     = {}; //業務處合計
								$scope.branchAreaSumList = {}; //營運區合計
								$scope.branchSumList     = {}; //分行合計
								$scope.totalSumList = {}
								$scope.outputVO={totalList:[]};
								$scope.showMsg("ehl_01_common_009");
	                			return;
							};
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							$scope.regionSumList = {};//業務處合計
							$scope.branchAreaSumList = {};//營運區合計
							$scope.branchSumList = {};//分行合計
							$scope.totalSumList = {
									"VS_CNT":0	,	"S_CNT":0	,"OS_CNT":0	,	"NS_CNT":0	,	"VD_CNT":0	,	"NC_CNT":0	, "TOT_CNT":0	,	
									"VS_PC":0	,	"S_PC":0	,	"OS_PC":0	,	"NS_PC":0	,	"VD_PC":0	,	"NC_PC":0	,	"TOT_PC":0
							};

							//region total
							angular.forEach($scope.paramList , function (row , index , objs) {
								var key = row.REGION_CENTER_NAME ; 
								if (key in $scope.regionSumList) {
									$scope.regionSumList[key].VS_CNT	= row.REGION_VS_CNT;
									$scope.regionSumList[key].S_CNT 	= row.REGION_S_CNT;
									$scope.regionSumList[key].OS_CNT 	= row.REGION_OS_CNT;
									$scope.regionSumList[key].NS_CNT	= row.REGION_NS_CNT;
									$scope.regionSumList[key].VD_CNT	= row.REGION_VD_CNT;
									$scope.regionSumList[key].NC_CNT	= row.REGION_NC_CNT;
									$scope.regionSumList[key].TOT_CNT	= row.REGION_TOT_CNT;

									$scope.regionSumList[key].VS_PC		= row.REGION_TOT_CNT == 0 ? 0 : row.REGION_VS_CNT/row.REGION_TOT_CNT*100;
									$scope.regionSumList[key].S_PC		= row.REGION_TOT_CNT == 0 ? 0 : row.REGION_S_CNT/row.REGION_TOT_CNT*100;
									$scope.regionSumList[key].OS_PC		= row.REGION_TOT_CNT == 0 ? 0 : row.REGION_OS_CNT/row.REGION_TOT_CNT*100;
									$scope.regionSumList[key].NS_PC		= row.REGION_TOT_CNT == 0 ? 0 : row.REGION_NS_CNT/row.REGION_TOT_CNT*100;
									$scope.regionSumList[key].VD_PC		= row.REGION_TOT_CNT == 0 ? 0 : row.REGION_VD_CNT/row.REGION_TOT_CNT*100;
									$scope.regionSumList[key].NC_PC		= row.REGION_TOT_CNT == 0 ? 0 : row.REGION_NC_CNT/row.REGION_TOT_CNT*100;
									$scope.regionSumList[key].TOT_PC	= row.REGION_TOT_CNT == 0 ? 0 : row.REGION_TOT_CNT/row.REGION_TOT_CNT*100;
								} else {
									$scope.regionSumList[key] = angular.copy(row);
								}
							});

							//branch area total
							angular.forEach($scope.paramList , function (row , index , objs) {
								var key = row.BRANCH_AREA_NAME ; 
								if (key in $scope.branchAreaSumList) {
									// # 5577
									// 計算區合計 -- 比例
									$scope.branchAreaSumList[key].VS_CNT	= row.BRANCH_AREA_VS_CNT;
									$scope.branchAreaSumList[key].S_CNT 	= row.BRANCH_AREA_S_CNT;
									$scope.branchAreaSumList[key].OS_CNT 	= row.BRANCH_AREA_OS_CNT;
									$scope.branchAreaSumList[key].NS_CNT	= row.BRANCH_AREA_NS_CNT;
									$scope.branchAreaSumList[key].VD_CNT	= row.BRANCH_AREA_VD_CNT;
									$scope.branchAreaSumList[key].NC_CNT	= row.BRANCH_AREA_NC_CNT;
									$scope.branchAreaSumList[key].TOT_CNT	= row.BRANCH_AREA_TOT_CNT;

									$scope.branchAreaSumList[key].VS_PC	    = row.BRANCH_AREA_TOT_CNT == 0 ? 0 : row.BRANCH_AREA_VS_CNT/row.BRANCH_AREA_TOT_CNT*100;
									$scope.branchAreaSumList[key].S_PC 	    = row.BRANCH_AREA_TOT_CNT == 0 ? 0 : row.BRANCH_AREA_S_CNT/row.BRANCH_AREA_TOT_CNT*100;
									$scope.branchAreaSumList[key].OS_PC 	= row.BRANCH_AREA_TOT_CNT == 0 ? 0 : row.BRANCH_AREA_OS_CNT/row.BRANCH_AREA_TOT_CNT*100;
									$scope.branchAreaSumList[key].NS_PC	    = row.BRANCH_AREA_TOT_CNT == 0 ? 0 : row.BRANCH_AREA_NS_CNT/row.BRANCH_AREA_TOT_CNT*100;
									$scope.branchAreaSumList[key].VD_PC   	= row.BRANCH_AREA_TOT_CNT == 0 ? 0 : row.BRANCH_AREA_VD_CNT/row.BRANCH_AREA_TOT_CNT*100;
									$scope.branchAreaSumList[key].NC_PC   	= row.BRANCH_AREA_TOT_CNT == 0 ? 0 : row.BRANCH_AREA_NC_CNT/row.BRANCH_AREA_TOT_CNT*100;
									$scope.branchAreaSumList[key].TOT_PC	= row.BRANCH_AREA_TOT_CNT == 0 ? 0 : row.BRANCH_AREA_TOT_CNT/row.BRANCH_AREA_TOT_CNT*100;
								} else {
									$scope.branchAreaSumList[key] = angular.copy(row);
								}
							});

							//branch total
							angular.forEach($scope.paramList , function (row , index , objs) {
								var key = row.BRANCH_NBR ;
								if (key in $scope.branchSumList) {
									// # 5577
									// 計算分行合計 -- 比例
									$scope.branchSumList[key].VS_CNT	+= row.VS_CNT;
									$scope.branchSumList[key].S_CNT 	+= row.S_CNT;
									$scope.branchSumList[key].OS_CNT 	+= row.OS_CNT;
									$scope.branchSumList[key].NS_CNT	+= row.NS_CNT;
									$scope.branchSumList[key].VD_CNT	+= row.VD_CNT;
									$scope.branchSumList[key].NC_CNT	+= row.NC_CNT;
									$scope.branchSumList[key].TOT_CNT	+= row.TOT_CNT;

									$scope.branchSumList[key].VS_PC		= row.BRANCH_CNT_TOT == 0 ? 0 : $scope.branchSumList[key].VS_CNT/row.BRANCH_CNT_TOT*100;
									$scope.branchSumList[key].S_PC 		= row.BRANCH_CNT_TOT == 0 ? 0 : $scope.branchSumList[key].S_CNT /row.BRANCH_CNT_TOT*100;
									$scope.branchSumList[key].OS_PC 	= row.BRANCH_CNT_TOT == 0 ? 0 : $scope.branchSumList[key].OS_CNT /row.BRANCH_CNT_TOT*100;
									$scope.branchSumList[key].NS_PC		= row.BRANCH_CNT_TOT == 0 ? 0 : $scope.branchSumList[key].NS_CNT/row.BRANCH_CNT_TOT*100;
									$scope.branchSumList[key].VD_PC		= row.BRANCH_CNT_TOT == 0 ? 0 : $scope.branchSumList[key].VD_CNT/row.BRANCH_CNT_TOT*100;
									$scope.branchSumList[key].NC_PC		= row.BRANCH_CNT_TOT == 0 ? 0 : $scope.branchSumList[key].NC_CNT/row.BRANCH_CNT_TOT*100;
									$scope.branchSumList[key].TOT_PC	= row.BRANCH_CNT_TOT == 0 ? 0 : $scope.branchSumList[key].TOT_CNT/row.BRANCH_CNT_TOT*100;
								} else {
									$scope.branchSumList[key] = angular.copy(row);
								}
							});

							
							angular.forEach($scope.paramList , function (row , index , objs) {
								var region_center = row.REGION_CENTER_NAME ; 
								var brach_area = row.BRANCH_AREA_NAME ;
								var brach_nbr = row.BRANCH_NBR ;
								var s_type = row.STATISTICS_TYPE;
								var i = 0;

								if (region_center in $scope.regionJson == false) {
									$scope.regionJson[region_center] = { 
										[brach_area]: {brach_nbr}
									};

									$scope.regionJson[region_center][brach_area] = { 
										[brach_nbr] : {[i] : [s_type]}
									};

									$scope.lastBranch = [brach_nbr];
									$scope.lastBranchArea = [brach_area];
									i++;
								} else {
									if (brach_area in $scope.regionJson[region_center] == false) {
										if(brach_area != $scope.lastBranchArea[0]){
											$scope.regionJson[region_center][brach_area] = { 
												[brach_nbr] : {[i] : [s_type]}
											};
										}

										$scope.lastBranch = [brach_nbr];
										i++;
									} else {
										if(brach_nbr != $scope.lastBranch[0]){
											$scope.regionJson[region_center][brach_area][brach_nbr] = { 
												 [i] : [s_type]
											};	
											
											$scope.lastBranch = [brach_nbr];
											i++;			
										}else{
											if (s_type in $scope.regionJson[region_center][brach_area][brach_nbr][i] == false) {
												$scope.regionJson[region_center][brach_area][brach_nbr][i].push(row.STATISTICS_TYPE);
											}

											i++;
										}
									}
								}
							});
							
							var j = 0;							
							var k = 0;
							var isSameRegion = true;
							angular.forEach($scope.regionJson, function (row, index, objs) {
								if(j == 0){
									$scope.regionSize[index] = 2;
								}

								if(k > 0){
									$scope.regionSize[index] = 2;
									j = 0;
								}
								
								angular.forEach($scope.regionJson[index] , function (row1 , index1 , objs1) {
									$scope.regionArray.push(index1);

									for(var x = 0; x < $scope.regionArray.length; x++){
										if(x == 0){
											isSameRegion = true;
										}else{
											if(index1 == $scope.regionArray[x-1]){
												isSameRegion = true;
											}else{
												isSameRegion = false;
											}
										}
									}

									$scope.regionSize[index] = $scope.regionSize[index] + 2;
									$scope.areaSize[index1] = 2;

									var i = 0;
									var count = 0;
									
									angular.forEach($scope.regionJson[index][index1] , function (row2 , index2 , objs2) {
										/*--------------------------------------------------------------------------*/
										// 計算 "貢獻度"
										$scope.regionSize[index] = $scope.regionSize[index] + row2[0].length;
										$scope.areaSize[index1] = $scope.areaSize[index1] + row2[0].length;
										i++;
										
										switch (i) {
											case 1:
												count = i - 1;
												break;
											case 2:
												count = i;
												break;
											case 3:
												count = i + 1;
												break;
											case 4:
												count = i + 2;
												break;
										}
										/*--------------------------------------------------------------------------*/
									});

									$scope.regionSize[index] = $scope.regionSize[index] + 2 + count;	
									$scope.areaSize[index1] = $scope.areaSize[index1] + 1 + count;

									if(j > 1){
										$scope.regionSize[index] = $scope.regionSize[index] - 1;
									}
									
									switch (j) {
										case 0:
											if (isSameRegion) {
												$scope.regionSize[index] = $scope.regionSize[index] + 1;
											}
											break;
										case 1:
											if (!isSameRegion) {
												$scope.regionSize[index] = $scope.regionSize[index] - 1;
											}
											break;
										case 3:
										case 4:
										case 5:
										case 6:
										case 7:
										case 8:
										case 9:
										case 10:
										case 11:
										case 12:
										case 13:
										case 14:
										case 15:
										case 16:
										case 17:
											$scope.regionSize[index] = $scope.regionSize[index]  + 1;
											break;
									}

									j++;
								});

								if(j == 1){
									$scope.regionSize[index] -= 2;
								}

								switch (k) {
								case 0:
									if (j == 2 && !isSameRegion) {
										$scope.regionSize[index] -= 1;
									}
									break;
								case 1:
								case 2:
									$scope.regionSize[index] += 1;
									break;
								}
								
								k++;
							});
								
							//All branch total
							angular.forEach($scope.paramList , function (row , index , objs) {
								$scope.totalSumList.VS_CNT  = row.TOTAL_VS_CNT;
								$scope.totalSumList.S_CNT   = row.TOTAL_S_CNT;
								$scope.totalSumList.OS_CNT  = row.TOTAL_OS_CNT;
								$scope.totalSumList.NS_CNT  = row.TOTAL_NS_CNT;
								$scope.totalSumList.VD_CNT  = row.TOTAL_VD_CNT;
								$scope.totalSumList.NC_CNT  = row.TOTAL_NC_CNT;
								$scope.totalSumList.TOT_CNT = row.TOTAL_TOT_CNT;
							})
							
							$scope.totalSumList.VS_PC  = $scope.totalSumList.TOT_CNT == 0 ? 0 : $scope.totalSumList.VS_CNT/$scope.totalSumList.TOT_CNT*100;
							$scope.totalSumList.S_PC   = $scope.totalSumList.TOT_CNT == 0 ? 0 : $scope.totalSumList.S_CNT/$scope.totalSumList.TOT_CNT*100;
							$scope.totalSumList.OS_PC  = $scope.totalSumList.TOT_CNT == 0 ? 0 : $scope.totalSumList.OS_CNT/$scope.totalSumList.TOT_CNT*100;
							$scope.totalSumList.NS_PC  = $scope.totalSumList.TOT_CNT == 0 ? 0 : $scope.totalSumList.NS_CNT/$scope.totalSumList.TOT_CNT*100;
							$scope.totalSumList.VD_PC  = $scope.totalSumList.TOT_CNT == 0 ? 0 : $scope.totalSumList.VD_CNT/$scope.totalSumList.TOT_CNT*100;
							$scope.totalSumList.NC_PC  = $scope.totalSumList.TOT_CNT == 0 ? 0 : $scope.totalSumList.NC_CNT/$scope.totalSumList.TOT_CNT*100;
							$scope.totalSumList.TOT_PC = $scope.totalSumList.TOT_CNT == 0 ? 0 : $scope.totalSumList.TOT_CNT/$scope.totalSumList.TOT_CNT*100;
							
							$scope.regionSumList;
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;
							
							return;
						}						
			});
		};

        $scope.numGroups = function(input){
        	var i=0;    
            for(var key in input) i++;      
 			return i;
        }

		$scope.exportRPT = function(){
			$scope.sendRecv("SQM130", "export", "com.systex.jbranch.app.server.fps.sqm130.SQM130InputVO", $scope.inputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                	}
	                }
			});
		};
});
