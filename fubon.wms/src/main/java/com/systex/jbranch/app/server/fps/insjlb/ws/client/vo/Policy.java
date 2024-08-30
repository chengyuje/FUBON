package com.systex.jbranch.app.server.fps.insjlb.ws.client.vo;

import java.util.ArrayList;
import java.util.List;

public class Policy {
    private PolicyColumn policyColumn;
    private List<PolicyDtlColumn> policyDtlColumns = new ArrayList();

    public PolicyColumn getPolicyColumn() {
        return policyColumn;
    }

    public void setPolicyColumn(PolicyColumn policyColumn) {
        this.policyColumn = policyColumn;
    }

    public List<PolicyDtlColumn> getPolicyDtlColumns() {
        return policyDtlColumns;
    }

    public void setPolicyDtlColumns(List<PolicyDtlColumn> policyDtlColumns) {
        this.policyDtlColumns = policyDtlColumns;
    }
    
    
}
