/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS357_DETAILController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS357_DETAILController";
		
		$scope.init = function(){
			$scope.inputVO = {					
					dataMonth: '',					
					rc_id: '',
					op_id: '' ,
					br_id: '',
					ao_code: '',
					emp_id: '',
					aocode  :'',
					branch  :'',
					region  :'',
					op      :'',
					sTime  :'',
					checked:'TBPMS_BR_TARGET'

        	};
			$scope.curDate = new Date();
			
		
		};
		$scope.init();
		  /**==類型年月==**/
        $scope.mappingSet['seture']=[];
        $scope.mappingSet['seture'].push({
			DATA : '2',
			LABEL : '目標'
		},{
			DATA : '1',
			LABEL : '實績'
		});

		
		$scope.query = function(){
			$scope.sendRecv("PMS357", "getDetail", "com.systex.jbranch.app.server.fps.pms357.PMS357InputVO", 
				$scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								$scope.paramList = [];
								$scope.outputVO = [];
	                			return;
	                		}	
							$scope.paramList = tota[0].body.resultList;
//							$scope.pagingList($scope.paramList, tota[0].body.resultList);						
							$scope.outputVO = tota[0].body;
							console.log($scope.paramList);
							return;
						}else{
							$scope.paramList = [];
						}
			});
		};
		// 初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        };
        $scope.inquireInit();
        
        $scope.initLoad = function(){
			$scope.sendRecv("PMS000", "getLastYMlist", "com.systex.jbranch.app.server.fps.pms000.PMS000InputVO", {},
					   function(totas, isError) {
				             	if (totas.length > 0) {
				               		$scope.ymList = totas[0].body.ymList;
				               	};
					   }
			);
		}
		$scope.initLoad();
    	
});
