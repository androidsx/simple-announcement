package com.androidsx.announcement.widget;

/**
 * Created by Androidsx on 15/12/15.
 */
public class DialogColors {
    private final int background;
    private final int title;
    private final int content;
    private final int buttonText;
    private final int buttonBackground;

    private DialogColors(Builder builder) {
        this.background = builder.background;
        this.title = builder.title;
        this.content = builder.content;
        this.buttonText = builder.buttonText;
        this.buttonBackground = builder.buttonBackground;
    }

    public int getBackground() {
        return background;
    }

    public int getTitle() {
        return title;
    }

    public int getContent() {
        return content;
    }

    public int getButtonText() {
        return buttonText;
    }

    public int getButtonBackground() {
        return buttonBackground;
    }

    public static class Builder {
        private int background = android.R.color.white;
        private int title = android.R.color.black;
        private int content = android.R.color.black;
        private int buttonText = android.R.color.black;
        private int buttonBackground = android.R.color.holo_blue_light;

        public Builder() {

        }

        public Builder background(int background) {
            this.background = background;
            return this;
        }

        public Builder title(int title) {
            this.title = title;
            return this;
        }

        public Builder content(int content) {
            this.content = content;
            return this;
        }

        public Builder buttonText(int buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        public Builder buttonBackground(int buttonBackground) {
            this.buttonBackground = buttonBackground;
            return this;
        }

        public DialogColors build() {
            return new DialogColors(this);
        }
    }
}
