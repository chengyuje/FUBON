/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM617Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM617Controller";
		
		// bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
		

        
      //xml
		 getParameter.XML(['CRM.ECHANNEL_STATUS','CRM.CRM230_HAS_YN','CRM.ECHANNEL_MOBIL_STATUS'], function(totas) {
				if(len(totas)>0) {
					$scope.mappingSet['CRM.ECHANNEL_STATUS'] = totas.data[totas.key.indexOf('CRM.ECHANNEL_STATUS')];
					$scope.mappingSet['CRM.CRM230_HAS_YN'] = totas.data[totas.key.indexOf('CRM.CRM230_HAS_YN')];
					$scope.mappingSet['CRM.ECHANNEL_MOBIL_STATUS'] = totas.data[totas.key.indexOf('CRM.ECHANNEL_MOBIL_STATUS')];				
				}
				 
			});
  
       
		//初始化
		$scope.init = function(){
			$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
		};
		$scope.init();
		
		//初始化
		$scope.inquireInit = function(){
			$scope.resultList = [];
			$scope.resultList2=[];
			$scope.Edata = [];
			$scope.Sdata = [];
			$scope.Mdata = [];
			$scope.data = [];
			$scope.data1 = [];
			$scope.data2 = [];
			$scope.outputVO =[];
		}
		$scope.inquireInit();
		
		//初始查詢
		$scope.initial = function(){
    		$scope.sendRecv("CRM617", "initial", "com.systex.jbranch.app.server.fps.crm617.CRM617InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					}
					if(tota[0].body.resultList != null && tota[0].body.resultList.length >0 ) {
						
						$scope.resultList = tota[0].body.resultList;
					}
				}
		)};
		$scope.initial();
		
		
	
	
//		$scope.cust_id = $scope.connector('get','CRM110_CUST_ID');
		$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
		$scope.sendRecv("CRM617", "inquire", "com.systex.jbranch.app.server.fps.crm617.CRM617InputVO", {'cust_id':$scope.cust_id},
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList2 && tota[0].body.resultList2.length == 0 ) {
							$scope.showMsg("ehl_01_common_009");
                			return;
                		}
						
						$scope.dataList = tota[0].body.resultList3;
						
						angular.forEach($scope.dataList, function(data) {
							debugger;
							if(data.USER_ID) {
								if(data.USER_ID.length <=3){
									
								} else if (data.USER_ID.length <=5) {
									data.USER_ID = data.USER_ID.substr(0,2) + '***';
								} else {
									data.USER_ID = data.USER_ID.substr(0,2) + '***' + data.USER_ID.substr(5);
								}
								
							}
						});
						$scope.Edata = $filter('filter')($scope.dataList,{BUS_TYPE:'1'});
						$scope.Sdata = $filter('filter')($scope.dataList,{BUS_TYPE:'2'});
						$scope.Mdata = $filter('filter')($scope.dataList,{BUS_TYPE:'3'});
						for(var i = 0 ; i < $scope.Edata.length ;  i ++) {
							$scope.Edata[i].SEQ = i+1;
						}
						for(var i = 0 ; i < $scope.Sdata.length ;  i ++) {
							$scope.Sdata[i].SEQ = i+1;
						}
						for(var i = 0 ; i < $scope.Mdata.length ;  i ++) {
							$scope.Mdata[i].SEQ = i+1;
						}
						$scope.outputVO_Edata = {'data' : $scope.Edata};
						$scope.outputVO_Sdata = {'data' : $scope.Sdata};
						$scope.outputVO_Mdata = {'data' : $scope.Mdata};
						return;
					}
		});
});
		