/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS407_UPLOADController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS407_UPLOADController";

		$scope.init = function(){
			$scope.inputVO = {
				rptYearMon: $scope.ym
        	};
			$scope.rptYM = '';
			$scope.confirm = false;
        };
        $scope.init();
        
        $scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];
		}
		$scope.inquireInit();
        
        /*** 查詢訊息資料 ***/
		$scope.query = function(){
			$scope.rptYM = $scope.inputVO.rptYearMon;
			
			$scope.sendRecv("PMS407", "queryMsg", "com.systex.jbranch.app.server.fps.pms407.PMS407InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.msgList.length == 0) {
								$scope.paramList = [];								
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
        	
			if(name){
        		$scope.inputVO.fileName = name;
        		$scope.confirm = true;
        	}else
        		$scope.confirm = false;
        };
        
        /** CSV 檔寫入資料庫 **/
        $scope.save = function(){        
        	if($scope.inputVO.rptYearMon == ''){				
				$scope.showErrorMsg('報表年月欄位輸入後，才能上傳檔案！');
				return;		
			}
          	
        	
        	
        	$scope.sendRecv("PMS407", "insertCSVFile", "com.systex.jbranch.app.server.fps.pms407.PMS407InputVO", $scope.inputVO,
        			function(totas, isError) {
        		
        	
        		  
        		if(totas[0].body.msgList==2)
        			$scope.showErrorMsg('訊息內容過長大於150字(含空格)');
        			
        		else if (isError) {             		
        			$scope.query();
        			$scope.showErrorMsg(totas[0].body.msgData);
        		}else if(totas[0].body.msgList==1) 
        		{
        			$scope.showErrorMsg('訊息類別必須小於4');
        			
        		}else if (totas[0].body.msgList==0) {
        			$scope.showSuccessMsg('ehl_01_common_004');
        			$scope.query();
//        			$scope.closeThisDialog('successful');
        		}        		      		
        	});
        };
                	
});
