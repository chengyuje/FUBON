/**================================================================================================
@program: eSorter.js
@description: 
@version: 1.0.20171011
=================================================================================================*/
eSoafApp.directive('eSorter', ["socketService", "$q", "$timeout", "$rootScope", "sysInfoService" , function(socketService, $q, $timeout, $rootScope, sysInfoService) {
	return {
		restrict: 'E',
		transclude: true,
        scope:{
          eConnect: "@?",   //connect to "eJumping"
        	 eMode: "@?",	//check component type
        	eInput: "=?",	//custom sorting way with own progarm.
           eOutput: "=?",	//check query for outside.
	    	  eCol: "@?",	//settle DB table Column Name.
	    	eQuery: "&?",	//settle JS Funtion Name.
	     eDisabled: "=?",	//disabled with sorter button function controll
	     	 eHide: "=?"	//visible with sorter button function controll
        },
        template: function(element, attrs) {
        	var htmlText = 
      			   '<div>'+
     			   '	<button id="eSorter_show" type="button" data-toggle="tooltip" style="text-shadow: 1px 1px 0 rgba(42,45,50,0.5);cursor:pointer!important;-webkit-cursor:pointer!important;margin:0!important;background:transparent!important;border:0px!important;vertical-align:middle!important;text-align:center!important;" ng-click="checker();sorting();">'+
     			   '		<label style="cursor:pointer;-webkit-cursor:pointer;vertical-align:middle!important;margin:0!important;text-align:center!important;font-weight:500!important" ng-transclude></label>'+
     			   '		<i id="eSorter_flag" style="cursor:pointer;-webkit-cursor:pointer;margin:0!important;top:2px!important;line-height:none!important;" ng-class="{\'glyphicon glyphicon-sort-by-attributes\':flag,\'glyphicon glyphicon-sort-by-attributes-alt\':!flag}"></i>'+
     			   '	</button>'+
     			   '	<label id="eSorter_hide" data-toggle="tooltip" class="eSorter-disabled" style="vertical-align:middle!important;margin:0!important;text-align:center!important;font-weight:500!important" ng-transclude></label><i id="eSorter_disabledIcon" class="glyphicon glyphicon-ban-circle" style="color:#ff0000;opacity:0.3!important;font-size:12px;pointer-events:none;"></i>'+
     			   '</div>'
     			;
        	return htmlText;
        },
		link: function(scope, element, attrs, OtherComponent, transclude) {

			/** <settle> **/
			// (1): [check mode]
			if(angular.isDefined(scope.eMode)){
				if(scope.eMode.trim().toLowerCase()!=="auto")scope.eMode = scope.eMode.trim().toLowerCase();
			}else{
				attrs.$set("eMode","auto");
			}
			// (2): [set sorting way]
			scope.flag = true;
			scope.status = false;
//			scope.eOutput = scope.eOutput || {};
//			scope.eOutput.eSwitch = false;
			scope.eSwitch = false;
			var connIdx = -1;
			// (3): [set initial with functions]
			// -disabled
			if(scope.eDisabled){
				$(element).find("#eSorter_show:first")
						  .css("color", "#F9F9F9")
						  .css("text-shadow","none")
				  		  .attr("disabled", true)
				  		  .attr("title", "排序功能限制使用。")
				  		  .tooltip();
				$(element).find("#eSorter_disabledIcon:first")
		  		  		  .css("display", "initial");
			}else{
				$(element).find("#eSorter_show:first")
						  .css("color", "#FFFFFF")
						  .css("text-shadow","1px 1px 0 rgba(42,45,50,0.5)")
						  .prop("disabled", false );
				$(element).find("#eSorter_disabledIcon:first")
						  .css("display", "none");
			}
			// -hide
			if(scope.eHide){
				$(element).find("#eSorter_show:first").css("display", "none");
				$(element).find("#eSorter_hide:first").css("display", "initial");
			}else{
				$(element).find("#eSorter_show:first").css("display", "initial");
				$(element).find("#eSorter_hide:first").css("display", "none");
			}
			// (4): System of directives
			// check "$sorter" is exist.
			if(scope.$parent.$sorter){
				// check "conn" is exist.
				if(scope.$parent.$sorter.conn.indexOf(scope.eConnect)!==-1){
					for(var i=0;i<scope.$parent.$sorter.conn.length;i++){
						if(scope.$parent.$sorter.conn[i]===scope.eConnect)connIdx+=1;
					}
					connIdx+=1;
				// check "conn" is not exist.
				}else{
					connIdx=0;
				}
				// setting attrs.
				scope.$parent.$sorter.conn.push(scope.eConnect);
				scope.$parent.$sorter.num.push(connIdx);
				scope.$parent.$sorter.chkidx.push(scope.eConnect+String(connIdx));
				scope.$parent.$sorter.chkpass.push(false);
			}else{
				connIdx=0;
				// creat attrs.
				scope.$parent.$sorter = {
					conn: [scope.eConnect],
					 num: [connIdx],
				  chkidx: [scope.eConnect+String(connIdx)],
				 chkpass: [false]
				}
			};


			/** <CSS Events> **/
			element.find("#eSorter_show:first").on("mouseenter",function(){
				element.find("#eSorter_show:first")
					   .attr("title","滑鼠點擊進行"+(scope.flag?"降冪":"升冪")+"排序。")
					   .tooltip();
				if($(element).find("#eSorter_show").hasClass("eSorter-main-desc")===true)return;
				if(!scope.flag)return;
				//setting CSS Style
//				element.find("#eSorter_show:first:not(.eSorter-main,.eSorter-main-desc)")
//					   .animate({'color':'hsla(255,90%,35%,0.9)'},"fast");
        	}).on("mouseleave",function(){
        		element.find("#eSorter_show:first")
        			   .attr("title","滑鼠點擊進行"+(scope.flag?"降冪":"升冪")+"排序。")
        			   .tooltip();
        		if($(element).find("#eSorter_show").hasClass("eSorter-main-desc")===true)return;
        		if(!scope.flag)return;
        		//setting CSS Style
//        		element.find("#eSorter_show:first:not(.eSorter-main,.eSorter-main-desc)")
//        			   .animate({'color':'#FFFFFF','text-shadow':'1px 1px 0 rgba(42,45,50,0.5)'},"fast");
        	});
			element.find("#eSorter_show:first").on("mouseup",function(){
				element.find("#eSorter_show:first").attr("title","滑鼠點擊進行"+(scope.flag?"降冪":"升冪")+"排序。")
					   .tooltip();
        	});
//			$("button:not(#eSorter_show)").on("click",function(){
//				scope.eSwitch=true;alert("onClick!");
//			});


			/** <Calls> **/
			scope.sorting = function(){
				// check sorter flag
				if(scope.flag===true){
					// check all sorter status
//					if($(element).parents("thead:first").find("#eSorter_flag:not([style*='display:none'])[class*='glyphicon-sort-by-attributes-alt']").length>0)return;
					// check changed status
					if(scope.$parent.$sorter.conn.indexOf(scope.eConnect)!==-1){
						for(var i=0;i<scope.$parent.$sorter.conn.length;i++){
							if(scope.$parent.$sorter.conn[i]===scope.eConnect && scope.$parent.$sorter.num[i]!==connIdx){
								scope.$parent.$sorter.chkpass[i]=true;
							}
						}
					}
					element.find("#eSorter_show:first").removeClass("eSorter-main").addClass("eSorter eSorter-main-desc");
					scope.status = false;
					scope.flag = false;
				}else{
					element.find("#eSorter_show:first").removeClass("eSorter-main-desc").addClass("eSorter eSorter-main");
					scope.status = true;
					scope.flag = true;
				}
				//CSS event
				$timeout(function(){element.find("#eSorter_show").trigger("blur");},500);
				// set current page
				var $currentIdx = $('e-jumping[e-connect='+scope.eConnect+']:not([style*="display:none"]) option[selected=selected]').attr("label")-1;



				// check component type
				if(scope.eMode==="auto"){
					/** [call sorter] **/
//					socketService.sendRecv("SorterInfo", "getVO", "com.systex.jbranch.common.sorterinfo.SorterInputVO", {'checkin':true, 'asc':scope.flag, 'column':scope.eCol.trim()})
//		    		.then(
//		    			function(oResp) {
//		    				if(oResp){
//		    					sortId = oResp[0].body;
//		    					scope.eInput.sortId = sortId;
//		    				}
//		    				//success things
//		    				if($currentIdx && $currentIdx>=0)scope.eInput.currentPageIndex = $currentIdx;
//		    				scope.eQuery();
//		    			},
//		    			function(oErr) {
//		    				//fail things
//		    			}
//		    		);

    				if($currentIdx && $currentIdx>=0){
    					scope.eInput.currentPageIndex = $currentIdx;
    				}

    				scope.eInput.asc = scope.flag;
    				scope.eInput.column = scope.eCol.trim();

    				scope.eQuery();

				}else{
					/** [call custom method] **/
					// check out
					if(!angular.isDefined(scope.eInput) || !angular.isDefined(scope.eCol) || !angular.isDefined(scope.eQuery))return;
					// settle
					scope.eInput.asc=scope.flag;
					scope.eInput.column=scope.eCol.trim();
					if($currentIdx && $currentIdx>=0)scope.eInput.currentPageIndex = $currentIdx;
					// call method
					scope.eQuery();
				}
			}
			/* <check query side> */
			scope.checker=function(){
				scope.eSwitch=true;
			}


			/** <Watch> **/
			/* <On call with disabled> */
			scope.$watch("eDisabled",function(){
				if(scope.eDisabled){
					$(element).find("#eSorter_show:first")
							  .css("color", "#f9f9f9")
							  .css("text-shadow","none")
					  		  .attr("disabled", true)
					  		  .attr("title", "排序功能限制使用。")
					  		  .tooltip();
					$(element).find("#eSorter_disabledIcon:first")
			  		  		  .css("display", "initial");
				}else{
					$(element).find("#eSorter_show:first")
							  .css("color", "#FFFFFF")
							  .css("text-shadow","1px 1px 0 rgba(42,45,50,0.5)")
							  .prop("disabled", false );
					$(element).find("#eSorter_disabledIcon:first")
							  .css("display", "none");
				}
			});
			/* <On call with hidden> */
			scope.$watch("eHide",function(){
				if(scope.eHide){
					$(element).find("#eSorter_show:first").css("display", "none");
					$(element).find("#eSorter_hide:first").css("display", "initial");
				}else{
					$(element).find("#eSorter_show:first").css("display", "initial");
					$(element).find("#eSorter_hide:first").css("display", "none");
				}
			});
			/* <On Changed with Sorter status> */
			scope.$watch("flag",function(){
				if(scope.flag===true){
					if(scope.status===true){ // ASC
						   element.find("#eSorter_show:first")
								  .css("text-shadow","0 0 6px #FFFFFF")
								  .css("color", "#0000FF")
								  .removeClass("eSorter-main-desc");
						if(element.find("#eSorter_show:first").hasClass("eSorter-main")===false)element.find("#eSorter_show:first").addClass("eSorter-main");
					}else{ // Default
						   element.find("#eSorter_show:first")
						          .css("text-shadow","1px 1px 0 rgba(42,45,50,0.5)")
						          .css("color", "#FFFFFF")
						          .removeClass("eSorter-main eSorter-main-desc");
					}
				}else{ // DESC
					element.find("#eSorter_show:first")
						   .css("text-shadow","0 0 6px #FFFFFF")
						   .css("color", "#FF0000")
						   .removeClass("eSorter-main");
					if(element.find("#eSorter_show:first").hasClass("eSorter-main-desc")===false)element.find("#eSorter_show:first").addClass("eSorter-main-desc");
				}
			});
			/* <On call with reset> */
			scope.$parent.$watch("$sorter.chkpass["+scope.$parent.$sorter.chkidx.indexOf(scope.eConnect+String(connIdx))+"]",function(){
				if(scope.$parent.$sorter.chkpass[scope.$parent.$sorter.chkidx.indexOf(scope.eConnect+String(connIdx))]===true){
					scope.status=false;
					scope.flag=true;
					scope.$parent.$sorter.chkpass[scope.$parent.$sorter.chkidx.indexOf(scope.eConnect+String(connIdx))]=false;
					// reset
					element.find("#eSorter_show:first")
						   .css("text-shadow","1px 1px 0 rgba(42,45,50,0.5)")
					       .css("color", "#FFFFFF")
					       .removeClass("eSorter-main eSorter-main-desc");
				}
			});
			/* <check query for outside/inside> */
			scope.$watch("eOutput",function(oldValue,newValue){
				var chk=false;
				if(scope.eSwitch===false){
					if(chk)return;
					scope.status=false;
					scope.flag=true;
					element.find("#eSorter_show")
						   .css("text-shadow","1px 1px 0 rgba(42,45,50,0.5)")
						   .css("color", "#FFFFFF")
						   .removeClass("eSorter-main eSorter-main-desc");
				}else{
					chk=true;
					scope.eSwitch=false;
				}
			});
			/* <check query for outside/inside - getEvent Que from ejumping> */
			$rootScope.$on(scope.eConnect, function(event,args) {
				if(scope.eSwitch===false)scope.eSwitch=true;
			});
		}
	};
}]);