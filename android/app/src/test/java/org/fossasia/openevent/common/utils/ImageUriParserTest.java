package org.fossasia.openevent.common.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImageUriParserTest {

    @Test
    public void shouldReturnNullOnNullOrEmpty() throws Exception {
        assertNull(Utils.parseImageUri(""));
        assertNull(Utils.parseImageUri(null));
    }

    @Test
    public void shouldReturnUrl() throws Exception {
        String url = "http://website.ext/resource.ext";
        String https_url = "http://website.ext/resource.ext";

        assertEquals(url, Utils.parseImageUri(url));
        assertEquals(https_url, Utils.parseImageUri(https_url));
    }

    @Test
    public void shouldReturnAssetUri() throws Exception {
        String uri = "/images/speakers/JohnWick.jpg";
        String sponsors_uri = "/images/sponsors/Google_1.png";

        assertEquals("file:///android_asset/images/speakers/JohnWick.jpg", Utils.parseImageUri(uri));
        assertEquals("file:///android_asset/images/sponsors/Google_1.png", Utils.parseImageUri(sponsors_uri));
    }

    @Test
    public void shouldReturnNullOnMalformedUri() throws Exception {
        String uri = "a_different/format.pop";

        assertNull(Utils.parseImageUri(uri));
    }

}