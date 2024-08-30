var webViewParamObj = null;

'use strict';
eSoafApp.config(['$locationProvider', function ($locationProvider) {
  $locationProvider.html5Mode(true);
}]);

//外部參數
function doSetParameter(data) {
	debugger;
	console.log(data);
  webViewParamObj.setParam(data);
  webViewParamObj.formatWebViewParam();
  webViewParamObj.setParamReady(true);
  webViewParamObj.isShowContent();
}

function doGetResult() {
  console.log(webViewParamObj.getResult());
  return webViewParamObj.getResult();
}

/**
 * get dynamic script
 * @param {string|array} urls
 * @param {fn} callback
 */
function doGetScript(urls, done) {
  if (typeof url === 'string') {
    urls = [url];
  }
  if (typeof done !== 'function') {
    done = function () {};
  }
  var cnt = urls.length;
  urls.forEach(function (url) {
    var s = document.createElement('script');
    s.src = url;
    s.onload = function () {
      console.log('url: <' + url + '> loaded.');
      cnt--;
      if (cnt === 0) {
        done();
      }
    };
    document.body.appendChild(s);
  });
}

eSoafApp.controller('webviewController', ['$controller', 'getParameter', '$q', "$scope", "$rootScope", "$http", "sysInfoService", '$location',
  function ($controller, getParameter, $q, $scope, $rootScope, $http, sysInfoService, $location) {
    function getWebViewParamInstance($scope) {
      var param = null;
      var scope = $scope;
      var result = null;
      var isControllerReady = false;
      var isParamReady = false;

      scope.isShowContent = false;

      return {
        setParam: function (val) {
          param = val;
        },
        getParam: function () {
          return param;
        },
        getScope: function () {
          return scope;
        },
        setResult: function (val) {
          result = val;
        },
        getResult: function () {
          return result;
        },
        isShowContent: function () {
          scope.isShowContent = isControllerReady && (isNoParam || isParamReady);
        },
        setParamReady: function (val) {
          isParamReady = val;
        },
        setControllerReady: function (val) {
          isControllerReady = val;
        },
        formatWebViewParam: formatWebViewParam,
        formatWebViewResParam: formatWebViewResParam,
        cancel: function () {
          this.setResult();
        }
      };
    }

    function refreshAuthorities(authorities) {
      var ans = {};
      ans["MODULEID"] = {};

      angular.forEach(authorities, function (row, index, objs) {
        ans.MODULEID[row.MODULEID] = {};
        ans.MODULEID[row.MODULEID]["ITEMID"] = {};
      });

      angular.forEach(authorities, function (row, index, objs) {
        ans.MODULEID[row.MODULEID].ITEMID[row.ITEMID] = {};
        ans.MODULEID[row.MODULEID].ITEMID[row.ITEMID]["FUNCTIONID"] = {};
      });

      angular.forEach(authorities, function (row, index, objs) {
        ans.MODULEID[row.MODULEID].ITEMID[row.ITEMID].FUNCTIONID[row.FUNCTIONID] = true;
      });

      return ans;
    }

    function loadWatermark() {
      var deferred = $q.defer();

      getParameter.XML('WATERMARK', function (totas) {
        if (totas) {
          sysInfoService.setWATERMARK(totas.data[0]);
          deferred.resolve(sysInfoService.getWATERMARK());
        }
      });

      return deferred.promise;
    }

    // load i18n
    function i18nRemoteLoad(language, keys) {
      var deferred = $q.defer();
      $scope.sendRecv("I18N", "inquireAll", "com.systex.jbranch.app.server.fps.i18n.I18NInputVO", {
          'locale': language
        },
        function (totas, isError) {
          if (isError) {
            $scope.showErrorMsg(totas[0].body.msgData);
            deferred.reject(keys);
          }
          if (totas.length > 0) {
            angular.forEach(totas[0].body.resultList,
              function (row, index, objs) {
                keys[row.CODE] = row.TEXT;
              });
            sysInfoService.setI18N(keys);
            deferred.resolve(sysInfoService.getI18N());
          };
        }
      );
      return deferred.promise;
    }

    function loadSysInfo(oResp) {
      var output = oResp.data.result.body;

      sysInfoService.setApServerName(output.apServerName);
      sysInfoService.setUserID(output.loginID);
      sysInfoService.setUserName(output.loginName);
      sysInfoService.setApplicationID(output.ApplicationID);
      sysInfoService.setBranchID(output.loginBrh);
      sysInfoService.setAreaID(output.loginArea);
      sysInfoService.setRegionID(output.loginRegion);
      sysInfoService.setBranchName(output.loginBrhName);
      sysInfoService.setWsID(output.wsId);
      sysInfoService.setRoleID(output.loginRole);
      sysInfoService.setPriID(output.priID);
      sysInfoService.setRoleName(output.loginRoleName);
      sysInfoService.setAvailBranch(output.availBranchList);
      sysInfoService.setAvailRegion(output.availRegionList);
      sysInfoService.setAvailArea(output.availAreaList);
      sysInfoService.setUser(output.userInfo);
      sysInfoService.setAoCode(output.userInfo.AoCode);
      sysInfoService.setLoginSourceToken(output.loginSourceToken);
      sysInfoService.setUserList(output.userList);
      sysInfoService.setAuthorities(refreshAuthorities(output.authorities));
      sysInfoService.setIsUHRM(output.isUHRM);
      sysInfoService.setMemLoginFlag(output.memLoginFlag);

      $rootScope.ApplicationID = oResp.data.ApplicationID;
      $rootScope.BranchID = oResp.data.BranchID;
      $rootScope.TlrID = oResp.data.TlrID;
      $rootScope.WsID = oResp.data.WsID;

      loadWatermark()
        .then(function () {
          var language = 'zh-tw';
          var keys = {
            'pf_sessionmanager_error_001': '工作站逾時',
            'pf_sessionmanager_error_002': '重複登入',
            'pf_sessionmanager_error_003': '系統異常',
            'pf_sessionmanager_error_004': 'Server關機登出',
            'pf_sessionmanager_error_005': '工作站已登出',
            'pf_sessionmanager_error_006': '營業日期已被改變',
            'pf_sessionmanager_error_007': '請重新登入',
            'ehl_01_cmfpg000_008': '連線失敗:{0}-{1}'
          };
          return i18nRemoteLoad(language, keys);
        }).then(function () {
          webViewParamObj.setControllerReady(true);
          webViewParamObj.isShowContent();
        });
    }

    webViewParamObj = getWebViewParamInstance($scope);

    $controller('BaseController', {
      $scope: $scope
    });
    $scope.controllerName = "webviewController";

    $rootScope.menuItemInfo = {
      url: mpUrl
    };

    var url = 'reSysInfo/' + $location.path().replace(/\//, "").replace(/.vw/, "") + '.serv';
    var uuid = $location.search().uuid;
    var request = $http({
      method: 'POST',
      url: url,
      data: {
        'uuid': uuid
      },
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      timeout: 10000
    });

    request.then(loadSysInfo, function (oErr) {
      alert(JSON.stringify(oErr));
      return oErr.data.message;
    });

    $scope.initPageData = function (data) {
      doSetParameter(data);
    }

    $scope.getResult = function () {
      alert(JSON.stringify(doGetResult()));
    }

  }
]);
