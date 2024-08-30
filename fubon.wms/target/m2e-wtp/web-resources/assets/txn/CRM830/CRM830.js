/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM830Controller',
	function($rootScope, $scope, $controller ,getParameter, $confirm, socketService, ngDialog, projInfoService, $q) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM830Controller";
		
		//初始化
		$scope.init = function(){
//			$scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
			$scope.inputVO.cust_id = $scope.custVO.CUST_ID;
		};
		$scope.init();
		
		//初始化
		$scope.inquireInit = function(){
			$scope.resultList = [];
			$scope.resultListJSB = [];
		}
		$scope.inquireInit();
		
		//xml參數初始化
		$scope.mapData = function() {
			var deferred = $q.defer();
			getParameter.XML(["PRD.INS_TYPE", "PRD.INS_TYPE_JSB"], function(totas) {
				if (totas) {
					debugger
					$scope.insTypeList = totas.data[totas.key.indexOf('PRD.INS_TYPE')];
					$scope.insTypeJSBList = totas.data[totas.key.indexOf('PRD.INS_TYPE_JSB')];
					deferred.resolve();
				}
			});
			return deferred.promise;
		}
		$scope.mapData().then(function() {$scope.inquire();});
		
		//設定北富銀保險類別pieChart
		$scope.fubonPeiChart = function() {
			//設定顏色
			var color_set = [{color: "#FFA556"},
			                 {color: "#3E8ABE"},
			                 {color: "#6BBC6B"},
			                 {color: "#F497B2"},
			                 {color: "#FFFF01"}
			                ];
			
			//設定畫圖資料
			var data = [];
		    for(var i = 0; i < $scope.resultList.length; i++){
		       data.push({
		    	   			key: $scope.ins_type_set[i] ,
		    	   			color: color_set[i].color,
		    	   			y: $scope.resultList[i].TOTAL_INS_AMT_TWD
		       			})
		    }
			//畫圖資料
			$scope.data = data;
			
			//畫圖參數設定
			$scope.options = {
					chart: {
						type: 'pieChart',
						height: 500,
						width : 500,
						x: function(d){return d.key;},
						y: function(d){return d.y;},
						showLabels: true,
						duration: 300,
						labelThreshold: 0.01,
						labelSunbeamLayout: false,
						labelType: "fubon",
						donutLabelsOutside: true,
						legend: {
							margin: {
								top: 5,
								right: 0,
								bottom: 0,
			                	left: 0
							},
							align: true
						}
					}
			};
		};
		
		//設定日盛保代保險類別pieChart
		$scope.JSBPeiChart = function() {
			//設定顏色
			var color_set = [{color: "#FFA556"},
			                 {color: "#3E8ABE"},
			                 {color: "#6BBC6B"},
			                 {color: "#F497B2"},
			                 {color: "#FFFF01"}
			                ];
			
			//設定畫圖資料
			var dataJSB = [];
		    for(var i = 0; i < $scope.resultListJSB.length; i++){
		    	dataJSB.push({
		    	   			key: $scope.ins_type_set_JSB[i] ,
		    	   			color: color_set[i].color,
		    	   			y: $scope.resultListJSB[i].TOTAL_INS_AMT_TWD
		       			})
		    }
			//畫圖資料
			$scope.dataJSB = dataJSB;
			
			//畫圖參數設定
			$scope.optionsJSB = {
					chart: {
						type: 'pieChart',
						height: 500,
						width : 500,
						x: function(d){return d.key;},
						y: function(d){return d.y;},
						showLabels: true,
						duration: 300,
						labelThreshold: 0.01,
						labelSunbeamLayout: false,
						labelType: "JSB",
						donutLabelsOutside: true,
						legend: {
							margin: {
								top: 5,
								right: 0,
								bottom: 0,
			                	left: 0
							},
							align: true
						}
					}
			};
		};
		
		//查詢
		$scope.inquire = function() {
			console.log('inquire');
			$scope.sendRecv("CRM830", "inquire", "com.systex.jbranch.app.server.fps.crm830.CRM830InputVO", $scope.inputVO,
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsg(totas[0].body.msgData);
					}
					if((tota[0].body.resultList == null || tota[0].body.resultList.length == 0)
							&& (tota[0].body.resultListJSB == null || tota[0].body.resultListJSB.length == 0)) {
						$scope.showMsg("ehl_01_common_009");
                		return;
					}
					$scope.resultList = tota[0].body.resultList; //北富銀
					$scope.resultListJSB = tota[0].body.resultListJSB; //日盛保代
					
					//總金額
					$scope.getTotal = function(){
					    var total = 0;
					    //北富銀
					    for(var i = 0; i < $scope.resultList.length; i++){
					        total += $scope.resultList[i].TOTAL_INS_AMT_TWD;
					    }
					    //日盛保代
					    for(var i = 0; i < $scope.resultListJSB.length; i++){
					        total += $scope.resultListJSB[i].TOTAL_INS_AMT_TWD;
					    }
					    return total;
					}
					
					if(tota[0].body.resultList != null && tota[0].body.resultList.length > 0) {
						//設定北富銀保險類別
						$scope.ins_type_set = [];					
						for(var i = 0; i < $scope.resultList.length; i++){
							for(var j = 0; j < $scope.insTypeList.length; j++){
								if($scope.resultList[i].INS_TYPE !=null){
									if($scope.resultList[i].INS_TYPE.trim() ===  $scope.insTypeList[j].DATA.trim()){
										$scope.ins_type_set[i] = $scope.insTypeList[j].LABEL;
										break;
									}
								}
							}
						}
						//設定北富銀保險類別pieChart
						$scope.fubonPeiChart();
					}
					
					if(tota[0].body.resultListJSB != null && tota[0].body.resultListJSB.length > 0) {
						//設定日盛保代保險類別
						$scope.ins_type_set_JSB = [];
						for(var i = 0; i < $scope.resultListJSB.length; i++){
							for(var j = 0; j < $scope.insTypeJSBList.length; j++){
								if($scope.resultListJSB[i].INS_TYPE !=null){
									if($scope.resultListJSB[i].INS_TYPE.trim() ===  $scope.insTypeJSBList[j].DATA.trim()){
										$scope.ins_type_set_JSB[i] = $scope.insTypeJSBList[j].LABEL;
										break;
									}
								}
							}
						}
						//設定日盛保代保險類別pieChart
						$scope.JSBPeiChart();
					}
					
					return;
			});
		};
//		$scope.inquire();
		
		//傳參數
        $scope.detail = function (row, category) {
//        	$scope.connector('set','CRM830ID', $scope.inputVO.cust_id);
        	if(row == 'all'){
        		$scope.connector('set','CRM830CATEGORY', 'all');
        		$scope.connector('set','CRM830TYPE', 'all');
        	}else{
        		$scope.connector('set','CRM830CATEGORY', category); //1:北富銀 2:日盛保代
        		$scope.connector('set','CRM830TYPE', row.INS_TYPE);
        	}
        	
//    		$rootScope.menuItemInfo.url = "assets/txn/CRM831/CRM831.html";
        	var path = "assets/txn/CRM831/CRM831.html";
			$scope.connector("set","CRM610URL",path);
			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
        }
		

});
		