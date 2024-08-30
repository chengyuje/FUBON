/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS900Controller',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS900Controller";
		
		 CanvasJS.addColorSet("greenShades",
	                [//colorSet Array
	                 "#BFC8D7", "#D2D2D2", "#E3E2B4", "#A2B59F", "#E8E7D2", "#BDC2BB", "#C9BA9B", "#C9CBE0", "#CCDBE2", "#ECD4D4", "#D5E1DF", "#83B1C9" 
	                ]);
		 CanvasJS.addColorSet("colorset2",["#E9967A", "#C1CDC1", "#8B7B8B", "#A2B5CD", "#9370DB", "#FFDAB9", "#EEC591", "#E6E6FA", "#BEBEBE", "#ADD8E6", "#CDBE70", "#CD9B9B"]);
		 CanvasJS.addColorSet("colorset3",["#6ECEDA", "#E5C1C5", "#C3E2DD"]);
		 CanvasJS.addColorSet("colorset4",["#EEC591", "#E6E6FA", "#BEBEBE", "#ADD8E6", "#CDBE70", "#CD9B9B"]);
		 
		var barchart = new CanvasJS.Chart("AUMBarChartContainer",
			    {
			      title:{
			        text: "AUM資產分布",
			        fontColor: "#8E8E8E",
				    fontStyle: "oblique"
			      },
			      colorSet: "greenShades",
			      
			      data: [ {
			        dataPoints: [
			        { x: 1, y: 2975234571, label: "個金分行業務一處"},
			        { x: 2, y: 2622734017,  label: "個金分行業務二處" },
			        { x: 3, y: 4342375200,  label: "個金分行業務三處"}
			        ]
			      }]
			    });

		barchart.render();
		
		
		var splinechart = new CanvasJS.Chart("splineChartContainer",
			    {
			    
			      title:{
			    	  text: "專員獎勵金成績表",
			    	  fontColor: "#8E8E8E",
					  fontStyle: "oblique"
			      },
			      colorSet: "colorset4",	      
			       data: [
			      {        
			        type: "spline",
			        dataPoints: [
			        { x: 1, y: 152342, label: "2020/01" },
			        { x: 2, y: 154314, label: "2020/02" },
			        { x: 3, y: 133321, label: "2020/03" },
			        { x: 4, y: 523463, label: "2020/04" },
			        { x: 5, y: 450778, label: "2020/05" },
			        { x: 6, y: 441201, label: "2020/06" },
			        { x: 7, y: 567186, label: "2020/07" },
			        { x: 8, y: 344328, label: "2020/08" },
			        { x: 9, y: 263438, label: "2020/09" },
			        { x: 10, y: 231305, label: "2020/10" },
			        { x: 11, y: 353480, label: "2020/11" },
			        { x: 12, y: 222291, label: "2020/12" }        
			        ]
			      }       
			        
			      ]
			    });

		splinechart.render();
			    
		
		var piechart = new CanvasJS.Chart("pieChartContainer",
				{
					title:{
						text: "客戶數分布",
						fontColor: "#8E8E8E",
					    fontStyle: "oblique"
					},		
					colorSet: "colorset2",
					data: [
					{       
						type: "pie",
						showInLegend: true,
						toolTipContent: "{y} - #percent %",
						yValueFormatString: "#,###,###,##0.## (人)",
						legendText: "{indexLabel}",
						dataPoints: [
							{  y: 41815, indexLabel: "個金分行業務一處" },
							{  y: 21754, indexLabel: "個金分行業務二處" },
							{  y: 31258, indexLabel: "個金分行業務三處" }
						]
					}
					]
				});
		piechart.render();
				
		var spline2chart = new CanvasJS.Chart("spline2ChartContainer",
			    {
			    
			      title:{
			    	  text: "專員手收總覽",
			    	  fontColor: "#8E8E8E",
				       fontStyle: "oblique"
			      },
			      colorSet: "colorset3",  
			       data: [{        
			    	   type: "spline",
			    	   showInLegend: true,
			    	   toolTipContent: "投資 <br/>{label} - {y}",
			    	   name: "投資手收",
				        dataPoints: [
				        { x: 1, y: 222342, label: "2020/01" },
				        { x: 2, y: 254314, label: "2020/02" },
				        { x: 3, y: 233321, label: "2020/03" },
				        { x: 4, y: 323463, label: "2020/04" },
				        { x: 5, y: 450778, label: "2020/05" },
				        { x: 6, y: 441201, label: "2020/06" },
				        { x: 7, y: 467186, label: "2020/07" },
				        { x: 8, y: 344328, label: "2020/08" },
				        { x: 9, y: 263438, label: "2020/09" },
				        { x: 10, y: 231305, label: "2020/10" },
				        { x: 11, y: 353480, label: "2020/11" },
				        { x: 12, y: 222291, label: "2020/12" }        
				        ]},
				      {
				      type: "spline",
				      showInLegend: true,
				      toolTipContent: "保險 <br/>{label} - {y}",
				      name: "保險手收",
				        dataPoints: [
				        { x: 1, y: 252342, label: "2020/01" },
				        { x: 2, y: 274314, label: "2020/02" },
				        { x: 3, y: 313321, label: "2020/03" },
				        { x: 4, y: 223463, label: "2020/04" },
				        { x: 5, y: 250778, label: "2020/05" },
				        { x: 6, y: 451201, label: "2020/06" },
				        { x: 7, y: 437186, label: "2020/07" },
				        { x: 8, y: 304328, label: "2020/08" },
				        { x: 9, y: 293438, label: "2020/09" },
				        { x: 10, y: 261305, label: "2020/10" },
				        { x: 11, y: 253480, label: "2020/11" },
				        { x: 12, y: 272291, label: "2020/12" }
				      ]},
				        {        
					    	   type: "spline",
					    	   showInLegend: true,
					    	   toolTipContent: "兌換 <br/>{label} - {y}",
					    	   name: "兌換收益",
						        dataPoints: [
						        { x: 1, y: 112342, label: "2020/01" },
						        { x: 2, y: 154314, label: "2020/02" },
						        { x: 3, y: 133321, label: "2020/03" },
						        { x: 4, y: 123463, label: "2020/04" },
						        { x: 5, y: 150778, label: "2020/05" },
						        { x: 6, y: 141201, label: "2020/06" },
						        { x: 7, y: 167186, label: "2020/07" },
						        { x: 8, y: 144328, label: "2020/08" },
						        { x: 9, y: 163438, label: "2020/09" },
						        { x: 10, y: 131305, label: "2020/10" },
						        { x: 11, y: 153480, label: "2020/11" },
						        { x: 12, y: 122291, label: "2020/12" }        
						        ]}
				  ]
			    });

		spline2chart.render();
		
		var bubblechart = new CanvasJS.Chart("bubbleChartContainer",
			    {
			      title:{
			       text: "分行客戶規模分析",
			       fontColor: "#8E8E8E",
			       fontStyle: "oblique"
			      },
			      colorSet: "greenShades",
			      axisX: {
			        title:"客戶數 (千人)"
			      },
			      axisY: {
			        title:"資產AUM (萬)"
			      },

			      legend:{
			        verticalAlign: "bottom",
			        horizontalAlign: "left"

			      },
			      data: [
			      {
			        type: "bubble",
			        legendText: "泡泡的大小顯示手收的多寡",
			        showInLegend: true,
			        legendMarkerType: "circle",
			        toolTipContent: "<strong>{name}</strong> <br/> 客戶AUM: {y}(萬)<br/> 客戶數: {x} (千)<br/> 手收貢獻度: {z} (萬)",
			     dataPoints: [
			     { x: 64.8, y: 122.66, z: 12074.4 , name: "大安分行"},
			     { x: 62.1, y: 431.61, z: 13313.8, name: "古亭分行"},
			     { x: 78.1, y: 342.00, z: 30689.77, name: "營業部" },
			     { x: 68.5, y: 522.15, z: 23712.414, name: "長安東路分行"},
			     { x: 72.5, y: 631.86, z: 19333.24, name: "士林分行"},
			     { x: 76.5, y: 945.36, z: 4122.24, name: "城東分行"},
			     { x: 50.9, y: 866.56, z: 3154.48, name: "士東分行"},
			     { x: 68.6, y: 712.54, z: 14121.91, name: "玉成分行" },

			     { x: 82.9, y: 643.37, z: 7127.55, name: "福港分行" },
			     { x: 79.8, y: 589.36, z: 8421.90, name:"忠孝分行" },
			     { x: 72.7, y: 462.78, z: 7954.71, name: "延平分行"},
			     { x: 80.1, y: 316.94, z: 6155.81, name:"木柵分行" },
			     { x: 55.8, y: 244.76, z: 5439.24, name: "八德分行"},
			     { x: 81.5, y: 188.93, z: 11221.95, name:"松南分行" },
			     { x: 68.1, y: 244.77, z: 10831.09, name: "永吉分行"},
			     { x: 47.9, y: 323.42, z: 3399.42, name: "中山分行"},
			     { x: 50.3, y: 655.58, z: 5618.55, name: "北投分行"}
			     ]
			   }
			   ]
			 });

		bubblechart.render();
			
//		var splinechart = new CanvasJS.Chart("splineChartContainer",
//			    {title:{
//			      text: "Books Issued from Central Library"
//			      },
//			       data: [{        
//			        type: "spline",
//			        
//			        dataPoints: [
//			        { x: new Date(2020, 00, 1), y: 1352 },
//			        { x: new Date(2020, 01, 1), y: 1514 },
//			        { x: new Date(2020, 02, 1), y: 1321 },
//			        { x: new Date(2020, 03, 1), y: 1163 },
//			        { x: new Date(2020, 04, 1), y: 950 },
//			        { x: new Date(2020, 05, 1), y: 1201 },
//			        { x: new Date(2020, 06, 1), y: 1186 },
//			        { x: new Date(2020, 07, 1), y: 1281 },
//			        { x: new Date(2020, 08, 1), y: 1438 },
//			        { x: new Date(2020, 09, 1), y: 1305 },
//			        { x: new Date(2020, 10, 1), y: 1480 },
//			        { x: new Date(2020, 11, 1), y: 1291 }        
//			        ]
//			      } ]
//			    });
//
//		splinechart.render();
		
		$scope.showChart = function(nbr) {
			if (nbr == '1') {
				var dialog=ngDialog.open({
					template: 'assets/txn/PMS350/PMS350_SPLINE.html',	
					className: 'PMS350_SPLINE',	
					controller:['$scope',function($scope){
						$scope.pms900 = true;
					}]					
				});
			} else if (nbr == '2') {
				var dialog=ngDialog.open({
					template: 'assets/txn/PMS900/PMS900_SPLINE.html',	
					className: 'PMS900_SPLINE',	
					controller:['$scope',function($scope){
						
					}]					
				});					
			}
		}
		
		$scope.showAUMChart = function() {			
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS900/PMS900_ROUTE.html',
				className: 'PMS901',
				showClose: false,
				scope : $scope,
				controller: ['$scope', function($scope) {
	        		$scope.txnName = "AUM資產分佈";
	        		$scope.routeURL = 'assets/txn/PMS901/PMS901.html';
	            }]
			}).closePromise.then(function (data) {
				if(data.value != "cancel") {
				}
			});
		}
			  
});