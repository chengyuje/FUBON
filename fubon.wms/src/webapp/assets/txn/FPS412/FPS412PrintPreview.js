/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('FPS412PrintPreviewController',
  function ($rootScope, $scope, $controller, $sce, $http, $timeout, ngDialog, projInfoService, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "FPS412PrintPreviewController";
    /* parameter */
    var pathMap = {};
    var custID = $scope.custID;
    var planID = $scope.planID;
    var creatorName = projInfoService.getUserID();
    creatorName = creatorName + "0".repeat((8 - creatorName.length) > 0 ? 8 - creatorName.length : 0);
    $scope.today = (new Date()).yyyyMMdd('.');
    $scope.todayPrint = (new Date()).yyyyMMdd('/');

    var getData = function () {    	
    	// data
        $scope.isReb = true; //true:顯示 單筆投入總額(NT$)、定期投入總額(NT$)
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
        $scope.checkTransaction = false;
        
        $scope.paramListStock = $scope.data.paramListStock;
        $scope.mixedParamList = $scope.data.mixedParamList;
        
        $scope.recommendations = $scope.data.recommendations;
        $scope.fromFps = $scope.data.fromFps;
    }
    
    var doUrlPrepare = function() {
        var getPrintTemplateURL = function () {
          return 'assets/txn/FPS_PDF_Template/FPS340_print_' +
          'hasins_commr' +
          // 'y' +
          ($scope.custInfo.SIGN_AGMT_YN === 'Y' ? 'y' : 'n') +
          '.template.html';
        }
        
        $scope.printTemplate = getPrintTemplateURL();
    }
    
    /* init */
    $scope.init = function () {
      $scope.custInfo.maskedEMAIL = emailExpression($scope.custInfo.EMAIL);
      $scope.custInfo.maskedCUST_NAME = nameExpression($scope.custInfo.CUST_NAME);
      doUrlPrepare();
      //console.log($scope.data);
      
      $scope.paramList.forEach(function (row) {
          if (row.TxnType) {
            $scope.checkTransaction = true;
          }
      });
      
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

    var param = function () {
    	$scope.connector('set', 'FPS412PrintSEQ', -1);
    	$scope.fromFps = '340';
        var deferred = $q.defer();
        var count = 1;
        var done = 2;
        var checkDone = function () {
          count += 1;
          if (count === done) {
          	$scope.mapping.statusMap = {
          		'1': 'PRINT_THINK', 	//客戶有下單意願
                '0': 'PRINT_THINK', 	//暫存，客戶需考慮
                '-1': 'PRINT_REJECT' 	//結束，客戶拒絕
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
        
        $scope.sendRecv('FPS240', 'getParameter', 'com.systex.jbranch.app.server.fps.fps240.FPS240InputVO', {planID: planID},
          function (tota, isError) {
          	if (!isError) {
                var notice = {};
                var noticeType = [];
                var noticeFlag = {};
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
                
                $scope.transactionList.forEach(function (row) {
                	if (row.PTYPE === 'INS') {
                		$scope.mapping.noticeFlags['INSI'] = true;
              	  	} else {
              	  		$scope.mapping.noticeFlags[row.PTYPE] = true;
              	  	}
              	  
              	  	if($scope.custInfo.SIGN_AGMT_YN === 'Y'){
              	  		$scope.mapping.noticeFlagAlls = $scope.mapping.noticeFlags    		  
              	  	} 
              	  	
              	  	if ($scope.transactionList.length > 0)
              	  		$scope.checkTransaction = true;
                });
                
                checkDone();
                return true;
              }
          deferred.reject(false);
        });
      return deferred.promise;
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

          var printSeq = $scope.connector('get', 'FPS412PrintSEQ');
          if (printSeq != -1) {
            // 相同時間已經印過
            savePdfnEncrypt();
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

          html2Pdf.getPdf(['*[print]'], 'blob', null, {
              pageFormat: 'a4',
              pdfName: $scope.today + '特定目的績效理財規劃書.pdf',
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

    var openDialog = function (path, data, type) {
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.data = data;
          $scope.trendPath = scope.mapping.trendPath;
        }]
      });

      dialog.closePromise.then(function (data) {
        if (typeof (data.value) === 'object') {
          popCbFn[type](data.value);
        }
      });
    };

    var popCbFn = {};

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
            savePdfnEncrypt(tempFileName, fileName, data, type);
          }
        }, function (error) {
          console.log(error);
        });
    };

    var savePdfnEncrypt = function (tempFileName, fileName, data, type) {
      $scope.sendRecv('FPS340', 'generatePdf', 'com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsPdfInputVO', {
          planID: planID,
          custID: custID,
          tempFileName: tempFileName,
          fileName: fileName,
          action: type,
          printSEQ: $scope.connector('get', 'FPS412PrintSEQ')
        },
        function (tota, isError) {
          if (!isError) {
//            console.log(tota);
//            $scope.connector('set', 'FPS412PrintSEQ', tota[1].body);
            $scope.connector('set', 'FPS412PrintSEQ', -1);
            if (type === 'resend')
              $scope.showMsg('轉寄完成');
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

    console.log('FPS412PrintPreviewController');
    
    if (typeof (webViewParamObj) != 'undefined') {
    	param().then(function () {
    		$scope.init();
    	});
     } else {
    	 getData();
    	 $scope.init();
     }
  }
);
