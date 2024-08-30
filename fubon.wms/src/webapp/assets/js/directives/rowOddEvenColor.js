/**================================================================================================
@program: rowOddEvenColor.js
@description:
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('rowOddEvenColor', function() {
  return function(scope, element, attrs, $index) {
	  
	  var $this = $(element);
	  var parent = $(element).parent();
	  var index = parent.children().index($this);
	  console.log('rowOddEven');
	  $this.find('div').addClass("rowOdd");
	  if((index % 2) == 0){
		  $this.find('div').addClass("rowEven");
	  }
  };
});