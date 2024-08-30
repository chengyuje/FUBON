/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS717Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS717Controller";	
		
		
		$scope.init = function()
        {
    		var NowDate = new Date();
    		var yr = NowDate.getFullYear();
    		var mm = NowDate.getMonth() + 1;
    		var strmm = '';
    		var xm = '';
    		$scope.mappingSet['timeE'] = [];
    		for (var i = 0; i < 12; i++)
    		{
    			mm = mm - 1;
    			if (mm == 0) {
    				mm = 12;
    				yr = yr - 1;
    			}
    			if (mm < 10)
    				strmm = '0' + mm;
    			else
    				strmm = mm;
    			$scope.mappingSet['timeE'].push({
    				LABEL : yr + '/' + strmm,
    				DATA : yr + '' + strmm
    			});
    		}; 
    		//主檔類型
    		$scope.mappingSet['prodType'] = [];
    		$scope.mappingSet['prodType'].push(
    				{LABEL: '基金'	, DATA: '1'},
    				{LABEL: 'SI'	, DATA: '2'},
    				{LABEL: '海外債'	, DATA: '3'},
    				{LABEL: 'SN'	, DATA: '4'},
    				{LABEL: '海外ETF'	, DATA: '5'},
    				{LABEL: '海外股票'	, DATA: '6'},
    				{LABEL: '保險'	, DATA: '7'}
    				);
    		$scope.csvList = [];
    		/***給定初始值***/
    		$scope.inputVO={
    				yearMon:'',    //資料年月
    				prodType:'',   //主檔類型
    				prodId: ''     //產品ID
    		}
    	};
    	$scope.init();
    	$scope.changeQuery = function(){
    		$scope.inputVO.prodId = '';
    		$scope.showList = [];
			$scope.csvList = [];
    	}
    	
    	/**
		 * 查詢
		 */
		$scope.inquire = function()
		{
			if($scope.parameterTypeEditForm.$invalid)
			{
	    		$scope.showMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS717", "queryData", "com.systex.jbranch.app.server.fps.pms717.PMS717InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) 
						{
							$scope.showList = [];
							if(tota[0].body.resultList.length == 0) 
							{
								$scope.showMsg("ehl_01_common_009");	//
	                			return;
	                		}
							$scope.showList = tota[0].body.resultList;
							$scope.csvList = tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							return;
						}else
						{
							$scope.showErrorMsg("ehl_01_common_024");	//執行失敗
						}	
			});
		};
    	
    	/** 上傳窗口彈出 **/
    	$scope.upload = function()
    	{
    		if($scope.parameterTypeEditForm.$invalid)
    		{
        		$scope.showErrorMsg('請選擇正確上傳檔案條件');
        		return;
        	}
    		var yearMon = $scope.inputVO.yearMon;
    		var prodType = $scope.inputVO.prodType;
    		var dialog = ngDialog.open({
    			template: 'assets/txn/PMS717/PMS717_DETAIL.html',
    			className: 'PMS717_DETAIL',
    			showClose: false,
    			controller: ['$scope', function($scope) {
    				$scope.yearMon = yearMon;
    				$scope.prodType = prodType;
                }]
    		});
    		/**
    		  關閉子界面時，刷新主界面
    		*/
        	dialog.closePromise.then(function(data) {
        		if(data.value == 'cancel1'){
        			 $scope.inquire();
    				}
        	});
    	}
    	/*
		 *匯出
		 */
    	$scope.exportRPT = function()
    	{
    		console.log('717-3');
    		if($scope.parameterTypeEditForm.$invalid)
    		{
        		$scope.showErrorMsg('請選擇正確匯出檔案條件');
        		return;
        	}
    		$scope.outputVO.prodType = $scope.inputVO.prodType;
    		
			$scope.sendRecv("PMS717", "export", "com.systex.jbranch.app.server.fps.pms717.PMS717OutputVO", $scope.outputVO,
					function(tota, isError) 
					{						
						if (isError) 
						{
		            		$scope.showErrorMsg("ehl_01_common_024");		//執行失敗            		
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
		
});