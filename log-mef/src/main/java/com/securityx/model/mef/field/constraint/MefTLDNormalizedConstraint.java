package com.securityx.model.mef.field.constraint;

import com.google.common.collect.ListMultimap;
import com.securityx.model.mef.field.api.*;
import com.securityx.shaded.google.common.net.InternetDomainName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * constraint checking if the value provided for destinationDnsDomain is a under public domain, if yes,
 * populates destinationDnsDomainTLD with the tld of the domain.
 */

public class MefTLDNormalizedConstraint extends AbstractNameSpacedConstraint {
  private Logger logger = LoggerFactory.getLogger(MefTLDNormalizedConstraint.class);
  public InputTuplizer<String> getInputTuplizer() {
    return new StringTuplizer();
  }

  public Map<SupportedFormat, Object> validate(ValidationLogger validationLogger, SupportedFormat field, String value,
          ListMultimap<String, Object> context) {
      Map<SupportedFormat, Object> results = new HashMap<SupportedFormat, Object>();
      initFromNamespace(field);
      if (this.tld != null && value!=null && value.length() > 2) {
          if (logger.isDebugEnabled())
              logger.debug("MefTLDNormalizedConstraint :" + field.getPrettyName() + " : " + value);

          InternetDomainName domain = InternetDomainName.from(value);
          if (domain.isUnderPublicSuffix()) {
              InternetDomainName owner = InternetDomainName.from(value).topPrivateDomain();
              results.put(this.tld, owner.toString());
              if (logger.isDebugEnabled())
                  logger.debug("MefTLDNormalizedConstraint : tld= " + owner.toString());

          }
      }
      return results;
  }

    public static String findPathJar(Class<?> context) throws IllegalStateException {
        if (context == null) context = InternetDomainName.class;
        String rawName = context.getName();
        String classFileName;
    /* rawName is something like package.name.ContainingClass$ClassName. We need to turn this into ContainingClass$ClassName.class. */ {
            int idx = rawName.lastIndexOf('.');
            classFileName = (idx == -1 ? rawName : rawName.substring(idx+1)) + ".class";
        }
        String uri = context.getResource(classFileName).toString();
        System.out.println(uri.toString());

        try {
            URL[] classpath = (((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs());
            for (URL u : classpath) {
                System.out.println("cp: " + u.toString());
            }
        }catch( Exception e){
            e.printStackTrace();
        }
            if (uri.startsWith("file:")) throw new IllegalStateException("This class has been loaded from a directory and not from a jar file.");
        if (!uri.startsWith("jar:file:")) {
            int idx = uri.indexOf(':');
            String protocol = idx == -1 ? "(unknown)" : uri.substring(0, idx);
            throw new IllegalStateException("This class has been loaded remotely via the " + protocol +
                    " protocol. Only loading from a jar on the local file system is supported.");
        }
        int idx = uri.indexOf('!');
        //As far as I know, the if statement below can't ever trigger, so it's more of a sanity check thing.
        if (idx == -1) throw new IllegalStateException("You appear to have loaded this class from a local jar file, but I can't make sense of the URL!");
        try {
            String fileName = URLDecoder.decode(uri.substring("jar:file:".length(), idx), Charset.defaultCharset().name());
            return new File(fileName).getAbsolutePath();
        } catch (UnsupportedEncodingException e) {
            throw new InternalError("default charset doesn't exist. Your VM is borked.");
        }

    }

    public static void main(String[] args){
        MefTLDNormalizedConstraint c = new MefTLDNormalizedConstraint();


        String value = "google.com";
        Map<SupportedFormat, Object> out = c.validate(new ValidationLogger(), WebProxyMefField.destinationDnsDomain, value, null);;
        System.out.println(out);
        System.out.println(findPathJar(InternetDomainName.class));

    }

}
