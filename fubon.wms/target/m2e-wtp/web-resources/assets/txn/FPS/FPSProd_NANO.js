/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSProdNANOController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSProdNANOController';
    
    // 讀取參數檔
    getParameter.XML(["FPS.PROD_RISK_LEVEL", "FPS.CURRENCY","FPS.STOCK_BOND_TYPE","FPS.PROD_INV_LEVEL"], function(totas) {
		if (totas) {
			debugger;
			$scope.PROD_RISK_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_RISK_LEVEL')];
			$scope.CURRENCY = totas.data[totas.key.indexOf('FPS.CURRENCY')];
			$scope.STOCK_BOND_TYPE = totas.data[totas.key.indexOf('FPS.STOCK_BOND_TYPE')];
			$scope.PROD_INV_LEVEL = totas.data[totas.key.indexOf('FPS.PROD_INV_LEVEL')];
		}
	});
	

    /* parameter */
    $scope.arrowUp = true;
    $scope.mapping = {};
    $scope.inputVO = {};
    $scope.paramList = [];
   
    var param = function () {
      var deferred = $q.defer();
      var cnt = 2;
      var done = function () {
        cnt -= 1;
        if (cnt === 0) {
          deferred.resolve();
        }
      };

      return deferred.promise;
    };

    /* init */
    $scope.init = function () {
      $scope.inputVO.type = 'NANO';
      $scope.inputVO.riskType = $scope.riskType;
      $scope.inputVO.isPro = $scope.isPro;
      $scope.inputVO.prd_id = '';
      $scope.inputVO.prd_name = '';
      $scope.inputVO.currency = '';
      $scope.inputVO.risk_level = '';
      $scope.inputVO.stock_bond_type = $scope.type ?  ($scope.type === 'fixed' ? 'B':'S') : null;
      $scope.inputVO.inv_level= '';
      $scope.inputVO.type = '2';   //依可申購商品查詢
      $scope.outputVO = {};
    };

    /* main function */
    $scope.inquire = function () {
      inquire()
        .then(function () {
          initList(true);
        });
    };

    var inquire = function () {
      var defer = $q.defer();
      $scope.sendRecv("PRD190", "inquire", "com.systex.jbranch.app.server.fps.prd190.PRD190InputVO", $scope.inputVO,
       function (tota, isError) {
          if (!isError) {
        	$scope.prodList = tota[0].body.resultList;
			$scope.outputVO = tota[0].body;
            defer.resolve(tota);
            return true;
          }
          defer.reject(false);
        });
      return defer.promise;
    };

    var openDialog = function (path, data) {
      if (!path) return false;
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.row = angular.copy(data);
          $scope.cust_id = scope.cust_id;
        }]
      });
    };
    
    $scope.getName = function() {
		var deferred = $q.defer();
		if($scope.inputVO.prd_id) {
			$scope.inputVO.prd_id = $scope.inputVO.prd_id.toUpperCase();
			$scope.sendRecv("PRD300", "checkID", "com.systex.jbranch.app.server.fps.prd300.PRD300InputVO", {'prd_id':$scope.inputVO.prd_id},
					function(tota, isError) {
						if (!isError) {
							if (tota[0].body.prd_name) {
								$scope.inputVO.prd_name = tota[0].body.prd_name;
							}
							deferred.resolve();
						}
			});
		} else
			deferred.resolve();
		return deferred.promise;
	};
	
    // toggle checkbox
    $scope.selected = function (row) {
//      debugger;
      if (row.selected) {
        row.selectedIndex = $scope.selection.length;
        $scope.selection.push(row);
        // 6.25 更改畫面
        $scope.addCart();
      } else if (row.selectedIndex !== undefined && row.selectedIndex >= 0) {
        $scope.selection.splice(row.selectedIndex, 1);
        row.selectedIndex = -1;
      }
    };

    /* sub function */
    var initList = function (showInfo) {
      $scope.prodList = angular.copy($scope.outputVO.resultList)
        .map(function (row) {
//          debugger;
          row.name = row.PRD_ID + ' ' + row.PRD_NAME;
          // row.showInfo = $scope.isMore;
          row.showInfo = showInfo;
          // for main
          row.INV_PRD_TYPE = '3';
          row.INV_PRD_TYPE_2 = row.INV_LEVEL;
          row.CURRENCY_TYPE = row.CURRENCY_STD_ID;
          row.INV_PERCENT = 0;
          row.PTYPE = 'NANO';
          row.RISK_TYPE = row.RISKCATE_ID;
          row.PRD_CNAME = row.PRD_NAME;
          row.MAIN_PRD = 'Y';
          return row;
        }) || [];
      console.log($scope.prodList);
    };

    /* emit */
    $scope.$on('FPS200ProdAddCart', function () {
      // initList();
      $scope.paramList.map(function (row) {
        row.selected = undefined;
      });
    });

    /* main progress */
    $scope.init();
    $scope.inquire();
    
    // 選購時檢核有無重複的key
    $scope.setKeyOfType('PRD_ID');
  }
);
