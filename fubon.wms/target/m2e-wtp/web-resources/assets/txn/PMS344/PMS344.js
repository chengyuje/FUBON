/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS344Controller',
	function($rootScope, $scope, $controller, $confirm, socketService, ngDialog, $filter, projInfoService, sysInfoService) {
		$controller('BaseController', {$scope: $scope});
		$scope.controllerName = "PMS344Controller";
		var c=0;
		// 繼承共用的組織連動選單
		$controller('PMSRegionController', {$scope: $scope});
		// filter
		$scope.dateChange = function(){
			if($scope.inputVO.sCreDate!=undefined){
				$scope.inputVO.reportDate = $filter('date')($scope.inputVO.sCreDate,'yyyyMMdd');
				$scope.RegionController_getORG($scope.inputVO);	
			}
		};
		
		$scope.limitDate = function(){
//			$scope.inputVO.sCreDate = $filter('date')($scope.inputVO.datelist,'yyyyMMdd');
			$scope.inputVO.sCreDate = $scope.toJsDate($scope.inputVO.datelist);
		};
		
		$scope.initLoad = function()
		{
			$scope.sendRecv("PMS344", "queryDataDate", "com.systex.jbranch.app.server.fps.pms344.PMS344InputVO", {},
				function(totas, isError) 
				{
					if (totas.length > 0) 
					{
					     $scope.ymList = totas[0].body.DateList;
					};
				}
			);
		}
		$scope.initLoad();
		
		//alert(sysInfoService.getPriID());
		
		$scope.dateChangeForTypeList = function(){
			var priID = sysInfoService.getPriID();
			var delIndexType;
			$scope.mappingSet['element']=[];			

			if (priID == '002' || priID == '003'){ //理專
				$scope.mappingSet['element'].push(
						{LABEL:'AO庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_AO'},
						{LABEL:'AO贖回量TOP10排名', DATA:'TBPMS_TOP10_RDMP_AO'},
						{LABEL:'AO申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_AO'});
			}
			else if (priID == '006' || priID == '009' || priID == '011'){ //作業主管 業務主管 分行個金主管
				$scope.mappingSet['element'].push(
						{LABEL:'AO庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_AO'},
						{LABEL:'AO贖回量TOP10排名', DATA:'TBPMS_TOP10_RDMP_AO'},
						{LABEL:'AO申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_AO'},						
						{LABEL:'分行庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_BR'},
						{LABEL:'分行贖回量TOP10排名', DATA: 'TBPMS_TOP10_RDMP_BR'},
						{LABEL:'分行申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_BR'});
			}
			else if (priID == '012'){ //營運督導
				$scope.mappingSet['element'].push(
						{LABEL:'AO庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_AO'},
						{LABEL:'AO贖回量TOP10排名', DATA:'TBPMS_TOP10_RDMP_AO'},
						{LABEL:'AO申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_AO'},						
						{LABEL:'分行庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_BR'},
						{LABEL:'分行贖回量TOP10排名', DATA: 'TBPMS_TOP10_RDMP_BR'},
						{LABEL:'分行申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_BR'},
						{LABEL:'營運區庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_OP'},
						{LABEL:'營運區贖回量TOP10排名', DATA: 'TBPMS_TOP10_RDMP_OP'},
						{LABEL:'營運區申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_OP'});		
			}
			else{
				$scope.mappingSet['element'].push(
						{LABEL:'AO庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_AO'},
						{LABEL:'AO贖回量TOP10排名', DATA:'TBPMS_TOP10_RDMP_AO'},
						{LABEL:'AO申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_AO'},						
						{LABEL:'分行庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_BR'},
						{LABEL:'分行贖回量TOP10排名', DATA: 'TBPMS_TOP10_RDMP_BR'},
						{LABEL:'分行申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_BR'},
						{LABEL:'營運區庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_OP'},
						{LABEL:'營運區贖回量TOP10排名', DATA: 'TBPMS_TOP10_RDMP_OP'},
						{LABEL:'營運區申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_OP'},
						{LABEL:'業務處贖回量TOP10排名', DATA: 'TBPMS_TOP10_RDMP_RC'},
						{LABEL:'業務處庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_RC'},							
						{LABEL:'業務處申購量TOP10排名', DATA: 'TBPMS_TOP10_SALE_RC'});				
			}
				
		};
	
        $scope.init2 = function(){
			$scope.paramList = [];
			$scope.paramList2=	[];
			$scope.checked='1';
			
		};
        $scope.init2();
    
        
        $scope.model = {};
    	$scope.open = function($event, elementOpened) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.model[elementOpened] = !$scope.model[elementOpened];
		};		
		
        /***ORG COMBOBOX END***/
    	$scope.init = function(){
			$scope.inputVO = {
					eTime: undefined,        		   		
					ao_code  :'',
					branch  :'',
					region  :'',
					op      :'',
					aocoded  :false,
					branchd  :false,
					opd      :false,
					sCreDate :undefined,
					element :'',
					flag:'0',
        	};
			$scope.paramList = [];
			$scope.paramList2=	[];
			$scope.checked='1';
			//新版可視範圍用
			$scope.disableRegionCombo = false;
			$scope.disableAreaCombo = false;
			$scope.disableBranchCombo = false;
			$scope.disableAoCombo = false;
//			$scope.dateChange();

		};
        $scope.init();
		
        

        
        $scope.chance = function() {
        	 $scope.master = angular.copy($scope.inputVO);
        	 $scope.inputVO.ao_code='';
        	 $scope.inputVO.branch_nbr='';        	
        	 $scope.inputVO.branch_area_id='';
        	 $scope.inputVO.region_center_id='';
			if( $scope.inputVO.element=='TBPMS_TOP10_RDMP_AO' || $scope.inputVO.element=='TBPMS_TOP10_SALE_AO' ||  $scope.inputVO.element=='TBPMS_TOP10_STOCK_AO')
				{
				 $scope.inputVO.aocoded=false;
	        	 $scope.inputVO.branchd=false;
	        	 $scope.inputVO.opd=false;
	        	 $scope.inputVO.ao_code=$scope.master.ao_code;
	        	 $scope.inputVO.branch_nbr=$scope.master.branch_nbr;        	
	        	 $scope.inputVO.branch_area_id=$scope.master.branch_area_id;
	        	 $scope.inputVO.region_center_id=$scope.master.region_center_id;
				
				}
			
			if( $scope.inputVO.element=='TBPMS_TOP10_RDMP_BR' || $scope.inputVO.element=='TBPMS_TOP10_SALE_BR' ||  $scope.inputVO.element=='TBPMS_TOP10_STOCK_BR')
			{
			 $scope.inputVO.aocoded=true;
        	 $scope.inputVO.branchd=false;        	 
        	 $scope.inputVO.opd=false;
        	 $scope.inputVO.branch_nbr=$scope.master.branch_nbr;        	
        	 $scope.inputVO.branch_area_id=$scope.master.branch_area_id;
        	 $scope.inputVO.region_center_id=$scope.master.region_center_id;
			
			}
			if( $scope.inputVO.element=='TBPMS_TOP10_RDMP_OP' || $scope.inputVO.element=='TBPMS_TOP10_SALE_OP' ||  $scope.inputVO.element=='TBPMS_TOP10_STOCK_OP')
			{
			 $scope.inputVO.aocoded=true;
        	 $scope.inputVO.branchd=true;        	
        	 $scope.inputVO.opd=false;
        	 $scope.inputVO.branch_area_id=$scope.master.branch_area_id;
        	 $scope.inputVO.region_center_id=$scope.master.region_center_id;
			
			}
			if( $scope.inputVO.element=='TBPMS_TOP10_RDMP_RC' || $scope.inputVO.element=='TBPMS_TOP10_SALE_RC' ||  $scope.inputVO.element=='TBPMS_TOP10_STOCK_RC')
			{
			 $scope.inputVO.aocoded=true;
        	 $scope.inputVO.branchd=true;        	
        	 $scope.inputVO.opd=true;
        	 $scope.inputVO.region_center_id=$scope.master.region_center_id;
			
			}
				
				
				
        };
        
        
        
        
	$scope.export = function() {
			angular.forEach($scope.paramList2, function(row, index, objs){
				if(row.ROR!=undefined)
					row.ROR=row.ROR*100;
			});
			$scope.sendRecv("PMS344", "export",
					"com.systex.jbranch.app.server.fps.pms344.PMS344OutputVO",
					{'List':$scope.paramList2,'element':$scope.inputVO.element}, function(tota, isError) {
						if (!isError) {
							return;
						}
					});
		};
   
        
        $scope.inquire = function(){
        	if($scope.parameterTypeEditForm.$invalid) {
        		$scope.showErrorMsgInDialog('欄位檢核錯誤:報表類型必要輸入欄位');
        		return;
        	}
        	if($scope.inputVO.eTime!=undefined)
        		$scope.inputVO.eTime=$scope.inputVO.eTime.getFullYear()+$scope.inputVO.eTime.getMonth();
//        	{LABEL:'AO庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_AO'},
//			{LABEL:'分行庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_BR'},
//			{LABEL:'營運區庫存TOP10排名', DATA:'TBPMS_TOP10_STOCK_OP'},
//			{LABEL:'業務處庫存TOP10排名', DATA: 'TBPMS_TOP10_STOCK_RC'},	
        	if($scope.inputVO.element=='TBPMS_TOP10_STOCK_AO' || $scope.inputVO.element=='TBPMS_TOP10_STOCK_BR' || $scope.inputVO.element=='TBPMS_TOP10_STOCK_OP' || $scope.inputVO.element=='TBPMS_TOP10_STOCK_RC'  )
        		$scope.inputVO.flag='1';
        	else
        		$scope.inputVO.flag='0';
//        	$scope.inputVO.region=$scope.inputVO.rc_id;
//        	$scope.inputVO.branch=$scope.inputVO.br_id;
//        	$scope.inputVO.op=$scope.inputVO.op_id;
        	$scope.sendRecv("PMS344","queryData", "com.systex.jbranch.app.server.fps.pms344.PMS344InputVO", $scope.inputVO,
					function(tota, isError) {
						if (!isError) {
							if(tota[0].body.resultList.length == 0) {
								$scope.paramList = [];
								$scope.paramList2 = [];
								$scope.showMsg("ehl_01_common_009");
	                			return;
	                		}
							$scope.paramList = tota[0].body.resultList;
							$scope.inputVO.eTime=undefined;
							$scope.paramList2 = tota[0].body.resultList2;
							
							$scope.outputVO= tota[0].body;
						
							return;
						}
			});
		};
	
  		
});
