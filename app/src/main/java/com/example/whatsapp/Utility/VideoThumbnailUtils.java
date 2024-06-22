package com.example.whatsapp.Utility;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.example.whatsapp.R;
import com.squareup.picasso.Picasso;

public class VideoThumbnailUtils {

    public static void loadVideoThumbnail(Context context, String videoUrl, ImageView imageView) {
        // Generate a bitmap thumbnail for the video
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Images.Thumbnails.MINI_KIND);

        // Load the thumbnail into the ImageView
        if (thumbnail != null) {
            imageView.setImageBitmap(thumbnail);
        } else {
            // If the thumbnail is null, you can load a placeholder image
            Picasso.get().load(R.drawable.ic_video).into(imageView);
        }
    }
}
