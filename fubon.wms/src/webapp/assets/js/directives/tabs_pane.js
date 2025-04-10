/**================================================================================================
@program: tabs_pane.js
@description:
@version: 1.0.20170313
=================================================================================================*/
//============================
// tabs、tabpane 配合交易頁簽使用
//============================

/*
屬性名稱	用途
restrict	說明該directive要以何種方式宣告，像是元素、屬性、註解
priority	說明該directive和其他在同個元素的directive的優先度為何，數字越小優先度越高
template	用字串的方式編寫HTML碼，使用該directive的元素將會替換為這裡指定的HTML
templateUrl	同template，但是是指定template的url
replace		若為true則會用template取代原本的Html元素，若為false則將元素insert到元本的Html裡面
transclude	設為true可以將原本的HTML的元素內容移到template定義的HTML元素裡
scope		若給一物件則會建立一個新的scope，若指定為true則繼成自父scope
controller	為directive定義一個controller(大部分的目的是為了和其他directive互動)
require		指定該directive需要和哪些directive互動
link		可以在該方法內增加監聽事件($scope.$watch)，或初始化一些資訊
compile		在link執行之前會先被執行，用於用程式動態修改template
*/

eSoafApp.directive('tabs', function() {
    return {
        restrict: 'A',
        transclude: true,
        scope: true,
        controller: function($scope, $element) {
    		//從所在的 Controller 繼承而來
            var panes = $scope.panes;

            $scope.selectPane = function(pane) {
                angular.forEach(panes, function(pane) {
                    pane.selected = false;
                });
                pane.selected = true;
            };
            this.addPane = function(pane) {
                if (panes.length === 0) $scope.selectPane(pane);
                panes.push(pane);
            };
			this.removePane = function(idx) {
				if (idx > 0) {
					panes.splice(idx, 1);
				}
			};
        },
        template:
            '<div class="tabbable">' +
                '<ul class="nav nav-tabs">' +
					'<li ng-repeat="pane in panes" ng-class="{active:pane.$parent.tabInfo.selected}">'+
						'<a href="javascript:void(0)" ng-click="pane.$parent.tabManager.selectTab($index)">{{pane.title}}' +
							'<div style="display:inline;color:red" ' +
							' ng-if="pane.$parent.tabInfo.allowClosed && pane.$parent.tabInfo.selected" ' +
							' ng-click="$event.stopPropagation();pane.$parent.tabManager.removeTab($index);"> X</div>' +
						'</a>' +
					'</li>' +
                '</ul>' +
                '<div class="tab-content" ng-transclude></div>' +
			'</div>',
        replace: true
    };
});
	
eSoafApp.directive('pane', function() {
    return {
        require: '^tabs',
        restrict: 'A',
        transclude: true,
        scope: { title: '@' },
        link: function(scope, element, attrs, tabsCtrl) {
            tabsCtrl.addPane(scope);
        },			
        template: '<div class="tab-pane" ng-class="{active: $parent.tabInfo.selected}" ng-transclude></div>',
        replace: true
    };
});