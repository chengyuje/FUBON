package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBJSB_INS_PROD_TARGETVO extends VOBase {

    /** identifier field */
    private TBJSB_INS_PROD_TARGETPK comp_id;

    /** nullable persistent field */
    private String TARGET_NAME;

    /** nullable persistent field */
    private String TRANSFER_FLG;

    /** nullable persistent field */
    private String TARGET_CURR;

    /** nullable persistent field */
    private String TARGET_RISK;

    /** nullable persistent field */
    private String INT_TYPE;

    /** nullable persistent field */
    private String P01;

    /** nullable persistent field */
    private String P02;

    /** nullable persistent field */
    private String P03;

    /** nullable persistent field */
    private String P04;

    /** nullable persistent field */
    private String P05;

    /** nullable persistent field */
    private String P06;

    /** nullable persistent field */
    private String P07;

    /** nullable persistent field */
    private String P08;

    /** nullable persistent field */
    private String P09;

    /** nullable persistent field */
    private String P10;

    /** nullable persistent field */
    private String P11;

    /** nullable persistent field */
    private String P12;

    /** nullable persistent field */
    private String P13;

    /** nullable persistent field */
    private String P14;

    /** nullable persistent field */
    private String P15;

    /** nullable persistent field */
    private String P16;

    /** nullable persistent field */
    private String P17;

    /** nullable persistent field */
    private String P18;

    /** nullable persistent field */
    private String P19;

    /** nullable persistent field */
    private String P20;

    /** nullable persistent field */
    private String P21;

    /** nullable persistent field */
    private String P22;

    /** nullable persistent field */
    private String P23;

    /** nullable persistent field */
    private String P24;

    /** nullable persistent field */
    private String P25;

    /** nullable persistent field */
    private String P26;

    /** nullable persistent field */
    private String P27;

    /** nullable persistent field */
    private String P28;

    /** nullable persistent field */
    private String P29;

    /** nullable persistent field */
    private String P30;

    /** nullable persistent field */
    private String P31;

    /** nullable persistent field */
    private String P32;

    /** nullable persistent field */
    private String P33;

    /** nullable persistent field */
    private String P34;

    /** nullable persistent field */
    private String P35;

    /** nullable persistent field */
    private String P36;

    /** nullable persistent field */
    private String P37;

    /** nullable persistent field */
    private String P38;

    /** nullable persistent field */
    private String P39;

    /** nullable persistent field */
    private String P40;

    /** nullable persistent field */
    private String P41;

    /** nullable persistent field */
    private String P42;

    /** nullable persistent field */
    private String P43;

    /** nullable persistent field */
    private String P44;

    /** nullable persistent field */
    private String P45;

    /** nullable persistent field */
    private String P46;

    /** nullable persistent field */
    private String P47;

    /** nullable persistent field */
    private String P48;

    /** nullable persistent field */
    private String P49;

    /** nullable persistent field */
    private String P50;

    /** nullable persistent field */
    private String P51;

    /** nullable persistent field */
    private String P52;

    /** nullable persistent field */
    private String P53;

    /** nullable persistent field */
    private String P54;

    /** nullable persistent field */
    private String P55;

    /** nullable persistent field */
    private String P56;

    /** nullable persistent field */
    private String P57;

    /** nullable persistent field */
    private String P58;

    /** nullable persistent field */
    private String P59;

    /** nullable persistent field */
    private String P60;

    /** nullable persistent field */
    private String P61;

    /** nullable persistent field */
    private String P62;

    /** nullable persistent field */
    private String P63;

    /** nullable persistent field */
    private String P64;

    /** nullable persistent field */
    private String P65;

    /** nullable persistent field */
    private String P66;

    /** nullable persistent field */
    private String P67;

    /** nullable persistent field */
    private String P68;

    /** nullable persistent field */
    private String P69;

    /** nullable persistent field */
    private String P70;

    /** nullable persistent field */
    private String P71;

    /** nullable persistent field */
    private String P72;

    /** nullable persistent field */
    private String P73;

    /** nullable persistent field */
    private String P74;

    /** nullable persistent field */
    private String P75;

    /** nullable persistent field */
    private String P76;

    /** nullable persistent field */
    private String P77;

    /** nullable persistent field */
    private String P78;

    /** nullable persistent field */
    private String P79;

    /** nullable persistent field */
    private String P80;

    /** nullable persistent field */
    private String P81;

    /** nullable persistent field */
    private String P82;

    /** nullable persistent field */
    private String P83;

    /** nullable persistent field */
    private String P84;

    /** nullable persistent field */
    private String P85;

    /** nullable persistent field */
    private String P86;

    /** nullable persistent field */
    private String P87;

    /** nullable persistent field */
    private String P88;

    /** nullable persistent field */
    private String P89;

    /** nullable persistent field */
    private String P90;

    /** nullable persistent field */
    private String P91;

    /** nullable persistent field */
    private String P92;

    /** nullable persistent field */
    private String P93;

    /** nullable persistent field */
    private String P94;

    /** nullable persistent field */
    private String P95;

    /** nullable persistent field */
    private String P96;

    /** nullable persistent field */
    private String P97;

    /** nullable persistent field */
    private String P98;

    /** nullable persistent field */
    private String P99;

    /** nullable persistent field */
    private String P100;

    /** nullable persistent field */
    private Timestamp CREATEDT;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBJSB_INS_PROD_TARGET";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBJSB_INS_PROD_TARGETVO(TBJSB_INS_PROD_TARGETPK comp_id, String TARGET_NAME, String TRANSFER_FLG, String TARGET_CURR, String TARGET_RISK, String INT_TYPE, String P01, String P02, String P03, String P04, String P05, String P06, String P07, String P08, String P09, String P10, String P11, String P12, String P13, String P14, String P15, String P16, String P17, String P18, String P19, String P20, String P21, String P22, String P23, String P24, String P25, String P26, String P27, String P28, String P29, String P30, String P31, String P32, String P33, String P34, String P35, String P36, String P37, String P38, String P39, String P40, String P41, String P42, String P43, String P44, String P45, String P46, String P47, String P48, String P49, String P50, String P51, String P52, String P53, String P54, String P55, String P56, String P57, String P58, String P59, String P60, String P61, String P62, String P63, String P64, String P65, String P66, String P67, String P68, String P69, String P70, String P71, String P72, String P73, String P74, String P75, String P76, String P77, String P78, String P79, String P80, String P81, String P82, String P83, String P84, String P85, String P86, String P87, String P88, String P89, String P90, String P91, String P92, String P93, String P94, String P95, String P96, String P97, String P98, String P99, String P100, Timestamp CREATEDT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.TARGET_NAME = TARGET_NAME;
        this.TRANSFER_FLG = TRANSFER_FLG;
        this.TARGET_CURR = TARGET_CURR;
        this.TARGET_RISK = TARGET_RISK;
        this.INT_TYPE = INT_TYPE;
        this.P01 = P01;
        this.P02 = P02;
        this.P03 = P03;
        this.P04 = P04;
        this.P05 = P05;
        this.P06 = P06;
        this.P07 = P07;
        this.P08 = P08;
        this.P09 = P09;
        this.P10 = P10;
        this.P11 = P11;
        this.P12 = P12;
        this.P13 = P13;
        this.P14 = P14;
        this.P15 = P15;
        this.P16 = P16;
        this.P17 = P17;
        this.P18 = P18;
        this.P19 = P19;
        this.P20 = P20;
        this.P21 = P21;
        this.P22 = P22;
        this.P23 = P23;
        this.P24 = P24;
        this.P25 = P25;
        this.P26 = P26;
        this.P27 = P27;
        this.P28 = P28;
        this.P29 = P29;
        this.P30 = P30;
        this.P31 = P31;
        this.P32 = P32;
        this.P33 = P33;
        this.P34 = P34;
        this.P35 = P35;
        this.P36 = P36;
        this.P37 = P37;
        this.P38 = P38;
        this.P39 = P39;
        this.P40 = P40;
        this.P41 = P41;
        this.P42 = P42;
        this.P43 = P43;
        this.P44 = P44;
        this.P45 = P45;
        this.P46 = P46;
        this.P47 = P47;
        this.P48 = P48;
        this.P49 = P49;
        this.P50 = P50;
        this.P51 = P51;
        this.P52 = P52;
        this.P53 = P53;
        this.P54 = P54;
        this.P55 = P55;
        this.P56 = P56;
        this.P57 = P57;
        this.P58 = P58;
        this.P59 = P59;
        this.P60 = P60;
        this.P61 = P61;
        this.P62 = P62;
        this.P63 = P63;
        this.P64 = P64;
        this.P65 = P65;
        this.P66 = P66;
        this.P67 = P67;
        this.P68 = P68;
        this.P69 = P69;
        this.P70 = P70;
        this.P71 = P71;
        this.P72 = P72;
        this.P73 = P73;
        this.P74 = P74;
        this.P75 = P75;
        this.P76 = P76;
        this.P77 = P77;
        this.P78 = P78;
        this.P79 = P79;
        this.P80 = P80;
        this.P81 = P81;
        this.P82 = P82;
        this.P83 = P83;
        this.P84 = P84;
        this.P85 = P85;
        this.P86 = P86;
        this.P87 = P87;
        this.P88 = P88;
        this.P89 = P89;
        this.P90 = P90;
        this.P91 = P91;
        this.P92 = P92;
        this.P93 = P93;
        this.P94 = P94;
        this.P95 = P95;
        this.P96 = P96;
        this.P97 = P97;
        this.P98 = P98;
        this.P99 = P99;
        this.P100 = P100;
        this.CREATEDT = CREATEDT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBJSB_INS_PROD_TARGETVO() {
    }

    /** minimal constructor */
    public TBJSB_INS_PROD_TARGETVO(TBJSB_INS_PROD_TARGETPK comp_id) {
        this.comp_id = comp_id;
    }

    public TBJSB_INS_PROD_TARGETPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(TBJSB_INS_PROD_TARGETPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTARGET_NAME() {
        return this.TARGET_NAME;
    }

    public void setTARGET_NAME(String TARGET_NAME) {
        this.TARGET_NAME = TARGET_NAME;
    }

    public String getTRANSFER_FLG() {
        return this.TRANSFER_FLG;
    }

    public void setTRANSFER_FLG(String TRANSFER_FLG) {
        this.TRANSFER_FLG = TRANSFER_FLG;
    }

    public String getTARGET_CURR() {
        return this.TARGET_CURR;
    }

    public void setTARGET_CURR(String TARGET_CURR) {
        this.TARGET_CURR = TARGET_CURR;
    }

    public String getTARGET_RISK() {
        return this.TARGET_RISK;
    }

    public void setTARGET_RISK(String TARGET_RISK) {
        this.TARGET_RISK = TARGET_RISK;
    }

    public String getINT_TYPE() {
        return this.INT_TYPE;
    }

    public void setINT_TYPE(String INT_TYPE) {
        this.INT_TYPE = INT_TYPE;
    }

    public String getP01() {
        return this.P01;
    }

    public void setP01(String P01) {
        this.P01 = P01;
    }

    public String getP02() {
        return this.P02;
    }

    public void setP02(String P02) {
        this.P02 = P02;
    }

    public String getP03() {
        return this.P03;
    }

    public void setP03(String P03) {
        this.P03 = P03;
    }

    public String getP04() {
        return this.P04;
    }

    public void setP04(String P04) {
        this.P04 = P04;
    }

    public String getP05() {
        return this.P05;
    }

    public void setP05(String P05) {
        this.P05 = P05;
    }

    public String getP06() {
        return this.P06;
    }

    public void setP06(String P06) {
        this.P06 = P06;
    }

    public String getP07() {
        return this.P07;
    }

    public void setP07(String P07) {
        this.P07 = P07;
    }

    public String getP08() {
        return this.P08;
    }

    public void setP08(String P08) {
        this.P08 = P08;
    }

    public String getP09() {
        return this.P09;
    }

    public void setP09(String P09) {
        this.P09 = P09;
    }

    public String getP10() {
        return this.P10;
    }

    public void setP10(String P10) {
        this.P10 = P10;
    }

    public String getP11() {
        return this.P11;
    }

    public void setP11(String P11) {
        this.P11 = P11;
    }

    public String getP12() {
        return this.P12;
    }

    public void setP12(String P12) {
        this.P12 = P12;
    }

    public String getP13() {
        return this.P13;
    }

    public void setP13(String P13) {
        this.P13 = P13;
    }

    public String getP14() {
        return this.P14;
    }

    public void setP14(String P14) {
        this.P14 = P14;
    }

    public String getP15() {
        return this.P15;
    }

    public void setP15(String P15) {
        this.P15 = P15;
    }

    public String getP16() {
        return this.P16;
    }

    public void setP16(String P16) {
        this.P16 = P16;
    }

    public String getP17() {
        return this.P17;
    }

    public void setP17(String P17) {
        this.P17 = P17;
    }

    public String getP18() {
        return this.P18;
    }

    public void setP18(String P18) {
        this.P18 = P18;
    }

    public String getP19() {
        return this.P19;
    }

    public void setP19(String P19) {
        this.P19 = P19;
    }

    public String getP20() {
        return this.P20;
    }

    public void setP20(String P20) {
        this.P20 = P20;
    }

    public String getP21() {
        return this.P21;
    }

    public void setP21(String P21) {
        this.P21 = P21;
    }

    public String getP22() {
        return this.P22;
    }

    public void setP22(String P22) {
        this.P22 = P22;
    }

    public String getP23() {
        return this.P23;
    }

    public void setP23(String P23) {
        this.P23 = P23;
    }

    public String getP24() {
        return this.P24;
    }

    public void setP24(String P24) {
        this.P24 = P24;
    }

    public String getP25() {
        return this.P25;
    }

    public void setP25(String P25) {
        this.P25 = P25;
    }

    public String getP26() {
        return this.P26;
    }

    public void setP26(String P26) {
        this.P26 = P26;
    }

    public String getP27() {
        return this.P27;
    }

    public void setP27(String P27) {
        this.P27 = P27;
    }

    public String getP28() {
        return this.P28;
    }

    public void setP28(String P28) {
        this.P28 = P28;
    }

    public String getP29() {
        return this.P29;
    }

    public void setP29(String P29) {
        this.P29 = P29;
    }

    public String getP30() {
        return this.P30;
    }

    public void setP30(String P30) {
        this.P30 = P30;
    }

    public String getP31() {
        return this.P31;
    }

    public void setP31(String P31) {
        this.P31 = P31;
    }

    public String getP32() {
        return this.P32;
    }

    public void setP32(String P32) {
        this.P32 = P32;
    }

    public String getP33() {
        return this.P33;
    }

    public void setP33(String P33) {
        this.P33 = P33;
    }

    public String getP34() {
        return this.P34;
    }

    public void setP34(String P34) {
        this.P34 = P34;
    }

    public String getP35() {
        return this.P35;
    }

    public void setP35(String P35) {
        this.P35 = P35;
    }

    public String getP36() {
        return this.P36;
    }

    public void setP36(String P36) {
        this.P36 = P36;
    }

    public String getP37() {
        return this.P37;
    }

    public void setP37(String P37) {
        this.P37 = P37;
    }

    public String getP38() {
        return this.P38;
    }

    public void setP38(String P38) {
        this.P38 = P38;
    }

    public String getP39() {
        return this.P39;
    }

    public void setP39(String P39) {
        this.P39 = P39;
    }

    public String getP40() {
        return this.P40;
    }

    public void setP40(String P40) {
        this.P40 = P40;
    }

    public String getP41() {
        return this.P41;
    }

    public void setP41(String P41) {
        this.P41 = P41;
    }

    public String getP42() {
        return this.P42;
    }

    public void setP42(String P42) {
        this.P42 = P42;
    }

    public String getP43() {
        return this.P43;
    }

    public void setP43(String P43) {
        this.P43 = P43;
    }

    public String getP44() {
        return this.P44;
    }

    public void setP44(String P44) {
        this.P44 = P44;
    }

    public String getP45() {
        return this.P45;
    }

    public void setP45(String P45) {
        this.P45 = P45;
    }

    public String getP46() {
        return this.P46;
    }

    public void setP46(String P46) {
        this.P46 = P46;
    }

    public String getP47() {
        return this.P47;
    }

    public void setP47(String P47) {
        this.P47 = P47;
    }

    public String getP48() {
        return this.P48;
    }

    public void setP48(String P48) {
        this.P48 = P48;
    }

    public String getP49() {
        return this.P49;
    }

    public void setP49(String P49) {
        this.P49 = P49;
    }

    public String getP50() {
        return this.P50;
    }

    public void setP50(String P50) {
        this.P50 = P50;
    }

    public String getP51() {
        return this.P51;
    }

    public void setP51(String P51) {
        this.P51 = P51;
    }

    public String getP52() {
        return this.P52;
    }

    public void setP52(String P52) {
        this.P52 = P52;
    }

    public String getP53() {
        return this.P53;
    }

    public void setP53(String P53) {
        this.P53 = P53;
    }

    public String getP54() {
        return this.P54;
    }

    public void setP54(String P54) {
        this.P54 = P54;
    }

    public String getP55() {
        return this.P55;
    }

    public void setP55(String P55) {
        this.P55 = P55;
    }

    public String getP56() {
        return this.P56;
    }

    public void setP56(String P56) {
        this.P56 = P56;
    }

    public String getP57() {
        return this.P57;
    }

    public void setP57(String P57) {
        this.P57 = P57;
    }

    public String getP58() {
        return this.P58;
    }

    public void setP58(String P58) {
        this.P58 = P58;
    }

    public String getP59() {
        return this.P59;
    }

    public void setP59(String P59) {
        this.P59 = P59;
    }

    public String getP60() {
        return this.P60;
    }

    public void setP60(String P60) {
        this.P60 = P60;
    }

    public String getP61() {
        return this.P61;
    }

    public void setP61(String P61) {
        this.P61 = P61;
    }

    public String getP62() {
        return this.P62;
    }

    public void setP62(String P62) {
        this.P62 = P62;
    }

    public String getP63() {
        return this.P63;
    }

    public void setP63(String P63) {
        this.P63 = P63;
    }

    public String getP64() {
        return this.P64;
    }

    public void setP64(String P64) {
        this.P64 = P64;
    }

    public String getP65() {
        return this.P65;
    }

    public void setP65(String P65) {
        this.P65 = P65;
    }

    public String getP66() {
        return this.P66;
    }

    public void setP66(String P66) {
        this.P66 = P66;
    }

    public String getP67() {
        return this.P67;
    }

    public void setP67(String P67) {
        this.P67 = P67;
    }

    public String getP68() {
        return this.P68;
    }

    public void setP68(String P68) {
        this.P68 = P68;
    }

    public String getP69() {
        return this.P69;
    }

    public void setP69(String P69) {
        this.P69 = P69;
    }

    public String getP70() {
        return this.P70;
    }

    public void setP70(String P70) {
        this.P70 = P70;
    }

    public String getP71() {
        return this.P71;
    }

    public void setP71(String P71) {
        this.P71 = P71;
    }

    public String getP72() {
        return this.P72;
    }

    public void setP72(String P72) {
        this.P72 = P72;
    }

    public String getP73() {
        return this.P73;
    }

    public void setP73(String P73) {
        this.P73 = P73;
    }

    public String getP74() {
        return this.P74;
    }

    public void setP74(String P74) {
        this.P74 = P74;
    }

    public String getP75() {
        return this.P75;
    }

    public void setP75(String P75) {
        this.P75 = P75;
    }

    public String getP76() {
        return this.P76;
    }

    public void setP76(String P76) {
        this.P76 = P76;
    }

    public String getP77() {
        return this.P77;
    }

    public void setP77(String P77) {
        this.P77 = P77;
    }

    public String getP78() {
        return this.P78;
    }

    public void setP78(String P78) {
        this.P78 = P78;
    }

    public String getP79() {
        return this.P79;
    }

    public void setP79(String P79) {
        this.P79 = P79;
    }

    public String getP80() {
        return this.P80;
    }

    public void setP80(String P80) {
        this.P80 = P80;
    }

    public String getP81() {
        return this.P81;
    }

    public void setP81(String P81) {
        this.P81 = P81;
    }

    public String getP82() {
        return this.P82;
    }

    public void setP82(String P82) {
        this.P82 = P82;
    }

    public String getP83() {
        return this.P83;
    }

    public void setP83(String P83) {
        this.P83 = P83;
    }

    public String getP84() {
        return this.P84;
    }

    public void setP84(String P84) {
        this.P84 = P84;
    }

    public String getP85() {
        return this.P85;
    }

    public void setP85(String P85) {
        this.P85 = P85;
    }

    public String getP86() {
        return this.P86;
    }

    public void setP86(String P86) {
        this.P86 = P86;
    }

    public String getP87() {
        return this.P87;
    }

    public void setP87(String P87) {
        this.P87 = P87;
    }

    public String getP88() {
        return this.P88;
    }

    public void setP88(String P88) {
        this.P88 = P88;
    }

    public String getP89() {
        return this.P89;
    }

    public void setP89(String P89) {
        this.P89 = P89;
    }

    public String getP90() {
        return this.P90;
    }

    public void setP90(String P90) {
        this.P90 = P90;
    }

    public String getP91() {
        return this.P91;
    }

    public void setP91(String P91) {
        this.P91 = P91;
    }

    public String getP92() {
        return this.P92;
    }

    public void setP92(String P92) {
        this.P92 = P92;
    }

    public String getP93() {
        return this.P93;
    }

    public void setP93(String P93) {
        this.P93 = P93;
    }

    public String getP94() {
        return this.P94;
    }

    public void setP94(String P94) {
        this.P94 = P94;
    }

    public String getP95() {
        return this.P95;
    }

    public void setP95(String P95) {
        this.P95 = P95;
    }

    public String getP96() {
        return this.P96;
    }

    public void setP96(String P96) {
        this.P96 = P96;
    }

    public String getP97() {
        return this.P97;
    }

    public void setP97(String P97) {
        this.P97 = P97;
    }

    public String getP98() {
        return this.P98;
    }

    public void setP98(String P98) {
        this.P98 = P98;
    }

    public String getP99() {
        return this.P99;
    }

    public void setP99(String P99) {
        this.P99 = P99;
    }

    public String getP100() {
        return this.P100;
    }

    public void setP100(String P100) {
        this.P100 = P100;
    }

    public Timestamp getCREATEDT() {
        return this.CREATEDT;
    }

    public void setCREATEDT(Timestamp CREATEDT) {
        this.CREATEDT = CREATEDT;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBJSB_INS_PROD_TARGETVO) ) return false;
        TBJSB_INS_PROD_TARGETVO castOther = (TBJSB_INS_PROD_TARGETVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
