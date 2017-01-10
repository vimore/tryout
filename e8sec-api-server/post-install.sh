#!/bin/bash

#E8-1449 fix 
sudo ln -sf /etc/e8sec-api-server/conf/email.vm /opt/e8sec/e8sec-api-server/email.vm
sudo ln -sf /opt/e8sec/e8sec-api-server/lib/feature-service-2.*.jar feature-service.jar
sudo chown -R e8sec.e8sec /opt/e8sec/e8sec-api-server
sudo chmod -R 0775 /opt/e8sec/e8sec-api-server
