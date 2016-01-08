package com.androidsx.announcement.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsx.announcement.R;
import com.androidsx.announcement.util.SharedPreferencesHelper;

public class DialogFullScreen extends DialogFragment {
    private static final String TAG = DialogFullScreen.class.getSimpleName();

    public static final int DEFAULT_LAYOUT_RES_ID = -1;
    private static final String SHARED_PRESSED_OK = "pressed_ok_";
    private static final String SHARED_DIALOG_NUM_LAUNCHES = "dialog_num_launches_";

    // View
    private ImageButton close;
    private Button actionButtonDialogFullScreen;
    private View fullScreenView;
    private TextView messageDialogFullScreen;
    private ImageView iconDialogFullScreen;
    
    // Configuration
    private Context context;
    private String message;
    private int fullScreenDialogBackgroundColor;
    private Bitmap iconBitmap;
    private int messageTextColor;
    private int actionButtonBackgroundColor;
    private int actionButtonTextColor;
    private Intent openIntent;
    private Intent dismissIntent;
    private Boolean cancellable;
    private String id;
    private String buttonTitle;
    private int layoutResId;

    public DialogFullScreen() {
    }

    @SuppressLint("ValidFragment")
    private DialogFullScreen(Builder builder) {
        this.context = builder.context;
        this.message = builder.message;
        this.fullScreenDialogBackgroundColor = builder.fullScreenDialogBackgroundColor;
        this.iconBitmap = builder.iconBitmap;
        this.messageTextColor = builder.messageTextColor;
        this.actionButtonBackgroundColor = builder.actionButtonBackgroundColor;
        this.actionButtonTextColor = builder.actionButtonTextColor;
        this.openIntent = builder.openIntent;
        this.dismissIntent = builder.dismissIntent;
        this.cancellable = builder.cancellable;
        this.id = builder.id;
        this.buttonTitle = builder.buttonTitle;
        this.layoutResId = builder.layoutResId;
    }

    public static boolean isPressedOkButton(Context context, String id) {
        return SharedPreferencesHelper.getBooleanValue(context, SHARED_PRESSED_OK + id);
    }

    private void savePressedOkButton() {
        SharedPreferencesHelper.saveBooleanValue(context, SHARED_PRESSED_OK + id, true);
    }

    private void saveNewLaunch() {
        int launchedTimes = getNumLaunches(context, id);
        SharedPreferencesHelper.saveIntValue(context, getDialogNumLaunchesSharedKey(id), ++launchedTimes);
    }

    public static int getNumLaunches(Context context, String id) {
        return SharedPreferencesHelper.getIntValue(context, getDialogNumLaunchesSharedKey(id), 0);
    }

    private static String getDialogNumLaunchesSharedKey(String id) {
        return SHARED_DIALOG_NUM_LAUNCHES + id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = null;
        try {
            dialog = new Dialog(context, android.R.style.Theme_Light);
        } catch (Throwable t) {
            try {
                dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar);
            } catch (Throwable throwable) {
                try {
                    dialog = new Dialog(context);
                } catch (Throwable throwable2) {
                    Log.e(TAG, "Could not create dynamic dialog", throwable);
                }
            }
        }
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(createView());
            dialog.setCancelable(cancellable);

            configureButtonDialogFullScreen();
        }

        saveNewLaunch();

        return dialog;
    }
    
    private View createView() {
        fullScreenView = getActivity().getLayoutInflater().inflate(layoutResId, null);
        close = (ImageButton) fullScreenView.findViewById(R.id.dialog_full_screen_close_button);
        actionButtonDialogFullScreen = (Button) fullScreenView.findViewById(R.id.dialog_full_screen_action_button);
        messageDialogFullScreen = (TextView) fullScreenView.findViewById(R.id.message_dialog_full_screen);
        iconDialogFullScreen = (ImageView) fullScreenView.findViewById(R.id.icon_dialog_full_screen);
        
        fullScreenView.setBackgroundColor(fullScreenDialogBackgroundColor);
        actionButtonDialogFullScreen.setTextColor(actionButtonTextColor);
        actionButtonDialogFullScreen.setBackgroundColor(actionButtonBackgroundColor);
        messageDialogFullScreen.setTextColor(messageTextColor);
        messageDialogFullScreen.setText(message);
        if (iconDialogFullScreen != null && iconBitmap != null) {
            iconDialogFullScreen.setImageBitmap(iconBitmap);
        }

        if (buttonTitle != null && !buttonTitle.equals("")) {
            actionButtonDialogFullScreen.setText(buttonTitle);
        }
        
        return fullScreenView;
    }
    
    private void configureButtonDialogFullScreen() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dismissIntent != null) {
                    context.sendBroadcast(dismissIntent);
                }
                dismiss();
            }
        });
        
        actionButtonDialogFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openIntent != null) {
                    context.sendBroadcast(openIntent);
                }
                savePressedOkButton();
                dismiss();
            }
        });
    }
    
    public static class Builder {
        private Context context;
        private String message;
        private int fullScreenDialogBackgroundColor = Color.WHITE;
        private Bitmap iconBitmap;
        private int messageTextColor = Color.BLUE;
        private int actionButtonBackgroundColor = Color.BLUE;
        private int actionButtonTextColor = Color.WHITE;
        private Intent openIntent;
        private Intent dismissIntent;
        private boolean cancellable = false;
        private String id;
        private String buttonTitle;
        private int layoutResId = R.layout.full_screen_dialog_big_image;

        public Builder(Context context) {
            this.context = context;
            this.buttonTitle = context.getString(R.string.dialog_full_screen_text_button);
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder fullScreenDialogBackgroundColor(int fullScreenDialogBackgroundColor) {
            this.fullScreenDialogBackgroundColor = fullScreenDialogBackgroundColor;
            return this;
        }

        public Builder iconBitmap(Bitmap iconBitmap) {
            this.iconBitmap = iconBitmap;
            return this;
        }

        public Builder messageTextColor(int messageTextColor) {
            this.messageTextColor = messageTextColor;
            return this;
        }

        public Builder actionButtonBackgroundColor(int actionButtonBackgroundColor) {
            this.actionButtonBackgroundColor = actionButtonBackgroundColor;
            return this;
        }

        public Builder actionButtonTextColor(int actionButtonTextColor) {
            this.actionButtonTextColor = actionButtonTextColor;
            return this;
        }
        
        public Builder openIntent(Intent openIntent) {
            this.openIntent = openIntent;
            return  this;
        }

        public Builder dismissIntent(Intent dismissIntent) {
            this.dismissIntent = dismissIntent;
            return  this;
        }
        
        public Builder cancellable(boolean cancellable) {
            this.cancellable = cancellable;
            return this;
        }

        public Builder buttonTitle(String buttonTitle) {
            this.buttonTitle = buttonTitle;
            return this;
        }
        
        public Builder id (String id) {
            this.id = id;
            return this;
        }

        public Builder layoutResId(int layoutResId) {
            if (layoutResId != DEFAULT_LAYOUT_RES_ID) {
                this.layoutResId = layoutResId;
            }
            return this;
        }

        public DialogFullScreen build() {
            return new DialogFullScreen(this);
        }
    }
}