package ga.ustre.smartwatchsensor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;
import ga.ustre.smartwatchsensor.R;
import ga.ustre.smartwatchsensor.WebSocketClientManager;
import serialization.action.Action;

public class CodeActivity extends WearableActivity implements WebSocketClientManager.Callbacks{

    private WebSocketClientManager clientManager;
    private String code;
    private TextView firstChar;
    private TextView secondChar;
    private TextView thirdChar;
    private TextView fourthChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        setAmbientEnabled();
        Intent i = getIntent();
        code = i.getStringExtra("code");
        clientManager = (WebSocketClientManager) i.getSerializableExtra("wsclient");
        clientManager.addCallback(this);
        firstChar = (TextView) findViewById(R.id.first_char);
        secondChar = (TextView) findViewById(R.id.second_char);
        thirdChar = (TextView) findViewById(R.id.fourth_char);
        fourthChar = (TextView) findViewById(R.id.fourth_char);
        if(code.length() == 4){
            firstChar.setText(code.charAt(0));
            secondChar.setText(code.charAt(1));
            thirdChar.setText(code.charAt(2));
            fourthChar.setText(code.charAt(3));
        }
        else {
            Intent intent = new Intent(CodeActivity.this, FirstActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }

    @Override
    public void onSuccessfulWebsocketConnection() {

    }

    @Override
    public void onFailedConnection() {

    }

    @Override
    public void onMaxReconnectionAttemptsReached() {

    }

    @Override
    public void onActionReceived(Action action) {

    }
}
