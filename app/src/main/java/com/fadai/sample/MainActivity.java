package com.fadai.sample;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fadai.particlesmasher.ParticleSmasher;
import com.fadai.particlesmasher.SmashAnimator;
import com.fadai.particlesmasher.Utils;

public class MainActivity extends AppCompatActivity {

    private ParticleSmasher mSmasher;
    private ImageView mIvTest;

    // Controls
    private Spinner mSpinnerStyle, mSpinnerShape;
    private SeekBar mSeekDuration, mSeekStartDelay, mSeekHorizontal, mSeekVertical, mSeekRadius;
    private TextView mTvDuration, mTvStartDelay, mTvHorizontal, mTvVertical, mTvRadius;
    private Switch mSwitchHideAnim;
    private Button mBtnStart, mBtnReset;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSmasher = new ParticleSmasher(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        mIvTest = findViewById(R.id.iv_test);

        mSpinnerStyle = findViewById(R.id.spinner_style);
        mSpinnerShape = findViewById(R.id.spinner_shape);

        mSeekDuration = findViewById(R.id.seek_duration);
        mSeekStartDelay = findViewById(R.id.seek_start_delay);
        mSeekHorizontal = findViewById(R.id.seek_horizontal);
        mSeekVertical = findViewById(R.id.seek_vertical);
        mSeekRadius = findViewById(R.id.seek_radius);

        mTvDuration = findViewById(R.id.tv_duration);
        mTvStartDelay = findViewById(R.id.tv_start_delay);
        mTvHorizontal = findViewById(R.id.tv_horizontal);
        mTvVertical = findViewById(R.id.tv_vertical);
        mTvRadius = findViewById(R.id.tv_radius);

        mSwitchHideAnim = findViewById(R.id.switch_hide_anim);
        mBtnStart = findViewById(R.id.btn_start);
        mBtnReset = findViewById(R.id.btn_reset);

        // Setup Spinners
        mSpinnerStyle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, STYLE_NAMES));
        mSpinnerShape.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SHAPE_NAMES));

        // Default to STYLE_RISE (index 6)
        mSpinnerStyle.setSelection(6);
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

        // Button listeners
        mBtnStart.setOnClickListener(v -> executeAnimation());
        mBtnReset.setOnClickListener(v -> mSmasher.reShowView(mIvTest));

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
    }

    private void executeAnimation() {
        int styleIndex = mSpinnerStyle.getSelectedItemPosition();
        int shapeIndex = mSpinnerShape.getSelectedItemPosition();

        int style = STYLE_VALUES[styleIndex];
        int shape = SHAPE_VALUES[shapeIndex];
        long duration = mSeekDuration.getProgress();
        long startDelay = mSeekStartDelay.getProgress();
        float horizontal = mSeekHorizontal.getProgress() / 10f;
        float vertical = mSeekVertical.getProgress() / 10f;
        int radius = Utils.dp2Px(Math.max(1, mSeekRadius.getProgress()));
        boolean hideAnim = mSwitchHideAnim.isChecked();

        mSmasher.with(mIvTest)
                .setStyle(style)
                .setShape(shape)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setHorizontalMultiple(horizontal)
                .setVerticalMultiple(vertical)
                .setParticleRadius(radius)
                .setHideAnimation(hideAnim)
                .start();
    }
}