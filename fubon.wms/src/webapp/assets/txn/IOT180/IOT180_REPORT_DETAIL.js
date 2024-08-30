/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('IOT180_REPORT_DETAILController',
	function($scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "IOT180_REPORT_DETAILController";
		
		$scope.mappingSet['status']=[];
		$scope.mappingSet['status'].push({LABEL : '核保通過待結案',DATA : 'B'},
										 {LABEL : '照會中',DATA : 'D'},
										 {LABEL : '照會中',DATA : 'E'},
										 {LABEL : '核保中',DATA : 'C'},
										 {LABEL : '契撤/取消/拒保/延期',DATA : 'F'},
										 {LABEL : '契撤/取消/拒保/延期',DATA : 'G'},
										 {LABEL : '契撤/取消/拒保/延期',DATA : 'H'},
										 {LABEL : '契撤/取消/拒保/延期',DATA : 'I'},
										 {LABEL : '其他',DATA : 'J'},
										 {LABEL : '已核實預估佣收',DATA : 'A'});
		
		$scope.init = function(){
			$scope.inputVO = $scope.row;
			$scope.inputVO.detailType = $scope.type;
			$scope.inputVO.branch_nbr = $scope.brh;
			$scope.inputVO.cls = $scope.cls;
        };
        $scope.init();
        
        /*** 查詢資料 ***/
		$scope.query = function(){			
			$scope.sendRecv("IOT180", "getReportDetail", "com.systex.jbranch.app.server.fps.iot180.IOT180InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if (!isError) {
								$scope.paramList = tota[0].body.ReportDetail;															
								$scope.outputVO = tota[0].body;														
								return;
							}
						}						
			});
		};
		$scope.query();
                	
});
