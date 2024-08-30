/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR004_EditController', 
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR004_EditController";
        
        $scope.initOpti = function() {
        	$scope.inputVO = {
        			scheduleid: '',				// 排程代號
        			schedulename: '',			// 排程名稱
        			description: '',			// 排程說明
        			cronexpression: null,		// 執行週期
        			calendar: '',				// 套用行事曆
        			scheduleparameter: '',		// 參數
        			isuse: 'N',					// 是否啟用
        			processor: '',				// 執行主機
        			executetime: null,			// 預計合理時間
        			startjob: 1,				// 起始JOB
        			isscheduled:'N',
        			lasttry:null				// 最後一次異動時間
        	};
        }
        
        $scope.init = function(){
        	
        	$scope.lstTotalJob = [];
        	$scope.lstChoiceJob = [];
        	$scope.displayList = []
        	$scope.searchText = '';
        	var lstChoiceJob =[];
        	
        	if($scope.row){
        		$scope.isNotCreate = true
        	}
        	
        	$scope.row = $scope.row || {};
        	
        	if($scope.row.SCHEDULEID != null) {
        		
	        	$scope.inputVO={
	        			hlbscheduleid:$scope.row.SCHEDULEID
	        	};
	        	
         		$scope.sendRecv("CMMGR004", 'query', "com.systex.jbranch.app.server.fps.cmmgr004.CMMGR004QueryInputVO", $scope.inputVO,
	        			function(totas, isError) {
	    	                if (isError) {
	    	                	$scope.showErrorMsgInDialog(totas.body.msgData);
	    	                    return;
	    	                }
//	    	                	alert('resultList : ' + JSON.stringify(totas[0].body.resultList[0]));
	    	                	var resultList = totas[0].body.resultList[0];
	    	                	lstChoiceJob = totas[0].body.lstChoiceJob;
	    	                	$scope.inputVO.scheduleid = resultList.SCHEDULEID;
	    	            		$scope.inputVO.schedulename = resultList.SCHEDULENAME;
	    	            		$scope.inputVO.description = resultList.DESCRIPTION;
	    	            		$scope.inputVO.cronexpression = resultList.CRONEXPRESSION;
	    	            		$scope.inputVO.processor = resultList.PROCESSOR;
	    	            		$scope.inputVO.scheduleparameter = resultList.SCHEDULEPARAMETER;
	    	            		$scope.inputVO.lasttry = resultList.LASTTRY;
	    	            		$scope.inputVO.isscheduled = resultList.ISSCHEDULED;
	    	            		$scope.inputVO.startjob = resultList.JOBSTART;
	    	            		$scope.inputVO.isuse = resultList.ISUSE;
	    	            		$scope.inputVO.executetime = resultList.EXECUTETIME;
	    	            		$scope.inputVO.calendar = resultList.CALENDAR_PROVIDER_ID;
	    	            		$scope.lstChoiceJob = lstChoiceJob ;
	    	            		//*** 刪除重複的lstChoiceJob ***
	    	            		for(var i = 0; i < $scope.lstTotalJob.length ; i++) {
	    	            			for(var j = 0; j < $scope.lstChoiceJob.length ; j++) {
	    	            				if($scope.lstTotalJob[i].JOBID == $scope.lstChoiceJob[j].JOBID) {
	    	            					$scope.lstTotalJob.splice(i, 1);
	    	            				}
	    	            			}
	    	            		}
	    	            }
         		);
        	} else {
        		$scope.initOpti();
        	}
        	
        	$scope.sendRecv("CMMGR004", 'queryJob', "com.systex.jbranch.app.server.fps.cmmgr004.CMMGR004QueryJobOutputVO", $scope.inputVO,
        			function(totas, isError) {
    	                if (isError) {
    	                	$scope.showErrorMsgInDialog(totas.body.msgData);
    	                    return;
    	                }
        				
    	                $scope.predicate = 'JOBID';
    	                $scope.lstTotalJob =  totas[0].body.lstTotalJob;
    	                $scope.displayList = angular.copy($scope.lstTotalJob);
//    	                alert('initin :' + JSON.stringify($scope.lstTotalJob));
        	})
        };
        
        $scope.init();
        
        $scope.save = function(){
        	$scope.inputVO.joblist = angular.copy($scope.lstChoiceJob);
        	if($scope.inputVO.scheduleid == undefined) {
		       		 $scope.showMsg('ehl_02_common_002',["排程代號"]);
		       		 return;
	       	}else if($scope.inputVO.cronexpression == undefined) {
		       		 $scope.showMsg('ehl_02_common_002',["執行週期"]);
		    		 return;
	       	}else if($scope.inputVO.isuse == undefined) {
		       		 $scope.showMsg('ehl_02_common_002',["是否啟用"]);
		    		 return;
	       	}else if($scope.inputVO.processor == undefined) {
		       		 $scope.showMsg('ehl_02_common_002',["執行主機"]);
		    		 return;
	       	}else {
	        	$scope.inputVO.type = 'Update';
	        	if($scope.row.SCHEDULEID == undefined){
	        		$scope.inputVO.type = 'Create';
	        	}
	        	$scope.sendRecv("CMMGR004", 'operationIT01', "com.systex.jbranch.app.server.fps.cmmgr004.CMMGR004IT01InputVO", $scope.inputVO,
	    			function(totas, isError) {
		                if (isError) {
		                	$scope.showErrorMsgInDialog(totas.body.msgData);
		                    return;
		                }else {
		                	 $scope.showMsg('ehl_01_common_002');
			       			$scope.closeThisDialog('successful');
		                }
		            });
	       	}
        }
        
        $scope.del = function(){
    		$scope.inputVO.type = 'Delete';
    		$scope.sendRecv("CMMGR004", "operationIT01", "com.systex.jbranch.app.server.fps.cmmgr004.CMMGR004IT01InputVO", $scope.inputVO,
	    			function(totas, isError) {
		                if (isError) {
		                	$scope.showErrorMsgInDialog(totas.body.msgData);
		                    return;
		                }
		                 if (totas.length > 0) {
		                	$scope.showMsg('ehl_01_common_003');
			       			$scope.closeThisDialog('successful');
		                 };
		            }
	        );
        }
        
        $scope.btnAddChange = function() {
        	for (var i = $scope.lstTotalJob.length - 1; i >= 0; i--) {
        		angular.forEach($scope.totalJob, function(element){
        			if ($scope.lstTotalJob[i].JOBID == element.JOBID) {
            			$scope.lstChoiceJob.push(element);
            			$scope.lstTotalJob.splice(i, 1);
            		}
        		});
        	}
        	
        	//如果搜尋欄有值，清空搜尋欄並重新display 目前還未選擇的job
        	if ($scope.searchText.trim()) {
        		$scope.searchText = '';
        	}
        	
        	//重新display 目前還未選擇的job
        	$scope.searchJob();
        };
        
     
        $scope.btnDelChange = function() {
        	for (var i = $scope.lstChoiceJob.length - 1; i >= 0; i--) {
        		if ($scope.lstChoiceJob[i].JOBID == $scope.currentDetailRow.JOBID) {
        			$scope.lstTotalJob.push($scope.lstChoiceJob[i]);
        			$scope.lstChoiceJob.splice(i, 1);
        		}
        	}
        	
        	//如果搜尋欄有值，清空搜尋欄
        	if ($scope.searchText.trim()) {
        		$scope.searchText = '';
        	}
        	
        	//重新display 目前還未選擇的job
        	$scope.searchJob();
        };
        
        $scope.show = function(row) {
        	$scope.currentDetailRow = row;
        };
        
        $scope.iniRoles = function(){
        	socketService.sendRecv("CMMGR004", "getProcessList", "com.systex.jbranch.app.server.fps.cmmgr004.CMMGR004ProcessorOutputVO", {})
    		.then(
    		function(oResp) {
    			$scope.mappingSet['processor'] = [];
    			angular.forEach(oResp[0].body.processList, function(row, index, objs){
    				$scope.mappingSet['processor'].push({LABEL: row, DATA: row});
    			});
    			projInfoService.mappingSet['processor'] = $scope.mappingSet['processor'];
    		},
    		function(oErr) {
    			$scope.showErrorMsg(oErr);
    		});	
        };
        
        $scope.iniRoles();
        
        $scope.execute = function() {
        	$scope.inputVO.hlbscheduleid = angular.copy($scope.inputVO.scheduleid);
        	$scope.sendRecv("CMMGR004", 'execute', "com.systex.jbranch.app.server.fps.cmmgr004.CMMGR004QueryInputVO",
        			$scope.inputVO,
        			function(totas, isError) {
		              	if (!isError) {
		              		$scope.showMsg("手動執行排程 代號[" + $scope.inputVO.scheduleid + "]");
		              	}
					}
        	);
        };
        
        /*增加搜尋功能，以方便查詢 20170801 add*/
        $scope.searchJob = function() {
        	//檢查有沒有輸入，無輸入則將所有目前尚未選擇的job顯示在UI上
        	var input = $scope.searchText.trim();
        	if (!input){
        		$scope.displayList = angular.copy($scope.lstTotalJob);
        		return;
        	}
        	
        	//配對輸入值，若符合條件將顯示結果於UI上
        	$scope.displayList = 
        		$scope.lstTotalJob.filter((e)=>
        		//每個JOBID 與 JOBNAME 去 ||""，避免無值的情況造成搜尋錯誤
        		(e.JOBID || "").indexOf(input) > -1 ||
        		(e.JOBNAME || "").indexOf(input) > -1); 
        }
        
        /*增加上移功能(已選擇Job)，20170809 add*/
        $scope.moveCurrentRowUp = function(){
        	//如果尚未選擇job，則return
        	if (!$scope.lstChoiceJob.length) {
        		return;
        	}
        	
        	//取得當前index值
        	var curIndex = $scope.lstChoiceJob.map((row)=>row.JOBID)
        						.indexOf($scope.currentDetailRow.JOBID);  
        	//檢核curIndex，如果無值或等於0，不做moveUp動作
        	if (curIndex) {
        		var tmp = $scope.lstChoiceJob[curIndex];
        		$scope.lstChoiceJob[curIndex] = $scope.lstChoiceJob[curIndex-1];
        		$scope.lstChoiceJob[curIndex-1] = tmp;
        	}	
        }
        
        /*增加下移功能(已選擇Job)，20170809 add*/
        $scope.moveCurrentRowDown = function(){
        	//如果尚未選擇job，則return
        	if (!$scope.lstChoiceJob.length) {
        		return;
        	}
        	
        	//取得當前index值
        	var curIndex = $scope.lstChoiceJob.map((row)=>row.JOBID)
        						.indexOf($scope.currentDetailRow.JOBID);  
        	//檢核curIndex，如果在最後一行，不做moveDown動作
        	if (curIndex != $scope.lstChoiceJob.length -1) {
        		var tmp = $scope.lstChoiceJob[curIndex];
        		$scope.lstChoiceJob[curIndex] = $scope.lstChoiceJob[curIndex+1];
        		$scope.lstChoiceJob[curIndex+1] = tmp;
        	}
        } 
	}   
);
