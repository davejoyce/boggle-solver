/*
 * Copyright (c) 2018 David Joyce.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/BSD-2-Clause
 */

package io.github.davejoyce.boggle.solver;

import net.openhft.chronicle.map.ChronicleMap;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.testng.annotations.Test;

import java.util.Locale;
import java.util.Map;

import static org.testng.Assert.*;

public class ApplicationConfigTest {

  @Test
  public void testDictionaryRegistry() {
    MockEnvironment environment = new MockEnvironment().withProperty("java.io.dir", "/tmp");
    ApplicationConfig config = new ApplicationConfig();
    Map<Locale, ChronicleMap<Integer, String>> dictionaryRegistry = config.dictionaryRegistry(environment);
    assertNotNull(dictionaryRegistry);
    assertFalse(dictionaryRegistry.isEmpty());
  }

}
