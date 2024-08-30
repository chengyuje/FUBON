/**
 * 
 */
package com.systex.jbranch.fubon.webservice.rs;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 1500617
 * @date 20/9/2016
 */
public class WASApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(HelloRS.class);
        classes.add(KycOperation.class);
        classes.add(MobileRS.class);
        classes.add(MonitorOperation.class);

        return classes;
    }
}
