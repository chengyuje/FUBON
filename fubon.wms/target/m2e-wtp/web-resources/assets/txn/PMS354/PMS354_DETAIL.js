/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS354_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS354_DETAILController";
		
		//組織連動
		$controller('PMSRegionController', {$scope: $scope});
		var prjSeq = $scope.connector('get', 'PMS354DETAIL');
		var starDt = $scope.connector('get', 'PMS354DETAIL_START_DT');
		var endDt  = $scope.connector('get', 'PMS354DETAIL_END_DT');
		starDt = starDt.replace("." , "/").replace("." , "/");
		endDt  = endDt .replace("." , "/").replace("." , "/");
		
		$scope.rptTitle = $scope.connector('get', 'PMS354DETAIL_title');
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		var priID = sysInfoService.getPriID();
		if (priID == '002' || priID == '003'){ //理專
		    $scope.isShowAO = true;
		}else{
			 $scope.isShowAO = false;
		}
		
		
		$scope.curDate = '';
		$scope.Appendzero =  function (obj) {
			if (obj < 10) 
				return "0" + "" + obj;
			else 
				return obj; 
		};
		
		var rp = "RC";
		if(sysInfoService.getRoleName().substring(0, 2) == 'FC')
			rp = "AO";
		if(sysInfoService.getRoleID() == 'A161')
			rp = "BR";
		
		
		$scope.init = function () {
			var d = new Date();
			$scope.inputVO = {	
				reportDate 	: d.getFullYear() + "" + $scope.Appendzero(d.getMonth()),
				prj_seq 	: prjSeq,
				rc_id   	: '',
				op_id   	: '',
				br_id   	: '',
				roleType    : rp,
				emp_id  	: '',
				start_dt    : starDt ,
				end_dt      : endDt
        	};
			
			$scope.RegionController_getORG($scope.inputVO);
			$scope.totalprd=[];
			$scope.totalbal=[];

			$scope.sumPrd=0;
			//總計用
			$scope.sums="";
			//總計用
			$scope.sums2="";
		};
		$scope.init();
		
		//動態抓取計算  [營運區][區域中心] 合計  前端撈取
		$scope.DynamicForm= function (DeptId) {
			var Object=[];
			
			angular.forEach($scope.eachSet, function(row,index) {
				if(row.DEPT_ID==DeptId)
				{
					angular.forEach($scope.colList, function(rows,indexs) {
						Object.push($filter('number')(row[rows],0));
					});
					Object.push($filter('number')(row["BAL"],0));
					if($scope.inputVO.roleType!='AO')
					{
						angular.forEach($scope.colList2, function(rows,indexs) {
							Object.push($filter('number')(row[indexs+1],0));
							Object.push($filter('number')((row["BAL"]/row[indexs+1])*100,0)+"%");
						});
					}
					
				}
			});

			//console.log(Object);
			return Object;
		}
		
		//計算  [營運區][區域中心] 合計
		$scope.sumCol = function (collect,collect2) {
			
			$scope.inputVO.sumCollect=[];

			if(collect!=undefined)
				$scope.inputVO.sumCollect.push({"BRANCH_AREA_NAME":collect,"REGION_CENTRER_NAME":collect});
			if(collect2!=undefined)
				$scope.inputVO.sumCollect.push({"BRANCH_AREA_NAME":collect2,"REGION_CENTRER_NAME":collect2});

			$scope.sendRecv("PMS354", "queryTotal", "com.systex.jbranch.app.server.fps.pms354.PMS354InputVO", $scope.inputVO,
					function(tota, isError) {
				if(!isError){
					//用來合計
					$scope.eachSet=tota[0].body.eachSet;
					$scope.DynamicForm();
				}			
			});
		}
		
		
	
		
		
		
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
			$scope.outputVO={totalList:[]};
		}
		$scope.inquireInit();	
		
		//區域中心
        $scope.Region = function(){
        	$scope.inputVO.rc_id = $scope.inputVO.region_center_id;
        }
        
        //營運區
        $scope.Area = function(){
        	$scope.inputVO.op_id = $scope.inputVO.branch_area_id;
        } 
        
        //分行
        $scope.Branch = function(){
        	$scope.inputVO.br_id = $scope.inputVO.branch_nbr;
        }
		
		/**返回上一頁**/
		$scope.backToMain = function(){
			$rootScope.menuItemInfo.url = "assets/txn/PMS354/PMS354.html";
		}
		
		/**「，」分割資料**/ 
		$scope.getListByComma = function(value){
			if (value != "" && value != undefined) {
				return value.split(',').map(Number);
			}
		}
		
		/**儲存格合併、加總**/
		//計算合併列數
		$scope.numGroupsA = function(input){
			if(input == null)
				return;
			var sum = 0;
			var ans = $filter('groupBy')(input, 'BRANCH_AREA_NAME');
			sum += Object.keys(ans).length * 2
			angular.forEach(Object.values(ans), function(row) {
    			var child = $filter('groupBy')(row, 'BRANCH_NBR');
    			sum += Object.keys(child).length;
			});
            return sum;
        }
		$scope.numGroupsB = function(input){
			if(input == null)
				return;
			var ans = $filter('groupBy')(input, 'BRANCH_NBR');
            return Object.keys(ans).length;
        }
		
		//橫向加總
        $scope.getSum = function(group, key) {
        	
        	var sum = 0;
            for (var i = 0; i < group.length; i++){            	
            	sum += _.sum(group[i][key].split(',').map(Number));
            }  
            return sum;
        }
        
        //直向加總
        $scope.getSumya = function(group, key) {
        	var sum = [];
        	var ha = 0;        	
        	if (group[0][key] != undefined) {
	        	for(var i = 0; i < group[0][key].split(',').map(Number).length; i++){
	        		for (var j = 0; j < group.length; j++){
	        			ha += group[j][key].split(',').map(Number)[i];
	        		}          		
	        		sum.push(ha);
	        		ha = 0;
	        	}
        	}
            return sum; 
        }
		
        /*** 查詢資料 ***/
		$scope.query = function(){										
			if($scope.REGION_LIST.length == 0){				
				$scope.inputVO.emp_id = sysInfoService.getUserID();
			}

			$scope.sendRecv("PMS354", "queryDetail", "com.systex.jbranch.app.server.fps.pms354.PMS354InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (isError) {
								$scope.showMsg("ehl_01_common_009");
								return;
		                	}
							if (tota.length > 0) {
								$scope.paramList = tota[0].body.totalList;
								$scope.outputVO = tota[0].body;
								$scope.curDate = $scope.paramList[0].DATA_DATE;

								$scope.ya = tota[0].body.currentPageIndex;
								$scope.ya1 = tota[0].body.totalPage;
								//拿總計
								//計算最後頁總計   2017/05/18							
								$scope.sendRecv("PMS354", "queryDetail2", "com.systex.jbranch.app.server.fps.pms354.PMS354InputVO", $scope.inputVO,
										function(tota, isError) {
											if (!isError) {
												
												//totalbal
												$scope.totalprd = tota[0].body.totalprd;
												$scope.totalbal = tota[0].body.totalbal;
												$scope.sumPrd=0;
												//跑函數  原始長度
												$scope.totallen = $scope.totalbal.length;
												//總和
												angular.forEach($scope.totalprd, function(row) {
									    			$scope.sumPrd += row.TW_BAL;
												});

//												if($scope.colList.length!=0 && $scope.colList.length!=undefined){ 	//銷量不為0
//													for(var x=0; x<$scope.colList.length;x++){
//														// 單號 : 6127 -- 排版問題修正
//														
//														if($scope.colList2.length == 1){
//															if($scope.colList2.length>=$scope.totalprd.length 
//																|| $scope.colList2.length<=$scope.colList.length){
////																
////																$scope.totalprd.push({BAL:0});
//															}
//														}
//														else{
//															if($scope.colList2.length>=$scope.totalprd.length){
//																
//																$scope.totalprd.push({BAL:0});
//															}													
//														}																						
//													}
//												}
												var i=0;
												//計算百分比      在原本List增加計算
												$scope.rePlaceFun = function(i){	
													angular.forEach($scope.totalbal, function(row, index, objs) {
														
														if(index==i){
															var perSen = 0;
															perSen = $scope.sumPrd / row.TARGET * 100;
															$scope.totalbal.splice(index, 1, row, {PRJ_SEQ:row.PRJ_SEQ, COL_NO:9999, TARGET:perSen});	
														}
													});

													i += 2;
													if($scope.totallen*2 >= i)
														$scope.rePlaceFun(i);

												}
												$scope.rePlaceFun(i);	
											}
									});
								
								
								
								//*****銷量目標   如果比數不足補0******//								
//								angular.forEach($scope.paramList, function(row, index, objs) {
//									//==銷量
//									if ($scope.colList.length != 0 && $scope.colList.length != undefined){ 	//銷量不為0
//										for (var x = 0; x < $scope.colList.length; x++) {
//											if (row.ORG_PRD_LIST == ' ') {
//												row.ORG_PRD_LIST = "無值";
//											} else {
//												if ($scope.colList.length != row.ORG_PRD_LIST.split(",").length) {
//													row.ORG_PRD_LIST += ",無值";
//													row.PRD_LIST += ",無值";
//												}
//											}
//										}										
//									} else if ($scope.colList.length == 0) {  //0就清空
//										row.TARGET_LIST = "";
//									}
//									
//									//==目標
//									if ($scope.colList2.length != 0 && $scope.colList2.length != undefined) {   //目標不為0
//										for (var x = 0; x < $scope.colList2.length; x++) {
//											if (row.TARGET_LIST == ' '){
//												row.TARGET_LIST = "無值";
//											} else {
//												if ($scope.colList2.length != row.TARGET_LIST.split(",").length) {
//													row.TARGET_LIST += ",無值";
//													row.PRD_LIST += ",無值";
//												}
//											}
//										}
//									} else if ($scope.colList2.length == 0){  //0就清空
//										row.TARGET_LIST = "";
//									}
//								});
								
								$scope.outputVO = tota[0].body;							
								return;
							}
						}						
			});
		};
		
		
		
	
		
		/***匯出EXCEL檔***/
		$scope.exportRPT = function(){	
			$scope.outputVO.colList = $scope.colList;
			$scope.outputVO.colList2 = $scope.colList2;
			$scope.outputVO.aocode = $scope.inputVO.ao_code;
			$scope.outputVO.totalprd=$scope.totalprd ;
			$scope.outputVO.totalbal=$scope.totalbal ;
			$scope.outputVO.eachSet=$scope.eachSet;
			$scope.sendRecv("PMS354", "export", "com.systex.jbranch.app.server.fps.pms354.PMS354OutputVO", $scope.outputVO,
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
		
		
		/**取表格欄位**/		
		$scope.getRPTCol = function(){			
			$scope.colList = [];
			$scope.sendRecv("PMS354", "queryRPTCol", "com.systex.jbranch.app.server.fps.pms354.PMS354InputVO", {'prj_seq':prjSeq},
					function(tota, isError) {
						if (!isError) {
							if (isError) {
								$scope.showMsg("ehl_01_common_009");
		               			return;
		                	}
							if (tota.length > 0) {
								
								$scope.colList = tota[0].body.colList;
								$scope.colList2 = tota[0].body.colList2;
								
								
								angular.forEach($scope.colList, function(row,index) {
									if($scope.colList.length-1!=index)
										$scope.sums+=row+",";
									else
										$scope.sums+=row
								});
								angular.forEach($scope.colList2, function(row,index) {
									if($scope.colList2.length-1!=index)
										$scope.sums2+=index+1+",";
									else
										$scope.sums2+=index+1
								});

								//console.log($scope.colList2);
								$scope.sumCol($scope.sums, $scope.sums2);
							
								$scope.outputVO = tota[0].body;											
								return;
		                	};							
						}						
			});
		}
		$scope.getRPTCol();
		
		$scope.showDetail = function(row, type){
			row.prj_seq = $scope.inputVO.prj_seq;
			$scope.inputVO.branch_nbr = row.BRANCH_NBR;
        	var dialog = ngDialog.open({
				template: 'assets/txn/PMS354/PMS354_BRDETAIL.html',
				className: 'PMS354_BRDETAIL',					
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.type = type;
                }]
            });
        	$scope.inputVO.branch_nbr = '';
        };
		
    	document.getElementById("wrap").addEventListener("scroll",function(){
 		   var translate = "translate(0," + this.scrollTop + "px)";
 		   if(/MSIE 9/.test(navigator.userAgent))
 		      this.querySelector("thead").style.msTransform = translate;
 		   else
 		      this.querySelector("thead").style.transform = translate;
 		});
});
