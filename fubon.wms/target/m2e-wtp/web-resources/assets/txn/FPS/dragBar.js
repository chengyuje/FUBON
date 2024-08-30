/**================================================================================================
@program: dragBar.js
@description: percent bar with drag
@version: 1.0.20180112
@param {object} options
@param {bool} options.showBtn -false
@param {bool} options.showLabel -true
@param {bool} options.showTip -false
@param {object} options.drag
@param {number} options.drag.interval -1 drag interval
@param {object} options.label
@param {number} options.label.decimal -0 percent decimal
@param {string} options.label.end -end label
@param {string} options.label.front -front label
@param {string} options.label.unit -dynamic tip `percent+unit`
@param {object} options.style
@param {string} options.style.barColor -'#3f97c0'
@param {string} options.style.barCss -undefined
@param {string} options.style.barDefaultColor -'#3f97c0'
@param {string} options.style.barLimitColor -'#e96143'
@param {string} options.style.btnColor -'#3f97c0'
@param {bool} draggable -false
@param {string} tip -static tip
@param {func} btnOnDrag
@param {func} btnOnDragEnd
=================================================================================================*/
"use strict";
eSoafApp.component('dragBar', {
  restrict: 'E',
  bindings: {
    percent: '@',
    options: '=',
    draggable: '=?',
    tip: '@?',
    btnOnDrag: '&?',
    btnOnDragEnd: '&?'
  },
  templateUrl: 'assets/txn/FPS/dragBar.html',
  controller: ['$document', '$element', function ($document, $element) {
    var ctrl = this;
    var hasInit = false;

    function pctOnChange(value) {
      ctrl._percentText = value.toFixed(ctrl._options.label.decimal) || '';
      ctrl._options.style.barColor = value > 100 ? ctrl._options.style.barLimitColor : ctrl._options.style.barDefaultColor;
      ctrl.barStyle = !ctrl._options.style.barCss ? {
          'background-image': 'linear-gradient(90deg, ' +
            ctrl._options.style.barColor +
            ' ' +
            value +
            '%, transparent 0%)'
        } :
        ctrl._options.style.barCss;
    }

    ctrl.$onInit = function () {
      ctrl.percent = ctrl.percent || 0;
      ctrl._percentText = ctrl.percent || '';
      ctrl.tip = ctrl.tip || '';
      ctrl.draggable = !!ctrl.draggable;

      // options
      ctrl._options = angular.copy(ctrl.options) || {};
      ctrl._options.showTip = ctrl._options.showTip || false;
      ctrl._options.showLabel = ctrl._options.showLabel || true;
      ctrl._options.showBtn = ctrl._options.showBtn || false;
      // drag
      ctrl._options.drag = ctrl._options.drag || {};
      ctrl._options.drag.interval = Number(ctrl._options.drag.interval) || 1;
      // style
      ctrl._options.style = ctrl._options.style || {};
      // default color
      ctrl._options.style.barDefaultColor = ctrl._options.style.barDefaultColor || '#3f97c0';
      // overlimit color
      ctrl._options.style.barLimitColor = ctrl._options.style.barLimitColor || '#e96143';
      // showing color
      ctrl._options.style.barColor = ctrl._options.style.barColor || ctrl._options.style.barDefaultColor;
      ctrl._options.style.barCss = ctrl._options.style.barCss || undefined;
      ctrl._options.style.btnColor = ctrl._options.style.btnColor || '#3f97c0';
      // label
      ctrl._options.label = ctrl._options.label || {};
      ctrl._options.label.front = ctrl._options.label.front || '0%';
      ctrl._options.label.end = ctrl._options.label.end || '100%';
      ctrl._options.label.unit = ctrl._options.label.unit || '';
      ctrl._options.label.decimal = Number(ctrl._options.label.decimal) || 0;

      console.log(ctrl._options);
      pctOnChange(Number(ctrl.percent) || 0);
      hasInit = true;
    };

    ctrl.$onChanges = function (newValue) {
      if (!hasInit) return;
      pctOnChange(Number(newValue.percent.currentValue) || 0);
    };

    ctrl.$postLink = function () {
      if (ctrl.draggable) {
        var btn = $element.find('.button');
        var bar = $element.find('.bar');
        var oldPct = 0;
        var barRect = bar[0].getBoundingClientRect();
        var barStyle = getComputedStyle(bar[0]);
        var startX = parseFloat(barRect.x) - parseFloat(barStyle['margin-left']);
        var x = 0;
        var getMouseX = function (e) {
          var x = e.clientX - startX < 0 ? 0 : e.clientX - startX;
          x = x > barRect.width ? barRect.width : x;
          return x;
        };
        var mousemove = function mousemove(e) {
          var tmpX = getMouseX(e);
          var tmpPct = (tmpX / barRect.width) * 100;
          if (tmpX === x || tmpPct === ctrl.percent || (tmpPct % ctrl._options.drag.interval) > 0.5) return;
          x = tmpX;
          ctrl.percent = Number((x / barRect.width) * 100).toFixed(Number(ctrl._options.label.decimal) || 0);
          pctOnChange(Number(ctrl.percent || 0));
          if (ctrl.btnOnDrag && typeof ctrl.btnOnDrag === 'function')
            ctrl.btnOnDrag({
              newPct: ctrl.percent,
              oldPct: oldPct
            });
        };

        var mouseup = function mouseup() {
          $document.off('mousemove', mousemove);
          $document.off('mouseup', mouseup);
          if (ctrl.btnOnDragEnd && typeof ctrl.btnOnDragEnd === 'function')
            ctrl.btnOnDragEnd(ctrl.percent, oldPct);
        };

        btn.on('mousedown', function (e) {
          // Prevent default dragging of selected content
          e.preventDefault();
          x = getMouseX(e);
          oldPct = ctrl.percent;
          $document.on('mousemove', mousemove);
          $document.on('mouseup', mouseup);
        });
      }
    };
  }]
});
