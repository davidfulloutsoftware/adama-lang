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

/** a table subscription represents a virtual change log of the table at a high level (pkey, index, all) */
public interface TableSubscription {
  /** is the subscription still alive */
  public boolean alive();

  /** a item was added with the given primary key */
  public void primary(int primaryKey);

  /** an item with the index value was changed */
  public void index(int primaryKey, int index, int value);

  /** a property of the entire table changes */
  public void all();
}
