package com.lapsa.eurasia36.facade;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.lapsa.insurance.domain.casco.CascoRequest;

@Stateless
@LocalBean
public class CascoRequestFacade extends AbstractInsuranceRequestFacade<CascoRequest> {
}
