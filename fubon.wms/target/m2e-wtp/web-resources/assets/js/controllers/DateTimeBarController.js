/**===============================================================================
								  DateTime Bar
==================================================================================
 	    @Author: 
   @description: DateTime Bar Controller
    @LastUpdate: 
    2016/03/13 ArthurKO: Add  
================================================================================*/
eSoafApp.controller("DateTimeBarController", ['$rootScope', '$scope', '$interval',
	function($rootScope, $scope, $interval) {
	
	
		
	    /** Get Time **/
	    /* current date time bar on bottom  */
	    var updateTime = function() {
	      return $scope.clock = new Date();
	    };
	    $interval(function() {
	      return updateTime();
	    }, 1000);
	    
		
		
	}]
);

