package com.kevinlu.airquality;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class OnboardingActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Note here that we DO NOT use setContentView();

        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.
        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitleColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        sliderPage1.setTitle("Hey there! Looks like it's your first time here.");
        sliderPage1.setDescColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        sliderPage1.setDescription("Let's guide you through the app!");
        sliderPage1.setImageDrawable(R.drawable.circle_background_green);
        sliderPage1.setBgColor(getResources().getColor(R.color.backgroundcolor));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Setup: Permissions");
        sliderPage2.setDescription("We need your help! Grant the app location and storage permissions.");
        sliderPage2.setImageDrawable(R.drawable.circle_background_yellow);
        sliderPage2.setBgColor(getResources().getColor(R.color.backgroundcolor));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.colorPrimary));
        setSeparatorColor(getResources().getColor(R.color.colorPrimaryLight));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);

        setDepthAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
