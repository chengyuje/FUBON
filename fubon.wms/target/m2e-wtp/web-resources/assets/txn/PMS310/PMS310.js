/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS310Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS310Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.mappingSet['tarType'] = [];
		$scope.mappingSet['tarType'].push({LABEL:'PS業績目標', DATA:'1'},{LABEL:'分行業績目標', DATA:'2'});
		
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		
		$scope.tarTypeChange = function(){
//			$scope.paramList={};
//			$scope.outputVO={};
			$scope.inputVO.branch_nbr='';
			$scope.inputVO.emp_id='';
		}
		
		$scope.dateChange = function(){
	        if($scope.inputVO.sCreDate!=''){ 	
	        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };
        
		
		$scope.toJsDate = new Date();
		var nowDt = new Date(); 
		var yyyy =	nowDt.getFullYear();
		var mm = nowDt.getMonth() + 1;
		var currDate = "";
		if(mm<10)
			currDate = yyyy + "0" + mm;
		else
			currDate = yyyy +""+ mm;
		
		$scope.init = function(){
			
			$scope.inputVO = {										
					reportDate: currDate,
					sCreDate:currDate,
					tarType: '' ,
					branch_nbr: '',
					emp_id: '',
					aoFlag           :'N',
					psFlag           :'Y'
				
        	};
			$scope.paramList = [];	
			$scope.outputVO = {totalPage:0,totalRecord:0,currentPageIndex:0};	
//			outputVO.setTotalPage(totalPage_i);// 總頁次
//			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			$scope.dateChange();
			$scope.type='';
		};
		$scope.init();
		$scope.inquireInit = function(){
//			$scope.initLimit();
//			$scope.paramList = [];		
			$scope.outputVO = {totalPage:0,totalRecord:0};	
			$scope.querySeen();
		}
//		$scope.inquireInit();	
		
		$scope.querySeen = function(){
			if($scope.inputVO.reportDate.length==6){
				$scope.RegionController_getORG($scope.inputVO);
			}    		
		}
		
		
		// 月份初始化
		var NowDate = new Date();
		
		var yr = NowDate.getFullYear();
	    var mm = NowDate.getMonth()+2;
	    var strmm='';
	    $scope.mappingSet['timeE'] = [];
	    for(var i=0; i<36; i++){
	    	mm = mm -1;
	    	if(mm == 0){
	    		mm = 12;
	    		yr = yr-1;
	    	}
	    	if(mm<10)
	    		strmm = '0' + mm;
	    	else
	    		strmm = mm;        		
	    	$scope.mappingSet['timeE'].push({
	    		LABEL: yr+'/'+strmm,
	    		DATA: yr +''+ strmm
	    	});        
	    }

		
        /**查詢資料**/
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請輸入/選擇必填欄位＊');
        		return;
        	}

			if($scope.inputVO.reportDate.length!=6){
				$scope.showErrorMsg('欄位檢核錯誤:日期輸入長度應為6碼');
        		return;
			}
			$scope.inputVO.types='1';	 //查詢用途
			
			if($scope.inputVO.tarType == '1')
				$scope.type = 'PS';
			else
				$scope.type = '分行';
			
			$scope.sendRecv("PMS310", "queryData", "com.systex.jbranch.app.server.fps.pms310.PMS310InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.today = new Date();
								$scope.paramList =[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}		
							
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;	
//							$scope.showSuccessMsg('查詢成功');
							return;
						}						
			});
		};
		
		/**上傳目標**/
		$scope.upload = function(input){
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS310/PMS310_EDIT.html',
				className: 'PMS310_EDIT',					
                controller: ['$scope', function($scope) {
                	$scope.input = input;
                }]
            });  
		}
});
