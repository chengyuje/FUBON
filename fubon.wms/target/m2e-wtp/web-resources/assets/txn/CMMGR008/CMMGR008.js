/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
*/
'use strict';
eSoafApp.controller('CMMGR008_Controller',
    function($scope, $controller, socketService, ngDialog, projInfoService, sysInfoService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR008_Controller";
        $scope.isCollapsed = false;
        
        // date picker
		$scope.dtfStartDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.dtfEndDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		// config
		$scope.model = {};
		$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		$scope.limitDate = function() {
			$scope.dtfStartDateOptions.maxDate = $scope.inputVO.dtfEndDate || $scope.maxDate;
			$scope.dtfEndDateOptions.minDate = $scope.inputVO.dtfStartDate || $scope.minDate;
		};
		// date picker end
        
        $scope.init = function(){
        	$scope.mappingSet['brchID'] = [];
         	$scope.inputVO = {
         			dtfStartDate: undefined,
         			dtfEndDate: undefined,
         			tipBrchId: '',
         			tipWsId: '',
         			tipTxnCode: '',
         			tipTellerId: '',
         			tipRoleId: '',
         			tipCustomerId: '',
         			tipCustomerName: '',
         			tipBizcodeName: '',
         			tipMemo: '',
         	};        	
        }  
        $scope.init();
        
//      初始分頁資訊
        $scope.inquireInit = function(){
        	$scope.initLimit();
        	$scope.paramList = [];
        }
        
        $scope.inquireInit();
        
   	    $scope.exportData = function(){
  			$scope.inputVO.varCsvType = $scope.CSV_TYPE;            
  			$scope.sendRecv("CMMGR008", "export",
  					"com.systex.jbranch.app.server.fps.cmmgr008.CMMGR008InputVO",
  					$scope.inputVO, function(tota, isError) {
  						if (!isError) {
  							$scope.adgParameter = tota[0].body.adgParameter;
  							console.log('$scope.adgParameter='+JSON.stringify($scope.adgParameter));
  							return;
  						}
  					});
  		}
         $scope.inquire = function(){	
        	  //可選最小日期
//		  	  $scope.inputVO.startDate=new Date();
// 			  $scope.inputVO.startDate.setMonth($scope.inputVO.startDate.getMonth() - 1);
 			  
//	  		  if($scope.inputVO.dtfStartDate == undefined){
//	  			$scope.inputVO.dtfStartDate = $scope.inputVO.startDate;
//	  		  }
//	  		  if($scope.inputVO.dtfStartDate < $scope.inputVO.startDate){
//  				  $scope.dateExceed=new Date($scope.inputVO.startDate - $scope.inputVO.dtfStartDate);
//  				  $scope.showErrorMsg('eh1_02_common_068'+','+'查詢日期超過'+ ($scope.dateExceed.getMonth()+1) +"個月");
//	  			  return;
//	  		  }
        	 if($scope.inputVO.dtfEndDate != undefined && $scope.inputVO.dtfStartDate != undefined){
	        	 $scope.dateExceed = new Date($scope.inputVO.dtfEndDate - $scope.inputVO.dtfStartDate);
	        	 if($scope.dateExceed.getMonth() > 0){
	        		 $scope.showErrorMsg('ehl_02_common_068', [1]); //查詢日期超過1個月
		        	 return;
	        	 }
        	 }
        	 
	    	  $scope.sendRecv("CMMGR008", "inquire", "com.systex.jbranch.app.server.fps.cmmgr008.CMMGR008InputVO", $scope.inputVO,
	                  function(tota, isError) {
	                      if (!isError) {	
//	                    	  分頁改寫
	                    	  $scope.pagingList($scope.paramList, tota[0].body.dataList);
	                          $scope.outputVO = tota[0].body;
	                          return;
	                      }
	                  });
	     }
         
      // 取得區域&分行CMB
			$scope.getDataSource = function() {
				$scope.mappingSet['avilBranchList'] = [];
				$scope.mappingSet['avilAreaList'] = [];

				$scope.branchList = sysInfoService.getAvailBranch();
				for (var i = 0; i < $scope.branchList.length; i++) {
					$scope.mappingSet['brchID'].push({
						DATA : $scope.branchList[i].BRANCH_NBR,
						LABEL : $scope.branchList[i].BRANCH_NAME
					});
				}
			}
			$scope.getDataSource();
			
			//角色
			$scope.getHeadRoleList = function() {
				$scope.sendRecv("CMMGR008", "getRoleList", "com.systex.jbranch.app.server.fps.cmmgr008.CMMGR008InputVO", $scope.inputVO,
		                  function(tota, isError) {
		                      if (!isError) {	
		                    	  $scope.RoleList = tota[0].body.RoleList;
		                          return;
		                      }
		                  });
			};
			$scope.getHeadRoleList();
			
    }
);