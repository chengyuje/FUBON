/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS314Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS314Controller";
	
		$scope.init = function(){
			$scope.inputVO = {
					seq: 0,
					summitINC: 0,
					peakINC: 0,
					summitFC1: 'N',
					summitFC2: 'N',
					summitFC3: 'N',
					summitFC4: 'N',
					summitFC5: 'N',
					peakFC1: 'N',
					peakFC2: 'N',
					peakFC3: 'N',
					peakFC4: 'N',
					peakFC5: 'N',
					fch1INC: 0,
					fch2INC: 0,
					fchAum: 0,
					fchNew: 0
			};			                     
		};
		$scope.init();
	    
		$scope.inquire = function(){
			$scope.sendRecv("PMS314", "inquire", "com.systex.jbranch.app.server.fps.pms314.PMS314InputVO", {},
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.inputVO.seq = [];
								$scope.inputVO.summitINC = [];
							    $scope.inputVO.peakINC = [];
							    $scope.inputVO.summitFC1 = [];
							    $scope.inputVO.summitFC2 = [];
							    $scope.inputVO.summitFC3 = [];
							    $scope.inputVO.summitFC4 = [];
							    $scope.inputVO.summitFC5 =[];
							    $scope.inputVO.peakFC1 = [];
							    $scope.inputVO.peakFC2 = [];
							    $scope.inputVO.peakFC3 = [];
							    $scope.inputVO.peakFC4 = [];
							    $scope.inputVO.peakFC5 = [];
							    $scope.inputVO.fch1INC =[];
							    $scope.inputVO.fch2INC =[];
							    $scope.inputVO.fchAum = [];
							    $scope.inputVO.fchNew = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
//							$scope.originalList = angular.copy(tota[0].body.resultList);
							$scope.paramList = tota[0].body.resultList;	

							$scope.inputVO.seq = $scope.paramList[0].SEQ;
							$scope.inputVO.summitINC = $scope.paramList[0].FC_SUMMIT_INC;
						    $scope.inputVO.peakINC = $scope.paramList[0].FC_PEAK_INC;
						    $scope.inputVO.summitFC1 = $scope.paramList[0].SUMMIT_FC1;
						    $scope.inputVO.summitFC2 = $scope.paramList[0].SUMMIT_FC2;
						    $scope.inputVO.summitFC3 = $scope.paramList[0].SUMMIT_FC3;
						    $scope.inputVO.summitFC4 = $scope.paramList[0].SUMMIT_FC4;
						    $scope.inputVO.summitFC5 = $scope.paramList[0].SUMMIT_FC5;
						    $scope.inputVO.peakFC1 = $scope.paramList[0].PEAK_FC1;
						    $scope.inputVO.peakFC2 = $scope.paramList[0].PEAK_FC2;
						    $scope.inputVO.peakFC3 = $scope.paramList[0].PEAK_FC3;
						    $scope.inputVO.peakFC4 = $scope.paramList[0].PEAK_FC4;
						    $scope.inputVO.peakFC5 = $scope.paramList[0].PEAK_FC5;
						    $scope.inputVO.fch1INC = $scope.paramList[0].FCH_1_INC;
						    $scope.inputVO.fch2INC = $scope.paramList[0].FCH_2_INC;
						    $scope.inputVO.fchAum = $scope.paramList[0].FCH_AUM;
						    $scope.inputVO.fchNew = $scope.paramList[0].FCH_NEW;
						    
							$scope.outputVO = tota[0].body;	
							
							return;
						}
			});
		};
		$scope.inquire();
		
		/*** 儲存功能 ***/
		$scope.save = function () {	
//			alert("ss="+$scope.inputVO.summitFC1);
			$scope.sendRecv("PMS314", "save", "com.systex.jbranch.app.server.fps.pms314.PMS314InputVO", $scope.inputVO,
					function(tota, isError) {
						if (isError) {
		            		$scope.showErrorMsgInDialog(tota[0].body.msgData);
		            	}
		            	if (tota.length > 0) {
		            		$scope.showMsg('儲存成功');
		            		$scope.inquire();
		            	};		
			});
			
		};
		
});
