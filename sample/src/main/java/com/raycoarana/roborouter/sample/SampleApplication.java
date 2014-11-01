package com.raycoarana.roborouter.sample;

import android.app.Application;

import com.raycoarana.roborouter.RoboRouterBuilder;

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

public class SampleApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		RoboRouterBuilder.from(this)
						 .addLoginScreen(LoginActivity.class, LoginActivity.ACCOUNT_TYPE)
						 .addWalkthroughScreen(WalkthroughActivity.class)
						 .addMainScreen(MainActivity.class)
						 .build();
	}

}