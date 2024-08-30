/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM371Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM371Controller";
		
		// combobox
		getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE", "COMMON.YES_NO", "CRM.TRS_APL_REASON", "CRM.TRS_CALL_REVIEW_TYPE", "CRM.QUERY_TRS_TYPE_1", "CRM.QUERY_TRS_TYPE_2", "CRM.QUERY_TRS_TYPE_3"], function(totas) {
			if (totas) {
				$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.TRS_APL_REASON = totas.data[totas.key.indexOf('CRM.TRS_APL_REASON')];
				$scope.TRS_CALL_REVIEW_TYPE = totas.data[totas.key.indexOf('CRM.TRS_CALL_REVIEW_TYPE')];
				$scope.TRS_TYPE = totas.data[totas.key.indexOf('CRM.QUERY_TRS_TYPE_1')];
				$scope.TRS_TYPE2 = totas.data[totas.key.indexOf('CRM.QUERY_TRS_TYPE_2')];
				$scope.TRS_TYPE3 = totas.data[totas.key.indexOf('CRM.QUERY_TRS_TYPE_3')];
				$scope.TRS_TYPE_ALL = angular.copy($scope.TRS_TYPE);
				$scope.TRS_TYPE_ALL = $scope.TRS_TYPE_ALL.concat($scope.TRS_TYPE2);
			}
		});
		//get CHG_FRQ (近1年N=365、1個月N=30、3個月N=90、6個月N=180)
		$scope.CHG_FRQ = [];
        $scope.CHG_FRQ.push({LABEL: '近一年有異動', DATA: '365'},{LABEL: '近一個月有異動', DATA: '30'},{LABEL: '近三個月有異動', DATA: '90'},{LABEL: '近六個月有異動', DATA: '180'});
        //get MATCH_YN
        $scope.MATCH_YN = [];
        $scope.MATCH_YN.push({LABEL: '符合', DATA: 'Y'},{LABEL: '不符合', DATA: 'N'});
        //get TEMP_YN
        $scope.TEMP_YN = [];
        $scope.TEMP_YN.push({LABEL: '暫存試算', DATA: 'Y'},{LABEL: '取消暫存試算', DATA: 'N'});
        //get PROCESS_TYPE
        $scope.PROCESS_TYPE = [];
        $scope.PROCESS_TYPE.push({LABEL: '暫存試算', DATA: '1'},{LABEL: '取消暫存試算', DATA: '2'},{LABEL: '進行分派', DATA: '3'},{LABEL: '退件', DATA: '4'});
        $scope.prj_inquire = function() {
        	// S6：個金主管指派完成 --所有客戶均以分派完成
			$scope.sendRecv("CRM371", "prj_inquire", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", {'prj_status':'S4,S5,S6'},
				function(tota, isError) {
					if (!isError) {
						$scope.prj_list = [];
						angular.forEach(tota[0].body.prj_list, function(row, index, objs){
							$scope.prj_list.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_ID});
						});
						//十保專區計劃
						$scope.prj_list2 = [];
						angular.forEach(tota[0].body.prj_list2, function(row, index, objs){
							$scope.prj_list2.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_CODE});
						})
						if($scope.prj_list2.length > 0){
							$scope.inputVO.prj_id = $scope.prj_list2[0].DATA;
						}else{
							 $scope.showPrjMessage = true;
						}
						return;
					}
				}
		   );
	    };
		$scope.prj_inquire();
        //
        // date picker
        // config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		var date    = new Date();
        var preDate = new Date(date.getFullYear(), date.getMonth(), date.getDate()+1);
        $scope.bgn_sDateOptions = {
            minDate: preDate
        };
	        
		//
        
        // 驗證用
		$scope.paramForm = {};
		// init
		$scope.child = {};
        $scope.init = function() {
			$scope.inputVO = {};
			$scope.inputVO.region_center_id = ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
	        $scope.inputVO.branch_area_id = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
	        $scope.inputVO.org_branch_nbr = ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
	        
	        $scope.pri_id = projInfoService.getPriID()[0];
			if($scope.pri_id == '043'|| $scope.pri_id == '044'){
				$scope.inputVO.trs_type = "A"; //十保專區
				$scope.activeTab = 3;
			}else{
				$scope.activeTab = 0;
			}
		};
		$scope.init();
		$scope.init2 = function() {
			$scope.inputVO2 = {};
			$scope.inputVO2.new_branch_nbr = ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
			$scope.inputVO2.agree_type = 'S';
		};
		$scope.init2();
		$scope.inquireInit = function(){
        	$scope.child.paramList = [];
			$scope.child.resultList = [];
			$scope.child.outputVO = {};
		}
		$scope.inquireInit();
        
		/***以下連動業務處.營運區.分行別***/
        //分行資訊
		$scope.genBranch = function() {
			$scope.mappingSet['branchNbr'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
				if($scope.inputVO.branch_area_id) {
					if(row.BRANCH_AREA_ID == $scope.inputVO.branch_area_id)			
						$scope.mappingSet['branchNbr'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
				} else
					$scope.mappingSet['branchNbr'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
        };
        $scope.genBranch();
        $scope.bra_list = projInfoService.getAvailBranch();
		
        $scope.ao_code = projInfoService.getAoCode();
        $scope.getAolist1 = function() {
			$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': $scope.inputVO.region_center_id,'branch_area_id': $scope.inputVO.branch_area_id,'branch_nbr': $scope.inputVO.org_branch_nbr},
					function(tota, isError) {
						if (!isError) {
							$scope.ao_code_common = [];
							$scope.inputVO.org_ao_code = '';
							angular.forEach(tota[0].body.ao_list, function(row) {
								$scope.ao_code_common.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
							});
							return;
						}
					}
				);
		};
		$scope.getAolist2 = function() {
			$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': $scope.inputVO.region_center_id,'branch_area_id': $scope.inputVO.branch_area_id,'branch_nbr': $scope.inputVO2.new_branch_nbr},
					function(tota, isError) {
						if (!isError) {
							$scope.ao_code_common2 = [];
							$scope.inputVO2.new_ao_code = '';
							angular.forEach(tota[0].body.ao_list, function(row) {
								$scope.ao_code_common2.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
							});
							
							$scope.new_ao_list = angular.copy($scope.ao_code_common2);
						}
					}
				);
		};
		$scope.getAolist1();
		$scope.getAolist2();
		
		// COMMON TAG
		$scope.inquire_common = function(controlYN) {
			$scope.inputVO.control_yn = controlYN;  //例行與最適客戶標籤判斷
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			$scope.child.inquire_type = "common";
			$scope.sendRecv("CRM371", "inquire_common", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.child.resultList = tota[0].body.resultList;
						$scope.child.outputVO = tota[0].body;
						return;
					}
				}
		)};
		// 以個金主管192880 (350分行)完成A100780326 分派後，同時出現｢處理完成｣、｢查無資料｣訊息應該不須出現｢查無資料｣訊息
		$scope.inquire_common_user = function() {
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			$scope.sendRecv("CRM371", "inquire_common", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.child.resultList = tota[0].body.resultList;
						$scope.child.outputVO = tota[0].body;
						return;
					}
				}
		)};
		// PROJECT TAG
		$scope.inquire_project = function() {
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			$scope.child.inquire_type = "project";
			$scope.sendRecv("CRM371", "inquire_project", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.child.resultList = tota[0].body.resultList;
						$scope.child.outputVO = tota[0].body;
						return;
					}
				}
		)};
		// 以個金主管192880 (350分行)完成A100780326 分派後，同時出現｢處理完成｣、｢查無資料｣訊息應該不須出現｢查無資料｣訊息
		$scope.inquire_project_user = function() {
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			$scope.sendRecv("CRM371", "inquire_project", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						$scope.child.resultList = tota[0].body.resultList;
						$scope.child.outputVO = tota[0].body;
						return;
					}
				}
		)};
		
		$scope.detail = function (data) {
        	// 客戶首頁 by cam190
        	var vo = {
        		CUST_ID: data.CUST_ID,
        		CUST_NAME: data.CUST_NAME
        	};
        	$scope.connector('set','CRM_CUSTVO', vo);
        	var set = $scope.connector("set","CRM610URL","assets/txn/CRM610/CRM610_MAIN.html");
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
		};
		
		$scope.cust_ans = function (data) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM371/CRM371_CUST.html',
				className: 'CRM371_CUST',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = angular.copy(data);
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					if($scope.child.inquire_type == "common")
                		$scope.inquire_common_user();
                	else
                		$scope.inquire_project_user();
				}
			});
		};
		
		$scope.download = function(row) {
			//資況表客戶同意書
			$scope.sendRecv("CRM371", "download", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", {'seq': row.TRS_SEQ},
					function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
			);
			//十保客戶指定聲明書
			$scope.sendRecv("CRM371", "download2", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", {'seq': row.TRS_SEQ},
					function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
			);
		};
		
		$scope.checkrow = function() {
        	if ($scope.child.clickAll) {
        		angular.forEach($scope.child.paramList, function(row){
    				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.child.paramList, function(row){
    				row.SELECTED = false;
    			});
        	}
        };
        $scope.checkrow2 = function() {
        	if ($scope.child.clickAll2) {
        		angular.forEach($scope.child.resultList, function(row) {
    				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.child.resultList, function(row) {
    				row.SELECTED = false;
    			});
        	}
        };
		
        $scope.setNewAo = function() {
        	angular.forEach($scope.child.resultList, function(row){
				row.NEW_AO_CODE = $scope.inputVO2.new_ao_code;
			});
        };
        
        $scope.checkOther = function() {
        	var deferred = $q.defer();
        	$scope.sendRecv("CRM371", "checkOther", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", $scope.inputVO2,
					function(tota, isError) {
						if (!isError) {
							deferred.resolve(tota[0].body);
						} else
							deferred.reject();
			});
        	return deferred.promise;
        };
        $scope.save = function() {
        	if($scope.paramForm.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	var data = $scope.child.resultList.filter(function(row){
				return (row.SELECTED == true);
	    	});
        	if(data.length == 0) {
        		return;
        	}
        	// 2017/12/19 退件不用檢核以下
        	if ($scope.inputVO2.process_type != '4') {
        		// 2017/5/26 整批移轉不得跨分行
            	var check = false;
            	angular.forEach(data, function(row) {
    				if(row.ORG_AO_BRH && row.TRS_FLOW_TYPE == '2' && row.ORG_AO_BRH != row.NEW_AO_BRH)
    					check = true;
    			});
            	if(check) {
            		$scope.showErrorMsg('整批移轉客戶僅能做同分行互移');
            		return;
            	}
            	// 2017/6/5 檢查勾選客戶清單中是否有重複客戶ID
            	var uniq = data.map((e) => {
            	  return {'count': 1, 'CUST_ID': e.CUST_ID}
            	})
            	.reduce((a, b) => {
            	  a[b.CUST_ID] = (a[b.CUST_ID] || 0) + b.count
            	  return a
            	}, {});
            	var duplicates = Object.keys(uniq).filter((a) => uniq[a] > 1);
            	if(duplicates.length > 0) {
            		$scope.showErrorMsg('有重複客戶ID: ' + duplicates + '，請重新分派。');
            		return;
            	}
        	}
        	
        	$scope.inputVO2.apply_list = data;
        	$scope.inputVO2.trs_type = $scope.inputVO.trs_type;
        	$scope.inputVO2.prj_id = $scope.inputVO.prj_id;
        	
        	// 2017/7/10 cust_id only 分派
        	if($scope.inputVO2.process_type == '3') {
        		$scope.checkOther().then(function(res_data) {
        			if (res_data.resultList2 == 'ERR1') {
        				$scope.showErrorMsg("客戶:" + res_data.resultList + "，該客戶有2筆以上正在進行跨分行移轉，不得進行放行。");
	        		}
        			else if (res_data.resultList2 == 'ERR2') {
        				$scope.showErrorMsg("客戶:" + res_data.resultList + "，公司戶與公司負責人掛Code需一致");
					} else {
						$scope.sendRecv("CRM371", "save", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", $scope.inputVO2,
                    			function(totas, isError) {
                        			if (isError) {
                        				$scope.showErrorMsgInDialog(totas.body.msgData);
                        				return;
                	                }
                	                if (totas.length > 0) {
                	                	$scope.showMsg('ehl_01_common_004');
                	                	$scope.inquireInit();
                	                	if($scope.child.inquire_type == "common")
                	                		$scope.inquire_common_user();
                	                	else
                	                		$scope.inquire_project_user();
                                		$scope.child.clickAll = false;
                	                };
                	            }
            			);
					}
            	},function(reason) {
            		$scope.showErrorMsg('檢核移轉失敗');
            	});
        	} else {
        		$scope.sendRecv("CRM371", "save", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", $scope.inputVO2,
            			function(totas, isError) {
                			if (isError) {
                				$scope.showErrorMsgInDialog(totas.body.msgData);
                				return;
        	                }
        	                if (totas.length > 0) {
        	                	$scope.showMsg('ehl_01_common_004');
        	                	$scope.inquireInit();
        	                	if($scope.child.inquire_type == "common")
        	                		$scope.inquire_common_user();
        	                	else
        	                		$scope.inquire_project_user();
                        		$scope.child.clickAll = false;
        	                };
        	            }
    			);
        	}
        };
        
        $scope.adjustment = function () {
        	// 原路徑
        	var path = $rootScope.menuItemInfo.txnPath;
			path.push({'MENU_ID':"CRM381",'MENU_NAME':"客戶調整前後統計表"});
    		$rootScope.GeneratePage({'txnName':"CRM381",'txnId':"CRM381",'txnPath':path});
        };
		
		
});