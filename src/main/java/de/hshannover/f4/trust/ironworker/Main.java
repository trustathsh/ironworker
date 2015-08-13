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

import static de.hshannover.f4.trust.ifmapj.metadata.MetadataWrapper.metadata;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.config.BasicAuthConfig;
import de.hshannover.f4.trust.ifmapj.config.CertAuthConfig;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.Identifier;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.messages.PublishElement;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.messages.ResultItem;
import de.hshannover.f4.trust.ifmapj.messages.SearchRequest;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import de.hshannover.f4.trust.ifmapj.metadata.Metadata;
import de.hshannover.f4.trust.ifmapj.metadata.VendorSpecificMetadataFactory;
import de.hshannover.f4.trust.ironcommon.properties.Properties;
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

		LOGGER.info("Loading configuration file: "
				+ Configuration.CONFIGURATION_FILENAME);
		Properties configuration = new Properties(Configuration.CONFIGURATION_FILENAME);

		String authenticationMethod =
				configuration.getString(Configuration.KEY_IFMAP_AUTHENTICATION_METHOD,
						Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_METHOD);
		String basicAuthUrl = configuration.getString(Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_URL,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_URL);
		String basicAuthUsername = configuration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_USERNAME,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_USERNAME);
		String basicAuthPassword = configuration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_PASSWORD,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_PASSWORD);
		String certAuthUrl = configuration.getString(Configuration.KEY_IFMAP_AUTHENTICATION_CERT_URL,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_CERT_URL);
		String trustStorePath = configuration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PATH,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_TRUSTSTORE_PATH);
		String trustStorePassword = configuration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PASSWORD,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_TRUSTSTORE_PASSWORD);
		boolean threadSafe = configuration.getBoolean(
				Configuration.KEY_IFMAP_THREADSAFE, Configuration.DEFAULT_VALUE_IFMAP_THREADSAFE);
		int initialConnectionTimeout = configuration.getInt(
				Configuration.KEY_IFMAP_INITIALCONNECTIONTIMEOUT,
				Configuration.DEFAULT_VALUE_IFMAP_INITIALCONNECTIONTIMEOUT);

		LOGGER.info("ironworker.ifmap.authentication.method: "
				+ authenticationMethod);
		LOGGER.info("ironworker.ifmap.authentication.basic.url: "
				+ basicAuthUrl);
		LOGGER.info("ironworker.ifmap.authentication.basic.username: "
				+ basicAuthUsername);
		LOGGER.info("ironworker.ifmap.authentication.basic.password: "
				+ basicAuthPassword);
		LOGGER.info("ironworker.ifmap.authentication.cert.url: "
				+ certAuthUrl);
		LOGGER.info("ironworker.ifmap.truststore.path: "
				+ trustStorePath);
		LOGGER.info("ironworker.ifmap.truststore.password: "
				+ trustStorePassword);
		LOGGER.info("ironworker.ifmap.threadsafe: "
				+ threadSafe);
		LOGGER.info("ironworker.ifmap.initialconnectiontimeout: "
				+ initialConnectionTimeout);

		BasicAuthConfig basicAuthConfig;
		CertAuthConfig certAuthConfig;
		SSRC ssrc;

		try {
			if (authenticationMethod.equals(Configuration.IFMAP_AUTHENTICATION_METHOD_BASIC)) {
				basicAuthConfig = new BasicAuthConfig(basicAuthUrl, basicAuthUsername, basicAuthPassword,
						trustStorePath, trustStorePassword, threadSafe,
						initialConnectionTimeout);
				LOGGER.info("Creating SSRC");
				ssrc = IfmapJ.createSsrc(basicAuthConfig);
			} else {
				certAuthConfig = new CertAuthConfig(certAuthUrl, trustStorePath, trustStorePassword, trustStorePath,
						trustStorePassword, threadSafe, initialConnectionTimeout);
				ssrc = IfmapJ.createSsrc(certAuthConfig);
			}

			LOGGER.info("Opening session");
			ssrc.newSession();
			PublishRequest req = Requests.createPublishReq();

			VendorSpecificMetadataFactory factory = IfmapJ
					.createVendorSpecificMetadataFactory();
			String vendorSpecificMetadataXml = "<custom:part-of "
					+ "ifmap-cardinality=\"singleValue\" "
					+ "xmlns:custom=\"http://www.example.com/vendor-metadata\"> "
					+ "</custom:part-of>";
			Document outgoingMetadata = factory
					.createMetadata(vendorSpecificMetadataXml);

			String extendedIdentifierXml = "<ns:network "
					+ "administrative-domain=\"\" "
					+ "address=\"192.168.1.1\" "
					+ "type=\"IPv4\" "
					+ "netmask=\"255.255.255.0\" "
					+ "xmlns:ns=\"http://www.example.com/extended-identifiers\" "
					+ "/>";
			Identifier identifier = Identifiers
					.createExtendedIdentity(extendedIdentifierXml);

			PublishElement pe = Requests.createPublishUpdate(identifier,
					outgoingMetadata);
			req.addPublishElement(pe);

			LOGGER.info("Publishing metadata");
			ssrc.publish(req);

			SearchRequest searchReq = Requests.createSearchReq();
			searchReq.setStartIdentifier(identifier);
			searchReq.setMaxDepth(1);

			LOGGER.info("Search for metadata");
			SearchResult searchResult = ssrc.search(searchReq);

			LOGGER.info("Printing search results");
			for (ResultItem resultItem : searchResult.getResultItems()) {
				Identifier[] identifiers = resultItem.getIdentifier();
				if (identifiers[0] != null) {
					LOGGER.info("Identifier: "
							+ identifiers[0]);
				}

				if (identifiers[1] != null) {
					LOGGER.info("Identifier: "
							+ identifiers[1]);
				}

				for (Document metadata : resultItem.getMetadata()) {
					Metadata incomingMetadata = metadata(metadata);

					String publisherId = incomingMetadata.getPublisherId();
					String publishTimestamp = incomingMetadata
							.getPublishTimestamp();
					String cardinality = incomingMetadata.getCardinality();
					String localname = incomingMetadata.getLocalname();
					String typename = incomingMetadata.getTypename();

					LOGGER.info("Publisher ID:      "
							+ publisherId);
					LOGGER.info("Publish timestamp: "
							+ publishTimestamp);
					LOGGER.info("Cardinality:       "
							+ cardinality);
					LOGGER.info("Local name:        "
							+ localname);
					LOGGER.info("Typename:          "
							+ typename);

					LOGGER.info("Formatted XML:     "
							+ incomingMetadata.toFormattedString());
				}
			}

			LOGGER.info("Ending session");
			ssrc.endSession();
		} catch (InitializationException e) {
			LOGGER.error(e.getMessage());
		} catch (IfmapErrorResult e) {
			LOGGER.error(e.getMessage());
		} catch (IfmapException e) {
			LOGGER.error(e.getMessage());
		}
	}
}
