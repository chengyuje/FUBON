/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('ORG220_INSERTController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,sysInfoService, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "ORG220_INSERTController";
		
/**====================================================初始化==============================================================**/
		$scope.init= function(){
			 $scope.inputVO = {
				 	RPT_TYPE     :'',
				 	emp_id       : '',
					emp_id_txt       :'',
					sCreDate     : undefined,
					eCreDate     : undefined,
					sTime        :'',
					eTime        :'',
					agent_desc   :'',
					emp_rc_id    :'',
					emp_ao_id    :'',
					dept_id      :'',
					agent_dept_id:'',
					emp_name     :'',
					mroleid      :'',
					emp_login_id :''
			 };
		};
		$scope.init();
		$scope.inputVO.emp_login_id = sysInfoService.getUserID();		
		
		//判斷登入角色
		$scope.initial = function(){

			var deferred = $q.defer();
			
			$scope.sendRecv("ORG220", "loginrole", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO,
				function(tota, isError) {
					if(tota[0].body.resultList5.length == 0) {
						return deferred.promise;
					}
					$scope.resultList5 = tota[0].body.resultList5;
					$scope.inputVO.mroleid = $scope.resultList5[0].COUNTS;
					
					deferred.resolve("success");									
					return deferred.promise;
			});
			
			return deferred.promise;
		};
		
		$scope.initial();
		
		
		//預設值
        $scope.region =sysInfoService.getRegionID();
        $scope.inputVO.emp_rc_id = $scope.region ; 
		// 被代理人EMP_NAME		
        $scope.empname = function(){
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
					
					
					$scope.mappingSet['ac'] = [];
			        angular.forEach($scope.allPerson, function(row, index, objs){
			        	$scope.mappingSet['ac'].push({LABEL: row.EMP_ANAME, DATA: row.EMP_ID});
			        	
					});
			       	$scope.inputVO.emp_ao_id = $scope.inputVO.emp_login_id;	
				});
		};
		
        $scope.initial().then(function(data) {
    		$scope.empname();
        });
		
		
		/**===========================================================新增畫面=========================================================================**/
		 	 
		/**新增代理人***/
		$scope.edit = function(){
			$scope.sendRecv("ORG220", "edit", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO,
					function(tota, isError) {
				
				if (isError) {
					$scope.showErrorMsg(tota[0].body.msgData);
				}else{
					if(tota.length > 0) {
						 /**=======================對代理人 TO 被代理人主管發送信件   內容待確認====================**/
						$scope.sendRecv("ORG220", "mailMsg", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO,
								function(tota, isError) {
									if (!isError) {
										if(tota[0].body.mailMsg.length>0){
														
											$scope.mailMsg = tota[0].body.mailMsg;
											$scope.emp_name = tota[0].body.mailMsg[0].EMP_NAME;
											$scope.dept_id = tota[0].body.mailMsg[0].DEPT_NAME;
											$scope.job_name = tota[0].body.mailMsg[0].JOB_TITLE_NAME;
											$scope.start_date = tota[0].body.mailMsg[0].START_DATE;
											$scope.end_date = tota[0].body.mailMsg[0].END_DATE;
											$scope.agent_name = tota[0].body.mailMsg[0].AGENT_NAME;
											$scope.agent_status_name =  tota[0].body.mailMsg[0].AGENT_STATUS_NAME;
											var subjectTitle = "個金分行業務管理系統代理人通知";
											var agentStatus = $scope.agent_status_name;
											var contect = '<table border="1" style="text-align:center;">' +
														  '<tr>' + 
														  '<td>申請人</td>' + 
														  '<td>被代理人組織</td>' + 
														  '<td>職務</td>' + 
														  '<td>代理時間</td>' + 
														  '<td>代理人</td>' + 
														  '<td>代理狀態</td>' + 
														  '</tr>' + 
														  '<tr>' + 
														  '<td>' + $scope.emp_name + '</td>' + 
														  '<td>' + $scope.dept_id + '</td>' + 
														  '<td>' + $scope.job_name + '</td>' + 
														  '<td>' + $scope.start_date + '~' + $scope.end_date + '</td>' + 
														  '<td>' + $scope.agent_name + '</td>' + 
														  '<td>' + agentStatus + '</td>' + 
														  '</tr>' + 
														  '</table>'; 
											
											$scope.inputData = {					
													custID: [$scope.inputVO.emp_ao_id, $scope.inputVO.emp_id],
													recipientType: 'AO',
													subjectTxt: subjectTitle,
													centerTextarea: '<html> <body> ' + contect + tota[0].body.mailContent + '</body> </html>'
											};	
														
											$scope.sendRecv("CUS110", "sendMail", "com.systex.jbranch.app.server.fps.cus110.CUS110InputVO",  $scope.inputData,
													function(tota, isError) {
														$scope.showMsg(tota[0].body.message);
														if(tota.length > 0){
															$scope.showMsg("ehl_01_common_001");
															$scope.closeThisDialog('successful');
														}
											});
										}
									}
						});
					}
				}
			});
		};
		
 		$scope.emplimit = function(){
 			$scope.sendRecv("ORG220", "limagent", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO,
				function(totas, isError) {
			          	       
		        	if (totas.length > 0) {
		        		$scope.resultList4 = totas[0].body.resultList4;
		           	    //理專人員 
		           		$scope.mappingSet['aocode'] = [];
		           		angular.forEach(totas[0].body.resultList4, function(row, index, objs){
		        			$scope.mappingSet['aocode'].push({LABEL: row.EMP_NAME, DATA: row.AGENT_ID});
		    			});
		           	
		        	};
				});
 		};
 
 
 		//檢核日期
 		$scope.checkDate = function() {
 			$scope.sendRecv("ORG220", "rewdate", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO",$scope.inputVO,
 					function(tota, isError) {
 				if(!isError){  
 					if(tota[0].body.reviewdate.length == 0) {
 					}
								
 					$scope.reviewdate = tota[0].body.reviewdate;
 					if( tota[0].body.reviewdate[0].SCREDATE=="Y" || tota[0].body.reviewdate[0].ECREDATE == "Y"){
 						if($scope.inputVO.sCreDate!=null && $scope.inputVO.sCreDate!="" ){
 							$scope.inputVO.sCreDate = undefined;
 							$scope.inputVO.eCreDate = undefined ;
 							$scope.showErrorMsg("ehl_01_org220_002");
 						}else if ($scope.inputVO.eCreDate!=null && $scope.inputVO.eCreDate!=""){
 							$scope.inputVO.sCreDate = undefined;
 							$scope.inputVO.eCreDate = undefined ;
 							$scope.showErrorMsg("ehl_01_org220_002");
 						}
 						
 					}
 				}
 			})
 		};	
 		var NowDate = new Date(); //現在時間
 		//代理起始時間
 		$scope.bgn_sDateOptions = {
 				maxDate: $scope.maxDate,
 				minDate: NowDate
 		};
 		//代理結束	時間
 		$scope.bgn_eDateOptions = {
 				maxDate: $scope.maxDate,
 				minDate: NowDate
 		};
 		//config
 		$scope.model = {};
			
 		$scope.open = function($event, elementOpened) {
 			$event.preventDefault();
 			$event.stopPropagation();
 			$scope.model[elementOpened] = !$scope.model[elementOpened];
 		};
			
				
 		//迄日時間
 		$scope.limitDate = function() {
 			$scope.mappingSet['timeE'] = [];
 			
 			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate; // 迄日最小值
 			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate; // 起日最大值
 			
 			if($scope.inputVO.eCreDate){
 				
 				var NowDate=new Date(); //現在時間
 				var t= NowDate.setHours(NowDate.getHours()); //取得現在時間(時)
 				var susdate=new Date(t); 
 				var h=susdate.getHours(); 
 				var nowTime  = new Date(NowDate.getFullYear(),NowDate.getMonth(),NowDate.getDate()); //取得現在日期
 				var eCreDate = new Date($scope.inputVO.eCreDate.getFullYear(),$scope.inputVO.eCreDate.getMonth(),$scope.inputVO.eCreDate.getDate());//取得結束VO日期	
 				/**當日以外的日期，皆從早上8點~下午5點**/
 				var elseDate = new Date();
 				elseDate.setHours(7,0,0,0);
 				var hs = elseDate.getHours();
	           		
 				for(var i=0;i<12;i++){
 					hs = hs+1;
 					if(hs<20){
 						$scope.mappingSet['timeE'].push({LABEL : (hs+1)+':00', DATA : (hs+1)*60*60*1000 });
 						
 					}
 				}
 			}else{
 				$scope.bgn_eDateOptions.minDate =  NowDate ;
 			}
 		};
 		
 		//起日時間
 		$scope.limitDate1 = function() {
 			$scope.mappingSet['timeS'] = [];
 			
 			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate; // 迄日最小值
 			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate; // 起日最大值
 			
 			if($scope.inputVO.sCreDate) {
 				/**現在時間**/
 				var NowDate=new Date(); //現在時間
 				var t= NowDate.setHours(NowDate.getHours()); //取得現在時間(時)
 				var susdate=new Date(t); 
 				var h=susdate.getHours(); 
 				var nowTime  = new Date(NowDate.getFullYear(),NowDate.getMonth(),NowDate.getDate()); //取得現在日期
 				var sCreDate = new Date($scope.inputVO.sCreDate.getFullYear(),$scope.inputVO.sCreDate.getMonth(),$scope.inputVO.sCreDate.getDate()); //取得起始VO日期
 				/**當日以外的日期，皆從早上8點~下午5點**/
 				var elseDate = new Date();
 				elseDate.setHours(7,0,0,0);
 				var hs = elseDate.getHours();

 				
 				for(var i=0;i<12;i++){
 					hs = hs+1;
 					if(hs<20){
 						$scope.mappingSet['timeS'].push({LABEL : hs+':00', DATA :  hs*60*60*1000 });            		
 					}
 				}
				        		
 			}else {
 				$scope.bgn_sDateOptions.maxDate = $scope.maxDate ;
 			}	
 		};
 		
 		$scope.timeCheck = function(){
 			if ($scope.inputVO.sCreDate != null) {
 				$scope.a = $scope.inputVO.sCreDate.getTime();
 			}
 			
 			if ($scope.inputVO.eCreDate != null) {
 				$scope.b = $scope.inputVO.eCreDate.getTime();
 			}
 			
 			if ($scope.a === $scope.b) {
 				if(parseInt($scope.inputVO.sTime) > parseInt($scope.inputVO.eTime)){
 					$scope.inputVO.eTime = '';
 				}else{
 					return;
 				}
 			} else if ($scope.a > $scope.b){
 				$scope.inputVO.eCreDate = undefined;
 				$scope.inputVO.eTime = '';
 			}
 		};
});