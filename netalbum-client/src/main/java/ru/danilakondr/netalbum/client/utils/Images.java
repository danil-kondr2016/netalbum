package ru.danilakondr.netalbum.client.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import ru.danilakondr.netalbum.api.data.ImageData;

public class Images {
	public static ImageData generateImage(File f, String name, int thumbnailWidth, int thumbnailHeight) throws IOException {
		ImageData data = new ImageData();
		data.setFileName(name);
		data.setFileSize(f.length());
		
		BufferedImage img = ImageIO.read(f);
		data.setWidth(img.getWidth());
		data.setHeight(img.getHeight());
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Thumbnails.of(img)
			.size(thumbnailWidth, thumbnailHeight)
			.outputFormat("JPEG")
			.toOutputStream(os);
		
		data.setThumbnail(os.toByteArray());
		return data;
	}
}
