package tech.lapsa.insurance.facade.beans;

import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.lapsa.insurance.domain.crm.User;
import com.lapsa.insurance.domain.crm.UserLogin;

import tech.lapsa.insurance.dao.UserDAO;
import tech.lapsa.insurance.facade.UserFacade;
import tech.lapsa.patterns.dao.NotFound;

@Stateless
public class UserFacadeBean implements UserFacade {

    @Inject
    private UserDAO userDAO;

    @Inject
    private Logger logger;

    @Override
    public User findOrCreate(String principalName) {
	if (principalName == null)
	    return null;
	try {
	    return userDAO.getByLogin(principalName);
	} catch (NotFound e) {
	    logger.info(String.format("New User creating '%1$s'", principalName));

	    User value = new User();
	    UserLogin login = value.addLogin(new UserLogin());
	    login.setName(principalName);

	    if (Util.isEmail(principalName)) {
		value.setEmail(principalName);
		value.setName(Util.stripEmailToName(principalName));
	    } else {
		value.setName(principalName);
	    }
	    return userDAO.save(value);
	}
    }

    @Override
    public User findOrCreate(Principal principal) {
	if (principal == null)
	    return null;
	return findOrCreate(principal.getName());
    }

    @Override
    public List<User> getWhoCreatedRequests() {
	return userDAO.findAllWhoCreatedRequest();
    }

    private static class Util {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	public static boolean isEmail(String principalName) {
	    return pattern.matcher(principalName).matches();
	}

	public static String stripEmailToName(String email) {
	    if (email == null)
		return null;
	    String[] verbs = email.split("\\@")[0].split("[\\.\\s]");
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < verbs.length; i++) {
		String verb = verbs[i];
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
