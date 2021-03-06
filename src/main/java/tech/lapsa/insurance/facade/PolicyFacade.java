package tech.lapsa.insurance.facade;

import javax.ejb.Local;
import javax.ejb.Remote;

import com.lapsa.insurance.domain.policy.Policy;

import tech.lapsa.java.commons.exceptions.IllegalArgument;

public interface PolicyFacade extends EJBConstants {

    public static final String BEAN_NAME = "PolicyFacadeBean";

    @Local
    public interface PolicyFacadeLocal extends PolicyFacade {
    }

    @Remote
    public interface PolicyFacadeRemote extends PolicyFacade {
    }

    Policy getByNumber(String number) throws PolicyNotFound, IllegalArgument;
}