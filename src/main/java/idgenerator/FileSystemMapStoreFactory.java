/**
 * Copyright 2015 Stnts Inc.
 * All rights reserved.
 * (注意：本内容仅限于盛天公司内部传阅，禁止外泄以及用于其他的商业目的)
 * Date: 2015年10月23日
 * 文件名称: FileSystemMapStoreFactory.java
 * 项目名称: cachetest
 * 作者: Administrator
 * 类说明:TODO
**/
package idgenerator;

import java.util.Properties;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;

public class FileSystemMapStoreFactory implements MapStoreFactory<String, Object>
{
    @Override
    public MapLoader<String, Object> newMapStore(String mapName, Properties properties) {
        return new FileSystemPersistenceProvider(mapName);
    }
}