/**================================================================================================
@program: eChart.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
/**================================================================================================
 						custom chart directive using jQuery-UI, AngularJS 												  
===================================================================================================
 @LastUpdate:
 			 2016/05/05 ArthurKO Add fontStyle
 			 2016/03/28 ArthurKO Add attrs
			 2016/03/22 ArthurKO Add directive
===================================================================================================
CHART(25):
"line", "stepLine", "spline", "column", "area", "stepArea", "splineArea", "bar", 
"bubble", "scatter", "stackedColumn", "stackedColumn100", "stackedBar", "stackedBar100",
"stackedArea", "stackedArea100","candlestick","ohlc","rangeColumn","rangeBar","rangeArea",
"rangeSplineArea","pie", "doughnut", "funnel"
=================================================================================================*/
eSoafApp.directive('eChart', function() {
    return {
        restrict: 'E',
        transclude: true,
        replace: true,
        scope:{
        	/**=============[main]=============**/
        	id: '@?', //String
        	title: '@?', //title String
        	exName: '@?', // export File name 'Pie Chart'...
        	exFlag: '@?', // export Enabled 'true' || 'false'
        	axisY: '@?', // axis Y title String
            axisX: '@?', // axis X title String
            alignV: '@?', //legend vertical align 'bottom'
            alignH: '@?', // legend horizontal align 'center'
        	/**=============[style]=============**/
            thSize: '@?', // title font size 16,20...   
            fontStyle: '@?', // title font style String
            toolStyle: '@?', // //Example: “normal”, “italic” , “oblique” 
        	width: '@?', // xx px || xx (!xx %)
        	height: '@?', // xx px || xx (!xx %)
        	theme: '@?', // 1,2,3,4,5,6...
        	point: '@?', // dataPointWidth: xx px
        	pointMin: '@?', // dataPointMinWidth: xx px
        	pointMax: '@?', // dataPointMaxWidth: xx px
        	zoom: '@?', // effect 'true' || 'false'
        	animation: '@?', // flash effect 'true' || 'false'
        	/**=============[data]=============**/
        	data: "=?", // all data as [{},{}...]
        	tipShared: '@?', // toolTip 'true' || 'false'
        	tipContent: '@?', // toolTip "{name}: {y} - <strong>#percent%</strong>"
        	xValueType: '@?', // axis X value type 'number' || 'dateTime'
        	xIn: '@?', // 1,2,3... axis X show by serise
        	yIn: '@?', // 1,2,3... axis Y show by serise
        	y2In: '@?', // 1,2,3... axis Y2 show by serise
        	xfontSize: '@?', // 10,12,14...
        	xtitleSize: '@?', // 16,18,20...
        	xThickness: '@?', // 0,1,2,3... axis X lineThickness
        	xMin: '@?', //
        	xMax: '@?', //
        	yMin: '@?', //
        	yMax: '@?', //
        	y2Min: '@?', //
        	y2Max: '@?', //
        	xAngle: '@', //
    		yFormat: '@?', // "$ 0" || "$#,##0k" ... axis Y value format as String
        	yThickness: '@?', // 0,1,2,3... axis Y lineThickness
        	yfontSize: '@?', // 10,12,14...
        	ytitleSize: '@?' // 16,18,20...        			
        },
        template: function(element, attrs) {   
        	//=============[chart main]=============
        	/** auto random id **/
        	if(!attrs.id){
        		attrs.id= 'eChart'+Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
        	};
        	//=============[chart size]=============
        	/** variable **/
        	var h = '300px';
        	var w = '600px';
        	if (attrs.height) {
        		/** height == 'PX' true **/
        		if (attrs.height.toString().toUpperCase().trim().indexOf('PX').toString() != '-1') {
        			if(attrs.height.toString().toUpperCase().trim().indexOf('%').toString() == '-1'){
        				attrs.height = attrs.height.toString().trim();
        			}else{
        				attrs.height = attrs.height.toString().toUpperCase().trim().substring(0,attrs.height.toString().toUpperCase().trim().indexOf('%')-1)+'px';
        			};
        		};
        		/** height == 'PX' false **/
        		if (attrs.height.toString().toUpperCase().trim().indexOf('PX').toString() == '-1'){
        			if(attrs.height.toString().toUpperCase().trim().indexOf('%').toString() == '-1'){
        				attrs.height = attrs.height.toString().trim()+'px';
        			}else{
        				attrs.height = attrs.height.toString().toUpperCase().trim().substring(0,attrs.height.toString().toUpperCase().trim().indexOf('%')-1)+'px';
        			};
        		};
        	};
        	if (attrs.width) {
        		/** width == 'PX' true **/
        		if (attrs.width.toString().toUpperCase().trim().indexOf('PX').toString() != '-1') {
        			if (attrs.width.toString().toUpperCase().trim().indexOf('%').toString() == '-1') {
        				attrs.width = attrs.width.toString().trim();
        			}else{
        				attrs.width = attrs.width.toString().toUpperCase().trim().substring(0,attrs.width.toString().toUpperCase().trim().indexOf('%')-1)+'px';
        			};
        		};
        		/** width == 'PX' false **/
        		if (attrs.width.toString().toUpperCase().trim().indexOf('PX').toString() == '-1') {
        			if (attrs.width.toString().toUpperCase().trim().indexOf('%').toString() == '-1') {
        				attrs.width = attrs.width.toString().trim()+'px';
        			}else{
        				attrs.width = attrs.width.toString().toUpperCase().trim().substring(0,attrs.width.toString().toUpperCase().trim().indexOf('%')-1)+'px';
        			};
        		};
        	};
        	if(!attrs.height){
        		attrs.height = h;
        	};
        	if(!attrs.width){
        		attrs.width = w;
        	};
//        	alert('[temp] h:'+attrs.height+' w:'+attrs.width); //break point
        	console.log('==============================[e-chart info]==============================');
        	console.log('[temp] id:'+attrs.id+' height:'+attrs.height+' width:'+attrs.width);   
        	console.log('==============================[e-chart info]==============================');
        	//template
            var htmlText = 
            				//template 2 by no style(fix)
							'<div style="height: {{height}}; width: {{width}};">'+
									'<div id="{{id}}"></div>'+
							'</div>'            						
							;
            return htmlText;
        },
        link: function (scope, element, attrs, ctlModel, transclude) {
        	
        	function setting () {
        		/** =============[default]============= **/
            	//main
            	var h = 300; //chart height
            	var w = 600; //chart width
            	var title = ''; //chart title string
            	var thSize = 20; //chart title font size
            	var fontStyle = "Microsoft JhengHei, EUDC, Helvetica, Arial, sans-serif";
            	var toolStyle = "normal";
            	var exName = null; //exportFileName 'Pie Chart'
            	var exFlag = null; //exportEnabled 'true' || 'false'
            	var axisY = ''; //axisY title string
            	var axisX = ''; //axisX title string
            	var alignV = null; //legend vertical align
            	var alignH = null; //legend horizontal align
            	//style
            	var theme = 'theme2'; //chart style
            	var zoom = true; //effect
            	var animation = true; //flash effect
            	var point = null; //data point width
            	var pointMin = null; //data point min width
            	var pointMax = null; //data point max width
            	//data || filler
            	var dataSource = [];
            	var tipShared = true; //toolTip shared 'true' || 'false'
            	var tipContent = null; //toolTip content "{name}: {y} - <strong>#percent%</strong>"
            	var xValueType = "number"; //number, dateTime
            	var xIn = null; //1
            	var yIn = null; //1
            	var y2In = null; //1
            	var xfontSize = 10; //10
            	var xtitleSize = 16; //16
            	var xThickness = 0; //0
            	var xMin = null;
            	var xMax = null;
            	var yMin = null;
            	var yMax = null;
            	var y2Min = null;
            	var y2Max = null;
            	var xAngle = null;
            	var yFormat = null; //"$ 0"
            	var yThickness = 0; //0
            	var yfontSize = 10; //10
            	var ytitleSize = 16; //16
            	
            	/** =============[Condition]============= **/
            	/* data */
            	if (scope.data) {
            		dataSource = scope.data;
            	};
            	/* title */
            	if (scope.title) {
            		title = scope.title;
            	};
            	if (scope.thSize) {
            		thSize = parseInt(scope.thSize);
            	};
            	if(scope.fontStyle){
            		fontStyle = scope.fontStyle;
            	}
            	if(scope.toolStyle){
            		toolStyle = scope.toolStyle;
            	}
            	/* export */
            	if (scope.exName) {
            		exName = scope.exName;
            	};
            	if (scope.exFlag) {
            		exFlag = scope.exFlag;
            	};
            	/* axis */
            	if (scope.axisY) {
            		axisY = scope.axisY;
            	};
            	if (scope.axisX) {
            		axisX = scope.axisX;
            	};
            	/* toolTip */
            	if (scope.tipShared) {
            		tipShared = scope.tipShared;
            	};
            	if (scope.tipContent) {
            		tipContent = scope.tipContent;
            	};
            	/* legend */
            	if (scope.alignV) {
            		alignV = scope.alignV;
            	};
            	if (scope.alignH) {
            		alignH = scope.alignH;
            	};
            	/* flash */
            	if (scope.zoom) {
            		zoom = scope.zoom;
            	};
            	if (scope.animation) {
            		animation = scope.animation;
            	};
            	/* theme */
            	if (scope.theme) {
            		theme = 'theme'+scope.theme;
            	};
            	/* graph size */
            	if (scope.point) {
            		point = parseInt(scope.point);
            	};
            	if (scope.pointMin) {
            		pointMin = parseInt(scope.pointMin);
            	};
            	if (scope.pointMax) {
            		pointMax = parseInt(scope.pointMax);
            	};
            	/* filler */
            	if (scope.tipShared) {
            		tipShared = scope.tipShared;
            	};
            	if (scope.tipContent) {
            		tipContent = scope.tipContent;
            	};
            	if (scope.xValueType) {
            		xValueType = scope.xValueType;
            	};
            	if (scope.xIn) {
            		xIn = parseInt(scope.xIn);
            	};
            	if (scope.yIn) {
            		yIn = parseInt(scope.yIn);
            	};
            	if (scope.y2In) {
            		y2In = parseInt(scope.y2In);
            	};
            	if (scope.xfontSize) {
            		xfontSize = parseInt(scope.xfontSize);
            	};
            	if (scope.xtitleSize) {
            		xtitleSize = parseInt(scope.xtitleSize);
            	};
            	if (scope.xThickness) {
            		xThickness = parseInt(scope.xThickness);
            	};
            	if (scope.xMin) {
            		xMin = parseInt(scope.xMin);
            	};
            	if (scope.xMax) {
            		xMax = parseInt(scope.xMax);
            	};
            	if (scope.yMin) {
            		yMin = parseInt(scope.yMin);
            	};
            	if (scope.yMax) {
            		yMax = parseInt(scope.yMax);
            	};
            	if (scope.y2Min) {
            		y2Min = parseInt(scope.y2Min);
            	};
            	if (scope.y2Max) {
            		y2Max = parseInt(scope.y2Max);
            	};
            	if (scope.xAngle) {
            		xAngle = parseInt(scope.xAngle);
            	};
            	if (scope.yFormat) {
            		yFormat = scope.yFormat;
            	};
            	if (scope.yThickness) {
            		yThickness = parseInt(scope.yThickness);
            	};
            	if (scope.yfontSize) {
            		yfontSize = parseInt(scope.yfontSize);
            	};
            	if (scope.ytitleSize) {
            		ytitleSize = parseInt(scope.ytitleSize);
            	};
            	
            	/** =============[chart size]============= **/
            	// [height]
            	if (scope.height) {
//            		alert('in [ctrl] height'); //break point
            		/** ======[ check 'PX' ]====== **/
            		/** height == 'PX' true **/
            		if (scope.height.toString().toUpperCase().trim().indexOf('PX').toString() != '-1') {
            			h = parseInt(scope.height.toString().toUpperCase().trim().substring(0,scope.height.toString().toUpperCase().trim().indexOf('PX')));
//            			alert('[ctrl] h.px:'+h); //break point
            		/** height == 'PX' false **/
            		} else if (scope.height.toString().toUpperCase().trim().indexOf('PX').toString() == '-1') {
            			h = parseInt(scope.height.trim());
//            			alert('[ctrl] h:'+h); //break point
            		};
            		/** ======[ check '%' ]====== **/
            		/* if h == % false */
            		if(scope.height.toString().toUpperCase().trim().indexOf('%').toString() == '-1') {
            			/** height == 'PX' true **/
                		if (scope.height.toString().toUpperCase().trim().indexOf('PX').toString() != '-1') {
                			h = parseInt(scope.height.toString().toUpperCase().trim().substring(0,scope.height.toString().toUpperCase().trim().indexOf('PX')));
//                			alert('[ctrl] h.px:'+h); //break point
                		};
                		/** height == 'PX' false **/
                		if (scope.height.toString().toUpperCase().trim().indexOf('PX').toString() == '-1') {
                			h = parseInt(scope.height.trim());
//                			alert('[ctrl] h:'+h); //break point
                		};
                	/* if h == % true */
            		} else if (scope.height.toString().toUpperCase().trim().indexOf('%').toString() != '-1') {
            			h = 300;
//            			alert('[ctrl] h%'); //break point
            		}; 
            	};
            	// [width]
            	if (scope.width) {       
//            		alert('in [ctrl] width'); //break point
            		/* if w == % false */
            		if (scope.width.toString().toUpperCase().trim().indexOf('%').toString() == '-1') {
    	        		/** width == 'PX' true **/
    	        		if (scope.width.toString().toUpperCase().trim().indexOf('PX').toString() != '-1') {
    	        			w = parseInt(scope.width.toString().toUpperCase().trim().substring(0,scope.width.toString().toUpperCase().trim().indexOf('PX')));
//    	        			alert('[ctrl] w.px:'+w); //break point
    	        		};
    	        		/** width == 'PX' false **/
    	        		if (scope.width.toString().toUpperCase().trim().indexOf('PX').toString() == '-1') {
    	        			w = parseInt(scope.width.trim());
//    	        			alert('[ctrl] w:'+w); //break point
    	        		};
    	        	/* if w == % true */
            		} else if (scope.width.toString().toUpperCase().trim().indexOf('%').toString() != '-1') {
            			w = 600;
//            			alert('[ctrl] w%'); //break point
            		};
            	};
            	console.log('====================== scope e-chart size ======================');
            	console.log('[ctrl] h:'+h+' w:'+w);
//            	alert('[ctrl] h:'+h+' w:'+w); //break point
            	console.log('====================== scope e-chart size ======================');
            	
            	//data
//            	alert(JSON.stringify(dataSource)); //break point
            	/** =============[setting]============= **/
            	var chart = new CanvasJS.Chart(scope.id, {
            		theme: theme, //$scope.theme
            		title:{
            			text: title, //$scope.title   
            			fontSize: thSize,//$scope.thSize 20
            			fontFamily: fontStyle//font style
            		},
            		//-------------[chart type]--------------
            		exportFileName: exName, //
            		exportEnabled: exFlag, //
            		//-------------[chart type]--------------
            		zoomEnabled: zoom, //$scope.zoom
            		animationEnabled: animation, //$scope.animation
            		axisY:{
            			//Allen add 2016/05/12
            			labelFontFamily: fontStyle,
            	        title: axisY, //$scope.axisY
            	        interval: yIn, //scope.yIn 1
            	        labelFontSize: yfontSize, //scope.xFontSize 10
            	        minimum: yMin, //
    	  				maximum: yMax, //	
    	    			titleFontSize: ytitleSize//18,
//            	        maximum: yMax//85
    	    	    },
    	    	    axisY2:{
    	    	    	//Allen add 2016/05/12
    	    	    	labelFontFamily: fontStyle,
    	    	    	interval: y2In, //scope.y2In 1
    	    			valueFormatString: yFormat, //scope.yFormat
    	    			minimum: y2Min, //
    	  				maximum: y2Max, //	
    	    			lineThickness: yThickness //scope.yThickness			
    	    		},
    	    	    axisX:{
    	    	    	//Allen add 2016/05/12
    	    	    	labelFontFamily: fontStyle,
    	    	        title: axisX, //$scope.axisX
    	    	        interval: xIn, //scope.xIn 1
    	    			labelFontSize: xfontSize, //scope.xFontSize 10
    	    			titleFontSize: xtitleSize,//18,
    	    			lineThickness: xThickness, //scope.xThickness 0	  
    	    			
    	    			minimum: xMin, //
    	  				maximum: xMax, //	  				
    	  				labelAngle: xAngle//-40
    	    	    },
    	    	    toolTip: {
    	    	    	fontStyle: toolStyle, //Example: “normal”, “italic” , “oblique” 
    	    			shared: tipShared, //$scope.tipShared
    	    			content: tipContent//$scope.tipContent "{name}: {y} - <strong>#percent%</strong>",
    	    		},
    	    	    legend:{
    	    	    	//Allen add 2016/05/12
    	    	    	fontFamily: fontStyle,
    	    	        verticalAlign: alignV,
    	    	        horizontalAlign: alignH
    	    	    },
            		data: dataSource,
            		//chart size
            		height: h, //$scope.height
            	    width: w, //$scope.width
            		//data graph size
            		dataPointWidth: point, //$scope.point
            		dataPointMinWidth: pointMin, //$scope.pointMin
            		dataPointMaxWidth: pointMax, //$scope.pointMax
            		xValueType: xValueType //$scope.xValueType
            		
            	});
            	chart.render();
        	}
        	setting();          	
        	
        	
        	
        	//Watch Data onChange
        	scope.$watch('data', function (newValue, oldValue) {
        		if(newValue == oldValue){
        			return;
        		}
//        		chart.options.data = scope.data;
//        		chart.render();
        		setting();
        	});
        	console.log('=========================[e-chart Data]=========================');
        	console.log(JSON.stringify(chart.options.data));
        	console.log('=========================[e-chart Data]=========================');
        }
    };
    
});