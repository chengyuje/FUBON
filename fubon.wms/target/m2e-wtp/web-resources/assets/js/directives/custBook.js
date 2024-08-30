/**===============================================================================================
 						custom directive using jQuery-UI, AngularJS 												  
==================================================================================================
 @LastUpdate:
			 2016/05/17 ArthurKO Add directive
=================================================================================================*/
eSoafApp.directive('custBook', ['projInfoService', function(projInfoService) {
    return {
        restrict: 'E',
        transclude: true,
        replace: true,
        scope:{ 
        },
        template: function(element, attrs) {
        	// 2017/8/9 follow banner
        	var pri_id = projInfoService.getPriID()[0];
        	var IsMobile = projInfoService.getLoginSourceToken() == 'mobile';
            var htmlText =  '<div>'+
	            				'<div class="btn-group right" style="padding-left:5px">'+
	            					'<button data-toggle="dropdown" class="btn btn-info dropdown-toggle" style="border-radius: 5px;padding-top:4px">'+
	            						'&nbsp;<i class="glyphicon glyphicon-th-list" style="font-size:20px !important;color:#FFF !important"></i>'+
	            					'</button>'+
	            					'<ul style="cursor: default !important;-webkit-cursor: default !important;" class="dropdown-menu">'+
							             '<li ng-click="getCustHomeInclude(\'CRM610\')"><a><i class="glyphicon glyphicon-th-list" style="font-size:16px !important;color:#1c95d4 !important"></i><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;客戶首頁</span></a></li>'+
							             '<li ng-click="getCustHomeInclude(\'CRM800\')"><a><i class="glyphicon glyphicon-user" style="font-size:16px !important;color:#1c95d4 !important"></i><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;資產負債總覽</span></a></li>';
            if(!IsMobile && (pri_id == '002' || pri_id == '003')) {
            	htmlText +=				'<li class="dropdown dropdown-submenu"><a><i class="glyphicon glyphicon-usd" style="font-size:16px !important;color:#1c95d4 !important"></i><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;下單</span></a>'+
								            '<ul class="dropdown-menu">'+
												'<li><span style="font-weight:bold;cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;基金</span></li>'+
												'<li ng-click="order(\'SOT110\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;單筆申購</span></a></li>'+
												'<li ng-click="order(\'SOT120\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;定期(不)定額申購</span></a></li>'+
												'<li ng-click="order(\'SOT130\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;贖回/贖回再申購</span></a></li>'+
												'<li ng-click="order(\'SOT140\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;轉換</span></a></li>'+
												'<li ng-click="order(\'SOT150\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;約定事項變更</span></a></li>'+
												'<li><span style="font-weight:bold;cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;海外ETF/海外股票</span></li>'+
												'<li ng-click="order(\'SOT210\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;申購</span></a></li>'+
												'<li ng-click="order(\'SOT220\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;贖回</span></a></li>'+
												'<li><span style="font-weight:bold;cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;海外債</span></li>'+
												'<li ng-click="order(\'SOT310\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;申購</span></a></li>'+
												'<li ng-click="order(\'SOT320\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;贖回</span></a></li>'+
												'<li><span style="font-weight:bold;cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;SI</span></li>'+
												'<li ng-click="order(\'SOT410\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;申購</span></a></li>'+
												'<li ng-click="order(\'SOT420\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;贖回</span></a></li>'+
												'<li><span style="font-weight:bold;cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;SN</span></li>'+
												'<li ng-click="order(\'SOT510\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;申購</span></a></li>'+
												'<li ng-click="order(\'SOT520\')"><a><span style="cursor: pointer !important;font-size:16px !important;color:#1c95d4 !important">&nbsp;贖回</span></a></li>'+
											'</ul>'+
										'</li>'
            	
            }
            htmlText += '</ul></div></div>';
            return htmlText;
        },
        controller: 'BaseController',
        link: function (scope, element, attrs, ctlModel, transclude, $location) {
        	//set Index
//        	scope.index=-1;
//        	if(!angular.isDefined(scope.$parent.connector("get","SACRM112CUST_INDEX"))){
//        		//if(scope.$parent.connector("get","SACRM112CUST_INDEX")==undefined){
//        		scope.index=-1;
//        	}else{
//        		scope.index=scope.$parent.connector("get","SACRM112CUST_INDEX");
//        	}
//        	scope.$parent.connector("set","SACRM112CUST_INDEX",(++scope.index).toString());
        	
        	//Process function
        	//include page
        	scope.getCustHomeInclude = function(path) {
//       			if(!angular.isDefined(scope.$parent.connector("get","SACRM112URL"))){
//            		scope.$parent.connector("set","SACRM112URL",[]);
//            	}
//        		var obj = scope.$parent.connector("get","SACRM112URL"),url;
//				if(path=='SACRM112'){
//					url="assets/txn/"+path+"/"+path+"_VIEW.html";
//				}else if(path=='CPCAM435'){
//					url="assets/txn/"+path+"/"+path+"_VIEW.html";
//				}else{
        		if(path == 'CRM610'){
        			 scope.url="assets/txn/"+path+"/"+path+"_MAIN.html";
        			 
        		}else{
        			scope.url="assets/txn/"+path+"/"+path+".html";
        		}	
        		
//        			scope.$parent.connector("set","CRM610URL",scope.url);
        			scope.$emit("CRM610VO", {action:"set", type:"URL", data:scope.url});
        	};
        	
        	//下單
        	scope.order = function(path) {
        		var CRM_CUSTVO = scope.$parent.CRM_CUSTVO;
        		scope.connector('set','SOTCustID', CRM_CUSTVO.CUST_ID);//更改connector變數 mod:Kent
//        		scope.$parent.connector('set','ORG110_custID',);
        		scope.$parent.GeneratePage({'txnName':path,'txnId':path,'txnPath':[]});
        		scope.$parent.closeThisDialog('cancel');
        	}
        	     
        	
//				}
//				if(!angular.isDefined(scope.$parent.connector("get","SACRM112URL")[scope.index])){
//					obj.push(url);
//				}else{
//					obj[scope.index]=url;
//				}
//				scope.$parent.connector("set","SACRM112URL",obj);
//        	}
//        	scope.getCustHomeInclude('SACRM112');
        	//Watch
        	//set custBook title name
//    		scope.$watchCollection('connector("get","SACRM112CUST_NAME")', function(newCol, oldCol, scope) {
//	    		if(newCol == oldCol) {return};
//	    		if(!angular.isDefined(scope.$parent.connector("get","SACRM112CUST_NAME"))) {return};
//	    		
//	    		;
//    		});
//        	scope.$parent.$watch('connector("get","SACRM112CUST_NAME").length', function (newValue, oldValue) {
//        		if( !angular.isDefined(oldValue)){
//        			oldValue=-1;
//        		}
//        		if( !angular.isDefined(newValue)){
//        			newValue=-1;
//        		}
//        		if(newValue>oldValue && scope.index==-1){
//        			scope.index=scope.$parent.connector("get","SACRM112CUST_NAME").length-1;
//        			scope.name=scope.$parent.connector("get","SACRM112CUST_NAME")[scope.index];
//        		}
//    		});
        	
        	//CSS
//        	$("#cust-book").css("z-index","101");
//        				   .css("position","fixed")
//			 			   .css("top","80px")
//			 			   .css("right","100px")
//			 			   .css("padding", "0");
        	
        }
    };
    
}]);