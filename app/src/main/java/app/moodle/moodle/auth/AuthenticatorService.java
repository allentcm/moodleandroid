package app.moodle.moodle.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    public AuthenticatorService() {
        // use the same authenticator across all context
        if (mAuthenticator == null)
            mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // bind service to authenticator
        return mAuthenticator.getIBinder();
    }
}
