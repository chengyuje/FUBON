function formatWebViewParam() {
  var productId = this.getParam().productId;
  var productName = this.getParam().productName;

  var obj = {
    'PRD_ID': productId,
    'BOND_CNAME': productName
  };

  setTimeout(() => {
    console.log(productId);
  }, 3000)
  this.getScope().row = obj;
}

function formatWebViewResParam(data) {
  this.setResult(data);
}
