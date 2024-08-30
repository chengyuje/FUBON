/**================================================================================================
 @program: CMMGR020.js
 @author Eli
 @Description Refactoring Menu
 @version: 1.0.20180905
 =================================================================================================*/
'use strict';
eSoafApp.controller('CMMGR020_Controller',
    function ($scope, $controller, socketService, ngDialog, projInfoService, $confirm, menuService) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR020_Controller";

        // 初始分頁資訊
        $scope.inquireInit = function () {
            $scope.paramList = [];
            $scope.itemList = [];
            $scope.outputVO = {};
        };
        $scope.inquireInit();

        /** Menu Edit Zone **/
        $scope.initDetail = function () {
            $scope.detail = {
                isEdit: false,    // 是否為編輯狀態
                curr: {           // 當前目錄
                    MENU_NAME: '',
                    MENU_ID: '',
                    PREV_MENU: ''
                },
                prevList: [],    // 可用目錄列表
                bak: {},          // 備份 this.curr
                addMenuZone: {   // 管理 Detail區的，新增目錄區
                    show: false,
                    newMenuId: '',
                    newMenuName: ''
                }
            }
        }

        var init = true; // 一開始載入的Menu節點是'ROOT'，所以在設置 $scope.detail.prevList時不需要再額外呼叫一次 menuService.loadMenu
        /**
         * 呼叫 menuService 載入相關資訊
         */
        $scope.loadMenu = function () {
            $scope.initDetail();
            $scope.treeMenu = [];

            menuService.loadMenu({node: $scope.inputVO.menuId, $scope: $scope}, (other) => {
                $scope.detail.curr = $.extend(true, {}, $scope.treeMenu[0]);
                showDetailDisplayName();
                // 初始
                if (init) setPrevList(other.record.folder);
                // 之後
                else getAvailablePrevMenuList();
                init = false;
            });
        }
        $scope.loadMenu();

        /** 取得可使用的目錄列表 **/
        function getAvailablePrevMenuList() {
            menuService.loadMenu({node: 'ROOT'}, (other) => {
                setPrevList(other.record.folder);
            });
        }

        /**
         * 設置可用目錄列表
         * @param folderList
         */
        function setPrevList(folderList) {
            $scope.detail.prevList = [];
            folderList.forEach((e) =>
                $scope.detail.prevList.push({LABEL: `${e.MENU_ID}-${e.MENU_NAME}`, DATA: e.MENU_ID}));
        }

        /**
         * Menu Detail Zone 點擊'儲存鍵'，儲存該目錄的相關資訊
         */
        $scope.saveMenu = function () {
            if ($scope.detail.curr.MENU_NAME && ($scope.detail.curr.MENU_ID == 'ROOT' || $scope.detail.curr.PREV_MENU)) {
                $scope.sendRecv("CMMGR020", "confirmMenu", "com.systex.jbranch.app.server.fps.cmmgr020.CMMGR020ItemVO", {menu: $scope.detail.curr},
                    function (totas, isError) {
                       if (isError) {
                            $scope.showErrorMsg('ehl_01_common_024');
                            return;
                        }
                        $scope.detail.isEdit = false;
                        $scope.showSuccessMsg('ehl_01_common_025');

                        toggleAddMenuZone(false);
                        clearAddMenuZoneInputs();
                        $scope.loadMenu();
                    }
                );
            } else {
                $scope.showErrorMsg('尚有欄位未輸入完成 ! 無法儲存 !');
            }

        }

        /**
         * 取得Detail's addMenuBtn css class
         * @returns {string}
         */
        $scope.getMenuAddBtnClass = function () {
            return `glyphicon glyphicon-${$scope.detail.addMenuZone.show ? 'ok' : 'plus'}-sign`;
        }

        /**
         * Menu Detail Zone 點擊'新增Menu鍵'
         */
        $scope.addMenu = function () {
            var isAddMenu = $scope.detail.addMenuZone.show; //判斷是否準備儲存新Menu
            if (isAddMenu) {
                if ($scope.detail.addMenuZone.newMenuId == 'ROOT') {
                    $scope.showErrorMsg("目錄編號不可為'ROOT'");
                    $scope.detail.addMenuZone.newMenuId = '';
                    $('#newMenuIdInput').focus();
                    return;
                }
                if (!$scope.detail.addMenuZone.newMenuName) {
                    $scope.showErrorMsg("目錄名稱為必填項目 !");
                    $('#newMenuNameInput').focus();
                    return;
                }
                addNewMenu();
            } else {
                $('#newMenuIdInput').focus();
                toggleAddMenuZone(true);
            }
        }

        /** 轉換 AddMenuZone 的顯示  **/
        function toggleAddMenuZone(isShow) {
            $scope.detail.addMenuZone.show = isShow;
        }

        /**
         * Menu Detail's AddMenu Zone 點擊'新增Menu鍵'
         */
        function addNewMenu() {
            var subItems = $scope.detail.curr.SUBITEM;
            var sub = {
                MENU_ID: $scope.detail.addMenuZone.newMenuId,
                MENU_NAME: $scope.detail.addMenuZone.newMenuName,
                SEQ_NUM: subItems.length + 100,
                PREV_MENU: $scope.detail.curr.MENU_ID,
                MENU_TYPE: 'F',
                IS_NEW_MENU: true
            }
            subItems.push(sub);
            toggleAddMenuZone(false);
            clearAddMenuZoneInputs();
        }

        /** 清除AddMenuZone的輸入欄位 **/
        function clearAddMenuZoneInputs() {
            $scope.detail.addMenuZone.newMenuId = '';
            $scope.detail.addMenuZone.newMenuName = '';
        }

        /** 提供 Detail SubItem Zone 可拖曳功能 **/
        $scope.sortableOptions = {
            stop: function (e, ui) {
                angular.forEach(ui.item.sortable.droptargetModel, function (row, index) {
                    row.SEQ_NUM = index + 1;
                });
            }
        };

        /** 顯示Menu詳細資料 **/
        $scope.clickMenuButtonAction = function (curr, prev) {
            // 將資料載入到Menu Detail Zone
            $scope.detail.isEdit = false;
            $scope.detail.curr = $.extend(true, {}, curr);
            $scope.detail.curr.SUBITEM = $scope.detail.curr.SUBITEM || []; // 如果該目錄為空則初始化一個空陣列
            showDetailDisplayName();
            toggleAddMenuZone(false);
            clearAddMenuZoneInputs();
            itemMatchTree();
        }

        /** 顯示Detail Zone的標題名稱 **/
        function showDetailDisplayName() {
            $scope.detail.displayName = `${$scope.detail.curr.MENU_ID}-${$scope.detail.curr.MENU_NAME}`;
        }

        /** 刪除目錄下的子功能 **/
        $scope.delFile = function (file) {
            var fileIndex = $scope.detail.curr.SUBITEM.indexOf(file);
            $scope.detail.curr.SUBITEM.splice(fileIndex, 1);
            itemMatchTree();
        }

        /**
         * 取得功能
         */
        $scope.getItem = function () {
            $scope.sendRecv("CMMGR020", "getItem", "com.systex.jbranch.app.server.fps.cmmgr020.CMMGR020ItemVO", $scope.inputVO,
                function (tota, isError) {
                    if (!isError) {
                        if (!tota[0].body.data.length) {
                            $scope.showMsg("ehl_01_common_009");
                            return;
                        }
                        $scope.itemList = tota[0].body.data;
                        $scope.outputVO = tota[0].body;

                        itemMatchTree();
                    }
                });
        };

        /**
         * 將功能ITEM LIST 取消勾選所有項目
         * 並與 MENU TREE 做配對，Menu含有的項目將無法再勾選
         */
        function itemMatchTree() {
            var menuIdArr = [];
            angular.forEach($scope.detail.curr.SUBITEM, (row) => menuIdArr.push(row.MENU_ID));
            angular.forEach($scope.itemList, (row) => {
                if (menuIdArr.includes(row.TXNCODE)) row.canSelected = false;
                else row.canSelected = true;
            });
        }

        /**
         * 加選功能邏輯
         */
        $scope.addItem = function (row) {
            if (row)
                addItemToMenu(row);
            else
                $scope.paramList.filter((row) => row.canSelected).map(addItemToMenu);

            itemMatchTree();
        };

        /**
         * 添加功能到Menu
         * @param row
         */
        function addItemToMenu(row) {
            var subItems = $scope.detail.curr.SUBITEM;
            var sub = {
                MENU_ID: row.TXNCODE,
                MENU_NAME: row.TXNNAME,
                SEQ_NUM: subItems.length + 100,
                PREV_MENU: $scope.detail.curr.MENU_ID,
                MENU_TYPE: 'P'
            };
            subItems.push(sub);
        }

        /**
         * Menu Detail Zone 點擊'刪除目錄鍵'，刪除整個目錄
         */
        $scope.delMenu = function () {
            $confirm({text: '請確定是否刪除此目錄？ 注意:刪除此目錄會連同目錄下功能目錄及功能名稱一起刪除 !'}).then(
                function () {
                    $scope.sendRecv("CMMGR020", "delMenu", "com.systex.jbranch.app.server.fps.cmmgr020.CMMGR020ItemVO", {menuId: $scope.detail.curr.MENU_ID},
                        function (tota, isError) {
                            if (!isError) {
                                $scope.detail.isEdit = false;
                                $scope.showSuccessMsg('ehl_01_common_003');
                                $scope.initDetail();
                                $scope.inputVO.menuId = 'ROOT';
                                $scope.loadMenu();
                            } else {
                                $scope.showErrorMsg('ehl_01_common_005');
                            }
                        }
                    )
                }
            );
        }

        /** 依照Menu的種類給予不同的css class **/
        $scope.tableTrClassSelector = function (row) {
            return row.MENU_TYPE == 'P' ? 'info' : (!row.SUBITEM || !row.SUBITEM.length) ? 'warning' : 'danger';
        }
    }
);
