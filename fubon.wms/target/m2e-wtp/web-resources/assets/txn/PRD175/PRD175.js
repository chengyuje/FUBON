/**
 * 
 */
'use strict';
eSoafApp.controller('PRD175Controller', 
    function($rootScope,$scope,$controller, $confirm, sysInfoService,socketService, ngDialog, projInfoService,$filter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD175Controller";
        
      
        
        //變換查詢table清空資料
        $scope.cllit=function(){
        	$scope.ins_ancdoclist=[];  //清除資料
        	$scope.outputVO=[];
        	$scope.paramList=[]; //清空分
        }
        
        $scope.model = {};
    	$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};
		 
		//初始化
        $scope.init = function(){
        	$scope.inputVO = {
        		
        		
        		REG_DATE:undefined,
        		BRANCH_NBR   :'',
        		EMP_NAME     :'',
        		EMP_ID       :'',
        	
      		
        	};
        	  $scope.check=true;
        };
        $scope.init();        
        $scope.btnClear = function(){
        	$scope.init();
        }
     
        $scope.inpet= function(){
        	//題目類型
        	console.log('1=' +$scope.inputVO.BRANCH_NBR);
        	console.log('2=' +$scope.inputVO.EMP_NAME);
        	console.log('3=' +$scope.inputVO.EMP_ID);
        	console.log('4=' +$scope.inputVO.REG_DATE);
        	if($scope.inputVO.BRANCH_NBR==undefined)
        		$scope.inputVO.BRANCH_NBR='';
        	if($scope.inputVO.EMP_ID==undefined)
        		$scope.inputVO.EMP_ID='';
        	if($scope.inputVO.EMP_NAME==undefined)
        		$scope.inputVO.EMP_NAME='';
 
        	if($scope.inputVO.BRANCH_NBR=='' 
        		&& $scope.inputVO.EMP_NAME=='' && $scope.inputVO.EMP_ID==''  
        			&& $scope.inputVO.REG_DATE==undefined)
        		$scope.check=true;
        	else
        		$scope.check=false;
        	
        }
        $scope.inpet();
        
        $scope.mapData = function(){
        	//題目類型
        	$scope.ngDatasource = projInfoService.mappingSet["PRD.INS_ANCDOC_Q_TYPE"];
			var comboboxInputVO = {'param_type': "PRD.INS_ANCDOC_Q_TYPE", 'desc': false};
			$scope.requestComboBox(comboboxInputVO, function(totas) {
					$scope.typelist = totas[0].body.result;
			});
        }
        $scope.mapData();
        
        //預設$scope.check=false;
       
      
        
		//查詢
        $scope.queryData = function(){
        	if($scope.parameterTypeEditForm.$invalid){
	    		$scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
        		return;
        	}
        	$scope.sendRecv("PRD175","queryData","com.systex.jbranch.app.server.fps.prd175.PRD175InputVO",
        			$scope.inputVO,function(tota,isError){
        				if(!isError){
                			$scope.ins_ancdoclist = tota[0].body.INS_ANCDOCList;
                			$scope.outputVO = tota[0].body;
        					if($scope.ins_ancdoclist.length == 0){
                				$scope.showMsg("ehl_01_common_009");
                    			return;
                			}
                			
							return;
        				}
        	});
        };
        

    
        
 

        
	}     
);