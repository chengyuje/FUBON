/**================================================================================================
@program: esoaf-tool.js

@description: Web tool by custom functions, Library of platform custom methods.
@version: 1.0.20170313
=================================================================================================*/
/** @prototype */
//array
Array.prototype.contains = function(item) {
    for (var i = 0; i < this.length; i += 1) {
        if (this[i] === item) {
            return true;
        }
    }
    return false;
};
Array.prototype.distinct = function() {
    var derivedArray = [];
    for (var i = 0; i < this.length; i += 1) {
        if (!derivedArray.contains(this[i])) {
            derivedArray.push(this[i])
        }
    }
    return derivedArray;
};
//trans
Date.prototype.toJSON = function() { return this.getTime() }
//date
Date.prototype.yyyyMMdd = function(join) {
   var yyyy = this.getFullYear();
   var MM = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this.getMonth() + 1); // getMonth() is zero-based
   var dd  = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
   return "".concat(yyyy).concat(join?join:"").concat(MM).concat(join?join:"").concat(dd);
};
Date.prototype.yyyyMMddhhmm = function(join) {
   var yyyy = this.getFullYear();
   var MM = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this.getMonth() + 1); // getMonth() is zero-based
   var dd  = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
   var hh = this.getHours() < 10 ? "0" + this.getHours() : this.getHours();
   var min = this.getMinutes() < 10 ? "0" + this.getMinutes() : this.getMinutes();
   return "".concat(yyyy).concat(join?join:"").concat(MM).concat(join?join:"").concat(dd).concat(" ").concat(hh).concat(":").concat(min);
};
Date.prototype.yyyyMMddhhmmss = function(join) {
   var yyyy = this.getFullYear();
   var MM = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this.getMonth() + 1); // getMonth() is zero-based
   var dd  = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
   var hh = this.getHours() < 10 ? "0" + this.getHours() : this.getHours();
   var min = this.getMinutes() < 10 ? "0" + this.getMinutes() : this.getMinutes();
   var ss = this.getSeconds() < 10 ? "0" + this.getSeconds() : this.getSeconds();
   return "".concat(yyyy).concat(join?join:"").concat(MM).concat(join?join:"").concat(dd).concat(" ").concat(hh).concat(":").concat(min).concat(":").concat(ss);
};
Date.prototype.yyyyMMddhhmmssms = function(join) {
   var yyyy = this.getFullYear();
   var MM = this.getMonth() < 9 ? "0" + (this.getMonth() + 1) : (this.getMonth() + 1); // getMonth() is zero-based
   var dd  = this.getDate() < 10 ? "0" + this.getDate() : this.getDate();
   var hh = this.getHours() < 10 ? "0" + this.getHours() : this.getHours();
   var min = this.getMinutes() < 10 ? "0" + this.getMinutes() : this.getMinutes();
   var ss = this.getSeconds() < 10 ? "0" + this.getSeconds() : this.getSeconds();
   var ms = this.getMilliseconds() < 10 ? "0" + this.getMilliseconds() : this.getMilliseconds();
   return "".concat(yyyy).concat(join?join:"").concat(MM).concat(join?join:"").concat(dd).concat(" ").concat(hh).concat(":").concat(min).concat(":").concat(ss).concat(":").concat(ms);
};

/** @normal */
//random numbers(36)
function uuid(){
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
    return uuid;
}
//(no using)
function tog(v){return v?'addClass':'removeClass';} 
$(document).on('input', '.clearable', function(){
    $(this)[tog(this.value)]('x');
}).on('mousemove', '.x', function( e ){
    $(this)[tog(this.offsetWidth-18 < e.clientX-this.getBoundingClientRect().left)]('onX');
}).on('touchstart click', '.onX', function( ev ){
    ev.preventDefault();
    $(this).removeClass('x onX').val('').change();
});
//(no using)
function queryParameter(name) {
	  var url = location.href;
	  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	  var regexS = "[\\?&]"+name+"=([^&#]*)";
	  var regex = new RegExp( regexS );
	  var results = regex.exec( url );
	  return results == null ? null : results[1];
}
//check brower kind.
function detectBrowser() {
        var Sys = {};
        var ua = navigator.userAgent.toLowerCase();
        var s;
        (s = ua.match(/rv:([\d.]+)\) like gecko/)) ? Sys.ie = s[1]:
            (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
            (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
            (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
            (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
            (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;

        /*
        if (Sys.ie) document.write('IE: ' + Sys.ie);
        if (Sys.firefox) document.write('Firefox: ' + Sys.firefox);
        if (Sys.chrome) document.write('Chrome: ' + Sys.chrome);
        if (Sys.opera) document.write('Opera: ' + Sys.opera);
        if (Sys.safari) document.write('Safari: ' + Sys.safari);
        */
        return Sys;
}
//check brower version.
function checkBrowser() {
    var flag = false;
    var Sys = detectBrowser();
    if (Sys.ie != null && parseFloat(Sys.ie) >= 11) {
    	flag = true;
    } else if (Sys.chrome != null && parseFloat(Sys.chrome) >= 16) {
    	flag = true;
    } else if (Sys.ipad != null) {
    	flag = true;
    }
    alert(JSON.stringify(Sys));
    return flag;
}
/* 修正 IE8 不支援 console.log (no using) */
function IE8Fix() {
    if (!window.console) window.console = {
        log: function() {}
    };

    if(typeof String.prototype.trim !== 'function') {
      String.prototype.trim = function() {
        return this.replace(/^\s+|\s+$/g, ''); 
      };
    }    
}
// IE8 選相容時 的 Version 為 7 (no using)
function reSizeMe() {
        var windowWidth = $(window).width();
        var windowHeight = $(window).height();
//      console.log("size=" + windowWidth + "x" + windowHeight);

        var Sys = detectBrowser();
        if (Sys.ie) {
            //IE8 不可執行，IE8 之後要
            var nVer = parseFloat(Sys.ie);            
            if (nVer > 8) {
                $("#main-content-leo").css("top", "-50px");
            } else {
                //$("#main-content-leo").css("top", "-10px");
            }
            
            if (nVer == 7) {
                $("#header").css("height", "85px");
            }
        }
        $(".tab-content").height(windowHeight - 177);
    }
///padding string left
function padLeft(str, nlenght) {
    if (str.toString().length >= nlenght)
        return str;
    else
        return padLeft("0" + str, nlenght);
}
//get cookie (no used)
function getCookie(name)        
{
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
     if(arr != null) return unescape(arr[2]); return null;
}
/* Get Url Parameters */
function QueryString1() {
  // This function is anonymous, is executed immediately and 
  // the return value is assigned to QueryString!
  var query_string = {};
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
    var pair = vars[i].split("=");
        // If first entry with this name
    if (typeof query_string[pair[0]] === "undefined") {
      query_string[pair[0]] = pair[1];
        // If second entry with this name
    } else if (typeof query_string[pair[0]] === "string") {
      var arr = [ query_string[pair[0]], pair[1] ];
      query_string[pair[0]] = arr;
        // If third or later entry with this name
    } else {
      query_string[pair[0]].push(pair[1]);
    }
  } 
  return query_string;
} 
/* Get Url Parameters (no used) */
function QueryString(name, url) {
  if (!url) url = location.href
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( url );
  return results == null ? null : results[1];
}
/*
 * (no used)
 * 執行交易，由 SL 端呼叫
 * @param  {string} sTxnCode 交易代號
 * @return {bool}
 */
function runHTMLTxn(sTxnCode) {
    try {
        //console.log(angular.element($("#menubar-content-leo")).scope());
        angular.element($("#menubar-content-leo")).scope().runTxnBySL(sTxnCode);
        window.focus();
    } catch (e) {  
        //初次啟動，無法馬上載入交易相關資料，
        //故先暫存全域變數
        //window.initialTxnArgs = sTxnCode;
    }
    return true;
}
/*
 * 從 Silverlight 取得使用者資料
 * @return {[type]} [description]
 */
function getUserInfo() {
    try {
        var SLWin = window.opener;
        if (SLWin) {
            var oUserInfo = JSON.parse(SLWin.GetUserInfo());
//            console.log(oUserInfo);
            //這邊會影響 LazyModuleLoad
            //var oHTMLUser = angular.injector(['ng', 'eSoafApp']).get("sysInfoService");
            //oHTMLUser.RoleId = oUserInfo.RoleId;
            //oHTMLUser.UserId = oUserInfo.UserId;
            return oUserInfo;
        }
    } catch(e) { }
    return null;
}
//同步登出時間
function syncLogoutTime() {
    try {
        var SLWin = window.opener;
//        console.log("window.opener: "+SLWin);
        if (SLWin) {
            SLWin.SyncLogoutTime();
        }
    } catch (e) {}
}

function closeMe() {
    try {
        //通知 SL，重設相關變數
        var SLWin = window.opener;
        if (SLWin) {
            SLWin.ReSetHTMLWin();
        }
    } catch (e) {}
    window.self.close();
}
function logout() {
    try {
        window.open("", "_self");
    } catch(e) { }
}
function notifySL() {
    try {
        var SLWin = window.opener;
        if (SLWin) {
            SLWin.SwitchWin();
            return true;
        }
    } catch (e) {}
    return false;
}
///======================
/// 回傳系統日 yyyyMMdd
///======================
function getToday() {
    var oDate = new Date();
    var sYear = oDate.getFullYear();
    var sMonth = padLeft((oDate.getMonth() + 1).toString(), 2);
    var sDate = padLeft(oDate.getDate().toString(), 2);
    var strToday = sYear + sMonth + sDate;
    // console.log("Today=" + strToday);
    return strToday;
}
///======================
/// 格式化日期
/// input 七-八位數字
/// isCYear 呈現民國年 或 西元年
/// 格式字元
///======================
function dateTimeFormat(input, isCYear, cData) {
    if (input == null) return "";
    input = input.trim();
    if (input.length >= 6 && input.length <= 8) {
        isCYear = (typeof isCYear !== "undefined") ? isCYear : false;
        cData = (typeof cData !== "undefined") ? cData : "/";

        var oMatch;
        var oRegExp = new RegExp("([0-9]{2,4})([0-9]{2})([0-9]{2})", "g");
        if ((oMatch = oRegExp.exec(input)) != null) {
            var cYear = oMatch[1]; //西元年
            if (isCYear) {
                cYear = padLeft((parseInt(cYear, 10) - 1911).toString(), 3);
            }
            return input.replace(oRegExp, cYear + cData + "$2" + cData + "$3");
        }
    }
    return input;
}

function S2M(strData, nFrac, IsThousandth) {
    nFrac = (typeof nFrac !== "undefined") ? nFrac : 0;
    IsThousandth = (typeof IsThousandth !== "undefined") ? IsThousandth : true;
    if (typeof strData === "undefined") return "";

    //去除多餘空白
    strData = strData.toString().trim();
    if (strData != "") {
        //先區分正負號、整數、小位數
        var oMatch;
        var oRegExp1 = new RegExp("^([+-]?)([0-9]*)(\\.?)([0-9]*)$", "g");
        if ((oMatch = oRegExp1.exec(strData)) != null) {
            var sSignMark = oMatch[1]; //正負號
            var sNum = oMatch[2]; //整數
            sNum = sNum.replace(/(^0*)/g, "");
            if (sNum == "") sNum = "0";
            var sNewValue = sNum; //

            if (IsThousandth) {
                //要撇節
                var oRegExp2 = new RegExp("([0-9]{1,3})(?=([0-9]{3})+(?:$|\\.))", "g");
                sNewValue = sNum.replace(oRegExp2, "$1,");
            }
            //組資料
            if (nFrac > oMatch[4].length) {
                strData = sSignMark + sNewValue + "." + padRight(oMatch[4].toString(), "0", nFrac);
            } else if (nFrac == 0) {
                strData = sSignMark + sNewValue;
            } else {
                strData = sSignMark + sNewValue + oMatch[3] + oMatch[4];
            }
        }
    }
    return strData;
}

function dataMaskFormat(inputValue) {
    var transformedInput = inputValue;
    var sDataLen = inputValue.length;
    if (sDataLen > 3) {
        //地址
        transformedInput = inputValue.substring(0, 3) + "ＯＯＯＯＯ" + inputValue.substring(sDataLen-1, sDataLen);
    } else {
        if (sDataLen > 1) {
            //姓名
            transformedInput = inputValue.substring(0, 1) + "Ｏ" + inputValue.substring(sDataLen-1, sDataLen);
        }
    }
    return transformedInput;
}

function beep() {
    try {
        var snd = new Audio("data:audio/wav;base64,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qJrx4KOQjahZxWbcZgztj2c49nKmkId44S71j0c8eV9yDK6uPRzx5X18eDvjvQ6yKo9ZSS6l//8elePK/Lf//IInrOF/FvDoADYAGBMGb7FtErm5MXMlmPAJQVgWta7Zx2go+8xJ0UiCb8LHHdftWyLJE0QIAIsI+UbXu67dZMjmgDGCGl1H+vpF4NSDckSIkk7Vd+sxEhBQMRU8j/12UIRhzSaUdQ+rQU5kGeFxm+hb1oh6pWWmv3uvmReDl0UnvtapVaIzo1jZbf/pD6ElLqSX+rUmOQNpJFa/r+sa4e/pBlAABoAAAAA3CUgShLdGIxsY7AUABPRrgCABdDuQ5GC7DqPQCgbbJUAoRSUj+NIEig0YfyWUho1VBBBA//uQZB4ABZx5zfMakeAAAAmwAAAAF5F3P0w9GtAAACfAAAAAwLhMDmAYWMgVEG1U0FIGCBgXBXAtfMH10000EEEEEECUBYln03TTTdNBDZopopYvrTTdNa325mImNg3TTPV9q3pmY0xoO6bv3r00y+IDGid/9aaaZTGMuj9mpu9Mpio1dXrr5HERTZSmqU36A3CumzN/9Robv/Xx4v9ijkSRSNLQhAWumap82WRSBUqXStV/YcS+XVLnSS+WLDroqArFkMEsAS+eWmrUzrO0oEmE40RlMZ5+ODIkAyKAGUwZ3mVKmcamcJnMW26MRPgUw6j+LkhyHGVGYjSUUKNpuJUQoOIAyDvEyG8S5yfK6dhZc0Tx1KI/gviKL6qvvFs1+bWtaz58uUNnryq6kt5RzOCkPWlVqVX2a/EEBUdU1KrXLf40GoiiFXK///qpoiDXrOgqDR38JB0bw7SoL+ZB9o1RCkQjQ2CBYZKd/+VJxZRRZlqSkKiws0WFxUyCwsKiMy7hUVFhIaCrNQsKkTIsLivwKKigsj8XYlwt/WKi2N4d//uQRCSAAjURNIHpMZBGYiaQPSYyAAABLAAAAAAAACWAAAAApUF/Mg+0aohSIRobBAsMlO//Kk4soosy1JSFRYWaLC4qZBYWFRGZdwqKiwkNBVmoWFSJkWFxX4FFRQWR+LsS4W/rFRb/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////VEFHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAU291bmRib3kuZGUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMjAwNGh0dHA6Ly93d3cuc291bmRib3kuZGUAAAAAAAAAACU=");
        snd.play();
    } catch (e) {}
}

//Function: get String Left by Number
//Add: 2016/06/22 ArthurKO
function left(str, num){
	if(!str)return;
	if(str.toString().trim().length <= num){
		return str;
	}else{
		return str.toString().trim().substring(0,num);
	}
}

//Function: get String Right by Number
//Add: 2016/06/22 ArthurKO
function right(str, num){
	if(!str)return;
	if(str.toString().length <= num){
		return str;
	}else{
		return str.toString().trim().substring(str.toString().trim().length-num, str.toString().trim().length);;
	}
}

//Function: counts input attrs
//Add: 2016/09/12 ArthurKO
function len($this){
	if(!$this)return false;
//	alert('Object.prototype.length: '+JSON.stringify(this));
	switch(Object.prototype.toString.call($this)){
		case '[object String]': return $this.length;
		case '[object Number]': return $this.length;
		case '[object Object]': return Object.keys($this).length;
		case '[object Array]': return $this.length;
		case '[object Date]': return 1;
		case '[object Null]': return false;
		default: return false;
	}
}

//Function: typing input attrs
//Add: 2016/09/13 ArthurKO
function type($this){
	if(!$this)return false;
//	alert(Object.prototype.toString.call($this));
//	alert('Object.prototype.length: '+JSON.stringify(this));
	switch(Object.prototype.toString.call($this)){
		case '[object String]': return 'string';
		case '[object Number]': return 'number';
		case '[object Object]': return 'object';
		case '[object Array]': return 'array';
		case '[object Date]': return 'date';
		case '[object Null]': return false;
		default: return false;
	}
}

//Function: typing input string is chinese or not
//Add: 2016/09/19 ArthurKO
function isChinese(str, type){
	if(!str)return;
	var reg;
	type ? reg=/^[\u4E00-\u9FA5]+$/ : reg=/[\u4E00-\u9FA5]|[\uFE30-\uFFA0]/gi;
	if(!reg.exec(str.toString())){
		return false;
	}else{
		return true;
	}
}

//Function: Platform setting style for loaction
//Add: 2016/12/08 ArthurKO
function setLocation($rootScope){
	if($rootScope.system.platform.type.indexOf('location')!==-1){
//		console.warn('location: ['+$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('location')]+']');
		switch($rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('location')]){
			case "localhost":
			case "127.0.0.1":
			case "www.fubonbank.com":
			default:
				break;
		}
	}
}

/** 
	  @class: main
	   @name: $esoaf
	@members: $touch, $watcher, $fn
/*/
var $esoaf = {};
$esoaf.$touch = false;
$esoaf.$watcher = {};
$esoaf.$fn = {};
$esoaf.$pressure = {};
// Function: Platform setting style for loaction
//  $xxx = object.prototype
//  _xxx = function
//      Add: 2016/12/16 ArthurKO
$esoaf._close = function($rootScope, socketService, $confirm, type) {
	//setting variable
	var $str = {$1:'確定登出財富管理系統？', $2:'閒置超時正在登出財富管理系統。'},
		$URL = $rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].URL,
		$locat = $rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('location')],
		$location;
	//switch location
	switch($locat){
		default:
			$location = $URL.$test;
			break;
	}
	switch(type) {
		case true: 
			// kind: logout by click & client-timeout.
			if($rootScope.system.platform.type.indexOf('logout')>-1){
				$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].status=true;
				$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].classic=true;
			}
			$confirm({text:$str.$1}).then(function(){ 
				// vote: yes.
		    	socketService.sendRecv("CMFPG000","tlroff","com.systex.jbranch.app.server.fps.cmfpg000.LoginPageVO",{}).then(		
					function(oResp){
						//conductive page
						window.location = angular.copy($location);
						//close page
						closeMe();
					}
				);
			},function(){ 
				// vote: no.
				if($rootScope.system.platform.type.indexOf('logout')>-1){
					$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].status=false;
					$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].classic=false;
				}
			});
			break;
		default:
			// sendback exception.
			if($rootScope.system.platform.type.indexOf('logout')>-1){
				$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].status=false;
				$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].classic=true;
			}
			$confirm({text:$str.$2}).then().then(function(){
				if($rootScope.system.platform.type.indexOf('logout')>-1){
					$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].status=true;
					$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].classic=true;
				}
				//conductive page
				window.location = angular.copy($location);
				//close page
				closeMe();
			}).then(function(){
				if($rootScope.system.platform.type.indexOf('logout')>-1)
					$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('logout')].classic=false;
			});
			break;
	}
}
//check app kind
if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)){
	$esoaf.$touch=true;
}else{
	$esoaf.$touch=false;
}
//checking window size always
$esoaf.$watcher.sizing = function(){
	var current;
	$(window).on("resize", function(){
		//dialog
		$('.modal-dialog').css({
			"max-width":($('html:first').width()-1), 
			"max-height":($('html:first').height()-1)
		});
		//rotate mobile
//		window.addEventListener("orientationchange",function(){
//			if(window.orientation===current)return;
//			current = window.orientation;
//            if(window.orientation===90){
//            	//horizontal
//            	current = window.orientation;
//            	$(".e-container").find(".container-home-box").each(function(i){
//            		$(this).width("40%");
//            	});
//            }else{
//            	//vertical
//            	current = window.orientation;
//            	$(".e-container").find(".container-home-box").each(function(i){
//            		$(this).width("80%");
//            	});
//            }
//		});
	});
}();

/** 
		  @class: sub
		   @name: $esoaf.$fn
	@description: all of public methods.
		@members: genterateCSV
**/
$esoaf.$fn.genterateCSV = function($data, $title, $filename) {
	// @description: export file from front-end method.
	// @parameters:     $data = [{A:'XXX',B:'XXX',...},{},{}...]
	//				   $title = [{LABEL:'XXX', DATA:'XXX'},{},{}...]
	//				$filename = 'XXX'
	
	/** check navigator **/
	/* Saves a text string as a blob file */  
    var ie = navigator.userAgent.match(/MSIE\s([\d.]+)/),
        ie11 = navigator.userAgent.match(/Trident\/7.0/) && navigator.userAgent.match(/rv:11/),
        ieEDGE = navigator.userAgent.match(/Edge/g),
        ieVer=(ie ? ie[1] : (ie11 ? 11 : (ieEDGE ? 12 : -1)));

    if (ie && ieVer<10) {
      console.log("No blobs on IE ver<10");
      return;
    }
    
    /* setting sheet */
    var str = '', row = [];
    //title
    for (var t = 0; t < $title.length; t++) {
    	row.push('"' + $title[t]["LABEL"] + '"');
    }
    str += row.join(',');
    str += '\r\n';
    //content
    for (var i = 0; i < $data.length; i++) {
    	row = [];
        for (var obj in $data[i]) {
        	if($data[i].hasOwnProperty(obj)){
        		if(obj.toString()==="$$hashKey")break;
        		switch(type($data[i][obj])){
	        		case 'date':
	        			row.push('"' + $data[i][obj].yyyyMMddhhmmssms("/") + '"');
	        			break;
        			default:
        				row.push('"' + $data[i][obj] + '"');
        		}
        	}
        }
        str += row.join(',');
        str += '\r\n';
    }
    
    /* Generate File */
    //variables
    var fileName = (!$filename?"esoaf":$filename) + "_" + new Date().yyyyMMdd("-") + ".csv";
        var downloadLink = document.createElement("a");
        var blob = new Blob(["\ufeff", str], { type: 'text/csv,charset=UTF-8' });
         var url = URL.createObjectURL(blob);
    //download     
    if (ieVer>-1) {
      window.navigator.msSaveBlob(blob, fileName);
    } else {
      var downloadLink = document.createElement("a");
      downloadLink.download = fileName;
      downloadLink.href = window.URL.createObjectURL(blob);
      downloadLink.onclick = function(e) { document.body.removeChild(e.target); };
      downloadLink.style.display = "none";
//      downloadLink.style = "visibility:hidden"; //only read
      document.body.appendChild(downloadLink);
      downloadLink.click();
    }

}

/** 
		  @class: sub
		   @name: $esoaf.$pressure
	@description: pressure-test.
		@members: data, clear, set, get
**/
$esoaf.$pressure.data = {
	type:[],
	  ID:[],
   value:[]
};
/*
		  @name: $esoaf.$pressure.clear
	@parameters:
			     $type = (string) file name
			       _ID = (string || object) index like uuid || element
	   @example: 
			     1. var list = $esoaf.$pressure.clear(); 						//clear all data.
			     2. var list = $esoaf.$pressure.clear("eCombobox.js");			//clear data by type of "eCombobox.js".
			     3. var list = $esoaf.$pressure.clear("eCombobox.js", element);	//clear data by type of "eCombobox.js" and ID with element.
*/
$esoaf.$pressure.clear = function($type,$ID){
	if(angular.isUndefined($type)){
		if(angular.isUndefined($ID)){
			$esoaf.$pressure.data = {
				type:[],
				  ID:[],
			   value:[]
			};
		}else{
			var idx = $esoaf.$pressure.data.ID.indexOf($ID);
			if(idx>-1){
				$esoaf.$pressure.data.type[idx] = undefined;
				$esoaf.$pressure.data.ID[idx] = undefined;
				$esoaf.$pressure.data.value[idx] = undefined;
			}
		}
	}else{
		if(angular.isUndefined($ID)){
			for(var i=0; i<$esoaf.$pressure.data.type.length; i++){
				if($esoaf.$pressure.data.type[i]===$type){
					$esoaf.$pressure.data.type[i] = undefined;
					$esoaf.$pressure.data.ID[i] = undefined;
					$esoaf.$pressure.data.value[i] = undefined;
				}
			}
		}else{
			var idx = $esoaf.$pressure.data.ID.indexOf($ID);
			if(idx>-1){
				$esoaf.$pressure.data.type[idx] = undefined;
				$esoaf.$pressure.data.value[idx] = undefined;
				$esoaf.$pressure.data.ID[idx] = undefined;
			}
		}
	}
}
/*
		  @name: $esoaf.$pressure.set
	@parameters:
 				   $type = (string) file name
				     _ID = (string || object) index like uuid || element
				   $step = (undefined || string) loading || local action
				 $status = (boolean) success or fail
	   @example: 
	   			 1.  [by UUID index]
		   		 		 //START COUNTS
		   		 		 var $sign = uuid();
		   		 		 $esoaf.$pressure.set('getParameterService.js', $sign, 'CUSTOM');
		   		 		 //END COUNTS
		   		 		 $esoaf.$pressure.set('getParameterService.js', $sign, 'CUSTOM', true);
	   			 2.  [by element index]
		   				 //START COUNTS
		   				 $esoaf.$pressure.set('getParameterService.js', element);
		   				 //END COUNTS
		   				 $esoaf.$pressure.set('getParameterService.js', element, undefined, true);
*/
$esoaf.$pressure.set = function($type, _ID, $step, $status) {
	if(!$type || !_ID)return;
	var $ID = undefined;
	if(_ID){
		if(type(_ID)==='string')$ID=angular.copy(_ID);
		if(type(_ID)==='object')Object.keys(_ID["0"]).forEach(function(key){
			if(key.indexOf("jQuery")>-1)$ID=_ID["0"][key];
		});
	}
	if($esoaf.$pressure.data.type.indexOf($type)>-1){
		if($esoaf.$pressure.data.ID.indexOf($ID)>-1){
			if(angular.isUndefined($esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].start)){
				$esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].start = new Date();
				$esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].status = false;
			}else{
				if(angular.isUndefined($esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].end)){
					$esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].end = new Date();
					$esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].differ = $esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].end.getTime() - $esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].start.getTime();
					if($status){
						$esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].status = true;
					}else{
						$esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)].status = false;
					};
				}
			}			
		}else{
			$esoaf.$pressure.data.type.push($type);
			$esoaf.$pressure.data.ID.push($ID);
			$esoaf.$pressure.data.value.push({
				 type:$type,
			     step:$step?$step:'loading',
				start:new Date(),
				  end:undefined,
			   differ:undefined,
			   status:false
			});
		}
	}else{
		$esoaf.$pressure.data.type.push($type);
		$esoaf.$pressure.data.ID.push($ID);
		$esoaf.$pressure.data.value.push({
			 type:$type,
		     step:$step?$step:'loading',
			start:new Date(),
			  end:undefined,
		   differ:undefined,
		   status:false
		});
	}
}
/*
		  @name: $esoaf.$pressure.get
	@parameters:
				 $type = (string) file name
				   _ID = (string || object) index like uuid || element
	   @example: 
				 1. var list = $esoaf.$pressure.get(); 							//get all data.
				 2. var list = $esoaf.$pressure.get("eCombobox.js");			//get data by type of "eCombobox.js".
				 3. var list = $esoaf.$pressure.get("eCombobox.js", element);	//get data by type of "eCombobox.js" and ID with element.
*/
$esoaf.$pressure.get = function($type,_ID) {
	var $ID = undefined;
	if(_ID){
		if(type(_ID)==='string')$ID=angular.copy(_ID);
		if(type(_ID)==='object')Object.keys(_ID["0"]).forEach(function(key){
			if(key.indexOf("jQuery")>-1)$ID=_ID["0"][key];
		});
	}
	if(angular.isUndefined($type)){
		if(angular.isUndefined($ID)){
			return angular.copy($esoaf.$pressure.data.value);
		}else{			
			return angular.copy($esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)]);
		}
	}else{
		if(angular.isUndefined($ID)){
			if(angular.isUndefined($type)){
				return angular.copy($esoaf.$pressure.data.value);
			}else{
				$data = [];
				for(var i=0; i<$esoaf.$pressure.data.type.length; i++){
					if($esoaf.$pressure.data.type[i]===$type){
						$data.push(angular.copy($esoaf.$pressure.data.value[i]));
					}
				}
				return $data;
			}
		}else{
			return angular.copy($esoaf.$pressure.data.value[$esoaf.$pressure.data.ID.indexOf($ID)]);
		}
	}
}