/**================================================================================================
 @program: INS970.js
 @author Eli
 @Description M+視訊錄影檔案瀏覽下載功能
 @version: 20210527
====================================*/
'use strict';
eSoafApp.controller('INS970_Controller',
    function ($scope, $controller) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "INS970_Controller";

        // 顯示所有檔案詳細資訊列表
        function listFiles() {
            $scope.sendRecv('INS970', 'listFiles', 'com.systex.jbranch.app.server.fps.ins970.INS970InputVO', {
                url: $scope.inputVO.url
            }, (tota, isError) => {
                    if (!isError) {
                        $scope.target.list = tota[0].body.files;
                        // 根目錄時校正用
                        $scope.inputVO.url = tota[0].body.url;
                    } else {
                        $scope.showErrorMsg('ehl_01_common_024');
                        $scope.target.list = []; // 檔案列表清空
                    }
                });
        }

        $scope.download = function(file) {
            // 執行任務下載時，如果沒有定時器（尋訪任務佇列是否有下載好的檔案）則建立
            if (!$scope.peeker)
                $scope.peeker = assignPeeker();

            let seq = new Date().getTime();
            $scope.sendRecv('INS970', 'download', 'com.systex.jbranch.app.server.fps.ins970.INS970InputVO', {
                    seq: seq,
                    fileIsDir: file.IS_DIR,
                    fileParent: file.PARENT_PATH,
                    fileName: file.FILE_NAME,
                    fileSize: file.FILE_SIZE
                }, (tota, isError) => {
                    if (!isError) {
                        // 推送下載資訊至任務佇列
                        $scope.missionQueue.push({
                            SEQ: seq,
                            NAME: `下載 ${file.FILE_NAME}`,
                            STATUS: $scope.MISSION_START,
                            MSG: '下載中...'
                        });
                    } else {
                        $scope.showErrorMsg('ehl_01_common_024');
                    }
                });
        }

        /** 進入選定的資料夾內，選定的是檔案則無動作 **/
        $scope.entry = file => {
            if (!file.IS_DIR) return;

            $scope.inputVO.url = file.FILE_PATH;
            listFiles();
        }

        /**  url 輸入欄按下 Enter 觸發動作 **/
        $scope.urlKeyUp = $event => {
            if ($event.keyCode == 13)
                listFiles();
        }

        /** 回到上一頁 **/
        $scope.goBack = () => {
            $scope.inputVO.url = goBackRegExpFormat($scope.inputVO.url);
            listFiles();
        }

        /** 取得回到上一頁的路徑 **/
        function goBackRegExpFormat(url) {
            let tmpUrl = url.replace(/([\\/])[^\\/]+[\\/]*$/g, '') || $scope.FILE_SEPERATOR;
            /** 非 AP 主機其根目錄為空 **/
            if (!url.contains($scope.FILE_SEPERATOR)) tmpUrl = '';
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

        /** 查看下載任務佇列是否已有完成的下載，若有，則讓通知瀏覽器下載檔案 **/
        function peek(seqList) {
            $scope.sendRecv('INS970', 'peek', 'com.systex.jbranch.app.server.fps.ins970.INS970InputVO',
                {seqList: seqList}, (tota, isError) => {
                    if (!isError) {
                        if (!(tota && tota.length)) return;
                        console.log(tota)

                        // 檢查回傳的任務資訊
                        let info = tota.filter(e => e.header.OutputType === 'Screen')[0];
                        let result = info.body.missionResult || [];
                        if (result.length)
                            result.forEach(r => updateMissionQueue(r.SEQ, r.STATUS, r.MSG));

                        // 檢查是否有成功下載檔案
                        let fileInfo = tota.filter(e => e.header.OutputType === 'NextProcess' && e.header.NextProc === 'downloadFile');
                        if (fileInfo.length) {
                            // 檢查是否還需要定時器（尋訪任務佇列是否有下載好的檔案），如果任務列表沒有待下載的任務那就清除定時器
                            let peddingSeq = getPeddingSeq()
                            if ($scope.peeker && !peddingSeq.length)
                                $scope.peeker = clearInterval($scope.peeker)
                        }
                    }
                });
        }

        /** 更新任務佇列的任務資訊 **/
        function updateMissionQueue(seq, status, msg) {
            $scope.missionQueue.forEach(m => {
                if (m.SEQ == seq) {
                    m.STATUS = status;
                    m.MSG = msg;
                }
            });
        }

        /** 取得任務列表中的待下載序號 **/
        function getPeddingSeq() {
            return $scope.missionQueue.filter(m => m.STATUS !== $scope.MISSION_END).map(m => m.SEQ);
        }

        /** 每隔一段時間就查詢一次任務列表中的未完成檔案執行狀況 **/
        function assignPeeker() {
            return setInterval(() => {
                console.log('檢查任務列表...')
                let peddingSeq = getPeddingSeq();

                if (peddingSeq.length) {
                    peek(peddingSeq);
                }
            }, 5000);
        }


        function init() {
            $scope.inputVO = {
                url: ''
            }

            // 巡查任務佇列定時器（離開交易還能作用，所以須嚴格控管）
            $scope.peeker = null;

            /** 取得系統 File.seperator **/
            $scope.sendRecv('INS970', 'getFileSeperator', 'com.systex.jbranch.app.server.fps.ins970.INS970InputVO',
                {}, (tota) => {
                    $scope.FILE_SEPERATOR = tota[0].body;

                    listFiles();
                });

            /** 環境物件 **/
            $scope.target = {
                list: [],  // 檔案列表
                order: {   // 排序 asc desc
                    name: undefined,
                    size: undefined,
                    time: undefined
                }
            }
            $scope.forSorter = {
                obj: '只為了排序而創建的物件'
            }

            /** 功能選單 **/
            $scope.funcTemplate = "funcTemplate.html";

            /** 任務佇列 **/
            $scope.missionQueue = [];

            $scope.MISSION_END = 'E';   // 任務完成
            $scope.MISSION_START = 'S'; // 任務開始
        }
        init();
    }
);
