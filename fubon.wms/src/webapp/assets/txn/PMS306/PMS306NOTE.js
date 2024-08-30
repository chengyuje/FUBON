/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS306NOTEController',
	function($rootScope, $scope, $controller, $confirm, $compile, sysInfoService,socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS306NOTEController";
			
		
		// filter
		$scope.init = function(){
			$scope.inputVO = {
				SDate:undefined,
				sttType:'note',
				NOTE:'',
				pk: '1'
        				
        	};
			$scope.paramList = [];
			
		};
        $scope.init();

        
        /******時間控制******/

    	// date picker
    	$scope.ivgStartDateOptions = {
    		maxDate: $scope.maxDate,
    		minDate: $scope.minDate
    	};
    	$scope.ivgEndDateOptions = {
    		maxDate: $scope.maxDate,
    		minDate: $scope.minDate
    	};
    	// config
    	$scope.model = {};
    	
    	$scope.open = function($event, elementOpened) {		
    		$scope.model[elementOpened] = !$scope.model[elementOpened];
    	};
    	$scope.limitDate = function() {
    		$scope.ivgStartDateOptions.maxDate = $scope.inputVO.EDate || $scope.maxDate;
    		$scope.ivgEndDateOptions.minDate = $scope.inputVO.SDate || $scope.minDate;
    	};
    	// date picker end
    	
    	$scope.showType = function(){
    		if($scope.inputVO.sttType == "discount"){
    			$scope.queryDis();
    		}else{
    			$scope.inquire();
    		}
    	}
   
    	$scope.queryDis = function(){
    		$scope.sendRecv("PMS306", "queryDiscount", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO,
					function(tota, isError){
    					if (!isError) {							
    							if(tota[0].body.Discountlist == 0) {
    								$scope.showMsg("ehl_01_common_009");								
    								return;
    						}
    						$scope.inputVO.DISCOUNTSALE = tota[0].body.Discountlist[0].PARAM_NAME_EDIT;
    						$scope.inputVO.DISCOUNT = tota[0].body.Discountlist[1].PARAM_NAME_EDIT;
    						$scope.inputVO.PARAM_ORDER = tota[0].body.Discountlist[0].PARAM_ORDER;
    						return;
    					}
    		});
    	};
    	
    	$scope.saveDiscount = function(){
			
    		if($scope.inputVO.UpDate == undefined){
    			$scope.showErrorMsgInDialog('欄位檢核錯誤:請輸入修改日期');
				return;
    		}
    		$scope.sendRecv("PMS306", "saveDiscount", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO",$scope.inputVO,
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            		return;
		            	}
		            	if (tota.length > 0) {
		            		$scope.showMsg('儲存成功');
		            		$scope.queryDis();
		            		$scope.inputVO.UpDate = '';
		            	};
			});
    	};
    	
    
  
        //開始SELECT 註解結果
        $scope.inquire = function(){
			$scope.sendRecv("PMS306", "noteText", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {							
							if(tota[0].body.notelist == 0) {
								$scope.inputVO.NOTE="";
								$scope.showMsg("ehl_01_common_009");								
	                			return;
	                		}
							$scope.inputVO.NOTE = tota[0].body.notelist[0].REMARK+"";
										
							return;
						}
			});
		};
		 $scope.inquire();
		 
		 
		   $scope.save = function(){
				$scope.sendRecv("PMS306", "noteTextUp", "com.systex.jbranch.app.server.fps.pms306.PMS306InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {		
								$scope.showMsg("儲存成功");
							}else{								
								alert("error");
							}
				});
			};
			
		 
		 
		 
	
       
		
});
