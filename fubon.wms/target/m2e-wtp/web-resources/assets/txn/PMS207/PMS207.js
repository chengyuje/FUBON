/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS207Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS207Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		var backFlag = $scope.connector('get', 'backFlag');
		$scope.opchage= function (){
			if($scope.inputVO.branch_nbr=='' && $scope.inputVO.ao_code==''){
				$scope.inputVO.region_center_id='';
				
				$scope.inputVO.branch_area_id='';
				$scope.ChangeRegion();
				$scope.ChangeArea();
			}
		}
		 $scope.edi = function (row) {
			 $scope.connector('set', 'PMS207_TYPE_DETAIL', $scope.inputVO.sttType);
				$scope.connector('set', 'PMS207_fnlDt_DETAIL', $scope.finalDate);
				$scope.connector('set', 'PMS207_YRMN', row.YEARMON);
				$scope.connector('set', 'PMS207_AOCODE', row.AO_CODE);
				$scope.connector('set', 'PMS207_EMPID', row.EMP_ID);
				$scope.connector('set', 'PMS207_WKDT', row.WKDT);
				$scope.connector('set', 'PMS207_BRID', $scope.inputVO.br_id);
	        			var custID = $scope.inputVO.custID;
	        			var dialog = ngDialog.open({
	        				template: 'assets/txn/PMS207/PMS207_ROUTE.html',
	        				className: 'PMS102',
	        				showClose: false,
	        				scope : $scope,
	        				controller: ['$scope', function($scope) {
						
	        					$scope.isPop = true;
	        					$scope.routeURL = 'assets/txn/PMS207/PMS207_DETAIL.html';
	        					$scope.row = row;
	        				}]
	        			}).closePromise.then(function (data) {
	        				if(data.value != "cancel") {
	        					$scope.getProdDTL();
	        				}
	        			});
	        	
			};
		
		
		$scope.query = function(){
			
			if(backFlag != '1'){
				if($scope.parameterTypeEditForm.$invalid){
					$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
					return;
				}				
			}
			
			$scope.sendRecv("PMS207", "queryData", "com.systex.jbranch.app.server.fps.pms207.PMS207InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.mastList.length == 0) {
								$scope.finalDate = "";
								$scope.saleTarget = [];
								$scope.predictPcnt = [];
								$scope.realPcnt = [];
								$scope.empDtl=[];
								$scope.dataMon ="";
								$scope.aoEmp ="";
								$scope.jobRank = "";
								$scope.actAMT = [];
								$scope.actROI = [];  //實際/建議 收益
								$scope.data=[];
								$scope.totEstAmt_w =0;
								$scope.totActAmt_w =0;
								$scope.totEstRoi_w =0;
								$scope.totActRoi_w =0;
								$scope.totEstAmt_d =0;
								$scope.totActAmt_d =0;
								$scope.totEstRoi_d =0;
								$scope.totActRoi_d =0;
								$scope.paramList_w = tota[0].body.diffList_w;
								$scope.paramList_d = tota[0].body.diffList_w;
								for(var i=0; i<$scope.paramList_w.length; i++){
									$scope.totEstAmt_w += $scope.paramList_w[i].EST_AMT;
									$scope.totActAmt_w += $scope.paramList_w[i].ACT_AMT;
									$scope.totEstRoi_w += $scope.paramList_w[i].EST_ROI;
									$scope.totActRoi_w += $scope.paramList_w[i].ACT_ROI;
								}	
								for(var i=0; i<$scope.paramList_d.length; i++){
									$scope.totEstAmt_d += $scope.paramList_d[i].EST_AMT;
									$scope.totActAmt_d += $scope.paramList_d[i].ACT_AMT;
									$scope.totEstRoi_d += $scope.paramList_d[i].EST_ROI;
									$scope.totActRoi_d += $scope.paramList_d[i].ACT_ROI;
								}	
								$scope.showType();
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.data=[];
							$scope.totEstAmt_w =0;
							$scope.totActAmt_w =0;
							$scope.totEstRoi_w =0;
							$scope.totActRoi_w =0;
							$scope.totEstAmt_d =0;
							$scope.totActAmt_d =0;
							$scope.totEstRoi_d =0;
							$scope.totActRoi_d =0;
							$scope.finalDate = tota[0].body.dateList[0].DATA_DATE;
							$scope.saleTarget = tota[0].body.mastList[0].TARGET_AMT;
							$scope.predictPcnt = tota[0].body.mastList[0].EST_RATE;
							$scope.realPcnt = tota[0].body.mastList[0].ACT_RATE;
							
							$scope.dataMon = tota[0].body.mastList[0].YEARMON || '';
							$scope.aoEmp = tota[0].body.mastList[0].AO_CODE+'-'+tota[0].body.mastList[0].EMP_NAME || '';
							$scope.jobRank = tota[0].body.mastList[0].AO_JOB_RANK || '';
							$scope.actAMT = tota[0].body.mastList[0].ACT_AMT_NTD;
							$scope.actROI = tota[0].body.mastList[0].ACT_ROI_NTD;  //實際/建議 收益
							$scope.PRODUCT_GOALS = tota[0].body.mastList[0].PRODUCT_GOALS;
							
							$scope.paramList_w = tota[0].body.diffList_w;
							$scope.paramList_d = tota[0].body.diffList_d;
							for(var i=0; i<$scope.paramList_w.length; i++){
								$scope.totEstAmt_w += $scope.paramList_w[i].EST_AMT;
								$scope.totActAmt_w += $scope.paramList_w[i].ACT_AMT;
								$scope.totEstRoi_w += $scope.paramList_w[i].EST_ROI;
								$scope.totActRoi_w += $scope.paramList_w[i].ACT_ROI;
							}	
							for(var i=0; i<$scope.paramList_d.length; i++){
								$scope.totEstAmt_d += $scope.paramList_d[i].EST_AMT;
								$scope.totActAmt_d += $scope.paramList_d[i].ACT_AMT;
								$scope.totEstRoi_d += $scope.paramList_d[i].EST_ROI;
								$scope.totActRoi_d += $scope.paramList_d[i].ACT_ROI;
							}	
							$scope.outputVO = tota[0].body;
							
							 $scope.targetSale();
							
							/** 趨勢圖資料 **/
							$scope.showType();
							
							/**計算目標**/
							$scope.targetSale()
							/** 趨勢圖資料end **/
							return;
						}						
			});
		};
		
		 //本月銷售目標
        $scope.targetSale = function() {
            $scope.sendRecv("PMS207", "queryTargetSale", "com.systex.jbranch.app.server.fps.pms207.PMS207InputVO", $scope.inputVO,
                function(totas, isError) {
	            	if (isError) {
	            		$scope.showErrorMsg(totas[0].body.msgData);
	            		return;
	            	}
	            	if (totas.length > 0) {
	            		$scope.empDtl = totas[0].body.empDtl;
	            		if ($scope.empDtl.length > 0) {
	            			$scope.inputVO.selfNote = $scope.empDtl[0].SELF_NOTE;
	            		}
	            		
						$scope.empDtlOutputVO = totas[0].body;
	            	};
                });
        };
       
	 
		$scope.init = function(){			
			$scope.inputVO = {					
					dataMonth: '',
					sttType: 'week',					
					branch_nbr: '',
					emp_id: '',
					ac_code:''
			};
			$scope.curDate = new Date();
			$scope.type = '週';	
			$scope.empDtl=[];
//			if(backFlag == '1'){				
//				$scope.inputVO.sttType = $scope.connector('get', 'mainType');
//				$scope.inputVO.dataMonth = $scope.connector('get', 'mainYrMn');
//				$scope.inputVO.emp_id = $scope.connector('get', 'mainEmpId');
//				$scope.inputVO.br_id = $scope.connector('get', 'mainBrId');
//				$scope.inputVO.ao_code=$scope.connector('get', 'mainaocode');
//				
//				
//				$scope.query();
//				$scope.connector('set', 'backFlag', '0');
//				backFlag = '0';
//			}
			
//			if($scope.ymList != null && $scope.ymList.length > 0){
//	        	   $scope.inputVO.sTime = $scope.currentYM;
//	        }
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
		};
		
		$scope.currentYM = "";
		
//		 /***取得年月****/
//        $scope.initLoad = function(){
//        	
//            $scope.sendRecv("PMS000", "getSalesPlanYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
//    				function(tota, isError) {
//				if (!isError) {
//					
//					$scope.ymList = tota[0].body.ymList;					
//					
//					if($scope.ymList != null && $scope.ymList.length > 0){
//					   $scope.inputVO.dataMonth = tota[0].body.currentYM;	
//					   $scope.currentYM = tota[0].body.currentYM;
//					   
//					   $scope.inputVO.reportDate = $scope.inputVO.dataMonth;
//					   $scope.RegionController_getORG($scope.inputVO).then(function() {
//		        			if($scope.ao_code) $scope.query(); //載入頁面如果是理專單一code自動發查
//					   });
//			        }
//				}
//		    });
//    	};
//        $scope.initLoad();
		
		// 月份初始化
		var NowDate = new Date();
		
		var yr = NowDate.getFullYear();
	    var mm = NowDate.getMonth()+2;
	    var strmm='';
	    $scope.mappingSet['timeE'] = [];
	    for(var i=0; i<12; i++){
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
        $scope.init();
      
        
      //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	//如果選擇系統時間之後月份則不需重查
//        	if($scope.inputVO.dataMonth > $scope.currentYM){
//        		return;
//        	}
        	$scope.inputVO.reportDate = $scope.inputVO.dataMonth;
        	if($scope.inputVO.dataMonth!=''){
        		$scope.RegionController_getORG($scope.inputVO);
        	}
        };
        
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
	
		
						
		
				
		$scope.inquireInit = function(){
			
			$scope.data = [];
//			$scope.inputVO.dataMonth=new Date();
			$scope.inputVO.sttType='week';					
			$scope.inputVO.br_id='';
			$scope.inputVO.emp_id='';
			$scope.inputVO.ac_code='';
			$scope.inputVO.branch_nbr='';			
			$scope.finalDate = '';
			$scope.saleTarget = '';
			$scope.predictPcnt = '';
			$scope.realPcnt = '';
			$scope.dataMon = '';
			$scope.aoEmp = '';
			$scope.jobRank = '';
			$scope.actAMT = '';
			$scope.actROI = '';
			$scope.totEstAmt_w = 0;
			$scope.totActAmt_w = 0;
			$scope.totEstRoi_w = 0;
			$scope.totActRoi_w = 0;
			$scope.totEstAmt_d = 0;
			$scope.totActAmt_d = 0;
			$scope.totEstRoi_d = 0;
			$scope.totActRoi_d = 0;
			//$scope.initLimit();
			$scope.paramList_w = [];
			$scope.paramList_d = [];
		}
		$scope.inquireInit();
		
		$scope.showType = function(){
			$scope.paramList = [];
			if($scope.inputVO.sttType == 'week'){
				$scope.type = '週';
				/** 趨勢圖資料 **/
				$scope.data = generateData_w();
				function generateData_w(){
					var soROI = [];		//建議手收
					var estROI = [];	//當週預估收益
					var actROI = [];	//當週實際收益
					var culROI = [];	//MTD實際收益
					var culvalue = 0;
												
					for(var h = 0; h < $scope.paramList_w.length; h++){
						soROI.push({
							x: $scope.paramList_w[h].WORK_DT,
							y: $scope.actROI/10000
						});			
						estROI.push({
							x:  $scope.paramList_w[h].WORK_DT,
							y: $scope.paramList_w[h].EST_ROI/10000
						});
						actROI.push({
							x:  $scope.paramList_w[h].WORK_DT,
							y: $scope.paramList_w[h].ACT_ROI/10000
						});
						culvalue += $scope.paramList_w[h].ACT_ROI;
						culROI.push({
							x:  $scope.paramList_w[h].WORK_DT,
							y: culvalue/10000
						});
					}							
					return [
					{
					    key: '建議手收',
					   	color: 'red',
					   	type: 'bar',
					   	values: soROI
					},
					{
						key: '當週預估收益',
						color: '#FA8258',
						values: estROI,
						type: 'bar'
					},
					{
						key: '當週實際收益',
						color: '#A5DF00',
						type: 'bar',
						values: actROI
					},
					{
						key: 'MTD實際收益',
						color: '#58ACFA',
						type: 'bar',
						values: culROI
					}
					];
				}
				
				/** ===== 收益趨勢圖 ====== **/
				$scope.options1 = {
			            chart: {
			                type: 'multiBarChart',
			                height: 380,
			                margin : {
			                    top: 20,
			                    right: 20,
			                    bottom: 50,
			                    left: 55
			                },	                	               
			                clipEdge : false,
			    			staggerLabels : false,
			    			transitionDuration : 1000,
			    			tooltips : true,
			    			tooltipContent : function(key, x, y, e, graph) {
			    				return '<p>' + key + ': ' + y + '</p>';
			    			},
			    			stacked : false,
			    			showControls : false,
			                xAxis: {
			                    axisLabel: 'Weeks',
			                    axisLabelDistance : 0,
			                    showMaxMin : true,
			    				tickFormat : function(d) {
			    					return  d ;
			    				}
			                },
			                yAxis: {
			                    axisLabel: '手收(萬)',
			                    axisLabelDistance: -5,
			                    tickFormat : function(d) {
			    					return d3.format(',.3f')(d);
			    				}
			                },
			                yDomain1:[0,1000]
              
			            },
			            title:{
		                	enable: true,
		                	text: "收益趨勢圖(週)"
		                },
			        };
					
				
			}else{
				$scope.type = '日';	
				/** 趨勢圖資料 **/	
				$scope.data = generateData_d();
				function generateData_d(){
					var soROI = [];
					var estROI = [];
					var actROI = [];
					var culROI = [];
					var culvalue = 0;
												
					for(var h = 0; h < $scope.paramList_d.length; h++){
						soROI.push({
							x: $scope.paramList_d[h].WORK_DT,
							y: $scope.actROI/10000
						});			
						estROI.push({
							x: $scope.paramList_d[h].WORK_DT,
							y: $scope.paramList_d[h].EST_ROI/10000
						});
						actROI.push({
							x: $scope.paramList_d[h].WORK_DT,
							y: $scope.paramList_d[h].ACT_ROI/10000
						});
						culvalue += $scope.paramList_d[h].ACT_ROI;
						culROI.push({
							x: $scope.paramList_d[h].WORK_DT,
							y: culvalue/10000
						});
					}							
					return [
					{
					    key: '建議手收',
					   	color: 'red',
					   	type: 'bar',
					   	values: soROI
					},
					{
						key: '當日預估收益',
						color: '#FA8258',
						values: estROI,
						type: 'bar'
					},
					{
						key: '當日實際收益',
						color: '#A5DF00',
						type: 'bar',
						values: actROI
					},
					{
						key: 'MTD實際收益',
						color: '#58ACFA',
						type: 'bar',
						values: culROI
					}
					];
				}		
			}
			
			/** ===== 收益趨勢圖 ====== **/
			$scope.options2 = {
		            chart: {
//		                type: 'multiChart',
		                type: 'multiBarChart',
		                height: 380,
		                margin : {
		                    top: 20,
		                    right: 20,
		                    bottom: 50,
		                    left: 55
		                },	                	               
		                clipEdge : false,
		    			staggerLabels : false,
		    			transitionDuration : 1000,
		    			tooltips : true,
		    			tooltipContent : function(key, x, y, e, graph) {
		    				return '<p>' + key + ': ' + y + '</p>';
		    			},
		    			stacked : false,
		    			showControls : false,
		                xAxis: {
		                    axisLabel: 'days',
		                    axisLabelDistance : 0,
		                    showMaxMin : true,

		    				tickFormat : function(d) {
		    					return   d ;
		    				}
		                },
		                yAxis: {
		                    axisLabel: '手收(萬)',
		                    axisLabelDistance: -5,

		                    tickFormat : function(d) {
		    					return d3.format(',.3f')(d);
		    				}
		                },
		                yDomain1:[0,1000]
              
		            },
		            title:{
	                	enable: true,
	                	text: "收益趨勢圖(日)"
	                },
		        };
		}

		
		
		// A GROUP
		
		/** ===== 趨勢圖 ===== **/
		$scope.dataDetail = function(row){
			$scope.connector('set', 'PMS207_TYPE_DETAIL', $scope.inputVO.sttType);
			$scope.connector('set', 'PMS207_fnlDt_DETAIL', $scope.finalDate);
			$scope.connector('set', 'PMS207_YRMN', row.YEARMON);
			$scope.connector('set', 'PMS207_AOCODE', row.AO_CODE);
			$scope.connector('set', 'PMS207_EMPID', row.EMP_ID);
			$scope.connector('set', 'PMS207_WKDT', row.WKDT);
			$scope.connector('set', 'PMS207_BRID', $scope.inputVO.br_id);
    		$rootScope.menuItemInfo.url = "assets/txn/PMS207/PMS207_DETAIL.html";
		};		
});
