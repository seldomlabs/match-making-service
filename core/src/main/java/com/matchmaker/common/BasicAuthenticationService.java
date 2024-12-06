package com.matchmaker.common;

import com.matchmaker.common.exception.ApplicationException;

//@Service("basicAuthenticationService")
public interface BasicAuthenticationService
{
	public boolean authenticate(String authCredentials) throws ApplicationException;
}
