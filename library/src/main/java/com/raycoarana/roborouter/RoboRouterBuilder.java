package com.raycoarana.roborouter;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;

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

public class RoboRouterBuilder {

	private final Application mApplication;
	private Class<? extends Activity> mLoginActivityClass;
	private Class<? extends Activity> mWalkthroughActivityClass;
	private Class<? extends Activity> mMainActivityClass;
	private String mAccountType;

	private RoboRouterBuilder(Application application) {
		mApplication = application;
	}

	public static RoboRouterBuilder from(Application application) {
		return new RoboRouterBuilder(application);
	}

	/**
	 * Adds a login screen to the router, associated with an Account Type. The screen will be showed to the user
	 * if no account is found. When login is complete, you have to call RoboRouter.loginComplete().
	 *
	 * @param loginActivityClass class of the login Activity screen. This activity must have an activity-alias in the
	 * AndroidManifest.xml with an Intent-Filter for LAUNCHER action.
	 * @param accountType account type to look for, if no account is found, the login screen is activated
	 *
     * @return this RoboRouterBuilder isntance
	 */
	public RoboRouterBuilder addLoginScreen(Class<? extends Activity> loginActivityClass, String accountType) {
		mLoginActivityClass = loginActivityClass;
		mAccountType = accountType;
		return this;
	}

	/**
	 * Adds a walkthrough screen to the router. This screen will be showed the first time the application is
	 * launched, once completed you call RoboRouter.endWalkthrough() to navigate to the appropriate screen (login or
	 * main). If you want to show it again, call RoboRouter.restartWalkthrough().
	 *
	 * @param walkthroughActivityClass class of the walkthrough Activity screen. This activity must have an activity-alias in the
	 * AndroidManifest.xml with an Intent-Filter for LAUNCHER action.
	 *
     * @return this RoboRouterBuilder isntance
	 */
	public RoboRouterBuilder addWalkthroughScreen(Class<? extends Activity> walkthroughActivityClass) {
		mWalkthroughActivityClass = walkthroughActivityClass;
		return this;
	}

	/**
	 * Adds the main screen of the application to the router. It's mandatory to have at least one main screen.
	 *
	 * @param mainActivityClass class of the walkthrough Activity screen. This activity must have an activity-alias
	 * in the AndroidManifest.xml with an Intent-Filter for LAUNCHER action.
     *
     * @return this RoboRouterBuilder isntance
	 */
	public RoboRouterBuilder addMainScreen(Class<? extends Activity> mainActivityClass) {
		mMainActivityClass = mainActivityClass;
		return this;
	}

    /**
     * Builds a RoboRouter instance. RoboRouter is a singleton so any new call to this method will
     * create a new RoboRouter singleton.
     *
     * @return the created RoboRouter
     */
	public RoboRouter build() {
		checkMainActivity();
		checkAtLeastOtherActivity();

		RoboRouter roboRouter = new RoboRouter(mApplication, mAccountType);
		roboRouter.mLoginActivityName = new ComponentName(mApplication, mLoginActivityClass);
		roboRouter.mMainActivityName = new ComponentName(mApplication, mMainActivityClass);
		roboRouter.mWalkthroughActivityName = new ComponentName(mApplication, mWalkthroughActivityClass);
		RoboRouter.sInstance = roboRouter;
		return roboRouter;
	}

	private void checkMainActivity() {
		if(mMainActivityClass == null) {
			throw new IllegalArgumentException("A MainActivity is mandatory for RoboRouter to work");
		}
	}

	private void checkAtLeastOtherActivity() {
		if(mLoginActivityClass == null && mWalkthroughActivityClass == null) {
			throw new IllegalArgumentException("Without a login or walkthrough activity, RoboRouter have no sense!!");
		}
	}

}