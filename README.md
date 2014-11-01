RoboRouter
==========

RoboRouter is a little utility library to manage your start-up activities in a efficient and easy way.
A typical application have a Walkthrough activity and/or a Login activity that are shown to the user
only on time, so why adding code in your Main activity to manage this over and over again?

Prevent filling your Main activity with code to protect the initialize of the application when for
the first case. Prevent the creation of Singletons, inject of views or any component that the user
will not see until he look at the Walkthrough and/or login in your app.

How to use it?
--------------

The first step is to add create your activities, add it to the AndroidManifest.xml and make Login
and Main activities disabled by default. It's important that all three of your activities (or two
if your app doesn't have a walkthrough or login) have the launcher intent filter.

```xml
<activity android:name=".WalkthroughActivity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity android:name=".LoginActivity" android:enabled="false">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity android:name=".MainActivity" android:enabled="false">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

Now we initialize RoboRouter, we have to do this in the Application class of our app. In this case,
we are adding a Login screen associated with an android account type and a Walkthrough screen.

```java
public class SampleApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		RoboRouterBuilder.from(this)
						 .addWalkthroughScreen(WalkthroughActivity.class)
						 .addLoginScreen(LoginActivity.class, LoginActivity.ACCOUNT_TYPE)
						 .addMainScreen(MainActivity.class)
						 .build();
	}

}
```

So when the user starts for the first time the app, the Walkthrough activity is launched. Then when
the user is done with it, we will take them to the Login activity. For that, all we have to do is
call RoboRouter on the event that have to fire the transition.

```java
public class WalkthroughActivity extends Activity {

    ...
    RoboRouter.getInstance().doneWith(this);
	...

}
```

Once the user is at the Login activity and does the login successfully, we have to do two things
now. First we should create an android account with the user credentials. Then, once we have all
our authentication and credentials stuff done, all we have to do again is call RoboRouter to get the
user to the Main activity.

```java
	private void doLogin() {
		//Do your login stuff
		Account account = new Account("sample", ACCOUNT_TYPE);
		mAccountManager.addAccountExplicitly(account, "1234", new Bundle());

		//When done, continue
		RoboRouter.getInstance().doneWith(this);
	}
```

And now the user will be at the Main activity. The good thing is that in your Main activity you
don't have to worry about your Login or Walkthrough activities, now the Main activity is the only
activity called by Android launcher.

If you want to logout the user, simply delete the user account and call RoboRouter.

If you want to take the user again to the Walkthrough, before calling doneWith(), make a call to
the method resetWalkthrough().

```java
	private void doLogout() {
	    deleteUserAccount();
		mRoboRouter.resetWalkthrough();
        mRoboRouter.doneWith(this);
	}
```

Take a look at the Sample project!

License
-------

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