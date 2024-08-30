/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS120Controller',
    function($rootScope, $scope, $controller, $confirm, $filter, $timeout, sysInfoService, socketService,
        ngDialog, projInfoService, getParameter) {

        $controller('BaseController', {
            $scope: $scope
        });
        // 繼承共用的組織連動選單
        $controller('PMSRegionController', {
            $scope: $scope
        });
        $scope.controllerName = "PMS120Controller";
        
        var NowDate = new Date();
		var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth() + (13-NowDate.getMonth());
        var strmm = '';
        $scope.mappingSet['timeE'] = [];

        debugger
        for(var i = 0; i < 36; i++){
            debugger
        	mm = mm - 1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr - 1;
        	}
        	if(mm < 10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr + '/' + strmm,
        		DATA : yr + '' + strmm
        	});        
        }
        
        /***取得年月****/
        $scope.initLoad = function(){
            $scope.sendRecv("PMS000", "getSalesPlanYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
    				function(tota, isError) {
				if (!isError) {
					
					$scope.ymList = tota[0].body.ymList;
					console.log($scope.ymList);
					
					if($scope.ymList != null && $scope.ymList.length > 0){
					   $scope.inputVO.dataMonth = tota[0].body.currentYM;
					   $scope.currentYM = tota[0].body.currentYM;
					   angular.forEach($scope.ymList, function(row, index, objs){
							if(row.DATA>=$scope.inputVO.dataMonth)
								$scope.ymList.splice(0,1)
						});	
					   $scope.inputVO.psFlag = "Y";
					   $scope.inputVO.reportDate = $scope.inputVO.dataMonth;
					   $scope.RegionController_getORG($scope.inputVO).then(function() {
		        			if($scope.ao_code) $scope.inquire(); //載入頁面如果是理專單一code自動發查
					   });
			        }
				}
		    });
    	};
    	//$scope.initLoad();
    	
        //選取下拉選單 --> 設定可視範圍
        $scope.dateChange = function() {
            if ($scope.inputVO.dataMonth) {
            	$scope.inputVO.psFlag = "Y";
                $scope.inputVO.reportDate = $scope.inputVO.dataMonth;
                $scope.RegionController_getORG($scope.inputVO);
            }
        };

        /***初始化***/
        $scope.init = function() {
        	$scope.dataList =[];
        	$scope.dataList2 =[];
        	$scope.dataList3 =[];
        	$scope.dataList4 =[];
        	$scope.dataList5 =[];
        	$scope.dataList6 =[];
            $scope.PLAN_AMT = 0;
            $scope.EST_AMT = 0;
            $scope.EST_EARNINGS = 0;
            //輸入值初始化
            $scope.inputVO = {
                aoFlag: 'N',
                psFlag: 'Y',  //消金ps
                dataMonth:'', //年月
                branch_area_id:'', //營運區
                branch_nbr : '',  //分行
                emp_id:'',   //員編                
                psFlagType:''
            };
            if(sysInfoService.getPriID()=='004')
				$scope.inputVO.psFlagType='7';
            
//            $scope.inputVO.dataMonth = undefined;
            if ($scope.ymList != null && $scope.ymList.length > 0) {
                $scope.inputVO.dataMonth = $scope.currentYM;
            }
            $scope.dateChange();
        };
        $scope.init();




        /****查詢****/
        $scope.inquire = function() {
            if ($scope.parameterTypeEditForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
                return;
            }
            $scope.paramList = [];
            $scope.PLAN_AMT = 0;
            $scope.EST_AMT = 0;
            $scope.EST_EARNINGS = 0;
            $scope.sendRecv("PMS120", "query", "com.systex.jbranch.app.server.fps.pms120.PMS120InputVO", $scope.inputVO,
                function(tota, isError) {
                    if (!isError) {
                    	$scope.dataList =tota[0].body.dataList;
                    	$scope.dataList2 =tota[0].body.dataList2;
                    	$scope.dataList3 =tota[0].body.dataList3;
                    	$scope.dataList4 =tota[0].body.dataList4;
                    	$scope.dataList5 =tota[0].body.dataList5;
                    	$scope.dataList6 =tota[0].body.dataList6;
                        $scope.outputVO = tota[0].body;

                    }
                    //refresh 本月銷售目標  	&&預估達成率  
//                    $scope.targetSale();
                });

        }

    });