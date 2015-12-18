/**
 * Copyright 2015 Stnts Inc. All rights reserved. (注意：本内容仅限于盛天公司内部传阅，禁止外泄以及用于其他的商业目的) Date:
 * 2015年10月23日 文件名称: FileSystemPersistenceProvider.java 项目名称: cachetest 作者: Administrator 类说明:TODO
 **/
package idgenerator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStore;

public class FileSystemPersistenceProvider implements MapLoader<String, Object>,
    MapStore<String, Object> {
  private String mapName = "default";

  public FileSystemPersistenceProvider() {}

  public FileSystemPersistenceProvider(String mapName) {
    this.mapName = mapName;
    File dir = new File(getPersistencePath());
    if (!dir.exists())
      dir.mkdir();
  }

  private String getPersistencePath() {
    return "persistence/";
  }

  private String getMapStorePath() {
    return getPersistencePath() + mapName + "/";
  }

  @Override
  public void store(String key, Object value) {
    try {
      File dir = new File(getMapStorePath());
      if (!dir.exists()) {
        dir.mkdir();
      }
      OutputStream file = new FileOutputStream(getMapStorePath() + key + ".ser");
      OutputStream buffer = new BufferedOutputStream(file);
      ObjectOutput output = new ObjectOutputStream(buffer);
      output.writeObject(value);
      output.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void storeAll(Map<String, Object> keyValueMap) {
    for (String key : keyValueMap.keySet()) {
      store(key, keyValueMap.get(key));
    }
  }

  @Override
  public void delete(String key) {
    File file = new File(getMapStorePath() + key + ".ser");
    if (file.exists()) {
      file.delete();
    }
  }

  @Override
  public void deleteAll(Collection<String> keys) {
    for (String key : keys) {
      delete(key);
    }
  }

  @Override
  public Object load(String key) {
    // I don't implement this because the grid is ALL IN CACHE
    // so you will never have something in persistence that is
    // not in memory and don't want a hit on the file system (slow)
    return null;
  }

  // This is only public so it can be used by tests
  // Not part of the MapLoader interface
  public Object loadFromPersistence(String key) {
    File file = new File(getMapStorePath() + key + ".ser");
    if (!file.exists())
      return null;

    try {
      InputStream inputStream = new FileInputStream(getMapStorePath() + key + ".ser");
      InputStream buffer = new BufferedInputStream(inputStream);
      ObjectInput input = new ObjectInputStream(buffer);
      return (Object) input.readObject();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @Override
  public Map<String, Object> loadAll(Collection<String> keys) {
    Map<String, Object> values = new HashMap<String, Object>();

    for (String key : keys) {
      values.put(key, loadFromPersistence(key));
    }
    return values;
  }

  private FilenameFilter getFilenameFilter() {
    FilenameFilter filter = new FilenameFilter() {
      @Override
      public boolean accept(File file, String name) {
        return name.toLowerCase().endsWith(".ser");
      }
    };
    return filter;
  }

  @Override
  public Set<String> loadAllKeys() {
    Set<String> keys = new HashSet<String>();

    File[] files = new File(getMapStorePath()).listFiles(getFilenameFilter());
    if (files == null)
      return keys;

    for (int i = 0; i < files.length; i++) {
      keys.add(files[i].getName().replaceFirst(".ser", ""));
    }
    return keys;
  }

  public void clear() {
    File[] files = new File(getMapStorePath()).listFiles(getFilenameFilter());
    for (File file : files) {
      file.delete();
    }
  }
}
