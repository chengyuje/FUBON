/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG250Controller', function(sysInfoService, $scope, $controller, $confirm, $compile, socketService, ngDialog, projInfoService, getParameter) {
	$controller('BaseController', { $scope : $scope });
	$scope.controllerName = "ORG250Controller";
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["COMMON.REVIEW_STATUS"], function(totas) {
		if (totas) {
			$scope.mappingSet['COMMON.REVIEW_STATUS'] = totas.data[totas.key.indexOf('COMMON.REVIEW_STATUS')];
		}
	});
    //
	
	$scope.init = function() {
		$scope.agentLst            = [];

		$scope.delInputVO = {
			EMP_ID         : ''
		};
		
		$scope.data = [];
		
		$scope.inputVO = {
			SEQNO              : '',
			region_center_id   : '',
			branch_area_id     : '',
			branch_nbr         : '',
			EMP_ID             : '',
			EMP_NAME           : '',
			AGENT_ID_1         : '',
			AGENT_ID_2         : '',
			AGENT_ID_3         : '',
			REVIEW_STATUS	   : '',
			FILE_NAME          : '',
			ACTUAL_FILE_NAME   : '',
			AGENT_LST          : []
		};
		
		//組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.region);
	};
	
	$scope.query = function() {
		$scope.sendRecv("ORG250", "query", "com.systex.jbranch.app.server.fps.org250.ORG250InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
				$scope.agentLst = tota[0].body.agentLst;
				$scope.outputVO = tota[0].body;
			}
			
		});
	};
	
	$scope.showORG250CONFIRM = function () {
		
		var dialog = ngDialog.open({
			template: 'assets/txn/ORG250/ORG250CONFIRM.html',
			className: 'ORG250',
			showClose: false,
			scope : $scope
          });
		dialog.closePromise.then(function (data) {
			if(data.value === 'successful'){
				$scope.init();
				$scope.query();
			}
			 
		});
	};
	
	$scope.upload = function(name, rname) {
		$scope.inputVO.FILE_NAME = name;
		$scope.inputVO.ACTUAL_FILE_NAME = rname;
		$confirm({text: '是否上傳：' + rname + '?'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG250", "upload", "com.systex.jbranch.app.server.fps.org250.ORG250InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.inputVO.AGENT_LST = tota[0].body.agentLst;
					$scope.showORG250CONFIRM();
				}
			});
		});
		angular.element($("#csvUpload")).remove();
		angular.element($("#csvBox")).append("<e-upload id='csvUpload' success='upload(name,rname)' text='上傳' accept='.csv'></e-upload>");
		var content = angular.element($("#csvBox"));
		var scope = content.scope();
		$compile(content.contents())(scope);
	};
	
	$scope.delete = function(row) {
		$scope.delInputVO.region_center_id = row.REGION_CENTER_NAME;
		$scope.delInputVO.branch_area_id = row.BRANCH_AREA_NAME;
		$scope.delInputVO.branch_nbr = row.BRANCH_NAME;
		$scope.delInputVO.EMP_ID = row.EMP_ID;
		$scope.delInputVO.AGENT_ID_1 = row.AGENT_ID_1;
		$scope.delInputVO.AGENT_ID_2 = row.AGENT_ID_2;
		$scope.delInputVO.AGENT_ID_3 = row.AGENT_ID_3;
		
		$confirm({text: '是否刪除' + row.EMP_ID + '-' + row.EMP_NAME + '的代理人資料'}, {size: '200px'}).then(function() {
			$scope.sendRecv("ORG250", "delete", "com.systex.jbranch.app.server.fps.org250.ORG250InputVO", $scope.delInputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
				}
				$scope.query();
			});
		});
	};
	
	$scope.getExample = function() {
		$scope.sendRecv("ORG250", "getExample", "com.systex.jbranch.app.server.fps.org250.ORG250InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}
			if (tota.length > 0) {
			}
		});
	};
	
	$scope.review = function(row){
		$scope.inputVO.SEQNO = row.SEQNO;
		$scope.inputVO.EMP_ID = row.EMP_ID;
		$scope.inputVO.AGENT_ID_1 = row.AGENT_ID_1;
		$scope.inputVO.AGENT_ID_2 = row.AGENT_ID_2;
		$scope.inputVO.AGENT_ID_3 = row.AGENT_ID_3;
		$scope.inputVO.ACT_TYPE = row.ACT_TYPE;
		$scope.inputVO.REVIEW_STATUS = row.REVIEW_STATUS;
		
		$confirm({text: '是否核可'}, {size: 'sm'}).then(function() {
			$scope.sendRecv("ORG250", "review", "com.systex.jbranch.app.server.fps.org250.ORG250InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.showSuccessMsg('ehl_01_common_021');
					$scope.init();
					$scope.query();
				}
			});
		});
	};
	
	$scope.reback = function(row){
		$scope.inputVO.SEQNO = row.SEQNO;
		$scope.inputVO.EMP_ID = row.EMP_ID;
		$scope.inputVO.AGENT_ID_1 = row.AGENT_ID_1;
		$scope.inputVO.AGENT_ID_2 = row.AGENT_ID_2;
		$scope.inputVO.AGENT_ID_3 = row.AGENT_ID_3;
		$scope.inputVO.ACT_TYPE = row.ACT_TYPE;
		$scope.inputVO.REVIEW_STATUS = row.REVIEW_STATUS;
		
		$confirm({text: '是否退回'}, {size: 'sm'}).then(function() {
			$scope.sendRecv("ORG250", "reback", "com.systex.jbranch.app.server.fps.org250.ORG250InputVO", $scope.inputVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.showSuccessMsg('ehl_01_common_020');
					$scope.init();
					$scope.query();
				}
			});
		});
	};
	
	$scope.alert = function (row){
		var dialog = ngDialog.open({
			template: 'assets/txn/ORG250/ORG250_REVIEW.html',
			className: 'ORG250',
			showClose: false,
			 controller: ['$scope', function($scope) {
            	$scope.row = row;
            }]
		});
		
	};
	
	$scope.export = function() {
		$scope.inputVO.exportList = $scope.agentLst;
		$scope.sendRecv("ORG250", "export", "com.systex.jbranch.app.server.fps.org250.ORG250InputVO", $scope.inputVO, 
				function(tota, isError) {
					if (isError) {
						$scope.showErrorMsgInDialog(tota.body.msgData);
						return;
					}
					if (tota.length > 0) {
					}
				}
		);
	};
	
	
	$scope.init();
});
