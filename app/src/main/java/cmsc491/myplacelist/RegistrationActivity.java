package cmsc491.myplacelist;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class RegistrationActivity extends ActionBarActivity {

    private EditText mUsername, mPassword, mEmail;
    private Button mSignUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEmail = (EditText) findViewById(R.id.emailField);
        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mSignUpBtn = (Button) findViewById(R.id.signUpBtn);

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString();

                // validate input
                if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this)
                            .setTitle("Oops!")
                            .setMessage("Please enter an email, username, and password.")
                            .setPositiveButton(android.R.string.ok, null);
                    builder.create().show();
                } else {
                    ParseUser newUser = new ParseUser();
                    newUser.setEmail(email);
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    setProgressBarIndeterminateVisibility(true);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if(e == null){
                                Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this)
                                        .setTitle("Oops!")
                                        .setMessage(e.getMessage())
                                        .setPositiveButton(android.R.string.ok, null);
                                builder.create().show();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
