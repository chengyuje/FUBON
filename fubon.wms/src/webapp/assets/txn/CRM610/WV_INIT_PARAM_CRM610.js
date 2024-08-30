function formatWebViewParam() {
  this.getScope().objMap = this.getParam();
  this.getScope().connector('set', 'CRM_CUSTVO', {
    CUST_ID: this.getScope().objMap.custID,
    CUST_NAME: this.getScope().objMap.custName
  });
  this.getScope().roleId = this.getScope().objMap.roleID;
  this.getScope().connector('set', 'CRM610URL', 'assets/txn/CRM610/CRM610_MAIN.html');
}

function formatWebViewResParam(data) {
  this.setResult(data);
}
