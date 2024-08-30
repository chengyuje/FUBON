/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS338Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS338Controller";
		$scope.init = function()
		{
			$scope.inputVO = 
			{	
					sTime: ''
        	};
			$scope.flag = '';
		};
	   $scope.init();
	  // $scope.curDate = new Date();
	   $scope.isq = function(){
			var NowDate = new Date();
			var yr = NowDate.getFullYear();
			$scope.mappingSet['timeE'] = [];
			for (var i = 0; i < 12; i++) {
				$scope.mappingSet['timeE'].push({
					LABEL : yr ,
					DATA : yr
				});
				yr = yr - 1; 
			} 
		};
    	$scope.isq();
		$scope.inquireInit = function()
		{
			$scope.initLimit();
			$scope.paramList = [];	
		}
		$scope.limitDate = function() 
		{
			if($scope.inputVO.sTime != ''){
				$scope.yearNow = $scope.inputVO.sTime;
			}else 
			{
				$scope.paramList = [];
			}
			
		};
		/** 明細窗口彈出 **/
    	$scope.upload = function(mm,type)
    	{
    		if($scope.parameterTypeEditForm.$invalid)
    		{
        		$scope.showErrorMsg('請選擇正確上的年月');
        		return;
        	}
    		var sTime = $scope.inputVO.sTime;
    		if(mm < 10) {
    			mm = "0" + mm;
    		}
    		var yearMon = ""+sTime+mm;
    		var dialog = ngDialog.open({
    			template: 'assets/txn/PMS338/PMS338_DETAIL.html',
    			className: 'PMS338_DETAIL',
    			showClose: false,
    			controller: ['$scope', function($scope) {
    				$scope.loanType = type,
    				$scope.yearMon = yearMon;
                }]
    		});
    	}
		/**
		 * 匯出
		 */
    	$scope.exportRPT = function()
    	{
    		$scope.outputVO.type = $scope.flag;
			$scope.sendRecv("PMS338", "export", "com.systex.jbranch.app.server.fps.pms338.PMS338OutputVO", $scope.outputVO,
					function(tota, isError) 
					{						
						if (isError) 
						{
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) 
						{
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) 
	                		{
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};		
		/**
		 * 查詢
		 */
		$scope.inquire = function()
		{
			if($scope.parameterTypeEditForm.$invalid)
			{
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS338", "inquire", "com.systex.jbranch.app.server.fps.pms338.PMS338InputVO", $scope.inputVO,
					function(tota, isError) 
					{
						if (!isError) 
						{
							if(tota[0].body.resultList.length == 0) 
							{
								$scope.showMsg("ehl_01_common_009");
								$scope.curDate = '';
	                			return;
	                		}														
							$scope.paramList = tota[0].body.resultList;
							$scope.l = $scope.paramList.length;
							$scope.outputVO = tota[0].body;
							$scope.outputVO.type = $scope.flag;
							$scope.curDate = new Date($scope.paramList[0].CREATETIME);
							return;
						}						
			});
		};
});
