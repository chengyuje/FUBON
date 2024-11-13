'use strict';
eSoafApp.controller('PMS350Controller', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter,$timeout,sysInfoService,validateService) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS350Controller";				               
	$scope.open = function($event, index) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope['opened'+index] = true;
	};				
	
	// date picker
	$scope.bgn_sDateOptions = {
		maxDate: $scope.maxDate,
		minDate: $scope.minDate
	};
	$scope.bgn_eDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
	// config
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.bgn_sDateOptions.maxDate = $scope.inputVO.endDate || $scope.maxDate;
		$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
	};
	
	
	
	//初始化
	var currDate = new Date();
	$scope.init = function() {
		var deferred = $q.defer();
		var promise = deferred.promise;
		$scope.inputVO = {
				RPT_TYPE:'',		
				RPT_DEPT:'',
				USER_TYPE:'not user',   //java 程式會判斷此參數. 不帶值會查全部
				ID:[],
				report_description:'',
				USER_ROLES:[],
				UPLOAD_ROLES:[],        // 新增報表用 - 可上傳角色list
				roles: [],              // 新增報表用 - 可使用角色list
				sCreDate:currDate,      // 新增報表用
				eCreDate:currDate,      // 新增報表用
				valid:'Y',              // 新增報表用
				rptName:'',             // 新增報表用
				rptExplain:'',          // 新增報表用
				marqueeFlag:'N',        // 新增報表用
				marqueeTxt:'',          // 新增報表用
				RPT_DEPT:'',            // 新增報表用
				RPT_TYPE:'',             // 新增報表用
				default3:'false'
//					rolesName:projInfoService.getRoleName()
		};		
		$scope.saveBtn = false;         // 新增報表用
		$scope.chkCode = [];            // 新增報表用 - 可上傳角色list
		$scope.chkCode2= [];            // 新增報表用 - 可使用角色list
		$scope.mappingSet['timeE'] = [];
		$scope.mappingSet['timeE2'] = [];
		$scope.paramList = [];
		$scope.paramList2 = [];
		$scope.CHANNEL_CODE2 = [];
		$scope.mappingSet['RPT_NAME_D'] = [];
		$scope.def = false;
		var PRI_ID=[];
		
		if($scope.METHOD_TYPE != undefined){
			$scope.inputVO.USER_TYPE = ''
		}
		
		$scope.sendRecv("PMS350", "personnel", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {

				if(tota[0].body.resultList.length == 0) {
					$scope.paramList = [];
					$scope.csvList = [];
					$scope.outputVO = [];
					
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.inputVO.default4 = false;
				$scope.CHANNEL_CODE2 = tota[0].body.resultList;
				
				for(var i = 0; i < tota[0].body.resultList.length; i++){
					PRI_ID[i] = tota[0].body.resultList[i].PRIVILEGEID;
				}
				
				for(var j = 0; j < PRI_ID.length; j++){
            		if(sysInfoService.getPriID() == PRI_ID[j]){
            			$scope.inputVO.default4 = true;
            		}
            	}
				
				if($scope.METHOD_TYPE == 'UPLOAD'){
					$scope.inputVO.USER_TYPE = 'true';
				}
				
				deferred.resolve();
				return;
			}				
		});		
		//===================================================================================
		promise.then(function(){
			var ID=[];
			var PRI_ID=[];
			$scope.inputVO.USER_TYPE = '';
			$scope.sendRecv("PMS350", "personnel", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {

					if(tota[0].body.resultList.length == 0) {
						$scope.paramList = [];
						$scope.csvList = [];
						$scope.outputVO = [];
						
						$scope.showMsg("ehl_01_common_009");
            			return;
            		}
					
					$scope.CHANNEL_CODE3 = tota[0].body.resultList;
					
					if($scope.METHOD_TYPE == undefined){
						$scope.queryCheck();
					}
                    return;
				}				
			});	
			$scope.sendRecv("PMS350", "Authority", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", {}, function(tota, isError) {
            	if (!isError) {
            		$scope.showErrorMsg(tota[0].body.msgData);
            	}

            	if(tota[0].body.resultList.length == 0){
            		return;	
		        }	
            	
            	ID = tota[0].body.resultList;     

            	for(var i = 0; i < ID.length; ++i){
            		$scope.inputVO.ID[i]=ID[i].EMP_ID;
            	}
			});

			var EMP_ID=[];
			$scope.sendRecv("PMS350", "AuthorityOf175B", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO",{}, function(tota, isError) {
            	if (!isError) {
            		$scope.showErrorMsg(tota[0].body.msgData);
            	}
            	if(tota[0].body.resultList.length == 0){
            		return;	
		        }
            	
            	$scope.inputVO.default3 = false;
            	EMP_ID = tota[0].body.resultList;
            	
            	//適用人員

            	for(var i = 0; i < EMP_ID.length; ++i){
            		if(sysInfoService.getUserID() == EMP_ID[i].EMP_ID){
            			$scope.inputVO.default3 = true;
            		}
            	}
			});
			$scope.query_insert();
			
			if($scope.METHOD_TYPE != undefined){
				$scope.query();
			}
		});
		//===================================================================================			
		return deferred.promise;	
		
	};
	$scope.init();			
	/***新增資料***/
	$scope.insert = function(row){
		var s = $scope.CHANNEL_CODE3;
		var ID = $scope.inputVO.ID;
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS350/PMS350_EDIT.html',
			className: 'PMS350_EDIT',
			controller: ['$scope', function($scope) {
				$scope.row = row;
				$scope.default2=true;
				$scope.ID=ID;
				$scope.CHANNEL_CODE3 = s;
				$scope.INSERT = true;
			}]
		});          
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.init();					
			}
		});
	};
	
	/**檢視報表**/
	$scope.Only = function(row, flag){
		var s=$scope.CHANNEL_CODE3;
		var y=true;
		
		var dialog=ngDialog.open({
			template:'assets/txn/PMS350/PMS350_EDIT.html',	
			className:'PMS350_EDIT',	
			controller:['$scope',function($scope){
				$scope.default2=true;
				$scope.row = row;
				$scope.row.MARQUEE_FLAG = row.MARQUEE_FLAG_Temp;
				$scope.CHANNEL_CODE3 = s; 
             	$scope.x = y;
             	$scope.onlyflag = flag;
			}]					
		});
	};
	/**修改功能**/
	$scope.update = function(row, flag) {
		var s = $scope.CHANNEL_CODE3;
		var ID = $scope.inputVO.ID;
		var dialog = ngDialog.open({			      
			template: 'assets/txn/PMS350/PMS350_EDIT.html',
			className: 'PMS350_EDIT',
			controller: ['$scope', function($scope) {
				$scope.row = row;
				$scope.row.MARQUEE_FLAG = row.MARQUEE_FLAG_Temp;
				$scope.row.ROLES_temp = row.ROLES_temp;
				$scope.CHANNEL_CODE3 = s;
				$scope.UPDATE = true;
				$scope.ID = ID;
				$scope.FILENAME = row.FILENAME;
				// 2017/7/5 我不改舊邏輯
				$scope.wtfflag = flag;
				$scope.wtfuser2 = row.use_roles_temp;
				$scope.roles = row.ROLES_temp;
				$scope.wtfupload = row.upload_roles_temp;
			}]
		});          
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.init(); 	
				//$scope.query();						
			}
		});
	};
	
	/**檢查報表名稱筆數功能**/
	$scope.checkCountOfData = function(row){
		$scope.sendRecv("PMS350", "dataCount", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", {'rptName': row.RPT_NAME}, function(totas, isError) {
			if(isError){
				$scope.showErrorMsg(totas[0].body.msgData);
			}
			
			if(totas.length > 0){
				if(totas[0].body.countList[0].DATANUM > 1){
					$scope.delData(row);
				}else{
					$scope.showErrorMsg("此資料為該報表名稱最後一筆，無法刪除!");
				}
			}
		});
	}
		
	/**刪除功能**/
	$scope.delData = function(row){
		$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
			$scope.sendRecv("PMS350", "delData", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", {'seq': row.SEQ}, function(totas, isError) {
				if (isError) {
					$scope.showErrorMsg(totas[0].body.msgData);
				}

				if (totas.length > 0) {
					$scope.showSuccessMsg('刪除成功');
					//$scope.init();
				};
        	});
		});
	};
	
	/**報表檢視扣除超過公告期間以及公告期間無效**/
	$scope.queryCheck = function(){
//			var deferred = $q.defer();
//			var promise = deferred.promise;
//			var PRI_ID=[];
//			$scope.inputVO.USER_TYPE = '';
//			$scope.sendRecv("PMS350", "personnel", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
//					function(tota, isError) {
//						if (!isError) {
//							if(tota[0].body.resultList.length == 0) {
//								$scope.paramList = [];
//								$scope.csvList = [];
//								$scope.outputVO = [];
//								
//								$scope.showMsg("ehl_01_common_009");
//		            			return;
//		            		}
//							$scope.CHANNEL_CODE2 = tota[0].body.resultList;
//		                    deferred.resolve();
//		                    return;
//						}				
//			});	
//			
//			promise.then(function(){
			$scope.sendRecv("PMS350", "queryData_Check", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError){
				if(!isError){

//					if(tota[0].body.resultList2.length == 0) {
//						return;
//					}
					
					$scope.paramList2 = tota[0].body.resultList2;
					$scope.outputVO2 = tota[0].body;
					angular.forEach($scope.paramList2, function(row) {
						
						row.default1 = false;
						row.default2 = false;
						
						if(row.ROLES) {
							var role = [];
							var temp = row.ROLES.split("、");
							row.ROLES_temp = [];
							
							angular.forEach(temp, function(wtff) {
								angular.forEach($scope.CHANNEL_CODE3,function(wtff2){
									if(wtff == wtff2.PRIVILEGEID){
										role.push(wtff2.NAME + "(檢視所屬資料)<br/>");
										row.ROLES_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
									}
								});
							});
							row.ROLES = role.toString();
							row.ROLES = row.ROLES.replace(/,/g,'');
						}
						
						// 適用人員
						if(row.USER_ROLES) {
							var role = [];
							var temp = row.USER_ROLES.split("、");
							row.use_roles_temp = [];
							for(var i = 0; i < temp.length; ++i){
								if(sysInfoService.getPriID() == temp[i]){
									row.default2 = true;
								}
							}
							
							angular.forEach(temp, function(wtff) {
								angular.forEach($scope.CHANNEL_CODE3,function(wtff2){
									if(wtff == wtff2.PRIVILEGEID){
										role.push(wtff2.NAME + "<br/>");
										row.use_roles_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
									}
								});
							});
							
							row.USER_ROLES = role.toString();
							row.USER_ROLES = row.USER_ROLES.replace(/,/g,'');
						} 
						
						if(row.CREATOR == sysInfoService.getUserID() || row.MODIFIER == sysInfoService.getUserID()) {	//#5908 報表建立者也可以看
							row.default2 = true;
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
								angular.forEach($scope.CHANNEL_CODE3,function(wtff2){
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
						
						var new_role = [];
						if(row.ROLES_temp == undefined)
							row.ROLES_temp = [];
						
						if(row.use_roles_temp == undefined)
							row.use_roles_temp = [];
						
						for(var i = 0 ; i < row.use_roles_temp.length ; i++){
							for(var j = 0 ; j < row.ROLES_temp.length ; j++){
								if(row.use_roles_temp[i].PRIVILEGEID == row.ROLES_temp[j].PRIVILEGEID){
									row.use_roles_temp[i].NAME = row.use_roles_temp[i].NAME + "(檢視所屬資料)" ;
								}
							}
							new_role.push(row.use_roles_temp[i].NAME + "<br/>");
							row.USER_ROLES = new_role.toString();
							row.USER_ROLES = row.USER_ROLES.replace(/,/g,'');
						}

						if(row.RPT_NAME.length > 20){
							row.RPT_NAME = row.RPT_NAME.substring(0,20) + "<br/>" + row.RPT_NAME.substring(20);
						}

						if(row.RPT_EXPLAIN != null && row.RPT_EXPLAIN.length > 20){
							row.RPT_EXPLAIN = row.RPT_EXPLAIN.substring(0,20) + "<br/>" + row.RPT_EXPLAIN.substring(20);
						}
						
					});
				}
			});
//			});
//			return deferred.promise;	
		
	}
		
	/***查詢資料***/
	$scope.query = function(){
		if($scope.METHOD_TYPE == 'UPLOAD')
			$scope.inputVO.USER_TYPE = 'true';
		
		$scope.sendRecv("PMS350", "queryData", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {

				if(tota[0].body.resultList.length == 0) {						
					return;
				}
				
				$scope.paramList = tota[0].body.resultList;
				$scope.outputVO = tota[0].body;
				//=============================================================
				angular.forEach($scope.paramList, function(row) {
					
					row.MARQUEE_FLAG_Temp = row.MARQUEE_FLAG;							
					row.MARQUEE_FLAG = row.MARQUEE_FLAG == 'Y' ? '啟用':'未啟用';
					
					row.default1 = false;
					row.default2 = false;

					// 適用人員
					if(row.USER_ROLES) {
						var role = [];
						var temp = row.USER_ROLES.split("、");
						row.use_roles_temp = [];
						for(var i = 0; i < temp.length; ++i){
							if(sysInfoService.getPriID() == temp[i]){
								row.default2 = true;
							}
						}
						
						angular.forEach(temp, function(wtff) {
							angular.forEach($scope.CHANNEL_CODE3,function(wtff2){
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
							angular.forEach($scope.CHANNEL_CODE3,function(wtff2){
								if(wtff == wtff2.PRIVILEGEID){
									role.push(wtff2.NAME + "<br/>");
									row.upload_roles_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
								}
							});
						});
						
						row.UPLOAD_ROLES = role.toString();
						row.UPLOAD_ROLES = row.UPLOAD_ROLES.replace(/,/g,'');
					}

					if(row.ROLES) {
						var role = [];
						var temp = row.ROLES.split("、");
						row.ROLES_temp = [];
						for(var i = 0; i < temp.length; ++i){
							if(sysInfoService.getPriID() == temp[i]){
								row.default1 = true;
							}
						}
						
						angular.forEach(temp, function(wtff) {
							angular.forEach($scope.CHANNEL_CODE3,function(wtff2){
								if(wtff == wtff2.PRIVILEGEID){
									role.push(wtff2.NAME + "(檢視所屬資料)<br/>");
									row.ROLES_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
								}
							});
						});
						
						row.ROLES = role.toString();
						row.ROLES = row.ROLES.replace(/,/g,'');
					}
					
					var new_role = [];
					if(row.ROLES_temp == undefined)
						row.ROLES_temp = [];
					
					if(row.use_roles_temp == undefined)
						row.use_roles_temp = [];

					for(var i = 0 ; i < row.use_roles_temp.length ; i++){
						for(var j = 0 ; j < row.ROLES_temp.length ; j++){
							if(row.use_roles_temp[i].PRIVILEGEID == row.ROLES_temp[j].PRIVILEGEID){
								row.use_roles_temp[i].NAME = row.use_roles_temp[i].NAME + "(檢視所屬資料)" ;
							}
						}
						new_role.push(row.use_roles_temp[i].NAME + "<br/>");
						row.NEW_USER_ROLES = new_role.toString();
						row.NEW_USER_ROLES = row.NEW_USER_ROLES.replace(/,/g,'');
					}
				});
				//=============================================================
				return;
			}
		}); 
	};
	
	
	/***清除***/
	$scope.repeatinit = function(){
		$scope.inputVO.RPT_TYPE = '';		
		$scope.inputVO.RPT_DEPT = '';
		$scope.inputVO.rptName = '';
		$scope.inputVO.report_description = '';
	};
	$scope.clear = function(){
		$scope.inputVO.rptName = '';		
		$scope.inputVO.RPT_DEPT_1 = '';
		$scope.inputVO.RPT_DEPT_2 = '';
		$scope.inputVO.RPT_DEPT = '';
		$scope.inputVO.RPT_TYPE2 = '';
		$scope.inputVO.RPT_TYPE = '';
		$scope.inputVO.UPLOAD_ROLES = '';
		$scope.inputVO.USER_ROLES = '';
		$scope.chkCode3 = [];
		$scope.chkCode2 = [];
		$scope.chkCode  = [];
	};
	
	/***跳轉報表查詢畫面***/
	$scope.RPTDetail = function(row) {
		var dialog = ngDialog.open({
			template: 'assets/txn/PMS350/PMS350_DETAIL.html',
			className: 'PMS350_DETAIL',
			showClose: false,
			closeByEscape: false,
            controller: ['$scope', function($scope) {
            	$scope.DataRow = row;
            }]
		});
		dialog.closePromise.then(function (data) {
			
		});
	};	
	
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
		$scope.sendRecv("PMS350", "QUERY_DEPTNAME2", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError) {
			$scope.mappingSet['DEPT_NAME_D'] = [];

			angular.forEach(tota[0].body.deptNamelist, function(row, index, objs){
				$scope.mappingSet['DEPT_NAME_D'].push({LABEL: row.DEPT_NAME_D, DATA: row.DEPT_ID});
			});
		});
		$scope.sendRecv("PMS350", "QUERY_NAME", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError) {
			$scope.mappingSet['RPT_NAME'] = [];

			angular.forEach(tota[0].body.namelist, function(row, index, objs){
				$scope.mappingSet['RPT_NAME'].push({LABEL: row.RPT_NAME, DATA: row.RPT_NAME});
			});
		});
//			$scope.sendRecv("PMS350", "QUERY_NAME2", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
//					function(tota, isError) {
//						$scope.mappingSet['RPT_NAME_D'] = [];
//						angular.forEach(tota[0].body.namelist, function(row, index, objs){
//							$scope.mappingSet['RPT_NAME_D'].push({LABEL: row.RPT_NAME_D, DATA: row.RPT_NAME_D});
//						});
//				});
		$scope.sendRecv("PMS350", "QUERY_DEPTNAME", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError) {
			$scope.mappingSet['DEPT_NAME'] =[];

			angular.forEach(tota[0].body.deptNamelist, function(row, index, objs){
				$scope.mappingSet['DEPT_NAME'].push({LABEL: row.DEPT_NAME, DATA: row.DEPT_ID});
			});
		});
		
		//選單1
		$scope.inputVO.ORG_TYPE = '10';
		$scope.sendRecv("PMS350", "QUERY_DEPT", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError) {
			$scope.mappingSet['DEPT_NAME_1'] = [];
			angular.forEach(tota[0].body.deptlist, function(row, index, objs){
				$scope.mappingSet['DEPT_NAME_1'].push({LABEL: row.DEPT_NAME, DATA: row.DEPT_ID});
			});
		});
	}
	
	$scope.NAME_CHANGE = function(){
		$scope.sendRecv("PMS350", "QUERY_NAME_VIEW", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError) {
			$scope.mappingSet['RPT_NAME_D'] = [];

			angular.forEach(tota[0].body.namelist, function(row, index, objs){
				$scope.mappingSet['RPT_NAME_D'].push({LABEL: row.RPT_NAME_D, DATA: row.RPT_NAME_D});
			});
		});
	}
	
	$scope.TYPE_CHANGES = function(){
		$scope.sendRecv("PMS350", "QUERY_TYPE_VIEW", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO, function(tota, isError) {
			$scope.mappingSet['RPT_TYPE'] = [];

			angular.forEach(tota[0].body.typelist, function(row, index, objs){
				$scope.mappingSet['RPT_TYPE'].push({LABEL: row.RPT_TYPE, DATA: row.RPT_TYPE});
			});
		});
	}
	
	$scope.insertData = function(){
		var dialog = ngDialog.open({			      
			template: 'assets/txn/PMS350/PMS350_INSERTDATA.html',
			className: 'PMS350_INSERTDATA',
			controller: ['$scope', function($scope) {
				$scope.METHOD_TYPE = 'INSERT';
			}]
		});     
		dialog.closePromise.then(function (data) {
			$scope.query_insert();
			$scope.queryCheck();
        });
     }
	
	$scope.upload = function(row){
		var s = $scope.CHANNEL_CODE3;
		var ID = $scope.inputVO.ID;
		var dialog = ngDialog.open({			      
			template: 'assets/txn/PMS350/PMS350_UPLOAD.html',
			className: 'PMS350_UPLOAD',
			controller: ['$scope', function($scope) {
				$scope.row = row;
				$scope.METHOD_TYPE = 'UPLOAD';
				//$scope.row.MARQUEE_FLAG = row.MARQUEE_FLAG_Temp;
				//$scope.row.ROLES_temp = row.ROLES_temp;
				$scope.CHANNEL_CODE3 = s;
				$scope.UPDATE = true;
				$scope.ID = ID;
				//$scope.FILENAME = row.FILENAME;
				// 2017/7/5 我不改舊邏輯
				//$scope.wtfflag = flag;
				//$scope.wtfuser2 = row.use_roles_temp;
				//$scope.roles = row.ROLES_temp;
				//$scope.wtfupload = row.upload_roles_temp;
			}]
		});     
		dialog.closePromise.then(function (data) {
			$scope.query_insert();
        });
     }
});
