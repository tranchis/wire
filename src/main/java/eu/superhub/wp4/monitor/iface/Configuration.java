package eu.superhub.wp4.monitor.iface;

import eu.superhub.wp4.models.mobilitypolicy.PolicyModel;
import eu.superhub.wp4.monitor.wp3servicedata.SituationalDataPusher;

public interface Configuration {
    PolicyModel getPolicyModel();
    SituationalDataPusher getDataPusher();
}
