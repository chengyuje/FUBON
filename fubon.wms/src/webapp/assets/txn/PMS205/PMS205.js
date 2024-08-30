/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS205Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, projInfoService, $filter) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS205Controller";
		
		// filter
	
		$scope.init = function(){
			$scope.inputVO = {
        			camId: '',
        			camName: '',
        			MAIN_COM_NBR: '',
        			REL_COM_NBR: '',
        			eTime:undefined,
        			sTimes:undefined,
        			eTimes:undefined,
        			checkgi:'2',
        			es:''
        				
        	};
			$scope.paramList = [];
		};
        $scope.init();
        
        $scope.inquireInit = function(){
			$scope.initLimit();
			$scope.paramList = [];						
		}
		$scope.inquireInit();
		
		$rootScope.$on("rootEvent", function(e,data){
			 if(data==-1)
				 $scope.inputVO.MAIN_COM_NBR='';
			 else
				 $scope.inputVO.MAIN_COM_NBR=data;
		
		});

        
        
    	$scope.isq = function(){
    	    
            var NowDate=new Date();         
            var t=NowDate.setMonth(NowDate.getMonth()-5);    
            var susdate=new Date(t);
            var y=susdate.getFullYear();
            var m=susdate.getMonth();            
            var xm='';
            $scope.mappingSet['timeE'] = [];
            for(var i=0;i<7;i++){
            	m = m+1;
            	if(m>12)
            	{
            		m=1;
            		y=y+1;            		
            	}
            	if(m<=9){xm='0'+m;}
            	if(m>=10){xm=m;}
            	
            		$scope.mappingSet['timeE'].push({
    					LABEL : y+'/'+xm,
    					DATA : y+''+xm
            		});            		           		
            }
        	
          //config
    		$scope.model = {};
    		$scope.open = function($event, elementOpened) {
    			$event.preventDefault();
    			$event.stopPropagation();
    			$scope.model[elementOpened] = !$scope.model[elementOpened];
    		};  
    		$scope.limitDate = function() {
    			$scope.bgn_sDateOptions.maxDate =  Today;
    		};
		
    	
    	
    	};
        $scope.isq();
        
        
        
        //刪除按鈕
        $scope.del = function(row){
   
        	$scope.inputVO.eTime_DEL=row.DATA_YEARMON;
        	$scope.inputVO.MAIN_COM_NBR_DEL=row.MAIN_COM_NBR;
        	$scope.inputVO.es = row.PRD_ID;
        
        	$confirm({text: '是否刪除此筆資料!!'}, {size: 'sm'}).then(function() {
        	$scope.sendRecv("PMS205","delRes", "com.systex.jbranch.app.server.fps.pms205.PMS205InputVO", $scope.inputVO,
	    			function(totas, isError) {
		                if (isError) {
		                	$scope.showErrorMsgInDialog(totas.body.msgData);
		                    return;
		                }
		                 if (totas.length > 0) {
		                	 $scope.inquire();
		                	$scope.showMsg('刪除成功');
		                 };
		            }
	        	);
        	});
        }
        

        $scope.inquire = function(){
//        	alert(JSON.stringify($scope.inputVO.eTimes));
        	
			$scope.sendRecv("PMS205", "queryData", "com.systex.jbranch.app.server.fps.pms205.PMS205InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList=[];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.outputVO = tota[0].body;
						  
							$scope.resulttest = [];
							angular.forEach($scope.paramList, function(row) {
								if(row.PRD_ID== '1'){
									$scope.resulttest.push({LABEL: '基金', DATA: row.PRD_ID});
								} else {
									$scope.resulttest.push({LABEL: '保險', DATA: row.PRD_ID});
								}
							});

						
							return;
						}
			});
		};
		
        $scope.qu=function(MAIN_COM_NBR,checkgi){
        	var dialog = ngDialog.open({			      
		        template: 'assets/txn/PMS205/PMS205_QUERY.html',
		        className: 'PMS205_QUERY',
		        controller: ['$scope', function($scope) {
		        	$scope.checkgi=checkgi;
		        	$scope.INS_ID=MAIN_COM_NBR;
//		        	alert(JSON.stringify($scope.checkgi));
		        	//$scope=$scope.inputVO.INS_ID;
		        }]
            });     
         }  

	
        $scope.edit = function () {
        	$scope.inputVO.sTimes=$filter('date')($scope.inputVO.sTimes,'yyyyMMdd');
        	$scope.inputVO.eTimes=$filter('date')($scope.inputVO.eTimes,'yyyyMMdd');
//        	alert(JSON.stringify($scope.inputVO.sTimes));
        	
        	$scope.sendRecv("PMS205", "ddlModify", "com.systex.jbranch.app.server.fps.pms205.PMS205InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							
							if (isError) {
    	                		$scope.showErrorMsg(tota[0].body.msgData);
    	                	}
    	                	if (tota.length > 0) {
    	                		$scope.inquire();
    	                		$scope.showSuccessMsg('新增成功');
    	                	};
							return;
						}
			});
        }
		
});
