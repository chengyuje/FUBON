/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS203_INS_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
	$controller('PMS203Controller', {$scope: $scope});
		$scope.controllerName = "PMS203_INS_EDITController";
	
		$scope.init = function(){
			if($scope.row!==undefined)
				$scope.isUpdate = true;
			$scope.inputVO = {
				reportDate: $filter('date')(new Date(), 'yyyyMM'),
				region_center_id: '',
				branch_area_id: '',
				BRANCH_NBR: '',
				rc_name: '',
				op_name: '',
				br_name: '',
				invTarAmount: 0,
				insTarAmount: 0,
				exchgTarAmount: 0,
				totTarAmount: 0,
				sCreDate:$scope.ym,
				type:$scope.tgtType
        	};						    		    		
			$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
			$scope.inputVO.tgtType = $scope.inputVO.type;
    		if($scope.isUpdate){
    			$scope.inputVO.BRANCH_NBR = $scope.row.BRANCH_NBR;
    			$scope.inputVO.invTarAmount = $scope.row.INV_TAR_AMOUNT;
    			$scope.inputVO.insTarAmount = $scope.row.INS_TAR_AMOUNT;
    			$scope.inputVO.exchgTarAmount = $scope.row.EXCHG_TAR_AMOUNT; 
    			$scope.inputVO.totTarAmount = $scope.inputVO.invTarAmount + $scope.inputVO.insTarAmount + $scope.inputVO.exchgTarAmount;
    		}
    		console.log($scope.inputVO);
    		$scope.RegionController_getORG($scope.inputVO);
        };
        $scope.init();

        
        $scope.UpdateGetNbr = function(){ 	
        	if($scope.isUpdate){
        		$scope.inputVO.branch_nbr = $scope.inputVO.BRANCH_NBR;
        	}
        }
        $scope.getBnrInfo = function(){ 
        	var deferred = $q.defer();
        	$scope.sendRecv("PMS203", "getBnrInfo", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO",
        			{'branch_nbr': $scope.inputVO.branch_nbr,'ao_code':$scope.inputVO.ao_code},
    				function(totas, isError) {
        				var row = totas[0].body.totalList[0];
    					$scope.inputVO.rc_name = row.REGION_CENTER_NAME;
    					$scope.inputVO.op_name = row.BRANCH_AREA_NAME;
    					$scope.inputVO.br_name = row.BRANCH_NAME;
    					deferred.resolve($scope.inputVO);
    		});
          
  		    return deferred.promise;
        	
        };	    
        
        var saveOrNext = function(val){
        	var totAmt = parseInt($scope.inputVO.invTarAmount) + parseInt($scope.inputVO.insTarAmount) + parseInt($scope.inputVO.exchgTarAmount);
			$scope.inputVO.totTarAmount = totAmt; 
          	if($scope.isUpdate) {
        		$scope.sendRecv("PMS203", "updateINS", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
    					}
    			);
        	} else {       
        		$scope.sendRecv("PMS203", "insertINS", "com.systex.jbranch.app.server.fps.pms203.PMS203InputVO", $scope.inputVO,
    					function(totas, isError) {
    	                	if (isError) {
    	                		$scope.showErrorMsg(totas[0].body.msgData);
    	                	}
    	                	if(totas[0].body === 'duplicate'){
                       		 $scope.showErrorMsg('ehl_01_common_016');
                       		 return
    	                	};
    	                	if (totas.length > 0) {
    	                		$scope.showSuccessMsg('ehl_01_common_004');
                        		$scope.closeThisDialog('successful');
    	                	};
                            if(val ==='next')
                            	$scope.edit(undefined,'INS');
    					}
    			);        		
        	} 
        }
  	    
        $scope.save = function() {

        	if($scope.parameterTypeEditForm.$invalid){
  	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	$scope.getBnrInfo().then(function(){
        		saveOrNext();
        	});
       	
        };
        
        $scope.addNext = function(value){
        	if($scope.parameterTypeEditForm.$invalid){
  	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	$scope.getBnrInfo().then(function(){
        		$scope.isUpdate = false;
        		saveOrNext(value);
        	});     	
        };    
                	
});
