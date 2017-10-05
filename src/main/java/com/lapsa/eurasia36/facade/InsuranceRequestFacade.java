package com.lapsa.eurasia36.facade;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.lapsa.insurance.domain.InsuranceRequest;

@Stateless
@LocalBean
public class InsuranceRequestFacade extends AbstractInsuranceRequestFacade<InsuranceRequest> {
}
