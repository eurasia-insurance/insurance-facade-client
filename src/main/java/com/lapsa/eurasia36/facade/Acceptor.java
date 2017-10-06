package com.lapsa.eurasia36.facade;

interface Acceptor<T> {

    default <X extends T> void accept(X request) {
	acceptAndReply(request);
    }

    <X extends T> X acceptAndReply(X request);
}
