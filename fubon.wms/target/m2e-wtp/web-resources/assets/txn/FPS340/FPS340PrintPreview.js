/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS340PrintPreviewController',
  function ($scope, $controller, $http, ngDialog, projInfoService, sysInfoService, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS340PrintPreviewController";

    /* parameter */
    var pathMap = {};
    var custID = $scope.custID;
    var planID = $scope.planID;
    var creatorName = projInfoService.getUserID();
    creatorName = creatorName + "0".repeat((8 - creatorName.length) > 0 ? 8 - creatorName.length : 0);
    $scope.today = (new Date()).yyyyMMdd('.');
    $scope.todayPrint = (new Date()).yyyyMMdd('/');
    $scope.fpsPrevBusiDay = $scope.connector('get', 'fpsPrevBusiDay');
    
    console.log($scope.data);
    // get data from parent

    var getData = function () {
      // data
      $scope.showFPS323Error = $scope.connector('get', 'FPS323_error');
      $scope.showFPS330Error = $scope.connector('get', 'FPS330_error');
      $scope.planDate = $scope.data.planDate.substring(0, 10).replace(/[-]+/g, "/");
      $scope.brief = $scope.data.brief;
      $scope.custInfo = $scope.data.custInfo;
      $scope.rptPicture = $scope.data.rptPicture;
      $scope.volatility = $scope.data.volatility;
      $scope.manualList = $scope.data.manualList;
      $scope.mapping = $scope.data.mapping;
      $scope.fpsPrint = $scope.data.fpsPrint;
      $scope.fps300 = $scope.data.fps300;
      $scope.hasIns = $scope.data.hasIns;
      $scope.MFDPerformanceList = $scope.data.MFDPerformanceList;
      $scope.paramList = $scope.data.paramList;
      $scope.paramList.forEach(function (row) {
        row.TRUST_CURR = row.CmbCur === '2' ? 'TWD' : row.CURRENCY_TYPE;
      });
      $scope.recommendations = $scope.data.recommendations;
      $scope.fromFps = $scope.data.fromFps;
    }

    /* init */
    $scope.init = function () {
      $scope.custInfo.maskedEMAIL = emailExpression($scope.custInfo.EMAIL);
      $scope.custInfo.maskedCUST_NAME = nameExpression($scope.custInfo.CUST_NAME);
      console.log($scope.data);
      doUrlPrepare();
      // debugger;
      // $timeout(function () {
      //   $scope.$broadcast('FPS340_print');
      // });

      //web view 需隱藏下載
      if (typeof (webViewParamObj) != 'undefined') {
        $scope.hideDownload = true;
      } else {
        $scope.hideDownload = false;
      }
    };
    // webview get parameters
    // getXML
    var param = function () {
      var deferred = $q.defer();
      var count = 1;
      var done = 2;
      var checkDone = function () {
        count += 1;
        if (count === done) {
        	$scope.mapping.statusMap = {
        		'1': 'PRINT_THINK', //客戶有下單意願
        		'0': 'PRINT_THINK', //暫存，客戶需考慮
        		'-1': 'PRINT_REJECT' //結束，客戶拒絕
	        };
	        $scope.mapping.fullProdType = {
	            MFD: '基金投資注意事項',
	            INSI: '連結全委帳戶之投資型保險投資注意事項'
	        };
	        $scope.mapping.noticeFlags = {
	        	MFD: false,
	        	INSI: false,
	        };
	        $scope.mapping.noticeTypes = {
	            0: 'MFD',
	            1: 'INSI',
	        }; 
	        deferred.resolve('success');
        }
      };
      $scope.connector('set', 'FPS340PrintSEQ', -1);
      $scope.sendRecv('FPS240', 'getParameter', 'com.systex.jbranch.app.server.fps.fps240.FPS240InputVO', {planID: planID},
        function (tota, isError) {
    	  if (!isError) {
    		  var notice = {};
              var noticeType = [];
              var noticeFlag = {};
              debugger
              tota[0].body.outputList.forEach(function (row) {
            	  if (row.PRD_TYPE === 'FUND') row.PRD_TYPE = 'MFD';
            	  else if (row.PRD_TYPE === 'BOND') row.PRD_TYPE = 'BND';
            	  else if (row.PRD_TYPE === 'INSI1' || row.PRD_TYPE === 'INSI2') row.PRD_TYPE = 'INSI';

            	  var tmp = (row.FONT || '').split('#');
            	  row.COLOR = tmp.length > 1 ? '#' + tmp[1] : undefined;
            	  row.FONT = (row.FONT).split('#')[0];

            	  // 篩選注意組合
            	  if (noticeType.indexOf(row.PRD_TYPE) < 0) {
            		  noticeType.push(row.PRD_TYPE);              
            	  }
            	  // 組注意資料列
            	  if (notice[row.PRD_TYPE] === undefined) {
	                  notice[row.PRD_TYPE] = [row];
	                  noticeFlag[row.PRD_TYPE] = false;
            	  } else {
	                  notice[row.PRD_TYPE].push(row);
	                  noticeFlag[row.PRD_TYPE] = false;
            	  }
              });
              var noticeTypeAll = [];
              var noticeFlagAll = {};
	          if($scope.custInfo.SIGN_AGMT_YN === 'N') {
	        	  noticeTypeAll.push('MFD');
	        	  noticeTypeAll.push('INSI');
	        	  noticeFlagAll = {
	        			  'MFD' : true,
	        			  'INSI' : true
	        	  }
	          } else {
	        	   noticeTypeAll = noticeType;
	        	   noticeFlagAll = noticeFlag;
	          }
	          $scope.mapping.noticeTypeAlls = noticeTypeAll;
	          $scope.mapping.noticeFlagAlls = noticeFlagAll;
	
	          $scope.mapping.noticeList = notice;
	          $scope.mapping.noticeTypes = noticeType;
	          $scope.mapping.noticeFlags = noticeFlag;
	          $scope.mapping.noticeToggles = false;
	          
	          checkDone();
	          return true;
          }
          deferred.reject(false);
        });
      	return deferred.promise;
    };

    // get 340 inquire
    var getFpsData = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPS340', 'inquire', 'com.systex.jbranch.app.server.fps.fps340.FPS340InputVO', {
    	  planId: $scope.planID,
          custId: $scope.custID,
          commRsYn: $scope.commYN,
          riskType: $scope.riskLevel,
          prdList: $scope.paramList
      }, function (tota, isError) {
    	  if (!isError) {
          	  // 前言
              var briefList = tota[0].body.briefList || [];
              $scope.brief = briefList.length > 0 ? briefList[0].CONTENT : '';
              
              // 規劃日期
              if (tota[0].body.planDate) {
              	$scope.planDate = tota[0].body.planDate.substring(0, 10).replace(/[-]+/g, '/');
              }
              
              // 商品
              $scope.manualList = tota[0].body.manualList;
              
              // 圖片
              $scope.rptPicture = tota[0].body.rptPicture;
              
              // 年度報酬
              $scope.yearRateList = tota[0].body.yearRateList;
              // 歷史年度平均報酬率
              $scope.historyYRate = tota[0].body.historyYRate === null ? '--' : ((Number(tota[0].body.historyYRate) * 100) || 0).toFixed(2);
              // 年化波動率
              $scope.volatility = tota[0].body.volatility === null ? '--' : ((Number(tota[0].body.volatility) * 100) || 0).toFixed(2);
              // 滿一年波波率
              $scope.fullyearVolatility = tota[0].body.fullYearVolatility === null ? '--' : ((Number(tota[0].body.fullYearVolatility) * 100) || 0).toFixed(2);
              
              // 基金績效
              $scope.MFDPerformanceList = tota[0].body.MFDPerformanceList.map(function (row) {
                row.RETURN_3M = row.RETURN_3M ? Math.round(row.RETURN_3M * 100) / 100 : '--';
                row.RETURN_6M = row.RETURN_6M ? Math.round(row.RETURN_6M * 100) / 100 : '--';
                row.RETURN_1Y = row.RETURN_1Y ? Math.round(row.RETURN_1Y * 100) / 100 : '--';
                row.RETURN_2Y = row.RETURN_2Y ? Math.round(row.RETURN_2Y * 100) / 100 : '--';
                row.RETURN_3Y = row.RETURN_3Y ? Math.round(row.RETURN_3Y * 100) / 100 : '--';
                row.VOLATILITY = row.VOLATILITY ? Math.round(row.VOLATILITY * 100) / 100 : '--';
                return row;
              });
              
              // set notice flag
              $scope.invPct = 0;
              $scope.invAmt = 0;
              
              $scope.paramList.forEach(function (row) {
                if (row.PTYPE === 'INS') {
                	$scope.mapping.noticeFlags['INSI'] = true;
	              	if ($scope.mapping.noticeTypeAlls.indexOf('INSI') < 0) {
	              		$scope.mapping.noticeTypeAlls.push('INSI');            		
	                }
                } else {
                	$scope.mapping.noticeFlags[row.PTYPE] = true;
                }
                //商品明細-配置比例、約當金額
                $scope.invPct += row.INV_PERCENT;
                $scope.invAmt += row.PURCHASE_TWD_AMT;
              });
              
              if($scope.custInfo.SIGN_AGMT_YN === 'Y') {
            	  $scope.mapping.noticeFlagAlls = $scope.mapping.noticeFlags;            	  
              }
              
              // 前一個營業日
              $scope.connector('set', 'fpsPrevBusiDay', tota[0].body.preBusiDay);
              
              defer.resolve(true);
              return true;
            }
            $scope.showErrorMsg(tota);
            defer.reject(false);
            return false;
      });
      return defer.promise;
    };

    /* main function */
    // 去那兒
    $scope.go = function (where, type) {
      switch (where) {
        case 'resend':
          break;
        case 'download':
          $scope.go('generatePDF');
          break;
        case 'generatePDF':

          var printSeq = $scope.connector('get', 'FPS340PrintSEQ');
          if (printSeq != -1) {
            // 相同時間已經印過
            savePdfnEncrypt(type);
            return true;
          }

          // show DN & creapy man
          var dialog = ngDialog.open({
            template: '<div><img style="display:block; margin:auto;vertical-align:middle;" width="150px" height="150px" src="./resource/loading.gif"></div>',
            plain: true,
            showClose: false,
            closeByDocument: false,
            closeByEscape: false,
            className: 'loading-Img'
          });

          if (typeof (webViewParamObj) != 'undefined')
            document.querySelector('.FPS .print.A4.iPad').classList.remove('iPad');
          html2Pdf.getPdf(['*[print]'], 'blob', null, {
              pageFormat: 'a4',
              pdfName: $scope.today + '特定目的理財規劃書.pdf',
              footer: {
                leftText: $scope.today + creatorName
              }
            })
            .then(function (data) {
              // fix back as usual
              dialog.close();
//              uploadPdf(custID + planID + '.pdf', data, type);
              uploadPdf($scope.today + planID + '.pdf', data, type);
            });
          break;
        default:
          break;
      }
    };

    // upload pdf
    var uploadPdf = function (fileName, data, type) {
      // test
      // var url = window.URL.createObjectURL(data);
      // window.open(url);
      // return;
      if (!data) return false;

      var formData = new FormData();
      var uploadFileName = uuid();
      var fileSize = data.size;
      var mimeType = '.pdf';
      var tempFileName = uploadFileName + mimeType;
      var file = new File([data], fileName, {
        type: 'application/pdf ',
        name: tempFileName
      });
      formData.append(tempFileName, file);

      $http.post('FileUpload', formData, {
          transformRequest: angular.identity,
          headers: {
            'Content-Type': undefined
          }
        })
        .then(function (jqXHR) {
          if (jqXHR.status === 200) {
            savePdfnEncrypt(type, tempFileName, fileName, data);
          }
        }, function (error) {
          console.log(error);
        });
    };

    var savePdfnEncrypt = function (type, tempFileName, fileName) {
      $scope.sendRecv('FPS340', 'generatePdf', 'com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsPdfInputVO', {
          planID: planID,
          custID: custID,
          tempFileName: tempFileName,
          fileName: fileName,
          action: type,
          aoCode: sysInfoService.getUserID(),
          printSEQ: $scope.connector('get', 'FPS340PrintSEQ')
        },
        function (tota, isError) {
          if (!isError) {
              if (typeof (webViewParamObj) != 'undefined') {
              	document.querySelector('.FPS .print.A4').classList.add('iPad');            	
              }
              $scope.connector('set', 'FPS340PrintSEQ', -1);
            if (type === 'resend')
              $scope.showMsg('轉寄完成');
          } else {
            $scope.showErrorMsg(type === 'resend' ? '轉寄失敗' : '下載失敗');
          }
        });
    };

    // 姓名遮中間
    var nameExpression = function (name) {
      if (!name || name.length < 2) {
        return name;
      } else {
        var len = name.length;
        var firstName = name.length < 4 ? name.substring(0, 1) : name.substring(0, 2);
        var lastName = 'O' + (name.length > 2 ? name.substring(len - 1, len) : '');
        return firstName + lastName;
      }
    };

    // EMAIL 遮罩邏輯 在@ 前的文字 只留前後英文或數字 其他改成圓圈
    // 如jeffery.jang@fubon.com 變成 jOOOOOOOOOOg@fubon.com
    var emailExpression = function (email) {
      if (!email || email.indexOf('@') < 4) {
        return '';
      } else {
        var firstStr = email.substring(0, 4);
        var index = email.indexOf('@');
        var repeatStr = ('O').repeat(index - 4);
        var finalStr = email.substring(index);
        return firstStr + repeatStr + finalStr;
      }
    };

    $scope.closeBack = function () {
      $scope.custInfo.EMAIL = $scope.tempEmail;
      $scope.closeThisDialog('cancel');
    }
    
    var doUrlPrepare = function() {
    	var getPrintTemplateURL = function () {
	      return 'assets/txn/FPS_PDF_Template/FPS340_print_' +
	        ($scope.hasIns ? 'has' : 'no') +
	        'ins_commr' +
	        ($scope.custInfo.SIGN_AGMT_YN === 'Y' ? 'y' : 'n') +
	        '.template.html';
	    }

	    $scope.printTemplate = getPrintTemplateURL();
    }

    console.log('FPS240PrintPreviewController');
    
    // if webview then get parameters
    if (typeof (webViewParamObj) != 'undefined') {
    	param().then(function () {
            return getFpsData().then(function () {
                $scope.init();
            })
        });
    } else {
    	getData();
    	$scope.init();
    	doUrlPrepare();
    }
 });
