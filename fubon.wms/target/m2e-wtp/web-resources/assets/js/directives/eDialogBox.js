/**================================================================================================
@program: eDialogbox.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eDialogbox', ['$rootScope','$filter', function($rootScope, $filter) {
    return {
        restrict: 'E',
        transclude: true,
        replace: true,
        template: function(element, attrs) {
            var htmlText =  '<div id="DialogBox_Outside" style="pointer-events:all;-webkit-pointer-events:none;">'+
	        			   		'<span ng-show="$counter.visit.length>0" id="DialogBox_Wrapper"'+
	        			   			'style="animation: ngdialog-fadein 0.5s;padding:14px 6px 14px 6px;pointer-events:all;margin:0px auto;border:3px solid #6fb3e0;border-radius:.2em;background:transparent">'+
	        						'<span style="animation: ngdialog-fadein 0.5s;cursor:pointer;margin:3px;padding:6px;color:#FFF;border-radius:.2em;background:#6fb3e0;font-size:14px;pointer-events:all;"'+
	        						'ng-repeat="row in $counter.visit" ng-click="openDialog(row)" title="{{row.name}}"'+
	        						'><i class="glyphicon glyphicon-file"></i>&nbsp;{{row.name | smallTitle}}'+
	        						'</span>'+
	        					'</span>'+
        					'</div>'
            				;
            return htmlText;
        },
//        controller: 'InitController',
        link: function (scope, element, attrs, ctlModel, transclude) {
        	
        	/** Settle **/
        	var e = ["#DialogBox_Outside", "#DialogBox_Wrapper", "#DialogBox_Boxes"];

        	/** function **/
        	//events
        	$(e[1]).on('DOMNodeInserted',function(eve) {
        		if (eve.type == 'DOMNodeInserted') {
        			$(eve.target).animate({color:'hsla(350,99%,55%,0.9)'},300).animate({color:'#FFF'},300).animate({color:'hsla(350,99%,55%,0.9)'},300).animate({color:'#FFF'},300);
        			$(eve.target).tooltip({
        				tooltipClass: "e-tooltip-dialogbox",
        				position: { at: "center-25 bottom+15" }
        			});
        		}
        	});    
        	//calls
        	scope.openDialog = function(row) {
        		$(row.element).show(500, ChgVisible(row.element));
        	};
        	
        }
    };
    
    function ChgVisible(e) {
    	var total = $rootScope.$counter.visit, counts = $rootScope.$counter.visit.length;
    	for(var i=0;i<counts; i+=1){
    		if(total[i].element == e){
    			$rootScope.$counter.visit.splice(i,1);
    			break;
    		}
    	}
    };
    
}]);