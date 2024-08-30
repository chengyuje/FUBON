/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('INS430Controller',
  function ($rootScope, $scope, $controller, $confirm, sysInfoService, socketService, ngDialog, projInfoService, getParameter, $timeout, $filter, $q) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "INS430Controller";

    getParameter.XML(["FPS.INS_AVERAGE", "PRD.PAY_TYPE", "INS.UNIT"], function (totas) {
      if (totas) {
        $scope.INS_AVERAGE_LIFE_EXPECTANCY = totas.data[totas.key.indexOf('FPS.INS_AVERAGE')];
        $scope.PAY_TYPE = totas.data[totas.key.indexOf('PRD.PAY_TYPE')];
        $scope.UNIT = totas.data[totas.key.indexOf('INS.UNIT')];
      }
    });

    var dragOptions = {
      showTip: true,
      showLabel: true,
      showBtn: true,
    };
    $scope.ageDragOption = angular.copy(dragOptions);
    $scope.ageDragOption.label = {
      front: '0歲',
      end: '100歲',
      unit: '歲'
    };
    $scope.targetDragOption = angular.copy(dragOptions);
    $scope.targetDragOption.label = {
      front: '0年',
      end: '30年',
      unit: '年'
    };
    $scope.amtDragOption = angular.copy(dragOptions);
    $scope.amtDragOption.label = {
      front: '0元',
      end: '1000萬元',
      unit: '%'
    };
    $scope.retireDragOption = angular.copy(dragOptions);
    $scope.retireDragOption.label = {
      front: '0歲',
      end: '65歲',
      unit: '歲'
    };
    $scope.monthlyDragOption = angular.copy(dragOptions);
    $scope.monthlyDragOption.label = {
      front: '0元',
      end: '100萬元',
      unit: '%'
    };

    $scope.init = function () {
      $rootScope.retiredYears = true;
      $rootScope.retiredMoney = true;
      $rootScope.year = true;
      $rootScope.needMoney = true;
      $rootScope.btnSuggest = true;
      $scope.changeSug = true;
      $scope.pensionUseYears = '';
      $scope.monLivExp = '';
      $scope.planYears = '';
      $scope.amount = '';
      $scope.inputVO = {
        custID: $scope.connector('get', 'INS400_cust_id'),
        paraType: '', // 參數類型
        currCD: '', // 幣別
        custRiskATR: $scope.custData.CUST_RISK_ATR, // 客戶風險屬性
        status: '', // 規劃狀態
        sppType: '', // 特定規劃項目
        sppName: '', // 規劃名稱
        dAMT1: 0, // 每月生活所需費用(退休規劃)
        dAMT2: 0, // 需求金額(子女教育)
        avgLife: 0, // 平均餘命
        avgRetire: 0, // 預計退休年齡(退休規劃)
        laborINSAMT1: 0, // 社會保險給付(每月)
        laborINSAMT2: 0, // 社會保險給付(一次)
        person1: 0, // 社會福利給付(每月)
        person2: 0, // 社會福利給付(一次)
        othINSAMT1: 0, // 商業保險給付(每月)
        othINSAMT2: 0, // 商業保險給付(一次)
        onceTotal: 0, // 其他給付(每月)
        othAMT2: 0, // 其他給付(一次)
        insprdID: '', // 險種代碼
        payYear: '', // 繳費年期
        policyAssureAMT: 0, // 保額
        unit: '', // 單位
        policyFEE: 0, // 保費
        dYear: 0, // 需求年數
        have: 0 // 已備資金
      };

      switch ($scope.hisPlan) {
        case "子女教育":
          $scope.inputVO.sppType = 1;
          break;
        case "退休規劃":
          $scope.inputVO.sppType = 2;
          break;
        case "購屋":
          $scope.inputVO.sppType = 3;
          break;
        case "購車":
          $scope.inputVO.sppType = 4;
          break;
        case "結婚":
          $scope.inputVO.sppType = 5;
          break;
        case "留遊學":
          $scope.inputVO.sppType = 6;
          break;
        case "旅遊":
          $scope.inputVO.sppType = 7;
          break;
        case "其他":
          $scope.inputVO.sppType = 8;
          break;
      }
      $scope.inputVO.insuredType = 'same';
      $scope.insuredType();
    }

    if ($scope.hisPlan == "退休規劃") {
      $scope.insPlanURL = 'assets/txn/INS430/INS431.html';
      $scope.insAdvURL = 'assets/txn/INS430/INS440.html';
      $rootScope.retiredNeedMoney = true;
    } else {
      $scope.insPlanURL = 'assets/txn/INS430/INS431.html';
      $scope.insAdvURL = 'assets/txn/INS430/INS450.html';
    }

    // 每月給付-小計
    $scope.sumMonth = function () {
      $scope.totalMonth = parseInt($scope.inputVO.laborINSAMT1 || 0) + parseInt($scope.inputVO.person1 || 0) + parseInt($scope.inputVO.othINSAMT1 || 0) + parseInt($scope.inputVO.onceTotal || 0);
      return $scope.totalMonth;
    };

    // 一次給付-小計
    $scope.sumOneTime = function () {
      $scope.totalOneTime = parseInt($scope.inputVO.laborINSAMT2 || 0) + parseInt($scope.inputVO.person2 || 0) + parseInt($scope.inputVO.othINSAMT2 || 0) + parseInt($scope.inputVO.othAMT2 || 0);
      return $scope.totalOneTime;
    };

    $scope.next = function () {
      //			需求金額 = 每月生活所需生活費用 * 12 * (平均餘命 - 預計退休年齡)
      //			已備金額 = 一次給付 + [ 每月給付 * 12 * (平均餘命 - 預計退休年齡)
      //			缺口 = 已備金額 – 需求金額
      var once = 0;
      var mon = 0;
      var avgAge = 0;

      if ($scope.inputVO.gender == '1') {
        var need = parseInt($scope.monLivExp * 12 * ($scope.INS_AVERAGE_LIFE_EXPECTANCY[0].LABEL - $scope.inputVO.retAge));
        avgAge = parseInt($scope.INS_AVERAGE_LIFE_EXPECTANCY[0].LABEL - $scope.inputVO.retAge, 10);
        once = parseInt($scope.inputVO.laborINSAMT2 || 0) + parseInt($scope.inputVO.person2 || 0) + parseInt($scope.inputVO.othINSAMT2 || 0) + parseInt($scope.inputVO.othAMT2 || 0);
        mon = (parseInt($scope.inputVO.laborINSAMT1 || 0) + parseInt($scope.inputVO.person1 || 0) + parseInt($scope.inputVO.othINSAMT1 || 0) + parseInt($scope.inputVO.onceTotal || 0)) * 12 * parseInt($scope.INS_AVERAGE_LIFE_EXPECTANCY[0].LABEL - $scope.inputVO.retAge);
      } else {
        var need = parseInt($scope.monLivExp * 12 * ($scope.INS_AVERAGE_LIFE_EXPECTANCY[1].LABEL - $scope.inputVO.retAge));
        avgAge = parseInt($scope.INS_AVERAGE_LIFE_EXPECTANCY[1].LABEL - $scope.inputVO.retAge, 10);
        once = parseInt($scope.inputVO.laborINSAMT2 || 0) + parseInt($scope.inputVO.person2 || 0) + parseInt($scope.inputVO.othINSAMT2 || 0) + parseInt($scope.inputVO.othAMT2 || 0);
        mon = (parseInt($scope.inputVO.laborINSAMT1 || 0) + parseInt($scope.inputVO.person1 || 0) + parseInt($scope.inputVO.othINSAMT1 || 0) + parseInt($scope.inputVO.onceTotal || 0)) * 12 * parseInt($scope.INS_AVERAGE_LIFE_EXPECTANCY[1].LABEL - $scope.inputVO.retAge);
      }
      // 已備金額 : have    需求金額 : need

      var have = once + mon;
      $scope.inputVO.have = have;
      //缺口
      $scope.gap = need - have <= 0 ? 0 : need - have
      //平均每月已備退休金  {一次給付/(平均餘命-預計退休年齡)/12} + 每月給付
      $scope.retiredM = (parseInt($scope.totalOneTime, 10) / avgAge / 12) + parseInt($scope.totalMonth, 10);
      $scope.retiredGAP = parseInt($scope.inputVO.monLivExp, 10) - parseInt($scope.retiredM, 10);
      //每月費用缺口
      $scope.gapPerMonth = Math.round($scope.gap / $scope.pensionUseYears / 12);

      if (!isNaN($scope.gap)) {
        //        console.log($scope.gap);
        $scope.insAdvURL = 'assets/txn/INS430/INS450.html';
      } else {
        $scope.showErrorMsg('ehl_01_INS430_001'); //請輸入退休年齡及每月所需生活費用！
        return;
      }
      // 頁面切換後查詢可投保商品
      $scope.getINS();
    }

    $scope.previous = function () {
      $scope.clearItem();
      $scope.insAdvURL = 'assets/txn/INS430/INS440.html';
//      $scope.add('N', undefined);

    }

    $scope.demand = function () {
      $scope.changeSug = false;
      if ($scope.inputVO.years == undefined || $scope.inputVO.amount == undefined) {
        $scope.showErrorMsg('ehl_01_common_022'); //欄位檢核錯誤：*為必要輸入欄位,請輸入後重試
        return;
      }
      $rootScope.btnSuggest = false; //顯示INS450.html
      $scope.years = $scope.inputVO.years;
      $scope.amount = $scope.inputVO.amount;

      // 頁面切換後查詢可投保商品
      $scope.getINS();
    }


    /*列印建議書*/
    $scope.print = function () {
      $scope.inputVO.saveType = 'PRINT';
    }

    //check if sppName has same name
    $scope.sppNameComp = () => {
      var deferred = $q.defer();
      $scope.sendRecv('INS450', 'checkName', 'com.systex.jbranch.app.server.fps.ins450.INS450InputVO', {
          custID: $scope.inputVO.custID,
          sppName: $scope.inputVO.sppName
        },
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body > 0) {
              deferred.reject(false);
            } else {
              deferred.resolve(true);
            }
            return true;
          }
          deferred.reject(false);
        });
      return deferred.promise;
    }

    /*儲存*/
    $scope.save = function (type) {
      var defer = $q.defer();
      var error = function () {
        $timeout(function () {
          defer.reject(false);
        });
        return defer.promise;
      }
      if ($scope.inputVO.sppName == '') {
        $scope.showErrorMsg('ehl_01_INS430_002'); //請輸入特定目的規畫名稱
        return error();
      } else if ($scope.inputVO.CHOICE == undefined || $scope.inputVO.CHOICE == null) {
        $scope.showErrorMsg('ehl_01_INS430_003'); //請勾選一項保險商品
        return error();
      } else if ($scope.inputVO.payYear == undefined || $scope.inputVO.payYear == '') {
        $scope.showErrorMsg('ehl_01_INS430_004'); //請選擇繳費年期
        return error();
      }

      $scope.sppNameComp()
        .then(function () {
          // 平均餘命
          if ($scope.inputVO.gender == '1') {
            $scope.inputVO.avgLife = $scope.INS_AVERAGE_LIFE_EXPECTANCY[0].LABEL;
          } else {
            $scope.inputVO.avgLife = $scope.INS_AVERAGE_LIFE_EXPECTANCY[1].LABEL;
          }

          if ($scope.inputVO.sppType == 2) {
            $scope.inputVO.dAMT1 = $scope.inputVO.monLivExp; // 每月所需生活費用
            $scope.inputVO.avgRetire = $scope.inputVO.retAge; // 退休年齡
            $scope.inputVO.othAMT1 = $scope.inputVO.onceTotal;
          } else {
            $scope.inputVO.dAMT2 = $scope.inputVO.amount; // 需求金額
            $scope.inputVO.dYear = $scope.inputVO.years; // 所需年期
          }

          var prdRow = $scope.paramList.filter(function (row) {
            return row.INSPRD_ID == $scope.inputVO.CHOICE;
          });
          $scope.inputVO.policyAssureAMT = prdRow[0].PRICE || 0;
          $scope.inputVO.policyFEE = prdRow[0].policyFee2 || 0;
          if ($scope.inputVO.policyAssureAMT == '' || 0) {
            $scope.showErrorMsg('ehl_01_INS430_005'); //請輸入保費
            return error();
          }
          //        debugger;
          $scope.inputVO.insprdKEYNO = $scope.inputVO.keyno || $scope.inputVO.insprdKEYNO || $scope.inputVO.KEY_NO;
          $scope.inputVO.saveType = type;
          //        debugger;
          $scope.sendRecv("INS450", "action", "com.systex.jbranch.app.server.fps.ins450.INS450InputVO", $scope.inputVO,
            function (tota, isError) {
              if (!isError) {
                //            	console.log(tota[0].body);
                if (type == 'SAVE') {
                  $scope.pagechose(2);
                }
                $scope.showMsg('ehl_01_common_001'); // 新增成功
                $scope.inputVO.sppID = tota[0].body;
                defer.resolve(true);
                return true;
              } else {
                $scope.showErrorMsg('ehl_01_common_008'); // 新增失敗
              }

              defer.reject(false);
            });
        }, function (err) {
          $scope.showErrorMsg('ehl_01_INS431_003'); //特定規劃目的名稱不可重複
          defer.reject(false);
        });
      return defer.promise;
    };
    // 去建議書
    $scope.go = function (where, type) {
      if (where == 'PRINT') {
        $scope.chgStep(3);
        $scope.previewPrint = !$scope.previewPrint;
        if ($scope.previewPrint) {
          $scope.save('PRINT')
            .then(function () {
              // success
              $scope.$broadcast('print_template');
              $scope.printPDF();
              $scope.setPrint(true);
            }, function () {
              // error
            });
        }

        $scope.connector('set', 'INS430PrintSEQ', -1);
      } else if (where == 'generatePDF') {
        //              var printSeq = $scope.connector('get', 'INS430PrintSEQ');
        //              if (printSeq != -1) {
        // 相同時間已經印過
        html2Pdf.getPdf(['*[print]'], 'blob', 'testPdf', '');
        //                return true;
        //              }
      }
    };
    $scope.printPDF = function () {

      $scope.sendRecv('INS450', 'checker', 'com.systex.jbranch.app.server.fps.ins450.INS450InputVO', {
          sppID: $scope.inputVO.sppID,
          custID: $scope.inputVO.custID,
          checkType: 'PRINT'
        },
        function (tota, isError) {
          if (!isError) {

            //            	  console.log(tota[0].body[0]);
            $scope.dataList = {
              RETIREDGAP: $scope.retiredGAP, //缺口
              HAVE: Math.round($scope.retiredM) || 0, //已備金額 
              PRINTLIST: tota[0].body[0], // 基本內容
              REPORT: tota[0].body[1] //birt報表
            }
            //            	  console.log($scope.dataList);
            $rootScope.$broadcast('INSprintJS', $scope.dataList);
          }
        });
    }

    // 選擇商品
    $scope.add = function (type, row) {
      debugger
      if (type) {
        console.log($scope.inputVO);
        console.log($scope.inputVO.insprdID);
        $scope.inputVO.insprdID = type;
        $scope.inputVO.policyAssureAMT = row.PRICE;
        $scope.inputVO.policyFEE = row.POLICY_FEE;
        $scope.inputVO.unit = row.PRD_UNIT;
        $scope.inputVO.currCD = row.CURR_CD;
      } else {
        if (row != undefined) {
          row.INSPRD_ANNUAL = '';
          row.POLICY_FEE = '';
          row.PRICE = '';
          row.policyFee2 = '';
        }
        $scope.inputVO.insprdID = '';
        $scope.inputVO.payYear = '';
        $scope.inputVO.policyAssureAMT = 0;
        $scope.row.policyFee2 = '';

        $scope.inputVO.unit = '';
        $scope.inputVO.currCD = '';
      }
      if (row.insprd_annualList.length === 1) {
        //    	  debugger;
        $scope.inputVO.payYear = row.insprd_annualList[0].DATA;
        $scope.inputVO.insprdKEYNO = row.insprd_annualList[0].KEY_NO;
        $scope.getRefRate(row);
      } else {
        $scope.inputVO.payYear = row.INSPRD_ANNUAL;
      }
      clear();
    }
    var clear = function () {
    	debugger;
    	if($scope.paramList) {
	      $scope.paramList.forEach(function (item) {
	        if ($scope.inputVO.CHOICE != item.INSPRD_ID) {
	          if (item.annual.length > 2) {
	            item.INSPRD_ANNUAL = '';
	          }
	          item.PRICE = 0;
	          item.POLICY_FEE = '';
	          item.policyFee2 = '';
	        }
	      });
	    }
    };
    $scope.clearItem = function () {
      $scope.inputVO.CHOICE = '';
      clear();
    }

    $scope.set = function (type, row, map) {
      if (type == 'ANNUAL' && row.INSPRD_ANNUAL != '') {
        $scope.inputVO.insprdID = row.INSPRD_ID;
        if ($scope.hisPlan == '退休規劃') {
          $scope.inputVO.gap = $scope.gap;
        } else {
          $scope.inputVO.gap = $scope.amount;
        }

        $scope.inputVO.payYear = row.INSPRD_ANNUAL;
        //				row.PRICE = $scope.inputVO.policyAssureAMT;
        $scope.getPolicyFee(row)
          .then(function (success) {
            if (row.PRD_UNIT == '4') {
              row.PRICE = calInitInsAmt(row) / 10000;
            } else if (row.PRD_UNIT == '3') {
              row.PRICE = calInitInsAmt(row) / 1000;
            } else if (row.PRD_UNIT == '2') {
              row.PRICE = calInitInsAmt(row) / 100;
            } else {
              row.PRICE = calInitInsAmt(row);
            }
            row.POLICY_FEE = calInsFee(row);
          }, function (error) {
            row.PRICE = 0;
          });
      } else if (type == 'POLICY_AMT') {
        $scope.inputVO.insprdID = row.INSPRD_ID;
        $scope.inputVO.policyAssureAMT = row.PRICE;
        row.PRICE = calInsAmt(row);
        row.POLICY_FEE = calInsFee(row);
      }
    };
    //    //		計算保額級距
    //    var calInitInsAmt = function (row) {
    //      var result = 0;
    //      var amount = parseInt($scope.amount, 10) / row.ratio || 0;
    //      var gap = parseInt($scope.gap, 10) / row.ratio || 0;
    //      if ($scope.gap == undefined) {
    //        result = Math.round(amount / row.distanceAmt) * row.distanceAmt + row.minAmt;
    //      } else {
    //        result = Math.round(gap / row.distanceAmt) * row.distanceAmt + row.minAmt;
    //      }
    //      if (result > row.maxAmt) {
    //        result = row.maxAmt;
    //      } else if (result < row.minAmt) {
    //        result = row.minAmt;
    //      }
    //      return result;
    //    }
    //    //		保額最大最小區間
    //    var calInsAmt = function (row) {
    //      console.log(row);
    //      var result = 0;
    //      var price = parseInt(row.PRICE, 10);
    //      if (price < row.minAmt) {
    //        result = row.minAmt;
    //      } else if (price > row.maxAmt) {
    //        result = row.maxAmt;
    //      } else {
    //        result = row.minAmt + Math.ceil((price - row.minAmt) / row.distanceAmt) * row.distanceAmt;
    //      }
    //      return result;
    //    };
    //		計算保費
    //    var calInsFee = function (row) {
    //      console.log(row);
    //      var result = 0;
    //      var feePrice = parseInt(row.POLICY_FEE || 0, 10);
    //      var price = parseFloat(row.PRICE);
    //      if (feePrice > 0) {
    //        result = feePrice * price || 0;
    //      }
    //      return result;
    //
    //    };

    $scope.getPolicyFee = function (row) {
      var defer = $q.defer();
      $scope.inputVO.keyno = row.map[row.INSPRD_ANNUAL].keyNo;
      $scope.inputVO.cvrgRatio = row.map[row.INSPRD_ANNUAL].cvrgRatio;
      //			$scope.inputVO.paraNO = row.PARA_NO;
      $scope.sendRecv("INS450", "getPolicyFEE", "com.systex.jbranch.app.server.fps.ins450.INS450InputVO", $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.outputList.length > 0) {
              var output = tota[0].body.outputList[0];
              row.minAmt = parseInt(output.POLICY_AMT_MIN, 10) || 0;
              row.maxAmt = parseInt(output.POLICY_AMT_MAX, 10) || 0;
              row.distanceAmt = parseInt(output.POLICY_AMT_DISTANCE, 10) || 0;
              row.ratio = parseInt(output.CVRG_RATIO, 10) || 0;
            }
            $scope.inputVO.policyFEE = tota[0].body.policyFEE || 0;
            row.POLICY_FEE = parseInt(tota[0].body.policyFEE, 10) || 0;

            defer.resolve(true);
            return true;
          }
          defer.reject(false);
        });
      return defer.promise;
    }

    //規劃目標年期bar(mantis:0004329 30年)
    $scope.changeYears = function () {
      if ($scope.inputVO.years < 0) {
        $scope.inputVO.years = '';
        $scope.showErrorMsg("目標規劃年期不可為負數")
      }
      $scope.yearsPct = parseInt($scope.inputVO.years, 10) / 30 * 100 || 0;
      $rootScope.btnSuggest = true;
      $scope.changeSug = true;
      $scope.clearItem();
    }

    //需求金額bar(mantis:0004329  一千萬)
    $scope.changeAmount = function () {
      $scope.amountPct = (parseInt($scope.inputVO.amount, 10) / 10000000 * 100).toFixed(2) || 0;
      $rootScope.btnSuggest = true;
      $scope.changeSug = true;
      $scope.clearItem();
    }

    //退休年紀bar
    $scope.changeRetAge = function () {
      if ($scope.inputVO.retAge < 0) {
        $scope.inputVO.retAge = '';
        $scope.showErrorMsg("預計退休年齡不可為負數")
      }
      $scope.retPct = parseInt($scope.inputVO.retAge, 10) / $scope.INS_AVERAGE_LIFE_EXPECTANCY[2].LABEL * 100 || 0;
    }
    
	//計算保險年齡
	$scope.count_age = function(){
		var birthday = new Date($scope.custData.BIRTH_DATE).getTime();
			$scope.sendRecv("INS200","countAge","com.systex.jbranch.app.server.fps.ins200.INS200InputVO",
					{BIRTHDAY : birthday},function(tota,isError){
						if(!isError){
							$scope.inputVO.insAge = tota[0].body.Age;
						}
			});
	}

    //每月所需生活費用bar(mantis:0004329  一百萬)
    $scope.changeMonLivExp = function () {
      $scope.monPct = (parseInt($scope.inputVO.monLivExp, 10) / 1000000 * 100).toFixed(2) || 0;
      $scope.previous();
    }

    $scope.blurAge = function () {
      $scope.ageFlag = false;
      $scope.pensionUseYears = '';
      if ($scope.inputVO.age != '' && $scope.inputVO.age != null &&
        $scope.inputVO.retAge != '' && $scope.inputVO.retAge != null) {

        if ($scope.inputVO.age > $scope.inputVO.retAge) {
          $scope.showErrorMsg('ehl_01_INS432_001'); //預計退休年齡不得低於實際年齡
          $scope.ageFlag = false;
          $scope.inputVO.monLivExp = 0;
          $rootScope.retiredNeedMoney = true;
          $scope.changeSug = true;
          $scope.previous();
          return;
        } else if ($scope.inputVO.retAge > parseInt($scope.inputVO.gender == '1' ? $scope.INS_AVERAGE_LIFE_EXPECTANCY[0].LABEL : $scope.INS_AVERAGE_LIFE_EXPECTANCY[1].LABEL)) {
          $scope.showErrorMsg('ehl_01_INS432_002'); //預計退休年齡不得大於於平均餘命
          $scope.ageFlag = false;
          $scope.inputVO.monLivExp = 0;
          $rootScope.retiredNeedMoney = true;
          $scope.changeSug = true;
          $scope.previous();
        } else {
          if ($scope.inputVO.gender == '1') {
            $scope.pensionUseYears = $scope.INS_AVERAGE_LIFE_EXPECTANCY[0].LABEL - $scope.inputVO.retAge;
          } else {
            $scope.pensionUseYears = $scope.INS_AVERAGE_LIFE_EXPECTANCY[1].LABEL - $scope.inputVO.retAge;
          }
          $scope.ageFlag = true;
          $rootScope.retiredYears = false;
        }
      }
    }

    $scope.blurMonLivExp = function () {
      $scope.monLivExp = '';
      if ($scope.inputVO.monLivExp != '' && $scope.inputVO.monLivExp != null) {
        $rootScope.retiredMoney = false;
        $rootScope.retiredNeedMoney = false; //顯示INS440.html
        $rootScope.btnSuggest = false;
        $scope.monLivExp = $scope.inputVO.monLivExp;
        $scope.changeSug = false;
      }
    }

    $scope.blurYears = function () {
      $scope.planYears = '';
      if ($scope.inputVO.years != '' && $scope.inputVO.years != null) {
        $rootScope.year = false;
        $scope.planYears = $scope.inputVO.years;
      }
    }

    $scope.blurAmount = function () {
      $scope.amount = '';
      if ($scope.inputVO.amount != '' && $scope.inputVO.amount != null) {
        $scope.amount = $scope.inputVO.amount;
        $rootScope.needMoney = false;

      }
    }

    $scope.insuredType = function () {
      if ($scope.inputVO.insuredType == 'same') {
        if ($scope.custData.AGE != null) {
          $scope.inputVO.age = $scope.custData.AGE;
          $scope.count_age();
        }
        if ($scope.custData.GENDER != null) {
          $scope.inputVO.gender = $scope.custData.GENDER;
        }
      }
    }

    $scope.init();

    $scope.getINS = function (type, estate) {
      if ($scope.inputVO.sppType == 1) {
        $scope.inputVO.prdSpp = 6;
      } else if ($scope.inputVO.sppType == 2) {
        $scope.inputVO.prdSpp = 5;
      } else {
        $scope.inputVO.prdSpp = 7;
      }
      $scope.sendRecv("INS810", "getSuggestPrd", "com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO", {
          paraType: $scope.inputVO.prdSpp,
          currCD: type,
          insAge: $scope.inputVO.insAge,
          estate: estate

        },
        function (tota, isError) {
          if (!isError) {
            console.log(tota[0].body.suggestPrdList);
            $scope.paramList = tota[0].body.suggestPrdList;
            angular.forEach($scope.paramList, function (row, index) {
              row.map = {};
              row.INSPRD_ID = row.PRD_ID;
              row.cvrgRatio = row.CVRG_RATIO.split(',');
              //								.map(function(item){ return { LABEL: item, DATA: item}})
              row.keyNo = row.KEY_NO.split(',');
              //								.map(function(item){ return { LABEL: item, DATA: item}});
              row.annual = row.INSPRD_ANNUAL.split(',')
                .map(function (item, _index) {
                  row.map[item] = {
                    annual: item,
                    cvrgRatio: row.cvrgRatio[_index],
                    keyNo: row.keyNo[_index]
                  }
                  return {
                    LABEL: item,
                    DATA: item
                  }
                });
              if (row.annual.length > 1) {
                row.INSPRD_ANNUAL = '';
              }
            })
          }
        });
    }

    // 保額計算
    $scope.getAssureAmt = function (row) {
      if (!row.INSPRD_ANNUAL) {
        $scope.showErrorMsg("請先選擇 繳費年期");
        row.PRICE = undefined;
        return;
      }
      
      if (row.PRICE.toString()) {
        console.log(row.PRICE);
        // 轉換成數值
        var assureAmt = Number(row.PRICE);
        if (row.insprd_annualList.length > 1) {
          var POLICY_AMT_MIN = Number(row.policyAmtMin2);
          var POLICY_AMT_MAX = Number(row.policyAmtMax2);
          var POLICY_AMT_DISTANCE = Number(row.policyAmtDistance2);
        } else {
          var POLICY_AMT_MIN = Number(row.POLICY_AMT_MIN);
          var POLICY_AMT_MAX = Number(row.POLICY_AMT_MAX);
          var POLICY_AMT_DISTANCE = Number(row.POLICY_AMT_DISTANCE);
        }
        if (assureAmt <= POLICY_AMT_MIN)
          row.PRICE = POLICY_AMT_MIN.toFixed(2);
        else if (assureAmt >= POLICY_AMT_MAX)
          row.PRICE = POLICY_AMT_MAX.toFixed(2);
        else
          row.PRICE = (POLICY_AMT_MIN + (Math.ceil((assureAmt - POLICY_AMT_MIN) / POLICY_AMT_DISTANCE) * POLICY_AMT_DISTANCE)).toFixed(2);

        if (row.rate2) {
          row.policyFee2 = row.PRICE * row['rate2'];
        }

      }
    }

    // 取得費率
    $scope.getRefRate = function (row) {
      if (row.insprd_annualList.length > 1) {
        if (row.INSPRD_ANNUAL) {
          var exIndex = row.insprd_annualList.map(function (e) {
            return e.DATA;
          }).indexOf(row.INSPRD_ANNUAL);
          var obj = row.insprd_annualList[exIndex];
          row.cvrgRatio2 = obj.CVRG_RATIO;
          row.keyNo2 = obj.KEY_NO;
          // 保額相關
          row.policyAmtMin2 = obj.POLICY_AMT_MIN; // 保額下限
          row.policyAmtMax2 = obj.POLICY_AMT_MAX; // 保額上限
          row.policyAmtDistance2 = obj.POLICY_AMT_DISTANCE; // 保額累加級距
        }
      } else {
        var POLICY_AMT_MIN = Number(row.POLICY_AMT_MIN);
        var POLICY_AMT_MAX = Number(row.POLICY_AMT_MAX);
        var POLICY_AMT_DISTANCE = Number(row.POLICY_AMT_DISTANCE);

      }
      var policySuggestInputVO = {
        insPrdId: row.PRD_ID,
        annual: row.INSPRD_ANNUAL,
        currCD: row.CURR_CD,
        age: $scope.inputVO.insAge,
        gender: $scope.inputVO.gender
      };
      $scope.inputVO.KEY_NO = row.keyNo2;
      $scope.inputVO.payYear = policySuggestInputVO.annual;
      if (!(policySuggestInputVO.insPrdId && policySuggestInputVO.annual && policySuggestInputVO.currCD &&
          policySuggestInputVO.age && policySuggestInputVO.gender)) {
        row['ref2'] = undefined;
        row['rate2'] = undefined;
        row.policyFee2 = 0; //年繳表定保費
        return;
      }
      //		console.log(policySuggestInputVO);


      $scope.sendRecv("INS810", "getPremAndExchangeRate", "com.systex.jbranch.app.server.fps.ins810.PolicySuggestInputVO",
        policySuggestInputVO,
        function (totas, isError) {
          if (!isError) {
            row['rate2'] = totas[0].body.premRate; // 費率 
            if (row.PRICE) {
              row.policyFee2 = row.PRICE * row['rate2'];
            }
          }
        });
    }

    // 子女教育HELP Dialog
    $scope.TO_INS431_CHILDREN = function () {
      var dialog = ngDialog.open({
        template: 'assets/txn/INS430/INS431_CHILDREN.html',
        className: 'INS431',
        showClose: false,
        scope: $scope,
        controller: ['$scope', function ($scope) {

        }]
      }).closePromise.then(function (data) {
        if (data.value != 'cancel') {
          $scope.amount = data.value;
          $scope.inputVO.amount = data.value;
          $scope.changeAmount();
          $rootScope.needMoney = false;
        }
      });
    }
    
    $scope.insDetail = function (row) {
      if (row.INSPRD_ANNUAL == '' || row.INSPRD_ANNUAL == undefined) {
        $scope.showErrorMsg('ehl_01_INS450_001'); //請選擇繳費年期
        return;
      }

      var id = $scope.inputVO.custID;
      var temp = $scope.PAY_TYPE;
      var dialog = ngDialog.open({
        template: 'assets/txn/PRD160/PRD160_DETAIL.html',
        className: 'PRD160_DETAIL',
        showClose: false,
        controller: ['$scope', function ($scope) {
          if (row.annual.length > 2) {
            $scope.row = angular.copy(row);
            $scope.row.KEY_NO = row.map[row.INSPRD_ANNUAL].keyNo;
            $scope.cust_id = id;
            $scope.PAY_TYPE = temp;

          } else {
            $scope.row = row;
            $scope.cust_id = id;
            $scope.PAY_TYPE = temp;
          }
        }]
      });
    };

    $scope.decomma = function (number) {
      return parseInt(number.toString().replace(/\,/g, ''), 10);
    }
    $scope.chgStep(2);
  });
