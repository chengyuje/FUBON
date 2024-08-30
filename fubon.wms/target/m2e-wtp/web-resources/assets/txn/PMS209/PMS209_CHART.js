/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS209_CHARTController', function($rootScope, $scope,
		$controller, socketService, ngDialog, projInfoService, $q, $confirm,$interval,
		$filter) {
	$controller('BaseController', {
		$scope : $scope
	});
	var label=[];  //年月
	$scope.datas=[];   //清空data
	$scope.values=0;
	$scope.count=0;  //刷新
	//連結過來放年月
	var date_data_x=new Date();
	$scope.controllerName = "PMS209_CHARTController";
	//時間初始設定  刷新用
	var auto = $interval(function() {
	        if($scope.count==0)
	        	$scope.count++;
	}, 100);
	
	/***
	 * 主查詢(趨勢圖)
	 * 
	 * by Kevin
	 */
	$scope.inquire_view = function() {

    	
		if($scope.inputVO.branch_area_id!='' && $scope.inputVO.type_Q!='' && $scope.inputVO.eTime!='' && $scope.inputVO.sTime!=''){
			  $scope.prodType=0;
			  if (angular.isDefined(auto)) {
		          $interval.cancel(auto);
		          auto = $interval(function() {
		  	        if($scope.count==0)
		  	        	$scope.count++;
		  	     
		          }, 800);
		          $scope.count=0;
		        }
			  if ($scope.inputVO.sTime > $scope.inputVO.eTime) {
		    		$scope.showErrorMsg('資料迄月小於資料起月');
		    		return;
		    	}
			  if ($scope.inputVO.prodType!='' ||  $scope.inputVO.prodID!='') {
		    		$scope.prodType=1;
		    		$scope.datas=[];
		    		return;
		    	}
		$scope.sendRecv("PMS209", "queryData",
				"com.systex.jbranch.app.server.fps.pms209.PMS209InputVO", 
				$scope.inputVO
				, function(tota, isError) {
					if (!isError) {
						label=[];
						$scope.values=0;
						var Array = []; 
						var nam = [];    //存放所有查資料    
						
						$scope.outputVO = tota[0].body;  
						/***=====以下是最多可以幾條線======***/
						angular.forEach(tota[0].body.DATA, function(
								row1, index1, objs1) {
							angular.forEach(tota[0].body.TITLE, function(
									row, index, objs) {
								var view_data=row1[row.YYYYMM] || 0;
								if($scope.values<view_data)
									$scope.values=view_data;
								var view_time=new Date(row.YYYYMM.substr(0,4)+"/"+row.YYYYMM.substr(4,5)+"/01 00:00:00");
								view_time.setDate(0);
								var Number_Time=Number(new Date(row.YYYYMM.substr(0,4)+"/"+row.YYYYMM.substr(4,5)+"/01 00:00:00"));
								Array.push([index, view_data]);
								//塞年月資訊
								if(index1==0)
									label.push(row.YYYYMM);
							});
							/**************KEY是顯示欄位名稱,VALUE是X時間,Y值*****************/
							//==  除了全行平均  ==  各線條設定
							if(row1.BRANCH_NAME!=undefined && row1.AO_CODE==undefined){
								nam.push({
									"key" : row1.BRANCH_NAME,
									"values" : Array
								});
							}else if(row1.AO_CODE!=undefined){
								nam.push({
									"key" :row1.AO_CODE+"-"+ row1.EMP_NAME,
									"values" : Array
								});
							}
							Array=[];
							
								
						});
						//設定data  <------圖的資料
						$scope.datas = nam; 
						
//						$scope.values=
//						$scope.options.yDomain([0, $scope.values]);
						$scope.options = {
					            chart: {
					                type: 'lineChart',
					                height: 450,
					                duration: 1000,
					                margin : {
					                    top: 20,
					                    right: 20,
					                    bottom: 40,
					                    left: 65
					                },
					                x: function(d){ return d[0]; },
					                y: function(d){  return d[1]; },
					                useInteractiveGuideline: true,
//					                yDomain:[0,],
					                xAxis: {
					                    axisLabel: '年月',
//					                    showMaxMin: true,
					                    tickFormat: function(d){
					                    	
					                        return label[d];
					                    },
					                },
					                yAxis: {
					                    axisLabel: '數值',
					                    showMaxMin: true,
					                    tickFormat: function(d){
					                    	   return d3.format('0,0.0f ')(d);
					                    },
					                },
					                
//					                x2Axis: {
//					                    tickFormat: function(d){
//					                    	return label[d];
//					                    }
//					                },
//					                y2Axis: {
//					                    tickFormat: function(d){
//					                        return d;
//					                    }
//					                }
					            },
					            title: {
					                enable: true,
					                text: '最近13個月歷史資料'
					            },
					        };
						$scope.options.chart.yDomain = [0, $scope.values ];
						return;
					}
				});
		}
	};
	
	

});
