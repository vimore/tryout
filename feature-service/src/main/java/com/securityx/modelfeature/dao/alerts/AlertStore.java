package com.securityx.modelfeature.dao.alerts;


import com.securityx.modelfeature.common.inputs.AlertDefinition;

import java.util.List;
import java.util.Optional;

/**
 * AlertStore
 */
public interface AlertStore {

    public Optional<AlertDefinition> getAlert();

    public Optional<AlertDefinition> createAlert(AlertDefinition alert);

    public Optional<AlertDefinition> updateAlert(AlertDefinition alert);

    public boolean deleteAlert(String alertId);


}