/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPSSOTController',
  function ($rootScope, $scope, $controller) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPSSOTController';

    /* parameter */
    $scope.path = '';
    $scope.arrowPath = {
      up: 'assets/images/ic-up.svg',
      down: 'assets/images/ic-down.svg'
    };
    var custID = $scope.cust_id;
    var rateMap = $scope.data.rateMap;

    $scope.mapping = $scope.data.mapping;
    $scope.riskType = $scope.data.riskType;
    $scope.hasIns = $scope.data.hasIns;

    // old

    // new
    $scope.fromFPS = true;
    var where = $scope.data.where;
    var transact = $scope.data.transact;
    $scope.FPSData = {
      custID: $scope.custID,
      planID: $scope.planID,
      prdID: transact.PRD_ID,
      invType: transact.INV_TYPE || transact.InvType,
      cmbCur: transact.CmbCur || transact.buyCur,
      trustCurr: transact.TRUST_CURR || transact.CURRENCY_TYPE,
      PURCHASE_ORG_AMT: transact.PURCHASE_ORG_AMT,
      PURCHASE_TWD_AMT: transact.PURCHASE_TWD_AMT,
    };
    $scope.SOTurl = '';

    // $scope.planID 從前帶來
    // $scope.custID 從前帶來

    /* init */
    $scope.init = function () {
      $scope.SOTurl = 'assets/txn/' + where + '/' + where + '.html';
    };

    /* main function */

    $scope.setSOTurl = function(url){
    	$scope.SOTurl = url;
    }
    /* helping function */

    /* watch */

    /* main progress */
    console.log('FPSSOTController');
    $scope.init();
  }
);
