/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS309Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS309Controller";
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});

		var NowDate = new Date();
        var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth() + 1; 
        var nowYr = yr;
        var nowMm = mm;
        
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i < 13; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm < 10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr + '/' + strmm,
        		DATA : yr + ''  + strmm
        	});        
        }
        
        $scope.mappingSet['srchType'] = [];
        $scope.mappingSet['srchType'].push(
				{LABEL:'銷量', DATA:'SAL'}, {LABEL:'收益', DATA:'INC'}
			);
		
		$scope.open = function($event, index) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope['opened'+index] = true;
		};
		
		$scope.curDate = new Date();
		
		/***TEST ORG COMBOBOX START***/
		
        //選取日期下拉選單 --> 重新設定可視範圍
        $scope.dateChange = function(){
        	if($scope.inputVO.sCreDate != undefined){
        		$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
//            	$scope.RegionController_getORG($scope.inputVO);
        	}
        };

        //選取月份下拉選單 --> 重新設定可視範圍
        $scope.dataMonthChange = function () {

            if ($scope.inputVO.dataMonth_E != undefined && $scope.inputVO.dataMonth_S != undefined
                && $scope.inputVO.dataMonth_E != '' && $scope.inputVO.dataMonth_S != '') {

                if ($scope.inputVO.dataMonth_S > $scope.inputVO.dataMonth_E){
                    $scope.showErrorMsg('起月不可大於迄月');
                    $scope.inputVO.dataMonth_S = '';
                    $scope.inputVO.dataMonth_E = '';
                    return;
                }
                
                var s = $scope.inputVO.dataMonth_S.substring(0,4);
                var e = $scope.inputVO.dataMonth_E.substring(0,4);
                if(s != e){
                	$scope.showErrorMsg('起迄年份需相同');
                	$scope.inputVO.dataMonth_S = '';
                	$scope.inputVO.dataMonth_E = '';
                	return;
                }

                $scope.inputVO.reportDate = $scope.inputVO.dataMonth_E;
//                $scope.RegionController_getORG($scope.inputVO);
            }
        };
		
	    // date picker
			var date    = new Date();
			var preDate = new Date(date.getTime() - 24*60*60*1000);
//			var monthFirstDate = new Date(preDate.getFullYear(), preDate.getMonth(), 1);

			$scope.bgn_sDateOptions = {
				maxDate: preDate,
//				minDate: monthFirstDate
			};

			// config
			$scope.model = {};
			$scope.open = function($event, elementOpened) {
				$event.preventDefault();
				$event.stopPropagation();
				$scope.model[elementOpened] = !$scope.model[elementOpened];
			};

			$scope.limitDate = function () {
				$scope.bgn_sDateOptions.maxDate = preDate;
//				$scope.bgn_sDateOptions.minDate = monthFirstDate;
				
			};
		// date picker end

		/***ORG COMBOBOX END***/
		$scope.curDate = $scope.curDate.setDate($scope.curDate.getDate()-1);
		$scope.init = function(){
			$scope.inputVO = {
					srchDate: '1',
					sCreDate: preDate,
					dataMonth_S: '',
					dataMonth_E: '',
					srchType: 'SAL',
					rc_id: '',
					op_id: '' ,
					br_id: ''															
			};
			$scope.limitDate();
		    $scope.dateChange();
			$scope.paramList = [];	
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
			$scope.RegionController_getAllORG($scope.inputVO);

		};
		$scope.init();

		$scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];	
			$scope.regionCenterList = []; //處
			$scope.branchAreaList = [];	 //區
			$scope.totalList = [];
		}

		$scope.inquireInit();	
		
        $scope.sdType = function () {
            //依報表日期查詢
            if ($scope.inputVO.srchDate == '1') {
                $scope.inputVO.dataMonth_S = '';
                $scope.inputVO.dataMonth_E = '';
                $scope.inputVO.sCreDate = preDate;

                $scope.inputVO.income = '1';
            }
            //依計績月份累計
            else {
                $scope.inputVO.sCreDate = undefined;
                $scope.inputVO.income = 1;
                $scope.inputVO.dataMonth_S = $scope.mappingSet['timeE'][0].DATA;
                $scope.inputVO.dataMonth_E = $scope.mappingSet['timeE'][0].DATA;
                
            }
            $scope.paramList =[];
        }

	   $scope.lock = function () {
		if ($scope.inputVO.srchType == '')
			$scope.inputVO.srchType = 'SAL';
		}

        
		$scope.query = function(){

			if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:請選擇「查詢類別」');
        		return;
			}
			$scope.sendRecv("PMS309", "queryData", "com.systex.jbranch.app.server.fps.pms309.PMS309InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList =[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
							}
							
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO  = tota[0].body;
							$scope.outputVO.srchType = $scope.inputVO.srchType;
							return;
						}						
			});
		};
		
        $scope.numGroups = function (input) {
            if (input == null) {
                return;
            }

            return Object.keys(input).length;
        }
		
		
		$scope.Change=function(){
			if ($scope.inputVO.srchType == '')
                $scope.inputVO.srchType = 1;
		}     
		
		$scope.exportRPT = function(){
			$scope.sendRecv("PMS309", "export", "com.systex.jbranch.app.server.fps.pms309.PMS309OutputVO", $scope.outputVO,
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

        $scope.initLoad = function () {
            $scope.sendRecv("PMS000", "getLastYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
                function (totas, isError) {
                        if (totas.length > 0) {
                            $scope.ymList = [];
                            var month = new Date().getMonth() ;
                            var year  = new Date().getFullYear();
                            var ymListLength = totas[0].body.ymList.length;

                            $scope.ymList = totas[0].body.ymList;
                    };
                }
            );
		}
		
        $scope.initLoad();
});
