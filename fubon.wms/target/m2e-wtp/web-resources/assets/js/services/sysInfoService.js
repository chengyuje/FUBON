/**================================================================================================
@program: sysinfoService.js
@description: public & system info with singleton.
@version: 1.0.20170828
=================================================================================================*/
eSoafApp.factory('sysInfoService', ['$rootScope', '$q',
	function($rootScope, $q) {

		/**============================================================================
		 *
		 * 				Platform storage device 平台儲存裝置 ($rootScope)
		 *
		 * ============================================================================*/
		/*-----------------------------------------------------------------------------
		 * @Description: 前端資料管理伺服器
		 *               font-end manager server
		 *-----------------------------------------------------------------------------*/
		$rootScope.mobile = sessionStorage.CMFPG000_MOBILE_LOGIN == 'mobile';
		$rootScope.navigator = (navigator.userAgent.match("Safari")!==null&&navigator.userAgent.match("Safari")) ? true : false;
		$rootScope.server = {id:[], data:[]};
		$rootScope.system = {
					 platform: {
								 type:[],
								 code:[],
						         info:[]},
				    component: {
					   			 type:[],
					   			 code:[],
					             info:[]}
					           };
		$rootScope.mode = {
				// pressure test mode
				pressure: {
						$record:false, // record
					      $test:false  // test
				}
		};
		//checker
		var $chk = {
			$obj: function(owner, match) {
				var flag = false, deferred = $q.defer();
				for(var i=0;i<owner.length;i+=1){
					if(owner[i] === match){
						flag = true;
						deferred.resolve(i);
						break;
					}
				}
				if(!flag)deferred.reject();
				return deferred.promise;
			}
		};

		/*-----------------------------------------------------------------------------
		 * @Description: 當前位置
		 *               host position check
		 *  @Parameters: [visit] hidden dialog, [total] all dialog
		 *-----------------------------------------------------------------------------*/
		var locatChecker = $q.defer(), locatInit = function(){
			if($rootScope.system.platform.type.indexOf('location')===-1){
				$rootScope.system.platform.type.push('location');
				$rootScope.system.platform.code.push(true);
				$rootScope.system.platform.info.push(document.location.hostname);
				locatChecker.resolve();
			}else{
				$rootScope.system.platform.info[$rootScope.system.platform.type.indexOf('location')]=document.location.hostname;
				locatChecker.resolve();
			}
			return locatChecker.promise;
		};
		locatInit().then(function(){
			setLocation($rootScope);
		});

		/*-----------------------------------------------------------------------------
		 * @Description: 彈跳視窗計數器
		 *               Dialog Counter
		 *  @Parameters: [visit] hidden dialog, [total] all dialog
		 *-----------------------------------------------------------------------------*/
		$rootScope.$counter = {'visit':[],'total':[]};

		/*-----------------------------------------------------------------------------
		 * @Description: 登出機制-判斷物件
		 *               Logout Checker
		 *  @Parameters:
		 *-----------------------------------------------------------------------------*/
		$chk.$obj($rootScope.system.platform.type,'logout').then(
			function(i){},
			function(){
				$rootScope.system.platform.type.push('logout');
				$rootScope.system.platform.code.push(0);
				$rootScope.system.platform.info.push({status:false, classic:false, URL:{$test:unfaker("%20L%23%23%23%23o%3F%3F%3F%3Fg%23%23i%20%20n%23.%3F%3F%3F%3Fh%3F%3F%3Ft%23%23m%20l"), $official:'', $official_test:'' }});
		});

		/**============================================================================
		 *
		 * 			Platform storage device and method 平台儲存裝置與方法 (singleton)
		 *
		 * ============================================================================*/
		if ( arguments.callee._singletonInstance ) {
	    	return arguments.callee._singletonInstance;
		}

		var obj = {};

		//每頁資訊
		obj.$paging = {};
		//single page rows(預備移除)
		obj.$paging.rowCountLimit = 10;

		obj.$currentModule = "WMS";

		obj.UserInfo = {};
		obj.RoleName = "";
		obj.OrganizationID = "IB";

		obj.TxnInfo = {};		//交易代號 TxnCode、TxnName
		obj.MenuInfo = {};		//選單
		obj.SysParam = {};		//系統變數
		obj.Marquee = [];		//跑馬燈

		obj.Exclude = {			//exclude
			"controller":["CMFPG000Controller","CRM124Controller","DialogAlertController"],
			     "title":["{{data.title}}","待辦事項提醒"],
			     	"ID":["MARQUEEController"],
			     	"code":["COMBOBOX","XMLInfo"],
			     	"IP":[unfaker("%211%3F%3F%3F%3F%3F%3F27%3F%3F%3F%3F%3F.%23%23%23%23%23%23%23%23%230%21%21.%20%20%20%20%20%20%20%20%200%23%23%23.%23%23%23%23%231"),
			     	      unfaker("%20%20%20%20%20%20%20%20l%21%21%21%21%21%21%21%21oc%21%21%21%21%21%21a%3F%3F%3F%3F%3F%3F%3F%3Fl%21%21%21%21h%20%20%20o%20%20%20s%3F%3F%3F%3F%3F%3Ft")]
		};

		obj.Browser = detectBrowser(); 	//瀏覽器

		obj.parseMenu = function(oXml) {
			parseMenu(oXml);
		};

		obj.parseMarquee = function(sJSON) {
			parseMarquee(sJSON);
		};

		/** get set **/
		/*=============================================================================
		 * @Description: AP Server 名稱參數
		 *               AP Server name
		 *  @Parameters: true|false typeOf String
		 *  @LastUpdate: 2017/08/28 ArthurKO
		 *=============================================================================*/
		obj.getApServerName = function() {
			return sessionStorage.apServerName;
		};
		obj.setApServerName = function(value) {
			sessionStorage.apServerName = value;
		};

		/*=============================================================================
		 * @Description: 瀏覽器類型判斷參數
		 *               Navigator
		 *  @Parameters: true|false typeOf String
		 *  @LastUpdate: 2016/05/26 ArthurKO
		 *=============================================================================*/
		obj.getNavigator = function() {
			return sessionStorage.Navigator;
		};
		obj.setNavigator = function(value) {
			sessionStorage.Navigator = value;
		};

		/*=============================================================================
		 * @Description: 系統-單頁顯示筆數參數
		 *               Single page shows the number of parameters
		 *  @Parameters: 10-50-100 typeOf Number
		 *  @LastUpdate: 2017/07/06 ArthurKO
		 *=============================================================================*/
		obj.getSinglePageRows = function() {
			return sessionStorage.SinglePageRows ? JSON.parse(sessionStorage.SinglePageRows) : [{"DATA":"DEFAULT","LABEL":"50"}];
		};
		obj.setSinglePageRows = function(value) {
			sessionStorage.SinglePageRows = value ? JSON.stringify(value) : [{"DATA":"DEFAULT","LABEL":"50"}];
		};

		/*=============================================================================
		 * @Description: 跑馬燈資料更新間距參數
		 *               Marquee info update parameters
		 *  @Parameters: 更新間距時間(單位:分鐘)(COMMON.MARQUEE)
		 *  @LastUpdate: 2016/08/04 ArthurKO
		 *=============================================================================*/
		obj.getMarqueeParameter = function() {
			return sessionStorage.MarqueeParameter ? JSON.parse(sessionStorage.MarqueeParameter) : [{"DATA":"PERIOD","LABEL":"15"}];
		};
		obj.setMarqueeParameter = function(value) {
			sessionStorage.MarqueeParameter = value ? JSON.stringify(value) : [{"DATA":"PERIOD","LABEL":"15"}];
		};

		/*=============================================================================
		 * @Description: 上傳檔案相關參數
		 *               upload parameters
		 *  @Parameters: 檔案大小限制(FILE_SIZE.LIMIT)
		 *  @LastUpdate: 2016/07/26 ArthurKO
		 *=============================================================================*/
		obj.getFileParameter = function() {
			return sessionStorage.FileParameter ? JSON.parse(sessionStorage.FileParameter) : [{"DATA":"DEFAULT","LABEL":"10"}];
		};
		obj.setFileParameter = function(value) {
			sessionStorage.FileParameter = value ? JSON.stringify(value) : [{"DATA":"DEFAULT","LABEL":"10"}];
		};

		/*=============================================================================
		 * @Description: 伺服器相關參數
		 *               server timeout limit parameters
		 *  @Parameters: 伺服器回應時間限制(TIMEOUT.LIMIT)
		 *  @LastUpdate: 2017/01/04 ArthurKO
		 *=============================================================================*/
		obj.getServerParameter = function() {
			return sessionStorage.ServerParameter ? JSON.parse(sessionStorage.ServerParameter) : [{"DATA":"DEFAULT","LABEL":"2"}];
		};
		obj.setServerParameter = function(value) {
			sessionStorage.ServerParameter = value ? JSON.stringify(value) : [{"DATA":"DEFAULT","LABEL":"2"}];
		};

		/*=============================================================================
		 * @Description: 彈跳視窗計數相關參數
		 *               Dialog Counts by XML
		 *  @Parameters: JSON
		 *  @LastUpdate: 2016/05/26 ArthurKO
		 *=============================================================================*/
		obj.getDialogCounts = function() {
			return sessionStorage.DialogCounts ? JSON.parse(sessionStorage.DialogCounts) : undefined;
		};
		obj.setDialogCounts = function(value) {
			sessionStorage.DialogCounts = value ? JSON.stringify(value) : undefined;
		};

		obj.getUser = function() {
			return JSON.parse(sessionStorage.User);
		};
		obj.setUser = function(user) {
			sessionStorage.User = JSON.stringify(user);
		};

		obj.getUserID = function() {
			return sessionStorage.UserID;
		};
		obj.setUserID = function(UserID) {
			sessionStorage.UserID = UserID;
		};
		obj.getUserName = function() {
			return sessionStorage.UserName;
		};
		obj.setUserName = function(UserName) {
			sessionStorage.UserName = UserName;
		};
		obj.getUserList = function() {
			return JSON.parse(sessionStorage.UserList);
		};
		obj.setUserList = function(list) {
			sessionStorage.UserList = JSON.stringify(list);
		};

		obj.getApplicationID = function() {
			return sessionStorage.ApplicationID;
		};
		obj.setApplicationID = function(ApplicationID) {
			sessionStorage.ApplicationID = ApplicationID;
		};

		obj.getAoCode = function() {
			return JSON.parse(sessionStorage.AoCode);
		};
		obj.setAoCode = function(AoCode) {
			sessionStorage.AoCode = JSON.stringify(AoCode);
		};

		obj.getBranchID = function() {
			return sessionStorage.BranchID;
		};
		obj.setBranchID = function(BranchID) {
			sessionStorage.BranchID = BranchID;
		};

		obj.getAreaID = function() {
			return sessionStorage.AreaID;
		};
		obj.setAreaID = function(AreaID) {
			sessionStorage.AreaID = AreaID;
		};

		obj.getRegionID = function() {
			return sessionStorage.RegionID;
		};
		obj.setRegionID = function(RegionID) {
			sessionStorage.RegionID = RegionID;
		};

		obj.getDefaultBranch = function() {
			return sessionStorage.DefaultBranch;
		};
		obj.setDefaultBranch = function(DefaultBranch) {
			sessionStorage.DefaultBranch = DefaultBranch;
		};

		obj.getBranchName = function() {
			return sessionStorage.BranchName;
		};
		obj.setBranchName = function(BranchName) {
			sessionStorage.BranchName = BranchName;
		};

		obj.getWsID = function() {
			return sessionStorage.WsID;
		};
		obj.setWsID = function(WsID) {
			sessionStorage.WsID = WsID;
		};

		obj.getRoleID = function() {
			return sessionStorage.RoleID;
		};
		obj.setRoleID = function(RoleID) {
			sessionStorage.RoleID = RoleID;
		};

		obj.getRoleName = function() {
			return sessionStorage.RoleName;
		};
		obj.setRoleName = function(RoleName) {
			sessionStorage.RoleName = RoleName;
		};

		obj.getMenu = function() {
			return JSON.parse(sessionStorage.Menu);
		};
		obj.setMenu = function(Menu) {
			sessionStorage.Menu = JSON.stringify(Menu);
		};

		obj.getAuthorities = function() {
			return JSON.parse(sessionStorage.Authorities);
		};
		obj.setAuthorities = function(Authorities) {
			sessionStorage.Authorities = JSON.stringify(Authorities);
		};

		obj.getI18N = function() {
			if(sessionStorage.I18N)
				return JSON.parse(sessionStorage.I18N);
			return null;
		};
		obj.setI18N = function(I18N) {
			sessionStorage.I18N = JSON.stringify(I18N);
		};

		obj.getAvailBranch = function() {
			return JSON.parse(sessionStorage.AvailBranch);
		};

		obj.setAvailBranch = function(AvailBranch) {
			sessionStorage.AvailBranch = JSON.stringify(AvailBranch);
		};

		obj.getAvailRegion = function() {
			return JSON.parse(sessionStorage.AvailRegion);
		};

		obj.setAvailRegion = function(AvailRegion) {
			sessionStorage.AvailRegion = JSON.stringify(AvailRegion);
		};

		obj.getAvailArea = function() {
			return JSON.parse(sessionStorage.AvailArea);
		};
		obj.setAvailArea = function(AvailArea) {
			sessionStorage.AvailArea = JSON.stringify(AvailArea);
		};

		obj.getWATERMARK = function() {
			return JSON.parse(sessionStorage.WATERMARK);
		};
		obj.setWATERMARK = function(WATERMARK) {
			sessionStorage.WATERMARK = JSON.stringify(WATERMARK);
		};

		obj.getLoginID = function() {
			return JSON.parse(sessionStorage.LoginID);
		};
		obj.setLoginID = function(LoginID) {
			sessionStorage.LoginID = JSON.stringify(LoginID);
		};

		obj.getPriID = function() {
			return JSON.parse(sessionStorage.PriID);
		};
		obj.setPriID = function(PriID) {
			sessionStorage.PriID = JSON.stringify(PriID);
		};

		obj.getMemLoginFlag = function() {
			return JSON.parse(sessionStorage.MemLoginFlag);
		};
		obj.setMemLoginFlag = function(MemLoginFlag) {
			sessionStorage.MemLoginFlag = JSON.stringify(MemLoginFlag);
		};

		///======================
		// 找交易資訊
		///======================
		obj.findTxnInfo = function(sTxnCode) {
			return obj.TxnInfo[sTxnCode];
		};

		obj.parseUserInfo = function(oJSON){
			parseUserInfo(oJSON);
		}

		///======================
		// 行動裝置token
		///======================
		obj.getLoginSourceToken = function(){
			return sessionStorage.loginSourceToken;
		};

		obj.setLoginSourceToken = function(loginSourceToken){
			sessionStorage.loginSourceToken = loginSourceToken;
		};

		obj.getCurrentUserId = function(){
			return sessionStorage.currentUserId;
		};

		obj.setCurrentUserId = function(currentUserId){
			sessionStorage.currentUserId = currentUserId;
		};

		obj.getIsUHRM = function() {
			return JSON.parse(sessionStorage.isUHRM);
		};

		obj.setIsUHRM = function(isUHRM) {
			sessionStorage.isUHRM = JSON.stringify(isUHRM);
		};

		obj.getTempUserInfo = function() {
			if (sessionStorage.tempUserInfo)
				return JSON.parse(sessionStorage.tempUserInfo);
			return null;
		};

		obj.setTempUserInfo = function(tempUserInfo) {
			if (tempUserInfo)
				sessionStorage.tempUserInfo = JSON.stringify(tempUserInfo);
			else
				sessionStorage.tempUserInfo = null;
		};

		/**************************************************************************/

		///======================
		/// 解析選單
		/// 因為目前選單混搭 SL、HTML
		/// 故將不必要的選單移除
		///======================
		function parseMenu(strXmlMenu) {
  			var oMenus = x2js.xml_str2json(strXmlMenu);
  			var oMenuLen = oMenus.Root.Menu.length;
  			for (var inx = oMenuLen-1; inx>=0; inx--) {
  				var sTxnGroupID = oMenus.Root.Menu[inx]._GroupID;
  				if (sTxnGroupID == "HIDE") {
  					//oMenus.Root.Menu.splice(inx, 1);
  				} else if (sTxnGroupID != "05CRM") {
  					//限顧客管理
  					oMenus.Root.Menu.splice(inx, 1);
  				}
  			}
  			obj.MenuInfo = oMenus.Root.Menu;
  			/**marking for production**/
//			console.log(obj.MenuInfo);
		}
		///======================
		/// 解析交易資訊 (代號、名稱、參數、權限...)
		///======================
		function parseTxncode(oMenuInfo) {
			var nMainMenu = oMenuInfo.length;
			for (var idx=0; idx<nMainMenu; idx++) {
				var oTxn = oMenuInfo[idx];
				if (oTxn._Type === "M") {
					parseTxncode(oTxn.Menu_asArray);
				} else if (oTxn._Type === "P" ||
						   oTxn._Type === "H") {
					var sTxnCode = oTxn._MenuCode;
					//var sTxnName = oTxn._Title;
					obj.TxnInfo[sTxnCode] = oTxn;
				}
			}
		}
		///======================
		/// 解析跑馬燈
		///======================
		function parseMarquee(sJSON) {
			var oJSON = x2js.xml_str2json(sJSON);
			/**marking for production**/
//			console.log(oJSON.WorkList);
//			console.log(oJSON.WorkList.Announce_asArray);

			obj.Marquee = [];
			if (oJSON.WorkList.Announce_asArray) {
				try {
					var nAnnounce = oJSON.WorkList.Announce_asArray.length;
					for (var idx=0; idx<nAnnounce; idx++) {
						var oItem = oJSON.WorkList.Announce_asArray[idx].Item;
						if (oItem._DisplayPanel==="Broad") {
							obj.Marquee.push(oItem._Content);
						}
					}
				} catch(e) { }
			}
		}
		///======================
		/// 入口點
		/// 解析 常用選單、系統變數、待辦事項
		///======================
		function parseUserInfo(oJSON) {
//			console.log("parseUserInfo (常用選單、系統變數、待辦事項)");
			var myInfo = JSON.parse(oJSON.d);
			/**marking for production**/
			//console.log(myInfo);

			/**marking for production**/
//			console.log("oJSON.d " + oJSON.d);
//			console.log("myInfo " + myInfo);
//			console.log("myInfo.UserInfo " + myInfo.UserInfo);

			//個人資訊
  			obj.UserInfo = JSON.parse(myInfo.UserInfo);
  			/**marking for production**/
  			//console.log(obj.UserInfo);

  			//系統變數
  			var oParam = x2js.xml_str2json(myInfo.SysParam);
  			obj.SysParam = oParam.root;
  			/**marking for production**/
//       		console.log(obj.SysParam);

			//選單 (去掉隱藏選單)
			parseMenu(myInfo.Menu);
			parseTxncode(obj.MenuInfo);

			//跑馬燈
			parseMarquee(myInfo.WorkItem);
		};



		arguments.callee._singletonInstance = obj;
		return obj;

}]);
