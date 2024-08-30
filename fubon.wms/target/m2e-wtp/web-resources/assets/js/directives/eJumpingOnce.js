/**================================================================================================
@program: eJumpingOnce.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eJumpingOnce', ['projInfoService', 'getParameter', '$q', function (projInfoService, getParameter, $q) {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      output: '=?', //java 端 outputVO，需extends PagingOutputVO
      list: '=?',
      totalList: '=?totallist',
      isEnd: '=?isend',
      limitTotal: '@?limittotal',
      rowCountLimit: '=?rowcountlimit' //pageCount || 10
    },
    template: function (element, attrs) {
      var htmlText =
        '<div>' +
        '	<div  style="text-align:left;height:28px;margin-bottom:5px">' +
        '		<table style="display:block;">' +
        '			<tr>' +
        '				<td style="padding:0 3px 3px 0;"><a style="font-size:10px;height:28px;border-bottom-right-radius:0;border-top-right-radius:0;" class="btn btn-info" ng-click="Paging(0, 0)"><i style="margin-right:2.5px;" class="glyphicon glyphicon-step-backward"></i>上一頁</a></td>' +
        '				<td style="padding:0 3px 3px 0;"><a style="font-size:10px;height:28px;border-bottom-left-radius:0;border-top-left-radius:0;" class="btn btn-info" ng-click="Paging(1, 1)">下一頁<i style="margin-left:2.5px;" class="glyphicon glyphicon-step-forward"></i></a></td>' +
        '				<td style="padding:0 3px 3px 0;"><select class="form-control" style="width:75px;height:28px;display:inline;padding: 0px 12px;" ng-options="page as page.text for page in pages" ng-model="selectedPage" ng-change="jumpPage(selectedPage.text - 1)"></select></td>' +
        '				<td style="padding:0 3px 3px 0;"><p style="display:inline;height:28px;">共&nbsp;</p></td>' +
        '				<td style="padding:0 3px 3px 0;"><label style="display:inline;height:28px;">{{totalPage}}</label></td>' +
        '				<td style="padding:0 3px 3px 0;"><p style="display:inline;height:28px;">&nbsp;頁&nbsp;/&nbsp;共&nbsp;</p></td>' +
        '				<td style="padding:0 3px 3px 0;"><label style="display:inline;height:28px;">{{totalRecord}}</label></td>' +
        '				<td style="padding:0 3px 3px 0;"><p style="display:inline;height:28px;">&nbsp;筆資料</p></td>' +
        '			</tr>' +
        '		</table>' +
        '	</div>' +
        '</div>';
      return htmlText;
    },
    link: function (scope, element, attrs, ctlModel) {
      //initialize
      scope.limitTotal = scope.limitTotal || 'true';
      init(scope);

      function eJumpingOnceGetXml() {
        var deferred = $q.defer();
        getParameter.XML([
          ["SYS", "MAX_QRY_ROWS"]
        ], function (totas) {
          if (totas) {
            var idx = itemIdxInArray(totas.key, ["SYS", "MAX_QRY_ROWS"]);
            scope.maxRecord = totas.data[idx][0].LABEL;
            deferred.resolve("success");
          }
        });
        return deferred.promise;
      }
      eJumpingOnceGetXml().then(function (data) {
        //watch data
        scope.$watch('output', function (newValue, oldValue) {
          if (scope.output) {
            // 2018/10/17 Juan bug fix set init value
            scope.totalList = scope.totalList || [];
            // 2017/5/11 add msg
            if (scope.limitTotal == 'true') {
              if (scope.totalList.length > 0 && scope.maxRecord < scope.totalList.length) {
                scope.totalList.splice(scope.maxRecord, scope.totalList.length);
                scope.$parent.showWarningMsg("ehl_01_common_031");
              }
            }
            //
            scope.output.totalPage = Math.ceil(scope.totalList.length / scope.rowCountLimit);
            scope.output.totalRecord = scope.totalList.length;
            counts(scope);
            scope.jumpPage(scope.output.currentPageIndex || 0);
          }
        });

        scope.$watch('currentPageIndex', function (newValue, oldValue) {
          if (newValue == oldValue) {
            return;
          }
          counts(scope);
        });
      });

      //paging call
      scope.Paging = function (fn, index) {
        switch (fn) {
          case 0: //prevPage
            var prevPageIndex = scope.selectedPage.text - 2;
            if (prevPageIndex >= 0) {
              scope.jumpPage(prevPageIndex);
            } else {
              return;
            }
            break;
          case 1: //nextPage
            var nextPageIndex = scope.selectedPage.text;
            if (nextPageIndex <= scope.output.totalPage - 1) {
              scope.jumpPage(nextPageIndex);
            } else {
              return;
            }
            break;
        };
      };
      scope.jumpPage = function (index) {
        scope.currentPageIndex = index;
        // 2015/5/15
        scope.isEnd = scope.currentPageIndex == scope.totalPage - 1;
        scope.list = [];
        var str = [0, 0]; //str[0]: start, str[1]: end
        str[0] = index * scope.rowCountLimit; // 1*10 = 10
        if ((scope.totalList.length - (str[0])) >= scope.rowCountLimit) { // 20-10 >= 10
          str[1] = str[0] + scope.rowCountLimit - 1; // 10+10 = str[1]
        } else {
          str[1] = scope.totalList.length - 1;
        }
        for (var i = str[0]; i <= str[1]; i += 1) {
          scope.list.push(scope.totalList[i]);
        }
      };

    }
  };

  function init(scope) {
    scope.rowCountLimit = scope.rowCountLimit || parseInt(projInfoService.getSinglePageRows()[0].LABEL);
    scope.pages = [];
    scope.totalPage = 0;
    scope.totalRecord = 0;
    scope.currentPageIndex = 0;
    scope.selectedPage = {};
  }

  function counts(scope) {
    scope.totalPage = scope.output.totalPage;
    scope.totalRecord = scope.output.totalRecord;
    scope.pages = [];
    for (var i = 1; i <= scope.output.totalPage; i++) {
      scope.page = {
        name: "page " + i,
        text: i
      };
      scope.pages.push(scope.page);
    }
    scope.selectedPage = scope.pages[scope.currentPageIndex];
    // for someone splice
    if (!scope.pages[scope.currentPageIndex] && scope.currentPageIndex > 0)
      scope.currentPageIndex -= 1;
  }

  function itemIdxInArray(array, item) {
    for (var i = 0; i < array.length; i++) {
      // This if statement depends on the format of your array
      if (array[i][0] == item[0] && array[i][1] == item[1]) {
        return i; // Found it
      }
    }
    return -1; // Not found
  }
}]);
