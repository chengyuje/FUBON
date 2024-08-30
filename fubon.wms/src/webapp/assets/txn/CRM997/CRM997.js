/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM997Controller',
  function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService ,getParameter) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "CRM997Controller";

    //#2061 潛力體驗等級參數化
    getParameter.XML(["CRM.POTENTIAL_LEVEL", "CRM.EXPERIENCE_LEVEL"], function(totas) {
	    if (totas) {
			//潛力等級
   			$scope.mappingSet['POTENTIAL_LEVEL'] = totas.data[totas.key.indexOf('CRM.POTENTIAL_LEVEL')];
   			//體驗等級
            $scope.mappingSet['EXPERIENCE_LEVEL'] = totas.data[totas.key.indexOf('CRM.EXPERIENCE_LEVEL')];
	    }
    });
    
    //datepicker
    $scope.model = {};
    $scope.open = function($event, elementOpened) {
      $event.preventDefault();
      $event.stopPropagation();
      $scope.model[elementOpened] = !$scope.model[elementOpened];
    };
    $scope.limitDate = function() {
      $scope.experience_sDateOptions.maxDate = $scope.inputVO.experience_end_date|| $scope.maxDate;
      $scope.experience_eDateOptions.minDate = $scope.inputVO.experience_begin_date || $scope.minDate;
    };


    $scope.init = function(){
      $scope.inputVO = {};
    };
    $scope.init();
    $scope.inquireInit = function(){
      $scope.paramList = [];
    }
    $scope.inquireInit();

    //資料查詢
    $scope.inquire = function() {
      $scope.sendRecv("CRM997", "inquire", "com.systex.jbranch.app.server.fps.crm997.CRM997InputVO", $scope.inputVO,
        function(tota, isError) {
          if (!isError) {
            if(tota[0].body.resultList.length == 0) {
              $scope.paramList = [];
              $scope.totalData = [];
              $scope.showMsg("ehl_01_common_009");
                    return;
                  }
            $scope.totalData = tota[0].body.resultList;
            $scope.outputVO = tota[0].body;
            return;
          }
      });
    };

    /**資料整批匯入**/
         $scope.upload = function (row) {
          var dialog = ngDialog.open({
            template: 'assets/txn/CRM997/CRM997_UPLOAD.html',
            className: 'CRM997_UPLOAD',
            controller: ['$scope', function($scope) {
            }]
          });
         };

});
