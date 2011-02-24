package de.escidoc.core.common.servlet;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.BeanLocator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.regex.Pattern;

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
    private static final long serialVersionUID = -1471080999315442967L;

    private String beanIds;

    private String factoryId;

    /**
     * See Interface for functional description.
     * 
     * @throws ServletException
     *             Thrown in case of an error.
     * 
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public final void init() throws ServletException {

        super.init();

        factoryId = getServletConfig().getInitParameter("factoryId");
        beanIds = getServletConfig().getInitParameter("beanIds");

        if (factoryId == null || beanIds == null) {
            throw new ServletException(
                "factory id and bean ids must be specified as init parameters.");
        }

        try {
            final String[] splitted = PATTERN_SPLIT_IDS.split(beanIds);
            for (final String beanId : splitted) {
                BeanLocator.getBean(factoryId, beanId);
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
    public final void destroy() {

        super.destroy();
    }

}
