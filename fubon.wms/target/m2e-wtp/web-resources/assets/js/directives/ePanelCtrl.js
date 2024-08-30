/**================================================================================================
@program: ePanelCtrl.js
@description:
@version: 1.0.20170525
=================================================================================================*/
eSoafApp.directive('panelDefault', ["$timeout", function($timeout) {
	return {
		restrict: 'C',
		scope: {},
		controller: 'BaseController',
		link: function(scope, element, attrs) {
			
			var e = [element, 
			         $(element).find(".panel-heading"),
			         $(element).find(".bootstrap-admin-panel-content")];
		
			_ePanelCtrl_events(e, $timeout);
			
		}
	};
}]);
function _ePanelCtrl_events(e, $timeout) {
	//double click
	$(e[1]).on("dblclick", function(eve){
//		$(e[2]).toggle(500);
		$(e[2]).slideToggle(500);
	});
	//tip tool
//	$(e[1]).attr('title', '雙擊滑鼠收合或展開面板');
	$(e[1]).attr("data-toggle","tooltip").attr('title', '雙擊滑鼠收合或展開面板').tooltip();
	//mouse enter/leave
	$(e[1]).on("mouseenter", function(){
		$(e[1]).css("cursor","pointer");
	}).on("mouseleave",function(){
		$(e[1]).css("cursor","default");
	});
}