/**================================================================================================
 @program     : app.js
 @description : platform main module.
 @author      : Ryan Huang, ArthurKO
 @version     : 1.0.20161227
 @lastUpdate  :
 			   2017/01/17 ArthurKO Add hotkey module.
 			　　 2016/11/29 ArthurKO Add $filterProvider with config.
 			   2016/08/23 ArthurKO Modify Version as 1.5.8.
=================================================================================================*/
/** <building> **/
var eSoafApp = angular.module("eSoafApp",  
			['ngRepeatUtils', 
             /* ui.grid 不支援 IE8 */ 
             /* 'ui.grid', 'ui.grid.selection', 'ui.grid.resizeColumns', */
			 'ngCookies',
             /* 'ui.multiselect' 不支援 IE8 */
             'multi-select',
             'ngDialog',
             'ui.bootstrap',
             'ui.sortable', 
             'angular-confirm',
             'i18nDynamic',
			 'chart.js',
			 'ngSanitize',
			 'colorpicker.module',
			 'textAngular',
			 'nvd3',
			 'ngTouch',
			 'angular.filter',
			 'mwl.calendar',
			 'ngRoute',
			 'ngCapsLock',
			 'pdf',
			 'uiGmapgoogle-maps']);

/** <injecting> **/
eSoafApp.config(["$httpProvider","$controllerProvider", "$provide", "$compileProvider", "uibDatepickerConfig", "uibDatepickerPopupConfig","$touchProvider","calendarConfig","uiGmapGoogleMapApiProvider",
	function($httpProvider, $controllerProvider, $provide, $compileProvider, uibDatepickerConfig, uibDatepickerPopupConfig, $touchProvider, calendarConfig, uiGmapGoogleMapApiProvider) {

	$httpProvider.defaults.useXDomain = true;
    $httpProvider.defaults.withCredentials = true;
    //$httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
    
    uiGmapGoogleMapApiProvider.configure({
        china: true
    });
    
    //ui.Bootstrap DatePicker Default Config
    uibDatepickerConfig.startingDay=1; //start with monday
    uibDatepickerConfig.showWeeks = false;
    uibDatepickerPopupConfig.showButtonBar = false;
    uibDatepickerPopupConfig.appendToBody=true;
    uibDatepickerPopupConfig.currentText="今天";
    uibDatepickerPopupConfig.closeText="取消";
    uibDatepickerPopupConfig.clearText="清除";
	
	// Let's keep the older references.
	eSoafApp._controller = eSoafApp.controller;
	eSoafApp._service = eSoafApp.service;
	eSoafApp._factory = eSoafApp.factory;
	eSoafApp._value = eSoafApp.value;
	eSoafApp._directive = eSoafApp.directive;

	// Provider-based controller.
	eSoafApp.controller = function( name, constructor ) {
		$controllerProvider.register( name, constructor );
		return( this );
	};

	// Provider-based service.
	eSoafApp.service = function( name, constructor ) {
		$provide.service( name, constructor );
		return( this );
	};

	// Provider-based factory.
	eSoafApp.factory = function( name, factory ) {
		$provide.factory( name, factory );
		return( this );
	};

	// Provider-based value.
	eSoafApp.value = function( name, value ) {
		$provide.value( name, value );
		return( this );
	};

	// Provider-based directive.
	eSoafApp.directive = function( name, factory ) {
		$compileProvider.directive( name, factory );
		return( this );
	};
	
	// ng-tough for mwl.calendar
	$touchProvider.ngClickOverrideEnabled(true);
	calendarConfig.displayAllMonthEvents = true;
	calendarConfig.allDateFormats.angular.title.year = "yyyy年";
	calendarConfig.allDateFormats.angular.title.month = "yyyy年 MMMM";
	calendarConfig.allDateFormats.angular.title.day = "yyyy年 MMMM d日 EEEE";
	calendarConfig.allDateFormats.angular.date.day = "MMM d日";
	

	// NOTE: You can do the same thing with the "filter"
	// and the "$filterProvider"; but, I don't really use
	// custom filters.
	
	// textAnuglar custom color
	$provide.decorator('taOptions', ['taRegisterTool', '$delegate', function(taRegisterTool, taOptions){
	      taRegisterTool('backgroundColor', {
	        display: "<button colorpicker class='btn btn-default ng-scope' title='Background Color' type='button' colorpicker-close-on-select colorpicker-position='bottom' ng-model='backgroundColor' style='background-color: {{backgroundColor}}'><i class='fa fa-paint-brush'></i></button>",
	        action: function (deferred) {
	          var self = this;
	          this.$editor().wrapSelection('backgroundColor', this.backgroundColor);
	          if (typeof self.listener == 'undefined') {
	            self.listener = self.$watch('backgroundColor', function (newValue) {
	              self.$editor().wrapSelection('backColor', newValue);
	            });
	          }
	          self.$on('colorpicker-selected', function () {
	            deferred.resolve();
	          });
	          self.$on('colorpicker-closed', function () {
	            deferred.resolve();
	          });
	          return;
	        }
	      });
	      taOptions.toolbar[1].unshift('backgroundColor');

	      taRegisterTool('fontColor', {
	        display: "<button colorpicker type='button' class='btn btn-default ng-scope'  title='Font Color'  colorpicker-close-on-select colorpicker-position='bottom' ng-model='fontColor' style='color: {{fontColor}}'><i class='fa fa-font '></i></button>",
	        action: function (deferred) {
	          var self = this;
	          if (typeof self.listener == 'undefined') {
	            self.listener = self.$watch('fontColor', function (newValue) {
	              self.$editor().wrapSelection('foreColor', newValue);
	            });
	          }
	          self.$on('colorpicker-selected', function () {
	            deferred.resolve();
	          });
	          self.$on('colorpicker-closed', function () {
	            deferred.resolve();
	          });
	          return false;
	        }
	      });
	      taOptions.toolbar[1].unshift('fontColor');

	      taOptions.setup.textEditorSetup=function($element) {
	        $element.attr('ui-codemirror', '');
	      };
	      return taOptions;
	}]);
	// textAnuglar custom color end

}]);

/** <running> **/
eSoafApp.run(function($confirmModalDefaults){
	  $confirmModalDefaults.templateUrl = 'assets/template/confirm.html';
	  $confirmModalDefaults.defaultLabels.title = '警告';
	  $confirmModalDefaults.defaultLabels.ok = '確定';
	  $confirmModalDefaults.defaultLabels.cancel = '取消';
});