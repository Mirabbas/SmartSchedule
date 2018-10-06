package mirabbas.smartschedule;

/**
 * Created by о on 15.02.2018.
 */

public interface Registration {

    boolean isValidate();
    void createAccount(String email, String password);
}
