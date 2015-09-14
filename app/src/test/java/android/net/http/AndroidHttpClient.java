package android.net.http;

/**
 * Workaround to resolve Robolectric shadows's dependency on AndroidHttpClient, which
 * was removed from API 23: https://github.com/robolectric/robolectric/issues/1862
 */
public class AndroidHttpClient {
}
