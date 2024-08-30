/* 
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('SOT520Controller',
	function($rootScope, $scope, $controller,$filter, $confirm, getParameter , socketService, ngDialog, projInfoService, $q, validateService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "SOT520Controller";

		
		getParameter.XML(["SOT.BOND_STORAGE_STATUS"], function(totas) {
			if (totas) {
				$scope.mappingSet['SOT.BOND_STORAGE_STATUS'] = totas.data[totas.key.indexOf('SOT.BOND_STORAGE_STATUS')];
			}
		});
		
		$scope.model = {};
		$scope.altInputFormats = ['M!/d!/yyyy'];
		
//		$scope.startDateOptions = {
//			maxDate: $scope.inputVO.endDate || $scope.maxDate,
//			minDate: $scope.minDate
//		};
//		$scope.endDateOptions = {
//			maxDate: $scope.maxDate,
//			minDate: $scope.inputVO.startDate || $scope.minDate
//		};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		$scope.init = function(){
			$scope.outputVO=[];
			$scope.custAssetBondList=[];
			$scope.data = [];
			$scope.inputVO = {
					custId:'',
					prodId:'',
					prodName:'',
					prodType:'1',
					startDate:undefined,
					endDate:undefined,
					trustTS: 'S' // 特金
			};
			var custID = $scope.connector('get','ORG110_custID');
			if(custID != undefined){
				$scope.inputVO.custId = custID;
			}
		};
		
		 $scope.query = function () {
			 if($scope.inputVO.custId == '' || $scope.inputVO.custId == undefined) {
		    		$scope.showErrorMsg("ehl_01_common_022");
		    		return;
		     }
			 $scope.inputVO.custId = $filter('uppercase')($scope.inputVO.custId);
			 $scope.inputVO.prodId = $filter('uppercase')($scope.inputVO.prodId);
			 $scope.inputVO.prodName = $filter('uppercase')($scope.inputVO.prodName);
			 var validCustID = validateService.checkCustID($scope.inputVO.custId); //自然人和法人檢查
			 if(validCustID==false) {
				 $scope.inputVO.custId='';
				 return;
			 }

			 /** 特金或是金錢信託才需要繼續往下 M:金錢信託、S:特金 **/
			 if (!/[MS]/.test($scope.inputVO.trustTS)) return;
			 let method = $scope.inputVO.trustTS === 'S'? 'getCustAssetBondData': 'getCustAssetBondData_TRUST';

			 $scope.sendRecv("SOT707", method, "com.systex.jbranch.app.server.fps.sot707.SOT707InputVO", $scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if (tota[0].body.custAssetBondList.length > 0) {
									$scope.custId=$scope.inputVO.custId;
									$scope.custAssetBondList = tota[0].body.custAssetBondList;
									$scope.outputVO = tota[0].body;
								} else {
									$scope.showErrorMsg("ehl_01_common_009");
									$scope.outputVO=[];
									$scope.custAssetBondList=[];
									$scope.data = [];
								}
							}else{
								$scope.outputVO=[];
								$scope.custAssetBondList=[];
								$scope.data = [];
							}
				});
	        };
	        
	        $scope.next = function (row) {
	        	$scope.connector('set','SOT521_prodDTL', row);
	        	$scope.connector('set','SOT521_custID', $scope.custId);
	        	$scope.connector('set','SOTTradeSEQ', null);
				$scope.connector('set','SOTCarSEQ', null);
				$scope.connector('set', 'SOTContractID', $scope.inputVO.contractID);
				$scope.connector('set', 'SOTDebitAcct', $scope.inputVO.debitAcct);
				$scope.connector('set', 'SOTTrustPeopNum', $scope.inputVO.trustPeopNum);
				$scope.connector('set', 'SOT521GUARDIANSHIP_FLAG', $scope.inputVO.GUARDIANSHIP_FLAG);

				// 特金指向 SOT521（特金 SN 贖回）; 金錢信託指向 SOT526（金錢信託 SN 贖回）
				let txn = row.trustTS === 'S'? 'SOT521': 'SOT526';

				if ($scope.fromFPS) {
					// from FPS_SOT.js
					$scope.setSOTurl(`assets/txn/${txn}/${txn}.html`);
				} else {
					$rootScope.menuItemInfo.url = `assets/txn/${txn}/${txn}.html`;
				}				
	        }
	        
	        $scope.init();
	        
	        //從其他地方進入下單
	        $scope.FromElse = function(){
	    		if($scope.connector('get','SOTCustID')){
	    			$scope.inputVO.custId=$scope.connector('get','SOTCustID');
	    			$scope.connector('set','SOTCustID',null);
	    			$scope.query();
	    		} else if ($scope.fromFPS){
					console.log($scope.FPSData);
					$scope.inputVO.custId = $scope.FPSData.custID;//客戶ID
//					$scope.inputVO.prodId = $scope.FPSData.prdID; //商品代號	 
					$scope.query();
				}
	        }
	       
	        $scope.FromElse();
	        
		
});