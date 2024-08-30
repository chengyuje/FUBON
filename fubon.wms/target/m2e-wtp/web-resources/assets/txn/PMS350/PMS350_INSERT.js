/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS350_INSERTController',
	function(sysInfoService,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS350_INSERTController";
      //新增報表查詢
		$scope.query_insert = function(){
			$scope.type_lock = false;
			$scope.mappingSet['RPT_NAME'] = [];		//報表名稱
			$scope.mappingSet['RPT_NAME_D'] = [];	//報表名稱扣除已失效
			$scope.mappingSet['RPT_TYPE'] = [];		//報表類型
			$scope.mappingSet['DEPT_NAME'] = [];	//報表提供單位
			$scope.mappingSet['DEPT_NAME_D'] = [];	//報表提供單位扣除已失效
			$scope.mappingSet['DEPT_NAME_1'] = [];
			$scope.mappingSet['DEPT_NAME_2'] = [];
			$scope.mappingSet['DEPT_NAME_3'] = [];
			$scope.inputVO.DEPT_NAME_1 = '';
			$scope.inputVO.DEPT_NAME_2 = '';
			$scope.inputVO.DEPT_NAME_3 = '';
			$scope.sendRecv("PMS350", "QUERY_DEPTNAME2", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['DEPT_NAME_D'] = [];
						angular.forEach(tota[0].body.deptNamelist, function(row, index, objs){
							$scope.mappingSet['DEPT_NAME_D'].push({LABEL: row.DEPT_NAME_D, DATA: row.DEPT_ID});
						});
				});
			$scope.sendRecv("PMS350", "QUERY_NAME", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['RPT_NAME'] = [];
						angular.forEach(tota[0].body.namelist, function(row, index, objs){
							$scope.mappingSet['RPT_NAME'].push({LABEL: row.RPT_NAME, DATA: row.RPT_NAME});
						});
				});
			$scope.sendRecv("PMS350", "QUERY_NAME2", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['RPT_NAME_D'] = [];
						angular.forEach(tota[0].body.namelist, function(row, index, objs){
							$scope.mappingSet['RPT_NAME_D'].push({LABEL: row.RPT_NAME_D, DATA: row.RPT_NAME_D});
						});
				});
			$scope.sendRecv("PMS350", "QUERY_DEPTNAME", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['DEPT_NAME'] =[];
						angular.forEach(tota[0].body.deptNamelist, function(row, index, objs){
							$scope.mappingSet['DEPT_NAME'].push({LABEL: row.DEPT_NAME, DATA: row.DEPT_ID});
						});
				});
			
			//選單1
			$scope.inputVO.ORG_TYPE = '10';
			$scope.sendRecv("PMS350", "QUERY_DEPT", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
				function(tota, isError) {
					$scope.mappingSet['DEPT_NAME_1'] = [];
					angular.forEach(tota[0].body.deptlist, function(row, index, objs){
						$scope.mappingSet['DEPT_NAME_1'].push({LABEL: row.DEPT_NAME, DATA: row.DEPT_ID});
					});
			});
			
			$scope.sendRecv("PMS350", "QUERY_NAME2", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['RPT_NAME_D'] = [];
						angular.forEach(tota[0].body.namelist, function(row, index, objs){
							$scope.mappingSet['RPT_NAME_D'].push({LABEL: row.RPT_NAME_D, DATA: row.RPT_NAME_D});
						});
				});
			
			$scope.sendRecv("PMS350", "QUERY_TYPE", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['RPT_TYPE'] = [];
						angular.forEach(tota[0].body.typelist, function(row, index, objs){
							$scope.mappingSet['RPT_TYPE'].push({LABEL: row.RPT_TYPE, DATA: row.RPT_TYPE});
						});
				});
		}
		
		$scope.NAME_CHANGE = function(){
			$scope.sendRecv("PMS350", "QUERY_NAME2", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['RPT_NAME_D'] = [];
						angular.forEach(tota[0].body.namelist, function(row, index, objs){
							$scope.mappingSet['RPT_NAME_D'].push({LABEL: row.RPT_NAME_D, DATA: row.RPT_NAME_D});
						});
				});
		}
		$scope.TYPE_CHANGES = function(){
			$scope.sendRecv("PMS350", "QUERY_TYPE", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['RPT_TYPE'] = [];
						angular.forEach(tota[0].body.typelist, function(row, index, objs){
							$scope.mappingSet['RPT_TYPE'].push({LABEL: row.RPT_TYPE, DATA: row.RPT_TYPE});
						});
				});
		}
		
		/**報表檢視扣除超過公告期間以及公告期間無效**/
		$scope.queryCheck = function(){
			$scope.sendRecv("PMS350", "queryData_Check", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError){
						if(!isError){
							if(tota[0].body.resultList2.length == 0) {
			        			return;
			        		}
							$scope.paramList2 = tota[0].body.resultList2;
							$scope.outputVO2 = tota[0].body;
							angular.forEach($scope.paramList2, function(row) {
								
								row.default1 = false;
								row.default2 = false;
								

								// 適用人員
								if(row.USER_ROLES) {
									var role = [];
									var temp = row.USER_ROLES.split("、");
									var temp2 = row.USER_ROLES.split("、");
									row.use_roles_temp = [];
									row.use_roles_temp2 = [];
									for(var i = 0; i < temp.length; ++i){
										if(sysInfoService.getPriID() == temp[i]){
											row.default2 = true;
										}
									}
									
									angular.forEach(temp, function(wtff) {
										angular.forEach($scope.CHANNEL_CODE2,function(wtff2){
											if(wtff == wtff2.PRIVILEGEID){
												role.push(wtff2.NAME + "<br/>");
												row.use_roles_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
											}
										});
									});
									
									row.USER_ROLES = role.toString();
									row.USER_ROLES = row.USER_ROLES.replace(/,/g,'');
								}
								
								if(row.UPLOAD_ROLES) {
									var role = [];
									var temp = row.UPLOAD_ROLES.split("、");
									row.upload_roles_temp = [];
									for(var i = 0; i < temp.length; ++i){
										if(sysInfoService.getPriID() == temp[i]){
											row.default1 = true;
										}
									}
									
									angular.forEach(temp, function(wtff) {
										angular.forEach($scope.CHANNEL_CODE2,function(wtff2){
											if(wtff == wtff2.PRIVILEGEID){
												role.push(wtff2.NAME + "<br/>");
												row.upload_roles_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
											}
										});
									});
									
									row.UPLOAD_ROLES = role.toString();
									row.UPLOAD_ROLES = row.UPLOAD_ROLES.replace(/,/g,'');
								}
								//2017-07-31 by Jacky 如果不是上傳角色也不是可檢視角色清空結果
//								if(row.default2 == false || row.default1 == false){
//									$scope.paramList2 = [];
//								}
							});
						}
			});
		}
        
		$scope.DEPT_change = function(type){
			if(type == '20'){
				//選單2
				$scope.inputVO.DEPT_NAME_2 = '';
				$scope.inputVO.DEPT_NAME_3 = '';
				$scope.sendRecv("PMS350", "QUERY_DEPT", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", {'ORG_TYPE':type,'DEPT_NAME':$scope.inputVO.RPT_DEPT_1},
					function(tota, isError) {
						$scope.mappingSet['DEPT_NAME_2'] = [];
						angular.forEach(tota[0].body.deptlist, function(row, index, objs){
							$scope.mappingSet['DEPT_NAME_2'].push({LABEL: row.DEPT_NAME, DATA: row.DEPT_ID});
						});
				});
			}
			if(type == '30'){
				//選單3
				$scope.inputVO.DEPT_NAME_3 = '';
				$scope.sendRecv("PMS350", "QUERY_DEPT", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", {'ORG_TYPE':type,'DEPT_NAME':$scope.inputVO.RPT_DEPT_2},
					function(tota, isError) {
						$scope.mappingSet['DEPT_NAME_3'] = [];
						angular.forEach(tota[0].body.deptlist, function(row, index, objs){
							$scope.mappingSet['DEPT_NAME_3'].push({LABEL: row.DEPT_NAME, DATA: row.DEPT_ID});
						});
				});
			}
		}
		var currDate = new Date();
		$scope.init = function(){
			$scope.inputVO = {
					sCreDate:currDate,
					eCreDate:currDate,
					valid:'N',
					rptName:'',
					rptExplain:'',
					marqueeFlag:'N',
					marqueeTxt:'',
					RPT_DEPT:'',
					RPT_TYPE:'',
					UPLOAD_ROLES:[],
					roles:[],
					USER_ROLES:[],
					EXPORT_YN: false
			}
			$scope.chkCode = [];            
			$scope.chkCode2= [];
			$scope.chkCode3= [];
			$scope.query_insert();
			$scope.queryCheck();
		};
        $scope.init();
		
        $scope.clear = function(){
			$scope.inputVO.rptName = '';		
			$scope.inputVO.RPT_DEPT_1 = '';
			$scope.inputVO.RPT_DEPT_2 = '';
			$scope.inputVO.RPT_DEPT = '';
			$scope.inputVO.RPT_TYPE2 = '';
			$scope.inputVO.RPT_TYPE = '';
			$scope.inputVO.UPLOAD_ROLES = [];
			$scope.inputVO.USER_ROLES = [];
			$scope.inputVO.EXPORT_YN = false;
			$scope.chkCode3 = [];
			$scope.chkCode2 = [];
			$scope.chkCode  = [];
		};
        
        $scope.TYPE_change = function(){
			if($scope.inputVO.RPT_TYPE2 == ''){
				$scope.type_lock = false;
				$scope.inputVO.RPT_TYPE = '';
			}else{
				$scope.type_lock = true;
				$scope.inputVO.RPT_TYPE = $scope.inputVO.RPT_TYPE2;
			}
		}
        //人員查詢
        $scope.qu = function(type,name){
			var dialog = ngDialog.open({			      
				template: 'assets/txn/PMS350/PMS350_INSLIST.html',
				className: 'PMS305_INSLIST',
				controller: ['$scope', function($scope) {
					$scope.name = name;
					$scope.type = type;
				}]
			});     
			dialog.closePromise.then(function (data) {
				if(type == 'uploader'){
	        		$scope.chkCode = data.value;
				}
	        	if(type == 'user'){
	        		$scope.chkCode2 = data.value.chkCode;
	        		$scope.chkCode3 = data.value.code;
	        	}
	        });
         }
        
        $scope.save = function() {
        	if($scope.inputVO.rptName == undefined || $scope.inputVO.rptName ==''){
	    		$scope.showErrorMsg('欄位檢核錯誤:報表名稱為必要輸入欄位');
        		return;
        	}
        	if($scope.inputVO.RPT_TYPE ==''){
	    		$scope.showErrorMsg('欄位檢核錯誤:報表類型為必要輸入欄位');
        		return;
        	}
        	if($scope.inputVO.RPT_DEPT_2 == undefined || $scope.inputVO.RPT_DEPT_2 ==''){
	    		$scope.showErrorMsg('欄位檢核錯誤:報表提供單位為必要輸入欄位');
        		return;
        	}
        	if(($scope.inputVO.RPT_TYPE2 == undefined || $scope.inputVO.RPT_TYPE2 =='') && $scope.inputVO.RPT_TYPE == undefined){
	    		$scope.showErrorMsg('欄位檢核錯誤:報表類型為必要輸入欄位');
        		return;
        	}
//        	$scope.inputVO.roles = $scope.inputVO.chkCode;
			angular.forEach($scope.chkCode, function(row) {
				$scope.inputVO.UPLOAD_ROLES.push(row.PRIVILEGEID);
			});
			angular.forEach($scope.chkCode2, function(row) {
				$scope.inputVO.roles.push(row.PRIVILEGEID);
			});
			angular.forEach($scope.chkCode3, function(row) {
				$scope.inputVO.USER_ROLES.push(row.PRIVILEGEID);
			});
			
			//$scope.inputVO.roles = $scope.inputVO.UPLOAD_ROLES;
			if($scope.inputVO.DEPT_NAME_3 != ''){
				$scope.inputVO.RPT_DEPT = $scope.inputVO.RPT_DEPT;
			}
			if($scope.inputVO.DEPT_NAME_2 != ''){
				$scope.inputVO.RPT_DEPT_2 = $scope.inputVO.RPT_DEPT_2;
			}
			if($scope.inputVO.RPT_DEPT_1 != ''){
				$scope.inputVO.RPT_DEPT_1 = $scope.inputVO.RPT_DEPT_1;
			}else{
//				$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
			}
			if($scope.inputVO.UPLOAD_ROLES == ""){
	    		$scope.showErrorMsg('欄位檢核錯誤:可上傳角色為必要輸入欄位');
        		return;
        	}
			if($scope.inputVO.roles =='' && $scope.inputVO.USER_ROLES == ''){
	    		$scope.showErrorMsg('欄位檢核錯誤:使用角色為必要輸入欄位');
        		return;
        	}
        	$scope.sendRecv("PMS350", "addRPT", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
        		function(totas, isError) {
	        		if (isError) {
	        			$scope.showErrorMsg(totas[0].body.msgData);
	        		}
	        		if (totas.length > 0) {
	        			if(totas[0].body.errorList && totas[0].body.errorList.length > 0) {
	        				angular.forEach(totas[0].body.errorList, function(row) {
	        					$scope.showErrorMsg(row);
	        				});
	        			}
	        			$scope.saveBtn = true;
	        			$scope.showSuccessMsg('ehl_01_common_004');
	        			$scope.closeThisDialog('successful');
	        		};
        	});         		
        };
});