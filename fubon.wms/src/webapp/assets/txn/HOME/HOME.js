'use strict';
eSoafApp.controller('HOMEController', 
    function($rootScope, $scope, sysInfoService, $controller, ngDialog, getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "HOMEController";
        
        $scope.pri = sysInfoService.getPriID()[0];
        $scope.IsMobile = sysInfoService.getLoginSourceToken() == 'mobile';
        // get xml
		getParameter.XML(['SYS.HOME_NOSNOW_CRM131_ROLE'], function(totas) {
			if (totas) {
				$scope.mappingSet['SYS.HOME_NOSNOW_CRM131_ROLE'] = totas.data[totas.key.indexOf('SYS.HOME_NOSNOW_CRM131_ROLE')];
				$scope.mappingSet['SYS.HOME_NOSNOW_CRM132_ROLE'] = totas.data[totas.key.indexOf('SYS.HOME_NOSNOW_CRM131_ROLE')];
				
				$scope.isNoShowCrm131 = false;
                angular.forEach($scope.mappingSet['SYS.HOME_NOSNOW_CRM131_ROLE'], function(row, index, objs) {
                	if(sysInfoService.getRoleID() == row.LABEL) {
                		$scope.isNoShowCrm131 = true;
                	}
    			});
                
                $scope.isNoShowCrm132 = false;
                angular.forEach($scope.mappingSet['SYS.HOME_NOSNOW_CRM131_ROLE'], function(row, index, objs) {
                	if(sysInfoService.getRoleID() == row.LABEL) {
                		$scope.isNoShowCrm132 = true;
                	}
    			});
			}
		});
		
        //檢視 業績達成(CRM151)角色權限 
        $scope.check151 = '';
        if(
        //先行開放理專及分行主管督導與處主管		
        $scope.pri =='002' || $scope.pri =='003' ||
        $scope.pri =='009' || $scope.pri =='010' || $scope.pri =='011'	
        	|| $scope.pri =='012' || $scope.pri =='013'	
        /*	
        $scope.pri =='002' || $scope.pri =='003' || $scope.pri =='004' ||  $scope.pri =='009' 
		|| $scope.pri =='010' || $scope.pri =='011' || $scope.pri =='012' || $scope.pri =='013'
		|| $scope.pri =='014' || $scope.pri =='015' || $scope.pri =='023' || $scope.pri =='024'
		*/
			){
        	
//        	業績達成狀況(CRM151): 2020/02/03 -- User確定不要顯示在工作首頁
//        	$scope.check151 = 'Y';
        	$scope.check151 = 'N';
        	
        }else{
        	$scope.check151 = 'N';
        }
        $scope.state='1';
        $scope.Scaling = function(){        	
        	switch($scope.state){
        		case '1':
        			$scope.state='2';
        			$scope.size='100%';
        			break;
        		case '2':
        			$scope.state='1';
        			$scope.size='48%';
        			break;
        	};        	
        }
        $scope.CRM123_init = function(){ 
        	$scope.$broadcast("CRM123.init");
        };
    }
);