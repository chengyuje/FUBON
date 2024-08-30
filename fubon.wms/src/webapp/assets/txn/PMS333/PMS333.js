/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS333Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS333Controller";
		$controller('PMSRegionController', {$scope: $scope});
		// combobox
	
		$scope.init = function(){
			$scope.inputVO = {
					sCreDate :'',
					reportDate :'',
					ao_code  :'',
					region_center_id  :'',
					branch_area_id  :'',
					branch_nbr      :'',
					dataMonth:''
			};
			$scope.startMaxDate = $scope.maxDate;
            $scope.endMinDate = $scope.minDate;
            $scope.paramList2 = [];
            $scope.paramList = [];
		};
		$scope.init();
		
		var NowDate = new Date();
		var yr = NowDate.getFullYear();
        var mm = NowDate.getMonth()+2;
        var strmm='';
        $scope.mappingSet['timeE'] = [];
        for(var i=0; i<36; i++){
        	mm = mm -1;
        	if(mm == 0){
        		mm = 12;
        		yr = yr-1;
        	}
        	if(mm<10)
        		strmm = '0' + mm;
        	else
        		strmm = mm;        		
        	$scope.mappingSet['timeE'].push({
        		LABEL: yr+'/'+strmm,
        		DATA: yr +''+ strmm
        	});        
        }
        
        $scope.numGroups = function(input){
        	if(input == null)
        		return;
            return Object.keys(input).length;
           }
         
        $scope.getSum = function(group, key) {
            var sum = 0;
            for (var i = 0; i < group.length; i++){
             sum += group[i][key];
            }  
            return sum;
           }
        
        $scope.filter2 = function(a){
            if (a.state == null){
                return true;
            } else{
                return;
            }
        };
		
      //選取月份下拉選單 --> 重新設定可視範圍
        
        $scope.dateChange = function(){
        	var d = new Date();
        	var yyyy = d.getFullYear()+"";
            var mm = (d.getMonth()+1)<10?"0"+(d.getMonth()+1):(d.getMonth()+1)+"";
            var dd = d.getDate()<10?"0"+d.getDate():d.getDate()+"";
            
            $scope.inputVO.sCreDate = yyyy+mm+dd;
    		$scope.inputVO.reportDate = yyyy+mm+dd;
    		$scope.RegionController_getORG($scope.inputVO);        
        }; 	
        $scope.dateChange();
        
		
	    $scope.export = function() {
			$scope.sendRecv("PMS333", "export",
					"com.systex.jbranch.app.server.fps.pms333.PMS333OutputVO",
					{'list':$scope.csvList}, function(tota, isError) {
						if (!isError) {
								return;
						}
					});
		};
		
		
		
		$scope.inquire = function(){
			if ($scope.inputVO.branch_area_id=='') {
				$scope.showErrorMsgInDialog('欄位檢核錯誤:營運區為必要輸入欄位');
				return;
			}
			//預設為當日(即時)
			var d = new Date();
        	var yyyy = d.getFullYear()+"";
            var mm = (d.getMonth()+1)<10?"0"+(d.getMonth()+1):(d.getMonth()+1)+"";
            var dd = d.getDate()<10?"0"+d.getDate():d.getDate()+"";
            
            $scope.inputVO.sCreDate = yyyy+mm+dd;
            $scope.inputVO.reportDate = yyyy+mm+dd;
            
			$scope.sendRecv("PMS333", "inquire", "com.systex.jbranch.app.server.fps.pms333.PMS333InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.csvList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							angular.forEach($scope.paramList, function(row, index, objs){
								if(row.REGION_CENTER_NAME == null){
									row.REGION_CENTER_NAME  = "";								
								}
							});	
							$scope.csvList=tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							return;
						}
			});
		};
		$scope.detail = function (ind,row) {
			var dialog = ngDialog.open({
				template: 'assets/txn/PMS333/PMS333_DETAIL.html',
				className: 'PMS333_DETAIL',
				showClose: false,
                controller: ['$scope', function($scope) {
                	row.ind=ind;
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
		
	
		
		
		
});
