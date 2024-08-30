package com.systex.jbranch.ws.external.service.domain.insurance_status;

import java.text.SimpleDateFormat;

public class InsuranceStatusConfig {
    public static final String SUCCESS = "s"; // 成功
    public static final String FAIL = "f";    // 失敗
    public static final String WARNING = "w"; // 警告

    public static final String APP_000_001 = "APP-000-001"; // 成功
    public static final String APP_000_002 = "APP-000-002"; // 失敗
    public static final String APP_000_003 = "APP-000-003"; // 查無資料
    public static final String APP_000_004 = "APP-000-004"; // 連線逾時
    public static final String APP_000_005 = "APP-000-005"; // 來源不合法

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // 日期格式
}
