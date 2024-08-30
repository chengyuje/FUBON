/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS101_LEADSController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS101_LEADSController";

		$scope.init = function(){
			$scope.inputVO = {
				CUST_ID: $scope.row.CUST_ID,   //取得客戶id
			    CUST_NAME: $scope.row.CUST_NAME,   //取得客戶姓名
			    DATA_DATE: $scope.row.DATA_DATE,   //日期
			    
        	};
		};
        $scope.init();
        
        $scope.mappingSet['CF_TYPE'] = [];
        $scope.mappingSet['CF_TYPE'].push({LABEL:'即期', DATA:'1'}, {LABEL:'入帳', DATA:'2'});  //修正入帳
		
        $scope.inquireLeads = function(){
        	 //===leaderlist查詢  ==
            $scope.sendRecv("PMS101", "getLeadsList", "com.systex.jbranch.app.server.fps.pms101.PMS101InputVO", $scope.inputVO,
            		function(tota, isError) {
    					if (!isError) {
    						$scope.resultList = tota[0].body.resultList;
    						$scope.outputVO = tota[0].body;
    						return;
    					}
    		});
        }
        $scope.inquireLeads();
       
});
