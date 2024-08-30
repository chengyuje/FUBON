/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS329_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS329_DETAILController";
		
	 	 $scope.inquire = function(){
	 		$scope.sendRecv("PMS329", "queryImage",
	 				"com.systex.jbranch.app.server.fps.pms329.PMS329IInputVO", {
	 					'list' : $scope.paramList,
	 					'ROB' : $scope.inputVO.ROB,
	 					'type' : $scope.inputVO.type
	 				}, function(tota, isError) {
	 					if (!isError) {
	 						var Array  = [];
	 						var Array0 = []; // 存放X,Y陣列
	 						var nam    = [];
	 						var c = [];
	 						var banchavg = 0;
	 						$scope.outputVO = tota[0].body;
	 						
	 						document.onkeydown = function(theEvent) {
	 					        if (theEvent != null) {
	 					                event = theEvent;
	 					        }
	 					        if (event.keyCode ==27) {
	 					                try {
	 					                	$scope.showMsg("請按右上角按鍵離開");
	 					                        // Firefox 會丟 Exception，所以用 try-cache 擋住
	 					           	var dialog = ngDialog.open({
	 									template: 'assets/txn/PMS329/PMS329_DETAIL.html',
	 									className: 'PMS329_DETAIL',
	 									showClose: false,
	 					                controller: ['$scope', function($scope) {
	 					                	$scope.row = $scope.rowlist;
	 					                }]
	 								});
	 								
	 					                        event.keyCode = 0; 
	 					                } catch(e){}
	 					                return false;
	 					        }
	 					}
//	 						alert(JSON.stringify(tota[0].body.resultList));

	 						for (var x = 0; x < $scope.paramList.length; x++) {

	 							angular.forEach(tota[0].body.resultList, function(
	 									row, index, objs) {
	 								if (banchavg == 0) {
	 									Array.push([ row.YEARMON, row.AVG ]);

	 								}
	 								if (row.AVG0 != undefined && x == 0) {
	 									Array0.push([ row.YEARMON, row.AVG0 ]);
	 								}
	 								if (row.AVG1 != undefined && x == 1) {
	 									Array0.push([ row.YEARMON, row.AVG1 ]);
	 								}
	 								if (row.AVG2 != undefined && x == 2) {
	 									Array0.push([ row.YEARMON, row.AVG2 ]);
	 								}
	 								if (row.AVG3 != undefined && x == 3) {
	 									Array0.push([ row.YEARMON, row.AVG3 ]);
	 								}
	 								if (row.AVG4 != undefined && x == 4) {
	 									Array0.push([ row.YEARMON, row.AVG4 ]);
	 								}
	 								if (row.AVG5 != undefined && x == 5) {
	 									Array0.push([ row.YEARMON, row.AVG5 ]);
	 								}
	 								if (row.AVG6 != undefined && x == 6) {
	 									Array0.push([ row.YEARMON, row.AVG6 ]);
	 								}
	 								if (row.AVG7 != undefined && x == 7) {
	 									Array0.push([ row.YEARMON, row.AVG7 ]);
	 								}
	 								if (row.AVG8 != undefined && x == 8) {
	 									Array0.push([ row.YEARMON, row.AVG8 ]);
	 								}
	 								if (row.AVG9 != undefined && x == 9) {
	 									Array0.push([ row.YEARMON, row.AVG9 ]);
	 								}
	 								if (row.AVG10 != undefined && x == 10) {
	 									Array0.push([ row.YEARMON, row.AVG10 ]);
	 								}
	 								if (row.AVG11 != undefined && x == 11) {
	 									Array0.push([ row.YEARMON, row.AVG11 ]);
	 								}
	 								if (row.AVG12 != undefined && x == 12) {
	 									Array0.push([ row.YEARMON, row.AVG12 ]);
	 								}
	 								if (row.AVG13 != undefined && x == 13) {
	 									Array0.push([ row.YEARMON, row.AVG13 ]);
	 								}
	 								if (row.AVG14 != undefined && x == 14) {
	 									Array0.push([ row.YEARMON, row.AVG14 ]);
	 								}
	 								if (row.AVG15 != undefined && x == 15) {
	 									Array0.push([ row.YEARMON, row.AVG15 ]);
	 								}
	 								if (row.AVG16 != undefined && x == 16) {
	 									Array0.push([ row.YEARMON, row.AVG16 ]);
	 								}
	 								if (row.AVG17 != undefined && x == 17) {
	 									Array0.push([ row.YEARMON, row.AVG17 ]);
	 								}
	 								if (row.AVG18 != undefined && x == 18) {
	 									Array0.push([ row.YEARMON, row.AVG18 ]);
	 								}
	 								if (row.AVG19 != undefined && x == 19) {
	 									Array0.push([ row.YEARMON, row.AVG19 ]);
	 								}
	 								if (row.AVG20 != undefined && x == 20) {
	 									Array0.push([ row.YEARMON, row.AVG20 ]);
	 								}
	 								if (row.AVG21 != undefined && x == 21) {
	 									Array0.push([ row.YEARMON, row.AVG21 ]);
	 								}
	 								if (row.AVG22 != undefined && x == 22) {
	 									Array0.push([ row.YEARMON, row.AVG22 ]);
	 								}
	 								if (row.AVG23 != undefined && x == 23) {
	 									Array0.push([ row.YEARMON, row.AVG23 ]);
	 								}
	 								if (row.AVG24 != undefined && x == 24) {
	 									Array0.push([ row.YEARMON, row.AVG24 ]);
	 								}
	 								if (row.AVG25 != undefined && x == 25) {
	 									Array0.push([ row.YEARMON, row.AVG25 ]);
	 								}
	 								if (row.AVG26 != undefined && x == 26) {
	 									Array0.push([ row.YEARMON, row.AVG26 ]);
	 								}
	 								if (row.AVG27 != undefined && x == 27) {
	 									Array0.push([ row.YEARMON, row.AVG27 ]);
	 								}
	 								if (row.AVG28 != undefined && x == 28) {
	 									Array0.push([ row.YEARMON, row.AVG28 ]);
	 								}
	 								if (row.AVG29 != undefined && x == 29) {
	 									Array0.push([ row.YEARMON, row.AVG29 ]);
	 								}


	 							});
	 							/**************全行平均*****************/
	 							if (banchavg == 0) {
	 								nam.push({
	 									"key" : "全行平均",
	 									"values" : Array
	 								});
	 								banchavg++;
	 							}
	 							
	 							/**************KEY是顯示欄位名稱,VALUE是X時間,Y值*****************/
	 							if ((x + 1) != $scope.paramList.length  && $scope.aotype != '1'
	 									) {
	 								nam.push({
	 									"key" : $scope.inputVO.type
	 											+ $scope.paramList[x + 1].LABEL,
	 									"values" : Array0
	 								});
	 							}
	 							/**************KEY是顯示欄位名稱,VALUE是X時間,Y值*****************/
	 							if (x != $scope.paramList.length
										&& $scope.aotype == '1' ) {
									nam.push({
										"key" : $scope.inputVO.type
												+ $scope.paramList[x].LABEL,
										"values" : Array0
									});
								}
	 							Array0 = [];
	 						}
	 						console.log("========================================"+JSON.stringify(nam));
	 						$scope.data = nam;
	 						$scope.inputVO.ROB = "";
	 						$scope.aotype = '0';
	 						return;
	 					}
	 				});
	 	};
			
			$scope.types= function(){
				
				// $scope.inquire();
				$scope.genRegion();
			};

		$scope.init = function(){
			  $scope.row = $scope.row || {};
			$scope.inputVO = {
        			eTime:'',
        			aocode  :'',
					branch  :'',
					region  :'',
					op      :'',
					aojob   :'',
					ROB     :'',
					type:''        	
						};
			$scope.mappingSet['type']=[];
			$scope.mappingSet['type'].push({LABEL : '保險-結餘',DATA : '(保險-結餘)'},{LABEL : '投資-結餘',DATA : '(投資-結餘)'},{LABEL : '存款-結餘',DATA : '(存款-結餘)'});
			$scope.paramList = [];
		};
        $scope.init();
        /***以下連動區域中心.營運區.分行別***/
		//大區域資訊
		$scope.genRegion = function() {
			$scope.mappingSet['region'] = [];
			$scope.mappingSet['branch'] = [];
			$scope.mappingSet['op'] = [];
			$scope.inputVO.region="";
			$scope.inputVO.op ="";
			$scope.inputVO.branch="";
			angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){
					$scope.mappingSet['region'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
					if($scope.mappingSet['region'].length>0){
							$scope.inputVO.ROB="1";	
						}
					
			});
			if($scope.mappingSet['region'].length>0)
			{
				$scope.paramList=$scope.mappingSet['region'];
			}	
			$scope.inquire();			
        };
        $scope.genRegion();
		
		
		//區域資訊
		$scope.genArea = function() {
			$scope.inputVO.op=[];
			$scope.mappingSet['branch'] = [];
			$scope.mappingSet['op'] = [];
			angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){
				if(row.REGION_CENTER_ID == $scope.inputVO.region)	
					{
						$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						$scope.inputVO.ROB="2";	
					}
					
			});
			if($scope.mappingSet['op'].length>0)
			{
				$scope.paramList=$scope.mappingSet['op'];
			}
			if($scope.mappingSet['op'].length==0)
					{
			     		$scope.genRegion();
					}
			$scope.inquire();
        };
      
        
        
        
    	$scope.bran = function(){
            $scope.sendRecv("PMS202", "aoCode", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", {},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);                    		
                    	}
                    	if (totas.length > 0) {
                    			$scope.mappingSet['aocode'] = [];
                        		$scope.mappingSet['aocodes'] = [];                    		
                        		angular.forEach(totas[0].body.aolist, function(row, index, objs){
	                    			if(row.BRANCH_NBR==$scope.inputVO.branch)
	                    				{
			                    			$scope.mappingSet['aocode'].push({LABEL: row.NAME, DATA: row.EMP_ID});
			                    			$scope.mappingSet['aocodes'].push({LABEL: row.NAME, DATA: row.EMP_ID});
			                    			$scope.inputVO.ROB="4";	
	                    				}
                    				});
                    	};
                    	
                    	if($scope.mappingSet['aocodes'].length>0)
        				{
		        			$scope.paramList=$scope.mappingSet['aocodes'];
		        			$scope.aotype='1';
        				}
        			$scope.inquire();
    				}
    		);
        }
        	
        
        
        
        
        
		
		//分行資訊
		$scope.genBranch = function() {
			$scope.mappingSet['branch'] = [];
			$scope.inputVO.aocode='';
			
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
				
				if(row.BRANCH_AREA_ID == $scope.inputVO.op)	
					{
					
						$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
						$scope.inputVO.ROB="3";	
						
					}
			});
				if($scope.mappingSet['branch'].length>0)
					{
							$scope.paramList=$scope.mappingSet['branch'];
					}
		
			$scope.inquire();			
        };

      
			
        //設定圖
		  $scope.options = {
		            chart: {
		                type: 'lineWithFocusChart',
		                height: 480,
		                margin : {
		                    top: 100,
		                    right: 100,
		                    bottom: 100,
		                    left: 100
		                },
		                x: function(d){return d[0];},
		                y: function(d){return d[1];},
		                useVoronoi: true,
		                clipEdge: true,
		                duration: 100,
		                useInteractiveGuideline: true,
		                xAxis: {
		                	 axisLabel: 'X 時間軸',
		                    showMaxMin: true,
		                    tickFormat: function(d) {
		                        return d3.time.format('%x')(new Date(d))
		                    }
		                },
		                yAxis: {
		                	 
		                	 tickFormat: function(d){
		                         return d3.format(',.2f')(d);
		                     }
		                },
		                x2Axis: {
		                    showMaxMin: true,
		                    tickFormat: function(d) {
		                        return d3.time.format('%x')(new Date(d))
		                    }
		                },
		             
		                y2Axis: {
		                    tickFormat: function(d){
		                        return d3.format(',f')(d);
		                    }
		                }
		               
		            }
		        };
		
		 
		     

		          

		        

		
});
