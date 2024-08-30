/**================================================================================================
@program: eCombobox.js
@description: JQuery-UI
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eCombobox', ['projInfoService', 'getParameter','$timeout', '$q', function(projInfoService, getParameter, $timeout, $q) {
    return {
        restrict: 'E',
        transclude: true,
        replace: true,
        scope:{
        	id:'@',
        	ngDisabled:'=?',
        	ngRequired:'=?',
        	ngReadonly:'=?',
        	ngModel:'=?',
        	ngDatasource:"=?",
        	ngParamtype:"=?",
        	ngValue:'=?',
        	ngLabel:'=?',
        	ngItem:'=?',
        	ngEditable:'=?',
        	ngValidate:'=?',
        	ngFormat:'@',
        	ngChange: '&?ngChange',
        	width:'@',
        	height:'@',
        	blankRow:'@',
        	modelName:'@?ngModel', //get Model Name.
        	replace:'=?', //get data from DB as always.

			multiple: '@?',
			separator: '@?', 	// 多選項的資料分隔符號
			labelSeparator: '@?'// 多選項的標籤分隔符號
        },
        template: function(element, attrs) {
			if (attrs.multiple) {
				return '<div class="multiselect">' +
					'    <div class="selectBox">' +
					'      <select class="combobox">' +
					'        <option>{{selected || "請選擇"}}</option>' +
					'      </select>' +
					'      <div class="overSelect"></div>' +
					'    </div>' +
					'    <div class="checkboxes">' +
					'		  <label style="color: #1c94c4; font-size: 1.1em; display: block;"  ' +
					'				 onmouseover="this.style.background=\'#FEF6D1\'; this.style.color=\'#C77405\'; "   ' +
					'				 onmouseout="this.style.background=\'#F9F9F9\'; this.style.color=\'#1c94c4\'; "  ' +
					'				 ng-repeat="each in ngDatasource track by $index"' +
									 // 如果 ngDatasource 同時分給單選與複選用，避開單選的「請選擇」項目
					'				 ng-if="each.DATA">' +
					'			<input class="checkboxValue" type="checkbox" ng-model="each.checked" ' +
					'				   value="{{each.DATA}}" />&emsp;{{each.LABEL}}</label>' +
					'    </div>' +
					'  </div>';
			}

            return '<span class="e-combobox" style="white-space: nowrap">'+
					   '<input name="' + attrs.id + '" style="line-height:normal!important;"/>'+
					   '<button type="button"></button>'+
					   '<button type="button" ng-click="Refresh()"></button>'+
				   '</span>';
        },
        link: function (scope, element, attrs, ctlModel, transclude) {
        	/**============================================================================
    		 *  @Description: Variable Settle
    		 *=============================================================================*/
			// 依 attrs.multiple 決定複選邏輯或單選邏輯
			if (attrs.multiple) multiple(attrs);
			else single();

			function single() {
				var input = $(element).children(":nth-child(1)"),
					btn = $(element).children(":nth-child(2)"),
					btnError = $(element).children(":nth-child(3)");

				scope.height = scope.height || '30px';

				transclude(function (clone) {
					element.parent().append(clone);
				});

				/**============================================================================
				 *  @Description: Function Calls
				 *=============================================================================*/
				setElementCss();
				createAutoComplete();
				createShowAllButton();

				/**============================================================================
				 *  @Description: 元件引用自訂函式
				 *=============================================================================*/
				/*-----------------------------------------------------------------------------
                 * @Description: button on click
                 *-----------------------------------------------------------------------------*/
				scope.Refresh = function () {
					if (attrs.ngParamtype) {
						getDsByParamType();
					}
				};

				/*-----------------------------------------------------------------------------
                 * @Description: refresh element select
                 *-----------------------------------------------------------------------------*/
				function focusThis() {
					input.trigger("focus").trigger("blur");
				}

				/*-----------------------------------------------------------------------------
                 * @Description: element style setting
                 *-----------------------------------------------------------------------------*/
				function setElementCss() {
					$(element).addClass("custom-combobox")
						.css("position", "relative")
						.css("display", "inline-block");
				}

				/*-----------------------------------------------------------------------------
                 * @Description: JQueryUI AutoComplete by 下拉選單設定
                 *-----------------------------------------------------------------------------*/
				function createAutoComplete() {

					//connect model value
					//check variable
					scope.modelName = scope.modelName.toString().trim();
					//setting name
					if (scope.modelName.indexOf(".") !== -1) {
						scope.str = scope.modelName.split(".");
					} else {
						scope.str = false;
					}

					var rawData = scope.ngDatasource;
					if (attrs.ngParamtype) {
						getDsByParamType();
					}
					var formatedData = [];
					if (scope.ngDatasource) {
						formatedData = formatDatasource(scope.ngDatasource, attrs, projInfoService);
					}

					input.addClass("custom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left")
						.css("margin", "0")
						.css("padding", "5px 10px")
						.css("width", attrs.width)
						.autocomplete({
							delay: 0,
							minLength: 0,
							focus: function (event, ui) {
								input.val(ui.item.label);
								return false;
							},
							select: function (event, ui) {
								$(input).val(ui.item.label);
								return false;
							},
							source: formatedData
						})
						.tooltip({
							tooltipClass: "ui-state-highlight"
						});
					//ngModel有預設值
					if (formatedData && scope.ngModel) {
						initComboDataByNgModel(scope, input, scope.ngModel);
					} else {
						input.val('請選擇');
					}
					//
					scope.$watch('ngDisabled', function (newValue, oldValue) {
						if (newValue === oldValue) {
							return;
						}
						input.prop('disabled', scope.ngDisabled);
						btn.prop('disabled', scope.ngDisabled || scope.ngReadonly);
						btnError.prop('disabled', scope.ngDisabled);
					});
					scope.$watch('ngRequired', function (newValue, oldValue) {
						if (newValue === oldValue) {
							return;
						}
						input.prop("required", scope.ngRequired);
					});
					scope.$watch('ngReadonly', function (newValue, oldValue) {
						if (newValue === oldValue) {
							return;
						}
						input.prop('readonly', scope.ngReadonly);
						btn.prop('disabled', scope.ngDisabled || scope.ngReadonly);
					});
					/*-----------------------------------------------------------------------------
                     * @Description: Data two ways binding (list)
                     * @Modify:
                     *-----------------------------------------------------------------------------*/
					scope.$watch("ngDatasource", function (newValue, oldValue) {
						checker("data-source-local", newValue, oldValue);
					});
					/*-----------------------------------------------------------------------------
                     * @Description: Data two ways binding (variables)
                     * @Modify:
                     * 			2016/04/08 ArthurKO
                     *-----------------------------------------------------------------------------*/
					scope.$watch('ngModel', function (newValue, oldValue) {
						if (newValue === oldValue) {
							return;
						}

						;
						if (angular.isDefined(attrs.ngChange)) {
							scope.ngChange();
						}
						;
						if (!newValue) {
							//check dataSource value type
							input.val('請選擇'); //reset value
							scope.ngModel = '';
							scope.ngValue = '';
							scope.ngLable = '請選擇';
							scope.ngItem = null;
						} else if (newValue != scope.ngValue) {
							scope.ngValue = newValue;
							initComboDataByNgModel(scope, input, newValue);
						}
						return;
					});
					//jQuery Events
					input.on("click", function (event) {
						if (!scope.ngDisabled && !scope.ngReadonly) {
							wasOpen = input.autocomplete("widget").is(":visible");
							if (wasOpen) {
								return;
							}
							// 2017/5/4 add
							if (input.val() == '請選擇')
								input.val('');
							// Pass empty string as value to search for, displaying all results
							input.autocomplete("search", "");
						}
					});
					input.on("keydown", function (event) {
						//9-TAB, 40-DOWN
						if (!scope.ngEditable) {
							event.preventDefault();
						}
					});
					//Add: pcc727
					input.on("blur", function (event) {
						scope.ngLable = '請選擇';
						if (scope.ngModel) {
							initComboDataByNgModel(scope, input, scope.ngModel);
						} else {
							input.val('請選擇');
						}
					});
					//回傳選項
					input.on("autocompleteselect", function (event, ui) {
						//connect model value
						for (var i = 0; i < scope.ngDatasource.length; i += 1) {
							if (scope.ngDatasource[i].label === input.val()) {
								scope.$parent[scope.modelName.toString()] = scope.ngDatasource[i].value;
								break;
							}
							;
						}
						;
						//others
						var placeholder = attrs.placeholder || projInfoService.eComboPlaceholder
						if (ui.item.value == placeholder) {
							input.autocomplete("instance").term = "";
							scope.ngModel = '';
							scope.ngValue = '';
							scope.ngLabel = '';
							scope.ngItem = '';
						} else {
							scope.ngModel = ui.item.value;
							scope.ngValue = ui.item.value;
							scope.ngLabel = ui.item.orgLabel;
							scope.ngItem = ui.item;
						}
					});
					//檢核輸入
					input.on('autocompletechange', function (event, ui) {
						if (scope.ngValidate == false) {
							return;
						}
						validateInput(event, ui, scope, input);
					});
					//檢核捲動自動收合選單
					$("html, body").on('scroll', function () {
						input.autocomplete().autocomplete("close");
					});

				}

				/*-----------------------------------------------------------------------------
                 * @Description: UI元素設定
                 *               element settle
                 *-----------------------------------------------------------------------------*/
				function createShowAllButton() {
					input.css("height", scope.height).css("vertical-align", "middle");
					btnError.attr("tabIndex", -1)
						.attr("title", "Refresh")

						.button({
							icons: {
								primary: "ui-icon-arrowrefresh-1-e"
							},
							text: false
						})
						.removeClass("ui-corner-all")
						.addClass("custom-combobox-toggle ui-corner-right")
						.css("vertical-align", "middle")
						.css("margin-left", "-1px")
						.css("height", scope.height)
						.css("padding", "0");
					btnError.hide();
					btn.attr("tabIndex", -1)
						//        		   .attr( "title", "Show All Items" )
						.tooltip()
						.button({
							icons: {
								primary: "ui-icon-triangle-1-s"
							},
							text: false
						})
						.removeClass("ui-corner-all")
						.addClass("custom-combobox-toggle ui-corner-right")
						.css("vertical-align", "middle")
						.css("height", scope.height)
						.css("margin-left", "-1px")
						.css("padding", "0")
						.on("mousedown", function () {
							wasOpen = input.autocomplete("widget").is(":visible");
						})
						.on("click", function (event) {
							event.preventDefault();
							input.trigger("focus");
							// Close if already visible
							if (wasOpen) {
								return;
							}
							// Pass empty string as value to search for, displaying all results
							input.autocomplete("search", "");
						});
				}

				/*-----------------------------------------------------------------------------
                 * @Description: 驗證資料
                 *               Verification data
                 *-----------------------------------------------------------------------------*/
				function validateInput(event, ui, scope, input) {
					// Selected an item, nothing to do
					if (ui.item) {
						return;
					}

					// Search for a match (case-insensitive)
					var textVal = input.val(),
						valueLowerCase = textVal.toLowerCase(),
						valid = false;
					angular.forEach(scope.ngDatasource, function (value, key) {
						if (value.label.toLowerCase() === valueLowerCase) {
							valid = true;
						}
					});

					// Found a match, nothing to do
					if (valid) {
						return;
					}
					scope.$parent.showErrorMsg(textVal + " 不在下拉選單中");
					// Remove invalid value
					input.val("")
						.attr("title", textVal + " 不在下拉選單中")
						.tooltip("open");
					input.on("tooltipclose", function (event, ui) {
						input.attr("title", "").tooltip("close");
					});

					input.autocomplete("instance").term = "";

				}

				/*-----------------------------------------------------------------------------
                 * @Description: 資料處理末端
                 *               Data processing terminal
                 *-----------------------------------------------------------------------------*/
				function formatDatasource(datasource) {
					/**
					 * F1 key:data, value:data-label
					 * F2 key:data, value:data
					 * F3 key:data, value:label
					 */
					if (attrs.ngFormat) {
						//clear exist 請選擇
						for (var i = 0; i < datasource.length; i += 1) {
							if (datasource[i].label == '請選擇') {
								datasource.splice(i, 1);
							}
							;
						}
						;
						angular.forEach(datasource, function (row, index, objs) {
							switch (attrs.ngFormat) {
								case "F1":
									row.orgLabel = row.LABEL ? row.LABEL.toString().trim() : "";
									row.value = row.DATA;
									if ((row.DATA && row.DATA.toString().trim() !== '') && (row.LABEL && row.LABEL.toString().trim() !== '')) row.label = row.DATA + '-' + row.LABEL.toString().trim();
									if ((!row.DATA || row.DATA.toString().trim() === '') && (row.LABEL && row.LABEL.toString().trim() !== '')) row.label = row.LABEL.toString().trim();
									if ((!row.LABEL || row.LABEL.toString().trim() === '') && (row.DATA && row.DATA.toString().trim() !== '')) row.label = row.DATA.toString().trim();
									break;
								case "F2":
									row.orgLabel = row.LABEL ? row.LABEL.toString().trim() : "";
									row.label = row.DATA;
									row.value = row.DATA;
									break;
								case "F3":
									row.orgLabel = row.LABEL ? row.LABEL.toString().trim() : "";
									row.label = row.LABEL ? row.LABEL.toString().trim() : "";
									row.value = row.DATA;
									break;
								default:
									row.orgLabel = row.LABEL ? row.LABEL.toString().trim() : "";
									row.label = row.LABEL ? row.LABEL.toString().trim() : "";
									row.value = row.DATA;
									break;
							}
						});
					}
					var flag = false;
					angular.forEach(datasource, function (row) {
						if (row.label && row.label.toString().trim() === '請選擇') {
							flag = true;
						}
						;
					});
					//add default item for select all
					if (!flag) {
						//check dataSource value type
						var str = '';
						for (var i = 0; i < datasource.length; i += 1) {
							if (datasource[i].value && (typeof datasource[i].value).toUpperCase() !== 'STRING') {
								str = null;
								break;
							}
						}
						datasource.unshift({
							LABEL: '請選擇',
							DATA: str,
							orgLabel: '請選擇',
							label: '請選擇',
							value: str
						});
					}
					;
					//clear no used {}
					for (var i = 0; i < datasource.length; i += 1) {
						if (datasource[i].label == undefined && datasource[i].value == undefined) {
							datasource.splice(i, 1);
						}
						;
					}
					;
					return datasource;
				}

				/*-----------------------------------------------------------------------------
                 * @Description: 向資料庫參數表取資料
                 *               get data with DB paramter sheets
                 *-----------------------------------------------------------------------------*/
				function getDsByParamType() {
					if (angular.isDefined(scope.ngDatasource)) {
						return;
					}
					getParameter.XML(attrs.ngParamtype, function (totas) {
						/**
						 * 取回相關 ComboBox Data
						 * @type {Array}
						 */
						//totas[totas.length - 1].body.result === 'success' => if(totas)
						if (totas) {
							btnError.hide();
							btn.show();
							input.attr('disabled', scope.ngDisabled);
							scope.ngDatasource = totas.data[0];
							input.autocomplete({source: formatDatasource(scope.ngDatasource)});
						} else {
							btnError.show();
							btn.hide();
							input.attr('disabled', true);
							input.attr("placeholder", "發生錯誤，請重新整理");
						}
					}, function () {
						if (angular.isDefined(scope.replace)) {
							return scope.replace;
						} else {
							return undefined;
						}
					});
				}

				/*-----------------------------------------------------------------------------
                 * @Description: 以綁定值初始化選單資料
                 *               To bind data to initialize the value menu
                 *-----------------------------------------------------------------------------*/
				function initComboDataByNgModel(scope, input, value) {
					if (scope.ngDatasource) {
						var keepGoing = true;
						angular.forEach(input.autocomplete("option", "source"), function (row, index, objs) {
							if (keepGoing && row.DATA == value) {
								scope.ngLable = row.label;
								scope.ngItem = row;
								keepGoing = false;
							}
						});
					}
					input.val(scope.ngLable);
				}

				/*-----------------------------------------------------------------------------
                 * @Description: 檢查器(檢查對象flag)
                 *               To checking with target.
                 *-----------------------------------------------------------------------------*/
				function checker(flag, n, o) {
					switch (flag) {
						case 'data-source-local':
							if (!angular.isDefined(scope.ngDatasource)) return;
							if (!angular.isDefined(n)) return;
							if (angular.isDefined(n) && angular.isDefined(o) && n === o) return;
							scope.ngLable = '請選擇';
							if (Object.prototype.toString.call(n) === '[object Array]') {
								input.autocomplete({source: formatDatasource(n)});
							} else {
								input.autocomplete({source: formatDatasource(scope.ngDatasource)});
							}
							if (scope.ngModel) {
								initComboDataByNgModel(scope, input, scope.ngModel);
							} else {
								input.val('請選擇');
							}
							break;
						default:
							break;
					}
				}

				/**============================================================================
				 *  @Description: 延遲檢查器
				 *=============================================================================*/
				$timeout(function () {
					checker("data-source-local", true, false);
				});

				/**============================================================================
				 *  @Description: CSS Settle
				 *=============================================================================*/
				//scrollable dropdown
				$(".ui-autocomplete").css("max-height", "150px")
					.css("overflow-y", "auto")
					.css("overflow-x", "hidden")
					.css("white-space", "nowrap");
				input.prop("required", scope.ngRequired);
				input.prop('readonly', scope.ngReadonly);
				input.prop('disabled', scope.ngDisabled);
				btn.prop('disabled', scope.ngDisabled || scope.ngReadonly);
				btnError.prop('disabled', scope.ngDisabled);
			}

			function getDataSourceDBMultiple() {
				if (!angular.isDefined(scope.ngDatasource)) {
					getParameter.XML(attrs.ngParamtype, function (totas) {
						if (totas) {
							scope.ngDatasource = totas.data[0];
						}
					});
				}
			}

			function multiple(attrs) {
				getDataSourceDBMultiple();

				const multiselect = $(element);
				const selectBox = multiselect.children(":nth-child(1)");
				const checkboxes = multiselect.children(":nth-child(2)");
				const combobox = selectBox.children(":nth-child(1)");
				const overSelect = selectBox.children(":nth-child(2)");

				selectBox.on('click', () => checkboxes.show())
					.css('position', 'relative');

				checkboxes.on('click', e => {
					// 捕捉 checkboxes 底下的 input click 事件，並將勾選的項目連動 ngModel
					const isInput = e.target.toString() === '[object HTMLInputElement]';
					if (isInput) {
						const checked = scope.ngDatasource.filter(e => e.checked);
						scope.ngModel = checked.map(e => e.DATA).join(attrs.separator);
					}
				}).on('mouseleave', e => {
					checkboxes.hide();
				}).css('display', 'none')
				  .css('border', '1px #dadada solid')
				  .css('position', 'absolute')
				  .css('min-width', '200px')
				  .css('max-height', '165px')
				  .css('background', '#F9F9F9')
				  .css('overflow', 'auto')
				  .css('padding', '0px 10px')
				  .css('zIndex', '1');

				multiselect.css('width', '200px');
				combobox.css('width', '100%')
					.css('fontWeight', 'bold')
					.css('paddingLeft', '10px')
					.css("height", '30px')
					.addClass("custom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left");

				overSelect
					.css('position', 'absolute')
					.css('left', '0')
					.css('right', '0')
					.css('top', '0')
					.css('bottom', '0')

				scope.$watch('ngModel', function (newValue, oldValue) {
					if (newValue) {
						const arr = newValue.split(attrs.separator);
						scope.ngDatasource.forEach(
							e => e.checked = arr.contains(e.DATA)
						);

						const checked = scope.ngDatasource.filter(e => e.checked);
						const checkedLabel = checked.map(e => e.LABEL).join(attrs.labelSeparator);
						const checkedNum = checked.length;

						scope.selected = checkedNum === 0 ? '請選擇' :
						checkedLabel.length < 20 ? checkedLabel : checkedNum + ' items selected';
					}
					// 當 ngModel 被外界清除值後，將 option 勾勾取消
					if (oldValue && !newValue) {
						scope.ngDatasource = scope.ngDatasource.map(e => _.omit(e, 'checked'));
						scope.selected = '請選擇';
					}

					if (newValue !== oldValue && angular.isDefined(attrs.ngChange)) {
						scope.ngChange();
					}
				});

				scope.$watch('ngDisabled', function (newValue, oldValue) {
					combobox.prop('disabled', scope.ngDisabled);
					if (scope.ngDisabled || scope.ngReadonly) {
						selectBox.unbind('click');
					} else {
						selectBox.on('click', () => checkboxes.show())
					}
				});

				scope.$watch('ngReadonly', function (newValue, oldValue) {
					combobox.prop('readonly', scope.ngReadonly);
					if (scope.ngReadonly || scope.ngDisabled) {
						selectBox.unbind('click');
					} else {
						selectBox.on('click', () => checkboxes.show())
					}
				});

				scope.$watch('ngRequired', function (newValue, oldValue) {
					combobox.prop("required", scope.ngRequired);
				});
			}
		}
    };

}]);
