package com.systex.jbranch.fubon.commons.esb.cons;

/**
 * Created by SebastianWu on 2016/8/16.
 *
 * for SOT 下單電文使用
 * 記錄各電文編號
 */
public class EsbSotCons {
	/**
     * 查詢臺幣活存(支票存款)交易明細電文
     */
    public final static String EB172656_DATA = "EB172656";
	/**
     * 查詢臺幣活存(支票存款)交易明細電文
     */
    public final static String EB202650_DATA = "EB202650";
	/**
     * 查詢一本萬利資料電文
     */
    public final static String EB202674_DATA = "EB202674";
    /**
     * 查詢客戶註記資料電文
     */
    public final static String FC032675_DATA = "FC032675";
    
    /**
     * 查詢客戶註記資料電文
     */
    public final static String FP032675_DATA = "FP032675";
    
    /**
     * 查詢客戶註記資料電文
     */
    public final static String VR032675_DATA = "VR032675";
    
    /**
     * 查詢網銀取得戶名電文
     */
    public final static String FC032659_DATA = "FC032659";
    
    /**
     * 查詢客戶學歷、職業、婚姻、子女數資料電文
     */
    public final static String FC032151_DATA = "FC032151";
    
    /**
     * 查詢客戶學歷、職業、婚姻、子女數資料電文
     */
    public final static String CLM032151_DATA = "CLM032151";
    
    /**
     * 查詢客戶學歷、職業、婚姻、子女數資料電文
     */
    public final static String FP032151_DATA = "FP032151";

    /**
     * 查詢DBU客戶停損停利設定電文
     */
    public final static String CUST_PL_DATA_DBU = "NFEE011";

    /**
     * 查詢OBU客戶停損停利設定電文
     */
    public final static String CUST_PL_DATA_OBU = "AFEE011";

    /**
     * 查詢客戶W-8BEN
     */
    public final static String CUST_W8_BEAN = "NKNE01";
    
    /**
     * 查詢FATCA客戶註記檢核
     */
    public final static String CUST_FATCA = "FC032275";

/**
     * 查詢客戶W-8BEN和FATCA客戶註記檢核
     */
    public final static String CUST_W8_BEAN_FATCA = "FC032275";
    
    /**
     * 查詢客戶W-8BEN和FATCA客戶註記檢核
     */
    public final static String NEW_CUST_W8_BEAN_FATCA = "WMS032275";

    /**
     * 查詢客戶期間議價效期 - 基金電文
     */
    public final static String CUST_FEE_PRD_DUE_DATE_FUND = "NFBRVCRM3";

    /**
     * 查詢客戶期間議價效期 - ETF電文
     */
    public final static String CUST_FEE_PRD_DUE_DATE_ETF = "NRBRVC3";

    /**
     * 現行基金申購相關帳號處理電文
     */
    public final static String CUST_ACCT = "FC032671";
    
    /**
     * 現行基金申購相關帳號處理電文
     */
    public final static String NEW_CUST_ACCT = "FP032671";

    
    /**
     * 基金首購電文
     */
    public final static String FIRST_TRADE_FUND = "NFEE012";

    /**
     * ETF首購電文
     */
    public final static String FIRST_TRADE_ETF = "NKNE01";
    
    /**
     * 金錢信託ETF首購電文
     */
    public final static String FIRST_TRADE_ETF_M = "NKNET1";

    /**
     * 理專鍵機基金申購交易
     */
    public final static String ESBN_F_PURCHASE = "NFBRVA9";

    /**
     * 理專鍵機基金贖回轉申購交易和查詢轉申購表定手續費率電文
     */
    public final static String ESBN_F_REDEEM = "NFBRVB9";

    /**
     * 理專鍵機基金轉換交易
     */
    public final static String ESBN_TRANSFER = "NFBRVC9";

    /**
     * 理專鍵機基金事件變更交易
     */
    public final static String ESBN_F_CHANGE = "NFBRVD9";

    /**
     * 理專鍵機基金客戶庫存查詢
     */
    public final static String ESBN_F_STORAGE = "NFBRV3B1";

    /**
     * 查詢客戶資料
     */
    public final static String TP032675_DATA = "TP032675";

    /**
     * 海外ETF/股票申購/贖回檢核、確認
     */
    public final static String ETF_ESB = "NRBRVA9";
    
    /**
     * 金錢信託海外ETF/股票申購/贖回檢核、確認
     */
    public final static String ETF_ESB_M = "NRBRVT9";

    /**
     * 海外ETF/股票事先單次議價手續費率資料查詢
     */
    public final static String ETF_SINGLE_FEE = "NRBRVA3";

    /**
     * 海外ETF/股票表定手續費率資料查詢
     */
    public final static String ETF_DEFAULT_FEE = "NRBRVA1";

    /**
     * 海外ETF/股票產品庫存資料查詢
     */
    public final static String ETF_ASSETS = "NFVIPA";
    
    /**
     * 金錢信託_海外ETF/股票產品庫存資料查詢
     */
    public final static String ETF_ASSETS_MON = "NMVP9A";
    
    /**
     * 海外債長效單資料查詢DBU
     */
    public final static String BOND_GTC_DATA_DBU = "NJBRVC1";
    
    /**
     * 海外債長效單資料明細查詢DBU
     */
    public final static String BOND_GTC_DATA_DETAIL_DBU = "NJBRVC2";

    /**
     * 海外債長效單資料查詢OBU
     */
    public final static String BOND_GTC_DATA_OBU = "AJBRVC1";
    
    /**
     * 海外債長效單資料明細查詢OBU
     */
    public final static String BOND_GTC_DATA_DETAIL_OBU = "AJBRVC2";
    
    /**
     * 海外ETF/股票產品庫存資料查詢
     */
    public final static String ETF_DUE_DATE = "NR096N";

    /**
     * 海外債/SN申購檢核、確認
     */
    public final static String ETF_SN_PURCHASE = "NJBRVA9";
    
    /**
     * 海外債長效單申購檢核、確認
     */
    public final static String BOND_GTC_PURCHASE = "NJBRVC9";

    /**
     * 海外債/SN贖回檢核、確認
     */
    public final static String ETF_SN_REDEEM = "NJBRVB9";

    /**
     * 海外債長效單贖回檢核、確認
     */
    public final static String BOND_GTC_REDEEM = "NJBRVD9";
    
    /**
     * 海外債/SN產品庫存資料查詢-DBU
     */
    public final static String ETF_SN_ASSETS_DBU = "NJBRVB1";
    
    /**
     * 海外債/SN產品庫存資料查詢-OBU
     */
    public final static String ETF_SN_ASSETS_OBU = "AJBRVB1";

    /**
     * 海外債/SN產品參考報價資料查詢
     */
    public final static String ETF_SN_QUG = "NJBRVA1";

    /**
     * 海外債/SN產品首購資料查詢
     */
    public final static String ETF_SN_FIRST_TRADE = "NJBRVA3";

    /**
     * 海外債表定手續費率、最優手續費率、應付前手息率資料查詢
     */
    public final static String FEE_RATE_DATA = "NJBRVA2";
    
    /**
     * 海外ETF/股票期間議價申請
     */
    public final static String ETF_PERIOD_BARGAIN = "NRBRVC1";
    
    /**
     * 海外ETF/股票單次議價申請
     */
    public final static String ETF_SINGLE_BARGAIN = "NRBRVC2";
    
    /**
     * 海外ETF/股票期間議價查詢
     */
    public final static String ETF_PERIOD_FEE_RATE = "NRBRVC3";
    
    /**
     * 海外ETF/股票單次議價查詢
     */
    public final static String ETF_SINGLE_FEE_RATE = "NRBRVC4";
    
    /**
     * 海外ETF/股票最優手續費率資料查詢
     */
    public final static String ETF_BEST_FEE = "NRBRVA2";

    /**
     * 海外ETF/股票收盤價資料取得
     */
    public final static String ETF_CLOSING_PRICE = "NR098N";

    /**
     * FCI申購檢核、確認
     */
    public final static String FCI_PURCHASE = "SDACTQ20";
    
    /**
     * SI申購檢核、確認
     */
    public final static String SI_PURCHASE = "SDACTQ3";

    /**
     * SI贖回檢核、確認
     */
    public final static String SI_REDEEM = "SDACTQ5";
    
    /**
     * SI產品庫存資料查詢
     */
    public final static String SI_ASSETS_DATA = "SDACTQ4";

	/**
	 * 特金交易-基金申購檢核
	 */
	public final static String NF_VERIFY_PURCHASE = "NFBRN1";
	
	/**
	 * 金錢信託-基金申購檢核
	 */
	public final static String XF_VERIFY_PURCHASE = "NFBRX1";
	
	/**
	 * 特金交易-動態鎖利基金申購檢核DBU
	 */
	public final static String NF_VERIFY_PURCHASE_DYNA_DBU = "NFBRNF";
	
	/**
	 * 特金交易-動態鎖利基金申購檢核OBU
	 */
	public final static String NF_VERIFY_PURCHASE_DYNA_OBU = "AFBRNF";
	
	/**
	 * 特金交易-動態鎖利母基金加碼檢核DBU
	 */
	public final static String NF_VERIFY_RAISE_AMT_DYNA_DBU = "NFBRNG";
	
	/**
	 * 特金交易-動態鎖利母基金加碼檢核OBU
	 */
	public final static String NF_VERIFY_RAISE_AMT_DYNA_OBU = "AFBRNG";
	
	/**
	 * 特金交易-動態鎖利贖回檢核DBU
	 */
	public final static String NF_VERIFY_REDEEM_DYNA_DBU = "NFBRNH";
	
	/**
	 * 特金交易-動態鎖利贖回檢核OBU
	 */
	public final static String NF_VERIFY_REDEEM_DYNA_OBU = "AFBRNH";
	
	/**
	 * 特金交易-動態鎖利事件變更檢核DBU
	 */
	public final static String NF_VERIFY_CHANGE_DYNA_DBU = "NFBRNI";
	
	/**
	 * 特金交易-動態鎖利事件變更檢核OBU
	 */
	public final static String NF_VERIFY_CHANGE_DYNA_OBU = "AFBRNI";
	
	/**
	 * 基金轉換檢核
	 */
	public final static String NF_VERIFY_ESB = "NFBRN2";

	/**
	 * 基金轉換檢核
	 */
	public final static String NF_CONFIRM_ESB = "NFBRN3";

	/**
	 * 基金變更檢核
	 */
	public final static String NF_VERIFY_ESB_NF = "NFBRN5";

	/**
	 * 特金交易-基金庫存資料查詢-DBU
	 */
	public final static String CUST_ASSET_NF_DBU = "NFBRN9";
	
	/**
	 * 金錢信託-基金庫存資料查詢-DBU
	 */
	public final static String CUST_ASSET_XF_DBU = "NFBRX9";
	
	/**
	 * 基金庫存資料查詢-OBU
	 */
	public final static String CUST_ASSET_NF_OBU = "AFBRN9";

	/**
	 * 特金交易-基金表定手續費以及最優手續費查詢 DBU
	 */
	public final static String NF_PROCESSING_FEE = "NFBRN7";

    /**
     * 特金交易-基金表定手續費以及最優手續費查詢 OBU
     */
    public final static String NF_OBU_PROCESSING_FEE = "AFBRN7";

	/**
	 * 金錢信託-基金表定手續費以及最優手續費查詢
	 */
	public final static String XF_PROCESSING_FEE = "NFBRX7";
	
	/**
	 * 基金期間議價查詢
	 */
	public final static String NF_DBU_BARGAIN_INQUIRE = "NFEE002";

    /**
     * 基金期間議價查詢
     */
    public final static String NF_OBU_BARGAIN_INQUIRE = "AFEE002";

	/**
	 * 基金單次議價查詢
	 */
	public final static String NF_SINGLE_BARGAIN_INQUIRE = "VN085N";
	/**
	 * 基金DBU單次議價申請
	 */
	public final static String NF_DBU_SINGLE_BARGAIN_INQUIRE = "NFBRN6";
    /**
     * 基金 OBU 單次議價申請
     */
    public final static String NF_OBU_SINGLE_BARGAIN_INQUIRE = "AFBRN6";
	/**
	 * 基金DBU區間議價
	 */
	public final static String NF_DBU_INTERVAL_BARGAIN_INQUIRE = "NFBRN8";
	/**
	 * 基金OBU區間議價
	 */
	public final static String NF_OBU_INTERVAL_BARGAIN_INQUIRE = "AFBRN8";

    /**
     * 基金贖回檢核
     */
    public final static String NF_VERIFY_REDEEM = "NFBRN3";
    
    
    /**
     * 網路快速下單 - 基金申購檢核
     */
    public final static String NF_BANK_VERIFY_PURCHASE = "VN067N";
    
    /**
     * 網路快速下單 – 發送電文至網銀讓網銀行事曆表單上可以出現快速申購之訊息
     */
    public final static String NF_BANK_SEND_WEBPURCHASENF = "EBPMN";
    
    /**
     * AI BANK - 發送電文至AI BANK, 行事曆表單上可以出現快速申購之訊息
     */
    public final static String AI_BANK_SEND_PURCHASE = "EBPMN2";

    /**
     * 查詢客戶Q值
     */
    public final static String CUST_Q_VALUE = "FC81";

    /**
     * 查詢客戶利害關係人註記
     */
    public final static String CUST_STACK_HOLDER = "FP052650";
    
    /**
     * 新查詢客戶利害關係人註記
     */
    public final static String NEW_CUST_STACK_HOLDER = "NB052650";
    
    /**
     * 基金單次定期(不)定額議價查詢
     */
    public final static String NF_ESB = "NFEE086";
    
    /**
     * 基金DBU單次定期(不)定額議價申請
     */
    public final static String NF_DBU = "NFBRN4";

    /**
     * 海外ETF&股票商品類型查詢
     */
    public final static String PRD_ETF_STOCK = "NR097N";
    
    
    /**
     * DCI資產查詢
     */
    public final static String DCI_ASSETS = "BKDCD003";
    
    /**
     * SI資產總覽查詢
     */
    public final static String SI_ASSETS = "SDPRC09A";
    
    /**
     * 指單資產總覽查詢
     */
    public final static String TRUST_ASSETS = "NMVIPA";
    
     /**
     * 貸款電文
     */
    public final static String EB372602_DATA = "EB372602";
    
    /**
     * 客戶基本資料
     */
    public final static String FC032153_DATA = "FC032153";
    
    /**
     * 客戶基本資料
     */
    public final static String VIP032153_DATA = "VIP032153";
    
    /**
     * 優惠次數型手續費
     */
    public final static String NFEE001_DATA = "NFEE001";
    
    /**
     * 解說人員需持有信託證照
     */
    public final static String NJCHKLC2_DATA = "NJCHKLC2";
     
    /**
     * 歷史交易明細
     */
    public final static String NRBRQ01_DATA = "NRBRQ01";
    
    /**
     * 海外ETF/股票可用餘額查詢
     */
    public final static String ETF_BALANCE_QUERY="NR070N";
    
    /**
     * 股票委託明細
     */
    public final static String NR080N_DATA="NR080N";

    /**
     * 即時成交交易資訊
     */
    public final static String NR074N_DATA="NR074N";
    

    /**
     * 取交易序號
     */
    public final static String FC032154_DATA="FC032154";
    
    /**
     * 結構型商品推介同意註記
     */
    public final static String SDACTQ8="SDACTQ8";
    
    /**
     * 庫存電文回傳:質借圈存註記	
     * MK01 法院圈存; MK02 警示戶圈存; MK03 質權設定圈存  => 區分 他行,個金,法金; MK99 其他
     */
    public final static String CMKTYPE_MK03 = "MK03";
    
    /**
     * 解除學歷待確認註記
     */
    public final static String EB032282 = "EB032282";
    
    /**
	 * 金錢信託首購判斷
	 */
	public final static String NMVP7A = "NMVP7A";

	/**
	 * 客戶統編查詢契約
	 */
	public final static String NMVP6A = "NMVP6A";
	
	/**
	 * 債券手續費議價-金錢信託
	 */
	public final static String NJBRVX1 = "NJBRVX1";
	
	/**
	 * 債券申購交易-金錢信託
	 */
	public final static String NJBRVX2 = "NJBRVX2";
	
	/**
	 * 債券贖回庫存查詢-金錢信託
	 */
	public final static String NJBRVX3 = "NJBRVX3";
	
	/**
	 * 基金申購交易-金錢信託
	 */
	public final static String NFBRX1 = "NFBRX1";
	
	/**
	 * 基金手續費議價-金錢信託
	 */
	public final static String NFBRX7 = "NFBRX7";
	
	/**
	 * 基金贖回庫存查詢-金錢信託
	 */
	public final static String NFBRX9 = "NFBRX9";
	
	/**
	 * OBU單筆議價編號查詢
	 */
	public final static String OBU_NF_SINGLE_BARGAIN_INQUIRE = "AFEE003";
	/**
	 * OBU特金交易-基金申購檢核
	 */
	public final static String OBU_NF_VERIFY_PURCHASE = "AFBRN1";
	/**
	 * OBU基金轉換檢核
	 */
	public final static String OBU_NF_VERIFY_ESB = "AFBRN2";
	/**
	 * OBU基金轉換檢核
	 */
	public final static String OBU_NF_CONFIRM_ESB = "AFBRN3";
    /**
     * OBU基金贖回檢核
     */
    public final static String OBU_NF_VERIFY_REDEEM = "AFBRN3";
	/**
	 * OBU基金變更檢核
	 */
	public final static String OBU_NF_VERIFY_ESB_NF = "AFBRN5";
	/**
	 * 信託帳號查詢 查詢168與368的帳號
	 */
	public final static String TRUST_ACCT_SEARCH = "NFEI003";
	/**
	 * 海外債成交結果查詢
	 */
	public final static String BOND_RESULT_SEARCH = "NJBRVC3";
	/**
	 * OBU海外債成交結果查詢
	 */
	public final static String OBU_BOND_RESULT_SEARCH = "AJBRVC3";

	/**
     * 國外私募基金贖回修正單位數交易(DBU)
     */
    public final static String NFBRNE = "NFBRNE";
    
    /**
     * 國外私募基金贖回修正單位數交易(OBU)
     */
    public final static String AFBRNE = "AFBRNE";
    
    /**
     * 取得高資產越級適配資訊
     */
    public final static String WMSHAIA = "WMSHAIA";
    
    /**
     * DBU, 取得基金總資產
     */
    public final static String VN084N = "VN084N";
    
    /**
     * OBU, 取得 基金 總資產
     */
    public final static String VN084N1 = "VN084N1";
    
    /**
     * 取得 海外ETF/海外股票 總資產
     */
    public final static String UK084N = "UK084N";
    
    /**
     * DBU, 取得 海外債 && SN 總資產
     */
    public final static String NJW084 = "NJW084";
    
    /**
     * OBU, 取得 海外債 && SN 總資產
     */
    public final static String AJW084 = "AJW084";
    
    /**
     * 取得 SI 總資產
     */
    public final static String SPWEBQ1 = "SPWEBQ1";
    
    /**
     * 債券預約單/長效單營業日註記
     */
    public final static String NJWEEA70 = "NJWEEA70";
    
    /**
     * 理專鍵機債券手續費議價OBU
     */
    public final static String AJBRVA2 = "AJBRVA2";
    
    /**
     * 理專鍵機債券首購查詢OBU
     */
    public final static String AJBRVA3 = "AJBRVA3";
    /**
     * 理專鍵機債券申購交易OBU
     */
    public final static String AJBRVA9 = "AJBRVA9";
    /**
     * 理專鍵機債券贖回交易OBU
     */
    public final static String AJBRVB9 = "AJBRVB9";
    /**
     * 理專鍵機債券長效單申購交易OBU
     */
    public final static String AJBRVC9 = "AJBRVC9";
    /**
     * 理專鍵機債券長效單贖回交易OBU
     */
    public final static String AJBRVD9 = "AJBRVD9";
    
}
