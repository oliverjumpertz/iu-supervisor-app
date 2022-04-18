package com.example.supervisionapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.supervisionapp.R;

public final class FileViewerUtils {
    private FileViewerUtils() {}

    public static void viewPdf(Context context, String fileUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if ("finalMockURI".equals(fileUrl)) {
            Toast
                    .makeText(
                            context,
                            R.string.view_pdf_final_state,
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        intent.setDataAndType(Uri.parse(fileUrl), "application/pdf");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent sendIntent = Intent.createChooser(intent, null);
        context.startActivity(sendIntent);
    }
}
