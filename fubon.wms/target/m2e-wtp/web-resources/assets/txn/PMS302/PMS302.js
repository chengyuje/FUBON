/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS302Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS302Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		
		$scope.mappingSet['srchType'] = [];
        $scope.mappingSet['srchType'].push({LABEL:'當日', DATA:'NOW'}, {LABEL:'MTD月報', DATA:'MTD'});
        $scope.inputVO.srchType ='NOW';
        
        $scope.sendRecv("PMS302", "getYearMon", "com.systex.jbranch.app.server.fps.pms302.PMS302InputVO", {},
    		    function(totas, isError) {
    	             	if (totas.length > 0) {
    	             		$scope.ymList = totas[0].body.resultList;
    	             		debugger;
    	               	    //#0000375: 報表留存時間 四個月
							$scope.ymList.splice(4);
    	               	};
    		    }
        );	
        
//        var NowDate = new Date();
//	    var strMon='';
//	    NowDate.setMonth(NowDate.getMonth()-1); 
//	    $scope.mappingSet['timeE'] = [];
//	    //資料日期區間限制為半年內資料
//	    for(var i=1; i<=6; i++){
//	    	
//	    	strMon = NowDate.getMonth()+1;
//	    	//10月以下做文字處理，+0在前面
//	    	if(strMon < 10 ){
//	    		strMon = '0'+strMon;
//	    	}
//	    	
//	    	$scope.mappingSet['timeE'].push({
//	    		LABEL: NowDate.getFullYear()+'/'+strMon,
//	    		DATA: NowDate.getFullYear() +''+ strMon
//	    	}); 
//	    	//每一筆減一個月，倒回去取前六個月內日期區間
//	    	NowDate.setMonth(NowDate.getMonth()-1);
//	    }
        
 		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		
		$scope.curDate = new Date();
		$scope.init = function(){
			$scope.inputVO = {										
					sCreDate:new Date(),
					rc_id: '',
					op_id: '' ,
					br_id: '',
					emp_id: '',
					srchType: 'NOW'
        	};
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			$scope.cList='';
			$scope.flag = 'MAST';
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];	
			$scope.paramList2 = [];
		}
		$scope.inquireInit();	
		
		$scope.dateChange = function(){
			$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
			$scope.RegionController_getORG($scope.inputVO);
		};
		$scope.dateChange();
		// date picker
		$scope.bgn_sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.bgn_eDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
			};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.bgn_sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
			$scope.bgn_eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
		};
		// date picker end
		
		$scope.query = function(){
			$scope.flag = 'MAST';
			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇必填欄位');
        		return;
        	}

			$scope.sendRecv("PMS302", "queryData", "com.systex.jbranch.app.server.fps.pms302.PMS302InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}														
							$scope.paramList = tota[0].body.resultList;
							$scope.cList = tota[0].body.cList[0].CDATE;
						
//							for(var i = 0 ; i<$scope.paramList.length ; i++){
//								$scope.paramList[i].INS_ACT = $scope.paramList[i].INV_ACT + $scope.paramList[i].INSU_ACT;
//								$scope.paramList[i].INS_RATE = ($scope.paramList[i].INS_ACT/$scope.paramList[i].INS_TAR)*100;
//								if ($scope.paramList[i].INS_RATE == Infinity){
//									$scope.paramList[i].INS_RATE = 0;
//								}
//								$scope.paramList[i].D_INS_ACT = $scope.paramList[i].D_INV_ACT + $scope.paramList[i].D_INSU_ACT;
//								$scope.paramList[i].D_INS_RATE = ($scope.paramList[i].D_INS_ACT/$scope.paramList[i].INS_TAR)*100;
//								if ($scope.paramList[i].D_INS_RATE == Infinity){
//									$scope.paramList[i].D_INS_RATE = 0;
//								}
//								var mm = new Date($scope.paramList[i].CREATETIME);
//								if($scope.inputVO.sCreDate.getMonth()==mm.getMonth()){
//									$scope.paramList[i].D_INS_TAR = $scope.paramList[i].INS_TAR;
//								}
//							}
							$scope.outputVO = tota[0].body;
							$scope.outputVO.type = $scope.flag;
							return;
						}						
			});
		};
		
		$scope.exportRPT = function(){
			$scope.outputVO.type = $scope.flag;
			$scope.sendRecv("PMS302", "export", "com.systex.jbranch.app.server.fps.pms302.PMS302OutputVO", $scope.outputVO,
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
		$scope.bn=function(row) {
        	var date = $scope.inputVO.sCreDate;
        	var srchType = $scope.inputVO.srchType;
        	var tx_ym = $scope.inputVO.tx_ym;
        	var branch_nbr = row.BRA_NBR;
        	var ao_code = row.AO_CODE;
        	var dialog=ngDialog.open({
        	    template:'assets/txn/PMS302/PMS302_detail.html',
        	    className:'PMS302',     
        	    controller:['$scope',function($scope) {
        	    	$scope.sCreDate = date;
        	    	$scope.ao_code = ao_code;
        	    	$scope.srchType = srchType;
        	    	$scope.tx_ym = tx_ym;
        	    	$scope.branch_nbr = branch_nbr;
        	    }]        	 
        	});
        };
        
		$scope.queryDetail = function(row){	
			$scope.flag = 'DETAIL';
			$scope.sendRecv("PMS302", "queryDetail", "com.systex.jbranch.app.server.fps.pms302.PMS302InputVO", $scope.inputVO ,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}	
							$scope.paramList2 = tota[0].body.resultList;
							for(var i = 0 ; i < $scope.paramList2.length; i++){
								$scope.TOTAL_INVE = $scope.paramList2[i].MFD + $scope.paramList2[i].SI   +
													$scope.paramList2[i].SN  + $scope.paramList2[i].DCI  + 
													$scope.paramList2[i].BND + $scope.paramList2[i].STOCK+
													$scope.paramList2[i].EX_GAIN_LOSS;
								
								$scope.TOTAL_INS = $scope.paramList2[i].WHO_PAY   + $scope.paramList2[i].SHORT_YEAR +
												   $scope.paramList2[i].LONG_YEAR + $scope.paramList2[i].INVEST;
								$scope.paramList2[i].TOTAL_INVE = $scope.TOTAL_INVE;
								$scope.paramList2[i].TOTAL_INS  = $scope.TOTAL_INS ;
								$scope.paramList2[i].TOTAL_INSU = $scope.TOTAL_INVE+$scope.TOTAL_INS;
							}
							$scope.outputVO = tota[0].body;
							$scope.outputVO.type = $scope.flag;							
							return;
						}						
			});
		};
});