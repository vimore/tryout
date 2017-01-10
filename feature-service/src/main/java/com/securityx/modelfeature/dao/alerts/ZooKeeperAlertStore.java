package com.securityx.modelfeature.dao.alerts;

import com.securityx.modelfeature.alert.AlertHandler;
import com.securityx.modelfeature.alert.zookeeper.ZookeeperClient;
import com.securityx.modelfeature.common.inputs.AlertDefinition;
import com.securityx.modelfeature.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ZooKeeperAlertStore implements  AlertStore{

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperAlertStore.class);
    private final ZookeeperClient zookeeperClient;

    public ZooKeeperAlertStore(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }

    private String getAlertPath() {
        return AlertHandler.ZOOKEEKER_BASE_PATH;
    }

    private String getAlertPath(String alertId) {
        return getAlertPath() + AlertHandler.ZOOKEEPER_PATH_SEPARATOR + alertId;
    }

    @Override
    public Optional<AlertDefinition> getAlert() {
        try{
            Optional<String> alertString = zookeeperClient.getData(getAlertPath());
            if(alertString.isPresent()) {
                return JsonUtils.getObjectFromJson(alertString.get(), AlertDefinition.class);
            }
        }catch(Exception e){
            throw  new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AlertDefinition> createAlert(AlertDefinition alert) {
        Optional<String> json = JsonUtils.toJsonString(alert);
        if(json.isPresent()) {
            try {
                zookeeperClient.setData(json.get(), getAlertPath(alert.getAlertId()));
                return Optional.of(alert);
            } catch (Exception e) {
                LOGGER.error("Error occurred while creating an Alert => " + e);
            }
        }
        return Optional.empty();

    }

    @Override
    public Optional<AlertDefinition> updateAlert(AlertDefinition alert) {
        Optional<String> json = JsonUtils.toJsonString(alert);
        if(json.isPresent()){
            try {
                zookeeperClient.setData(json.get(), getAlertPath(alert.getAlertId()));
                return Optional.of(alert);
            } catch (Exception e) {
                LOGGER.error("Error occurred while updating Alert => " + e);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean deleteAlert(String alertId) {
        try {
            zookeeperClient.deleteData(getAlertPath(alertId));
            return true;
        } catch (Exception e) {
            LOGGER.error("Error while deleting the alert-node => " + e);
        }
        return false;
    }
}
