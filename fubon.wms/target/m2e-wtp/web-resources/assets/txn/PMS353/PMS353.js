/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS353Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS353Controller";
		
		//filter
	
        
       
        $scope.init = function(){
			$scope.inputVO = {
					CUST_ID:'',
					PROD_TYPE:'',
					TYPE:'',
					PRD_ID : '',
        	};
			
		};
        $scope.init();
        
        
       
        
        $scope.init_his = function(){
		
			
			
		    $scope.mappingSet['type']=[];
            $scope.mappingSet['type'].push({
				LABEL : 'Y',
				DATA :'Y'
    		},{
				LABEL : 'N',
				DATA :'N'
    		});
                  
           
	
		};
        $scope.init_his();
		
        // 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        }
        $scope.inquireInit();
        
        $scope.inquire = function(){        
			$scope.sendRecv("PMS353", "queryData", "com.systex.jbranch.app.server.fps.pms353.PMS353InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.max='';
							$scope.max= tota[0].body.resultList[0].MAXA;					
							$scope.outputVO = tota[0].body;
							
							return;
						}
			});
		};		
		 $scope.inquire(); //初始查詢
   
		 //delData
      
	      
	        $scope.del = function(row){   
	        	//2017/2/10新增確認刪除彈跳視窗
	        	var x=false;
	        	$scope.inputVO.docno = row.DOC_ID;
	        	$confirm({text: '請確定是否刪除此筆資料？'}, {size: 'sm'}).then(function() {
				$scope.sendRecv("PMS353", "delData", "com.systex.jbranch.app.server.fps.pms353.PMS353InputVO", row,
						function(tota, isError) {
						  	
					         
					if (!isError&&x==false) {
//								if(tota[0].body.resultList.length == 0) {
//								for(var i = 1 ; i == tota[0])
								
								 $scope.showMsg("刪除成功");
									$scope.inquire(); //初始查詢
								    x=true;
									return;
							}
					});
				});
			};		
			
	
		
	   
        /*****以下是修改鍵2開啟視窗******/
        $scope.sys = function (row,num) {
        		row.NUM=num;
        		
        	
	    	   var dialog = ngDialog.open({
                template: 'assets/txn/PMS353/PMS353_LEADS.html',
                className: 'PMS353_LEADS',
                controller: ['$scope', function($scope) {
              	  $scope.row = row;
                }]
            });
            dialog.closePromise.then(function (data) {
            
              	  
              	  $scope.inquire();
               
            });
        };
        
        
	
});
