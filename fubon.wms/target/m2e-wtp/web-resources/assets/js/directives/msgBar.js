/**================================================================================================
@program: msgBar.js
@description:
@version: 1.0.20170601
=================================================================================================*/
eSoafApp.directive('msgBar', ['$timeout','$interval','sysInfoService','socketService', function($timeout,$interval,sysInfoService,socketService) {
    return {
        restrict: 'E',
        transclude: true,
        replace: true,
        scope:{
        },
        template: function(element, attrs) {
            var htmlText =  '<div>'+
	            				'<div id="mwt_fb_tab"><a class="left_no"><span><i class="glyphicon glyphicon-chevron-right mwt_btn_color"></i></span><span class="mwt_btn_color">訊</span><span class="mwt_btn_color">息</span><span class="badge" ng-if="msglist.length>0" ng-bind="msglist.length"></span></a></div>'+
		            			'<div id="mwt_mwt_slider_scroll">'+
			            			'<div id="mwt_slider_content">'+
				            			'<div class="bootstrap-admin-panel-content in">'+
					            			'<div class="row">'+
					            				'<div class="col-lg-1 col-md-1 col-sm-1"></div>'+
					            				'<div id="mwt_msg_panl" class="col-lg-11 col-md-11 col-sm-11">'+
					            					'<div id="e-msgbar-clear" class="text-right" ng-if="msglist.length>0"><span ng-click="remove(null,true)"><i class="glyphicon glyphicon-trash"></i> 全部清除</span></div>'+
					            					'<div ng-init="insert(row)" id="mwt_msg_box" style="border-radius:5px!important;line-height:25px;text-align:left;animation: ngdialog-fadein 0.5s;" ng-repeat="row in msglist" ng-class="{\'alert alert-success\': row.type == \'S\',\'alert alert-info\': row.type == \'N\', \'alert alert-warning\': row.type==\'W\', \'alert alert-danger\': row.type==\'E\'}"><a class="close" style="color:#000;" ng-click="remove(row)">&times;</a><span ng-class="{\'label label-info\': row.type == \'N\', \'label label-success\': row.type == \'S\', \'label label-warning\': row.type == \'W\', \'label label-danger\': row.type == \'E\'}">{{row.label}}</span>&nbsp;{{row.message}}</div>'+
					            				'</div>'+
				            				'</div>'+
					            		'</div>'+
					            	'</div>'+
					            '</div>'+
            				'</div>'
            				;
            return htmlText;
        },
        controller: ['$rootScope','$scope', function($rootScope,$scope) {
        	/** Function **/
        	/* Initialize */
        	$scope.msglist = [];
        	$scope.getApServerInfo = function(){
        		socketService.sendRecv("CMFPG000", "getApServerInfo", "com.systex.jbranch.app.server.fps.cmfpg000.LoginPageVO", {})
        		.then(function(tota, isError) {
					if(!isError){
						var info = tota[0].body;
						sysInfoService.setApServerName(info.apServerName);
					}
        		});
			}
        	$scope.getApServerInfo();

        	/* Remove MsgBox */
        	$scope.remove = function(row, removeall){
        		if(removeall){
        			$scope.msglist = [];		
        		}else{
        			for(var i=0; i<$scope.msglist.length; i+=1){
        				if($scope.msglist[i] == row)$scope.msglist.splice(i, 1);
        			}; 
        		}    
        		//Close MsgBox
    			if($scope.msglist.length>0)return;
    			var w = $("#mwt_slider_content").width();
	        	$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 500 ,'swing');
	    		$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
	    		$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
	    		$("#mwt_fb_tab a").removeClass("left_go");
	    		$("#mwt_fb_tab a").addClass("left_no");
    		};
    		/** Get Message **/
    	    /* 秀訊息, 由 BaseController 負責呼叫 */
    		$rootScope.showStatusMsg = function(sMsg, status) {
    	    	if(sMsg){
    	    		//Check Type
    	    		var msgType = status.toString().toUpperCase().trim();
    	    		var msgBody = sMsg.toString().trim();
    	    		var tmpTP = '';
    	    		var tmpTXT = '';
    	    		//Switch Type
    	    		switch(msgType){
    		    		case 'SUCCESS':
    		    			tmpTP = 'S';
    	    				tmpTXT = '成功訊息';
    	    				break;
    		    		case 'NORMAL':
    		    			tmpTP = 'N';
    	    				tmpTXT = '一般訊息';
    	    				break;
    		    		case 'WARNING':
    		    			tmpTP = 'W';
    	    				tmpTXT = '警告訊息';
    	    				break;
    		    		case 'DANGER':
    	    				tmpTP = 'E';
    		    			tmpTXT = '錯誤訊息';
    		    			break;
    	    			default:
    	    				tmpTP = 'N';
	    					tmpTXT = '一般訊息';
	    					break;
    	    		};
    	    		$scope.msglist.unshift({'type': tmpTP, 'message': (angular.isDefined(sysInfoService.getApServerName()) && sysInfoService.getApServerName() != (null || "null") && sysInfoService.getApServerName() != "undefined" ? " ("+sysInfoService.getApServerName()+") ":"")+msgBody, 'label': tmpTXT});
    	    		if($scope.msglist.length>=3)$scope.msglist.length=3;
    	    	}
    		};
    		/** append row in list **/
    		$scope.insert = function(row){
    			var w = $("#mwt_slider_content").width(),
    				e = $("#mwt_msg_panl").children(":nth-child("+($scope.msglist.indexOf(row)+1)+")"),
    				_color, _color_chg;
    			switch(row.type){
	    			case 'N':
	    				_color = "rgba(42,176,237,1)";
	    				_color_chg = "hsla(350,99%,55%,0.9)";
	    				break;
	    			case 'S':
	    				_color = "rgba(0,36,0,1)";
	    				_color_chg = "hsla(350,99%,55%,0.9)";
	    				break;
	    			case 'W':
	    				_color = "rgba(237,144,23,1)";
	    				_color_chg = "hsla(350,99%,55%,0.9)";
	    				break;
	    			case 'E':
	    				_color = "rgba(231,56,39,1)";
	    				_color_chg = "rgb(51,51,51)";
	    				break;
    				default:
    					_color = "rgba(42,176,237,1)";
    					_color_chg = "hsla(350,99%,55%,0.9)";
    					break;
    			};
    			//level 1
    			$("#mwt_fb_tab>a>span>i, #mwt_fb_tab>a>span:not([class*='badge'])").animate({color:'hsla(350,99%,55%,0.9)'},300).animate({color:'#FFF'},300);
    			$("#mwt_fb_tab>a>span.badge").animate({color:'hsla(350,99%,55%,0.9)'},300).animate({color:'#ff8080'},300);
				//level 2
    			$(e).not("#e-msgbar-clear").animate({color:_color_chg},300).animate({color:_color},300).animate({color:_color_chg},300).animate({color:_color},300);
    			if ($("#mwt_mwt_slider_scroll").css('left') == '-'+w+'px')
				{
					$("#mwt_mwt_slider_scroll").animate({ left:'0px' }, 500 ,'swing');
					$("#mwt_fb_tab i").removeClass("glyphicon-chevron-right");
					$("#mwt_fb_tab i").addClass("glyphicon-chevron-left");
					$("#mwt_fb_tab a").removeClass("left_no");
					$("#mwt_fb_tab a").addClass("left_go");
					$scope.$autoclose(true);
				}
        	};
        	
        }],
        link: function (scope, element, attrs, ctlModel, transclude) {
        	
        	var w = $("#mwt_slider_content").width(),
        	isLocal = (sysInfoService.Exclude.IP.indexOf(document.location.hostname)>-1);

	        //auto close
        	scope.$autoclose = function(flag){
    			if(flag){
    				var t = 0, base = 30;
    				var closer = $interval(function(){
    					t++;
	        			if(t>=base){
	        				//Close
	            			$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 500 ,'swing');
	            			$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
	            			$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
	            			$("#mwt_fb_tab a").removeClass("left_go");
	            			$("#mwt_fb_tab a").addClass("left_no");
	            			t = 0;
	            			$interval.cancel(closer);
	        			}
	        		}, 1000);
    			}else{
    				if(closer)$interval.cancel(closer);
    			}
        	};
        		
        	//Animate style
        	$("#mwt_fb_tab").attr("title","滑鼠點擊展開或隱藏訊息視窗").on("mouseenter",function(){
        		$("#mwt_fb_tab").addClass("mwt_fb_tab-hover","fast");
        		$(".mwt_btn_color").animate({color:'hsla(350,99%,55%,0.9)!important'},600);
        	}).on("mouseleave",function(){
        		$("#mwt_fb_tab").removeClass("mwt_fb_tab-hover","fast");
        		$(".mwt_btn_color").animate({color:'#FFF!important'},600);
        	});
        	/** Open/Close by Click **/
        	$("#mwt_fb_tab").on("click", function(){
        		scope.$autoclose(false);
        		if ($("#mwt_mwt_slider_scroll").css('left') == '-'+w+'px')
        		{
        			//Open
        			$("#mwt_mwt_slider_scroll").animate({ left:'0px' }, 500 ,'swing');
        			$("#mwt_fb_tab i").removeClass("glyphicon-chevron-right");
        			$("#mwt_fb_tab i").addClass("glyphicon-chevron-left");
        			$("#mwt_fb_tab a").removeClass("left_no");
        			$("#mwt_fb_tab a").addClass("left_go");
        			scope.$autoclose(true);
        		}else{
        			//Close
        			$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 500 ,'swing');
        			$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
        			$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
        			$("#mwt_fb_tab a").removeClass("left_go");
        			$("#mwt_fb_tab a").addClass("left_no");
        		}
        	});
        	        	
        	/** 無訊息自動關閉 **/
        	// Add: 2016/07/27 ArthurKO
        	scope.$watch('msglist', function (newValue, oldValue) {
        		if(scope.msglist.length>0)return;
        		if(!($("#mwt_mwt_slider_scroll").css('left') == '-'+w+'px')){
        			$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 500 ,'swing');
    	    		$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
    	    		$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
    	    		$("#mwt_fb_tab a").removeClass("left_go");
    	    		$("#mwt_fb_tab a").addClass("left_no");
    	    		scope.$autoclose(false);
        		}
        	});

			/** 滑鼠移開移入 **/
//        	if(isLocal){
//        		$("#mwt_slider_content").mouseleave(function() {
//            		$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 500 ,'swing');
//            		$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
//            		$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
//            		$("#mwt_fb_tab a").removeClass("left_go");
//            		$("#mwt_fb_tab a").addClass("left_no");
//            	});
//        	}
        	
            /** 滑鼠移開點擊 **/
//        	$('body').click(function(e) {
//        	    var target = $(e.target);
//        	    if(!target.is('#mwt_fb_tab') &&
//            	   !target.is('#mwt_slider_content') &&
//            	   !target.is('#mwt_mwt_slider_scroll') &&
//            	   !target.is('#mwt_fb_tab') &&
//            	   !target.is('#mwt_msg_panl')
//        	    )
//        	    {
//        			$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 500 ,'swing');
//        			$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
//        			$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
//        			$("#mwt_fb_tab a").removeClass("left_go");
//        			$("#mwt_fb_tab a").addClass("left_no");
//        	    };
//        	});	  					
        	
        }
    };
    
}]);