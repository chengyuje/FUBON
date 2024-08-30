/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS356Controller',
	['$scope', '$controller', 'socketService', 'ngDialog', 'projInfoService', '$q', '$confirm', '$filter',
	 function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$controller('PMSRegionController', {$scope: $scope});
		$scope.controllerName = "PMS356Controller";
		
		
		$scope.dataMonthChange = function(){
			if($scope.inputVO.reportDate)
				$scope.RegionController_getORG($scope.inputVO);
		}
		
		
		 /***TEST ORG COMBOBOX START***/
        var org = [];
       
        $scope.init = function(){
        	console.log('empty')
        	$scope.paramList=[];
        	$scope.inputVO={};
		};
		$scope.init();
		
		var NowDate = new Date();
//		$scope.toJsDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+1;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<36; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
        
        
        $scope.mappingSet['seture']=[];
        $scope.mappingSet['seture'].push({
			DATA : '2',
			LABEL : '理專統計'
		},{
			DATA : '1',
			LABEL : '分行統計'
		});
        
	
		$scope.inquireInit = function(){
		
			$scope.paramList = [];
			$scope.originalList = [];
		}
		$scope.inquireInit();
		
	/***new***/	
		$scope.numGroups = function(input){
			if(!input)
				input=0;
            return Object.keys(input).length;
           }
         
           $scope.getSum = function(group, key) {
            var sum = 0;
            for (var i = 0; i < group.length; i++){
             sum += group[i][key];
            }  
            return sum;
           }
		
		
		
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid) {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:資料月份為必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("PMS356", "inquire", "com.systex.jbranch.app.server.fps.pms356.PMS356InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							$scope.csvList=tota[0].body.csvList;
							return;
						}					
			});
		};
		
		
	//匯出exceil
    	$scope.export = function() {			
			$scope.sendRecv("PMS356", "export",
					"com.systex.jbranch.app.server.fps.pms356.PMS356OutputVO",
					{'list':$scope.csvList}, function(tota, isError) {
						if (!isError) {
										
							return;
						}
					});
		};
		
		
		$scope.btnNew = function(){				
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS356/PMS356_EDIT.html',
				className: 'PMS356_EDIT',
				controller:['$scope', function($scope){
					$scope.row = {};
				}]
			});
			dialog.closePromise.then(function(data) {				
				if(data.value === 'successful'){
					$scope.inquireInit();
					$scope.query();						
				}
			});
		};

		
		
		/**
		 * 下載
		 */
		$scope.downLoad1 = function() {
			$scope.sendRecv("PMS356", "downLoad1", "com.systex.jbranch.app.server.fps.pms356.PMS356OutputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
				}
			});
		}

}]);
