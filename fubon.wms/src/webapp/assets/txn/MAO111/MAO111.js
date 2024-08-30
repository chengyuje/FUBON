'use strict';
eSoafApp.controller('MAO111Controller', function ($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter, $filter, $q, sysInfoService) {
	$controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "MAO111Controller";

    $scope.priID = String(sysInfoService.getPriID());
    $scope.memLoginFlag = String(sysInfoService.getMemLoginFlag());
    
    //組織連動繼承
    $controller('RegionController', {
      $scope: $scope
    });

    //===filter
    getParameter.XML(["CRM.REL_TYPE", "MAO.DEV_SITE_TYPE"], function (totas) {
      if (totas) {
        $scope.mappingSet['CRM.REL_TYPE'] = totas.data[totas.key.indexOf('CRM.REL_TYPE')];
        $scope.mappingSet['MAO.DEV_SITE_TYPE'] = totas.data[totas.key.indexOf('MAO.DEV_SITE_TYPE')];
      }
    });
    //===

    // date picker
    $scope.model = {};
    $scope.open = function ($event, elementOpened) {
      $event.preventDefault();
      $event.stopPropagation();
      $scope.model[elementOpened] = !$scope.model[elementOpened];
    };

    $scope.init = function () {
      $scope.inputVO = {
        cust_id: '',
        cust_name: '',
        branchNbr: '',
        use_date: new Date(),
        ao_list: String(projInfoService.getAoCode()),
        ao_code: projInfoService.getAoCode()[0] ? projInfoService.getAoCode()[0] : "",
        dev_nbr: '',
        use_period: '',
        custString: '',
        emp_id: '',
        dev_site_type: ''
      }

      //組織連動
      $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branchNbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
      $scope.RegionController_setName($scope.region);
    }
    $scope.init();

    $scope.init2 = function () {
      $scope.inputVO.cust_id = '';
      $scope.inputVO.cust_name = '';
      $scope.relationshipList = [];
    }
    $scope.init2();

    //輸出欄初始化
    $scope.inquireInit = function () {
      $scope.dev_save_time = [];
      $scope.resultList = [];
      $scope.canEdit = true;
      $scope.dev_nbr = null;
    }
    $scope.inquireInit();
    
  //取得AO_CODE對應的員工編號
    $scope.get_emp_id = function () {
    	var defer = $q.defer();
      $scope.sendRecv("MAO111", "get_emp_id", "com.systex.jbranch.app.server.fps.mao111.MAO111InputVO", $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
        	  $scope.inputVO.emp_id = tota[0].body;
          }
          defer.resolve("success");
        });
      return defer.promise;
    };

    //查詢
    $scope.inquire = function () {
    	
      $scope.get_emp_id().then(function(){
          $scope.sendRecv("MAO111", "inquire", "com.systex.jbranch.app.server.fps.mao111.MAO111InputVO", $scope.inputVO,
        	        function (tota, isError) {
        	          if (!isError) {
        	            if (tota[0].body.resultList.length == 0) {
        	              $scope.showMsg("查無可申請之載具，請先於設備管理功能中新增設備");
        	              return;
        	            }
        	            $scope.resultList = tota[0].body.resultList;
        	            return;
        	          }
        	        });
      })
    	
    };

    //add_page
    $scope.add_page = function () {
      $scope.sendRecv("MAO111", "add_query", "com.systex.jbranch.app.server.fps.mao111.MAO111InputVO", $scope.inputVO,
        function (tota, isError) {
          if (!isError) {
            if (tota[0].body.addList.length == 0) {
              $scope.showMsg("非轄下客戶不可申請");
              return;
            }

            var dialog = ngDialog.open({
              template: 'assets/txn/MAO111/MAO111_add.html',
              className: 'MAO111_add',
              showClose: false,
              controller: ['$scope', function ($scope) {
                $scope.addList = tota[0].body.addList;
                $scope.outputVO = tota[0].body;
              }]

            });
            dialog.closePromise.then(function (list) {
              if (list.value === 'cancel') {
                return;
              } else {

                // old select 2017/4/7
                $scope.canEdit = true;
                $scope.oldsel = tota[0].body.resultList.filter(function (obj) {
                  return (obj.CUST_TYPE == 'M');
                }).map(function (e) {
                  return e.CUST_ID;
                });
                if ($scope.relationshipList.length == 0) {
                  // 主戶最多六個 2017/4/7
                  // 2017/5/4 REL_TYPE空的也丟主戶
                  var howmm = $scope.oldsel.length;
                  angular.forEach(list.value, function (row) {
                    if (row.REL_TYPE == '00' || !row.REL_TYPE) {
                      if ($scope.oldsel.indexOf(row.CUST_ID) == -1)
                        howmm++;
                      $scope.relationshipList.push(row);
                    } else
                      $scope.relationshipList.push(row);
                  });
                  if (howmm > 6) {
                    $scope.showErrorMsg("ehl_01_MAO111_001");
                    $scope.canEdit = false;
                  }
                } else {
                  // 主戶最多六個 2017/4/7
                  // 2017/5/4 REL_TYPE空的也丟主戶
                  var howmm = $scope.oldsel.length + $scope.relationshipList.filter(function (obj) {
                    return ((obj.REL_TYPE == '00' || !obj.REL_TYPE) && $scope.oldsel.indexOf(obj.CUST_ID) == -1);
                  }).length;
                  angular.forEach(list.value, function (row) {
                    // 已經選了
                    var temp = $scope.relationshipList.map(function (e) {
                      return e.CUST_ID;
                    }).indexOf(row.CUST_ID) > -1;
                    if (row.REL_TYPE == '00' || !row.REL_TYPE) {
                      if (!temp && $scope.oldsel.indexOf(row.CUST_ID) == -1)
                        howmm++;
                      if (!temp)
                        $scope.relationshipList.push(row);
                    } else {
                      if (!temp)
                        $scope.relationshipList.push(row);
                    }
                  });
                  if (howmm > 6) {
                    $scope.showErrorMsg("ehl_01_MAO111_001");
                    $scope.canEdit = false;
                  }
                }
              }
            });
          }
        });
    };

    $scope.deletelist = function (row) {
      var index = $scope.relationshipList.indexOf(row);
      $scope.relationshipList.splice(index, 1);
      // check 2017/4/10
      // 2017/5/4 REL_TYPE空的也丟主戶
      var howmm = $scope.oldsel.length + $scope.relationshipList.filter(function (obj) {
        return ((obj.REL_TYPE == '00' || !obj.REL_TYPE) && $scope.oldsel.indexOf(obj.CUST_ID) == -1);
      }).length;
      if (howmm > 6)
        $scope.canEdit = false;
      else
        $scope.canEdit = true;
    }

    var dev_check = 0;
    $scope.dev_nbr = null;
    $scope.check = function (row, time) {
      if (row['TITLE_' + time] == 'Y') {
        if ($scope.dev_nbr == null) {
          $scope.dev_nbr = row.DEV_NBR;
        }
        $scope.dev_save_time.push(time);
        dev_check++;
      } else {
        $scope.removeElementInArray($scope.dev_save_time, time);
        dev_check--;
        if (dev_check == 0) {
          $scope.dev_nbr = null;
          $scope.dev_save_time = [];
        }
      }

    }

    //刪除陣列中的數字
    $scope.removeElementInArray = function (array, element) {
      array.splice(array.indexOf(element), 1);
    }

    //呼叫後端驗證申請的方法
    $scope.applicationMethod = function () {
      $scope.sendRecv("MAO111", "application", "com.systex.jbranch.app.server.fps.mao111.MAO111InputVO", $scope.inputVO,
        function (totas, isError) {
          if (isError) {
            $scope.showErrorMsgInDialog(totas.body.msgData);
            return;
          }
          if (totas.length > 0) {
            var done = function () {
              $scope.showMsg('申請成功');
              $scope.inquireInit();
              $scope.inquire();
              $scope.inputVO.cust_id = '';
              $scope.inputVO.cust_name = '';
            };

            if (!!$scope.inputVO.custList.length)
              showFpsValidAlert(totas[0].body, done);
            else
              done();
          }
        });
    }

    $scope.application = function () {
    
      $scope.inputVO.dev_nbr = $scope.dev_nbr;
      $scope.inputVO.useTimeList = $scope.dev_save_time;
      $scope.inputVO.relationshipList = $scope.relationshipList;

      //使用時間由小至大排序
      $scope.inputVO.useTimeList.sort(function (a, b) {
        return a - b
      });

      //判斷申請時段是否連續
      var temp = null;
      var use_time = $scope.inputVO.useTimeList;

      for (var i = 0; i < use_time.length; i++) {
        if (i == 0) {
          temp = use_time[i];
        } else {
          if (use_time[i] != temp) {
            $scope.showMsg('載具借用申請需為連續時段，非連續時段請分批申請');
            return;
          }
        }

        temp++;
      }

      // 2017/5/9 add
      $scope.inputVO.custString = $scope.relationshipList.map(function (e) {
        if (e.REL_TYPE == '00' || !e.REL_TYPE) {
          if (e.JOIN_SRV_CUST_ID)
            return e.CUST_ID + ":" + e.CUST_NAME + "(主戶)";
          else
            return e.CUST_ID + ":" + e.CUST_NAME;
        } else
          return e.CUST_ID + ":" + e.CUST_NAME + "(" + $filter('mapping')(e.REL_TYPE, $scope.mappingSet['CRM.REL_TYPE']) + "-主戶" + e.JOIN_SRV_CUST_ID + ")";
      }).toString();
      // 檢核關係戶用主戶
      // 2017/5/4 REL_TYPE空的也丟主戶
      $scope.inputVO.custList = $scope.relationshipList.filter(function (obj) {
        return (obj.REL_TYPE == '00' || !obj.REL_TYPE);
      }).map(function (e) {
        return e.CUST_ID;
      });
      $scope.inputVO.custList2 = $scope.relationshipList.filter(function (obj) {
        return (!(obj.REL_TYPE == '00' || !obj.REL_TYPE));
      }).map(function (e) {
        return e.CUST_ID;
      });

      if ($scope.relationshipList.length == 0) {
        $confirm({
          title: "提醒視窗",
          text: '本次載具借用，未申請查詢客戶基本資料'
        }, {
          size: 'sm'
        }).then(function () {
          $scope.applicationMethod();
        });
      } else {
        $scope.applicationMethod();
      }
    };

 // 檢核 kyc日期, 推介同意, email, 特殊客戶
    var showFpsValidAlert = function (valids, callback) {
      valids.forEach(function (valid) {
        var str = [];
        
        if (!!valid.kycInvalid) {
        	str.push('客戶KYC已失效，請客戶重新執行KYC。(有效KYC及推介註記，才可進行理財規劃唷!)  ');
        	
        } else if (!!valid.kycNearlyInvalid){
        	var d = $scope.toJsDate(valid.kycDueDate);
            str.push('客戶KYC' + (valid.kycDueDate && d ? ('將於' + d.yyyy/MM/dd()) : '') + '失效，提醒客戶可於網銀或行銀執行KYC。(有效KYC及推介註記，才可進行理財規劃唷!)  ');
        }
        
        if (valid.signAgmtInvalid){
        	str.push('客戶符合推介資格但尚未簽署推介同意書，提醒客戶可於網銀填寫推介同意書。(有效KYC及推介註記，才可進行理財規劃唷!)  ');        	
        }
        if (valid.recInvalid){
        	str.push('客戶未能簽署推介同意書，無法進行理財規劃。  ');        	
        }
        if (valid.emailInvalid)
          str.push('客戶無有效email，提醒客戶可於網銀或行銀留存email。  ');
        if (valid.rsInvalid || valid.nsInvalid) {
          var tmp = [];
          if (valid.rsInvalid) tmp.push('拒銷戶');
          if (valid.nsInvalid) tmp.push('禁銷戶');
          str.push('為' + tmp.join('、') + '：客戶為' + tmp.join('、') + '，於外訪時不開放理財規劃功能。  ');
        }
        if (valid.complainInvalid)
          str.push('提醒您，客戶為曾經客訴之客戶。');
        if (str.length > 0) {
          $scope.showMsg('[' + valid.custID + ']' + str.join(''));
        }
      });

      callback();
    };
  });
