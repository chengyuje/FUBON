/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR021_Controller',
    function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $confirm) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR021_Controller";
        
        
        
        /** initialize **/
        $scope.init = function(){
        	/* inputVO */
        	$scope.inputVO = {
        		fileName: '',
        		filePath: '',
        		folderName: ''
        	};
        	/* Setting ComBobox list */
        	$scope.folderList = [{ 'LABEL' : 'CURRENT_LOG', 'DATA' : 'CURRENT_LOG' }];
        	$scope.cmbModel = 'CURRENT_LOG';
        };       
        $scope.init();
        
        
        $scope.inquireInit = function(){
        	$scope.initLimit(); 
        	$scope.paramList = [];
        };
        $scope.inquireInit();
        
        
        
        /** Get File List **/
        $scope.inquire = function(){        	
			  console.log('inquire');	    	  
			  $scope.sendRecv("CMMGR021", "getFileList", "com.systex.jbranch.app.server.fps.cmmgr021.CMMGR021InputVO", $scope.inputVO,
		              function(tota, isError) {
		                  if (!isError) {
		                	  /* [fileList] by OutputVO */
		                	  $scope.paramList = tota[0].body.fileList;
		                	  $scope.outputVO = tota[0].body;
		                	  console.log('tota: '+JSON.stringify(tota[0].body));
		                	  console.log('paramList: '+JSON.stringify($scope.paramList));
		                      //System Message
		                      if ($scope.paramList == null) {
		                    	  $scope.showMsg('ehl_01_common_001');
		                      }
		                  }
		              });
        };
        $scope.inquire();
        
        
        
        /** Download File **/
        $scope.downloadFile = function(){
        	//setting variable
        	var flag = false;
        	var tmp = [];
        	//check selected
        	for(var i in $scope.paramList){
        		if($scope.paramList[i].SELECTED){
        			flag = true;
        			tmp = $scope.paramList[i];
        			break;
        		}
        	}
        	//download
        	if(flag){  
        		console.log('downloadFile');
        		//check input data than download
        		if(tmp){
        			$scope.inputVO.fileName = angular.copy(tmp.fileName);
        			$scope.inputVO.filePath = angular.copy(tmp.filePath);
        			$scope.inputVO.folderName = angular.copy(tmp.folderName);        				    	  
        			$scope.sendRecv("CMMGR021", "downloadFile", "com.systex.jbranch.app.server.fps.cmmgr021.CMMGR021InputVO", $scope.inputVO,
        		        function(tota, isError) {
        			        if (!isError) {
        			        	console.log('downloadFile: sucess');
        			        	return;
        			        }
        		        });
        		}else{
        			console.log('downloadFile: fail(no data from list row)');
        		}
        		
        	}else{
        		$scope.showMsg('請選擇項目！');
        	}
			
        };
        $scope.inquire();

        
       
        
        
        
    }
);