/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSProdETFController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPSProdETFController";



    /* parameter */
    $scope.arrowUp = true;
    $scope.mapping = {};
    $scope.inputVO = {};
    $scope.paramList = [];
    // $scope.selection = []; 這個在主畫面
    var param = function () {
      var defer = $q.defer();
      var flag = 0;
      var checkFlag = function () {
        if (flag++ === 2) {
          defer.resolve(true);
        } else {
          flag += 1;
        }
      };
      
      getParameter.XML(['PRD.ETF_CUSTOMER_LEVEL'], function (totas) {
          if (totas) {
            $scope.ETF_CUSTOMER_LEVEL = totas.data[totas.key.indexOf('PRD.ETF_CUSTOMER_LEVEL')];
            done();
          } else {
            deferred.reject(false);
          }
        });
      getParameter.XML(['PRD.ETF_PROJECT'], function (totas) {
          if (totas) {
            $scope.ETF_PROJECT = totas.data[totas.key.indexOf('PRD.ETF_PROJECT')];
            done();
          } else {
            deferred.reject(false);
          }
        });

      $scope.sendRecv("PRD120", "getCombo", "com.systex.jbranch.app.server.fps.prd120.PRD120InputVO", {},
        function (tota, isError) {
          if (!isError) {
            $scope.countryList = tota[0].body.countryList;
            $scope.tacticsList = tota[0].body.tacticsList;
            $scope.investList = tota[0].body.investList;
            $scope.companyList = tota[0].body.companyList;
            $scope.stockList = tota[0].body.stockList;
            $scope.industryList = tota[0].body.industryList;
            checkFlag();
            return;
          }
        });
      $scope.sendRecv("PRD110", "getCompany", "com.systex.jbranch.app.server.fps.prd110.PRD110InputVO", {},
        function (tota, isError) {
          if (!isError) {
            $scope.TRUST_COM = [];
            angular.forEach(tota[0].body.resultList, function (row) {
              $scope.TRUST_COM.push({
                LABEL: row.FUND_COMPANY_NAME,
                DATA: row.FUND_COMPANY_ID
              });
            });
          }
          checkFlag();
        });
      return defer.promise;
    };

    /* init */
    $scope.init = function () {
      $scope.inputVO.type = 'ETF';
      $scope.inputVO.isRanked = 'Y';
      $scope.inputVO.riskType = $scope.riskType;
      $scope.inputVO.OBU = $scope.OBU;
      $scope.inputVO.isPro = $scope.isPro;
      $scope.inputVO.etfID = '';
      $scope.inputVO.etfName = '';
      $scope.inputVO.currency = '';
      $scope.inputVO.riskLev = '';
      $scope.inputVO.company = '';
      $scope.inputVO.country = '';
      $scope.inputVO.tactics = '';
      $scope.inputVO.invType = '';
      $scope.inputVO.stockBondType = $scope.type === 'fixed' ? 'B':'S';
      $scope.inputVO.etf_project = '';
      $scope.inputVO.etf_customer_level = '';
      // $scope.prodList = [];
      $scope.outputVO = {};
      
      $scope.isWeb = !(typeof (webViewParamObj) !== 'undefined');
    };

    /* main function */
    $scope.inquire = function (isRanked) {
      $scope.inputVO.isRanked = isRanked;
      inquire()
        .then(function () {
          initList();
        });
    };

    // toggle checkbox
    $scope.selected = function (row) {
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
          row.name = row.PRD_ID + ' ' + row.ETF_CNAME;
          row.rRETURN_1Y = (Math.round(parseFloat(row.RETURN_1Y) * 100) || 0) + '%';
          row.rRETURN_3Y = (Math.round(parseFloat(row.RETURN_3Y) * 100) || 0) + '%';
          row.rRETURN_3M = (Math.round(parseFloat(row.RETURN_3M) * 100) || 0) + '%';
          row.rRETURN_6M = (Math.round(parseFloat(row.RETURN_6M) * 100) || 0) + '%';
          row.showInfo = showInfo;
          // for main
          row.CURRENCY_TYPE = row.CURRENCY_STD_ID;
          row.PTYPE = 'ETF';
          row.INV_PRD_TYPE = '3';
          row.INV_PERCENT = 0;
          row.RISK_TYPE = row.RISKCATE_ID;
          row.PRD_CNAME = row.ETF_CNAME;
          row.MAIN_PRD = row.PRD_RANK ? 'Y' : 'N';
          row.GEN_SUBS_MINI_AMT_FOR = row.TXN_AMOUNT || undefined;

          return row;
        });
    };

    var inquire = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPSProd', 'inquire', 'com.systex.jbranch.app.server.fps.fpsprod.FPSProdInputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.outputVO = tota[0].body;
            console.log($scope.outputVO);
            defer.resolve(tota);
            return true;
          }
          defer.reject(false);
        });
      return defer.promise;
    };

    /* watch */

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
          $scope.inputVO.etfID = $scope.query.prdID;
          $scope.inputVO.isRanked = undefined;
          inquire()
            .then(function () {
              initList(true);
              $scope.inputVO.etfID = '';
              $scope.query = undefined;
            });
        }
        // 選購時檢核有無重複的key
        $scope.setKeyOfType('PRD_ID');
      });
    
    $scope.openETFUrl = function(url) {
    	debugger
		var dialog = ngDialog.open({
			template: 'assets/txn/FPS/FPSProd_ETF_Dialog.html',
			className: 'FPSProd_ETF_Dialog',
			controller:['$scope',function($scope){
					$scope.url = url
			}]
		});
		dialog.closePromise.then(function(data){
			
		});
	}
    
  }
);
