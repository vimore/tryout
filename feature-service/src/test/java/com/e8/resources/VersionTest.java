package com.e8.resources;


import com.securityx.modelfeature.resources.Version;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VersionTest {
    @Test
    public void testVersionResource() throws Exception{
        Version version = new Version();
        HashMap<String, String> map = version.getVersion();
        assertNotNull(map);
        assertTrue(!map.isEmpty());
        assertTrue(map.containsKey("Version"));
        assertTrue(map.containsKey("BuildNumber"));
        assertTrue(map.containsKey("BuildTime"));
    }
}
