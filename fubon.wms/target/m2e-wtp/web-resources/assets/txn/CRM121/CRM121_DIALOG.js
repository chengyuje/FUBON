'use strict';
eSoafApp.controller('CRM121_DIALOGController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
    $controller('BaseController', {
      $scope: $scope
    });
    $controller('PPAPController', {
      $scope: $scope
    });
    $scope.controllerName = "CRM121_DIALOGController";

    // combobox
    getParameter.XML(["FPS.DEPOSIT_CUR"], function (totas) {
      if (totas) {
        $scope.DEPOSIT_CUR = totas.data[totas.key.indexOf('FPS.DEPOSIT_CUR')];
        $scope.DEPOSIT_CUR.push({
          'LABEL': '台幣',
          'DATA': 'TWD'
        });
      }
    });
    // ------------------------------------待理財規劃客戶-----------------------------------
    $scope.queryVO = {};
    $scope.reQuire = function () {
      $scope.sendRecv("CRM121", "fortuna", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", $scope.queryVO,
        function (tota, isError) {
          if (!isError) {
            $scope.FORTUNA_CUST_LIST = tota[0].body.resultList;
          };
        }
      );
    };
    // old code from crm210
    $scope.goCRM610 = function (row) {
      $scope.connector('set', 'CRM_CUSTVO', {
        'CUST_ID': row.CUST_ID,
        'CUST_NAME': row.CUST_NAME
      });
      $scope.connector("set", "CRM610URL", "assets/txn/CRM610/CRM610_MAIN.html");

      var dialog = ngDialog.open({
        template: 'assets/txn/CRM610/CRM610.html',
        className: 'CRM610',
        showClose: false
      });
    };
    // ------------------------------------待執行交易客戶-----------------------------------
    $scope.sendRecv("CRM121", "fortunaGetfunc", "com.systex.jbranch.app.server.fps.crm121.CRM121InputVO", {},
      function (tota, isError) {
        if (!isError) {
          $scope.FORTUNA_FUNC_LIST = refreshList(tota[0].body.resultList);
        };
      }
    );

    function refreshList(list) {
      var ans = [];
      for (var i = 0; i < list.length; i++) {
        generateList(ans, list[i], 1);
      }
      ans = _.orderBy(ans, ['DAYS']);
      angular.forEach(ans, function (row) {
        row.FORTUNA_TOTAL_COUNT = 1;
        angular.forEach(row.SUBITEM, function (row2) {
          row.FORTUNA_TOTAL_COUNT += row2.SUBITEM.length + 1;
        });
        angular.forEach(row.SUBITEM2, function (row2) {
          row.FORTUNA_TOTAL_COUNT += row2.SUBITEM.length + 1;
        });
      });
      return ans;
    };

    function generateList(ansRow, row, circle) {
      var obj = {},
        exist = false;
      // 客戶ID
      if (circle == 1) {
        for (var i = 0; i < ansRow.length; i++) {
          if (row["CUST_ID"] == ansRow[i]["CUST_ID"]) {
            exist = true;
            break;
          }
        }
        if (!exist) {
          obj["CUST_ID"] = row["CUST_ID"];
          obj["CUST_NAME"] = row["CUST_NAME"];
          obj["CIRCLE"] = circle;
          obj["DAYS"] = row["DAYS"];
          obj["SUBITEM"] = [];
          obj["SUBITEM2"] = [];
          ansRow.push(obj);
          if (row["TITLE"] == "全資產規劃")
            generateList(obj["SUBITEM"], row, circle + 1);
          else
            generateList(obj["SUBITEM2"], row, circle + 1);
          return;
        }
        var old = ansRow.slice(-1).pop();
        if (row["TITLE"] == "全資產規劃")
          generateList(old["SUBITEM"], row, circle + 1);
        else
          generateList(old["SUBITEM2"], row, circle + 1);
      }
      // TITLE
      else if (circle == 2) {
        for (var i = 0; i < ansRow.length; i++) {
          if (row["TITLE"] == ansRow[i]["TITLE"]) {
            exist = true;
            break;
          }
        }
        if (!exist) {
          obj["TITLE"] = row["TITLE"];
          obj["CIRCLE"] = circle;
          obj["SUBITEM"] = [];
          ansRow.push(obj);
          generateList(obj["SUBITEM"], row, circle + 1);
          return;
        }
        var old = ansRow.slice(-1).pop();
        generateList(old["SUBITEM"], row, circle + 1);
      }
      // 內層
      else {
        obj["PLAN_ID"] = row["PLAN_ID"];
        obj["CREATOR"] = row["CREATOR"];
        obj["CUST_ID"] = row["CUST_ID"];
        obj["CUST_NAME"] = row["CUST_NAME"];
        obj["TITLE"] = row["TITLE"];
        obj["PRD_ID"] = row["PRD_ID"];
        obj["PRD_NAME"] = row["PRD_NAME"];
        obj["TXN_TYPE"] = row["TXN_TYPE"];
        obj["TRUST_CURR"] = row["TRUST_CURR"];
        obj["PURCHASE_ORG_AMT"] = row["PURCHASE_ORG_AMT"];
        obj["DAYS"] = row["DAYS"];
        obj["CIRCLE"] = circle;
        ansRow.push(obj);
        return;
      }
    };

    // old code from crm210
    $scope.goCRM610 = function (row) {
      $scope.connector('set', 'CRM_CUSTVO', {
        'CUST_ID': row.CUST_ID,
        'CUST_NAME': row.CUST_NAME
      });
      $scope.connector("set", "CRM610URL", "assets/txn/CRM610/CRM610_MAIN.html");

      var dialog = ngDialog.open({
        template: 'assets/txn/CRM610/CRM610.html',
        className: 'CRM610',
        showClose: false
      });
    };

    $scope.goFPS200 = function (row) {
      $scope.connector('set', 'CRM121_CUST_PLAN', {
        'custID': row.CUST_ID,
        'planID': row.PLAN_ID
      });
      $rootScope.menuItemInfo.url = "assets/txn/FPS200/FPS200.html";
      $scope.closeThisDialog('successful');
    };

    $scope.goFPS300 = function (row) {
        $scope.connector('set', 'CRM121_CUST_PLAN', {'custID': row.CUST_ID});
        $rootScope.menuItemInfo.url = "assets/txn/FPS300/FPS300.html";
        $scope.closeThisDialog('successful');
      };
  });
