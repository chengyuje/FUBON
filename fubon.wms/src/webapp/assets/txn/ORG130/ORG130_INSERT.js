/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG130_INSERTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, sysInfoService, validateService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG130_INSERTController";
		/**====================================初始化============================================**/
		$scope.initial = function(){
			$scope.inputVO = {
			}
		};
		$scope.initial();
		
		
		$scope.clear = function(){
			$scope.inputVO.cust_id = '';
		};
		
		$scope.init = function(){
			$scope.inputVO.login_id = sysInfoService.getUserID();
//			$scope.sendRecv("ORG130", "initial", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO",$scope.inputVO,
//					function(tota, isError) {
//				   	if(!isError){
//					 
//						$scope.inputVO.region_center_id = tota[0].body.resultList[0].REGION_CENTER_ID;
//		            	$scope.inputVO.branch_area_id = tota[0].body.resultList[0].BRANCH_AREA_ID;
//		            	$scope.inputVO.branch_nbr = tota[0].body.resultList[0].BRANCH_NBR;
//		            	$scope.inputVO.intv_emp_id = tota[0].body.resultList[0].INTV_EMP_ID;
//		            	$scope.inputVO.intv_emp_name =  tota[0].body.resultList[0].INTV_EMP_NAME;
//		            }
//				});
			$scope.inputVO.region_center_id = sysInfoService.getAvailRegion()[0].REGION_CENTER_ID;
        	$scope.inputVO.branch_area_id = sysInfoService.getAvailArea()[0].BRANCH_AREA_ID;
        	console.log("sysInfoService.getAvailArea()", sysInfoService.getAvailArea());
        	console.log("sysInfoService.getAvailArea()[0]", sysInfoService.getAvailArea()[0]);
        	$scope.inputVO.branch_nbr = sysInfoService.getAvailBranch()[0].BRANCH_NBR;
        	$scope.inputVO.intv_emp_id = sysInfoService.getUserID();
        	$scope.inputVO.intv_emp_name =  sysInfoService.getUserName();
		};
			
		$scope.init();
		//區域中心主管、營運主管
//		$scope.init2 = function(){
//			$scope.inputVO.login_id = sysInfoService.getUserID();
//				
//			$scope.sendRecv("ORG130", "manager", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO",$scope.inputVO,
//					function(tota, isError) {
//				 		if(!isError){  
//							if(tota[0].body.resultList3.length == 0) {
//								$scope.showMsg("ehl_01_common_009");
//			            		return;
//							}
//							
//							$scope.resultList3 = tota[0].body.resultList3;
//						  	   $scope.inputVO.rc_sup_emp_id = tota[0].body.resultList3[0].RC_SUP_EMP_ID;
//							   $scope.inputVO.op_sup_emp_id = tota[0].body.resultList3[0].OP_SUP_EMP_ID;
//						}
//				   })
//				};
//			
//		
//		$scope.init2();
		/**======================================================================================**/
		//檢視CUST 是否重複
		$scope.custrew = function() {
			$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			//檢核客戶ID合理性
			$scope.sendRecv("INS200", "vaildCUSTID", "com.systex.jbranch.app.server.fps.ins200.INS200InputVO", 
				{'CUST_ID': $scope.inputVO.cust_id}, function(tota,isError){
					if(!isError){
						if(!tota[0].body.vaildcustid){
							$scope.clear();
							$scope.showErrorMsg('ehl_01_common_030');
						} else {
							$scope.sendRecv("ORG130", "rewcust", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO", $scope.inputVO,
								function(tota, isError) {
									if (isError) {
										$scope.showErrorMsg(tota[0].body.msgData);
									}
									$scope.rewcust = tota[0].body.rewcust;
									$scope.outputVO = tota[0].body;
									;
									if(tota[0].body.rewcust[0].REWCUST=="1"){
										$scope.clear();
										$scope.showErrorMsg("該面談者ID曾於分行面試。");
									}
							 });
						}
					} else {
						$scope.clear();
//						$scope.showErrorMsg('ehl_01_common_030');
					}
			});
		};
	//新增
		$scope.add = function(){
			$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			$confirm({text: '是否新增：' + $scope.inputVO.cust_id +'-'+ $scope.inputVO.emp_name + '進件資料?'}, {size: '200px'}).then(function() {
			 $scope.sendRecv("ORG130", "add", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO", $scope.inputVO,
				function(tota, isError) {
		
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
						
					}

					if(tota.length > 0) {
						$scope.closeThisDialog('successful');
					}else{
						$scope.showMsg("ehl_01_common_008");
					}
			 	});
			});
		};
		
		//檢核日期
		$scope.checkDate = function(){
			
			 $scope.sendRecv("ORG130", "rewdate", "com.systex.jbranch.app.server.fps.org130.ORG130InputVO",$scope.inputVO,
					function(tota, isError) {
				 		if(!isError){  
							if(tota[0].body.reviewdate.length == 0) {
	//									$scope.showMsg("ehl_01_common_009");
			            		return;
							}
							$scope.reviewdate = tota[0].body.reviewdate;
							$scope.outputVO = tota[0].body;
							if( tota[0].body.reviewdate[0].BRCHINIINTDATE=="Y"){
									$scope.inputVO.brch_ini_int_date='';
								
									$scope.showErrorMsg("該日期非工作日，請重新輸入。");
							
								
							}
				 		}
			 	  })
		};	
		/**====================================== date picker ====================================**/
	     var NowDate=new Date(); //現在時間
       //代理起始時間
       $scope.brch_ini_int_DateOptions = {
       		maxDate: $scope.maxDate
//       		,
//         	minDate: NowDate
   	    };
       
       //config
		$scope.model = {};
		
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.brch_ini_int_DateOptions.maxDate =  $scope.maxDate;
			
		};
});