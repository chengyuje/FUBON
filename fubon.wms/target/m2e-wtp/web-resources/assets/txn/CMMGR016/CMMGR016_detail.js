/**================================================================================================
 @program: CMMGR016_detail.js
 @author Eli
 @description 報表詳細頁面，提供檢索、編輯、新增功能
 @date 20190502 增加驗證檔功能
 @date 20190705 新增流程邏輯
 填寫報表資訊 -> 選擇動作 -> 放置報表 -> 填寫 Ftp 資訊 -> 填寫 Job 資訊
 -> 寄送報表 -> 填寫寄信參數   ->
 @date 20190711 調整執行流程。修正頁面 Bug。
 @date 20190729 修正 JOB 參數列錯誤邏輯。調整 FTP 預設來源檔名。

 =================================================================================================**/
'use strict';
eSoafApp.controller('CMMGR016_DetailController', ($rootScope, $scope, $controller, ngDialog, getParameter) => {
    $controller('BaseController', {$scope: $scope});
    $scope.controllerName = "CMMGR016_DetailController";

    /** 載入 CommonUtil **/
    $controller('CommonUtil', {$scope: $scope});

    /** 新增修改動作 **/
    $scope.createOrUpdate = () => {
        $scope.checkLogic(() => {
            $scope.reportLogic();
            $scope.ftpLogic();
            $scope.mailLogic();
            $scope.jobLogic();
            setTimeout(()=> $scope.closeThisDialog('successful'), 500);
        });
    }

    /** 檢查邏輯 **/
    $scope.checkLogic = logic => {
        if ($scope.checkInvalid($scope.form.report, '報表')) return;
        if ($scope.needChk && $scope.checkInvalid($scope.form.check, '驗證檔')) return;
        if ($scope.needFtp && $scope.checkInvalid($scope.form.ftp, 'FTP')) return;
        if ($scope.needMail && $scope.checkInvalid($scope.form.mail, 'MAIL')) return;
        if ($scope.needJob && $scope.checkInvalid($scope.form.job, 'JOB')) return;

        logic();
    }

    /** 檢查欄位是否有填寫完畢 **/
    $scope.checkInvalid = (form, text) => {
        if (form.$invalid) {
            $scope.showErrorMsg(`${text}資訊尚有欄位未完成填寫！`);
            return true;
        }
        return false;
    }

    /** Report 新增修改 **/
    $scope.reportLogic = () => {
        $scope.formatReport();
        $scope.sendRecv('CMMGR016', $scope.isCreate ? 'insert' : 'update', 'com.systex.jbranch.fubon.bth.job.business_logic.ReportConfig',
            $scope.reportVO, (_, isError) => $scope.afterAction(isError, 'Report'));
    }

    /** FTP 新增/修改/刪除 **/
    $scope.ftpLogic = () => {
        /** 不需要且原本沒有 => 不做任何動作 **/
        if (!$scope.needFtp && !$scope.hasFtp) return;

        /** 原本有資訊，但修改時清除了 FTP 資訊的話，儲存代表著 "刪除" **/
        if (!$scope.needFtp && $scope.hasFtp) {
            $scope.ftpVO.tipFtpSettingId = `RPT.${$scope.code}`;
            $scope.ftpVO.operType = 'Delete';
        } else {
            /** 原本沒有 FTP 就用 Create、否則就是 Update **/
            $scope.ftpVO.operType = !$scope.hasFtp? 'Create': 'Update';
        }

        $scope.sendRecv('CMMGR014', 'operation', 'com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO',
            $scope.ftpVO, (_, isError) => $scope.afterAction(isError, 'FTP'));
    }

    /** MAIL 新增/修改/刪除 **/
    $scope.mailLogic = () => {
        /** 不需要且原本沒有 => 不做任何動作 **/
        if (!$scope.needMail && !$scope.hasMail) return;

        let typeAction;
        /** 原本有資訊，但修改時清除了 Mail 資訊的話，儲存代表著 "刪除" **/
        if (!$scope.needMail && $scope.hasMail) {
            $scope.mailVO.paramType = `RPT.${$scope.code}`;
            typeAction = 'deleteType';
        } else {
            /** 原本沒有 MAIL 就用 Create、否則就是 Update **/
            typeAction = !$scope.hasMail? 'insertType': 'updateType';

            $scope.codeLogic(!$scope.hasMail);
        }

        $scope.sendRecv("CMMGR006", typeAction, "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO",
            $scope.mailVO, (_, isError) => $scope.afterAction(isError, 'MAIL'));
    }

    /** Mail 的參數集邏輯 **/
    $scope.codeLogic = isCreate => {
        $scope.mailVO.codeData.forEach(each => {
            each.prevCode = each.paramCode;
            /** 如果使用者沒有填上 paramName，塞入空白到資料庫 **/
            each.paramName = each.paramName == undefined? ' ': each.paramName;

            $scope.sendRecv("CMMGR006", isCreate? 'insertCode': 'updateCode',
                "com.systex.jbranch.app.server.fps.cmmgr006.CMMGR006InputVO", each, () => {});
        });
    }

    /** JOB 新增/修改/刪除 **/
    $scope.jobLogic = () => {
        /** 不需要且原本沒有 => 不做任何動作 **/
        if (!$scope.needJob && !$scope.hasJob) return;

        /** 原本有資訊，但修改時清除了 JOB 資訊的話，儲存代表著 "刪除" **/
        if (!$scope.needJob && $scope.hasJob) {
            $scope.jobVO.jobid = `RPT.${$scope.code}`;
            $scope.jobVO.type = 'Delete';
        } else {
            /** 原本沒有 JOB 就用 Create、否則就是 Update **/
            $scope.jobVO.type = !$scope.hasJob? 'Create': 'Update';
            /** 更新的話需要再多個 updatejobid 欄位，其值為 jobid **/
            if ($scope.hasJob)
                $scope.jobVO.updatejobid = $scope.jobVO.jobid;
        }

        $scope.sendRecv("CMMGR007", "operation", "com.systex.jbranch.app.server.fps.cmmgr007.CMMGR007InputVO",
            $scope.jobVO, (_, isError) => $scope.afterAction(isError, 'JOB'));
    }

    /** 新增修改執行完後的動作 **/
    $scope.afterAction = (isError, action) => $scope.showMsg(`【${$scope.code}：${action}】執行${isError? '失敗': '成功'}`);

    /** 格式化報表資訊欄位 **/
    $scope.formatReport = () => {
        const vo = $scope.reportVO;
        /** SQL 去掉結尾分號 **/
        let sql = vo.sql;
        if (sql[sql.length - 1] == ';')
            vo.sql = sql.substring(0, sql.length - 1);

        /** checkBox空值視為 N **/
        if (!vo.needHeader)
            vo.needHeader = 'N';

        if (!vo.needDoubleQuotes)
            vo.needDoubleQuotes = 'N';

        /** 將驗證檔內容的各個欄位寬度（名稱、筆數、日期格式）整合成一個字串並以","隔開 **/
        if ($scope.needChk)
            vo.chkWidth = `${vo.cWidth1},${vo.cWidth2},${vo.cWidth3}`;
    }

    /** 取得 FTP Server ID 列表 **/
    $scope.getServerIdList = () => {
        $scope.sendRecv("CMMGR014", "init", "com.systex.jbranch.app.server.fps.cmmgr014.CMMGR014InputVO",
            {}, (tota, isError) => {
                if (!isError) {
                    $scope.mappingSet['hostId'] = [];
                    tota[0].body.hostIdList.filter(host => {
                        $scope.mappingSet['hostId'].push({
                            LABEL: host.IP,
                            DATA: host.HOSTID
                        });
                    });
                }
            });
    }

    /** 開始建立 FTP **/
    $scope.startFtp = () => {
        if (!$scope.reportVO.code) {
            $scope.showErrorMsg('傳檔 ID 固定為 RPT.報表代號，請輸入報表代號，方可進行！');
            return;
        }

        $scope.ftpVO = {
            tipFtpSettingId: `RPT.${$scope.reportVO.code}`,
            /** 來源檔目錄 **/
            tipSrcDirectory: '%APP_ROOT%/temp/reports/',
            /** 來源檔檔名預設報表名稱 + 報表種類（副檔名）**/
            tipSrcFileName: `${$scope.reportVO.name || ''}${/C/.test($scope.reportVO.type)? '.csv': /T/.test($scope.reportVO.type)? '.txt': ''}`,
            /** 檢核檔名 **/
            tipCheckFile: '',
            /** 重試次數 **/
            tipRepeat: 0,
            /** 檢核檔重試間隔時間 **/
            tipRepeatInterval: 0
        }
        $scope.needFtp = true;
    }

    /** 清除 ftpVO **/
    $scope.clearFtp = () => {
        $scope.ftpVO = {}
        $scope.needFtp = false;
    }

    /** 開始建立驗證檔 **/
    $scope.startChk = () => {
        $scope.needChk = true;
    }

    /** 清除驗證檔 **/
    $scope.clearChk = () => {
        $scope.needChk = false;
        $scope.reportVO.chkFileName = undefined;
        $scope.reportVO.chkName = undefined;
        $scope.reportVO.chkDateFormat = undefined;
        $scope.reportVO.chkWidth = undefined;
        $scope.reportVO.cWidth1 = undefined;
        $scope.reportVO.cWidth2 = undefined;
        $scope.reportVO.cWidth3 = undefined;
    }

    /** 開始建立 mail 參數 **/
    $scope.startMail = () => {
        if (!$scope.reportVO.code) {
            $scope.showErrorMsg('參數種類固定為 RPT.報表代號，請輸入報表代號，方可進行！');
            return;
        }

        $scope.needMail = true;
        $scope.mailVO = {
            paramType: `RPT.${$scope.reportVO.code}`,
            ptypeName: $scope.reportVO.name,
            /** 種類為業務參數 **/
            ptypeBuss: 'BU',
            ptypeDesc: $scope.reportVO.desc,
            /** TBSYSPARAMETER 固定參數 **/
            codeData: ['RPT', 'RECEIVER', 'ZIP_MAIL_SUBJECT', 'ZIP_MAIL_CONTENT', 'ZIP_PASSWORD', 'PS_MAIL_SUBJECT', 'PS_MAIL_CONTENT', 'NOT_ATTACHED']
                .map((item, index) => {
                    return {
                        paramType: `RPT.${$scope.reportVO.code}`,
                        paramOrder: index,
                        paramCode: item,
                        paramName: undefined,
                        paramDesc: undefined,
                        tip: $scope.paramTip[index]
                    }
                })
        }
        /** TBSYSPARAMETER 預設值**/
        /** RPT 為 報表代號 **/
        $scope.mailVO.codeData[0].paramName = $scope.reportVO.code;
        /** NOT_ATTACHED 為 false => 預設信件附檔 **/
        $scope.mailVO.codeData[7].paramName = false;
    }

    /** 清除 mail **/
    $scope.clearMail = () => {
        $scope.needMail = false;
        $scope.mailVO = {};
    }

    /** 開始建立 job 參數 **/
    $scope.startJob = () => {
        if (!$scope.reportVO.code) {
            $scope.showErrorMsg('JOB 代號固定為 RPT.報表代號，請輸入報表代號，方可進行！');
            return;
        }
        $scope.needJob = true;
        $scope.jobVO = {
            jobid: `RPT.${$scope.reportVO.code}`,
            jobname: $scope.reportVO.name,
            description: $scope.reportVO.desc,
            beanid: 'JobClient',
            action: $scope.getAction(),
            parameters: $scope.getParam($scope.getAction())
        }
    }

    /** 根據是否點選 FTP 與 Mail 頁籤來決定哪個 Job Action，預設為產出 **/
    $scope.getAction = () => $scope.needMail ? 'mailRpt' : $scope.needFtp ? 'genRpt' : 'genRpt';

    /** 取得 Job 的參數列 **/
    $scope.getParam = action => action ? `method=${action};arg=${action == 'mailRpt'? 'RPT.': ''}${$scope.reportVO.code};${action == 'genRpt' ? 'ftpPutCode=RPT.' + $scope.reportVO.code + ';' : ''}` : '';

    /** Job Action 選擇切換時 **/
    $scope.changeAction = () => {
        if ($scope.doNotChangedJobParameters) {
            // 一開始 loading 完資料後，因為 $scope.jobVO.action 被指派了數值，所以會觸發 changed 事件進而改變 Parameters
            // 在這裡避免一開始的觸發，之後再正常觸發
            $scope.doNotChangedJobParameters = false;
        } else {
            $scope.jobVO.parameters = $scope.getParam($scope.jobVO.action);
        }
    }

    /** 清除 Job **/
    $scope.clearJob = () => {
        $scope.needJob = false;
        $scope.jobVO = {};
    }

    /** 開啟規則頁面視窗 **/
    $scope.openDateRuleUI = () => {
        ngDialog.open({
            template: 'assets/txn/CMMGR016/CMMGR016_ftpFileDateRule.html',
            className: 'CMMGR016DateRule',
            showClose: false,
            controller: ['$scope', function ($scope) {
            }]
        });
    }

    /** 載入參數 **/
    $scope.loadParameter = () => {
        $scope.mappingSet = {
            /** 報表種類 **/
            type: [{LABEL: '無', DATA: 'N'}, {LABEL: 'CSV', DATA: 'C'}, {LABEL: 'TXT', DATA: 'T'}],
            /** 分隔方式 **/
            txt_delimiter: [{LABEL: '逗點分隔', DATA: 'DOT'}, {LABEL: '固定寬度', DATA: 'FIX'}],
            /** 執行環境 **/
            sql_type: [{LABEL: '新理專', DATA: 'WMS'}, {LABEL: '保險證照', DATA: 'INS'}, {LABEL: '叫號', DATA: 'BNK'}],
            /** 檔案編碼 **/
            coding: [{LABEL: 'UTF-8', DATA: 'U'}, {LABEL: 'MS950', DATA: 'M'}],
            /** Job 欲執行的動作 **/
            jobAct: [{LABEL: '生產報表', DATA: 'genRpt'}, {LABEL: '寄送報表', DATA: 'mailRpt'}]
        }

        /** ServerId **/
        $scope.getServerIdList();

        /** 參數種類 **/
        getParameter.XML(['TBSYSPARAMTYPE.PTYPE_BUSS'], (totas) => {
            if (totas)
                $scope.mappingSet['TBSYSPARAMTYPE_PTYPE_BUSS'] = totas.data[totas.key.indexOf('TBSYSPARAMTYPE.PTYPE_BUSS')];
        });

        /** mail 的參數提示 **/
        $scope.paramTip = ['欲寄送之報表代號', '收件者', '壓縮檔信件主旨', '壓縮檔信件內容', '密碼（default:日期YYYYMMDD, 空白代表不需密碼）', '密碼信主旨（如有）', '密碼信內容（如有）', '報表不須以附檔型式'];
    }

    /** 根據不同功能進入詳細頁面有不同的狀態 **/
    $scope.statusInit = () => {
        const type = $scope.funcType;
        $scope.isCreateOrUpdate = /C|U/.test(type);   // 建立或修改狀態
        $scope.isRetrieveOrUpdate = /R|U/.test(type); // 檢索或修改狀態
        $scope.isCreate = /C/.test(type);             // 建立狀態
        $scope.isUpdate = /U/.test(type);             // 修改狀態
        $scope.isRetrieve = /R/.test(type);           // 檢索狀態
    }


    /** 將資料以報表 reportVO 物件型式返回 **/
    $scope.getReportVO = data => {
        let reportVO = {
            code: data.RPT_CODE,
            sql: data.RPT_SQL,
            name: data.RPT_NAME,
            type: data.RPT_TYPE,
            width: data.FIX_WIDTH,
            delimiter: data.DELIMITER,
            needHeader: data.NEED_HEADER,
            sqlType: data.SQL_TYPE,
            desc: data.RPT_DESC,
            fileCoding: data.FILE_CODING,
            needDoubleQuotes: data.NEED_DOUBLE_QUOTES,
            /** 驗證檔欄位 **/
            chkFileName: data.CHK_FILE_NAME,
            chkName: data.CHK_NAME,
            chkDateFormat: data.CHK_DATE_FORMAT,
            chkWidth: data.CHK_WIDTH
        }

        if (data.CHK_WIDTH) {
            let widthArr = data.CHK_WIDTH.split(',');
            reportVO.cWidth1 = +widthArr[0];
            reportVO.cWidth2 = +widthArr[1];
            reportVO.cWidth3 = +widthArr[2];
        }
        return reportVO;
    }

    /** 將資料以 FTP ftpVO 物件型式返回 **/
    $scope.getFtpVO = data => {
        return {
            tipFtpSettingId: data.FTPSETTINGID,
            tipSrcDirectory: data.SRCDIRECTORY,
            tipSrcFileName: data.SRCFILENAME,
            tipCheckFile: data.CHECKFILE,
            tipDesDirectory: data.DESDIRECTORY,
            tipDesFileName: data.DESFILENAME,
            tipRepeat: data.REPEAT,
            tipRepeatInterval: data.REPEATINTERVAL,
            cmbHostId: data.HOSTID,
            srcDelete: data.SRCDELETE
        }
    }

    /** 將資料以 MAIL mailVO 物件型式返回 **/
    $scope.getMailVO = data => {
        return {
            paramType: data.PARAM_TYPE,
            ptypeName: data.PTYPE_NAME,
            ptypeBuss: data.PTYPE_BUSS,
            ptypeDesc: data.PTYPE_DESC
        }
    }

    /** 將 Mail 的參數集其參數以駝峰型態返回 **/
    $scope.getCodeDataVO = list => {
        if (list && list.length)
            return list.map((each, index) => {
                return {
                    paramType: each.PARAM_TYPE,
                    paramCode: each.PARAM_CODE,
                    /** 如果 paramName 是空白的，代表之前沒有打上數值，在這裡移除空白。 **/
                    paramName: each.PARAM_NAME.trim() || undefined,
                    paramOrder: each.PARAM_ORDER,
                    paramDesc: each.PARAM_DESC,
                    tip: $scope.paramTip[index]
                }
            });
    }

    /** 將資料以 JOB jobVO 物件型式返回 **/
    $scope.getJobVO = data => {
        return {
            jobid: data.JOBID,
            jobname: data.JOBNAME,
            description: data.DESCRIPTION,
            beanid: data.BEANID,
            action: /.+genRpt.+/.test(data.PARAMETERS) ? 'genRpt' : /.+mailRpt.+/.test(data.PARAMETERS) ? 'mailRpt' : '',
            parameters: data.PARAMETERS,
            precondition: data.PRECONDITION,
            postcondition: data.POSTCONDITION
        }
    }

    /** 檢索模式與修改模式載入資料 **/
    $scope.loadData = () => {
        if ($scope.isRetrieveOrUpdate)
            $scope.sendRecv('CMMGR016', 'detail', 'com.systex.jbranch.app.server.fps.cmmgr016.CMMGR016InputVO',
                {code: $scope.code}, (tota, isError) => {
                    if (!isError) {
                        const data = tota[0].body.resultList[0];

                        $scope.reportVO = $scope.getReportVO(data);
                        if ($scope.reportVO.chkFileName) {
                            $scope.needChk = true;
                            $scope.hasChk = true;
                        }

                        $scope.ftpVO = $scope.getFtpVO(data);
                        if ($scope.ftpVO.tipFtpSettingId) {
                            $scope.needFtp = true;
                            $scope.hasFtp = true;
                        }

                        $scope.mailVO = $scope.getMailVO(data);
                        $scope.mailVO.codeData = $scope.getCodeDataVO(tota[0].body.codeData);
                        if ($scope.mailVO.paramType) {
                            $scope.needMail = true;
                            $scope.hasMail = true;
                        }

                        $scope.jobVO = $scope.getJobVO(data);
                        if ($scope.jobVO.jobid) {
                            $scope.doNotChangedJobParameters = true; // 為防止因為 $scope.job.action 改變而觸發 changed 事件改變 parameters
                            $scope.needJob = true;
                            $scope.hasJob = true;
                        }
                    }
                }
            );
        else {
            /** 創建模式初始化即可 **/
            $scope.initReport();
            $scope.clearFtp();
            $scope.clearMail();
            $scope.clearJob();
        }
    }

    /** 初始化報表與驗證檔 **/
    $scope.initReport = () => {
        $scope.reportVO = {};
    }

    /** 清空分隔與寬度，原本使用 ng-change 來達到目的。
     * 不過因為 combo-box 會執行兩次 change，就會導致一直被清空，因此改用 $watch **/
    $scope.$watch('reportVO.type', (prev, next) => {
        if (next && next !== prev) {
            $scope.reportVO.delimiter = '';
            $scope.reportVO.width = '';
        }
    });

    /** 初始化 **/
    $scope.init = () => {
        /** 表格初始化 **/
        $scope.form = {};
        /** 參數初始化 **/
        $scope.loadParameter();
        /** 狀態初始化 **/
        $scope.statusInit();
        /** 資料載入 **/
        $scope.loadData();
    }
    $scope.init();

});