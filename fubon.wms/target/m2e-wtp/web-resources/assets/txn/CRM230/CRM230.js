/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM230Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM230Controller";
		$scope.vip_degree = [];
		$scope.ao_code = sysInfoService.getAoCode();
				
		getParameter.XML(['CRM.VIP_DEGREE', 'KYC.MARRAGE', "CRM.CRM239_CONTRACT_STATUS", "CRM.CRM230_YN", "CRM.CON_DEGREE", "FPS.CURRENCY"], function(totas) {
			if (totas) {
				$scope.vip_degree = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.mappingSet['CRM.VIP_DEGREE'] = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.mappingSet['MARRIAGE_STAT'] = totas.data[totas.key.indexOf('KYC.MARRAGE')];
				$scope.mappingSet['CRM.CRM239_CONTRACT_STATUS'] = totas.data[totas.key.indexOf('CRM.CRM239_CONTRACT_STATUS')];
				$scope.mappingSet['CRM.CRM230_YN'] = totas.data[totas.key.indexOf('CRM.CRM230_YN')];
				$scope.mappingSet['CRM.CON_DEGREE'] = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.mappingSet['FPS.CURRENCY'] = totas.data[totas.key.indexOf('FPS.CURRENCY')];
			}
		});

		$scope.pri = '';
	    $scope.pri = String(sysInfoService.getPriID());
	    
		// gender
		$scope.mappingSet['GENDER'] = [];
		$scope.mappingSet['GENDER'].push({LABEL : '男',DATA : '1'},{LABEL : '女',DATA : '2'});

		// child
		$scope.mappingSet['CHILD_NUM'] = [];
		$scope.mappingSet['CHILD_NUM'].push({LABEL : '有',DATA : 'Y'},{LABEL : '無',DATA : 'N'});
		// other
		$scope.mappingSet['OTHER'] = [];
		$scope.mappingSet['OTHER'].push({LABEL : '未申請',DATA : 'N'},{LABEL : '已申請未開通',DATA : 'X'},{LABEL : '已開通',DATA : 'Y'});
		
		
		// init
		$scope.init_common = function(){
			$scope.inputVO = {};
			$scope.obj = {};
//			$scope.inputVO.ao_code = String(sysInfoService.getAoCode());  //載入時就要先給，否則無法進行getGroupList()
			
			// AO CODE
			// 20210629 add by Ocean => #0662: WMS-CR-20210624-01_配合DiamondTeam專案調整系統模組功能_組織+客管 change svn:log
//			if ($scope.pri == '002') {
//				$scope.sendRecv("CRM211", "getAOCodeList", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", $scope.inputVO, function(tota, isError) {
//					if (!isError) {
//						if(tota[0].body.msgData != undefined){
//							$scope.showErrorMsg(tota[0].body.msgData);
//						}
//					}
//					
//					if (tota.length > 0) {
//						$scope.aolist = tota[0].body.resultList;
//						
//						$scope.mappingSet['AO_CODE'] = tota[0].body.resultList;
//						
//						$scope.inputVO.aolist = [];
//						
//						angular.forEach($scope.aolist, function(row, index, objs){
//							switch (row.AO_CODE) {
//								case "Diamond Team":
//									break;
//								default :
//									$scope.inputVO.aolist.push({"AO_CODE":row.AO_CODE});
//									break;
//							}							
//						});
//					}
//				});
//			} else {
			    $scope.sendRecv("CRM211", "getAOCode", "com.systex.jbranch.app.server.fps.crm211.CRM211InputVO", {}, function(tota, isError) {
					if (!isError) {
						if(tota[0].body.msgData != undefined){
							$scope.showErrorMsg(tota[0].body.msgData);
						}		
					}
					if (tota.length > 0) {
						$scope.aolist = tota[0].body.resultList;
						$scope.mappingSet['AO_CODE'] = [];
						$scope.mappingSet['NEW_AO_CODE'] = [];
						$scope.mappingSet['NEW_AO_CODE'].push({LABEL: '客戶所屬理專', DATA: 'OWN'});
						
						/**#2107 客戶篩選需可篩選空code客戶角色**/
						// 增加 FAIA 權限 5440
						
						if ($scope.pri == '006' || $scope.pri == '009' || $scope.pri == '010' || $scope.pri == '011' || $scope.pri == '012' || 
							$scope.pri == 'O001' || $scope.pri == '013' || $scope.pri == '045' || $scope.pri == '046' || $scope.pri == '055' || $scope.pri == '056' ||
							$scope.pri == '014' || $scope.pri == '015' || $scope.pri == '023' || $scope.pri == '024'	
						) {
							$scope.mappingSet['AO_CODE'].push({LABEL:'空CODE', DATA:'0'});
							$scope.mappingSet['NEW_AO_CODE'].push({LABEL:'空CODE', DATA:'0'});
						}
						
						$scope.inputVO.aolist = [];
						if ($scope.ao_code != '' && $scope.ao_code != undefined) {		//有AO_CODE
	            			if($scope.ao_code.length > 1){		//有兩個以上AO_CODE的理專
	            				angular.forEach($scope.aolist, function(row, index, objs){
	            					angular.forEach($scope.ao_code, function(row2, index2, objs2){
	            						if(row.AO_CODE == row2){
	            							$scope.mappingSet['AO_CODE'].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	            							$scope.mappingSet['NEW_AO_CODE'].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	            							$scope.inputVO.aolist.push(row.AO_CODE);
	            						}
	            					});
	                			});
	            			}else if($scope.ao_code.length == 1){		//只有一個AO_CODE的理專
	                    		angular.forEach($scope.aolist, function(row, index, objs){
	                				$scope.mappingSet['AO_CODE'].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	                				$scope.mappingSet['NEW_AO_CODE'].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	                				$scope.inputVO.aolist.push(row.AO_CODE);
	                			});
	                    		$scope.inputVO.ao_code = String(sysInfoService.getAoCode());				//預設AO Code為理專自己
	            				$scope.inputVO.campaign_ao_code = String(sysInfoService.getAoCode());		//指派至新理專
	            			}
	        			} else {		
	        				// 無AO_CODE
	        				angular.forEach($scope.aolist, function(row, index, objs){
	        					$scope.mappingSet['AO_CODE'].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	        					$scope.mappingSet['NEW_AO_CODE'].push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
	        					$scope.inputVO.aolist.push(row.AO_CODE);
	        				});
	        			}
	            	};
				});
//			}
			
			/** 取得RM **/
//			$scope.mappingSet['RM'] = [];
//		    $scope.sendRecv("CRM230", "getRM", "com.systex.jbranch.app.server.fps.crm230.CRM230InputVO", {},
//			function(tota, isError) {
//				if (!isError) {
//					$scope.mappingSet['RM'] = tota[0].body.resultList;
//				}
//			});

			/*
			 * 取得UHRM人員清單(由員工檔+角色檔)
			 */
			$scope.sendRecv("ORG260", "getUHRMListByType", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
					function(tota, isError) {
						if (isError) {
							return;
						}
						if (tota.length > 0) {
							$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
							if ($scope.mappingSet['UHRM_LIST'].length >= 1 && String(sysInfoService.getPriID()) == 'UHRM002') {
								$scope.inputVO.uEmpID = $scope.mappingSet['UHRM_LIST'][0].DATA;
							} else {
								$scope.inputVO.uEmpID = '';
							}
						}
			});
			
			// bra
			$scope.mappingSet['branchsDesc'] = [];
			$scope.inputVO.availbranchlist = [];
			angular.forEach(_.sortBy(projInfoService.getAvailBranch(), ['BRANCH_NBR']), function(row, index, objs){
				if (null != row.BRANCH_NBR) {
					$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NBR + '-' + row.BRANCH_NAME, DATA: row.BRANCH_NBR});
					$scope.inputVO.availbranchlist.push(row.BRANCH_NBR);
				}
			});
			
			//出生年起/迄日
			$scope.birth_sDateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
			
			$scope.birth_eDateOptions = {
					maxDate: $scope.maxDate,
					minDate: $scope.minDate
			};
			
			$scope.belong_bra_nbr = '';
			if($scope.mappingSet['branchsDesc'].length == 1){
				$scope.inputVO.belong_bra_nbr = '';
				angular.forEach($scope.mappingSet['branchsDesc'], function(row, index, objs){
					$scope.inputVO.belong_bra_nbr = row.DATA;	
				});
				$scope.belong_bra_nbr = $scope.inputVO.belong_bra_nbr;
			}
		};
		
		$scope.init_common();
		
		$scope.inquireInit_common = function(){
			$scope.outputVO = {};
			$scope.resultList = [];
		}
		
		$scope.inquireInit_common();
		
	    //date picker
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDatebirth = function() {
			$scope.birth_sDateOptions.maxDate = $scope.inputVO.birth_eDate || $scope.maxDate;
			$scope.birth_eDateOptions.minDate = $scope.inputVO.birth_sDate || $scope.minDate;
		};
		
		// group
		$scope.sendRecv("CRM230", "getGroupList", "com.systex.jbranch.app.server.fps.crm230.CRM230InputVO", {'ao_code':String(sysInfoService.getAoCode())},
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
                	if (totas.length > 0) {
                		$scope.mappingSet['GroupList'] = [];
                		angular.forEach(totas[0].body.resultList, function(row, index, objs){
            				$scope.mappingSet['GroupList'].push({LABEL: row.GROUP_NAME, DATA: row});
            			});
                	};
				}
		);
		
		// dear CRM230_CUSTTAB.html定義是custList not resultList
		$scope.checkAdd = function() {
	    	var ans = _.uniqBy($scope.custList.filter(function(obj){
	    		return (obj.SELECTED == true);
	    	}),"CUST_ID");
	    	
	    	if (ans.length == 0) {
	    		$scope.showErrorMsgInDialog("請選取客戶");
	    		return;
			}
			$scope.inputVO.grouplist = ans;
			$scope.inputVO.group_name = $scope.inputVO.group['GROUP_NAME'];
			$scope.inputVO.group = $scope.inputVO.group['GROUP_ID'];

	    	$scope.sendRecv("CRM230", "group_join", "com.systex.jbranch.app.server.fps.crm230.CRM230InputVO", $scope.inputVO, 
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('加入成功');
	                };
	           }
	        );
		};
		
		$scope.checkrow = function() {
        	if ($scope.obj.clickAll) {
        		angular.forEach($scope.resultList, function(row){
    				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.resultList, function(row){
    				row.SELECTED = false;
    			});
        	}
        };
        // 2018/3/16 我看CRM230_CUSTTAB.html在2017/5把共用html把resultList改成custList
        // 我收尋不到CRM230_RESULT在幹嘛的耶, 這頁一樣用resultList
        // 這邊定一個新的吧
        $scope.check_cust_row = function() {
        	if ($scope.obj.cust_clickAll) {
        		angular.forEach($scope.custList, function(row) {
    				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.custList, function(row) {
    				row.SELECTED = false;
    			});
        	}
        };
		
        $scope.getSum = function(group, key) {
            var sum = 0;
            for (var i = 0; i < group.length; i++) {
            	if (group[i][key] != null){
            		sum += group[i][key];
            	}
            }
            return sum;
	    };
        
	    // dear CRM230_CUSTTAB.html定義是custList not resultList
	    $scope.add_campaign = function() {
	    	var ans = _.uniqBy($scope.custList.filter(function(obj){
	    		return (obj.SELECTED == true);
	    	}),"CUST_ID");
	    	
	    	if (ans.length == 0) {
	    		$scope.showErrorMsgInDialog("請選取客戶");
	    		return;
	    	}
	    	$scope.inputVO.campaign_custlist = ans;
	    	$scope.inputVO.source = 'crm230';
	    	$scope.sendRecv("CRM230", "add_campaign", "com.systex.jbranch.app.server.fps.crm230.CRM230InputVO", $scope.inputVO, 
	    			function(totas, isError) {
	        			if (isError) {
	        				$scope.showErrorMsgInDialog(totas.body.msgData);
	        				return;
		                }
		                if (totas.length > 0) {
		                	$scope.showMsg('加入成功');
		                };
		           }
	    	);
	    };
	    
	    //名單與代辦工作
	    $scope.cam200 = function(row) {
	    	$scope.connector('set', "custID", row.CUST_ID);
			$scope.connector('set', "custName", row.CUST_NAME);
			$scope.connector('set', "tab", "tab1");
	    	$rootScope.menuItemInfo.url = "assets/txn/CAM200/CAM200.html";
	    };
	    
	    //自建待辦
	    $scope.openCUS160_ADD = function (row){
	    	var dialog = ngDialog.open({
	    		template: 'assets/txn/CUS160/CUS160_ADD.html',
				className: 'CUS160_ADD',
				showClose: false,
				controller: ['$scope', function($scope) {
					$scope.CUS160PAGE = "CUS160";
					$scope.custID = row.CUST_ID;
					$scope.custName = row.CUST_NAME;
				}]						    	        
	    	}).closePromise.then(function (data) {
	    		//
	    	});	        	 
	    };
	    	    
	    //連結至客戶首頁
	    $scope.goCRM610 = function(row){
//	    	$scope.connector('set','CRM110_CUST_ID', row.CUST_ID);
//			$scope.connector('set','CRM110_CUST_NAME', row.CUST_NAME);
//			$scope.connector('set','CRM110_AOCODE', row.AO_CODE);
	    	
	    	$scope.connector('set','CRM_CUSTVO', row);
	    	
			var path = "assets/txn/CRM610/CRM610_MAIN.html";
			$scope.connector("set","CRM610URL",path);
			
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
	    };
	    
	    //連結至薪轉公司查詢
	    $scope.query_company = function(row){
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM230/CRM230_COMPANY.html',
				className: 'CRM230_COMPANY',
				showClose: false,
				controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function(data) {
	       		if(data.value != 'cancel'){
	       			$scope.inputVO.sal_company = data.value;
	  			}
 	  		});
	    }; 
});