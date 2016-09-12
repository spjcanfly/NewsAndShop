package com.example.numberaddsubview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by spj on 2016/9/1.
 */
public class NumberAddSubview extends LinearLayout implements View.OnClickListener {

    private Button btn_sub;
    private TextView tv_value;
    private Button btn_add;
    private int value = 1;
    private int minValue = 1;
    private int maxValue = 15;

    public NumberAddSubview(Context context) {
        this(context, null);
    }

    public NumberAddSubview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberAddSubview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //让布局和当前类形成一个整体
        View.inflate(context, R.layout.number_add_subview, this);
        btn_sub = (Button) findViewById(R.id.btn_sub);
        tv_value = (TextView) findViewById(R.id.tv_value);
        btn_add = (Button) findViewById(R.id.btn_add);

        getValue();

        btn_add.setOnClickListener(this);

        btn_sub.setOnClickListener(this);
    }


    public int getValue() {
        String valueStr =  tv_value.getText().toString().trim();//文本的内容
        if(!TextUtils.isEmpty(valueStr)){
            value = Integer.valueOf(valueStr);
        }
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        tv_value.setText(value+"");
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_add:

                addNumber();
                if(onNumberClickListener!=null) {
                    onNumberClickListener.onButtonAdd(view,value);
                }

                break;
            case R.id.btn_sub:

                subNumber();

                if(onNumberClickListener != null) {
                    onNumberClickListener.onButtonSub(view,value);
                }
                break;
        }

    }

    private void subNumber() {
        if (value > minValue) {
            value--;
        }
        setValue(value);

    }

    private void addNumber() {
        if (value < maxValue) {
            value++;
        }
        setValue(value);
    }
    public interface OnNumberClickListener{
        void onButtonSub(View view,int value);

        void onButtonAdd(View view,int value);
    }
    private OnNumberClickListener onNumberClickListener;

    public void setOnNumberClickListener(OnNumberClickListener onNumberClickListener) {
        this.onNumberClickListener = onNumberClickListener;
    }
}
