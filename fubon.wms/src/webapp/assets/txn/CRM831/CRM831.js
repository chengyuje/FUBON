/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM831Controller',
	function($rootScope, $scope, $controller, getParameter , $confirm, socketService, ngDialog, projInfoService , $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM831Controller";
		
		//初始化
		$scope.init = function(){
        	$scope.inputVO = {
//        			cust_id: $scope.connector('get','CRM830ID'),
        			cust_id:  $scope.custVO.CUST_ID,
        			ins_type: $scope.connector('get','CRM830TYPE'),
        			category: $scope.connector('get','CRM830CATEGORY')
            };
		};
		$scope.init();
		
		
		//xml參數初始化
		$scope.mapData = function() {
			var deferred = $q.defer();
			getParameter.XML(["PRD.INS_TYPE"], function(totas) {
				if (totas) {
					$scope.insTypeList = totas.data[totas.key.indexOf('PRD.INS_TYPE')];
					deferred.resolve();
				}
			});
			return deferred.promise;
		}
		$scope.mapData().then(function() {
			if($scope.inputVO.category != '2') $scope.inquire();
			if($scope.inputVO.category != '1') $scope.inquireJSB();
		});
		
		//初始化
		$scope.inquireInit = function(){
			$scope.resultList = [];
		}
		$scope.inquireInit();
		
		//查詢北富銀
		$scope.inquire = function() {
			$scope.sendRecv("CRM831", "inquire", "com.systex.jbranch.app.server.fps.crm831.CRM831InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
//								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
	    };
//	    $scope.inquire();

	    //查詢日盛保代
		$scope.inquireJSB = function() {
			$scope.sendRecv("CRM831", "inquireJSB", "com.systex.jbranch.app.server.fps.crm831.CRM831InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
//								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultListJSB = tota[0].body.resultList;
							$scope.outputVOJSB = tota[0].body;
							return;
						}
			});
	    };
	    
	    //呼叫DIALOG 北富銀
		$scope.detail = function(row) {
			var custID = $scope.custVO.CUST_ID;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM831/CRM831_detail.html',
				className: 'CRM831_detail',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.custID = custID;
                }]
			});
		};
		
		//呼叫DIALOG 日盛保代
		$scope.detailJSB = function(row) {
			var custID = $scope.custVO.CUST_ID;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM831/CRM831_detailJSB.html',
				className: 'CRM831_detailJSB',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                	$scope.custID = custID;
                }]
			});
		};
		
		//呼叫DIALOG 日盛保代歷史對帳單
		$scope.openBillJSB = function() {
			var custID = $scope.custVO.CUST_ID;
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM831/CRM831_billJSB.html',
				className: 'CRM831_billJSB',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.custID = custID;
                }]
			});
		};
		
		//呼叫DIALOG
		$scope.money = function(row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM831/CRM831_money.html',
				className: 'CRM831_money',
				showClose: false,
                controller: ['$scope', function($scope) {
                	$scope.row = row;
                }]
			});
		};
		
		
		$scope.a = function(row) {
			alert("還沒做");
		};
		
		$scope.b = function(row) {
			alert("還沒做");
		};
		
		
		
});