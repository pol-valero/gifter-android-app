package com.example.giftr.business;

import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class ImageViewLoader {
	private ImageView imageView;

	public ImageViewLoader(ImageView imageView) {
		this.imageView = imageView;
	}

	public void loadImage(String imageUrl) {
		Picasso.get()
				.load(imageUrl)
				.fit()
				.centerCrop()
				.into(imageView);
	}
}