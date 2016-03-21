package com.mimerhellas.opencmis.dbshare;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.server.support.wrapper.CallContextAwareCmisService;

/**
 * CMIS Service Implementation.
 */
public class DBShareCmisService extends AbstractCmisService implements CallContextAwareCmisService {

    private final DBShareRepositoryManager repositoryManager;
    private CallContext context;

    public DBShareCmisService(final DBShareRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    // --- Call Context ---
    /**
     * Sets the call context.
     *
     * This method should only be called by the service factory.
     *
     * @param context
     */
    @Override
    public void setCallContext(CallContext context) {
        this.context = context;
    }

    /**
     * Gets the call context.
     *
     * @return
     */
    @Override
    public CallContext getCallContext() {
        return context;
    }

    // --- CMIS Operations ---
    @Override
    public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
        // very basic repository info set up
        /*RepositoryInfoImpl repositoryInfo = new RepositoryInfoImpl();

        repositoryInfo.setId("default");
        repositoryInfo.setName("RDBMS");
        repositoryInfo.setDescription("This is a database repository!");

        repositoryInfo.setCmisVersionSupported("1.0");

        repositoryInfo.setProductName("NewRS");
        repositoryInfo.setProductVersion("0.1");
        repositoryInfo.setVendorName("Mimer Hellas Ltd.");

        repositoryInfo.setRootFolder("@root@");

        repositoryInfo.setThinClientUri("");

        RepositoryCapabilitiesImpl capabilities = new RepositoryCapabilitiesImpl();
        capabilities.setCapabilityAcl(CapabilityAcl.NONE);
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

        return Collections.singletonList((RepositoryInfo) repositoryInfo);*/

        List<RepositoryInfo> result = new ArrayList<RepositoryInfo>();

        for (DBShareRepository fsr : repositoryManager.getRepositories()) {
            result.add(fsr.getRepositoryInfo(getCallContext()));
        }

        return result;
    }

    /**
     * Gets the repository for the current call.
     *
     * @return
     */
    public DBShareRepository getRepository() {
        return repositoryManager.getRepository(getCallContext().getRepositoryId());
    }

    @Override
    public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions,
            BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
        return getRepository().getTypeChildren(getCallContext(), typeId, includePropertyDefinitions, maxItems,
                skipCount);
    }

    @Override
    public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {
        return getRepository().getTypeDefinition(getCallContext(), typeId);
    }

    @Override
    public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy,
            Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
            Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
        return getRepository().getChildren(getCallContext(), folderId, filter, orderBy, includeAllowableActions,
                includePathSegment, maxItems, skipCount, this);
    }

    @Override
    public List<ObjectInFolderContainer> getDescendants(String repositoryId, String folderId, BigInteger depth,
            String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {
        return getRepository().getDescendants(getCallContext(), folderId, depth, filter, includeAllowableActions,
                includePathSegment, this, false);
    }

    @Override
    public ObjectData getFolderParent(String repositoryId, String folderId, String filter, ExtensionsData extension) {
        return getRepository().getFolderParent(getCallContext(), folderId, filter, this);
    }

    @Override
    public List<ObjectInFolderContainer> getFolderTree(String repositoryId, String folderId, BigInteger depth,
            String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {
        return getRepository().getDescendants(getCallContext(), folderId, depth, filter, includeAllowableActions,
                includePathSegment, this, true);
    }

    @Override
    public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter,
            Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
            Boolean includeRelativePathSegment, ExtensionsData extension) {
        return getRepository().getObjectParents(getCallContext(), objectId, filter, includeAllowableActions,
                includeRelativePathSegment, this);
    }

    @Override
    public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
            IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
            Boolean includeAcl, ExtensionsData extension) {
        return getRepository().getObject(getCallContext(), objectId, null, filter, includeAllowableActions, includeAcl,
                this);
    }

    @Override
    public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
            IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
            Boolean includeAcl, ExtensionsData extension) {
        return getRepository().getObjectByPath(getCallContext(), path, filter, includeAllowableActions, includeAcl,
                this);
    }

    @Override
    public ObjectData getObjectOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
            Boolean major, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
            String renditionFilter, Boolean includePolicyIds, Boolean includeAcl, ExtensionsData extension) {
        return getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, includeAllowableActions,
                includeAcl, this);
    }

    // --- object service ---
    @Override
    public String create(String repositoryId, Properties properties, String folderId, ContentStream contentStream,
            VersioningState versioningState, List<String> policies, ExtensionsData extension) {
        ObjectData object = getRepository().create(getCallContext(), properties, folderId, contentStream,
                versioningState, this);

        return object.getId();
    }

    @Override
    public String createDocument(String repositoryId, Properties properties, String folderId,
            ContentStream contentStream, VersioningState versioningState, List<String> policies, Acl addAces,
            Acl removeAces, ExtensionsData extension) {
        return getRepository().createDocument(getCallContext(), properties, folderId, contentStream, versioningState);
    }

    /*@Override
    public String createDocumentFromSource(String repositoryId, String sourceId, Properties properties,
            String folderId, VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces,
            ExtensionsData extension) {
        return getRepository().createDocumentFromSource(getCallContext(), sourceId, properties, folderId,
                versioningState);
    }*/
    @Override
    public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset,
            BigInteger length, ExtensionsData extension) {
        return getRepository().getContentStream(getCallContext(), objectId, offset, length);
    }

    @Override
    public void setContentStream(String repositoryId, Holder<String> objectId, Boolean overwriteFlag,
            Holder<String> changeToken, ContentStream contentStream, ExtensionsData extension) {
        getRepository().changeContentStream(getCallContext(), objectId, overwriteFlag, contentStream, false);
    }

    @Override
    public void deleteObjectOrCancelCheckOut(String repositoryId, String objectId, Boolean allVersions,
            ExtensionsData extension) {
        getRepository().deleteObject(getCallContext(), objectId);
    }

    @Override
    public FailedToDeleteData deleteTree(String repositoryId, String folderId, Boolean allVersions,
            UnfileObject unfileObjects, Boolean continueOnFailure, ExtensionsData extension) {
        return getRepository().deleteTree(getCallContext(), folderId, continueOnFailure);
    }

}
