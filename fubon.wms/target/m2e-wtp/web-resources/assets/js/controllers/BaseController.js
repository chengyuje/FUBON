///======================
/// Base Controller
///======================
function BaseController($rootScope, $scope, $http, projInfoService, socketService, ngDialog, $q, i18nLanguage, $filter, $window, $timeout, $interval, getParameter) {
    $scope.controllerName = "";

	$rootScope.language = $window.navigator.language || $window.navigator.userLanguage;



    /* current controller name */
    if($scope.$parent.controllerName){
    	$scope.localCtrl = $scope.$parent.controllerName;
    	$rootScope.localCtrl = $scope.localCtrl;
    }

    /*
     * 初始分頁所需資訊
     */
	$scope.initLimit = function(){
		// 聽說這個(預備移除)
		$scope.rowCountLimit = projInfoService.$paging.rowCountLimit;
		// 聽說這個也是
		$scope.displayLimit = projInfoService.getSinglePageRows()[0].DATA;
		$scope.inputVO = $scope.inputVO || {};
		$scope.inputVO.pageCount = $scope.rowCountLimit;
		$scope.inputVO.currentPageIndex = 0;
	};

	$scope.initLimit();
//	/*
//	 * 排序
//	 */
//	$scope.reverse = false;
//    $scope.orderby = function(predicate) {
//      $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
//      $scope.predicate = predicate;
//    };

    /** Get Time **/
    /* current date time bar on bottom  */
    var updateTime = function() {
      return $scope.clock = new Date();
    };
	/** angular filter 將因 $scope 不斷變化而多次執行計算
	 *  但該功能可能是讓 $scope 數據每秒即時更新，即時反應在畫面上的用途。
	 *  因此還是先打開，體感較好。
	 * **/
    $interval(function() {
      return updateTime();
    }, 1000);

    $scope.pagingList = function(dest, src){
    	console.log('mode='+projInfoService.$paging.mode);
    	if(projInfoService.$paging.mode == 1 || projInfoService.$paging.mode == 3){
    		angular.forEach(src, function(element){
        		dest.push(element);
        	});
    		return;
    	}
    	if(projInfoService.$paging.mode == 2){
    		dest.length = 0;
    		angular.forEach(src, function(element){
        		dest.push(element);
        	});
    	}

    }

    /*
     * data mapping label資料
     */
    $scope.mappingSet = $scope.mappingSet || {};


    /**
     * 改變多國語系
     * 繁中 - zh-tw
     * 英文 - en
     */
    $scope.$changeLocale = function(newLocale){
    	i18nLanguage.setLocale(newLocale);
		$rootScope.language = newLocale;
	};

    /**
     * 平台或交易間傳遞參數用
     * @type {[type]}
     */
    $scope.args = $rootScope.args;
    $scope.btnAuth = $rootScope.btnAuth || "";
    $scope.today = getToday();
    /**
     * dgData 最大筆數
     * @type {Number}
     */
    $scope.pageMaxRow = 50;
    /**
     * 儲存 ComboBox Data 相關資料的地方
     * @type {Object}
     */
    $scope.cbData = {};

    /**========================================================================
	Platform Communication function and server
	平台程式即時通訊資料傳遞函式與擬伺服器
    @description: controller to controller, scope to scope.
    @LastUpdate: 2016/03/19 ArthurKO
    @parameter:
    	type = 'set' || 'get'
    	name = 'FPPDM100'
    	args = {CUST_ID:'A123456789',PROD_ID:'10010',...} || any kind of data
    @example:
    ※'set':
    	var data = row;
    	$scope.connector('set','FPPDM100', data);
    ※'get':
    	var data = $scope.connector('get','FPPDM100');
    ※print:
    	$scope.connector();
    ==========================================================================*/
    $scope.connector = function (type, name, args) {
    	if(!type && !name && !args){
    		return;
    	};
    	//check parameter
    	if(!type || !name){
    		return;
		};
		//set id
		var id = angular.copy(name.toString().toUpperCase().trim());
		//select type
    	switch(type.toString().toUpperCase().trim()){
	    	case 'SET':
		    	var data = angular.copy(args);
		    	if($rootScope.server.id.indexOf(id)===-1){
		    		$rootScope.server.id.push(id);
		    		$rootScope.server.data.push(data);
		    	}else{
		    		$rootScope.server.data[$rootScope.server.id.indexOf(id)] = data;
		    	}
	    		break;
	    	case 'GET':
	    		if($rootScope.server.id.indexOf(id)===-1){
	    			return;
	    		}else{
	    			return (angular.copy($rootScope.server.data[$rootScope.server.id.indexOf(id)]));
	    		}
	    		break;
    		default:
    			return;
    	};
    };
    $rootScope.connector = $scope.connector;

    ///======================
    /// 註冊監聽事件，由 TabController 廣播
    ///======================
	$scope.$on('to-TabPane', function(e, d) {
		if ($scope.$parent.tabInfo.selected) {
			console.log($scope.controllerName + " Recv " + d);
			$scope.tabSelected();
			$scope.showMsg();
		}
	});
	///======================
	/// 由交易負責改寫，主要是事件通知
	///======================
	$scope.tabSelected = function() {
		//第一次建立的時候，不會觸發
	};
	/**
	 * 權限判斷
	 * @param  {String}  sBtnAuthName AddEditDel|Export|Print|Query 其中一項
	 * @return {Boolean}          [description]
	 */
	$scope.hasAuth = function(sBtnAuthName) {
		if (sBtnAuthName.length > 0) {
			return ($scope.btnAuth.indexOf(sBtnAuthName) >= 0);
		}
		return false;
	};

	///======================
	/// DATEPICKER
	///======================
	$rootScope.toJsDate = function(str){
    	if(!str)return null;
    	var a=str.split(" ");
    	var d=a[0].split("-");
    	var t=a[1].split(":");
        return new Date(d[0],(d[1]-1),d[2],t[0],t[1],t[2]);
    };
    $rootScope.nowDate = new Date();
    $rootScope.minDate = new Date(new Date().getFullYear()-130,0,1);
    $rootScope.maxDate = new Date(new Date().getFullYear()+130,11,31);

    ///======================
	/// Tablesaw
	///======================
    $rootScope.etablesaw = function() {
    	$timeout(function () {
			$('.tablesaw-advance').remove();
			$('.tablesaw').removeData();
			$('.tablesaw').table();
        	$( document ).trigger( "enhance.tablesaw" );
        });
    }
    ///======================
	/// slim-scroll
	///======================
    $rootScope.escroll = function() {
    	$timeout(function () {
    		$('.slim-scroll').each(function () {
                var $this = $(this);
                $this.slimScroll({
                    height: $this.data('height') ||  100,
                    railVisible: true
                });
            });
    	});
    }

	///======================
	/// 訊息通知
	///======================
    //Modify: 2016/03/22 ArthurKO
    //Modify: 2016/03/31 William: 新增參數，允許訊息帶參數
    //Success
	$rootScope.showSuccessMsg = function(sMsg, params) {
		$rootScope.showMsgProcess(sMsg, 'SUCCESS', params);
	}
	//Normal Message
	$rootScope.showMsg = function(sMsg, params) {
		$rootScope.showMsgProcess(sMsg, 'NORMAL', params);
	}
    //Warning Message
	$rootScope.showWarningMsg = function(sMsg, params) {
		$rootScope.showMsgProcess(sMsg, 'WARNING', params);
	}
	//Error Message
	$rootScope.showErrorMsg = function(sMsg, params) {
		$rootScope.showMsgProcess(sMsg, 'DANGER', params);
	}
	//Error Message(dialog)
	$rootScope.showErrorMsgInDialog = function(sMsg, params){
		$rootScope.showMsgProcess(sMsg, 'DANGER', params);
//		$scope.errorMessage = 'ERROR:[' + msg + ']';
	}

	// [2016/03/31] appended by Willaim: 將參數帶入訊息中
	function sMsgHandler(sMsg, params) {
		var txtMsg = $filter('i18n')(sMsg);

//		if(typeof params !== "undefined"){
		try{
			var i = 0;
			if(txtMsg.match('field')){
				angular.forEach(params, function (){
					txtMsg = txtMsg.replace('#{field'+ (i+1) +'}#', params[i]);
					i++;
				});
			}
			else{
				angular.forEach(params, function (){
					txtMsg = txtMsg.replace('{'+ i +'}', params[i]);
					i++;
				});
			}
		}
		catch(e){
			//
		}
		return txtMsg;
	}

	//Process Message
	$rootScope.showMsgProcess = function(sMsg, status, params) {

		if (typeof sMsg !== "undefined" && sMsg !== "") {
			var oDate = new Date();
			sMsg = padLeft(oDate.getHours().toString(), 2) + ":" +
					padLeft(oDate.getMinutes().toString(), 2) + ":" +
        			padLeft(oDate.getSeconds().toString(), 2) + "-" +
        			sMsgHandler(sMsg, params);
        			//$filter('i18n')(sMsg).replace('{0}', params);
        			//$filter('i18n')(sMsg);
		}

		sMsg = sMsg || $scope.msg;
		$scope.msg = sMsg;
//		console.log(sMsg);
//		var clazz = 'msg';
//		switch(status){
//		case 'warning':
//			clazz = 'warningMsg';
//			break;
//		case 'danger':
//			clazz = 'dangerMsg';
//		break;
//		default:
//
//		}
//		$('#msgContent').removeClass().addClass(clazz);
		// if ($scope.$parent.tabInfo && $scope.$parent.tabInfo.selected) {
//		alert('baseCtrl status: ['+status+']');
		$rootScope.showStatusMsg(sMsg, status);
		// }
	};
	///======================
	/// 載入交易
	///======================
	$scope.addTabByTxncode = function(sTxnCode, oParam)
	{
		$rootScope.addTabByTxncode(sTxnCode, oParam);
	};
	$scope.addTab = function(oMenu) {
		$rootScope.addTab(oMenu);
	};
	///======================
	/// 送收電文
	///======================
	$scope.sendRecv = function(sTxnCode, sFuncType, sFuncCode, oJSON, oCallbackFunc) {
		socketService.sendRecv(sTxnCode, sFuncType, sFuncCode, oJSON)
		.then(
		function(totas) {
			for(var i = 0; i < totas.length; i++){
				var body = totas[i].body;
				switch(totas[i].header.OutputType){
					case 'Message':
						if(body.showType === 'Popup') {
							if (body.messageType === 'ForceLogOut') {
								if(!$scope.connector('get','ForceLogOut'))
									alert("超過系統閒置時間，請重新登入");
								$scope.connector('set','ForceLogOut', 'Y');
								window.location = 'Login.html'
							}
						}else if(body.showType === 'Show'){
							if(angular.isArray(body.msgData)){
								angular.forEach(body.msgData, function(row){
									$scope.showMsg($filter('i18n')(row));
								});
							}else{
								$scope.showMsg($filter('i18n')(body.msgData));
							}
						}
						if(body.messageType === 'Error'){
							console.log('error')
							oCallbackFunc(totas[i], true);
						}
						break;
					case 'Screen':
						try{
							oCallbackFunc(totas, false);
						} catch(err) {
							$scope.showMsg('ERROR:' + err);
							console.log('stack:' + err.stack);
						}
						break;
					case 'EndBracket':
						break;
					case 'NextProcess':
						if(totas[i].header.NextProc == 'downloadFile'){
							var fileUrl = body.fileUrl;
							var defaultFileName = body.defaultFileName;
							var xhr = new XMLHttpRequest();
							xhr.open('GET', fileUrl, true);
							xhr.responseType = 'blob';
							xhr.setRequestHeader('Cache-Control', 'no-cache');
							xhr.onload = function(e) {
							  if (this.status == 200) {
							    saveAs(this.response, defaultFileName);
							  }
							};
							xhr.send();
						} else if (totas[i].header.NextProc == 'viewDoc') {
							var fileUrl = body.docUrl;
							var docType = body.docType;
							$http.get(fileUrl, {responseType: 'arraybuffer'})
						       .success(function (data) {
						           var file = new Blob([data], {type: 'application/' + docType});
						           var fileURL = URL.createObjectURL(file);
						           $window.open(fileURL);
						       })
					           .error(function (data) {
					        	   $scope.showErrorMsg('GET ' + fileUrl + " : " + data.status);
						    });
						}
						break;
					default:
						break;
				}

			}

			// try {
//				if (oStatus.RetMsg) {
//					$scope.showMsg(oStatus.RetCode + " - " + oStatus.RetMsg);
//				}
			// } catch(e) { }
//			if (oCallbackFunc) {
//				oCallbackFunc(oRspToa, oStatus.RetLevel === "Error");
//			}
		},
		function(oErr) {
			//通常指通訊異常的時候
			console.log('err='+oErr);
			$scope.showErrorMsg(oErr);
		});
	};
	///======================
	/// 匯出
	///======================
	$scope.exportFile = function(sTxnCode, sFuncCode, sExportType, oJSON) {
      	if (! oJSON) {
        	oJSON = {};
      	}

		oJSON.ExportType = sExportType;
		socketService.sendRecv(sTxnCode, "Export", sFuncCode, oJSON)
		.then(
		function(oResp) {
			var oRspToa = JSON.parse(oResp.d);
			var oStatus = oRspToa.Header.Status;
			//console.log(oStatus);
			try {
				if (oStatus.RetMsg) {
					$scope.showMsg(oStatus.RetCode + " - " + oStatus.RetMsg);
				}
				if (oStatus.RetLevel !== "Error") {
					window.open("Service/ExportFile.aspx?GetFile=1", "_blank");
				}
			} catch(e) { }
		},
		function(oErr) {
			//通常指通訊異常的時候
			$scope.showMsg(oErr);
		});
	};

	$scope.parseFloat = function(val) {
		if(val)
			return parseFloat(val);
		else
			return null;
	};

	///======================
	/// 取得下拉選單的資料
	///======================
	$scope.requestComboBox = function(oJSON, oCallbackFunc, replace) {
		if(replace)
			getParameter.XML(oJSON, function(totas) {
				oCallbackFunc([{body:{result:totas.data[0]}}, {body:{result:'success'}}]);
			}, replace);
		else
			getParameter.XML(oJSON, function(totas) {
				oCallbackFunc([{body:{result:totas.data[0]}}, {body:{result:'success'}}]);
			});
	};
	///======================
	/// 將下行電文轉為 JQGrid 可辨認的格式
	///======================
	$scope.JSON2GRID = function(oJSON) {
		console.log("JSON2GRID");

		// oJSON = [["param_code", "param_name"],["test01", "01"],["test02", 02]];
		if (typeof oJSON !== "undefined") {
			var oGrids = [];
			var oRowCount = oJSON.length - 1;
			if (oRowCount > 0) {
				//Header Column
				var oHeader = oJSON[0];
				var nHeaderLen = oHeader.length;
				// console.log('header.len='+nHeaderLen)
				// console.log('grid json=' + JSON.stringify(oJSON));
				//Single Row
				for (var nPos=1; nPos <= oRowCount; nPos++) {
					// console.log(oJSON[nPos]);
					var objData = {};
					//Single Column
					for (var nCol=0; nCol < nHeaderLen; nCol++) {
						// console.log('oJSON[' + nPos + '][' + nCol + ']=' + oJSON[nPos][nCol]);
						objData[oHeader[nCol]] = oJSON[nPos][nCol];
					}
					// console.log('objData='+JSON.stringify(objData));
					oGrids.push(objData);
				}
				return oGrids;
			}
		}

		var oDataGrid = {};
		return oDataGrid;
	};
	///======================
	/// 將下行電文轉為 ComboBox 可辨認的格式
	///[{name: 'one', age: 30 },{ name: 'two', age: 27 },{ name: 'three', age: 50 }]
	///======================
	$scope.JSON2COMBO = function(oJSON) {
		console.log("JSON2COMBO");
		//console.log(oJSON);

		var oCombos = {};
		//Single Combo Attr
		for (var oCombo in oJSON) {
			oCombos[oCombo] = [];

			var nComboLen = oJSON[oCombo].length;
			if (nComboLen > 0) {
				//取對應屬性名稱 (應為 ['code_id','code_desc'])
				var oIdDesc = oJSON[oCombo][0];
				for (var nPos=1; nPos<nComboLen; nPos++) {
					var oToaComboData = oJSON[oCombo][nPos];

					var objData = {};
					objData[oIdDesc[0]] = oToaComboData[0];
					objData[oIdDesc[1]] = oToaComboData[1];
					oCombos[oCombo].push(objData);
				}
			}
		}
		return oCombos;
	};
	///======================
	/// 取得 ComboBox 內的指定值說明
	///======================
	$scope.comboFind = function(oCombo, sValue) {
		try {
			var nComboLen = oCombo.length;
			for (var nPos=0; nPos<nComboLen; nPos++) {
				var obj = oCombo[nPos];
				if (obj.code_id == sValue) {
					return obj.code_desc;
				}
			}
		} catch(e) { }
		return "";
	};
	///======================
	/// 供 DataGrid 使用
	/// 取得 ComboBox 內的指定值說明
	///======================
	$scope.dataGridComboFind = function(oCombo, sCellvalue)
	{
		try {
			if (typeof sCellvalue !== "undefined") {
		        //從 ComboBox Data 取得對應資料
		        sCellvalue = sCellvalue.trim();
		        if (sCellvalue !== "") {
		          return sCellvalue + "-" + $scope.comboFind(oCombo, sCellvalue);
		        }
	    	}
		} catch(e) { }
        return "";
	};
	///======================
	/// 字串格式化
	///======================
	$scope.hyperlinkFormat = function(sCellvalue, sLink) {
		try {
			if (typeof sCellvalue !== "undefined") {
				if (typeof sLink === "undefined") {
					sLink = "#";
				}
				sCellvalue = sCellvalue.trim();
				if (sCellvalue !== "") {
					if (sLink==="#") {
						return '<a href="' + sLink + '">' + sCellvalue + '</a>';
					} else {
						return '<a href="' + sLink + '" target="_blank">' + sCellvalue + '</a>';
					}
				}
			}
		} catch(e) { }
		return "";
	}
	$scope.dateTimeFormat = function(sCellvalue, isCYear, cData) {
		try {
			if (typeof sCellvalue !== "undefined") {
				sCellvalue = sCellvalue.trim();
				if (sCellvalue !== "") {
					return dateTimeFormat(sCellvalue, isCYear, cData);
				}
			}
		} catch(e) { }
		return "";
	};
	$scope.moneyFormat = function(sCellvalue, nFrac, IsThousandth) {
		try {
			if (typeof sCellvalue !== "undefined") {
				return S2M(sCellvalue, nFrac, IsThousandth);
			}
		} catch(e) { }
		return "";
	};
	$scope.moneyUnFormat = function(sCellvalue, nFrac, IsThousandth) {
		try {
			if (typeof sCellvalue !== "undefined" &&
				typeof sCellvalue === "string") {
				return sCellvalue.replace(/[,]/g, "");
			}
		} catch(e) { }
		return sCellvalue;
	};
	$scope.dataMaskFormat = function(sCellvalue, nFrac, IsThousandth) {
		try {
			if (typeof sCellvalue !== "undefined") {
				return dataMaskFormat(sCellvalue);
			}
		} catch(e) { }
		return "";
	};
	///======================
	/// 複製下行電文，通常使用在查詢交易
	///======================
	$scope.cloneTOA = function(oSource, oTarget) {
		for (var obj in oSource) {
			oTarget[obj] = oSource[obj];
		}
	};
	///======================
	/// 判斷是否有資料
	///======================
    $scope.dataIsEmpty = function(sMsg) {
    	sMsg = sMsg || "請至少輸入一項查詢條件";
      	for (var oAttr in $scope.tioa) {
        	if (typeof $scope.tioa[oAttr] === "string") {
           		if ($scope.tioa[oAttr] !== "") {
              		return false;
           		}
    		}
      	}
      	$scope.showMsg(sMsg);
      	return true;
    };
	///======================
	/// 清除上行電文=清除畫面欄位資料
	///======================
	$scope.cleanPane = function() {
		var nArgs = arguments.length;
		for (var nPos=0; nPos<nArgs; nPos++) {
			var oPane = arguments[nPos];
			for (var oAttr in oPane) {
				//需判斷 string 類型?
				oPane[oAttr] = "";
			}
		}
	};
	///======================
	/// 上下頁
	///======================
	$scope.nextPageQuery = function(inputVO, oCallbackFunc) {
		console.log('nextPageQuery.tita=' + JSON.stringify(inputVO));
		var currentPage = parseInt(inputVO.displayPage, 10);
		if (isNaN(currentPage)) {
		  currentPage = 0;
		}
		currentPage += 1;
		inputVO.displayPage = currentPage.toString();
		if (oCallbackFunc) {
		  oCallbackFunc();
		}
	};
	$scope.prevPageQuery = function(inputVO, oCallbackFunc) {
		var currentPage = parseInt(inputVO.displayPage, 10);
		if (isNaN(currentPage)) {
		  currentPage = 0;
		}
		if (currentPage > 0) {
		  currentPage -= 1;
		}
		inputVO.displayPage = currentPage.toString();
		if (oCallbackFunc) {
		  oCallbackFunc();
		}
	};
	///======================
	/// 離開交易
	///======================
	$scope.exitTxn = function() {
		$rootScope.removeSelectedTab();
	};
	//=======================
	//
	//=======================
	$scope.resizeDataGrid = function(nHeight) {
		var nWindowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
		$(".tab-pane .active .ui-jqgrid-bdiv").height(nWindowHeight - 160 - nHeight - 160);
	};
	$scope.showDialog = function(sMsg) {
		alert(sMsg);
		return;
		var dialog = ngDialog.open({
			template: '<div class="ngdialog-message" style="background:white;width:200px;height:200px">' + sMsg + '</div>',
			plain: true,
			showClose: false,
			closeByDocument: true,
			closeByEscape: false
		});
	}

	$scope.$on('$includeContentLoaded', function(){
	    includeContentLoaded();
	});
}
eSoafApp.controller('BaseController',
	function($rootScope, $scope, $http, projInfoService, socketService, ngDialog, $q, i18nLanguage, $filter, $window, $timeout, $interval, getParameter) {

		BaseController($rootScope, $scope, $http, projInfoService, socketService, ngDialog, $q, i18nLanguage, $filter, $window, $timeout, $interval, getParameter);
});


function includeContentLoaded() {

//    console.log('delayInit');

    aceInit();

    $('.dialogs,.comments').slimScroll({
        height: '300px'
    });

    $('#tasks').sortable();
    $('#tasks').disableSelection();
    $('#tasks input:checkbox').prop("checked", false).on('click', function () {
        if (this.checked) $(this).closest('li').addClass('selected');
        else $(this).closest('li').removeClass('selected');
    });

    $('.sparkline').each(function () {
        var $box = $(this).closest('.infobox');
        var barColor = !$box.hasClass('infobox-dark') ? $box.css('color') : '#FFF';
        $(this).sparkline('html', { tagValuesAttribute: 'data-values', type: 'bar', barColor: barColor, chartRangeMin: $(this).data('min') || 0 });
    });

    $('[data-rel="tooltip"]').tooltip();

    $('.slim-scroll').each(function () {
        var $this = $(this);
        // console.log('element='+$this);
        $this.slimScroll({
            height: $this.data('height') ||  100,
            railVisible: true
        });
    });

    $(".span4").sortable({
        connectWith: ".span4",
        handle: ".widget-header",
        cancel: ".portlet-toggle",
        placeholder: "portlet-placeholder ui-corner-all"
    });

    $(".widget-box transparent")
      .addClass("ui-widget ui-widget-content ui-helper-clearfix ui-corner-all")
      .find(".portlet-header")
      .addClass("ui-widget-header ui-corner-all")
      .prepend("<span class='ui-icon ui-icon-minusthick portlet-toggle'></span>");

};

function aceInit(){
//	console.log('ace.min.js init');
    $("#gn_Main").hide();
    $('#gn_search').hide();
    // initialize first tab
    // total_tabs++;
    // addtab(total_tabs);
    $("#gn").on("click", function () {
        showgn();
    });

    function showgn() {
        $("#work1_menu li").removeClass("active");
        $("#gn").addClass("active");
        $("#poc_Main").hide();
        $("#gn_search").hide();
        $("#gn_Main").fadeIn('slow');
    };

    $("#poc").on("click", function () {
        showpoc();
    });

    function showpoc() {
        $("#work1_menu li").removeClass("active");
        $("#poc").addClass("active");
        $("#gn_Main").hide();
         $("#gn_search").hide();
        $("#poc_Main").fadeIn('slow');

    };

    $("#search_nav").on("click", function () {
        console.log("search_nav");
        showsearch();
    });

    function showsearch() {
        $("#work1_menu li").removeClass("active");
        $("#search_nav").addClass("active");
        $("#gn_Main").hide();
        $("#poc_Main").hide();
        $("#gn_search").fadeIn('slow');
    };

    // $(".addtab, #litab").on("click", function () {
    //     total_tabs++;
    //     $("#tabcontent p").hide();
    //     var tabname = $(this).attr('id');
    //     console.log(tabname);
    //     addtab(total_tabs, tabname);
    //     return false;
    // });

	//快速開啟
        $(".business_input").on("keypress", function(e){
          code = (e.keyCode ? e.keyCode : e.which);
          if (code == 13)
          {
              var keynum=$(this).val();
              console.log(keynum);
              switch(keynum){
                case "110320":
                total_tabs++;
                addtab(total_tabs, "A110320");
                break;

                case "120606":
                total_tabs++;
                addtab(total_tabs, "A120606");
                break;
              }
          }
        });
    //<li class="active"><a data-toggle="tab" href="#task-tab">主選單</a></li>
    function addtab(count, nameID) {
        var inputValue = $("#" + nameID + "").text();
        console.log(inputValue);
        var closetab = '<span href="" id="close' + count + '" class="close" style="  position: absolute; right: 1px; padding: 0px 3px 0px 3px;text-align: right;">&times;</span> ';
        $("#recent-tab li").removeClass("active");
        $("#task-tab").removeClass("active");
        $("#recent-tab").append('<li class="active" ><a data-toggle="tab" href="#t' + count + '"> <span class="text_title_size">' + inputValue + '</span>&nbsp;&nbsp;' + closetab + '</a></li>');
        switch (inputValue) {
        case "黃金存摺餘額/部分餘額證明":
            onA120606_init(count, inputValue);
            break;
        case "A. A110000 存款轉帳收入":
            //A. A110000 存款轉帳收入
            console.log("A. A110000");
            $("#tabnav_menu").append('<div id="t' + count + '" class="tab-pane active " ><div class="widget-header"><h4>        ' + inputValue + '</h4></div><div class="widget-body"><div class="widget-main"><form class="form-horizontal"><div class="row-fluid"><div class="span6"><div class="control-group "id="ACNO_control-group"><label class="control-label" for="form-field-1">                            存款帳號</label><div class="controls" id="ACNO_control" ><span class="span6 input-icon input-icon-right" id="ACNO_control_span"><input class="input-medium input-mask-eyescript" type="text" id="ACNO_form-field-mask-1"></span><span class="help-inline" style="margin-top:5px; display:none" id="ACNO_control_span_help-inline" >存款帳號錯誤</span></div></div></div><div class="span6"><div class="control-group " id="DepAmount_control-group"><label class="control-label" for="form-field-1">                            存款額數</label><div class="controls" id="DepAmount_control"><input class="input-medium"  type="text" id="DepAmount_form-field-mask-1" ></div></div></div></div><div class="row-fluid"><div class="span6"><div class="control-group " id="Currency_control-group"><label class="control-label" for="form-field-1">                                幣別</label><div class="controls" id="Currency_control"><select id="Currency_form-field-select-1" class="input-medium"><option value=""></option><option value="AL">                                    Alabama</option><option value="AK">                                    Alaska</option><option value="AZ">                                    Arizona</option></select></div></div></div><div class="span6"><div class="control-group " id="Passbook_control-group"><label class="control-label" for="form-field-1">                                    存摺行數</label><div class="controls" id="Passbook_control"><input class="span2 limited"  type="text" id="Passbook_form-field-mask-1" ></div></div></div></div><div class="row-fluid"><div class="span6"><div class="control-group " id="Price_control-group"><label class="control-label" for="form-field-1">                            金額</label><div class="controls" id="Price_control"><input class="input-medium" type="text" id="Price_form-field-mask-1"></div></div></div><div class="span6"><div class="control-group " id="FoldNo_control-group"><label class="control-label" for="form-field-1">                                摺號</label><div class="controls" id="FoldNo_control"><input class="span3 limited"  type="text" id="FoldNo_form-field-mask-1" ></div></div></div></div><div class="row-fluid"><div class="span6"><div class="control-group " id="Remark_control-group"><label class="control-label" for="form-field-1">                                附註</label><div class="controls" id="Remark_control"><input class="input-medium" type="text" id="Remark_form-field-mask-1"></div></div></div><div class="span6"><div class="control-group " id="Date_control-group"><label class="control-label" for="form-field-1">                                起息日</label><div class="controls" id="Date_control"><input class="input-medium"  type="text" id="Date_form-field-mask-1" ></div></div></div></div><div class="row-fluid"><div class="span12"><div class="control-group " id="Question_control-group"><label class="control-label" for="form-field-1">                                關懷提問</label><div class="controls" id="Question_control"><select id="Question_form-field-select-1" class="span5 limited"><option value=""></option><option value="WA">                                    Washington</option><option value="WV">                                    West Virginia</option><option value="WI">                                    Wisconsin</option><option value="WY">                                    Wyoming</option></select></div></div></div></div><div class="control-group ">&nbsp;</div><div class="hr"></div><br><div class="row-fluid"><div class="span12"><div class="control-group " id="UserName_control-group"><label class="control-label" for="form-field-1">                                戶名</label><div class="controls" id="UserName_control"><input class="span5 limited"  type="text" id="UserName_form-field-mask-1" ></div></div></div></div><div class="form-actions"><button onclick="return false;" class="btn btn-info" id="a110000_submit" type="submit"><i class="icon-ok"></i>送出</button>                    &nbsp; &nbsp; &nbsp;<button class="btn" type="reset"><i class="icon-undo"></i>取消</button></div></form></div></div></div>');
            break;

        case "分行電子日誌查詢/列印":
            onSIEJ001_init(count, inputValue);
            break;

        case "110320+110220 整合性交易":
            onA110320_init(count, inputValue);
            break;
        }

        $("#close" + count).bind("click", function () {
            // activate the previous tab
            $("#tabnav_menu div").removeClass("active");
            $("#recent-tab li").removeClass("active");
            $("#tabcontent p").hide();
            console.log("count : "+count);
            var num=count+1;
            for (var a=0;a<count;a++)
            {
             $("#t" + a).removeClass("active");

            }

            $(this).parent().remove();
            $("#c" + count).remove();
            $("#t" + count).remove();
            $('#task-tab').addClass("active");
            $('#nav_tab_main').addClass("active");

            return false;
        });
    }
}
