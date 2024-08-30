/**================================================================================================
 @program: CMMGR019.js
 @author Eli
 @Description FTP's UI Version
 @version: 1.0.20190305
 @version: 1.0.20190314 從系統取得 File.seperator，以及調整其相關邏輯。
 @version: 1.0.20190320 =>  1、刪除檢索檔案功能。
 2、改變使用者角度，本地端變為電腦使用者。（原本地端是 AP_SERVER）
 3、依改變使用者角度需求，更改刪除動作、上傳動作、下載動作等面向邏輯
 @version: 1.0.2090412 AP 根目錄為 system seperator，其餘為空
 =================================================================================================*/
'use strict';
eSoafApp.controller('CMMGR019_Controller',
    function ($scope, $controller, socketService, ngDialog, projInfoService, $confirm) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR019_Controller";

        $scope.init = () => {
            /** 查詢 FTP 連線主機清單 **/
            $scope.sendRecv('CMMGR019', 'listHost', 'com.systex.jbranch.app.server.fps.cmmgr019.CMMGR019InputVO',
                {}, (tota, isError) => {
                    $scope.mappingSet['HOST'] = [];
                    $scope.mappingSet['HOST'].push({LABEL: 'AP', DATA: 'AP'});

                    if (!isError) tota[0].body.hostList.forEach(e => $scope.mappingSet['HOST'].push({
                        LABEL: e.HOSTID,
                        DATA: e.HOSTID
                    }));
                    else $scope.showErrorMsg('ehl_01_common_024');
                });

            $scope.inputVO = {
                hostId: 'AP' // 預設 HOST 為 AP Server
            }

            /** 取得系統 File.seperator **/
            $scope.sendRecv('CMMGR019', 'getFileSeperator', 'com.systex.jbranch.app.server.fps.cmmgr019.CMMGR019InputVO',
                {}, (tota) => {
                    $scope.FILE_SEPERATOR = tota[0].body;
                    $scope.inputVO.url = $scope.FILE_SEPERATOR;

                    $scope.listFiles('listLocalFiles');
                });

            /** 設置動作參數 **/
            $scope.setInputActionParameter = (isLocal, srcDir, srcFile, srcIsDir, desDir, desFile, seq) => {
                $scope.inputVO.isLocal = isLocal;
                $scope.inputVO.srcDir = srcDir;
                $scope.inputVO.srcFile = srcFile;
                $scope.inputVO.srcIsDir = srcIsDir;
                $scope.inputVO.desDir = desDir;
                $scope.inputVO.desFile = desFile;
                $scope.inputVO.seq = seq;
            }
            $scope.setInputActionParameter();

            /** 環境物件 **/
            $scope.target = {
                list: [],  // 檔案列表
                type: 'L', // 環境 L : Local、R : Remote
                order: {   // 排序 asc desc
                    name: undefined,
                    size: undefined,
                    time: undefined
                }
            }
            $scope.forSorter = {
                obj: 'just for sorter propose'
            }

            /** 功能選單 **/
            $scope.funcTemplate = "funcTemplate.html";

            /** 任務佇列 **/
            $scope.missionQueue = [];

            $scope.MISSION_END = 'E';   // 任務完成
            $scope.MISSION_START = 'S'; // 任務開始
        }
        $scope.init();

        /**
         *
         * @param methodName 欲呼叫 Server side 的方法名
         */
        $scope.listFiles = (methodName) => {
            $scope.sendRecv('CMMGR019', methodName, 'com.systex.jbranch.app.server.fps.cmmgr019.CMMGR019InputVO',
                $scope.inputVO, (tota, isError) => {
                    if (!isError) $scope.target.list = tota[0].body.fileInfoList;
                    else {
                        $scope.showErrorMsg('ehl_01_common_024');
                        $scope.target.list = []; // 檔案列表清空
                    }
                });
        }

        /** 主機切換時，根目錄邏輯為：AP 使用斜線當作根目錄，其餘為空 **/
        $scope.hostChange = () => $scope.inputVO.url = $scope.isLocal()? $scope.FILE_SEPERATOR: '';

        /** 讀取指定主機之檔案列表 **/
        $scope.hostLoad = () => {
            if (!$scope.inputVO.hostId) return;
            $scope.listFiles($scope.isLocal() ? 'listLocalFiles' : 'listRemoteFiles');
        }

        /** 是否要連線到 AP_SERVER **/
        $scope.isLocal = () => $scope.inputVO.hostId == 'AP';

        /** 進入選定的資料夾內，選定的是檔案則無動作 **/
        $scope.entry = file => {
            if (!file.IS_DIR) return;

            $scope.inputVO.url = file.FILE_PATH;
            $scope.hostLoad();
        }

        /**  url 輸入欄按下 Enter 觸發動作 **/
        $scope.urlKeyUp = $event => {
            if ($event.keyCode == 13)
                $scope.hostLoad();
        }

        /** 回到上一頁 **/
        $scope.goBack = () => {
            $scope.inputVO.url = $scope.goBackRegExpFormat($scope.inputVO.url);
            $scope.hostLoad();
        }

        /** 取得回到上一頁的路徑 **/
        $scope.goBackRegExpFormat = url => {
            let tmpUrl = url.replace(/([\\/])[^\\/]+[\\/]*$/g, '') || $scope.FILE_SEPERATOR;
            /** 非 AP 主機其根目錄為空 **/
            if (!$scope.isLocal() && !url.contains($scope.FILE_SEPERATOR)) tmpUrl = '';
            return tmpUrl;
        }

        /** 控管彈跳視窗邏輯 **/
        $scope.pop = row => $scope.target.list.map(each => each.show = each == row);

        /** 彈跳視窗黯淡邏輯 **/
        $scope.fade = row => setTimeout(() => row.show = false, 3000);

        /** 依給定 type（種類）排序檔案 **/
        $scope.order = type => {
            var target = $scope.target;
            switch (type) {
                case 'N':
                    $scope.orderLogic(target, 'name',
                        (a, b) => a.FILE_NAME.charAt().charCodeAt() - b.FILE_NAME.charAt().charCodeAt());
                    break;
                case 'S':
                    $scope.orderLogic(target, 'size', (a, b) => a.FILE_SIZE - b.FILE_SIZE);
                    break;
                case 'T':
                    $scope.orderLogic(target, 'time',
                        (a, b) => new Date(a.LAST_MODIFIED).getTime() - new Date(b.LAST_MODIFIED).getTime());
            }
        }

        /** 排序主邏輯 **/
        $scope.orderLogic = (target, orderItem, comparefn) => {
            if (!target.order[orderItem] || target.order[orderItem] === 'asc') {
                target.list.sort((a, b) => comparefn(b, a));
                target.order[orderItem] = 'desc';
            } else {
                target.list.sort((a, b) => comparefn(a, b));
                target.order[orderItem] = 'asc';
            }
        }

        /** 遠端下載檔案 **/
        $scope.downloadFile = file => {
            var seq = new Date().getTime();
            $scope.action($scope.isLocal(), file, null, 'downloadRemoteFile',
                (_) => {
                    $scope.assign(seq, `遠端${$scope.inputVO.hostId}：從目錄（${$scope.inputVO.url}）中下載（${file.FILE_NAME}）`, $scope.MISSION_START, '下載中...');
                }, seq);
        }

        /** 本地上傳檔案 **/
        $scope.uploadFile = (name, rname) => {
            if ($scope.target.list.filter(file => file.IS_DIR && (file.FILE_NAME.toUpperCase() === rname.toUpperCase())).length) {
                $scope.showErrorMsg('上傳檔案名稱與該目錄下資料夾同名，請指定其他名稱！');
                return;
            }

            var confirmMsg;
            if ($scope.target.list.filter(file => !file.IS_DIR && (file.FILE_NAME.toUpperCase() === rname.toUpperCase())).length)
                confirmMsg = `目錄下已有一個名為${rname}的檔案，是否覆蓋該檔案？`;

            var file = {
                FILE_NAME: name,
                TAR_DIR: $scope.inputVO.url,
                TAR_FILE: rname,
            }
            var seq = new Date().getTime();
            $scope.action($scope.isLocal(), file, confirmMsg, 'uploadLocalFile',
                (_) => {
                    $scope.assign(seq, `上傳（${rname}）到遠端${$scope.inputVO.hostId}的目錄（${$scope.inputVO.url}）中`, $scope.MISSION_START, '上傳中...');
                }, seq);
        }

        /** 任務分派 **/
        $scope.assign = (seq, name, status, msg) => {
            $scope.missionQueue.push({
                SEQ: seq,
                NAME: name,
                STATUS: status,
                MSG: msg
            });
        }

        /** 刪除檔案 **/
        $scope.delFile = file => {
            var seq = new Date().getTime();
            $scope.action($scope.isLocal(), file, `確定刪除${file.FILE_NAME}？ ${file.IS_DIR ? '注意！ 將會刪除該資料夾內所有檔案！' : ''}`,
                'deleteFile', (_) => {
                    $scope.assign(seq, `遠端${$scope.inputVO.hostId}：從目錄（${$scope.inputVO.url}）中刪除（${file.FILE_NAME}）`, $scope.MISSION_START, '刪除中...');
                }, seq);
        }

        /** 動作參數 (上傳、下載、檢索、刪除)  **/
        $scope.action = (isLocal, srcFile, confirmContent, action, postFn, seq) => {
            if (confirmContent)
                $confirm({text: confirmContent}, {size: 'sm'})
                    .then(() => $scope.actionLogic(isLocal, srcFile, action, postFn, seq));
            else $scope.actionLogic(isLocal, srcFile, action, postFn, seq);
        }

        /** 動作參數核心邏輯**/
        $scope.actionLogic = (isLocal, srcFile, action, postFn, seq) => {
            $scope.setInputActionParameter(isLocal, srcFile.PARENT_PATH, srcFile.FILE_NAME, srcFile.IS_DIR, srcFile.TAR_DIR, srcFile.TAR_FILE, seq);

            $scope.sendRecv('CMMGR019', action, 'com.systex.jbranch.app.server.fps.cmmgr019.CMMGR019InputVO',
                $scope.inputVO, (tota, isError) => {
                    if (!isError) postFn(tota);
                    else $scope.showErrorMsg('ehl_01_common_024');

                    $scope.setInputActionParameter(); // 清除動作參數
                });
        }

        /** 查看 FTP 資訊 **/
        $scope.lookFtpInfo = (seqList) => {
            $scope.sendRecv('CMMGR019', 'lookFtpInfo', 'com.systex.jbranch.app.server.fps.cmmgr019.CMMGR019InputVO',
                {seqList: seqList}, (tota, isError) => {
                    if (!isError) {
                        var beforeEndCount = $scope.getEndCountFromMissionQueue();

                        var result = tota[0].body.missionResult || [];
                        if (result.length)
                            result.forEach(r => $scope.updateMissionQueue(r.SEQ, r.STATUS, r.MSG));
                        else
                            tota.map(each => each.body.defaultFileName)
                                .filter(file => file)
                                .forEach(file => $scope.updateMissionQueue(file.replace(/\D/gi, ''), $scope.MISSION_END, '下載完成'));

                        var afterEndCount = $scope.getEndCountFromMissionQueue();

                        if (afterEndCount !== beforeEndCount)
                            $scope.hostLoad();
                    }
                });
        }

        $scope.getEndCountFromMissionQueue = () => $scope.missionQueue.filter(m => m.STATUS === $scope.MISSION_END).length;

        /** 更新 missionQueue 資訊 **/
        $scope.updateMissionQueue = (seq, status, msg) => {
            $scope.missionQueue.forEach(m => {
                if (m.SEQ == seq) {
                    m.STATUS = status;
                    m.MSG = msg;
                }
            });
        }

        /** 每隔一段時間就查詢一次任務列表中的未完成檔案執行狀況 **/
        setInterval(() => {
            var peddingSeq = $scope.missionQueue.filter(m => m.STATUS !== $scope.MISSION_END).map(m => m.SEQ);

            if (peddingSeq.length) {
                $scope.lookFtpInfo(peddingSeq.join(';'));
            }
        }, 5000);
    }
);
