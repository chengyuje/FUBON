/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('PMS202UPDATEController', 
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PMS202UPDATEController";
        /****初始化資料*****/
        
        $scope.isCollapsed = false;
        
        var rptMin = new Date();
        rptMin.setDate(new Date().getDate())
		$scope.bgn_sDateOptions = {
			minDate: rptMin,
		};
        
        $scope.model = {};
    	$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.minDate = rptMin;
		};
//		alert(JSON.stringify($scope.row));
        $scope.init = function(){
        	
        	if($scope.row){
        		$scope.isUpdate = true
        	}
        	
            $scope.row = $scope.row || {};
            $scope.startMaxDate=[];
            $scope.endMinDate=[];
        	$scope.inputVO = {
        			/****inputvo***/
        			EMP_ID:$scope.row.EMP_ID,
        			EMP_NAME:$scope.row.EMP_NAME,
        			ROLE_ID:'',
        		
            		ao_code  :$scope.row.AO_CODE,
					branch  :'',
					region  :'',
					op      :'',
					aojob   :'',
					type    :'',
        			camId: '',
        			camName: '',
        			MAIN_COM_NBR: '',
        			REL_COM_NBR: '',
        			eTime:$scope.row.DATE_S,
        			datec:''
            };
        	
        };
        $scope.init();
        
        
    	$scope.bran = function(){
            $scope.sendRecv("PMS202", "aoCode", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", {},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.mappingSet['aocode'] = [];
                    		angular.forEach(totas[0].body.aolist, function(row, index, objs){
                    			if(row.BRANCH_NBR==$scope.inputVO.branch)
                    			$scope.mappingSet['aocode'].push({LABEL: row.NAME, DATA: row.AO_CODE});
                			});
                    	};
    				}
    		);
        }
    	$scope.bran()
      
        
        /*****新增修改insert or update******/
        $scope.save = function(){
        	if( $scope.parameterTypeEditForm.$invalid){
        		console.log('form=' + JSON.stringify($scope.parameterTypeEditForm));
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:必要輸入欄位');
        		return;
        }
        
        
        $scope.sendRecv("PMS202", "ddlModify", "com.systex.jbranch.app.server.fps.pms202.PMS202InputVO", $scope.inputVO,
    			function(totas, isError) {
	                if (isError) {
	                	 $scope.showErrorMsgInDialog(totas.body.msgData);
	                     return;
	                }
	                 if (totas.length > 0) {
	                	 $scope.showMsg('儲存成功');
		       			 $scope.closeThisDialog('successful');
	                 };
	            }
        	);
        }
        
        
        
   








}
);
