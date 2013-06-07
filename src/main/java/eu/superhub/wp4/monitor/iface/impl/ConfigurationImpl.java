package eu.superhub.wp4.monitor.iface.impl;

import eu.superhub.wp4.models.mobilitypolicy.PolicyModel;
import eu.superhub.wp4.monitor.iface.Configuration;
import eu.superhub.wp4.monitor.situationaldata.SituationalDataPusher;

public class ConfigurationImpl implements Configuration {
    
    private PolicyModel policyModel;
    private SituationalDataPusher dataPusher;

    public ConfigurationImpl(PolicyModel p, SituationalDataPusher sdp) {
	this.policyModel = p;
	this.dataPusher = sdp;
    }

    public PolicyModel getPolicyModel() {
	return policyModel;
    }

    public SituationalDataPusher getDataPusher() {
	return dataPusher;
    }
}
