/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CMMGR014_Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR014_Controller";
        
        // combobox
		getParameter.XML(["TBSYSPARAMTYPE.CSV_TYPE"], function(totas) {
			if (totas) {
				$scope.mappingSet['TBSYSPARAMTYPE.CSV_TYPE'] = totas.data[totas.key.indexOf('TBSYSPARAMTYPE.CSV_TYPE')];
			}
		});
        
        $scope.sendRecv("CMMGR014", "init", "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO", {},
				function(tota, isError) {
					if (!isError) {
						$scope.mappingSet['hostId'] = [];
						angular.forEach(tota[0].body.hostIdList, function(row){
		    				$scope.mappingSet['hostId'].push({LABEL: row.IP, DATA:row.HOSTID});
		    			});
						return;
					}
		});
        
        // init
		$scope.init = function(){
			$scope.inputVO = {
				tipFtpSettingId: '',
    			cmbHostId: '',
    			tipSrcDirectory: '',
    			tipSrcFileName: '',
    			tipDesDirectory: '',
    			tipDesFileName: '',
    			tipCheckFile: '',
    			tipRepeat:undefined,
    			tipRepeatInterval: undefined
			};
		};
		$scope.init();
		$scope.inquireInit = function(){
			$scope.paramList = [];
		}
		$scope.inquireInit();
        
        $scope.inquire = function(){
	    	  $scope.sendRecv("CMMGR014", "inquire", "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {
	                    	  $scope.paramList = tota[0].body.dataList;
	                    	  $scope.outputVO = tota[0].body;
	                          return;
	                      }
	                  });
	      }
        
	      $scope.edit = function (row) {
	    	  console.log('edit data='+JSON.stringify(row));
              var dialog = ngDialog.open({
            	  template: 'assets/txn/CMMGR014/CMMGR014_EDIT.html',
            	  className: 'CMMGR014',
            	  showClose: false,
                  controller: ['$scope', function($scope) {
                	  $scope.row = row;
                  }]
              });
              dialog.closePromise.then(function (data) {
                  if(data.value === 'successful'){
                	  $scope.inquireInit();
                	  $scope.inquire();
                  }
              });
          };
          
          
    }
);
