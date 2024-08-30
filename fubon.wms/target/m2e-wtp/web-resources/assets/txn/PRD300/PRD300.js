/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('PRD300Controller',
  function ($scope, $controller, ngDialog, socketService, alerts, projInfoService, $q, getParameter, $filter, $confirm) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'PRD300Controller';
    
    //get xml parameter
    $scope.param = function () {
        var defer = $q.defer();
        getParameter.XML([
          'FPS.STOCK_BOND_TYPE',
          'PRD.CORE_TYPE',
          'FPS.CURRENCY',
          'INS.IS_SALE',
          'FPS.PROD_INV_LEVEL'
        ], function (totas) {
          if (totas) {
            $scope.STOCK_BOND_TYPE = totas.data[totas.key.indexOf('FPS.STOCK_BOND_TYPE')];
            $scope.CORE_TYPE = totas.data[totas.key.indexOf('PRD.CORE_TYPE')];
            $scope.CURRENCY = totas.data[totas.key.indexOf('FPS.CURRENCY')];
            $scope.IS_SALE = totas.data[totas.key.indexOf('INS.IS_SALE')];
            $scope.PROD_INV_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_INV_LEVEL')];
            defer.resolve(true);
          }
        });
        return defer.promise;
     };
          
    $scope.inquireInit = function(){
		$scope.paramList = [];
		$scope.outputVO = {};
	}
	$scope.inquireInit();
	
	$scope.init = function() {
		debugger;
		$scope.param().then(
			function(){$scope.inquire();}
		);
	}
	$scope.init();
	
    // inquire
	$scope.inquire = function() {
		$scope.sendRecv("PRD300", "inquire", "com.systex.jbranch.app.server.fps.prd300.PRD300InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					$scope.paramList = tota[0].body.resultList;
					$scope.outputVO = tota[0].body;
				}
		});
	};
	
    $scope.edit = function (row) {
		var dialog = ngDialog.open({
			template: 'assets/txn/PRD300/PRD300_EDIT.html',
			className: 'PRD300',
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
	
	$scope.delete = function (row, index) {
	      $confirm({
	        text: '您確認刪除此筆資料?'
	      }, {
	        size: 'sm'
	      }).then(function () {
	        // 4
	    	$scope.inputVO.prd_id = row.PRD_ID;
	        $scope.sendRecv('PRD300', 'delete', 'com.systex.jbranch.app.server.fps.prd300.PRD300InputVO', $scope.inputVO
	        , function (tota, isError) {
	          if (!isError) {
	            $scope.inquire()
	          }
	        });
	      });
	};
 }
);