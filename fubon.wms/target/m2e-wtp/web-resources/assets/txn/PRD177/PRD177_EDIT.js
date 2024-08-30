'use strict';
eSoafApp.controller('PRD177_EDITController',
    function ($scope, $controller, getParameter) {
        $controller('BaseController', {$scope: $scope});
        $scope.controllerName = "PRD177_EDITController";
        $controller('CommonUtil', {$scope: $scope}); // 載入 CommonUtil

        const getComboboxOptions = () => {
            getParameter.XML([
                'JSB.INS_PROD.CONTRACTID',
                'JSB.INS_PROD.SUMASSUMEDTYPE',
                'JSB.INS_PROD.POLICY_TYPE',
                'JSB.INS_PROD.PRODUCTRISK',
                'JSB.INS_PROD.RISKRATE'], function(totas) {
                if(len(totas)>0){
                    $scope.mappingSet['CONTRACT_ID'] = totas.data[totas.key.indexOf('JSB.INS_PROD.CONTRACTID')];
                    $scope.mappingSet['SUM_ASSUMED_TYPE'] = totas.data[totas.key.indexOf('JSB.INS_PROD.SUMASSUMEDTYPE')];
                    $scope.mappingSet['POLICY_TYPE'] = totas.data[totas.key.indexOf('JSB.INS_PROD.POLICY_TYPE')];
                    $scope.mappingSet['PRODUCT_RISK'] = totas.data[totas.key.indexOf('JSB.INS_PROD.PRODUCTRISK')];
                    $scope.mappingSet['RISK_RATE'] = totas.data[totas.key.indexOf('JSB.INS_PROD.RISKRATE')];
                }

            });

            $scope.mappingSet['MAINSTAY_PRODUCT'] = [{LABEL: 'Y', DATA: 'Y'}, {LABEL: 'N', DATA: 'N'}]
            $scope.mappingSet['CONTRACT_BONUS'] = [{LABEL: '是', DATA: 'Y'}, {LABEL: '否', DATA: 'N'}];
        }

        const inquireProdCodeAndCheck = currData => {
            $scope.sendRecv("PRD177", "getProdCode", "com.systex.jbranch.app.server.fps.prd177.ProdCodeInputVO",
                {cdIndexList: ['A05', 'A06', 'A08']}, function (tota, isError) {
                    if (!isError) {
                        const data = tota[0].body.list;

                        $scope.mappingSet['PRODUCT_TYPE1'] = _.chain(data).filter(e => e.CD_INDEX === 'A05') // 產品大分類
                            .map(each => ({ LABEL: each.CD_NM, DATA: each.CD_NM }))
                            .value();

                        $scope.mappingSet['PRODUCT_CATEGORY'] = _.chain(data).filter(e => e.CD_INDEX === 'A06') // 產品中分類
                            .map(each => ({ LABEL: each.CD_NM, DATA: each.CD_NM }))
                            .value();

                        $scope.options['CURRENCY'] = _.chain(data).filter(e => e.CD_INDEX === 'A08') // 幣別
                            .map(each => ({
                                label: each.CD_NM, value: each.CD_NM,
                                checked: currData.contains(each.CD_NM)
                            }))
                            .value();
                    }
                });
        };

        const premiumTableChecked = (premiumTableData) => {
            const premiumTable = ['年金', '壽險'];
            $scope.options['PREMIUM_TABLE'] = _.chain(premiumTable)
                .map(each => ({
                    label: each, value: each,
                    checked: premiumTableData.contains(each)
                }))
                .value();
        }

        const channelChecked = (channelData) => {
            const channel = ['一般', '小花平台'];
            $scope.options['CHANNEL'] = _.chain(channel)
                .map(each => ({
                    label: each, value: each,
                    checked: channelData.contains(each)
                }))
                .value();
        }

        const inquireBoundCode = productSerialNum => {
            $scope.sendRecv("PRD177", "inquireBound", "com.systex.jbranch.app.server.fps.prd177.PRD177InputVO",
                {productSerialNum: productSerialNum}, function (tota, isError) {
                    if (!isError) {
                        const {codeList, boundList} = tota[0].body;
                        const channelMap1 = {};
                        const channelMap2 = {};
                        boundList.filter(e => e.CHANNEL.contains('一般')).forEach(e => channelMap1[e.BOUNDITEM] = e);
                        boundList.filter(e => e.CHANNEL.contains('小花')).forEach(e => channelMap2[e.BOUNDITEM] = e);
                        $scope.bound = {
                            // 依照對應的鍵值（CODE.CD_ITEM = BOUND.BOUNDITEM），將 BOUND 的屬性與 CODE 屬性一起變成一個物件。
                            channel1: codeList.map(c => ({CH: '一般', ...c, ...channelMap1[c.CD_ITEM]})), // 一般
                            channel2: codeList.map(c => ({CH: '小花平台', ...c, ...channelMap2[c.CD_ITEM]}))  // 小花
                        }
                    }
                });
        }

        function init() {
            /** 初始日曆 **/
            $scope.productValidFrom = {};
            $scope.initDateOptions($scope.productValidFrom);

            $scope.productOnDt = {};
            $scope.initDateOptions($scope.productOnDt);

            $scope.productValidThru = {};
            $scope.initDateOptions($scope.productValidThru);

            $scope.row = $scope.row || {};
            $scope.dialogTitle = `${$scope.row.CNAME} - ${$scope.row.PRODUCTNAME}`;
            $scope.inputVO = {
                productSerialNum: $scope.row.PRODUCTSERIALNUM,
                insuranceCoSerialNum: $scope.row.INSURANCECOSERIALNUM,
                productId: $scope.row.PRODUCTID,
                mof: $scope.row.MOF,
                productName: $scope.row.PRODUCTNAME,
                mainstayProduct: $scope.row.MAINSTAY_PRODUCT,
                productShortName: $scope.row.PRODUCTSHORTNAME,
                contractId: $scope.row.CONTRACTID,
                addSubFlag: $scope.row.ADDSUB_FLAG,
                productOnDt: $scope.toJsDate($scope.row.PRODUCTON_DT),
                productValidFrom: $scope.toJsDate($scope.row.PRODUCTVALIDFROM),
                productValidThru: $scope.toJsDate($scope.row.PRODUCTVALIDTHRU),
                comm1Flag: $scope.row.COMM1_FLAG,
                comm2Flag: $scope.row.COMM2_FLAG,
                productType1: $scope.row.PRODUCTTYPE1,
                productCategory: $scope.row.PRODUCTCATEGORY,
                sumAssumedType: $scope.row.SUMASSUMEDTYPE,
                premiumTable: $scope.row.PREMIUMTABLE || '',
                contractBonus: $scope.row.CONTRACTBONUS,
                currency1: $scope.row.CURRENCY1 || '',
                channel: $scope.row.CHANNEL || '',
                productRisk: $scope.row.PRODUCTRISK,
                riskRate: $scope.row.RISKRATE,
                commRate: $scope.row.COMMRATE,
                commRateA: $scope.row.COMMRATEA,
                commRateC: $scope.row.COMMRATEC,
                boundRate: $scope.row.BOUNDRATE,
                productPudType: $scope.row.PRODUCTPUDTYPE,
                productPud: $scope.row.PRODUCTPUD,
                productedType: $scope.row.PRODUCTEDTYPE,
                producted: $scope.row.PRODUCTED,
                productExpDate: $scope.row.PRODUCTEXPDATE,
                memo: $scope.row.MEMO,
                chFlag: $scope.row.CH_FLAG,
                aFlag: $scope.row.A_FLAG,
                bFlag: $scope.row.B_FLAG,
                cFlag: $scope.row.C_FLAG,

                commRate2: $scope.row.COMMRATE_2,
                commRateA2: $scope.row.COMMRATEA_2,
                commRateC2: $scope.row.COMMRATEC_2,
                policyType: $scope.row.POLICY_TYPE
            };
            $scope.mappingSet['COMPANY'] = $scope.companies;
            $scope.options = {};

            const {premiumTable, currency1, channel, productSerialNum} = $scope.inputVO;
            getComboboxOptions();
            // 平台
            premiumTableChecked(premiumTable);
            // CODE Table 取得選項資料，並且 check 相關資料
            inquireProdCodeAndCheck(currency1);
            // 案件來源
            channelChecked(channel);
            // BOUND
            inquireBoundCode(productSerialNum)
        }

        init();

        // 只要 Bound 輸入欄位有點選動作，就視為改動
        $scope.boundBlur = row => {
            row.isDirty = true;
        }

        $scope.save = () => {
            $scope.inputVO.premiumTable = _.chain($scope.options['PREMIUM_TABLE']).filter('checked').map('value').join(', ').value();
            $scope.inputVO.currency1 = _.chain($scope.options['CURRENCY']).filter('checked').map('value').join(', ').value();
            $scope.inputVO.channel = _.chain($scope.options['CHANNEL']).filter('checked').map('value').join(', ').value();

            const {premiumTable, currency1, channel} = $scope.inputVO;
            if ($scope.prd177EditForm.$invalid ||
                !premiumTable || !currency1 || !channel // 這三個複選欄位皆必填，至少勾選一個
            ) {
                $scope.showErrorMsg('欄位檢核錯誤:必要輸入欄位');
                return;
            }

            $scope.inputVO.prodBoundGroup = [...$scope.bound.channel1, ...$scope.bound.channel2]
                .filter(e => e.isDirty)
                .map(transform);

            $scope.sendRecv("PRD177", "save", "com.systex.jbranch.app.server.fps.prd177.PRD177DataVO",
                $scope.inputVO, function (tota, isError) {
                    if (isError) {
                        $scope.showErrorMsg(tota[0].body.msgData);
                    }
                    if (tota.length > 0) {
                        $scope.showSuccessMsg('ehl_01_common_004');
                        $scope.closeThisDialog('successful');
                    }
                });
        };

        const transform = bound => {
            // 如果該平台的 TBJSB_INS_PROD_BOUND 初始查詢無資料，則使用預設值
            return {
                productSerialNum: bound.PRODUCTSERIALNUM || $scope.inputVO.productSerialNum,
                boundItem: bound.CD_ITEM,
                boundName: bound.CD_DESC,
                add1StartMonth: bound.ADD_1_START_MONTH,
                add1EndMonth: bound.ADD_1_END_MONTH,
                add1CommissionRate: bound.ADD_1_COMMISSIONRATE,
                add1CommRateA: bound.ADD_1_COMMRATEA,
                channel: bound.CHANNEL || bound.CH
            }
        }
    });
