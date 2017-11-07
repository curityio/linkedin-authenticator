/*
 * Copyright (C) 2017 Curity AB. All rights reserved.
 *
 * The contents of this file are the property of Curity AB.
 * You may not copy or use this file, in either source code
 * or executable form, except in compliance with terms
 * set by Curity AB.
 *
 * For further information, please contact Curity AB.
 */

package io.curity.identityserver.plugin.linkedin.authentication;

import se.curity.identityserver.sdk.web.Request;

public class CallbackGetRequestModel {
    private String _code;
    private String _url;
    private String _state;

    public CallbackGetRequestModel(Request request) {
        _code = request.getParameterValueOrError("code");
        _state = request.getParameterValueOrError("state");
        _url = request.getUrl();
    }

    public String getCode() {
        return _code;
    }

    public String getState() {
        return _state;
    }

    public String getUrl() {
        return _url;
    }
}
