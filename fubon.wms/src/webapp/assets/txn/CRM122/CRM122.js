/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM122Controller',
		function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter, $rootScope) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM122Controller";
/**==================================================================初始化=====================================================**/
		//xml
		 getParameter.XML(['CAM.TASK_STATUS','PMS.COACHING_ACTION', 'CAM.CAL_SALES_TASK_AMC_DESC','PMS.SALE_PLAN_PRD_TYPE' ], function(totas) {
				if(len(totas)>0) {
					$scope.mappingSet['CAM.TASK_STATUS'] = totas.data[totas.key.indexOf('CAM.TASK_STATUS')];
					$scope.mappingSet['PMS.COACHING_ACTION'] = totas.data[totas.key.indexOf('PMS.COACHING_ACTION')];
					$scope.mappingSet['CAM.CAL_SALES_TASK_AMC_DESC'] = totas.data[totas.key.indexOf('CAM.CAL_SALES_TASK_AMC_DESC')];
					$scope.mappingSet['PMS.SALE_PLAN_PRD_TYPE'] = totas.data[totas.key.indexOf('PMS.SALE_PLAN_PRD_TYPE')];
				}
		});
		
		// init
		$scope.calendarView = 'month';
		$scope.calendarView1 = 'day';
        $scope.viewDate = new Date();
        $scope.viewDate1 = new Date();
        $scope.ans = '';
        $scope.ans1 = '';
        
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
        
        $scope.inputVO = {
        		date   : undefined,
        		EMP_ID : '',
        		AO_CODE : '',
        		AOsList:[],
        		updateType:'',
        		taskDo :[],
        		taskAMC :[]
       
    	};
        
        $scope.init = function() {
        	$scope.MyTodoLst =[];
			$scope.MyAUMLst = [];
        	$scope.login = '';
        	$scope.login = String(projInfoService.getPriID());
        	console.log($scope.login)

        	$scope.amc = false;
        	if($scope.login <= '003' || $scope.login == '009' || $scope.login == '011' || $scope.login == '012'){
        		$scope.amc = true;
        	}
        };
        $scope.init();
        
        //複選-待辦事項
        $scope.toggleSelection = function  toggleSelection(data) {
 		    var ans = $scope.MyTodoLst.filter(function(obj) {
 		    	return (obj.SELECTED == true);
 		    });
 		         
 		    $scope.ans = ans;
 		    $scope.inputVO.updateType ='';
 		    $scope.inputVO.updateType ='B';
 		    $scope.updateType = $scope.inputVO.updateType;
 		    $scope.seq_no= data;
 		    if($scope.inputVO.updateType == 'B'){
           	  	angular.forEach($scope.MyTodoLst, function(row, index, objs) {
    				 if(row.SEQ_NO == $scope.seq_no) { 
    					 $scope.inputVO.taskDo = row;
    				 }
           	  	});
           	  	    
           	  	$scope.row = $scope.inputVO.taskDo;
           	  	$scope.seq_no ='';
 		    }	        	
        }
        
//        //複選-AMC
        $scope.toggleSelection_1 = function toggleSelection_1(data,data_1) {	
	       	var ans1 = $scope.MyAUMLst.filter(function(obj){
	       		return (obj.SELECTED == true);
	       	});
	    	
	    	
	    	$scope.inputVO.updateType ='';
        	$scope.inputVO.updateType ='M';
        	$scope.updateType = $scope.inputVO.updateType;
        	$scope.seq = data;
	    	$scope.custID = data_1;
        	if($scope.inputVO.updateType == 'M'){
        		$scope.ans1 = ans1;    	    	
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
        

        //轄下理專
        $scope.sendRecv("CRM122", "getMyAOs","com.systex.jbranch.app.server.fps.crm122.CRM122InputVO",$scope.inputVO,
        		function(totas, isError) {
				if (isError) {
	        		$scope.showErrorMsg(totas[0].body.msgData);
	        	}
	
				if (totas[0].body.MyAOsList !=null && totas[0].body.MyAOsList.length > 0) {
	           		$scope.MyAOsList = totas[0].body.MyAOsList;
	           		$scope.mappingSet['emp'] = [];			
	    			angular.forEach($scope.MyAOsList , function(row, index, objs){	
	    				$scope.inputVO.AOsList.push( row.EMP_ID );
	    				$scope.mappingSet['emp'].push({LABEL: row.AO_CODE +"-"+ row.EMP_NAME, DATA: row.AO_CODE});				
	    			});
	    			
	    			$scope.initdetail();
				}
	        })
        
        //行事曆
        $scope.initdetail = function(){
        	$scope.MyTodoLst =[];
			$scope.MyAUMLst = [];
			
			if($scope.inputVO.EMP_ID == undefined){
				$scope.inputVO.EMP_ID = '';
			}
			$scope.sendRecv("CRM122", "getTodo", "com.systex.jbranch.app.server.fps.crm122.CRM122InputVO", {'AOsList':$scope.inputVO.AOsList , 'EMP_ID':$scope.inputVO.EMP_ID},
					function(totas, isError) {
						if (isError) {
		            		$scope.showErrorMsg(totas[0].body.msgData);
		            	}
						$scope.events = [];
						if (totas[0].body.resultList != null && totas[0].body.resultList.length > 0) {
			           		$scope.resultList = totas[0].body.resultList;
			           		
		               		angular.forEach($scope.resultList, function(row, index, objs){
		               			if(row.DATETIME){
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
	                			if(row.STATUS == 'A' || row.STATUS == 'M' || row.STATUS == 'C')
	            					$scope.events.push({'title':row.TITLE, 'type':'type1', 'startsAt':start, 'endsAt':end, 'NAME':row.CUST_ID +"-"+ row.CUST_NAME, 'ddate':$scope.toJsDate(row.DATETIME), 'SEQ_NO':row.SEQ_NO});
	            				else
	            					$scope.events.push({'title':row.TITLE, 'type':'type2', 'startsAt':start, 'endsAt':end, 'NAME':row.CUST_ID +"-"+ row.CUST_NAME, 'ddate':$scope.toJsDate(row.DATETIME), 'SEQ_NO':row.SEQ_NO});
		               			}
		               		});
	               	};
	        		$scope.dateClicked($scope.viewDate1);
			});
		};
		// group
//		$scope.groupEvents = function(cell) {
//	        cell.groups = {};
//	       	cell.groups["TEST"] = [];
//	       	cell.groups2 = {};
//	       	cell.groups2["TEST"] = [];
//	       	var today = new Date();
//	       		cell.events.forEach(function(event) {
//	       		var a = $filter('date')(event.ddate,'yyyyMMdd');
//	       		var b = $filter('date')(today,'yyyyMMdd');
//	           	//待辦事項
//	       		if(event.type == 'type2') {
//	       			// startsAt endsAt
//	            	if(a >= b) {
//	            		cell.groups["COLOR"] = "success";
//	                  	cell.groups["TEST"].push(event);
//	            	}
//	            	else {
//	            		cell.groups["COLOR"] = "danger";
//	                   	cell.groups["TEST"].push(event);
//	            	}
//	           	}
//	            //AMC
////	            else {
////	            	// startsAt endsAt
////	            	if(a >= b) {
////	            		cell.groups2["COLOR"] = "success";
////	                   	cell.groups2["TEST"].push(event);
////	           		}
////	           		else {
////	           			cell.groups2["COLOR"] = "danger";
////	                   	cell.groups2["TEST"].push(event);
////	           		}
////	           	}
//	       	});
//		};
	        
		//明細
		$scope.expiry = function (date){
			$scope.inputVO.date = date;		
			if($scope.inputVO.AOsList.length > 0) {
				$scope.sendRecv("CRM122", "getTodoDtl", "com.systex.jbranch.app.server.fps.crm122.CRM122InputVO", {'AOsList':$scope.inputVO.AOsList , 'date':$scope.inputVO.date , 'EMP_ID':$scope.inputVO.EMP_ID},
						function(totas, isError) {
							if (isError) {
								$scope.showErrorMsg(totas[0].body.msgData);
							}
							
							if (totas[0].body.MyTodoLst!=null && totas[0].body.MyTodoLst.length > 0) {
								$scope.MyTodoLst = totas[0].body.MyTodoLst;
								$scope.outputVO = totas[0].body;
							}
				});
				if($scope.amc){
					$scope.sendRecv("CRM122", "getAMC", "com.systex.jbranch.app.server.fps.crm122.CRM122InputVO", {'AOsList':$scope.inputVO.AOsList,'date':$scope.inputVO.date , 'EMP_ID':$scope.inputVO.EMP_ID},
							function(totas, isError) {
								if (isError) {
					 				$scope.showErrorMsg(totas[0].body.msgData);
					 			}
					 			
					 			if (totas[0].body.MyAUMLst!=null && totas[0].body.MyAUMLst.length > 0) {
					 				
					 				$scope.MyAUMLst = totas[0].body.MyAUMLst;
					 				$scope.outputVO_1 = totas[0].body;
					 			}
					 });					 		
				}				 	
			}
		};


        $scope.getEMP_ID = function(){
        	$scope.set = '';
        	$scope.set  = $scope.inputVO.EMP_ID;
        }
        //新增
        $scope.insert = function(){
        	var date = $scope.inputVO.date;

        	var date = $scope.inputVO.date;
        	var dialog = ngDialog.open({
        		template: 'assets/txn/CUS160/CUS160_ADD.html',
        		className: 'CAM160_ADD',
        		showClose: false,
        		controller: ['$scope', function($scope) {
        			$scope.CUS160PAGE = "CUS160";
        			$scope.date = date;
        		}]						    	        
        	});
        	dialog.closePromise.then(function(data) {
        		if(data.value === 'successful'){
        			$scope.initdetail();
        			$scope.dateClicked($scope.inputVO.date);
        		}
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
        				$scope.initdetail(); 
        				$scope.dateClicked($scope.inputVO.date);
        			}
        		});
        	}
        }	 
			 
        //刪除
        $scope.deleTodo = function (){
        	//待辦事項
        	var ans = $scope.MyTodoLst.filter(function(obj) {
        		return (obj.SELECTED == true);
        	});
	           	
//        	//AMC
//        	var ans1 = $scope.MyAUMLst.filter(function(obj) {
//        		return (obj.SELECTED == true);
//        	});
	        	
        	if(ans.length == 0 && ans1.length == 0) {
        		return;
        	}
        	$confirm({text:  '是否確定要刪除?'}, {size: '200px'}).then(function() {	
        		$scope.sendRecv("CRM122", "delTack", "com.systex.jbranch.app.server.fps.crm122.CRM122InputVO", {'chkCodedata': ans,'chkCode_1data': ans1},
        				function(tota, isError) {
        					if (isError) {
        						$scope.showErrorMsg(tota[0].body.msgData);
        					}
		        				
        					if(tota.length > 0) {
        						$scope.initdetail();
        						$scope.dateClicked($scope.inputVO.date);
		     					$scope.showSuccessMsg('ehl_01_common_003');
		     				}else{
		    					$scope.showErrorMsg("ehl_01_common_005");
		    				}
		     		 	}
		       	)
	       	});
        };
//        //AMC詳細資料
        $scope.opendtl = function(row){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/CRM122/CRM122_DETAIL.html',
        		className: 'CRM122_DETAIL',
        		showClose: false,
        		controller: ['$scope', function($scope) {
        			$scope.row = row;
        		}]						    	        
        	});
        	dialog.closePromise.then(function(data) {
        		if(data.value === 'successful') {
        			$scope.initdetail();
        			$scope.dateClicked($scope.inputVO.date);
        		}
        	});
        };
	         
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
	        					$scope.dateClicked($scope.inputVO.date);
	        					$scope.initdetail(); 
	        				}else{
	        					$scope.showMsg("ehl_01_common_007");
	        				}
        		});
        	});
        };
        //AO_CODE下拉選單取EMP_ID
        $scope.AO_change = function(){
        	angular.forEach($scope.MyAOsList , function(row, index, objs){	
				if($scope.inputVO.AO_CODE == row.AO_CODE){
					$scope.inputVO.EMP_ID = row.EMP_ID;
					$scope.connector('set', "CRM122_EMPID",$scope.inputVO.EMP_ID);
					$scope.init();
					console.log(console.log($scope.login))
		        	$scope.initdetail();
				}
			});
        }
});