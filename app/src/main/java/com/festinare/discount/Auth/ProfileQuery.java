package com.festinare.discount.auth;

import android.provider.ContactsContract;

public interface ProfileQuery {

    String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
    };

    int ADDRESS = 0;
}
