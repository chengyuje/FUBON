/**================================================================================================
@program: esoaf-framework.js

@description: Web fixed by framework functions, Library of platform superfluity methods.
@version: 1.0.20170419
=================================================================================================*/
//JQueryUI
//combobox
var bsButton = $.fn.button.noConflict(); // reverts $.fn.button to jqueryui btn
$.fn.bsButton = bsButton; // assigns bootstrap button functionality to $.fn.btn

var bsTooltip = $.fn.tooltip.noConflict(); // reverts $.fn.button to jqueryui btn
$.fn.bsTooltip = bsTooltip; // assigns bootstrap button functionality to $.fn.btn

//set base within mobile
//Moved by mobile.js
angular.element(document.getElementsByTagName('head')).append(angular.element('<base href="' + window.location.pathname + '" />'));