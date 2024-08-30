/**================================================================================================
 @program: CMMGR006_EDIT.js
 @author: Eli
 @date: 20190416 參數種類與編碼設定（TBSYSPARAMTYPE & TBSYSPARAMETER）
 =================================================================================================*/
'use strict';
eSoafApp.controller('CMMGR006_EditController',
    function ($scope, $controller, $confirm) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "CMMGR006_EditController";

        /** 載入 CommonUtil **/
        $controller('CommonUtil', {$scope: $scope});

        /** 配置 Input **/
        $scope.configureInputVO = () => {
            /** 更新模式會有 row（參數種類資料） **/
            if ($scope.row)
                $scope.isUpdate = true;

            $scope.row = $scope.row || {};
            $scope.inputVO = {
                paramType: $scope.row.PARAM_TYPE,
                ptypeName: $scope.row.PTYPE_NAME,
                ptypeDesc: $scope.row.PTYPE_DESC,
                ptypeBuss: $scope.row.PTYPE_BUSS
            }

            $scope.newVO = {}
            $scope.codeData = [];

        }

        /** 確認輸入的參數種類是否已存在資料庫中 **/
        $scope.checkParam = () => {
            if ($scope.inputVO.paramType)
                $scope.sendRecv("CMMGR006", "existType", "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO",
                    $scope.inputVO, (tota) => {
                        if (tota[0].body) {
                            $scope.showErrorMsg(`【${$scope.inputVO.paramType}】參數種類已存在！請重新輸入！`);
                            $scope.inputVO.paramType = '';
                        }
                    });
        }

        /** 儲存參數種類 **/
        $scope.saveType = () => {
            if ($scope.paramtypeForm.$invalid) {
                $scope.showErrorMsg('欄位檢核錯誤：請輸入必填欄位！');
                return;
            }

            if ($scope.lengthIsInvalid()) return;

            $scope.sendRecv("CMMGR006", $scope.isUpdate ? 'updateType' : 'insertType', "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO",
                $scope.inputVO, (_, isError) => {
                    if (!isError) {
                        $scope.showSuccessMsg('ehl_01_common_025');
                        $scope.isUpdate = true;
                        $scope.loadCodeData();
                    }
                }
            );
        }

        /** 刪除參數種類 **/
        $scope.delType = () => {
            $confirm({text: `確定要刪除參數種類 ${$scope.inputVO.paramType}（${$scope.inputVO.ptypeName}）及其底下所有參數編碼資料？`}, {size: 'sm'}).then(() => {
                $scope.sendRecv("CMMGR006", "deleteType", "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO", $scope.inputVO,
                    (_, isError) => {
                        if (isError) {
                            $scope.showErrorMsg('ehl_01_common_005');
                            return;
                        } else {
                            $scope.showSuccessMsg('ehl_01_common_003');
                            $scope.closeThisDialog('successful');
                        }
                    }
                );
            });
        }

        /** 若存在參數種類資料，則載入該種類編碼資料 **/
        $scope.loadCodeData = () => {
            if (!$scope.isUpdate) return;
            $scope.sendRecv("CMMGR006", "inquireCode", "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO", $scope.inputVO,
                (tota, isError) => {
                    if (!isError)
                        $scope.codeData = tota[0].body.paramData;
                }
            );
        }

        /** 編輯參數編碼資料 **/
        $scope.editCode = code => {
            code.isEdit = true;
            /** 使用者更新的資料 **/
            code.next = {
                paramCode: code.PARAM_CODE,
                paramName: code.PARAM_NAME,
                paramDesc: code.PARAM_DESC,
                prevCode: code.PARAM_CODE,          // 保留原本參數編碼，更新時會用到
                paramType: $scope.inputVO.paramType // 添加參數種類
            }
        }

        /** 取消編輯參數編碼資料 **/
        $scope.cancelCode = code => {
            code.isEdit = false;
            code.next = {};
        }

        /** 提供 Paramcode 的拖曳功能，藉此實現參數排序值 **/
        $scope.sortableOptions = {
            stop: (e, ui) => {
                const len = $scope.codeData.length - 1;
                ui.item.sortable.droptargetModel.forEach((row, index) => {
                    row.PARAM_ORDER = len - index;
                });

                let map = {};
                $scope.codeData.forEach(sub => map[sub.PARAM_CODE] = sub.PARAM_ORDER);

                let orderVO = {
                    paramType: $scope.inputVO.paramType,
                    codeMap: map
                }

                /** 進行排序更新 **/
                $scope.sendRecv("CMMGR006", "updateCodeOrder", "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO", orderVO);
            }
        }

        /** 確認是否有重複輸入 PARAM_CODE **/
        $scope.existParamCode = vo => {
            if ($scope.codeData.filter(sub => sub.PARAM_CODE == vo['paramCode']).length) {
                if (vo.prevCode && vo.prevCode == vo['paramCode']) return; // 不檢查是否與原本參數編碼輸入一樣

                $scope.showErrorMsg(`【${vo['paramCode']}】參數編碼已存在！請重新輸入！`);
                vo['paramCode'] = '';
            }
        }

        /** 新增一筆參數到 TBSYSPARAMETER **/
        $scope.insertCode = () => {
            if (!$scope.newVO.paramCode || !$scope.newVO.paramName) {
                $scope.showErrorMsg('欄位檢核錯誤：【參數編碼】及【參數值】為必填欄位！');
                return;
            }

            $scope.newVO.paramType = $scope.inputVO.paramType;
            $scope.sendRecv("CMMGR006", "insertCode", "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO", $scope.newVO,
                (_, isError) => {
                    if (!isError) {
                        $scope.showSuccessMsg('ehl_01_common_001');
                        $scope.addNewVO();
                    }
                }
            );
        }

        /** 將新增的 newVO 加入到 codeData 陣列裡，隨後清空 newVO **/
        $scope.addNewVO = () => {
            let vo = {
                PARAM_CODE: $scope.newVO.paramCode,
                PARAM_NAME: $scope.newVO.paramName,
                PARAM_ORDER: $scope.newVO.paramOrder,
                PARAM_DESC: $scope.newVO.paramDesc
            }
            $scope.codeData.reverse();
            $scope.codeData.push(vo);
            $scope.codeData.reverse();
            $scope.newVO = {};
        }

        /** 儲存參數編碼的更新資料 **/
        $scope.saveCode = code => {
            $scope.sendRecv("CMMGR006", "updateCode", "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO", code.next,
                (_, isError) => {
                    if (!isError) {
                        $scope.showSuccessMsg('ehl_01_common_025');
                        $scope.updateCodeInfo(code);
                    }
                }
            );
        }

        /** 更新使用者的參數編碼資料到原物件 **/
        $scope.updateCodeInfo = code => {
            code.PARAM_CODE = code.next.paramCode;
            code.PARAM_NAME = code.next.paramName;
            code.PARAM_DESC = code.next.paramDesc;
            $scope.cancelCode(code);
        }

        /** 刪除指定參數編碼資料 **/
        $scope.delCode = code => {
            let vo = {
                paramType: $scope.inputVO.paramType,
                paramCode: code.PARAM_CODE
            }

            $scope.sendRecv("CMMGR006", "deleteCode", "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO", vo,
                (_, isError) => {
                    if (!isError) {
                        $scope.showSuccessMsg('ehl_01_common_003');
                        $scope.codeData.splice($scope.codeData.indexOf(code), 1);
                    }
                }
            );
        }

        /** 初始化 **/
        $scope.init = () => {
            $scope.configureInputVO();

            /** 計數器 **/
            $scope.configureCounter('ptypeName', 100, '參數名稱');
            $scope.configureCounter('ptypeDesc', 300, '參數說明');
            /** 初始數字 **/
            $scope.countLength($scope.inputVO, 'ptypeName', $scope.counter);
            $scope.countLength($scope.inputVO, 'ptypeDesc', $scope.counter);

            $scope.loadCodeData();
        }

        $scope.init();
    }
);
