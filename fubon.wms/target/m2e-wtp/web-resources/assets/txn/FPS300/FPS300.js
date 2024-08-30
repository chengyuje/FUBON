/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('FPS300_Controller',
  function ($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService, getParameter, $q, $filter) {
    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = 'FPS300_Controller';

    var signText = {
      valid: '提醒您：此客戶尚未填寫推介同意書。建議請客戶填寫完成後，本行即可提供完整之商品投資標的規劃。若客戶未簽署推介同意書，則本行所提供之規劃文件將不包含個別商品標的。是否列印推介同意書?',
      notValid: '提醒您，此客戶無法簽署推介同意書，本行所提供之規劃文件將不包含個別商品標的。是否繼續規劃?'
    };

    /* parameter */
    $scope.getXML = ['KYC.EDUCATION'];
    getParameter.XML($scope.getXML, function (totas) {
      if (len(totas) > 0) {
        var tmp = totas;
        $scope.kycEducation = tmp.data[tmp.key.indexOf('KYC.EDUCATION')];
      }
    });
    var empID = sysInfoService.getUserID();
    var pathMap = {
      search: 'assets/txn/FPS/FPS_SEARCH.html'
    };
    $scope.arrowPath = {
      up: 'assets/images/ic-up.svg',
      down: 'assets/images/ic-down.svg'
    };
    $scope.arrowUp = true;

    //已命名來辨識
    $scope.fps300 = {
      recommendations: null, // 組合建議資料
      currentProgress: null, // 當前進度
      custData: null, // 客戶資料
      mapping: {}, // xml參數
      searchBar: null, // 客戶快查link
      tabBarUrl: null, // step1 ~ 4流程控件
      inputVoName: null, // inputVo的名稱
      contentUrl: null, //下方content link
      tempCustId: null, // 暫存用客戶id，用於判斷離開客戶ID焦點時是否有異動客戶id
      inputVO: null, // 傳遞物件IN
      outputVO: null, // 傳遞物件OUT
      isHistoryShow: null, // 歷史規劃開關
      isTrackShow: null, // 績效追蹤與投組調整開關
      planID: null, // 計畫ID
      tab: null,
      step350: null,
      _DATA: {}, // for set get once
      stored: {}, // for mapping or static data
      rate: [], // currency rate

      //get & set 組合建議資料
      setRecommendations: function (recommendations) {
        this.recommendations = recommendations;
      },
      getRecommendations: function () {
        return this.recommendations || {};
      },

      //get & set 當前進度
      setCurrentProgress: function (currentProgress) {
        this.currentProgress = currentProgress;
      },
      getCurrentProgress: function () {
        return this.currentProgress || {};
      },

      //get & set 績效追蹤上一頁判斷
      setIsOld: function (isOld) {
        this.isOld = isOld;
      },
      getIsOld: function () {
        return this.isOld || false;
      },

      //get & set 客戶基本資料
      getCustData: function () {
        return this.custData;
      },
      setCustData: function (val) {
        this.custData = val;
      },

      //get & set 計畫ID
      getPlanID: function () {
        return this.planID;
      },
      setPlanID: function (val) {
        this.planID = val;
      },

      //get & set 客戶風險屬性等級
      getVOL_RISK_ATTR: function () {
        return this.VOL_RISK_ATTR;
      },
      setVOL_RISK_ATTR: function (val) {
        this.VOL_RISK_ATTR = val;
      },

      //get & set 客戶風險屬性等級
      getRISK_ATTR: function () {
        return this.RISK_ATTR;
      },
      setRISK_ATTR: function (val) {
        this.RISK_ATTR = val;
      },

      //get & set 市場概況
      getMarketOverview: function () {
        return this.marketOverview;
      },
      setMarketOverview: function (val) {
        this.marketOverview = val;
      },

      //get & set 投組明細
      getParamList: function () {
        return this.paramList;
      },
      setParamList: function (val) {
        this.paramList = val;
      },

      //get & set 投組明細初始資料
      getParamListOrg: function () {
        return this.paramListOrg;
      },
      setParamListOrg: function (val) {
        this.paramListOrg = val;
      },

      //get & set 是否鎖住投組名稱
      getDisBeacon: function () {
        return this.disBeacon;
      },
      setDisBeacon: function (val) {
        this.disBeacon = val;
      },

      //get & set 規劃日期
      getPlanDate: function () {
        return this.planDate;
      },
      setPlanDate: function (val) {
        this.planDate = val;
      },

      //get & set 預計到期日期
      getOverDate: function () {
        return this.overDate;
      },
      setOverDate: function (val) {
        this.overDate = val;
      },

      //PAGE路徑
      path: {
        url: 'assets/txn/FPS300/FPS300.html',
        SEARCH_BAR: 'assets/txn/FPS300/FPS300_SEARCH_BAR.html',
        TAB2: 'assets/txn/FPS300/FPS300_TAB2.html',
        fps310: {
          url: 'assets/txn/FPS310/FPS310.html',
          COMFIRM: 'assets/txn/FPS310/FPS310_COMFIRM.html'
        },
        fps324: {
          url: 'assets/txn/FPS324/FPS324.html',
        },
        fps340: {
          url: 'assets/txn/FPS340/FPS340.html',
        },
        fps350: {
          url: 'assets/txn/FPS350/FPS350.html',
        },
        fps400: {
          url: 'assets/txn/FPS400/FPS400.html'
        },
        fps410: {
          url: 'assets/txn/FPS410/FPS410.html'
        },
        fps411: {
          url: 'assets/txn/FPS411/FPS411.html',
          COMFIRM: 'assets/txn/FPS411/FPS411_COMFIRM.html'
        },
        fps412: {
          url: 'assets/txn/FPS412/FPS412.html'
        },
        fps410Edit: {
          url: 'assets/txn/FPS410/FPS410_EDIT.html'
        },
        fps410Preview: {
          url: 'assets/txn/FPS410/FPS410_PREVIEW.html'
        }
      },

      // Tag Step Map
      TSMap: {
        '1': {
          1: '1',
          2: 'FPS324',
          3: ''
        },
        '2': {
          1: '2',
          2: 'FPS410',
          3: 'FPS410Edit',
          4: 'FPS410Preview'
        }
      },

      // Mapping Map
      mappingMap: {
        'KYC.EDUCATION': 'kycEducation',
        'CRM.CUST_EDUCATIONAL_BACKGROUND': 'educationCif',
        'CUST.RISK_ATTR': 'riskAttr',
        'FPS.PLANNING': 'planning',
        'FPS.PROD_TYPE': 'pType',
        'FPS.PARAM_STATUS': 'paramStatus',
        'SYS.COMMON_YN': 'commonYN',
        'PRD.PRD_RR': 'pRiskType',
        'FPS.PARAM_PTYPE': 'paramPtype',
        'PRD.PRD_STATUS': 'prdStatus',
        'FPS.INV_PRD_TYPE': 'invPrdType',
        'FPS.RISK_ATTR_MAPPING': 'riskAttrMapping',
        'PRD.CURRENCY': 'cur',
        'FPS.BUY_CURRENCY': 'cmbCur',
        'FPS.TXN_TYPE': 'TxnType',
        'FPS.INV_TYPE': 'invType', // 單筆 定額
        'FPS.REPORT_ALERT': 'reportAlert',
        'SOT.NF_MIN_BUY_AMT_1': 'invType1Limit',
        'SOT.NF_MIN_BUY_AMT_2': 'invType2Limit'
      },

      getXmlParam: function (arr) {
        var fps300 = this;
        var req = [];
        var deferred = $q.defer();
        arr.forEach(function (item) {
          if (!fps300.mapping[fps300.mappingMap[item]]) {
            req.push(item);
          }
        });
        getParameter.XML(req, function (totas) {
          if (totas) {
            req.forEach(function (item) {
              fps300.mapping[fps300.mappingMap[item]] = totas.data[totas.key.indexOf(item)] || [];
            });
            deferred.resolve(fps300.mapping);
          } else {
            deferred.reject(false);
          }
        });

        return deferred.promise;
      },

      //FPS300初始化
      init: function () {
        this.searchBar = this.path.SEARCH_BAR; //上方客戶快查、查詢結果區塊
        this.tabBarUrl = this.path.TAB2; //下方特定目的理財規劃流程tab
        this.inputVoName = 'com.systex.jbranch.app.server.fps.fps300.FPS300InputVO'; //inputVo完整路徑
        this.contentUrl = this.path.fps310.url; //下方content
        this.tempCustId = '';
        this.chgTab();
        this.inputVO = {};
        this.outputVO = {};
        this.chgStep(1);
        this.isHistoryShow = false;
        this.isTrackShow = false;
        this.planID = '';
        this.tab = '1';
      },

      //初始化參數
      initXmlParam: function () {
        var mapping = this.mapping;
        var fps300 = this;
        //共用參數
        fps300.getXmlParam(['CRM.CUST_EDUCATIONAL_BACKGROUND', 'CUST.RISK_ATTR', 'FPS.PLANNING', 'KYC.EDUCATION'])
          .then(function (mapping) {
            fps300.mapping = mapping;
          }, function (err) {
            console.log(err);
          });
      },

      showTab: function (num) {
        this.tab = num;
        //                this['tab' + num].tab('show');
        $scope.ACTIVE = Number(num) - 1;
      },

      //setp1 ~ setp4控制
      chgStep: function (flag) {
        var subStepNum = flag;
        for (var i = 0; i < 4; i++) {
          this['isChangeStep' + (i + 1)] = subStepNum > i;
        }
      },

      //切換content
      chgTab: function (name, args) {
        var fps300 = this;
        var chgTabInit = function () {
          fps300.setRecommendations(null);
          fps300.setCurrentProgress(null);
          fps300.chgStep(1);
          fps300.setPlanID(null);
        };
        switch (name) {
          case 'FPS324':
            this.changeToFps324(args);
            this.showTab('1');
            break;
          case 'ReturnFPS324':
            this.changeToReturnFPS324();
            this.showTab('1');
            break;
          case 'FPS340':
            this.changeToFps340(args);
            this.showTab('1');
            break;
          case 'ReturnFPS400':
            this.changeToReturnFPS400(args);
            this.showTab('2');
            break;
          case 'FPS410':
            this.changeToFps410(args);
            this.showTab('2');
            break;
          case 'ReturnFPS410':
            this.changeToReturnFPS410();
            this.showTab('2');
            break;
          case 'FPS411':
            this.changeToFps411(args);
            this.showTab('2');
            break;
          case 'ReturnFPS411':
            this.changeToReturnFPS411();
            this.showTab('2');
            break;
          case 'FPS412':
            this.changeToFps412(args);
            this.showTab('2');
            break;
          case 'FPS410Edit':
            this.changeToFps410Edit(args);
            this.showTab('2');
            break;
          case 'FPS410Preview':
            this.changeToFps410Preview(args);
            this.showTab('2');
            break;
          case '1':
            this.contentUrl = this.path.fps310.url;
            chgTabInit();
            fps300.step350 = args;
            if (fps300.step350 != null) {
              fps300.getStep(); //績效追蹤與投組調整，歷史規劃 按鈕顯示
            }
            this.showTab('1');
            break;
          case '2':
            this.contentUrl = this.path.fps400.url;
            chgTabInit();
            this.showTab('2');
            fps300.chgStep(0);
            break;
          case '3':
            this.contentUrl = this.path.fps350.url;
            chgTabInit();
            this.showTab('3');
            break;
          default:
            this.contentUrl = this.path.fps310.url;
            chgTabInit();
            this.showTab('1');
            break;
        }
        $("#fps300focusCustId01").focus();
      },

      // check custData & path
      checkValid: function (path) {
        var fps300 = this;
        var custData = this.getCustData();
        if (this.contentUrl == path) {
          return false;
        }
        //客戶基本資料還沒查回來
        else if (custData == undefined) {
          $scope.showMsg('無客戶基本資料');
          return false;
        } else if (custData.kycDisable && fps300.step == '1') {
          $scope.showMsg('客戶無有效KYC，無法進行理財規劃！');
          return false;
        }
        return true;
      },

      // 切換頁面投組現況檢視
      changeToFps410: function (planningCode) {
        var fps300 = this;
        var custData = this.getCustData();
        var path = this.path.fps410.url;
        var recommendations = this.getRecommendations();
        if (planningCode == undefined && recommendations.planningCode == undefined) {
          $scope.showMsg('請選擇投資組合建議');
          return false;
        } else if (planningCode != undefined && recommendations.planningCode != planningCode) {
          this.setRecommendations({});
          recommendations = this.getRecommendations();
          recommendations.planningCode = planningCode;
          recommendations.planningName = this.getPlanningName(recommendations.planningCode);
        }
        if (!this.checkValid(path)) {
          return false;
        }

        fps300.chgStep(1);
        fps300.contentUrl = path;
        return;
      },

      // 切換頁面投組調整
      changeToFps411: function (planningCode) {
        var fps300 = this;
        var custData = this.getCustData();
        var path = this.path.fps411.url;
        var recommendations = this.getRecommendations();
        if (planningCode == undefined && recommendations.planningCode == undefined) {
          $scope.showMsg('請選擇投資組合建議');
          return false;
        } else if (planningCode != undefined && recommendations.planningCode != planningCode) {
          this.setRecommendations({});
          recommendations = this.getRecommendations();
          recommendations.planningCode = planningCode;
          recommendations.planningName = this.getPlanningName(recommendations.planningCode);
        }
        if (!this.checkValid(path)) {
          return false;
        }

        this.chgStep(2);
        this.contentUrl = path;
        return;
      },

      // 切換頁面理財規劃書
      changeToFps412: function (planningCode) {
        var fps300 = this;
        var custData = this.getCustData();
        var checkMoney = false;
        $scope.recommendations = this.getRecommendations();
        $scope.paramList = this.getParamList();
        $scope.paramList.forEach(function (row) {
          if (parseFloat(row.CHG_PERCENT) < parseFloat(row.LowMoney)) {
            checkMoney = true;
          }
        });
        var path;
        path = this.path.fps412.url;
        //下一步
        this.chgStep(3);
        this.contentUrl = path;
      },

      // 切換頁面到投組調整
      changeToFps410Edit: function () {
        var fps300 = this;
        var custData = this.getCustData();
        var path = this.path.fps410Edit.url;
        if (!this.checkValid(path)) {
          return false;
        }

        // include 410Edit and step 2 -> 3
        if (custData.AC_FLAG != undefined && custData.AC_FLAG != '9') {
          this.chgStep(3);
          this.contentUrl = path;
          return;
        }
      },

      // 切換頁面到投組調整
      changeToFps410Preview: function () {
        var fps300 = this;
        var custData = this.getCustData();
        var path = this.path.fps410Preview.url;
        if (!this.checkValid(path)) {
          return false;
        }

        // include 410Preview and step 3 -> 4
        if (custData.AC_FLAG != undefined && custData.AC_FLAG != '9') {
          this.chgStep(4);
          this.contentUrl = path;
          return;
        }
      },

      // 切換頁面到確認投資組合建議
      changeToFps324: function (planningCode) {
        var fps300 = this;
        var custData = this.getCustData();
        var path = this.path.fps324.url;
        var recommendations = this.getRecommendations();
        if (planningCode == undefined && recommendations.planningCode == undefined) {
          $scope.showMsg('請選擇投資組合建議');
          return false;
        } else if (planningCode != undefined && recommendations.planningCode != planningCode) {
          this.setRecommendations({});
          recommendations = this.getRecommendations();
          recommendations.planningCode = planningCode;
          recommendations.planningName = this.getPlanningName(recommendations.planningCode);
        }

        if (!this.checkValid(path)) {
          return false;
        }
        // include 324 and step 1 -> 2
        if (custData.CUST_PRO_FLAG !== 'Y' && custData.SIGN_AGMT_YN !== 'Y') {
          $confirm({
            text: custData.REC_YN === 'N' ? signText.notValid : signText.valid
          }, {
            size: 'sm'
          }).then(function () {
            //REC_YN 判斷是否可簽署推介同意書
            if (custData.REC_YN !== 'N') {
              // 產生〝推介同意書〞PDF檔案
              printSignAgmt().then(function () {}, function () {
                $scope.showErrorMsg('無法產生推介同意書');
              });
            }
            fps300.chgStep(2);
            fps300.contentUrl = path;
          }, function () {
            //REC_YN 判斷是否可下一步
            if (custData.REC_YN !== 'N') {
              fps300.chgStep(2);
              fps300.contentUrl = path;
            } else {
              return false;
            }
          });
        } else {
          fps300.chgStep(2);
          fps300.contentUrl = path;
        }
      },


      // 切換頁面到確認投資組合建議(上一步)
      changeToReturnFPS324: function () {
        this.setCurrentProgress("ReturnFPS324");
        var path = this.path.fps324.url;
        if (!this.checkValid(path)) {
          return false;
        }
        //上一步
        this.chgStep(2);
        this.contentUrl = path;
      },

      // 切換頁面到績效追蹤檢視(上一步)
      changeToReturnFPS400: function () {
        this.setCurrentProgress("ReturnFPS400");
        var path = this.path.fps400.url;
        if (!this.checkValid(path)) {
          return false;
        }
        //上一步
        this.chgStep(0);
        this.contentUrl = path;
      },

      // 切換頁面到確認投資組合現況檢視(上一步)
      changeToReturnFPS410: function () {
        this.setCurrentProgress("ReturnFPS410");
        var path = this.path.fps410.url;
        if (!this.checkValid(path)) {
          return false;
        }
        //上一步
        this.chgStep(1);
        this.contentUrl = path;
        this.setIsOld(true);
      },

      // 切換頁面到確認投資組合調整(上一步)
      changeToReturnFPS411: function () {
        this.setCurrentProgress("ReturnFPS411");
        var path = this.path.fps411.url;
        if (!this.checkValid(path)) {
          return false;
        }
        //上一步
        this.chgStep(2);
        this.contentUrl = path;
      },

      // 切換頁面到確認投資組合建議(下一步)
      changeToFps340: function () {
        var checkMoney = false;
        var path;
        path = this.path.fps340.url;
        //下一步
        this.chgStep(3);
        this.contentUrl = path;
      },

      //特定理財規劃link對應
      getPlanningName: function (planningCode) {
        var planningMapping = this.mapping.planning;
        for (var planning in planningMapping) {
          if (planningMapping[planning].DATA == planningCode) {
            return planningMapping[planning].LABEL;
          }
        }
      },

      //查詢客戶資料
      searchCustData: function () {
        var deferred = $q.defer();
        var fps300 = this;

        //CUST_ID轉大寫
        fps300.inputVO.custID = $filter('uppercase')(fps300.inputVO.custID);
        //法人8碼，自然人10碼
        if (fps300.inputVO.custID && fps300.tempCustId != fps300.inputVO.custID && fps300.inputVO.custID.length > 7) {

          $scope.sendRecv('FPS200', 'getCust', 'com.systex.jbranch.app.server.fps.fps200.FPS200InputVO', fps300.inputVO,
            function (tota, isError) {
              if (!isError) {
                if (tota[0].body == undefined || tota[0].body.custInfo == undefined || tota[0].body.custInfo.length == 0) {
                  fps300.clearForQueryCust();
                  $scope.showMsg('ehl_01_common_009');
                  deferred.reject('error');
                  return;
                }
                // kyc
                $scope.toDay = $filter('date')(new Date(), 'yyyy-MM-dd 00:00:00'); //取當日日期
                if (!tota[0].body.custInfo[0].KYC_LEVEL || $scope.toJsDate(tota[0].body.custInfo[0].KYC_DUE_DATE) < $scope.toJsDate($scope.toDay)) {
                  $scope.showErrorMsg('客戶無有效KYC，無法進行理財規劃！');
                  tota[0].body.custInfo[0].kycDisable = true;
                }
                fps300.clearForQueryCust();
                fps300.tempCustId = fps300.inputVO.custID;
                fps300.setCustData(tota[0].body.custInfo[0]);
                $scope.custInfo = tota[0].body.custInfo[0];
                $scope.hasIns = tota[0].body.hasInvest;
                $scope.custInfo.AGE = getAge($scope.custInfo.BDAY_D);
                fps300.topTabChk();
                fps300.getStep(); //績效追蹤與投組調整，歷史規劃 按鈕顯示，導頁用
                deferred.resolve('successful');
              } else {
                fps300.clearForQueryCust();
                $scope.showMsg('ehl_01_common_009');
                deferred.reject('error');
              }
            });
        } else if (!fps300.inputVO.custID) {
          this.clearForQueryCust();
          deferred.reject('error');
        }
        return deferred.promise;
      },

      //初始化頁面
      clearForQueryCust: function () {
        this.chgTab($scope.FROM_SAARV101 ? $scope.FROM_SAARV101.tag : null);
        this.tempCustId = '';
        this.chgStep(1);
        this.setCustData(null);
        this.setRecommendations(null);
        this.isHistoryShow = false;
        this.isTrackShow = false;
        //                this.isTabBarShow = false;
        this.planID = '';
        this.step = '1';
        this.step350 = null;
      },

      //判斷歷程記錄、績效追蹤與投組調整
      topTabChk: function () {
        this.isHistoryShow = true;
        this.isTrackShow = true;
      },

      //績效追蹤與投組調整，歷史規劃 按鈕顯示，導頁用
      getStep: function () {
        var deferred = $q.defer();
        var fps300 = this;
        $scope.sendRecv("FPS300", "getStep", "com.systex.jbranch.app.server.fps.fps300.FPS300InputVO", {
            custId: fps300.inputVO.custID
          },
          function (tota, isError) {
            if (!isError) {
              fps300.step = tota[0].body.step;
              if (fps300.step350 == null) {
                fps300.chgTab(fps300.step || '1');
              }

              deferred.resolve('successful');
            }
          });
        return deferred.promise;
      },

      //複製物件內容
      clone: function (data, fields) {
        if (null == data || 'object' != typeof data)
          return data;
        var copy = data.constructor();
        for (var attr in data) {
          if (data.hasOwnProperty(attr)) copy[attr] = data[attr];
        }
        return copy;
      },

      drawDoughnut: function (paramList, ratioKey) {
        ratioKey = ratioKey || '';
        var temp = {};
        var fps300 = this;
        paramList.forEach(function (row) {
          if (temp[row.MF_MKT_CAT] === undefined) {
            temp[row.MF_MKT_CAT] = {
              y: row[ratioKey],
              legendText: row.NAME + '市場',
              indexLabel: row[ratioKey] + '%'
            };
          } else {
            temp[row.MF_MKT_CAT].y = fps300.floatFormat(temp[row.MF_MKT_CAT].y + row[ratioKey], 2);
            temp[row.MF_MKT_CAT].indexLabel = temp[row.MF_MKT_CAT].y + '%';
          }
        });
        var dataPoints = Object.keys(temp).map(function (key) {
          return temp[key];
        });
        var chartData = {
          legend: {
            fontSize: 16,
            verticalAlign: 'top',
            horizontalAlign: 'right'
          },
          data: [{
            type: 'doughnut',
            showInLegend: true,
            dataPoints: dataPoints
          }]
        };
        return new CanvasJS.Chart('doughnutChart', chartData);
      },
      // set & get once(default)
      _get: function (id, flag) {
        var data = this._DATA[id];
        if (!flag) this._DATA[id] = {};
        return data;
      },

      _set: function (id, val) {
        this._DATA[id] = val;
      },

      floatFormat: function formatFloat(num, pos) {
        var size = Math.pow(10, pos);
        return Math.round(num * size) / size;
      },

      isEmpty: function (value, type) {
        var _temp = value === undefined || value === null;
        if (type === 'string') {
          return _temp || value.trim() === '';
        } else if (type === 'int') {
          return _temp && value !== 0;
        } else {
          return _temp;
        }
      },

      decomma: function (number) {
        return parseInt(number.toString().replace(/\,/, ''), 10);
      },

      groupBy: function (arr, key, fn) {
        var arr = angular.copy(arr);
        var temp = {};
        arr.forEach(function (row, index) {
          if (temp[row[key]] === undefined) {
            temp[row[key]] = fn(row);
          } else {
            temp[row[key]] = fn(row, temp[row[key]]);
          }
        });
        return temp;
      }
    };

    var printSignAgmt = function () {
      var defer = $q.defer();
      $scope.sendRecv('FPS210', 'printSignAgmt', 'com.systex.jbranch.app.server.fps.fps210.FPS210InputVO', {
          custID: $scope.fps300.inputVO.custID
        },
        function (tota, isError) {
          if (!isError) {
            defer.resolve(true);
            return true;
          }
          defer.reject(false);
          return false;
        });
      return defer.promise;
    };

    var getValidEmpAuth = function () {
      var defer = $q.defer();
      defer.resolve(true);
      
      // #6437: 移除理規使用者權限設定
      /*
      $scope.sendRecv('FPS200', 'checkEmpFpsAuth', 'com.systex.jbranch.app.server.fps.fps200.FPS200InputVO', {
          empID: empID
        },
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body) {
              defer.resolve(true);
            } else {
              defer.reject(false);
            }
            return true;
          }
          defer.reject(false);
          return false;
        }
      );  */
      return defer.promise;
    };

    /**
     * noDataReason:
     * cust Detail error find reason
     */
    var noDataReason = function (inputVO) {
      $scope.sendRecv('CRM110', 'inquireCust', 'com.systex.jbranch.app.server.fps.crm110.CRM110InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            var result = tota[0].body.resultList;
            var list = result.length > 0 ? [result[0].BRA_NBR, result[0].BRANCH_NAME, ''] : [];
            if (result.length === 0 || (result.length === 1 && !result[0].BRA_NBR)) {
              //查無資料
              $scope.showErrorMsg('ehl_01_cus130_002', [inputVO.cust_id]); //無此客戶ID：{0}
              return false;
            } else if (result.length == 1 && !result[0].EMP_NAME) {
              //空code客戶
              $scope.showErrorMsg('ehl_01_cus130_006', list); //客戶歸屬：{0}-{1}，不提供客戶首頁查詢。
              return false;
            } else {
              //有歸屬行&所屬理專
              list.push(result[0].EMP_NAME);
              $scope.showErrorMsg('ehl_01_cus130_005', list); //客戶歸屬( {0} {1} ) {2} 理專，不提供客戶首頁查詢。
              return false;
            }
          }
        }
      );
      return false;
    };

    // crm110 inquire cp codes, no idea to extend controller
    var custDetail = function (custID) {
      var defer = $q.defer();
      var inputVO = {
        type: 'ID',
        cust_id: custID
      };
      var role = sysInfoService.getPriID();
      if (Array.isArray(role) && role.length === 1) {
        role = role[0];
      }
      var ao_code = String(sysInfoService.getAoCode());
      var FA = ['014', '015', '023', '024'];
      //消金
      if (role == '004') {
        inputVO.role = 'ps';
      } else if (FA.indexOf(role) >= 0) { //輔銷FA
        inputVO.role = 'faia';
      }
      //理專 : $scope.role == '001' => 是AFC，AFC 應可從快查KEY ID或姓名來查詢同歸屬行內的任一客戶(#0002070)
      else if (ao_code && $scope.role != '001') {
        inputVO.role = 'ao';
        inputVO.ao_code = ao_code;
      } else {
        inputVO.role = 'other';
      }

      $scope.sendRecv('CRM110', 'inquire', 'com.systex.jbranch.app.server.fps.crm110.CRM110InputVO', inputVO,
        function (tota, isError) {
          if (!isError) {
            var result = tota[0].body.resultList;
            if (result.length === 0) { // 查無資料時(查詢為何查無資料)
              defer.reject(noDataReason(inputVO));
            } else {
              defer.resolve({
                result: result,
                inputVO: inputVO,
                body: tota[0].body
              });
            }
            return true;
          }
          defer.reject(tota);
          return false;
        }
      );
      return defer.promise;
    };

    // search data then form fps300 search bar
    $scope.getCust = function () {
      var fps300 = $scope.fps300;
      //CUST_ID轉大寫
      fps300.inputVO.custID = $filter('uppercase')(fps300.inputVO.custID);
      if (fps300.inputVO.custID == '' || fps300.inputVO.custID == undefined) {
        $scope.showMsg('請輸入客戶ID！');
        $scope.init();
        return;
      }

      if (fps300.inputVO.custID === $scope.connector('get', 'custID')) return false;
      // 檢查是否在轄下
      custDetail(fps300.inputVO.custID)
        .then(function () {
          // 撈客戶資料
          fps300.searchCustData();
        }, function (err) {
          $scope.init();
        });
    };

    $scope.go = function (where) {
      switch (where) {
        case 'search':
          openDialog(pathMap[where], null, where);
          break;
        case 'custInfo':
          custDetail($scope.fps300.inputVO.custID)
            .then(function (data) {
                if (data.result.length === 1) { //單筆資料
                  var path = '';
                  $scope.connector('set', 'CRM110_CUST_ID', data.result[0].CUST_ID);
                  $scope.connector('set', 'CRM110_CUST_NAME', data.result[0].CUST_NAME);
                  $scope.connector('set', 'CRM110_AOCODE', data.result[0].AO_CODE);
                  $scope.connector('set', 'CRM_CUSTVO', {
                    CUST_ID: data.result[0].CUST_ID,
                    CUST_NAME: data.result[0].CUST_NAME
                  });

                  if (data.inputVO.role === '004') {
                    //消金首頁
                    path = 'assets/txn/CRM711/CRM711.html';
                  } else {
                    //一般客戶首頁
                    path = 'assets/txn/CRM610/CRM610_MAIN.html';
                  }
                  $scope.connector('set', 'CRM610URL', path);

                  ngDialog.open({
                    template: 'assets/txn/CRM610/CRM610.html',
                    className: 'CRM610',
                    showClose: false
                  });
                  return true;
                } else if (data.result.length > 1) { // 多筆資料
                  ngDialog.open({
                    template: 'assets/txn/CRM110/CRM110_MultiData.html',
                    className: 'CRM110_MultiData',
                    showClose: false,
                    controller: ['$scope', function ($scope) {
                      $scope.row = data.result;
                      $scope.totaBody = data.body;
                      $scope.role = data.inputVO.role;
                      $scope.cust_name = '';
                      $scope.ao_code = data.inputVO.ao_code;
                    }]
                  });
                  return true;
                }
              },
              function (err) {

              });
          break;
      }
    };

    /* sub function */
    var openDialog = function (path, data, type) {
      var scope = $scope;
      var dialog = ngDialog.open({
        template: path,
        className: 'FPS200',
        controller: ['$scope', function ($scope) {
          $scope.data = data;
        }]
      });

      dialog.closePromise.then(function (data) {
        if (typeof (data.value) === 'object') {
          popCbFn[type](data.value);
        }
      });
    };

    var popCbFn = {
      search: function (data) {
        $scope.fps300.inputVO.custID = data.CUST_ID;
        $scope.getCust();
        //        $scope.searchCust();
      }
    };

    //年齡計算
    function getAge(dateString) {
      //        	console.log(dateString);
      var today = new Date();
      var birthDate = new Date(dateString);
      var age = today.getFullYear() - birthDate.getFullYear();
      var m = today.getMonth() - birthDate.getMonth();
      if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
      return age;
    }

    //初始化
    $scope.init = function () {
      var fps300 = $scope.fps300;
      fps300.init();
      fps300.initXmlParam();
      fps300.setCustData(null);
      $scope.custInfo = [];
      $scope.hasIns = false;
      $scope.connector('set', 'custID', undefined);
      
      //由首頁待覆核來
      if ($scope.connector('get','MAO151_PARAMS') != undefined) {
//    	  alert(JSON.stringify($scope.connector('get','MAO151_PARAMS').CUST_ID));
    	  fps300.inputVO.custID = $scope.connector('get','MAO151_PARAMS').CUST_ID;
    	  $scope.connector('set','MAO151_PARAMS', undefined);
      }
      
      //由工作首頁財神爺待執行交易客戶數來
      if ($scope.connector('get', 'CRM121_CUST_PLAN') != undefined) {
    	  fps300.inputVO.custID = $scope.connector('get','CRM121_CUST_PLAN').custID;
    	  $scope.connector('set','CRM121_CUST_PLAN', undefined);
      }
      
      // test
      // fps300.inputVO.custID = 'F101665378';
      // fps300.inputVO.custID = 'C200481206';
      // fps300.inputVO.custID = 'L102435234';
      // fps300.inputVO.custID = 'D120942101';
      // fps300.inputVO.custID = 'A221419977';
      // fps300.inputVO.custID = 'Q120427458';
      // fps300.inputVO.custID = 'A101766731';

      if (projInfoService.fromFPS814 != undefined) {
        projInfoService.fromFPS814 = undefined;
        fps300.inputVO.custID = $scope.connector('get', 'FPS300').custID;
        $scope.connector('set', 'FPS300', undefined);
        fps300.searchCustData().then(function (data) {
          projInfoService.fromFPS814 = true;
          fps300.chgTab('3');
        });
      } else {
        fps300.searchCustData();
      }
    };
    console.log('FPS300');

    getValidEmpAuth().then(
      function () {
        $scope.init();
      },
      function () {
        $scope.showErrorMsg('無此模組權限，請洽系統管理員');
        $scope.GeneratePage({
          txnName: 'HOME',
          txnId: 'HOME',
          txnPath: []
        });
      }
    );
  }
);
