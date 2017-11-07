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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class PostDataParamBuilder {
    private List<NameValuePair> pairs = new ArrayList<>();

    public List<NameValuePair> getPairs() {
        return pairs;
    }

    public PostDataParamBuilder addPair(String key, String value) {
        pairs.add(new BasicNameValuePair(key, value));

        return this;
    }
}
