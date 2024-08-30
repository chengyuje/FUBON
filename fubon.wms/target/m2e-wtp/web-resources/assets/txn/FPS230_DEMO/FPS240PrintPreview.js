/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS240_DEMOPrintPreviewController',
  function ($scope, $controller, $http, ngDialog, projInfoService, $q, fps200Service) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS240_DEMOPrintPreviewController";

    /* parameter */
    var custID = $scope.custID;
    var planID = $scope.planID;
    var creatorName = projInfoService.getUserID();

    creatorName = creatorName + ('0').repeat((8 - creatorName.length) > 0 ? 8 - creatorName.length : 0);
    $scope.today = (new Date()).yyyyMMdd('.');
    $scope.todayPrint = (new Date()).yyyyMMdd('/');

    console.log($scope.data);
    // get data from parent
    var getData = function () {
      // data
      $scope.planDate = $scope.data.planDate;
      $scope.brief = $scope.data.brief;
      $scope.custInfo = $scope.data.custInfo;
      $scope.rptPicture = $scope.data.rptPicture;
      $scope.headList = $scope.data.headList;

      $scope.depositNowPct = $scope.data.depositNowPct;
      $scope.depositPct = $scope.data.depositPct;
      $scope.depositAmt = $scope.data.depositAmt;
      $scope.depositList = $scope.data.depositList;

      $scope.fixedNowPct = $scope.data.fixedNowPct;
      $scope.fixedPct = $scope.data.fixedPct;
      $scope.fixedAmt = $scope.data.fixedAmt;
      $scope.fixedList = $scope.data.fixedList;

      $scope.stockNowPct = $scope.data.stockNowPct;
      $scope.stockPct = $scope.data.stockPct;
      $scope.stockAmt = $scope.data.stockAmt;
      $scope.stockList = $scope.data.stockList;

      $scope.yearRateList = $scope.data.yearRateList;
      $scope.stockVolatility = $scope.data.stockVolatility;
      $scope.historyYRate = $scope.data.historyYRate;
      $scope.volatility = $scope.data.volatility;

      $scope.MFDPerformanceList = $scope.data.MFDPerformanceList;
      $scope.transactionList = $scope.data.transactionList;
      $scope.manualList = $scope.data.manualList;
      $scope.mapping = $scope.data.mapping;
      $scope.fpsChart = $scope.data.fpsChart;
      $scope.hasIns = $scope.data.hasIns;
      $scope.overValue = $scope.data.overValue;
      
    };

    // test 1
    // $scope.printTemplate = 'assets/txn/FPS_PDF_Template/FPS240_print.template.html';
    // test 2
    $scope.printTemplate = '';

    var getPrintTemplateURL = function () {
      return 'assets/txn/FPS_PDF_Template_DEMO/FPS240_print_hasins_commry.template.html';
    }

    /* init */
    $scope.init = function () {
      $scope.custInfo.maskedEMAIL = emailExpression($scope.custInfo.EMAIL);
      $scope.custInfo.maskedCUST_NAME = nameExpression($scope.custInfo.CUST_NAME);
//      $scope.depositList = formatDepositList($scope.depositList);
      console.log($scope.data);
      $scope.printTemplate = getPrintTemplateURL();
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
      var fps200 = fps200Service;
      var count = 0;
      var done = 2;
      var checkDone = function () {
        count += 1;
        if (count === done) {
          deferred.resolve('success');
        }
      };

      fps200.getXmlParam([
        'FPS.CUST_RISK_ATR_2',
        'FPS.BUY_CURRENCY',
        'FPS.TXN_TYPE',
        'FPS.INV_TYPE',
        'FPS.PROD_TYPE',
        'FPS.INV_PRD_TYPE',
        'FPS.INV_PRD_TYPE_2',
        'FPS.CURRENCY'
      ]).then(function (mapping) {
        $scope.mapping = Object.assign($scope.mapping, mapping);
        $scope.mapping.ETFTxnType = $scope.mapping.txnType.filter(function (row) {
          return row.DATA.charAt(0) === 'E';
        });
        $scope.mapping.INSTxnType = $scope.mapping.txnType.filter(function (row) {
          return row.DATA.charAt(0) === 'I';
        });
        $scope.mapping.MFDTxnType = $scope.mapping.txnType.filter(function (row) {
          return row.DATA.charAt(0) === 'F';
        });

        $scope.mapping.TxnType = $scope.mapping.txnType.filter(function (row) {
          return row.DATA.charAt(0) === '1';
        });
        $scope.mapping.fixedTxnType = $scope.mapping.txnType.filter(function (row) {
          return !!Number(row.DATA.charAt(0));
        });

        checkDone();
      }, function (err) {
        deferred.reject(false);
      });

      $scope.sendRecv('FPS240', 'getParameter', 'com.systex.jbranch.app.server.fps.fps240.FPS240InputVO', {
          planID: planID
        },
        function (tota, isError) {
          if (!isError) {
            var notice = {};
            var noticeType = [];
            var noticeFlag = {};

            tota[0].body.outputList.forEach(function (row) {
              row.type = row.PRD_TYPE.replace(/\d/g, '') || row.PRD_TYPE;
              var tmp = (row.FONT || '').split('#');
              row.COLOR = tmp.length > 1 ? '#' + tmp[1] : undefined;
              row.FONT = (row.FONT).split('#')[0];

              // 篩選注意組合
              if (noticeType.indexOf(row.type) < 0) {
                noticeType.push(row.type);
              }
              // 組注意資料列
              if (notice[row.PRD_TYPE] === undefined) {
                notice[row.PRD_TYPE] = [row];
                noticeFlag[row.type] = false;
              } else {
                notice[row.PRD_TYPE].push(row);
                noticeFlag[row.type] = false;
              }
            });

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

    // get 240 inquire
    var getFpsData = function () {
      var modelPortfolioList = [];
      $scope.recommend = {
        deposit: 0,
        fixed: 0,
        stock: 0
      };
      var defer = $q.defer();
      $scope.sendRecv('FPS240', 'inquire', 'com.systex.jbranch.app.server.fps.fps240.FPS240InputVO', {
          planID: $scope.planID,
          custID: $scope.custID,
          riskType: $scope.riskLevel,
          OBU: $scope.OBU,
          isPro: $scope.isPro,
          commRsYn: $scope.commYN
        },
        function (tota, isError) {
          if (!isError) {
            $scope.headList = {};
            $scope.paramList = tota[0].body.outputList;

            $scope.recoVolatility = tota[0].body.recoVolatility || 0;
            // 年度報酬
            $scope.yearRateList = tota[0].body.yearRateList;
            // 歷史年度平均報酬率
            $scope.historyYRate = tota[0].body.historyYRate === null ? '--' : ((Number(tota[0].body.historyYRate) * 100) || 0).toFixed(2);
            // 波波率
            $scope.volatility = tota[0].body.volatility === null ? '--' : ((Number(tota[0].body.volatility) * 100) || 0).toFixed(2);
            // 初始模組
            modelPortfolioList = tota[0].body.initModelPortfolioList || [];
            // 滿一年波波率
            $scope.fullyearVolatility = tota[0].body.fullYearVolatility === null ? '--' : ((Number(tota[0].body.fullYearVolatility) * 100) || 0).toFixed(2);
            // 商品
            $scope.manualList = tota[0].body.manualList;
            // 圖片
            $scope.rptPicture = tota[0].body.rptPicture;
            // plan date
            $scope.planDate = $scope.toJsDate(tota[0].body.planDate).yyyyMMdd('/');
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
            // 前言
            var briefList = tota[0].body.briefList || [];
            $scope.brief = briefList.length > 0 ? briefList[0].CONTENT : '';

            // cust head data
            if (tota[0].body.headList && tota[0].body.headList.length > 0) {
              $scope.headList = tota[0].body.headList[0];
              // fps210
              $scope.headList.deposit = $scope.headList.DEPOSIT_AMT || 0;
              // hasIns
              $scope.headList.annuityProd = $scope.headList.ANNUITY_INS_AMT || 0;
              $scope.headList.fixedProd = $scope.headList.FIXED_INCOME_AMT || 0;
              $scope.headList.fundProd = $scope.headList.FUND_AMT || 0;
              $scope.headList.liquidAmt = $scope.headList.annuityProd + $scope.headList.fixedProd + $scope.headList.fundProd + $scope.headList.deposit;

              $scope.headList.sowAmt = $scope.headList.SOW_AMT || 0;
              // INS_YEAR_AMT_1(new) INS_POLICY_AMT(inv)
              $scope.headList.insPolicyAmt = ($scope.headList.INS_POLICY_AMT || $scope.headList.INS_YEAR_AMT_1) || 0;
              // INS_YEAR_AMT_2(new) INS_SAV_AMT(inv)
              $scope.headList.insSavAmt = ($scope.headList.INS_SAV_AMT || $scope.headList.INS_YEAR_AMT_2) || 0;
              $scope.headList.ins = $scope.headList.INS_AMT || 0;
              $scope.headList.cash = $scope.headList.CASH_YEAR_AMT || 0;
              $scope.headList.price = (Number($scope.headList.deposit) + Number($scope.headList.sowAmt)) || 0;

              // fps220
              $scope.headList.planAmt = $scope.headList.PLAN_AMT;
              $scope.headList.depositAmt = $scope.headList.ANNUITY_INS_AMT + $scope.headList.DEPOSIT_AMT + $scope.headList.SOW_AMT -
                $scope.headList.CASH_YEAR_AMT - $scope.headList.INS_SAV_AMT - $scope.headList.INS_POLICY_AMT;
              $scope.headList.fixedAmt = $scope.headList.FIXED_INCOME_AMT;
              $scope.headList.stockAmt = $scope.headList.FUND_AMT;
              $scope.headList.depositPct = Math.round($scope.headList.depositAmt / $scope.headList.planAmt * 100);
              $scope.headList.fixedPct = Math.round($scope.headList.fixedAmt / $scope.headList.planAmt * 100);
              $scope.headList.stockPct = Math.round((100 - $scope.headList.depositPct - $scope.headList.fixedPct));

              // pct same as headlist
              $scope.depositNowPct = $scope.headList.depositPct;
              $scope.fixedNowPct = $scope.headList.fixedPct;
              $scope.stockNowPct = $scope.headList.stockPct;

              // hasIns
              $scope.stockVolatility = tota[0].body.stockVolatility === null ? '--' : ((Number(tota[0].body.stockVolatility) * 100) || 0).toFixed(2);
            }

            // recommend model
            modelPortfolioList.forEach(function (row) {
              if ('1' === row.INV_PRD_TYPE) {
                $scope.recommend.deposit += Number(row.INV_PERCENT);
              } else if ('2' === row.INV_PRD_TYPE) {
                $scope.recommend.fixed += Number(row.INV_PERCENT);
              } else if ('3' === row.INV_PRD_TYPE) {
                $scope.recommend.stock += Number(row.INV_PERCENT);
              }
            });

            // add stock ratio
            if ($scope.hasIns) {
              $scope.mapping.keys.forEach(function (key) {
                $scope[key + 'List'].forEach(function (row) {
                  row.NOW_INV_PERCENT = Math.round(parseFloat((((row.NOW_AMT_TWD || 0) / $scope.headList.planAmt) || 0) * 100));
                });
              });
            }

            // set notice flag
            var pTypeMap = {
              MFD: 'FUND',
              ETF: 'ETF',
              BND: 'BND',
              SI: 'SI',
              SN: 'SN',
              S: 'INS-S',
              I: 'INSI'
            };
            $scope.transactionList.forEach(function (row) {
              // make up txnTypeList
              row.txnTypeList = $scope.mapping.txnType;
              var tmp = pTypeMap[row.PTYPE];
              if (row.PTYPE === 'INS') {
                tmp = row.INV_PRD_TYPE === '3' ? pTypeMap.I : pTypeMap.S;
              }
              $scope.mapping.noticeFlags[tmp] = true;
            });

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
        case 'download':
          $scope.go('generatePDF', type);
          break;
        case 'generatePDF':

          var printSeq = $scope.connector('get', 'FPS240PrintSEQ');
          if (printSeq !== -1) {
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

          // var timer = $timeout(() => {
          //   dialog.close();
          // }, 12000);

          var today = new moment().format('YYYYMMDD');
          // today + creatorName
          // test
          // html2Pdf.getPdf(['*[print]'], 'save', null, {
          //     pageFormat: 'a4',
          //     pdfName: today + '非特定目的理財規劃書.pdf',
          //     footer: {
          //       leftText: today + creatorName
          //     }
          //   })
          // test
          // html2Pdf.getPdf(['*[print]'], 'save', null, today + creatorName)
          // test
          // html2Pdf.getPdf(['*[print]'], 'blob', null, today + creatorName)
          // selectorList, type, callbacks, options
          if (typeof (webViewParamObj) != 'undefined')
            document.querySelector('.FPS .print.A4.iPad').classList.remove('iPad');
          html2Pdf.getPdf(['*[print]'], 'blob', null, {
              pageFormat: 'a4',
              pdfName: today + '非特定目的理財規劃書.pdf',
              footer: {
                leftText: today + creatorName
              }
            }).then(function (data) {
              // fix back as usual
              dialog.close();
              // $timeout.cancel(timer);
//              uploadPdf(custID + planID + '.pdf', data, type);
              uploadPdf(today + planID + '.pdf', data, type);
              // uploadPdf(today + '非特定目的理財規劃書.pdf', data, type);
            }, function (reject) {
              dialog.close();
              // $timeout.cancel(timer);
            })
            .catch(function (e) {
              console.log(e);
              dialog.close();
              $scope.showErrorMsg('下載/轉寄失敗');
              // $timeout.cancel(timer);
            });
          break;
        default:
          break;
      }
    };

    // upload pdf
    var uploadPdf = function (fileName, data, type) {
      // test
       var url = window.URL.createObjectURL(data);
       window.open(url);
       return;
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
          $scope.showErrorMsg('上傳失敗');
        });
    };

    var savePdfnEncrypt = function (type, tempFileName, fileName) {
      $scope.sendRecv('FPS240', 'generatePdf', 'com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsPdfInputVO', {
          planID: planID,
          custID: custID,
          tempFileName: tempFileName,
          fileName: fileName,
          action: type,
          printSEQ: $scope.connector('get', 'FPS240PrintSEQ')
        },
        function (tota, isError) {
          if (!isError) {
            console.log(tota);
            if (typeof (webViewParamObj) != 'undefined')
              document.querySelector('.FPS .print.A4').classList.add('iPad');
            $scope.connector('set', 'FPS240PrintSEQ', tota[1].body);
            if (type === 'resend')
              $scope.showMsg('轉寄完成');
          } else {
            $scope.showErrorMsg('轉寄失敗');
          }
        });
    };

    var formatDepositList = function (ls) {
      var orgCur = null;
      var formatedDepositList = ls.reduce(function (a, b) {
        if (b.INV_PRD_TYPE_2 !== '2') {
          a.push(angular.copy(b));
        } else {
          if (!orgCur) {
            orgCur = angular.copy(b);
            orgCur.CURRENCY_TYPE = '--';
            orgCur.PURCHASE_ORG_AMT = '-';
            orgCur.NOW_AMT = '-';
            orgCur.TRUST_CURR = '--';
            orgCur.INV_PERCENT = (Number(orgCur.INV_PERCENT) || 0);
            orgCur.PURCHASE_TWD_AMT = (Number(orgCur.PURCHASE_TWD_AMT) || 0);
            orgCur.NOW_AMT_TWD = (Number(orgCur.NOW_AMT_TWD) || 0);
            orgCur.NOW_INV_PERCENT = (Number(orgCur.NOW_INV_PERCENT) || 0);
          } else {
            orgCur.INV_PERCENT += (Number(b.INV_PERCENT) || 0);
            orgCur.PURCHASE_TWD_AMT += (Number(b.PURCHASE_TWD_AMT) || 0);
            orgCur.NOW_AMT_TWD += (Number(b.NOW_AMT_TWD) || 0);
            orgCur.NOW_INV_PERCENT += (Number(b.NOW_INV_PERCENT) || 0);
          }
        }
        return a;
      }, []);
      if (orgCur) {
        formatedDepositList.push(orgCur);
      }
      return formatedDepositList;
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
      $scope.closeThisDialog('cancel');
    };

    console.log('FPS240PrintPreviewController');

    // // if webview then get parameters
    if (typeof (webViewParamObj) != 'undefined') {
      $scope.fpsChart = FPSChartUtils($scope.mapping, canvasJsConfig);
      $scope.connector('set', 'FPS240PrintSEQ', -1);
      param()
        .then(function () {
          return getFpsData()
        })
        .then(function () {
          $scope.init();
        });
    } else {
      getData();
      $scope.init();
    }
  }
);
