/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSProdMFDController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSProdMFDController';
    
    // 讀取參數檔
	getParameter.XML(["FPS.FUND_TYPE"],function(tota){
//		debugger
		if(tota){
			$scope.FUND_TYPE = tota.data[tota.key.indexOf('FPS.FUND_TYPE')];
			$scope.SHOW_FUND_TYPE = [];
		    
		    if($scope.type == 'fixed') {
		    	$scope.SHOW_FUND_TYPE = $scope.FUND_TYPE.filter(function(item, index, array){
				  return item.DATA != '01';  // 債券不顯示股票
				});
		    } else if($scope.type == 'stock') {
		    	$scope.SHOW_FUND_TYPE = $scope.FUND_TYPE.filter(function(item, index, array){
		  		  return item.DATA != '02' && item.DATA != '04';  // 股票不顯示債券/貨幣
		  		});
		    } else {
		    	$scope.SHOW_FUND_TYPE = $scope.FUND_TYPE;
		    }
		}
	});
	

    /* parameter */
    $scope.arrowUp = true;
    $scope.mapping = {};
    $scope.inputVO = {};
    $scope.paramList = [];
    var detailPath = './assets/txn/PRD110/PRD110_DETAIL.html';
    // $scope.selection = []; 這個在主畫面

    var param = function () {
      var deferred = $q.defer();
      var cnt = 2;
      var done = function () {
        cnt -= 1;
        if (cnt === 0) {
          deferred.resolve();
        }
      };

      getParameter.XML(['FPS.DIVIDEND_FREQUENCY'], function (totas) {
        if (totas) {
          $scope.mapping.freq = totas.data[totas.key.indexOf('FPS.DIVIDEND_FREQUENCY')];
          done();
        } else {
          deferred.reject(false);
        }
      });
      getParameter.XML(['PRD.FUND_SUBJECT'], function (totas) {
          if (totas) {
            $scope.FUND_SUBJECT = totas.data[totas.key.indexOf('PRD.FUND_SUBJECT')];
            done();
          } else {
            deferred.reject(false);
          }
        });
      getParameter.XML(['PRD.FUND_PROJECT'], function (totas) {
          if (totas) {
            $scope.FUND_PROJECT = totas.data[totas.key.indexOf('PRD.FUND_PROJECT')];
            done();
          } else {
            deferred.reject(false);
          }
        });
      getParameter.XML(['PRD.FUND_CUSTOMER_LEVEL'], function (totas) {
          if (totas) {
            $scope.FUND_CUSTOMER_LEVEL = totas.data[totas.key.indexOf('PRD.FUND_CUSTOMER_LEVEL')];
            done();
          } else {
            deferred.reject(false);
          }
        });

      $scope.sendRecv('PRD110', 'getCompany', 'com.systex.jbranch.app.server.fps.prd110.PRD110InputVO', {},
        function (tota, isError) {
          if (!isError) {
            $scope.TRUST_COM = [];
            angular.forEach(tota[0].body.resultList, function (row) {
              $scope.TRUST_COM.push({
                LABEL: row.FUND_COMPANY_NAME,
                DATA: row.FUND_COMPANY_ID
              });
            });
            done();
          } else {
            deferred.reject(false);
          }
        });
      return deferred.promise;
    };

    /* init */
    $scope.init = function () {
      $scope.inputVO.type = 'MFD';
      $scope.inputVO.isRanked = 'Y';
      $scope.inputVO.riskType = $scope.riskType;
      $scope.inputVO.OBU = $scope.OBU;
      $scope.inputVO.isPro = $scope.isPro;
      $scope.inputVO.fund_id = '';
      $scope.inputVO.fund_name = '';
      $scope.inputVO.currency = '';
      $scope.inputVO.dividend_type = '';
      $scope.inputVO.dividend_fre = '';
      $scope.inputVO.fund_type = '';
      $scope.inputVO.inv_area = '';
      $scope.inputVO.inv_target = '';
      $scope.inputVO.trust_com = '';
      $scope.inputVO.riskLev = '';
      $scope.inputVO.fund_subject = '';
      $scope.inputVO.fund_project = '';
      $scope.inputVO.fund_customer_level = '';
      debugger
      $scope.inputVO.stockBondType = $scope.type ?  ($scope.type === 'fixed' ? 'B':'S') : null;
      // $scope.prodList = [];
      $scope.outputVO = {};
    };

    /* main function */
    $scope.inquire = function (isRanked) {
      $scope.inputVO.isRanked = isRanked;
      inquire()
        .then(function () {
          initList();
        });
    };

    var inquire = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPSProd', 'inquire', 'com.systex.jbranch.app.server.fps.fpsprod.FPSProdInputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.outputVO = tota[0].body;
            defer.resolve(tota);
            return true;
          }
          defer.reject(false);
        });
      return defer.promise;
    };

    $scope.go = function (where, data) {
      switch (where) {
        case 'detail':
          openDialog(detailPath, data);
          break;
      }
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

    $scope.getName = function () {
      var deferred = $q.defer();
      if ($scope.inputVO.fund_id) {
        $scope.sendRecv('PRD110', 'getFundName', 'com.systex.jbranch.app.server.fps.prd110.PRD110InputVO', {
            'fund_id': $scope.inputVO.fund_id
          },
          function (tota, isError) {
            if (!isError) {
              if (tota[0].body.fund_name) {
                $scope.inputVO.fund_name = tota[0].body.fund_name;
              } else {
                $scope.inputVO.fund_name = '';
              }
              deferred.resolve();
            }
          });
      } else
        deferred.resolve();
      return deferred.promise;
    };

    $scope.getArea = function () {
      if ($scope.inputVO.fund_type) {
        $scope.inputVO.inv_target = '';
        $scope.mapping.INV_TARGET = [];
        $scope.sendRecv('PRD110', 'getArea', 'com.systex.jbranch.app.server.fps.prd110.PRD110InputVO', $scope.inputVO,
          function (tota, isError) {
            if (!isError) {
              $scope.mapping.INV_AREA = [];
              angular.forEach(tota[0].body.resultList, function (row) {
                $scope.mapping.INV_AREA.push({
                  LABEL: row.PARAM_NAME,
                  DATA: row.NEXT_TIER_VALUE
                });
              });
              $scope.inputVO.inv_area = '';
              return;
            }
          });
      } else {
        $scope.inputVO.inv_area = '';
        $scope.inputVO.inv_target = '';
        $scope.mapping.INV_AREA = [];
        $scope.mapping.INV_TARGET = [];
      }
    };

    $scope.getTarget = function () {
      if ($scope.inputVO.inv_area) {
        $scope.sendRecv('PRD110', 'getTarget', 'com.systex.jbranch.app.server.fps.prd110.PRD110InputVO', $scope.inputVO,
          function (tota, isError) {
            if (!isError) {
              $scope.mapping.INV_TARGET = [];
              angular.forEach(tota[0].body.resultList, function (row) {
                $scope.mapping.INV_TARGET.push({
                  LABEL: row.PARAM_NAME,
                  DATA: row.NEXT_TIER_VALUE
                });
              });
              $scope.inputVO.inv_target = '';
              return;
            }
          });
      } else {
        $scope.inputVO.inv_target = '';
        $scope.mapping.INV_TARGET = [];
      }
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
      $scope.prodList = angular.copy($scope.outputVO.prodList)
        .map(function (row) {
          row.name = row.PRD_ID + ' ' + row.FUND_CNAME;
          // row.showInfo = $scope.isMore;
          row.showInfo = showInfo;
          // for main
          row.INV_PRD_TYPE = '3';
          row.CURRENCY_TYPE = row.CURRENCY_STD_ID;
          row.INV_PERCENT = 0;
          row.PTYPE = 'MFD';
          row.RISK_TYPE = row.RISKCATE_ID;
          row.PRD_CNAME = row.FUND_CNAME;
          row.MAIN_PRD = row.CROWN ? row.CROWN : 'N';
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
    param()
      .then(function () {
        if ($scope.query === null || $scope.query === undefined) {
          $scope.inquire('Y');
        } else {
          $scope.inputVO.fund_id = $scope.query.prdID;
          $scope.inputVO.isRanked = undefined;
          inquire()
            .then(function () {
              initList(true);
              $scope.inputVO.fund_id = '';
            });
        }
      });

    // 選購時檢核有無重複的key
    $scope.setKeyOfType('PRD_ID');
  }
);
