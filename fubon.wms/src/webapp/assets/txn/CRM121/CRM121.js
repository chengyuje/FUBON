/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM121Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $rootScope) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM121Controller";
		$controller('PPAPController', {$scope: $scope});
/**==================================================================初始化=====================================================**/
		//xml
		 getParameter.XML(['CAM.TASK_STATUS','PMS.FIN_TYPE','CAM.CAL_SALES_TASK_AMC_DESC','PMS.SALE_PLAN_PRD_TYPE'], function(totas) {
				if(len(totas)>0) {
					$scope.mappingSet['CAM.TASK_STATUS'] = totas.data[totas.key.indexOf('CAM.TASK_STATUS')];
					$scope.mappingSet['PMS.FIN_TYPE'] = totas.data[totas.key.indexOf('PMS.FIN_TYPE')];
					$scope.mappingSet['CAM.CAL_SALES_TASK_AMC_DESC'] = totas.data[totas.key.indexOf('CAM.CAL_SALES_TASK_AMC_DESC')];
					$scope.mappingSet['PMS.SALE_PLAN_PRD_TYPE'] = totas.data[totas.key.indexOf('PMS.SALE_PLAN_PRD_TYPE')];
				}
		});
		
		// init
		$scope.calendarView = 'month';
		$scope.calendarView1 = 'day';
		$scope.nowDate = new Date();
		$scope.nowDate.setHours(0,0,0,0);
        $scope.viewDate = new Date();
        $scope.viewDate.setHours(0,0,0,0);
        $scope.viewDate1 = new Date();
        $scope.viewDate1.setHours(0,0,0,0);
        $scope.ans = '';
        $scope.ans1 = '';
        $scope.inputVO={
        		date : undefined,
        		CUST_ID :'',
        		delAMCLst :'',
        		taskDo :[],
        		taskAMC :[],
        		privilege:'',
        		TASK_DATE:undefined,
        		updateType : ''
        	}
        
        
        $scope.init = function(){
			$scope.MyAUMLst = [];
			$scope.MyTodoLst = [];
        	$scope.loginR();
        }
        
        // 2018/4/17 財神爺
        $scope.sendRecv("CRM121", "fortuna", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {},
			function(tota, isError) {
            	if (!isError) {
            		// 待規劃客戶數
            		$scope.FORTUNA_CUST_COUNT = tota[0].body.resultList.length;
            		$scope.FORTUNA_CUST_LIST = tota[0].body.resultList;
            		// 待執行交易客戶數
            		$scope.FORTUNA_FUNC_COUNT = tota[0].body.resultList2.length > 0 ? tota[0].body.resultList2.length : 0;
            	};
			}
		);
        $scope.OPEN_FORTUNA = function(type) {
        	if((type == 'CUST' && $scope.FORTUNA_CUST_COUNT == 0) || (type == 'FUNC' && $scope.FORTUNA_FUNC_COUNT == 0))
        		return;
        	
        	var list = $scope.FORTUNA_CUST_LIST;
        	var cust_dis = $scope.FORTUNA_CUST_COUNT == 0 ? true : false;
        	var func_dis = $scope.FORTUNA_FUNC_COUNT == 0 ? true : false;
        	var dialog = ngDialog.open({
				template: 'assets/txn/CRM121/CRM121_DIALOG.html',
				className: 'CRM121_DIALOG',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.ACTIVE = type == 'CUST' ? 0 : 1;
                	$scope.FORTUNA_CUST_LIST = list;
                	$scope.disableTag1 = cust_dis;
                	$scope.disableTag2 = func_dis;
                }]
			});
        };
        //
     
    	$scope.loginR = function(){
    		$scope.sendRecv("CRM121", "login", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {},
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
	
						if (totas[0].body.privilege.length > 0) {
							$scope.inputVO.privilege = totas[0].body.privilege;
			           		$scope.initdetail();
						}
    		});
    	};
              
        //行事曆
        $scope.initdetail = function() {
        	$scope.sendRecv("CRM121", "getTodo", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {'privilege':$scope.inputVO.privilege},
        			function(totas, isError) {
        		if (isError) {
        			$scope.showErrorMsg(totas[0].body.msgData);
        		}
        		
        		if (totas[0].body.resultList.length > 0) {
        			$scope.resultList = []; 
        			$scope.resultList = totas[0].body.resultList;
        			$scope.events = [];
        			angular.forEach($scope.resultList, function(row) {
        				// 2017/8/4 time can null
        				if(row.DATETIME) {
        					var start = $scope.toJsDate(row.DATETIME);
            				if(row.STIME) {
            					start.setHours(row.STIME.substr(0,2));
                				start.setMinutes(row.STIME.substr(2,4));
            				}
            				var end = $scope.toJsDate(row.DATETIME);
            				if(row.ETIME) {
            					end.setHours(row.ETIME.substr(0,2));
                				end.setMinutes(row.ETIME.substr(2,4));
            				}
            				if(row.STATUS == 'A' || row.STATUS == 'M' || row.STATUS == 'C')
            					$scope.events.push({'title':row.TITLE, 'type':'type1', 'startsAt':start, 'endsAt':end, 'NAME':row.CUST_ID +"-"+ row.CUST_NAME, 'ddate':$scope.toJsDate(row.DATETIME), 'SEQ_NO':row.SEQ_NO});
            				else
            					$scope.events.push({'title':row.TITLE, 'type':'type2', 'startsAt':start, 'endsAt':end, 'NAME':row.CUST_ID +"-"+ row.CUST_NAME, 'ddate':$scope.toJsDate(row.DATETIME), 'SEQ_NO':row.SEQ_NO});
        				}
        				//
        			});
        		};
        	});
        };
        // group
//        $scope.groupEvents = function(cell) {
//        	cell.groups = {};
//        	cell.groups["TEST"] = [];
//        	cell.groups2 = {};
//        	cell.groups2["TEST"] = [];
//        	var today = new Date();
//            cell.events.forEach(function(event) {
//            	var a = $filter('date')(event.ddate,'yyyyMMdd');
//        		var b = $filter('date')(today,'yyyyMMdd');
//            	//待辦事項
//            	if(event.type == 'type2') {
//            		// startsAt endsAt
//            		if(a >= b) {
//            			cell.groups["COLOR"] = "success";
//                    	cell.groups["TEST"].push(event);
//            		}
//            		else {
//            			cell.groups["COLOR"] = "danger";
//                    	cell.groups["TEST"].push(event);
//            		}
//            	}
////            	//AMC
////            	else {
////            		// startsAt endsAt
////            		if(a >= b) {
////            			cell.groups2["COLOR"] = "success";
////                    	cell.groups2["TEST"].push(event);
////            		}
////            		else {
////            			cell.groups2["COLOR"] = "danger";
////                    	cell.groups2["TEST"].push(event);
////            		}
////            	}
//            });
//        };
        
		//click
        $scope.dateClicked = function(date) {
        	$scope.MyTodoLst = [];
        	$scope.MyAUMLst = [];
        	$scope.ans = '';
            $scope.ans1 = '';
            $scope.viewDate = date;
        	$scope.viewDate1 = date;
        	$scope.expiry(date);
        };
        
		$scope.expiry = function (date) {			
			$scope.inputVO.date = date;
			$scope.sendRecv("CRM121", "login", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {},
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
	
						if (totas[0].body.privilege.length > 0) {
							$scope.inputVO.privilege = totas[0].body.privilege;
							$scope.sendRecv("CRM121", "getTodoDtl", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {'date':$scope.inputVO.date,'privilege':$scope.inputVO.privilege},
									function(totas, isError) {
										if (isError) {
						            		$scope.showErrorMsg(totas[0].body.msgData);
						            	}	
										if (totas[0].body.MyTodoLst.length > 0) {
											$scope.MyTodoLst = totas[0].body.MyTodoLst;
											$scope.outputVO = totas[0].body;
											if($scope.inputVO.privilege == '0') {
												$scope.events1 = [];
												angular.forEach($scope.MyTodoLst, function(row, index, objs){
													if(row.DATETIME) {
														var start = $scope.toJsDate(row.DATETIME);
														if(row.STIME){
															start.setHours(row.STIME.substr(0,2));
								                			start.setMinutes(row.STIME.substr(2,4));
														}
														var end = $scope.toJsDate(row.DATETIME);
														if(row.ETIME){
															end.setHours(row.ETIME.substr(0,2));
								                			end.setMinutes(row.ETIME.substr(2,4));
														}
							                			$scope.events1.push({'title':row.TITLE, 'type':'info', 'startsAt':start, 'endsAt':end, 'NAME':row.CUST_ID +"-"+ row.CUST_NAME, 'ddate':$scope.toJsDate(row.DATETIME), 'SEQ_NO':row.SEQ_NO });

													}
							                	});
												
											}
											return;
										}
							});
							
							$scope.sendRecv("CRM121", "getAMC", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {'date':$scope.inputVO.date,'privilege':$scope.inputVO.privilege},
									function(totas, isError) {
										if (isError) {
							           		$scope.showErrorMsg(totas[0].body.msgData);
							           	}
					
										/**AMC有資料時
										 * 檢查M是否有陪訪覆核的狀態
										 */
										if (totas[0].body.MyAUMLst.length > 0) {
								           	$scope.MyAUMLst = totas[0].body.MyAUMLst;
								           	$scope.outputVO_1 = totas[0].body;      		
											for (var i = 0; i < $scope.MyAUMLst.length; i++) {
												//審核中
												if ($scope.MyAUMLst[i].FAIA_STATUS == '01' || 
													$scope.MyAUMLst[i].FAIA_STATUS == '02' ||
													$scope.MyAUMLst[i].FAIA_STATUS == '03' ||
													$scope.MyAUMLst[i].FAIA_STATUS == '0A1' ||
													$scope.MyAUMLst[i].FAIA_STATUS == '0A2' ||
													$scope.MyAUMLst[i].FAIA_STATUS == '0A3') {
													$scope.MyAUMLst[i].FAIA_STATUS = '未覆核';
												}
												//陪訪覆核遭退回
												else if ($scope.MyAUMLst[i].FAIA_STATUS == '1R' ||
													$scope.MyAUMLst[i].FAIA_STATUS == '2R' ||
													$scope.MyAUMLst[i].FAIA_STATUS == '3R' ||
													$scope.MyAUMLst[i].FAIA_STATUS == '4C' ||
													$scope.MyAUMLst[i].FAIA_STATUS == '5C'){
													$scope.MyAUMLst[i].FAIA_STATUS = '對方已取消';
												}		
												else{
													$scope.MyAUMLst[i].FAIA_STATUS = '';			
												}
											}
											return;
										}
								});
						}
    		});
		};
		$scope.dateClicked($scope.viewDate1);
		
		// change view
        $scope.changeView = function(data) {
        	$scope.calendarView = data;
        };
        
        // 點擊行事曆的事項
        $scope.eventClicked = function(event) {
        	for(var i = 0; i < $scope.MyTodoLst.length; i++) {	
        		if($scope.MyTodoLst[i].SEQ_NO == event){
        			$scope.modify($scope.MyTodoLst[i],'B');
        		}
        	}       	
        };
        
		//複選-待辦事項
        $scope.toggleSelection = function  toggleSelection(data) {
        	var ans = $scope.MyTodoLst.filter(function(obj){
        		return (obj.SELECTED == true);
		    });	
        	
	       	$scope.ans = ans;
	       	
	       	if(ans.length > 0){
	       		$scope.seq_no = ans[0].SEQ_NO;
	       	}	
	       	
    	    $scope.inputVO.updateType ='';
        	$scope.inputVO.updateType ='B';
        	$scope.updateType = $scope.inputVO.updateType;
//	        $scope.seq_no= data;
        	if($scope.inputVO.updateType == 'B'){
        		
        	  	angular.forEach($scope.MyTodoLst, function(row, index, objs){
        	  		if(row.SEQ_NO == $scope.seq_no){ 
 						$scope.inputVO.taskDo = row;
        	  		}
 			 	});
        	  	
    	  		$scope.row = [];
    	  		$scope.row = $scope.inputVO.taskDo;
    	  		$scope.seq_no ='';
        	} 
        };       
        
//        //複選-AMC
        $scope.toggleSelection_1 = function toggleSelection_1(data,data_1) {
        	//AMC
	       	var ans1 = $scope.MyAUMLst.filter(function(obj){
	       		return (obj.SELECTED == true);
	       	});
	    	$scope.ans1 = ans1;
        	$scope.inputVO.updateType ='';
        	$scope.inputVO.updateType ='M';
        	$scope.updateType = $scope.inputVO.updateType;
        	$scope.seq = data;
        	$scope.custID = data_1;
        	if($scope.inputVO.updateType =='M'){
        		angular.forEach($scope.MyAUMLst, function(row, index, objs){
        			if(row.SEQ == $scope.seq && row.CUST_ID == $scope.custID){ 
        				$scope.inputVO.taskAMC = row;	    						
        			}
        		});
        		$scope.row = [];
        		$scope.row = $scope.inputVO.taskAMC;
        		$scope.seq = '';
        		$scope.custID = '';	             		
        	}         	
        };
        
        //新增 
        $scope.insertTodo = function (){
        	var date = $scope.viewDate1;
        	var dialog = ngDialog.open({
        		template: 'assets/txn/CRM121/CRM121_INSERT.html',
        		className: 'CRM121_INSERT',
        		showClose: false,
        		controller: ['$scope', function($scope) {
        			$scope.date = date;
        		}]	
        	});
        	dialog.closePromise.then(function (data) {
        		//待辦事項
        		if(data.value === 'T'){
        			var date = $scope.date;
    				var dialog = ngDialog.open({
    	 				template: 'assets/txn/CUS160/CUS160_ADD.html',
    	 				className: 'CAM160_ADD',
    	 				showClose: false,
    	                 controller: ['$scope', function($scope) {
    	                 	$scope.CUS160PAGE = "CUS160";
    	                 	$scope.date = date;
    	                 }]
    	 			});
    	 			dialog.closePromise.then(function (data) {
    	 				$rootScope.IamInitFunction();//#0001958:由待辦行事曆打開的任何是窗關閉後做行事曆頁面的重新整理
    	 			});
        		}
			}); 
        };
        //主管直接新增
        $scope.openCUS160_ADD = function (){       	
        	$scope.connector('set', "CRM122_EMPID",sysInfoService.getUserID());
        	var date = $scope.viewDate1;
        	var dialog = ngDialog.open({
        		template: 'assets/txn/CUS160/CUS160_ADD.html',
	 			className: 'CUS160_ADD',
	 			showClose: false,
	            controller: ['$scope', function($scope) {
	                 $scope.CUS160PAGE = "CUS160";
	                 $scope.date = date;
	            }]						    	        
	        });
        	dialog.closePromise.then(function (data) {
        		$rootScope.IamInitFunction();//#0001958:由待辦行事曆打開的任何是窗關閉後做行事曆頁面的重新整理
			});    		        	 
        };
        //主管直接新增 END
          
        //刪除
        $scope.deleTack = function (){      	
     	   
        	//待辦事項
           	var ans = $scope.MyTodoLst.filter(function(obj){
           		return (obj.SELECTED == true);
           	});           	
//           	
//        	//AMC
        	var ans1 = $scope.MyAUMLst.filter(function(obj){
        		return (obj.SELECTED == true);
        	});        	
        	
        	if(ans.length == 0 && ans1.length == 0){
        		return;
        	}     
        	$confirm({text:  '是否確定要刪除?'}, {size: '200px'}).then(function() {	
	        	$scope.sendRecv("CRM121", "delTodo", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {'chkCodedata': ans,'chkCode_1data': ans1},
	     				function(tota, isError) {
	        				if (isError) {
	     						$scope.showErrorMsg(tota[0].body.msgData);
	     					}   
	        				
	     					if(tota.length > 0) {
	     						$rootScope.IamInitFunction();//#0001958:由待辦行事曆打開的任何是窗關閉後做行事曆頁面的重新整理
	     						$scope.showSuccessMsg('ehl_01_common_003');
	     					}else{
	     						$scope.showErrorMsg("ehl_01_common_005");
	     					}
	     			 	});
        	});
        };
                
        
        //編輯 B-待辦事項、M-AMC
        $scope.modify = function (row,updateType){
        	$scope.updateType = updateType.toString();
        	if($scope.updateType == 'B'){
        		var dialog = ngDialog.open({
        			template: 'assets/txn/CRM121/CRM121_MODIFY.html',
	 				className: 'CRM121_MODIFY',
	 				showClose: false,
	 				controller: ['$scope', function($scope) {
	 					$scope.row = row ;
	 					$scope.updateType = updateType;
	 				}]
	        	});
	        	dialog.closePromise.then(function(data) {
		       		if(data.value === 'successful'){
		       			$rootScope.IamInitFunction();//#0001958:由待辦行事曆打開的任何是窗關閉後做行事曆頁面的重新整理
		  			}
	 	  		});
	        }
        	else{
	        	$scope.connector('set','CRM121ToPMS103Modify_CUST_ID',row.CUST_ID);
//	        	$scope.connector('set','CRM121ToPMS103Modify_SEQ',row.SEQ);
//	        	alert(JSON.stringify(row));
//	        	$rootScope.menuItemInfo.url = "assets/txn/PMS103/PMS103.html";
	        	
	        	
				$scope.sendRecv("PMS103", "queryData", "com.systex.jbranch.app.server.fps.pms103.PMS103InputVO", {CUST_ID:row.CUST_ID,SEQ:row.SEQ},
						function(tota, isError) {
							if (!isError) {							
								if(tota[0].body.resultList.length == 0) {	
									//總合計歸0								
									$scope.showMsg("無該筆銷售計畫");
		                			return;
		                		}							
								$scope.paramList = tota[0].body.resultList;  //結果							
								$scope.outputVO = tota[0].body;
								//查詢完一次
							
								angular.forEach($scope.paramList, function(row, index, objs){
									if(index == 0)
										$scope.ppap(row, row.SEQ, row.CUST_ID, row.CUST_NAME, row.SRC_TYPE, 'upd'); 
								});	
								
								return;
							}
				});	
	        	
	        }
        }
        
        $scope.init();
          
        //date picker
        $scope.altInputFormats = ['M!/d!/yyyy'];
        $scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.dateDate = function (date) {
			
			var ans = $scope.MyTodoLst.filter(function(obj){
          		return (obj.SELECTED == true);
          	});		         
			$scope.ans = ans;
			$scope.inputVO.TASK_DATE = date;
			$scope.inputVO.updateType = '';
			$scope.inputVO.updateType = 'D';
			
			
			var newDate = $filter('date')($scope.inputVO.TASK_DATE,'yyyy-MM-dd');
			var oldDate = $filter('date')($scope.inputVO.date,'yyyy-MM-dd');
			
			$confirm({text:  oldDate + '改為：' + newDate + '?'}, {size: '200px'}).then(function() {
				$scope.sendRecv("CRM121", "update", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {'chkCodedata': ans, 'updateType': $scope.inputVO.updateType ,'TASK_DATE': $scope.inputVO.TASK_DATE},
						function(totas, isError) {
							if (isError) {
			            		$scope.showErrorMsg(totas[0].body.msgData);
			            	}
		
							if(totas.length > 0) {
								$scope.showSuccessMsg('ehl_01_common_025');
								$rootScope.IamInitFunction(); //#0001958:由待辦行事曆打開的任何是窗關閉後做行事曆頁面的重新整理
							}else{
								$scope.showMsg("ehl_01_common_007");
							}
				});
			});
		};
		
		/**#0001958:由待辦行事曆打開的任何是窗關閉後做行事曆頁面的重新整理**/
		$rootScope.IamInitFunction = function () {
			// init
			$scope.calendarView = 'month';
			$scope.calendarView1 = 'day';
			$scope.nowDate = new Date();
			$scope.nowDate.setHours(0,0,0,0);
	        $scope.viewDate = new Date();
	        $scope.viewDate.setHours(0,0,0,0);
	        $scope.ans = '';
	        $scope.ans1 = '';
	        $scope.events = [];
	        $scope.events1 = [];
	        $scope.inputVO={
	        		date : undefined,
	        		CUST_ID :'',
	        		delAMCLst :'',
	        		taskDo :[],
	        		taskAMC :[],
	        		privilege:'',
	        		TASK_DATE:undefined,
	        		updateType : ''
	        	}
	        $scope.init();
	        $scope.expiry($scope.viewDate);
		}
});