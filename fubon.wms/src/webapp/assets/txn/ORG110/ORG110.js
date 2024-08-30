/*
 * Modify LOG
 * 
 * 2017-02-02 組織連動(專案同步修正) modify by ocean
 * 
 */
'use strict';
eSoafApp.controller('ORG110Controller', function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, getParameter, $confirm) {
	$controller('BaseController', {$scope : $scope});
	$scope.controllerName = "ORG110Controller";
	
	$scope.roleID = sysInfoService.getRoleID();
	$scope.memLoginFlag = sysInfoService.getMemLoginFlag();
	
	//組織連動繼承
	$controller('RegionController', {$scope: $scope});
	
	// filter
	getParameter.XML(["COMMON.REVIEW_STATUS", "ORG.JOB_TITLE_NAME_SEARCH"], function(totas) {
		if (totas) {
			$scope.mappingSet['COMMON.REVIEW_STATUS'] = totas.data[totas.key.indexOf('COMMON.REVIEW_STATUS')];
			$scope.mappingSet['ORG.JOB_TITLE_NAME_SEARCH'] = totas.data[totas.key.indexOf('ORG.JOB_TITLE_NAME_SEARCH')];
		}
	});
    //
	
	$scope.init = function() {
		$scope.mappingSet['jtLst'] = [];
		$scope.orgMemberLst = [];
		$scope.memberPhotoLst = [];

		$scope.inputVO = {
			region_center_id   : '',
			branch_area_id     : '',
			branch_nbr         : '',
			JOB_TITLE_NAME     : '',
			EMP_ID             : '',
			EMP_NAME           : '',
			AO_CODE            : '',
			PHOTO_FLAG         : ''
		};
		
		$scope.photoInVO = {
			EMP_ID : ''
		};
		
		$scope.tnsfData = {
			EMP_ID 			   : '',
			BRANCH_NBR 		   : '',
			REVIEW_STATUS 	   : ''
		};
		
//		$scope.getTitleLst();
		
        //組織連動
        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
		$scope.RegionController_setName($scope.region).then(function(data) {
	        if ($scope.connector('get', 'ORG110_queryCondition') != undefined) {
	        	$scope.inputVO.region_center_id = $scope.connector('get', 'ORG110_queryCondition').region_center_id;
	        	$scope.inputVO.branch_area_id 	= $scope.connector('get', 'ORG110_queryCondition').branch_area_id;
	        	$scope.inputVO.branch_nbr 		= $scope.connector('get', 'ORG110_queryCondition').branch_nbr;
	        	$scope.inputVO.JOB_TITLE_NAME 	= $scope.connector('get', 'ORG110_queryCondition').JOB_TITLE_NAME;
	        	$scope.inputVO.EMP_ID 			= $scope.connector('get', 'ORG110_queryCondition').EMP_ID;
	        	$scope.inputVO.EMP_NAME 		= $scope.connector('get', 'ORG110_queryCondition').EMP_NAME;
	        	$scope.inputVO.AO_CODE 			= $scope.connector('get', 'ORG110_queryCondition').AO_CODE;
	        	$scope.inputVO.PHOTO_FLAG 		= $scope.connector('get', 'ORG110_queryCondition').PHOTO_FLAG;
	        	
	        	$scope.connector('set','ORG110_queryCondition', undefined);
	        	$scope.getOrgMemberLst();
	        }
		});
	};

	$scope.getOrgMemberLst = function() {
		$scope.sendRecv("ORG110", "getOrgMemberLst", "com.systex.jbranch.app.server.fps.org110.ORG110InputVO", $scope.inputVO, function(tota, isError) {
			if (isError) {
				$scope.showErrorMsgInDialog(tota.body.msgData);
				return;
			}

			if (tota.length > 0) {
				
				if(tota[0].body.orgMemberLst && tota[0].body.orgMemberLst.length == 0) {
					$scope.showErrorMsg("ehl_01_common_009");
					return;
				}
				$scope.mappingSet['COMMON.YES_NO'] = projInfoService.mappingSet['COMMON.YES_NO'];
				$scope.orgMemberLst = tota[0].body.orgMemberLst;
				$scope.outputVO = tota[0].body;
			}
		});
	};
	
	$scope.getProfilePicture = function(row) {
		$scope.photoInVO.EMP_ID = row.EMP_ID;
		if (row.pictureSrc == '' || row.pictureSrc == undefined) {
			$scope.sendRecv("ORG110", "getProfilePicture", "com.systex.jbranch.app.server.fps.org110.ORG110InputVO", $scope.photoInVO, function(tota, isError) {
				if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				}
				if (tota.length > 0) {
					$scope.memberPhotoLst = tota[0].body.orgMemberLst;
					
					var bufView = new Uint8Array(tota[0].body.orgMemberLst[0].EMP_PHOTO);
					var length = bufView.length;
				    var result = '';
				    var addition = Math.pow(2,16)-1;
				    for(var i = 0; i<length; i += addition) {
				    	if(i + addition > length)
				    		addition = length - i;
				    	result += String.fromCharCode.apply(null, bufView.subarray(i,i+addition));
				    }
				    row.pictureSrc = "data:image/png;base64,"+btoa(result);
				}
			});
		}
	};
	
	$scope.cancelUnauth = function(row)   {
        row.UNAUTH_DATE = new Date(row.UNAUTH_DATE);
        row.REAUTH_DATE = new Date(row.REAUTH_DATE);
        $confirm({text:"是否回復" + row.EMP_ID + "權限？"}).then( function() {
            $scope.sendRecv("ORG110", "cancelUnauth", "com.systex.jbranch.app.server.fps.org110.ORG110InputVO", row, function(tota, isError)  {
                if (isError) {
					$scope.showErrorMsgInDialog(tota.body.msgData);
					return;
				} else {
					$scope.getOrgMemberLst();
                    $scope.showMsg("已回復" + row.EMP_ID + "系統權限");
                }
            });
        });
    };
    
	$scope.openORG111 = function(row) {
		$scope.connector('set','ORG110_queryCondition', $scope.inputVO);
		
		$scope.tnsfData.EMP_ID = row.EMP_ID;
		$scope.tnsfData.REVIEW_STATUS = row.REVIEW_STATUS;
		$scope.connector('set','ORG110_tnsfData', $scope.tnsfData);
		$rootScope.menuItemInfo.url = "assets/txn/ORG111/ORG111.html";
	}
	
	$scope.openORG120 = function(row) {
		$scope.connector('set','ORG110_queryCondition', $scope.inputVO);
		
		$scope.connector('set','ORG110_tnsfData', row);
		$rootScope.menuItemInfo.url = "assets/txn/ORG120/ORG120.html";
	}
	
	$scope.openORG210 = function(row) {
		$scope.connector('set','ORG110_queryCondition', $scope.inputVO);
		
		$scope.tnsfData.BRANCH_NBR = row.BRANCH_NBR;
		$scope.connector('set','ORG110_tnsfData', $scope.tnsfData);
		$rootScope.menuItemInfo.url = "assets/txn/ORG210/ORG210.html";
	}
	
	$scope.init();
	
	$scope.getReviewList = function (row) {
		var dialog = ngDialog.open({
			template: 'assets/txn/ORG110/ORG110_REVIEW.html',
			className: 'ORG110',
			showClose: false,
			 controller: ['$scope', function($scope) {
            	$scope.EMP_ID = row.EMP_ID;
            }]
		});
	}

});
