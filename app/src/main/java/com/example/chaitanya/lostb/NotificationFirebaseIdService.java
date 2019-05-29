package com.example.chaitanya.lostb;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class NotificationFirebaseIdService extends FirebaseInstanceIdService {

    FirebaseUser mUser;
    DatabaseReference ref;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String t = FirebaseInstanceId.getInstance().getToken();
        if (mUser != null){
            newToken(t);
        }
    }

    private void newToken(String t) {
        ref = FirebaseDatabase.getInstance().getReference("Tokens");
        NotificationTokensModel token = new NotificationTokensModel(t);
        ref.child(mUser.getUid()).setValue(token);
    }
}
