'use strict';
eSoafApp.controller('INS300Controller',
  function ($scope, $controller, $q, $confirm, $filter, getParameter, ngDialog) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'INS300Controller';

    $scope.mapping = {};
    // combobox:INS.QID 險種類別, radio:INS.IS_MAIN 主附約, radio:INS.IS_SALE 現停售
    getParameter.XML(['INS.TYPE_CLASS', 'INS.IS_MAIN', 'INS.IS_SALE'], function (totas) {
      $scope.mapping.TYPE_CLASS = [];
      $scope.mapping.IS_MAIN = [];
      $scope.mapping.IS_SALE = [];
      // console.log(totas);
      if (totas) {
        $scope.mapping.TYPE_CLASS = totas.data[totas.key.indexOf('INS.TYPE_CLASS')] || [];
        $scope.mapping.IS_MAIN = totas.data[totas.key.indexOf('INS.IS_MAIN')] || [];
        $scope.mapping.IS_SALE = totas.data[totas.key.indexOf('INS.IS_SALE')] || [];
      }
    });

    // combobox:INS.COM_NAME 公司名稱
    $scope.sendRecv('INS300', 'getComName', 'com.systex.jbranch.app.server.fps.ins300.INS300InputVO', {},
      function (totas, isError) {
        if (isError) {
          $scope.showErrorMsg(totas);
          return;
        }
        if (totas.length > 0 && totas[0].body && totas[0].body.Ins_outputList) {
          $scope.mapping.COM_NAME = [];
          totas[0].body.Ins_outputList.forEach(function (row, index) {
            $scope.mapping.COM_NAME.push({
              'LABEL': row.COM_NAME,
              'DATA': row.COM_ID
            });
          });
        };
      });

    /** main function **/
    $scope.init = function () {
      //query data
      $scope.query = {};
      $scope.query.COM_NAME = '';
      $scope.query.QID = '';
      $scope.query.PRD_NAME = '';
//      $scope.query.IS_SALEY = undefined;
//      $scope.query.IS_SALEN = undefined;
      $scope.query.IS_MAIN = 'Y';
      //table data
      $scope.insCheck = [];
      $scope.paramList = [];
      $scope.outputVO = {};
      $scope.pdfParamList = [];
      $scope.totalParamList = [];
      $scope.query.req_certificate = []
    };

    $scope.init();

    $scope.queryData = function queryData() {
      // check validation
//      if ($scope.INS300QueryForm.$invalid || !$scope.query.QID)
//        return $scope.showErrorMsgInDialog('欄位檢核錯誤: 險種類別為必要輸入欄位');

      var mapCOM_NAME;
      //test
      if ($scope.query.COM_NAME) {
        mapCOM_NAME = $scope.mapping.COM_NAME.filter(function (item) {
          return item.DATA == $scope.query.COM_NAME;
        });
      }
      
      if(!$scope.query.req_certificate || $scope.query.req_certificate.length == 0) {
    	  $scope.showErrorMsg('請選擇 現售 或 停售');
    	  return ;
      }

      $scope.inputVO.COM_NAME = mapCOM_NAME && mapCOM_NAME.length > 0 ? mapCOM_NAME[0].LABEL : '';
      $scope.inputVO.QID = $scope.query.QID || '';
      $scope.inputVO.PRD_NAME = $scope.query.PRD_NAME || '';
//      $scope.inputVO.IS_SALE = $scope.query.IS_SALE;
      $scope.inputVO.IS_MAIN = $scope.query.IS_MAIN;
      $scope.inputVO.isSaleList = $scope.query.req_certificate
      console.log($scope.inputVO);
      // send request
      $scope.sendRecv('INS300', 'queryData', 'com.systex.jbranch.app.server.fps.ins300.INS300InputVO', $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.Ins_outputList.length === 0) {
              $scope.totalParamList = [];
              $scope.paramList = [];
              $scope.outputVO = {};
              return $scope.showMsg('ehl_01_common_009');
            }
            $scope.totalParamList = tota[0].body.Ins_outputList.map(function (row) {
              row.ITEM_Y = row.ITEM_Y ? row.ITEM_Y.split(',').filter(function (a) {
                return a != undefined && a != null && a != '';
              }).join(',') : '';
              return row;
            });
            
            $scope.outputVO = tota[0].body;
            $scope.initCheckedList();
            console.log($scope.outputVO);
            return;
          }
        });
    };

    $scope.btnClear = function btnClear() {
      $scope.init();
    };

    // 檢視說明
    $scope.btnDescription = function btnDescription() {
      // check checked
      var _checkedList = getCheckedList();

      if (_checkedList.length <= 0)
        return $scope.showErrorMsg('ehl_01_INS300_007');
      else if (_checkedList.length > 3)
        return $scope.showErrorMsg('ehl_01_INS300_004');

      var inputVO = {
        prdIDArr: _checkedList
      };
      // test
      // var test = ['http://134.208.29.93/cdrfb3/thesis_user_guide.pdf', 'http://134.208.29.93/cdrfb3/thesis_user_guide.pdf'];
      // var _str = test.join('&&pdfurl=');
      // window.open('INS300_PDF.html?pdfurl=' + _str, 'Description');

      // send request
      $scope.sendRecv('INS300', 'btnDescription', 'com.systex.jbranch.app.server.fps.ins300.INS300InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.lstPdf.length === 0) {
              $scope.paramList = [];
              $scope.outputVO = {};
              return $scope.showMsg('ehl_01_common_009');
            }

            var description = tota[0].body.lstPdf.filter(function (row) {
                return row.URL1;
              })
              .map(function (row) {
                return row.URL1;
              });
            console.log(tota[0].body);
            if (description.length > 0) {
              if (description.length > 3) {
                description.splice(3, description.length - 3);
              }
              var _str = description.join('&&pdfurl=');
              window.open('./INS300_PDF.html?pdfurl=' + _str, 'Description');
            } else {
              $scope.showErrorMsg('ehl_01_common_009');
            }
            return true;
          }
        });
    };
    
    // 是否停售
    //複選
    $scope.toggleSelection = function toggleSelection(data) {
    	var idx = $scope.query.req_certificate.indexOf(data);
    	if (idx > -1) {
    		$scope.query.req_certificate.splice(idx, 1);      //若已存在，就將它移除
    	} else {
    		$scope.query.req_certificate.push(data);          //若不存在，就將它加入
    	}
    };

    // 檢視條款
    $scope.btnTerms = function btnTerms() {
      // check checked
      var _checkedList = getCheckedList();

      if (_checkedList.length <= 0)
        return $scope.showErrorMsg('ehl_01_INS300_008');
      else if (_checkedList.length > 3)
        return $scope.showErrorMsg('ehl_01_INS300_005');

      var inputVO = {
        prdIDArr: _checkedList
      };
      console.log(inputVO);
      // send request
      $scope.sendRecv('INS300', 'btnTerms', 'com.systex.jbranch.app.server.fps.ins300.INS300InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.lstPdf.length === 0) {
              return $scope.showMsg('ehl_01_common_009');
            }

            var terms = tota[0].body.lstPdf.filter(function (row) {
                return row.URL1;
              })
              .map(function (row) {
                return row.URL1;
              });
            console.log(tota[0].body);
            if (terms.length > 0) {
              if (terms.length > 3) {
                terms.splice(3, terms.length - 3);
              }
              var _str = terms.join('&&pdfurl=');
              window.open('./INS300_PDF.html?pdfurl=' + _str, 'Terms');
            } else {
              $scope.showErrorMsg('ehl_01_common_009');
            }
            return true;
          }
        });
    };

    // 檢視完整內容
    $scope.btnContent = function btnContent() {
      // check checked
      var _checkedList = getCheckedList();

      if (_checkedList.length <= 0)
        return $scope.showErrorMsg('ehl_01_INS300_009');
      else if (_checkedList.length > 1)
        return $scope.showErrorMsg('ehl_01_INS300_006');

      var inputVO = {
        prdIDArr: _checkedList
      };
      // test
      // var test = ['http://134.208.29.93/cdrfb3/thesis_user_guide.pdf', 'http://134.208.29.93/cdrfb3/thesis_user_guide.pdf'];
      // var _str = test.join('&&pdfurl=');
      // window.open('INS300_PDF.html?pdfurl=' + _str, 'Content');

      // send request
      $scope.sendRecv('INS300', 'btnContent', 'com.systex.jbranch.app.server.fps.ins300.INS300InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.lstPdf.length === 0) {
              return $scope.showMsg('ehl_01_common_009');
            }

            var content = tota[0].body.lstPdf
              .filter(function (row) {
                return row.URL1 && row.URL2;
              });
            console.log(tota[0].body);
            console.log(content);
            if (content.length > 0) {
              var _str = [content[0].URL1, content[0].URL2].join('&&pdfurl=');

              window.open('./INS300_PDF.html?pdfurl=' + _str, 'Content');
            } else {
              $scope.showErrorMsg('ehl_01_common_009');
            }
            return true;
          }
        });
    };

    // 列印
    $scope.btnPrint = function btnPrint() {
      // check checked
      var _checkedList = getCheckedList();

      if (_checkedList.length <= 0)
        return $scope.showErrorMsg('ehl_01_INS300_010');
      else if (_checkedList.length > 1)
        return $scope.showErrorMsg('ehl_01_INS300_011');

      var inputVO = {
        prdIDArr: _checkedList
      };
      // send request
      $scope.sendRecv('INS300', 'btnPrint', 'com.systex.jbranch.app.server.fps.ins300.INS300InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.lstPdf.length === 0) {
              return $scope.showSuccessMsg('ehl_01_common_001');
            }
            return;
          }
          $scope.showErrorMsg('ehl_01_common_009');
        });
    };

    // 比較
    $scope.btnComp = function btnComp() {
      // check checked
      var _checkedList = getCheckedList();

      if (_checkedList.length <= 1)
        return $scope.showErrorMsg('ehl_01_INS300_012');
      else if (_checkedList.length > 4)
        return $scope.showErrorMsg('ehl_01_INS300_003');

      var inputVO = {
        prdIDArr: _checkedList
      };

      // call getInsCompare (prdID collection ) then notifidownload 
      // > BIRT 140 145 => url 
      console.log(inputVO);
      // send request
      $scope.sendRecv('INS300', 'btnComp', 'com.systex.jbranch.app.server.fps.ins300.INS300InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            console.log(tota[0].body);
            if (!tota[0].body) {
              return $scope.showMsg('ehl_01_common_009');
            }
            var compare = tota[0].body;
            var _str = [compare].join('&&pdfurl=');
            window.open('./INS300_PDF.html?pdfurl=' + _str, 'Compare');
            return true;
          }
        });
    };

    $scope.initCheckedList = function () {
      $scope.totalParamList.forEach(function (row) {
        row.checked = false;
      });
    };

    /** sub function **/
    $scope.toggleCheck = function toggleCheck(row) {
      row.checked = !row.checked;
    };

    /** helping function **/

    function getCheckedList() {
      var _checkedList = [];
      $scope.totalParamList.forEach(function (item, index) {
        if (item.checked) {
          _checkedList.push(item.KEY_NO);
        }
      });
      return _checkedList;
    }
  });
