/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG220_UPDATEController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG220_UPDATEController";
		
		/**========================修改初始畫面========================================================**/
		$scope.initial = function(){
			if($scope.row)
				$scope.isUpdate = true;
			
			$scope.inputVO = {
				 	RPT_TYPE:'',
				 	emp_id: '',
					emp_id_txt:'',
					sCreDate: undefined,
					eCreDate: undefined,
					sTime:'',
					eTime:'',
					agent_desc:'',
					emp_rc_id:'',
					emp_op_id:'',
					emp_br_id:'',
					emp_ao_id:'',
					dept_id :'',
					agent_dept_id:'',
					roleid:'',
					mroleid:'',
					emp_login_id:''
			};
			
			if ($scope.isUpdate){
				$scope.inputVO.RPT_TYPE = 'ORG';
				$scope.inputVO.rc_id = $scope.row.REGION_CENTER_ID;
				$scope.inputVO.op_id = $scope.row.BRANCH_AREA_ID;
				$scope.inputVO.br_id = $scope.row.BRANCH_NBR;
				$scope.inputVO.emp_id = $scope.row.AGENT_ID;
				$scope.inputVO.dept_id = $scope.row.DEPT_ID;
				$scope.inputVO.emp_rc_id = $scope.row.REGION_CENTER_ID;
				$scope.inputVO.emp_op_id = $scope.row.BRANCH_AREA_ID;
				$scope.inputVO.emp_br_id = $scope.row.BRANCH_NBR;
				$scope.inputVO.sCreDate = $scope.toJsDate($scope.row.START_DATE);
				$scope.inputVO.eCreDate = $scope.toJsDate($scope.row.END_DATE);
				$scope.inputVO.sTime = $scope.row.STIME;
				$scope.inputVO.eTime = $scope.row.ETIME;
				$scope.inputVO.agent_desc = $scope.row.AGENT_DESC;
				$scope.inputVO.seq_no = $scope.row.SEQ_NO;
				$scope.inputVO.emp_ao_id = $scope.row.EMP_ID;	
			}	
		};
		$scope.initial();
		
		$scope.inputVO.roleid = sysInfoService.getRoleID();
		$scope.inputVO.emp_login_id = sysInfoService.getUserID();
		
		$scope.login = function() {
			$scope.sendRecv("ORG220", "loginrole", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO,
					function(tota, isError) {
						if(tota[0].body.resultList5.length == 0) {
//								$scope.showMsg("ehl_01_common_009");
		            		return;
						}
						$scope.resultList5 = tota[0].body.resultList5;
						$scope.inputVO.mroleid = $scope.resultList5[0].COUNTS;
		
						// 被代理人區域中心
						$scope.mappingSet['region_c'] = [];
						angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){	
							$scope.mappingSet['region_c'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});										
						});
						// 被代理人營運區
						$scope.mappingSet['op_a'] = [];			
						angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){				
							$scope.mappingSet['op_a'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});				
						});
									
						// 被代理人分行
						$scope.mappingSet['br_i'] = [];
						angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){				
								$scope.mappingSet['br_i'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});

						});	
						// 被代理人EMP_NAME
						$scope.sendRecv("ORG220", "allPerson", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO,
							function(tota, isError) {
								if (isError) {
									$scope.showErrorMsg(tota[0].body.msgData);
								}
								if(tota[0].body.allPerson.length == 0) {
									$scope.showMsg("ehl_01_common_009");
			                		return;
								}
								$scope.allPerson = tota[0].body.allPerson;
								$scope.mappingSet['ac_a'] = [];
						        angular.forEach($scope.allPerson , function(row, index, objs){
					    			$scope.mappingSet['ac_a'].push({LABEL: row.EMP_ANAME, DATA: row.EMP_ID});
					    		
					 			});
													
				 		});
				})
		};
		$scope.login();
			
		/**修改代理人***/
		$scope.update = function() {
			$scope.sendRecv("ORG220", "update", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsg(tota[0].body.msgData);
				} else {
					$scope.showMsg("ehl_01_common_002");
					$scope.closeThisDialog('successful');
					return;
				}
			});
		};
		
		
		/**組織選取**/
		$scope.org = function(){
			$scope.initial();
			$scope.inputVO.RPT_TYPE = 'ORG';
			$scope.inputVO.emp_id_txt = '' ;
			//員工姓名(限定代理人)
			$scope.branchChange = function(){
			$scope.sendRecv("ORG220", "limagent", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO,
				function(totas, isError) {
		        	if (totas.length > 0) {
		        		$scope.resultList4 = totas[0].body.resultList4;
		        	    //理專人員 
		           		$scope.mappingSet['aocode'] = [];
		           		angular.forEach(totas[0].body.resultList4, function(row, index, objs){
		           			if($scope.inputVO.br_id == row.BRANCH_NBR)
		        			$scope.mappingSet['aocode'].push({LABEL: row.EMP_NAME, DATA: row.AGENT_ID});
		        			
		    			});
		         
		        	};
				});
			};
			
			$scope.branchChange();
			/*** 連動區域中心.營運區.分行別、理專 END ***/
		};
		$scope.org();
		
		$scope.emp_change = function (){
			if($scope.inputVO.RPT_TYPE=="EMP"){
				$scope.sendRecv("ORG220", "limagent", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO",  $scope.inputVO,
						function(totas, isError) {
	          	           	if (totas.length > 0) {
	          	           		$scope.resultList4 = totas[0].body.resultList4;
	          	           		angular.forEach($scope.resultList4, function(row, index, objs){
			           			if(row.AGENT_ID == $scope.inputVO.emp_id_txt){
		            				 $scope.type = 'Y';
		            				 $scope.inputVO.emp_id_txt = row.AGENT_ID;
		            				 $scope.dept();
		            			 }
		            		});
			           		if($scope.type !='Y' ){
			           			$scope.inputVO.emp_id_txt ='';
			           		   	$scope.showMsg("ehl_01_org220_001");
		        	        }
			           	}
					});
				$scope.type = '';
			};
		};

		/**員工編號**/
		$scope.emp = function(){
			$scope.inputVO.RPT_TYPE = 'EMP';
			$scope.inputVO.emp_id = '';
		};
		
		$scope.dept = function(){
			$scope.sendRecv("ORG220", "inquire3", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO,
					function(totas, isError) {
	        	       	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	        	       	$scope.resultList3 = totas[0].body.resultList3;
	        	       	$scope.inputVO.dept_id = totas[0].body.resultList3[0].DEPT_ID;
	        	        $scope.inputVO.agent_dept_id = totas[0].body.resultList3[0].AGENT_DEPT_ID;
	        	      
	        		})
		};
		
        /**========================================================= date picker ============================================================**/
		//檢核日期
		$scope.checkDate = function(){
			 $scope.sendRecv("ORG220", "rewdate", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO",$scope.inputVO,
					function(tota, isError) {
				 		if(!isError){  
							if(tota[0].body.reviewdate.length == 0) {
//											$scope.showMsg("ehl_01_common_009");
			            	}
							$scope.reviewdate = tota[0].body.reviewdate;
							if( tota[0].body.reviewdate[0].SCREDATE=="Y" || tota[0].body.reviewdate[0].ECREDATE == "Y"){
									if($scope.inputVO.sCreDate!=null && $scope.inputVO.sCreDate!="" ){
										$scope.inputVO.sCreDate = undefined;
									}else if ($scope.inputVO.eCreDate!=null && $scope.inputVO.eCreDate!=""){
										$scope.inputVO.eCreDate = undefined ;
										
									}
									$scope.showErrorMsg("ehl_01_org220_002");
							}
				 		}
			 })
		};	
	
		
		var NowDate=new Date(); //現在時間
		
        //代理起始時間
        $scope.bgn_sDateOptions = {
        		maxDate: $scope.inputVO.eCreDate,
          		minDate: NowDate,
        };
        
        //代理結束時間
        $scope.bgn_eDateOptions = {
        		maxDate: $scope.maxDate,
          		minDate: NowDate,
        };
        
        //config
		$scope.model = {};
		
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.mappingSet['timeE'] = [];
			if($scope.inputVO.eCreDate){
				
				$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate; // 迄日最小值
	 			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate; // 起日最大值
	 							
				var NowDate = new Date(); //現在時間
				var t = NowDate.setHours(NowDate.getHours()); //取得現在時間(時)
				var susdate = new Date(t); 
				var h = susdate.getHours(); 
				
				var nowTime  = new Date(NowDate.getFullYear(), NowDate.getMonth(), NowDate.getDate()); //取得現在日期
				var eCreDate = new Date($scope.inputVO.eCreDate.getFullYear(), $scope.inputVO.eCreDate.getMonth(), $scope.inputVO.eCreDate.getDate());//取得結束VO日期	
				
				/**當日以外的日期，皆從早上8點~下午5點**/
				var elseDate = new Date();
				elseDate.setHours(7, 0, 0, 0);
				var hs = elseDate.getHours();
				
				if (eCreDate.getTime() === nowTime.getTime() ){
					for (var i = 0; i < 11; i++) {
						h = h + 1;
						if (h < 17) {
							$scope.mappingSet['timeE'].push({LABEL : (h+1) + ':00', DATA : (h + 1) * 60 * 60 * 1000 });	
						}
					}
				} else {
					for (var i = 0; i < 11; i++) {
						hs = hs + 1;
						if (hs < 20) {
							$scope.mappingSet['timeE'].push({LABEL : (hs+1) + ':00', DATA : (hs + 1) * 60 * 60 * 1000 });
						}
					}
				}
			} else {
				$scope.bgn_eDateOptions.minDate = NowDate ;
			}
		};
		
		$scope.limitDate1 = function() {
			$scope.mappingSet['timeS'] = [];	
				if($scope.inputVO.sCreDate) {
					
					$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate; // 迄日最小值
		 			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate; // 起日最大值
		 			
		        	/**現在時間**/
		        	var NowDate = new Date(); //現在時間
		        	var t = NowDate.setHours(NowDate.getHours()); //取得現在時間(時)
			        var susdate = new Date(t); 
			        var h = susdate.getHours(); 
			        
			        var nowTime  = new Date(NowDate.getFullYear(),NowDate.getMonth(),NowDate.getDate()); //取得現在日期
		        	var sCreDate = new Date($scope.inputVO.sCreDate.getFullYear(),$scope.inputVO.sCreDate.getMonth(),$scope.inputVO.sCreDate.getDate()); //取得起始VO日期
		        	 
		        	/**當日以外的日期，皆從早上8點~下午5點**/
		        	var elseDate = new Date();
		  	 	 	elseDate.setHours(7, 0, 0, 0);
		  	 	 	var hs = elseDate.getHours();

		  	 	 	if (sCreDate.getTime() === nowTime.getTime()) {
		  	 	 		for(var i = 0; i < 12; i++){
		  	 	 			h = h + 1;
		  	 	 			if (h < 17) {   
		  	 	 				$scope.mappingSet['timeS'].push({LABEL : h + ':00', DATA : h * 60 * 60 * 1000 });            		
		  	 	 			}
		  	 	 		}
		  	 	 	} else {
		  	 	 		for (var i = 0; i < 12; i++) {
		  	 	 			hs = hs + 1;
		  	 	 			if (hs < 20) {
		  	 	 				$scope.mappingSet['timeS'].push({LABEL : hs + ':00', DATA : hs * 60 * 60 * 1000 });            		
		  	 	 			}
		  	 	 		}
		  	 	 	}
				}else {
					$scope.bgn_sDateOptions.maxDate = $scope.maxDate ;
				}	
		};
		
		$scope.isq = function(){
			$scope.mappingSet['timeS'] = [];
			$scope.mappingSet['timeE'] = [];
			
			/**現在時間**/
        	var NowDate = new Date(); //現在時間
        	var t = NowDate.setHours(NowDate.getHours()); //取得現在時間(時)
	        var susdate = new Date(t); 
	        var h = susdate.getHours(); 
	        
	        var nowTime  = new Date(NowDate.getFullYear(),NowDate.getMonth(),NowDate.getDate()); //取得現在日期
        	var sCreDate = new Date($scope.inputVO.sCreDate.getFullYear(),$scope.inputVO.sCreDate.getMonth(),$scope.inputVO.sCreDate.getDate()); //取得起始VO日期
        	var eCreDate = new Date($scope.inputVO.eCreDate.getFullYear(),$scope.inputVO.eCreDate.getMonth(),$scope.inputVO.eCreDate.getDate()); //取得結束VO日期	

        	/**當日以外的日期，皆從早上8點~下午5點**/
        	var elseDate = new Date();
  	 	 	elseDate.setHours(7, 0, 0, 0);
  	 	 	var hsBYstime = elseDate.getHours();
  	 	 	var hsBYetime = elseDate.getHours();
  	 	 
  	 	 	if (sCreDate.getTime() === nowTime.getTime()) {
	  	 	 	for (var i = 0; i < 12; i++){
	  	 	 		hsBYstime = hsBYstime + 1;
	  	 	 		if (hsBYstime < 17) {
	  	 	 			$scope.mappingSet['timeS'].push({LABEL : hsBYstime + ':00', DATA : hsBYstime });
	  	 	 		}
	  	 	 	}
  	 	 	} else {
	  	 	 	for (var i = 0; i < 12; i++) {
	  	 	 		hsBYstime = hsBYstime + 1;
	  	 	 		if (hsBYstime < 20) {
	  	 	 			$scope.mappingSet['timeS'].push({LABEL : hsBYstime + ':00', DATA : hsBYstime });
	  	 	 		}
	  	 	 	}
  	 	 	}
  	 	 	
  	 	 	if (eCreDate.getTime() === nowTime.getTime()) {
	  	 	 	for (var i = 0; i < 12; i++) {
	  	 	 		hsBYetime = hsBYetime + 1;
	  	 	 		if (hsBYetime < 17) {
	  	 	 			$scope.mappingSet['timeE'].push({LABEL : (hsBYetime + 1) + ':00', DATA : hsBYetime + 1 });
	  	 	 		}
	  	 	 	}
	 	 	} else {
	  	 	 	for (var i = 0; i < 12; i++){
	  	 	 		hsBYetime = hsBYetime + 1;
	  	 	 		if (hsBYetime < 20) {
	  	 	 			$scope.mappingSet['timeE'].push({LABEL : (hsBYetime + 1) + ':00', DATA : hsBYetime + 1 });
	  	 	 		}
	  	 	 	}
	 	 	}
		};
		$scope.isq();
		
		$scope.timeCheck = function(){
			if($scope.inputVO.sCreDate!=null){
				$scope.a = $scope.inputVO.sCreDate.getTime();
			}
			if($scope.inputVO.eCreDate!=null){
				$scope.b = $scope.inputVO.eCreDate.getTime();
			}
			if($scope.a === $scope.b){
				if(parseInt($scope.inputVO.sTime) > parseInt($scope.inputVO.eTime)){
					$scope.inputVO.eTime = '';
					
				}else{
					return;
				}
			}
		};
	    /**** date picker end ****/
});