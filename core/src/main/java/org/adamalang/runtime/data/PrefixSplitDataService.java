package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

public class PrefixSplitDataService implements DataService {
  private final DataService dataServiceA;
  private final String prefixB;
  private final DataService dataServiceB;

  public PrefixSplitDataService(DataService dataServiceA, String prefixB, DataService dataServiceB) {
    this.dataServiceA = dataServiceA;
    this.prefixB = prefixB;
    this.dataServiceB = dataServiceB;
  }

  public DataService ds(Key key) {
    if (key.key.startsWith(prefixB)) {
      return dataServiceB;
    }
    return dataServiceA;
  }

  @Override
  public void get(Key key, Callback<LocalDocumentChange> callback) {
    ds(key).get(key, callback);
  }

  @Override
  public void initialize(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
    ds(key).initialize(key, patch, callback);
  }

  @Override
  public void patch(Key key, RemoteDocumentUpdate[] patches, Callback<Void> callback) {
    ds(key).patch(key, patches, callback);
  }

  @Override
  public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
    ds(key).compute(key, method, seq, callback);
  }

  @Override
  public void delete(Key key, Callback<Void> callback) {
    ds(key).delete(key, callback);
  }

  @Override
  public void compact(Key key, int history, Callback<Integer> callback) {
    ds(key).compact(key, history, callback);
  }
}