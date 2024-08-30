/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS356_EDITController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS356_EDITController";
		     
		$scope.comTypeChange = function(){
	    	if($scope.inputVO.comType == '2')
	    		$scope.ctp = '分行';	    		    		
	    	else
	    		$scope.ctp = '公用';	    		    			    			
	    };
		
		$scope.init = function(){
			
			$scope.inputVO = {
					comType: '1',
					ip: '',
					rc_id: '',
					rc_name: '',
					op_id: '',
					op_name: '',
					br_id: '',
					br_name: '',
					emp_id: '',
					emp_name: '',
					name_fg: ''
        	};						    		    		
    		$scope.ctp = '公用';
    		
        };
        $scope.init();
        
        
        $scope.exl_file =function(set_name){
        	$scope.inputVO.set_code = "PMS356";
	    	$scope.exceil_delete = function() {
	    		$scope.sendRecv("PMS356", "tuncateTable", "com.systex.jbranch.app.server.fps.pms356.PMS356InputVO", $scope.inputVO,
						function(totas, isError) {
							if (!isError) {
								 $scope.exceil=function() {
									 if($scope.inputVO.realfileName == undefined){
										 $scope.showMsg("請選擇檔案");
										 return;
									 }
									 console.log($scope.inputVO.realfileName);
										$scope.inputVO.set_code = "PMS356";
										$scope.sendRecv("IMPORTFILE", "importfile", "com.systex.jbranch.app.server.fps.importfile.IMPORTFILEInputVO", $scope.inputVO,
												function(totas, isError) {
													if (isError) {
														$scope.showErrorMsgInDialog(totas.body.msgData);
														return;
													}
													if (totas.length > 0) {
														$scope.showMsg('上傳成功');
														$scope.closeThisDialog('cancel');
													};
												}
										);
								    };
								  $scope.exceil();
						        }
								return;
							});
				}   
	    		$scope.exceil_delete();
	    };
	       
	    //upload
	    $scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.fileName = name;
            	$scope.inputVO.realfileName = rname;
        	}
        };
		        
        
        
      //function
        $scope.action = function(row) {
        	if($scope.cmbAction) {
				if($scope.cmbAction == "A") {
					$scope.add();
				}else if($scope.cmbAction == "F") {
					$scope.detail(row);
				}else{
					$scope.delete(row);
				}
				$scope.cmbAction = "";
        	}
        }

        
       
	    
 /**==========================*/
      
  /**==========================*/  	    
	    
        
                	
});
