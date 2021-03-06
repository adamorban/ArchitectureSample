package com.icostel.arhitecturesample.navigation;

import android.content.Intent;

public class IntentNavigationAction extends NavigationAction {
    private Intent intent;
    private Integer flags;

    @Override
    public void navigate(Navigator navigator) {
        if (intent != null) {
            if (flags != null) {
                intent.setFlags(intent.getFlags() | flags);
            }
            if (requestCode == null) {
                navigator.startActivity(intent);
            } else {
                navigator.startActivityForResult(intent, requestCode);
            }
            finishIfNeeded(navigator);
        }
    }

    public static class Builder {
        private IntentNavigationAction action = new IntentNavigationAction();

        public Builder setIntent(Intent intent) {
            action.intent = intent;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            action.requestCode = requestCode;
            return this;
        }

        public Builder setShouldFinish(boolean shouldFinish) {
            action.shouldFinish = shouldFinish;
            return this;
        }

        public Builder setFlags(Integer flags) {
            action.flags = flags;
            return this;
        }

        public IntentNavigationAction build() {
            return action;
        }
    }
}
