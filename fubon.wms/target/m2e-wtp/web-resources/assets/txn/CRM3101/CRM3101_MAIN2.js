/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3101_MAIN2Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM3101_MAIN2Controller";
		
		// combobox
		getParameter.XML(["CRM.TRS_DELCAM_ULIST_STATUS"], function(totas) {
			if (totas) {
				$scope.TRS_DELCAM_ULIST_STATUS = totas.data[totas.key.indexOf('CRM.TRS_DELCAM_ULIST_STATUS')];
			}
		});
		
		$scope.sendRecv("CRM3101", "getListName", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.ListName = [];
						angular.forEach(tota[0].body.resultList, function(row) {
							$scope.ListName.push({LABEL: row.LIST_NAME, DATA: row.SEQ});
	        			});
						return;
					}
		});
		
		// date picker
		$scope.sDateOptions = {};
		$scope.eDateOptions = {};
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.edate;
			$scope.eDateOptions.minDate = $scope.inputVO.sdate;
		};
		//
		
		$scope.init = function() {
			$scope.inputVO = {};
		};
		$scope.init();
		$scope.inquireInit = function() {
			$scope.resultList = [];
			$scope.data = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
		
		$scope.inquire = function() {
			$scope.sendRecv("CRM3101", "inquire2", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO",$scope.inputVO,
					function(tota, isError) {
						if(!isError) {
							if (tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
							}
							else {
								$scope.resultList = tota[0].body.resultList;
								$scope.outputVO = tota[0].body;
								angular.forEach($scope.resultList, function(row) {
									row.set = [];
									row.set.push({LABEL: "刪除名單", DATA: "1"});
									row.set.push({LABEL: "下載名單", DATA: "2"});
								});
		                	}
					   }
				});
		};
		
		$scope.insertExp = function() {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM3101/CRM3101_UPLOAD.html',
				className: 'CRM3101_UPLOAD',
				showClose: false
			});
			dialog.closePromise.then(function(data) {
				if(data.value === 'successful') {
					$scope.inquireInit();
					$scope.inquire();
	  			}
			});
		};
		
		$scope.download = function(row) {
        	$scope.sendRecv("CRM3101", "downloadListFile", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {'seq': row.SEQ, 'list_name': row.LIST_FILE_NAME},
					function(totas, isError) {
		        		if (!isError) {
		        			if(totas[0].body.resultList && totas[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
						}
					}
			);
        };
        $scope.download2 = function(row) {
        	$scope.sendRecv("CRM3101", "downloadBrhFile", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {'seq': row.SEQ, 'list_name': row.BRH_FILE_NAME},
					function(totas, isError) {
		        		if (!isError) {
		        			if(totas[0].body.resultList && totas[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
						}
					}
			);
        };
        
        $scope.action = function(row) {
        	if(row.cmbAction) {
        		// 刪除名單
        		if(row.cmbAction == "1") {
        			$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
        				$scope.sendRecv("CRM3101", "delete", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {'seq': row.SEQ},
            					function(tota, isError) {
            						if (!isError) {
            							$scope.showSuccessMsg("ehl_01_common_003");
                                		$scope.inquireInit();
                                		$scope.inquire();
            						}
            			});
        			});
        		}
        		// 下載名單
        		else if (row.cmbAction == "2") {
        			if(row.BACK_CNT < row.TOTAL_CNT) {
        				$scope.showErrorMsg('達康尚未回檔完成');
                		return;
        			}
        			$scope.sendRecv("CRM3101", "downloadMast", "com.systex.jbranch.app.server.fps.crm3101.CRM3101InputVO", {'seq': row.SEQ},
        					function(totas, isError) {
        		        		if (!isError) {
        		        			if(totas[0].body.resultList && totas[0].body.resultList.length == 0) {
        	                			$scope.showMsg("ehl_01_common_009");
        	                			return;
        	                		}
        						}
        					}
        			);
        		}
        		row.cmbAction = "";
        	}
        };
		
		
});