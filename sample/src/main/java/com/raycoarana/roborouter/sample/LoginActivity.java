package com.raycoarana.roborouter.sample;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
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

public class LoginActivity extends Activity {

	public static final String ACCOUNT_TYPE = "com.raycoarana.roborouter.sample.account";

	private AccountManager mAccountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mAccountManager = (AccountManager) getSystemService(Activity.ACCOUNT_SERVICE);

		findViewById(R.id.login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doLogin();
			}
		});
	}

	private void doLogin() {
		//Do your login stuff
		Account account = new Account("sample", ACCOUNT_TYPE);
		mAccountManager.addAccountExplicitly(account, "1234", new Bundle());

		//When done, continue
		RoboRouter.getInstance().doneWith(this);
	}

}