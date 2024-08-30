/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS330Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService,$filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS330Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		// filter
		$scope.dateChange = function(){
        	$scope.inputVO.reportDate = $filter('date')($scope.sCreDate,'yyyyMMdd');
        	$scope.RegionController_getORG($scope.inputVO); 
    };


    var date = new Date();
    var before2Years = new Date(date.getFullYear()-2, date.getMonth(), date.getDate());

    $scope.bgn_sDateOptions = {
    	minDate: before2Years
    };
    
    $scope.limitDate = function () {
        $scope.bgn_sDateOptions.minDate = before2Years;
    };
	
		$scope.init = function(){
			$scope.DATA_DATE = '';
			$scope.sCreDate= "";	
			$scope.inputVO = {
				
					DATA_DATE:'',
					sCreDate: undefined,					
					rc_id: '',
					op_id: '' ,
					br_id: '',					
					ao_code: ''
        	};
			
			$scope.paramList2 = [];
			$scope.lnow='';
			$scope.time='';
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
//			$scope.genRegion();
		};
		$scope.DATA_DATE = '';
        $scope.init();
        $scope.model = {};
    	$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
        
		//先取得正確的比較日，再做查詢
		$scope.getBusiday = function(){ 
			$scope.sendRecv("PMS330", "getBusiDay", "com.systex.jbranch.app.server.fps.pms330.PMS330InputVO", {'sCreDate':$scope.sCreDate},
					function(tota, isError) {
	    		if(!isError){    
	    			var BusiDay = tota[0].body.resultList[0].BUSIDAY; 	    			    
	    			$scope.inputVO.sCreDate =new Date(BusiDay);	    			
	    		}
	    		 $scope.inquire();
	    	});
		}
		
     
        //查詢
        $scope.inquire = function(){ 
			$scope.sendRecv("PMS330", "queryData", "com.systex.jbranch.app.server.fps.pms330.PMS330InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {							
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList2 = [];
								$scope.param=[];
								$scope.csvList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.time=$scope.inputVO.sCreDate;
							$scope.lnow = tota[0].body.resultList[0].DATA_DATE;							
							$scope.paramList2 = tota[0].body.resultList;
							$scope.inputVO.DATA_DATE= tota[0].body.resultList[0].DATA_DATE;
							//最近日:組字串
							$scope.DATA_DATE_YEAR = $scope.inputVO.DATA_DATE.substr(0,4);
							$scope.DATA_DATE_MONTH = $scope.inputVO.DATA_DATE.substr(4,2);
							$scope.DATA_DATE_DAY = $scope.inputVO.DATA_DATE.substr(6,2);
							$scope.DATA_DATE = $scope.DATA_DATE_YEAR + "/" + $scope.DATA_DATE_MONTH + "/" + $scope.DATA_DATE_DAY;
							
							//組產出日字串
							$scope.sendRecv("PMS330", "queryCDate", "com.systex.jbranch.app.server.fps.pms330.PMS330InputVO", $scope.inputVO,
									function(tota, isError) {
								//日期組字串
								if(tota[0].body.resultList[0].CREATDATE !=null){
									$scope.CREATE_DATE = tota[0].body.resultList[0].CREATDATE;				
									$scope.CREATE_DATE_YEAR = $scope.CREATE_DATE.substr(0,4);
									$scope.CREATE_DATE_MONTH = $scope.CREATE_DATE.substr(5,2);
									$scope.CREATE_DATE_DAY = $scope	.CREATE_DATE.substr(8,2);
									$scope.CREATE_DATE = $scope.CREATE_DATE_YEAR + "/" + $scope.CREATE_DATE_MONTH + "/" + $scope.CREATE_DATE_DAY;
								//該月沒建表才有可能發生的例外狀況
								}else{			
									$scope.CREATE_DATE ='';
								}	
							});	
	
							$scope.csvList=tota[0].body.csvList;						
							$scope.param=tota[0].body.DATA;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		
		$scope.export = function() {			
			$scope.sendRecv("PMS330", "export",
					"com.systex.jbranch.app.server.fps.pms330.PMS330OutputVO",
					{'csvList':$scope.csvList,'time':$scope.time}, function(tota, isError) {
						if (!isError) {
							return;
						}
					});
		};
	
    
    
		
});
