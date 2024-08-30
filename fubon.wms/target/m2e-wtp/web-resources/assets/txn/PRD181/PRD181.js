/**
 * 
 */
'use strict';
eSoafApp.controller('PRD181Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,$filter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD181Controller";
 
        
		//查詢
        $scope.queryData = function(){
        	$scope.sendRecv("PRD181","queryData","com.systex.jbranch.app.server.fps.prd181.PRD181InputVO",
        			$scope.inputVO,function(tota,isError){
        				if(!isError){
                			$scope.dicountlist = tota[0].body.DicountList;
                			$scope.outputVO = tota[0].body;
                			
        					if($scope.dicountlist.length == 0){
        						
                				$scope.showMsg("ehl_01_common_009");
                    			return;
                			}
                			angular.forEach($scope.dicountlist, function(row, index, objs){
								row.edit = [];			    				
								row.edit.push({LABEL: "刪除", DATA: "D"});
							});
							return;
        				}
        	});
        };
	
		//初始化
        $scope.init = function(){
        	$scope.queryData();
        };
        $scope.init();

        //分頁初始化
        $scope.inquireInit = function(){
        	$scope.questionList = [];
        };
        $scope.inquireInit();
        
        $scope.edit = function(index,row){
        	switch (row.editto) {
        
			case 'D':
				var txtMsg = $filter('i18n')('ehl_02_common_001');
				$scope.inputVO.INSPRD_KEYNO = row.INSPRD_KEYNO
	        	$confirm({text: txtMsg},{size: 'sm'}).then(function(){
	               	$scope.sendRecv("PRD181","deleteData","com.systex.jbranch.app.server.fps.prd181.PRD181InputVO",
	            			$scope.inputVO,function(tota,isError){
	            			if(isError){
	            				$scope.showErrorMsg(tota[0].body.msgData);
	            			}
	                       	if (tota.length > 0) {
	                    		$scope.showSuccessMsg('ehl_01_common_003');
	                    		$scope.queryData();
	                    	};
	            	});
	        	});				
				break;
			default:
				break;
			}
        }
        
        $scope.addRow = function(){
        	var dialog = ngDialog.open({
        		template: 'assets/txn/PRD181/PRD181_EDIT.html',
        		className: 'PRD181',
        		controller: ['$scope',function($scope){
        		
        		}]
        	});
        	dialog.closePromise.then(function(data){
        		$scope.queryData();
        	});

        	
        }

        
	}     
);