/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM124Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PPAPController', {$scope: $scope});
		
		/**=================================================================================================================**
		 **													init初始化														**		
		 **=================================================================================================================**/			
		
		$scope.controllerName = "CRM124Controller";
		$scope.mappingSet['emp_id'] = [];
		$scope.emp_id = projInfoService.getUserID();
		
		//xml
		 getParameter.XML(['CAM.TASK_STATUS','PMS.FIN_TYPE','CAM.CAL_SALES_TASK_AMC_DESC'], function(totas) {
				if(len(totas)>0) {
					$scope.mappingSet['CAM.TASK_STATUS'] = totas.data[totas.key.indexOf('CAM.TASK_STATUS')];
					$scope.mappingSet['PMS.FIN_TYPE'] = totas.data[totas.key.indexOf('PMS.FIN_TYPE')];
					$scope.mappingSet['CAM.CAL_SALES_TASK_AMC_DESC'] = totas.data[totas.key.indexOf('CAM.CAL_SALES_TASK_AMC_DESC')];
				}
		});
		 
		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		// CAM.TASK_SOURCE
		var vo = {'param_type': 'CAM.TASK_SOURCE', 'desc': false};
		
        if(!projInfoService.mappingSet['CAM.TASK_SOURCE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CAM.TASK_SOURCE'] = totas[0].body.result;
        			$scope.mappingSet['CAM.TASK_SOURCE'] = projInfoService.mappingSet['CAM.TASK_SOURCE'];
        		}
        	});
        } else{
        	$scope.mappingSet['CAM.TASK_SOURCE'] = projInfoService.mappingSet['CAM.TASK_SOURCE'];
        }
        
		$scope.calendarView = 'month';
		$scope.calendarView2 = 'day';
		$scope.viewDate = new Date();
        $scope.isCellOpen = false;
        $scope.type = '';
        
        //login方法:CRM121查詢需要用到privilege
		$scope.loginR = function(){
    		$scope.sendRecv("CRM121", "login", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {},
				function(totas, isError) {
					if (isError) {
		           		$scope.showErrorMsg(totas[0].body.msgData);
		           	}
					
					if (totas[0].body.privilege.length > 0) {
						$scope.inputVO.privilege = totas[0].body.privilege;
					}
    		});
    	};
    	
    	//初始化
		$scope.init = function(){
			$scope.ans = '';	//輔銷-提醒
			$scope.ans2 = '';	//輔銷-代辦事項
			$scope.ans3 = '';	//輔銷-陪訪紀錄
			$scope.ans4 = '';	//輔銷-代辦事項(理專)
			$scope.ansAO = '';	//理專-輔銷駐點變更通知
			$scope.loginR();
			
			//輔銷科長頁面
			if (projInfoService.getRoleID() == "FA9" || projInfoService.getRoleID() == "IA9"){
				$scope.type = 1;
				$scope.remindList = [];		//輔銷提醒
				$scope.remindList2 = [];	//待辦事項
				$scope.remindList3 = [];	//陪訪紀錄
				$scope.remindList4 = [];	//待辦事項(理專)
				//輔銷人員選單
				$scope.sendRecv("CRM124", "emp_inquire", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {},
						function(tota, isError) {
							if (!isError) {
								var emp_id_list = [];
								emp_id_list = tota[0].body.resultList;			
								$scope.mappingSet['emp_id'] = [];
								angular.forEach(emp_id_list, function(row, index, objs){				
									$scope.mappingSet['emp_id'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
								});	
								return;
							}
				});
			//輔銷人員頁面	
			}else if(projInfoService.getRoleID() == "FA" || projInfoService.getRoleID() == "IA"){
				$scope.type = 2;
				$scope.remindList = [];		//輔銷提醒
				$scope.remindList2 = [];	//待辦事項
				$scope.remindList3 = [];	//陪訪紀錄
				$scope.remindList4 = [];	//待辦事項(理專)
			}else{
				$scope.type = 3;
				$scope.MyTodoLst = [];		//輔銷變更駐點通知
			}
			
			//2017/08/10 CRM122 getMyAOs 執行較久導致同步順序問題，將此方法包起來處理
			//#0003359 : 輔銷行事曆顯示理專的代辦事項，用CRM122理專行事曆的方法
			//轄下理專
			$scope.sendRecv("CRM122", "getMyAOs","com.systex.jbranch.app.server.fps.crm122.CRM122InputVO",{},
				function(totas, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					}
					$scope.MyAOsList = [];
					if (totas[0].body.MyAOsList != null && totas[0].body.MyAOsList.length > 0) {
						$scope.MyAOsList = totas[0].body.MyAOsList;
						$scope.mappingSet['emp'] = [];
						$scope.inputVO.AOsList = [];
						angular.forEach($scope.MyAOsList , function(row, index, objs){
							$scope.inputVO.AOsList.push(row.EMP_ID);
							$scope.mappingSet['emp'].push({LABEL: row.AO_CODE +"-"+ row.EMP_NAME, DATA: row.AO_CODE});
						});
					}
			
			/**=================================================================================================================**
			 **													左側行事曆紅點														**		
			 **=================================================================================================================**/			
			
				//原輔銷駐點行事曆查詢資料
				$scope.sendRecv("CRM124", "initial", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {'date':$scope.viewDate,'emp_id':$scope.emp_id,'branch_id':projInfoService.getBranchID() ,'faia_type':$scope.inputVO.faia_type},
					function(totas, isError) {
		                if (isError) {
		                	$scope.showErrorMsg(totas[0].body.msgData);
		               	}
		               	if (totas.length > 0) {
		               		$scope.events = [];
			               	
		                	// --輔銷
		                	if($scope.type != "3"){
		                		//時間
		                		angular.forEach(totas[0].body.resultList, function(row, index, objs){
			                		if (row.TYPE == '1') {
			                			if (row.START_TIME == "1") {
			               					var start = $scope.toJsDate(row.ONSITE_DATE);
					               				start.setHours(start.getHours()+8);
					               			var end = $scope.toJsDate(row.ONSITE_DATE);
					                			end.setHours(end.getHours()+12);
			                			}else {
				                			var start = $scope.toJsDate(row.ONSITE_DATE);
					                			start.setHours(start.getHours()+13);
					                		var end = $scope.toJsDate(row.ONSITE_DATE);
					                			end.setHours(end.getHours()+17);
			                			}
			                		}else{
			            				if(row.ONSITE_DATE){
			            					var start = $scope.toJsDate(row.ONSITE_DATE);
			            					if(row.START_TIME){
			            						start.setHours(Number(row.START_TIME.substr(0,2)));
					                			start.setMinutes(Number(row.START_TIME.substr(2,4)));
			            					}
			            					var end = $scope.toJsDate(row.ONSITE_DATE);
			                				if(row.END_TIME){
			                					end.setHours(Number(row.END_TIME.substr(0,2)));
					                			end.setMinutes(Number(row.END_TIME.substr(2,4)));
			            					}
			            				}
			                		}
			                		
			                		/**輔銷行事曆會議記錄 理專的輔銷行事曆不再顯示分行駐點**/
			                		if(row.TYPE != '1'){
			                			$scope.events.push({'function_type': row.TYPE, 'seq': row.SEQ, 'title':row.TITLE, 'startsAt':start,
	        								'endsAt':end, 'type':'info', 'period':row.ONSITE_PERIOD,
	        								'onsite_date':row.ONSITE_DATE, 'onsite_brh': row.ONSITE_BRH});
			                		}
								});
		                	}
		                	// --理專
		                	else{
		                		$scope.resultList = []; 
					          	$scope.resultList = totas[0].body.resultList;
					          	$scope.events = [];
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
				               			
				                		var TITLE = "輔銷人員 駐點時間  : " + start + " - " + end;
				                		//STATUS = 2 :待覆核
				                		if(row.STATUS == '2'){
				                			TITLE = "輔銷人員變更駐點行";
				                		}
				               			$scope.events.push({'title':TITLE, 'type':'info', 'startsAt':start, 'endsAt':end, 'NAME':row.CUST_ID +"-"+ row.CUST_NAME, 'ddate':$scope.toJsDate(row.DATETIME)});
				               		}
				               	});
		                	}
		               	};
		               	
				});
				$scope.timespanClicked($scope.viewDate);
			})
			
		};
		
        
        //輔銷科長查詢
		$scope.faia_query = function () {
			/*		最初進入	type = undefined	emp = userid
					請選擇	type = 1			emp = userid
					FA/IA	type = 2			emp = inputemp	*/
			if ($scope.inputVO.emp_id == "" || $scope.inputVO.emp_id == undefined) {
				$scope.inputVO.faia_type = "1";
				$scope.inputVO.emp_id = "";
			} else {
				$scope.inputVO.faia_type = "2";
				$scope.emp_id = $scope.inputVO.emp_id;
			}	
			$scope.inputVO.date = $scope.viewDate
			
			
			$scope.init();
		}
		
        // change view
        $scope.changeView = function(data) {
        	$scope.calendarView = data;
        };
        
        // viewclick 點擊橫列時間
        $scope.viewclick = function() {
        	if ($scope.calendarView == 'year') {
        		$scope.calendarView = 'month';
        	} else if ($scope.calendarView == 'month'){
        		return false;
        	}
        };
        
        //點擊日期
        $scope.timespanClicked = function(date) {
        	$scope.viewDate = date;
        	
        	if($scope.type != "3"){
	        	$scope.ans = '';
	            $scope.ans2 = '';
	            $scope.ans3 = '';
	            $scope.ans4 = '';
	        	$scope.expiry_FAIA(date);     	
        	}
        	if($scope.type == "3"){
	            $scope.ansAO = ''; 
	        	$scope.expiry(date);     	
        	}
        }
        
        /**=============================================================================================================**
		 **													輔銷右側事項查詢													**		
		 **=============================================================================================================**/			
        $scope.expiry_FAIA = function (date){
			$scope.inputVO.task_date = date;
			$scope.inputVO.emp_id = projInfoService.getUserID();
			
			//--輔銷提醒
    		$scope.remindList = [];
			$scope.sendRecv("CRM124", "edit_remind_inquire", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {'task_date':$scope.inputVO.task_date,'emp_id':$scope.inputVO.emp_id},
				function(totas, isError) {
					if (!isError) { 		
						if (totas[0].body.edit_remind_List.length > 0) {
							$scope.remindList = totas[0].body.edit_remind_List;
							$scope.outputVO = totas[0].body;
						}
						return;
					}
			});
			
			//--輔銷待辦事項		
			$scope.remindList2 = [];
			$scope.sendRecv("CRM121", "getTodoDtl", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {'date':$scope.inputVO.task_date,'privilege':$scope.inputVO.privilege},
				function(totas, isError) {
					if (isError) {
		           		$scope.showErrorMsg(totas[0].body.msgData);
		           	}	
					if (totas[0].body.MyTodoLst.length > 0) {
						$scope.remindList2 = totas[0].body.MyTodoLst;
						$scope.outputVO = totas[0].body;
						return;
					}
			});
			
			//--陪訪紀錄
			$scope.remindList3 = [];
			$scope.sendRecv("CRM1241", "inquire", "com.systex.jbranch.app.server.fps.crm1241.CRM1241InputVO", {'viewDate':$scope.inputVO.task_date,'emp_id':$scope.emp_id,'querytype':$scope.inputVO.faia_type},
				function(totas, isError) {
					if (isError) {
		           		$scope.showErrorMsg(totas[0].body.msgData);
		           	}	
					if (totas[0].body.resultList.length > 0) {
						$scope.remindList3 = totas[0].body.resultList;
						$scope.outputVO = totas[0].body;
						//覆核狀態
						angular.forEach($scope.remindList3, function(row){
							if (row.STATUS == '4C') {
								row.STATUS_TITLE = '(已取消)';
							}
						});
						
						return;
					}
			});	
			
			//#0003359 : 輔銷行事曆顯示理專的代辦事項，用CRM122理專行事曆的方法
			$scope.remindList4 = [];
			$scope.inputVO.date = date;
			if($scope.inputVO.AOsList.length > 0) {
				$scope.sendRecv("CRM122", "getTodoDtl", "com.systex.jbranch.app.server.fps.crm122.CRM122InputVO", {'AOsList':$scope.inputVO.AOsList , 'date':$scope.inputVO.date , 'EMP_ID':$scope.inputVO.EMP_ID},
						function(totas, isError) {
							if (isError) {
								$scope.showErrorMsg(totas[0].body.msgData);
							}
							if (totas[0].body.MyTodoLst!=null && totas[0].body.MyTodoLst.length > 0) {
								$scope.remindList4 = totas[0].body.MyTodoLst;
							}
				});				 	
			}
		};
        
        /**=============================================================================================================**
		 **													理專右側事項查詢													**		
		 **=============================================================================================================**/			
    	$scope.expiry = function (date){
			$scope.inputVO.date = date;
			
			//--理專檢視輔銷駐點分行
    		$scope.MyTodoLst = [];
			$scope.sendRecv("CRM124", "ao_code_Query", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {'date':$scope.inputVO.date,'privilege':$scope.inputVO.privilege},
				function(totas, isError) {
					if (isError) {
		           		$scope.showErrorMsg(totas[0].body.msgData);
		           	}	
					if (totas[0].body.resultList.length > 0) {
						$scope.MyTodoLst = totas[0].body.resultList;
						angular.forEach($scope.MyTodoLst, function(row, index, objs){
							if(row.STATUS == '2'){
								if(row.NEW_ONSITE_BRH == row.ONSITE_BRH){
									row.TITLE = "輔銷人員 " + row.ROLE_NAME + " " + row.EMP_NAME + " 變更(待覆核) 原駐點時間 : " + row.STIME + " - " + row.ETIME + "變更為: " + row.NEW_ONSITE_DATE + row.NEW_ONSITE_PERIOD;
								}else{
									row.TITLE = "輔銷人員 " + row.ROLE_NAME + " " + row.EMP_NAME + " 變更(待覆核) 原駐點時間 : " + row.STIME + " - " + row.ETIME + "變更駐點行至: " + row.NEW_BRA;
								}
								
							}else{
								row.TITLE = "輔銷人員 " + row.ROLE_NAME + " " + row.EMP_NAME + " 駐點時間 : " + row.STIME + " - " + row.ETIME;
							}
						});
						$scope.outputVO = totas[0].body;
						return;
					}
			});
		};	
        
        
		 /**=============================================================================================================**
		 **													輔銷新增按鈕													**		
		 **=============================================================================================================**/			
        // add_remind 新增提醒
        $scope.add_remind = function (){
        	var FAIAtype = '';
        	if($scope.type == "1"){
        		FAIAtype = 'FAIA9';
        	}
        	if($scope.type == "2"){
        		FAIAtype = 'FAIA';
        	}
        	
        	var date = $scope.viewDate1;
        	var dialog = ngDialog.open({
        		template: 'assets/txn/CRM121/CRM121_INSERT.html',
        		className: 'CRM121_INSERT',
        		showClose: false,
        		controller: ['$scope', function($scope) {
        			$scope.date = date;
        			$scope.FAIAtype = FAIAtype;
        		}]	
        	});
        	dialog.closePromise.then(function (data) {
        		//待辦事項
        		if(data.value === 'T'){
        			$scope.connector('set', "CRM122_EMPID",sysInfoService.getUserID());
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
    	 				$scope.init();
    	 			});
        		}
        		//輔銷提醒
        		if(data.value === 'R'){
        			var date = $scope.date;
                	var dialog = ngDialog.open({
        				template: 'assets/txn/CRM124/CRM124_ADD_REMIND.html',
        				className: 'CRM124_ADD_REMIND',
        				showClose: false,
//                        controller: ['$scope', function($scope) {
//                        	$scope.row = row;
//                        }]
        			});
                	dialog.closePromise.then(function (data) {
        				if(data.value === 'successful'){
        					$scope.init();
        				}
        			});
        		}
			}); 
        };
		      
		//客戶首頁
		$scope.cust_home = function (row) {
			$scope.sendRecv("CRM110", "inquire", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", {'cust_id':row},
					function(tota110, isError) {	
				if(tota110[0].body.resultList.length == 1) {
					var path = '';					
					$scope.CRM_CUSTVO = {
						CUST_ID :  tota110[0].body.resultList[0].CUST_ID,
						CUST_NAME :tota110[0].body.resultList[0].CUST_NAME
					}
					$scope.connector('set','CRM_CUSTVO',$scope.CRM_CUSTVO)
					path = "assets/txn/CRM610/CRM610_MAIN.html";					
					$scope.connector("set","CRM610URL",path);
					var dialog = ngDialog.open({
						template: 'assets/txn/CRM610/CRM610.html',
						className: 'CRM610',
						showClose: false
					});
					return;
				}			
			});
		}
		
		/**=============================================================================================================**
		 **													右側事項勾選													**		
		 **=============================================================================================================**/			
		
		//提醒
		$scope.toggleSelection_1 = function  toggleSelection_1(data) {
			var ans = $scope.remindList.filter(function(obj){
				return (obj.SELECTED == true);
	    	});		         
			$scope.ans = ans;
			if(ans.length > 0){
				$scope.seq_no = ans[0].SEQ_NO;
				//準備好編輯用資料
				$scope.inputVO.updateType ='R1';
				$scope.updateType = $scope.inputVO.updateType;
				angular.forEach($scope.remindList, function(row, index, objs){
					if(row.SEQ_NO == $scope.seq_no){ 
						$scope.inputVO.taskDo = row;
					}
				});
				
			}
			
			$scope.row = [];
			$scope.row = $scope.inputVO.taskDo;
			$scope.seq_no ='';		
		}
		
		//待辦事項
		$scope.toggleSelection_2 = function  toggleSelection_2(data) {
			var ans2 = $scope.remindList2.filter(function(obj){
				return (obj.SELECTED == true);
	    	});
			$scope.ans2 = ans2;
			if(ans2.length > 0){
				$scope.seq_no = ans2[0].SEQ_NO;
				//準備好編輯用資料
				$scope.inputVO.updateType ='R2';
				$scope.updateType = $scope.inputVO.updateType;
				angular.forEach($scope.remindList2, function(row, index, objs){
					if(row.SEQ_NO == $scope.seq_no){ 
						$scope.inputVO.taskDo = row;
					}
				});
			}		          
			
			$scope.row = [];
			$scope.row = $scope.inputVO.taskDo;
			$scope.seq_no ='';
		}
		
		//陪訪紀錄
		$scope.toggleSelection_3 = function  toggleSelection_3(data,type) {
			
			$scope.seq_no = data;
			//準備好編輯用資料
			$scope.inputVO.updateType ='R3';
			$scope.updateType = $scope.inputVO.updateType;
			angular.forEach($scope.remindList3, function(row){
				if(row.SEQ == $scope.seq_no){ 
					$scope.inputVO.taskDo = row;				
				}
			});	          
			
			$scope.row = [];
			$scope.row = $scope.inputVO.taskDo;
			
			/**type = 1 : 編輯 or 檢視
			 * type = 2 : 完成
			 * type = 3 : 取消
			 * **/			
			if (type == '1') {
				$scope.edit_remind($scope.row,$scope.inputVO.updateType);
			}
			if (type == '2') {
				$scope.confirm($scope.row);
			}
			if (type == '3') {
				$scope.cancel($scope.row);
			}
			
			$scope.seq_no ='';
			
		}
		
		//待辦事項-理專
		$scope.toggleSelection_4 = function  toggleSelection_4(data) {
			var ans4 = $scope.remindList4.filter(function(obj){
				return (obj.SELECTED == true);
	    	});
			$scope.ans4 = ans4;
			if(ans4.length > 0){
				$scope.seq_no = ans4[0].SEQ_NO;
				//準備好編輯用資料
				$scope.inputVO.updateType ='R2';
				$scope.updateType = $scope.inputVO.updateType;
				angular.forEach($scope.remindList4, function(row, index, objs){
					if(row.SEQ_NO == $scope.seq_no){ 
						$scope.inputVO.taskDo = row;
					}
				});
			}		          
			
			$scope.row = [];
			$scope.row = $scope.inputVO.taskDo;
			$scope.seq_no ='';
		}
		
		//輔銷駐點變更通知
		$scope.toggleSelection_AO = function  toggleSelection_AO(data) {		
			var ansAO = $scope.MyTodoLst.filter(function(obj){
				return (obj.SELECTED == true);
	    	});		         
			$scope.ansAO = ansAO;
			if(ansAO.length > 0){
				$scope.seq_no = ansAO[0].SEQ_NO;
			}	
			
			//準備好編輯用資料
			$scope.inputVO.updateType ='C';
			$scope.updateType = $scope.inputVO.updateType;
			angular.forEach($scope.MyTodoLst, function(row, index, objs){
				if(row.SEQ_NO == $scope.seq_no){ 
					$scope.inputVO.taskDo = row;
				}
			});
			$scope.row = [];
			$scope.row = $scope.inputVO.taskDo;
			$scope.seq_no ='';
		};
		
		
		/**=============================================================================================================**
		 **													輔銷右側編輯按鈕													**		
		 **=============================================================================================================**/
		
		$scope.edit_remind = function (row,updateType) {
			$scope.updateType = updateType.toString();
			//編輯-提醒
			if($scope.updateType == 'R1'){
				var dialog = ngDialog.open({
					template: 'assets/txn/CRM124/CRM124_EDIT_REMIND.html',
					className: 'CRM124_EDIT_REMIND',
					showClose: false,
					controller: ['$scope', function($scope) {
						$scope.row = row;
					}]
				});
				dialog.closePromise.then(function (data) {
					if(data.value === 'successful'){
						$scope.init();
					}
				});
			}
			
			//編輯-待辦事項
			if($scope.updateType == 'R2'){
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
		       			$scope.init(); 					
		  			}
	 	  		});
			}
			
			//編輯-陪訪紀錄
			if($scope.updateType == 'R3'){
				var dialog = ngDialog.open({
					template: 'assets/txn/CRM1241/CRM1241_RECORD.html',
					className: 'CRM1241_RECORD',
					showClose: false,
	                controller: ['$scope', function($scope) {
	                	$scope.row = row;
	                }]
				});
	        	dialog.closePromise.then(function (data) {
					if(data.value === 'successful'){
	  					$scope.init(); 
					}
				});
			}		
		};
		
		/**=============================================================================================================**
		 **													陪訪記錄其他按鈕													**		
		 **=============================================================================================================**/
		
		//完成-輔銷陪訪紀錄
		$scope.confirm = function (row) {
			$scope.inputVO.seq = row.SEQ;
			$scope.sendRecv("CRM1241", "confirm", "com.systex.jbranch.app.server.fps.crm1241.CRM1241InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota[0].body.msgData);
							return;
						}
						if (tota.length > 0) {
							$scope.showMsg('儲存成功');
							$scope.init();
						}
			});
		}
		
		//取消-輔銷陪訪紀錄
		$scope.cancel = function (row) {
			$scope.sendRecv("CRM124", "delete_visit", "com.systex.jbranch.app.server.fps.crm124.CRM124InputVO", {'seq':row.SEQ},
    			function(tota, isError) {
                   	if (isError) {
                   		$scope.showErrorMsg(tota[0].body.msgData);
                   	}else{
                   		$scope.showSuccessMsg('ehl_01_common_004');
                   		$scope.init();
                   	} 	 
    		});
		}
		
		/**=============================================================================================================**
		 **													理專右側檢視按鈕													**		
		 **=============================================================================================================**/
		
		//檢視-輔銷駐點變更通知
        $scope.AO_check = function (row,updateType){
        	$scope.updateType = updateType.toString();
        	if($scope.updateType == 'C'){
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
	  					$scope.init(); 
		  			}
	 	  		});
	        }
        }	

		/**=============================================================================================================**
		 **													輔銷右側刪除按鈕													**		
		 **=============================================================================================================**/
		
        $scope.deleTack = function (row) {      	
        	//提醒
        	var ans = $scope.remindList.filter(function(obj){
				return (obj.SELECTED == true);
	    	});	
        	//待辦事項
        	var ans2 = $scope.remindList2.filter(function(obj){
				return (obj.SELECTED == true);
	    	});		
        	
        	if(ans.length == 0 && ans2.length == 0){
        		return;
        	} 	
        	
        	$confirm({text:  '是否確定要刪除?'}, {size: '200px'}).then(function() {	
	        	$scope.sendRecv("CRM121", "delTodo", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {'chkCodedata': ans2,'chkCode_2data': ans},
	     			function(tota, isError) {
	        			if (isError) {
	     					$scope.showErrorMsg(tota[0].body.msgData);
	     				}   	        			
	     				if(tota.length > 0) {
	     					$scope.showSuccessMsg('ehl_01_common_003');
	     					$scope.init();
	     				}else{
	     					$scope.showErrorMsg("ehl_01_common_005");
	     				}
	     		});
        	});
        }
		/**=============================================================================================================**
		 **													跨頁重整整理方法													**		
		 **=============================================================================================================**/
        $scope.init(); 
//        $scope.$on("CRM124.init", function(event) {
//        	$scope.init();  
//		});
		
});
