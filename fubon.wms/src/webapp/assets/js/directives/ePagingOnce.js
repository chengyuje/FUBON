/**================================================================================================
@program: ePagingOnce.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('ePagingOnce', ['projInfoService', function(projInfoService) {
    return {
        restrict: 'E',
        replace: true,
        scope:{
        	output: '=',	//java 端 outputVO，需extends PagingOutputVO
        	list: '=?',
        	mode: '=?',
        	page: '@?',
        	rowCountLimit: '=?rowcountlimit' //可顯示的列數
        },
        template: function(element, attrs) {
        	var str = '<div class="pagingRow">' +
        	'    <ul class="pagination" ng-if="mode == 1 && dataList.length > 0">' +
			'       <li><a href="page/1" onclick="return false" ng-click="changePage(0)">&laquo;</a></li>' +
			'       <li><a href="/page/{{outputVO.currentPageIndex == 0 ? 1 : outputVO.currentPageIndex}}" onclick="return false" ng-click="changePage(outputVO.currentPageIndex == 0 ? 0 : outputVO.currentPageIndex - 1)">&lsaquo;</a></li>' +
			'       <li ng-repeat="row in resultArray" class="{{row.clazz}}"><a href="/page/{{row.text + 1}}" onclick="return false" ng-click="changePage(row.text)">{{row.text + 1}}</a></li>' +
			'       <li><a href="/page/{{outputVO.currentPageIndex == outputVO.totalPage - 1 ? outputVO.totalPage : outputVO.currentPageIndex + 2}}" onclick="return false" ng-click="changePage(outputVO.currentPageIndex == outputVO.totalPage - 1 ? outputVO.totalPage - 1 : outputVO.currentPageIndex + 1)">&rsaquo;</a></li>' +
			'		<li><a href="page/{{outputVO.totalPage}}" onclick="return false" ng-click="changePage(outputVO.totalPage - 1)">&raquo;</a></li>' +
			'		<span style="padding-left:20px;position: relative;top: 10px;" ng-if="mode == 1 && outputVO.totalRecord > 0">' +
			'		{{1 + (rowCountLimit * outputVO.currentPageIndex)}} - {{dataList.length + (rowCountLimit * outputVO.currentPageIndex)}}&nbsp;&nbsp;&nbsp;&nbsp;共{{outputVO.totalRecord}}筆' +
			'		</span>' +
			'    </ul>' +
			'</div>';
        	return str;
        },
        link: function (scope, element, attrs, ctlModel) {
        	scope.totalList = [];
        	scope.rowCountLimit = projInfoService.$paging.rowCountLimit;
        	scope.mode = scope.mode || 1; //default append mode
        	projInfoService.$paging.mode = scope.mode;
        	console.log('ePaging.mode='+projInfoService.$paging.mode);
        	
        	scope.changePage = function(pageIndex){
        		scope.outputVO.currentPageIndex = pageIndex;
        		$.extend(scope.dataList, scope.totalList);
        		scope.dataList.splice(0, (pageIndex*scope.rowCountLimit));
        	};
        	
        	scope.$watch('outputVO', function (newValue, oldValue) {
        		if(scope.dataList && scope.outputVO) {
        			$.extend(scope.totalList, scope.dataList);
            		scope.outputVO.totalPage = Math.ceil(scope.dataList.length / 10);
            		scope.outputVO.totalRecord = scope.dataList.length;
        		}
        	});
        	
        	scope.$watch('outputVO.currentPageIndex', function (newValue, oldValue) {
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