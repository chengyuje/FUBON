/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS200Controller', function ($scope, $controller, ngDialog, sysInfoService, $q, getParameter, $filter, fps200Service) {
  $controller('BaseController', {
    $scope: $scope
  });
  $scope.controllerName = 'FPS200Controller';

  /* parameter */
  var empID = sysInfoService.getUserID();
  var pathMap = {
    search: 'assets/txn/FPS/FPS_SEARCH.html'
  };
  $scope.arrowPath = {
    up: 'assets/images/ic-up.svg',
    down: 'assets/images/ic-down.svg'
  };
  $scope.arrowUp = true;
  $scope.validCust = false;
  var fps200 = fps200Service;
  $scope.fps200 = {
    // parameter
    custID: null,
    planID: null,
    custInfo: null,
    step: 5,
    prevStep: null,
    // function
    /**
     * step1 ~ step4 control
     * flag: int step num
     */
    chgStep: function (flag) {
      var subStepNum = flag;
      for (var i = 0; i < 4; i++) {
        this['isChangeStep' + (i + 1)] = subStepNum > i;
      }
    }
  };

  $scope.getDataSource = function () {
    $scope.getXML = ['KYC.EDUCATION', 'CUST.RISK_ATTR'];
    getParameter.XML($scope.getXML, function (totas) {
      if (len(totas) > 0) {
        var tmp = totas;
        $scope.kycEducation = tmp.data[tmp.key.indexOf('KYC.EDUCATION')];
        $scope.riskAttr = tmp.data[tmp.key.indexOf('CUST.RISK_ATTR')];
      }
    });

    $scope.YNList = [{
        DATA: 'Y',
        LABEL: '是'
      },
      {
        DATA: 'N',
        LABEL: '否'
      }
    ];
  };

  /* init */
  $scope.inquireInit = function () {
    $scope.custInfo = {};
  };

  $scope.init = function () {
    $scope.connector('set', 'STEP', undefined);
    $scope.connector('set', 'custID', undefined);
    $scope.connector('set', 'custInfo', undefined);
    $scope.connector('set', 'planID', undefined);
    $scope.inputVO = {
      custID: ''
    };
    $scope.inquireInit();
  };

  /* main funciton */
  var getValidEmpAuth = function () {
    var defer = $q.defer();
    defer.resolve(true);
    
    // #6437: 移除理規使用者權限設定
    /*
    $scope.sendRecv('FPS200', 'checkEmpFpsAuth', 'com.systex.jbranch.app.server.fps.fps200.FPS200InputVO', {
        empID: empID
      },
      function (tota, isError) {
        if (!isError) {
          if (tota[0].body) {
            defer.resolve(true);
          } else {
            defer.reject(false);
          }
          return true;
        }
        defer.reject(false);
        return false;
      }
    );*/
    return defer.promise;
  };

  /**
   * noDataReason:
   * cust Detail error find reason
   */
  var noDataReason = function (inputVO) {
    $scope.sendRecv('CRM110', 'inquireCust', 'com.systex.jbranch.app.server.fps.crm110.CRM110InputVO',
      inputVO,
      function (tota, isError) {
        if (!isError) {
          var result = tota[0].body.resultList;
          var list =
            result.length > 0 ? [result[0].BRA_NBR, result[0].BRANCH_NAME, ''] : [];
          if (
            result.length === 0 ||
            (result.length === 1 && !result[0].BRA_NBR)
          ) {
            //查無資料
            $scope.showErrorMsg('ehl_01_cus130_002', [inputVO.cust_id]); //無此客戶ID：{0}
            return false;
          } else if (result.length === 1 && !result[0].EMP_NAME) {
            //空code客戶
            $scope.showErrorMsg('ehl_01_cus130_006', list); //客戶歸屬：{0}-{1}，不提供客戶首頁查詢。
            return false;
          } else {
            //有歸屬行&所屬理專
            list.push(result[0].EMP_NAME);
            $scope.showErrorMsg('ehl_01_cus130_005', list); //客戶歸屬( {0} {1} ) {2} 理專，不提供客戶首頁查詢。
            return false;
          }
        }
      }
    );
    return false;
  };

  /**
   * custDetail:
   * cust Detail
   */
  var custDetail = function (custID) {
    var defer = $q.defer();
    var inputVO = {
      type: 'ID',
      cust_id: custID
    };
    var role = sysInfoService.getPriID();
    if (Array.isArray(role) && role.length === 1) {
      role = role[0];
    }
    var ao_code = String(sysInfoService.getAoCode());
    var FA = ['014', '015', '023', '024'];
    //消金
    if (role === '004') {
      inputVO.role = 'ps';
    } else if (FA.indexOf(role) >= 0) {
      //輔銷FA
      inputVO.role = 'faia';
    }
    //理專 : $scope.role == '001' => 是AFC，AFC 應可從快查KEY ID或姓名來查詢同歸屬行內的任一客戶(#0002070)
    else if (ao_code && $scope.role !== '001') {
      inputVO.role = 'ao';
      inputVO.ao_code = ao_code;
    } else {
      inputVO.role = 'other';
    }

    $scope.sendRecv(
      'CRM110',
      'inquire',
      'com.systex.jbranch.app.server.fps.crm110.CRM110InputVO',
      inputVO,
      function (tota, isError) {
        if (!isError) {
          var result = tota[0].body.resultList;
          if (result.length === 0) {
            // 查無資料時(查詢為何查無資料)
            defer.reject(noDataReason(inputVO));
          } else {
            defer.resolve({
              result: result,
              inputVO: inputVO,
              body: tota[0].body
            });
          }
          return true;
        }
        defer.reject(tota);
        return false;
      }
    );
    return defer.promise;
  };

  $scope.getCust = function () {
    //CUST_ID轉大寫
    $scope.inputVO.custID = ($scope.inputVO.custID || '').toUpperCase();
    if ($scope.inputVO.custID === '' || $scope.inputVO.custID == undefined) {
      $scope.showMsg('請輸入客戶ID！');
      return;
    }

    if ($scope.inputVO.custID === $scope.connector('get', 'custID'))
      return false;
    $scope.inquireInit();
    // 檢查是否在轄下
    custDetail($scope.inputVO.custID).then(
      function () {
        // 撈客戶資料
        getCust();
      },
      function (err) {}
    );
  };

  var getCust = function () {
    var defer = $q.defer();
    $scope.sendRecv(
      'FPS200',
      'getCust',
      'com.systex.jbranch.app.server.fps.fps200.FPS200InputVO',
      $scope.inputVO,
      function (tota, isError) {
        if (!isError) {
          if (tota[0].body.custInfo.length > 0) {
            $scope.validCust = true;
            $scope.custInfo = tota[0].body.custInfo[0];
            $scope.hasIns = tota[0].body.hasInvest;
            $scope.custInfo.AGE = getAge($scope.custInfo.BDAY_D);
            var today = $filter('date')(new Date(), 'yyyy-MM-dd 00:00:00'); //取當日日期
            var kycInvalid = !$scope.custInfo.KYC_LEVEL ||
              $scope.toJsDate($scope.custInfo.KYC_DUE_DATE) <
              $scope.toJsDate(today);
            if (kycInvalid)
              $scope.showErrorMsg('客戶無有效KYC，無法進行理財規劃！');
            $scope.connector('set', 'kycInvalid', kycInvalid);
            $scope.connector('set', 'hasIns', $scope.hasIns);
            $scope.connector('set', 'custID', $scope.inputVO.custID);
            $scope.connector('set', 'custInfo', $scope.custInfo);
            $scope.connector('set', 'STEP', 'STEP5');
            defer.resolve(true);
          } else {
            $scope.showMsg('ehl_01_common_009');
            defer.reject(true);
          }
          defer.reject(false);
        }
      }
    );
    return defer.promise;
  };

  $scope.go = function (where) {
    switch (where) {
      case 'search':
        openDialog(pathMap[where], null, where);
        break;
      case 'history':
        $scope.connector('set', 'STEP', 'STEP5');
        break;
      case 'custInfo':
        custDetail($scope.connector('get', 'custID')).then(
          function (data) {
            if (data.result.length === 1) {
              //單筆資料
              var path = '';
              $scope.connector('set', 'CRM110_CUST_ID', data.result[0].CUST_ID);
              $scope.connector(
                'set',
                'CRM110_CUST_NAME',
                data.result[0].CUST_NAME
              );
              $scope.connector('set', 'CRM110_AOCODE', data.result[0].AO_CODE);
              $scope.connector('set', 'CRM_CUSTVO', {
                CUST_ID: data.result[0].CUST_ID,
                CUST_NAME: data.result[0].CUST_NAME
              });

              if (data.inputVO.role === '004') {
                //消金首頁
                path = 'assets/txn/CRM711/CRM711.html';
              } else {
                //一般客戶首頁
                path = 'assets/txn/CRM610/CRM610_MAIN.html';
              }
              $scope.connector('set', 'CRM610URL', path);

              ngDialog.open({
                template: 'assets/txn/CRM610/CRM610.html',
                className: 'CRM610',
                showClose: false
              });
              return true;
            } else if (data.result.length > 1) {
              // 多筆資料
              ngDialog.open({
                template: 'assets/txn/CRM110/CRM110_MultiData.html',
                className: 'CRM110_MultiData',
                showClose: false,
                controller: [
                  '$scope',
                  function ($scope) {
                    $scope.row = data.result;
                    $scope.totaBody = data.body;
                    $scope.role = data.inputVO.role;
                    $scope.cust_name = '';
                    $scope.ao_code = data.inputVO.ao_code;
                  }
                ]
              });
              return true;
            }
          },
          function (err) {}
        );
        break;
    }
  };

  /* sub function */
  var openDialog = function (path, data, type) {
    var scope = $scope;
    var dialog = ngDialog.open({
      template: path,
      className: 'FPS200',
      controller: [
        '$scope',
        function ($scope) {
          $scope.data = data;
        }
      ]
    });

    dialog.closePromise.then(function (data) {
      if (typeof data.value === 'object') {
        popCbFn[type](data.value);
      }
    });
  };

  var popCbFn = {
    search: function (data) {
      $scope.inputVO.custID = data.CUST_ID;
      $scope.getCust();
    }
  };

  /* helping function */
  //年齡計算
  var getAge = function (dateString) {
    console.log(dateString);
    var today = new Date();
    var birthDate = new Date(dateString);
    var age = today.getFullYear() - birthDate.getFullYear();
    var m = today.getMonth() - birthDate.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    return age;
  };

  /* watch */
  $scope.$watch(
    function () {
      return $scope.connector('get', 'STEP');
    },
    function (newValue, oldValue) {
      var step =
        newValue && newValue.toString().match(/\d{1}/) ?
        newValue.toString().match(/\d{1}/) :
        '5';
      $scope.fps200.step = step;
      $scope.fps200.prevStep = oldValue;
      $scope.FPS200_module =
        'assets/txn/FPS2' + step + '0/FPS2' + step + '0.html';
      $scope.fps200.chgStep(step);
      $scope.goTop();
    }
  );

  /* main progress */
  $scope.goTop = fps200.setGoTop(document.getElementById('headingPanel'), 30);
  getValidEmpAuth().then(
    function () {
      $scope.getDataSource();
      $scope.init();

      // 從crm121
      if ($scope.connector('get', 'CRM121_CUST_PLAN')) {
        var crm121 = $scope.connector('get', 'CRM121_CUST_PLAN');
        $scope.inputVO.custID = crm121.custID;
        $scope.getCust();
      }
    },
    function () {
      $scope.showErrorMsg('無此模組權限，請洽系統管理員');
      $scope.GeneratePage({
        txnName: 'HOME',
        txnId: 'HOME',
        txnPath: []
      });
    }
  );
  console.log('FPS200');
});
