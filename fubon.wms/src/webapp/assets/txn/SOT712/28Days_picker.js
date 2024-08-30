/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('28Days_picker',
			function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "28Days_picker";
	
	$scope.click = function(date,preIndex,index){
		if($scope.arr.length >= 6){
			$scope.dates[preIndex][index].check = false;
			return;
		}
    	if($scope.isSelect){
      		$scope.isSelect = false;
    	}else{
      		$scope.isSelect = true;
    	}
    
    	if($scope.arr.includes(date.index)){
      		$scope.arr.splice($scope.arr.indexOf(date.index), 1);
    	}else{
      		$scope.arr.push(date.index);
    	} 
    	
  	}
	$scope.init = function(){ 
  		$scope.arr = [];
  		$scope.dates = []; 
		var start = 1;
		var end = 7;
  		for(var a=0; a<4; a++){
    		$scope.dates.push([]);
   
    		for(var i=start; i<=end; i++){
      			$scope.dates[a].push({"index":i,"check":false});
    		}
    		start+=7;
    		end+=7;
  		}
  		
  		
  	    //設定已選日期
  		try{
  		$scope.chargeDateList = $scope.connector('get','chargeDateList');
  		
  		if ($scope.chargeDateList) {
  			console.log("28Days_picker init:"+$scope.chargeDateList +"; "+$scope.chargeDateList.length); 
  			
  			$scope.chargeDateArr = [];
  			$scope.chargeDateArr = $scope.chargeDateList.split("、");
  			if($scope.chargeDateArr){
  			  if($scope.chargeDateArr.length>0){
  			 	 for(var j=0;j<$scope.chargeDateArr.length;j++){
  			 		//TODO 勾選? $scope.arr.push($scope.chargeDateArr[j]);
  			 	 }
  			 }
  			}
  			
  		}
  		}catch(e){
  			console.log('28DaysErr:'+e);
  		}
	}
	$scope.init(); 
	 
	$scope.selectDays = function(){  
		$scope.arr.sort(function(a, b){return a-b});
		var dateList = $scope.arr.toString();
		dateList = dateList.replace(/[,]+/g,"、");
		$scope.connector('set','chargeDateList',dateList);
		$scope.closeThisDialog('successful');
	} 
	
}
);