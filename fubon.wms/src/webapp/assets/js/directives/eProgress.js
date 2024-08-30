/**================================================================================================
@program: eProgress.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eProgress', ["$q", "$timeout", "$interval", "ngDialog", function($q, $timeout, $interval, ngDialog) {
	return {
		restrict: 'E',
		transclude: true,
        scope:{
        	type:"@?",
           value:"=?",
             max:"=?",
        	 min:"=?",
           width:"@?",
          height:"@?",
            hide:"=?"
        },
        template: function(element, attrs) {
        	var htmlText = 
        		'<div style="margin:10px;">'+
        		'<div class="e-prog">'+
		    		'<div class="e-prog-graph">'+
		    			'<div class="e-prog-graph-right">'+
		    				'<div class="e-prog-graph-right-inside"></div>'+
		    			'</div>'+
		    			'<div class="e-prog-graph-left">'+
		    		    	'<div class="e-prog-graph-left-inside"></div>'+
		    		    '</div>'+
		    		'</div>'+
		    		'<div class="e-prog-middle">'+		    		
		    		'	<div class="e-prog-middle-icon-watch"></div>'+
		    		'	<div class="e-prog-middle-icon-arrow"></div>'+		
		    		'	<div class="e-prog-middle-text-wrap" ng-if="!hide.txt">'+
		    		'		<span class="e-prog-middle-text-1" ng-bind="$percent.toString().substring(0,1)"></span>'+
		    		'		<span ng-if="$percent.toString().length>1" class="e-prog-middle-text-2" ng-bind="$percent.toString().substring(1,2)"></span>'+
		    		'		<span ng-if="$percent.toString().length>2" class="e-prog-middle-text-3" ng-bind="$percent.toString().substring(2,3)"></span>'+
		    		'	%</div>'+
		    		'</div>'+
		    	'</div>'+
		    	'</div>'
     			;
        	return htmlText;
        },
		link: function(scope, element, attrs, OtherComponent, transclude) {

			/** Initialize **/
			scope.value ? scope.value : 0;
			scope.angle ? scope.angle : 0;
			scope.angleArr ? scope.angleArr : 0;
			scope.width = scope.width | "200px"; //default width
			scope.height = scope.height | "200px"; //default height
			scope.w = null; //result width
			scope.h = null; //result width
			scope._w = element.parent().width(); //container width
			scope._h = element.parent().height(); //container height
			scope.$w = null;
			scope.$h = null;
			var tempCSS = {};
			
			//Value
			scope.$watch("value",function(){
				scope.$percent = 0;
				//check counter type
				switch(type(scope.value)){
					case "string": //get value to percent number only
						if(scope.value.indexOf("%")>-1){
							scope.$percent = parseInt(scope.value.substring(0, scope.value.indexOf("%").trim()));
						}else{
							scope.max ? scope.max : scope.max = 100;
							isNaN(parseInt(scope.max)) ? scope.max = 100 : scope.max;
							isNaN(parseInt(scope.value)) ? scope.value = 0 : scope.value;
							scope.$percent = parseInt((parseInt(scope.value) / parseInt(scope.max))*100);
						}																		
						break;
					case "number": //counts percent
						scope.max ? scope.max : scope.max = 100;
						isNaN(parseInt(scope.max)) ? scope.max = 100 : scope.max;
						scope.$percent = parseInt((scope.value / parseInt(scope.max))*100);
						break;
				}
				//set value
				if(scope.$percent <= 50){
					scope.angle = scope.$percent*3.6-180;
					$(element).find('.e-prog-graph-left-inside').css('transform','rotate(180deg)').animate({'':''});
					$(element).find('.e-prog-graph-right-inside').css('transform','rotate(' + scope.angle + 'deg)');
					$(element).find('.e-prog-middle-icon-arrow').css('transform','rotate(' + scope.$percent*3.6 + 'deg)');
					if(scope.$percent < 10)
						$(element).find('.e-prog-middle-text-wrap').css('margin-left','64px');
					else
						$(element).find('.e-prog-middle-text-wrap').css('margin-left','60px');
					
				}else if(scope.$percent > 50 && scope.$percent <= 100){
					//when value over 50%, needs modify the left side, and the right side 0deg is 50%.
					$(element).find('.e-prog-graph-right-inside').css('transform','rotate(0)');
					//left side 0deg is 100%, transform.
					scope.angle = scope.$percent*3.6-360;
					$(element).find('.e-prog-graph-left-inside').css('transform','rotate(' + scope.angle + 'deg)');
					$(element).find('.e-prog-middle-icon-arrow').css('transform','rotate(' + scope.angle + 'deg)');					
					if(scope.$percent < 100)
						$(element).find('.e-prog-middle-text-wrap').css('margin-left','60px');
					else
						$(element).find('.e-prog-middle-text-wrap').css('margin-left','50px');
					
				}else if(scope.$percent > 100){
					$(element).find('.e-prog-middle-text-wrap').css('margin-left','50px');
				}
				//set color level
				$(element).find('.e-prog-graph-right-inside').removeClass("background-level-1 background-level-2 background-level-3");
				$(element).find('.e-prog-graph-left-inside').removeClass("background-level-1 background-level-2 background-level-3");
				if(scope.$percent>=0 && scope.$percent<33){
					$(element).find('.e-prog-graph-right-inside').addClass("background-level-3");
					$(element).find('.e-prog-graph-left-inside').addClass("background-level-3");
				}else if(scope.$percent>=33 && scope.$percent<60){
					$(element).find('.e-prog-graph-right-inside').addClass("background-level-2");
					$(element).find('.e-prog-graph-left-inside').addClass("background-level-2");
				}else if(scope.$percent>=60){
					$(element).find('.e-prog-graph-right-inside').addClass("background-level-1");
					$(element).find('.e-prog-graph-left-inside').addClass("background-level-1");
				}
			});	
			
			//Size
			scope.sizing = function($w,$h){
				var deferrd = $q.defer();
				$(element).find(".e-prog-graph, .e-prog-middle").css({
					"-ms-transform":"scale("+$w+","+$h+")",
				    "-webkit-transform":"scale("+$w+","+$h+")",
				    "transform":"scale("+$w+","+$h+")"
				});
				deferrd.resolve();
				return deferrd.promise;
			}
			scope.$watch("width",function(newCol, oldCol, scope){
				if(!newCol)return;
				//width
				if(scope.width){
					//percent(%)
					if(scope.width.indexOf("%")>-1){
						scope.w = parseInt(scope.width.replace("%","")) * 0.01;
						scope.h = parseInt(scope.width.replace("%","")) * 0.01;
					}
					//pixels(px)
					else if(scope.width.toLowerCase().indexOf("px")>-1){
						scope.w = scope.width.replace("px","") / scope._w;
						scope.h = scope.width.replace("px","") / scope._w;
					}						
					scope.sizing(scope.w, scope.h).then(function(){
						$(element).children().css({
							width:$(element).find(".e-prog-graph").outerWidth() * scope.w,
							height:$(element).find(".e-prog-graph").outerHeight() * scope.w
						});
					});
					if(scope.w>1){
						$(element).children().css({
							"padding-top":scope.w*20,
							"padding-left":scope.w*20
						});
					}
				}else{
					//percent(%)
					if(scope.height.indexOf("%")>-1){
						scope.w = parseInt(scope.height.replace("%","")) * 0.01;
						scope.h = parseInt(scope.height.replace("%","")) * 0.01;
					}
					//pixels(px)
					else if(scope.height.toLowerCase().indexOf("px")>-1){
						scope.w = scope.height.replace("px","") / scope._w;
						scope.h = scope.height.replace("px","") / scope._w;
					}
					scope.sizing(scope.w, scope.h).then(function(){
						$(element).children().css({
							width:$(element).find(".e-prog-graph").outerWidth() * scope.w,
							height:$(element).find(".e-prog-graph").outerHeight() * scope.w
						});
					});
					if(scope.w>1){
						$(element).children().css({
							"padding-top":scope.w*20,
							"padding-left":scope.w*20
						});
					}
				}
			});
			scope.$watch("height",function(newCol, oldCol, scope){
				if(!newCol)return;
				//width
				if(scope.width){
					//percent(%)
					if(scope.width.indexOf("%")>-1){
						scope.w = parseInt(scope.width.replace("%","")) * 0.01;
						scope.h = parseInt(scope.width.replace("%","")) * 0.01;
					}
					//pixels(px)
					else if(scope.width.toLowerCase().indexOf("px")>-1){
						scope.w = scope.width.replace("px","") / scope._w;
						scope.h = scope.width.replace("px","") / scope._w;
					}
					scope.sizing(scope.w, scope.h).then(function(){
						$(element).children().css({
							width:$(element).find(".e-prog-graph").outerWidth() * scope.w,
							height:$(element).find(".e-prog-graph").outerHeight() * scope.w
						});
					});
					if(scope.w>1){
						$(element).children().css({
							"padding-top":scope.w*20,
							"padding-left":scope.w*20
						});
					}
				}else{
					//percent(%)
					if(scope.height.indexOf("%")>-1){
						scope.w = parseInt(scope.height.replace("%","")) * 0.01;
						scope.h = parseInt(scope.height.replace("%","")) * 0.01;
					}
					//pixels(px)
					else if(scope.height.toLowerCase().indexOf("px")>-1){
						scope.w = scope.height.replace("px","") / scope._w;
						scope.h = scope.height.replace("px","") / scope._w;
					}
					scope.sizing(scope.w, scope.h).then(function(){
						$(element).children().css({
							width:$(element).find(".e-prog-graph").outerWidth() * scope.w,
							height:$(element).find(".e-prog-graph").outerHeight() * scope.w
						});
					});
					if(scope.w>1){
						$(element).children().css({
							"padding-top":scope.w*20,
							"padding-left":scope.w*20
						});
					}
				}
			});
			//hide
//			scope.$watch("hide",function(newCol, oldCol, scope){
//				if(!newCol)return;
//				alert(scope.hide.txt+"\n"+scope.hide.toString());
//				if(scope.hide.txt)$(element).find(".e-prog-middle-text-wrap").hide();
//			});
			
		}
	};
}]);