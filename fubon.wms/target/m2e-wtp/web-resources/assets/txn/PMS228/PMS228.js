/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS228Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS228Controller";
		
		var NowDate = new Date();
	    //修正前人BUG 問題單號:0004098  BY 20180102-Willis
		NowDate.setDate(1);
	    NowDate.setMonth(NowDate.getMonth()+1); 
	    var strMon='';
	    $scope.mappingSet['timeE'] = [];
	    //資料日期一區間限制為過去一年加下個月
	    for(var i=0; i<13; i++){
	    	
	    	strMon = NowDate.getMonth()+1;
	    	//10月以下做文字處理，+0在前面
	    	if(strMon < 10 ){
	    		strMon = '0'+strMon;
	    	}
	    	
	    	$scope.mappingSet['timeE'].push({
	    		LABEL: NowDate.getFullYear()+'/'+strMon,
	    		DATA: NowDate.getFullYear() +''+ strMon
	    	}); 
	    	//每一筆減一個月，倒回去取年內日期區間
	    	NowDate.setMonth(NowDate.getMonth()-1);
	    }		
		
		
		//***目前提供測試用設定年月***//
		//***測試用設定年月END***//
/*		
		var nowDt = new Date();
		var yyyy = nowDt.getFullYear();
		var mm = nowDt.getMonth()+(13-nowDt.getMonth());        
		var currDate='';
		$scope.mappingSet['timeE'] = [];
		for(var i=0; i<10; i++){
			mm = mm -1;
			if(mm == 0){
				mm = 12;
				yyyy = yyyy-1;
			}
			if(mm<10)
				currDate = '0' + mm;
			else
				currDate = mm;        		
			$scope.mappingSet['timeE'].push({
				LABEL: yyyy+'/'+currDate,
				DATA: yyyy +''+ currDate
			});        
		}
*/		
		
		
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};				
		
		$scope.init = function(){
			$scope.inputVO = {
					reportDate: '',
					rc_id: '',
					op_id: '' ,
					branch_nbr: '',
					ao_code: ''					
        	};
			$scope.inputVO.reportDate = $filter('date')(new Date(), 'yyyyMM');
			$scope.rsDate = '';
			$scope.curDate = new Date();
			$scope.paramList = [];
		};
	
		$scope.init();
	
		
		
		
		
		/**查詢資料**/
		$scope.queryDatas = function(){
			//表格TITLE顯示查詢年月
			$scope.sendRecv("PMS228", "queryData", "com.systex.jbranch.app.server.fps.pms228.PMS228InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.totalData = tota[0].body.totalList;
							$scope.outputVO = tota[0].body;
						
							return;
						}						
			});
		};
		
		/**匯出EXCEL**/
		$scope.exportRPT = function(){			
			$scope.sendRecv("PMS228", "export", "com.systex.jbranch.app.server.fps.pms228.PMS228OutputVO", $scope.outputVO,
					function(tota, isError) {						
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);		            		
		            	}
						if (tota.length > 0) {
	                		if(tota[0].body.resultList && tota[0].body.resultList.length == 0) {
	                			$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
	                	};
			});
		};
		

		/**download sample files**/
		$scope.downloadSample = function() {	
        	$scope.sendRecv("PMS228", "downloadSample", "com.systex.jbranch.app.server.fps.pms228.PMS228InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							return;
						}
			});
		};
	
        
        
        /**資料整批匯入上傳**/
		$scope.upload = function(){
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS228/PMS228_UPLOAD.html',
				className: 'PMS228_UPLOAD',				
                controller: ['$scope', function($scope) {                
                }]
            });
		};
               
		
});
