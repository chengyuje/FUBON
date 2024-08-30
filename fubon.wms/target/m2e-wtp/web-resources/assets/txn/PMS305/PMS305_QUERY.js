/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS305_QUERYController',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS305_QUERYController";
		   		
		var currDate = new Date();
		
		$scope.init = function(){
			 if($scope.INS_ID)
				$scope.isUpdate = true;
			 $scope.checked=-1;
			 $scope.check=[];
			 $scope.len=0;
			 $scope.inputVO = {				
					INS_ID:''
        	
			
			
			     };			
			$scope.paramList=[];
			if($scope.isUpdate){
				$scope.inputVO.INS_ID = $scope.INS_ID;
			 }

			//alert($scope.inputVO.INS_ID)
			
        
		
		
		
		
		};
        $scope.init();
     $scope.ch=function(index){
    	//alert(index); 
      
         $scope.checked=$scope.paramList[index].INSPRD_NAME;
         
         $scope.del(index);
         
         
     }  
     $scope.del=function(index)    
      {
       for(var i=0;i<$scope.len;++i)	 
    	 if(i!=index)
         {
    		 $scope.check[i]=false;	 
    		 
         } 
      
       $scope.check[index]=true;	 
		 
      }  
        
   $scope.confirm=function(){
	   $rootScope.$broadcast("rootEvent",$scope.checked);  
	   
   }  
     
     
       $scope.aa=function(){
    	  
    	   
    	  
    	   $scope.sendRecv("PMS305", "queryINS", "com.systex.jbranch.app.server.fps.pms305.PMS305detailInputVO", $scope.inputVO,
					function(tota, isError) {
						
    		              if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList =tota[0].body.resultList;
							$scope.len=$scope.paramList.length;
							
							return;
						}				
			});
    	   
    	   
    	   
    	   
    	   
    	   
    	 
       }
       $scope.aa();  
        /*確定有xml參數在使用*/
        /*
    	getParameter.XML(["A","B"], function(totas) {
			if (totas) {
				$scope.mappingSet['A'] = totas.data[totas.key.indexOf('A')];
				$scope.mappingSet['B'] = totas.data[totas.key.indexOf('B')];
			}
		});
		*/
     
    
                	
});
