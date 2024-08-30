/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT131Controller',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT131Controller";
		
		$scope.init = function(){
			$scope.temp_MatchList = $scope.connector('get','IOT131');
			$scope.inputVO = {
				in_OPRSTATUS:$scope.OPR_STATUSList.OPR_STATUS,
				in_INSKEYNO:$scope.OPR_STATUSList.INSPRD_ID,
				in_RISK:$scope.OPR_STATUSList.PROPOSER_RISK,
				up_INSKEYNO:$scope.OPR_STATUSList.INS_KEYNO,
				MATCH_DATE:$scope.OPR_STATUSList.MATCH_DATE,
				C_SENIOR_PVAL:$scope.OPR_STATUSList.C_SENIOR_PVAL
			}

			if($scope.temp_MatchList != undefined){
				$scope.MatchList = $scope.temp_MatchList;
				if($scope.MatchList == ''){
					$scope.sendRecv("IOT131","Initial","com.systex.jbranch.app.server.fps.iot131.IOT131InputVO",
							$scope.inputVO,function(tota,isError){
							$scope.MatchList = tota[0].body.MatchList;
					});
				}
			}else{
				$scope.sendRecv("IOT131","Initial","com.systex.jbranch.app.server.fps.iot131.IOT131InputVO",
						$scope.inputVO,function(tota,isError){
						$scope.MatchList = tota[0].body.MatchList;
				});
			}
		}
		$scope.init();

		$scope.Compute = function(){
			var sumLINK_PCT = 0;
			for(var a=0;a<$scope.MatchList.length;a++){
				if($scope.MatchList[a].LINK_PCT != undefined){
					if($scope.MatchList[a].LINK_PCT != ''){
						if($scope.MatchList[a].LINK_PCT<5){
							return false
							break;
						}else{
							sumLINK_PCT+=Number($scope.MatchList[a].LINK_PCT);
						}
					}
				}
			}
			if(sumLINK_PCT != 100){
				return false;
			}else{
				return true;
			}
		}
		
		$scope.saveData = function(){
			if($scope.Compute()){
				$scope.connector('set','IOT131',$scope.MatchList);
				$scope.closeThisDialog('successful');
			}else{
				$scope.showMsg("比例小於5或加總不等於100")
			}

		}
		
//        $scope.mapData = function(){
//        	//文件重要性
//        	$scope.ngDatasource = projInfoService.mappingSet["IOT.DOC_CHK_LEVEL"];
//			var comboboxInputVO = {'param_type': "IOT.DOC_CHK_LEVEL", 'desc': false};
//			$scope.requestComboBox(comboboxInputVO, function(totas) {
//					$scope.implist = totas[0].body.result;
//			});
//
//        }
//        $scope.mapData();
		

		

		
	}
);