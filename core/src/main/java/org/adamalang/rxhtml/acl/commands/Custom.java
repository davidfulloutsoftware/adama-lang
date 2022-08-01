/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.acl.commands;

import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.StatePath;

/** set the string value of the path to the given value */
public class Custom implements Command {
  public final String command;

  public Custom(String command) {
    this.command = command;
  }

  @Override
  public void write(Environment env, String type, String eVar) {
    env.writer.tab().append("$.exCC(").append(eVar).append(",'").append(type).append("',").append(env.stateVar).append(",'").append(command).append("');").newline();
  }
}