/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG220Controller', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $confirm, getParameter, $timeout, $filter) {
	
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "ORG220Controller";
	
	$scope.loginID = projInfoService.getUserID();

	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();
		
	/*
	 * 取得UHRM人員清單(由員工檔+角色檔)
	 */
	$scope.sendRecv("ORG260", "get031EmpList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
			function(tota, isError) {
				if (isError) {
					return;
				}
				if (tota.length > 0) {
					$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
					$scope.inputVO.uEmpID = sysInfoService.getUserID();
				}
	});
	
	/**==============================================初始化========================================================**/

	$scope.initial= function() {
		$scope.data = [];
		$scope.resultList = [];
		
		$scope.inputVO = {
				RPT_TYPE    		: 'ORG',
				region_center_id   	: '',
				branch_area_id     	: '',
				branch_nbr         	: '',
				emp_id				: '',
				emp_id_txt			: '',
				sCreDate 			: undefined,
				eCreDate 			: undefined,
				sTime   			: '',
				eTime    			: '',
				agent_desc			: '',
				mroleid				: '',
			    act_type    		: ''
		}
		
		if ($scope.memLoginFlag.startsWith('UHRM') && $scope.memLoginFlag != 'UHRM') {
			$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'ORG220'}, function(tota, isError) {
				if (!isError) {
					$scope.uhrmRCList = [];
					$scope.uhrmOPList = [];

					if (null != tota[0].body.uhrmORGList) {
						angular.forEach(tota[0].body.uhrmORGList, function(row) {
							$scope.uhrmRCList.push({LABEL: row.REGION_CENTER_NAME, DATA: row.REGION_CENTER_ID});
						});	
						
						$scope.inputVO.uhrmRC = tota[0].body.uhrmORGList[0].REGION_CENTER_ID;
						
						angular.forEach(tota[0].body.uhrmORGList, function(row) {
							$scope.uhrmOPList.push({LABEL: row.BRANCH_AREA_NAME, DATA: row.BRANCH_AREA_ID});
						});
						
						$scope.inputVO.uhrmOP = tota[0].body.uhrmORGList[0].BRANCH_AREA_ID;
			        }
				}
			});
			
			$scope.inputVO.region_center_id = $scope.inputVO.uhrmRC;
			$scope.inputVO.branch_area_id = $scope.inputVO.uhrmOP;
		} else {
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
		}

	};
	$scope.initial();	

	//登入角色 
	$scope.login = function(){
		$scope.sendRecv("ORG220", "loginrole", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", {},
				function(tota, isError) {
					if(tota[0].body.resultList5.length == 0) {
						$scope.showMsg("ehl_01_common_009");
	            		return;
					}
					$scope.resultList5 = tota[0].body.resultList5;
					$scope.inputVO.mroleid = $scope.resultList5[0].COUNTS;
					
					// 被代理人
	           		if($scope.inputVO.mroleid == '0'){
	           			$scope.inputVO.emp_id = projInfoService.getUserID();	
	           		}
		});
    };
    $scope.login();

	$scope.emp = function(){
		$scope.inputVO.RPT_TYPE = 'EMP';
		if($scope.inputVO.mroleid == '0'){
			$scope.inputVO.emp_id_txt = projInfoService.getUserID();
   		}
	};
			
		
	/**============================================================Console====================================================================**/	
	
	/***查詢結果***/
	$scope.inquire = function() {
		$scope.resultList = [];
		$scope.sendRecv("ORG220", "inquire", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsg(tota[0].body.msgData);
			}
			
			if (tota[0].body.resultList.length == 0) {
				$scope.showMsg("ehl_01_common_009");
				return;
			} else {
				$scope.resultList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;	
				
				angular.forEach($scope.resultList, function(row, index, objs){
					row.set = [];
					row.actionIF = false;
					
					if (row.AGENT_ID == projInfoService.getUserID() && row.AGENT_STATUS == 'W') {
						row.actionIF = true;
						row.set.push({LABEL: '同意', DATA: 'Y'}, {LABEL:'不同意', DATA:'N'});
					}
					
					if (row.EMP_ID == projInfoService.getUserID() && row.AGENT_STATUS == 'S') {
						row.actionIF = true;
						row.set.push({LABEL: '代理結束', DATA: 'E'});
					}

					if (row.EMP_ID == projInfoService.getUserID() && (row.AGENT_STATUS == 'U' || row.AGENT_STATUS == 'W')) {
						row.actionIF = true;
						row.set.push({LABEL: '代理取消', DATA: 'C'}, {LABEL:'代理修改', DATA:'M'});
					}
				});
			}
		})
	};
	
	/***執行動作欄位下拉式選單***/
	$scope.action = function(row, act) {
		row.act = act;

		if (row.act != "" && row.act != undefined) {
			$scope.inputVOtemp = {					
				 seq_no: row.SEQ_NO,
				 agent_status: row.AGENT_STATUS,
				 emp_ao_id: row.EMP_ID,
				 aocode: row.AGENT_ID,
				 act_type: row.act
			};	
			
			$scope.emp_name = row.EMP_NAME;
			$scope.start_date = row.START_DATE;
			$scope.end_date = row.END_DATE;
			$scope.dept_id = row.DEPT_NAME;
			$scope.job_name = row.JOB_TITLE_NAME;
			$scope.agent_name = row.AGENT_NAME;
			
			switch (row.AGENT_STATUS) { 
				case "U" : // 代理狀態:預計代理
					switch (row.act) {
						case "C" :
							var txtMsg = $filter('i18n')('ehl_02_org220_003');
							 
							$confirm({text: txtMsg}, {size: '200px'}).then(function(){
								$scope.sendRecv("ORG220", "updateAct", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVOtemp,
										function(tota, isError) {
									if (isError) {
										$scope.showErrorMsg(tota[0].body.msgData);
									}
									$scope.inquire();
								});	
							});
							break;
						case "M" :
							/**彈跳視窗到設定代理人**/		
							var dialog = ngDialog.open({
								template: 'assets/txn/ORG220/ORG220_UPDATE.html',
								className: 'ORG220_UPDATE',
								showClose: false,
								controller: ['$scope', function($scope) {
									$scope.row = row;
								}]
							}).closePromise.then(function (data) {
								if(data.value === 'successful'){
									$scope.login();
									$scope.inquire();
								}
							});
							break;
					}
					break;
				case "S": // 代理狀態:代理中
					switch (row.act) {
						case "E" :
							var txtMsg = $filter('i18n')('ehl_02_org220_004');
							 
							$confirm({text: txtMsg}, {size: '200px'}).then(function(){
								$scope.sendRecv("ORG220", "updateAct", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVOtemp,
										function(tota, isError) {
									if (isError) {
										$scope.showErrorMsg(tota[0].body.msgData);
									}
									$scope.inquire();
								})	
							});
							break;
					}
					break;
				case "W" : // 代理人是否同意成為代理人
					var subjectTitle = "個金分行業務管理系統代理人通知";
					var agentStatus = (row.act == "Y" ? "預計代理" : "取消代理");
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
					 
					var txtMsg = "";
					
					switch (row.act) {
						case "E" :
							txtMsg = $filter('i18n')('ehl_02_org220_004');
							break;
						case "C" : 
							txtMsg = $filter('i18n')('ehl_02_org220_003');
							break;
						case "Y" :
						case "N" : 
							txtMsg = "是否" + (row.act == 'N' ? "不" : "") + "同意成為" + row.EMP_NAME + "代理人？";
							break;
						case "M" :
							/**彈跳視窗到設定代理人**/
							var dialog = ngDialog.open({
								template: 'assets/txn/ORG220/ORG220_UPDATE.html',
								className: 'ORG220_UPDATE',
								showClose: false,
								controller: ['$scope', function($scope) {
									$scope.row = row;
								}]
							}).closePromise.then(function (data) {
								if (data.value === 'successful'){
									$scope.login();
									$scope.inquire();
								}
							});
							break;
					}
					
					if (row.act != "M") {
						$confirm({text: txtMsg}, {size: '200px'}).then(function(){
							$scope.sendRecv("ORG220", "updateAct", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVOtemp, function(tota, isError) {
								if (isError) {
									$scope.showErrorMsg(tota[0].body.msgData);
								}
								$scope.inquire();
							});
							
							/**=======================對代理人 TO 被代理人主管發送信件   內容待確認====================**/
							$scope.sendRecv("ORG220", "message", "com.systex.jbranch.app.server.fps.org220.ORG220InputVO", $scope.inputVOtemp, function(tota, isError) {
								if (!isError) {
									$scope.mailLst = tota[0].body.mailLst;
			
									$scope.inputData = {					
											custID: [$scope.inputVOtemp.emp_ao_id, $scope.inputVOtemp.aocode],
											recipientType: 'AO',
											subjectTxt: subjectTitle,
											centerTextarea: '<html> <body> ' + contect + tota[0].body.mailContent + '</body> </html>'
									};	
									
									if(tota[0].body.mailLst.length > 0){
										for(var i = 0 ; i < $scope.mailLst.length; i++){
											$scope.inputData.custID.push($scope.mailLst[i].EMP_ID);
										}
									}
									
									$scope.sendRecv("CUS110", "sendMail", "com.systex.jbranch.app.server.fps.cus110.CUS110InputVO",  $scope.inputData, function(tota, isError) {
										$scope.showMsg(tota[0].body.message);
									});
								}
							});
						});
					}
					
					break;
			}
		}
	};

	/**===========================================新增========================================================================**/
	$scope.insert = function (row){
		var dialog = ngDialog.open({
			template: 'assets/txn/ORG220/ORG220_INSERT.html',
			className: 'ORG220_INSERT',
			showClose: false, 
			controller: ['$scope', function($scope) {
				$scope.row = row;
			}]
               
		}).closePromise.then(function(data) {
			 if (data.value === 'successful') {//新增時
				 $scope.login();
				 $scope.inquire();
			 }
		});
	 };
				
    /****======================================================== date picker =======================================================****/
	var NowDate = new Date(); //現在時間

    $scope.bgn_sDateOptions = {
		maxDate: $scope.maxDate,
  		minDate: $scope.minDate
    };

    $scope.bgn_eDateOptions = {
		maxDate: $scope.maxDate,
  		minDate: $scope.minDate
    };

    $scope.model = {};
	
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	$scope.limitDate = function() {
		$scope.mappingSet['timeE'] = [];
		$scope.bgn_sDateOptions.maxDate = $scope.maxDate || $scope.inputVO.eCreDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
		 if ($scope.inputVO.eCreDate) {
        	
        	var NowDate = new Date(); //現在時間
        	var t = NowDate.setHours(NowDate.getHours()); //取得現在時間(時)
	        var susdate = new Date(t); 
	        var h = susdate.getHours(); 
	        var nowTime = new Date(NowDate.getFullYear(),NowDate.getMonth(),NowDate.getDate()); //取得現在日期
        	var eCreDate = new Date($scope.inputVO.eCreDate.getFullYear(),$scope.inputVO.eCreDate.getMonth(),$scope.inputVO.eCreDate.getDate());//取得結束VO日期	
        	 /**當日以外的日期，皆從早上8點~下午5點**/
        	var elseDate = new Date();
  	 	 	elseDate.setHours(7,0,0,0);
  	 	 	var hs = elseDate.getHours();
           	
        	 for(var i = 0; i < 11; i++){
        		hs = hs+1;
        		if(hs < 17){
        		  	$scope.mappingSet['timeE'].push({LABEL : (hs+1)+':00', DATA : hs+1});
        		}
        	 }
        } else {
        	$scope.bgn_eDateOptions.minDate =  NowDate ;
        }
	      
	};
	
	$scope.limitDate1 = function() {
			$scope.mappingSet['timeS'] = [];
			$scope.bgn_sDateOptions.maxDate = $scope.maxDate || $scope.inputVO.eCreDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate ;
			if ($scope.inputVO.sCreDate) {
	        	/**現在時間**/
	        	var NowDate = new Date(); //現在時間
	        	var t = NowDate.setHours(NowDate.getHours()); //取得現在時間(時)
		        var susdate = new Date(t); 
		        var h = susdate.getHours(); 
		        var nowTime  = new Date(NowDate.getFullYear(),NowDate.getMonth(),NowDate.getDate()); //取得現在日期
	        	var sCreDate = new Date($scope.inputVO.sCreDate.getFullYear(),$scope.inputVO.sCreDate.getMonth(),$scope.inputVO.sCreDate.getDate()); //取得起始VO日期
	        	 /**當日以外的日期，皆從早上8點~下午5點**/
	        	var elseDate = new Date();
	  	 	 	elseDate.setHours(7,0,0,0);
	  	 	 	var hs = elseDate.getHours();
	  	 	    for(var i = 0; i < 11; i++){
	            		hs = hs+1;
	            		if(hs < 17){
	            		   	$scope.mappingSet['timeS'].push({LABEL : hs+':00', DATA :  hs });            		
			            }
	        	 }
	        } else {
	        	$scope.bgn_sDateOptions.maxDate = $scope.maxDate ;
	        }	

	};
    /**** date picker end ****/
});