$(function(){
	var w = $("#mwt_slider_content").width();
//	$('#mwt_slider_content').css('height', ($(window).height() - 20) + 'px' ); 
	
	/** Open/Close by Click **/
	$("#mwt_fb_tab").click(function(){
		if ($("#mwt_mwt_slider_scroll").css('left') == '-'+w+'px')
		{
			//Open
			$("#mwt_mwt_slider_scroll").animate({ left:'0px' }, 500 ,'swing');
			$("#mwt_fb_tab i").removeClass("glyphicon-chevron-right");
			$("#mwt_fb_tab i").addClass("glyphicon-chevron-left");
			$("#mwt_fb_tab a").removeClass("left_no");
			$("#mwt_fb_tab a").addClass("left_go");
		}
		else
		{
			//Close
			$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 500 ,'swing');
			$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
			$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
			$("#mwt_fb_tab a").removeClass("left_go");
			$("#mwt_fb_tab a").addClass("left_no");
		}
	});
	
	
	
	/** Get MSG **/
	/* Add: 2016/02/22 ArthurKO */
	$("#mwt_msg_panl").bind('DOMNodeInserted',function(event){
		//Inserted
		if (event.type == 'DOMNodeInserted') {
			var i=0;
			var temp = $("#mwt_msg_panl > div").each(function(){i++;});
			if (i > 1) {
				if ($("#mwt_mwt_slider_scroll").css('left') == '-'+w+'px')
				{
					$("#mwt_mwt_slider_scroll").animate({ left:'0px' }, 500 ,'swing');
					$("#mwt_fb_tab i").removeClass("glyphicon-chevron-right");
					$("#mwt_fb_tab i").addClass("glyphicon-chevron-left");
					$("#mwt_fb_tab a").removeClass("left_no");
					$("#mwt_fb_tab a").addClass("left_go");
				}
			}
	    }
	});
	
	
	
	/** Close by blur **/
	/* Add: 2016/03/02 ArthurKO */
//	$("#mwt_slider_content").blur(function(){
//		if ($("#mwt_mwt_slider_scroll").css('left') == w+'px'){
//			$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 600 ,'swing');
//			$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
//			$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
//			$("#mwt_fb_tab a").removeClass("left_go");
//			$("#mwt_fb_tab a").addClass("left_no");
//		}
//	});
	
	
	
    /** 滑鼠移開移入 **/	
	/* Modify: 2016/03/02 ArthurKO */
//	$("#mwt_slider_content").mouseleave(function() {
//		$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 500 ,'swing');
//		$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
//		$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
//		$("#mwt_fb_tab a").removeClass("left_go");
//		$("#mwt_fb_tab a").addClass("left_no");
//	});
	
	
	
//  滑鼠移開點擊	
//	$('body').click(function(e) {
//	    var target = $(e.target);
//	    if(!target.is('#mwt_fb_tab') &&
//    	   !target.is('#mwt_slider_content') &&
//    	   !target.is('#mwt_mwt_slider_scroll') &&
//    	   !target.is('#mwt_fb_tab') &&
//    	   !target.is('#mwt_msg_panl')
//	    )
//	    {
//			$("#mwt_mwt_slider_scroll").animate( { left:'-'+w+'px' }, 500 ,'swing');
//			$("#mwt_fb_tab i").removeClass("glyphicon-chevron-left");
//			$("#mwt_fb_tab i").addClass("glyphicon-chevron-right");
//			$("#mwt_fb_tab a").removeClass("left_go");
//			$("#mwt_fb_tab a").addClass("left_no");
//	    };
//	});	
	
	

});



//次選單
//$(function(){
//	var w = $("#menu_slider_content").width();
//	
//	$("#menu_fb_tab").click(function(){
//		if ($("#menu_mwt_slider_scroll").css('right') == '-'+w+'px')
//		{
//			$("#menu_mwt_slider_scroll").animate({ right:'0px' }, 600 ,'swing');
//			$("#menu_fb_tab i").removeClass("glyphicon-chevron-right");
//			$("#menu_fb_tab i").addClass("glyphicon-chevron-left");
//			$("#menu_fb_tab a").removeClass("menu_no");
//			$("#menu_fb_tab a").addClass("menu_go");
//		}
//		else
//		{
//		$("#menu_mwt_slider_scroll").animate( { right:'-'+w+'px' }, 600 ,'swing');
//		$("#menu_fb_tab i").removeClass("glyphicon-chevron-left");
//		$("#menu_fb_tab i").addClass("glyphicon-chevron-right");
//		$("#menu_fb_tab a").removeClass("menu_go");
//		$("#menu_fb_tab a").addClass("menu_no");
//		}
//	});
//
//});



//判斷TOP鎖定		
$(function(){
　$(window).load(function(){
　　$(window).bind('scroll resize', function(){
　　var $this = $(this);
　　var $this_Top=$this.scrollTop();

　　//當高度小於100時，關閉區塊 
　　if($this_Top < 71){
	$("#top-bar").removeClass("top-menubar-no");
	$("#top-bar").addClass("top-menubar-yes");
	$('.container_up').css("padding-top","5px");
　　　}
　　　　if($this_Top > 71){
	$("#top-bar").removeClass("top-menubar-yes");
	$("#top-bar").addClass("top-menubar-no");
	$('.container_up').css("padding-top","48px");
　　　 }
　　}).scroll();
　});
});