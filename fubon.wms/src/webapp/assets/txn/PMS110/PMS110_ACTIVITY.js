/**================================================================================================
 @program: PMS110_ACTIVITY.js
 @author Eli
 @Description 提供銷售計劃的活動其新增修改邏輯
 @version: ??
 =================================================================================================**/
'use strict';
eSoafApp.controller("PMS110_ACTIVITYController", function ($rootScope, $scope, $controller, validateService, socketService, ngDialog,
                                                           projInfoService, $q, $confirm, $filter) {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "PMS110_ACTIVITYController";

    /** 設置日曆 **/
    $scope.activityModel = {};
    $scope.openActivityDate = ($event, elementOpened) => {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.activityModel[elementOpened] = !$scope.activityModel[elementOpened];
    };

    /** 設置日曆選項 **/
    $scope.setDateOptions = obj => {
        obj.sDateOptions = {
            maxDate: obj.maxDate,
            minDate: obj.minDate
        };
        obj.eDateOptions = {
            maxDate: obj.maxDate,
            minDate: obj.minDate
        };
    }

    /** 新活動選擇日期的邏輯 **/
    $scope.limitDate = () => {
        $scope.sDateOptions.maxDate = $scope.activityVO.actEnd || $scope.maxDate;
        $scope.eDateOptions.minDate = $scope.activityVO.actStart || $scope.minDate;
    }

    /** 舊活動日曆選擇日期的邏輯 **/
    $scope.limitDateForObj = obj => {
        obj.sDateOptions.maxDate = obj.actEnd || obj.maxDate;
        obj.eDateOptions.minDate = obj.actStart || obj.minDate;
    }

    /***初始化***/
    $scope.init = () => {
        $scope.subActList = [];
        $scope.actList = [];
        $scope.outputVO = {}
        $scope.inputVO = {}

        /** 新活動控制物件 **/
        $scope.activity = {
            insertFlag: false
        }

        /** 活動名稱字數限制 **/
        $scope.activityNameLimit = 90;

        /** 新活動字名稱字數提醒物件 **/
        $scope.newActCounter = {
            limit: $scope.activityNameLimit,
            curCnt: 0
        }

        /** 新活動 model **/
        $scope.setActVO = (name, startDate, endDate, status) => {
            $scope.activityVO = {
                actName: name,
                actStart: startDate,
                actEnd: endDate,
                actStatus: status,
            }
        }
        $scope.setActVO();

        /** 設置日曆選項 **/
        $scope.setDateOptions($scope); // For 新活動
        $scope.setDateOptions($scope.inputVO); // For 查詢
    }

    $scope.init();

    /** 點擊編輯活動  **/
    $scope.editActivity = row => {
        row.editFlag = true;
        /** 計數器 **/
        row.counter = {
            limit: $scope.activityNameLimit,
            curCnt: 0
        }
        /** 設置日曆選項 **/
        $scope.setDateOptions(row);

        $scope.attachColsToRow(row);
    }

    /**
     * 將編輯欄位附上資料 row 上
     * @param row 資料
     * @param clear 是否清除
     */
    $scope.attachColsToRow = (row, clear) => {
        row.actSeq = clear ? undefined : row.SEQ;
        row.actName = clear ? undefined : row.NAME;
        row.actStart = clear ? undefined : new Date($scope.dateType(row.START_DATE));
        row.actEnd = clear ? undefined : new Date($scope.dateType(row.END_DATE));
        row.actStatus = clear ? undefined : row.STATUS;
    }

    /** 取消編輯活動 **/
    $scope.cancelEdit = row => {
        row.editFlag = false;
        $scope.attachColsToRow(row, true);
    }

    /** 儲存活動的變更 **/
    $scope.saveActivityChange = row => {
        if (!$scope.colsCheck(row)) return;
        if (!$scope.lengthCheck(row.counter)) return;

        $scope.sendRecv("PMS110", "editActivity", "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.getformattedVO(row),
            (_, isError) => {
                if (!isError) {
                    $scope.showSuccessMsg('ehl_01_common_025');
                    row.editFlag = false;
                    $scope.qryActivity();
                }
            });
    }

    /** 點擊新增活動 **/
    $scope.addNewActivity = () => {
        $scope.activity.insertFlag = true;
    }

    /** 取消新增活動 **/
    $scope.cancelInsert = () => {
        $scope.activity.insertFlag = false;
        $scope.newActCounter.curCnt = 0;
        $scope.setActVO();
    }

    /** 新增活動 **/
    $scope.saveNewActivity = () => {
        if (!$scope.colsCheck($scope.activityVO)) return;
        if (!$scope.lengthCheck($scope.newActCounter)) return;

        $scope.sendRecv("PMS110", "insertActivity", "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.getformattedVO($scope.activityVO),
            (_, isError) => {
                if (!isError) {
                    $scope.showSuccessMsg('ehl_01_common_001');
                    $scope.activity.insertFlag = false;
                    $scope.setActVO();
                    $scope.qryActivity();
                }
            });
    }

    /** 取得格式化後的物件 **/
    $scope.getformattedVO = actVO => {
        let tempVO = angular.copy(actVO);
        tempVO.actStart = $scope.formatDate(tempVO.actStart);
        tempVO.actEnd = $scope.formatDate(tempVO.actEnd);
        return tempVO;
    }


    /** 欄位是否上未填妥 **/
    $scope.colsCheck = actVO => {
        if (!(actVO.actName && actVO.actStatus && actVO.actStart && actVO.actEnd)) {
            $scope.showErrorMsg('尚有欄位未填妥！');
            return false;
        }
        return true;
    }

    /** 是否超過長度限制 **/
    $scope.lengthCheck = counter => {
        if (counter.curCnt > counter.limit) {
            $scope.showErrorMsg(`活動名稱不可超過 ${counter.limit} 字`);
            return false;
        }
        return true;
    }


    /** 查詢活動 **/
    $scope.qryActivity = () => {
        $scope.sendRecv("PMS110", "queryActivity", "com.systex.jbranch.app.server.fps.pms110.PMS110InputVO", $scope.getformattedVO($scope.inputVO),
            (tota, isError) => {
                if (!isError) {
                    $scope.actList = tota[0].body.actList;
                    $scope.outputVO = tota[0].body;
                    if (!$scope.actList.length)
                        $scope.showMsg('ehl_01_common_009');
                }
            });
    }

    /** TODO 確認後，變為資料庫參數 **/
    $scope.mappingSet['PMS.PIPELINE_ACT_STATUS'] = [
        {LABEL: '進行中', DATA: '1'},
        {LABEL: '已結束', DATA: '2'}
    ];

    /** 計算活動名稱的字數 **/
    $scope.countActivityName = (actVO, counter) => {
        if (actVO.actName) {
            actVO.actName = actVO.actName.trim();
            counter.curCnt = actVO.actName.length + (actVO.actName.match(/[^\x00-\xff]/ig) || []).length * 2;
        } else
            counter.curCnt = 0;
    }

});