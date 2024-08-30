package com.systex.jbranch.platform.common.initiation;

import java.util.List;

public class PlatformInitiator {
// ------------------------------ FIELDS ------------------------------

    private List<InitiatorIF> initiators;
    private List<InitiatorIF> postInitiators;

// -------------------------- OTHER METHODS --------------------------

    public void initiate() throws Exception {
        if (initiators != null) {
            for (InitiatorIF initiator : initiators) {
                initiator.execute();
            }
        }
    }

    public void initiatePost() throws Exception {
        if (postInitiators != null) {
            for (InitiatorIF initiator : postInitiators) {
                initiator.execute();
            }
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setInitiators(List<InitiatorIF> initiators) {
        this.initiators = initiators;
    }

    public void setPostInitiators(List<InitiatorIF> postInitiators) {
        this.postInitiators = postInitiators;
    }
}
