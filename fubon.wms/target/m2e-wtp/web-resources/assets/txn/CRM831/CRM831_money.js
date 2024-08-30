/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM831_moneyController',
	function($rootScope, $scope, $controller, getParameter, $confirm, socketService, ngDialog, projInfoService , $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM831_moneyController";
		
		//初始化
//		$scope.init = function(){
//        	$scope.inputVO = {
//        			cust_id: $scope.connector('get','CRM830ID'),
//        			ins_type: $scope.connector('get','CRM830TYPE'),
//					policy_nbr: $scope.row.POLICY_NBR
//            };
//		};
		//初始化
		$scope.init = function(){
        	$scope.inputVO = {
        			cust_id:  $scope.custID,
        			ins_type: $scope.row.INS_TYPE,
        			policy_nbr_str: $scope.row.POLICY_NBR_STR,
					policy_nbr: $scope.row.POLICY_NBR,
					policy_seq: $scope.row.POLICY_SEQ,
					id_dup : $scope.row.ID_DUP,
					contract_status : $scope.row.CONTRACT_STATUS
            };
        	
        	if ($scope.row.CONTRACT_STATUS == '16') {
        		var str = '保單自動墊繳中\n相關繳費資訊請至保經代服務網-保費帳務(墊繳資料)查詢';
        		var dialog = ngDialog.open({
    				template: 'assets/txn/CONFIRM/CONFIRM.html',
    				className: 'CONFIRM',
    				showClose: false,
    				scope : $scope,
    				controller: ['$scope', function($scope) {
    					$scope.dialogLabel = str;
    	            }]
    			});
        	}        	
		};
		$scope.init();
		
		//xml參數初始化
		$scope.mapData = function() {
			var deferred = $q.defer();
			getParameter.XML(["CRM.CRM831_PAY_TYPE","CRM.PAY_WAY"], function(totas) {
				if (totas) {
					$scope.payTypeList = totas.data[totas.key.indexOf('CRM.CRM831_PAY_TYPE')];
					$scope.paywayList = totas.data[totas.key.indexOf('CRM.PAY_WAY')];
					deferred.resolve();
				}
			});
			return deferred.promise;
		}
		$scope.mapData().then(function() {$scope.inquire3();});
		
		//初始化
		$scope.inquireInit = function(){
			$scope.resultList2 = [];
		}
		$scope.inquireInit();
		
		//查詢
		$scope.inquire3 = function() {
			$scope.sendRecv("CRM831", "inquire3", "com.systex.jbranch.app.server.fps.crm831.CRM831InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList2.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList2 = tota[0].body.resultList2;
							$scope.outputVO = tota[0].body;
						}
			});
	    };
//	    $scope.inquire3();
		
		
		
});