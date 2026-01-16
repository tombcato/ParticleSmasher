package com.fadai.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.tombcato.particlesmasher.ParticleSmasher;
import com.tombcato.particlesmasher.SmashAnimator;
import com.tombcato.particlesmasher.Utils;

public class MainActivity extends AppCompatActivity {

    private ParticleSmasher mSmasher;
    private ImageView mIvTest, mIvCover;

    // Controls
    private Spinner mSpinnerStyle, mSpinnerShape, mSpinnerInterpolator, mSpinnerScaleMode;
    private SeekBar mSeekDuration, mSeekStartDelay, mSeekHorizontal, mSeekVertical, mSeekRadius, mSeekGap;
    private SeekBar mSeekStartRandomness, mSeekEndRandomness;
    private TextView mTvDuration, mTvStartDelay, mTvHorizontal, mTvVertical, mTvRadius, mTvGap;
    private TextView mTvStartRandomness, mTvEndRandomness;
    private Switch mSwitchHideAnim;
    private Button mBtnStart, mBtnSave;

    private static final String PREFS_NAME = "ParticleSmasherConfig";

    // Style options
    private static final String[] STYLE_NAMES = {
            "爆炸 (EXPLOSION)",
            "下落 (DROP)",
            "从左往右飘落 (FLOAT_LEFT)",
            "从右往左飘落 (FLOAT_RIGHT)",
            "从上往下飘落 (FLOAT_TOP)",
            "从下往上飘落 (FLOAT_BOTTOM)",
            "向上飘散 (RISE)",
            "从左往右向上 (RISE_LEFT)",
            "从右往左向上 (RISE_RIGHT)",
            "从上往下向上 (RISE_TOP)"
    };
    private static final int[] STYLE_VALUES = {
            SmashAnimator.STYLE_EXPLOSION,
            SmashAnimator.STYLE_DROP,
            SmashAnimator.STYLE_FLOAT_LEFT,
            SmashAnimator.STYLE_FLOAT_RIGHT,
            SmashAnimator.STYLE_FLOAT_TOP,
            SmashAnimator.STYLE_FLOAT_BOTTOM,
            SmashAnimator.STYLE_RISE,
            SmashAnimator.STYLE_RISE_LEFT,
            SmashAnimator.STYLE_RISE_RIGHT,
            SmashAnimator.STYLE_RISE_TOP
    };

    private static final String[] SHAPE_NAMES = {"圆形 (CIRCLE)", "方形 (SQUARE)"};
    private static final int[] SHAPE_VALUES = {SmashAnimator.SHAPE_CIRCLE, SmashAnimator.SHAPE_SQUARE};

    // Scale Mode options
    private static final String[] SCALE_MODE_NAMES = {"逐渐变小 (DOWN)", "大小不变 (SAME)", "逐渐变大 (UP)"};
    private static final int[] SCALE_MODE_VALUES = {SmashAnimator.SCALE_DOWN, SmashAnimator.SCALE_SAME, SmashAnimator.SCALE_UP};

    // Interpolator options
    private static final String[] INTERPOLATOR_NAMES = {
            "加速 (Accelerate)",
            "减速 (Decelerate)",
            "线性 (Linear)",
            "先加后减 (AccelerateDecelerate)"
    };
    private static final Interpolator[] INTERPOLATOR_VALUES = {
            new AccelerateInterpolator(0.6f),
            new DecelerateInterpolator(),
            new LinearInterpolator(),
            new AccelerateDecelerateInterpolator()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏，内容延伸到状态栏，并设置状态栏图标为深色（黑色）
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_main);
        mSmasher = new ParticleSmasher(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        mIvTest = findViewById(R.id.iv_test);
        mIvCover = findViewById(R.id.iv_cover);

        mSpinnerStyle = findViewById(R.id.spinner_style);
        mSpinnerShape = findViewById(R.id.spinner_shape);
        mSpinnerInterpolator = findViewById(R.id.spinner_interpolator);
        mSpinnerScaleMode = findViewById(R.id.spinner_scale_mode);

        mSeekDuration = findViewById(R.id.seek_duration);
        mSeekStartDelay = findViewById(R.id.seek_start_delay);
        mSeekHorizontal = findViewById(R.id.seek_horizontal);
        mSeekVertical = findViewById(R.id.seek_vertical);
        mSeekRadius = findViewById(R.id.seek_radius);
        mSeekGap = findViewById(R.id.seek_gap);
        mSeekStartRandomness = findViewById(R.id.seek_start_randomness);
        mSeekEndRandomness = findViewById(R.id.seek_end_randomness);

        mTvDuration = findViewById(R.id.tv_duration);
        mTvStartDelay = findViewById(R.id.tv_start_delay);
        mTvHorizontal = findViewById(R.id.tv_horizontal);
        mTvVertical = findViewById(R.id.tv_vertical);
        mTvRadius = findViewById(R.id.tv_radius);
        mTvGap = findViewById(R.id.tv_gap);
        mTvStartRandomness = findViewById(R.id.tv_start_randomness);
        mTvEndRandomness = findViewById(R.id.tv_end_randomness);

        mSwitchHideAnim = findViewById(R.id.switch_hide_anim);
        mBtnStart = findViewById(R.id.btn_start);
        mBtnSave = findViewById(R.id.btn_save);

        // Setup Spinners
        mSpinnerStyle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, STYLE_NAMES));
        mSpinnerShape.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SHAPE_NAMES));
        mSpinnerInterpolator.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, INTERPOLATOR_NAMES));
        mSpinnerScaleMode.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SCALE_MODE_NAMES));

        // Default selections
        mSpinnerStyle.setSelection(6);  // STYLE_RISE
        mSpinnerInterpolator.setSelection(0);  // Accelerate

        // Load saved config
        loadConfig();
    }

    private void saveConfig() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("style", mSpinnerStyle.getSelectedItemPosition());
        editor.putInt("shape", mSpinnerShape.getSelectedItemPosition());
        editor.putInt("interpolator", mSpinnerInterpolator.getSelectedItemPosition());
        editor.putInt("scaleMode", mSpinnerScaleMode.getSelectedItemPosition());
        editor.putInt("duration", mSeekDuration.getProgress());
        editor.putInt("startDelay", mSeekStartDelay.getProgress());
        editor.putInt("horizontal", mSeekHorizontal.getProgress());
        editor.putInt("vertical", mSeekVertical.getProgress());
        editor.putInt("radius", mSeekRadius.getProgress());
        editor.putInt("gap", mSeekGap.getProgress());
        editor.putInt("startRandomness", mSeekStartRandomness.getProgress());
        editor.putInt("endRandomness", mSeekEndRandomness.getProgress());
        editor.putBoolean("hideAnim", mSwitchHideAnim.isChecked());
        editor.apply();
        Toast.makeText(this, "配置已保存", Toast.LENGTH_SHORT).show();
    }

    private void loadConfig() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mSpinnerStyle.setSelection(prefs.getInt("style", 6));
        mSpinnerShape.setSelection(prefs.getInt("shape", 0));
        mSpinnerInterpolator.setSelection(prefs.getInt("interpolator", 0));
        mSpinnerScaleMode.setSelection(prefs.getInt("scaleMode", 0));
        mSeekDuration.setProgress(prefs.getInt("duration", 1000));
        mSeekStartDelay.setProgress(prefs.getInt("startDelay", 150));
        mSeekHorizontal.setProgress(prefs.getInt("horizontal", 10));
        mSeekVertical.setProgress(prefs.getInt("vertical", 40));
        mSeekRadius.setProgress(prefs.getInt("radius", 2));
        mSeekGap.setProgress(prefs.getInt("gap", 10)); // Default 10 = 0dp gap
        mSeekStartRandomness.setProgress(prefs.getInt("startRandomness", 10));
        mSeekEndRandomness.setProgress(prefs.getInt("endRandomness", 40));
        mSwitchHideAnim.setChecked(prefs.getBoolean("hideAnim", true));
    }

    private void setupListeners() {
        // SeekBar listeners
        SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateLabels();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        mSeekDuration.setOnSeekBarChangeListener(seekListener);
        mSeekStartDelay.setOnSeekBarChangeListener(seekListener);
        mSeekHorizontal.setOnSeekBarChangeListener(seekListener);
        mSeekVertical.setOnSeekBarChangeListener(seekListener);
        mSeekRadius.setOnSeekBarChangeListener(seekListener);
        mSeekGap.setOnSeekBarChangeListener(seekListener);
        mSeekStartRandomness.setOnSeekBarChangeListener(seekListener);
        mSeekEndRandomness.setOnSeekBarChangeListener(seekListener);

        // Button listeners
        mBtnStart.setOnClickListener(v -> executeAnimation());
        mBtnSave.setOnClickListener(v -> saveConfig());

        // Click on image to trigger animation
        mIvTest.setOnClickListener(v -> executeAnimation());

        updateLabels();
    }

    private void updateLabels() {
        mTvDuration.setText(mSeekDuration.getProgress() + "ms");
        mTvStartDelay.setText(mSeekStartDelay.getProgress() + "ms");
        mTvHorizontal.setText(String.format("%.1f", mSeekHorizontal.getProgress() / 10f));
        mTvVertical.setText(String.format("%.1f", mSeekVertical.getProgress() / 10f));
        mTvRadius.setText(Math.max(1, mSeekRadius.getProgress()) + "dp");
        int gapProgress = mSeekGap.getProgress();
        int gapDp = gapProgress - 10; // 0-30 -> -10 to +20
        mTvGap.setText(gapDp + "dp");
        mTvStartRandomness.setText(String.format("%.2f", mSeekStartRandomness.getProgress() / 100f));
        mTvEndRandomness.setText(String.format("%.2f", mSeekEndRandomness.getProgress() / 100f));
    }

    private void executeAnimation() {
        int styleIndex = mSpinnerStyle.getSelectedItemPosition();
        int shapeIndex = mSpinnerShape.getSelectedItemPosition();
        int interpolatorIndex = mSpinnerInterpolator.getSelectedItemPosition();
        int scaleModeIndex = mSpinnerScaleMode.getSelectedItemPosition();

        int style = STYLE_VALUES[styleIndex];
        int shape = SHAPE_VALUES[shapeIndex];
        Interpolator interpolator = INTERPOLATOR_VALUES[interpolatorIndex];
        int scaleMode = SCALE_MODE_VALUES[scaleModeIndex];
        long duration = mSeekDuration.getProgress();
        long startDelay = mSeekStartDelay.getProgress();
        float horizontal = mSeekHorizontal.getProgress() / 10f;
        float vertical = mSeekVertical.getProgress() / 10f;
        int radius = Utils.dp2Px(Math.max(1, mSeekRadius.getProgress()));
        int gapDp = mSeekGap.getProgress() - 10;
        int gapPx = Utils.dp2Px(gapDp);
        float startRandomness = mSeekStartRandomness.getProgress() / 100f;
        float endRandomness = mSeekEndRandomness.getProgress() / 100f;
        boolean hideAnim = mSwitchHideAnim.isChecked();

        mSmasher.with(mIvTest)
                .setStyle(style)
                .setShape(shape)
                .setInterpolator(interpolator)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setHorizontalMultiple(horizontal)
                .setVerticalMultiple(vertical)
                .setStartRandomness(startRandomness)
                .setEndRandomness(endRandomness)
                .setParticleRadius(radius)
                .setParticleGap(gapPx)
                .setScaleMode(scaleMode)
                .setHideAnimation(hideAnim)
                .addAnimatorListener(new SmashAnimator.OnAnimatorListener() {
                    @Override
                    public void onAnimatorEnd() {
                        mSmasher.reShowView(mIvTest);
                    }
                })
                .start();

        mSmasher.with(mIvCover)
                .setStyle(style)
                .setShape(shape)
                .setInterpolator(interpolator)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setHorizontalMultiple(horizontal)
                .setVerticalMultiple(vertical)
                .setStartRandomness(startRandomness)
                .setEndRandomness(endRandomness)
                .setParticleRadius(radius)
                .setParticleGap(gapPx)
                .setScaleMode(scaleMode)
                .setHideAnimation(hideAnim)
                .addAnimatorListener(new SmashAnimator.OnAnimatorListener() {
                    @Override
                    public void onAnimatorEnd() {
                        mSmasher.reShowView(mIvCover);
                    }
                })
                .start();
    }
}