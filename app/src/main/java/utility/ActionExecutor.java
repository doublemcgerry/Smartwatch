package utility;

/**
 * Created by achelius on 16/01/2017.
 */

public interface ActionExecutor {
    void publishMessage(String message);
    void showProgressBar();
    void hideProgressBar();
    void showIcon(int drawable);
    void hideIcon();
    void triggerVibration();
    void saveCode(String code);
    void changeContext(boolean firstContext);
}
