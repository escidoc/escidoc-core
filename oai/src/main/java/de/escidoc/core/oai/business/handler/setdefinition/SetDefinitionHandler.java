package de.escidoc.core.oai.business.handler.setdefinition;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.filter.DbRequestParameters;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.stax.handler.filter.FilterHandler;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.common.util.xml.stax.handler.OptimisticLockingStaxHandler;
import de.escidoc.core.oai.business.filter.SetDefinitionFilter;
import de.escidoc.core.oai.business.interfaces.SetDefinitionHandlerInterface;
import de.escidoc.core.oai.business.persistence.SetDefinition;
import de.escidoc.core.oai.business.persistence.SetDefinitionDaoInterface;
import de.escidoc.core.oai.business.renderer.VelocityXmlSetDefinitionRenderer;
import de.escidoc.core.oai.business.renderer.interfaces.SetDefinitionRendererInterface;
import de.escidoc.core.oai.business.stax.handler.set_definition.SetDefinitionCreateHandler;
import de.escidoc.core.oai.business.stax.handler.set_definition.SetDefinitionUpdateHandler;

/**
 * @spring.bean id="business.SetDefinitionHandler" scope="prototype"
 * @author rof
 * 
 */
public class SetDefinitionHandler implements SetDefinitionHandlerInterface {

    private SetDefinitionDaoInterface setDefinitionDao = null;

    private SetDefinitionRendererInterface renderer = null;

    private final String MSG_SET_DEFINITION_NOT_FOUND_BY_ID =
        "Set definition with provided id does not exist.";

    private PolicyDecisionPointInterface pdp = null;

    /**
     * The logger.
     */
    private static final AppLogger LOG = new AppLogger(
        SetDefinitionHandler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.oai.business.interfaces.SetDefinitionHandlerInterface
     * #create(java.lang.String)
     */
    public String create(final String xmlData)
        throws UniqueConstraintViolationException, InvalidXmlException,
        MissingMethodParameterException, SystemException {
        ByteArrayInputStream in =
            XmlUtility.convertToByteArrayInputStream(xmlData);
        StaxParser sp = new StaxParser();
        SetDefinitionCreateHandler sdch = new SetDefinitionCreateHandler(sp);

        sp.addHandler(sdch);
        try {
            sp.parse(in);
            sp.clearHandlerChain();
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        SetDefinition setDefinition = new SetDefinition();
        setModificationValues(setDefinition, sdch.getSetProperties());
        setCreationValues(setDefinition, sdch.getSetProperties());
        this.setDefinitionDao.save(setDefinition);

        return getRenderer().render(setDefinition);

    }

    /**
     * Sets the creation date and the created-by user in the provided
     * <code>SetDefinition</code> object.<br/>
     * The values are set with the values of modification date and modifying
     * user of the provided set definition.<br/>
     * 
     * @param setDefinition
     *            definition The <code>SetDefinition</code> object to modify.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @throws UniqueConstraintViolationException
     *             The specification of the given set definition has already
     *             been used.
     */
    private void setCreationValues(
        final SetDefinition setDefinition,
        final Map<String, String> setProperties) throws SystemException,
        UniqueConstraintViolationException {

        // initialize creation-date value
        setDefinition.setCreationDate(setDefinition.getLastModificationDate());

        // initialize created-by values
        setDefinition.setCreatorId(UserContext.getId());
        setDefinition.setCreatorTitle(UserContext.getRealName());

        String specification = setProperties.get("specification");
        if (!checkSpecificationUnique(specification)) {

            String message = "The provided set specification is not unique.";
            LOG.error(message);
            throw new UniqueConstraintViolationException(message);
        }
        setDefinition.setSpecification(specification);
        String query = setProperties.get("query");
        setDefinition.setQuery(query);
    }

    /**
     * Sets the last modification date, the modified-by user and all values from
     * the given set map in the provided <code>SetDefinition</code> object. <br/>
     * The last modification date is set to the current time, and the modified
     * by user to the user account of the current, authenticated user.
     * 
     * @param setDefinition
     *            The <code>SetDefinition</code> object to modify.
     * @param setProperties
     *            map which contains all properties of the set definition
     * 
     * @throws SystemException
     *             Thrown in case of an internal error.
     * 
     */
    private boolean setModificationValues(
        final SetDefinition setDefinition,
        final Map<String, String> setProperties) throws SystemException {
        boolean changed = false;
        if (setProperties != null) {
            String newDescription =
                setProperties.get(Elements.ELEMENT_DESCRIPTION);
            if (newDescription != null
                && ((setDefinition.getDescription() != null
                    && !newDescription.equals(setDefinition.getDescription()) || setDefinition
                    .getDescription() == null))) {
                setDefinition.setDescription(newDescription);
                changed = true;
            }
            String newName = setProperties.get(Elements.ELEMENT_NAME);
            if (setDefinition.getName() == null
                || ((setDefinition.getName() != null) && !newName
                    .equals(setDefinition.getName()))) {
                setDefinition.setName(setProperties.get(Elements.ELEMENT_NAME));
                changed = true;
            }
        }
        if (changed) {
            setDefinition.setModifiedById(UserContext.getId());
            setDefinition.setModifiedByTitle(UserContext.getRealName());
            setDefinition.setLastModificationDate(new Timestamp(System
                .currentTimeMillis()));
        }
        return changed;
    }

    /**
     * Check if the given specification is already used as set definition
     * specification in the database.
     * 
     * @param specification
     *            set definition specification
     * 
     * @return true if the specification is still unused
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database error.
     */
    private boolean checkSpecificationUnique(final String specification)
        throws SqlDatabaseSystemException {
        return setDefinitionDao.findSetDefinitionBySpecification(specification) == null;
    }

    /**
     * See Interface for functional description.
     * 
     * @param setDefinitionId
     * 
     * @return
     * @throws ResourceNotFoundException
     *             e
     * @throws SystemException
     *             e
     * @see de.escidoc.core.oai.service.interfaces.SetDefinitionIdHandlerInterface
     *      #retrieve(java.lang.String)
     * @aa
     */
    public String retrieve(final String setDefinitionId)
        throws ResourceNotFoundException, SystemException {
        SetDefinition setDefinition =
            setDefinitionDao.retrieveSetDefinition(setDefinitionId);

        if (setDefinition == null) {
            throw new ResourceNotFoundException(StringUtility
                .concatenateWithBrackets(MSG_SET_DEFINITION_NOT_FOUND_BY_ID,
                    setDefinitionId).toString());
        }
        return getRenderer().render(setDefinition);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.oai.business.interfaces.SetDefinitionHandlerInterface
     * #update(java.lang.String, java.lang.String)
     */
    public String update(final String setDefinitionId, final String xmlData)
        throws ResourceNotFoundException, OptimisticLockingException,
        MissingMethodParameterException, SystemException {
        SetDefinition setDefinition =
            setDefinitionDao.retrieveSetDefinition(setDefinitionId);
        if (setDefinition == null) {
            String message =
                StringUtility
                    .concatenateWithBrackets(
                        MSG_SET_DEFINITION_NOT_FOUND_BY_ID, setDefinitionId)
                    .toString();
            LOG.error(message);
            throw new ResourceNotFoundException(message);
        }

        ByteArrayInputStream in =
            XmlUtility.convertToByteArrayInputStream(xmlData);
        StaxParser sp = new StaxParser();

        OptimisticLockingStaxHandler optimisticLockingHandler =
            new OptimisticLockingStaxHandler(
                setDefinition.getLastModificationDate());
        sp.addHandler(optimisticLockingHandler);

        SetDefinitionUpdateHandler sduh = new SetDefinitionUpdateHandler(sp);

        sp.addHandler(sduh);
        try {
            sp.parse(in);
            // sp.clearHandlerChain();
        }
        catch (OptimisticLockingException e) {
            throw e;
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        if (setModificationValues(setDefinition, sduh.getSetProperties())) {
            setDefinitionDao.save(setDefinition);
        }
        return getRenderer().render(setDefinition);
    }

    /**
     * See Interface for functional description.
     * 
     * @param setDefinitionId
     * 
     * @throws ResourceNotFoundException
     *             e
     * @throws SystemException
     *             e
     * @see de.escidoc.core.oai.service.interfaces.SetDefinitionHandlerInterface
     *      #delete(java.lang.String)
     * @aa
     */
    public void delete(final String setDefinitionId)
        throws ResourceNotFoundException, SystemException {
        SetDefinition setDefinition =
            setDefinitionDao.retrieveSetDefinition(setDefinitionId);

        if (setDefinition == null) {
            String message =
                StringUtility
                    .concatenateWithBrackets(
                        MSG_SET_DEFINITION_NOT_FOUND_BY_ID, setDefinitionId)
                    .toString();
            LOG.error(message);
            throw new ResourceNotFoundException(message);
        }
        setDefinitionDao.delete(setDefinition);
    }

    /**
     * See Interface for functional description.
     * 
     * @param filter
     * 
     * @return
     * @throws AuthenticationException
     *             e
     * @throws AuthorizationException
     *             e
     * @throws InvalidSearchQueryException
     *             e
     * @throws SystemException
     *             e
     * @see de.escidoc.core.oai.service.interfaces.SetDefinitionHandlerInterface
     *      #retrieveSetDefinitions(java.util.Map)
     */
    public String retrieveSetDefinitions(final Map<String, String[]> filter)
        throws AuthenticationException, AuthorizationException,
        InvalidSearchQueryException, SystemException {
        String result = null;
        String query = null;
        int offset = FilterHandler.DEFAULT_OFFSET;
        int limit = FilterHandler.DEFAULT_LIMIT;
        boolean explain = false;

        SRURequestParameters parameters =
            new DbRequestParameters((Map<String, String[]>) filter);

        query = parameters.query;
        limit = parameters.limit;
        offset = parameters.offset;
        explain = parameters.explain;

        if (explain) {
            Map<String, Object> values = new HashMap<String, Object>();

            values.put("PROPERTY_NAMES",
                new SetDefinitionFilter(null).getPropertyNames());
            result =
                ExplainXmlProvider.getInstance().getExplainSetDefinitionXml(
                    values);
        }
        else {
            int needed = offset + limit;
            int currentLimit = needed;
            int currentOffset = 0;
            final List<SetDefinition> permittedSetDefinitions =
                new ArrayList<SetDefinition>();
            final int size = permittedSetDefinitions.size();

            while (size <= needed) {
                final List<SetDefinition> tmpSetDefinitions;

                tmpSetDefinitions =
                    setDefinitionDao.retrieveSetDefinitions(query,
                        currentOffset, currentLimit);
                if (tmpSetDefinitions == null || tmpSetDefinitions.isEmpty()) {
                    break;
                }
                else {
                    final List<String> ids =
                        new ArrayList<String>(tmpSetDefinitions.size());
                    for (SetDefinition setDefinition : tmpSetDefinitions) {
                        ids.add(setDefinition.getId());
                    }
                    setPdpHandler();
                    try {
                        final List<String> tmpPermitted =
                            pdp.evaluateRetrieve(
                                XmlUtility.NAME_SET_DEFINITION, ids);
                        final int numberPermitted = tmpPermitted.size();
                        if (numberPermitted == 0) {
                            break;
                        }
                        else {
                            int permittedIndex = 0;
                            String currentPermittedId =
                                tmpPermitted.get(permittedIndex);
                            for (SetDefinition setDefinition : tmpSetDefinitions) {
                                if (currentPermittedId.equals(setDefinition
                                    .getId())) {
                                    permittedSetDefinitions.add(setDefinition);
                                    ++permittedIndex;
                                    if (permittedIndex < numberPermitted) {
                                        currentPermittedId =
                                            tmpPermitted.get(permittedIndex);
                                    }
                                    else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    catch (MissingMethodParameterException e) {
                        throw new SystemException(
                            "Unexpected exception during evaluating access "
                                + "rights.", e);
                    }
                    catch (ResourceNotFoundException e) {
                        throw new SystemException(
                            "Unexpected exception during evaluating access "
                                + "rights.", e);
                    }
                }
                currentOffset += currentLimit;
            }

            final List<SetDefinition> offsetSetDefinitions;
            final int numberPermitted = permittedSetDefinitions.size();
            if (offset < numberPermitted) {
                offsetSetDefinitions = new ArrayList<SetDefinition>(limit);
                for (int i = offset; i < numberPermitted && i < needed; i++) {
                    offsetSetDefinitions.add(permittedSetDefinitions.get(i));
                }
            }
            else {
                offsetSetDefinitions = new ArrayList<SetDefinition>(0);
            }
            result =
                getRenderer().renderSetDefinitions(offsetSetDefinitions);
        }
        return result;
    }

    /**
     * Injects the set definition data access object.
     * 
     * @param setDefinitionDao
     *            The data access object.
     * 
     * @spring.property ref="persistence.SetDefinitionDao"
     * @aa
     */
    public void setSetDefinitionDao(
        final SetDefinitionDaoInterface setDefinitionDao) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.concatenateWithBracketsToString(
                "setDefinitionDao", setDefinitionDao));
        }
        this.setDefinitionDao = setDefinitionDao;
    }

    /**
     * Set Policy decision point handler.
     * 
     * @throws WebserverSystemException
     *             e
     */
    public void setPdpHandler() throws WebserverSystemException {
        if (this.pdp == null) {
            this.pdp = BeanLocator.locatePolicyDecisionPoint();
        }
        LOG.debug("setPdp");
    }

    /**
     * @return the renderer
     * 
     */
    public SetDefinitionRendererInterface getRenderer() {
        if (renderer == null) {
            renderer = new VelocityXmlSetDefinitionRenderer();
        }
        return renderer;
    }

}
