'use strict';
eSoafApp.controller('CRM997_UPLOADController',function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "CRM997_UPLOADController";
    
    $scope.inputVO.setting_type = '2';


    /**download sample files**/
    $scope.downloadSample = function() {
          $scope.sendRecv("CRM997", "downloadSample", "com.systex.jbranch.app.server.fps.crm997.CRM997InputVO", $scope.inputVO,
        function(tota, isError) {
          if (!isError) {
            return;
          }
      });
    };

    /**upload csv files**/
    $scope.uploadFinshed = function(name, rname) {
          if(name){
            $scope.inputVO.fileName = name;
            $scope.confirm = true;
          }else
            $scope.confirm = false;
        };

    /** CSV 檔寫入資料庫 **/
        $scope.checkCSVFile = function(){
          $scope.sendRecv("CRM997", "insertCSVFile", "com.systex.jbranch.app.server.fps.crm997.CRM997InputVO", $scope.inputVO,
              function(tota, isError) {
                if (isError) {
                  $scope.showErrorMsg(totas[0].body.msgData);
                }
                if (tota.length > 0) {
                  $scope.showSuccessMsg('ehl_01_common_004');
                  $scope.closeThisDialog('successful');
                };
              });
        };


});
