function formatWebViewParam() {
	var product = this.getParam().product;
	this.getScope().data = product;
}

function formatWebViewResParam(data) {
	this.setResult(data);
}