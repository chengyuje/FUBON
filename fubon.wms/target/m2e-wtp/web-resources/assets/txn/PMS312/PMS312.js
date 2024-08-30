/*
  交易畫面邏輯定義
  請修改 Controller 名稱，以符合交易畫面的定義
 */
'use strict';
eSoafApp.controller('PMS312Controller', function($scope, $controller,
		socketService, ngDialog, projInfoService, $q, $confirm, $filter) {
	$controller('BaseController', {
		$scope : $scope
	});
	$scope.controllerName = "PMS312Controller";
	$controller('PMSRegionController', {$scope: $scope});
	$scope.init = function() {

		$scope.inputVO = {
			dataMonth : '',
			region_center_id : '',
			branch_area_id : '',
			branch_nbr : '',
			ao_code : '',
			emp_id : '',
			aocode : '',
			branch : '',
			region : '',
			type2:'1',
			op : '',
			sTime : '201608',
			checked : '1',
			sCreDate :''

		};
		$scope.curDate = new Date();
		$scope.checked = '1';
		$scope.paramList = [];
		$scope.paramList2 = [];
	};
	$scope.init();
	// 月份初始化
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

	$scope.dateChange = function(){
        if($scope.inputVO.sCreDate!=''){ 	
        	$scope.inputVO.reportDate = $scope.inputVO.sCreDate;
        	$scope.RegionController_getORG($scope.inputVO);
        }
    };
	$scope.mappingSet['seture'] = [];
	$scope.mappingSet['seture'].push({
		DATA : '2',
		LABEL : 'YTD'
	}, {
		DATA : '1',
		LABEL : 'MTD'
	});

	$scope.mappingSet['type'] = [];
	$scope.mappingSet['type'].push({
		DATA : '1',
		LABEL : '房貸週報'
	}, {
		DATA : '2',
		LABEL : '信貸週報'
	});
	
	
	$scope.mappingSet['type2'] = [];
	$scope.mappingSet['type2'].push({
		DATA : '1',
		LABEL : '分行'
	}, {
		DATA : '2',
		LABEL : '業務處/營運區'
	});

	$scope.inquireInit = function() {

		$scope.paramList = [];
		$scope.originalList = [];
	}
	$scope.inquireInit();

	/** *new** */
	$scope.numGroups = function(input) {
		if (input == null)
			return;
		return Object.keys(input).length;
	}
	$scope.getSum = function(group, key) {
		var sum = 0;
		for (var i = 0; i < group.length; i++) {
			sum += group[i][key];
		}
		return sum;
	}

	/** *percent** */
	$scope.percent = function() {
		$scope.M_OP_CNT = 0;
		$scope.M_OP_AMT = 0;
		$scope.M_AO_CNT = 0;
		$scope.M_AO_AMT = 0;
		$scope.M_BO_CNT = 0;
		$scope.M_BO_AMT = 0;
		$scope.M_BT_CNT = 0;
		$scope.M_BT_AMT = 0;
		$scope.M_FG_CNT = 0;
		$scope.M_FG_AMT = 0;
		$scope.M_LI_CNT = 0;
		$scope.M_LI_AMT = 0;
		$scope.M_S_CNT = 0;
		$scope.M_S_AMT = 0;
		$scope.M_PC_CNT = 0;
		$scope.M_PC_AMT = 0;
		$scope.M_OT_CNT = 0;
		$scope.M_OT_AMT = 0;
		$scope.M_PS_CNT = 0;
		$scope.M_PS_AMT = 0;
		$scope.M_NBT_CNT = 0;
		$scope.M_NBT_AMT = 0;
		$scope.M_TT_CNT = 0;
		$scope.M_TT_AMT = 0;
		$scope.C_OP_CNT = 0;
		$scope.C_OP_AMT = 0;
		$scope.C_AO_CNT = 0;
		$scope.C_AO_AMT = 0;
		$scope.C_BO_CNT = 0;
		$scope.C_BO_AMT = 0;
		$scope.C_BT_CNT = 0;
		$scope.C_BT_AMT = 0;
		$scope.C_FG_CNT = 0;
		$scope.C_FG_AMT = 0;
		$scope.C_LI_CNT = 0;
		$scope.C_LI_AMT = 0;
		$scope.C_S_CNT = 0;
		$scope.C_S_AMT = 0;
		$scope.C_PC_CNT = 0;
		$scope.C_PC_AMT = 0;
		$scope.C_OT_CNT = 0;
		$scope.C_OT_AMT = 0;
		$scope.C_PS_CNT = 0;
		$scope.C_PS_AMT = 0;
		$scope.C_NBT_CNT = 0;
		$scope.C_NBT_AMT = 0;
		$scope.C_TT_CNT = 0;
		$scope.C_TT_AMT = 0;
		$scope.AM_OP_CNT = 0;
		$scope.AM_OP_AMT = 0;
		$scope.AM_AO_CNT = 0;
		$scope.AM_AO_AMT = 0;
		$scope.AM_BO_CNT = 0;
		$scope.AM_BO_AMT = 0;
		$scope.AM_BT_CNT = 0;
		$scope.AM_BT_AMT = 0;
		$scope.AM_FG_CNT = 0;
		$scope.AM_FG_AMT = 0;
		$scope.AM_LI_CNT = 0;
		$scope.AM_LI_AMT = 0;
		$scope.AM_S_CNT = 0;
		$scope.AM_S_AMT = 0;
		$scope.AM_PC_CNT = 0;
		$scope.AM_PC_AMT = 0;
		$scope.AM_OT_CNT = 0;
		$scope.AM_OT_AMT = 0;
		$scope.AM_PS_CNT = 0;
		$scope.AM_PS_AMT = 0;
		$scope.AM_NBT_CNT = 0;
		$scope.AM_NBT_AMT = 0;
		$scope.AM_TT_CNT = 0;
		$scope.AM_TT_AMT = 0;
		$scope.AC_OP_CNT = 0;
		$scope.AC_OP_AMT = 0;
		$scope.AC_AO_CNT = 0;
		$scope.AC_AO_AMT = 0;
		$scope.AC_BO_CNT = 0;
		$scope.AC_BO_AMT = 0;
		$scope.AC_BT_CNT = 0;
		$scope.AC_BT_AMT = 0;
		$scope.AC_FG_CNT = 0;
		$scope.AC_FG_AMT = 0;
		$scope.AC_LI_CNT = 0;
		$scope.AC_LI_AMT = 0;
		$scope.AC_S_CNT = 0;
		$scope.AC_S_AMT = 0;
		$scope.AC_PC_CNT = 0;
		$scope.AC_PC_AMT = 0;
		$scope.AC_OT_CNT = 0;
		$scope.AC_OT_AMT = 0;
		$scope.AC_PS_CNT = 0;
		$scope.AC_PS_AMT = 0;
		$scope.AC_NBT_CNT = 0;
		$scope.AC_NBT_AMT = 0;
		$scope.AC_TT_CNT = 0;
		$scope.AC_TT_AMT = 0;
		
		angular.forEach($scope.paramList, function(row, index, objs) {
			
			$scope.AC_TT_CNT += parseInt(row.C_TT_CNT);
			$scope.AC_TT_AMT += parseInt(row.C_TT_AMT);
			$scope.AM_TT_CNT += parseInt(row.M_TT_CNT);
			$scope.AM_TT_AMT += parseInt(row.M_TT_AMT);
			if ($scope.inputVO.region_center_id == row.REGION_CENTER_ID) {
				
				$scope.AM_OP_CNT += parseInt(row.M_OP_CNT);
				$scope.AM_OP_AMT += parseInt(row.M_OP_AMT);
				$scope.AM_AO_CNT += parseInt(row.M_AO_CNT);
				$scope.AM_AO_AMT += parseInt(row.M_AO_AMT);
				$scope.AM_BO_CNT += parseInt(row.M_BO_CNT);
				$scope.AM_BO_AMT += parseInt(row.M_BO_AMT);
				$scope.AM_BT_CNT += parseInt(row.M_BT_CNT);
				$scope.AM_BT_AMT += parseInt(row.M_BT_AMT);
				$scope.AM_FG_CNT += parseInt(row.M_FG_CNT);
				$scope.AM_FG_AMT += parseInt(row.M_FG_AMT);
				$scope.AM_LI_CNT += parseInt(row.M_LI_CNT);
				$scope.AM_LI_AMT += parseInt(row.M_LI_AMT);
				$scope.AM_S_CNT += parseInt(row.M_S_CNT);
				$scope.AM_S_AMT += parseInt(row.M_S_AMT);
				$scope.AM_PC_CNT += parseInt(row.M_PC_CNT);
				$scope.AM_PC_AMT += parseInt(row.M_PC_AMT);
				$scope.AM_OT_CNT += parseInt(row.M_OT_CNT);
				$scope.AM_OT_AMT += parseInt(row.M_OT_AMT);
				$scope.AM_PS_CNT += parseInt(row.M_PS_CNT);
				$scope.AM_PS_AMT += parseInt(row.M_PS_AMT);
				$scope.AM_NBT_CNT += parseInt(row.M_NBT_CNT);
				$scope.AM_NBT_AMT += parseInt(row.M_NBT_AMT);				
				$scope.AC_OP_CNT += parseInt(row.C_OP_CNT);
				$scope.AC_OP_AMT += parseInt(row.C_OP_AMT);
				$scope.AC_AO_CNT += parseInt(row.C_AO_CNT);
				$scope.AC_AO_AMT += parseInt(row.C_AO_AMT);
				$scope.AC_BO_CNT += parseInt(row.C_BO_CNT);
				$scope.AC_BO_AMT += parseInt(row.C_BO_AMT);
				$scope.AC_BT_CNT += parseInt(row.C_BT_CNT);
				$scope.AC_BT_AMT += parseInt(row.C_BT_AMT);
				$scope.AC_FG_CNT += parseInt(row.C_FG_CNT);
				$scope.AC_FG_AMT += parseInt(row.C_FG_AMT);
				$scope.AC_LI_CNT += parseInt(row.C_LI_CNT);
				$scope.AC_LI_AMT += parseInt(row.C_LI_AMT);
				$scope.AC_S_CNT += parseInt(row.C_S_CNT);
				$scope.AC_S_AMT += parseInt(row.C_S_AMT);
				$scope.AC_PC_CNT += parseInt(row.C_PC_CNT);
				$scope.AC_PC_AMT += parseInt(row.C_PC_AMT);
				$scope.AC_OT_CNT += parseInt(row.C_OT_CNT);
				$scope.AC_OT_AMT += parseInt(row.C_OT_AMT);
				$scope.AC_PS_CNT += parseInt(row.C_PS_CNT);
				$scope.AC_PS_AMT += parseInt(row.C_PS_AMT);
				$scope.AC_NBT_CNT += parseInt(row.C_NBT_CNT);
				$scope.AC_NBT_AMT += parseInt(row.C_NBT_AMT);
			
			

			}

		});
		$scope.M_OP_CNT = $scope.AM_OP_CNT / $scope.AM_TT_CNT;
		$scope.M_OP_AMT = $scope.AM_OP_AMT / $scope.AM_TT_AMT;
		$scope.M_AO_CNT = $scope.AM_AO_CNT / $scope.AM_TT_CNT;
		$scope.M_AO_AMT = $scope.AM_AO_AMT / $scope.AM_TT_AMT;
		$scope.M_BO_CNT = $scope.AM_BO_CNT / $scope.AM_TT_CNT;
		$scope.M_BO_AMT = $scope.AM_BO_AMT / $scope.AM_TT_AMT;
		$scope.M_BT_CNT = $scope.AM_BT_CNT / $scope.AM_TT_CNT;
		$scope.M_BT_AMT = $scope.AM_BT_AMT / $scope.AM_TT_AMT;
		$scope.M_FG_CNT = $scope.AM_FG_CNT / $scope.AM_TT_CNT;
		$scope.M_FG_AMT = $scope.AM_FG_AMT / $scope.AM_TT_AMT;
		$scope.M_LI_CNT = $scope.AM_LI_CNT / $scope.AM_TT_CNT;
		$scope.M_LI_AMT = $scope.AM_LI_AMT / $scope.AM_TT_AMT;
		$scope.M_S_CNT = $scope.AM_S_CNT / $scope.AM_TT_CNT;
		$scope.M_S_AMT = $scope.AM_S_AMT / $scope.AM_TT_AMT;
		$scope.M_PC_CNT = $scope.AM_PC_CNT / $scope.AM_TT_CNT;
		$scope.M_PC_AMT = $scope.AM_PC_AMT / $scope.AM_TT_AMT;
		$scope.M_OT_CNT = $scope.AM_OT_CNT / $scope.AM_TT_CNT;
		$scope.M_OT_AMT = $scope.AM_OT_AMT / $scope.AM_TT_AMT;
		$scope.M_PS_CNT = $scope.AM_PS_CNT / $scope.AM_TT_CNT;
		$scope.M_PS_AMT = $scope.AM_PS_AMT / $scope.AM_TT_AMT;
		$scope.M_NBT_CNT = $scope.AM_NBT_CNT / $scope.AM_TT_CNT;
		$scope.M_NBT_AMT = $scope.AM_NBT_AMT / $scope.AM_TT_AMT;
		$scope.M_TT_CNT = $scope.AM_TT_CNT / $scope.AM_TT_CNT;
		$scope.M_TT_AMT = $scope.AM_TT_AMT / $scope.AM_TT_AMT;
		$scope.C_OP_CNT = $scope.AC_OP_CNT / $scope.AC_TT_CNT;
		$scope.C_OP_AMT = $scope.AC_OP_AMT / $scope.AC_TT_AMT;
		$scope.C_AO_CNT = $scope.AC_AO_CNT / $scope.AC_TT_CNT;
		$scope.C_AO_AMT = $scope.AC_AO_AMT / $scope.AC_TT_AMT;
		$scope.C_BO_CNT = $scope.AC_BO_CNT / $scope.AC_TT_CNT;
		$scope.C_BO_AMT = $scope.AC_BO_AMT / $scope.AC_TT_AMT;
		$scope.C_BT_CNT = $scope.AC_BT_CNT / $scope.AC_TT_CNT;
		$scope.C_BT_AMT = $scope.AC_BT_AMT / $scope.AC_TT_AMT;
		$scope.C_FG_CNT = $scope.AC_FG_CNT / $scope.AC_TT_CNT;
		$scope.C_FG_AMT = $scope.AC_FG_AMT / $scope.AC_TT_AMT;
		$scope.C_LI_CNT = $scope.AC_LI_CNT / $scope.AC_TT_CNT;
		$scope.C_LI_AMT = $scope.AC_LI_AMT / $scope.AC_TT_AMT;
		$scope.C_S_CNT = $scope.AC_S_CNT / $scope.AC_TT_CNT;
		$scope.C_S_AMT = $scope.AC_S_AMT / $scope.AC_TT_AMT;
		$scope.C_PC_CNT = $scope.AC_PC_CNT / $scope.AC_TT_CNT;
		$scope.C_PC_AMT = $scope.AC_PC_AMT / $scope.AC_TT_AMT;
		$scope.C_OT_CNT = $scope.AC_OT_CNT / $scope.AC_TT_CNT;
		$scope.C_OT_AMT = $scope.AC_OT_AMT / $scope.AC_TT_AMT;
		$scope.C_PS_CNT = $scope.AC_PS_CNT / $scope.AC_TT_CNT;
		$scope.C_PS_AMT = $scope.AC_PS_AMT / $scope.AC_TT_AMT;
		$scope.C_NBT_CNT = $scope.AC_NBT_CNT / $scope.AC_TT_CNT;
		$scope.C_NBT_AMT = $scope.AC_NBT_AMT / $scope.AC_TT_AMT;
		$scope.C_TT_CNT = $scope.AC_TT_CNT / $scope.AC_TT_CNT;
		$scope.C_TT_AMT = $scope.AC_TT_AMT / $scope.AC_TT_AMT;  
		
	
	}

	$scope.query = function() {
		if ($scope.parameterTypeEditForm.$invalid) {
			$scope.showErrorMsgInDialog('欄位檢核錯誤:請檢查必要輸入欄位');
			return;
		}
		$scope.sendRecv("PMS312", "inquire","com.systex.jbranch.app.server.fps.pms312.PMS312InputVO",$scope.inputVO, 
				function(tota, isError) {
					if (!isError) {
						if (tota[0].body.resultList == null) {
							$scope.paramList = [];
							$scope.paramList2 = [];
							$scope.showMsg("ehl_01_common_009");
						}else if (tota[0].body.resultList2 == null) {
							$scope.paramList = tota[0].body.resultList;
							$scope.csvList = tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							$scope.percent();
						}else{
							$scope.paramList = tota[0].body.resultList;
							$scope.paramList2 = tota[0].body.resultList2;
							$scope.csvList = tota[0].body.csvList;
							$scope.outputVO = tota[0].body;
							$scope.percent();
						}
						return;
					}
				});
			console.log($scope.paramList);
			console.log($scope.paramList2);
	};


	// 匯出exceil
	$scope.export = function() {

		$scope.sendRecv("PMS312", "export",
				"com.systex.jbranch.app.server.fps.pms312.PMS312OutputVO", {
					'list' : $scope.csvList
				}, function(tota, isError) {
					if (!isError) {
						return;
					}
				});
	};
	
	//類型轉換，分行營運區選單清空
	$scope.clear_ba = function() {
		$scope.inputVO.branch_area_id = '';
		$scope.inputVO.branch_nbr = '';	
	}
});
