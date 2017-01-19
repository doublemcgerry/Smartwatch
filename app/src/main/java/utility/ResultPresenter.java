package utility;

/**
 * Created by achelius on 16/01/2017.
 */

public interface ResultPresenter {
    void publishMessage(String message);
    void showProgressBar();
    void hideProgressBar();
    void showIcon(int drawable);
    void hideIcon();
}
