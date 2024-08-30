function formatWebViewParam(){
	this.getScope().appCUST_ID = this.getParam().CUST_ID;
	this.getScope().appSPP_ID = this.getParam().SPP_ID;
}	

function formatWebViewResParam(data){
	this.setResult(data);
}