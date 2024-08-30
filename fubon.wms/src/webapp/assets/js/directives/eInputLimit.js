/**================================================================================================
@program: eInputLimit.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('eInputLimit', function() {
	addCommasToInteger = function(val) {
		var commas, decimals, wholeNumbers;
		while(val.indexOf(',')!=-1){
			val = val.replace(',','');
		}
		decimals = val.indexOf('.') == -1 ? '' : val.replace(/^\d+(?=\.)/, '');
		wholeNumbers = val.replace(/(\.\d*)$/, '');
		commas = wholeNumbers.replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
		return "" + commas + decimals;
	};
	addPerCenToInteger = function(val) {
		return val + "%";
	};
	return {
		restrict: 'A',
		require: 'ngModel',
		scope: {
			pattern: '@pattern',
			nuu1: '@nuu1',
			nuu2: '@nuu2',
			type: '@eInputLimit'
		},
		link: function(scope, elem, attrs, ngModelCtrl) {
			if (!ngModelCtrl) {
				return;
			}
			scope.nuu1 = scope.nuu1 || "4";
			scope.nuu2 = scope.nuu2 || "2";

			ngModelCtrl.$formatters.push(function(val) {
				if (val && ("NN" === scope.type || "NABS" === scope.type || "NC" === scope.type || "NUU" === scope.type)) {
					val = addCommasToInteger(val.toString());
				}
				if (val && ("NP" === scope.type)) {
					val = addPerCenToInteger(val.toString());
				}
				
				return val;
			});
			
			elem.on('blur', function() {
				var formatter, viewValue, _i, _len, _ref;
				viewValue = ngModelCtrl.$modelValue;
				if (viewValue == null) {
					return;
				}
				_ref = ngModelCtrl.$formatters;
				for (_i = 0, _len = _ref.length; _i < _len; _i++) {
					formatter = _ref[_i];
					viewValue = formatter(viewValue);
				}
				ngModelCtrl.$viewValue = viewValue;
				return ngModelCtrl.$render();
			});
			
			elem.on('focus', function() {
				var val;
				val = elem.val();
				if (val && ("NN" === scope.type || "NC" === scope.type || "NUU" === scope.type))
					elem.val(val.replace(/,/g, ''));
				if (val && ("NP" === scope.type))
					elem.val(val.replace(/%/g, ''));
				return elem[0].select();
			});
			
			ngModelCtrl.$parsers.push(function(val) {
				var reg;
				scope.pattern = scope.pattern || '';
				
				if ("EA" === scope.type) {
					reg = new RegExp("[^a-zA-Z0-9" + scope.pattern + "]", "g");
				} else if ("EAD" === scope.type) {
					reg = new RegExp("[^a-zA-Z0-9-_" + scope.pattern + "]", "g");
				} else if ("N" === scope.type || "NN" === scope.type) {
					reg = new RegExp("[^0-9" + scope.pattern + "]", "g");
				}
		
				var clean = val == undefined ? undefined : val.replace(reg, '');
				
				if("ND" === scope.type || "NC" === scope.type)
					clean = /^-?\d*[.]?\d*/.exec(clean).toString();
				//NABS : 不能負號，千分位一個逗點
				else if("NABS" === scope.type)
					clean = /^\d*[.]?\d*/.exec(clean).toString();
				else if ("NUU" === scope.type || "NP" === scope.type) {
					var regnuu = new RegExp("\^\\d\{1," + scope.nuu1 + "\}\(\\\.\\d\{0," + scope.nuu2 + "\}\)\?\$", "g");
					if(clean && !regnuu.test(clean))
						clean = ngModelCtrl.$modelValue;
				}
//				clean = clean == '' ? undefined : clean;
				
				if (val != clean) {
					ngModelCtrl.$setViewValue(clean);
					ngModelCtrl.$render();
				}
				
				return clean;
			});
		}
	};
});