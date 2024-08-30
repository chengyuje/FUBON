/**================================================================================================
@program: eJumping.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eJumping', ['projInfoService', '$q', 'socketService', '$rootScope', 'sysInfoService' , 
    function(projInfoService, $q, socketService, $rootScope , sysInfoService) {
	    return {
			restrict: 'E',
			scope:
			{
				eConnect		: '@?' ,//connect to "eSorter"
				input			: '=?' ,//java 端 inputVO，需extends PagingInputVO
				output			: '=?' ,//java 端 outputVO，需extends PagingOutputVO
				query			: '&?' ,//controller function : query
				list			: '=?' ,
				results			: '=?' ,//get back list
				rowCountLimit	: '=?rowcountlimit' ,//pageCount || 10 (projInfoService)
				mode			: '@?' ,				
				theme			: '@?'					
	        },
	        template: function(element, attrs) {
	        	var htmlText = 
		        	'<div>'+
		        	'	<div style="text-align:left !important;height:30px !important;margin-bottom:5px !important;">'+
					'		<table style="display:block !important;">'+
		        	'			<tr>'+
		        	'				<td style="padding:0 3px 3px 0 !important;"><a style="font-size:10px !important;height:30px !important;border-bottom-right-radius:0 !important;border-top-right-radius:0 !important;" class="btn btn-info" ng-click="chkSorter();Paging(0, 0)"><i style="margin-right:2.5px !important;" class="glyphicon glyphicon-step-backward"></i>上一頁</a></td>'+	
		        	'				<td style="padding:0 3px 3px 0 !important;"><a style="font-size:10px !important;height:30px !important;border-bottom-left-radius:0 !important;border-top-left-radius:0 !important;" class="btn btn-info" ng-click="chkSorter();Paging(1, 1)">下一頁<i style="margin-left:2.5px !important;" class="glyphicon glyphicon-step-forward"></i></a></td>'+
		        	'				<td style="padding:0 3px 3px 0 !important;"><select class="form-control" style="text-align:center !important;width:75px !important;height:30px !important;display:inline !important;" ng-options="page as page.text for page in pages" ng-model="selectedPage" ng-change="chkSorter();jumpPage(selectedPage.text - 1)"></select></td>'+	
		        	'				<td style="padding:0 3px 3px 0 !important;"><p style="display:inline !important;height:30px !important;">共&nbsp;</p></td>'+
		        	'				<td style="padding:0 3px 3px 0 !important;"><label style="display:inline !important;height:30px !important;">{{totalPage}}</label></td>'+
		        	'				<td style="padding:0 3px 3px 0 !important;"><p style="display:inline !important;height:30px !important;">&nbsp;頁&nbsp;/&nbsp;共&nbsp;</p></td>'+
		        	'				<td style="padding:0 3px 3px 0 !important;"><label style="display:inline !important;height:30px !important;">{{totalRecord}}</label></td>'+
		        	'				<td style="padding:0 3px 3px 0 !important;"><p style="display:inline !important;height:30px !important;">&nbsp;筆資料</p></td>'+
		        	'			</tr>'+
		        	'		</table>'+
		        	'	</div>'+
					'</div>';
	        	return htmlText;
	        },
	        link: function (scope, element, attrs, ctlModel) {
	        	
	        	/** <methods> **/
	        	function _eJumping_watches() {
	        		// by initialize input condition changed
	        		scope.$watchCollection('input', function(newCol, oldCol, scope) {
	        			if (newCol == oldCol) {
	        				return;
	        			}
	        			if (!angular.isDefined(scope.mode)) {
	        				_eJumping_initChg();
	        				return;
	        			}
	        			if (scope.mode.trim() != "1") {
	        				_eJumping_initChg();
	        				return;
	        			}
	        		});
	        		// by on query
	        		scope.$watchCollection('output', function(newCol, oldCol, scope) {
	        			if(newCol == oldCol){
	        				return;
	        			}
	        			if(!angular.isDefined(scope.mode)){
	        				_eJumping_counts();
	        				return;
	        			}
	        			if(scope.mode.trim()==="1"){
	        				scope.input.currentPageIndex = 0;
	        				_eJumping_getList();
	        	    		_eJumping_counts();
	        	    		_eJumping_totaQuery();
	        			}else{
	        				_eJumping_counts();
	        			}
	        		});
	        		// by initialize query condition
	        		if(angular.isDefined(scope.mode)){
	        			if(scope.mode.trim() === "1" || scope.mode.trim() === "2"){
	            			scope.$watch('list', function (newValue, oldValue){
	            				if(!newValue || newValue.length == 0){
	            					return;
	            				}
	            				if(newValue == oldValue){
	            					return;
	            				}
	            				if(scope.chkR === 2){
	            					return;
	            				}
	            				_eJumping_result();        		
	            			}, true);
	            		}
	        		}
	        		// by initialize query condition
	        		scope.$watch('list', function (newValue, oldValue) {
	        			if(newValue == oldValue){
	        				return;
	        			}
	        			if(!newValue || newValue.length == 0) {
	        				_eJumping_init();
	        				return;
	        			}
	        			_eJumping_counts();
	        		});
	        	}
	        	// <methods: mode all>
	        	function _eJumping_init() {
	        		scope.input = scope.input || {};
	        		scope.rowCountLimit = scope.rowCountLimit || defaultLimit;
	        		scope.input.pageCount = scope.rowCountLimit || 0;
	        		scope.input.currentPageIndex = 0;
	        		scope.pages = [];
	        		scope.totalPage = "0";
	        		scope.totalRecord = "0";
	        		scope.selectedPage = [];
	        		_eJumping_initResult(); //Modify: 2016/07/13 David
	        		//list
	        		if(!angular.isDefined(scope.mode)){
	        			return;
	        		}
	        		if(scope.mode!="1"){
	        			return;
	        		}
	        		scope.list = scope.list || []; // DataSource list
	        		scope.tota = []; 			   // Template list
	        	};
	        	// <methods: mode all, change>
	        	function _eJumping_initChg() {
	        		scope.input = scope.input || {};
	        		scope.rowCountLimit = scope.rowCountLimit || defaultLimit;
	        		scope.input.pageCount = scope.rowCountLimit || 0;
	        		scope.input.currentPageIndex = 0;
	        	}
	        	// <methods: mode all>
	        	function _eJumping_counts() {
	        		if(!scope.list) scope.list = [];
	        		
	        		var Limit = scope.rowCountLimit || defaultLimit,
	        		    total = scope.list.length;
	        		if(angular.isDefined(scope.tota) && scope.mode.trim()=="1") total = scope.tota.length;
	        		
	        		if(angular.isDefined(scope.mode)){
	        			switch(scope.mode.trim()){
	        				case "0": //Data Source are Limited on BackEnd
	        					scope.totalPage = scope.output.totalPage;
	        				    scope.totalRecord = scope.output.totalRecord;
	        				    scope.pages = [];
	        				    for (var i = 1; i <= scope.output.totalPage; i++) {
	        					    scope.page = {name : "page " + i, text : i};
	        					    scope.pages.push(scope.page);
	        				    }
	        				    scope.selectedPage = scope.pages[scope.output.currentPageIndex];
	        				    break;
	        				case "1": //Data Source are not Limit on BackEnd
	        					scope.totalPage = Math.ceil(total/Limit);
	        					scope.totalRecord = total;
	        					scope.pages = [];
	        					for (var i=1; i<=scope.totalPage; i+=1) {
	        					    scope.page = {"text": i};
	        					    scope.pages.push(scope.page);
	        				    }
	        					scope.selectedPage = scope.pages[scope.input.currentPageIndex];
	        					break;
	        				case "2": //Data Source are Limited on BackEnd
	        					scope.totalPage = scope.output.totalPage;
	        				    scope.totalRecord = scope.output.totalRecord;
	        				    scope.pages = [];
	        				    for (var i = 1; i <= scope.output.totalPage; i++) {
	        					    scope.page = {name : "page " + i, text : i};
	        					    scope.pages.push(scope.page);
	        				    }
	        				    scope.selectedPage = scope.pages[scope.output.currentPageIndex];
	        				    break;
	        			};
	        		}else{
	        			scope.totalPage = scope.output.totalPage;
	        		    scope.totalRecord = scope.output.totalRecord;
	        		    scope.pages = [];
	        		    for (var i = 1; i <= scope.output.totalPage; i++) {
	        			    scope.page = {name : "page " + i, text : i};
	        			    scope.pages.push(scope.page);
	        		    }
	        		    scope.selectedPage = scope.pages[scope.output.currentPageIndex];
	        		}
	        		
	        	};
	        	// <methods: mode2>
	        	function _eJumping_getList() {
	        		scope.tota = angular.copy(scope.list);
	        	};
	        	// <methods: mode1>
	        	function _eJumping_totaQuery() {
	        		var Limit = scope.rowCountLimit || defaultLimit;
	        		var str = [0,0]; //str[0]: start, str[1]: end
	        		if(scope.input.currentPageIndex>0){
	        			str[0] = scope.input.currentPageIndex * Limit; // 1*10 = 10
	        		}
	        		if((scope.tota.length-(str[0]))>=Limit){ // 20-10 >= 10
	        			str[1] = str[0] + Limit-1; // 10+10 = str[1]
	        		}else{
	        			str[1] = scope.tota.length - 1; //
	        		}        		
	        		scope.list = [];        		
	        		for(var i=str[0]; i<=str[1]; i+=1){
	        			scope.list.push(scope.tota[i]);
	        		};
	        	};
	        	// <methods: mode2>
	        	function _eJumping_initResult() {
	        		scope.ary.pg = [];
	        		scope.ary.data = [];
	        		scope.results = [];
	        	}
	        	// <methods: mode2>
	        	function _eJumping_result() {
	        		if(!angular.isDefined(scope.results)){
	        			return;
	        		}
	        		var chk = false, spg;
	        		spg = scope.output.currentPageIndex || scope.input.currentPageIndex;
	        		if(scope.list.length>0){
	        			if(scope.ary.pg.indexOf(spg) == -1){
	        				scope.ary.pg.push(spg);
	        				scope.ary.data.push(angular.copy(scope.list));
	        			}else{
	        				scope.ary.data.splice(scope.ary.pg.indexOf(spg), 1, angular.copy(scope.list));
	        			}
	        			scope.results.length = 0;
	        			for(var i=0; i<scope.ary.pg.length; i+=1){
	        				for(var p=0; p<scope.ary.data[i].length; p+=1){
	        					scope.results.push(scope.ary.data[i][p]);
	        				}
	        			}
	        		}
	        	}
	        	// <methods: mode2>
	        	function _eJumping_settingView(index) {
	        		if(!angular.isDefined(scope.results)){
	        			return;
	        		}
	        		if(scope.results.length == 0 || scope.ary.pg.length == 0 || scope.ary.pg.indexOf(index) == -1){
	        			return;
	        		}
	        		scope.list = [];
	        		for(var i=0; i<scope.ary.data[scope.ary.pg.indexOf(index)].length; i+=1){
	        			scope.list.push(scope.ary.data[scope.ary.pg.indexOf(index)][i]);
	        		}
	        		return;
	        	}
	        	
	        	/** <initialize> **/
	        	var defaultLimit = parseInt(projInfoService.getSinglePageRows()[0].LABEL),
	        	$sorter_matcher = {side:{current:false, before:false},
	        						col:{current:false, before:false}},
	        	$sorter = function(){
	        		var deferred = $q.defer();
	        		

	    			
	        		// ASC
	        		$('e-sorter[e-connect='+scope.eConnect+']:not([style*="display:none"], [e-hide=true]) #eSorter_show.eSorter-main #eSorter_flag.glyphicon-sort-by-attributes:not([style*="display:none"])').each(function(i) {
	        			var $col = $(this).parents('e-sorter[e-connect='+scope.eConnect+']:not([style*="display:none"], [e-hide=true]):first').attr("e-col").trim(),
	    				   $attr = $(this).parents('e-sorter[e-connect='+scope.eConnect+']:not([style*="display:none"], [e-hide=true]):first').attr("e-mode");
		    			// check component type
	        			
	        			if($attr){
							/** [call sorter] **/
//							socketService.sendRecv("SorterInfo", "getVO", "com.systex.jbranch.common.sorterinfo.SorterInputVO", {'checkin':true, 'asc':true, 'column':$col},function(tota, isError) {
//			        			if(tota[0].body){
//			        				sortId = tota[0].body;
//			    					scope.input.sortId = tota[0].body;
//			    				}
//							});
	        				
	        				scope.input.asc = true;
							scope.input.column = $col;
						}else{
							/** [call custom method] **/
							// settle
							
						}
						// sorter matching
						$sorter_matcher = {
							side: {
							    before: $sorter_matcher.side.before || "ASC",
							   current: "ASC"
							},
							col: {
								before: $sorter_matcher.col.before || $col,
							   current: $col
							}	
						}
	        		});
	        		// DESC
	        		$('e-sorter[e-connect='+scope.eConnect+']:not([style*="display:none"], [e-hide=true]) #eSorter_show.eSorter-main-desc #eSorter_flag.glyphicon-sort-by-attributes-alt:not([style*="display:none"])').each(function(i) {
	        			var $col = $(this).parents('e-sorter[e-connect='+scope.eConnect+']:not([style*="display:none"], [e-hide=true]):first').attr("e-col").trim(),
	        				$attr = $(this).parents('e-sorter[e-connect='+scope.eConnect+']:not([style*="display:none"], [e-hide=true]):first').attr("e-mode");
	        			// check component type
	    				if($attr){
	    					/** [call sorter] **/
	    					scope.input.asc = false;
	    					scope.input.column=$col;
	    					
	    				}else{
	    					/** [call custom method] **/
	    					scope.input.asc=false;
	    					scope.input.column=$col;
	    				}
	    				// sorter matching
	    				$sorter_matcher = {
							side: {
							    before: $sorter_matcher.side.before || "DESC",
							   current: "DESC"
							},
							col: {
								before: $sorter_matcher.col.before || $col,
							   current: $col
							}	
						}
	        		});
	        		switch($sorter_matcher.side.current){
		        		case "ASC":
		        			if($sorter_matcher.side.current !== $sorter_matcher.side.before){
		    					$sorter_matcher.side.before = angular.copy($sorter_matcher.side.current);
		    					$sorter_matcher.col.before = angular.copy($sorter_matcher.col.current);
		    					_eJumping_initResult();
		    				}
		    				if($sorter_matcher.side.current === $sorter_matcher.side.before && $sorter_matcher.col.current !== $sorter_matcher.col.before){
		    					$sorter_matcher.side.before = angular.copy($sorter_matcher.side.current);
		    					$sorter_matcher.col.before = angular.copy($sorter_matcher.col.current);
		    					_eJumping_initResult();
		    				}
		    				deferred.resolve();
		    				break;
		        		case "DESC":
		        			if($sorter_matcher.side.current !== $sorter_matcher.side.before){
		    					$sorter_matcher.side.before = angular.copy($sorter_matcher.side.current);
		    					$sorter_matcher.col.before = angular.copy($sorter_matcher.col.current);
		    					_eJumping_initResult();
		    				}
		    				if($sorter_matcher.side.current === $sorter_matcher.side.before && $sorter_matcher.col.current !== $sorter_matcher.col.before){
		    					$sorter_matcher.side.before = angular.copy($sorter_matcher.side.current);
		    					$sorter_matcher.col.before = angular.copy($sorter_matcher.col.current);
		    					_eJumping_initResult();
		    				}
		    				deferred.resolve();
		    				break;
		    			default:
		    				deferred.resolve();
		    				break;
	        		}
	        		return deferred.promise;
	    		}; 
	    		scope.chkSorter = function(){
	    			$rootScope.$emit(scope.eConnect, true);
	    		}
	        	scope.chkR = 1; //default watch index 1: work for ones
	        	scope.ary = {
					"pg": [], //number of current page
				  "data": []  //data of current page
	        	};
	        	_eJumping_init();
	        	
	        	/** <Watching> **/
	    		_eJumping_watches();
	        	
	        	/** <Calls> **/
	        	scope.Paging = function(fn, index) {
	            	switch(fn){
	    	        	case 0: // [prevPage]
	    	        		var prevPageIndex = scope.selectedPage.text - 2;
	    	    			if (prevPageIndex >= 0) {
	    	    				scope.jumpPage(prevPageIndex);
	    	    			} else {
	    	    				return;
	    	    			}
	    	    			break;
	    	        	case 1: // [nextPage]
	    	        		var nextPageIndex = scope.selectedPage.text;
	    	        		var totaPage = scope.output.totalPage;
	    	        		if(angular.isDefined(scope.mode)){
	    	        			switch(scope.mode.trim()){
			    	        		case "0":
			    	        			totaPage = scope.output.totalPage;
			    	        			break;
			    	        		case "1":
			    	        			totaPage = scope.totalPage;
			    	        			break;
			    	        		case "2":
			    	        			totaPage = scope.output.totalPage;
			    	        			break;
		    	        			default:
		    	        				totaPage = scope.output.totalPage;
		    	        				break;
	    	        			};
	    	        		}else{
	    	        			totaPage = scope.output.totalPage;
	    	        		}
	    	    			if (nextPageIndex <= totaPage - 1) {
	    	    				scope.jumpPage(nextPageIndex);
	    	    			} else {
	    	    				return;
	    	    			}
	    	        		break;
	            	};
	    		};
	    		scope.jumpPage = function(index) {
	    			scope.input.pageCount = scope.rowCountLimit;
	    			scope.input.currentPageIndex = index;
	    			$sorter().then(function(){
	    				// check mode
	        			if(scope.mode){
	        				switch(scope.mode.trim()){
	    		    			case "0":
	    		    				scope.query();
	    		    				break;
	    		    			case "1":
	    		    				scope.chkR = 2;
	    		    				_eJumping_totaQuery();
	    		    				scope.selectedPage = scope.pages[index];
	    		    				_eJumping_settingView(index);
	    		    				scope.chkR = 0 // Modify: 2016/07/13 David
	    		    				break;
	    		    			case "2":
	    		    				scope.chkR = 2;
	    		    				if(scope.ary.pg.indexOf(index)===-1){
	    		    					scope.query();
	    		    				}else{
	    		    					scope.output.currentPageIndex = index;
	    		    				}													
	    		    				_eJumping_settingView(index);
	    		    				scope.chkR = 0 // Modify: 2016/07/13 David
	    		    				break;
	    	    				default:
	    	    					scope.query();
	    	    					break;
	        				};
	        			}else{
	        				scope.query();
	        			}
	    			});
	    		};
				
	        }
	    };    
	}
]);