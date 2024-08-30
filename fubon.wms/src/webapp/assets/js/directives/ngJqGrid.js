/**================================================================================================
@program: ngJqGrid.js
@description: JQuery
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('ngJqGrid', function () {
    return {
      restrict: 'E',
      scope: {
        config: '=',
        data: '='
      },
      link: function (scope, element, attrs) {        
        var guid = (function() {
          function s4() {
            return Math.floor((1 + Math.random()) * 0x10000)
                       .toString(16)
                       .substring(1);
          }
          return function() {
            return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
                   s4() + '-' + s4() + s4() + s4();
          };
        })();
        scope.uuid = guid();        
        element.attr("id", scope.uuid);
        var sPage = scope.config.pager || "";
        if (sPage != "") {
          console.log(sPage);
          var nPagerId = sPage.substr(1, sPage.length-1) + scope.uuid;
          $(sPage).attr("id", nPagerId);
          scope.config.pager = "#" + nPagerId; 
        } 

        scope.$watch('config', function (newValue) {
          //console.log("step2");
          $(element).jqGrid(newValue);
        });
        
        scope.$watch('data', function (newValue, oldValue) {
          console.log("reloadGrid");
          $(element).clearGridData();
          $(element).setGridParam({
              datatype: "local",
              data: newValue
          }).trigger("reloadGrid", [{current: true}]);
          return;

          /* 效能太差
          if (oldValue) {
            for (var i = oldValue.length - 1; i >= 0; i--) {
              $(element).jqGrid('delRowData', i);
            }
          }
          if (newValue) {
            var nMax = Math.min(50, newValue.length);
            for (var i = 0; i < nMax; i++) {
              $(element).jqGrid('addRowData', i, newValue[i]);
            }
          }
          */
        });       
      },
      template: "<table></table>",
      replace: true
    };
  });