package com.mimerhellas.opencmis.dbshare;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.support.wrapper.CallContextAwareCmisService;
import org.apache.chemistry.opencmis.server.support.wrapper.CmisServiceWrapperManager;
import org.apache.chemistry.opencmis.server.support.wrapper.ConformanceCmisServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CMIS Service Factory.
 */
public class DBShareCmisServiceFactory extends AbstractServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DBShareCmisServiceFactory.class);
    private static final String PREFIX_REPOSITORY = "repository.";
    private static final String PREFIX_TYPE = "type.";

    /**
     * Default maxItems value for getTypeChildren()}.
     */
    private static final BigInteger DEFAULT_MAX_ITEMS_TYPES = BigInteger.valueOf(1000);

    /**
     * Default depth value for getTypeDescendants().
     */
    private static final BigInteger DEFAULT_DEPTH_TYPES = BigInteger.valueOf(-1);

    /**
     * Default maxItems value for getChildren() and other methods returning
     * lists of objects.
     */
    private static final BigInteger DEFAULT_MAX_ITEMS_OBJECTS = BigInteger.valueOf(100000);

    /**
     * Default depth value for getDescendants().
     */
    private static final BigInteger DEFAULT_DEPTH_OBJECTS = BigInteger.valueOf(10);

    /**
     * Each thread gets its own {@link FileShareCmisService} instance.
     */
    private ThreadLocal<CallContextAwareCmisService> threadLocalService = new ThreadLocal<CallContextAwareCmisService>();

    private DBShareRepositoryManager repositoryManager;
    private DBShareTypeManager typeManager;
    private CmisServiceWrapperManager wrapperManager;

    @Override
    public void init(Map<String, String> parameters) {
        repositoryManager = new DBShareRepositoryManager();
        typeManager = new DBShareTypeManager();

        wrapperManager = new CmisServiceWrapperManager();
        wrapperManager.addWrappersFromServiceFactoryParameters(parameters);
        wrapperManager.addOuterWrapper(ConformanceCmisServiceWrapper.class, DEFAULT_MAX_ITEMS_TYPES,
                DEFAULT_DEPTH_TYPES, DEFAULT_MAX_ITEMS_OBJECTS, DEFAULT_DEPTH_OBJECTS);

        //userManager = new FileShareUserManager();
        readConfiguration(parameters);
    }

    @Override
    public void destroy() {
        threadLocalService = null;
    }

    public DBShareRepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

    public DBShareTypeManager getTypeManager() {
        return typeManager;
    }

    @Override
    public CmisService getService(CallContext context) {
        // authentication can go here
        String user = context.getUsername();
        String password = context.getPassword();

        // if the authentication fails, throw a CmisPermissionDeniedException
        // get service object for this thread
        CallContextAwareCmisService service = threadLocalService.get();
        if (service == null) {
            DBShareCmisService rdbmsCmisService = new DBShareCmisService(repositoryManager);
            service = (CallContextAwareCmisService) wrapperManager.wrap(rdbmsCmisService);
            threadLocalService.set(service);
        }

        // hand over the call context to the service object
        service.setCallContext(context);

        // create a new service object
        // (can also be pooled or stored in a ThreadLocal)
        // add the conformance CMIS service wrapper
        // (The wrapper catches invalid CMIS requests and sets default values
        // for parameters that have not been provided by the client.)
        /*ConformanceCmisServiceWrapper wrapperService
                = new ConformanceCmisServiceWrapper(service, DEFAULT_MAX_ITEMS_TYPES, DEFAULT_DEPTH_TYPES,
                        DEFAULT_MAX_ITEMS_OBJECTS, DEFAULT_DEPTH_OBJECTS);

        // hand over the call context to the service object
        wrapperService.setCallContext(context);*/
        return service;
    }

    /**
     * Reads the configuration and sets up the repositories, logins, and type
     * definitions.
     */
    private void readConfiguration(Map<String, String> parameters) {

        List<String> keys = new ArrayList<String>(parameters.keySet());
        Collections.sort(keys);

        DBShareRepository fsr = new DBShareRepository("default", "/", typeManager);
        repositoryManager.addRepository(fsr);

        for (String key : keys) {
            /*if (key.startsWith(PREFIX_LOGIN)) {
                // get logins
                String usernameAndPassword = replaceSystemProperties(parameters.get(key));
                if (usernameAndPassword == null) {
                    continue;
                }

                String username = usernameAndPassword;
                String password = "";

                int x = usernameAndPassword.indexOf(':');
                if (x > -1) {
                    username = usernameAndPassword.substring(0, x);
                    password = usernameAndPassword.substring(x + 1);
                }

                LOG.info("Adding login '{}'.", username);

                userManager.addLogin(username, password);
            } else */
            if (key.startsWith(PREFIX_TYPE)) {
                // load type definition
                String typeFile = replaceSystemProperties(parameters.get(key).trim());
                if (typeFile.length() == 0) {
                    continue;
                }

                LOG.info("Loading type definition: {}", typeFile);

                if (typeFile.charAt(0) == '/') {
                    try {
                        typeManager.loadTypeDefinitionFromResource(typeFile);
                        continue;
                    } catch (IllegalArgumentException e) {
                        // resource not found -> try it as a regular file
                    } catch (IOException e) {
                        LOG.warn("Could not load type defintion from resource '{}': {}", typeFile, e.getMessage(), e);
                        continue;
                    } catch (XMLStreamException e) {
                        LOG.warn("Could not load type defintion from resource '{}': {}", typeFile, e.getMessage(), e);
                        continue;
                    }
                }

                try {
                    typeManager.loadTypeDefinitionFromFile(typeFile);
                } catch (IOException e) {
                    LOG.warn("Could not load type defintion from file '{}': {}", typeFile, e.getMessage(), e);
                } catch (XMLStreamException e) {
                    LOG.warn("Could not load type defintion from file '{}': {}", typeFile, e.getMessage(), e);
                }
            } else if (key.startsWith(PREFIX_REPOSITORY)) {
                // configure repositories
                String repositoryId = key.substring(PREFIX_REPOSITORY.length()).trim();
                int x = repositoryId.lastIndexOf('.');
                if (x > 0) {
                    repositoryId = repositoryId.substring(0, x);
                }

                if (repositoryId.length() == 0) {
                    throw new IllegalArgumentException("No repository id!");
                }

                /*if (key.endsWith(SUFFIX_READWRITE)) {
                    // read-write users
                    DBShareRepository fsr = repositoryManager.getRepository(repositoryId);
                    for (String user : split(parameters.get(key))) {
                        fsr.setUserReadWrite(replaceSystemProperties(user));
                    }
                } else if (key.endsWith(SUFFIX_READONLY)) {
                    // read-only users
                    DBShareRepository fsr = repositoryManager.getRepository(repositoryId);
                    for (String user : split(parameters.get(key))) {
                        fsr.setUserReadOnly(replaceSystemProperties(user));
                    }
                } else {*/
                // new repository
                /*String root = replaceSystemProperties(parameters.get(key));

                    LOG.info("Adding repository '{}': {}", repositoryId, root);*/

 /*DBShareRepository fsr = new DBShareRepository(repositoryId, "/", typeManager);
                    repositoryManager.addRepository(fsr);*/
                //}
            }
        }
    }

    /**
     * Finds all substrings in curly braces and replaces them with the value of
     * the corresponding system property.
     */
    private String replaceSystemProperties(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder result = new StringBuilder(128);
        StringBuilder property = null;
        boolean inProperty = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (inProperty) {
                if (c == '}') {
                    String value = System.getProperty(property.toString());
                    if (value != null) {
                        result.append(value);
                    }
                    inProperty = false;
                } else {
                    property.append(c);
                }
            } else if (c == '{') {
                property = new StringBuilder(32);
                inProperty = true;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
