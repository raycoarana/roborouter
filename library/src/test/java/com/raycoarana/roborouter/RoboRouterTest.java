package com.raycoarana.roborouter;

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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.raycoarana.roborouter.activities.LoginActivity;
import com.raycoarana.roborouter.activities.MainActivity;
import com.raycoarana.roborouter.activities.WalkthroughActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RoboRouterTest {

    private static final String SOME_ACCOUNT_TYPE = "some_account_type";

    @Mock
    private ComponentName mMainActivityName;
    @Mock
    private ComponentName mLoginActivityName;
    @Mock
    private ComponentName mWalkthroughActivityName;
    @Mock
    private MainActivity mMainActivity;
    @Mock
    private LoginActivity mLoginActivity;
    @Mock
    private WalkthroughActivity mWalkthroughActivity;
    @Mock
    private SharedPreferences mSharedPreferences;
    @Mock
    private SharedPreferences.Editor mSharedPreferencesEditor;
    @Mock
    private AccountManager mAccountManager;
    @Mock
    private PackageManager mPackageManager;
    @Mock
    private Application mApplication;
    @Captor
    private ArgumentCaptor<Intent> mIntentCaptor;
    @Captor
    private ArgumentCaptor<Application.ActivityLifecycleCallbacks> mActivityLifecycleCallbacksCaptor;

    private Activity mCurrentActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RoboRouter.sInstance = null;
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToGetInstaceIfBuilderNotCalled() {
        RoboRouter.getInstance();
    }

    @Test
    public void shouldLaunchLoginActivityAfterWalkthrough() {
        givenAMockApplication();
        givenARoboRouter();
        givenThatNoAccountExists();
        givenThatRoboRouterHaveAMainScreen();
        givenThatRoboRouterHaveALoginScreen();
        givenThatRoboRouterHaveAWalkthroughScreen();
        givenThatWalkthroughIsTheCurrentActivity();
        thenWalkthroughActivityWasShown();
        whenDoneWithWalkthroughActivity();
        thenWalkthroughActivityIsDeactivated();
        thenLoginActivityIsStarted();
    }

    @Test
    public void shouldLaunchMainActivityAfterWalkthrough() {
        givenAMockApplication();
        givenARoboRouter();
        givenThatNoAccountExists();
        givenThatRoboRouterHaveAMainScreen();
        givenThatRoboRouterHaveAWalkthroughScreen();
        givenThatWalkthroughIsTheCurrentActivity();
        thenWalkthroughActivityWasShown();
        whenDoneWithWalkthroughActivity();
        thenWalkthroughActivityIsDeactivated();
        thenMainActivityIsStarted();
    }

    @Test
    public void shouldLaunchMainActivityAfterLogin() {
        givenAMockApplication();
        givenARoboRouter();
        givenThatSomeAccountExists();
        givenThatRoboRouterHaveAMainScreen();
        givenThatRoboRouterHaveALoginScreen();
        givenThatLoginIsTheCurrentActivity();
        whenDoneWithWalkthroughActivity();
        thenLoginActivityIsDeactivated();
        thenMainActivityIsStarted();
    }

    @Test
    public void shouldLaunchWalkthroughActivityIfNeverShown() {
        givenAMockApplication();
        givenARoboRouter();
        givenThatRoboRouterHaveAMainScreen();
        givenThatRoboRouterHaveAWalkthroughScreen();
        givenThatRoboRouterHaveALoginScreen();
        whenLoginActivityIsTheCurrentActivity();
        thenLoginActivityIsDeactivated();
        thenWalkthroughActivityIsActivated();
        thenWalkthroughActivityIsStarted();
    }

    @Test
    public void shouldLaunchLoginActivityIfNoAccountIsFound() {
        givenAMockApplication();
        givenARoboRouter();
        givenThatNoAccountExists();
        givenThatRoboRouterHaveAMainScreen();
        givenThatRoboRouterHaveAWalkthroughScreen();
        givenThatRoboRouterHaveALoginScreen();
        givenThatWalkthroughActivityWasShown();
        whenMainActivityIsTheCurrentActivity();
        thenMainActivityIsDeactivated();
        thenLoginActivityIsActivated();
        thenLoginActivityIsStarted();
    }

    @Test
    public void shouldKeepMainActivityIfExistsAccountsAndWalkthroughtWasShown() {
        givenAMockApplication();
        givenARoboRouter();
        givenThatSomeAccountExists();
        givenThatRoboRouterHaveAMainScreen();
        givenThatRoboRouterHaveAWalkthroughScreen();
        givenThatRoboRouterHaveALoginScreen();
        givenThatWalkthroughActivityWasShown();
        whenMainActivityIsTheCurrentActivity();
        thenNoActivityIsDeactivatedOrActivated();
        thenNoActivityIsStarted();
    }

    @SuppressLint("CommitPrefEdits")
    private void givenAMockApplication() {
        when(mApplication.getPackageManager()).thenReturn(mPackageManager);
        when(mApplication.getSystemService(Context.ACCOUNT_SERVICE)).thenReturn(mAccountManager);
        when(mApplication.getSharedPreferences(anyString(), anyInt())).thenReturn(mSharedPreferences);
        when(mSharedPreferences.edit()).thenReturn(mSharedPreferencesEditor);
        when(mSharedPreferencesEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mSharedPreferencesEditor);
        doNothing().when(mApplication).registerActivityLifecycleCallbacks(mActivityLifecycleCallbacksCaptor.capture());
    }

    private void givenARoboRouter() {
        RoboRouter.sInstance = new RoboRouter(mApplication, SOME_ACCOUNT_TYPE);
    }

    private void givenThatNoAccountExists() {
        when(mAccountManager.getAccountsByType(SOME_ACCOUNT_TYPE)).thenReturn(new Account[]{});
    }

    private void givenThatSomeAccountExists() {
        when(mAccountManager.getAccountsByType(SOME_ACCOUNT_TYPE)).thenReturn(new Account[]{ mock(Account.class)});
    }

    private void givenThatRoboRouterHaveAMainScreen() {
        RoboRouter.sInstance.mMainActivityName = mMainActivityName;
        when(mMainActivity.getComponentName()).thenReturn(mMainActivityName);
    }

    private void givenThatRoboRouterHaveALoginScreen() {
        RoboRouter.sInstance.mLoginActivityName = mLoginActivityName;
        when(mLoginActivity.getComponentName()).thenReturn(mLoginActivityName);
    }

    private void givenThatRoboRouterHaveAWalkthroughScreen() {
        RoboRouter.sInstance.mWalkthroughActivityName = mWalkthroughActivityName;
        when(mWalkthroughActivity.getComponentName()).thenReturn(mWalkthroughActivityName);
    }

    private void givenThatWalkthroughIsTheCurrentActivity() {
        mCurrentActivity = mWalkthroughActivity;
        mActivityLifecycleCallbacksCaptor.getValue().onActivityCreated(mCurrentActivity, null);
    }

    private void whenLoginActivityIsTheCurrentActivity() {
        mCurrentActivity = mLoginActivity;
        mActivityLifecycleCallbacksCaptor.getValue().onActivityCreated(mCurrentActivity, null);
    }

    private void whenMainActivityIsTheCurrentActivity() {
        mCurrentActivity = mMainActivity;
        mActivityLifecycleCallbacksCaptor.getValue().onActivityCreated(mCurrentActivity, null);
    }

    private void givenThatLoginIsTheCurrentActivity() {
        mCurrentActivity = mLoginActivity;
    }

    private void givenThatWalkthroughActivityWasShown() {
        when(mSharedPreferences.contains(RoboRouter.KEY_WALKTHROUGH_ALREADY_SHOWN)).thenReturn(true);
    }

    private void whenDoneWithWalkthroughActivity() {
        RoboRouter.getInstance().doneWith(mCurrentActivity);
    }

    private void thenWalkthroughActivityWasShown() {
        verify(mSharedPreferencesEditor).putBoolean(eq(RoboRouter.KEY_WALKTHROUGH_ALREADY_SHOWN), anyBoolean());
        givenThatWalkthroughActivityWasShown();
    }

    private void thenWalkthroughActivityIsDeactivated() {
        verifyComponentIsDisabled(mWalkthroughActivityName);
    }

    private void thenWalkthroughActivityIsActivated() {
        verifyComponentIsEnable(mWalkthroughActivityName);
    }

    private void thenLoginActivityIsDeactivated() {
        verifyComponentIsDisabled(mLoginActivityName);
    }

    private void thenLoginActivityIsActivated() {
        verifyComponentIsEnable(mLoginActivityName);
    }

    private void thenMainActivityIsDeactivated() {
        verifyComponentIsDisabled(mMainActivityName);
    }

    private void thenNoActivityIsDeactivatedOrActivated() {
        verify(mPackageManager, never()).setComponentEnabledSetting(any(ComponentName.class),
                anyInt(),
                anyInt());
    }

    private void verifyComponentIsDisabled(ComponentName componentName) {
        verify(mPackageManager).setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void verifyComponentIsEnable(ComponentName componentName) {
        verify(mPackageManager).setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void thenLoginActivityIsStarted() {
        verifyActivityIsStarted(mLoginActivityName);
    }

    private void thenMainActivityIsStarted() {
        verifyActivityIsStarted(mMainActivityName);
    }

    private void thenWalkthroughActivityIsStarted() {
        verifyActivityIsStarted(mWalkthroughActivityName);
    }

    private void thenNoActivityIsStarted() {
        verify(mCurrentActivity, never()).startActivity(any(Intent.class));
    }

    private void verifyActivityIsStarted(ComponentName componentName) {
        verify(mCurrentActivity).startActivity(mIntentCaptor.capture());
        Intent intent = mIntentCaptor.getValue();
        assertEquals(componentName, intent.getComponent());
    }

}
