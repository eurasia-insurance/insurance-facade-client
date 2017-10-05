package com.lapsa.eurasia36.facade;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.lapsa.insurance.domain.policy.PolicyRequest;

@Stateless
@LocalBean
public class PolicyRequestFacade extends AbstractInsuranceRequestFacade<PolicyRequest> {
}
