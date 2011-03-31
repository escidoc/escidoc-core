package de.escidoc.core.test.om.item.contentTools;

import java.awt.*;
import java.io.File;

/**
 * Reading and determinig properties from Image file.
 *
 * @author Steffen Wagner
 */
public class ImageProperties extends Canvas {

    private static final long serialVersionUID = -5512035341024238819L;

    private static final long WAIT_TIME = 1000;

    private Image image = null;

    private MediaTracker mTracker = new MediaTracker(this);

    /**
     * Construct ImageProperties.
     *
     * @param file The image file.
     * @throws InterruptedException Thrown if loading image via toolkit failed.
     */
    public ImageProperties(final File file) throws InterruptedException {

        loadImage(file.getAbsolutePath());
    }

    /**
     * Construct ImageProperties.
     *
     * @param filePath The name and path to image file.
     * @throws InterruptedException Thrown if loading image via toolkit failed.
     */
    public ImageProperties(final String filePath) throws InterruptedException {

        loadImage(filePath);
    }

    /**
     * Get width of image.
     *
     * @return image width in pixel
     * @throws InterruptedException Thrown if loading image via toolkit failed.
     */
    public int getImageWidth() throws InterruptedException {

        return image.getWidth(null);
    }

    /**
     * Get height of image.
     *
     * @return image height in pixel
     * @throws InterruptedException Thrown if loading image via toolkit failed.
     */
    public int getImageHeight() throws InterruptedException {

        return image.getHeight(null);
    }

    /**
     * Wait until Image with id is loaded.
     *
     * @param imageID the id of the image within the toolkit
     * @throws InterruptedException Thrown if loading image via toolkit failed.
     */
    private void waitImageLoad(final int imageID) throws InterruptedException {

        while (!this.mTracker.checkID(1)) {
            wait(WAIT_TIME);
        }

    }

    /**
     * Loading image.
     *
     * @param filePath Path to image file
     * @throws InterruptedException Thrown if loading image failed.
     */
    private void loadImage(final String filePath) throws InterruptedException {
        // loading image
        this.image = Toolkit.getDefaultToolkit().getImage(filePath);

        mTracker.addImage(image, 1);
        mTracker.waitForID(1);

        waitImageLoad(1);
    }
}
