/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('REF130Controller',
	function(sysInfoService, $rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "REF130Controller";
		
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		
		// filter
		getParameter.XML(["CAM.REF_PROD", "CAM.REF_SALES_ROLE"], function(totas) {
			if (totas) {
				$scope.mappingSet['CAM.REF_PROD'] = totas.data[totas.key.indexOf('CAM.REF_PROD')];
				$scope.mappingSet['CAM.REF_SALES_ROLE'] = totas.data[totas.key.indexOf('CAM.REF_SALES_ROLE')];
			}
		});
        //
        
        $scope.init = function(){
			$scope.inputVO = {
					regionID: '',
					branchAreaID:  '',
					branchID: '', 
					txnDate: '', 
					salesPerson: '', 
					refProd: ''
			};
			$scope.resultList = [];
			
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "regionID", "REGION_LIST", "branchAreaID", "AREA_LIST", "branchID", "BRANCH_LIST", "ao_code", "AO_LIST", "salesPerson", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
	        
			$scope.inputVO.regionID = ("null" == projInfoService.getRegionID() ? "" : projInfoService.getRegionID());
			$scope.inputVO.branchAreaID = ("null" == projInfoService.getAreaID() ? "" : projInfoService.getAreaID());
			$scope.inputVO.branchID = ("000" == projInfoService.getBranchID() ? "" : projInfoService.getBranchID());
			$scope.inputVO.salesPerson = projInfoService.getUserID();
            
			$scope.mappingSet['REF_RPOD'] = [];
            if (sysInfoService.getPriID() == '004') {
            	$scope.mappingSet['REF_RPOD'].push({LABEL: '投保商品', DATA: '5'});
            	$scope.inputVO.refProd = '5';
            	$scope.disType = true;
            } else if(sysInfoService.getPriID() == '002' || sysInfoService.getPriID() == '003') {
            	$scope.mappingSet['REF_RPOD'].push({LABEL: '貸款商品', DATA: '0'}); 
            	$scope.inputVO.refProd = '0';
            	$scope.disType = true;
            } else {
            	$scope.mappingSet['REF_RPOD'].push({LABEL: '投保商品', DATA: '5'}); 
            	$scope.mappingSet['REF_RPOD'].push({LABEL: '貸款商品', DATA: '0'}); 
            	$scope.inputVO.refProd = '5';
            	$scope.disType = false;
            }
		};
		$scope.init();
		
		$scope.query = function () {
			if ($scope.inputVO.refProd == "") {
				$scope.inputVO.refProd = "5";
			}
			
			$scope.resultList = [];
			$scope.outputVO = [];
			$scope.sendRecv("REF130", "query", "com.systex.jbranch.app.server.fps.ref130.REF130InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.resultList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		}
		
        $scope.refProdChange = function() {
        	$scope.resultList = [];
        }
		
        $scope.export = function() {
			$scope.inputVO.EXPORT_LST = $scope.resultList;
			
			$scope.sendRecv("REF130", "export", "com.systex.jbranch.app.server.fps.ref130.REF130InputVO", $scope.inputVO, 
					function(tota, isError) {
						if (isError) {
							$scope.showErrorMsgInDialog(tota.body.msgData);
							return;
						}
						if (tota.length > 0) {}
					}
			);
		};
});
