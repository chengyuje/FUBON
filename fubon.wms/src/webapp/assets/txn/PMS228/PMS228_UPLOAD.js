/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS228_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('PMS228Controller', {$scope: $scope});
		$scope.controllerName = "PMS228_UPLOADController";

		 /**upload csv files**/
		$scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.fileName = name;
        		$scope.confirm = true;
        	}else
        		$scope.confirm = false;
        };
        
        /** CSV 檔寫入資料庫 **/
        $scope.save = function(){   
        	if($scope.inputVO.reportDate == ''){				
				$scope.showErrorMsg('報表年月欄位輸入後，才能上傳檔案！');
				return;		
			}
        	$scope.sendRecv("PMS228", "insertCSVFile", "com.systex.jbranch.app.server.fps.pms228.PMS228InputVO", $scope.inputVO,
        			function(totas, isError) {
        		if (isError) {             		        			
        			$scope.showErrorMsg(totas[0].body.msgData);
        		}        		
        		if (totas.length > 0) {
        			$scope.showSuccessMsg('ehl_01_common_004'); 
        			$scope.closeThisDialog('successful');
        		};        		      		
        	});
        };
                	
});
