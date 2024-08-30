/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS132Controller',
  function ($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, $q, validateService, getParameter) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "INS132Controller";

    // 計算值
    getParameter.XML(["INS.FAMILY_P_GAP", 'INS.FAMILY_HR_GAP', 'INS.FAMILY_D_GAP'], function (totas) {
      if (totas) {
        $scope.mappingSet['INS.FAMILY_P_GAP'] = totas.data[0]; // 意外
        $scope.mappingSet['INS.FAMILY_HR_GAP'] = totas.data[1]; // 住院醫療
        $scope.mappingSet['INS.FAMILY_D_GAP'] = totas.data[2]; // 重大疾病
      }
    });

    $scope.inti = function () {
      $scope.callError = false;
      $scope.inputVO = {
        custID: $scope.connector('get', 'insCustID'), // 客戶ID
        custName: $scope.connector('get', 'insCustName'), // 客戶姓名
        custBirth: new Date($scope.connector('get', 'insCustBirthday')), // 客戶生日
        custGender: $scope.connector('get', 'insCustGender'), // 客戶性別
      }
      $scope.connector('set', "INS_PARGE", undefined);
      $scope.connector('set', 'insCustID', undefined); // 客戶ID
      $scope.connector('set', 'insCustName', undefined); // 客戶姓名
      $scope.connector('set', 'insCustBirthday', undefined); // 客戶生日
      $scope.connector('set', 'insCustGender', undefined); // 客戶性別
      // 調後端
      debugger
      $scope.sendRecv("INS132", "queryData", "com.systex.jbranch.app.server.fps.ins132.INS132InputVO", $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            debugger
            if (tota[0].body.errorMsg != null) {
              $scope.callError = true;
              $scope.showErrorMsg(tota[0].body.errorMsg);
              return;
            } else {
              $scope.callError = false;
              // 列表
              $scope.inputVO.lstCashFlow = tota[0].body[1].lstCashFlow;
              $scope.lstCashFlow = tota[0].body[1].lstCashFlow;
              $scope.inputVO.AGE = tota[0].body[0]; // 保險年齡
              // 下方四個區塊
              var tmpGap = tota[0].body[1].insFamilyGap ? parseInt(tota[0].body[1].insFamilyGap) * 10000 : 0;
              $scope.inputVO.numInsFamilyGap = tmpGap;
              $scope.inputVO.numInsItemAccident = (tmpGap + parseInt($scope.mappingSet['INS.FAMILY_P_GAP'][0].DATA));
              $scope.inputVO.numInsItemHealth = parseInt($scope.mappingSet['INS.FAMILY_HR_GAP'][0].DATA);
              
              $scope.inputVO.insFamilyGap = parseInt(tota[0].body[1].insFamilyGap).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '萬'; // 最大費用不足額(萬元)
              if ((tmpGap + parseInt($scope.mappingSet['INS.FAMILY_P_GAP'][0].DATA)) > 10000) {
                $scope.inputVO.insItemAccident = parseInt((tmpGap + parseInt($scope.mappingSet['INS.FAMILY_P_GAP'][0].DATA)) / 10000, 10).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '萬'; // 意外
              } else {
                $scope.inputVO.insItemAccident = (tmpGap + parseInt($scope.mappingSet['INS.FAMILY_P_GAP'][0].DATA)).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,'); // 意外
              }
              if (parseInt($scope.mappingSet['INS.FAMILY_HR_GAP'][0].DATA) > 10000) {
                $scope.inputVO.insItemHealth = parseInt($scope.mappingSet['INS.FAMILY_HR_GAP'][0].DATA / 10000, 10).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,'); // 住院醫療
              } else {
                $scope.inputVO.insItemHealth = ($scope.mappingSet['INS.FAMILY_HR_GAP'][0].DATA).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,'); // 住院醫療
              }
              if(parseInt($scope.mappingSet['INS.FAMILY_D_GAP'][0].DATA) > 10000){
				$scope.inputVO.insDreadGap = parseInt($scope.mappingSet['INS.FAMILY_D_GAP'][0].DATA / 10000,10) + '萬';// 重大疾病
			  }else{
				$scope.inputVO.insDreadGap = ($scope.mappingSet['INS.FAMILY_D_GAP'][0].DATA).replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');// 重大疾病
			  }
              
              if (tota[0].body[1].oldItemLife > 10000) {
                $scope.inputVO.oldItemLife = parseInt(tota[0].body[1].oldItemLife / 10000, 10).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '萬'; // 壽險
              } else {
                $scope.inputVO.oldItemLife = tota[0].body[1].oldItemLife.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,'); // 壽險
              }
              if (tota[0].body[1].oldItemAccident > 10000) {
                $scope.inputVO.oldItemAccident = parseInt(tota[0].body[1].oldItemAccident / 10000, 10).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '萬'; // 意外險
              } else {
                $scope.inputVO.oldItemAccident = tota[0].body[1].oldItemAccident.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,'); // 意外險
              }
              debugger
              if (tota[0].body[1].oldItemHealth > 10000) {
                $scope.inputVO.oldItemHealth = parseInt(tota[0].body[1].oldItemHealth / 10000, 10).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '萬'; // 住院醫療
              } else {
                $scope.inputVO.oldItemHealth = tota[0].body[1].oldItemHealth.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,'); // 住院醫療
              }
              if (tota[0].body[1].oldItemDread > 10000) {
                $scope.inputVO.oldItemDread = parseInt(tota[0].body[1].oldItemDread / 10000, 10).toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '萬'; // 重大疾病
              } else {
                $scope.inputVO.oldItemDread = tota[0].body[1].oldItemDread.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,'); // 重大疾病
              }
            }
          } else {
            $scope.callError = true;
          }
        });
    }


    //回保單健診首頁
    $scope.toHealthCare = function () {
      $scope.connector('set', "INS_PARGE", "INS100");
      $scope.connector('set', "INS100_VIEW_DATA_BACK", $scope.connector('get', "INS100_VIEW_DATA"));
      $scope.connector('set', "INS_PARGE_BACK_INS100", "INS132-100");
      $rootScope.menuItemInfo.url = "assets/txn/INS100/INS100.html";
    }

    //列印保單健診及家庭財務安全報告書
    $scope.toHealthAndFamilyFin = function () {
      $scope.CUST_VO = {
        CUST_ID: $scope.inputVO.custID,
        CUST_NAME: $scope.inputVO.custName,
        BIRTH_DATE: $scope.inputVO.custBirth,
        GENDER: $scope.inputVO.custGender,
      }
      $scope.connector('set', "INS_PARGE", "INS140"); // 頁面跳轉
      $scope.connector('set', "TO_INS140", $scope.CUST_VO); // 資料連結進INS140
      $rootScope.menuItemInfo.url = "assets/txn/INS100/INS100.html";
    }

    //轉跳保險規劃入口
    $scope.To_INS200 = function (num) {
      // ins200 old code use
      $scope.connector('set', 'INS200_CUST_ID', $scope.inputVO.custID);
      $scope.connector('set', 'INS200_CUST_DATA', {
        'AGE': $scope.inputVO.AGE,
        'GENDER': $scope.inputVO.custGender
      });
      $scope.connector('set', "INS132_INPUT_VO", $scope.inputVO);
      $scope.connector('set', "loader_active", num);
      $rootScope.menuItemInfo.url = "assets/txn/INS200/INS200_Loader.html";
    };

    $scope.inti();

  });
