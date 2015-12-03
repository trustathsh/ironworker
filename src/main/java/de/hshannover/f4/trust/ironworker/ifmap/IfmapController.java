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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.binding.IfmapStrings;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.config.BasicAuthConfig;
import de.hshannover.f4.trust.ifmapj.config.CertAuthConfig;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.Identifier;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.identifier.IdentityType;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.messages.SubscribeUpdate;
import de.hshannover.f4.trust.ironcommon.properties.Properties;
import de.hshannover.f4.trust.ironworker.Main;
import de.hshannover.f4.trust.ironworker.ResultHandler;
import de.hshannover.f4.trust.ironworker.util.Configuration;

/**
 * @author Bastian Hellmann
 *
 */
public class IfmapController {

	private SSRC mSsrc;

	private final Logger LOGGER = LogManager.getLogger(IfmapController.class);

	private Properties mConfiguration;

	private ResultHandler mHandler;

	private String mSubscriptionName;
	private String mSubscriptionIdentifierType;
	private String mSubscriptionIdentifierValue;
	private Integer mSubscriptionMaxDepth;
	private Integer mSubscriptionMaxSize;
	private String mSubscriptionMatchLinksFilter;
	private String mSubscriptionResultFilter;

	private String mAuthenticationMethod;

	private String mBasicAuthUrl;

	private String mBasicAuthUsername;

	private String mBasicAuthPassword;

	private String mCertAuthUrl;

	private String mTrustStorePath;

	private String mTrustStorePassword;

	private boolean mThreadSafe;

	private int mInitialConnectionTimeout;

	public IfmapController(ResultHandler handler) {
		mConfiguration = Main.getConfiguration();
		mHandler = handler;

		readIfmapConnectionInformationFromConfiguration();
		logIfmapConnectionInformation();

		readSubscriptionInformationFromConfiguration();
		logSubscriptionInformation();
	}

	private void logIfmapConnectionInformation() {
		LOGGER.debug(Configuration.KEY_IFMAP_AUTHENTICATION_METHOD
				+ ": " + mAuthenticationMethod);
		LOGGER.debug(Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_URL
				+ ": " + mBasicAuthUrl);
		LOGGER.debug(Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_USERNAME
				+ ": " + mBasicAuthUsername);
		LOGGER.debug(Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_PASSWORD
				+ ": " + mBasicAuthPassword);
		LOGGER.debug(Configuration.KEY_IFMAP_AUTHENTICATION_CERT_URL
				+ ": " + mCertAuthUrl);
		LOGGER.debug(Configuration.KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PASSWORD
				+ ": " + mTrustStorePath);
		LOGGER.debug(Configuration.KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PATH
				+ ": " + mTrustStorePassword);
		LOGGER.debug(Configuration.KEY_IFMAP_THREADSAFE
				+ ": " + mThreadSafe);
		LOGGER.debug(Configuration.KEY_IFMAP_INITIALCONNECTIONTIMEOUT
				+ ": " + mInitialConnectionTimeout);
	}

	private void readIfmapConnectionInformationFromConfiguration() {
		mAuthenticationMethod = mConfiguration.getString(Configuration.KEY_IFMAP_AUTHENTICATION_METHOD,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_METHOD);
		mBasicAuthUrl = mConfiguration.getString(Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_URL,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_URL);
		mBasicAuthUsername = mConfiguration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_USERNAME,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_USERNAME);
		mBasicAuthPassword = mConfiguration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_BASIC_PASSWORD,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_BASIC_PASSWORD);
		mCertAuthUrl = mConfiguration.getString(Configuration.KEY_IFMAP_AUTHENTICATION_CERT_URL,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_CERT_URL);
		mTrustStorePath = mConfiguration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PATH,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_TRUSTSTORE_PATH);
		mTrustStorePassword = mConfiguration.getString(
				Configuration.KEY_IFMAP_AUTHENTICATION_TRUSTSTORE_PASSWORD,
				Configuration.DEFAULT_VALUE_IFMAP_AUTHENTICATION_TRUSTSTORE_PASSWORD);
		mThreadSafe = mConfiguration.getBoolean(
				Configuration.KEY_IFMAP_THREADSAFE, Configuration.DEFAULT_VALUE_IFMAP_THREADSAFE);
		mInitialConnectionTimeout = mConfiguration.getInt(
				Configuration.KEY_IFMAP_INITIALCONNECTIONTIMEOUT,
				Configuration.DEFAULT_VALUE_IFMAP_INITIALCONNECTIONTIMEOUT);
	}

	private void logSubscriptionInformation() {
		LOGGER.debug(Configuration.KEY_SUBSCRIPTION_NAME
				+ ": " + mSubscriptionName);
		LOGGER.debug(Configuration.KEY_SUBSCRIPTION_IDENTIFIER_TYPE
				+ ": " + mSubscriptionIdentifierType);
		LOGGER.debug(Configuration.KEY_SUBSCRIPTION_IDENTIFIER_VALUE
				+ ": " + mSubscriptionIdentifierValue);
		LOGGER.debug(Configuration.KEY_SUBSCRIPTION_MAXDEPTH
				+ ": " + mSubscriptionMaxDepth);
		LOGGER.debug(Configuration.KEY_SUBSCRIPTION_MAXSIZE
				+ ": " + mSubscriptionMaxSize);
		LOGGER.debug(Configuration.KEY_SUBSCRIPTION_MATCHLINKS
				+ ": " + mSubscriptionMatchLinksFilter);
		LOGGER.debug(Configuration.KEY_SUBSCRIPTION_RESULTFILTER
				+ ": " + mSubscriptionResultFilter);
	}

	private void readSubscriptionInformationFromConfiguration() {
		mSubscriptionName = mConfiguration.getString(Configuration.KEY_SUBSCRIPTION_NAME,
				Configuration.DEFAULT_VALUE_SUBSCRIPTION_NAME);
		mSubscriptionIdentifierType = mConfiguration.getString(Configuration.KEY_SUBSCRIPTION_IDENTIFIER_TYPE,
				Configuration.DEFAULT_VALUE_SUBSCRIPTION_IDENTIFIER_TYPE);
		mSubscriptionIdentifierValue = mConfiguration.getString(Configuration.KEY_SUBSCRIPTION_IDENTIFIER_VALUE,
				Configuration.DEFAULT_VALUE_SUBSCRIPTION_IDENTIFIER_VALUE);
		mSubscriptionMaxDepth = mConfiguration.getInt(Configuration.KEY_SUBSCRIPTION_MAXDEPTH,
				Configuration.DEFAULT_VALUE_SUBSCRIPTION_MAXDEPTH);
		mSubscriptionMaxSize = mConfiguration.getInt(Configuration.KEY_SUBSCRIPTION_MAXSIZE,
				Configuration.DEFAULT_VALUE_SUBSCRIPTION_MAXSIZE);
		mSubscriptionMatchLinksFilter = mConfiguration.getString(Configuration.KEY_SUBSCRIPTION_MATCHLINKS,
				Configuration.DEFAULT_VALUE_SUBSCRIPTION_MATCHLINKS);
		mSubscriptionResultFilter = mConfiguration.getString(Configuration.KEY_SUBSCRIPTION_RESULTFILTER,
				Configuration.DEFAULT_VALUE_SUBSCRIPTION_RESULTFILTER);
	}

	private void initSSRC() {
		try {
			if (mAuthenticationMethod.equals(Configuration.IFMAP_AUTHENTICATION_METHOD_BASIC)) {
				BasicAuthConfig basicAuthConfig =
						new BasicAuthConfig(mBasicAuthUrl, mBasicAuthUsername, mBasicAuthPassword,
								mTrustStorePath, mTrustStorePassword, mThreadSafe,
								mInitialConnectionTimeout);
				LOGGER.info("Creating SSRC with basic authentication");
				mSsrc = IfmapJ.createSsrc(basicAuthConfig);
			} else if (mAuthenticationMethod.equals(Configuration.IFMAP_AUTHENTICATION_METHOD_CERT)) {
				CertAuthConfig certAuthConfig =
						new CertAuthConfig(mCertAuthUrl, mTrustStorePath, mTrustStorePassword, mTrustStorePath,
								mTrustStorePassword, mThreadSafe, mInitialConnectionTimeout);
				LOGGER.info("Creating SSRC with certificate-based authentication");
				mSsrc = IfmapJ.createSsrc(certAuthConfig);
			} else {
				throw new IllegalArgumentException("Unknown authentication method '"
						+ mAuthenticationMethod + "'");
			}
		} catch (InitializationException e) {
			LOGGER.error(e.getMessage());
			System.exit(-1);
		}
	}

	private void initSession() {
		try {
			LOGGER.info("Opening session");
			mSsrc.newSession();
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
			mSsrc.endSession();
		} catch (IfmapErrorResult e) {
			LOGGER.error(e.getMessage());
			System.exit(-1);
		} catch (IfmapException e) {
			LOGGER.error(e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * @param request
	 */
	public void publish(PublishRequest request) {
		try {
			synchronized (mSsrc) {
				mSsrc.publish(request);
			}
			LOGGER.info("New metadata was published.");
		} catch (IfmapErrorResult e) {
			LOGGER.error("Got IfmapErrorResult: "
					+ e.getMessage() + ", " + e.getCause());
		} catch (IfmapException e) {
			LOGGER.error("Got IfmapException: "
					+ e.getMessage() + ", " + e.getCause());
		}
	}

	private void subscribe() {
		LOGGER.info("Subscribe for configured identifier ("
				+ mSubscriptionIdentifierType + ", " + mSubscriptionIdentifierValue + ")");

		SubscribeUpdate subscribeUpdate = Requests.createSubscribeUpdate();

		Identifier startIdentifier = createIdentifier(mSubscriptionIdentifierType, mSubscriptionIdentifierValue);

		if (startIdentifier == null) {
			LOGGER.error("Could not create start identifier for subscription");
			System.exit(-1);
		}

		subscribeUpdate.setName(mSubscriptionName);
		subscribeUpdate.setStartIdentifier(startIdentifier);
		subscribeUpdate.setMaxDepth(mSubscriptionMaxDepth);
		subscribeUpdate.setMaxSize(mSubscriptionMaxSize);
		subscribeUpdate.setMatchLinksFilter(mSubscriptionMatchLinksFilter);
		subscribeUpdate.setResultFilter(mSubscriptionResultFilter);

		subscribeUpdate.addNamespaceDeclaration(IfmapStrings.BASE_PREFIX,
				IfmapStrings.BASE_NS_URI);
		subscribeUpdate.addNamespaceDeclaration(
				IfmapStrings.STD_METADATA_PREFIX,
				IfmapStrings.STD_METADATA_NS_URI);

		synchronized (mSsrc) {
			try {
				mSsrc.subscribe(Requests.createSubscribeReq(subscribeUpdate));
			} catch (IfmapErrorResult e) {
				LOGGER.error(e.getMessage());
				System.exit(-1);
			} catch (IfmapException e) {
				LOGGER.error(e.getMessage());
				System.exit(-1);
			}
		}
		LOGGER.debug("Subscription running.");
	}

	private Identifier createIdentifier(String type, String value) {
		switch (type) {
			case "device":
				return Identifiers.createDev(value);
			case "access-request":
				return Identifiers.createAr(value);
			case "ip-address":
				String[] ip = value.split(";");
				String ipType = ip[0];
				String ipValue = ip[1];
				if (ipType.equals("IPv4")) {
					return Identifiers.createIp4(ipValue);
				} else {
					return Identifiers.createIp6(ipValue);
				}
			case "mac-address":
				return Identifiers.createMac(value);
			case "identity":
				String[] id = value.split(";");
				IdentityType idType = IdentityType.valueOf(id[0]);
				String idValue = id[1];
				return Identifiers.createIdentity(idType, idValue);
			default:
				break;
		}

		return null;
	}

	/**
	 *
	 */
	public void start() {
		this.initSSRC();
		this.initSession();
		this.createAndStartWorkerThreads();
		this.subscribe();
	}

	/**
	 *
	 */
	public void stop() {
		this.endSession();
	}

	private void createAndStartWorkerThreads() {
		LOGGER.debug("Starting worker threads ...");

		SubscriptionPoller poller = null;
		try {
			poller = new SubscriptionPoller(mHandler, mSsrc.getArc());
		} catch (InitializationException e) {
			LOGGER.error("Could not initialize ifmapj: "
					+ e.getMessage() + ", " + e.getCause());
			System.exit(-1);
		}

		Thread pollerThread = new Thread(poller,
				SubscriptionPoller.class.getSimpleName());
		pollerThread.start();
	}

}
