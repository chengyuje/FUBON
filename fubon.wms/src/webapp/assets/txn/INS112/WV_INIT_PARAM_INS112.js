function formatWebViewParam(){
	this.getScope().COM_ID = this.getParam().COM_ID;
	this.getScope().IS_MAIN_TYPE = this.getParam().IS_MAIN_TYPE;
	if(this.getScope().IS_MAIN_TYPE == 'N') {
		this.getScope().CURR_CD = this.getParam().CURR_CD;
	}
}	

function formatWebViewResParam(data){
	this.setResult(data);
}