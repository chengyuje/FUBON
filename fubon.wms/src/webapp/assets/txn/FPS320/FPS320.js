'use strict';
eSoafApp.controller('FPS320Controller',
  function ($scope, $controller, ngDialog, $q, $filter, $confirm) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS320Controller';

    /* parameter */
    var fps300 = $scope.fps300;
    var helpTemp = {};
    var targetTemp = 0;
    $scope.helpFlag = false;
    var isCreate = true;
    $scope.hasRemind = false;

    $scope.dragOptions = {
      label: {
        front: '低風險/',
        end: '高風險/',
      },
      style: {
        barCss: {
          background: 'linear-gradient(to right, rgba(88, 221, 102, 1) 0%, rgba(252, 197, 47, 1) 49%, rgba(242, 2, 6, 1) 99%)'
        }
      },
      showTip: false,
      showLabel: true,
      showBtn: true,
    };
    /* init */
    $scope.init = function () {
      $scope.inputVO = angular.copy($scope.recommendations) || {};
      isCreate = $scope.recommendations.PLANNAME ? false : true;
      $scope.inputVO.PLANNAME = $scope.inputVO.PLANNAME ? $scope.inputVO.PLANNAME : '';
      $scope.inputVO.PLANHEAD = $scope.inputVO.PLANHEAD ? $scope.inputVO.PLANHEAD : '';
      $scope.inputVO.ONETIME = $scope.inputVO.ONETIME ? $scope.inputVO.ONETIME : '';
      $scope.inputVO.PERMONTH = $scope.inputVO.PERMONTH ? $scope.inputVO.PERMONTH : '';
      $scope.inputVO.TARGET = $scope.inputVO.TARGET ? $scope.inputVO.TARGET : '';
      $scope.inputVO.isChange = 'N';
      targetTemp = $scope.inputVO.TARGET;
      $scope.inputVO.CUST_RISK_ATTR = $scope.inputVO.CUST_RISK_ATTR_NAME ? $scope.inputVO.CUST_RISK_ATTR_NAME.substring(3, 4) : ($scope.custRiskAttrNumber * 2).toString();
      //用來判斷是否鎖住投組名稱
      // from FPS411
      $scope.inputVO.beacon = $scope.beacon;
      $scope.inputVO.isReb = $scope.isReb;
      $scope.inputVO.sppTypeDelete = 'N';
      $scope.sendRecv('FPS320', 'inquire', 'com.systex.jbranch.app.server.fps.fps320.FPS320InputVO', {
          planId: fps300.planID
        },
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.outputMap.checkHelp) {
              helpTemp = tota[0].body.outputMap.checkHelpValue;
              if (helpTemp.UNIVERSITY || helpTemp.MASTER || helpTemp.PHD) {
                $scope.helpFlag = true;
              } else {
                $scope.helpFlag = false;
              }
            }
            return true;
          }
        });

    };

    /* main function */
    $scope.confirm = function () {
      $scope.inputVO.isChange = 'N';
      if (helpTemp) {
        helpTemp.sppTypeDelete = $scope.inputVO.sppTypeDelete;
        $scope.inputVO.fromFPS321 = helpTemp;
      }

      // check
      if (parseInt($scope.inputVO.PLANHEAD) < 1) {
        $scope.showErrorMsg('投資天期不可以輸入0');
        return false;
      }
      if (parseInt($scope.inputVO.PLANHEAD) > 30) {
        $scope.showErrorMsg('最高投資天期為30年');
        return false;
      }

      if ($scope.fps320Form.$invalid || $scope.inputVO.CUST_RISK_ATTR == '') {
        $scope.showErrorMsg('1~5欄位為必要輸入欄位，請輸入後重試');
        return false;
      }

      $scope.inputVO.ONETIME = !$scope.inputVO.ONETIME ? 0 : $scope.inputVO.ONETIME;
      $scope.inputVO.PERMONTH = !$scope.inputVO.PERMONTH ? 0 : $scope.inputVO.PERMONTH;
      if ($scope.inputVO.ONETIME + $scope.inputVO.PERMONTH < 1) {
        $scope.showErrorMsg('期初投資/每月投資不可以同時為空白或輸入0');
        $scope.inputVO.ONETIME = '';
        $scope.inputVO.PERMONTH = '';
        return false;
      }

      $scope.toDay = $filter('date')(new Date(), 'yyyy-MM-dd 00:00:00'); //取當日日期
      $scope.overDateTmp = $scope.overDate.substring(0, 10).replace(/[/]+/g, '-') + ' 00:00:00'
      if ($scope.toJsDate($scope.overDateTmp) < $scope.toJsDate($scope.toDay)) {
        $scope.showErrorMsg('預計到期日期小於今日');
        return false;
      }

      if (!checktarget()) {
        return false;
      }
      $scope.inputVO.ONETIME = $scope.inputVO.ONETIME == 0 ? '' : $scope.inputVO.ONETIME;
      $scope.inputVO.PERMONTH = $scope.inputVO.PERMONTH == 0 ? '' : $scope.inputVO.PERMONTH;
      if ($scope.inputVO.beacon == 1) {
        var worString = '調整投資方式之後，現有投資組合建議資料將會被刪除並重新帶入商品配置建議。';
        if (!isCreate) {
          if ($scope.recommendations.ONETIME > 0) {
            if (!$scope.inputVO.ONETIME) {
              $scope.inputVO.isChange = 'Y';
            }
          } else {
            if ($scope.inputVO.ONETIME) {
              $scope.inputVO.isChange = 'Y';
            }
          }
          // isRebalance 不會帶入新商品
          if ($scope.inputVO.CUST_RISK_ATTR != $scope.recommendations.CUST_RISK_ATTR_NAME.substring(3, 4) && !$scope.isReb) {
            worString = '調整風險承受度之後，現有投資組合建議資料將會被刪除並重新帶入商品配置建議。';
            $scope.inputVO.isChange = 'Y';
          }
        }
        if ($scope.inputVO.isChange === 'Y') {
          $confirm({
            title: '警告',
            size: 'sm',
            text: worString
          }).then(function () {
            $scope.closeThisDialog($scope.inputVO);
          });
        } else {
          $scope.closeThisDialog($scope.inputVO);
        }
      } else {
        $scope.checkName()
          .then(function (check) {
            if (check.toString() === '0') {
              $scope.closeThisDialog($scope.inputVO);
            } else {
              $scope.showErrorMsg('命名不可重複');
            }
          }, function (e) {
            $scope.showErrorMsg(e);
          });
      }
    };

    /* sub function */
    // check if has same name
    $scope.checkName = function () {
      var deferred = $q.defer();
      $scope.sendRecv('FPS324', 'checkName', 'com.systex.jbranch.app.server.fps.fps324.FPS324InputVO', {
          planName: $scope.inputVO.PLANNAME,
          custId: fps300.inputVO.custID,
          planID: fps300.planID
        },
        function (tota, isError) {
          if (!isError) {
            deferred.resolve(tota[0].body);
            return true;
          } else {
            deferred.reject(tota);
          }
        });
      return deferred.promise;
    };

    $scope.chgHelp = function () {
      $scope.inputVO.TARGET = $scope.inputVO.TARGET ? Math.floor($scope.inputVO.TARGET) : '0';
      if (parseInt($scope.inputVO.TARGET) < 1) {
        $scope.showMsg('達成目標金額不可以輸入0');
        $scope.inputVO.TARGET = '';
        return;
      }
      if (targetTemp == $scope.inputVO.TARGET) {
        return;
      }
      $confirm({
        text: '修改"目標所需資金"欄位資料時，原HELP中明細資料將被刪除',
        title: '警告',
        size: 'sm'
      }).then(function () {
        $scope.inputVO.sppTypeDelete = 'Y';
        targetTemp = $scope.inputVO.TARGET;
        helpTemp = {};
        $scope.helpFlag = false;
      }, function () {
        $scope.inputVO.TARGET = targetTemp;
        $scope.helpFlag = true;
        return false;
      });
    };

    $scope.chkTarget = function () {
      $scope.inputVO.TARGET = $scope.inputVO.TARGET ? Math.floor($scope.inputVO.TARGET) : '0';
      if (parseInt($scope.inputVO.TARGET) < 1) {
        $scope.showMsg('達成目標金額不可以輸入0');
        $scope.inputVO.TARGET = '';
      }
    };

    /* helping function */
    $scope.goFPS321 = function () {
      //FPS321 HELP
      var url = $scope.inputVO.planningCode === 'EDUCATION' ? 'FPS321' :
        $scope.inputVO.planningCode === 'RETIRE' ? 'FPS322' : '';

      var dialog = ngDialog.open({
        template: 'assets/txn/' + url + '/' + url + '.html',
        className: url,
        showClose: false,
        controller: ['$scope', function ($scope) {
          $scope.custID = fps300.inputVO.custID;
          $scope.planID = fps300.planID;
          $scope.fromFPS320 = helpTemp;
        }]
      });

      dialog.closePromise.then(function (data) {
        if (data.value != 'cancel' && data.value) {
          if (parseInt(data.value.total) < 1) {
            $scope.showMsg('達成目標金額不可以輸入0');
            $scope.inputVO.TARGET = '';
            return;
          }
          $scope.inputVO.sppTypeDelete = 'N';
          $scope.inputVO.TARGET = data.value.total;
          targetTemp = $scope.inputVO.TARGET;
          helpTemp = {};
          if (data.value.total != 0) {
            $scope.helpFlag = true;
            helpTemp = data.value;
          }
          if (data.value.isDelete) {
            $scope.inputVO.sppTypeDelete = 'Y';
            $scope.helpFlag = false;
          }
          checktarget();
        }
      });
    };

    $scope.estimateAmt = function () {
      return ((parseInt($scope.inputVO.PERMONTH) * parseInt($scope.inputVO.PLANHEAD)) * 12 || 0) + (parseInt($scope.inputVO.ONETIME) || 0);
    };

    //取前一日
    $scope.getPreDay = function (s) {
      var y = parseInt(s.substring(0, 4), 10);
      var m = parseInt(s.substring(5, 7), 10) - 1;
      var d = parseInt(s.substring(8, 10), 10);
      var dt = new Date(y, m, d - 1);
      y = dt.getFullYear();
      m = dt.getMonth() + 1;
      d = dt.getDate();
      m = m >= 10 ? m : '0' + m;
      d = d >= 10 ? d : '0' + d;
      return y + '/' + m + '/' + d;
    };

    $scope.chgPlanhead = function () {
      if (parseInt($scope.inputVO.PLANHEAD) < 1) {
        $scope.showMsg('投資天期不可以輸入0');
        $scope.inputVO.PLANHEAD = '';
      }
      if (parseInt($scope.inputVO.PLANHEAD) > 30) {
        $scope.showMsg('最高投資天期為30年');
        $scope.inputVO.PLANHEAD = '';
      }
      //預計到期日期
      if ($scope.inputVO.PLANHEAD == undefined || $scope.inputVO.PLANHEAD == '') {
        $scope.overDate = $scope.planDate;
      } else {
        $scope.overDate = $scope.getPreDay((parseInt($scope.planDate.substring(0, 4)) + parseInt($scope.inputVO.PLANHEAD)) + $scope.planDate.substring(4, 10));
      }

      $scope.chgMomey();
    };

    var checktarget = function () {
      if (parseInt($scope.inputVO.TARGET) < (parseInt($scope.inputVO.ONETIME == '' ? '0' : $scope.inputVO.ONETIME) +
          parseInt($scope.inputVO.PERMONTH == '' ? '0' : $scope.inputVO.PERMONTH) * parseInt($scope.inputVO.PLANHEAD) * 12)) {
        $scope.showErrorMsg('設定之目標金額需大於期初投資加每月投資金額，請重新輸入。');
        return false;
      }
      return true;
    };

    $scope.chgMomey = function () {
      $scope.inputVO.PLANHEAD = $scope.inputVO.PLANHEAD ? Math.floor($scope.inputVO.PLANHEAD) : '';
      $scope.inputVO.ONETIME = $scope.inputVO.ONETIME ? Math.floor($scope.inputVO.ONETIME) : '0';
      $scope.inputVO.PERMONTH = $scope.inputVO.PERMONTH ? Math.floor($scope.inputVO.PERMONTH) : '0';
    }

    /* main process */
    $scope.init();
  }
);
