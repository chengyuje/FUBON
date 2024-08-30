/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS233_GREATController', function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {	
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "FPS233_GREATController";
	
	$scope.init = function() {
		$scope.row = $scope.row || {};
		$scope.inputVO = {
			/**
			 * 潛力金流條件種類 含息報酬率 台幣庫存金額
			 */
			ROI1 : '', // 基金
			ROI2 : '', // ETF
			ROI3 : '', // SI/SN
			ROI4 : '', // 海外債
			AMT_TWD1 : '', // 基金
			AMT_TWD2 : '', // ETF
			AMT_TWD3 : '', // SI/SN
			AMT_TWD4 : '', // 海外債
			TYPE : '',
			ROI : '',
			AMT_TWD : '',
			TER_FEE_YEAR : '', // 險種年期
			INS_NBR : '', // 險種代號
			INS_NAME : '', // 險種名稱
			INS : '',//頁面險種代號
			YEAR : ''//頁面險種年期
		};
		$scope.List = []; // 存放新增修改的
		$scope.resultList = []; // 存放前四筆List
		$scope.paramList = []; // 存放最後List
		$scope.insList = [];//暫存保險資料List
	};
	$scope.init();
	
	//檢查是否大於零
	$scope.check=function(data){
	   data=data.replace(/[^\d\.]/g,'');
	   
	   if(data==0&&data!='') 
		  $scope.showErrorMsg("數值不得零及負數，請重新輸入");
	   return data;
	   
   }
	
	
	$scope.inquire = function() {}
	$scope.inquire();

	//保險加入
	$scope.saveins = function() {}
	
	//保險刪除
	$scope.delins = function(row) {
		//刪除前端資料，不更動後端資料庫
		for(var i = 0; i < $scope.insList.length; i++) {
			if($scope.insList[i].INS_NBR == row.INS_NBR){
				$scope.insList.splice(i,1);
				return;
			}
		}
	
	}
	
 	// 繪圖資料
    var datas = [[0,0],
                   [1,0.09983341664682815],
                   [2,0.19866933079506122],
                   [3,0.29552020666133955],
                   [4,0.3894183423086505],
                   [5,0.479425538604203],
                   [6,0.5646424733950354],
                   [7,0.644217687237691],
                   [8,0.7173560908995228],
                   [9,0.7833269096274834],
                   [10,0.8414709848078965],
                   [11,0.8912073600614354],
                   [12,0.9320390859672263],
                   [13,0.963558185417193],
                   [14,0.9854497299884601],
                   [15,0.9974949866040544],
                   [16,0.9995736030415051],
                   [17,0.9916648104524686],
                   [18,0.9738476308781951],
                   [19,0.9463000876874145]];
    var label=['1',
               '2',
               '3',
               '4',
               '5',
               '6',
               '7',
               '8',
               '9',
               '10',
               '11',
               '12',
               '13',
               '14',
               '15',
               '16',
               '17',
               '18',
               '19'];
    
    
    $scope.data = [
	               {
	                   "key" : "投資組合計算" ,
	                   "color" : "#278F54",
	                   "values" : datas
	               }
	           ]
	// 繪圖程式 LINE CHART
	$scope.options = {
            chart: {
                type: 'lineChart',
                height: 450,
                margin : {
                    top: 20,
                    right: 20,
                    bottom: 40,
                    left: 65
                },
                x: function(d){ return d[0]; },
                y: function(d){ return d[1]; },
                useInteractiveGuideline: true,
                xAxis: {
                    axisLabel: '年化標準差',
                    tickFormat: function(d){
                        return label[d];
                    },
                },
                yAxis: {
                    axisLabel: '年畫斌準差率',
                    tickFormat: function(d){
                        return d;
                    },
//                    axisLabelDistance: -10
                },
            },
            title: {
                enable: true,
                text: '投資組合計算結果'
            },
        };
    
    
    
    
    
    /* Chart options */
    $scope.options2 = {
            chart: {
                type: 'pieChart',
                height: 500,
                x: function(d){return d.key;},
                y: function(d){return d.y;},
                showLabels: true,
                duration: 500,
                labelThreshold: 0.01,
                labelSunbeamLayout: true,
                legend: {
                    margin: {
                        top: 5,
                        right: 35,
                        bottom: 5,
                        left: 0
                    }
                }
            }
    };


    /* Chart data */
    $scope.data2 = [
                   {
                       key: "One",
                       y: 5
                   },
                   {
                       key: "Two",
                       y: 2
                   },
                   {
                       key: "Three",
                       y: 9
                   },
                   {
                       key: "Four",
                       y: 7
                   },
                   {
                       key: "Five",
                       y: 4
                   },
                   {
                       key: "Six",
                       y: 3
                   },
                   {
                       key: "Seven",
                       y: .5
                   }
               ];

	
	/**彈跳視窗打開FPS233_GREAT
	 * $scope.dialog = '1'
	 * 以此判斷是否關閉彈跳視窗
	 */
	
	
	$scope.saveleft=function(){
//		$scope.showSuccessMsg("儲存成功");
		$scope.closeThisDialog('close');
	}
});
