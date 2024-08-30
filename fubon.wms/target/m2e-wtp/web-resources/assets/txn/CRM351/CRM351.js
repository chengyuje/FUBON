/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM351Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM351Controller";

		// combobox
		getParameter.XML(["CRM.VIP_DEGREE", "CRM.CON_DEGREE"], function(totas) {
			if (totas) {
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
			}
		});
		//
		
		// init
		$scope.init = function() {
			$scope.inputVO = {};			
			$scope.inputVO.pri_id = projInfoService.getPriID()[0];
			$scope.inputVO.region_center_id = ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
	        $scope.inputVO.branch_area_id = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
	        $scope.inputVO.bra_nbr = ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
	        if (projInfoService.getAoCode()[0] != "")
				$scope.inputVO.ao_code = projInfoService.getAoCode()[0];
		}
		$scope.init();
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit(); 
		
		//權限控管
		$scope.priList = ['009','010','011','012','013'];  //業務主管、分行助理個金主管、分行個金主管、營運督導、業務處處長
		
		
		/***以下連動業務處.營運區.分行別***/
        //分行資訊
		$scope.genBranch = function() {
			$scope.BRANCH_LIST = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row) {
				if($scope.inputVO.branch_area_id) {
					if(row.BRANCH_AREA_ID == $scope.inputVO.branch_area_id)			
						$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
				} else
					$scope.BRANCH_LIST.push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
        };
        $scope.genBranch();
        $scope.bra_list = projInfoService.getAvailBranch();
		
        // ao_code
        $scope.ao_code = projInfoService.getAoCode();
        $scope.AO_LIST = [];
        $scope.getAolist = function() {
			$scope.sendRecv("CRM381", "ao_inquire", "com.systex.jbranch.app.server.fps.crm381.CRM381InputVO", {'region_center_id': $scope.inputVO.region_center_id,'branch_area_id': $scope.inputVO.branch_area_id,'branch_nbr': $scope.inputVO.bra_nbr},
					function(tota, isError) {
						if (!isError) {
							$scope.AO_LIST = [];
							$scope.inputVO.ao_code = "";
							if($scope.ao_code.length > 0) {
								if($scope.ao_code.length > 1 ) {
		    						angular.forEach($scope.ao_code, function(row) {
		    							$scope.AO_LIST.push({LABEL: projInfoService.getUserName(), DATA: row});
		    						});	
		    					}
		    					else {
		    						$scope.AO_LIST.push({LABEL: projInfoService.getUserName(), DATA: $scope.ao_code[0]});
		    					}
							}
							else {
								angular.forEach(tota[0].body.ao_list, function(row) {
									$scope.AO_LIST.push({LABEL: row.EMP_NAME, DATA: row.AO_CODE});
								});
							}
							return;
						}
			});
		};
		$scope.getAolist();
		
		$scope.inquire = function() {
			// toUpperCase
			if($scope.inputVO.cust_id)
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();
			$scope.sendRecv("CRM351", "inquire", "com.systex.jbranch.app.server.fps.crm351.CRM351InputVO", $scope.inputVO,
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
			});
	    };
	    // MAO151
	    $scope.MAO151_PARAMS = $scope.connector('get', 'MAO151_PARAMS');
		$scope.connector('set', "MAO151_PARAMS", null);
		if($scope.MAO151_PARAMS) {
			$scope.inputVO.cust_id = $scope.MAO151_PARAMS.CUST_ID;
			$scope.inquire();
		}	

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
        
        //換頁全選按鈕初始化
        $scope.$watchCollection('paramList', function(newNames, oldNames) {
        	$scope.clickAll = false;
        });
	    
		$scope.save = function() {
			var data = $scope.resultList.filter(function(row){
				return (row.SELECTED == true);
	    	});
        	if(data.length == 0){
        		$scope.showErrorMsg('請至少勾選一個');
        		return;
        	}
        	$scope.sendRecv("CRM351", "save", "com.systex.jbranch.app.server.fps.crm351.CRM351InputVO", {'row_data': data},
    			function(totas, isError) {
        			if (isError) {
        				$scope.showErrorMsgInDialog(totas.body.msgData);
        				return;
	                }
	                if (totas.length > 0) {
	                	$scope.showMsg('ehl_01_common_004');
	                	$scope.inquireInit();
                		$scope.inquire();
	                };
	            }
        	);
        };
        
        //放行前處置
        //@param: type1:同意部分放行  ， type2:同意全部放行，type3:不同意放行
        $scope.preReview = function(type) {
        	var check = '';   //flag : Y覆核同意、N覆核不同意
        	switch (type) {
        		case 1 :
        			check = 'Y';
        			break;
        		case 2 :
        			check = 'Y';
        			angular.forEach($scope.resultList, function(row){
        				row.SELECTED = true;
        			});
        			break;
        		case 3 :
        			check = 'N';
        			break;
        		default :
        			return;
        	} 

        	//檢核有無資料可放行
        	var data = $scope.resultList.filter(function(row){
				return row.SELECTED;
	    	});
        	if(!data.length) {
        		return;
        	}
        	
        	//執行放行將會提示尚未填寫交接資訊的客戶，如果選擇確定，將會儲存尚未交接客戶並執行
        	//尚未有交接資訊客戶
        	var noData = data.filter(function(row){
				return !row.SEQ;
	    	});
        	
        	//產出訊息
        	//20170606問題單0002095，無須顯示ID
        	var msg = '選取' + data.length + '筆客戶中，有' + noData.length + '筆尚未填寫交接註記，確定是否放行?';
//        	未填寫交接客戶ID顯示
//        	if (noData.length) {
//        		var wid = [];
//        		var i = 0; //計數
//        		var count = 5; //顯示筆數
//            	angular.forEach(noData, function(row) {
//    				if (i++ < count) {
//    					wid.push(row.CUST_ID);
//    				}
//    			});
//            	msg += wid.toString();
//            	if (noData.length > count) {
//            		msg += '...' ;
//            	}
//            	msg += '(共' + noData.length + '筆)，尚未填寫交接資訊。\n'; 
//        	}
//        	msg += '確定要執行?';
        	
        	$confirm({text: msg}, {size: 'sm'}).then(function() {
        		if (noData.length) {  //再放行之前，存入未填寫的交接資訊
        			$scope.sendRecv("CRM351", "save", "com.systex.jbranch.app.server.fps.crm351.CRM351InputVO", {'row_data': noData},
        	    			function(totas, isError) {
        	        			if (isError) {
        	        				$scope.showErrorMsgInDialog(totas.body.msgData);
        	        				return;
        		                }
        		                if (totas.length > 0) {
        		                	//務必重新inquire，抓取剛存進去的資料
        		                	$scope.sendRecv("CRM351", "inquire", "com.systex.jbranch.app.server.fps.crm351.CRM351InputVO", $scope.inputVO,
        		        					function(tota, isError) {
        		        						if (!isError) {
        		        							if(tota[0].body.resultList.length == 0) {
        		        								$scope.showMsg("ehl_01_common_009");
        		        	                			return;
        		        	                		}
        		        							$scope.resultList = tota[0].body.resultList;
        		        							
        		        	                		if (type == '2') {   //全部放行
        		        	                			$scope.review(check,$scope.resultList);
        		        	                		}
        		        	                		else if (type == '1' || type == '3') {   //部份放行(OR 不同意)
        		        	                			angular.forEach(data, function(oldRow) {
        		        	                				if (!oldRow.SEQ){
        		        	                					var custId = oldRow.CUST_ID;
        		        	                					angular.forEach($scope.resultList, function(newRow) {
        		        	                						if (newRow.CUST_ID == custId) {
        		        	                							oldRow.SEQ = newRow.SEQ;
        		        	                						}
        		        	                					});
        		        	                			    }				
        		        	                			});
        		        	                			
        		        	                			$scope.review(check,data);
        		        	                		}	
        		        						}
        		        					});
        		                };
        		            }
        	        	);
        		}
        		else {
        			$scope.review(check,data);
        		}
			});
        }
        
        //放行
        $scope.review = function(check,data) {
        	$scope.sendRecv("CRM351", "saveReviewStatus", "com.systex.jbranch.app.server.fps.crm351.CRM351InputVO", {'row_data':data,'check':check},
    				function(tota, isError) {
    					if (!isError) {
    						$scope.showMsg('ehl_01_common_004');
    	                	$scope.inquireInit();
                    		$scope.inquire();
    						return;
    					}
    		});
        };
	    
});