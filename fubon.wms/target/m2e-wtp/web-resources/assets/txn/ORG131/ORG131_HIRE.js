/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG131_HIREController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG131_HIREController";

		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		
		$scope.priID = sysInfoService.getPriID();
		
		var now = new Date();
		//月份
		$scope.booked_onboard_DateOptions = {
			maxDate: $scope.maxDate,
//			minDate: now
		};
		
		//預定報到日
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
//			minDate: now
		};
		
		//適性測驗日
		$scope.test_DateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		
		//可報到日
		$scope.able_onboard_DateOptions = {
    		maxDate: $scope.maxDate,
//    		minDate: now
		};
		
		//面試日期
		$scope.brch_ini_int_DateOptions = {
    		maxDate: $scope.maxDate,
    		minDate: $scope.minDate
		};
		
		//進件日期
		$scope.brch_recv_case_DateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate		
		}
		
		//config
		$scope.model = {};
			
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
			
		//月份
		$scope.limitDate = function() {
			$scope.booked_onboard_DateOptions.maxDate =  $scope.maxDate;
//			$scope.booked_onboard_DateOptions.minDate = now;
		};
	        
		//預定報到日
		$scope.limitDate1 = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.maxDate;
//			$scope.bgn_sDateOptions.minDate = now;
		};
		
		//適性測驗日
		$scope.limitDate2 = function() {
			$scope.test_DateOptions.maxDate = $scope.maxDate;
//			$scope.test_DateOptions.minDate = $scope.minDate;
		};
		
		//可報到日
		$scope.limitDate3 = function() {
			$scope.able_onboard_DateOptions.maxDate = $scope.maxDate;
//			$scope.able_onboard_DateOptions.minDate = now;
		};
		
		//面試日期
		$scope.limitDate4 = function() {
			$scope.brch_ini_int_DateOptions.maxDate = $scope.maxDate;
//			$scope.brch_ini_int_DateOptions.minDate = now;
		};
			
		//進件日期
		$scope.limitDate5 = function() {
			$scope.brch_recv_case_DateOptions.maxDate = $scope.maxDate;
//			$scope.brch_recv_case_DateOptions.minDate = $scope.minDate;
		};
		
		$scope.initial = function(){
			if($scope.row)
				$scope.isUpdate = true;
			
			$scope.inputVO = {} 
			
			if($scope.isUpdate){
				$scope.inputVO.seq = $scope.row.SEQ;
				$scope.inputVO.cust_id = $scope.row.CUST_ID;
				$scope.inputVO.emp_id = $scope.row.EMP_ID;
				$scope.inputVO.emp_name = $scope.row.EMP_NAME;
				$scope.inputVO.intv_emp_id = $scope.row.INTV_EMP_ID;
				$scope.inputVO.intv_emp_name = $scope.row.INTV_EMP_NAME;
				$scope.inputVO.region_center_id = $scope.row.REGION_CENTER_ID;
				$scope.inputVO.branch_area_id = $scope.row.BRANCH_AREA_ID;
				$scope.inputVO.branch_nbr = $scope.row.BRANCH_NBR;
				$scope.inputVO.brch_recv_case_date = $scope.toJsDate($scope.row.BRCH_RECV_CASE_DATE);
				$scope.inputVO.ho_recv_case_date = $scope.toJsDate($scope.row.HO_RECV_CASE_DATE);
				$scope.inputVO.oa_sup_rt_date = $scope.toJsDate($scope.row.OA_SUP_RT_DATE);
				$scope.inputVO.brch_ini_int_date = $scope.toJsDate($scope.row.BRCH_INI_INT_DATE);
				$scope.inputVO.status = $scope.row.STATUS;
				$scope.inputVO.sCreDate =$scope.toJsDate($scope.row.BOOKED_ONBOARD_DATE);
				$scope.inputVO.job_rank= $scope.row.JOB_RANK;
				$scope.inputVO.job_title_name = $scope.row.JOB_TITLE_NAME;
				$scope.inputVO.ao_job_rank = $scope.row.AO_JOB_RANK;
				$scope.inputVO.testDate =$scope.toJsDate($scope.row.ADA_TEST_DATE);
				$scope.inputVO.status_reason = $scope.row.NO_SHOW_REASON;
				$scope.inputVO.result = $scope.row.RT_RESULT;
				$scope.inputVO.desc = $scope.row.RECRUIT_REMARK;
				$scope.inputVO.black_listed = $scope.row.BLACK_LISTED;
				$scope.inputVO.resign_return = $scope.row.RESIGN_RETURN;
				$scope.inputVO.return_remark = $scope.row.RETURN_REMARK;
				$scope.inputVO.prev_job_exp = $scope.row.PREV_JOB_EXP;
				$scope.inputVO.cur_job =$scope.row.CUR_JOB;
				$scope.inputVO.cur_job_name = $scope.row.CUR_JOB_NAME;
				$scope.inputVO.cur_aum = $scope.row.CUR_AUM;
				$scope.inputVO.cur_m_goal = $scope.row.CUR_MONTHLY_GOAL;
				$scope.inputVO.actual_accomplish = $scope.row.ACTUAL_ACCOMPLISH;
				$scope.inputVO.cur_fee_income= $scope.row.CUR_FEE_INCOME;
				$scope.inputVO.cust_cnt = $scope.row.CUST_CNT;
				$scope.inputVO.ao_year_of_exp = $scope.row.AO_YEAR_OF_EXP;
				$scope.inputVO.able_onboard_date = $scope.toJsDate($scope.row.ABLE_ONBOARD_DATE);
				$scope.inputVO.prev_job = $scope.row.PREV_JOB;
//				$scope.inputVO.rc_sup_emp_id = $scope.row.RC_SUP_EMP_ID;
//				$scope.inputVO.op_sup_emp_id = $scope.row.OP_SUP_EMP_ID;
				$scope.inputVO.hr =$scope.row.HR;
				$scope.inputVO.trans_from_branch_id =$scope.row.TRANS_FROM_BRANCH_ID;
				$scope.inputVO.trans_remark = $scope.row.TRANS_REMARK;
				$scope.inputVO.resume_source = $scope.row.RESUME_SOURCE;
				$scope.inputVO.recomm_emp_id = $scope.row.RECOMMEN_EMP_ID;
				$scope.inputVO.recommender_emp_id =$scope.row.RECOMMENDER_EMP_ID;
				$scope.inputVO.recommend_awardee_emp_id=$scope.row.RECOMMEND_AWARDEE_EMP_ID;
				$scope.inputVO.recommend_letter = $scope.row.RECOMMEND_LETTER;
				$scope.inputVO.req_certificate1 = $scope.row.REQ_CERTIFICATE;
				$scope.inputVO.financial_exp = $scope.row.FINANCIAL_EXP;
				$scope.inputVO.pre_fin_inst = $scope.row.PRE_FIN_INST;
				$scope.inputVO.other_fi = $scope.row.OTHER_FI;
				$scope.inputVO.other_pre_fin_inst = $scope.row.OTHER_PRE_FIN_INST;
				$scope.inputVO.resign_reason = $scope.row.RESIGN_REASON;
				$scope.inputVO.cust_satisfaction = $scope.row.CUST_SATISFACTION;
				$scope.inputVO.achievement = $scope.row.ACHIEVEMENT;
				$scope.inputVO.sales_skill = $scope.row.SALES_SKILL;
				$scope.inputVO.active = $scope.row.ACTIVE;
				$scope.inputVO.pressure_manage = $scope.row.PRESSURE_MANAGE;
				$scope.inputVO.communication = $scope.row.COMMUNICATION;
				$scope.inputVO.problem_solving_skill = $scope.row.PROBLEM_SOLVING_SKILL;
				$scope.inputVO.intv_sup_remark = $scope.row.INTV_SUP_REMARK;
				$scope.inputVO.hire_status = $scope.row.HIRE_STATUS;
				$scope.inputVO.createtime = $scope.toJsDate($scope.row.CREATETIME);
				$scope.inputVO.creator =$scope.row.CREATOR;
				$scope.inputVO.modifier=$scope.row.MODIFIER;
				$scope.inputVO.lastupdate =$scope.toJsDate($scope.row.ADA_TEST_DATE);
			}
			
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id_MY", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
		};
		$scope.initial();
		
		$scope.init = function () {
			$scope.inputVO.login_id = sysInfoService.getUserID();
			$scope.inputVO.roleID =  sysInfoService.getRoleID();
			if ($scope.inputVO.intv_emp_id == null) {	
				$scope.inputVO.intv_emp_id = $scope.inputVO.login_id;
//				$scope.init = function () {
					$scope.sendRecv("ORG131", "initial", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
							function(tota, isError) {
								if(!isError){
						   
									if(tota[0].body.resultList.length == 0) {
										return;
									}
							
									$scope.inputVO.intv_emp_name =  tota[0].body.resultList[0].INTV_EMP_NAME;
								}
					});
//				};
			}
			
//			$scope.sendRecv("ORG131", "manager", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
//					function(tota, isError) {
//				 		if(!isError){  
//							if(tota[0].body.opList.length == 0 || tota[0].body.rcList.length == 0) {
//			            		return;
//							}
//							
//						   $scope.opList = tota[0].body.opList;
//						   $scope.rcList = tota[0].body.rcList;
//						   
//					  	   $scope.mappingSet['rc_sup']=[];
//					  	   for (var i = 0 ; i < $scope.rcList.length ; i++) {
//					  		   $scope.mappingSet['rc_sup'].push({LABEL:tota[0].body.rcList[i].RC_SUP_EMP_NAME, DATA: tota[0].body.rcList[i].RC_SUP_EMP_ID });
//					  		   if ($scope.rcList.length == 1) {
//					  			   $scope.inputVO.rc_sup_emp_id = $scope.mappingSet['rc_sup'][0].DATA;
//					  		   }
//					  	   }
//						   $scope.mappingSet['op_sup']=[];
//						   for (var i = 0 ; i < $scope.opList.length ; i++) {
//							   $scope.mappingSet['op_sup'].push({LABEL: tota[0].body.opList[i].OP_SUP_EMP_NAME, DATA: tota[0].body.opList[i].OP_SUP_EMP_ID });
//							   if ($scope.opList.length == 1) {
//								   $scope.inputVO.op_sup_emp_id = $scope.mappingSet['op_sup'][0].DATA;
//							   }	
//						   }
//			 		}
//			   });
			
		   $scope.sendRecv("ORG131", "login", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
				function(tota, isError) {
			 		if(!isError){  
						if(tota[0].body.roleID.length == 0) {
		            		return;
						}
						
					   $scope.roleID = tota[0].body.roleID;
		 		}
		   })
		};
			
		$scope.init();
		
		//檢核日期
		$scope.checkDate = function(){
			 $scope.sendRecv("ORG131", "rewdate", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
					function(tota, isError) {
				 		if(!isError){  
							if(tota[0].body.reviewdate.length == 0) {
			            		return;
							}
							$scope.reviewdate = tota[0].body.reviewdate;
							$scope.outputVO = tota[0].body;

							if(tota[0].body.reviewdate[0].SCREDATE=="Y" || 
							   tota[0].body.reviewdate[0].TESTDATE=="Y" ||
							   tota[0].body.reviewdate[0].ABLEONBOARDDATE=="Y"||
							   tota[0].body.reviewdate[0].BRCHINIINTDATE=="Y"){
								
								if(tota[0].body.reviewdate[0].SCREDATE=="Y"){
									$scope.inputVO.sCreDate = undefined;
								}else if(tota[0].body.reviewdate[0].TESTDATE=="Y"){
									$scope.inputVO.testDate = undefined;
								}else if( tota[0].body.reviewdate[0].ABLEONBOARDDATE=="Y"){
									$scope.inputVO.able_onboard_date = undefined;
								}else if( tota[0].body.reviewdate[0].BRCHINIINTDATE=="Y"){
									$scope.inputVO.brch_ini_int_date = undefined;
								}

							}
					
				 		}
			 });
		};
		
		/**====================================================================================================================================**/
		
		//暫存
		$scope.temp = function(row){
			$confirm({text: '是否暫存：' + $scope.row.CUST_ID +'-'+ $scope.row.EMP_NAME + '進用資料?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG131", "tempedit", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
					function(tota, isError) {
				
					if(!isError){
						
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
						}
						
						if(tota.length > 0) {
							$scope.showMsg("ehl_01_common_006");
							$scope.closeThisDialog('successful');
							return;
						}
					}
			 	});
			});
		};
		
		//==通過
		$scope.pass = function(){
			$confirm({text: '是否通過：' + $scope.row.CUST_ID +'-'+ $scope.row.EMP_NAME + '進用資料?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG131", "passedit", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,function(tota, isError) {
				if(!isError){
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
						$scope.showMsg("ehl_01_common_007");
					}
					if(tota.length > 0) {
						$scope.closeThisDialog('successful');
		        		return;
					}
				}
			});
			})
		};
		
		//==不通過
		$scope.unpass = function(row){
			$confirm({text: '是否不通過：'+ $scope.row.CUST_ID +'-'+ $scope.row.EMP_NAME + '進用資料?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG131", "unpassedit", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
					function(tota, isError) {
				
					if(!isError){
						
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
						}
						
						if(tota.length > 0) {
							$scope.showMsg("ehl_01_common_006");
							$scope.closeThisDialog('successful');
			        		return;
						}
					}
			 	});
			})
		}
		
		//==轉介至他行
		$scope.transelse = function(row){
			$confirm({text: '是否轉介至他行：' + $scope.row.CUST_ID +'-'+ $scope.row.EMP_NAME + '進用資料?'}, {size: '200px'}).then(function() {
				 var dialog = ngDialog.open({
					template: 'assets/txn/ORG131/ORG131_TRANS.html',
					className: 'ORG131_TRANS',
					showClose: false, 
					controller: ['$scope', function($scope) {
						$scope.row = row;
					}]
				 });
				 
				 dialog.closePromise.then(function (data) {
					 if(data.value === 'successful'){
						 $scope.closeThisDialog('successful');
					 }
				 });
			})
		};
		
		/**===================================================================================================================**/	   
		//推薦人員編
		$scope.reempid = function() {
			if($scope.inputVO.recommender_emp_id || $scope.inputVO.recomm_emp_id) {
				$scope.sendRecv("ORG131", "reempid", "com.systex.jbranch.app.server.fps.org131.ORG131InputVO",$scope.inputVO,
						function(tota, isError) {
					 		if(!isError){  
								 if(tota[0].body.resultList4.length == 0) {
									 $scope.showMsg("ehl_01_org131_001");
									 return;
								 }
								 else {
									 $scope.resultList4 = tota[0].body.resultList4;
									 $scope.mappingSet['red']=[];
									 $scope.mappingSet['red'].push({LABEL:tota[0].body.resultList4[0].EMP_NAME, DATA: tota[0].body.resultList4[0].EMP_ID });
						  			 $scope.inputVO.recommender_emp_id = tota[0].body.resultList4[0].EMP_ID;
						  			 $scope.inputVO.recommend_awardee_emp_id = tota[0].body.resultList4[0].EMP_ID;
								 }
					 		}
						});
				
			};
		};
		$scope.reempid();

		//轉介行
		$scope.mappingSet['trans_from_branch_id'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){				
			if(row.BRANCH_AREA_ID == $scope.inputVO.branch_area_id && row.BRANCH_NBR!=$scope.inputVO.branch_nbr)			
				$scope.mappingSet['trans_from_branch_id'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});	
		
		//推薦表
		$scope.mappingSet['letter'] = [];
		$scope.mappingSet['letter'].push({LABEL: '是', DATA:'Y'},{LABEL: '否', DATA: 'N'});
				
		
	/**===================================================================================================================**/		
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
				if(row.REGION_CENTER_ID == $scope.inputVO.region_center_id)	
					$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});				
			});
			
        };
        $scope.regionChange();
        
        //分行資訊
		$scope.areaChange = function() {
			
			$scope.mappingSet['branch'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){				
				if(row.BRANCH_AREA_ID == $scope.inputVO.branch_area_id)			
					$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});	
		
        }; 
        $scope.areaChange();
        
        /**======================================================date=================================================================**/
       
		
});