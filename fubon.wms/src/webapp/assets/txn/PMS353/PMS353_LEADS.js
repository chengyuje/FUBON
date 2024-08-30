/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS353_LEADSController',
	function($rootScope, $scope, $controller, socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS353_LEADSController";
	
		$scope.row=$scope.row || {};
        
		/**upload csv files**/
		$scope.uploadFinshed = function(name, rname) {
        	if(name){
        		$scope.inputVO.fileName = name;
            	$scope.inputVO.realFileName = rname;
            	$scope.inputVO.uploadFlag = 'Y';
        	}
		};
		
        
        //日自
    	// date picker
		// 名單建立日期
		$scope.sDateOptions = {
			maxDate: $scope.maxDate,
			minDate: $scope.minDate
		};
		$scope.eDateOptions = {
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
			$scope.sDateOptions.maxDate = $scope.inputVO.eDate || $scope.maxDate;
			$scope.eDateOptions.minDate = $scope.inputVO.sDate || $scope.minDate;
			var after = $scope.maxDate;
			if($scope.inputVO.sDate)
				after = new Date(($scope.inputVO.sDate.getFullYear()+1),$scope.inputVO.sDate.getMonth(),$scope.inputVO.sDate.getDate());
			$scope.eDateOptions.maxDate = after;
			if($scope.inputVO.sDate || $scope.inputVO.eDate || $scope.inputVO.sEDate || $scope.inputVO.eEDate)
				$scope.date = false;
		};

		// date picker end
	
		$scope.init = function(){
			$scope.inputVO = {
				
					
					PRJ_SEQ:$scope.row.PRJ_SEQ || '',       //專案編號
					PRD_ID:$scope.row.PRJID || '',       //序號
					PRJ_NAME:$scope.row.PRJ_NAME || '',      //專案名稱
					sDate:new Date($scope.row.START_DT)  || undefined,      //活動起始日
					eDate:new Date($scope.row.END_DT)  || undefined,      //活動結束日
					BT_DT:$scope.row.BT_DT || '' ,     //更新日期
					uploadFlag:'N'
        	};
			
			$scope.limitDate();
		
		};
        $scope.init();
        
        
		$rootScope.$on("rootEvent", function(e,data){
			 if(data==-1)
				 $scope.inputVO.PRD_ID='';
			 else
				 $scope.inputVO.PRD_ID=data;
		
		});

        

		$scope.detail = function (row) {
			row.PRJ_SEQ=$scope.inputVO.PRJ_SEQ;
			row.MAX=$scope.row.MAX;
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS353/PMS353_DETAIL.html',
				className: 'PMS353_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                
                	$scope.row = row;
                }]
			});
			dialog.closePromise.then(function (data) {
				if(data.value === 'successful'){
				
					$scope.inquireInit();
					$scope.inquire();
				
				}
			});
		};
        
   
		//放大鏡查詢
        $scope.qu=function(PRD_ID){
        	var dialog = ngDialog.open({			      
		        template: 'assets/txn/PMS353/PMS353_QUERY.html',
		        className: 'PMS353_QUERY',
		        controller: ['$scope', function($scope) {
//		        	$scope.checkgi=checkgi;
		        	$scope.INS_ID=PRD_ID;
//		        	alert(JSON.stringify(PRD_ID));
		        	//$scope=$scope.inputVO.INS_ID;
		        }]
            });     
         }  

        
        $scope.save = function() {
        	if($scope.parameterTypeEditForm.$invalid){
			    $scope.showErrorMsg('ehl_01_common_022');
        		return;
        	}
         
        	if($scope.row.NUM==1)
        	{
        
				$scope.sendRecv("PMS353","addRPT","com.systex.jbranch.app.server.fps.pms353.PMS353InputVO",
								$scope.inputVO, function(tota, isError) {
									if (!isError) {
										$scope.showMsg("新增成功");
										$scope.closeThisDialog('save')
										return;
									}
								});
        	}
        	if($scope.row.NUM==2)
        	{
        		$scope.sendRecv("PMS353","updateRPT","com.systex.jbranch.app.server.fps.pms353.PMS353InputVO",
						$scope.inputVO, function(tota, isError) {
							if (!isError) {
								$scope.showMsg("修改成功");
								$scope.closeThisDialog('save')
								return;
							}
						});
        		
        	}
			
		}

});
