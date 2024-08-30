/**
 * 
 */
'use strict';
eSoafApp.controller('PRD173Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,$filter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD173Controller";
 
        
		//查詢
        $scope.queryData = function(){
        	$scope.sendRecv("PRD173","queryData","com.systex.jbranch.app.server.fps.prd173.PRD173InputVO",
        			$scope.inputVO,function(tota,isError){
        				if(!isError){
                			$scope.fxd_dicountlist = tota[0].body.FXD_DicountList;
                			$scope.outputVO = tota[0].body;
        					if($scope.fxd_dicountlist.length == 0){
                				$scope.showMsg("ehl_01_common_009");
                    			return;
                			}
                			angular.forEach($scope.fxd_dicountlist, function(row, index, objs){
								row.edit = [];
			    				row.edit.push({LABEL: "修改", DATA: "U"});
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
        	case 'U':
        		var dialog = ngDialog.open({
	        		template: 'assets/txn/PRD173/PRD173_EDIT.html',
	        		className: 'PRD173',
	        		controller: ['$scope',function($scope){
	        			$scope.title_type='Update';
	        			$scope.row_data = row;
	        		}]
	        	});
	        	dialog.closePromise.then(function(data){
	        		$scope.queryData();
	        	});
        		break;
			case 'D':
				var txtMsg = $filter('i18n')('ehl_02_common_001');
				$scope.inputVO.FXD_KEYNO = row.FXD_KEYNO
	        	$confirm({text: txtMsg},{size: 'sm'}).then(function(){
	               	$scope.sendRecv("PRD173","deleteData","com.systex.jbranch.app.server.fps.prd173.PRD173InputVO",
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
        		template: 'assets/txn/PRD173/PRD173_EDIT.html',
        		className: 'PRD173',
        		controller: ['$scope',function($scope){
        			$scope.title_type='Add';
        		}]
        	});
        	dialog.closePromise.then(function(data){
        		$scope.queryData();
        	});

        	
        }

        
	}     
);