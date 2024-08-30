/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS711_fcSubController',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService) {
		//console.log('111')
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS711_fcSubController";
		$scope.select = function() {
			$scope.inputVO.date_year = $scope.date_year;
			$scope.inputVO.personType = $scope.personType;
			$scope.inputVO.EDate1 = $scope.endDate;
			$scope.showRowList = new Object();
			for(var key in $scope.showMonthList){
				$scope.showRowList[$scope.showMonthList[key]] = key; 
			}
			$scope.inputVO.subProjectSeqId = $scope.subProjectSeqId;
			if($scope.inputVO.personType=='5'){
				$scope.sendRecv("PMS711","queryMetSub","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								if(tota[0].body.showList.length>0){
									$scope.showList = tota[0].body.showList;
									$scope.outputVO = tota[0].body;
									$scope.totalChange();
								}
							}
						});
			}else{
				$scope.sendRecv("PMS711","queryFcSub","com.systex.jbranch.app.server.fps.pms711.PMS711InputVO",$scope.inputVO,
						function(tota, isError) {
							if (!isError) {
								$scope.showList = tota[0].body.showList;
								$scope.outputVO = tota[0].body;
								$scope.totalChange();
								return;
							}
						});
			}
			
		}
		$scope.select();
		//計算統計值
		$scope.totalChange = function(){
			for(var i=0; i < $scope.showList.length; i++){
				var sum = 0;
				for(var key in $scope.showMonthList){
					sum += Number($scope.showList[i][key]);
				}
				$scope.showList[i].TOTAL = sum;
			}
		}	
});