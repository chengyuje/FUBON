'use strict';
eSoafApp.controller('CRM311_UPLOADController',function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "CRM311_UPLOADController";


    /**download sample files**/
    $scope.downloadSample = function() {
          $scope.sendRecv("CRM311", "downloadSample", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
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
            $scope.sendRecv("CRM311", "insertCSVFile", "com.systex.jbranch.app.server.fps.crm311.CRM311InputVO", $scope.inputVO,
                function(tota, isError) {
                	if (!isError) {
                		$scope.showSuccessMsg("匯入完成，匯入筆數為：" + tota[0].body.count);
                		$scope.closeThisDialog('successful');
                	};
                });
          };
    


});
