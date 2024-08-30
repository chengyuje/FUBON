/**================================================================================================
@program: eSmartTag.js
@description: 
@version: 1.0.20170313
=================================================================================================*/
//============================
// 欄位編審、格式化
//============================
//
// DataFormat: 輸入限制 (DF)
//      $-金額字元 (0-9、小數點、正負號)
//      N-純數     (0-9)
//      E-[0-9]以外的字元
//      C-[0-9][a-zA-Z]跟中文
//      X-無中文
//      Z-任意字元
//      K-全型字元(銀行才使用)
// 
// EditMask: 輸出格式(EM)
//      A1:
//      A2:
//      A3:
//      A4:
//
// EditType: 補值 (ET)
//      L0
//      R0
//      LS
//      RS
//
// DataExt: 轉型 (DE)
//      F 全型
//      U 大寫
//      L 小寫
//      N
//
// RegularExpress: (RE)
//
//====================================================
// 使用流程 
// onfocus
//
//
//
eSoafApp.directive('eSmartTag',['$filter', function($filter) {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {},
        link: function (scope, elem, attrs, ctrl) {
            //var curr = $filter('currency');
            
            if (!ctrl) return;  
            ///======================
            /// 屬性定義 (＊在 HTML 的定義，到此會變成小寫＊)
            ///======================
            console.log("Link");
            //scope = originalScope.$new();
            scope.Frac = parseInt(attrs.datafrac || "0", 10);
            ///======================
            ///
            ///======================            
            scope.DataFormat = attrs.dataformat || "Z";
            scope.EditMask = attrs.editmask || scope.DataFormat;
            if (scope.EditMask === "D1" ||
                scope.EditMask === "D2" ||
                scope.EditMask === "C1" ||
                scope.EditMask === "C2") {
                scope.DataFormat = "N";     //限數字
            } else if (scope.EditMask === "N1" ||
                scope.EditMask === "N2" ||
                scope.EditMask === "N3" ||
                scope.EditMask === "N4") {
                scope.DataFormat = "$";     //限金額相關字元
            }
            ///======================
            ///
            ///======================
            scope.EditType = attrs.edittype || "N"; //補值方式
            scope.FillType = "";
            if ((scope.EditType !== "N") && (scope.EditType.length >= 2)) {
                scope.FillType = scope.EditType.substr(1, 1);
                scope.EditType = scope.EditType.substr(0, 1).toUpperCase();
            }
            scope.DataExt = attrs.dataext || "N";   //資料轉換
            ///======================
            /// 欄位初始化
            ///======================
            function initialField(objField) {
                if (scope.EditMask === "A1") {
                    scope.DataExt = "U";
                }
                if (scope.DataExt === "U") {
                    objField.style.textTransform = "uppercase";
                } else if (scope.DataExt === "L") {
                    objField.style.textTransform = "lowercase";
                }
            }
            ///======================
            /// 填值
            ///======================
            function fillField(sValue, nMaxLength) {
                //console.log("fillField");
                if ((nMaxLength > 0) && (scope.FillType !== "")) {
                    if (scope.EditType === "L") {
                        return padLeft(sValue, scope.FillType, nMaxLength);
                    } else if (scope.EditType === "R") {
                        return padRight(sValue, scope.FillType, nMaxLength);
                    }
                }
                return sValue;
            }
            ///======================
            /// 右補滿
            ///======================
            function padRight(sValue, cData, nLen) {
                if (sValue.length < nLen) {
                    var oArray = [];
                    oArray.push(sValue);
                    var nCount = nLen - sValue.length;
                    for(var nPos=0; nPos<nCount; nPos++) {
                        oArray.push(cData);
                    }
                    return oArray.join("");
                }
                return sValue;
            }
            ///======================
            /// 左補滿
            ///======================
            function padLeft(sValue, cData, nLen) {
                if (sValue.length < nLen) {
                    var oArray = new Array();
                    var nCount = nLen - sValue.length;
                    for(var nPos=0; nPos<nCount; nPos++) {
                        oArray.push(cData);
                    }
                    oArray.push(sValue);
                    return oArray.join("");
                }
                return sValue;
            }
            ///======================
            /// 回傳系統日 yyyyMMdd
            ///======================
            //function getToday() {
            //    var oDate = new Date();
            //    var sYear = oDate.getFullYear();
            //    var sMonth = padLeft((oDate.getMonth() + 1).toString(), "0", 2);
            //    var sDate = padLeft(oDate.getDate().toString(), "0", 2);
            //    var strToday = sYear + sMonth + sDate;
            //    console.log("Today=" + strToday);
            //    return strToday;
            //}
            ///======================
            /// 補滿完整日期
            ///======================
            function getFullDate(strDate) {
                console.log(strDate);
                var strToday = getToday();
                return strToday.substr(0, strToday.length - strDate.length) + strDate;                
            }
            ///======================
            /// 判斷是否為正確的西元年日期
            ///======================
            function isFullDate(strDate) {
                var oMatch;
                var oRegExp = new RegExp("[^0-9]","g");
                strDate = strDate.replace(oRegExp, "");
                if (strDate.length === 8) {
                    oRegExp = new RegExp("([0-9]{2,4})([0-9]{2})([0-9]{2})","g");
                    if ((oMatch = oRegExp.exec(strDate)) != null) {
                        var nYear = parseInt(oMatch[1]);  //年
                        var nMonth = parseInt(oMatch[2]); //月
                        var nDay = parseInt(oMatch[3]);   //日

                        try {
                            //原先要利用 JS 本身的 Date()
                            //判斷日期的正確性
                            //結果卻發現，日期設定時，不會馬上生效
                            //而造成後續判斷異常
                            //如 0201、0331 等日期格式
                            var oDate = new Date(nYear, nMonth - 1, nDay);
                            if ((oDate.getFullYear() == nYear) &&
                                (oDate.getMonth() == nMonth - 1) && 
                                (oDate.getDate() == nDay)) {
                                return true;
                            }
                        } catch(e) { }                        
                    }
                }
                return false;
            }
            ///======================
            /// 字串轉格式化西元年日期
            ///======================
            function S2D(sValue, cData) {
                cData = (typeof cData !== "undefined") ? cData : "/";
                var oRegExp = new RegExp("([0-9]{2,4})([0-9]{2})([0-9]{2})","g");
                var dValue = sValue.replace(oRegExp, "$1" + cData + "$2" + cData + "$3");
                return dValue;
            }
            ///======================
            /// 字串轉格式化民國年日期 (只秀七位)
            ///======================
            function S2C(sValue, cData) {
                var oMatch;
                cData = (typeof cData !== "undefined") ? cData : "/";
                var oRegExp = new RegExp("([0-9]{2,4})([0-9]{2})([0-9]{2})","g");
                if ((oMatch = oRegExp.exec(sValue)) != null) {
                    var cYear = oMatch[1];  //西元年
                    cYear = padLeft((parseInt(cYear, 10) - 1911).toString(), "0", 3);
                    return sValue.replace(oRegExp, cYear + cData + "$2" + cData + "$3");
                } else {
                    return sValue.replace(oRegExp, "$1" + cData + "$2" + cData + "$3");    
                }
            }
            ///======================
            /// 西元年與民國年互換 (無格式化)
            ///======================
            function trans2CYear(sValue, IsCYear) {
                var oMatch;
                if (scope.EditMask === "C1" ||
                    scope.EditMask === "C2") {

                    var oRegExp = new RegExp("([0-9]{2,4})([0-9]{2})([0-9]{2})","g");
                    if ((oMatch = oRegExp.exec(sValue)) != null) {
                        var nYear = parseInt(oMatch[1]);  //年
                        console.log("trans2CYear=" + nYear.toString());

                        if (IsCYear) {
                            ///轉民國年
                            nYear -= 1911;
                            return sValue.replace(oRegExp, padLeft(nYear.toString(), "0", 3) + "$2" + "$3");
                        } else {                            
                            ///轉西元年
                            nYear += 1911;
                            return sValue.replace(oRegExp, nYear.toString() + "$2" + "$3");
                        }
                    }
                }
                return sValue;
            }
            ///======================
            /// 字串轉撇節化金額
            ///======================
            function S2M(strData, nFrac, IsThousandth) {
                //去除多餘空白
                strData = strData.trim();
                if (strData != "") {                    
                    //先區分正負號、整數、小位數
                    var oMatch;
                    var oRegExp1 = new RegExp("^([+-]?)([0-9]*)(\\.?)([0-9]*)$", "g");
                    if ((oMatch = oRegExp1.exec(strData)) != null) {                        
                        var sSignMark = oMatch[1];  //正負號
                        var sNum = oMatch[2];       //整數
                        sNum = sNum.replace(/(^0*)/g, "");                        
                        if (sNum=="") sNum = "0";
                        var sNewValue = sNum;       //

                        if (IsThousandth) {
                            //要撇節
                            var oRegExp2 = new RegExp("([0-9]{1,3})(?=([0-9]{3})+(?:$|\\.))","g");
                            sNewValue = sNum.replace(oRegExp2, "$1,");
                        }
                        //組資料
                        if (nFrac > oMatch[4].length) {
                            strData = sSignMark + sNewValue + "." + padRight(oMatch[4].toString(), "0" ,nFrac);
                        } else if (nFrac == 0) {
                            strData = sSignMark + sNewValue;                            
                        } else {
                            strData = sSignMark + sNewValue + oMatch[3] + oMatch[4];
                        }                        
                    }
                }
                return strData;                 
            }
            ///======================
            /// 判斷是否為有效金額格式
            ///======================
            function isCurrency(strData, nFrac) {
                if (nFrac > 0) {
                    //var sNumLen=nMax-nFrac;
                    var oMatch;
                    var oRegExp = new RegExp("^([+-]{0,1}[0-9]*)\\.?[0-9]{0," + nFrac.toString() + "}$");
                    if ((oMatch = oRegExp.exec(strData)) != null) {
                        //2011.08.05 增加整數位數長度判斷
                        //var sNum = oMatch[1];
                        //if (sNum.length <= sNumLen) {
                            return true;
                        //}
                    }
                    return false;
                } else {
                    var oRegExp = new RegExp("^[+-]{0,1}[0-9]*$");
                    return oRegExp.test(strData);       
                }                
            }
            ///======================
            /// 驗證資料正確性
            ///======================
            function checkValidation(obj) {
                //資料轉換
                if (scope.DataExt === "U") {
                    obj.value = obj.value.toUpperCase();
                } else if (scope.DataExt === "L") {
                    obj.value = obj.value.toLowerCase();
                }

                //日期
                if (scope.EditMask === "D1" ||
                    scope.EditMask === "D2" ||
                    scope.EditMask === "C1" ||
                    scope.EditMask === "C2") {
                    if (obj.value.length > 0) {
                        //先補滿八位數，再驗證
                        obj.value = getFullDate(obj.value);
                        return isFullDate(obj.value);
                    }
                }
                //金額
                if (scope.EditMask === "N1" ||
                    scope.EditMask === "N2" ||
                    scope.EditMask === "N3" ||
                    scope.EditMask === "N4") {
                    if (isCurrency(obj.value, scope.Frac)) {
                        //調整為正確的金額格式，Ex 將多餘的前置零去除
                        obj.value = S2M(obj.value, scope.Frac, false);
                        return true;
                    }
                    return false;
                }
                //身分證字號
                if (scope.EditMask === "A1") {
                    if (obj.value.length > 0) {                        
                        var oRegExp = new RegExp("[A-Z][12][0-9]{8}","g");
                        return oRegExp.test(obj.value);
                    }
                }
                //統一編號
                if (scope.EditMask === "A2") {
                    if (obj.value.length > 0) {                        
                        var oRegExp = new RegExp("[0-9]{8}","g");
                        return oRegExp.test(obj.value);
                    }
                }
                return true;
            }
            ///======================
            /// 統一格式化入口
            ///======================
            function S2Format(sModelValue) {
                if (scope.EditMask === "D1" ||
                    scope.EditMask === "D2") {
                    //西元年
                    return S2D(sModelValue);
                } else if (scope.EditMask === "C1" ||
                    scope.EditMask === "C2") {
                    //民國年
                    return S2C(sModelValue);
                } else if (scope.EditMask === "N1" ||
                    scope.EditMask === "N2" ||
                    scope.EditMask === "N3" ||
                    scope.EditMask === "N4") {
                    //轉金額
                    var IsThousandth = (scope.EditMask === "N2" || scope.EditMask === "N4");
                    return S2M(sModelValue, scope.Frac, IsThousandth);
                }
                return sModelValue;
            }
            ///======================
            ///
            ///======================
            function isControlKey(nKeyCode) {
                return (nKeyCode === 8 ||
                        nKeyCode === 46 ||
                        nKeyCode === 35 ||
                        nKeyCode === 36 ||
                        nKeyCode === 37 ||
                        nKeyCode === 38 ||
                        nKeyCode === 39 ||
                        nKeyCode === 40);
            }
            ///======================
            ///
            ///======================
            function toASCII(nKeyCode, isShiftKey) {
                var _to_ascii = {
                    '188': '44',
                    '109': '45',
                    '190': '46',
                    '191': '47',
                    '192': '96',
                    '220': '92',
                    '222': '39',
                    '221': '93',
                    '219': '91',
                    '173': '45',
                    '187': '61', //IE Key codes
                    '186': '59', //IE Key codes
                    '189': '45'  //IE Key codes
                };
                var shiftUps = {
                    "96": "~",
                    "49": "!",
                    "50": "@",
                    "51": "#",
                    "52": "$",
                    "53": "%",
                    "54": "^",
                    "55": "&",
                    "56": "*",
                    "57": "(",
                    "48": ")",
                    "45": "_",
                    "61": "+",
                    "91": "{",
                    "93": "}",
                    "92": "|",
                    "59": ":",
                    "39": "\"",
                    "44": "<",
                    "46": ">",
                    "47": "?"
                };
                var sKeyCode = nKeyCode.toString();
                if (_to_ascii.hasOwnProperty(sKeyCode)) {
                    sKeyCode = _to_ascii[sKeyCode];
                    nKeyCode = parseInt(sKeyCode, 10);
                }

                if (!isShiftKey && (nKeyCode >= 65 && nKeyCode <= 90)) {
                    sKeyCode = String.fromCharCode(nKeyCode + 32);
                } else if (isShiftKey && shiftUps.hasOwnProperty(sKeyCode)) {
                    //get shifted keyCode value
                    sKeyCode = shiftUps[sKeyCode];
                } else {
                    sKeyCode = String.fromCharCode(nKeyCode);
                }
                return sKeyCode;
            }
            ///======================
            /// Initial
            /// 負責呈現原值
            ///======================
            elem.bind('focus', function() {
                initialField(this);
                this.value = trans2CYear(ctrl.$viewValue, true);
            });
            ///======================
            /// 負責去除不合理輸入，不驗證資料正確性
            ///======================
            elem.bind('keydown', function() {
                var isShiftKey = event.shiftKey;
                var nKeyCode = event.keyCode || event.which;                
                var isPassCase = (nKeyCode === 9 && isShiftKey);   //上移
                
                if (isPassCase || isControlKey(nKeyCode)) return;

                var sCharCode = toASCII(nKeyCode, isShiftKey);
                console.log("keydown=" + sCharCode)
                if (scope.DataFormat === "N") {
                    //純數
                    var oRegExp = new RegExp("[0-9]");
                    if (! oRegExp.test(sCharCode)) {
                        event.preventDefault ? event.preventDefault() : event.returnValue = false;
                    }
                    return;
                } else if (scope.DataFormat === "$") {
                    //金額字元
                    var oRegExp = new RegExp("[+\\\-\\.0-9]");
                    if (! oRegExp.test(sCharCode)) {
                        event.preventDefault ? event.preventDefault() : event.returnValue = false;
                    }
                    return;
                }
            });
            ///======================
            /// Finish 
            /// 1.負責驗證資料正確性 (Success:繼續往下，Failure:SetError)
            /// 2.補值 (LNR)
            /// 3.轉型 (FLNU)
            /// 4.Format 原值
            ///======================
            elem.bind('blur', function() {
                this.value = trans2CYear(this.value, false);
                var sNewValue = this.value;
                //Step1 驗證資料正確性
                var IsOK = checkValidation(this);
                if (IsOK && (this.value.length>0)) {
                    //Step2 補值 (LNR)
                    this.value = fillField(this.value, this.maxLength);
                    //Step3 轉型 (FLNU)
                    console.log(this.value);
                    //更新到 ctrl.$modelValue 以及 ctrl.$viewValue
                    ctrl.$setViewValue(this.value);  
                    ctrl.$render();
                    //Step4 Format 原值
                    sNewValue = S2Format(this.value);
                    //Finish
                    if (sNewValue !== this.value) {
                        this.value = sNewValue;
                    }
                }
                this.style.background= IsOK ? "#ffffff" : "red";
            });
            ///======================
            /// 只在 Binding 時使用
            ///======================
            ctrl.$render = function() {
                console.log("$render");
                elem.val(S2Format(ctrl.$modelValue));
            };
        }
    };
}]);