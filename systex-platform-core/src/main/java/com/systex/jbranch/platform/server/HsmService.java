package com.systex.jbranch.platform.server;


public class HsmService {
	private static HSM hsm;
    //private static Logger logger = LoggerFactory.getLogger(HSM.class);

    static {
        try {
            hsm = new HSM();//(HSM) PlatformContext.getBean("dataManager");
            hsm.setPpKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzNTWkDY98iwRl54HO5YlNV0Ojll4mAJ/DMyfJ7WqaRm//jz2NSXRZEd8ysHnip5lDZtcPLGji4tv4U2tD4qb31YlGcCQ1t0iv2fLOg9QhGVHE6OOccwTkpADKUCGClRUPFRHzeQCSogqFMPPzjYCTH6WwdReh52qLPYyi10FlU7NqCy+Wxt3ASkM7SMDroco6S4vmK4OzOkjeEekGYdx4cnCKqfMVFKx5Pzb1ypU+1duaqSTpHiP1vdOK5LY95SJ7Ooy6CQYLFPN/K7xH9H69smBYMHNJlLVAec5t2kd+rbryNPAYpxnkHUVhqAlz7ooPB1p+p7QzRTjUWk/JX7tcQIDAQAB");
        }
        catch (Throwable e) {
        	e.printStackTrace();
            //logger.error(e.getMessage(), e);
        }
    }
    public static HSM getHsm() {
        return hsm;
    }

}
