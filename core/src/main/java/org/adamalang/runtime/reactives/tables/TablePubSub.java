/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.reactives.tables;

import org.adamalang.runtime.contracts.RxParent;

import java.util.ArrayList;
import java.util.Iterator;

public class TablePubSub implements TableSubscription {
  private final RxParent owner;
  private final ArrayList<TableSubscription> _subscriptions;

  public TablePubSub(RxParent owner) {
    this.owner = owner;
    this._subscriptions = new ArrayList<>();
  }

  public void subscribe(TableSubscription ts) {
    _subscriptions.add(ts);
  }

  @Override
  public boolean alive() {
    if (owner != null) {
      return owner.__isAlive();
    }
    return true;
  }

  public void gc() {
    Iterator<TableSubscription> it = _subscriptions.iterator();
    while (it.hasNext()) {
      if (!it.next().alive()) {
        it.remove();
      }
    }
  }

  @Override
  public void add(int primaryKey) {
    for (TableSubscription ts : _subscriptions) {
      ts.add(primaryKey);
    }
  }

  @Override
  public void change(int primaryKey) {
    for (TableSubscription ts : _subscriptions) {
      ts.change(primaryKey);
    }
  }

  @Override
  public void remove(int primaryKey) {
    for (TableSubscription ts : _subscriptions) {
      ts.remove(primaryKey);
    }
  }

  @Override
  public void index(int primaryKey, String field, int value) {
    for (TableSubscription ts : _subscriptions) {
      ts.index(primaryKey, field, value);
    }
  }
}