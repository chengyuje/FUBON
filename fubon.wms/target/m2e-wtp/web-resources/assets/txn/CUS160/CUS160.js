/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CUS160Controller',
	function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CUS160Controller";

		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		
		// == filter
		getParameter.XML(["CAM.TASK_STATUS", "CAM.TASK_SOURCE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CAM.TASK_STATUS'] = totas.data[totas.key.indexOf('CAM.TASK_STATUS')];
				$scope.mappingSet['CAM.TASK_SOURCE'] = totas.data[totas.key.indexOf('CAM.TASK_SOURCE')];
			}
		});
		// ===
		
		$scope.startDateOptions = {
			maxDate: $scope.inputVO.eDate || $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.endDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.inputVO.sDate || $scope.minDate
		};
		$scope.altInputFormats = ['M!/d!/yyyy'];
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.startDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.endDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
		};
		
//		// 取得分行列表
//		$scope.getBranch = function() {
//			$scope.bra_list = projInfoService.getAvailBranch();
//			$scope.mappingSet['branchsDesc'] = [];
//			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
//				$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});  
//			});
//	    };

//        // 該分行所有AO
//        $scope.getEmpList = function() {
//        	var deferred = $q.defer();
//			$scope.sendRecv("CUS160", "getAOEmpList", "com.systex.jbranch.app.server.fps.cus160.CUS160InputVO", {branchID: $scope.inputVO.branchID},
//        			function(tota, isError) {
//						$scope.mappingSet['empList'] = [];
//						$scope.inputVO.empID = '';
//        				if (!isError) {
//        					if (projInfoService.getAoCode().length > 0) {
//        						$scope.mappingSet['empList'].push({LABEL: projInfoService.getUserName(), DATA: projInfoService.getUserID()});
//        					} else {
//            					angular.forEach(tota[0].body.aoEmpList, function(row, index, objs){
//            						$scope.mappingSet['empList'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});  
//            					});
//        					}
//        					$scope.emp_list = [];
//        					$.extend($scope.emp_list, $scope.mappingSet['empList']);
//        					deferred.resolve("success");
//        				}
//        			}
//			);
//			return deferred.promise;
//        };

        $scope.init = function(){
        	
        	$scope.priID = String(sysInfoService.getPriID());
        	
			$scope.inputVO = {
					branchID: '', 
					status: '01', 
					taskSource: '',
					taskStatus: '',
					custID: ($scope.custVO == undefined ? '' : $scope.custVO.CUST_ID), 
					custName: '', 
					sDate: undefined,
					eDate: undefined, 
					empID: ''
        	};
//			$scope.inputVO.branchID = ("000" == sysInfoService.getBranchID() ? "" : sysInfoService.getBranchID());
//			$scope.getBranch();
//		    $scope.getEmpList().then(function(data) {
//		    	var exIndex = $scope.mappingSet['empList'].map(function(e) { return e.DATA; }).indexOf(projInfoService.getUserID());
//		    	if($scope.inputVO.branchID && exIndex > -1)
//		    		$scope.inputVO.empID = projInfoService.getUserID();
//			});
		    
		  //組織連動
	        $scope.region = ['N', $scope.inputVO, "regionCenterID", "REGION_LIST", "branchAreaID", "AREA_LIST", "branchID", "BRANCH_LIST", "aoCode", "AO_LIST", "emp_id", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
		};
        $scope.init();
        $scope.inquireInit = function(){
        	$scope.taskList = [];
        }
        $scope.inquireInit();
        
        $scope.addTask = function() {
        	$scope.connector('set', "CRM122_EMPID",sysInfoService.getUserID());
        	var dialog = ngDialog.open({
				template: 'assets/txn/CUS160/CUS160_ADD.html',
				className: 'CAM160',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.CUS160PAGE = "CUS160";
                }]
			}).closePromise.then(function (data) { // 新增自建提醒後，編輯名單狀態與訪談內容
				if(data.value === 'successful'){
					$scope.inquire();
				}
			});
        }
		
        $scope.inquire = function(){
        	$scope.sendRecv("CUS160", "queryData", "com.systex.jbranch.app.server.fps.cus160.CUS160InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.taskList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		
		$scope.custConnectData = function(row) {
        	$scope.connector('set','custID', row.CUST_ID);
        	$scope.connector('set','custName', row.CUST_NAME);
        	$scope.connector('set','tab', 'tab1');
    		$rootScope.menuItemInfo.url = "assets/txn/CAM200/CAM200.html";
        }
		
		$scope.custConnectData = function(row) {
        	var custID = row.CUST_ID;
        	var custName = row.CUST_NAME;
			var dialog = ngDialog.open({
				template: 'assets/txn/CUS160/CUS160_ROUTE.html',
				className: 'CAM200',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
					$scope.txnName = "名單執行紀錄";
	        		$scope.routeURL = 'assets/txn/CAM200/CAM200.html';
	        		$scope.connector('set','custID', row.CUST_ID);
	            	$scope.connector('set','custName', row.CUST_NAME);
	                $scope.connector('set', 'tab', 'tab1');
	            }]
			}).closePromise.then(function (data) {
//				if(data.value != "cancel") {
					$scope.inquire();
//				}
			});
		};
		
		$scope.custDTL = function(row) {
			// 客戶首頁 by cam190
        	var vo = {
        		CUST_ID: row.CUST_ID,
        		CUST_NAME: row.CUST_NAME
        	};
        	$scope.connector('set','CRM_CUSTVO', vo);
        	var set = $scope.connector("set","CRM610URL","assets/txn/CRM610/CRM610_MAIN.html");
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
		}
		
		$scope.showBack = $scope.connector("get", "CRM671");
		$scope.connector("set", "CRM671", null);
		
		$scope.reBack = function() {
			$scope.connector('set','CRM610_tab', 2);
			var path = "assets/txn/CRM610/CRM610_DETAIL.html";
			$scope.connector("set", "CRM610URL", path);
		}
        
});