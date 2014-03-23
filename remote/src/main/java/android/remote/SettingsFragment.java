package android.remote;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.remote.connection.ConnectionConfig;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences
        .OnSharedPreferenceChangeListener {
    private static final String KEY_PREF_SERVER_ADDRESS = "pref_server_address";
    private static final String KEY_PREF_SERVER_PORT = "pref_server_port";
    private static final String KEY_PREF_LOGIN_USER = "pref_login_user";
    private static final String KEY_PREF_LOGIN_PASSWORD = "pref_login_password";
    private static final String KEY_PREF_RSA_MODULUS = "pref_rsa_modulus";
    private static final String KEY_PREF_RSA_EXPONENT = "pref_rsa_exponent";
    private SharedPreferences mSharedPreferences;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public static ConnectionConfig getConnectionConfig(SharedPreferences sharedPreferences)
            throws GeneralSecurityException, NumberFormatException, UnsupportedEncodingException {
        String serverAddress = sharedPreferences.getString(SettingsFragment
                .KEY_PREF_SERVER_ADDRESS, "");
        String serverPort = sharedPreferences.getString(SettingsFragment.KEY_PREF_SERVER_PORT, "");
        int port = Integer.parseInt(serverPort);
        String userLogin = sharedPreferences.getString(SettingsFragment.KEY_PREF_LOGIN_USER, "");
        String userPassword = sharedPreferences.getString(SettingsFragment
                .KEY_PREF_LOGIN_PASSWORD, "");
        String rsaModulus = sharedPreferences.getString(SettingsFragment.KEY_PREF_RSA_MODULUS, "");
        String rsaExponent = sharedPreferences.getString(SettingsFragment.KEY_PREF_RSA_EXPONENT,
                "");
        return new ConnectionConfig(rsaModulus, rsaExponent, serverAddress, port, userLogin,
                userPassword);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        PreferenceManager preferenceManager = getPreferenceManager();
        if (preferenceManager != null) {
            mSharedPreferences = preferenceManager.getSharedPreferences();
            // Update dynamic summaries
            updateServerAddress();
            updateServerPort();
            updateLoginUser();
            updateLoginPassword();
            updateRsaModulus();
            updateRsaExponent();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link android.content.SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_SERVER_ADDRESS)) {
            updateServerAddress();
        } else if (key.equals(KEY_PREF_SERVER_PORT)) {
            updateServerPort();
        } else if (key.equals(KEY_PREF_LOGIN_USER)) {
            updateLoginUser();
        } else if (key.equals(KEY_PREF_LOGIN_PASSWORD)) {
            updateLoginPassword();
        } else if (key.equals(KEY_PREF_RSA_MODULUS)) {
            updateRsaModulus();
        } else if (key.equals(KEY_PREF_RSA_EXPONENT)) {
            updateRsaExponent();
        }
    }

    private void updateServerAddress() {
        Preference preference = findPreference(KEY_PREF_SERVER_ADDRESS);
        if (preference != null) {
            preference.setSummary(mSharedPreferences.getString(KEY_PREF_SERVER_ADDRESS, ""));
        }
    }

    private void updateServerPort() {
        Preference preference = findPreference(KEY_PREF_SERVER_PORT);
        if (preference != null) {
            preference.setSummary(mSharedPreferences.getString(KEY_PREF_SERVER_PORT, ""));
        }
    }

    private void updateLoginUser() {
        Preference preference = findPreference(KEY_PREF_LOGIN_USER);
        if (preference != null) {
            preference.setSummary(mSharedPreferences.getString(KEY_PREF_LOGIN_USER, ""));
        }
    }

    private void updateLoginPassword() {
        Preference preference = findPreference(KEY_PREF_LOGIN_PASSWORD);
        if (preference != null) {
            // Display password as stars
            int length = mSharedPreferences.getString(KEY_PREF_LOGIN_PASSWORD, "").length();
            char[] chars = new char[length];
            Arrays.fill(chars, '*');
            preference.setSummary(new String(chars));
        }
    }

    private void updateRsaModulus() {
        Preference preference = findPreference(KEY_PREF_RSA_MODULUS);
        if (preference != null) {
            // Only display 20 first digits
            String modulus = mSharedPreferences.getString(KEY_PREF_RSA_MODULUS, "");
            if (modulus.length() > 20) {
                modulus = modulus.substring(0, 20) + "...";
            }
            preference.setSummary(modulus);
        }
    }

    private void updateRsaExponent() {
        Preference preference = findPreference(KEY_PREF_RSA_EXPONENT);
        if (preference != null) {
            preference.setSummary(mSharedPreferences.getString(KEY_PREF_RSA_EXPONENT, ""));
        }
    }
}
