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

import android.app.Activity;
import android.app.Application;

import com.raycoarana.roborouter.activities.LoginActivity;
import com.raycoarana.roborouter.activities.MainActivity;
import com.raycoarana.roborouter.activities.WalkthroughActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RoboRouterBuilderTest {

    private static final String SOME_ACCOUNT_TYPE = "some_account_type";

    private Class<? extends Activity> mLoginScreenClass;
    private Class<? extends Activity> mMainScreenClass;
    private Class<? extends Activity> mWalkthroughScreenClass;
    private Application mApplication;
    private RoboRouter mRoboRouter;

    @Test
    public void shouldBuildRoboRouterWithALoginScreen() {
        givenAMainScreen();
        givenALoginScreen();
        givenAnApplication();
        whenBuildRoboRouter();
        thenTheBuiltRoboRouterHasMainScreen();
        thenTheBuiltRoboRouterHasLoginScreen();
    }

    @Test
    public void shouldBuildRoboRouterWithAWalkthroughScreen() {
        givenAMainScreen();
        givenAWalkthroughScreen();
        givenAnApplication();
        whenBuildRoboRouter();
        thenTheBuiltRoboRouterHasMainScreen();
        thenTheBuiltRoboRouterHasWalkthroughScreen();
    }

    @Test
    public void shouldBuildRoboRouterWithAllScreens() {
        givenAMainScreen();
        givenALoginScreen();
        givenAWalkthroughScreen();
        givenAnApplication();
        whenBuildRoboRouter();
        thenTheBuiltRoboRouterHasLoginScreen();
        thenTheBuiltRoboRouterHasMainScreen();
        thenTheBuiltRoboRouterHasWalkthroughScreen();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNoMainScreenIsProvided() {
        givenAWalkthroughScreen();
        givenAnApplication();
        whenBuildRoboRouter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfOnlyMainScreenIsProvided() {
        givenAMainScreen();
        givenAnApplication();
        whenBuildRoboRouter();
    }

    private void givenAMainScreen() {
        mMainScreenClass = MainActivity.class;
    }

    private void givenALoginScreen() {
        mLoginScreenClass = LoginActivity.class;
    }

    private void givenAWalkthroughScreen() {
        mWalkthroughScreenClass = WalkthroughActivity.class;
    }

    private void givenAnApplication() {
        mApplication = Robolectric.application;
    }

    private void whenBuildRoboRouter() {
        mRoboRouter = RoboRouterBuilder.from(mApplication)
                                       .addLoginScreen(mLoginScreenClass, SOME_ACCOUNT_TYPE)
                                       .addMainScreen(mMainScreenClass)
                                       .addWalkthroughScreen(mWalkthroughScreenClass)
                                       .build();
    }

    private void thenTheBuiltRoboRouterHasMainScreen() {
        assertNotNull(mRoboRouter.mMainActivityName);
        assertEquals(mMainScreenClass.getName(),
                     mRoboRouter.mMainActivityName.getClassName());
    }

    private void thenTheBuiltRoboRouterHasLoginScreen() {
        assertNotNull(mRoboRouter.mLoginActivityName);
        assertEquals(mLoginScreenClass.getName(),
                mRoboRouter.mLoginActivityName.getClassName());
    }

    private void thenTheBuiltRoboRouterHasWalkthroughScreen() {
        assertNotNull(mRoboRouter.mWalkthroughActivityName);
        assertEquals(mWalkthroughScreenClass.getName(),
                mRoboRouter.mWalkthroughActivityName.getClassName());
    }

}
