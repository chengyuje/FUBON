/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('CRM3111Controller',
	function($rootScope, $scope, $controller, $confirm, $filter, socketService, ngDialog, projInfoService, getParameter) {
		$controller('BaseController', {$scope: $scope});
		//組織連動繼承
		$controller('RegionController', {$scope: $scope});
		$scope.controllerName = "CRM3111Controller";
		
		// 2017/6/7
		getParameter.XML(["CRM.VIP_DEGREE", "CRM.CON_DEGREE"], function(totas) {
			if (totas) {
				$scope.VIP_DEGREE = totas.data[totas.key.indexOf('CRM.VIP_DEGREE')];
				$scope.CON_DEGREE = totas.data[totas.key.indexOf('CRM.CON_DEGREE')];
			}
		});
		//
		
		/**============================================date picker======================================================**/
		//異動日期開始
        $scope.sDateOptions = {
    		maxDate: $scope.maxDate,
    		minDate: $scope.inputVO.sCreDate
    	};
        //異動日期結束
        $scope.eDateOptions = {
    		maxDate: $scope.inputVO.eCreDate,
    		minDate: $scope.minDate
    	};
        //config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.sDateOptions.maxDate = $scope.inputVO.eCreDate || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sCreDate || $scope.minDate;
		};
		
		/**==============================================初始化========================================================**/
		$scope.mappingSet['querytype']=[];
		$scope.mappingSet['querytype'].push({LABEL: '訪談內容', DATA: '1'}, {LABEL:'近6個月AUM變化', DATA:'2'});
		
		$scope.initialList = function(){
			$scope.resultList = [];
			$scope.outputVO = {};
		};
		$scope.initialList();
		
		$scope.init = function() {
			$scope.inputVO = {
				new_ao_brh : '',
				new_ao_code: '',
				sCreDate: undefined,
				eCreDate: undefined,
				cust_id:'',
				cust_name:'',
				act_type:'1',
				mroleid:''
			}
			//組織連動
	        $scope.region = ['N', $scope.inputVO, "region_center_id", "REGION_LIST", "branch_area_id", "AREA_LIST", "new_ao_brh", "BRANCH_LIST", "new_ao_code", "AO_LIST", "GGGG", "EMP_LIST"];
	        $scope.RegionController_setName($scope.region);
		};
		$scope.init();
			
		/**=================================================查詢============================================================**/
		$scope.query = function() {
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
			$scope.sendRecv("CRM3111", "inquire", "com.systex.jbranch.app.server.fps.crm3111.CRM3111InputVO",$scope.inputVO,
					function(tota, isError) {
					   if(!isError){
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
								return;
							}
						   $scope.resultList = tota[0].body.resultList;			
						   $scope.outputVO = tota[0].body;
					   }
				});
			
		};
		
	    $scope.goCRM610 = function(row){
	    	$scope.CRM_CUSTVO = {
					CUST_ID :  row.CUST_ID,
					CUST_NAME :row.CUST_NAME
			}
			$scope.connector('set','CRM_CUSTVO',$scope.CRM_CUSTVO);
	    	var path = "assets/txn/CRM610/CRM610_MAIN.html";
			$scope.connector("set","CRM610URL",path);
			
			var dialog = ngDialog.open({
				template: 'assets/txn/CRM610/CRM610.html',
				className: 'CRM610',
				showClose: false
			});
	    };
		
		
});