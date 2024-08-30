/**================================================================================================
@program: enterAsTab.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
eSoafApp.directive('enterAsTab', function () {
	return {
		restrict: 'A',
      	link: function (scope, element, attrs) {
			//console.log(element);
			var elements = element.find(':input:not([type=file]):enabled:visible:not([readonly])');
			var nFormElemLen = elements.length;
      		for (var nPos=0; nPos<nFormElemLen;nPos++) {
      			var elem = elements[nPos]; 

      			if (elem.type === "button" || elem.type === "submit") {
					// $(elem).bind("focus", function () {
					// 	// this.style.background="yellow";
					// });
					// $(elem).bind("blur", function () {
					// 	//待 Fix
					// 	// this.style.border="outset 1px #9c9c9c";
					// 	// this.style.background="#E1E1E1";
					// });					
      			} else {
					// $(elem).bind("focus", function () {
					// 	// try {
					// 	// 	this.style.background="yellow";
					// 	// 	this.select();
					// 	// } catch(e) { }
					// });      				
					// $(elem).bind("blur", function () {
					// 	console.log("blur");
					// 	///======================
					// 	/// 由 e-smart-tag 依欄位狀態，決定顏色
					// 	///======================
					// 	if ($(this).attr("e-smart-tag") === undefined) {
					// 		this.style.background="#ffffff";
					// 		return;
					// 	}
					// });								
					$(elem).bind("keydown", function (event) {
						var nKeyCode = event.keyCode || event.which;
						var isShiftKey = event.shiftKey;
						var nCase1 = (nKeyCode === 13);					//下移
						var nCase2 = (nKeyCode === 9 && (!isShiftKey));	//下移
						var nCase3 = (nKeyCode === 9 && isShiftKey);	//上移

						if (nCase1 || nCase2 || nCase3) {
							var oNext = null;
							var oAll = $(".tab-content > div.tab-pane.active > div > div :input:enabled:visible:not([readonly]):not([tabindex=-1])");
							var nAllElemLen = oAll.length;
							for (var nPos=0; nPos<nAllElemLen;nPos++) {
								if (oAll[nPos] == this) {
									if (nCase1 || nCase2) {
										event.preventDefault ? event.preventDefault() : event.returnValue = false;
										if (nPos === nAllElemLen-1) {
											oNext = oAll[0];
										} else {
											oNext = oAll[nPos+1];
										}
									} else if (nCase3) {
										//第一個
										if (nPos === 0) {
											event.preventDefault ? event.preventDefault() : event.returnValue = false;
											oNext = oAll[nAllElemLen-1];
										}
									}
									break;
								}
							}
							if (oNext != null) {
								oNext.focus();	
							}						
						}
					});
				}
        	}
    	}
    };
});