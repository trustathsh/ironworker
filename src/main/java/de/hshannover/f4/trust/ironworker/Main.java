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
package de.hshannover.f4.trust.ironworker;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.ironcommon.properties.Properties;
import de.hshannover.f4.trust.ironworker.ifmap.IfmapController;
import de.hshannover.f4.trust.ironworker.util.Configuration;

/**
 * Main class of ironworker.
 * Reads configuration files, creates IF-MAP threads and worker threads.
 *
 * @author Bastian Hellmann
 *
 */
public final class Main {

	private static final String VERSION = "${project.version}";

	private static final Logger LOGGER = Logger.getLogger(Main.class);

	private static Properties configuration;

	/**
	 * Empty private constructor since everything is static in this class
	 */
	private Main() {
	}

	/**
	 * An exemplary IF-MAP client.
	 * Uses YAML-configuration file, creates a new MAP server connection,
	 * publishes some vendor-specific metadata and extended identifiers,
	 * then searches for this data on the MAPS and prints the data.
	 *
	 * @param args
	 *            command line arguments (not used)
	 */
	public static void main(String[] args) {
		LOGGER.info("Starting ironworker version "
				+ VERSION);

		IfmapController controller = new IfmapController();
		controller.start();
		controller.publish();
		controller.search();
		controller.stop();
	}

	public static Properties getConfiguration() {
		if (configuration == null) {
			LOGGER.info("Loading configuration file: "
					+ Configuration.CONFIGURATION_FILENAME);
			configuration = new Properties(Configuration.CONFIGURATION_FILENAME);
		}

		return configuration;
	}

}
