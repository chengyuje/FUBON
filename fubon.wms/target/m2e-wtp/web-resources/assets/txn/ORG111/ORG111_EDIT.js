/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('ORG111_EDITController', 
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "ORG111_EDITController";   
        
    	var connect = $scope.connector('get','ORG110_tnsfData');
    	
    	//===filter
        getParameter.XML(["ORG.RESIGN_HANDOVER"], function(totas) {
        	$scope.mappingSet['ORG.RESIGN_HANDOVER'] = [];
			if (totas) {
				angular.forEach( totas.data[totas.key.indexOf('ORG.RESIGN_HANDOVER')], function(row){
					if ($scope.loginPrivilegeID == "043") {
						$scope.mappingSet['ORG.RESIGN_HANDOVER'].push({LABEL: row.LABEL, DATA: row.DATA});
					} else {
						if ((row.DATA).substr(0, 1) == '0') {
							$scope.mappingSet['ORG.RESIGN_HANDOVER'].push({LABEL: row.LABEL, DATA: row.DATA});
						}
					}
				});
			}
		});
        //===
    	
        /*****初始化*****/
        $scope.init = function(){
        	
        	if($scope.row){
        		$scope.isUpdate = true;
        	}       	
        	
            $scope.eComboDisabled = false; 
            $scope.inputVO.EMP_ID = $scope.row.EMP_ID;
            
            $scope.sendRecv("ORG111", "getResignMemo", "com.systex.jbranch.app.server.fps.org111.ORG111ResignMemoVO", $scope.inputVO,
        			function(tota, isError) {
  	                   	if (!isError) {
  	                   		
	  	                   	if (tota[0].body != null) {
	  	                   		$scope.resignList = tota[0].body.resignList;
		  	                   	$scope.inputVO.RESIGN_HANDOVER = $scope.resignList[0].RESIGN_HANDOVER;
		  	                   	$scope.inputVO.RESIGN_REASON = $scope.resignList[0].RESIGN_REASON;
		  	                   	$scope.inputVO.RESIGN_DESTINATION = $scope.resignList[0].RESIGN_DESTINATION;
		  	                   	$scope.inputVO.DESTINATION_BANK_ID = $scope.resignList[0].DESTINATION_BANK_ID;
	  	                   	} else {
	  	                   		$scope.inputVO = {
	  	                   			EMP_ID:$scope.row.EMP_ID,
		  	              			RESIGN_REASON:'',                                            
		  	              			RESIGN_DESTINATION:'',                                       
		  	              			DESTINATION_BANK_ID:'',  
		  	              			RESIGN_HANDOVER:($scope.loginPrivilegeID == "043" ? 'A1' : '01'),
		  	                   	};
	  	                   	}
  	                   	}
        	});
        }
        
        $scope.init();
        
        
        /*觸發連動*/
        $scope.action = function(val){
        	if(val === '01'){
        		$scope.checked = true;
    		}else{
    			$scope.checked = false;
    			$scope.inputVO.DESTINATION_BANK_ID = '';
    		}
        }
      
        /******以下是用來做radio button******/
        $scope.save = function(){
    		$scope.sendRecv("ORG111", "resignHandover", "com.systex.jbranch.app.server.fps.org111.ORG111ResignMemoVO", $scope.inputVO,
        			function(tota, isError) {
  	                   	if (!isError) {
  	                   		$scope.showMsg("ehl_01_common_025");
  	                   		$scope.closeThisDialog($scope.inputVO);
  	                   	}
        	});
        }
});
