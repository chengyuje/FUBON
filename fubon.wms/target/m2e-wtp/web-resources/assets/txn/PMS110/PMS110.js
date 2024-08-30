/**================================================================================================
 @program: PMS110.js
 @author Eli
 @Description 銷售計劃主邏輯
 @version: ??
 =================================================================================================**/
'use strict';
eSoafApp.controller('PMS110Controller', function ($rootScope, $scope, $controller, $confirm, $filter, $timeout, sysInfoService, socketService, ngDialog, projInfoService, getParameter) {

	$controller('BaseController', {$scope: $scope});
	$scope.controllerName = "PMS110Controller";

	/** 繼承共用的組織連動選單 **/
	$controller('PMSRegionController', {$scope: $scope});

	$scope.memLoginFlag = sysInfoService.getMemLoginFlag();
	
	/***初始化***/
	$scope.init = () => {
        $scope.subData = [];

        $scope.inputVO = {
            /** 連動組織參數會用到 **/
            aoFlag: 'N',
            psFlag: 'Y',
            psFlagType: sysInfoService.getPriID() == '004' ? '7' : '',
            region_center_id: undefined,
            branch_area_id: undefined,
            branch_nbr: undefined,
            emp_id: undefined,
            planYearmon: $filter('date')(new Date(), 'yyyyMM')
            /** ============================================== **/
        }
        $scope.outputVO = {};

    }
    $scope.init();


    /** 載入參數 **/
    $scope.loadParameter = () => {
    	/** 銷售計劃月份（往前半年、包含當月再往後三個月，總計 10 個月） **/
        var LastDate = new Date();
    	LastDate.setMonth(LastDate.getMonth() + 3, 1);
	    var strLastMon='';
	    $scope.mappingSet['timeE'] = [];
	    
	    //資料日期區間限制為兩年內資料
	    for (var i = 0; i <= 10; i++) {
	    	strLastMon = LastDate.getMonth();
	    	strLastMon++;
	    	if (strLastMon < 10 ) {
	    		strLastMon = '0' + strLastMon;
	    	} else {
	    		strLastMon = '' + strLastMon;
	    	}

			$scope.mappingSet['timeE'].push({
	    		LABEL: LastDate.getFullYear() + '/' + strLastMon,
	    		DATA: LastDate.getFullYear() + '' + strLastMon
	    	}); 
	    	
	    	//每一筆減一個月，倒回去取前六個月內日期區間
	    	LastDate.setMonth(LastDate.getMonth() - 1);
	    }
	    
        /** 撥款月份同銷售計劃月份 **/
        $scope.mappingSet['drawMonth'] = angular.copy($scope.mappingSet['timeE']);

        /***XML參數***/
        getParameter.XML(['PMS.PIPELINE_PRD', 'PMS.PIPELINE_STATUS', 'PMS.PIPELINE_PRD_ITEM',
                          'PMS.PIPELINE_SOURCE', 'PMS.PIPELINE_VIEW_RESULT', 'PMS.PIPELINE_REFUSE', 
                          'PMS.CUST_SOURCE', 'PMS.PIPELINE_PRD_ITEM_1', 'PMS.PIPELINE_PRD_ITEM_2', 'PMS.PIPELINE_PRD_ITEM_3'
        ], (totas) => {
            if (totas) {
                $scope.mappingSet['PMS.PIPELINE_PRD'] = totas.data[totas.key.indexOf('PMS.PIPELINE_PRD')];					// 預計承作商品(大項)
                $scope.mappingSet['PMS.PIPELINE_STATUS'] = totas.data[totas.key.indexOf('PMS.PIPELINE_STATUS')];			// 案件狀態
                $scope.mappingSet['PMS.PIPELINE_PRD_ITEM'] = totas.data[totas.key.indexOf('PMS.PIPELINE_PRD_ITEM')];		// 實際承作商品(小項)
                $scope.mappingSet['PMS.CUST_SOURCE'] = totas.data[totas.key.indexOf('PMS.CUST_SOURCE')];					// 客戶來源
            }
        });
        
        // 實際承作商品(小項) - 房貸
        var vo = {'param_type': 'PMS.PIPELINE_PRD_ITEM_1', 'desc': false}; 
    	$scope.requestComboBox(vo, function(totas) {
    		if (totas[totas.length - 1].body.result === 'success') {
    			projInfoService.mappingSet['PMS.PIPELINE_PRD_ITEM_1'] = totas[0].body.result;
    			$scope.mappingSet['PMS.PIPELINE_PRD_ITEM_1'] = projInfoService.mappingSet['PMS.PIPELINE_PRD_ITEM_1'];
    		}
    	});
    	
    	// 實際承作商品(小項) - 信貸
    	var vo = {'param_type': 'PMS.PIPELINE_PRD_ITEM_2', 'desc': false};
    	$scope.requestComboBox(vo, function(totas) {
    		if (totas[totas.length - 1].body.result === 'success') {
    			projInfoService.mappingSet['PMS.PIPELINE_PRD_ITEM_2'] = totas[0].body.result;
    			$scope.mappingSet['PMS.PIPELINE_PRD_ITEM_2'] = projInfoService.mappingSet['PMS.PIPELINE_PRD_ITEM_2'];
    		}
    	});
    	
    	// 實際承作商品(小項) - 留貸
    	var vo = {'param_type': 'PMS.PIPELINE_PRD_ITEM_3', 'desc': false};
    	$scope.requestComboBox(vo, function(totas) {
    		if (totas[totas.length - 1].body.result === 'success') {
    			projInfoService.mappingSet['PMS.PIPELINE_PRD_ITEM_3'] = totas[0].body.result;
    			$scope.mappingSet['PMS.PIPELINE_PRD_ITEM_3'] = projInfoService.mappingSet['PMS.PIPELINE_PRD_ITEM_3'];
    		}
    	});
    }
    $scope.loadParameter();

    //選取下拉選單 --> 設定可視範圍
    $scope.dateChange = () => {
        if ($scope.inputVO.planYearmon) {
            $scope.inputVO.reportDate = $scope.inputVO.planYearmon;
            $scope.RegionController_getORG($scope.inputVO);
        }
    }
    $scope.dateChange();

    /** 文字日期加斜線 **/
    $scope.dateType = val => {
        if (!val || val.length !== 8) return;
        let date = val.split('');
        date.splice(6, 0, "/");
        date.splice(4, 0, "/");
        return date.join('');
    }

    /****查詢****/
    $scope.inquire = () => {
        if ($scope.parameterTypeEditForm.$invalid) {
            $scope.showErrorMsg('欄位檢核錯誤:請輸入必填欄位！');
            return;
        }

        $scope.sendRecv("PMS110", "query", "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.inputVO,
            (tota, isError) => {
                if (!isError) {
                    $scope.pipelineList = tota[0].body.pipelineList;
                    $scope.pilelineSumList = tota[0].body.pilelineSumList;
                    
                    $scope.outputVO = tota[0].body;

                    if (!$scope.pipelineList.length)
                        $scope.showMsg('ehl_01_common_009');
                }
            });
    }

    /** 將 Date 變更為 yyyyMMdd 形式 **/
    $scope.formatDate = date => $filter('date')(date, 'yyyyMMdd');

    /** 新增/修改 Pipeline 資料 **/
    $scope.insert = row => {
        let self = $scope;
        let dialog = ngDialog.open({
            template: 'assets/txn/PMS110/PMS110_INSERT.html',
            className: 'PMS110_INSERT',
            controller: ['$scope', function ($scope) {
            	$scope.BRANCH_LIST = self.BRANCH_LIST;
                $scope.row = angular.copy(row);
            }]
        }).closePromise.then(function (data) {
            if (data.value != 'cancel') {
                $scope.inquire();
            }
        });
    }

    /** 匯出 **/
    $scope.export = () => {
    	$scope.inputVO.pipelineList = $scope.pipelineList;
        $scope.inputVO.pilelineSumList = $scope.pilelineSumList;

		$scope.sendRecv("PMS110", "export", "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.inputVO,
				function(totas, isError) {
                	if (isError) {
                		$scope.showErrorMsg(totas[0].body.msgData);
                	}
				}
		);
    }
    
    /** 只顯示備註 **/
    $scope.openMemo = memo => {
        ngDialog.open({
            template: 'assets/txn/PMS110/PMS110_MEMO.html',
            className: 'PMS110_MEMO',
            controller: ['$scope', function ($scope) {
                $scope.inputVO = {
                    memo: memo
                }
            }]
        });
    }
});