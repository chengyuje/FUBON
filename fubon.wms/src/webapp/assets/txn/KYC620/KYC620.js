/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC620Controller',
		function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC620Controller";
		$controller('PMSRegionController', {$scope: $scope});

		/**初始化**/
		$scope.init = function(){	
			$scope.inputVO = {
					CUST_ID : '' ,
					CUST_NAME     : '' ,
					CREATE_DATE   : undefined,
					branch_nbr:'',			//分行
        	};
			//清除時，連同查詢結果一起清除
			$scope.isClear = false;

	    	//設定回傳時間
			$scope.inputVO.reportDate = new Date();
	    	//可是範圍  觸發 
	    	$scope.RegionController_getORG($scope.inputVO);
		};
		$scope.init();
		
		$scope.clear = function(){
			$scope.init();
			$scope.outputVO = {};
			$scope.data = {};
			$scope.printList = [];
		}
        
		//英文字母轉大寫
		$scope.text_toUppercase = function(text,type){
			var toUppercase_text = text.toUpperCase();
			switch (type) {
			case 'CUST_ID':
				$scope.inputVO.CUST_ID = toUppercase_text;
				break;
			default:
				break;
			}
		}
		
    	$scope.altInputFormats = ['M!/d!/yyyy'];
    	//時間
        $scope.model = {};
    	$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		/********查詢********/
		$scope.query = function(){
			//重新查詢時，上一次查詢結果需清除
			$scope.data = {};
			
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('填寫問卷時間必須輸入!!!');
        		return;
        	}			
			
			$scope.sendRecv("KYC620", "inquire", "com.systex.jbranch.app.server.fps.kyc620.KYC620InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								$scope.paramList=[];
	                			return;
	                		}					
							$scope.printList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;	
							return;
						}					
			});
			
			$scope.isClear = false;
		};
});
