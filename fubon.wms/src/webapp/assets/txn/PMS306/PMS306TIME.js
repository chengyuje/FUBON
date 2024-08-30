/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS306TIMEController',
	function($rootScope, $scope, $controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS306TIMEController";
			
	
		// 預設的filter
		$scope.init = function(){
			$scope.inputVO = {					
					YEARS:'2016'    //年月     				
        	};
			$scope.paramList = [];	
			$scope.outputVO={};
		};
        $scope.init();
        
        var NowDate = new Date();
		
		NowDate.setMonth(NowDate.getMonth()+3); //君榮提出下拉選單可以顯示未來月份
		var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+2;
        var strmm='';
        $scope.mappingSet['timeA'] = [];
        for (var i = 0; i < 24; i++) {
			mm = mm - 1;
			if (mm == 0) {
				mm = 12;
				yr = yr - 1;
			}
			if (mm < 10)
				strmm = '0' + mm;
			else
				strmm = mm;
			$scope.mappingSet['timeA'].push({
				LABEL : yr + '/' + strmm,
				DATA : yr + '' + strmm
			});
		} 
        /*****查詢資料******/
        $scope.inquire = function(){        	
			$scope.sendRecv("PMS306", "queryperson", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
							if(tota[0].body.personlist.length == 0) {
								$scope.paramList =[];
								$scope.showMsg("ehl_01_common_009");								
	                			return;
	                		}
							$scope.paramList = tota[0].body.personlist;
							$scope.originalList = angular.copy(tota[0].body.personlist);
							$scope.outputVO = tota[0].body;					
							return;
						}
			});
		};
//		
		 
		 
		 
		   /*****儲存人壽資料*****/
	        $scope.save = function(){   				
				$scope.sendRecv("PMS306", "savePerson", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", {'YEARS':$scope.inputVO.YEARS ,'list':$scope.paramList, 'list2':$scope.originalList},
						function(tota, isError) {						
							if (isError) {
			            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
			            		return;
			            	}
			            	if (tota.length > 0) {
			            		$scope.showMsg('儲存成功');			            	
			            	};		
				});
			};
		
		 
		 
	
         
       
		
});
