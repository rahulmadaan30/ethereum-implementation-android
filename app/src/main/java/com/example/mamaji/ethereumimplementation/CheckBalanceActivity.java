package com.example.mamaji.ethereumimplementation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import de.petendi.ethereum.android.EthereumAndroid;
import de.petendi.ethereum.android.EthereumAndroidCallback;
import de.petendi.ethereum.android.EthereumAndroidFactory;
import de.petendi.ethereum.android.EthereumNotInstalledException;
import de.petendi.ethereum.android.Utils;
import de.petendi.ethereum.android.service.model.RpcCommand;
import de.petendi.ethereum.android.service.model.WrappedRequest;
import de.petendi.ethereum.android.service.model.WrappedResponse;

public class CheckBalanceActivity extends AppCompatActivity {
    private AutoCompleteTextView accountInput;
    private View progressView;
    private View formView;
    private EthereumAndroid ethereumAndroid;

    private class MyAsyncTask extends AsyncTask<Void,Void,WrappedResponse> {
        private final WrappedRequest request;


        private MyAsyncTask(WrappedRequest request) {
            this.request = request;
        }


        @Override
        protected WrappedResponse doInBackground(Void... voids) {
            return ethereumAndroid.send(request);
        }

        @Override
        protected void onPostExecute(final WrappedResponse wrappedResponse) {
            showResponse(wrappedResponse);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);
        formView = findViewById(R.id.form);
        progressView = findViewById(R.id.progress);
        try {
            Field devField = EthereumAndroidFactory.class.getDeclaredField("DEV");
            devField.setAccessible(true);
            devField.set(null,true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        EthereumAndroidFactory ethereumAndroidFactory = new EthereumAndroidFactory(this);
        try {
            ethereumAndroid = ethereumAndroidFactory.create((EthereumAndroidCallback) this);
        } catch (EthereumNotInstalledException e) {
            Toast.makeText(this,R.string.ethereum_ethereum_not_installed,Toast.LENGTH_LONG).show();
            finish();
        }
        accountInput =  findViewById(R.id.address_input);
        Button requestButton =  findViewById(R.id.request_account);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request();
            }
        });
    }

    private void request() {

        accountInput.setError(null);
        String accountAddress = accountInput.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(accountAddress)) {
            accountInput.setError(getString(R.string.error_field_required));
            focusView = accountInput;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            WrappedRequest wrappedRequest = new WrappedRequest();
            wrappedRequest.setCommand(RpcCommand.eth_getBalance.toString());
            wrappedRequest.setParameters(new String[]{accountAddress, "latest"});
            ethereumAndroid.sendAsync(wrappedRequest);
        }
    }

    private void showResponse(final WrappedResponse response) {
        showProgress(false);
        TextView balanceTextView = (TextView) findViewById(R.id.account_balance);
        if(response.isSuccess()) {
            String balance = getString(R.string.balance);
            balanceTextView.setText(balance + " " + Utils.fromHexString((String)response.getResponse()));
        } else {
            balanceTextView.setText(response.getErrorMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            formView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
