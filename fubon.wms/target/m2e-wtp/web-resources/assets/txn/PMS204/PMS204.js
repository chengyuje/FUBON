/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS204Controller',
	function($rootScope, $scope, $controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,getParameter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS204Controller";
		
		getParameter.XML(["PMS.AO_JOB_RANK"], function(totas) {
			if (totas) {
				$scope.mappingSet['PMS.AO_JOB_RANK'] = totas.data[totas.key.indexOf('PMS.AO_JOB_RANK')];
			}
		});
			
		$scope.genBranch = function() {
			$scope.mappingSet['branchsDesc'] = [];
			angular.forEach(projInfoService.getAvailBranch(), function(row, index, objs){
				$scope.mappingSet['branchsDesc'].push({LABEL: row.BRANCH_NAME, DATA: row.BRANCH_NBR});
			});
        };
        $scope.genBranch();
		// filter
		$scope.init = function(){
			$scope.inputVO = {
					MTD_ACH_RATE_S:'',
        			MTD_ACH_RATE_E:'',
        			JOB_TITLE_ID:'',
        			APOINT:'',
        			BPOINT:'',
        			CPOINT:'',
        			DPOINT:'',
        			EMP_ID:'',
        			COACHING_STATE:'',
        			camId: '',
        			camName: '',
        			MAIN_COM_NBR: '',
        			REL_COM_NBR: '',
        			eTime:'',
        			SEQ:''
        				
        	};
			$scope.paramList = [];			
		};
        $scope.init();
        
        /***年月***/
        var NowDate=new Date();               
//        NowDate.setYear(2017);
//		NowDate.setMonth(6);
        var t=NowDate.setMonth(NowDate.getMonth()-12);           
  
        var susdate=new Date(t);
        var y=susdate.getFullYear();
        var m=susdate.getMonth();
        var xm='';
        $scope.mappingSet['timeM'] = [];
        for(var i=0;i<13;i++){
        	if(m<=9){xm='0'+m;}
        	if(m>=10){xm=m;}            	
        		$scope.mappingSet['timeM'].push({
					LABEL : y+'/'+xm,
					DATA : y+''+xm
        		});            		
        		if(m<=11){	
        			m=m+1;               		
        		}
        		if(m==12){
        			m=1;
        			y=y+1;            		
        		}
         }
        /***年月END***/
        $scope.mappingSet['timeM'].reverse();  
         
        
        var vo = {'param_type': 'PMS.COACHING_POINT', 'desc': false};
        if(!projInfoService.mappingSet['PMS.COACHING_POINT']) {
        	$scope.requestComboBox(vo, function(totas) {
        	
        		if (totas[totas.length - 1].body.result === 'success') {
        		
        			projInfoService.mappingSet['PMS.COACHING_POINT'] = totas[0].body.result;
        			$scope.mappingSet['PMS.COACHING_POINT'] = projInfoService.mappingSet['PMS.COACHING_POINT'];
        		
        		}
        	});
        } else
        	$scope.mappingSet['PMS.COACHING_POINT'] = projInfoService.mappingSet['PMS.COACHING_POINT'];
        
        
        var vo = {'param_type': 'PMS.COACHING_STATE', 'desc': false};
        if(!projInfoService.mappingSet['PMS.COACHING_STATE']) {
        	$scope.requestComboBox(vo, function(totas) {
        	
        		if (totas[totas.length - 1].body.result === 'success') {
        		
        			projInfoService.mappingSet['PMS.COACHING_STATE'] = totas[0].body.result;
        			$scope.mappingSet['PMS.COACHING_STATE'] = projInfoService.mappingSet['PMS.COACHING_STATE'];
        		
        		}
        	});
        } else
        	$scope.mappingSet['PMS.COACHING_STATE'] = projInfoService.mappingSet['PMS.COACHING_STATE'];
   
        
        //刪除按鈕
        $scope.del = function(row){
        $scope.inputVO.MTD_ACH_RATE_S=row.MTD_ACH_RATE_S;
        $scope.inputVO.MTD_ACH_RATE_E=row.MTD_ACH_RATE_E;
        $scope.inputVO.JOB_TITLE_ID=row.JOB_TITLE_ID;        
        $scope.inputVO.APOINT=row.COACHING_POINT_A;
        $scope.inputVO.BPOINT=row.COACHING_POINT_B;
        $scope.inputVO.CPOINT=row.COACHING_POINT_C;
        $scope.inputVO.DPOINT=row.COACHING_POINT_D;
		$scope.inputVO.COACHING_STATE=row.COACHING_FREQ;
		$scope.inputVO.PLAN_YEARMON=row.YEARMON;
        	
        $scope.sendRecv("PMS204","delRes", "com.systex.jbranch.app.server.fps.pms204.PMS204InputVO", $scope.inputVO,
	    			function(totas, isError) {
		                if (isError) {
		                	$scope.showErrorMsgInDialog(totas.body.msgData);
		                    return;
		                }
		                 if (totas.length > 0) {
		                	$scope.showMsg('刪除成功');
		                	$scope.inquire2();
		                 };
		            }
	        	);
        }
        
        $scope.inquire = function(){
			$scope.sendRecv("PMS204", "queryData", "com.systex.jbranch.app.server.fps.pms204.PMS204InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							$scope.mappingSet['PMS.COACHING_POINT'] = projInfoService.mappingSet['PMS.COACHING_POINT'];
							return;
						}
			});
		};
		$scope.inquire();
		
		$scope.inquire2 = function(){
			$scope.sendRecv("PMS204", "queryData", "com.systex.jbranch.app.server.fps.pms204.PMS204InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
							if(tota[0].body.resultList.length == 0) {
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
							$scope.mappingSet['PMS.COACHING_POINT'] = projInfoService.mappingSet['PMS.COACHING_POINT'];
							return;
						}
			});
		};

		 
		 $scope.edit = function (row) {
             var dialog = ngDialog.open({          
                 template: 'assets/txn/PMS204/PMS204_EDIT.html',
                 className: 'PMS204_EDIT',
                 controller: ['$scope', function($scope) {
               	  $scope.row = row;
                 }]
             });
           
             dialog.closePromise.then(function (data) {
                 if(data.value === 'successful'){
               	  $scope.inquire();
                 }
             });
         };        
         
         $scope.insert = function (row) {
            var dialog = ngDialog.open({
                template: 'assets/txn/PMS204/PMS204_INSERT.html',
                className: 'PMS204_INSERT',
                controller: ['$scope', function($scope) {
              	  $scope.row = row;
                }]
            });
          
            dialog.closePromise.then(function (data) {
                if(data.value === 'successful'){
              	  $scope.inquire();
                }
            });
        };
		
});
