/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.androlib;

import brut.androlib.res.Framework;
import brut.common.BrutException;
import brut.directory.ExtFile;

import java.io.File;

import org.junit.*;
import static org.junit.Assert.*;

public class SharedLibraryTest extends BaseTest {
    private static final String LIBRARY_APK = "library.apk";
    private static final String CLIENT_APK = "client.apk";

    @BeforeClass
    public static void beforeClass() throws Exception {
        TestUtils.copyResourceDir(SharedLibraryTest.class, "shared_libraries", sTmpDir);
    }

    @Test
    public void isFrameworkTaggingWorking() throws BrutException {
        sConfig.setFrameworkDirectory(sTmpDir.getAbsolutePath());
        sConfig.setFrameworkTag("building");

        ExtFile libraryApk = new ExtFile(sTmpDir, LIBRARY_APK);
        new Framework(sConfig).install(libraryApk);

        assertTrue(new File(sTmpDir, "0-building.apk").exists());
    }

    @Test
    public void isFrameworkInstallingWorking() throws BrutException {
        sConfig.setFrameworkDirectory(sTmpDir.getAbsolutePath());

        ExtFile libraryApk = new ExtFile(sTmpDir, LIBRARY_APK);
        new Framework(sConfig).install(libraryApk);

        assertTrue(new File(sTmpDir, "0.apk").exists());
    }

    @Test
    public void isSharedResourceDecodingAndRebuildingWorking() throws BrutException {
        sConfig.setFrameworkDirectory(sTmpDir.getAbsolutePath());
        sConfig.setLibraryFiles(new String[] {
            "com.google.android.test.shared_library:" + new File(sTmpDir, LIBRARY_APK).getAbsolutePath()
        });

        // decode library.apk
        ExtFile libraryApk = new ExtFile(sTmpDir, LIBRARY_APK);
        ExtFile libraryDir = new ExtFile(libraryApk + ".out");
        new ApkDecoder(libraryApk, sConfig).decode(libraryDir);

        // decode client.apk
        ExtFile clientApk = new ExtFile(sTmpDir, CLIENT_APK);
        ExtFile clientDir = new ExtFile(clientApk + ".out");
        new ApkDecoder(clientApk, sConfig).decode(clientDir);

        // build library.apk
        new ApkBuilder(libraryDir, sConfig).build(null);

        assertTrue(new File(libraryDir, "dist/" + libraryApk.getName()).exists());

        // build client.apk
        new ApkBuilder(clientDir, sConfig).build(null);

        assertTrue(new File(clientDir, "dist/" + clientApk.getName()).exists());
    }
}
