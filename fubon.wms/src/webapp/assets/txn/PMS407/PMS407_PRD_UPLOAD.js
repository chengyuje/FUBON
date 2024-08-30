'use strict';
eSoafApp.controller('PMS407_PRD_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS407_PRD_UPLOADController";

		getParameter.XML(["PMS.PRD_TYPE", "PMS.INFO_TYPE"], function(totas) {
			if (totas) {
				$scope.PRD_TYPE = totas.data[totas.key.indexOf('PMS.PRD_TYPE')];
				$scope.INFO_TYPE = totas.data[totas.key.indexOf('PMS.INFO_TYPE')];
			}
		});
		
		// date picker
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		//
		
		$scope.init = function() {
			$scope.inputVO = {
				ptype: $scope.connector('get','PMS407_TYPE_PRD')
			};
        };
        $scope.init();
        
        $scope.inquireInit = function() {
			$scope.paramList = [];
			$scope.outputVO = {};
		}
		$scope.inquireInit();
        
        /*** 查詢訊息資料 ***/
		$scope.query = function() {
			if($scope.connector('get','PMS407_TYPE_PRD')!=undefined){
				$scope.inputVO.ptype=$scope.connector('get','PMS407_TYPE_PRD');
			}
			
			$scope.sendRecv("PMS407", "queryMsg", "com.systex.jbranch.app.server.fps.pms407.PMS407InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.msgList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.msgList;							
							$scope.outputVO = tota[0].body;
							return;
						}						
			});
		};
		
		/**download sample files**/
		$scope.downloadSample = function() {			
        	$scope.sendRecv("PMS407", "downloadSample", "com.systex.jbranch.app.server.fps.pms407.PMS407InputVO", {},
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
		
		 /**upload csv files**/
		$scope.uploadFinshed = function(name, rname) {
			$scope.inputVO.fileName = name;
        };
        
        /** CSV 檔寫入資料庫 **/
        $scope.save = function() {
        	if(!$scope.inputVO.rptYearMon) {
				$scope.showErrorMsg('報表年月欄位輸入後，才能上傳檔案！');
				return;
			}
        	$scope.sendRecv("PMS407", "insertCSVFile", "com.systex.jbranch.app.server.fps.pms407.PMS407InputVO", $scope.inputVO,
    				function(tota, isError) {
    					if (!isError) {
    						if (tota[0].body.msgList==0)
    							$scope.showSuccessMsg('ehl_01_common_004');	
    						else if(tota[0].body.msgList==1)
    							$scope.showErrorMsg('訊息類別必須小於4');
    						else if(tota[0].body.msgList==2)
            					$scope.showErrorMsg('訊息內容過長大於150字(含空格)');
    						$scope.inquireInit();
    						$scope.query();
    					}
    		});
        };
                	
});