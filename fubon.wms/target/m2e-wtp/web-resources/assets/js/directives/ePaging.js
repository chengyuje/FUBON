/**================================================================================================
@program: ePaging.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('ePaging', ['projInfoService', function(projInfoService) {
    return {
        restrict: 'E',
        replace: true,
        scope:{
        	outputVO: '=outputvo',	//java 端 outputVO，需extends PagingOutputVO
        	inputVO: '=?inputvo',	//java 端 inputVO，需extends PagingInputVO
        	displayLimit: '=displaylimit', //可顯示的列數
        	rowCountLimit: '=?rowcountlimit',	//按下[顯示下N筆資料]，N = rowCountLimit [option]
        	callQuery: '&?callquery',
        	mode: '=?',
        	dataList: '=?datalist',
        	page: '@?'
        },
        template: function(element, attrs) {
        	var str = '<div class="pagingRow">' +
//			mode 1
			'    <span ng-if="mode == 1 && dataList.length > 0">' +
			'	    1 - {{dataList.length}}&nbsp;&nbsp;&nbsp;&nbsp;共{{outputVO.totalRecord | number}}筆資料' +
			'	    <span ng-if="displayLimit < outputVO.totalRecord">' +
			'	    [<a href="/next/row/{{rowCountLimit}}" onclick="return false" ng-click="pagingAppend(rowCountLimit)">' +
			'       顯示下{{outputVO.totalRecord - displayLimit > rowCountLimit ? rowCountLimit : outputVO.totalRecord - displayLimit}}筆資料</a>]' +
			'	    </span>' +
			'    </span>' +
//			mode 2
			'    <ul class="pagination" ng-if="mode == 2 && dataList.length > 0">' +
			'       <li><a href="page/1" onclick="return false" ng-click="changePage(0)">&laquo;</a></li>' +
			'       <li><a href="/page/{{outputVO.currentPageIndex == 0 ? 1 : outputVO.currentPageIndex}}" onclick="return false" ng-click="changePage(outputVO.currentPageIndex == 0 ? 0 : outputVO.currentPageIndex - 1)">&lsaquo;</a></li>' +
			'       <li ng-repeat="row in resultArray" class="{{row.clazz}}"><a href="/page/{{row.text + 1}}" onclick="return false" ng-click="changePage(row.text)">{{row.text + 1}}</a></li>' +
			'       <li><a href="/page/{{outputVO.currentPageIndex == outputVO.totalPage - 1 ? outputVO.totalPage : outputVO.currentPageIndex + 2}}" onclick="return false" ng-click="changePage(outputVO.currentPageIndex == outputVO.totalPage - 1 ? outputVO.totalPage - 1 : outputVO.currentPageIndex + 1)">&rsaquo;</a></li>' +
			'		<li><a href="page/{{outputVO.totalPage}}" onclick="return false" ng-click="changePage(outputVO.totalPage - 1)">&raquo;</a></li>' +
			'		<span style="padding-left:20px;position: relative;top: 10px;" ng-if="mode == 2 && outputVO.totalRecord > 0">' +
			'		{{1 + (rowCountLimit * outputVO.currentPageIndex)}} - {{dataList.length + (rowCountLimit * outputVO.currentPageIndex)}}&nbsp;&nbsp;&nbsp;&nbsp;共{{outputVO.totalRecord}}筆' +
			'		</span>' +
			'    </ul>' +
//			mode 3
			'    <span ng-if="mode == 3 && dataList.length > 0">' +
			'	    1 - {{displayLimit < dataList.length ? displayLimit : dataList.length}}&nbsp;&nbsp;&nbsp;&nbsp;共{{dataList.length}}筆資料' +
			'	    <span ng-if="displayLimit < dataList.length">' +
			'	    [<a href="/next/row/{{rowCountLimit}}" onclick="return false" ng-click="pagingAppend(rowCountLimit)">' +
			'       顯示下{{dataList.length - displayLimit > rowCountLimit ? rowCountLimit : dataList.length - displayLimit}}筆資料</a>]' +
			'	    </span>' +
			'    </span>' +
			'</div>';
        	return str;
        },
        controller: ["$scope", function($scope) {
        	$scope.outputVO = $scope.outputVO || {};
        	
        }],
        link: function (scope, element, attrs, ctlModel) {
        	scope.requestRowCountLimit = projInfoService.$paging.rowCountLimit;
    		//每頁N筆
    		if(angular.isDefined(attrs.callquery) == false){//有callQuery時才分頁，否則全查(若server端有分頁，tita.pageCount=0可得到全部資料)
    			scope.requestRowCountLimit = 0;
    		}
    		console.log('scope.requestRowCountLimit='+scope.requestRowCountLimit);
    		scope.rowCountLimit = projInfoService.$paging.rowCountLimit;
        	scope.displayLimit = scope.displayLimit || scope.rowCountLimit;
        	scope.mode = scope.mode || 1;//default append mode
        	projInfoService.$paging.mode = scope.mode;
        	console.log('ePaging.mode='+projInfoService.$paging.mode);
        	
        	scope.pagingAppend = function(appendRow){
        		console.log('displayLimit='+scope.displayLimit+',appendRow='+appendRow);
        		scope.displayLimit += appendRow;
        		
        		if(angular.isDefined(attrs.callquery)){
        			scope.inputVO.pageCount = scope.rowCountLimit;
        			scope.inputVO.currentPageIndex = scope.outputVO.currentPageIndex + 1;
        			scope.callQuery();
        			
        		}
        	};
        	
        	scope.changePage = function(pageIndex){
        		if(scope.displayLimit > 0){
        			scope.displayLimit = scope.displayLimit * -1;
        		}
        		if(angular.isDefined(attrs.callquery)){
        			scope.inputVO.pageCount = scope.rowCountLimit;
        			scope.inputVO.currentPageIndex = pageIndex;
        			scope.callQuery();
        		}
        	};
        	
        	// for query
        	scope.$watch('outputVO', function (newValue, oldValue) {
        		if(newValue == oldValue){
        			return;
        		}
        		scope.page = scope.page - 0 || 4;	// mode2 前後幾頁
        		var count = 0;	// 為了固定頁數
        		scope.resultArray = [];
        		var currentPageIndex = scope.outputVO.currentPageIndex;
        		var totalPage = scope.outputVO.totalPage;
        		if(2 * scope.page + 1 > totalPage) {
        			for(var i = 0; i < totalPage; i++){
            			var row = {};
            			row.clazz = '';
            			if(currentPageIndex == i){
            				row.clazz='active';
            			}
            			row.text = i;
            			scope.resultArray.push(row);
            		}
        		} else {
        			for (var i = -scope.page;i <= scope.page;i++){
            			var row = {};
            			row.clazz = '';
            			if(i == 0){
            				row.clazz='active';
            			}
            			if (currentPageIndex + i < 0) {
            				count++;
            				continue;
            			}
            			if (currentPageIndex + i > totalPage -1) {
            				count--;
            				continue;
            			}
            			row.text = currentPageIndex + i;
        				scope.resultArray.push(row);
            		}
            		for (var i = 1;i <= Math.abs(count);i++) {
            			var row = {};
            			row.clazz = '';
            			if(count > 0){
            				row.text = currentPageIndex + scope.page + i;
            				scope.resultArray.push(row);
            			} else {
            				row.text = currentPageIndex - scope.page - i;
            				scope.resultArray.splice(0,0,row);
            			}
            		}
        		}
                return;
            });
        	
        }
    };
    
}]);