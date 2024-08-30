/**================================================================================================
 @program: CommonUtil.js
 @author: Eli
 @date: 20190416 管理一些常用到的函式
 =================================================================================================**/
'use strict';
eSoafApp.controller('CommonUtil', function ($rootScope, $scope, $controller) {
    $scope.controllerName = "CommonUtil";

    /**===================================== 設置長度計數器相關邏輯 =====================================**/
    $scope.counter = {};
    /** 設置計數器 **/
    $scope.configureCounter = (colName, limit = 100, twName, curCnt = 0, invalid = false) => {
        if (!colName) return;

        $scope.counter[colName] = {
            curCnt: curCnt,
            limit: limit,
            invalid: invalid,
            twName: twName || colName
        }
    }

    /** 計算字數 **/
    $scope.countLength = (vo, colName, counter) => {
        if (vo[colName]) {
            vo[colName] = vo[colName].trim();
            counter[colName].curCnt = vo[colName].length + (vo[colName].match(/[^\x00-\xff]/ig) || []).length * 2;
            counter[colName].invalid = counter[colName].curCnt > counter[colName].limit;
        } else
            counter[colName].curCnt = 0;
    }

    /** 字數檢查器 **/
    $scope.lengthIsInvalid = () => {
        let flag = false;
        Object.keys($scope.counter).forEach(attr => {
            if ($scope.counter[attr].invalid) {
                $scope.showErrorMsg(`【${$scope.counter[attr].twName}】不得超過 ${$scope.counter[attr].limit} 字！`);
                flag = true;
            }
        });
        return flag;
    }
    /** ============================================================================================ **/

    /** 一鍵複製 **/
    $scope.copyText = copyClass => {
        document.querySelector(copyClass).select();
        try {
            $scope.showMsg(document.execCommand('copy')? '資料已複製到剪貼簿！': '複製失敗！');
        } catch (err) {
            $scope.showErrorMsg('Oops! 複製功能出了點問題，目前無法使用！');
        }
    }

    /** =================================Date Picker================================================
     *  將常用的的 Date Picker 相關邏輯提出，程式端寫法可參考範例。
     *  Ex:
     *  js:
     *      $scope.objName = {};
     *      // $scope.objName.maxDate = [optional];
     *      // $scope.objName.minDate = [optional];
     *      $scope.setDateOptions($scope.objName);
     *  html:
     *      <div class="input-group datebox datewidth">
     *          <input type="text"
     *                 class="form-control"
     *                 uib-datepicker-popup="yyyy/MM/dd"
     *                 ng-model="inputVO.start"
     *                 is-open="calendar.start"
     *                 datepicker-options="objName.sDateOptions"
     *                 ng-readonly="true"
     *                 ng-change="limitDate(objName, inputVO.start, inputVO.end)"/>
     *          <!-- 製作日曆button -->
     *          <span class="input-group-btn">
     *              <button type="button" class="btn btn-default" ng-click="open($event,'start')">
     *                  <i class="glyphicon glyphicon-calendar"></i>
     *              </button>
     *          </span>
     *      </div>
     *  ============================================================================================ **/
    /** 設置日曆狀態物件，用來管理物件開啟/關閉狀態 **/
    $scope.calendar = {};
    $scope.openCalendar = ($event, elementOpened) => {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.calendar[elementOpened] = !$scope.calendar[elementOpened];
    }

    /** 日期範圍物件初始
     *  obj: 將作為日期範圍物件
     *  maxDate、minDate 為自訂最大日期與最小日期
     **/
    $scope.initDateOptions = obj => {
        obj.sDateOptions = {
            maxDate: obj.maxDate,
            minDate: obj.minDate
        };
        obj.eDateOptions = {
            maxDate: obj.maxDate,
            minDate: obj.minDate
        };
    }

    /** 選擇日期範圍限制邏輯
     *  obj: 日期範圍物件
     *  start: 起始日曆的 ng-model 變數
     *  end: 結束日曆的 ng-model 變數
     **/
    $scope.limitDate = (obj, start, end) => {
        obj.sDateOptions.maxDate = end || obj.maxDate;
        obj.eDateOptions.minDate = start || obj.minDate;
    }

    /** e-freeze-table 造成的多次呼叫解法：
     * 因凍結實作方法，當 DOM 發生 Event 時，Event 將不是一個而是多個。
     * 解決方案在於只接受第一個 Event，同次剩餘的 Event 則不反應。
     * **/
    $scope.isFirstCallFnInFreeze = false;
    $scope.firstCallInFreeze = () => {
        if (!$scope.isFirstCallFnInFreeze) {

            const dialogTimeout = setTimeout(() => {
                $scope.isFirstCallFnInFreeze = false;
            }, 1000);

            $scope.isFirstCallFnInFreeze = true;
            return true;
        }
        return false;
    }
});
