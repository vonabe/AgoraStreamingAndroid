package ru.vonabe.audiostreaming;

import android.content.Context;
import android.util.AttributeSet;
import com.google.android.material.textfield.TextInputEditText;

public class CustomEditText extends TextInputEditText {

    private int[] STATE_MESSAGE_ERROR = {R.attr.state_error};

    private boolean state_error = false;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            if(attrs.getAttributeName(i).equalsIgnoreCase("state_error")){
                state_error = attrs.getAttributeBooleanValue(i, false);
            }
        }
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            if(attrs.getAttributeName(i).equalsIgnoreCase("state_error")){
                state_error = attrs.getAttributeBooleanValue(i, false);
            }
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        if(state_error){
            int[] drawableState = super.onCreateDrawableState(extraSpace+1);
            mergeDrawableStates(drawableState, STATE_MESSAGE_ERROR);
            return drawableState;
        }else{
            return super.onCreateDrawableState(extraSpace);
        }

    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    public void setError(boolean state){
        if(this.state_error != state) {
            this.state_error = state;
            refreshDrawableState();
        }
    }

}
