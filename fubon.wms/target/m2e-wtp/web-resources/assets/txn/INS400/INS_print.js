/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('INSPrintController',
  function ($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $timeout, $http) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'INSPrintController';
    getParameter.XML(["CRM.CRM239_CONTRACT_STATUS", "INS.UNIT"], function (totas) {
      if (totas) {
        $scope.contractStatus = totas.data[totas.key.indexOf('CRM.CRM239_CONTRACT_STATUS')];
        $scope.UNIT = totas.data[totas.key.indexOf('INS.UNIT')];
      }
    });

    $scope.inputVO.downloadBtn = false;

    $scope.init = function () {
      // app
      if (typeof (webViewParamObj) != 'undefined') {
    	  $scope.inputVO.isComeFromApp = true;

        $scope.outputList = {};
        $scope.outputList.CUST_ID = $scope.appCUST_ID;
        $scope.outputList.SPP_ID = $scope.appSPP_ID;

        $scope.sendRecv('INS450', 'checker', 'com.systex.jbranch.app.server.fps.ins450.INS450InputVO', {
            sppID: $scope.outputList.SPP_ID,
            custID: $scope.outputList.CUST_ID,
            checkType: 'APP_PRINT'
          },
          function (tota, isError) {
            if (!isError) {
              var custInfo = tota[0].body[2];
              if (custInfo) {
                $scope.custData = {};
                $scope.custData["CUST_NAME"] = custInfo.CUST_NAME;
                $scope.custData["AGE"] = custInfo.AGE;
                $scope.hisPlan = custInfo.SPPTYPE;
                $scope.retiredGAP = custInfo.GAP;
                $scope.retiredM = custInfo.AVG_P;
              }
              $scope.dataList = {
                RETIREDGAP: $scope.retiredGAP, //缺口
                HAVE: Math.round($scope.retiredM) || 0, //已備金額 
                PRINTLIST: tota[0].body[0], // 基本內容
                REPORT: tota[0].body[1] //birt報表
              }
              doPDFProcess($scope.dataList);
            }
          });



        //    		$scope.custData = {};
        //    		$scope.custData["CUST_NAME"] = '小沈沈';
        //    		$scope.custData["AGE"] = '26';
        //    		$scope.hisPlan = '我要更多錢';
        //    		
        //    		// 是不是退休規劃
        //    		if($scope.hisPlan == '退休規劃') {
        //    		} else {
        //    			$scope.outputList = {};
        //    			$scope.outputList["D_YEAR"] = '5';
        //    			$scope.outputList["D_AMT2"] = 7000000
        //    			
        //    		}

      }
      // 不是 app
      else {
        $scope.inputVO.isComeFromApp = false;
      }
    }
    $scope.init();
    $rootScope.$on('INSprintJS', function (event, data) {
      doPDFProcess(data);
    });



    var doPDFProcess = function (data) {
      $scope.outputList = data.PRINTLIST[0] || {};
      $scope.outputList.have = data.HAVE;
      $scope.outputList.retiredGAP = data.RETIREDGAP;
      $scope.paramList = data.REPORT;

      $scope.insprdId430 = data.PRINTLIST[0] ? data.PRINTLIST[0].INSPRD_ID : undefined;
      $scope.policyFee430 = data.PRINTLIST[0] ? data.PRINTLIST[0].POLICY_FEE : undefined;
      $scope.custName430 = $scope.custData.CUST_NAME;
      $scope.custBirthday430 = $scope.custData.BIRTH_DATE;


      console.log(data.REPORT);
      console.log(data.PRINTLIST);
      console.log(data.HAVE);
      console.log(data.RETIREDGAP);

      var insDoughut = new CanvasJS.Chart("p_retiredChart", {
        colorSet: 'tri',
        backgroundColor: 'transparent',
        data: [{
          type: "pie",
          startAngle: 60,
          indexLabelFontSize: 17,
          indexLabel: "{label} - #percent%",
          toolTipContent: "<b>{label}:</b> {y} (#percent%)",
          dataPoints: [{
              y: $scope.outputList.have / $scope.outputList.D_AMT1,
              label: "已準備退休規劃"
            },
            {
              y: $scope.outputList.retiredGAP / $scope.outputList.D_AMT1,
              label: "退休規劃缺口"
            }
          ]
        }]
      });
      insDoughut.render();
      $timeout(() => {
        $scope.go('generatePDF', 'download');
      })
    }

    // generatePDF
    $scope.go = function (where, type) {
      console.log(where);
      if (where == 'generatePDF') {
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
            pdfName: '保險規劃書.pdf',
          })
          .then(function (data) {
            uploadPdf($scope.outputList.CUST_ID + $scope.outputList.SPP_ID + '.pdf', data);
            $scope.chgStep(3);
            dialog.close();
          })
          .catch(function (e) {
            console.log(e);
            dialog.close();
          });
      }
    };

    var getBase64PDF = function (file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject('error');
      });

    }

    // upload pdf
    var uploadPdf = function (fileName, data) {
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

      var isError = null;
      // 方法三
      getBase64PDF(file).then(
        result => {
          $scope.sendRecv('FPS200', 'genPDFSafariUplodFile', 'java.util.Map', {
              'base64': result,
              'tempFileName': tempFileName
            },
            function (tota, isError) {
              debugger
              if (!isError) {
                generatePdfWithEncryption(tempFileName, fileName, data);
              }
            });
        }
      );



      //      if(typeof(webViewParamObj) != 'undefined') {
      //    	  
      //      } else {
      //    	  
      //      }

      // 方法二 失敗
      //      var request = $http({	method: 'POST', 
      //			url: 'FileUpload',
      //			data: formData,
      //			headers: {
      //				'Content-Type': undefined
      //			}
      //		});
      //debugger
      //      request.then(
      //    		  function(oRep) {
      //    			  if(oRep.status === 200) {
      //    				  generatePdfWithEncryption(tempFileName, fileName, data);
      //    			  }
      //    		  },function(oErr) {
      //    			  console.log(error);
      //    		  }    
      //      );
      // 方法一 失敗
      //      $http.post('FileUpload', formData, {
      //          transformRequest: angular.identity,
      //          headers: {
      //            'Content-Type': undefined
      //          }
      //        })
      //        .then(function (jqXHR) {
      //          if (jqXHR.status === 200) {
      //            generatePdfWithEncryption(tempFileName, fileName, data);
      //          }
      //        }, function (error) {
      //          console.log(error);
      //        });
    };



    var generatePdfWithEncryption = function (tempFileName, fileName, data) {
      console.log(tempFileName);
      console.log(fileName);

      var inputObject = {}

      inputObject = {
			  sppID: $scope.outputList.SPP_ID,
	          custID: $scope.outputList.CUST_ID,
	          tempFileName: tempFileName,
	          fileName: fileName,
	          printSEQ: typeof(webViewParamObj) != 'undefined' ? -1 : $scope.connector('get', 'INS430PrintSEQ')  
	  }

      $scope.sendRecv('INS450', 'generatePdf', 'com.systex.jbranch.app.server.fps.ins450.INS450InputVO', inputObject,
        function (tota, isError) {
          $scope.inputVO.printSEQ = tota[0].body;
        });
    };

    //download
    $scope.downloadPDF = function () {
      if (typeof (webViewParamObj) != 'undefined') {
        $scope.go('generatePDF', null);
      } else {
        $scope.inputVO.downloadBtn = true;
        console.log($scope.hisPlan);
        console.log($scope.outputList);
        console.log($scope.inputVO);
        $scope.sendRecv("INS450", "downloadPdf", "com.systex.jbranch.app.server.fps.ins450.INS450InputVO", {
            sppID: $scope.outputList.SPP_ID,
            printSEQ: $scope.inputVO.printSEQ,
            sppType: $scope.hisPlan,
            sppName: $scope.outputList.SPP_NAME
          },
          function (totas, isError) {
            if (!isError) {
              //            		$scope.outputList = totas[0].body.outputList;
              console.log(totas);
            };
          }
        );
      }
    };

    /*我要投保*/
    $scope.insured = function () {
      var CUST_ID = $scope.inputVO.cust_id;
      var CUST_NAME = $scope.custName430;
      var BIRTHDAY = $scope.custBirthday430;
      var PRD_ID = $scope.insprdId430;
      var POLICYFEE = $scope.policyFee430;
      var dialog = ngDialog.open({
        template: 'assets/txn/INS/INS_IOT.html',
        className: 'INS_SOT',
        controller: ['$scope', function ($scope) {
          $scope.FROM450_CUST_ID = CUST_ID;
          $scope.FROM450_CUST_NAME = CUST_NAME;
          $scope.FROM450_BIRTHDAY = BIRTHDAY;
          $scope.FROM450_PRD_ID = PRD_ID;
          $scope.FROM450_POLICYFEE = POLICYFEE;
          $scope.from450 = true;
        }]
      });
      dialog.closePromise.then(function (data) {
        // 關掉就關掉拉
      });
    }

    /* main progress */
    console.log('INSPrintController');
    $scope.$on('print_template', function (event, data) {
      $timeout(function () {
        init();

      });
    });
  }
);
