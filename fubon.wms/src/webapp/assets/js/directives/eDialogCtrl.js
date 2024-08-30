/**================================================================================================
@program: eDialogCtrl.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('modalHeader', ["sysInfoService", "$rootScope", "ngDialog", "$timeout", function(sysInfoService, $rootScope, ngDialog, $timeout) {
	return {
		restrict: 'C',
		scope: {
		},
		controller: 'BaseController',
		link: function(scope, element, attrs) {
			
			/* [check out] */
			/* exclude items */
			if(sysInfoService.Exclude.title.indexOf($(element).children().text())!=-1 ||
			   sysInfoService.Exclude.controller.indexOf($rootScope.localCtrl)!=-1
			){
				$(element).parent().parent("[class*=modal-dialog]:first").find(".modal-content:first").css("overflow","hidden");
				$(element).parent().parent("[class*=modal-dialog]:first").find(".modal-content:first").css("height","auto");
				return;
			}
			/* confirm */
			if(attrs['class'].indexOf('esoaf-confirm')>-1?true:false){
				$(element).parent().parent("[class*=modal-dialog]:first").find(".modal-content:first").css("overflow","hidden");
				$(element).parent().parent("[class*=modal-dialog]:first").find(".modal-content:first").css("height","auto");
				return;
			}
			
			/*
			 * [Array Defined]
			 * 
			 * dialog[0] : dialog
			 * dialog[1] : wapper
			 * dialog[2] : header
			 * 
			 * */
			var dialog = ["#"+ngDialog.latestID,
			              $(element).parent().parent("[class*=modal-dialog]:first"),
			              $(element)],
			    chk = false, re = false, gator = sysInfoService.getNavigator() || false,
			    $css = {"width":"","height":"","max-width":"","max-height":"","margin":"","position":"","top":"","right":"","bottom":"","left":""};
			
			/* [running function] */
			dialogSave($rootScope, dialog, chk, ngDialog.latestID);
			dialogIcon(scope, dialog);
			dialogMove(scope, dialog);
			dialogSize(scope, dialog, $rootScope, sysInfoService, $timeout);
			dialogSort(scope, dialog, element, gator, $timeout);
			
			/** Watch **/
			dialog[1].css("max-width", ($('html').width()-1));
			dialog[1].on("remove", function () {
				if(!re){
					re = true;
					DialogRemove($rootScope, dialog);
				}
			});
			//btn-close
			element.find(".close").on("click", function(){
				if($(dialog[1]).length)$(dialog[0]).remove();
			});
			//btn-zoom-in
			element.find(".zoom-in").on("click", function() {
				dialog[1].hide(500, settleVisible());
			});
			//btn-zoom-out
			element.find(".zoom-out").on("click", function() {
				//initialize
				$css["width"] = $css["width"] || dialog[1].width();
				$css["max-width"] = $css["max-width"] || dialog[1].css("max-width");
				$css["margin"] = $css["margin"] || dialog[1].css("margin");
				$css["top"] = $css["top"] || dialog[1].css("top");
				$css["right"] = $css["right"] || dialog[1].css("right");
				$css["bottom"] = $css["bottom"] || dialog[1].css("bottom");
				$css["left"] = $css["left"] || dialog[1].css("left");
				//switch
				if(element.find(".zoom-out>i").hasClass("glyphicon glyphicon-unchecked")===true){
					dialog[1].css({
						"width":"100%",
					   "margin":"auto",
				   "margin-top":"10px",
					"max-width":"none",
					 	  "top":"0",
					    "right":"0",
					   "bottom":"0",
					     "left":"0"
					});
					element.find(".zoom-out>i").removeClass("glyphicon glyphicon-unchecked").addClass("glyphicon glyphicon-modal-window");
				}else{
					dialog[1].css({
						"width":$css["width"],
					   "margin":$css["margin"],
					"max-width":$css["max-width"],
					      "top":$css["top"],
					    "right":$css["right"],
					   "bottom":$css["bottom"],
					     "left":$css["left"]
					});
					element.find(".zoom-out>i").removeClass("glyphicon glyphicon-modal-window").addClass("glyphicon glyphicon-unchecked");
				}
			});
			
			function dialogSave($rootScope, dialog, chk, id) {
				if($rootScope.$counter.total.length>0){
					for(var i=0; i<$rootScope.$counter.total.length; i+=1) {
						if($rootScope.$counter.total[i].element == dialog[1]) {
							chk=i;
							break;
						}
					};
					if(chk){
						$rootScope.$counter.total.splice(chk,1,{"id": id, "element": dialog[1], "title": dialog[2].find("h4").text()});
					}else{
						$rootScope.$counter.total.push({"id": id, "element": dialog[1], "title": dialog[2].find("h4").text()});
					};
				}else{
					$rootScope.$counter.total.push({"id": id, "element": dialog[1], "title": dialog[2].find("h4").text()});
				}
			}
			function dialogIcon(scope, dialog) {
				var e = $(dialog[2]) || false;			       
				if(e){
					e.find(".close")
					 .after("<button class='zoom-in'><i class='glyphicon glyphicon-minus'></i></button>")
					 .after("<button class='zoom-out'><i class='glyphicon glyphicon-unchecked'></i></button>");
				}
			}
			function dialogMove(scope, dialog) {
				/** move **/
				/* version: 1 */
				var _move = false, _x, _y;
				dialog[2].css("cursor","pointer");
				dialog[2].on("mousedown", function(e) {
					_move = true;
					_x = e.pageX - parseInt($(dialog[1]).css("left"));
					_y = e.pageY - parseInt($(dialog[1]).css("top"));
					dialog[2].css("cursor","move");
				}); 
				$(document).on("mousemove", function(e) {
					if(_move){
						var x = e.pageX - _x, y = e.pageY - _y;
						$(dialog[1]).css({top:y, left:x});
					}
				}).on("mouseup", function() {
					_move = false;
					dialog[2].css("cursor","pointer");
				});
				/** settle back **/
				$("body").on("mouseleave", function(e){
					$(document).trigger("mouseup");
				});
			}
			function dialogSort(scope, dialog, element, gator, $timeout) {
				$(dialog[1]).on("click", function() {
					$(".ngdialog").css("z-index","589");
					$(dialog[0]).css("z-index","590");
				});
			}
			function dialogSize(scope, dialog, $rootScope, sysInfoService, $timeout) {
				
				//resizable
				if(type(dialog[1].attr("resizable"))){
					if(dialog[1].find(".modal-content:first>*").hasClass(".esoaf-confirm")===false){
						dialog[1].find(".modal-content:first").resizable({
							minWidth: 200,
							minHeight: 200
						});
					}
				}
				
				//when double click
				dialog[2].not(".zoom-in").not(".zoom-out").on("dblclick", function() {
					  dialog[1].hide(500, settleVisible());
				});
				
				//when hover
				dialog[2].attr("data-toggle","tooltip").attr('title', '雙擊滑鼠隱藏或展開彈跳視窗').tooltip({
					position: { at: "center top+52" }
				});

			}			
			function settleVisible() {
				$timeout(function(){
					var tmp = $rootScope.$counter.visit || false;
					if(tmp) {
						for(var i=0; i<tmp.length; i+=1) {
							if(tmp[i].element == dialog[1]) {
								return;
							}
						}
						tmp.push({"name": dialog[2].find("h4").text(), "element": dialog[1]});
					}else{
						tmp = [{"name": dialog[2].find("h4").text(), "element": dialog[1]}];
					}
					$rootScope.$counter.visit = tmp;
				}, 500);
			}
			function DialogRemove($rootScope, dialog) {
				for(var i=0; i<$rootScope.$counter.total.length; i+=1){
					if($rootScope.$counter.total[i].element == dialog[1]){
						$rootScope.$counter.total.splice(i,1);
						break;
					}
				}
				for(var i=0; i<$rootScope.$counter.visit.length; i+=1){
					if($rootScope.$counter.visit[i].element == dialog[1]){
						$rootScope.$counter.visit.splice(i,1);
						break;
					}
				}
			}
			
		}
	};
}]);