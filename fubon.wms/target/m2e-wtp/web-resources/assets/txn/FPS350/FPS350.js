/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS350Controller',
  function ($scope, $controller, ngDialog, socketService, alerts, projInfoService, $q, getParameter, $filter, $confirm) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS350Controller';
    var log = console.log;

    /* parameter */
    var fps300 = $scope.fps300;
    $scope.kycDisable = fps300.custData.kycDisable;
    $scope.mapping = {};
    var planStatusMultiArr = [
      'PRINT_REJECT',
      'PRINT_THINK',
      'PRINT_ORDER'
    ];

    // getXML
    var param = function () {
      var deferred = $q.defer();

      getParameter.XML([
        'FPS.SPP_PLAN_STATUS',
        'FPS.VALID_FLAG',
        'FPS.YN',
      ], function (totas) {
        if (totas) {
          $scope.mapping.planStatus = totas.data[totas.key.indexOf('FPS.SPP_PLAN_STATUS')];
          $scope.mapping.validFlag = totas.data[totas.key.indexOf('FPS.VALID_FLAG')];
          $scope.mapping.fpsYN = totas.data[totas.key.indexOf('FPS.YN')];
          $scope.mapping.planning = fps300.mapping.planning;
          deferred.resolve('success');
        }
      });
      return deferred.promise;
    };

    // get function type
    $scope.functionID = projInfoService.getAuthorities().MODULEID.WMS.ITEMID.FPS350 ?
      projInfoService.getAuthorities().MODULEID.WMS.ITEMID.FPS350.FUNCTIONID : {};
    $scope.isMaintenance = $scope.functionID.maintenance;
    /* main function */

    /* init */
    $scope.init = function () {
      $scope.paramList = [];
      $scope.inputVO = {
        custID: fps300.inputVO.custID,
        planStatus: '',
        SD: undefined,
        ED: undefined,
        planID: undefined,
        isDisable: undefined
      };
    };

    /* main function */
    $scope.inquire = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPS350', 'inquire', 'com.systex.jbranch.app.server.fps.fps350.FPS350InputVO',
        $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            $scope.outputVO = tota[0].body;
            $scope.paramList = tota[0].body.outputList;
            $scope.paramList.forEach(function (item) {
              item.LASTUPDATE = dateConvert(item.LASTUPDATE, '-', '/');
              item.CREATETIME = dateConvert(item.CREATETIME, '-', '/');
              var vf = $filter('mapping')(item.VALID_FLAG ? item.VALID_FLAG : 'Y', $scope.mapping.validFlag) || '';
              var vfArr = vf.split('-');
              item.VALID_FLAG_TEXT = vfArr.length > 0 ? vfArr[0] : '';
              item.VALID_FLAG_REASON = vfArr.length > 1 ? vfArr[1] : '';
            });
            defer.resolve(tota[0].body);
            return true;
          }
          $scope.showErrorMsg(tota);
          defer.reject(tota);
          return false;
        });
      return defer.promise;
    };

    var tag = function (num, args) {
      fps300.chgTab(num, args);
    };

    $scope.goFPS310 = function () {
      tag('1', '350');
      fps300.setPlanDate("");
      fps300.setOverDate("");
    };

    $scope.go = function (row) {
      fps300.setPlanID(row.PLAN_ID);
      fps300.setRISK_ATTR(row.RISK_ATTR);
      fps300.setVOL_RISK_ATTR(row.VOL_RISK_ATTR);
      if (row.PLAN_STATUS === 'PLAN_STEP') {
        // tag('FPS324', getSppTypeName(row.SPP_TYPE));
        tag('FPS324', row.SPP_TYPE);
      } else if (row.PLAN_STATUS === 'ACTIVE') {
        tag('2');
      } else if (row.PLAN_STATUS.split('_')[0] === 'RE') {
        // tag('FPS410', getSppTypeName(row.SPP_TYPE));
        tag('FPS410', row.SPP_TYPE);
      } else if (planStatusMultiArr.indexOf(row.PLAN_STATUS) >= 0) {
        // tag('FPS340', getSppTypeName(row.SPP_TYPE));
        $scope.connector('set', 'FPS324TOFPS340', "Y");
        tag('FPS324', row.SPP_TYPE);
      }
    };

    $scope.delete = function (row, index) {
      $confirm({
        text: '刪除確認?'
      }, {
        size: 'sm'
      }).then(function () {
        // 4
        $scope.sendRecv('FPS350', 'deletePlan', 'com.systex.jbranch.app.server.fps.fps350.FPS350InputVO', {
          planID: row.PLAN_ID
        }, function (tota, isError) {
          if (!isError) {
            $scope.inquire()
              .then(function () {
                if ($scope.paramList.length <= 0) {
                  $scope.goFPS310();
                }
              });
            return true;
          }
          return false;
        });
      }, function () {
        // four
      });
    };

    /* helping function */
    var dateConvert = function (date, b, c, time) {
      date = date.toString();
      date = time ? date : date.split(' ')[0];
      return date.replace(new RegExp(b, 'g'), c);
    };

    $scope.open = function ($event, dataPicker, parent) {
      $event.preventDefault();
      $event.stopPropagation();
      if (parent === undefined) {
        $scope[dataPicker] = true;
      } else {
        parent[dataPicker] = true;
      }
    };

    var getSppTypeName = function (type) {
      var name = '';
      switch (type) {
        case '1':
          name = 'EDUCATION';
          break;
        case '2':
          name = 'RETIRE';
          break;
        case '3':
          name = 'BUY_HOUSE';
          break;
        case '4':
          name = 'BUY_CAR';
          break;
        case '5':
          name = 'MARRY';
          break;
        case '6':
          name = 'OV_EDU';
          break;
        case '7':
          name = 'TRAVEL';
          break;
        case '8':
          name = 'OTHER';
          break;
      }
      return name;
    };

    // 開啟轉寄規劃書小視窗
    $scope.goDetail = function (row) {
      var dialog = ngDialog.open({
        template: 'assets/txn/FPS350/FPS350_DETAIL.html',
        className: 'FPS350_DETAIL',
        showClose: true,
        controller: ['$scope', function ($scope) {
          $scope.plan = row;
        }]
      });
      dialog.closePromise.then(function (data) {
        if (data.value === 'cancel') {}
      });
    };

    // date picker
    // 活動起迄日
    $scope.sDateOptions = {};
    $scope.eDateOptions = {};
    $scope.limitDate = function () {
      $scope.sDateOptions.maxDate = $scope.inputVO.ED;
      $scope.eDateOptions.minDate = $scope.inputVO.SD;
    };
    // date picker end
    $scope.nowDate = new Date();
    $scope.nowDate.setHours(0, 0, 0, 0);

    /* main process */
    $scope.init();
    if (projInfoService.fromFPS814 != undefined) {
      projInfoService.fromFPS814 = undefined;
      //        	$scope.inquire();
    }

    param().then(function (success) {
      $scope.inquire();
    });

  }
);
