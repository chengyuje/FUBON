/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS353_QUERYController',
	function($rootScope,$scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS353_QUERYController";
		   		
		var currDate = new Date();
		
		$scope.init = function(){
			 if($scope.INS_ID)
				$scope.isUpdate = true;
			 $scope.checked=[];
			 $scope.check=[];
			 $scope.len=0;
			 $scope.inputVO = {				
					INS_ID:'',
					checkgi:$scope.checkgi

			
			     };			
			$scope.paramList=[];
			if($scope.isUpdate){
				$scope.inputVO.INS_ID = $scope.INS_ID;
			 }

			//alert($scope.inputVO.INS_ID)
//			alert("query "+JSON.stringify($scope.inputVO.checkgi));	
		};
        $scope.init();
        
     //基金查詢
     $scope.ch=function(index){
      
//    	 if($scope.checkgi == '2') {
//             $scope.checked=$scope.paramList[index].INSPRD_ID;
//    	 } else if($scope.checkgi == '1'){
    	 
//    	 for(var i= 0 ;i<$scope.paramList.length;i++){
    		 $scope.checked.push($scope.paramList[index].PRD_ID);
//    	 }
//     }
    	 
//         $scope.del(index);這行原本有，因為要多選所以拿掉
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
	   //把陣列轉成字串傳回353_leads
	   $rootScope.$broadcast("rootEvent",$scope.checked.join());  
	   
   }  
     
     
       $scope.aa=function(){
    	   $scope.sendRecv("PMS353", "queryINS", "com.systex.jbranch.app.server.fps.pms205.PMS205detailInputVO", $scope.inputVO,
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
