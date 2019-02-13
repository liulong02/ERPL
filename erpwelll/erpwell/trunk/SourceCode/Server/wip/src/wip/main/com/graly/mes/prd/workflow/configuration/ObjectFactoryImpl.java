package com.graly.mes.prd.workflow.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.util.ClassLoaderUtil;

public class ObjectFactoryImpl implements ObjectFactory {

	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(ObjectFactoryImpl.class);
	
	ClassLoader classLoader = ClassLoaderUtil.getClassLoader();
	List<ObjectInfo> objectInfos = null;
	Map<String, ObjectInfo> namedObjectInfos = null;
	Map singletons = new HashMap();
	Map objects = new HashMap();
	Collection objectsUnderConstruction = new HashSet();

	public ObjectFactoryImpl() {
		objectInfos = new ArrayList<ObjectInfo>();
		namedObjectInfos = new HashMap<String, ObjectInfo>();
	}

	public ObjectFactoryImpl(Map<String, ObjectInfo> namedObjectInfos, List<ObjectInfo> objectInfos) {
		this.namedObjectInfos = namedObjectInfos;
		this.objectInfos = objectInfos;
	}

	public void addObjectInfo(ObjectInfo objectInfo) {
		if (objectInfo.hasName()) {
			logger.debug("adding object info '" + objectInfo.getName() + "'");
			Object removed = namedObjectInfos.put(objectInfo.getName(), objectInfo);
			if (removed != null) {
				objectInfos.remove(removed);
			}
		}
		objectInfos.add(objectInfo);
	}

	/**
	 * create a new object of the given name.
	 * Before creation starts, the non-singlton objects will be cleared
	 * from the registry. The singletons will remain in the registry.   
	 */
	public synchronized Object createObject(String name) {
		ObjectInfo objectInfo = (ObjectInfo) namedObjectInfos.get(name);
		if (objectInfo == null) {
			throw new ConfigurationException("name '"
							+ name
							+ "' is not defined in the configuration. configured names: "
							+ namedObjectInfos.keySet());
		}
		return createObject(objectInfo);
	}

	public synchronized boolean hasObject(String name) {
		return namedObjectInfos.containsKey(name);
	}

	/**
	 * create a new object for the given index.
	 * Before creation starts, the non-singlton objects will be cleared
	 * from the registry. The singletons will remain in the registry.   
	 */
	public Object createObject(int index) {
		if ((index < 0) || (index >= objectInfos.size())) {
			throw new ConfigurationException("index '" + index
					+ "' is not defined in the configuration.  range [0.."
					+ (objectInfos.size() - 1) + "]");
		}
		return createObject((ObjectInfo) objectInfos.get(index));
	}

	/**
	 * create a new object for the given {@link ObjectInfo}.
	 * Before creation starts, the non-singlton objects will be cleared
	 * from the registry. The singletons will remain in the registry.   
	 */
	public Object createObject(ObjectInfo objectInfo) {
		clearRegistry();
		return getObject(objectInfo);
	}

	void clearRegistry() {
		objects.clear();
		objectsUnderConstruction.clear();
	}

	/**
	 * create an object of the given name.
	 * If the object was created before, that object is returned from 
	 * the registry.
	 */
	Object getObject(String name) {
		Object object = null;
		ObjectInfo objectInfo = (ObjectInfo) namedObjectInfos.get(name);
		if (objectInfo != null) {
			object = getObject(objectInfo);
		} else {
			logger.warn("no info for object '" + name + "'. defined objects: "
					+ namedObjectInfos.keySet().toString());
		}
		return object;
	}

	/**
	 * create an object for the given {@link ObjectInfo}.
	 * If the object was created before, that object is returned from 
	 * the registry.
	 */
	Object getObject(ObjectInfo objectInfo) {
		Object object = null;

		Object registryKey = getRegistryKey(objectInfo);
		if (isInRegistry(registryKey)) {
			object = findInRegistry(registryKey);

		} else {
			if (registryKey != null) {
				if (objectsUnderConstruction.contains(registryKey)) {
					throw new JbpmException(
							"circular object dependency on bean '"
									+ registryKey + "'");
				}
				objectsUnderConstruction.add(registryKey);
				try {
					object = objectInfo.createObject(this);
				} finally {
					objectsUnderConstruction.remove(registryKey);
				}

				putInRegistry(objectInfo, object, registryKey);

			} else {
				object = objectInfo.createObject(this);
			}
		}
		return object;
	}

	Class loadClass(String className) {
		try {
			return classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new JbpmException("couldn't load class '" + className + "'",
					e);
		}
	}

	Object getRegistryKey(ObjectInfo objectInfo) {
		Object key = null;
		if (objectInfo.hasName()) {
			key = objectInfo.getName();
		}
		return key;
	}

	boolean isInRegistry(Object registryKey) {
		return ((registryKey != null) && ((objects.containsKey(registryKey)) || (singletons
				.containsKey(registryKey))));
	}

	void putInRegistry(ObjectInfo objectInfo, Object object, Object registryKey) {
		if (objectInfo.isSingleton()) {
			singletons.put(registryKey, object);
		} else {
			objects.put(registryKey, object);
		}
	}

	Object findInRegistry(Object registryKey) {
		Object object = null;
		if (registryKey != null) {
			object = objects.get(registryKey);
			if (object == null)
				object = singletons.get(registryKey);
		}
		return object;
	}
}