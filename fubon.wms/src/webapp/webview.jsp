<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<style>
	.webview .modal-dialog{
		width: 100% !important;
		max-width: none !important;
		height: calc(100vh + 53px) !important;
		margin: -53px 0 0 0 !important;
		position: absolute !important;
		top: 0 !important;
		right: 0 !important;
		bottom: 0 !important;
		left: 0 !important;
	}

	.webview .modal-content{
		height: 100% !important;
	}

	.webview .modal-body{
		height: calc(100% - 53px) !important;
		max-height: 99vh !important;
	}

	.modal-body{
		max-height: 80vh !important;
	}
</style>
<html ng-app="eSoafApp">
	<head>
		<!-- InstanceBeginEditable name="doctitle" -->
		<title>富邦銀行財富管理系統</title>
		<!-- InstanceEndEditable -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">

		<!-- platform icon -->
		<link rel="shortcut icon" href="assets/images/favicon.ico" type="image/x-icon" />

		<!-- CSS-framework: Bootstrap -->
		<link rel="stylesheet" media="screen" href="assets/css/bootstrap.min.css">
		<link rel="stylesheet" media="screen" href="assets/css/bootstrap-theme.min.css">
		<link rel="stylesheet" media="screen" href="assets/css/bootstrap-admin-theme.css">
		<link rel="stylesheet" href="assets/lib/angular-bootstrap-calendar/dist/css/angular-bootstrap-calendar.min.css">

		<!-- CSS-framework: AngularJS -->
		<link rel="stylesheet" type="text/css" href="assets/lib/Angular.MultiSelect/angular-multi-select.css" />
		<link rel="stylesheet" href="assets/lib/ngDialog/css/ngDialog.css">
		<link rel="stylesheet" href="assets/lib/ngDialog/css/ngDialog-theme-default.css">
		<link rel="stylesheet" href="assets/lib/ngDialog/css/ngDialog-theme-plain.css">
		<link rel="stylesheet" href="assets/lib/ngDialog/css/ngDialog-custom-width.css">

		<!-- CSS-framework: JQuery -->
		<link rel="stylesheet" href="assets/lib/JQuery.UI/jquery-ui.min.css">
		<link rel="stylesheet" href="assets/lib/JQueyFileUpload/css/jquery.fileupload.css">

		<!-- CSS-others -->
		<link rel="stylesheet" href="assets/css/font-awesome/css/font-awesome.min.css">
		<link rel='stylesheet' href="assets/lib/angular-bootstrap-colorpicker/css/colorpicker.css">
		<link rel='stylesheet' href="assets/lib/textAngular/dist/textAngular.css">
		<link rel="stylesheet" href="assets/lib/nvD3/css/nv.d3.css">

		<!-- CSS-Platform: initial -->
		<link rel="stylesheet" href="assets/css/fubon.css" />

		<!-- CSS-Platform: highest priority -->
		<link rel="stylesheet" href="assets/css/esoaf-font.css" />	<!-- priority:-1 info:library of font -->
	    <link rel="stylesheet" href="assets/css/esoaf-animate.css" />	<!-- priority:0 info:library of animate -->
	    <link rel="stylesheet" href="assets/css/esoaf-theme.css" /> 	<!-- priority:2 info:style-theme -->
	    <link rel="stylesheet" href="assets/css/esoaf-framework.css" /> <!-- priority:3 info:framework-custom-sttle -->
	    <link rel="stylesheet" href="assets/css/esoaf-platform.css" />  <!-- priority:4 info:platform-framework -->
	    <link rel="stylesheet" href="assets/css/esoaf-component.css" /> <!-- priority:5 info:platform-component -->

		<!------------------------------------------------------------------------->
		<!-- 						                 						     -->
		<!-- 						   < Library >	 						     -->
		<!-- 						                 						     -->
		<!-- 						      JQuery 								 -->
		<!-- 						                 						     -->
		<!------------------------------------------------------------------------->
		<script src="assets/lib/JQuery/jquery-3.6.0.min.js"></script>
		<script src="assets/lib/JQuery/jquery-migrate-3.3.2.min.js"></script>
		<script src="assets/lib/JQuery.UI/jquery-ui.min.js" type="text/javascript"></script>
		<script src="assets/lib/FileSaver.min.js" type="text/javascript"></script>
		<script src="assets/lib/pdf.js" type="text/javascript"></script>
		<script src="assets/lib/pdf.worker.js" type="text/javascript"></script>

		<!------------------------------------------------------------------------->
		<!-- 						                 						     -->
		<!-- 						  < Framework > 						     -->
		<!-- 						                 						     -->
		<!-- 						    AngularJS 								 -->
		<!-- 						                 						     -->
		<!------------------------------------------------------------------------->
		<!--
		    		   CurrentVerion:
		   								AngularJS 1.5.5
		    	Adaptability Version:
		         						AngularJS 1.2x 支援 IE8+
		         						AngularJS 1.3x 支援 IE9+
		-->
		<!-- AngularJS Main Module -->
		<script src="assets/lib/Angular/angular.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<!-- AngularJS Main PlugIn Module -->
		<script src="assets/lib/Angular/angular-route.min.js" type="text/javascript"></script>
		<script src="assets/lib/Angular/angular-cookies.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/Angular/angular-sanitize.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/Angular/angular-touch.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/Angular/angular-confirm.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper/moved -->
		<script src="assets/lib/ngDialog/js/ngDialog.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper/moved -->
		<script src="assets/lib/i18nDynamic/i18nDynamic.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper/moved -->
		<script src="assets/lib/i18nDynamic/i18nDynDictionary.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper/moved -->
		<script src="assets/lib/ng-caps-lock-master/ng-caps-lock.min.js" type="text/javascript"></script>
		<!-- AngularJS Others PlugIn Module -->
		<script src="assets/lib/nvD3/js/d3.min.js" type="text/javascript"></script> <!-- Angular-nvD3 Chart --> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/nvD3/js/nv.d3.min.js" type="text/javascript"></script> <!-- Angular-nvD3 Chart --><!-- or use another assembly --> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/nvD3/js/angular-nvd3.min.js" type="text/javascript"></script> <!-- Angular-nvD3 Chart --> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/angular-filter-master/dist/angular-filter.min.js"></script>	<!-- Angular-filter -->
		<script src="assets/lib/lodash.min.js" type="text/javascript"></script> <!-- Angular Google Maps --> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/angular-simple-logger-master/dist/angular-simple-logger.min.js" type="text/javascript"></script> <!-- Angular Google Maps -->
		<script src="assets/lib/angular-google-maps-master/dist/angular-google-maps.min.js" type="text/javascript"></script> <!-- Angular Google Maps -->
		<script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAMwo711dUFYHdBj7xgFvYL_B9r8CIEumg" type="text/javascript"></script> <!-- Angular Google Maps -->
		<script src="assets/lib/Angular/Chart.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/Angular/angular-chart.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/angular-bootstrap-colorpicker/js/bootstrap-colorpicker-module.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/textAngular/dist/textAngular-rangy.min.js" type="text/javascript"></script>
		<script src="assets/lib/textAngular/dist/textAngular-sanitize.min.js" type="text/javascript"></script>
		<script src="assets/lib/textAngular/dist/textAngular.min.js" type="text/javascript"></script>
		<script src="assets/lib/Angular.MultiSelect/angular-multi-select.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/script/canvasjs.min.js" type="text/javascript"></script>
		<script src="assets/script/chart.config.js" type="text/javascript"></script>
		<script src="assets/script/html2canvas.js" type="text/javascript"></script>
		<script src="assets/script/jspdf.js" type="text/javascript"></script>

		<!-- Others PlugIn Module -->
		<script src="assets/lib/Slimccroll/jquery.slimscroll.min.js" type="text/javascript"></script>
		<script src="assets/lib/rangy-core.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/rangy-selectionsaverestore.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/JQueyFileUpload/jquery.iframe-transport.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/JQueyFileUpload/jquery.fileupload.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/fullcalendar/lib/moment.min.js" type="text/javascript"></script>
		<script src="assets/lib/angular-pdf/dist/angular-pdf.js" type="text/javascript"></script>

		<!-- angular-bootstrap-calendar -->
		<script src="assets/lib/interact.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/angular-bootstrap-calendar/dist/js/angular-bootstrap-calendar-tpls.min.js" type="text/javascript"></script>

		<script src="assets/lib/Request/require.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->
		<script src="assets/lib/Bootstrap/bootstrap.min.js" type="text/javascript"></script>

		<script src="assets/lib/sortable.min.js" type="text/javascript"></script> <!-- 2017/08/01: zipper -->


		<script src="assets/lib/ui.bootstrap/ui-bootstrap-tpls-1.3.3.min.js" type="text/javascript"></script>

		<!--------------------------------< Platform >-------------------------------->

		<!-- function -->
		<script src="assets/js/functions/esoaf-framework.min.js" type="text/javascript"></script>
		<script src="assets/js/functions/esoaf-fakement.min.js" type="text/javascript"></script>
		<script src="assets/js/functions/esoaf-tool.min.js" type="text/javascript"></script>

		<!-- module -->
		<script src="assets/js/modules/app.min.js" type="text/javascript"></script>

		<!-- component: services -->
		<script src="assets/js/services/validateService.min.js" type="text/javascript"></script>
		<script src="assets/js/services/imgFileUploadService.min.js" type="text/javascript"></script>
		<script src="assets/js/services/sysInfoService.min.js" type="text/javascript"></script>
		<script src="assets/js/services/projInfoService.min.js" type="text/javascript"></script>
		<script src="assets/js/services/socketService.min.js" type="text/javascript"></script>
		<script src="assets/js/services/withLazyModuleService.js" type="text/javascript"></script>
		<script src="assets/js/services/alerts.min.js" type="text/javascript"></script>
		<script src="assets/js/services/getParameterService.min.js" type="text/javascript"></script>
		<script src="assets/txn/SOT701/SotService.js" type="text/javascript" ></script>
		
		<!-- Juan -->
		<script src="assets\txn\FPS\FPSUtilsService.js" type="text/javascript"></script>

		<!-- component: filters -->
		<script src="assets/js/filters/eDateTime.min.js" type="text/javascript"></script>
		<script src="assets/js/filters/eMasking.min.js" type="text/javascript"></script>
		<script src="assets/js/filters/ePersent.min.js" type="text/javascript"></script>
		<script src="assets/js/filters/repeatCal.min.js" type="text/javascript"></script>
		<script src="assets/js/filters/smallTitle.min.js" type="text/javascript"></script>
		<script src="assets/js/filters/mapping.min.js" type="text/javascript"></script>
		<script src="assets/js/filters/eMakePositive.js" type="text/javascript"></script>
		<script src="assets/js/filters/eChangeBraByte.js" type="text/javascript"></script>

		<!-- component: controllers -->
		<script src="assets/js/controllers/BaseController.min.js" type="text/javascript"></script>
		<!-- <script src="assets/js/controllers/MenuBarController.min.js" type="text/javascript"></script> -->
		<script src="webview.js" type="text/javascript"></script>
		<script src="${initJsUrl}" type="text/javascript"></script>
		<script type="text/javascript" src="${initJsPath}"></script>

		<!-- <script src="assets/js/controllers/InitController.min.js" type="text/javascript"></script> -->
		<script src="assets/js/controllers/wvInitController.js" type="text/javascript"></script>
		<script src="assets/js/controllers/MarqueeRunController.min.js" type="text/javascript"></script>
		<script src="assets/js/controllers/RegionController.min.js" type="text/javascript"></script>
		<script src="assets/js/controllers/BannerController.min.js" type="text/javascript"></script>

		<!-- component: directives -->
		<script src="assets/js/directives/ngRepeatEndWatch.min.js" type="text/javascript"></script>
    	<script src="assets/js/directives/elemReady.min.js" type="text/javascript"></script>
    	<script src="assets/js/directives/autofocus.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/enterEvent.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eCombobox.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eUpload.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eUploadOnly.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/ePaging.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/ePagingOnce.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eInputLimit.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/slimScrollInit.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eLengthLimit.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/functionType.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/ControllerManager.min.js" type="text/javascript"></script>
<!-- 	<script src="assets/js/directives/tablesaw.min.js" type="text/javascript"></script> -->
		<script src="assets/js/directives/msgBar.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eJumping.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eJumpingOnce.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eTrustUrl.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eDialogBox.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eDialogCtrl.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/ePanelCtrl.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/custBook.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eNumberRange.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eSorter.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eProgress.min.js" type="text/javascript"></script>
		<script src="assets/js/directives/eTableFreeze.min.js" type="text/javascript"></script>

		<!-- platform init: controllers, services -->
		<script src="assets/js/controllers/DialogAlertController.min.js" type="text/javascript"></script>

		<!-- platform txn -->
		<script src="assets/txn/CMFPG000/CMFPG000.min.js" type="text/javascript"></script>
		<script src="assets/txn/CMFPG000/CMFPG000_AGENT.min.js" type="text/javascript"></script>

		<!-- component: javascsript -->
		<script src="assets/lib/highchart/highcharts.min.js" type="text/javascript"></script>
		<script src="assets/lib/highchart/exporting.min.js" type="text/javascript"></script>

		<!-- Juan FPS components -->
	  <script src="assets/js/components/iosNav/iosNav.js" type="text/javascript"></script>
	  <link href="assets/js/components/iosNav/iosNav.css" rel="stylesheet">
	  <script src="assets/js/components/dragBar/dragBar.js" type="text/javascript"></script>
		<link href="assets/js/components/dragBar/dragBar.css" rel="stylesheet">
		<!-- Juan plugins -->
		<script src="assets/script/html2Pdf.js" type="text/javascript"></script>
	  <!-- Juan FPS module CSS -->
		<link rel="stylesheet" href="assets/txn/FPS/FPS.css">

		<script type="text/javascript">
			var isNoParam = ${isNoParam};
			var mpUrl = '${mpUrl}';
		</script>
	</head>

	<div ng-controller="webviewController" class="webview">
<!-- 測試用 -->
<!-- 		<input type="button" value="test input value" ng-click="initPageData({COM_ID:'209', IS_MAIN_TYPE:'Y'})"> -->
<!-- 		<input type="button" value="input param" ng-click="initPageData({productId :  '1712' , productName :'聯博新興市場企業債配'});"> -->
<!-- 		<input type="button" value="get result" ng-click="getResult()"> -->

		<!-- 沒有圓圈圈的行事曆template -->
		<%@include file="webviewScript.html" %>
		<!-- fake body -->
		<div ng-if="isShowContent">
			<body>
				<div ng-controller="InitController">
					<!-- zoom-01:watermark -->
					<div id="watermark" class="hidden">
						<div ng-repeat="row in WATERMARK" ng-bind="row.LABEL"></div>
					</div>

					<!-- zoom-02:banner -->
					<div style="padding-top:2em;"></div>
					<!-- <div id="banner" ng-include="'assets/platform/banner.html'"></div> -->

					<!-- zoom-03:menubar -->
					<!-- *reserved element id* -->
<!--  					<div id="menubar" ng-include="'assets/platform/menubar.html'"></div> -->

					<!-- zoom-04:pathrow -->
					<!-- <div id="pathrow"><table ng-if="menuItemInfo.txnPath"><tr><td><a ng-click="MenuBarLink(path,0)"><i class="glyphicon glyphicon-home"></i></a><i class="glyphicon glyphicon-chevron-right e_padding_2"></i></td><td ng-repeat="path in menuItemInfo.txnPath track by $index"><label class="e_padding_2"><a ng-click="MenuBarLink(path,1)">{{path.MENU_NAME}}</a></label><i ng-if="!$last" class="glyphicon glyphicon-chevron-right e_padding_2"></i></td></tr></table></div> -->

					<!-- zoom-05:wrapper -->
					<div id="wrapper" class="container-fluid">
						<div ng-include="menuItemInfo.url"></div>
					</div>

					<!-- zoom-06:info -->
					<!-- <div class="tfb_footer">
						<i class="glyphicon glyphicon-copyright-mark"></i> 台北富邦商業銀行股份有限公司<br> 建議瀏覽器版本：IE11 或 Chrome 59 以上 最佳解析度 1024×768
					</div> -->

					<!-- zoom-07:messagebar --> <!-- css:footer&footer2 has been cannel -->
					<div id="footer-msgbar">
				   		<div style="margin:6px 0 0 0;"><msg-bar></msg-bar></div>
				   	</div>

					<!-- zoom-08:dialogbox -->
					<div id="dialog_box">
						<div id="#dialog_insitBox">
							<e-dialogbox></e-dialogbox>
						</div>
					</div>
				</div>
			</body>
		</div>
	</div>
</html>