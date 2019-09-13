package com.smartn.passgen;

import java.io.Serializable;

public class ListItem implements Serializable {

    String website,username,password;

    @Override
    public String toString()
    {
        return website;
    }

    public String getWebsite() {
        return website;
    }
}
