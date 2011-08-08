package de.escidoc.core.policy;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.DeclareWarning;

/**
 * @author Eduard Hildebrandt
 */
@Aspect
final class NoCollectionImplAsReturnValuePolicy {

    @SuppressWarnings("unused")
    @DeclareError(
            "execution(" + "(java.util.TreeSet+" + " || java.util.Vector+" +
                    " || java.util.concurrent.SynchronousQueue+" + " || java.util.Stack+" +
                    " || javax.management.relation.RoleUnresolvedList+" + " || javax.management.relation.RoleList+" +
                    " || java.util.PriorityQueue+" + " || java.util.concurrent.PriorityBlockingQueue+" +
                    " || java.util.LinkedList+" + " || java.util.LinkedHashSet+" +
                    " || java.util.concurrent.LinkedBlockingQueue+" + " || java.util.HashSet+" +
                    " || java.util.concurrent.DelayQueue+" + " || java.util.concurrent.CopyOnWriteArraySet+" +
                    " || java.util.concurrent.CopyOnWriteArrayList+" +
                    " || java.util.concurrent.ConcurrentLinkedQueue+" +
                    " || java.beans.beancontext.BeanContextSupport+" +
                    " || java.beans.beancontext.BeanContextServicesSupport+" + " || java.util.ArrayList+" +
                    " || java.util.concurrent.ArrayBlockingQueue+" + ")" + " *..*.*(..))")
    private static final String NO_COLLECTION_IMPL_AS_RETURN_VALUE =
            "Returning a Collection implementation as not allowed. Please use the Collection interface instead of " +
                    "the implementation class. For example: Use java.lang.List instead of java.lang.ArrayList.";

    private NoCollectionImplAsReturnValuePolicy() {
    }

}
