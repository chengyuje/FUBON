/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM421Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM421Controller";
		
		$scope.role = sysInfoService.getPriID();
		
		$scope.mappingSet['APPLY_CAT'] = [];
		$scope.mappingSet['APPLY_CAT'].push({LABEL:"期間議價" , DATA: '1'},
											{LABEL:"單次議價" , DATA: '2'});
		
		$scope.mappingSet['PROD_CAT'] = [];
		$scope.mappingSet['PROD_CAT'].push({LABEL:"基金" , DATA: '1'},
										   {LABEL:"ETF/股票" , DATA: '2'});

		// filter
		getParameter.XML(["CRM.APPLY_STATUS", "CRM.AUTH_STATUS", "CRM.SINGLE_TYPE"], function(totas) {
			if (totas) {
//				$scope.TEMP = totas.data[totas.key.indexOf('CRM.APPLY_STATUS')];
//				$scope.mappingSet['CRM.APPLY_STATUS'] = [];
//				angular.forEach($scope.TEMP, function(row) {
//					if(row.DATA == "1" || row.DATA == "2" || row.DATA == "3" || row.DATA == "0")
//						$scope.mappingSet['CRM.APPLY_STATUS'].push(row);
//				});
				$scope.mappingSet['APPLY_STATUS'] = totas.data[totas.key.indexOf('CRM.APPLY_STATUS')];
				$scope.mappingSet['AUTH_STATUS'] = totas.data[totas.key.indexOf('CRM.AUTH_STATUS')];
				$scope.mappingSet['CRM.SINGLE_TYPE'] = totas.data[totas.key.indexOf('CRM.SINGLE_TYPE')];
			}
		});
	    //
		
		//2017-8-31 by Jacky 修改為
		$scope.mappingSet['CRM.APPLY_STATUS'] = [];
		$scope.mappingSet['CRM.APPLY_STATUS'].push({LABEL: '暫存', DATA: '0'},
												   {LABEL: '待覆核', DATA: '1'},
				                                   {LABEL: '已同意', DATA: '2'},
				                                   {LABEL: '已終止', DATA: '3'},
				                                   {LABEL: '已退回', DATA: '9'});
		
		// date picker
		$scope.apply_sDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
		};
		$scope.apply_eDateOptions = {
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
			$scope.apply_sDateOptions.maxDate = $scope.inputVO.apply_eDate;
			$scope.apply_sDateOptions.minDate = new Date($scope.inputVO.apply_eDate.getFullYear() - 1, $scope.inputVO.apply_eDate.getMonth(), $scope.inputVO.apply_eDate.getDate());
			
			$scope.apply_eDateOptions.maxDate = new Date($scope.inputVO.apply_sDate.getFullYear() + 1, $scope.inputVO.apply_sDate.getMonth(), $scope.inputVO.apply_sDate.getDate());
			$scope.apply_eDateOptions.minDate = $scope.inputVO.apply_sDate;
		};
		
		$scope.IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
		$scope.init = function(){
			$scope.data = [];
			$scope.temp_resultList = [];
			$scope.resultList = [];
			$scope.outputVO = [];
			
			var startDate = new Date();
			var month = startDate.getMonth();
			startDate.setMonth(month - 6);
			
			$scope.inputVO = {
					bargain_status : [],
					cust_id : '',
					con_degree : '',
					apply_status: '1',
					apply_sDate : startDate,
					apply_eDate : new Date(),
					ao_code : String(sysInfoService.getAoCode()),
			};
			
			$scope.limitDate();
		};
		$scope.init();
		
		$scope.inquireInit = function(){
			$scope.outputVO = [];
			$scope.temp_resultList = [];
			$scope.resultList = [];
			$scope.data = [];
		}
		$scope.inquireInit();

		/*
		 * 期間議價
		 *  
		 * 2016-10-11 add by walala
		 * 2016-12-27 modify by ocean
		 * 
		 */
		$scope.PeriodPage = function() {
//			var cust_id = $scope.inputVO.cust_id.toUpperCase();
			var cust_id = "";
			if($scope.inputVO.cust_id){
				cust_id = $scope.inputVO.cust_id.toUpperCase();				
			}
	    	
			$scope.sendRecv("CRM421", "getCustBranch", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", {'cust_id':cust_id},
					function(tota1, isError) {
						if (!isError) {
//							if (tota1[0].body.branchNbr == null || tota1[0].body.branchNbr == "" || 
//							    sysInfoService.getBranchID().trim() != tota1[0].body.branchNbr.trim()) {
//								$scope.showErrorMsg("ehl_02_CRM421_008");	//此客戶歸屬行非理專分行
//								return;
//		            		} else {
		            			var dialog = ngDialog.open({
		            				template: 'assets/txn/CRM421/CRM421_PeriodPage.html',
		            				className: 'CRM421_PeriodPage',
		            				showClose: false,
		                            controller: ['$scope', function($scope) {
		                            	$scope.cust_id = cust_id;
		                            }]
		            			});
		            			dialog.closePromise.then(function (data) {
		            				if(data.value === 'successful'){
		            					$scope.inquireInit();
		                        		$scope.inquire();
		            				}
		            			});
//		            		}
						}
			});
		}
		
		/*
		 * 單次議價
		 * 
		 * 2016-10-11 add by walala
		 * 2016-12-27 modify by ocean
		 * 
		 */
		$scope.SinglePage = function() {
			var cust_id = "";
			if($scope.inputVO.cust_id){
				cust_id = $scope.inputVO.cust_id.toUpperCase();				
			}
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM421/CRM421_SinglePage.html',
				className: 'CRM421_SinglePage',
				showClose: false,
				 controller: ['$scope', function($scope) {
	                	$scope.cust_id = cust_id;
	             }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
            		$scope.inquire();
				}
			});
		}
		
		/*
		 * 查詢
		 * 
		 * 2016-10-11 add by walala
		 * 2016-12-27 modify by ocean
		 * 
		 */
		$scope.inquire = function() {
			if($scope.inputVO.cust_id){
				$scope.inputVO.cust_id = $scope.inputVO.cust_id.toUpperCase();				
			}
			$scope.sendRecv("CRM421", "inquire", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							
							if($scope.inputVO.cust_id){		//有輸入客戶ID，則由電文取得資料 ==> 前端篩選覆核狀態
								$scope.List = _.sortBy(tota[0].body.resultList, ['APPLY_DATE']).reverse();
								
								if($scope.inputVO.apply_status == '1'){
									angular.forEach($scope.List, function(row) {
										if(row.APPLY_STATUS == '1' || row.APPLY_STATUS == '4'){
											$scope.resultList.push(row);
										}
									});		
								}else{
									angular.forEach($scope.List, function(row) {
										if(row.APPLY_STATUS == $scope.inputVO.apply_status){
											$scope.resultList.push(row);
										}
									});										
								}
								
							}else{		//沒有輸入客戶ID，由DB取得資料 ==> JAVA篩選覆核狀態
								$scope.resultList = _.sortBy(tota[0].body.resultList, ['APPLY_DATE']).reverse();
							}
							
							$scope.outputVO = tota[0].body;
						}
			});
	    };
	    
		/** MAO151-start **/
	    //客戶ID
	    $scope.MAO151_PARAMS = $scope.connector('get', 'MAO151_PARAMS');
		$scope.connector('set', "MAO151_PARAMS", null);
		if($scope.MAO151_PARAMS) {
			$scope.inputVO.cust_id = $scope.MAO151_PARAMS.CUST_ID;
		}
		//申請日期
		$scope.MAO151_APPLIED_TIME = $scope.connector('get', 'MAO151_APPLIED_TIME');
		$scope.connector('set', "MAO151_APPLIED_TIME", null);
		if($scope.MAO151_APPLIED_TIME) {
			$scope.inputVO.apply_sDate = $scope.toJsDate($scope.MAO151_APPLIED_TIME);
			$scope.inputVO.apply_eDate = $scope.toJsDate($scope.MAO151_APPLIED_TIME);
		}
		/** MAO151-end **/
		
	    $scope.doPurchase = function(row) {
	    	var sendURL = 'CRM421';//預設原來頁面
	    	switch (row.APPLY_CAT) {
	    	//期間議價
			case '1':
				//基金
				if(row.APPLY_TYPE == '1'){
					sendURL = 'SOT110';
				}else if(row.APPLY_TYPE == '2'){
					sendURL = 'SOT210';
				}
				$scope.connector('set','SOTCustID',row.CUST_ID);
				break;
			//單次議價
			case '2':
				if(row.APPLY_TYPE == '1'){//基金單筆申購 
					sendURL = 'SOT110';
				}else if(row.APPLY_TYPE == '2'){//基金定期(不)定額申購
					sendURL = 'SOT120';
				}else if(row.APPLY_TYPE == '4'){//海外ETF/股票
					sendURL = 'SOT210';
				}
				$scope.connector('set','SOTCustID',row.CUST_ID);
				$scope.connector('set','SOTProd',row);
				break;
			default:
				break;
			}
	    	$rootScope.menuItemInfo.url = "assets/txn/"+sendURL+"/"+sendURL+".html";
	    }
	    
	    /*
		 * 刪除議價
		 * 
		 * 2016-10-11 add by walala
		 * 2016-12-27 modify by ocean
		 * 
		 */
	    $scope.deleteApplyFlow = function(row) {
	    	$scope.inputVO.seq = row.APPLY_SEQ;
	    	$scope.inputVO.apply_cat = row.APPLY_CAT;
	    	$scope.inputVO.cust_id = row.CUST_ID;
	    	
	    	$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
	    		$scope.sendRecv("CRM421", "deleteApplyFlow", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", $scope.inputVO,
	    				function(totas, isError) {
	                    	if (isError) {
	                    		$scope.showErrorMsg(totas[0].body.msgData);
	                    	}
	                    	if (totas.length > 0) {
    	                		$scope.showSuccessMsg("ehl_01_common_003");
    	                		$scope.inquireInit();
	                    		$scope.inquire();
	                    	};
	    				}
	    		);
	    	});
	    }
	    
	    $scope.terminate = function(row) {	    	
	    	var dialog = ngDialog.open({
				template: 'assets/txn/CRM421/CRM421_terminate.html',
				className: 'CRM421_terminate',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			}).closePromise.then(function (data) {
				if (data.value === 'successful') {
					$scope.inquireInit();
					$scope.inquire();
				}
			});
	    }
	    
	    $scope.terminateAndApply = function(row) {
	    	var dialog = ngDialog.open({
				template: 'assets/txn/CRM421/CRM421_terminateAndApply.html',
				className: 'CRM421_terminateAndApply',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			}).closePromise.then(function (data) {
				if (data.value === 'successful') {
					$scope.inquireInit();
					$scope.inquire();
				}
			});
	    }
	    
	    // 連至客戶首頁
	    $scope.custDTL = function(row) {
	    	//判斷行動載具登入，查詢條件需檢核申請許可時間段的客戶清單
	    	$scope.sendRecv("CRM421", "mobileInquire", "com.systex.jbranch.app.server.fps.crm421.CRM421InputVO", {'cust_id' : row.CUST_ID},
    				function(totas, isError) {
	    				if (!isError) {
	    					$scope.custVO = {
	    		    				CUST_ID :  row.CUST_ID,
	    		    				CUST_NAME :row.CUST_NAME	
	    		    		}
	    		    		$scope.connector('set','CRM_CUSTVO',$scope.custVO);
	    		        	
	    		        	var path = "assets/txn/CRM610/CRM610_MAIN.html";
	    					var set = $scope.connector("set","CRM610URL",path);
	    					var dialog = ngDialog.open({
	    						template: 'assets/txn/CRM610/CRM610.html',
	    						className: 'CRM610',
	    						showClose: false
	    					});
	    				}
	    	});
		}
	    
	    /*
	     * 修改
	     * 
	     * 2016-10-11 add by walala
	     * 2016-12-27 modify by ocean
	     * 
	     */
	    $scope.updateApplyFlow = function(row) {
	    	if (row.APPLY_CAT == "2") { // 單次議價
    			var cust_id = row.CUST_ID;
    			var dialog = ngDialog.open({
    				template: 'assets/txn/CRM421/CRM421_SinglePage.html',
    				className: 'CRM421_SinglePage',
    				showClose: false,
    				controller: ['$scope', function($scope) {
    					$scope.singleDTL = row;
    					$scope.cust_id = cust_id;
    				}]
    			});
    			dialog.closePromise.then(function (data) {
    				if(data.value === 'successful'){
    			    	$scope.singleDTL = undefined;
    			    	$scope.inquireInit();
    					$scope.inquire();
    				}
    			});
	    	}
	    };
	    
	    /*
	     * 查詢明細狀態
	     */
	    $scope.detail = function(row) {
	    	console.log("aaaaa");
    		var dialog = ngDialog.open({
    			template: 'assets/txn/CRM431/CRM431_DETAIL.html',
    			className: 'CRM431_DETAIL',
    			showClose: false, 
    			controller: ['$scope', function($scope) {
    				$scope.row = row;
    			}]
    		
    		});
    		console.log("bbbb");
    		dialog.closePromise.then(function(data) {
    			if(data.value === 'successful'){//新增時
    				$scope.init();
    				$scope.query();
    			}
    		});
    	};
	    
});
