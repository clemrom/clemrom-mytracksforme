/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.android.apps.mytracks;

import com.google.android.apps.mytracks.content.MyTracksProviderUtils;
import com.google.android.apps.mytracks.util.ApiFeatures;
import com.google.android.maps.mytracks.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * A utility class that can be used to delete all tracks and track points
 * from the provider, including asking for confirmation from the user via
 * a dialog.
 *
 * @author Leif Hendrik Wilden
 */
public class DeleteAllTracks extends Handler {

  private final Context context;
  private final Runnable done;

  public DeleteAllTracks(Context context, Runnable done) {
    this.context = context;
    this.done = done;
  }

  @Override
  public void handleMessage(Message msg) {
    super.handleMessage(msg);

    AlertDialog dialog = null;
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(
        context.getString(R.string.track_list_delete_all_confirm_message));
    builder.setTitle(context.getString(R.string.generic_confirm_title));
    builder.setIcon(android.R.drawable.ic_dialog_alert);
    builder.setPositiveButton(context.getString(R.string.generic_yes),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            Log.w(Constants.TAG, "deleting all!");
            MyTracksProviderUtils.Factory.get(context).deleteAllTracks();
            SharedPreferences prefs = context.getSharedPreferences(
                Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
            Editor editor = prefs.edit();
            // TODO: Go through data manager
            editor.putLong(context.getString(R.string.selected_track_key), -1);
            ApiFeatures.getInstance().getApiAdapter().applyPreferenceChanges(editor);
            if (done != null) {
              Handler h = new Handler();
              h.post(done);
            }
          }
        });
    builder.setNegativeButton(context.getString(R.string.generic_no),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        });
    dialog = builder.create();
    dialog.show();
  }
}
