package com.lapsa.insurance.facade.beans;

import com.lapsa.insurance.domain.Request;

interface RequestAcceptor<T extends Request> {

    default void accept(T request) {
	acceptAndReply(request);
    }

    T acceptAndReply(T request);
}
