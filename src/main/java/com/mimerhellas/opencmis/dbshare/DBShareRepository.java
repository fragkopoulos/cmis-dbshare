/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimerhellas.opencmis.dbshare;

import com.mimerhellas.opencmis.dbshare.controller.DatablockJpaController;
import com.mimerhellas.opencmis.dbshare.controller.FsblockJpaController;
import com.mimerhellas.opencmis.dbshare.controller.exceptions.RollbackFailureException;
import com.mimerhellas.opencmis.dbshare.entity.Datablock;
import com.mimerhellas.opencmis.dbshare.entity.Fsblock;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.chemistry.opencmis.commons.BasicPermissions;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.PermissionMapping;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CapabilityAcl;
import org.apache.chemistry.opencmis.commons.enums.CapabilityChanges;
import org.apache.chemistry.opencmis.commons.enums.CapabilityContentStreamUpdates;
import org.apache.chemistry.opencmis.commons.enums.CapabilityJoin;
import org.apache.chemistry.opencmis.commons.enums.CapabilityOrderBy;
import org.apache.chemistry.opencmis.commons.enums.CapabilityQuery;
import org.apache.chemistry.opencmis.commons.enums.CapabilityRenditions;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.enums.SupportedPermissions;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisStorageException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisStreamNotSupportedException;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;
import org.apache.chemistry.opencmis.commons.impl.MimeTypes;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlPrincipalDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AclCapabilitiesDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AllowableActionsImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CreatablePropertyTypesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.FailedToDeleteDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.NewTypeSettableAttributesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderContainerImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectParentDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PartialContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PermissionDefinitionDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PermissionMappingDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyBooleanImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDateTimeImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDecimalImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyHtmlImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIdImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyUriImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryCapabilitiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryInfoImpl;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.ObjectInfoHandler;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.server.impl.ServerVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kostas
 */
public class DBShareRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DBShareRepository.class);
    private static final String ROOT_ID = "ROOT";
    private static final String USER_UNKNOWN = "<unknown>";

    private final FsblockJpaController fsblockFacade = new FsblockJpaController();
    private final DatablockJpaController datablockJpaController = new DatablockJpaController();

    /**
     * Repository id.
     */
    private final String repositoryId;
    /**
     * Root directory.
     */
    private final Fsblock root;

    /**
     * Types.
     */
    private final DBShareTypeManager typeManager;
    /**
     * Users.
     */
    private final Map<String, Boolean> readWriteUserMap;

    /**
     * CMIS 1.0 repository info.
     */
    private final RepositoryInfo repositoryInfo10;
    /**
     * CMIS 1.1 repository info.
     */
    private final RepositoryInfo repositoryInfo11;

    public DBShareRepository(final String repositoryId, final String rootPath, final DBShareTypeManager typeManager) {

        // check repository id
        if (repositoryId == null || repositoryId.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid repository id!");
        }

        this.repositoryId = repositoryId;

        // check root folder
        if (rootPath == null || rootPath.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid root folder!");
        }

        root = fsblockFacade.findByPath(rootPath);
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("Root is not a directory!");
        }

        // set type manager objects
        this.typeManager = typeManager;

        // set up read-write user map
        readWriteUserMap = new HashMap<String, Boolean>();

        // set up repository infos
        repositoryInfo10 = createRepositoryInfo(CmisVersion.CMIS_1_0);
        repositoryInfo11 = createRepositoryInfo(CmisVersion.CMIS_1_1);
    }

    /**
     * @return the repositoryId
     */
    public String getRepositoryId() {
        return repositoryId;
    }

    /**
     * Returns the root directory of this repository
     *
     * @return
     */
    public Fsblock getRootDirectory() {
        return root;
    }

    private RepositoryInfo createRepositoryInfo(CmisVersion cmisVersion) {
        assert cmisVersion != null;

        RepositoryInfoImpl repositoryInfo = new RepositoryInfoImpl();

        repositoryInfo.setId(repositoryId);
        repositoryInfo.setName(repositoryId);
        repositoryInfo.setDescription(repositoryId);

        repositoryInfo.setCmisVersionSupported(cmisVersion.value());

        repositoryInfo.setProductName("OpenCMIS DBShare");
        repositoryInfo.setProductVersion(ServerVersion.OPENCMIS_VERSION);
        repositoryInfo.setVendorName("OpenCMIS");

        repositoryInfo.setRootFolder(ROOT_ID);

        repositoryInfo.setThinClientUri("");
        repositoryInfo.setChangesIncomplete(true);

        RepositoryCapabilitiesImpl capabilities = new RepositoryCapabilitiesImpl();
        capabilities.setCapabilityAcl(CapabilityAcl.DISCOVER);
        capabilities.setAllVersionsSearchable(false);
        capabilities.setCapabilityJoin(CapabilityJoin.NONE);
        capabilities.setSupportsMultifiling(false);
        capabilities.setSupportsUnfiling(false);
        capabilities.setSupportsVersionSpecificFiling(false);
        capabilities.setIsPwcSearchable(false);
        capabilities.setIsPwcUpdatable(false);
        capabilities.setCapabilityQuery(CapabilityQuery.NONE);
        capabilities.setCapabilityChanges(CapabilityChanges.NONE);
        capabilities.setCapabilityContentStreamUpdates(CapabilityContentStreamUpdates.ANYTIME);
        capabilities.setSupportsGetDescendants(true);
        capabilities.setSupportsGetFolderTree(true);
        capabilities.setCapabilityRendition(CapabilityRenditions.NONE);

        if (cmisVersion != CmisVersion.CMIS_1_0) {
            capabilities.setCapabilityOrderBy(CapabilityOrderBy.COMMON);

            NewTypeSettableAttributesImpl typeSetAttributes = new NewTypeSettableAttributesImpl();
            typeSetAttributes.setCanSetControllableAcl(false);
            typeSetAttributes.setCanSetControllablePolicy(false);
            typeSetAttributes.setCanSetCreatable(false);
            typeSetAttributes.setCanSetDescription(false);
            typeSetAttributes.setCanSetDisplayName(false);
            typeSetAttributes.setCanSetFileable(false);
            typeSetAttributes.setCanSetFulltextIndexed(false);
            typeSetAttributes.setCanSetId(false);
            typeSetAttributes.setCanSetIncludedInSupertypeQuery(false);
            typeSetAttributes.setCanSetLocalName(false);
            typeSetAttributes.setCanSetLocalNamespace(false);
            typeSetAttributes.setCanSetQueryable(false);
            typeSetAttributes.setCanSetQueryName(false);

            capabilities.setNewTypeSettableAttributes(typeSetAttributes);

            CreatablePropertyTypesImpl creatablePropertyTypes = new CreatablePropertyTypesImpl();
            capabilities.setCreatablePropertyTypes(creatablePropertyTypes);
        }

        repositoryInfo.setCapabilities(capabilities);

        AclCapabilitiesDataImpl aclCapability = new AclCapabilitiesDataImpl();
        aclCapability.setSupportedPermissions(SupportedPermissions.BASIC);
        aclCapability.setAclPropagation(AclPropagation.OBJECTONLY);

        // permissions
        List<PermissionDefinition> permissions = new ArrayList<PermissionDefinition>();
        permissions.add(createPermission(BasicPermissions.READ, "Read"));
        permissions.add(createPermission(BasicPermissions.WRITE, "Write"));
        permissions.add(createPermission(BasicPermissions.ALL, "All"));
        aclCapability.setPermissionDefinitionData(permissions);

        // mapping
        List<PermissionMapping> list = new ArrayList<PermissionMapping>();
        list.add(createMapping(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_CREATE_FOLDER_FOLDER, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT, BasicPermissions.WRITE));
        list.add(createMapping(PermissionMapping.CAN_DELETE_OBJECT, BasicPermissions.ALL));
        list.add(createMapping(PermissionMapping.CAN_DELETE_TREE_FOLDER, BasicPermissions.ALL));
        list.add(createMapping(PermissionMapping.CAN_GET_ACL_OBJECT, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_GET_ALL_VERSIONS_VERSION_SERIES, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_GET_CHILDREN_FOLDER, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_GET_FOLDER_PARENT_OBJECT, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_GET_PARENTS_FOLDER, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_GET_PROPERTIES_OBJECT, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_MOVE_OBJECT, BasicPermissions.WRITE));
        list.add(createMapping(PermissionMapping.CAN_MOVE_SOURCE, BasicPermissions.READ));
        list.add(createMapping(PermissionMapping.CAN_MOVE_TARGET, BasicPermissions.WRITE));
        list.add(createMapping(PermissionMapping.CAN_SET_CONTENT_DOCUMENT, BasicPermissions.WRITE));
        list.add(createMapping(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT, BasicPermissions.WRITE));
        list.add(createMapping(PermissionMapping.CAN_VIEW_CONTENT_OBJECT, BasicPermissions.READ));
        Map<String, PermissionMapping> map = new LinkedHashMap<String, PermissionMapping>();
        for (PermissionMapping pm : list) {
            map.put(pm.getKey(), pm);
        }
        aclCapability.setPermissionMappingData(map);

        repositoryInfo.setAclCapabilities(aclCapability);

        return repositoryInfo;
    }

    /**
     * CMIS getRepositoryInfo.
     *
     * @param context
     * @return
     */
    public RepositoryInfo getRepositoryInfo(CallContext context) {
        debug("getRepositoryInfo");

        //checkUser(context, false);
        if (context.getCmisVersion() == CmisVersion.CMIS_1_0) {
            return repositoryInfo10;
        } else {
            return repositoryInfo11;
        }
    }

    private PermissionDefinition createPermission(String permission, String description) {
        PermissionDefinitionDataImpl pd = new PermissionDefinitionDataImpl();
        pd.setId(permission);
        pd.setDescription(description);

        return pd;
    }

    private PermissionMapping createMapping(String key, String permission) {
        PermissionMappingDataImpl pm = new PermissionMappingDataImpl();
        pm.setKey(key);
        pm.setPermissions(Collections.singletonList(permission));

        return pm;
    }

    private void debug(String msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("<{}> {}", repositoryId, msg);
        }
    }

    /**
     * Create* dispatch for AtomPub.
     *
     * @param context
     * @param properties
     * @param folderId
     * @param contentStream
     * @param versioningState
     * @param objectInfos
     * @return
     */
    public ObjectData create(CallContext context, Properties properties, String folderId, ContentStream contentStream,
            VersioningState versioningState, ObjectInfoHandler objectInfos) {
        debug("create");
        boolean userReadOnly = false; //checkUser(context, true);

        String typeId = DBShareUtils.getObjectTypeId(properties);
        TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
        if (type == null) {
            throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
        }

        String objectId = null;
        if (null != type.getBaseTypeId()) {
            switch (type.getBaseTypeId()) {
                case CMIS_DOCUMENT:
                    objectId = createDocument(context, properties, folderId, contentStream, versioningState);
                    break;
                case CMIS_FOLDER:
                    if (contentStream != null || versioningState != null) {
                        throw new CmisInvalidArgumentException("Cannot create a folder with content or a versioning state!");
                    }
                    objectId = createFolder(context, properties, folderId);
                    break;
                default:
                    throw new CmisObjectNotFoundException("Cannot create object of type '" + typeId + "'!");
            }
        }

        return compileObjectData(context, getFile(objectId), null, false, false, userReadOnly, objectInfos);
    }

    /**
     * CMIS createFolder.
     *
     * @param context
     * @param properties
     * @param folderId
     * @return
     */
    public String createFolder(CallContext context, Properties properties, String folderId) {
        debug("createFolder");
        //checkUser(context, true);

        // check properties
        if (properties == null || properties.getProperties() == null) {
            throw new CmisInvalidArgumentException("Properties must be set!");
        }

        // check type
        String typeId = DBShareUtils.getObjectTypeId(properties);
        TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
        if (type == null) {
            throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
        }
        if (type.getBaseTypeId() != BaseTypeId.CMIS_FOLDER) {
            throw new CmisInvalidArgumentException("Type must be a folder type!");
        }

        // compile the properties
        PropertiesImpl props = compileWriteProperties(typeId, context.getUsername(), context.getUsername(), properties);

        // check the name
        String name = DBShareUtils.getStringProperty(properties, PropertyIds.NAME);
        if (!isValidName(name)) {
            throw new CmisNameConstraintViolationException("Name is not valid.");
        }

        // get parent File
        Fsblock parent = getFile(folderId);
        if (!parent.isDirectory()) {
            throw new CmisObjectNotFoundException("Parent is not a folder!");
        }

        // create the folder
        Fsblock newFolder;
        try {
            newFolder = fsblockFacade.createFolder(parent, name);
            /*File newFolder = new File(parent, name);
            if (!newFolder.mkdir()) {
            throw new CmisStorageException("Could not create folder!");
            }*/
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new CmisStorageException("Could not create folder!");
        }

        // set creation date
        addPropertyDateTime(props, typeId, null, PropertyIds.CREATION_DATE,
                DBShareUtils.millisToCalendar(newFolder.getFslastmodtime()));

        // write properties
        //writePropertiesFile(newFolder, props);
        return newFolder.getFsuuid(); // getId(newFolder);
    }

    /**
     * CMIS createDocument.
     *
     * @param context
     * @param properties
     * @param folderId
     * @param contentStream
     * @param versioningState
     * @return
     */
    public String createDocument(CallContext context, Properties properties, String folderId,
            ContentStream contentStream, VersioningState versioningState) {
        debug("createDocument");
        //checkUser(context, true);

        // check properties
        if (properties == null || properties.getProperties() == null) {
            throw new CmisInvalidArgumentException("Properties must be set!");
        }

        // check versioning state
        if (!(VersioningState.NONE == versioningState || versioningState == null)) {
            throw new CmisConstraintException("Versioning not supported!");
        }

        // check type
        String typeId = DBShareUtils.getObjectTypeId(properties);
        TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
        if (type == null) {
            throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
        }
        if (type.getBaseTypeId() != BaseTypeId.CMIS_DOCUMENT) {
            throw new CmisInvalidArgumentException("Type must be a document type!");
        }

        // compile the properties
        PropertiesImpl props = compileWriteProperties(typeId, context.getUsername(), context.getUsername(), properties);

        // check the name
        String name = DBShareUtils.getStringProperty(properties, PropertyIds.NAME);
        if (!isValidName(name)) {
            throw new CmisNameConstraintViolationException("Name is not valid!");
        }

        // get parent File
        Fsblock parent = getFile(folderId);
        if (!parent.isDirectory()) {
            throw new CmisObjectNotFoundException("Parent is not a folder!");
        }

        // check the file
        Fsblock newFile = fsblockFacade.findByNameParent(name, parent);
        //Fsblock newFile = new File(parent, name);
        if (newFile != null) {
            throw new CmisNameConstraintViolationException("Document already exists!");
        }

        try {
            // create the file
            newFile = fsblockFacade.createDocument(parent, name);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new CmisStorageException("Could not create file: " + ex.getMessage(), ex);
        }

        /*try {
            newFile.createNewFile();
        } catch (IOException e) {
            throw new CmisStorageException("Could not create file: " + e.getMessage(), e);
        }*/
        // write content, if available
        if (contentStream != null && contentStream.getStream() != null) {
            writeContent(newFile, contentStream.getStream());
        }

        // set creation date
        addPropertyDateTime(props, typeId, null, PropertyIds.CREATION_DATE,
                DBShareUtils.millisToCalendar(newFile.getFslastmodtime()));

        // write properties
        //writePropertiesFile(newFile, props);
        return newFile.getFsuuid();
    }

    /**
     * CMIS setContentStream, deleteContentStream, and appendContentStream.
     *
     * @param context
     * @param objectId
     * @param overwriteFlag
     * @param contentStream
     * @param append
     */
    public void changeContentStream(CallContext context, Holder<String> objectId, Boolean overwriteFlag,
            ContentStream contentStream, boolean append) {
        debug("setContentStream or deleteContentStream or appendContentStream");
        //checkUser(context, true);

        if (objectId == null) {
            throw new CmisInvalidArgumentException("Id is not valid!");
        }

        // get the file
        Fsblock file = getFile(objectId.getValue());
        if (!file.isFile()) {
            throw new CmisStreamNotSupportedException("Not a file!");
        }

        // check overwrite
        boolean owf = DBShareUtils.getBooleanParameter(overwriteFlag, true);
        if (!owf && file.getFsfilesize() > 0) {
            throw new CmisContentAlreadyExistsException("Content already exists!");
        }

        writeContent(file, contentStream.getStream());
        /*OutputStream out = null;
        InputStream in = null;
        try {
            out = new FileOutputStream(file, append);

            if (contentStream == null || contentStream.getStream() == null) {
                // delete content
                out.write(new byte[0]);
            } else {
                // set content
                in = contentStream.getStream();
                IOUtils.copy(in, out, BUFFER_SIZE);
            }
        } catch (Exception e) {
            throw new CmisStorageException("Could not write content: " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }*/
    }

    /**
     * CMIS createDocumentFromSource.
     */
    /*public String createDocumentFromSource(CallContext context, String sourceId, Properties properties,
            String folderId, VersioningState versioningState) {
        debug("createDocumentFromSource");
        //checkUser(context, true);

        // check versioning state
        if (!(VersioningState.NONE == versioningState || versioningState == null)) {
            throw new CmisConstraintException("Versioning not supported!");
        }

        // get parent File
        Fsblock parent = getFile(folderId);
        if (!parent.isDirectory()) {
            throw new CmisObjectNotFoundException("Parent is not a folder!");
        }

        // get source File
        Fsblock source = getFile(sourceId);
        if (!source.isFile()) {
            throw new CmisObjectNotFoundException("Source is not a document!");
        }

        // file name
        String name = source.getFsname();

        // get properties
        PropertiesImpl sourceProperties = new PropertiesImpl();
        //readCustomProperties(source, sourceProperties, null, new ObjectInfoImpl());

        // get the type id
        String typeId = DBShareUtils.getIdProperty(sourceProperties, PropertyIds.OBJECT_TYPE_ID);
        if (typeId == null) {
            typeId = BaseTypeId.CMIS_DOCUMENT.value();
        }

        // copy properties
        PropertiesImpl newProperties = new PropertiesImpl();
        for (PropertyData<?> prop : sourceProperties.getProperties().values()) {
            if (prop.getId().equals(PropertyIds.OBJECT_TYPE_ID) || prop.getId().equals(PropertyIds.CREATED_BY)
                    || prop.getId().equals(PropertyIds.CREATION_DATE)
                    || prop.getId().equals(PropertyIds.LAST_MODIFIED_BY)) {
                continue;
            }

            newProperties.addProperty(prop);
        }

        // replace properties
        if (properties != null) {
            // find new name
            String newName = DBShareUtils.getStringProperty(properties, PropertyIds.NAME);
            if (newName != null) {
                if (!isValidName(newName)) {
                    throw new CmisNameConstraintViolationException("Name is not valid!");
                }
                name = newName;
            }

            // get the property definitions
            TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
            if (type == null) {
                throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
            }
            if (type.getBaseTypeId() != BaseTypeId.CMIS_DOCUMENT) {
                throw new CmisInvalidArgumentException("Type must be a document type!");
            }

            // replace with new values
            for (PropertyData<?> prop : properties.getProperties().values()) {
                PropertyDefinition<?> propType = type.getPropertyDefinitions().get(prop.getId());

                // do we know that property?
                if (propType == null) {
                    throw new CmisConstraintException("Property '" + prop.getId() + "' is unknown!");
                }

                // can it be set?
                if (propType.getUpdatability() != Updatability.READWRITE) {
                    throw new CmisConstraintException("Property '" + prop.getId() + "' cannot be updated!");
                }

                // empty properties are invalid
                if (isEmptyProperty(prop)) {
                    throw new CmisConstraintException("Property '" + prop.getId() + "' must not be empty!");
                }

                newProperties.addProperty(prop);
            }
        }

        addPropertyId(newProperties, typeId, null, PropertyIds.OBJECT_TYPE_ID, typeId);
        addPropertyString(newProperties, typeId, null, PropertyIds.CREATED_BY, context.getUsername());
        addPropertyDateTime(newProperties, typeId, null, PropertyIds.CREATION_DATE,
                DBShareUtils.millisToCalendar(System.currentTimeMillis()));
        addPropertyString(newProperties, typeId, null, PropertyIds.LAST_MODIFIED_BY, context.getUsername());

        // check the file
        Fsblock newFile = fsblockFacade.findByNameParent(name, parent);
        if (newFile != null) {
            throw new CmisNameConstraintViolationException("Document already exists.");
        }

        // create the file
        try {
            // create the file
            newFile = fsblockFacade.createDocument(parent, name);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new CmisStorageException("Could not create file: " + ex.getMessage(), ex);
        }

        writeContent(newFile, new FileInputStream(source));

        // write properties
        //writePropertiesFile(newFile, newProperties);
        return newFile.getFsuuid();
    }*/
    /**
     * Writes the content to disc.
     */
    private void writeContent(Fsblock newFile, InputStream stream) {

        try {
            Datablock datablock = new Datablock();
            datablock.setFsblockid(newFile.getFsid());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            datablock.setDrawbindata(buffer.toByteArray());

            datablockJpaController.create(datablock);
            newFile.setFsfilesize(new Long(datablock.getDrawbindata().length));
            fsblockFacade.edit(newFile);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new CmisStorageException("Could not write content: " + ex.getMessage(), ex);
        } catch (RollbackFailureException ex) {
            java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new CmisStorageException("Could not write content: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new CmisStorageException("Could not write content: " + ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * CMIS getContentStream.
     *
     * @param context
     * @param objectId
     * @param offset
     * @param length
     * @return
     */
    public ContentStream getContentStream(CallContext context, String objectId, BigInteger offset, BigInteger length) {
        debug("getContentStream");
        //checkUser(context, false);

        // get the file
        final Fsblock file = getFile(objectId);
        if (!file.isFile()) {
            throw new CmisStreamNotSupportedException("Not a file!");
        }

        if (file.getFsfilesize() == 0) {
            throw new CmisConstraintException("Document has no content!");
        }

        List<Datablock> datablocks = datablockJpaController.findByFsblockid(file.getFsid());
        if (datablocks.isEmpty()) {
            throw new CmisConstraintException("Document has no content!");
        }
        InputStream stream = new ByteArrayInputStream(datablocks.get(0).getDrawbindata());

        // compile data
        ContentStreamImpl result;
        if ((offset != null && offset.longValue() > 0) || length != null) {
            result = new PartialContentStreamImpl();
        } else {
            result = new ContentStreamImpl();
        }

        result.setFileName(file.getFsname());
        result.setLength(BigInteger.valueOf(file.getFsfilesize()));
        result.setMimeType(MimeTypes.getMIMEType(file.getFsname()));
        result.setStream(stream);

        return result;
    }

    /**
     * CMIS deleteObject.
     *
     * @param context
     * @param objectId
     */
    public void deleteObject(CallContext context, String objectId) {
        debug("deleteObject");
        //checkUser(context, true);

        // get the file or folder
        Fsblock file = getFile(objectId);
        if (file == null) {
            throw new CmisObjectNotFoundException("Object not found!");
        }

        // check if it is a folder and if it is empty
        if (file.isDirectory() && !file.getFsblockList().isEmpty()) {
            throw new CmisConstraintException("Folder is not empty!");
        }

        try {
            // delete properties and actual file
            //getPropertiesFile(file).delete();
            /*if (!file.delete()) {
            throw new CmisStorageException("Deletion failed!");
            }*/
            fsblockFacade.destroy(file.getFsid());
        } catch (RollbackFailureException ex) {
            java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new CmisStorageException("Deletion failed! " + ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
            throw new CmisStorageException("Deletion failed! " + ex);
        }
    }

    /**
     * CMIS deleteTree.
     *
     * @param context
     * @param folderId
     * @param continueOnFailure
     * @return
     */
    public FailedToDeleteData deleteTree(CallContext context, String folderId, Boolean continueOnFailure) {
        debug("deleteTree");
        //checkUser(context, true);

        boolean cof = DBShareUtils.getBooleanParameter(continueOnFailure, false);

        // get the file or folder
        Fsblock file = getFile(folderId);

        FailedToDeleteDataImpl result = new FailedToDeleteDataImpl();
        result.setIds(new ArrayList<String>());

        // if it is a folder, remove it recursively
        if (file.isDirectory()) {
            try {
                fsblockFacade.destroy(file.getFsid());
            } catch (RollbackFailureException ex) {
                java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new CmisStorageException("Deletion failed! " + ex);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(DBShareRepository.class.getName()).log(Level.SEVERE, null, ex);
                throw new CmisStorageException("Deletion failed! " + ex);
            }
            //deleteFolder(file, cof, result);
            //deleteFolder(file, cof, result);
        } else {
            throw new CmisConstraintException("Object is not a folder!");
        }

        return result;
    }

    /**
     * CMIS getObject.
     *
     * @param context
     * @param objectId
     * @param versionServicesId
     * @param filter
     * @param includeAllowableActions
     * @param includeAcl
     * @param objectInfos
     * @return
     */
    public ObjectData getObject(CallContext context, String objectId, String versionServicesId, String filter,
            Boolean includeAllowableActions, Boolean includeAcl, ObjectInfoHandler objectInfos) {
        debug("getObject");
        boolean userReadOnly = false;//checkUser(context, false);

        // check id
        if (objectId == null && versionServicesId == null) {
            throw new CmisInvalidArgumentException("Object Id must be set.");
        }

        if (objectId == null) {
            // this works only because there are no versions in a file system
            // and the object id and version series id are the same
            objectId = versionServicesId;
        }

        // get the file or folder
        Fsblock file = getFile(objectId);

        // set defaults if values not set
        boolean iaa = DBShareUtils.getBooleanParameter(includeAllowableActions, false);
        boolean iacl = DBShareUtils.getBooleanParameter(includeAcl, false);

        // split filter
        Set<String> filterCollection = DBShareUtils.splitFilter(filter);

        // gather properties
        return compileObjectData(context, file, filterCollection, iaa, iacl, userReadOnly, objectInfos);
    }

    /**
     * CMIS getObjectByPath.
     *
     * @param context
     * @param folderPath
     * @param filter
     * @param includeAllowableActions
     * @param includeACL
     * @param objectInfos
     * @return
     */
    public ObjectData getObjectByPath(CallContext context, String folderPath, String filter,
            boolean includeAllowableActions, boolean includeACL, ObjectInfoHandler objectInfos) {
        debug("getObjectByPath");
        boolean userReadOnly = false; //checkUser(context, false);

        // split filter
        Set<String> filterCollection = DBShareUtils.splitFilter(filter);

        // check path
        if (folderPath == null || folderPath.length() == 0 || folderPath.charAt(0) != '/') {
            throw new CmisInvalidArgumentException("Invalid folder path!");
        }

        // get the file or folder
        Fsblock file = fsblockFacade.findByPath(folderPath);
        /*Fsblock file = null;
        if (folderPath.length() == 1) {
            file = root;
        } else {
            String path = folderPath.replace('/', File.separatorChar).substring(1);
            file = new File(root, path);
        }*/

        if (file == null) {
            throw new CmisObjectNotFoundException("Path doesn't exist.");
        }

        return compileObjectData(context, file, filterCollection, includeAllowableActions, includeACL, userReadOnly,
                objectInfos);
    }

    /**
     * CMIS getFolderParent.
     *
     * @param context
     * @param folderId
     * @param filter
     * @param objectInfos
     * @return
     */
    public ObjectData getFolderParent(CallContext context, String folderId, String filter, ObjectInfoHandler objectInfos) {
        List<ObjectParentData> parents = getObjectParents(context, folderId, filter, false, false, objectInfos);

        if (parents.isEmpty()) {
            throw new CmisInvalidArgumentException("The root folder has no parent!");
        }

        return parents.get(0).getObject();
    }

    /**
     * CMIS getObjectParents.
     *
     * @param context
     * @param objectId
     * @param filter
     * @param includeAllowableActions
     * @param includeRelativePathSegment
     * @param objectInfos
     * @return
     */
    public List<ObjectParentData> getObjectParents(CallContext context, String objectId, String filter,
            Boolean includeAllowableActions, Boolean includeRelativePathSegment, ObjectInfoHandler objectInfos) {
        debug("getObjectParents");
        boolean userReadOnly = false; //checkUser(context, false);

        // split filter
        Set<String> filterCollection = DBShareUtils.splitFilter(filter);

        // set defaults if values not set
        boolean iaa = DBShareUtils.getBooleanParameter(includeAllowableActions, false);
        boolean irps = DBShareUtils.getBooleanParameter(includeRelativePathSegment, false);

        // get the file or folder
        Fsblock file = getFile(objectId);

        // don't climb above the root folder
        if (root.equals(file)) {
            return Collections.emptyList();
        }

        // set object info of the the object
        if (context.isObjectInfoRequired()) {
            compileObjectData(context, file, null, false, false, userReadOnly, objectInfos);
        }

        // get parent folder
        Fsblock parent = file.getFsparent();
        ObjectData object = compileObjectData(context, parent, filterCollection, iaa, false, userReadOnly, objectInfos);

        ObjectParentDataImpl result = new ObjectParentDataImpl();
        result.setObject(object);
        if (irps) {
            result.setRelativePathSegment(file.getFsname());
        }

        return Collections.<ObjectParentData>singletonList(result);
    }

    /**
     * CMIS getChildren.
     *
     * @param context
     * @param folderId
     * @param filter
     * @param orderBy
     * @param includeAllowableActions
     * @param includePathSegment
     * @param maxItems
     * @param skipCount
     * @param objectInfos
     * @return
     */
    public ObjectInFolderList getChildren(CallContext context, String folderId, String filter, String orderBy,
            Boolean includeAllowableActions, Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount,
            ObjectInfoHandler objectInfos) {
        debug("getChildren");
        boolean userReadOnly = false; //checkUser(context, false);

        // split filter
        Set<String> filterCollection = DBShareUtils.splitFilter(filter);

        // set defaults if values not set
        boolean iaa = DBShareUtils.getBooleanParameter(includeAllowableActions, false);
        boolean ips = DBShareUtils.getBooleanParameter(includePathSegment, false);

        // skip and max
        int skip = skipCount == null ? 0 : skipCount.intValue();
        if (skip < 0) {
            skip = 0;
        }

        int max = maxItems == null ? Integer.MAX_VALUE : maxItems.intValue();
        if (max < 0) {
            max = Integer.MAX_VALUE;
        }

        // get the folder
        Fsblock folder = getFile(folderId);
        if (!folder.isDirectory()) {
            throw new CmisObjectNotFoundException("Not a folder!");
        }

        // get the children
        List<Fsblock> children = new ArrayList<Fsblock>();
        for (Fsblock child : folder.getFsblockList()) {
            // skip hidden and shadow files
            /*if (child.isHidden() || child.getName().equals(SHADOW_FOLDER) || child.getPath().endsWith(SHADOW_EXT)) {
                continue;
            }*/

            children.add(child);
        }

        /*// very basic sorting
        if (orderBy != null) {
            boolean desc = false;
            String queryName = orderBy;

            int commaIdx = orderBy.indexOf(',');
            if (commaIdx > -1) {
                queryName = orderBy.substring(0, commaIdx);
            }

            queryName = queryName.trim();
            if (queryName.toLowerCase(Locale.ENGLISH).endsWith(" desc")) {
                desc = true;
                queryName = queryName.substring(0, queryName.length() - 5).trim();
            }

            Comparator<File> comparator = null;

            if ("cmis:name".equals(queryName)) {
                comparator = new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return f1.getName().toLowerCase(Locale.ENGLISH)
                                .compareTo(f2.getName().toLowerCase(Locale.ENGLISH));
                    }
                };
            } else if ("cmis:creationDate".equals(queryName) || "cmis:lastModificationDate".equals(queryName)) {
                comparator = new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return Long.compare(f1.lastModified(), f2.lastModified());
                    }
                };
            } else if ("cmis:contentStreamLength".equals(queryName)) {
                comparator = new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return Long.compare(f1.length(), f2.length());
                    }
                };
            } else if ("cmis:objectId".equals(queryName)) {
                comparator = new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        try {
                            return fileToId(f1).compareTo(fileToId(f2));
                        } catch (IOException e) {
                            return 0;
                        }
                    }
                };
            } else if ("cmis:baseTypeId".equals(queryName)) {
                comparator = new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        if (f1.isDirectory() == f2.isDirectory()) {
                            return 0;
                        }
                        return f1.isDirectory() ? -1 : 1;
                    }
                };
            } else if ("cmis:createdBy".equals(queryName) || "cmis:lastModifiedBy".equals(queryName)) {
                // do nothing
            } else {
                throw new CmisInvalidArgumentException("Cannot sort by " + queryName + ".");
            }

            if (comparator != null) {
                Collections.sort(children, comparator);
                if (desc) {
                    Collections.reverse(children);
                }
            }
        }*/
        // set object info of the the folder
        if (context.isObjectInfoRequired()) {
            compileObjectData(context, folder, null, false, false, userReadOnly, objectInfos);
        }

        // prepare result
        ObjectInFolderListImpl result = new ObjectInFolderListImpl();
        result.setObjects(new ArrayList<ObjectInFolderData>());
        result.setHasMoreItems(false);
        int count = 0;

        // iterate through children
        for (Fsblock child : children) {
            count++;

            if (skip > 0) {
                skip--;
                continue;
            }

            if (result.getObjects().size() >= max) {
                result.setHasMoreItems(true);
                continue;
            }

            // build and add child object
            ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
            objectInFolder.setObject(compileObjectData(context, child, filterCollection, iaa, false, userReadOnly,
                    objectInfos));
            if (ips) {
                objectInFolder.setPathSegment(child.getFsname());
            }

            result.getObjects().add(objectInFolder);
        }

        result.setNumItems(BigInteger.valueOf(count));

        return result;
    }

    /**
     * CMIS getDescendants.
     *
     * @param context
     * @param folderId
     * @param depth
     * @param filter
     * @param includeAllowableActions
     * @param includePathSegment
     * @param objectInfos
     * @param foldersOnly
     * @return
     */
    public List<ObjectInFolderContainer> getDescendants(CallContext context, String folderId, BigInteger depth,
            String filter, Boolean includeAllowableActions, Boolean includePathSegment, ObjectInfoHandler objectInfos,
            boolean foldersOnly) {
        debug("getDescendants or getFolderTree");
        boolean userReadOnly = false; //checkUser(context, false);

        // check depth
        int d = depth == null ? 2 : depth.intValue();
        if (d == 0) {
            throw new CmisInvalidArgumentException("Depth must not be 0!");
        }
        if (d < -1) {
            d = -1;
        }

        // split filter
        Set<String> filterCollection = DBShareUtils.splitFilter(filter);

        // set defaults if values not set
        boolean iaa = DBShareUtils.getBooleanParameter(includeAllowableActions, false);
        boolean ips = DBShareUtils.getBooleanParameter(includePathSegment, false);

        // get the folder
        Fsblock folder = getFile(folderId);
        if (!folder.isDirectory()) {
            throw new CmisObjectNotFoundException("Not a folder!");
        }

        // set object info of the the folder
        if (context.isObjectInfoRequired()) {
            compileObjectData(context, folder, null, false, false, userReadOnly, objectInfos);
        }

        // get the tree
        List<ObjectInFolderContainer> result = new ArrayList<ObjectInFolderContainer>();
        gatherDescendants(context, folder, result, foldersOnly, d, filterCollection, iaa, ips, userReadOnly,
                objectInfos);

        return result;
    }

    /**
     * Gather the children of a folder.
     */
    private void gatherDescendants(CallContext context, Fsblock folder, List<ObjectInFolderContainer> list,
            boolean foldersOnly, int depth, Set<String> filter, boolean includeAllowableActions,
            boolean includePathSegments, boolean userReadOnly, ObjectInfoHandler objectInfos) {
        assert folder != null;
        assert list != null;

        // iterate through children
        for (Fsblock child : folder.getFsblockList()) {
            // skip hidden and shadow files
            /*if (child.isHidden() || child.getName().equals(SHADOW_FOLDER) || child.getPath().endsWith(SHADOW_EXT)) {
                continue;
            }*/

            // folders only?
            if (foldersOnly && !child.isDirectory()) {
                continue;
            }

            // add to list
            ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
            objectInFolder.setObject(compileObjectData(context, child, filter, includeAllowableActions, false,
                    userReadOnly, objectInfos));
            if (includePathSegments) {
                objectInFolder.setPathSegment(child.getFsname());
            }

            ObjectInFolderContainerImpl container = new ObjectInFolderContainerImpl();
            container.setObject(objectInFolder);

            list.add(container);

            // move to next level
            if (depth != 1 && child.isDirectory()) {
                container.setChildren(new ArrayList<ObjectInFolderContainer>());
                gatherDescendants(context, child, container.getChildren(), foldersOnly, depth - 1, filter,
                        includeAllowableActions, includePathSegments, userReadOnly, objectInfos);
            }
        }
    }

    /**
     * Compiles an object type object from a file or folder.
     */
    private ObjectData compileObjectData(CallContext context, Fsblock file, Set<String> filter,
            boolean includeAllowableActions, boolean includeAcl, boolean userReadOnly, ObjectInfoHandler objectInfos) {
        ObjectDataImpl result = new ObjectDataImpl();
        ObjectInfoImpl objectInfo = new ObjectInfoImpl();

        result.setProperties(compileProperties(context, file, filter, objectInfo));

        if (includeAllowableActions) {
            result.setAllowableActions(compileAllowableActions(file, userReadOnly));
        }

        if (includeAcl) {
            result.setAcl(compileAcl(file));
            result.setIsExactAcl(true);
        }

        if (context.isObjectInfoRequired()) {
            objectInfo.setObject(result);
            objectInfos.addObjectInfo(objectInfo);
        }

        return result;
    }

    /**
     * Gathers all base properties of a file or folder.
     */
    private Properties compileProperties(CallContext context, Fsblock file, Set<String> orgfilter,
            ObjectInfoImpl objectInfo) {
        if (file == null) {
            throw new IllegalArgumentException("File must not be null!");
        }

        // copy filter
        Set<String> filter = orgfilter == null ? null : new HashSet<String>(orgfilter);

        // find base type
        String typeId = null;

        if (file.isDirectory()) {
            typeId = BaseTypeId.CMIS_FOLDER.value();
            objectInfo.setBaseType(BaseTypeId.CMIS_FOLDER);
            objectInfo.setTypeId(typeId);
            objectInfo.setContentType(null);
            objectInfo.setFileName(null);
            objectInfo.setHasAcl(true);
            objectInfo.setHasContent(false);
            objectInfo.setVersionSeriesId(null);
            objectInfo.setIsCurrentVersion(true);
            objectInfo.setRelationshipSourceIds(null);
            objectInfo.setRelationshipTargetIds(null);
            objectInfo.setRenditionInfos(null);
            objectInfo.setSupportsDescendants(true);
            objectInfo.setSupportsFolderTree(true);
            objectInfo.setSupportsPolicies(false);
            objectInfo.setSupportsRelationships(false);
            objectInfo.setWorkingCopyId(null);
            objectInfo.setWorkingCopyOriginalId(null);
        } else {
            typeId = BaseTypeId.CMIS_DOCUMENT.value();
            objectInfo.setBaseType(BaseTypeId.CMIS_DOCUMENT);
            objectInfo.setTypeId(typeId);
            objectInfo.setHasAcl(true);
            objectInfo.setHasContent(true);
            objectInfo.setHasParent(true);
            objectInfo.setVersionSeriesId(null);
            objectInfo.setIsCurrentVersion(true);
            objectInfo.setRelationshipSourceIds(null);
            objectInfo.setRelationshipTargetIds(null);
            objectInfo.setRenditionInfos(null);
            objectInfo.setSupportsDescendants(false);
            objectInfo.setSupportsFolderTree(false);
            objectInfo.setSupportsPolicies(false);
            objectInfo.setSupportsRelationships(false);
            objectInfo.setWorkingCopyId(null);
            objectInfo.setWorkingCopyOriginalId(null);
        }

        // let's do it
        try {
            PropertiesImpl result = new PropertiesImpl();

            // id
            String id = file.getFsuuid();
            addPropertyId(result, typeId, filter, PropertyIds.OBJECT_ID, id);
            objectInfo.setId(id);

            // name
            String name = file.getFsname();
            addPropertyString(result, typeId, filter, PropertyIds.NAME, name);
            objectInfo.setName(name);

            // created and modified by
            addPropertyString(result, typeId, filter, PropertyIds.CREATED_BY, USER_UNKNOWN);
            addPropertyString(result, typeId, filter, PropertyIds.LAST_MODIFIED_BY, USER_UNKNOWN);
            objectInfo.setCreatedBy(USER_UNKNOWN);

            // creation and modification date
            GregorianCalendar lastModified = DBShareUtils.millisToCalendar(file.getFslastmodtime());
            addPropertyDateTime(result, typeId, filter, PropertyIds.CREATION_DATE, lastModified);
            addPropertyDateTime(result, typeId, filter, PropertyIds.LAST_MODIFICATION_DATE, lastModified);
            objectInfo.setCreationDate(lastModified);
            objectInfo.setLastModificationDate(lastModified);

            // change token - always null
            addPropertyString(result, typeId, filter, PropertyIds.CHANGE_TOKEN, null);

            // CMIS 1.1 properties
            if (context.getCmisVersion() != CmisVersion.CMIS_1_0) {
                addPropertyString(result, typeId, filter, PropertyIds.DESCRIPTION, null);
                addPropertyIdList(result, typeId, filter, PropertyIds.SECONDARY_OBJECT_TYPE_IDS, null);
            }

            // directory or file
            if (file.isDirectory()) {
                // base type and type name
                addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
                addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
                addPropertyString(result, typeId, filter, PropertyIds.PATH, file.getFspath());

                // folder properties
                if (!root.equals(file)) {
                    addPropertyId(result, typeId, filter, PropertyIds.PARENT_ID,
                            (root.equals(file.getFsparent()) ? ROOT_ID : file.getFsparent().getFsuuid()));
                    objectInfo.setHasParent(true);
                } else {
                    addPropertyId(result, typeId, filter, PropertyIds.PARENT_ID, null);
                    objectInfo.setHasParent(false);
                }

                addPropertyIdList(result, typeId, filter, PropertyIds.ALLOWED_CHILD_OBJECT_TYPE_IDS, null);
            } else {
                // base type and type name
                addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
                addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());

                // file properties
                addPropertyBoolean(result, typeId, filter, PropertyIds.IS_IMMUTABLE, false);
                addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_VERSION, true);
                addPropertyBoolean(result, typeId, filter, PropertyIds.IS_MAJOR_VERSION, true);
                addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_MAJOR_VERSION, true);
                addPropertyString(result, typeId, filter, PropertyIds.VERSION_LABEL, file.getFsname());
                addPropertyId(result, typeId, filter, PropertyIds.VERSION_SERIES_ID, file.getFsuuid());
                addPropertyBoolean(result, typeId, filter, PropertyIds.IS_VERSION_SERIES_CHECKED_OUT, false);
                addPropertyString(result, typeId, filter, PropertyIds.VERSION_SERIES_CHECKED_OUT_BY, null);
                addPropertyString(result, typeId, filter, PropertyIds.VERSION_SERIES_CHECKED_OUT_ID, null);
                addPropertyString(result, typeId, filter, PropertyIds.CHECKIN_COMMENT, "");
                if (context.getCmisVersion() != CmisVersion.CMIS_1_0) {
                    addPropertyBoolean(result, typeId, filter, PropertyIds.IS_PRIVATE_WORKING_COPY, false);
                }

                if (file.getFsfilesize() == 0) {
                    addPropertyBigInteger(result, typeId, filter, PropertyIds.CONTENT_STREAM_LENGTH, null);
                    addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_MIME_TYPE, null);
                    addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_FILE_NAME, null);

                    objectInfo.setHasContent(false);
                    objectInfo.setContentType(null);
                    objectInfo.setFileName(null);
                } else {
                    addPropertyInteger(result, typeId, filter, PropertyIds.CONTENT_STREAM_LENGTH, file.getFsfilesize());
                    addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_MIME_TYPE,
                            MimeTypes.getMIMEType(file.getFsname()));
                    addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_FILE_NAME, file.getFsname());

                    objectInfo.setHasContent(true);
                    objectInfo.setContentType(MimeTypes.getMIMEType(file.getFsname()));
                    objectInfo.setFileName(file.getFsname());
                }

                addPropertyId(result, typeId, filter, PropertyIds.CONTENT_STREAM_ID, null);
            }

            // read custom properties
            //readCustomProperties(file, result, filter, objectInfo);
            if (filter != null) {
                if (!filter.isEmpty()) {
                    debug("Unknown filter properties: " + filter.toString());
                }
            }

            return result;
        } catch (CmisBaseException cbe) {
            throw cbe;
        } catch (Exception e) {
            throw new CmisRuntimeException(e.getMessage(), e);
        }
    }

    private boolean isEmptyProperty(PropertyData<?> prop) {
        if (prop == null || prop.getValues() == null) {
            return true;
        }
        return prop.getValues().isEmpty();
    }

    private void addPropertyId(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
        if (!checkAddProperty(props, typeId, filter, id)) {
            return;
        }

        props.addProperty(new PropertyIdImpl(id, value));
    }

    private void addPropertyIdList(PropertiesImpl props, String typeId, Set<String> filter, String id,
            List<String> value) {
        if (!checkAddProperty(props, typeId, filter, id)) {
            return;
        }

        props.addProperty(new PropertyIdImpl(id, value));
    }

    private void addPropertyString(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
        if (!checkAddProperty(props, typeId, filter, id)) {
            return;
        }

        props.addProperty(new PropertyStringImpl(id, value));
    }

    private void addPropertyInteger(PropertiesImpl props, String typeId, Set<String> filter, String id, long value) {
        addPropertyBigInteger(props, typeId, filter, id, BigInteger.valueOf(value));
    }

    private void addPropertyBigInteger(PropertiesImpl props, String typeId, Set<String> filter, String id,
            BigInteger value) {
        if (!checkAddProperty(props, typeId, filter, id)) {
            return;
        }

        props.addProperty(new PropertyIntegerImpl(id, value));
    }

    private void addPropertyBoolean(PropertiesImpl props, String typeId, Set<String> filter, String id, boolean value) {
        if (!checkAddProperty(props, typeId, filter, id)) {
            return;
        }

        props.addProperty(new PropertyBooleanImpl(id, value));
    }

    private void addPropertyDateTime(PropertiesImpl props, String typeId, Set<String> filter, String id,
            GregorianCalendar value) {
        if (!checkAddProperty(props, typeId, filter, id)) {
            return;
        }

        props.addProperty(new PropertyDateTimeImpl(id, value));
    }

    private boolean checkAddProperty(Properties properties, String typeId, Set<String> filter, String id) {
        if (properties == null || properties.getProperties() == null) {
            throw new IllegalArgumentException("Properties must not be null!");
        }

        if (id == null) {
            throw new IllegalArgumentException("Id must not be null!");
        }

        TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
        if (type == null) {
            throw new IllegalArgumentException("Unknown type: " + typeId);
        }
        if (!type.getPropertyDefinitions().containsKey(id)) {
            throw new IllegalArgumentException("Unknown property: " + id);
        }

        String queryName = type.getPropertyDefinitions().get(id).getQueryName();

        if (queryName != null && filter != null) {
            if (!filter.contains(queryName)) {
                return false;
            } else {
                filter.remove(queryName);
            }
        }

        return true;
    }

    /**
     * Adds the default value of property if defined.
     */
    @SuppressWarnings("unchecked")
    private boolean addPropertyDefault(PropertiesImpl props, PropertyDefinition<?> propDef) {
        if (props == null || props.getProperties() == null) {
            throw new IllegalArgumentException("Props must not be null!");
        }

        if (propDef == null) {
            return false;
        }

        List<?> defaultValue = propDef.getDefaultValue();
        if (defaultValue != null && !defaultValue.isEmpty()) {
            switch (propDef.getPropertyType()) {
                case BOOLEAN:
                    props.addProperty(new PropertyBooleanImpl(propDef.getId(), (List<Boolean>) defaultValue));
                    break;
                case DATETIME:
                    props.addProperty(new PropertyDateTimeImpl(propDef.getId(), (List<GregorianCalendar>) defaultValue));
                    break;
                case DECIMAL:
                    props.addProperty(new PropertyDecimalImpl(propDef.getId(), (List<BigDecimal>) defaultValue));
                    break;
                case HTML:
                    props.addProperty(new PropertyHtmlImpl(propDef.getId(), (List<String>) defaultValue));
                    break;
                case ID:
                    props.addProperty(new PropertyIdImpl(propDef.getId(), (List<String>) defaultValue));
                    break;
                case INTEGER:
                    props.addProperty(new PropertyIntegerImpl(propDef.getId(), (List<BigInteger>) defaultValue));
                    break;
                case STRING:
                    props.addProperty(new PropertyStringImpl(propDef.getId(), (List<String>) defaultValue));
                    break;
                case URI:
                    props.addProperty(new PropertyUriImpl(propDef.getId(), (List<String>) defaultValue));
                    break;
                default:
                    assert false;
            }

            return true;
        }

        return false;
    }

    /**
     * Compiles the allowable actions for a file or folder.
     */
    private AllowableActions compileAllowableActions(Fsblock file, boolean userReadOnly) {
        if (file == null) {
            throw new IllegalArgumentException("File must not be null!");
        }

        // we can't gather allowable actions if the file or folder doesn't exist
        /*if (!file.exists()) {
            throw new CmisObjectNotFoundException("Object not found!");
        }*/
        boolean isReadOnly = false;//!file.canWrite();
        boolean isFolder = file.isDirectory();
        boolean isRoot = root.equals(file);

        Set<Action> aas = EnumSet.noneOf(Action.class);

        addAction(aas, Action.CAN_GET_OBJECT_PARENTS, !isRoot);
        addAction(aas, Action.CAN_GET_PROPERTIES, true);
        addAction(aas, Action.CAN_UPDATE_PROPERTIES, !userReadOnly && !isReadOnly);
        addAction(aas, Action.CAN_MOVE_OBJECT, !userReadOnly && !isRoot);
        addAction(aas, Action.CAN_DELETE_OBJECT, !userReadOnly && !isReadOnly && !isRoot);
        addAction(aas, Action.CAN_GET_ACL, true);

        if (isFolder) {
            addAction(aas, Action.CAN_GET_DESCENDANTS, true);
            addAction(aas, Action.CAN_GET_CHILDREN, true);
            addAction(aas, Action.CAN_GET_FOLDER_PARENT, !isRoot);
            addAction(aas, Action.CAN_GET_FOLDER_TREE, true);
            addAction(aas, Action.CAN_CREATE_DOCUMENT, !userReadOnly);
            addAction(aas, Action.CAN_CREATE_FOLDER, !userReadOnly);
            addAction(aas, Action.CAN_DELETE_TREE, !userReadOnly && !isReadOnly);
        } else {
            addAction(aas, Action.CAN_GET_CONTENT_STREAM, file.getFsfilesize() > 0);
            addAction(aas, Action.CAN_SET_CONTENT_STREAM, !userReadOnly && !isReadOnly);
            addAction(aas, Action.CAN_DELETE_CONTENT_STREAM, !userReadOnly && !isReadOnly);
            addAction(aas, Action.CAN_GET_ALL_VERSIONS, true);
        }

        AllowableActionsImpl result = new AllowableActionsImpl();
        result.setAllowableActions(aas);

        return result;
    }

    private void addAction(Set<Action> aas, Action action, boolean condition) {
        if (condition) {
            aas.add(action);
        }
    }

    /**
     * Compiles the ACL for a file or folder.
     */
    private Acl compileAcl(Fsblock file) {
        AccessControlListImpl result = new AccessControlListImpl();
        result.setAces(new ArrayList<Ace>());

        for (Map.Entry<String, Boolean> ue : readWriteUserMap.entrySet()) {
            // create principal
            AccessControlPrincipalDataImpl principal = new AccessControlPrincipalDataImpl(ue.getKey());

            // create ACE
            AccessControlEntryImpl entry = new AccessControlEntryImpl();
            entry.setPrincipal(principal);
            entry.setPermissions(new ArrayList<String>());
            entry.getPermissions().add(BasicPermissions.READ);
            if (!ue.getValue()) {
                entry.getPermissions().add(BasicPermissions.WRITE);
                entry.getPermissions().add(BasicPermissions.ALL);
            }

            entry.setDirect(true);

            // add ACE
            result.getAces().add(entry);
        }

        return result;
    }

    /**
     * Checks if the given name is valid for a file system.
     *
     * @param name the name to check
     *
     * @return <code>true</code> if the name is valid, <code>false</code>
     * otherwise
     */
    private boolean isValidName(String name) {
        return !(name == null || name.length() == 0);
    }

    /**
     * Returns the File object by id or throws an appropriate exception.
     */
    private Fsblock getFile(String id) {
        try {
            return fsblockFacade.findByUUID(id);
        } catch (Exception e) {
            throw new CmisObjectNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * CMIS getTypeDefinition.
     *
     * @param context
     * @param typeId
     * @return
     */
    public TypeDefinition getTypeDefinition(CallContext context, String typeId) {
        debug("getTypeDefinition");
        //checkUser(context, false);

        return typeManager.getTypeDefinition(context, typeId);
    }

    /**
     * CMIS getTypesChildren.
     *
     * @param context
     * @param typeId
     * @param includePropertyDefinitions
     * @param maxItems
     * @param skipCount
     * @return
     */
    public TypeDefinitionList getTypeChildren(CallContext context, String typeId, Boolean includePropertyDefinitions,
            BigInteger maxItems, BigInteger skipCount) {
        debug("getTypesChildren");
        //checkUser(context, false);

        return typeManager.getTypeChildren(context, typeId, includePropertyDefinitions, maxItems, skipCount);
    }

    /**
     * Checks and compiles a property set that can be written to disc.
     */
    private PropertiesImpl compileWriteProperties(String typeId, String creator, String modifier, Properties properties) {
        PropertiesImpl result = new PropertiesImpl();
        Set<String> addedProps = new HashSet<String>();

        if (properties == null || properties.getProperties() == null) {
            throw new CmisConstraintException("No properties!");
        }

        // get the property definitions
        TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
        if (type == null) {
            throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
        }

        // check if all required properties are there
        for (PropertyData<?> prop : properties.getProperties().values()) {
            PropertyDefinition<?> propType = type.getPropertyDefinitions().get(prop.getId());

            // do we know that property?
            if (propType == null) {
                throw new CmisConstraintException("Property '" + prop.getId() + "' is unknown!");
            }

            // can it be set?
            if (propType.getUpdatability() == Updatability.READONLY) {
                throw new CmisConstraintException("Property '" + prop.getId() + "' is readonly!");
            }

            // empty properties are invalid
            // TODO: check
            // if (isEmptyProperty(prop)) {
            // throw new CmisConstraintException("Property '" + prop.getId() +
            // "' must not be empty!");
            // }
            // add it
            result.addProperty(prop);
            addedProps.add(prop.getId());
        }

        // check if required properties are missing
        for (PropertyDefinition<?> propDef : type.getPropertyDefinitions().values()) {
            if (!addedProps.contains(propDef.getId()) && propDef.getUpdatability() != Updatability.READONLY) {
                if (!addPropertyDefault(result, propDef) && propDef.isRequired()) {
                    throw new CmisConstraintException("Property '" + propDef.getId() + "' is required!");
                }
            }
        }

        addPropertyId(result, typeId, null, PropertyIds.OBJECT_TYPE_ID, typeId);
        addPropertyString(result, typeId, null, PropertyIds.CREATED_BY, creator);
        addPropertyString(result, typeId, null, PropertyIds.LAST_MODIFIED_BY, modifier);

        return result;
    }
}
