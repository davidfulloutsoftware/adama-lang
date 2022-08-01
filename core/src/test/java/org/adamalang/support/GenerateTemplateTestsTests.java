/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support;

import org.junit.Test;

import java.io.File;

public class GenerateTemplateTestsTests {
  @Test
  public void empty() throws Exception {
    final var testdata = new File("./test_data");
    testdata.mkdir();
    final var testdataCode = new File("./test_data/code1");
    testdataCode.mkdir();
    final var javaOut = new File("./test_data/java-out1");
    javaOut.mkdir();
    GenerateTemplateTests.generate(
        0,
        new String[] {
            "--input", "./test_data/code1", "--output", "./test_data/java-out1", "--what", "ok", "--errors", "./test_data/errors.csv"
        });
    javaOut.delete();
    testdataCode.delete();
  }
}