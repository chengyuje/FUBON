/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM614Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM614Controller";

		$scope.inquire = function() {
			$scope.sendRecv("CRM614", "inquire", "com.systex.jbranch.app.server.fps.crm614.CRM614InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList != null && tota[0].body.resultList.length >0 ) {
								
							//分頁用
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							//總和用
							$scope.resultList2 = tota[0].body.resultList2.reverse();	//0002624:1.走勢圖橫軸年月，月份由左而右呈現，新的月份在右邊。
							//PDF用
							$scope.resultList4 = tota[0].body.resultList4;
							$scope.resultList3 = tota[0].body.resultList3;
							
							//繪圖資料設定
							var avg_aum_amt = [];
				        	var lday_aum_amt = [];
				        	var label =[];
				        	var name = new Array();
				        	for (var i = 0; i <$scope.resultList2.length; i++){
				        		name[i] = $scope.resultList2[i].DATA_YEAR + "年" + $scope.resultList2[i].DATA_MONTH + "月";
				        		avg_aum_amt.push([i, ($scope.resultList2[i].AVG_AUM_AMT/1000)])
				        		lday_aum_amt.push([i, ($scope.resultList2[i].LDAY_AUM_AMT/1000)])
				        		label.push(name[i])
				        	}
				        	
				        	//繪圖資料
				    		$scope.data = [
				    		               {
				    		                   "key" : "月平均AUM" ,
				    		                   "color" : "#278F54",
				    		                   "values" : avg_aum_amt
				    		               },

				    		               {
				    		                   "key" : "月餘額AUM" ,
				    		                   "color" : "#FF8C26",
				    		                   "values" : lday_aum_amt
				    		               },
				    		           ]
				    		//繪圖程式 LINE CHART
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
				                            axisLabel: '年月',
				                            tickFormat: function(d){
				                                return label[d];
				                            },
				                        },
				                        yAxis: {
				                            axisLabel: '資產(千元)',
				                            tickFormat: function(d){
				                                return d;
				                            },
//				                            axisLabelDistance: -10
				                        },
				                    },
				                    title: {
				                        enable: true,
				                        text: '最近18個月歷史AUM'
				                    },
				                };
							return;
						}
					}
			});
	    };
		$scope.inquire();
		$scope.report = function() {
//			console.log($scope.resultList2);
			if($scope.resultList4==undefined || $scope.resultList3==undefined){
				$scope.showErrorMsg('無此客戶月歷史AUM資料，請洽系統管理員。');
				return
			}
				
			$scope.sendRecv("CRM614", "report", "com.systex.jbranch.app.server.fps.crm614.CRM614OutputVO", {'resultList2':$scope.resultList4,'resultList3':$scope.resultList3}, 
				function(tota, isError) {
					if (!isError) {	
						if(tota[0].body.msgCust=='IsNull'){
							$scope.showErrorMsg('無此客戶月歷史AUM資料，請洽系統管理員。');
						}
						return;
					}
	   			});
			
		}


		
});
		