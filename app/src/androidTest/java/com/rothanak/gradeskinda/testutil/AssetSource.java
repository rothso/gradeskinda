package com.rothanak.gradeskinda.testutil;

import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;

import com.github.tomakehurst.wiremock.common.BinaryFile;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.common.TextFile;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import timber.log.Timber;

/**
 * Hacky implementation of FileSource to allow WireMock to access mockserver files while running
 * on the device/emulator. The assets folder must accessible from the androidTest flavor. If the
 * assets folder is not in the default location (under androidTest/assets), link it with Gradle:
 * <pre>{@code
 * // app/build.gradle
 * android {
 *     sourceSets {
 *         androidTest {
 *             assets.srcDir "src/myFlavor/myAssets"
 *         }
 *     }
 * }
 * }</pre>
 */
public class AssetSource implements FileSource {

    private final String subfolder;

    public AssetSource(String subFolder) {
        this.subfolder = subFolder;
    }

    @Override
    public BinaryFile getBinaryFileNamed(String name) {
        AssetManager assetManager = InstrumentationRegistry.getInstrumentation().getContext().getAssets();
        String assetName = subfolder + File.separator + name; // relative to the assets folder
        return new AssetFile(assetManager, assetName);
    }

    @Override
    public void createIfNecessary() {
        // noop
    }

    @Override
    public FileSource child(String subDirectoryName) {
        // Force us to stay inside the current subfolder
        return new AssetSource(this.subfolder) {
            @Override
            public boolean exists() {
                return false;
            }
        };
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public List<TextFile> listFilesRecursively() {
        return null;
    }

    @Override
    public void writeTextFile(String name, String contents) {
        // noop
    }

    @Override
    public void writeBinaryFile(String name, byte[] contents) {
        // noop
    }

    @Override
    public boolean exists() {
        return true;
    }

    /**
     * Files in the assets folder do not have URIs, so there is no way to use the default
     * BinaryFile implementation. The dirty workaround is to read the bytes ourselves and
     * override {@link #readContents} to return the result.
     */
    private static final class AssetFile extends BinaryFile {

        private final AssetManager assetManager;
        private final String assetName;

        public AssetFile(AssetManager assetManager, String assetName) {
            super(null);
            this.assetManager = assetManager;
            this.assetName = assetName;
        }

        /* Don't use this constructor. */
        @SuppressWarnings("unused")
        private AssetFile(URI uri) {
            super(uri);
            assetManager = null;
            assetName = null;
        }

        @Override
        public byte[] readContents() {
            InputStream inputStream = null;
            try {
                inputStream = assetManager.open(assetName);
                return ByteStreams.toByteArray(inputStream);
            } catch (IOException e) {
                Timber.e(e, "Failed to open asset file " + assetName);
                throw new RuntimeException(e);
            } finally {
                closeStream(inputStream);
            }
        }

        private void closeStream(InputStream inputStream) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Timber.e(e, "Failed to close asset InputStream");
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public String name() {
            return assetName;
        }
    }
}
