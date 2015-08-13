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

import de.hshannover.f4.trust.ifmapj.channel.ARC;
import de.hshannover.f4.trust.ifmapj.exception.EndSessionException;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.messages.PollResult;
import de.hshannover.f4.trust.ironworker.ResultHandler;

/**
 * @author Bastian Hellmann
 *
 */
public class SubscriptionPoller implements Runnable {

	private final Logger LOGGER = LogManager.getLogger(SubscriptionPoller.class);

	private ARC mArc;

	private ResultHandler mResultHandler;

	public SubscriptionPoller(ResultHandler resultHandler, ARC arc) {
		mResultHandler = resultHandler;
		mArc = arc;
	}

	@Override
	public void run() {
		LOGGER.info(SubscriptionPoller.class.getSimpleName()
				+ " runs ...");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				LOGGER.info("Polling ...");
				PollResult pollResult = mArc.poll();
				mResultHandler.submitNewPollResult(pollResult);
			} catch (IfmapErrorResult e) {
				LOGGER.error("Got IfmapError: "
						+ e.getMessage() + ", " + e.getCause());
			} catch (EndSessionException e) {
				LOGGER.error("The session with the MAP server was closed: "
						+ e.getMessage() + ", " + e.getCause());
				System.exit(-1);
			} catch (IfmapException e) {
				LOGGER.error("Error at polling the MAP server: "
						+ e.getMessage() + ", " + e.getCause());
				System.exit(-1);
			}
		}
	}

}
