/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('PMS204InsertController', 
    function($scope, $controller, socketService, alerts, projInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PMS204InsertController";
        
        
        /***年月***/
        var NowDate=new Date();    
        var t=NowDate.setMonth(NowDate.getMonth()-12);           
  
        var susdate=new Date(t);
        var y=susdate.getFullYear();
        var m=susdate.getMonth()+1;
        var xm='';
        $scope.mappingSet['timeM'] = [];
        for(var i=0;i<13;i++){
        	if(m<=9){xm='0'+m;}
        	if(m>=10){xm=m;}            	
        		$scope.mappingSet['timeM'].push({
					LABEL : y+'/'+xm,
					DATA : y+''+xm
        		});            		
        		if(m<=11){	
        			m=m+1;               		
        		}
        		if(m==12){
        			m=1;
        			y=y+1;            		
        		}
         }
        /***年月END***/
        $scope.mappingSet['timeM'].reverse();
        
        var vo = {'param_type': 'PMS.COACHING_POINT', 'desc': false};
        $scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['PMS.COACHING_POINT'] = totas[0].body.result;
        			$scope.mappingSet['PMS.COACHING_POINT'] = projInfoService.mappingSet['PMS.COACHING_POINT'];
        		}
        });
    
        	/****初始化資料*****/
        $scope.init = function(){
        	var NowDate=new Date();
        	var y=NowDate.getFullYear();
            var m=NowDate.getMonth()+1;
            var xm='';
            for(var i=0;i<13;i++){
            	if(m<=9){xm='0'+m;}
            	if(m>=10){xm=m;}            	
            	 $scope.nowDate = y+''+xm;
            		};
            		
            $scope.row = $scope.row || {};
        	$scope.inputVO = {
        			/****inputvo***/
        			SEQ:$scope.row.SEQ,
        			MTD_ACH_RATE_S:'',
        			MTD_ACH_RATE_E:'',
        			JOB_TITLE_ID:'',
        			APOINT:'',
        			BPOINT:'',
        			CPOINT:'',
        			DPOINT:'',
        			EMP_ID:'',
        			COACHING_STATE:'',
        			EMP_NAME:'',
        			ROLE_ID:'',
        			PLAN_YEARMON: $scope.nowDate || '',
            };
        };
        $scope.init();
 
        
        /*****新增修改insert or update******/
        $scope.save = function(){        	
        	if($scope.parameterTypeEditForm.$invalid){
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:(*)必輸入欄位必須輸入!!');
        		return;
        	}
        	if($scope.inputVO.APOINT =='' && $scope.inputVO.BPOINT =='' && 
        			$scope.inputVO.CPOINT =='' && $scope.inputVO.DPOINT ==''){
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:請勾選 「Coaching重點」！');
        		return;
        	}
        	
        	$scope.sendRecv("PMS204", "addModify", "com.systex.jbranch.app.server.fps.pms204.PMS204InputVO", $scope.inputVO,
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
        
    
});
