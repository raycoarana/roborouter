package com.raycoarana.roborouter.sample;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.raycoarana.roborouter.RoboRouter;

/*
    Copyright 2014 Rayco Araña

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

public class MainActivity extends Activity {

	private RoboRouter mRoboRouter;
	private AccountManager mAccountManager;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAccountManager = (AccountManager) getSystemService(Activity.ACCOUNT_SERVICE);
		mRoboRouter = RoboRouter.getInstance();

		findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
                runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        doLogout();
                    }
                });
			}
		});
		findViewById(R.id.logout_and_reset).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
                runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        doLogoutAndReset();
                    }
                });
			}
		});
	}

    private void doLogout() {
		deleteAccount();
		mRoboRouter.doneWith(this);
	}

	private void doLogoutAndReset() {
        deleteAccount();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRoboRouter.resetWalkthrough();
                mRoboRouter.doneWith(MainActivity.this);
            }
        });
	}

    private void deleteAccount() {
        //Do the logout stuff
        Account account = new Account("sample", LoginActivity.ACCOUNT_TYPE);
        try {
            mAccountManager.removeAccount(account, null, null).getResult();
        } catch (Exception e) {
            Log.e("roborouter", "Something goes wrong", e);
        }
    }

	private void runOnBackground(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
	}

}