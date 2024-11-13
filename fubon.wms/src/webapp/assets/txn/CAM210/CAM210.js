'use strict';
eSoafApp.controller('CAM210Controller', function($scope, $rootScope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService, getParameter) {
	$controller('BaseController', {$scope: $scope});
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	$scope.controllerName = "CAM210Controller";
	
	/*
	 * 取得UHRM人員清單(由員工檔+角色檔)
	 */
	$scope.getUHRMList = function() {
		$scope.inputVO.regionCenterID = $scope.inputVO.region;
		$scope.inputVO.branchAreaID = $scope.inputVO.op;
		
		$scope.sendRecv("ORG260", "getUHRMList", "com.systex.jbranch.app.server.fps.org260.ORG260InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				return;
			}
			if (tota.length > 0) {
				$scope.mappingSet['UHRM_LIST'] = tota[0].body.uhrmList;
				$scope.inputVO.pCode = tota[0].body.uEmpID;
			}
		});
	};
	
	$scope.priID = String(sysInfoService.getPriID());
	$scope.memLoginFlag = String(sysInfoService.getMemLoginFlag()).toUpperCase();
	
	// 2017/9/13 date picker
	// 活動起訖日
	$scope.camp_sDateOptions = {};
	$scope.camp_eDateOptions = {};
	$scope.camp_esDateOptions = {};
	$scope.camp_eeDateOptions = {};
	// config
	$scope.altInputFormats = ['M!/d!/yyyy'];
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	$scope.limitDate = function() {
		$scope.camp_sDateOptions.maxDate = $scope.inputVO.camp_eDate;
		$scope.camp_eDateOptions.minDate = $scope.inputVO.camp_sDate;
	};
	$scope.limitDate2 = function() {
		$scope.camp_esDateOptions.maxDate = $scope.inputVO.camp_eeDate;
		$scope.camp_eeDateOptions.minDate = $scope.inputVO.camp_esDate;
	};
	// date picker end
	
	$scope.refreshAoCode = function () {
		// 2017/6/2
		var trytestAO = $filter('filter')($scope["TOTAL_AO_LIST"], {'AO_CODE': $scope.inputVO.aoCode})[0];
		if(!trytestAO)
			$scope.inputVO.aoCode = "";
	};
	
	$scope.passParams = $scope.connector('get','passParams');
	$scope.connector('set','passParams', null);
	$scope.comeFromCrm141 = $scope.connector('get','comeFromCrm141');
	$scope.connector('set','comeFromCrm141', null);
	$scope.comeFromCrm132 = $scope.connector('get','comeFromCrm132');
	$scope.connector('set','comeFromCrm132', null);
	
	$scope.fromOTHER = function() {
		if($scope.passParams != null){				
			$scope.tabNumber = '3';
			$scope.tabSheet = 2;
			$scope.query_3();
		}
		if($scope.comeFromCrm141 != null || $scope.comeFromCrm132 != null){				
			$scope.tabNumber = '4';
			$scope.tabSheet = 3;
			$scope.query_4();
		}
	};
	
	$scope.tabNumber = ($scope.connector('get', 'tabNumber') != undefined ? $scope.connector('get', 'tabNumber') : '1');
	$scope.tabSheet = ($scope.connector('get', 'tab') != undefined ? $scope.connector('get', 'tab') : 0);
	
	$scope.inquireInit = function() {
		$scope.paramList1 = [];
		$scope.outputVO1 = {};
		
		$scope.paramList2 = [];
		$scope.outputVO2 = {};
		
		$scope.paramList3 = [];
		$scope.outputVO3 = {};
		
		$scope.paramList4 = [];
		$scope.outputVO4 = {};
		
		$scope.paramList5 = [];
		$scope.outputVO5 = {};
		
		$scope.paramList6 = [];
		$scope.outputVO6 = {};

		$scope.reSetList = [];
		$scope.LEAD_TYPE = []; 
		
		// #0000114
	    getParameter.XML(["CAM.LEAD_TYPE"], function(totas) {
			if (totas) {
				$scope.TEMP2 = totas.data[totas.key.indexOf('CAM.LEAD_TYPE')];
			}

			$scope.LEAD_TYPE = []; 
			angular.forEach($scope.TEMP2, function(row) {
				if (($scope.tabSheet == '0' || $scope.tabSheet == '1') && row.DATA != "04") { //  || $scope.tabSheet == '2'
					$scope.LEAD_TYPE.push(row);
				} else if ($scope.tabSheet == '2' || $scope.tabSheet == '3' || $scope.tabSheet == '4'){
					$scope.LEAD_TYPE.push(row);
				}
			});
		});
	}
	
	$scope.inquireInit();
	
	$scope.init = function() {
		$scope.inputVO = {
			leadType : ($scope.connector('get', 'leadType') != undefined ? $scope.connector('get', 'leadType') : ''),
			campType : ($scope.connector('get', 'campType') != undefined ? $scope.connector('get', 'campType') : '')
		};
		$scope.connector('set', 'leadType', '');
		$scope.connector('set', 'campType', '');

		var min_mon = new Date();
		min_mon.setMonth(min_mon.getMonth() - 2, 1);
		min_mon.setHours(0, 0, 0, 0);
		$scope.inputVO.camp_sDate = min_mon;
		$scope.limitDate();
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region", "REGION_LIST", "op", "AREA_LIST", "branch", "BRANCH_LIST", "aoCode", "AO_LIST", "empId", "EMP_LIST"];
        $scope.RegionController_setName($scope.region).then(function(data) {
        	if($scope.EMP_LIST.length > 2) {
        		$scope.inputVO.empId = "";
        		$scope.inputVO.aoCode = "";
        		$scope.fromOTHER();
        	}
        });
        
        //登入者權限
		$scope.loginRole = function (){
			$scope.sendRecv("CRM131", "login", "com.systex.jbranch.app.server.fps.crm131.CRM131InputVO", {}, function(tota, isError) {
				if (!isError) {
					if(tota[0].body.privilege.length == 0) {
            			return;
            		}else{
						$scope.privilege = tota[0].body.privilege;
						$scope.inputVO.mroleid = $scope.privilege[0].COUNTS;
            		}
				}
			})
		};
		
		$scope.loginRole();
	};
	
	$scope.retuenTabNumber1 = function() {			
		$scope.tabNumber = '1';
	}
	
	$scope.inquire = function() {
		if ($scope.memLoginFlag.startsWith('UHRM') && $scope.memLoginFlag != 'UHRM') {
			
		} else {
			if ((sysInfoService.getMemLoginFlag()).toLowerCase().startsWith("uhrm")) {
			} else if(!$scope.inputVO.op && !$scope.inputVO.custId) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
		}
		
		if($scope.inputVO.custId)
			$scope.inputVO.custId = $scope.inputVO.custId.toUpperCase();
		
		$scope.sendRecv("CAM210", "queryData", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
			if (!isError) {
				if(tota[0].body.resultList.length == 0) {
					$scope.paramList1 = [];
					$scope.showMsg("ehl_01_common_009");
        			return;
        		}
				
				$scope.paramList1 = tota[0].body.resultList;
				$scope.outputVO1 = tota[0].body;
				return;
			}
		});
	};
	// 預設開啟第一頁 進到這個畫面應該就要自動查出資料
	 
	/*****理專下活動資訊*****/
	$scope.txtAO_1 = function (row) {	
		var input = $scope.inputVO;
		var dialog = ngDialog.open({
			template: 'assets/txn/CAM210/CAM210_1.html',
			className: 'CAM210_1_DETAIL',
            showClose: false,
            controller: ['$scope', function($scope) {
           	   $scope.row = row;
           	   $scope.input = input;
            }]
		});
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.inquireInit();
				$scope.inquire();
			}
		});
     };
     
     $scope.query_2 = function(type) {
    	 $scope.tabNumber = '2';
    	 
    	 if ('Y' == type) {
    		 if ((sysInfoService.getMemLoginFlag()).toLowerCase().startsWith("uhrm")) {
    		 } else if(!$scope.inputVO.op && !$scope.inputVO.custId) {
    			 $scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
    			 return;
    		 }

	    	 if($scope.inputVO.custId)
	    		 $scope.inputVO.custId = $scope.inputVO.custId.toUpperCase();
	    	 
	    	 $scope.sendRecv("CAM210", "queryData_2", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
	                	return;
					}
					$scope.paramList2 = tota[0].body.resultList;
					$scope.outputVO2 = tota[0].body;
					return;
				}
			});
    	 }
	};
	
	/*****AO下活動資訊*****/
    $scope.txtAO_2 = function (row) {	
		 var dialog = ngDialog.open({
	            template: 'assets/txn/CAM210/CAM210_2.html',
	            className: 'CAM210_2_DETAIL',
	            showClose: false,
	            controller: ['$scope', function($scope) {
	           	   $scope.row = row;
	            }]
	        });
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.inquire();
			}
		});
	};
	
	$scope.status = "";
	$scope.query_3 = function(type) {
		$scope.tabNumber = '3';
		
		if ('Y' == type) {
			if ((sysInfoService.getMemLoginFlag()).toLowerCase().startsWith("uhrm")) {
			} else if(!$scope.inputVO.op && !$scope.inputVO.custId) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}

	    	if($scope.inputVO.custId)
	    		$scope.inputVO.custId = $scope.inputVO.custId.toUpperCase();
	    	
			$scope.sendRecv("CAM210", "queryData_3", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
	                	return;
	                }
					$scope.paramList3 = tota[0].body.resultList;
					$scope.outputVO3 = tota[0].body;
					for(var i=0; i<$scope.paramList3.length; i++){
						$scope.status = $scope.paramList3[i].STATUS;
						if($scope.status == "Y"){					
							$scope.redLight = "block";
						}else{								
							$scope.redLight = "none";
						}
					}
					return;
				}
			});
		}
	};
	
	/*****名單改/派作廢*****/
    $scope.editList = function (row) {
    	var dialog = ngDialog.open({
    		template: 'assets/txn/CAM210/CAM210_3.html',
    		className: 'CAM210_3_DETAIL',
    		showClose: true,
    		controller: ['$scope', function($scope) {
    			$scope.row = row;
    		}]
    	});
    	dialog.closePromise.then(function (data) {
    		if(data.value === 'successful'){
    			$scope.inquireInit();
 	  			$scope.inquire();
    		}
    	}); 
    };
    
	$scope.query_4 = function(type) {
		$scope.tabNumber = '4';
		
		if($scope.connector('get','CRM141_ROW')) {
			var temp = $scope.connector('get','CRM141_ROW');
			$scope.inputVO.campaignName = temp.CAMPAIGN_NAME;
			$scope.inputVO.stepId = temp.STEP_ID;
			$scope.inputVO.camp_sDate = $scope.toJsDate(temp.START_DATE);
			$scope.connector('set','CRM141_ROW', null);
		}
		
		if($scope.connector('get','CRM132_ROW')) {
			var temp = $scope.connector('get','CRM132_ROW');
			$scope.inputVO.campaignName = temp.CAMPAIGN_NAME;
			$scope.inputVO.stepId = temp.STEP_ID;
			$scope.inputVO.camp_sDate = $scope.toJsDate(temp.CAMP_START_DATE);
			$scope.connector('set','CRM132_ROW', null);
		}
		
		if ('Y' == type) {
			if (String(sysInfoService.getMemLoginFlag()).toLowerCase().startsWith('uhrm')) {
				
			} else {
				if (!$scope.inputVO.op && !$scope.inputVO.campaignName && !$scope.inputVO.custId) {
					$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
					return;
				}
			}
			
	    	if($scope.inputVO.custId)
	    		$scope.inputVO.custId = $scope.inputVO.custId.toUpperCase();
	    	
			$scope.sendRecv("CAM210", "queryData_4", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
						return;
					}
					$scope.paramList4 = tota[0].body.resultList;
					$scope.outputVO4 = tota[0].body;
					for(var i=0; i<$scope.paramList4.length; i++) {
						if($scope.paramList4[i].LEAD_COUNTS==0){
							$scope.paramList4[i].LEAD_COUNTS ='';
							$scope.paramList4[i].LEAD_CLOSE ='';
							$scope.paramList4[i].INVS_BAL ='';
							$scope.paramList4[i].INSS_BAL ='';
							$scope.paramList4[i].CARD_SUM ='';
							$scope.paramList4[i].LOAN1_SUM ='';
							$scope.paramList4[i].LOAN2_SUM ='';
						}
					}
					return;
				}
			});
		}
	};
	
	/*****即期活動 By 活動*****/	
	$scope.editList = function (row) {
      var dialog = ngDialog.open({
          template: 'assets/txn/CAM210/CAM210_3.html',
          className: 'modal-dialog-CAM210_3',
          showClose: true,
          controller: ['$scope', function($scope) {
      	   $scope.row = row;
          }]
      });
      dialog.closePromise.then(function (data) {
          if(data.value === 'successful'){
        	$scope.paramList3 = []; 
        	$scope.outputVO3 = {}; 
      	    $scope.query_3();
          }
      }); 
    };
	
	$scope.txtAO_4 = function (row) {
		var input = $scope.inputVO;
		var dialog = ngDialog.open({
			template: 'assets/txn/CAM210/CAM210_4.html',
			className: 'CAM210_4_DETAIL',
			showClose: false,
			controller: ['$scope', function($scope) {
				$scope.row = row;
				$scope.input = input;
			}]
		});
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.inquireInit();
				$scope.inquire();
			}
		});
	};
	
	$scope.query_5 = function(type){
		$scope.tabNumber = '5';
		
		if ('Y' == type) {
			if (String(sysInfoService.getMemLoginFlag()).toLowerCase().startsWith('uhrm')) {
				
			} else {
				if (!$scope.inputVO.op && !$scope.inputVO.campaignName && !$scope.inputVO.custId) {
					$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
					return;
				}
			}

			if($scope.inputVO.custId)
				$scope.inputVO.custId = $scope.inputVO.custId.toUpperCase();
			
			$scope.sendRecv("CAM210", "queryData_5", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
	                	return;
	                }
					$scope.paramList5 = tota[0].body.resultList;
					$scope.outputVO5 = tota[0].body;
					for(var i=0; i<$scope.paramList5.length; i++) {
						if($scope.paramList5[i].LEAD_COUNTS==0){
							$scope.paramList5[i].LEAD_COUNTS ='';
							$scope.paramList5[i].LEAD_CLOSE ='';
							$scope.paramList5[i].INVS_BAL ='';
							$scope.paramList5[i].INSS_BAL ='';
							$scope.paramList5[i].CARD_SUM ='';
							$scope.paramList5[i].LOAN1_SUM ='';
							$scope.paramList5[i].LOAN2_SUM ='';
						}
					}
					return;
				}
			});
		}
	};
	
	$scope.inputVO.clickAllType = undefined;
	$scope.clickAll = function() {
		switch ($scope.inputVO.clickAllType) {
			case "page":
				angular.forEach($scope.paramList6, function(row) {
	    			row.SELECTED = 'N';
	    			
	    		});
				
				$scope.reSetList = [];
				
	    		angular.forEach(this.data6, function(row) {
	    			row.SELECTED = 'Y';
	    			$scope.reSetList.push(row);
	    		});
				break;
			case "list":
				angular.forEach($scope.paramList6, function(row) {
	    			row.SELECTED = 'Y';
	    			$scope.reSetList.push(row);
	    		});
				break;
			default:
				$scope.reSetList = [];
			
				angular.forEach($scope.paramList6, function(row) {
	    			row.SELECTED = 'N';
	    		});
				break;
		}
	}
	
	$scope.setReSetList = function (row) {
		var idx = $scope.reSetList.indexOf(row);
    	
		switch (row.SELECTED) {
			case "Y":
				$scope.reSetList.push(row);
				break;
			default:
				$scope.reSetList.splice(idx, 1);
				break;
		}
	}
    
	$scope.chgLeads = function () {
		var reLeads = $scope.reSetList;
		
    	var dialog = ngDialog.open({
    		template: 'assets/txn/CAM210/CAM210_CHGLEADS.html',
    		className: 'CAM210_CHGLEADS',
    		showClose: true,
    		controller: ['$scope', function($scope) {
    			$scope.reLeads = reLeads;
    		}]
    	});
    	dialog.closePromise.then(function (data) {
    		if(data.value === 'successful'){
    			$scope.paramList6 = [];
    			$scope.outputVO6 = {};
 	  			$scope.query_6('Y');
    		}
    	}); 
	}
	
	$scope.query_6 = function(type){
		$scope.tabNumber = '6';
		$scope.paramList6 = [];
		$scope.outputVO6 = {};
		
		if ('Y' == type) {
			if(!$scope.inputVO.op && !$scope.inputVO.campaignName && !$scope.inputVO.custId) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}

			if($scope.inputVO.custId)
				$scope.inputVO.custId = $scope.inputVO.custId.toUpperCase();
			
			$scope.sendRecv("CAM210", "queryData_6", "com.systex.jbranch.app.server.fps.cam210.CAM210InputVO", $scope.inputVO, function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.showMsg("ehl_01_common_009");
	                	return;
	                }
					
					$scope.paramList6 = tota[0].body.resultList;
					$scope.outputVO6 = tota[0].body;
					
					return;
				}
			});
		}
	};
	
	/*****已過期活動 By 活動*****/
	$scope.txtAO_5 = function (row) {	
		var input = $scope.inputVO;
		var dialog = ngDialog.open({
			template: 'assets/txn/CAM210/CAM210_5.html',
			className: 'CAM210_5_DETAIL',
			showClose: true,
			controller: ['$scope', function($scope) {
				$scope.row = row;
				$scope.input = input;
			}]
		});
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.inquireInit();
				$scope.inquire();
			}
		});
	};
	
	// 2018/1/15 等定義完再INIT
	$scope.init();
	
	// 20170524 ADD BY OCEAN
	$scope.download = function() {
		if($scope.tabSheet == '3' || $scope.tabSheet == '4') {
			if(!$scope.inputVO.op && !$scope.inputVO.campaignName) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
		} else {
			if(!$scope.inputVO.op) {
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
		}
		var temp = $scope.inputVO;
		var dialog = ngDialog.open({
			template: 'assets/txn/CAM210/CAM210_DOWNLOAD.html',
			className: 'CAM210_DOWNLOAD',
			showClose: false,
            controller: ['$scope', function($scope) {
            	$scope.inputVO = temp;
            }]
		});
	};
	
	$scope.goAction = function() {
		switch ($scope.tabNumber) {
			case '1':
				$scope.inquire();
				break;
			case '2':
				$scope.query_2('N');
				break;
			case '3':
				$scope.query_3('N');
				break;
			case '4':
				$scope.query_4('N');
				break;
			case '5':
				$scope.query_5('N');
				break;
			case '6':
				$scope.query_6('N');
				break;
		}
    };
    
    $scope.custConnectData = function(row) {
    	var custID = row.CUST_ID;
    	var custName = row.CUST_NAME;
		var dialog = ngDialog.open({
			template: 'assets/txn/CAM190/CAM190_ROUTE.html',
			className: 'CAM200',
			showClose: false,
			scope : $scope,
			controller: ['$scope', function($scope) {
				$scope.txnName = "名單執行紀錄";
        		$scope.routeURL = 'assets/txn/CAM200/CAM200.html';
        		$scope.connector('set','custID', row.CUST_ID);
            	$scope.connector('set','custName', row.CUST_NAME);
            	$scope.connector('set','tab', 'tab1');
            }]
		}).closePromise.then(function (data) {
			switch ($scope.tabNumber) {
				case '1':
					$scope.inquire();
					break;
				case '2':
					$scope.query_2('Y');
					break;
				case '3':
					$scope.query_3('Y');
					break;
				case '4':
					$scope.query_4('Y');
					break;
				case '5':
					$scope.query_5('Y');
					break;
				case '6':
					$scope.query_6('Y');
					break;
			}
		});
	};
	
	// 連至客戶首頁
    $scope.custDTL = function(row) {
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
	};
	
	// #0000109
	$scope.getCampaignName = function () {
		var input = $scope.inputVO;
		var tabSheet = $scope.tabSheet;
		var dialog = ngDialog.open({
			template: 'assets/txn/CAM210/CAM210_GETCNAME.html',
			className: 'CAM210_GETCNAME',
			showClose: true,
			controller: ['$scope', function($scope) {
				$scope.input = input;
				$scope.tabSheet = tabSheet;
			}]
		});
		dialog.closePromise.then(function (data) {
			if (data.value != 'cancel') {
				$scope.inputVO.campaignName = data.value;
			}
		}); 
    };
});