/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS250Controller',
  function ($scope, $controller, ngDialog, socketService, alerts, projInfoService, $q, getParameter, $filter, $confirm) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS250Controller';

    /* parameter */
    var crm121 = ''; // from CRM121 Dialog with connector CRM121_CUST_PLAN
    $scope.mapping = {};
    var suggestPct = [];

    // getXML
    var param = function () {
      var defer = $q.defer();
      getParameter.XML([
        'FPS.INV_PLAN_STATUS',
        'FPS.VALID_FLAG',
        'FPS.YN',
        'COMMON.YES_NO',
      ], function (totas) {
        if (totas) {
          $scope.mapping.validFlag = totas.data[totas.key.indexOf('FPS.VALID_FLAG')];
          $scope.mapping.planStatus = totas.data[totas.key.indexOf('FPS.INV_PLAN_STATUS')];
          $scope.mapping.fpsYN = totas.data[totas.key.indexOf('FPS.YN')];
          $scope.mapping.commonYN = totas.data[totas.key.indexOf('COMMON.YES_NO')];
          defer.resolve(true);
        }
      });
      return defer.promise;
    };
    
    // getPct
    var getPct = function () {
        var deferred = $q.defer();
        $scope.sendRecv('FPS210', 'getSuggestPct', 'java.util.Map', {custID:$scope.connector('get', 'custID'),hasIns:$scope.hasIns,riskType:$scope.connector('get', 'custInfo').KYC_LEVEL},
          function (tota, isError) {
            if (!isError) {
              suggestPct.push(tota[0].body[0]);
              suggestPct.push(tota[0].body[1]);
              
              if($scope.hasIns) {
            	  suggestPct.push(tota[0].body[2]);
                  suggestPct.push(tota[0].body[3]);
              }
              deferred.resolve('success');
              return true;
            }
            deferred.reject(tota);
            return false;
          });
        return deferred.promise;
    };


    // get function type
    $scope.functionID = projInfoService.getAuthorities().MODULEID.WMS.ITEMID.FPS250 ?
      projInfoService.getAuthorities().MODULEID.WMS.ITEMID.FPS250.FUNCTIONID : {};
    $scope.isMaintenance = $scope.functionID.maintenance;
    /* main function */

    /* init */
    $scope.init = function () {
      $scope.paramList = [];
      $scope.inputVO = {
        custID: $scope.inputVO.custID,
        planStatus: '',
        SD: undefined,
        ED: undefined,
        planID: undefined,
        isDisable: undefined
      };
      $scope.connector('set', 'planID', null);
      $scope.connector('set', 'STEP1VO', null);
      $scope.connector('set', 'STEP2VO', null);
    };

    /* main function */
    $scope.inquire = function (planID) {
      var defer = $q.defer();
      var inputVO = angular.copy($scope.inputVO);
      inputVO.planID = planID;
      console.log(inputVO.isDisable);
      $scope.sendRecv('FPS250', 'inquire', 'com.systex.jbranch.app.server.fps.fps250.FPS250InputVO',
        inputVO,
        function (tota, isError) {
          if (!isError) {
            if (!planID) {
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
            }
            defer.resolve(tota[0].body);
            return true;
          }
          $scope.showErrorMsg(tota);
          defer.reject(tota);
          return false;
        });
      return defer.promise;
    };

    $scope.go = function (where, data) {
      switch (where) {
        case 'plan':
          if (!!data && data.VALID_FLAG === 'Y') {
            $scope.connector('set', 'planID', data.PLAN_ID);
            setPlanParam(data)
              .then(function (step) {
                if (crm121) {
                  $scope.connector('set', 'custID', crm121.custID);
                  $scope.connector('set', 'plan', crm121.planID);
                  crm121 = '';
                  $scope.connector('set', 'CRM121_CUST_PLAN', null);
                }
                $scope.connector('set', 'STEP', 'STEP' + step);
              });
          }
          break;
        case 'detail':
          detail(data);
          break;
        case 'download':
          if (data.IS_PRINT > 0)
            $scope.go('detail', data);
          break;
        case 'resend':
          if (data.IS_EMAIL > 0)
            $scope.go('detail', data);
          break;
        case 'new':
          $scope.connector('set', 'planID', undefined);
          $scope.connector('set', 'STEP', 'STEP1');
          break;
        case 'create':
          $confirm({
            text: '建立全新規劃將取代目前的規劃，是否同意?'
          }, {
            size: 'sm'
          }).then(function () {
            // 4
            $scope.go('new');
          }, function () {
            // four
          });
          break;
      }
    };

    $scope.delete = function (row) {
      $confirm({
        text: '刪除確認?'
      }, {
        size: 'sm'
      }).then(function () {
        // 4
        $scope.sendRecv('FPS250', 'deletePlan', 'com.systex.jbranch.app.server.fps.fps250.FPS250InputVO', {
          planID: row.PLAN_ID
        }, function (tota, isError) {
          if (!isError) {
            $scope.inquire()
              .then(function () {
                if ($scope.paramList.length <= 0) {
                  $scope.go('new');
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

    var setPlanParam = function (row) {
      var defer = $q.defer();
      $scope.inquire(row.PLAN_ID)
        .then(function (data) {
          var param = data.outputList[0];
          switch (param.PLAN_STEP) {
            case '3':
            case '4':
              $scope.connector('set', 'STEP2VO', {
        	    PORTFOLIO2_AMT_AFTER_ORIGIN: Number($scope.hasIns ? Math.round(suggestPct[2]) : (Math.round(param.PLAN_AMT*suggestPct[0]/100))||0),
                PORTFOLIO3_AMT_AFTER_ORIGIN: Number($scope.hasIns ? Math.round(suggestPct[3]) : (Math.round(param.PLAN_AMT*suggestPct[1]/100))||0),
                PORTFOLIO2_RATIO_AFTER_ORIGIN: Number(suggestPct[0]),
                PORTFOLIO3_RATIO_AFTER_ORIGIN: Number(suggestPct[1]),
                PORTFOLIO2_AMT_AFTER: Number(param.PORTFOLIO2_AMT),
                PORTFOLIO3_AMT_AFTER: Number(param.PORTFOLIO3_AMT),
                PORTFOLIO2_RATIO_AFTER: Number(param.PORTFOLIO2_RATIO),
                PORTFOLIO3_RATIO_AFTER: Number(param.PORTFOLIO3_RATIO)
              });
            case '2':
              var custInfo = $scope.connector('get', 'custInfo');
              $scope.connector('set', 'STEP1VO', {
                CUST_RISK: custInfo.KYC_LEVEL,
                PLAN_ID: param.PLAN_ID,
                PLAN_AMT: param.PLAN_AMT,
                CUST_ID: param.CUST_ID,
                SIGN_AGMT_YN: custInfo.SIGN_AGMT_YN || 'N',
                NEW_FLAG: 'N',
              });
            case '1':
              break;
          }
          defer.resolve(param.PLAN_STEP);
        });
      return defer.promise;
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

    // 開啟轉寄規劃書小視窗
    var detail = function (row) {
      var scope = $scope;
      var dialog = ngDialog.open({
        template: 'assets/txn/FPS250/FPS250Detail.html',
        className: 'FPS250Detail',
        showClose: true,
        controller: ['$scope', function ($scope) {
          $scope.plan = row;
          $scope.custID = scope.inputVO.custID;
          $scope.planID = row.PLAN_ID;
          $scope.helpingFn = {
            dateConvert: dateConvert
          };
        }]
      });
      dialog.closePromise.then(function (data) {
        if (data.value === 'cancel') {}
        $scope.inquire();
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

    /* watcher */
    $scope.$watch('connector(\'get\', \'custID\')',
      function (newValue, oldValue) {
        console.info('250 newValue---=' + newValue);
        if (newValue !== undefined) {
          $scope.inputVO.custID = newValue;
          $scope.init();
          $scope.inquire().then(function () {
            if ($scope.paramList.length <= 0) {
              $scope.go('new');
            } else {
               getPct();
            }
          });
        }
      });

    /* main process */
    $scope.init();
    console.log('fps250');

    param().then(function () {
        if ($scope.connector('get', 'CRM121_CUST_PLAN') && !$scope.connector('get', 'custInfo'))
          return;
        if ($scope.connector('get', 'custID')) {
          return $scope.inquire();
        } else {
          $scope.go('new');
        }
      }).then(function () {
        if (projInfoService.fromFPS814 !== undefined) {
          projInfoService.fromFPS814 = undefined;
        }
        if ($scope.paramList.length <= 0) {
          $scope.go('new');
        }
        // 從crm121
        if ($scope.connector('get', 'CRM121_CUST_PLAN')) {
          crm121 = $scope.connector('get', 'CRM121_CUST_PLAN');
          $scope.inputVO.custID = crm121.custID;

          if (crm121.planID && $scope.connector('get', 'custInfo')) {
            $scope.go('plan', $scope.paramList.find(function (row) {
              return row.PLAN_ID === crm121.planID;
            }));
            return;
          }
        }
      });
  }
);
