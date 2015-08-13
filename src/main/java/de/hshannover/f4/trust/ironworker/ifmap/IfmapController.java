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
package de.hshannover.f4.trust.ironworker.ifmap;

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
import de.hshannover.f4.trust.ifmapj.exception.MarshalException;
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
import de.hshannover.f4.trust.ironworker.Main;
import de.hshannover.f4.trust.ironworker.util.Configuration;

public class IfmapController {

	private SSRC mSSRC;

	private final Logger LOGGER = Logger.getLogger(IfmapController.class);

	private Properties mConfiguration;

	private String mExtendedIdentifierXml;

	private Identifier mStartIdentifier;

	public IfmapController() {
		mConfiguration = Main.getConfiguration();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<ns:network ");
		stringBuilder.append("administrative-domain=\"\" ");
		stringBuilder.append("address=\"192.168.1.1\" ");
		stringBuilder.append("type=\"IPv4\" ");
		stringBuilder.append("netmask=\"255.255.255.0\" ");
		stringBuilder.append("xmlns:ns=\"http://www.example.com/extended-identifiers\" ");
		stringBuilder.append("/>");
		mExtendedIdentifierXml = stringBuilder.toString();

		try {
			mStartIdentifier = Identifiers
					.createExtendedIdentity(mExtendedIdentifierXml);
		} catch (MarshalException e) {
			e.printStackTrace();
		}
	}

	private void initSSRC() {
		String authenticationMethod =
				mConfiguration.getString(Configuration.KEY_IFMAP_AUTHENTICATION_METHOD,
						Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_METHOD);
		String basicAuthUrl = mConfiguration.getString(Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_URL,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_URL);
		String basicAuthUsername = mConfiguration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_USERNAME,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_USERNAME);
		String basicAuthPassword = mConfiguration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_PASSWORD,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_PASSWORD);
		String certAuthUrl = mConfiguration.getString(Configuration.KEY_IFMAP_AUTHENTICATION_CERT_URL,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_CERT_URL);
		String trustStorePath = mConfiguration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PATH,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_TRUSTSTORE_PATH);
		String trustStorePassword = mConfiguration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PASSWORD,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_TRUSTSTORE_PASSWORD);
		boolean threadSafe = mConfiguration.getBoolean(
				Configuration.KEY_IFMAP_THREADSAFE, Configuration.DEFAULT_VALUE_IFMAP_THREADSAFE);
		int initialConnectionTimeout = mConfiguration.getInt(
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

		try {
			if (authenticationMethod.equals(Configuration.IFMAP_AUTHENTICATION_METHOD_BASIC)) {
				BasicAuthConfig basicAuthConfig =
						new BasicAuthConfig(basicAuthUrl, basicAuthUsername, basicAuthPassword,
								trustStorePath, trustStorePassword, threadSafe,
								initialConnectionTimeout);
				LOGGER.info("Creating SSRC with basic authentication");
				mSSRC = IfmapJ.createSsrc(basicAuthConfig);
			} else if (authenticationMethod.equals(Configuration.IFMAP_AUTHENTICATION_METHOD_CERT)) {
				CertAuthConfig certAuthConfig =
						new CertAuthConfig(certAuthUrl, trustStorePath, trustStorePassword, trustStorePath,
								trustStorePassword, threadSafe, initialConnectionTimeout);
				LOGGER.info("Creating SSRC with certificate-based authentication");
				mSSRC = IfmapJ.createSsrc(certAuthConfig);
			} else {
				throw new IllegalArgumentException("Unknown authentication method '"
						+ authenticationMethod + "'");
			}
		} catch (InitializationException e) {
			LOGGER.error(e.getMessage());
			System.exit(-1);
		}
	}

	private void initSession() {
		try {
			LOGGER.info("Opening session");
			mSSRC.newSession();
		} catch (IfmapErrorResult e) {
			LOGGER.error(e.getMessage());
			System.exit(-1);
		} catch (IfmapException e) {
			LOGGER.error(e.getMessage());
			System.exit(-1);
		}
	}

	private void endSession() {
		LOGGER.info("Ending session");
		try {
			mSSRC.endSession();
		} catch (IfmapErrorResult e) {
			LOGGER.error(e.getMessage());
			System.exit(-1);
		} catch (IfmapException e) {
			LOGGER.error(e.getMessage());
			System.exit(-1);
		}
	}

	public void publish() {
		PublishRequest req = Requests.createPublishReq();

		VendorSpecificMetadataFactory factory = IfmapJ
				.createVendorSpecificMetadataFactory();
		String vendorSpecificMetadataXml = "<custom:part-of "
				+ "ifmap-cardinality=\"singleValue\" "
				+ "xmlns:custom=\"http://www.example.com/vendor-metadata\"> "
				+ "</custom:part-of>";
		Document outgoingMetadata = factory
				.createMetadata(vendorSpecificMetadataXml);

		try {
			PublishElement pe = Requests.createPublishUpdate(mStartIdentifier,
					outgoingMetadata);
			req.addPublishElement(pe);

			LOGGER.info("Publishing metadata");
			mSSRC.publish(req);
		} catch (IfmapErrorResult e) {
			e.printStackTrace();
		} catch (IfmapException e) {
			e.printStackTrace();
		}
	}

	public void search() {
		SearchRequest searchReq = Requests.createSearchReq();
		searchReq.setStartIdentifier(mStartIdentifier);
		searchReq.setMaxDepth(1);

		try {
			LOGGER.info("Search for metadata");
			SearchResult searchResult = mSSRC.search(searchReq);

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
		} catch (IfmapErrorResult e) {
			e.printStackTrace();
		} catch (IfmapException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		this.initSSRC();
		this.initSession();
		this.createAndStartWorkerThreads();
	}

	public void stop() {
		this.endSession();
	}

	private void createAndStartWorkerThreads() {
		// TODO Auto-generated method stub

	}

}
