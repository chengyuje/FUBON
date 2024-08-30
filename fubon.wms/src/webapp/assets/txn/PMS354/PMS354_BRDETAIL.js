/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS354_BRDETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS354_BRDETAILController";
		$scope.init = function(){
			$scope.inputVO = $scope.row;
			$scope.sumFlag = false;
			//設定查詢初始值
			$scope.inputVO = {						
					prj_seq 	: $scope.row.prj_seq,
					branch_nbr   	: $scope.row.BRANCH_NBR,
					branch_area_id   : $scope.row.BRANCH_AREA_ID,					
					emp_id  	: $scope.row.EMP_ID,
				
        	};
        };
        $scope.init();
        /*** 查詢資料 ***/
		$scope.query = function(){
			$scope.sumFlag = false;
			$scope.sendRecv("PMS354", "queryBRDetail", "com.systex.jbranch.app.server.fps.pms354.PMS354InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							$scope.paramList = tota[0].body.resultList;	
							$scope.paramList2 = tota[0].body.resultList2;
							$scope.outputVO = tota[0].body;	
							//*****銷量目標   如果比數不足補0******//								
							angular.forEach($scope.paramList, function(row, index, objs) {
								//==銷量
								if($scope.colList.length!=0  && $scope.colList.length!=undefined){ 	//銷量不為0
									for(var x=0; x<$scope.colList.length;x++){
										if(row.PRD_LIST==null){
											row.PRD_LIST="無值";
										}else{
											if($scope.colList.length!=row.PRD_LIST.split(",").length)
												row.PRD_LIST+=",無值";
										}
									}										
								}else if($scope.colList.length==0){  //0就清空
									row.TARGET_LIST="";
								}
								
								//==目標
								if($scope.colList2.length!=0 && $scope.colList2.length!=undefined){   //目標不為0
									for(var x=0; x<$scope.colList2.length;x++){
										if(row.TARGET_LIST==null){
											row.TARGET_LIST="無值";
										}else{
											if($scope.colList2.length!=row.TARGET_LIST.split(",").length)
												row.TARGET_LIST+=",無值";
										}
									}
								}else if($scope.colList2.length==0){  //0就清空
									row.TARGET_LIST="";
								}
							});
							
							
							
							//*****總計用途******//								
							angular.forEach($scope.paramList2, function(row, index, objs) {
								//==銷量
								if($scope.colList.length!=0  && $scope.colList.length!=undefined){ 	//銷量不為0
									for(var x=0; x<$scope.colList.length;x++){
										if(row.PRD_LIST==null){
											row.PRD_LIST="無值";
										}else{
											if($scope.colList.length!=row.PRD_LIST.split(",").length)
												row.PRD_LIST+=",無值";
										}
									}										
								}else if($scope.colList.length==0){  //0就清空
									row.TARGET_LIST="";
								}
								
								//==目標
								if($scope.colList2.length!=0 && $scope.colList2.length!=undefined){   //目標不為0
									for(var x=0; x<$scope.colList2.length;x++){
										if(row.TARGET_LIST==null){
											row.TARGET_LIST="無值";
										}else{
											if($scope.colList2.length!=row.TARGET_LIST.split(",").length)
												row.TARGET_LIST+=",無值";
										}
									}
								}else if($scope.colList2.length==0){  //0就清空
									row.TARGET_LIST="";
								}
							});
							return;
						}						
			});
		};
//		$scope.query();
		
		$scope.getRPTCol = function(){			
			$scope.colList = [];
			$scope.sendRecv("PMS354", "queryRPTCol", "com.systex.jbranch.app.server.fps.pms354.PMS354InputVO", {'prj_seq':$scope.inputVO.prj_seq},
					function(tota, isError) {
						if (!isError) {
							if (isError) {
								$scope.showMsg("ehl_01_common_009");
		               			return;
		                	}
							if (tota.length > 0) {
								$scope.colList = tota[0].body.colList;
								$scope.colList2 = tota[0].body.colList2;
								$scope.outputVO = tota[0].body;		
								$scope.query();
//								return;
		                	};							
						}						
			});
			
		}
		$scope.getRPTCol()
		
		/**「，」分割資料**/ 
		$scope.getListByComma = function(value){
			if (value != "" && value != undefined) {
				return value.split(',').map(Number);
			}
		}
		//橫向加總
		$scope.getSum = function(group, key) {
        	var sum = 0;
            for (var i = 0; i < group.length; i++){            	
            	sum += _.sum(group[i][key].split(',').map(Number));
            }  
            return sum;
        }
		//直向加總
		$scope.getSumya = function(group, key) {
        	var sum = [];
        	var ha = 0;        	
        	if (group[0][key] != undefined) {
	        	for(var i = 0; i < group[0][key].split(',').map(Number).length; i++){
	        		for (var j = 0; j < group.length; j++){
	        			ha += group[j][key].split(',').map(Number)[i];
	        		}          		
	        		sum.push(ha);
	        		ha = 0;
	        	}
        	}
            return sum; 
        }
});
