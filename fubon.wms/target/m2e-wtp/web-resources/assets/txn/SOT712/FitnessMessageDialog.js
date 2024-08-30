/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('fitnessMessage_dialog',
			function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "fitnessMessage_dialog";
	
	
	//msgAndParam= ehl_01_SOT702_#param1^param2
	$scope.msgHandler = function(msgAndParam) {
		var msgParam = msgAndParam.split("#");
		var params = undefined;
		if(msgParam[1]){
			params = msgParam[1].split("^");
		}
		return $scope.sMsgHandler(msgParam[0], params);
	};
	
	$scope.sMsgHandler = function(sMsg, params) {
		var txtMsg = $filter('i18n')(sMsg);
		
 		if(typeof params !== "undefined"){
			try{
				var i = 0;
				if(txtMsg.match('field')){
					angular.forEach(params, function (){
						txtMsg = txtMsg.replace('#{field'+ (i+1) +'}#', params[i]);
						i++;
					});
				}
				else{
					angular.forEach(params, function (){
						txtMsg = txtMsg.replace('{'+ i +'}', params[i]);
						i++;
					});
				}
			}
			catch(e){
				//
			}
 		 }	
		return txtMsg;
	}
	
	$scope.init = function(){  
  		$scope.fitnessMessageList = [];  
  		var aaa=['TEST_PRD_ID'];
  		try{
	  		var fitnessMsgList = $scope.connector('get','fitnessMessageList');
	  		$scope.connector('set','fitnessMessageList',null);
	  		
	  		if(fitnessMsgList){
	  			console.log("fitnessMessage init:"+fitnessMsgList); 
	  			//$scope.fitnessMessageList.push($scope.msgHandler('ehl_01_SOT702_007#testPrdId' ) );
	  			//$scope.fitnessMessageList.push($scope.msgHandler('ehl_01_SOT702_005#testPrdId' ) ); 
	  			//$scope.fitnessMessageList.push($scope.msgHandler('ehl_01_SOT702_002' ) );
	  			var fitnessMessageIdList = fitnessMsgList.split(",");
	  			if(fitnessMessageIdList && fitnessMessageIdList.length>0){ 
	  			 	 for(var j=0;j<fitnessMessageIdList.length;j++){ 
	  			 		$scope.fitnessMessageList.push($scope.msgHandler(fitnessMessageIdList[j]));
	  			 	 } 
	  			} 
	  			console.log("fitnessMessageList[] :"+JSON.stringify($scope.fitnessMessageList));  
	  		}
  		}catch(e){
  			console.log('fitnessMessageErr:'+e);
  		}
	}
	$scope.init(); 
	  
}
).directive('msgDiv', function() {
	return {
	  	restrict: 'E',
	    template: function(element, attrs) {
	    	
	      var htmlText = '<ol width="650px">'+
	                     '<li ng-repeat="msg in fitnessMessageList">'+ 
	                     '{{msg}}'+
	                     '</li>'+
	                     '</ol>';
	      
	    	return htmlText;
	    }
	  };
	});