package de.escidoc.core.common.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.BeanLocator;

/**
 * Servlet that initializes the AA beans during startup.
 * 
 * @author TTE
 * 
 */
public class InitBeansServlet extends HttpServlet {

    /**
     * Pattern used to split the comma separated list of bean ids.
     */
    private static final Pattern PATTERN_SPLIT_IDS = Pattern.compile(",\\s*");

    private String beanIds;

    private String factoryId;

    private final Map<String, Object> beans = new HashMap<String, Object>();

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @throws ServletException
     *             Thrown in case of an error.
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {

        super.init();

        factoryId = getServletConfig().getInitParameter("factoryId");
        beanIds = getServletConfig().getInitParameter("beanIds");

        if (factoryId == null || beanIds == null) {
            throw new ServletException(
                "factory id and bean ids must be specified as init parameters.");
        }

        try {
            final String[] splitted = PATTERN_SPLIT_IDS.split(beanIds);
            for (int i = 0; i < splitted.length; i++) {
                final String beanId = splitted[i];
                final Object bean = BeanLocator.getBean(factoryId, beanId);
                beans.put(beanId, bean);
            }
        }
        catch (WebserverSystemException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {

        super.destroy();
    }

    // CHECKSTYLE:JAVADOC-ON
}
