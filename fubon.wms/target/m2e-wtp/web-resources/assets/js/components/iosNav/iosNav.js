/**================================================================================================
@program: iosNav.js
@description: ios like navibar
@version: 1.0.20180112
titleMap = [{
        title: '',
        path: '',
        order: 0,
        data: {},
        active: true
      }, {
        title: '',
        path: '',
        order: 0,
        data: {},
        active: false
      }],
=================================================================================================*/
eSoafApp.directive('iosNav', [
  function () {
    return {
      scope: {
        titleMap: '=',
        path: '=',
        value: '=?',
        disabled: '=?',
      },
      templateUrl: 'assets/js/components/iosNav/iosNav.html',
      link: function (scope, element, attrs, ctlModel) {
        scope.$watch(scope.titleMap, function (value) {
          var initIndex = -1;
          scope.titleList = scope.titleMap.sort(function (a, b) {
              return a.order > b.order;
            })
            .map(function (row, index) {
              if (row.active) {
                initIndex = index;
              }
              row.index = index;
              return row;
            });
          scope.path = initIndex >= 0 ? scope.titleMap[initIndex].path : '';
          scope.value = initIndex >= 0 ? angular.copy(scope.titleMap[initIndex].data) : null;
        });

        scope.chgBtn = function (i) {
          if (scope.disabled) return false;
          scope.titleList.forEach((title, index) => {
            title.active = index === i;
          });

          if (i >= 0 && scope.titleMap[i] && scope.titleMap[i].path) {
            scope.path = scope.titleMap[i].path || '';
            scope.value = angular.copy(scope.titleMap[i].data) || null;
            return true;
          }
          scope.path = '';
        };
      }
    };
  }
]);
