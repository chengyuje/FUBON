'use strict';
eSoafApp.controller('CRM241Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, $filter, $timeout) {
		
		$controller('BaseController', {$scope: $scope});
		$controller('RegionController', {$scope: $scope});
		$controller('PPAPController', {$scope: $scope});
		$scope.controllerName = "CRM241Controller";
		
		$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();
		/*
		 * 取得UHRM人員清單(由員工檔+角色檔)
		 */
		$scope.sendRecv("ORG260", "getUHRMListByType", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						return;
					}
					if (tota.length > 0) {
						$scope.mappingSet['UHRM_LIST_TEMP'] = tota[0].body.uhrmList;
						$scope.mappingSet['UHRM_LIST'] = [];
						angular.forEach($scope.mappingSet['UHRM_LIST_TEMP'], function(row){
							$scope.mappingSet['UHRM_LIST'].push({LABEL : row.LABEL, DATA : row.UHRM_CODE});
                		});

						if ($scope.mappingSet['UHRM_LIST'].length >= 1 && $scope.priID == 'UHRM002') {
							$scope.inputVO.inq_ao_code = $scope.mappingSet['UHRM_LIST'][0].DATA;
						} else {
						}
					}
		});
				
		//取的類別
		$scope.mappingSet['query_type'] = [{LABEL: "登錄", DATA: "1"}, {LABEL: "移出", DATA: "2"}];
		
		$scope.init = function() {
			$scope.inputVO = {};
			if ($scope.memLoginFlag.startsWith('UHRM') && $scope.memLoginFlag != 'UHRM') {
				$scope.sendRecv("PMS401U", "isMainten", "com.systex.jbranch.app.server.fps.pms401u.PMS401UInputVO", {'itemID': 'CRM241'}, function(tota, isError) {
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
				$scope.region = ['N', $scope.inputVO, "center_id", "REGION_LIST", "area_id", "AREA_LIST", "bra_nbr", "BRANCH_LIST", "inq_ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
				$scope.RegionController_setName($scope.region);
			}
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		// 查詢時間設置
		$scope.isq = function() {
			var NowDate = new Date();
	    	var yr = NowDate.getFullYear();
	    	var mm = NowDate.getMonth() + 2;
	    	var strmm = '';
	    	var xm = '';
	    	$scope.mappingSet['timeE'] = [];
	    	for (var i = 0; i < 12; i++) {
	    		mm = mm - 1;
	    		if (mm == 0) {
	    			mm = 12;
	    			yr = yr - 1;
	    		}
	   			if (mm < 10)
	   				strmm = '0' + mm;
	   			else
	   				strmm = mm;
	   			$scope.mappingSet['timeE'].push({
    				LABEL : yr + '/' + strmm,
	   				DATA : yr + '' + strmm
	    		});
	    	} 
	    };
	   	$scope.isq();
		
		//查詢
		$scope.inquire = function() {
			$scope.sendRecv("CRM241", "inquire", "com.systex.jbranch.app.server.fps.crm241.CRM241InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
						}
			});
	    };
		
	    $scope.checkrow = function() {
        	if ($scope.clickAll) {
        		angular.forEach($scope.resultList, function(row){
    				row.SELECTED = true;
    			});
        	} else {
        		angular.forEach($scope.resultList, function(row){
    				row.SELECTED = false;
    			});
        	}
        };

        $scope.add = function(row) {
        	//AMC
			$scope.ppap(undefined, undefined, row.CUST_ID, row.CUST_NAME, '3', 'add');
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
	    
	    //CRM110 可視客戶查詢。此頁面不會有PS來使用
	    $scope.detailVO = {};
	    $scope.loginAo = String(sysInfoService.getAoCode());

		if ($scope.loginAo != '' &&  $scope.loginAo != undefined && $scope.loginAo != '001') {		
			//$scope.loginAo == '001' ===>是AFC，AFC 應可從快查KEY ID或姓名來查詢同歸屬行內的任一客戶(#0002070)
			$scope.detailVO.role = 'ao';
			$scope.detailVO.ao_code = $scope.loginAo;
		} else {
			$scope.detailVO.role = 'other';
		}
	    
	    $scope.detail = function(custId) {
			$scope.detailVO.cust_id = custId;
			// 2017/11/1 start
			$scope.sendRecv("CRM110", "checkCust", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.detailVO,
				function(totas, isError) {
	        		if (!isError) {
	        			if(totas[0].body.resultList.length == 0) {
	        				// 非轄下
							$scope.showErrorMsg('ehl_01_cus130_008');
							return;
	        			}
	        			// old code start
	        			$scope.sendRecv("CRM110", "inquire", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.detailVO,
        					function(tota, isError) {
        						if (!isError) {
        							if(tota[0].body.resultList.length == 0) {
        								$scope.sendRecv("CRM110", "inquireCust", "com.systex.jbranch.app.server.fps.crm110.CRM110InputVO", $scope.detailVO,
        										function(tota, isError) {
        									if (!isError) {
        										if(tota[0].body.resultList.length == 0) {
        											//查無資料
        											$scope.showErrorMsg('ehl_01_cus130_002',[$scope.detailVO.cust_id]);			//無此客戶ID：{0}
        											return;
        										}
        										if(tota[0].body.resultList.length == 1) {
        											if(tota[0].body.resultList[0].BRA_NBR == null){
        												//無歸屬行
        												$scope.showErrorMsg('ehl_01_cus130_002',[$scope.detailVO.cust_id]);		//無此客戶ID：{0}
        												return;
        											}else{
        												if(tota[0].body.resultList[0].EMP_NAME == null){
        													//空code客戶
        													var list = [tota[0].body.resultList[0].BRA_NBR, tota[0].body.resultList[0].BRANCH_NAME];
        													$scope.showErrorMsg('ehl_01_cus130_006', list);		//客戶歸屬：{0}-{1}，不提供客戶首頁查詢。
        													return;	
        												}else{
        													//有歸屬行&所屬理專
        													var list = [tota[0].body.resultList[0].BRA_NBR, tota[0].body.resultList[0].BRANCH_NAME, tota[0].body.resultList[0].EMP_NAME];
        													$scope.showErrorMsg('ehl_01_cus130_005', list);		//客戶歸屬( {0} {1} ) {2} 理專，不提供客戶首頁查詢。
        													return;												
        												}
        											}											
        										}									
        									}								
        								});								
        							}
        						}
        					
        						//單筆資料
        						if(tota[0].body.resultList.length == 1) {
        							var vo = {
        								CUST_ID: tota[0].body.resultList[0].CUST_ID,
        								CUST_NAME: tota[0].body.resultList[0].CUST_NAME
        							};
        							$scope.connector('set','CRM_CUSTVO', vo);
        				        	var set = $scope.connector("set","CRM610URL","assets/txn/CRM610/CRM610_MAIN.html");
        							var dialog = ngDialog.open({
        								template: 'assets/txn/CRM610/CRM610.html',
        								className: 'CRM610',
        								showClose: false
        							});
        						}
        				});
	        			// old code end
					}
				}
			);
			// 2017/11/1 end
		}
	    
});