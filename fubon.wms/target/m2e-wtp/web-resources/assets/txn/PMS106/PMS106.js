/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS106Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter,sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		
		$scope.controllerName = "PMS106Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		$scope.dateChange = function(){
	        if($scope.inputVO.sCreDate!=''){ 	
	        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
	        	$scope.RegionController_getORG($scope.inputVO);
	        }
	    };
	    
	  
	    var NowDate = new Date();
	    //每月1日產出次月統計數字，問題單號:0003899  BY 20171121-Willis
	    NowDate.setMonth(NowDate.getMonth()+1); 
	    var strMon='';
	    $scope.mappingSet['timeE'] = [];
	    //資料日期區間限制為半年內資料
	  //#0000375: 報表留存時間 四個月
	    for(var i=0; i<=4; i++){
	    	
	    	strMon = NowDate.getMonth()+1;
	    	//10月以下做文字處理，+0在前面
	    	if(strMon < 10 ){
	    		strMon = '0'+strMon;
	    	}
	    	
	    	$scope.mappingSet['timeE'].push({
	    		LABEL: NowDate.getFullYear()+'/'+strMon,
	    		DATA: NowDate.getFullYear() +''+ strMon
	    	}); 
	    	//每一筆減一個月，倒回去取前六個月內日期區間
	    	NowDate.setMonth(NowDate.getMonth()-1);
	    }
      	
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		
		$scope.curDate = new Date();
		
		$scope.init = function(){
			$scope.inputVO = {
					region_center_id: '',
					branch_area_id: '' ,
					branch_nbr: '',
					ao_code: '',
					srchType: '1',
					dataMonth: '',					
					rc_id: '',
					op_id: '' ,
					br_id: ''															
        	};			
			$scope.paramList = [];
			$scope.inputVO.sCreDate = '';
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			if(sysInfoService.getPriID() == '001' || sysInfoService.getPriID() == '002' || sysInfoService.getPriID() == '003')
			{
			    $scope.inputVO.aoflag = true;  //理專隱藏分行資訊
			    $scope.inputVO.srchType= '2';
			}
			else
			{
				$scope.inputVO.aoflag = false;  //理專隱藏分行資訊
				 $scope.inputVO.srchType= '1';
			}
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];	
			$scope.paramList2 = [];
		}
		$scope.inquireInit();	    
        
        $scope.changeType = function(){
        	$scope.paramList = [];
        	$scope.paramList2 = [];
        	$scope.ya = undefined;
			$scope.ya1 = undefined;
        }
        
        $scope.showDetail = function(row, type , srchType){
        	var templ, clzName = '';
        	if(type == 'FCD' ){
        		templ = 'assets/txn/PMS106/PMS106_FCD_DETAIL.html';
        		clzName = 'PMS106_FCD_DETAIL';
        	}
        	else if(type == 'BOND' ){
        		templ = 'assets/txn/PMS106/PMS106_BOND_DETAIL.html';
        		clzName = 'PMS106_BOND_DETAIL';
        	}
        	else if(type == 'NEWINS' ){
        		templ = 'assets/txn/PMS106/PMS106_NEWINS_DETAIL.html';
        		clzName = 'PMS106_NEWINS_DETAIL';
        	}
        	else if(type == 'ACUMINS' ){
        		templ = 'assets/txn/PMS106/PMS106_ACUMINS_DETAIL.html';
        		clzName = 'PMS106_ACUMINS_DETAIL';
        	}else if (srchType == '1'){
        		templ = 'assets/txn/PMS106/PMS106_BRAN_DETAIL.html';
        		clzName = 'PMS106_BRAN_DETAIL';
        	}else if (type == 'GI'){
        		templ = 'assets/txn/PMS106/PMS106_GI.html';
        		clzName = 'PMS106_GI';
        	}
        	else
        		return;
        	
        	var dialog = ngDialog.open({
				template: templ,
				className: clzName,					
                controller: ['$scope', function($scope) {
                	$scope.type = type;
                	$scope.row = row;
                }]
            });
        };
        
        //***合計function***//
		$scope.getSum = function(group, key) {
            var sum = 0;
            for (var i = 0; i < group.length; i++){
             sum += group[i][key];
            }  
            return sum;
		}
        
        /*** 查詢資料 ***/
		$scope.query = function(){
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:報表月份為必填欄位');
        		return;
        	}
			$scope.sendRecv("PMS106", "queryData", "com.systex.jbranch.app.server.fps.pms106.PMS106InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}								
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;	
							
							//取得分頁數值   用來做分頁
							$scope.ya = tota[0].body.currentPageIndex;
							$scope.ya1 = tota[0].body.totalPage;
							//拿總計
							//計算最後頁總計   2017/05/18
							if($scope.ya==$scope.ya1-1){
								$scope.sendRecv("PMS106", "inquire2", "com.systex.jbranch.app.server.fps.pms106.PMS106InputVO",$scope.inputVO ,
										function(tota, isError) {
											if (!isError) {
												$scope.paramList2 = tota[0].body.resultList2;												
											}
								});
							}
							return;
						}else{
							$scope.showBtn = 'none';
						}						
			});
		};
});
