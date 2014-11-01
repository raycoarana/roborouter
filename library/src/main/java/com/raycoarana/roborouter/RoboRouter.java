package com.raycoarana.roborouter;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

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

public class RoboRouter {

	private static final String ROBO_ROUTER_PREFERENCES = "robo_router_preferences";
	private static final String KEY_WALKTHROUGH_ALREADY_SHOWN = "key_walkthrough_already_shown";

	static RoboRouter sInstance;

	public static RoboRouter getInstance() {
		if(sInstance == null) {
			throw new IllegalStateException("RoboRouter not initialized, did you call RoboRouterBuilder in your Application class?");
		}
		return sInstance;
	}

	private final SharedPreferences mSharedPreferences;
	private final AccountManager mAccountManager;
	private final PackageManager mPackageManager;
	private final String mAccountType;

	ComponentName mLoginActivityName;
	ComponentName mWalkthroughActivityName;
	ComponentName mMainActivityName;

	RoboRouter(Application application, String accountType) {
		mPackageManager = application.getPackageManager();
		mAccountManager = (AccountManager) application.getSystemService(Application.ACCOUNT_SERVICE);
		mAccountType = accountType;

		mSharedPreferences = application.getSharedPreferences(ROBO_ROUTER_PREFERENCES, Context.MODE_PRIVATE);
		application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
	}

	/**
	 * This is the method to call when your user is done with one of the activities involved (Login,
	 * Walkthrough or Main).
	 *
	 * Ex:
	 *  - Your user ends with the walkthrough? Call doneWith() to transition to the Login/Main
	 * activity.
	 *  - Your user has successfully logged in? Call doneWith() to transition to the Main activity,
	 * remember to create a user account in AccountManager first.
	 *  - Your user logs out? Call doneWith() once you have deleted the user account from AccountManager to
	 *  transition to the Login activity. If you want to transition to the Walkthrough instead of the Login activity,
	 *  call resetWalkthrough() before doneWith().
	 *
	 * @param activity your current activity
	 */
	public void doneWith(Activity activity) {
        doneWith(activity, false);
    }

    private void doneWith(Activity activity, boolean isFromPackageManager) {
		ComponentName name = activity.getComponentName();
		if(name.equals(mLoginActivityName)) {
            doneWithLoginActivity(activity);
        } else if(name.equals(mWalkthroughActivityName)) {
            doneWithWalkthroughActivity(activity, isFromPackageManager);
        } else {
            doneWithActivity(activity);
        }
	}

    private void doneWithLoginActivity(Activity activity) {
        if(hasWalkthrough() && !walkthroughAlreadyShown()) {
            enableWalkthroughActivity();
            disableLoginActivity();
            startWalkthroughActivity(activity);
        } else if(someAccountExists()) {
            enableMainActivity();
            disableLoginActivity();
            startMainActivity(activity);
        }
    }

    private void doneWithWalkthroughActivity(Activity activity, boolean isFromPackageManager) {
        if(walkthroughAlreadyShown() && !isFromPackageManager) {
            if (hasLogin() && !someAccountExists()) {
                enableLoginActivity();
                disableWalkthroughActivity();
                startLoginActivity(activity);
            } else {
                enableMainActivity();
                disableWalkthroughActivity();
                startMainActivity(activity);
            }
        } else {
            walkthroughWasShow();
        }
    }

    private void doneWithActivity(Activity activity) {
        boolean walkthroughNeverShown = !walkthroughAlreadyShown();
        boolean dontHasAnyAccount = !someAccountExists();
        if(walkthroughNeverShown || dontHasAnyAccount) {
            disableMainActivity();
            if(hasWalkthrough() && walkthroughNeverShown) {
                enableWalkthroughActivity();
                startWalkthroughActivity(activity);
            } else {
                enableLoginActivity();
                startLoginActivity(activity);
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
	/**
	 * Resets the walkthrough so instead of showing the login screen, it will display the Walkthrough once the user
	 * has logout.
	 */
	public void resetWalkthrough() {
		mSharedPreferences.edit()
				.remove(KEY_WALKTHROUGH_ALREADY_SHOWN)
				.commit();
	}

	private boolean walkthroughAlreadyShown() {
		return mSharedPreferences.contains(KEY_WALKTHROUGH_ALREADY_SHOWN);
	}

	@SuppressLint("CommitPrefEdits")
	private void walkthroughWasShow() {
		mSharedPreferences.edit()
				.putBoolean(KEY_WALKTHROUGH_ALREADY_SHOWN, false)
				.commit();
	}

	private boolean hasWalkthrough() {
		return mWalkthroughActivityName != null;
	}

	private boolean hasLogin() {
		return mLoginActivityName != null;
	}

	private boolean someAccountExists() {
		return mAccountManager.getAccountsByType(mAccountType).length > 0;
	}

	private void enableWalkthroughActivity() {
		enable(mWalkthroughActivityName);
	}

	private void disableWalkthroughActivity() {
		disable(mWalkthroughActivityName);
	}

	private void startWalkthroughActivity(Activity from) {
		Intent intent = Intent.makeMainActivity(mWalkthroughActivityName);
		from.startActivity(intent);
		from.finish();
	}

	private void enableLoginActivity() {
		enable(mLoginActivityName);
	}

	private void disableLoginActivity() {
		disable(mLoginActivityName);
	}

	private void startLoginActivity(Activity from) {
		Intent intent = Intent.makeMainActivity(mLoginActivityName);
		from.startActivity(intent);
		from.finish();
		walkthroughWasShow();
	}

	private void enableMainActivity() {
		enable(mMainActivityName);
	}

	private void disableMainActivity() {
		disable(mMainActivityName);
	}

	private void startMainActivity(Activity from) {
		Intent intent = Intent.makeMainActivity(mMainActivityName);
		from.startActivity(intent);
		from.finish();
		walkthroughWasShow();
	}

	private void enable(ComponentName componentName) {
		if(mPackageManager.getComponentEnabledSetting(componentName) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
			mPackageManager.setComponentEnabledSetting(componentName,
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
					PackageManager.DONT_KILL_APP);
		}
	}

	private void disable(ComponentName componentName) {
		if(mPackageManager.getComponentEnabledSetting(componentName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
			mPackageManager.setComponentEnabledSetting(componentName,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
		}
	}

	@SuppressWarnings("FieldCanBeLocal")
	private final ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {

		@Override
		public void onActivityCreated(Activity activity, Bundle bundle) {
			doneWith(activity, true);
		}

		@Override
		public void onActivityStarted(Activity activity) {}

		@Override
		public void onActivityResumed(Activity activity) {}

		@Override
		public void onActivityPaused(Activity activity) {}

		@Override
		public void onActivityStopped(Activity activity) {}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

		@Override
		public void onActivityDestroyed(Activity activity) {}

	};

}