package tech.lapsa.insurance.facade.beans;

import java.security.Principal;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.lapsa.insurance.domain.crm.User;
import com.lapsa.insurance.domain.crm.UserLogin;

import tech.lapsa.insurance.dao.UserDAO;
import tech.lapsa.insurance.facade.UserFacade;
import tech.lapsa.insurance.facade.UserFacade.UserFacadeLocal;
import tech.lapsa.insurance.facade.UserFacade.UserFacadeRemote;
import tech.lapsa.insurance.facade.beans.ejb.EJBViaCDI;
import tech.lapsa.java.commons.logging.MyLogger;
import tech.lapsa.patterns.dao.NotFound;

@Stateless
public class UserFacadeBean implements UserFacadeLocal, UserFacadeRemote {

    // READERS

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<User> getWhoEverCreatedRequests() {
	return _getWhoEverCreatedRequests();
    }

    // MODIFIERS

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public User findOrCreate(final String principalName) throws IllegalArgumentException {
	return _findOrCreate(principalName);
    }

    @Override
    public User findOrCreate(final Principal principal) throws IllegalArgumentException {
	return _findOrCreate(principal);
    }

    // PRIVATE

    @Inject
    @EJBViaCDI
    private UserDAO userDAO;

    private final MyLogger logger = MyLogger.newBuilder() //
	    .withNameOf(UserFacade.class) //
	    .build();

    private List<User> _getWhoEverCreatedRequests() {
	return userDAO.findAllWhoCreatedRequest();
    }

    private User _findOrCreate(final Principal principal) {
	if (principal == null)
	    return null;
	return _findOrCreate(principal.getName());
    }

    private User _findOrCreate(final String principalName) {
	if (principalName == null)
	    return null;
	try {
	    return userDAO.getByLogin(principalName);
	} catch (final NotFound e) {
	    logger.INFO.log("New User creating '%1$s'", principalName);

	    final User value = new User();
	    final UserLogin login = value.addLogin(new UserLogin());
	    login.setName(principalName);

	    if (Util.isEmail(principalName)) {
		value.setEmail(principalName);
		value.setName(Util.stripEmailToName(principalName));
	    } else
		value.setName(principalName);
	    return userDAO.save(value);
	}
    }

    private static class Util {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	public static boolean isEmail(final String principalName) {
	    return pattern.matcher(principalName).matches();
	}

	public static String stripEmailToName(final String email) {
	    if (email == null)
		return null;
	    final String[] verbs = email.split("\\@")[0].split("[\\.\\s]");
	    final StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < verbs.length; i++) {
		final String verb = verbs[i];
		if (verb.length() == 0)
		    continue;
		sb.append(Character.toUpperCase(verb.charAt(0)));
		if (verb.length() > 1)
		    sb.append(verb.substring(1));
		if (i < verbs.length - 1)
		    sb.append(" ");
	    }
	    return sb.toString();
	}
    }
}
