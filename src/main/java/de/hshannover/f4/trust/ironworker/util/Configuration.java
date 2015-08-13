/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of ironworker, version 0.0.1,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2015 Trust@HsH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.hshannover.f4.trust.ironworker.util;

/**
 * @author Bastian Hellmann
 *
 */
public class Configuration {

	private Configuration() {
	}

	public static final String CONFIGURATION_FILENAME = "config/ironworker.yml";

	public static final String KEY_IFMAP_AUTHENTICATION_METHOD = "ironworker.ifmap.authentication.method";
	public static final String DEFAULT_VALUE_IFMAP_AUTHENTICATION_METHOD = "basic";

	public static final String KEY_IFMAP_AUTHENTICATION_BASIC_URL = "ironworker.ifmap.authentication.basic.url";
	public static final String DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_URL = "https://localhost:8443";

	public static final String KEY_IFMAP_AUTHENTICATION_BASIC_USERNAME =
			"ironworker.ifmap.authentication.basic.username";
	public static final String DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_USERNAME = "ironworker";

	public static final String KEY_IFMAP_AUTHENTICATION_BASIC_PASSWORD =
			"ironworker.ifmap.authentication.basic.password";
	public static final String DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_PASSWORD = "ironworker";

	public static final String KEY_IFMAP_AUTHENTICATION_CERT_URL = "ironworker.ifmap.authentication.cert.url";
	public static final String DEFAULT_VALUE_IFMAP_AUTHENTICATION_CERT_URL = "https://localhost:8444";

	public static final String KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PATH =
			"ironworker.ifmap.authentication.truststore.path";
	public static final String DEFAULT_VALUE_IFMAP_AUTHENTICATION_TRUSTSTORE_PATH = "/keystore/ironworker.jks";

	public static final String KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PASSWORD =
			"ironworker.ifmap.authentication.truststore.password";
	public static final String DEFAULT_VALUE_IFMAP_AUTHENTICATION_TRUSTSTORE_PASSWORD = "ironworker";

	public static final String KEY_IFMAP_THREADSAFE = "ironworker.ifmap.threadsafe";
	public static final boolean DEFAULT_VALUE_IFMAP_THREADSAFE = true;

	public static final String KEY_IFMAP_INITIALCONNECTIONTIMEOUT = "ironworker.ifmap.initialconnectiontimeout";
	public static final int DEFAULT_VALUE_IFMAP_INITIALCONNECTIONTIMEOUT = (120
			* 1000);

	public static final String IFMAP_AUTHENTICATION_METHOD_BASIC = "basic";
	public static final String IFMAP_AUTHENTICATION_METHOD_CERT = "cert";

	public static final String KEY_SUBSCRIPTION_NAME = "ironworker.subscription.name";
	public static final String DEFAULT_VALUE_SUBSCRIPTION_NAME = "ironworker-sub";

	public static final String KEY_SUBSCRIPTION_IDENTIFIER_TYPE = "ironworker.subscription.identifier.type";
	public static final String DEFAULT_VALUE_SUBSCRIPTION_IDENTIFIER_TYPE = "device";

	public static final String KEY_SUBSCRIPTION_IDENTIFIER_VALUE = "ironworker.subscription.identifier.value";
	public static final String DEFAULT_VALUE_SUBSCRIPTION_IDENTIFIER_VALUE = "freeradius-pdp";

	public static final String KEY_SUBSCRIPTION_MAXDEPTH = "ironworker.subscription.maxDepth";
	public static final int DEFAULT_VALUE_SUBSCRIPTION_MAXDEPTH = 10;

	public static final String KEY_SUBSCRIPTION_MAXSIZE = "ironworker.subscription.maxSize";
	public static final int DEFAULT_VALUE_SUBSCRIPTION_MAXSIZE = 1000000;

	public static final String KEY_SUBSCRIPTION_MATCHLINKS = "ironworker.subscription.matchLinks";
	public static final String DEFAULT_VALUE_SUBSCRIPTION_MATCHLINKS = null;

	public static final String KEY_SUBSCRIPTION_RESULTFILTER = "ironworker.subscription.resultFilter";
	public static final String DEFAULT_VALUE_SUBSCRIPTION_RESULTFILTER = null;

	public static final String KEY_REACTIONS_LOG_ACTIVE = "ironworker.reactions.log.active";
	public static final boolean DEFAULT_VALUE_REACTIONS_LOG_ACTIVE = true;

	public static final String KEY_REACTIONS_PUBLISH_ACTIVE = "ironworker.reactions.publish.active";
	public static final boolean DEFAULT_VALUE_REACTIONS_PUBLISH_ACTIVE = true;

	public static final String KEY_REACTIONS_PUBLISH_NOTIFY = "ironworker.reactions.publish.notify";
	public static final boolean DEFAULT_VALUE_REACTIONS_PUBLISH_NOTIFY = true;

	public static final String KEY_REACTIONS_RUNSCRIPT_ACTIVE = "ironworker.reactions.runscript.active";
	public static final boolean DEFAULT_VALUE_REACTIONS_RUNSCRIPT_ACTIVE = false;

	public static final String KEY_REACTIONS_RUNSCRIPT_SCRIPT = "ironworker.reactions.runscript.script";
	public static final String DEFAULT_VALUE_REACTIONS_RUNSCRIPT_SCRIPT = "foo.sh";
}
