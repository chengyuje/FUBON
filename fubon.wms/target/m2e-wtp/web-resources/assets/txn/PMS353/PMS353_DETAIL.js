/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS353_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS353_DETAILController";
		$scope.inputVO={
				PRJ_SEQ:$scope.row.PRJ_SEQ
		};
		// filter
		var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        		}
        	});
        } else
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
     
	
		$scope.inquire = function(){
			$scope.sendRecv("PMS353", "getDetail", "com.systex.jbranch.app.server.fps.pms353.PMS353InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.header=tota[0].body.header;
							$scope.paramList=tota[0].body.resultList;
							angular.forEach($scope.paramList, function(row, index, objs){
								$scope.mapp(row.BRANCH_NBR);  					                 					                 					                 			    					    																												
							});	
							
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		$scope.inquire();
		
		
		
		//import
		$scope.test = function(set_name) {
			$scope.inputVO.set_name = set_name;
			$scope.sendRecv("TESTWALA", "test", "com.systex.jbranch.app.server.fps.testwala.TESTWALAInputVO", $scope.inputVO,
					function(totas, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(totas.body.msgData);
							return;
						}
						if (totas.length > 0) {
							$scope.showMsg('上傳成功');
						};
					}
			);
	    };
	    
	    //upload
	    $scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.fileName = name;
            	$scope.inputVO.realfileName = rname;
        	}
        };
		
		
		
		// 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        };
        $scope.inquireInit();
        
     
        
      $scope.mapp=function(rows){
    		$scope.mappingSet[rows] = [];
    	
    		$scope.sendRecv("PMS353", "queryRPTCol2", "com.systex.jbranch.app.server.fps.pms353.PMS353InputVO",{BRANCH_NBR:rows,PRJ_SEQ:$scope.row.PRJ_SEQ},
					function(tota, isError) {
    			if(!isError){
    				$scope.inputVO.io=[];
    				$scope.inputVO.io=tota[0].body.resultList
    			
    			
						angular.forEach(tota[0].body.collist, function(row, index, objs){
								$scope.mappingSet[rows].push({LABEL: row.TARGET});	                 					                 					                 					                 			    					    																												
		        		});	
    			}else{
    				
    			return;
    			}
			});	

    	 return $scope.mappingSet[rows];
      }
   
  
     
        
		
});
