// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.projectoxford.face.rest;

import org.json.JSONObject;

public class ClientException extends Exception {

    public ClientException(String message) {
        super(message);
    }

    public ClientException(JSONObject errorObject) {
        super(errorObject.toString());
    }
}
