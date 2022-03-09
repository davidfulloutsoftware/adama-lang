/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

/** the contract for the data service */
public interface DataService {

  /** Download the entire object and return the entire json */
  void get(Key key, Callback<LocalDocumentChange> callback);

  /** write the first entry for the document */
  void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback);

  /** Apply a series of patches to the document using rfc7396 */
  void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback);

  /** Compute the change the state of the document to the indicated seq by the given client */
  void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback);

  /** Delete the document given by the ID */
  void delete(Key key, Callback<Void> callback);

  /** Compact the storage backing a key such that the given value is shown as the history */
  void compactAndSnapshot(Key key, String snapshot, int history, Callback<Integer> callback);

}
