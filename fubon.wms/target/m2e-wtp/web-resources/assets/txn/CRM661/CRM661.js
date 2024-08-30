/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM661Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM661Controller";

		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		
		//vip_degree
		var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
		if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
			$scope.requestComboBox(vo, function(tota) {
				if (tota[tota.length - 1].body.result === 'success') {
					projInfoService.mappingSet['CRM.VIP_DEGREE'] = tota[0].body.result;
					$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
				}
			});
		} else {
			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
		}
		
		//con_degree
		var vo = {'param_type': 'CRM.CON_DEGREE', 'desc': false};
		if(!projInfoService.mappingSet['CRM.CON_DEGREE']) {
			$scope.requestComboBox(vo, function(tota) {
				if (tota[tota.length - 1].body.result === 'success') {
					projInfoService.mappingSet['CRM.CON_DEGREE'] = tota[0].body.result;
					$scope.mappingSet['CRM.CON_DEGREE'] = projInfoService.mappingSet['CRM.CON_DEGREE'];
				}
			});
		} else{
			$scope.mappingSet['CRM.CON_DEGREE'] = projInfoService.mappingSet['CRM.CON_DEGREE'];
		}
		
		//關係類型:CRM.REL_TYPE=>顯示用(含本人)	CRM.REL_TYPE_N00=>選單用(不含本人)及法人系列A1~A6
		var vo = {'param_type': 'CRM.REL_TYPE', 'desc': false};
		$scope.mappingSet['CRM.REL_TYPE_N00'] = [];
		if(!projInfoService.mappingSet['CRM.REL_TYPE']) {
			$scope.requestComboBox(vo, function(tota) {
				if (tota[tota.length - 1].body.result === 'success') {
					$scope.mappingSet['CRM.REL_TYPE'] = tota[0].body.result;
					$scope.mappingSet['CRM.REL_TYPE_N00'] = tota[0].body.result.filter(function(obj){
						return (obj.DATA != '00' && obj.DATA != 'A1' && obj.DATA != 'A2' 
							&& obj.DATA != 'A3' && obj.DATA != 'A4' && obj.DATA != 'A5' && obj.DATA != 'A6');
					});
				}
			});
		} else {
			$scope.mappingSet['CRM.REL_TYPE'] = projInfoService.mappingSet['CRM.REL_TYPE'];
		}
		
		//status
		$scope.mappingSet['STATUS'] = [];
		$scope.mappingSet['STATUS'].push({LABEL: '(申請待覆核)', DATA: 'RAN'},{LABEL: '', DATA: 'RSN'},{LABEL: '', DATA: 'RRN'},
										 {LABEL: '(刪除待覆核)', DATA: 'RAD'},{LABEL: '', DATA: 'RSD'},{LABEL: '', DATA: 'RRD'},
										 {LABEL: '(歸戶申請待覆核)', DATA: 'RAJ'}, {LABEL: '', DATA: 'RSJ'},{LABEL: '', DATA: 'RRJ'},
										 {LABEL: '(歸戶刪除待覆核)', DATA: 'RAC'}, {LABEL: '', DATA: 'RSC'},{LABEL: '', DATA: 'RRC'},
										 {LABEL: '', DATA: ''});
		
		//#0002495 : COMMON_XML取值CRM.REL_TYPE時，若PARAM_CODE開頭是A的選項不要顯示。
		$scope.mappingSet['NEW_CRM.REL_TYPE'] = [];
		angular.forEach($scope.mappingSet['CRM.REL_TYPE'], function(row, index, objs){
        	if(!(row.DATA == 'A1' || row.DATA == 'A2' || row.DATA == 'A3' ||
        		row.DATA == 'A4' || row.DATA == 'A5' || row.DATA == 'A6')){
        		$scope.mappingSet['NEW_CRM.REL_TYPE'].push({LABEL: row.LABEL, DATA: row.DATA});
        	}			
		});
        
        //JOIN_SRV_CUST_ID => CUST_NAME
        $scope.mappingSet['JOIN_SRV_CUST_ID'] = [];
        
        //權限
        $scope.pri = '';
        $scope.pri = String(sysInfoService.getPriID());
        $scope.ao = '';
        $scope.ao = String(sysInfoService.getAoCode());
        
        if ($scope.ao != '' && $scope.ao != undefined) {
        	$scope.check_ao = true;
        } else {
        	$scope.check_ao = false;
        }
        
        if ($scope.pri == '009' || $scope.pri == '010' || $scope.pri == '011' ||
        	$scope.pri == '012' || $scope.pri == '013' || 
        	$scope.pri == 'UHRM012' || $scope.pri == 'UHRM013') {
        	$scope.check_rpy = true;
        } else {
        	$scope.check_rpy = false;
        }
        
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
			$scope.inputVO.cust_name = $scope.custVO.CUST_NAME;
			$scope.inputVO.ao_code = $scope.custVO.AO_CODE;
			$scope.inputVO.emp_id = sysInfoService.getUserID();
			$scope.inputVO.rel_type = '';
			$scope.inputVO.rel_type_oth = '';
			$scope.inputVO.seq = '';
			$scope.inputVO.rel_status = '';
			$scope.inputVO.rel_cust_id = '';
        	$scope.inputVO.rel_cust_name = '';
		}
		$scope.init();
		
		//輸出欄初始化
		$scope.inquireInit = function(){
			$scope.mainList = [];
			$scope.resultList_rel = [];
			$scope.resultList_rel_add = [];
			$scope.CUST_LIST = [];
			$scope.TXN_LIST = [];
		}
		$scope.inquireInit();
		
		//關係戶初始查詢 
		$scope.inquire = function(){
			$scope.MS_type = $scope.custVO.CRM661_ID_M;
			$scope.sendRecv("CRM661", "inquire", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", {'cust_id':$scope.custVO.CUST_ID},
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(tota[0].body.msgData);
						return;
					}
					if(tota[0].body.resultList_rel !=null && tota[0].body.resultList_rel.length > 0){
						$scope.resultList_rel = tota[0].body.resultList_rel;
						$scope.CUST_LIST = [];
						angular.forEach($scope.resultList_rel, function(row, index, objs){
//							//歸戶服務主戶
//							$scope.mappingSet['JOIN_SRV_CUST_ID'].push({LABEL: row.CUST_NAME, DATA: row.CUST_ID});
							$scope.CUST_LIST.push(row.CUST_ID);
						});
					}
					
					//一年內最近一次下單費率
					$scope.sendRecv("CRM661", "inquire_5", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", {'cust_list':$scope.CUST_LIST},
						function(tota, isError) {
							if (isError) {
								$scope.showErrorMsg(tota[0].body.msgData);
								return;
							}
							$scope.TXN_LIST = tota[0].body.resultList_rel;
							
							for(var i = 0; i < $scope.resultList_rel.length; i++){
								for(var j = 0; j < $scope.TXN_LIST.length; j++){
									if($scope.resultList_rel[i].CUST_ID == $scope.TXN_LIST[j].CUST_ID){
										if($scope.TXN_LIST[j].P_TYPE == '01'){
											$scope.resultList_rel[i].TXFEE_DSCNT_RATE_1 = $scope.TXN_LIST[j].FEE_RT;
										}
										if($scope.TXN_LIST[j].P_TYPE == '02'){
											$scope.resultList_rel[i].TXFEE_DSCNT_RATE_2 = $scope.TXN_LIST[j].FEE_RT;
										}
										if($scope.TXN_LIST[j].P_TYPE == '03'){
											$scope.resultList_rel[i].TXFEE_DSCNT_RATE_3 = $scope.TXN_LIST[j].FEE_RT;
										}
										if($scope.TXN_LIST[j].P_TYPE == 'ETF'){
											$scope.resultList_rel[i].TXFEE_DSCNT_RATE_4 = $scope.TXN_LIST[j].FEE_RT;
										}
										if($scope.TXN_LIST[j].P_TYPE == 'TXN'){
											$scope.resultList_rel[i].TXFEE_DSCNT_RATE_5 = $scope.TXN_LIST[j].FEE_RT;
										}
									}
								}
							}
					});
			});
		}
		$scope.inquire();
		
	    //open 關係戶 Dialog	
		//防呆功能:避免多開彈跳視窗
	    $scope.flag = 0;
		$scope.open_rel = function(Flag){
			//只能在無該彈跳視窗的狀況下打開，打開後防呆功能啟動
			if (Flag != $scope.flag) {
				$scope.flag = Flag;
				var cust_id_m = $scope.custVO.CUST_ID;
				var ao_code = $scope.inputVO.ao_code;
				
				var dialog = ngDialog.open({
					template: 'assets/txn/CRM661/CRM661_rel_add.html',
					className: 'CRM661_rel_add',
					showClose: false,
					controller: ['$scope', function($scope) {
						 $scope.cust_id_m = cust_id_m;
						 $scope.ao_code = ao_code;
		             }]
				});
				
				dialog.closePromise.then(function (list) {
					//關閉彈跳視窗後，防呆功能關閉
					$scope.flag = 0;
					
					//取消關閉
					if(list.value === 'cancel'){
						return;
		            }
					//確定傳值
					else {
		            	$scope.inputVO.rel_cust_id = list.value.CUST_ID;
		            	$scope.inputVO.rel_cust_name = list.value.CUST_NAME;
		            	if('SEQ' in list.value){
		            		$scope.inputVO.seq = list.value.SEQ;
		            	}
		            }
	            });
			}
	        return false;
		}
		
		//open 歸戶服務 Dialog
		$scope.open_set = function(){
			var cust_id = $scope.inputVO.cust_id;
			var ao_code = $scope.inputVO.ao_code;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM661/CRM661_rel_set.html',
				className: 'CRM661_rel_set',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 $scope.cust_id = cust_id;
					 $scope.ao_code = ao_code;
	             }]
			});
			
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.init();
                	$scope.inquireInit();
                	$scope.inquire();
				}
            });
		}
		
		//open 待覆核清單 Dialog
		$scope.open_rpy = function(){
			var cust_id = $scope.inputVO.cust_id;
			var ao_code = $scope.inputVO.ao_code;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM661/CRM661_rel_rpy_dialog.html',
				className: 'CRM661_rel_rpy',
				showClose: false,
				 controller: ['$scope', function($scope) {
					 $scope.cust_id = cust_id;
					 $scope.ao_code = ao_code;
	             }]
			});
			
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.init();
                	$scope.inquireInit();
                	$scope.inquire();
				}
            });
		}
		
		//連客戶首頁
		$scope.gohome = function(row) {
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
		//新增關係戶
		$scope.rel_add = function() {
			if ($scope.inputVO.rel_cust_id == '') {
				$scope.showErrorMsgInDialog('欄位檢核錯誤:客戶身份證字號為空');
				return;
			}
			if ($scope.inputVO.rel_type == '') {
				$scope.showErrorMsgInDialog('欄位檢核錯誤:主從戶關係為空');
				return;
			}
			if ($scope.inputVO.rel_type == '99' && $scope.inputVO.rel_type_oth == '') {
				$scope.showErrorMsgInDialog('欄位檢核錯誤:主從戶關係為空');
				return;
			}
			
			$confirm({text: '確定要新增關係戶?'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("CRM661", "rel_add", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", $scope.inputVO,
						function(tota, isError) {
							if (isError) {
								$scope.showErrorMsgInDialog(tota.body.msgData);
								return;
							}
							if (tota.length > 0) {
								$scope.showMsg('新增申請成功，待主管覆核');
								$scope.init();
			                	$scope.inquireInit();
			                	$scope.inquire();
			                };
				});
			});
		}
		
		//刪除關係戶
		$scope.rel_delete = function(row) {
			$confirm({text: '確定要刪除關係戶?'}, {size: 'sm'}).then(function() {
				$scope.inputVO.rel_status = row.REL_STATUS;
				$scope.inputVO.seq = row.SEQ;	
				$scope.sendRecv("CRM661", "rel_delete", "com.systex.jbranch.app.server.fps.crm661.CRM661InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (tota.length > 0) {
							if(tota[0].body.rel_delete == 'D'){
								$scope.showMsg('刪除成功');
							}else{
								$scope.showMsg('刪除申請成功，待主管覆核');
							}
							$scope.init();
			               	$scope.inquireInit();
			               	$scope.inquire();
						};
				});
			});
		}
		
	}
);
		