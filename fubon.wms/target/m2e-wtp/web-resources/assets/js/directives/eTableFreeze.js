/**================================================================================================
@program: eTableFreeze.js
@description: freeze local table
@version: 1.0.20170823(use transclude and ng-repeat to clone, and no mode any more)
@modified: 1.0.20171012 Juan
=================================================================================================*/
eSoafApp.directive('eTableFreeze', ['$q', '$timeout', function ($q, $timeout) {
    return {
        restrict: 'C',
        scope: {
            eMode: '@?',
            eScroll: '=?',
            eHeight: '@?',
            eWidth: '@?',
            eSide: '@?',
            eTop: '@?',
            eLeft: '@?',
            eList: '=?'
        },
        transclude: true,
        template: '<div ng-repeat="ft in ftables track by $index" e-index="{{ft}}" ng-transclude></div>',
        link: function (scope, element, attrs, ctrl) {

            /**
             *
             * settle pattern
             *
             * */

            scope.ftables = (scope.eTop && scope.eTop === 'true' && !scope.eLeft) ? [0, 1] : (!scope.eTop || scope.eTop === 'false' && scope.eLeft) ? [0, 2, 3] : [0, 1, 2, 3];
            $timeout(function () {
                scope.freeze();
            });

            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
             *
             * [Pattern:3] Repeat Method
             *
             * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

            scope.freeze = function () {

                /** Initialize **/
                //settle variable
                var $cnt = scope.eLeft ? parseInt(scope.eLeft) : false,
                    _class = $(element).attr('class').replace('e-table-freeze', '').replace(/ng\-\S+/g, ''), // add dynamic class
                    $t = $(element).addClass('sticky-enabled')
                    .prepend($(element).find('[e-index="0"]').children())
                    .css({
                        margin: 0,
                        width: '100%'
                    })
                    .wrap('<div class="sticky-wrap"/>'),
                    $w = $t.parent('.sticky-wrap') //create wrapper
                    .attr('style', function () {
                        var str = '';
                        if (scope.eWidth) str += 'max-width:' + scope.eWidth + ';';
                        if (scope.eHeight) str += 'max-height:' + scope.eHeight + ';';
                        return str;
                    }),
                    $th = $(element).find('[e-index="1"]').length > 0 && scope.eTop && scope.eTop === 'true' ? //add top
                    $('<table class="sticky-thead table"></table>')
                    .addClass(_class)
                    .append($(element).find('[e-index="1"]').children())
                    .css({
                        'margin': 0,
                        'width': '100%',
                        'display': 'none',
                        'opacity': 0,
                        'border': 0
                    })
                    .appendTo($w)
                    .find('tbody')
                    // 17-08-30 Juan remove replace display none
                    //.addClass('removeCell')
                    .remove()
                    .end() : null,
                    $col = $(element).find('[e-index="2"]').length > 0 && $cnt ?
                    $('<table class="sticky-col table"></table>')
                    .addClass(_class)
                    .append($(element).find('[e-index="2"]').children())
                    .css({
                        'margin': 0,
                        'width': '100%',
                        'display': 'none',
                        'opacity': 0,
                        'border-top': 0,
                        'border-left': 0
                    })
                    .appendTo($w)
                    .find('thead')
                    .remove()
                    .end() : null,
                    $intersect = $(element).find('[e-index="3"]').length > 0 && $cnt ?
                    $('<table class="sticky-intersect table"></table>')
                    .addClass(_class)
                    .append($(element).find('[e-index="3"]').children())
                    .css({
                        'margin': 0,
                        'width': '100%',
                        'display': 'none',
                        'opacity': 0
                    })
                    .appendTo($w)
                    .find('tbody')
                    .remove()
                    .end() : null,
                    _css = {},
                    _leftWidth = 0, //record left col width;
                    bindList = [],
                    hasFunctionType = false;

                //set scroll bar on/off
                if (scope.eScroll) {
                    //version:1 hidden by attr control.
                    if (scope.eTop) {
                        _css['overflow-y'] = 'auto';
                    } else {
                        _css['overflow-y'] = 'hidden';
                    }
                    if (scope.eLeft) {
                        _css['overflow-x'] = 'auto';
                    } else {
                        _css['overflow-x'] = 'hidden';
                    }
                } else {
                    //version:2 always auto.
                    _css['overflow-y'] = 'auto';
                    _css['overflow-x'] = 'auto';
                }
                $w.css(_css);



                /** events **/

                $w.on('scroll', function () {
                    scope._top();
                    scope._left();
                });


                $(window).on("resize", function () {
                    renderTable(); // resize renderTable again to do the right size
                    scope._top();
                    scope._left();
                });

                // popup modal will add scroll bar need resize
                scope.$watch(function () {
                    return $('html,body').css('overflow');
                }, function (newValue, oldValue) {
                    if (newValue !== oldValue && scope.eIndex === '1') {
                        $th.outerWidth($t[0].getBoundingClientRect().width);
                    }
                });

                scope.$watch(function () { // watch table width
                    return $(element).width();
                }, function (newValue, oldValue) {
                    if (newValue && newValue !== oldValue) {
                        $timeout(function () {
                            renderTable();
                        });
                    }
                });

                // change data trigger
                scope.$watchCollection('eList',
                    function (newValue, oldValue) {
                        //17-08-18 Juan: add watch bind list
                        $(element).find('[ng-if]')
                            .each(function () {
                                var _bind = $(this).attr('ng-if');
                                if (bindList.indexOf(_bind) < 0) {
                                    bindList.push(_bind);
                                    scope.$watch(function () {
                                        return scope.$parent.$eval(_bind);
                                    }, function (newValue, oldValue) {
                                        if (newValue !== oldValue) {
                                            $timeout(function () {
                                                renderTable();
                                            });
                                        }
                                    });
                                }
                            });
                        $(element).find('[ng-show]')
                            .each(function () {
                                var _bind = $(this).attr('ng-show');
                                if (bindList.indexOf(_bind) < 0) {
                                    bindList.push(_bind);
                                    scope.$watch(function () {
                                        return scope.$parent.$eval(_bind);
                                    }, function (newValue, oldValue) {
                                        if (newValue !== oldValue) {
                                            $timeout(function () {
                                                renderTable();
                                            });
                                        }
                                    });
                                }
                            });

                        if (!newValue) return;

                        $timeout(function () {
                            //17-08-23 Juan: add for multi repeat tbody pms330
                            if ($(element).find('div tbody').length > 0) {
                                if ($t)
                                    $t.append($(element)
                                        .find('[e-index="0"]')
                                        .children()
                                    );
                                if ($th)
                                    $th.append($(element)
                                        .find('[e-index="1"]')
                                        .children())
                                    .find('tbody')
                                    .remove()
                                    .end();
                                if ($col)
                                    $col.append($(element)
                                        .find('[e-index="2"]')
                                        .children())
                                    .find('thead')
                                    .remove()
                                    .end();
                                if ($intersect)
                                    $intersect.append($(element)
                                        .find('[e-index="3"]')
                                        .children())
                                    .find('tbody')
                                    .remove()
                                    .end();
                            }
                            // 17-09-05 Juan: disable multi execute ng-change
                            if ($col && $col.find('[ng-change]').length > 0) {
                                $col.find('[ng-change]').each(function () {
                                    $(this).attr('ng-change', '');
                                });
                            }
                            renderTable();
                            hasFunctionType = false;
                        });
                    });



                scope.$watch(function () { // watch if table wrapper is invisible
                    return $w.is(':visible');
                }, function (newValue, oldValue) {
                    if (newValue && newValue !== oldValue) {
                        $timeout(function () {
                            renderTable();
                        });
                    }
                });

                scope.$watch(function () { // watch if table is invisible
                    return $t.is(':visible');
                }, function (newValue, oldValue) {
                    if (newValue === false) {
                        if ($th) $th.hide();
                        if ($col) $col.hide();
                        if ($intersect) $intersect.hide();
                    }
                });

                var resizeTHeader = function resizeTHeader(el) {
                    // set sticky head
                    el.find('tr').each(function (index) {
                            $(this).find('th').each(function (_index) {
                                // 17-08-28 Juan: dynamic padding
                                var _this = $t.find('thead tr').eq(index)
                                    .find('th').eq(_index);
                                $(this)
                                    .outerWidth(_this[0].getBoundingClientRect().width)
                                    .outerHeight(_this[0].getBoundingClientRect().height);

                                $(this).css({
                                    'vertical-align': 'middle',
                                    'padding-right': _this.css('padding-right') ? _this.css('padding-right') : 0,
                                    'padding-left': _this.css('padding-left') ? _this.css('padding-left') : 0,
                                    'padding-top': 0,
                                    'padding-bottom': 0
                                });
                            });
                        })
                        .end()
                        .outerWidth($t[0].getBoundingClientRect().width)
                        .outerHeight($t.find('thead')[0].getBoundingClientRect().height);
                };

                // 17-08-30 Juan: split to be independent count countSpan
                var countTableSpan = function () {
                    var _tbtr = $t.find('tbody tr'), //cal tbody tr
                        countSpan = 0, //real span in row
                        rowSpan = {}, //record next row left span
                        rowWidth = {}, //record left width had settle
                        countCol = 0, //record had counted col span
                        _tbtrLength = _tbtr.length;

                    _leftWidth = 0; //record left col width

                    for (var tr = 0; tr < _tbtrLength; tr++) {
                        var _tr = _tbtr.eq(tr),
                            _tds = _tr.find('td'),
                            _tdLength = _tds.length,
                            td = 0,
                            _countSpan = rowSpan[tr] ? rowSpan[tr] : 0,
                            _countWidth = rowWidth[tr] ? rowWidth[tr] : 0;

                        for (td = 0; td < _tdLength; td++) {
                            var _td = _tds.eq(td);
                            if (_td.length <= 0) break;
                            _td.removeClass('removeCell');
                            if (!_td.hasClass('ng-hide') && _td.css('display') != 'none') {
                                var _colSpan = parseInt(_td.attr('colspan') || 1),
                                    _rowSpan = parseInt(_td.attr('rowspan') || 0),
                                    thisWidth = _tbtr.eq(tr).find('td').eq(td)[0].getBoundingClientRect().width;

                                countCol += 1;
                                _leftWidth += thisWidth;
                                countSpan += _colSpan;
                                for (var i = 1; i < _rowSpan; i++) {
                                    // row span
                                    if (!rowSpan[tr + i]) rowSpan[tr + i] = 0;
                                    rowSpan[tr + i] += 1;
                                    if (rowSpan[tr + i] >= $cnt) rowSpan[tr + i] = $cnt;
                                    // row width
                                    if (!rowWidth[tr + i]) rowWidth[tr + i] = 0;
                                    rowWidth[tr + i] += thisWidth;
                                    if (rowWidth[tr + i] >= _leftWidth) rowWidth[tr + i] = _leftWidth;
                                }
                                if (countCol >= $cnt) break;
                            }
                        }
                        if (countCol >= $cnt) break;
                    }
                    return countSpan;
                };

                // 17-08-30 Juan: split to be independent
                var countTableTbody = function (el, countSpan) {
                    var _tbtr = $t.find('tbody tr'), //cal tbody tr
                        rowSpan = {}, //record next row left span
                        rowWidth = {}, //record left width had settle
                        countCol = 0; //record had counted col span

                    // count tbody sticky-col sticky-intersect
                    el.find('tbody tr').each(function (tr) {
                        var _tds = $(this).find('td'),
                            _tdLength = _tds.length,
                            td = 0,
                            _countSpan = rowSpan[tr] ? rowSpan[tr] : 0,
                            _countWidth = rowWidth[tr] ? rowWidth[tr] : 0;

                        for (td = 0; td < _tdLength; td++) {
                            var _td = _tds.eq(td);
                            if (_td.length <= 0) break;
                            _td.removeClass('removeCell');
                            if (!_td.hasClass('ng-hide') && _td.css('display') != 'none') {
                                var _colSpan = parseInt(_td.attr('colspan') || 1),
                                    _rowSpan = parseInt(_td.attr('rowspan') || 0),
                                    thisWidth = _tbtr.eq(tr).find('td').eq(td)[0].getBoundingClientRect().width;


                                if (_countSpan < countSpan) {
                                    _countSpan += _colSpan;
                                    for (var i = 1; i < _rowSpan; i++) {
                                        // row span
                                        if (!rowSpan[tr + i]) rowSpan[tr + i] = 0;
                                        rowSpan[tr + i] += 1;
                                        if (rowSpan[tr + i] >= $cnt) rowSpan[tr + i] = $cnt;
                                        // row width
                                        if (!rowWidth[tr + i]) rowWidth[tr + i] = 0;
                                        rowWidth[tr + i] += thisWidth;
                                        if (rowWidth[tr + i] >= _leftWidth) rowWidth[tr + i] = _leftWidth;
                                    }

                                    //set height and width to style
                                    var oriTd = _tbtr.eq(tr).find('td').eq(td);
                                    _td.outerHeight(oriTd[0].getBoundingClientRect().height)
                                        .outerWidth(oriTd[0].getBoundingClientRect().width)
                                        .css({ // 17-08-28 Juan: dynamic padding
                                            'padding-right': oriTd.css('padding-right') ? oriTd.css('padding-right') : 0,
                                            'padding-left': oriTd.css('padding-left') ? oriTd.css('padding-left') : 0,
                                            'padding-top': 0,
                                            'padding-bottom': 0,
                                            'vertical-align': 'middle',
                                        });

                                    if (_countSpan >= countSpan) {
                                        _td.attr('colspan', _colSpan - (_countSpan - countSpan));
                                        if (rowWidth[tr] || 0 + thisWidth > _leftWidth) { //count rowSpan col width and set td width
                                            _td.outerWidth(_leftWidth - (rowWidth[tr] || 0));
                                        }
                                        break;
                                    } else
                                        rowWidth[tr] += thisWidth;

                                } else {
                                    td -= 1;
                                    break;
                                }
                            }
                        }
                        //remove
                        if (td < 0)
                            $(this).find('td').addClass('removeCell');
                        else
                            $(this).find('td:gt(' + (td ? td : 0) + ')').addClass('removeCell');
                    });
                };

                // 17-08-30 Juan: split to be independent
                var countTableThead = function (el, countSpan) {
                    var _thtr = $t.find('thead tr'), //cal thead tr
                        rowSpan = {}, //record next row left span
                        rowWidth = {}, //record left width had settle
                        countCol = 0; //record had counted col span

                    // count thead sticky-col sticky-intersect
                    rowSpan = {};
                    rowWidth = {};
                    el.find('thead tr').each(function (tr) {
                        var _countSpan = rowSpan[tr] ? rowSpan[tr] : 0,
                            th = 0,
                            _ths = $(this).find('th'),
                            _thLength = _ths.length;
                        for (th = 0; th < _thLength; th++) {
                            var _th = _ths.eq(th),
                                _colSpan = parseInt(_th.attr('colspan') || 1),
                                _rowSpan = parseInt(_th.attr('rowspan') || 1),
                                thisWidth = _thtr.eq(tr).find('th').eq(th)[0].getBoundingClientRect().width;
                            _th.removeClass('removeCell');
                            if (!_th.hasClass('ng-hide') && _th.css('display') != 'none') {
                                if (_colSpan && _countSpan < countSpan) {
                                    _countSpan += _colSpan;
                                    for (var i = 0; i < _rowSpan; i++) {
                                        // row span
                                        if (!rowSpan[tr + i]) rowSpan[tr + i] = 0;
                                        rowSpan[tr + i] += _colSpan;
                                        if (rowSpan[tr + i] >= countSpan) rowSpan[tr + i] = countSpan;
                                        // row width
                                        if (!rowWidth[tr + i]) rowWidth[tr + i] = 0;
                                        rowWidth[tr + i] += thisWidth;
                                        if (rowWidth[tr + i] >= _leftWidth) rowWidth[tr + i] -= thisWidth;
                                    }

                                    //set height and width to style
                                    var oriTh = _thtr.eq(tr).find('th').eq(th);
                                    _th.outerWidth(oriTh[0].getBoundingClientRect().width)
                                        .outerHeight(oriTh[0].getBoundingClientRect().height)
                                        .css({ // 17-08-28 Juan: dynamic padding
                                            'padding-right': oriTh.css('padding-right') ? oriTh.css('padding-right') : 0,
                                            'padding-left': oriTh.css('padding-left') ? oriTh.css('padding-left') : 0,
                                            'padding-top': 0,
                                            'padding-bottom': 0,
                                        });

                                    if (_countSpan >= countSpan) {
                                        _th.attr('colspan', _colSpan - (_countSpan - countSpan));
                                        if (rowWidth[tr] || 0 + thisWidth > _leftWidth) { //count rowSpan col width and set td width
                                            _th.outerWidth(_leftWidth - (rowWidth[tr] || 0));
                                        }
                                        break;
                                    }
                                } else {
                                    th -= 1;
                                    break;
                                }
                            }
                        }
                        //remove
                        if (th < 0) $(this).find('th').addClass('removeCell');
                        else $(this).find('th:gt(' + (th ? th : 0) + ')').addClass('removeCell');
                    });
                };

                //17-07-21 Juan: add renderTable(init) function
                var renderTable = function () {
                    $cnt = scope.eLeft ? parseInt(scope.eLeft) : false;

                    /**set variables **/
                    var _thtr = $t.find('thead tr'), //cal thead tr
                        _tbtr = $t.find('tbody tr'), //cal tbody tr
                        $cnt = scope.eLeft ? parseInt(scope.eLeft) : false,
                        recordState = {}; // 17-08-02 Juan add record display state

                    /* sticky-thead */
                    if (scope.eTop && scope.eTop === 'true' && $th) {
                        recordState['sticky-thead'] = $th.is(':visible');
                        recordState.top = $th.css('top');
                        if (!recordState['sticky-thead']) {
                            $th
                                .css({
                                    'opacity': 0,
                                    'display': 'none'
                                });
                        } else {
                            $th
                                .css({
                                    'opacity': 1,
                                    'display': 'table',
                                    'top': recordState.top
                                });
                        }

                        resizeTHeader($th);
                    }

                    var countSpan = $cnt ? countTableSpan() : 0;

                    /* sticky-col */
                    if ($cnt && $col) {
                        recordState['sticky-col'] = $col.is(':visible');
                        recordState.left = $col.css('left');
                        $col
                            .css({
                                display: 'table',
                            });
                        // countTable($col);
                        var clonedThead = $t
                            .find('thead')
                            .clone();
                        $col
                            .find('thead')
                            .remove()
                            .end()
                            .prepend($t.find('thead').clone());

                        countTableTbody($col, countSpan);
                        countTableThead($col, countSpan);
                        $col
                            .css({
                                'width': _leftWidth,
                                'max-width': 'none', //IE fixed
                                'border-width': 'none', //fixed border
                                'margin-bottom': '0px'
                            });

                        if (!recordState['sticky-col']) {
                            $col
                                .css({
                                    'opacity': 0,
                                    'display': 'none'
                                });
                        } else {
                            $col
                                .css({
                                    'opacity': 1,
                                    'display': 'table',
                                    'left': recordState.left
                                });
                        }
                    }

                    /* sticky-intersect */
                    if ($cnt && $intersect) {
                        recordState['sticky-intersect'] = $intersect.is(':visible'); // 17-08-02 Juan add record display state
                        recordState.top = $intersect.css('top');
                        recordState.left = $intersect.css('left');
                        $intersect.css({
                            display: 'table',
                        });
                        // countTable($intersect);
                        countTableThead($intersect, countSpan);
                        // $intersect.find('tbody').addClass('removeCell');
                        $intersect
                            .css({
                                'width': _leftWidth + 1, //'auto', 17-08-28 Juan: width larger then $col
                                'max-width': 'none', //IE fixed
                                'border-width': 'none', //fixed border
                                'margin-bottom': '0px'
                            });
                        if (!recordState['sticky-intersect']) {
                            $intersect
                                .css({
                                    'opacity': 0,
                                    'display': 'none',
                                });
                        } else {
                            $intersect
                                .css({
                                    'opacity': 1,
                                    'display': 'table',
                                    'top': recordState.top,
                                    'left': recordState.left
                                });
                        }
                    }
                };



                //renderTable end

                /** API **/
                /* top */
                scope._top = function () {
                    //17-08-23 Juan: hack function type
                    if ($(element).find('[function-type]').length > 0 && !hasFunctionType) {
                        hasFunctionType = true;
                        $timeout(function () {
                            renderTable();
                        });
                    }

                    // Check if wrapper parent is overflowing along the y-axis
                    if ($t.height() > $w.height() && $w.scrollTop() > 0) {
                        /** set clone **/

                        if ($th) $th
                            .css({
                                'top': scope.eTop && scope.eTop === 'true' ? $w.scrollTop() - 1 : 0,
                                'max-width': 'none', //IE fixed
                                'border-width': 'none', //fixed border
                                'opacity': scope.eTop && scope.eTop === 'true' ? 1 : 0,
                                'display': scope.eTop && scope.eTop === 'true' ? 'table' : 'none',
                                'box-shadow': scope.eTop && scope.eTop === 'true' ? 'rgba(153, 153, 153, .8) 2px 2px 12px' : 'initial'
                            });

                        if ($intersect) $intersect
                            .css({
                                'top': scope.eTop && scope.eTop === 'true' ? $w.scrollTop() - 1 : 0,
                                'opacity': 1,
                                'display': 'table'
                            });

                    } else {
                        $t.find('thead')
                            .css({
                                'opacity': 1,
                            });

                        if ($th) $th
                            .css({
                                'top': 0,
                                'box-shadow': 'initial'
                            });

                        if ($intersect) $intersect
                            .css({
                                'top': 0
                            });
                    }
                };

                /* left */
                scope._left = function () {
                    if (!$cnt) return;
                    // Check if wrapper parent is overflowing along the x-axis
                    if ($t.width() > $w.width() && $w.scrollLeft() > 0) {

                        if ($t.find('tbody tr').length > 0) {

                            if ($col) $col
                                .css({
                                    'display': 'initial',
                                    'top': 0,
                                    'left': $w.scrollLeft(),
                                    'width': _leftWidth,
                                    'max-width': 'none', //IE fixed
                                    'border-width': 'none', //fixed border
                                    'opacity': 1,
                                });

                            if ($intersect) $intersect
                                .css({
                                    'left': $w.scrollLeft(),
                                    'width': _leftWidth + 1, //'auto', 17-08-28 Juan: width larger then $col
                                    'max-width': 'none', //IE fixed
                                    'border-width': 'none', //fixed border
                                    'opacity': 1,
                                    'display': 'table'
                                });
                        }

                    } else {
                        if ($col) $col
                            .css({
                                'opacity': 0,
                                'display': 'none'
                            });

                        if ($intersect) $intersect
                            .css({
                                'opacity': 0,
                                'display': 'none'
                            });
                    }
                };
                // 17-08-28 Juan: render it for init
                renderTable();
            }; // scope.freeze end
        }
    };
}]);
