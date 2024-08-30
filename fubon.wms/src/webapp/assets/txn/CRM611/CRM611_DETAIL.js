/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM611_DETAILController', function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "CRM611_DETAILController";

		// init
		$scope.inputVO = {};
		$scope.data = $scope.connector('get', 'CRM611_DATA');
		$scope.esbData = $scope.connector('get','CRM611_esbData');
		$scope.siPromData = $scope.connector('get','CRM611_siPromData');
		$scope.w8BenData = $scope.connector('get','CRM611_w8BenData');
		$scope.inputVO.emp_id = $scope.data.CUST_ID; 
		
		$scope.mappingSet['YN'] = [];
		$scope.mappingSet['YN'].push({LABEL:'是',DATA:'Y'},{LABEL:'否',DATA:'N'},{LABEL:'否',DATA:''});
		$scope.mappingSet['AG'] = [];
		$scope.mappingSet['AG'].push({LABEL:'同意',DATA:'Y'},{LABEL:'不同意',DATA:'N'},{LABEL:'無資料',DATA:''});
		
		
	getParameter.XML([ 'SOT.FATCA_CHK_IDFN', 'KYC.CAREER', 'CRM.SIGN_AGMT', 'CRM.CBS.ORG.TYPE', 'CRM.BILLS_TYPE', 'KYC.EDUCATION', 'KYC.MARRAGE', 'KYC.CHILD_NO', 'CRM.SI_SIGN_AGMT', 'CRM.POTENTIAL_LEVEL', 'CRM.EXPERIENCE_LEVEL' ], function(totas) {
				if(len(totas)>0){
					$scope.mappingSet['SOT.FATCA_CHK_IDFN'] = totas.data[totas.key.indexOf('SOT.FATCA_CHK_IDFN')];
					$scope.mappingSet['KYC.CAREER'] = totas.data[totas.key.indexOf('KYC.CAREER')];
					$scope.mappingSet['CRM.SIGN_AGMT'] = totas.data[totas.key.indexOf('CRM.SIGN_AGMT')];
			        $scope.mappingSet['CRM.CBS.ORG.TYPE'] = totas.data[totas.key.indexOf('CRM.CBS.ORG.TYPE')];
					$scope.mappingSet['CRM.BILLS_TYPE'] = totas.data[totas.key.indexOf('CRM.BILLS_TYPE')];
					$scope.mappingSet['KYC.EDUCATION'] = totas.data[totas.key.indexOf('KYC.EDUCATION')];
			        $scope.mappingSet['KYC.MARRAGE'] = totas.data[totas.key.indexOf('KYC.MARRAGE')];
			        $scope.mappingSet['KYC.CHILD_NO'] = totas.data[totas.key.indexOf('KYC.CHILD_NO')];
					$scope.mappingSet['CRM.SI_SIGN_AGMT'] = totas.data[totas.key.indexOf('CRM.SI_SIGN_AGMT')];
					$scope.mappingSet['CRM.POTENTIAL_LEVEL'] = totas.data[totas.key.indexOf('CRM.POTENTIAL_LEVEL')];
					$scope.mappingSet['CRM.EXPERIENCE_LEVEL'] = totas.data[totas.key.indexOf('CRM.EXPERIENCE_LEVEL')];
				}
				 
			});
		
		
		// filter
		var vo = {'param_type': 'CRM.VIP_DEGREE', 'desc': false};
        if(!projInfoService.mappingSet['CRM.VIP_DEGREE']) {
        	$scope.requestComboBox(vo, function(totas) {
        		if (totas[totas.length - 1].body.result === 'success') {
        			projInfoService.mappingSet['CRM.VIP_DEGREE'] = totas[0].body.result;
        			$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        		}
        	});
        } else
        	$scope.mappingSet['CRM.VIP_DEGREE'] = projInfoService.mappingSet['CRM.VIP_DEGREE'];
        // bra
		$scope.mappingSet['branchsDesc'] = [];
		angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
			$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
		});
        //
        
//		$scope.inputVO.cust_id = $scope.connector('get','CRM110_CUST_ID');
		$scope.inputVO.cust_id =  $scope.custVO.CUST_ID;
		$scope.sendRecv("CRM611", "inquire", "com.systex.jbranch.app.server.fps.crm611.CRM611InputVO", $scope.inputVO,
				function(tota, isError) {
					if (!isError) {
						if(tota[0].body.resultList != null && tota[0].body.resultList.length > 0) {							
							$scope.dbData = tota[0].body.resultList[0];
//							$scope.inputVO.infrom_way = $scope.dbData.PREF_WF_ROUTE_MTHD;
							$scope.inputVO.infrom_way = $scope.dbData.PREF_INFORM_WAY;
						}
						if(tota[0].body != null) {
							//電文資料
				$scope.esbData2 = tota[0].body.fp032151OutputVO;

						}
					}
		});
//		 MARK: 20161220 FATCA 欄位打032275電文  BY Stella
	$scope.sendRecv("SOT701", "getCustW8BenFATCA", "com.systex.jbranch.app.server.fps.sot701.SOT701InputVO", {
		'custID' : $scope.inputVO.cust_id
	}, function(tota, isError) {
						if (!isError) {
							
							if(tota[0].body != null ) {
				$scope.w8bList = tota[0].body.w8BenDataVO;
							}
							
						}
			});
		
		$scope.save = function() {
			$scope.sendRecv("CRM611", "save", "com.systex.jbranch.app.server.fps.crm611.CRM611InputVO", $scope.inputVO,
					function(totas, isError) {
	                	if (isError) {
	                		$scope.showErrorMsg(totas[0].body.msgData);
	                	}
	                	if (totas.length > 0) {
	                		$scope.showSuccessMsg('ehl_01_common_004');
	                		// Mark : 2016/12/02 返回客戶首頁 -- By Stella
				// $scope.connector('set','CRM110_CUST_ID',
				// $scope.inputVO.cust_id);
	                		var path = "assets/txn/CRM610/CRM610_MAIN.html";
	            			$scope.connector("set","CRM610URL",path);
	            			$scope.$emit("CRM610VO", {action:"set", type:"URL", data:path});
//	                		$rootScope.menuItemInfo.url = "assets/txn/CRM611/CRM611.html";
	                	};
					}
			);
        };
		
       
        
        
        
//    Mark: 2016/12/20 SRS沒有畫 ，先註解  BY Stella       
//        $scope.cancel = function() {
//        	// Mark : 2016/12/02 返回客戶首頁 -- By Stella
////    		$scope.connector('set','CRM110_CUST_ID', $scope.inputVO.cust_id);
//    		var path = "assets/txn/CRM610/CRM610_MAIN.html";
//			$scope.connector("set","CRM610URL",path);
////			$rootScope.menuItemInfo.url = "assets/txn/CRM611/CRM611.html";
//        };
        
});

//年齡計算
function getAge(dateString) {
	console.log(dateString);
    var today = new Date();
    var birthDate = new Date(dateString);
    var age = today.getFullYear() - birthDate.getFullYear();
    var m = today.getMonth() - birthDate.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }
    return age;
}