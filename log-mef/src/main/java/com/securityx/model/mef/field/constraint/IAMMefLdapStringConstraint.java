package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ldap.LdapName;
import java.util.HashMap;
import java.util.Map;


/**
 * early stages version of ldap parsing for AD Distinguished Names
 * Keep in mind that AD is specific : see http://www.selfadsi.org/ldap-path.htm for some details
 */
public class IAMMefLdapStringConstraint implements MefFieldConstrait<String> {
    private Logger logger = LoggerFactory.getLogger(InetNameOrIpConstraint.class);
    private IAMMefField userName;
    private IAMMefField userNtDomain;


    @Override
    public InputTuplizer<String> getInputTuplizer() {
        return new StringTuplizer();
    }

    private void initFromNamespace(SupportedFormat f) {
        //TODO : improve namespace management
        if (f.getNamespace().equals("destination")) {
            this.userName = IAMMefField.destinationUserName;
            this.userNtDomain = IAMMefField.destinationNtDomain;
        } else if (f.getNamespace().equals("source")) {
            this.userName = IAMMefField.sourceUserName;
            this.userNtDomain = IAMMefField.sourceNtDomain;
        }

    }

    @Override
    public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value, ListMultimap<String, Object> context) {
        boolean isFirstCn = true;
        String user = null;
        String domain = null;
        initFromNamespace(field);
        Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
        if (logger.isDebugEnabled())
            logger.debug("IAMMefLdapStringConstraint :" + field.getPrettyName() + " : " + value);
       // String dn = "CN=Jimmy Blooptoop,OU=Someplace,OU=Employees,DC=Bloopsoft-Inc";
        LdapName ln = null;
        String[] parts = value.split("(?<=,?(?:DC|OU|CN))=|,(?=DC|OU|CN)");
        for (int i =0; i< parts.length;i=i+2){
             if (parts[i].equalsIgnoreCase("CN") && isFirstCn) {
                 user = parts[i + 1];
                isFirstCn = false;
                 continue;
             }
             if (parts[i].equalsIgnoreCase("DC"))
                domain = (domain==null?parts[i+1]:domain.concat(".".concat(parts[i+1])));

        }

        if (null != domain && user  != null) {
            results.put(this.userNtDomain, domain);
            results.put(this.userName, user);
            results.put(field, value);
        }
        return results;
    }
}

