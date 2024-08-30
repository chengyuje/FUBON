/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('TEST_REGIONController', 
    function($scope, projInfoService, $controller, $confirm) {
		$controller('BaseController', {$scope: $scope});
		// 繼承這個
		$controller('RegionController', {$scope: $scope});
        $scope.controllerName = "TEST_REGIONController";
        
        $scope.inputVO = {};
        // 區域		 AVAIL_REGION 
		// 營運區		 AVAIL_AREA
		// 分行別		 AVAIL_BRANCH
		// ao_code	 TOTAL_AO_LIST, AVAIL_AO_CODE
		// emp_id	 TOTAL_EMP_LIST
        // ["塞空ao_code用YN", $scope.inputVO, "區域NAME", "區域LISTNAME", "營運區NAME", "營運區LISTNAME", "分行別NAME", "分行別LISTNAME", "ao_codeNAME", "ao_codeLISTNAME", "emp_idNAME", "emp_idLISTNAME"]
        $scope.test = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "branch_nbr", "BRANCH_LIST", "ao_code", "AO_LIST", "emp_id", "EMP_LIST"];
        $scope.RegionController_setName($scope.test);
        
        
		
//        $scope.test2 = ['N', $scope.inputVO, "region_center_id2", "REGION_LIST2", "branch_area_id2", "AREA_LIST2", "branch_nbr2", "BRANCH_LIST2", "ao_code2", "AO_LIST2", "emp_id2", "EMP_LIST2"];
//        $scope.RegionController_setName($scope.test2);
        // 不能傳空值，用不到還是要給
        // 所以說，如果我只要分行別，ao_code選下去會連動到區域
        // 然後分行別就會只剩下該區域，解決方法大概就分行別選請選擇時把 region_center_id,branch_area_id清空就好
        
        // see
        $scope.see = function() {
        	alert(JSON.stringify($scope.inputVO));
        };
        
        
    }
);
