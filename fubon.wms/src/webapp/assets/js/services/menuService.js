/**================================================================================================
 @program: menuService.js
 @author Eli
 @description: menu serice provider
 @version: 1.0.20180831
 =================================================================================================*/
'use strict';
eSoafApp.factory('menuService', ['$rootScope', 'socketService', function ($rootScope, socketService) {
    return {
        /**
         * 使用 loadMenu 來取得 Menu 相關資訊，
         *
         * EX: 載入Menu資訊
         *     menuService.loadMenu({node: 'ROOT', $scope: $scope})
         *     $scope.treeMenu 為Menu的Tree物件
         *     $scope.treeMenuHtmlTemplate  為Menu的Dynamic HTML元件
         *     引入 Dynamic.js
         *     在 HTML 頁面所選位置插入 <dynamic template="{{treeMenuHtmlTemplate}}"></dynamic>
         *     可在頁面上呈現根節點為 'ROOT'的 MENU TREE 元件
         *     Menu Button 的Click事件，必須實作 clickMenuButtonAction(row: 當前MENU, prev: 父MENU)
         *
         *
         * @param menuObj {node:選擇要得MENU NODE, $scope: 裝填資訊}
         * @param fn callBack Function 來處理額外資訊
         */
        loadMenu(menuObj, fn) {
            var node = menuObj.node || 'ROOT';
            var scope = menuObj.$scope || {};

            socketService.sendRecv("CMMGR020", "getMENUTree", "com.systex.jbranch.app.server.fps.cmmgr020.CMMGR020ItemVO", {menuId: node}).then(
                function (totas) {
                    var menu = totas[0].body.meunTreeLS;

                    if (menu && menu.length) {
                        var rootMenu = menu.filter((e) => e.MENU_ID == node);
                        var tree = [];
                        var record = {
                            defaultIndex: 1,
                            index: 1,
                            maxLevel: 1,
                            maxFileLevel: 1,
                            saveMaxLevel(index) {
                                if (index > this.maxLevel) {
                                    this.maxLevel = index;
                                    this.maxFileLevel = index + 1;
                                }
                                this.index = this.defaultIndex;
                            },
                            folder: [],
                            txn: []
                        };
                        var treeHtml = [];

                        var service = {
                            /**
                             * 組織Menu Tree
                             * @param menu loadMenu傳回的資料
                             * @param menuArr 根結點
                             * @param tree 載體，負責組織TREE
                             * @param record 負責記錄Tree的額外資訊
                             */
                            getMenuTree(menu, menuArr, tree, record) {
                                menuArr.forEach((sub) => {
                                    tree.push(sub); // 為使順利排序，故使用陣列存放而不是使用物件存放 (物件無法按照順序)
                                    // 查看是否有子類並依照SEQ_NUM排序
                                    var subMenu = menu.filter((e) => e.PREV_MENU == sub.MENU_ID).sort((a, b) => a.SEQ_NUM > b.SEQ_NUM ? 1 : a.SEQ_NUM < b.SEQ_NUM ? -1 : 0);


                                    // 紀錄不同類型的集合
                                    if (sub.MENU_TYPE != 'P')
                                        record.folder.push(sub);
                                    else
                                        record.txn.push(sub);


                                    // 如果有子類，使用遞迴繼續組織TREE
                                    if (subMenu.length) {
                                        record.index += 1;
                                        tree[tree.indexOf(sub)].SUBITEM = [];
                                        this.getMenuTree(menu, subMenu, tree[tree.indexOf(sub)].SUBITEM, record);
                                        record.saveMaxLevel(record.index);
                                    }
                                });
                            },

                            /**
                             *  產生Menu樹狀圖元件
                             * @param treeHtml 裝載Tree Element的容器，如使用 $scope.treeHtml = '[Prefix[optional]]'，執行完後，將此注入到你要的元素裡
                             * @param treeRecord getMenuTree 後的取得的紀錄物件，將用此作為限制條件用
                             */
                            generateMenuTreeElement(treeHtml, treeRecord) {
                                var rowName = `row${treeRecord.index}`;
                                var prevName = `row${treeRecord.index - 1}`;

                                treeHtml.push(`<div ng-repeat="${rowName} in ${treeRecord.index == 1 ? 'treeMenu' : prevName + '.SUBITEM'} track by $index">
                                    <button ng-class="menuClassSelector(${rowName})" ng-click="clickMenuButtonAction(${rowName}, ${treeRecord.index == 1 ? '{}' : prevName});">
                                        <span ng-class="getMenuIcon(${rowName}.MENU_TYPE)"></span>&emsp;
                                        <span ng-bind="${rowName}.MENU_NAME"></span>&emsp;
                                        <span ng-show="showMenuCntBadge(${rowName})" class="badge" ng-bind="${rowName}.SUBITEM.length"></span>
                                    </button>
                                    <button ng-class="menuClassSelector(${rowName})" ng-show="${rowName}.MENU_TYPE != 'P'" ng-click="showMenu${treeRecord.index} = !showMenu${treeRecord.index}">
                                        <span ng-class="getMenuExpandIcon(showMenu${treeRecord.index})"></span>
                                    </button>
                                    <div ng-show="showMenu${treeRecord.index}" style="margin: 0; padding-left: 20px;">`);
                                if (treeRecord.index <= treeRecord.maxFileLevel) {
                                    treeRecord.index++;
                                    this.generateMenuTreeElement(treeHtml, treeRecord);
                                }
                                treeHtml.push('</div>');
                            },
                            /** 依照Menu的種類取得相應的 css class **/
                            getMenuIcon(type) {
                                return type != 'P' ? 'glyphicon glyphicon-folder-close' : 'glyphicon glyphicon-file';
                            },
                            /** 依照Menu的種類以及其是否有SubMenu來選擇是否顯示Badge **/
                            showMenuCntBadge(row) {
                                return row.MENU_TYPE != 'P' && row.SUBITEM;
                            },
                            /** 依照傳入值取得展開或縮放ICON **/
                            getMenuExpandIcon(needCollapse) {
                                return needCollapse ? 'glyphicon glyphicon-minus' : 'glyphicon glyphicon-plus';
                            },
                            /** 依照Menu的種類給予不同的css class **/
                            menuClassSelector(row) {
                                return row.MENU_TYPE == 'P' ? 'btn btn-info' : (!row.SUBITEM || !row.SUBITEM.length) ? 'btn btn-success' : 'btn btn-primary';
                            }
                        }

                        service.getMenuTree(menu, rootMenu, tree, record);
                        service.generateMenuTreeElement(treeHtml, record);

                        treeHtml = treeHtml.join('');
                        /** 賦值給傳入的$scope物件 **/
                        scope.treeMenu = tree;
                        scope.treeMenuHtmlTemplate = treeHtml;
                        scope.getMenuIcon = service.getMenuIcon;
                        scope.showMenuCntBadge = service.showMenuCntBadge;
                        scope.getMenuExpandIcon = service.getMenuExpandIcon;
                        scope.menuClassSelector = service.menuClassSelector;

                        var others = {
                            menu: menu,
                            tree: tree,
                            record: record,
                            treeHtml: treeHtml,
                            service: angular.copy(service)
                        }

                        if (fn) fn(others);
                    } else {
                        $rootScope.showMsgProcess('ehl_01_common_009', 'NORMAL');
                    }
                })
        }
    }
}]);
