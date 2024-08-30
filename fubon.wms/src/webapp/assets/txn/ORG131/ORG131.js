/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG131Controller', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "ORG131Controller";

	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	$scope.priID = sysInfoService.getPriID();
    
	/**=========================================初始化================================================================**/
	$scope.initial = function(){
		$scope.data = [];
		$scope.resultList2 = [];
		
		$scope.inputVO = { 
			region_center_id : '',
			branch_area_id  : '',
			branch_nbr      : '',
			intv_emp_id		:'',
			intv_emp_name	:'',
			recv_case_sdate	:undefined,
			recv_case_edate	:undefined,
			book_onbo_sdate	:undefined,
			book_onbo_edate	:undefined,
			cust_id			:'',
			status			:'',
			roleID			:'',
			mroleID			:'',
			seqno			:'',
			login_id		:'',
			isSupervisor:false,
			isHeadMgr:false
		}
		
        //組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
	};
	$scope.initial();
			
	$scope.init = function(){
		$scope.inputVO.login_id = sysInfoService.getUserID();
		$scope.inputVO.roleID =  sysInfoService.getRoleID();
		$scope.sendRecv("ORG131", "initial", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
				function(tota, isError) {
				   if(!isError){
				        	 				
						$scope.inputVO.rc_id = tota[0].body.resultList[0].REGION_CENTER_ID;
		            	$scope.inputVO.op_id = tota[0].body.resultList[0].BRANCH_AREA_ID;
		            	$scope.inputVO.br_id = tota[0].body.resultList[0].BRANCH_NBR;
		            	$scope.inputVO.intv_emp_id = tota[0].body.resultList[0].INTV_EMP_ID;
		            	$scope.inputVO.intv_emp_name =  tota[0].body.resultList[0].INTV_EMP_NAME;
				   }
			});
		
		$scope.sendRecv("ORG131", "login", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
				function(tota, isError) {
			 		if(!isError){  
												
					   $scope.inputVO.mroleID = tota[0].body.roleID[0].COUNTS;
				        $scope.inputVO.isHeadMgr    =   tota[0].body.isHeadMgr;
                        $scope.inputVO.isSupervisor =   tota[0].body.isSupervisor;
				
					}
		   })
			
	};
	
	$scope.init();

/**=================================================================查詢/匯出/新增==============================================================================**/
		$scope.query = function (){
			
			$scope.sendRecv("ORG131", "inquire", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO", $scope.inputVO,
				function(tota, isError) {
	        	    if(!isError){  
						if (isError) {
		                		$scope.showErrorMsg(tota[0].body.msgData);
		                		
		                	}
		                	if (tota[0].body.resultList2.length == 0) {
		                		$scope.showMsg("ehl_01_common_009");
		                	};
		                	$scope.mappingSet['set']=[];
		                	angular.forEach(tota[0].body.resultList2, function(row, index, objs){
							
		                	});
		                	$scope.resultList2 = tota[0].body.resultList2;
		                	$scope.csvList = tota[0].body.csvList;
		                	$scope.outputVO = tota[0].body;
					}else{
						$scope.showBtn = 'none';
					}		
				});
	      };
	    //匯出  
	      $scope.exportRPT = function(){
				$scope.sendRecv("ORG131", "export",	"com.systex.jbranch.app.server.fps.org131.ORG131OutputVO",{'list':$scope.csvList},
						function(tota, isError) {
							if (!isError) {
						
								$scope.resultList5 = tota[0].body.resultList5;
								$scope.outputVO = tota[0].body;
							
								return;
							}
						});
				};

	    $scope.action = function(row, action){
	    	var mroleID = $scope.inputVO.mroleID;
	    	var isHeadMgr = $scope.inputVO.isHeadMgr;
	    	//編輯資料
	    	if(action =='A'){
	    		var dialog = ngDialog.open({
	    			template: 'assets/txn/ORG131/ORG131_HIRE.html',
	    			className: 'ORG131_HIRE',
	    			showClose: false, 
	    			controller: ['$scope', function($scope) {
	    				$scope.row = row;
	    				$scope.mroleID = mroleID;
	    				$scope.isHeadMgr = isHeadMgr;
	    			}]
	    		
	    		});
	    		dialog.closePromise.then(function(data) {
	    			if(data.value === 'successful'){//新增時
	    				$scope.query();
	    			}
	    		});
		      };
		     //詳細資料
		      if(action == 'B'){
		    	  var dialog = ngDialog.open({
						template: 'assets/txn/ORG131/ORG131_DETAIL.html',
						className: 'ORG131_DETAIL',
						showClose: false, 
						 controller: ['$scope', function($scope) {
			                	$scope.row = row;
			                }]
		                
					});  
		      };

		      //面談評估表
		      if(action == "C"){
		    	
			    	  var dialog = ngDialog.open({
							template: 'assets/txn/ORG131/ORG131_AS.html',
							className: 'ORG131_AS',
							showClose: false, 
							 controller: ['$scope', function($scope) {
				                	$scope.row = row;
				                }]
			               
						});
			    	  	dialog.closePromise.then(function(data) {
			  				if(data.value === 'successful'){//新增時
			  					$scope.query();
			  			}
			  		});
			    };
            
			     //金控版面談評估表
			     if(action == "D"){
			         var dialog = ngDialog.open({
				        template: 'assets/txn/ORG131/ORG131_SAS.html',
						className: 'ORG131_SAS',
						showClose: false, 
						controller: ['$scope', function($scope) {
				            $scope.row = row;
				        }]
			               
					   });
			    	   dialog.closePromise.then(function(data) {
			  				if(data.value === 'successful'){//新增時
			  					$scope.query();
			  			    }
			  		   });
			     };
	  
	    };
        //20180626加入刪除功能
        $scope.deleteData = function(row){
        	$confirm({text: '是否刪除此筆資料!!'},{size: 'sm'}).then(function(){
            	$scope.sendRecv("ORG131","delete","com.systex.jbranch.app.server.fps.org131.ORG131InputVO",
            			{"cust_id":row.CUST_ID , "branch_nbr":row.BRANCH_NBR, "seq":row.SEQ},function(tota,isError){
            			if (tota.length > 0) {
                			$scope.init();
                    		$scope.query();
    	            		$scope.showSuccessMsg('刪除成功');
            			}
            	});
        	});
        }
	      
	    //新增
		$scope.insert = function(){
			 var dialog = ngDialog.open({
					template: 'assets/txn/ORG131/ORG131_INSERT.html',
					className: 'ORG131_INSERT',
					showClose: false, 
				});
			 dialog.closePromise.then(function(data) {
	  				if(data.value === 'successful'){//新增時
	  					$scope.query();
	  			}
			 });
		};
		
		// 2017/4/19 empty and empty2 to one
		$scope.download = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/ORG131/ORG131_DOWNLOAD.html',
				className: 'ORG131_DOWNLOAD',
				showClose: false,
                controller: ['$scope', function($scope) {
                }]
			});
		};
		
		//核可
		$scope.review = function (row){
			$scope.inputVO.seqno = row.SEQNO;
			$scope.inputVO.cust_id = row.CUST_ID;
			$scope.inputVO.branch_nbr = row.BRANCH_NBR;
			$scope.inputVO.seq = row.SEQ;
			$confirm({text: '是否核可：' + row.CUST_ID +'-'+ row.EMP_NAME + '進用資料?'}, {size: '200px'}).then(function() {
				$scope.sendRecv("ORG131", "review", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
					function(tota, isError) {
				
					if(!isError){
						
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
							$scope.showMsg("ehl_01_common_007");
						}
						$scope.query();
					}
			 	});
			});
			
		};
		//退回
		$scope.reback = function(row){
			var seqno = row.SEQNO;
			var cust_id = row.CUST_ID;
			var branch_nbr = row.BRANCH_NBR;
			var seq = row.SEQ;
			var emp_id = row.EMP_NAME;
			var result = row.RT_RESULT;

			var dialog = ngDialog.open({
				template: 'assets/txn/ORG131/ORG131_REVIEW.html',
				className: 'ORG131_REVIEW',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.seqno = seqno;
                	$scope.cust_id = cust_id;
                	$scope.branch_nbr = branch_nbr;
                	$scope.seq = seq;
                	$scope.emp_id = emp_id;
                	$scope.result = result;
                }]
			}).closePromise.then(function (data) {
				$scope.query();
			});
		};
		
	
		/**========================================下拉式選單連動==========================================================**/
		 
		$scope.genRegion = function() {
			$scope.mappingSet['region'] = [];
			angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){						
				$scope.mappingSet['region'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});										
			});
			
        };
        $scope.genRegion();
		
        //營運區資訊
		$scope.regionChange = function() {	
			$scope.mappingSet['op'] = [];
			angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){	
				if(row.REGION_CENTER_ID == $scope.inputVO.rc_id)
				$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});				
			});	
			if ($scope.inputVO.rc_id != $scope.region) {
				$scope.mappingSet['branch'] = [];
				$scope.inputVO.op_id = "";
				$scope.inputVO.br_id = "";
				
			}
			
        };
        $scope.regionChange();
        
        
        //分行資訊
		$scope.areaChange = function() {
			$scope.mappingSet['branch'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){				
				if(row.BRANCH_AREA_ID == $scope.inputVO.op_id)			
					$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});				
        }; 
        $scope.areaChange();
       
        
        $scope.mappingSet['black']=[];
		$scope.mappingSet['black'].push({LABEL: '是', DATA: 'Y'}, {LABEL:'否', DATA:'N'});
        
		$scope.mappingSet['hire_status']=[];
		$scope.mappingSet['hire_status'].push({LABEL: '無', DATA: '0'}, {LABEL:'通過', DATA:'1'}, {LABEL:'不通過', DATA:'2'}, {LABEL:'轉介至他行', DATA:'3'},
				 {LABEL:'暫存', DATA:'4'});
	
		/**=================================================DATE============================================================**/
        //進件日期開始
        $scope.recv_case_sDateOptions = {
        		maxDate: $scope.maxDate,
          		minDate: $scope.minDate
    	    };
        //進件日期結束
        $scope.recv_case_eDateOptions = {
        		maxDate: $scope.maxDate,
          		minDate: $scope.minDate
    	    };
        //預定報到日起開始
        $scope.book_onbo_sDateOptions = {
        		maxDate: $scope.maxDate,
          		minDate: $scope.minDate
    	    };
        
        //預定報到日起結束
        $scope.book_onbo_eDateOptions = {
        		maxDate: $scope.maxDate,
          		minDate: $scope.minDate
    	    };
        //config
		$scope.model = {};
		
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.recv_case_sDateOptions.maxDate = $scope.inputVO.recv_case_edate || $scope.maxDate;
			$scope.recv_case_eDateOptions.minDate = $scope.inputVO.recv_case_sdate || $scope.minDate;
		};
		$scope.limitDate1 = function() {
			$scope.book_onbo_sDateOptions.maxDate = $scope.inputVO.book_onbo_edate || $scope.maxDate;
			$scope.book_onbo_eDateOptions.minDate = $scope.inputVO.book_onbo_sdate || $scope.minDate;
		};
        
		
});