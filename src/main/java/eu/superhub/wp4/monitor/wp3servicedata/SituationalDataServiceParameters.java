/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.superhub.wp4.monitor.wp3servicedata;

import java.math.BigInteger;

/**
 *
 * @author igomez
 */
public class SituationalDataServiceParameters {
       private String serviceTrafficSocialNetworkCityName;
       private String serviceTrafficSocialNetworkStateName;
       private String serviceTrafficSocialNetworkStateAbbr;
       private long serviceTrafficSocialNetworkLongitude;
       private long serviceTrafficSocialNetworkLatitude;
       private BigInteger servicePredictedTrafficFromCellNetLongitude;
       private BigInteger servicePredictedTrafficFromCellNetLatitude;
       
       //Constructor assigns values by default for testing and backwards compatibility
       public SituationalDataServiceParameters(){
           serviceTrafficSocialNetworkCityName = "Barcelona";
           serviceTrafficSocialNetworkStateName = "Spain";
           serviceTrafficSocialNetworkStateAbbr = "ES";
           serviceTrafficSocialNetworkLatitude = 0;
           serviceTrafficSocialNetworkLongitude = 0;
           servicePredictedTrafficFromCellNetLongitude = BigInteger.valueOf(45350000);
           servicePredictedTrafficFromCellNetLatitude = BigInteger.valueOf(9100000);
       }               

    /**
     * @return the serviceTrafficSocialNetworkCityName
     */
    public String getServiceTrafficSocialNetworkCityName() {
        return serviceTrafficSocialNetworkCityName;
    }

    /**
     * @param serviceTrafficSocialNetworkCityName the serviceTrafficSocialNetworkCityName to set
     */
    public void setServiceTrafficSocialNetworkCityName(String serviceTrafficSocialNetworkCityName) {
        this.serviceTrafficSocialNetworkCityName = serviceTrafficSocialNetworkCityName;
    }

    /**
     * @return the serviceTrafficSocialNetworkStateName
     */
    public String getServiceTrafficSocialNetworkStateName() {
        return serviceTrafficSocialNetworkStateName;
    }

    /**
     * @param serviceTrafficSocialNetworkStateName the serviceTrafficSocialNetworkStateName to set
     */
    public void setServiceTrafficSocialNetworkStateName(String serviceTrafficSocialNetworkStateName) {
        this.serviceTrafficSocialNetworkStateName = serviceTrafficSocialNetworkStateName;
    }

    /**
     * @return the serviceTrafficSocialNetworkStateAbbr
     */
    public String getServiceTrafficSocialNetworkStateAbbr() {
        return serviceTrafficSocialNetworkStateAbbr;
    }

    /**
     * @param serviceTrafficSocialNetworkStateAbbr the serviceTrafficSocialNetworkStateAbbr to set
     */
    public void setServiceTrafficSocialNetworkStateAbbr(String serviceTrafficSocialNetworkStateAbbr) {
        this.serviceTrafficSocialNetworkStateAbbr = serviceTrafficSocialNetworkStateAbbr;
    }

    /**
     * @return the serviceTrafficSocialNetworkLatitude
     */
    public long getServiceTrafficSocialNetworkLatitude() {
        return serviceTrafficSocialNetworkLatitude;
    }

    /**
     * @param serviceTrafficSocialNetworkLatitude the serviceTrafficSocialNetworkLatitude to set
     */
    public void setServiceTrafficSocialNetworkLatitude(long serviceTrafficSocialNetworkLatitude) {
        this.serviceTrafficSocialNetworkLatitude = serviceTrafficSocialNetworkLatitude;
    }

    /**
     * @return the serviceTrafficSocialNetworkLongitude
     */
    public long getServiceTrafficSocialNetworkLongitude() {
        return serviceTrafficSocialNetworkLongitude;
    }

    /**
     * @param serviceTrafficSocialNetworkLongitude the serviceTrafficSocialNetworkLongitude to set
     */
    public void setServiceTrafficSocialNetworkLongitude(long serviceTrafficSocialNetworkLongitude) {
        this.serviceTrafficSocialNetworkLongitude = serviceTrafficSocialNetworkLongitude;
    }

    /**
     * @return the servicePredictedTrafficFromCellNetLatitude
     */
    public BigInteger getServicePredictedTrafficFromCellNetLatitude() {
        return servicePredictedTrafficFromCellNetLatitude;
    }

    /**
     * @param servicePredictedTrafficFromCellNetLatitude the servicePredictedTrafficFromCellNetLatitude to set
     */
    public void setServicePredictedTrafficFromCellNetLatitude(BigInteger servicePredictedTrafficFromCellNetLatitude) {
        this.servicePredictedTrafficFromCellNetLatitude = servicePredictedTrafficFromCellNetLatitude;
    }

    /**
     * @return the servicePredictedTrafficFromCellNetLongitude
     */
    public BigInteger getServicePredictedTrafficFromCellNetLongitude() {
        return servicePredictedTrafficFromCellNetLongitude;
    }

    /**
     * @param servicePredictedTrafficFromCellNetLongitude the servicePredictedTrafficFromCellNetLongitude to set
     */
    public void setServicePredictedTrafficFromCellNetLongitude(BigInteger servicePredictedTrafficFromCellNetLongitude) {
        this.servicePredictedTrafficFromCellNetLongitude = servicePredictedTrafficFromCellNetLongitude;
    }
       
       
}
