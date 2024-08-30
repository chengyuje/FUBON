/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM362Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $q, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM362Controller";

		// combobox
		getParameter.XML(["CRM.CON_DEGREE", "CRM.VIP_DEGREE", "COMMON.YES_NO", "CRM.TRS_APL_REASON", "CRM.QUERY_TRS_TYPE_3","CRM.CHG_TYPE"], function(totas) {
			if (totas) {
				$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.COM_YN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
				$scope.TRS_APL_REASON = totas.data[totas.key.indexOf('CRM.TRS_APL_REASON')];
				$scope.TRS_TYPE = totas.data[totas.key.indexOf('CRM.QUERY_TRS_TYPE_3')];
				$scope.CHG_TYPE = totas.data[totas.key.indexOf('CRM.CHG_TYPE')];
			}
		});
		$scope.prj_inquire = function() {
			//S4:已啟動客戶換手名單 --總行進行啟動分行個金主管分派作業
			//S5:個金主管進行名單指派中(定義：任一分行未完成指派)
			$scope.inputVO.prj_status = "S4,S5"
			$scope.sendRecv("CRM371", "prj_inquire", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {						
						//十保專區計劃
						$scope.prj_list2 = [];
						angular.forEach(tota[0].body.prj_list2, function(row, index, objs){
							$scope.prj_list2.push({LABEL: row.PRJ_NAME, DATA: row.PRJ_CODE});
						})						
						if($scope.prj_list2.length > 0){
							$scope.inputVO.prj_code = $scope.prj_list2[0].DATA;
						}else{
						   $scope.showPrjMessage = true;
						}
						return;
					}
				}
		)};
		$scope.prj_inquire();
		
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
        
        // init
		$scope.pri_id = projInfoService.getPriID()[0];
		$scope.init = function(){
			$scope.inputVO = {};
			$scope.inputVO.region_center_id = ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
	        $scope.inputVO.branch_area_id = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
	        $scope.inputVO.org_branch_nbr = ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
	        
	        $scope.inputVO.trs_type = "A"; //十保專區
	        $scope.inputVO2 = {};
			$scope.inputVO2.new_branch_nbr = ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
	        $scope.inputVO2.process_type = "3";
	        $scope.prj_inquire();
		};
		$scope.init();
		
		
		
        $scope.inquireInit = function(){
        	$scope.paramList = [];
			$scope.resultList = [];
			$scope.outputVO = {};
			//20200824 重新查詢後新理專應回覆請選擇
			$scope.inputVO2.new_ao_code = "";
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
							$scope.ao_code_project = [];
							$scope.inputVO.org_ao_code = '';
							angular.forEach(tota[0].body.ao_list, function(row) {
								if(row.TYPE == '1')
	    							$scope.ao_code_project.push({'LABEL': (row.EMP_NAME+'(主Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
	    						else if(row.TYPE == '2')
	    							$scope.ao_code_project.push({'LABEL': (row.EMP_NAME+'(副Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
	    						// mantis 3901 請移除下拉選單有(維護) CODE的選項 //#5649加回來，可移入任何CODE
	    						else if(row.TYPE == '3')
	    							$scope.ao_code_project.push({'LABEL': (row.EMP_NAME+'(維護Code)'), 'DATA': row.AO_CODE, 'TYPE': row.TYPE});
							});
							
							$scope.ao_code_project = $filter('orderBy')($scope.ao_code_project, 'TYPE', false);
						}
					}
				);
		};
		
		$scope.getAolist2 = function() {
			var deferred = $q.defer();
			$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': $scope.inputVO.region_center_id,'branch_area_id': $scope.inputVO.branch_area_id,'branch_nbr': $scope.inputVO2.new_branch_nbr},
					function(tota, isError) {
						if (!isError) {
							$scope.ao_code_project2 = [];
							$scope.inputVO2.new_ao_code = '';
							angular.forEach(tota[0].body.ao_list, function(row) {
								if(row.TYPE == '1')
	    							$scope.ao_code_project2.push({'LABEL': (row.EMP_NAME+'(主Code)'), 'DATA': row.AO_CODE, 'BRANCH_NBR': row.BRANCH_NBR, 'TYPE': row.TYPE});
	    						else if(row.TYPE == '2')
	    							$scope.ao_code_project2.push({'LABEL': (row.EMP_NAME+'(副Code)'), 'DATA': row.AO_CODE, 'BRANCH_NBR': row.BRANCH_NBR, 'TYPE': row.TYPE});
	    						// mantis 3901 請移除下拉選單有(維護) CODE的選項 //#5649加回來，可移入任何CODE
	    						else if(row.TYPE == '3')
	    							$scope.ao_code_project2.push({'LABEL': (row.EMP_NAME+'(維護Code)'), 'DATA': row.AO_CODE, 'BRANCH_NBR': row.BRANCH_NBR, 'TYPE': row.TYPE});
							});
							
							$scope.ao_code_project2 = $filter('orderBy')($scope.ao_code_project2, 'TYPE', false);
							deferred.resolve();
						}
					}
			);
			return deferred.promise;
		};
		$scope.getAolist1();
		$scope.getAolist2().then(function(data) {
			$scope.new_ao_list = angular.copy($scope.ao_code_project2);
		});
		
		$scope.inquire = function() {
			if(!$scope.inputVO.prj_code){
				$scope.showErroeMsg("專案名稱必須輸入!");
				return;
			}
				
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			$scope.sendRecv("CRM362", "inquire", "com.systex.jbranch.app.server.fps.crm362.CRM362InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList.length == 0) {
							$scope.showMsg("ehl_01_common_009");
							return;
						}
						$scope.resultList = tota[0].body.resultList;
						$scope.outputVO = tota[0].body;
						return;
					}
				}
		)};
		
		$scope.download = function(row) {
			$scope.sendRecv("CRM371", "download", "com.systex.jbranch.app.server.fps.crm371.CRM371InputVO", {'seq': row.TRS_SEQ},
					function(totas, isError) {
		        		if (!isError) {
							return;
						}
					}
			);
		};
		
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
		
		$scope.checkrow = function() {
        	if ($scope.clickAll) {
        		angular.forEach($scope.paramList, function(row){
    				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.paramList, function(row){
    				row.SELECTED = false;
    			});
        	}
        };
        
        $scope.checkrow2 = function() {
        	if ($scope.clickAll2) {
        		angular.forEach($scope.resultList, function(row){
    				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.resultList, function(row){
    				row.SELECTED = false;
    			});
        	}
        };
        
        // 換頁全選按鈕初始化
        $scope.$watchCollection('paramList', function(newNames, oldNames) {
        	$scope.clickAll = false;
        });
        
        $scope.setNewAo = function() {
        	// 2018/11/13
        	if($scope.inputVO2.new_ao_code) {
        		
        		var msg = $filter('i18n')('ehl_02_common_009'); //維護CODE的客戶將不計績，請再次確認是否認養或移入哦！
        		
        		if($filter('filter')($scope.ao_code_project2, {'DATA': $scope.inputVO2.new_ao_code})[0].TYPE == '3') {
        			$confirm({text: msg, title: '提醒', hideCancel: true}, {size: 'sm'}).then(function() {
    					
    				});
        		}
        		angular.forEach($scope.resultList, function(row) {
            		if(row.SELECTED && !(row.AGMT_SEQ || row.TRS_TXN_SOURCE == '2')) {
            			row.NEW_AO_BRH = $scope.inputVO2.new_branch_nbr;
            			row.NEW_BRANCH_NAME = $filter('filter')($scope.mappingSet['branchNbr'], {'DATA': row.NEW_AO_BRH})[0].LABEL;
            			row.NEW_AO_CODE = $scope.inputVO2.new_ao_code;
            		}	
    			});
        	}
        };
        
        $scope.setNewBnr = function(data) {
        	if(data.NEW_AO_CODE) {
        		data.NEW_AO_BRH = $filter('filter')($scope.new_ao_list, {'DATA': data.NEW_AO_CODE})[0].BRANCH_NBR;
        		data.NEW_BRANCH_NAME = $filter('filter')($scope.mappingSet['branchNbr'], {'DATA': data.NEW_AO_BRH})[0].LABEL;
        	}
        	
        	var msg = $filter('i18n')('ehl_02_common_009'); //維護CODE的客戶將不計績，請再次確認是否認養或移入哦！
    		
        	if(data.NEW_AO_CODE) {
        		if($filter('filter')($scope.new_ao_list, {'DATA': data.NEW_AO_CODE})[0].TYPE == '3') {
        			$confirm({text: msg , title: '提醒', hideCancel: true}, {size: 'sm'}).then(function() {
    					
    				});
        		}
        	}    		
        };
        
        $scope.checkMax = function() {
        	var deferred = $q.defer();
        	$scope.sendRecv("CRM361", "checkMax", "com.systex.jbranch.app.server.fps.crm361.CRM361InputVO", $scope.inputVO2,
					function(tota, isError) {
						if (!isError) {
							deferred.resolve(tota[0].body);
						} else{
							deferred.reject();
						}
			});
        	return deferred.promise;
        };
        
        //儲存前檢核
        $scope.save = function() {
        	if(!$scope.inputVO.prj_code){
				$scope.showErroeMsg("專案名稱必須輸入!");
				return;
			}else{
				$scope.inputVO2.prj_code = $scope.inputVO.prj_code;
			}
        	
        	if($scope.parameterTypeEditForm.$invalid) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	//取出有勾選項目
			var data = $scope.resultList.filter(function(row) {
				return (row.SELECTED == true);
	    	});
        	if(data.length == 0) {
        		return;
        	}
        	
        	
        	var IsMBRM = false;
    		var check1 = false;
        	var check2 = false;
        	var check3 = false;
        	angular.forEach(data, function(row) {
        		// 2017/5/26 TRS_FLOW_TYPE == '2'不得跨分行
				if(row.ORG_AO_BRH && row.TRS_FLOW_TYPE == '2' && row.ORG_AO_BRH != row.NEW_AO_BRH)
					check1 = true;
				// 2017/6/22 移入前和移入後相同Code,應不可申請移入
				if(row.ORG_AO_CODE == row.NEW_AO_CODE)
					check2 = true;
				// null
				if(!row.NEW_AO_CODE)
					check3 = true;
				// 2017/10/2 TRS_FLOW_TYPE = 1 or 2 是營運督導
				if(row.TRS_FLOW_TYPE == '1' || row.TRS_FLOW_TYPE == '2')
					IsMBRM = true;
			});
        	if(check1) {
        		$scope.showErrorMsg('客戶僅能做同分行互移，請重新調整');
        		return;
        	}
        	if(check2) {
        		$scope.showErrorMsg('移入前和移入後相同Code，應不可申請移入');
        		return;
        	}
        	if(check3) {
        		$scope.showErrorMsg('不得移入空Code，請重新調整');
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
        	
        	// 2017/6/6 a list group by AO_CODE
        	var groupAo = data.map((e) => {
        	  return {'count': 1, 'AO_CODE': e.NEW_AO_CODE}
        	})
        	.reduce((a, b) => {
        	  a[b.AO_CODE] = (a[b.AO_CODE] || 0) + b.count
        	  return a
        	}, {});
        	
        	$scope.inputVO2.apply_list = data;
        	$scope.inputVO2.ao_object = groupAo;
        	
        	//進行分派  ，只有分派需要進行超過最適客戶數提醒 。2017/6/9 add
        	$scope.checkMax().then(function(res_data) {
        		if (res_data.resultList2 == 'ERR2') {
        			$scope.showErrorMsg("客戶:" + res_data.resultList + ", FC客戶不可移入FCH");
        		}
        		else if (res_data.resultList2 == 'ERR3') {
        			$scope.showErrorMsg("客戶:" + res_data.resultList + ", FCH計績 Code不可轉入FCH維護Code");
				}
				else if (res_data.resultList2 == 'ERR4') {
					$scope.showErrorMsg("客戶:" + res_data.resultList + ", 公司戶與公司負責人掛Code需一致");
				}
				else if (res_data.resultList2 == 'ERR1') {
					if(IsMBRM) {
						$confirm({text: '此次放行名單將使得理專：' + res_data.resultList + '會超過最適客戶管理原則，需經營運督導放行生效，請確認是否放行？'}, {size: 'sm'}).then(function() {
							$scope.inputVO2.flaggedList = res_data.resultList;
							$scope.save2("Y");
	        			});
					} else {
						$confirm({text: '此次放行名單將使得理專：' + res_data.resultList + '會超過最適客戶管理原則，需經總行放行生效，請確認是否放行？'}, {size: 'sm'}).then(function() {
							$scope.inputVO2.flaggedList = res_data.resultList;
							$scope.save2("Y");
	        			});
					}
				}
				else
        			$scope.save2();
        	},function(reason) {
        		$scope.showErrorMsg('檢核上限失敗');
        	});
        };
		
		//儲存
		$scope.save2 = function (flag) {
			if(flag)
				$scope.inputVO2.flag = "Y";
			else
				$scope.inputVO2.flag = null;
			$scope.sendRecv("CRM362", "save", "com.systex.jbranch.app.server.fps.crm362.CRM362InputVO", $scope.inputVO2,
    			function(totas, isError) {
        			if (!isError) {
        				if( totas[0].body.errorMsg != null ){
        					$scope.showWarningMsg(totas[0].body.errorMsg);
        				}
        				$scope.showMsg('ehl_01_common_004');
	                	$scope.inquireInit();
	                	$scope.inquire();
	                }
	            }
        	);
		};
});