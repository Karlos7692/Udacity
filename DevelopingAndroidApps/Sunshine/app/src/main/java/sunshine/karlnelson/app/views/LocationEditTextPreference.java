package sunshine.karlnelson.app.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

import sunshine.karlnelson.app.R;


/**
 * Created by Karl on 26/02/2016.
 */
public class LocationEditTextPreference extends EditTextPreference {

    private static final int DEFAULT_MIN_LENGTH = 2;

    private int mMinLength;

    public LocationEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray as = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.LocationEditTextPreference, 0, 0);
        try {
            mMinLength = as.
                    getInteger(R.styleable.LocationEditTextPreference_minLength, DEFAULT_MIN_LENGTH);
        } finally {
            as.recycle();
        }
    }


    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        EditText et = getEditText();

        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Dialog d = getDialog();
                if ( d instanceof  AlertDialog ) {
                    Button b = ((AlertDialog) d).getButton(DialogInterface.BUTTON_POSITIVE);
                    if ( s.length() < mMinLength ) {
                        b.setEnabled(false);
                    } else {
                        b.setEnabled(true);
                    }
                }
            }
        });
    }


}
