package de.escidoc.core.sm.business.vo.database;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;

/**
 * Checks for database conventions (like tablename, fieldname etc).
 *
 * @author Michael Hoppe
 */
public final class DatabaseConventionChecker {

    private static final String INCORRECT_NAME_MSG =
        new StringBuffer(" may not contain whitespaces or quotes, ")
            .append("has to start with a letter or underscore, ").append(
                "may only contain letters, digits or underscore ").append("and length may be 63 characters maximum")
            .toString();

    /**
     * Private constructor to avoid instantiation.
     */
    private DatabaseConventionChecker() {
    }

    /**
     * Checks (table, field, index)-name and throws Exception if name has errors. name may not contain whitespaces or
     * quotes, has to start with a letter or an underscore, may only contain letters, digits or underscore and length
     * may be 30 characters maximum As we use schemas and aliases, name may contain a dot. As we select *, name may be
     * *.
     *
     * @param name name
     * @throws SqlDatabaseSystemException e
     */
    public static void checkName(final String name) throws SqlDatabaseSystemException {
        String trimedName = null;
        if (name != null) {
            trimedName = name.trim();
        }
        if (trimedName != null
            && (trimedName.matches("(?s).*?\\s.*|.*?'.*|.*?[^\\*\\._a-zA-z0-9].*")
                || !trimedName.matches("[_A-Za-z].*|\\*") || trimedName.length() > 63)) {
            throw new SqlDatabaseSystemException('\'' + name + '\'' + INCORRECT_NAME_MSG);
        }
    }

}
