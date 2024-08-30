/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('PMS204EditController', 
    function($scope, $controller, socketService, alerts, projInfoService,getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PMS204EditController";
    
        
        
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
        
        
      //分行資訊
		$scope.genBranch = function() {
			$scope.mappingSet['branchsDesc'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
				if(row.AreaCode == $scope.inputVO.cmbArea)
					$scope.mappingSet['branchsDesc'].push({LABEL: row.BranchName, DATA: row.BranchNbr});
			});
        };
        //EMP資訊
        $scope.genEmp = function() {
			$scope.sendRecv("CMORG104", "getBranchEmp", "com.systex.jbranch.app.server.fps.cmorg104.CMORG104EMPVO", {'cmbArea': $scope.inputVO.cmbArea,'cmbBranch': $scope.inputVO.cmbBranch},
    				function(totas, isError) {
                    	if (isError) {
                    		$scope.showErrorMsg(totas[0].body.msgData);
                    	}
                    	if (totas.length > 0) {
                    		$scope.mappingSet['empsDesc'] = [];
                    		angular.forEach(totas[0].body.agent, function(row, index, objs){
                    			$scope.mappingSet['empsDesc'].push({LABEL: row.EMP_NAME, DATA: row.EMP_ID});
                    		});
                    	};
    				}
    		);
        };
        
        
        	
    		getParameter.XML(["PMS.COACHING_POINT"], function(totas) {
    			if (totas) {
    				$scope.mappingSet['PMS.COACHING_POINT'] = totas.data[totas.key.indexOf('PMS.COACHING_POINT')];
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
        			
        			PLAN_YEARMON:$scope.nowDate,
        			OLD_PLAN_YEARMON:$scope.row.YEARMON,
        			
        			SEQ:$scope.row.SEQ,
        			MTD_ACH_RATE_SO:$scope.row.MTD_ACH_RATE_S,
        			MTD_ACH_RATE_EO:$scope.row.MTD_ACH_RATE_E,
        			MTD_ACH_RATE_S: $scope.row.MTD_ACH_RATE_S,
        			MTD_ACH_RATE_E:$scope.row.MTD_ACH_RATE_E,
        			
        			OLD_MTD_ACH_RATE_SO:$scope.row.MTD_ACH_RATE_S,
        			OLD_MTD_ACH_RATE_EO:$scope.row.MTD_ACH_RATE_E,
        			
        			JOB_TITLE_ID:$scope.row.JOB_TITLE_ID,
        			OLD_JOB_TITLE_ID:$scope.row.JOB_TITLE_ID,
        			
        			APOINT:$scope.row.COACHING_POINT_A,
        			BPOINT:$scope.row.COACHING_POINT_B,
        			CPOINT:$scope.row.COACHING_POINT_C,
        			DPOINT:$scope.row.COACHING_POINT_D,
        			COACHING_STATE:$scope.row.COACHING_FREQ,
        			ROLE_ID:''
            	
            };
        	
        };
        $scope.init();
        
        $scope.copy_check = function(){
			if($scope.inputVO.ADD_MONTH){
				var nYEAR =($scope.row.YEARMON).substring(0,4);
				var nMonth = ($scope.row.YEARMON).substring(4,6);
				var xm='';
				if( nMonth < 12 ){
					nMonth++;
				}
				if( nMonth == 12 ){
					nMonth = 1;
					nYEAR = nYEAR + 1;
				}
				if(nMonth<=9){
					xm='0'+nMonth;
				}else{
					xm = nMonth;
				}
				$scope.strYrMn = ($scope.row.YEARMON).substring(0,4)+''+xm;
				$scope.inputVO.PLAN_YEARMON = $scope.strYrMn.trim();
			   	$scope.disabled_else = true;
				$scope.disabled_required = false;
			}else{
				$scope.disabled_else = false;
				$scope.disabled_required = true;
			}
		}
        
        /*****新增修改insert or update******/
        $scope.save = function(){
        	if($scope.inputVO.APOINT =='' && $scope.inputVO.BPOINT =='' && 
        			$scope.inputVO.CPOINT =='' && $scope.inputVO.DPOINT ==''){
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:請勾選 「Coaching重點」！');
        		return;
        	}
        	if($scope.parameterTypeEditForm.$invalid){
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:請選擇「Coaching頻次」！');
        		return;
        	}
       
  
       $scope.sendRecv("PMS204", "ddlModify", "com.systex.jbranch.app.server.fps.pms204.PMS204InputVO", $scope.inputVO,
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
