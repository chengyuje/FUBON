/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM251_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM251_DETAILController";
		
		$scope.priID = String(sysInfoService.getPriID());
		
		// combobox
		getParameter.XML(["CRM.VIP_DEGREE", "CRM.CON_DEGREE", "CRM.CUST_GENDER"], function(totas) {
			if (totas) {
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
				$scope.CUST_GENDER = totas.data[totas.key.indexOf('CRM.CUST_GENDER')];
			}
		});
		
		$scope.EMPID = projInfoService.getUserName();
		$scope.inquire = function(){
			$scope.sendRecv("CRM251", "getDetail", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", {'group_id': $scope.row.GROUP_ID},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.totalList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		$scope.inquire();
		// 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        	$scope.totalList = [];
        };
        $scope.inquireInit();
        
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
        		angular.forEach($scope.totalList, function(row){
    				row.CHECK = true;
    			});
        	} else {
        		angular.forEach($scope.totalList, function(row){
    				row.CHECK = false;
    			});
        	}
        };
        
        $scope.del = function() {
        	var ans = $scope.totalList.filter(function(obj){
	    		return (obj.CHECK == true);
	    	});
        	if(ans.length == 0){
        		return;
        	}
        	$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
        		$scope.sendRecv("CRM251", "deleteCustGroup", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", {'chkId': ans},
        				function(totas, isError) {
                        	if (isError) {
                        		$scope.showErrorMsg(totas[0].body.msgData);
                        	}
                        	if (totas.length > 0) {
                        		$scope.showSuccessMsg('刪除成功');
                        		$scope.inquireInit();
                        		$scope.inquire();
                        	};
        				}
        		);
        	});
        };
        
        $scope.checkMail = function(ans) {
        	var deferred = $q.defer();
        	$scope.sendRecv("CRM251", "checkMail", "com.systex.jbranch.app.server.fps.crm251.CRM251InputVO", {'chkId': ans},
				function(tota, isError) {
					if (!isError) {
						deferred.resolve(tota[0].body.resultList);
					}
			});
        	return deferred.promise;
        };
        
        $scope.email = function() {
        	var ans = $scope.totalList.filter(function(obj){
	    		return (obj.CHECK == true);
	    	});
        	if(ans.length == 0){
        		return;
        	}
        	// 2017/4/26 add
        	$scope.checkMail(ans).then(function(data) {
        		var dialog = ngDialog.open({
    				template: 'assets/txn/CUS110/CUS110.html',
    				className: 'CUS110',
    				showClose: false,
                    controller: ['$scope', function($scope) {
                    	$scope.custID = data;
                    	$scope.recipientType = "CUST";
                    	$scope.isConfirm = false;
                    }]
    			});
    			dialog.closePromise.then(function (data) {
    				if(data.value === 'successful') {				
    					$scope.inquire();			
    				}
    			});
        	});
        };
        
        
		
});
