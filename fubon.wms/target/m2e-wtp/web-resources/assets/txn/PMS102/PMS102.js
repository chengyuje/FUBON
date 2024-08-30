/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS102Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PPAPController', {$scope: $scope});
		$scope.controllerName = "PMS102Controller";
		$controller('PMSRegionController', {$scope: $scope});
		//filter
		$scope.dateChange = function(){
	        if($scope.inputVO.sCreDate!=''){ 	
	        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };
	    
	  //***合計function***//
    	$scope.getSum = function(group, key) {
              var sum = 0;
              for (var i = 0; i < group.length; i++){
               sum += group[i][key];
              }  
              return sum;
        }
	    	    
	    $scope.initLoad = function(){
			$scope.sendRecv("PMS000", "getLastYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
					   function(totas, isError) {
				             	if (totas.length > 0) {
									var NowDate = new Date();
									var yr = NowDate.getFullYear();
									var mm = NowDate.getMonth()+1; 
									var strMon ='';
									if(mm<10){
										strMon='0'+mm;
									}else{
										strMon=mm;
									}
										
				               		$scope.ymList = totas[0].body.ymList;
									$scope.ymList.unshift({
										LABEL: yr+'/'+strMon,
										DATA: yr +''+ strMon
									});
									$scope.ymList.pop();
									//#0000375: 報表留存時間 四個月
									$scope.ymList.splice(4);
				               		$scope.inputVO.sCreDate = '';
				               	};
					   }
			);
		}
		$scope.initLoad();
        $scope.init = function(){
			$scope.inputVO = {
					region_center_id: '',
					branch_area_id: '' ,
					branch_nbr: '',
					emp_id: '',
					CUST_ID:'',
					PROD_TYPE:'',
					TYPE:''	
        	};
			$scope.paramList=[];
			$scope.inputVO.sCreDate = '';
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
		};
        $scope.init();
        // 系統參數顯示篩選。
        $scope.testPriID = function(){
        	$scope.PriID = ['016','017','018','025','026','027','032','045','046','055','056'];
        	for (var i = 0 ; i <= $scope.PriID.length ; i++){
        		if(sysInfoService.getPriID() == $scope.PriID[i]){
        			return true;
        		}
        	}
        };
        $scope.testPriID();
        
        $scope.edi = function (row) {
        	if($scope.testPriID() == true){
        			var custID = $scope.inputVO.custID;
        			var dialog = ngDialog.open({
        				template: 'assets/txn/PMS102/PMS102_ROUTE.html',
        				className: 'PMS102',
        				showClose: false,
        				scope : $scope,
        				controller: ['$scope', function($scope) {
					
        					$scope.isPop = true;
        					$scope.routeURL = 'assets/txn/PMS108/PMS108.html';
        					$scope.row = row;
        					$scope.dialog = '1';
        				}]
        			}).closePromise.then(function (data) {
        				if(data.value != "cancel") {
        					$scope.getProdDTL();
        				}
        			});
        	}else{
        		$scope.showMsg("非主管身分不可更動系統參數");
        	}
		};
        
        $scope.init_his = function(){		
		    $scope.mappingSet['type']=[];
            $scope.mappingSet['type'].push({
				LABEL : 'Y',
				DATA :'Y'
    		},{
				LABEL : 'N',
				DATA :'N'
    		});    
		};
        $scope.init_his();
		
        // 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        }
        $scope.inquireInit();
        
        // 初始分頁資訊
        $scope.pins = function(){
//        	alert($scope.inputVO.PROD_TYPE);
			
        }
        $scope.pins();
        
        
        $scope.inquire = function(){   
        	if($scope.inputVO.sCreDate == '' || $scope.inputVO.sCreDate == undefined){
        		$scope.showMsg("欄位檢核錯誤：報表年月為必要欄位");
        		return;
        	}
        	$scope.paramList2=[];
			$scope.sendRecv("PMS102", "queryData", "com.systex.jbranch.app.server.fps.pms102.PMS102InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							
//							angular.forEach($scope.paramList,function(row, index, objs) {
//								 $scope.planAmt(row,index);
//							});
							
							$scope.outputVO = tota[0].body;
							$scope.ya = tota[0].body.currentPageIndex;
							$scope.ya1 = tota[0].body.totalPage;
							//拿總計
							//計算最後頁總計   2017/05/18
							
							if($scope.ya==$scope.ya1-1){
								var tmpColumn = $scope.inputVO.column;
								$scope.inputVO.column = null;
								$scope.sendRecv("PMS102", "inquire2", "com.systex.jbranch.app.server.fps.pms102.PMS102InputVO",$scope.inputVO ,
										function(tota, isError) {
											if (!isError) {
												$scope.paramList2 = tota[0].body.resultList2;												
											}
								});
								
								$scope.inputVO.column = tmpColumn;
							}

							return;
						}
			});
		};		

   	   
        /*****以下是修改鍵2開啟視窗******/
        $scope.edit2 = function (row) {
	    	    var dialog = ngDialog.open({
	                template: 'assets/txn/PMS102/PMS102_LEADS.html',
	                className: 'PMS102_LEADS',
	                controller: ['$scope', function($scope) {
	              	  $scope.row = row;
	              	  $scope.inputVO = row;
	                }]
                });
	            dialog.closePromise.then(function (data) {
	              // $scope.inquire();               
	            });
        };
        
		
        
        /*****以下是修改建一開始視窗畫面'*****/
	      $scope.edit = function (row) { 
	    	  $scope.sendRecv("PMS109", "competence", "com.systex.jbranch.app.server.fps.pms109.PMS109InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.empList.length == 0) {
									$scope.showMsg("非理專身分/理專一級主館身分舞法使用新增銷售計劃");
		                			return ;
		                		}
								$scope.empList = tota[0].body.empList;
								$scope.outputVO = tota[0].body;
							}
	    	  });
	    		
	          var dialog = ngDialog.open({
	              template: 'assets/txn/PMS109/PMS109.html',
	              className: 'PMS109',
	              controller: ['$scope', function($scope) {
	            	  $scope.cust_id = row.CUST_ID;
	            	  $scope.src_type = 'PMS102';
	              }]
	          });
	          dialog.closePromise.then(function (data) {
	            	  $scope.inquire();
	          });
      };
      
    //總金額
    $scope.TOT_PLAN_AMT=0;
    //回傳銷售計畫    金額
//    $scope.planAmt = function (row,ind) { 
//    	//已規劃
//    	$scope.mappingSet[ind]=0;
//    	//未規劃
//    	$scope.mappingSet[ind+10]=0;
//    	$scope.PLAN_AMT=0;
//    	$scope.UNPLAN_AMT=0;
//    	$scope.sendRecv("PMS102", "bePlan", "com.systex.jbranch.app.server.fps.pms102.PMS102InputVO", {    	
//    		CUST_ID:row.CUST_ID,
//    		reportDate:row.PLAN_YEARMON
//    	},function(tota, isError) {
//						if (!isError) {	
//							if(tota[0].body.bePlanList.length>0){
//								$scope.mappingSet[ind] =tota[0].body.bePlanList[0].PLAN_AMT || 0;
//								//計算未規劃
//								$scope.mappingSet[ind+10]=row.TOTAL_AMT-tota[0].body.bePlanList[0].PLAN_AMT;
//								if($scope.mappingSet[ind]>=0)
//									$scope.PLAN_AMT += $scope.mappingSet[ind];
//								if($scope.mappingSet[ind+10]>=0)
//									$scope.UNPLAN_AMT += $scope.mappingSet[ind+10];
//							}else{
//								$scope.mappingSet[ind] = 0;
//								$scope.PLAN_AMT += $scope.mappingSet[ind];
//								//未規劃+0
//								$scope.mappingSet[ind+10] = 0;
//								$scope.UNPLAN_AMT += $scope.mappingSet[ind+10];
//							}							
//							if(tota[0].body.totallist.length>0){								
//								$scope.TOT_PLAN_AMT =tota[0].body.totallist[0].TOT_PLAN_AMT;
//							}else{
//								$scope.TOT_PLAN_AMT=0;
//							
//							}
//						}else{
//								$scope.mappingSet[ind] =0;
//								//未規劃
//								$scope.mappingSet[ind+10] =0;
//								$scope.TOT_PLAN_AMT=0;								
//								$scope.PLAN_AMT=0;
//						}
//		});	
//    }
});
