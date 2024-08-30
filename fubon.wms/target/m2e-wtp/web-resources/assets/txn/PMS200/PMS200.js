/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS200Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$filter, getParameter,$q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS200Controller";
		$controller('PMSRegionController', {$scope: $scope});
//	$scope.P=function(){	
//		var deferred=$q.defer();
//		getParameter.XML(["PMS.CONTACT_SEQ"],function(totas){
//			if (totas) {
//			$scope.mappingSet['CONTACT_SEQ']=totas.data[totas.key.indexOf('PMS.CONTACT_SEQ')];
//        	   
//			 deferred.resolve();
//			   }
//        	});	
//	     return deferred.promise;
//	}

		
	$scope.init = function(){	
		
		$scope.inputVO = {
				aoFlag	:'Y',
				psFlag	:'N',
				sTime	: '',				
			};
//		Date now=new Date();
		var Today=new Date();
		$scope.date=Today;
		$scope.paramList2 = [];
		$scope.paramList = [];
		$scope.time;
		$scope.head;
		$scope.chang=[];
		$scope.intermediate=[];
		$scope.level=[];
		$scope.leveltemp=[];
		$scope.leveltemp2=[];
		$scope.sumFlag = false;
		$scope.disableRegionCombo = false;
		$scope.disableAreaCombo = false;
		$scope.disableBranchCombo = false;
		$scope.disableAoCombo = false;
		
		var vo = {'param_type': 'FUBONSYS.FC_ROLE', 'desc': false};
        $scope.requestComboBox(vo, function(totas) {      	
        	if (totas[totas.length - 1].body.result === 'success') {        		
        		projInfoService.mappingSet['FUBONSYS.FC_ROLE'] = totas[0].body.result;
        		for(var key in projInfoService.mappingSet['FUBONSYS.FC_ROLE']){
        	    	if(projInfoService.mappingSet['FUBONSYS.FC_ROLE'][key].DATA == projInfoService.getRoleID()){
        	    		$scope.inputVO.empHistFlag = 'Y';
        	    	}
        	    }
        	}
	    });
         
        	
        $scope.sendRecv("PMS200", "inquire", "com.systex.jbranch.app.server.fps.pms200.PMS200InputVO", $scope.inputVO,
			function(tota, isError) {
				if (!isError) {
					if(tota[0].body.resultList.length == 0) {
						$scope.paramList = [];
						$scope.showMsg("ehl_01_common_009");
	                	return;
	                }
					$scope.paramList = tota[0].body.resultList;
					$scope.time=$scope.paramList[0].time;
					$scope.time=Math.ceil($scope.time/30);  
					console.log($scope.paramList[0]);
					angular.forEach($scope.paramList,function(row, index, objs){
						var head=row.PARAM_CODE.substring(0,1);	
						var check=row.PARAM_CODE.substring(1);
						
						$scope.intermediate.push({
							header:head, 
							DATA:row.PARAM_NAME_EDIT 
						}); 
					});
					var keys=Object.keys($scope.intermediate);
					var len=keys.length;	
					for (var i=0,j=0;i<len;j++) {
						$scope.level.push({
							head:$scope.intermediate[keys[i]].header,   
							DATA1:$scope.intermediate[keys[i++]].DATA,   
							DATA2:$scope.intermediate[keys[i++]].DATA,   
							DATA3:$scope.intermediate[keys[i++]].DATA,
							DATA4:$scope.intermediate[keys[i++]].DATA
						});
					}
					angular.forEach($scope.level,function(row, index, objs){
						
                        if ($scope.level[index].head=='P') {                       		
                        	var head=$scope.level[index].head;
                        	var data1=$scope.level[index].DATA1;
                        	var data2=$scope.level[index].DATA2;
                        	var data3=$scope.level[index].DATA3;
                        	var data4=$scope.level[index].DATA4;	
                        	$scope.level[index].head=$scope.level[index-1].head;
                        	$scope.level[index].DATA1=$scope.level[index-1].DATA1;
                        	$scope.level[index].DATA2=$scope.level[index-1].DATA2;
                        	$scope.level[index].DATA3=$scope.level[index-1].DATA3;
                        	$scope.level[index].DATA4=$scope.level[index-1].DATA4;
                        	$scope.level[index-1].head=head;
                        	$scope.level[index-1].DATA1=data1;
                        	$scope.level[index-1].DATA2=data2;
                        	$scope.level[index-1].DATA3=data3;
                        	$scope.level[index-1].DATA4=data4;
                        }
					});   
					//$scope.outputVO = tota[0].body;
					return;
				}						
			});		
	};		
	$scope.init();		
	
	
	$scope.inquireInit = function(){
		$scope.initLimit();
		$scope.paramList = [];
		$scope.originalList = [];
	}
	$scope.inquireInit();	
		
        
	//選取月份下拉選單 --> 重新設定可視範圍
	$scope.dateChange = function(){
        	
		if ($scope.inputVO.sCreDate!=''&&$scope.inputVO.sCreDate!=undefined) { 			
        	$scope.inputVO.reportDate =$filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');	
        	//alert($scope.inputVO.sCreDate);
        	$scope.RegionController_getORG($scope.inputVO);
    	
        	// alert($scope.AREA_LIST);  
		} else {   
			$scope.inputVO.sCreDate='201701';
    		  
			$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
			$scope.RegionController_getORG($scope.inputVO);        
			$scope.inputVO.sCreDate=undefined; 
		} 
		
    	//alert($scope.inputVO.sCreDate)	
    	$scope.inputVO.region = '';
    	$scope.inputVO.op = '';
    	$scope.inputVO.branch = '';
    	$scope.inputVO.ao_code = '';
    	//$scope.genRegion();
    }; 
    
    //儲存
	$scope.save=function(){	
//		alert(JSON.stringify($scope.level));
		$scope.sendRecv("PMS200","save","com.systex.jbranch.app.server.fps.pms200.PMS200InputVO",{'list':$scope.level},
		function(tota,isError){
			if (isError) {
        		$scope.showErrorMsgInDialog(tota[0].body.msgData);	
        		return;
        	}
			if (tota.length > 0) {       		
				$scope.showMsg('儲存成功');
				$scope.init();
				return;
        	}
			
		});
				
	}	
		
});
