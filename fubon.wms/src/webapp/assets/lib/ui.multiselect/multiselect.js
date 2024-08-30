//
// Source: https://github.com/amitava82/angular-multiselect
//
//=========================================================
//
//使用範例
// JavaScript
//        $scope.cbData.xxx =  [{code_id:'1', code_desc: 'Audi'}, 
//                              {code_id:'2', code_desc: 'BMW'}, 
//                              {code_id:'3', code_desc: 'Honda'}];
//        $scope.tioa.yyy = "2,3";
//
// Html
//        <multiselect class="input-xlarge" width="280px"
//          multiple="true"
//          ng-model="tioa.yyy" 
//          options="o.code_id+'-'+o.code_desc as o.code_id for o in cbData.xxx">
//        </multiselect>
//
//=========================================================
angular.module('ui.multiselect', [])
  //解析 Options 格式
  //from bootstrap-ui typeahead parser
  .factory('optionParser', ['$parse', function ($parse) {

    //                      00000111000000000000022200000000000000003333333333333330000000000044000
    var TYPEAHEAD_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?\s+for\s+(?:([\$\w][\$\w\d]*))\s+in\s+(.*)$/;

    return {
      parse: function (input) {

        var match = input.match(TYPEAHEAD_REGEXP), modelMapper, viewMapper, source;
        if (!match) {
          throw new Error(
            "Expected typeahead specification in form of '_modelValue_ (as _label_)? for _item_ in _collection_'" +
              " but got '" + input + "'.");
        }
        //console.log("parse");
        //console.log(match[4] + "|" + match[3] + "|" + match[2] + "|" + match[1]);

        return {          
          itemName: match[3],
          source: $parse(match[4]),
          viewMapper: $parse(match[1]),
          modelMapper: $parse(match[1]),
          keyMapper: $parse(match[2] || match[1])
        };
      }
    };
  }])

  .directive('multiselect', ['$parse', '$document', '$compile', '$interpolate', 'optionParser',

    function ($parse, $document, $compile, $interpolate, optionParser) {
      return {
        restrict: 'E',
        require: 'ngModel',
        link: function (originalScope, element, attrs, modelCtrl) {

          var exp = attrs.options,
            parsedResult = optionParser.parse(exp),
            isMultiple = attrs.multiple ? true : false,
            required = false,
            nWidth = attrs.width || "200px",
            scope = originalScope.$new(),
            changeHandler = attrs.change || angular.noop;

          scope.Width = nWidth;
          scope.items = [];
          scope.header = 'Select';
          scope.multiple = isMultiple;
          scope.disabled = false;

          originalScope.$on('$destroy', function () {
            scope.$destroy();
          });

          var popUpEl = angular.element('<multiselect-popup' + 
                        (attrs.templateUrl ? (' template-url="' + attrs.templateUrl + '"'): '' ) + 
                        '></multiselect-popup>');
						
          //required validator
          if (attrs.required || attrs.ngRequired) {
            required = true;
          }
          attrs.$observe('required', function(newVal) {
            required = newVal;
          });

          //watch disabled state
          scope.$watch(function () {
            return $parse(attrs.disabled)(originalScope);
          }, function (newVal) {
            scope.disabled = newVal;
          });

          //watch single/multiple state for dynamically change single to multiple
          scope.$watch(function () {
            return $parse(attrs.multiple)(originalScope);
          }, function (newVal) {
            isMultiple = newVal || false;
          });

          //watch option changes for options that are populated dynamically
          scope.$watch(function () {
            return parsedResult.source(originalScope);
          }, function (newVal) {
            if (angular.isDefined(newVal))
              parseModel();
          }, true);

          //watch model change
          scope.$watch(function () {
            return modelCtrl.$modelValue;
          }, function (newVal, oldVal) {
            //when directive initialize, newVal usually undefined. Also, if model value already set in the controller
            //for preselected list then we need to mark checked in our scope item. But we don't want to do this every time
            //model changes. We need to do this only if it is done outside directive scope, from controller, for example.

            //Edit By Ryan
            if (scope.multiple && (typeof newVal==="string")) {
              newVal = newVal.split(",");
            }

            if (angular.isDefined(newVal)) {
              markChecked(newVal);
              scope.$eval(changeHandler);
            }
            getHeaderText();
            modelCtrl.$setValidity('required', scope.valid());
          }, true);

          //資料來源
          function parseModel() {
            scope.items.length = 0;
            var model = parsedResult.source(originalScope);
            //console.log("parseModel");
            //console.log(model);
            if(!angular.isDefined(model)) return;
            for (var i = 0; i < model.length; i++) {
              var local = {};
              local[parsedResult.itemName] = model[i];
              scope.items.push({
                label: parsedResult.viewMapper(local),
                model: parsedResult.modelMapper(local),
                key: parsedResult.keyMapper(local),
                checked: false
              });
            }
          }

          parseModel();

          element.append($compile(popUpEl)(scope));

          //呈現區
          function getHeaderText() {
            if (is_empty(modelCtrl.$modelValue)) {
              scope.header = attrs.msHeader || '';
              return;
            } 
            
            if (isMultiple) {
                if (attrs.msSelected) {
                    scope.header = $interpolate(attrs.msSelected)(scope);
                } else {                 
                  if (modelCtrl.$modelValue.length >= 1) {
                    value = [];
                    angular.forEach(scope.items, function (item) {
                      if (item.checked) value.push(item.label);
                    })
                    scope.header = value.join(",");
                  } else {
                    scope.header = '';
                  }
                }
            } else {
              var local = {};
              local[parsedResult.itemName] = modelCtrl.$modelValue;
              scope.header = parsedResult.viewMapper(local) || scope.items[modelCtrl.$modelValue].label;
            }
          }
          
          function is_empty(obj) {
            if (angular.isNumber(obj)) return false;
            if (obj && obj.length && obj.length > 0) return false;
            for (var prop in obj) if (obj[prop]) return false;
            return true;
          };

          scope.valid = function validModel() {
            if(!required) return true;
            var value = modelCtrl.$modelValue;
            return (angular.isArray(value) && value.length > 0) || (!angular.isArray(value) && value != null);
          };

          function selectSingle(item) {
            if (item.checked) {
              scope.uncheckAll();
            } else {
              scope.uncheckAll();
              item.checked = !item.checked;
            }
            setModelValue(false);
          }

          function selectMultiple(item) {
            item.checked = !item.checked;
            setModelValue(true);
          }

          function setModelValue(isMultiple) {
            var value;

            if (isMultiple) {
              value = [];
              angular.forEach(scope.items, function (item) {
                if (item.checked) value.push(item.key || item.model);
              })
            } else {
              angular.forEach(scope.items, function (item) {
                if (item.checked) {
                  value = item.key || item.model;
                  return false;
                }
              })
            }

            //Edit By Ryan
            if (scope.multiple && angular.isArray(value)) {
              value = value.join();
            }
            //回傳
            modelCtrl.$setViewValue(value);
          }
          //設定、判斷是否要勾選的地方
          function markChecked(newVal) {
            if (!angular.isArray(newVal)) {
              angular.forEach(scope.items, function (item) {
                if (angular.equals(item.model, newVal)) {
                  scope.uncheckAll();
                  item.checked = true;
                  setModelValue(false);
                  return false;
                }
              });
            } else {
              angular.forEach(scope.items, function (item) {
                console.log(item);                

                item.checked = false;
                angular.forEach(newVal, function (i) {
                    var sKey = item.key || item.model;
                    if (angular.equals(sKey, i)) {
                      item.checked = true;
                    }                     
                });
              });
            }
          }

          scope.checkAll = function () {
            if (!isMultiple) return;
            angular.forEach(scope.items, function (item) {
              item.checked = true;
            });
            setModelValue(true);
          };

          scope.uncheckAll = function () {
            angular.forEach(scope.items, function (item) {
              item.checked = false;
            });
            setModelValue(true);
          };

          scope.select = function (item) {
            if (isMultiple === false) {
              selectSingle(item);
              scope.toggleSelect();
            } else {
              selectMultiple(item);
            }
          }
        }
      };
    }])

  .directive('multiselectPopup', ['$document', function ($document) {
    return {
      restrict: 'E',
      scope: false,
      replace: true,
      templateUrl: function (element, attr) {
                return attr.templateUrl || 'assets/lib/Angular.multiselect/multiselect.tmpl.html';
        },
      link: function (scope, element, attrs) {

        scope.isVisible = false;

        scope.toggleSelect = function () {
          if (element.hasClass('open')) {
            element.removeClass('open');
            $document.unbind('click', clickHandler);
          } else {
            element.addClass('open');
            $document.bind('click', clickHandler);
            scope.focus();
          }
        };

        function clickHandler(event) {
          if (elementMatchesAnyInArray(event.target, element.find(event.target.tagName)))
            return;
          element.removeClass('open');
          $document.unbind('click', clickHandler);
          scope.$apply();
        }

        scope.focus = function focus(){
          var searchBox = element.find('input')[0];
          searchBox.focus(); 
        }

        var elementMatchesAnyInArray = function (element, elementArray) {
          for (var i = 0; i < elementArray.length; i++)
            if (element == elementArray[i])
              return true;
          return false;
        }
      }
    }
  }]);
