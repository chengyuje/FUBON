/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS350_UPLOADController',
	function(sysInfoService,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS350_UPLOADController";
		$controller('PMS350Controller', {$scope: $scope});
//		$scope.init = function(){
//			$scope.inputVO = {
//					var deferred = $q.defer();
//					var promise = deferred.promise;
//					rolesName:'',
//					chkCode:[],
//					code:[]
//			}
//			
//		}
		$scope.char_type = [];
		$scope.char_type.push({LABEL: '長條圖', DATA: '1'},
			              	  {LABEL: '趨勢圖', DATA: '2'},
			              	  {LABEL: '圓餅圖', DATA: '3'});
		
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
//			$scope.sendRecv("PMS350", "QUERY_NAME2", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
//					function(tota, isError) {
//						$scope.mappingSet['RPT_NAME_D'] = [];
//						angular.forEach(tota[0].body.namelist, function(row, index, objs){
//							$scope.mappingSet['RPT_NAME_D'].push({LABEL: row.RPT_NAME_D, DATA: row.RPT_NAME_D});
//						});
//				});
			$scope.sendRecv("PMS350", "QUERY_DEPTNAME_UPLOAD", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
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
		}
		
		/**報表檢視扣除超過公告期間以及公告期間無效**/
//		$scope.queryCheck = function(){
//			$scope.sendRecv("PMS350", "queryData_Check", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
//					function(tota, isError){
//						if(!isError){
//							if(tota[0].body.resultList2.length == 0) {
//			        			return;
//			        		}
//							$scope.paramList2 = tota[0].body.resultList2;
//							$scope.outputVO2 = tota[0].body;
//							angular.forEach($scope.paramList2, function(row) {
//								
//								row.default1 = false;
//								row.default2 = false;
//								
//
//								// 適用人員
//								if(row.USER_ROLES) {
//									var role = [];
//									var temp = row.USER_ROLES.split("、");
//									var temp2 = row.USER_ROLES.split("、");
//									row.use_roles_temp = [];
//									row.use_roles_temp2 = [];
//									for(var i = 0; i < temp.length; ++i){
//										if(sysInfoService.getPriID() == temp[i]){
//											row.default2 = true;
//										}
//									}
//									
//									angular.forEach(temp, function(wtff) {
//										angular.forEach($scope.CHANNEL_CODE2,function(wtff2){
//											if(wtff == wtff2.PRIVILEGEID){
//												role.push(wtff2.NAME + "<br/>");
//												row.use_roles_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
//											}
//										});
//									});
//									
//									row.USER_ROLES = role.toString();
//									row.USER_ROLES = row.USER_ROLES.replace(/,/g,'');
//								}
//								
//								if(row.UPLOAD_ROLES) {
//									var role = [];
//									var temp = row.UPLOAD_ROLES.split("、");
//									row.upload_roles_temp = [];
//									for(var i = 0; i < temp.length; ++i){
//										if(sysInfoService.getPriID() == temp[i]){
//											row.default1 = true;
//										}
//									}
//									
//									angular.forEach(temp, function(wtff) {
//										angular.forEach($scope.CHANNEL_CODE2,function(wtff2){
//											if(wtff == wtff2.PRIVILEGEID){
//												role.push(wtff2.NAME + "<br/>");
//												row.upload_roles_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
//											}
//										});
//									});
//									
//									row.UPLOAD_ROLES = role.toString();
//									row.UPLOAD_ROLES = row.UPLOAD_ROLES.replace(/,/g,'');
//								}
//								var new_role = [];
//								if(row.ROLES_temp == undefined)
//									row.ROLES_temp = [];
//								for(var i = 0 ; i < row.use_roles_temp.length ; i++){
//									for(var j = 0 ; j < row.ROLES_temp.length ; j++){
//										if(row.use_roles_temp[i].PRIVILEGEID == row.ROLES_temp[j].PRIVILEGEID){
//											row.use_roles_temp[i].NAME = row.use_roles_temp[i].NAME + "(檢視所屬資料)" ;
//										}
//									}
//									new_role.push(row.use_roles_temp[i].NAME + "<br/>");
//									row.NEW_USER_ROLES = new_role.toString();
//									row.NEW_USER_ROLES = row.NEW_USER_ROLES.replace(/,/g,'');
//								}
//								//2017-07-31 by Jacky 如果不是上傳角色也不是可檢視角色清空結果
////								if(row.default2 == false || row.default1 == false){
////									$scope.paramList2 = [];
////								}
//							});
//						}
//			});
//		}
		
		/***查詢資料***/
//		$scope.query = function(){
//			$scope.paramList = [];
//			$scope.inputVO.USER_TYPE = $scope.UPDATE;
//			$scope.sendRecv("PMS350", "queryData", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
//				function(tota, isError) {
//					if (!isError) {
//						if(tota[0].body.resultList.length == 0) {						
//		        			return;
//		        		}
//						$scope.paramList = tota[0].body.resultList;
//						$scope.outputVO = tota[0].body;
//						//=============================================================
//						angular.forEach($scope.paramList, function(row) {
//							
//							row.MARQUEE_FLAG_Temp = row.MARQUEE_FLAG;							
//							row.MARQUEE_FLAG = row.MARQUEE_FLAG == 'Y' ? '啟用':'未啟用';
//							
//							row.default1 = false;
//							row.default2 = false;
//							
//							// 適用人員
//							if(row.USER_ROLES) {
//								var role = [];
//								var temp = row.USER_ROLES.split("、");
//								row.use_roles_temp = [];
//								for(var i = 0; i < temp.length; ++i){
//									if(sysInfoService.getPriID() == temp[i]){
//										row.default2 = true;
//									}
//								}
//								
//								angular.forEach(temp, function(wtff) {
//									angular.forEach($scope.CHANNEL_CODE2,function(wtff2){
//										if(wtff == wtff2.PRIVILEGEID){
//											role.push(wtff2.NAME + "<br/>");
//											row.use_roles_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
//										}
//									});
//								});
//								
//								row.USER_ROLES = role.toString();
//								row.USER_ROLES = row.USER_ROLES.replace(/,/g,'');
//							}
//							
//							if(row.UPLOAD_ROLES) {
//								var role = [];
//								var temp = row.UPLOAD_ROLES.split("、");
//								row.upload_roles_temp = [];
//								for(var i = 0; i < temp.length; ++i){
//									if(sysInfoService.getPriID() == temp[i]){
//										row.default1 = true;
//									}
//								}
//								
//								angular.forEach(temp, function(wtff) {
//									angular.forEach($scope.CHANNEL_CODE2,function(wtff2){
//										if(wtff == wtff2.PRIVILEGEID){
//											role.push(wtff2.NAME + "<br/>");
//											row.upload_roles_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
//										}
//									});
//								});
//								
//								row.UPLOAD_ROLES = role.toString();
//								row.UPLOAD_ROLES = row.UPLOAD_ROLES.replace(/,/g,'');
//							}
//							
//							if(row.ROLES) {
//								var role = [];
//								var temp = row.ROLES.split("、");
//								row.ROLES_temp = [];
//								for(var i = 0; i < temp.length; ++i){
//									if(sysInfoService.getPriID() == temp[i]){
//										row.default1 = true;
//									}
//								}
//								
//								angular.forEach(temp, function(wtff) {
//									angular.forEach($scope.CHANNEL_CODE2,function(wtff2){
//										if(wtff == wtff2.PRIVILEGEID){
//											role.push(wtff2.NAME + "(檢視所屬資料)<br/>");
//											row.ROLES_temp.push({'PRIVILEGEID': wtff, 'NAME': wtff2.NAME});
//										}
//									});
//								});
//								
//								row.ROLES = role.toString();
//								row.ROLES = row.ROLES.replace(/,/g,'');
//							}
//						});
//						//=============================================================
//						return;
//					}
//			}); 
//		};
		
		var currDate = new Date();
		$scope.init = function() {
			var deferred = $q.defer();
			var promise = deferred.promise;
			$scope.inputVO = {
					RPT_TYPE:'',		
					RPT_DEPT:'',
					USER_TYPE:'',   //java 程式會判斷此參數. 不帶值會查全部
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
			};		
			$scope.saveBtn = false;         // 新增報表用
			$scope.chkCode = [];            // 新增報表用 - 可上傳角色list
			$scope.chkCode2= [];            // 新增報表用 - 可使用角色list
			$scope.mappingSet['timeE'] = [];
			$scope.mappingSet['timeE2'] = [];
			$scope.paramList = [];
			$scope.paramList2 = [];
			$scope.CHANNEL_CODE2 = [];
			$scope.def = false;
			var PRI_ID=[];
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
//							$scope.inputVO.default4 = false;
//							$scope.CHANNEL_CODE2 = tota[0].body.resultList;
//							for(var i = 0; i < tota[0].body.resultList.length; i++){
//								PRI_ID[i] = tota[0].body.resultList[i].PRIVILEGEID;
//							}
//							for(var j = 0; j < PRI_ID.length; j++){
//		                		if(sysInfoService.getPriID() == PRI_ID[j]){
//		                			$scope.inputVO.default4 = true;
//		                		}
//		                	}
//		                     deferred.resolve();
//		                     return;
//						}				
//			});		
//			//===================================================================================
//			promise.then(function(){
//				var ID=[];
//				$scope.sendRecv("PMS350", "Authority", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO",{} ,
//						function(tota, isError) {
//		                	if (!isError) {
//		                		$scope.showErrorMsg(tota[0].body.msgData);
//		                	}
//		                	if(tota[0].body.resultList.length == 0){
//		                		return;	
//					        }	
//		                	ID = tota[0].body.resultList;        
//		                	for(var i = 0; i < ID.length; ++i){
//		                		$scope.inputVO.ID[i]=ID[i].EMP_ID;
//		                	}
//				});
//
//				var EMP_ID=[];
//				$scope.sendRecv("PMS350", "AuthorityOf175B", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO",{},
//						function(tota, isError) {
//		                	if (!isError) {
//		                		$scope.showErrorMsg(tota[0].body.msgData);
//		                	}
//		                	if(tota[0].body.resultList.length == 0){
//		                		return;	
//					        }
//		                	$scope.inputVO.default3 = false;
//		                	EMP_ID = tota[0].body.resultList;
//		                	//適用人員
//		                	for(var i = 0; i < EMP_ID.length; ++i){
//		                		if(sysInfoService.getUserID() == EMP_ID[i].EMP_ID){
//		                			$scope.inputVO.default3 = true;
//		                		}
//		                	}
//				});
//				$scope.query_insert();
////				$scope.queryCheck();
////				$scope.query();
//			});
			//===================================================================================			
			return deferred.promise;	
			
		};
		$scope.init();
		
//		$scope.aa=function(){
//			$scope.inputVO.USER_TYPE = $scope.type;
//	    	   $scope.sendRecv("PMS350", "personnel", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
//						function(tota, isError) {
//	    		              if (!isError) {
//								if(tota[0].body.resultList.length == 0) {
//									
//									$scope.showMsg("ehl_01_common_009");
//		                			return;
//		                		}
//								$scope.paramList =tota[0].body.resultList;
//								$scope.len=$scope.paramList.length;
//								return;
//							}				
//				});
//	       }
//	       $scope.aa();  
//	       
//	       
//        $scope.toggleSelection = function (data) {
//        	var idx = $scope.inputVO.chkCode.indexOf(data);
//        	if (idx > -1) {
//        		$scope.inputVO.chkCode.splice(idx, 1);
//        	} else {
//        		$scope.inputVO.chkCode.push(data);
//        	}
//        };
//        
//        $scope.authoritySelection = function (data) {
//        	var idx = $scope.inputVO.code.indexOf(data);
//        	if (idx > -1) {
//        		$scope.inputVO.code.splice(idx, 1);
//        	} else {
//        		$scope.inputVO.code.push(data);
//        	}
//        };
        
        $scope.save = function() {
        	if($scope.inputVO.USER_TYPE == 'uploader'){
        		$scope.closeThisDialog($scope.inputVO.chkCode);
        	}else{
        		$scope.closeThisDialog($scope.inputVO);
        	}
        	
        };
        
        $scope.NAME_CHANGE = function(){
			$scope.sendRecv("PMS350", "QUERY_NAME_UPLOAD", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['RPT_NAME_D'] = [];
						angular.forEach(tota[0].body.namelist, function(row, index, objs){
							$scope.mappingSet['RPT_NAME_D'].push({LABEL: row.RPT_NAME_D, DATA: row.RPT_NAME_D});
						});
				});
		}
		$scope.TYPE_CHANGES = function(){
			$scope.sendRecv("PMS350", "QUERY_TYPE_UPLOAD", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", $scope.inputVO,
					function(tota, isError) {
						$scope.mappingSet['RPT_TYPE'] = [];
						angular.forEach(tota[0].body.typelist, function(row, index, objs){
							$scope.mappingSet['RPT_TYPE'].push({LABEL: row.RPT_TYPE, DATA: row.RPT_TYPE});
						});
				});
		}
        
		/***清除***/
		$scope.repeatinit = function(){
			$scope.inputVO.RPT_TYPE = '';		
			$scope.inputVO.RPT_DEPT = '';
			$scope.inputVO.rptName = '';
			$scope.inputVO.report_description = '';
		};
		/***檢視***/
		$scope.Only = function(row, flag){
			var s=$scope.CHANNEL_CODE2;
			var y=true;
			
			var dialog=ngDialog.open({
				template:'assets/txn/PMS350/PMS350_EDIT.html',	
				className:'PMS350_EDIT',	
				controller:['$scope',function($scope){
					$scope.default2=true;
					$scope.row = row;
					$scope.row.MARQUEE_FLAG = row.MARQUEE_FLAG_Temp;
					$scope.CHANNEL_CODE2 = s; 
	             	$scope.x = y;
	             	$scope.onlyflag = flag;
				}]					
			});
		};
		
		/**修改功能**/
		$scope.update = function(row, flag) {
			var s = $scope.CHANNEL_CODE2;
			var ID = $scope.inputVO.ID;
			var dialog = ngDialog.open({			      
				template: 'assets/txn/PMS350/PMS350_EDIT.html',
				className: 'PMS350_EDIT',
				controller: ['$scope', function($scope) {
					$scope.row = row;
					$scope.row.MARQUEE_FLAG = row.MARQUEE_FLAG_Temp;
					$scope.row.ROLES_temp = row.ROLES_temp;
					$scope.CHANNEL_CODE2 = s;
					$scope.UPDATE = true;
					$scope.ID = ID;
					$scope.FILENAME = row.FILENAME;
					// 2017/7/5 我不改舊邏輯
					$scope.wtfflag = flag;
					$scope.wtfuser2 = row.use_roles_temp;
					$scope.roles = row.ROLES_temp;
					$scope.wtfupload = row.upload_roles_temp;
					$scope.uploadFlag = 'Y';
				}]
			});          
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
//					$scope.init(); 	
					$scope.query();						
				}
			});
		};
		
		/**檢查報表名稱筆數功能**/
		$scope.checkCountOfData = function(row){
			$scope.sendRecv("PMS350", "dataCount", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", {'rptName': row.RPT_NAME},
	       			function(totas, isError) {
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
				$scope.sendRecv("PMS350", "delData", "com.systex.jbranch.app.server.fps.pms350.PMS350InputVO", {'seq': row.SEQ},
	       			function(totas, isError) {
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
		
		$scope.pickData = function(row){
			var char_type = row.CHAR_TYPE
			var class_name = ''
			if (row.CHAR_TYPE == '1') {
				class_name = 'PMS350_COLUMN';
			} else if (row.CHAR_TYPE == '2') {
				class_name = 'PMS350_SPLINE';
			} else if (row.CHAR_TYPE == '3') {
				class_name = 'PMS350_PIE';
			}
			var dialog=ngDialog.open({
				template: 'assets/txn/PMS350/' + class_name + '.html',	
				className: class_name,	
				controller:['$scope',function($scope){
					$scope.char_type = char_type;
				}]					
			});				
		}
		
});
