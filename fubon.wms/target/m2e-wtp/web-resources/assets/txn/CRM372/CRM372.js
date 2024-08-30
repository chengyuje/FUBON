/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CRM372Controller',
  function ($scope, $controller, ngDialog, socketService, alerts, projInfoService, $q, getParameter, $filter, $confirm, sysInfoService) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'CRM372Controller';
    
    // date picker
	$scope.model = {};
	$scope.open = function($event, elementOpened) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.model[elementOpened] = !$scope.model[elementOpened];
	};
	
	// combobox
	getParameter.XML(["CRM.PRJ_STATUS"], function(totas) {
		if (totas) {
			$scope.crmPrjStatusList = totas.data[totas.key.indexOf('CRM.PRJ_STATUS')];
		}
	});
	
	$scope.init = function() {
		$scope.priID = sysInfoService.getPriID();
		console.log('$scope.roleID :' + $scope.roleID );
		$scope.inputVO = {
				prj_code:'',
				prd_name:'',
				process:'',
				startDate_s:undefined,
				startDate_e:undefined,
				endDate_s:undefined,
				endDate_e:undefined,
				createDate_s:undefined,
				createDate_e:undefined,
				creator:''
		};
		//總行預設查自己建立資料
		if($scope.priID == '043' || $scope.priID == '044') $scope.inputVO.creator = sysInfoService.getUserID();
	};
	$scope.init();

    $scope.edit = function (row) {
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM372/CRM372_EDIT.html',
			className: 'CRM372',
			showClose: false,
            controller: ['$scope', function($scope) {
            	$scope.row = row;
            }]
		});
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.inquire();
			}
		});
	};
	
	$scope.review = function (row) {
		var dialog = ngDialog.open({
			template: 'assets/txn/CRM372/CRM372_REVIEW.html',
			className: 'CRM372',
			showClose: false,
            controller: ['$scope', function($scope) {
            	$scope.row = row;
            }]
		});
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.inquire();
			}
		});
	};
	
	$scope.delete = function (row, index) {
	      $confirm({
	        text: '您確認刪除此筆資料?'
	      }, {
	        size: 'sm'
	      }).then(function () {
	        $scope.sendRecv('CRM372', 'delete', 'com.systex.jbranch.app.server.fps.crm372.CRM372InputVO', {'prj_code' :row.PRJ_CODE}
	        , function (tota, isError) {
	          if (!isError) {
	            $scope.inquire()
	          }
	        });
	      });
	};
	
	$scope.inquire = function () {
		$scope.sendRecv("CRM372", "inquire", "com.systex.jbranch.app.server.fps.crm372.CRM372InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.resultList = tota[0].body.resultList;
        			$scope.outputVO = tota[0].body;	
				}
		});
	}
	
  }
);
