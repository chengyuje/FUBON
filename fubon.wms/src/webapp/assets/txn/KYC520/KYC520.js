/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('KYC520Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, sysInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "KYC520Controller";
		
        //簽核狀態
		getParameter.XML(["PRO.QUEST_STATUS"],function(totas){
			if(totas){
				//簽核狀態
				$scope.mappingSet['PRO.QUEST_STATUS'] = totas.data[0];
			}
		});
		// date picker
		$scope.approveStartDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.approveEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		
		//config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		
		//主管簽核日期
		$scope.approveDate = function() {
			$scope.approveStartDateOptions.maxDate = $scope.inputVO.approveEndDate || $scope.maxDate;
			$scope.approveEndDateOptions.minDate = $scope.inputVO.approveStartDate || $scope.minDate;
		};

		$scope.submitStartDateOptions = {
				maxDate: $scope.maxDate,
				minDate: $scope.minDate
		};
		$scope.submitEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.submitDate = function() {
			$scope.submitStartDateOptions.maxDate = $scope.inputVO.submitEndDate || $scope.maxDate;
			$scope.submitEndDateOptions.minDate = $scope.inputVO.submitStartDate || $scope.minDate;
		};
		// date picker end

		// init
		$scope.init = function(){
			$scope.inputVO = {
					approveStartDate:undefined,
					approveEndDate:undefined,
					submitStartDate:undefined,
					submitEndDate:undefined,
					aoCode:undefined,
					quesName:'',
					approveStatus:''	
			}
//			$scope.approveDate;
//			$scope.submitDate;
		};
		$scope.init();
		
		$scope.clear = function(){
			$scope.inputVO = {
				approveStartDate:undefined,
				approveEndDate:undefined,
				submitStartDate:undefined,
				submitEndDate:undefined,
				aoCode:undefined,
				quesName:'',
				approveStatus:''
			}
			$scope.approveDate();
			$scope.submitDate();
			$scope.inquireInit();
		}

		$scope.inquireInit = function() {
			$scope.paramList = [];
		}
		$scope.inquireInit();


		// inquire
		$scope.inquire = function() {
			// alert('pause')
			$scope.sendRecv("KYC520", "inquire", "com.systex.jbranch.app.server.fps.kyc520.KYC520InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList == null) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							angular.forEach($scope.paramList, function(row, index, objs){
								$scope.paramList[index].MODIFIER= $scope.paramList[index].MODIFIER != null ?
																  $scope.paramList[index].MODIFIER.concat("/",$scope.paramList[index].EMP_NAME) : "";
								$scope.paramList[index].SIGNOFF = $scope.paramList[index].SIGNOFF_ID != null ?
																  $scope.paramList[index].SIGNOFF_ID.concat("/",$scope.paramList[index].SIGNOFF_NAME) : "";
							});
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};

		// check
		$scope.check = function(row){
			var dialog = ngDialog.open({
				template: 'assets/txn/KYC520/KYC520_Check.html',
				className: 'KYC520_Check',
				controller: ['$scope', function($scope){
					$scope.row = row;
				}]
			});
			dialog.closePromise.then(function(data){
				if(data.value == 'successful'){
					$scope.inquire();
				}
			})
		}

});
