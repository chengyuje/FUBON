/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG130_ASController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG130_ASController";
		
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		
		// filter
		getParameter.XML(["ORG.FINANCIAL_EXP", "ORG.PRE_FIN_INST", "ORG.REQ_CERTIFICATE", "ORG.MEET_JOB"], function(totas) {
			if (totas) {
				$scope.mappingSet['ORG.FINANCIAL_EXP'] = totas.data[totas.key.indexOf('ORG.FINANCIAL_EXP')];
				$scope.mappingSet['ORG.PRE_FIN_INST'] = totas.data[totas.key.indexOf('ORG.PRE_FIN_INST')];
				$scope.mappingSet['ORG.REQ_CERTIFICATE'] = totas.data[totas.key.indexOf('ORG.REQ_CERTIFICATE')];
				$scope.mappingSet['ORG.MEET_JOB'] = totas.data[totas.key.indexOf('ORG.MEET_JOB')];
			}
		});
	    //
		
		$scope.clear = function () {
			$scope.inputVO.req_certificate 		= [];
			$scope.inputVO.financial_exp		= "";		//相關金融工作資歷(參數維護)
			
			$scope.inputVO.pre_fin_inst 		= "";		//前職金融機構(參數維護1～8)
			$scope.inputVO.other_fi 			= "";		//前職金融機構 (7: 其他金融機構)
			$scope.inputVO.other_pre_fin_inst 	= "";		//前職金融機構(8.其他分行名稱)
			
			$scope.inputVO.cur_m_goal			= undefined;//業績目標
			$scope.inputVO.actual_accomplish	= undefined;//實際達成(前職績效表現)
			$scope.inputVO.cur_aum				= undefined;//管理AUM
			$scope.inputVO.cust_cnt				= undefined;//管理客戶數(前職績效表現)
			
			$scope.inputVO.resign_reason		= "";		//離職原因
			
			$scope.inputVO.cust_satisfaction	= "";		//客戶紛爭/滿意度
			
			$scope.inputVO.achievement			= "";		//成就導向
			$scope.inputVO.sales_skill 			= "";		//銷售技巧
			$scope.inputVO.active				= "";		//積極主動性
			$scope.inputVO.pressure_manage		= "";		//抗壓性
			$scope.inputVO.communication		= "";		//溝通協調性
			$scope.inputVO.problem_solving_skill= "";		//解決問題能力
			
			$scope.inputVO.intv_sup_remark 		= "";		//面談主管評語
			$scope.inputVO.fee_6m_ability		= "";		//評估未來6M手收能力
			$scope.inputVO.fee_1y_ability		= "";		//評估未來1Y手收能力
			$scope.inputVO.suggest_job			= "";		//建議職等職稱
			$scope.inputVO.suggest_salary		= "";		//建議薪資(整包月薪)
		};
		
		$scope.setValue = function () {
			$scope.inputVO.seq                      = $scope.row.SEQ;
			$scope.inputVO.branch_nbr				= $scope.row.BRANCH_NBR;
			$scope.inputVO.cust_id 					= $scope.row.CUST_ID;				//應徵者身份證編號
			$scope.inputVO.status 					= $scope.row.STATUS;				//目前狀態
			$scope.inputVO.review_status 			= $scope.row.REVIEW_STATUS;			//主管覆核狀態：Y-已覆核/W-待覆核/N-退回
			$scope.inputVO.hire_status 				= $scope.row.HIRE_STATUS;			//進用狀態：0-無/1-通過/2-不通過/3-轉介至他行/4-暫存
			$scope.inputVO.emp_name					= $scope.row.EMP_NAME;				//應徵者姓名
			$scope.inputVO.rc_id 					= $scope.row.REGION_CENTER_ID;		//區域中心代碼
			$scope.inputVO.op_id 					= $scope.row.BRANCH_AREA_ID;		//營運區代碼
			$scope.inputVO.br_id 					= $scope.row.BRANCH_NBR;			//分行代碼
			$scope.inputVO.recommender_emp_id 		= $scope.row.RECOMMENDER_EMP_ID;	//推薦人
			$scope.inputVO.recommender_emp_name 	= $scope.row.RECOMMENDER_EMP_NAME;	//推薦人
			$scope.inputVO.ao_job_rank				= $scope.row.AO_JOB_RANK;			//職務
			$scope.inputVO.financial_exp			= $scope.row.FINANCIAL_EXP;			//相關金融工作資歷(參數維護)
			$scope.inputVO.prev_job 				= $scope.row.PREV_JOB;				//前職
			$scope.inputVO.cur_m_goal				= $scope.row.CUR_MONTHLY_GOAL;		//業績目標
			$scope.inputVO.actual_accomplish		= $scope.row.ACTUAL_ACCOMPLISH;		//實際達成(前職績效表現)
			$scope.inputVO.cur_aum					= $scope.row.CUR_AUM;				//管理AUM
			$scope.inputVO.cust_cnt					= $scope.row.CUST_CNT;				//管理客戶數(前職績效表現)
			$scope.inputVO.cust_satisfaction		= $scope.row.CUST_SATISFACTION;		//客戶紛爭/滿意度
			$scope.inputVO.resign_reason			= $scope.row.RESIGN_REASON;			//離職原因
			$scope.inputVO.achievement				= $scope.row.ACHIEVEMENT;			//成就導向
			$scope.inputVO.sales_skill 				= $scope.row.SALES_SKILL;			//銷售技巧
			$scope.inputVO.active					= $scope.row.ACTIVE;				//積極主動性
			$scope.inputVO.pressure_manage			= $scope.row.PRESSURE_MANAGE;		//抗壓性
			$scope.inputVO.communication			= $scope.row.COMMUNICATION;			//溝通協調性
			$scope.inputVO.problem_solving_skill	= $scope.row.PROBLEM_SOLVING_SKILL;	//解決問題能力
			$scope.inputVO.intv_sup_remark 			= $scope.row.INTV_SUP_REMARK;		//面談主管評語
			$scope.inputVO.pre_fin_inst 			= $scope.row.PRE_FIN_INST;			//前職金融機構(參數維護1～8)
			$scope.inputVO.other_fi 				= $scope.row.OTHER_FI;				//前職金融機構 (7: 其他金融機構)
			$scope.inputVO.other_pre_fin_inst 		= $scope.row.OTHER_PRE_FIN_INST;	//前職金融機構(8.其他分行名稱)
			$scope.inputVO.fee_6m_ability			= $scope.row.FEE_6M_ABILITY;		//評估未來6M手收能力
			$scope.inputVO.fee_1y_ability			= $scope.row.FEE_1Y_ABILITY;		//評估未來1Y手收能力
			$scope.inputVO.suggest_job				= $scope.row.SUGGEST_JOB;			//建議職等職稱
			$scope.inputVO.suggest_salary			= $scope.row.SUGGEST_SALARY;		//建議薪資(整包月薪)
			
			if ($scope.inputVO.ao_job_rank == "FC1" || 
				$scope.inputVO.ao_job_rank == "FC2" || 
				$scope.inputVO.ao_job_rank == "FC3" || 
				$scope.inputVO.ao_job_rank == "FC4" || 
				$scope.inputVO.ao_job_rank == "FC5" || 
				$scope.inputVO.ao_job_rank == "FCH"){
			} else {
				$scope.inputVO.type = 'text';
				$scope.inputVO.ao_job_rank1 = $scope.inputVO.ao_job_rank;
			}
			
			if($scope.row.REQ_CERTIFICATE){
            	$scope.inputVO.req_certificate = $scope.row.REQ_CERTIFICATE.split(",");
			}else{
				$scope.inputVO.req_certificate = [];
			}
			
			$scope.inputVO.roleID =  sysInfoService.getRoleID();
			$scope.sendRecv("ORG130", "login", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO",$scope.inputVO,
				function(tota, isError) {
			 		if(!isError){
					   $scope.inputVO.mroleID = tota[0].body.roleID[0].COUNTS;
					}
		   });
		};
		
		$scope.init = function() {
			if($scope.row)
				$scope.isUpdate = true;
			
			$scope.inputVO = {
			}
			
			if ($scope.isUpdate) {
				$scope.setValue();
			}
	
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
		};
		$scope.init();
		$scope.inputVO.login_id = sysInfoService.getUserID();

    	//複選
        $scope.toggleSelection = function toggleSelection(data) {
        	var idx = $scope.inputVO.req_certificate.indexOf(data);
        	if (idx > -1) {
        		$scope.inputVO.req_certificate.splice(idx, 1);      //若已存在，就將它移除
        	} else {
        		$scope.inputVO.req_certificate.push(data);          //若不存在，就將它加入
        	}
        };
		
        $scope.changepre = function(){
        	if($scope.inputVO.pre_fin_inst=='8'){
        		$scope.inputVO.other_pre_fin_inst = $scope.inputVO.other_pre_fin_inst;
        	}else{
        		$scope.inputVO.other_pre_fin_inst = '';
        	}   
        	if($scope.inputVO.pre_fin_inst=='7'){
        		$scope.inputVO.other_fi = $scope.inputVO.other_fi;
        	}else{
        		$scope.inputVO.other_fi = '';  
        	}
    		   
        };
		
		$scope.save = function(row){
			$confirm({text: '是否儲存：' + $scope.row.CUST_ID +'-'+ $scope.row.EMP_NAME + '面談評估表?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG130", "interview", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO",$scope.inputVO,
					function(tota, isError) {
					if(!isError){
						if (isError) {
							$scope.showErrorMsg(tota[0].body.msgData);
							$scope.showMsg("ehl_01_common_007");
							return;
						}else{
							if(tota.length > 0) {
								$scope.showMsg("ehl_01_common_006");
								$scope.closeThisDialog('successful');
				        		
							}
						}
					}
		 	});
			});
		};
		
		
		
//		/**===================================================================================================================**/
//		
//		$scope.genRegion = function() {
//			$scope.mappingSet['region'] = [];
//			angular.forEach(projInfoService.getAvailRegion(), function(row, index, objs){	
//				
//				$scope.mappingSet['region'].push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});										
//			});
//			
//        };
//        $scope.genRegion();
//		
//        //營運區資訊
//		$scope.regionChange = function() {			
//			
//			$scope.mappingSet['op'] = [];			
//			angular.forEach(projInfoService.getAvailArea(), function(row, index, objs){				
//				if(row.REGION_CENTER_ID == $scope.inputVO.rc_id)	
//					$scope.mappingSet['op'].push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});				
//			});
//			
//        };
//        $scope.regionChange();
//        
//        //分行資訊
//		$scope.areaChange = function() {
//			
//			$scope.mappingSet['branch'] = [];
//			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){				
//				if(row.BRANCH_AREA_ID == $scope.inputVO.op_id)			
//					$scope.mappingSet['branch'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
//			});	
//		
//        }; 
//        $scope.areaChange();
//		
//       
//       
//        
//        /**======================================================date=================================================================**/
		
		
		
});