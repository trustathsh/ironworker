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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hshannover.f4.trust.ifmapj.messages.PollResult;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import de.hshannover.f4.trust.ironworker.reactions.Reaction;

/**
 * @author Bastian Hellmann
 *
 */
public class ResultHandler implements Runnable {

	private final Logger LOGGER = LogManager.getLogger(ResultHandler.class);

	private LinkedBlockingQueue<PollResult> mIncomingResults;

	private List<Reaction> mReactions;

	public ResultHandler() {
		mIncomingResults = new LinkedBlockingQueue<PollResult>();
		mReactions = new ArrayList<>();
	}

	/**
	 * Run the handler loop. The following steps are performed:
	 * <p>
	 * 1. Wait for new SearchResults in the queue.<br>
	 * 2. If new SearchResults arrive:<br>
	 * 2.1 ...
	 * 3. Start at 1. again.
	 */
	@Override
	public void run() {
		LOGGER.info(ResultHandler.class.getSimpleName()
				+ " runs ...");

		try {
			while (!Thread.currentThread().isInterrupted()) {
				PollResult lastPollResult = mIncomingResults.take();
				onNewPollResult(lastPollResult);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.info("Got interrupt signal while waiting for new work, exiting ...");
		} finally {
			LOGGER.info("Shutdown complete.");
		}
	}

	/**
	 * Handle a {@link PollResult} and take actions as defined in the configuration.
	 *
	 * @param lastPollResult
	 *            the last {@link SearchResult} inside the incoming queue
	 */
	private void onNewPollResult(PollResult lastPollResult) {
		LOGGER.info("PollResult received...");

		for (Reaction reaction : mReactions) {
			reaction.react(lastPollResult);
		}
		LOGGER.info("All reactions handled");
	}

	/**
	 * Submit a new {@link PollResult} to this {@link ResultHandler}.
	 *
	 * @param pollResult
	 *            the new {@link PollResult} to submit
	 */
	public void submitNewPollResult(PollResult pollResult) {
		try {
			mIncomingResults.put(pollResult);
			LOGGER.debug("PollResult was inserted");
		} catch (InterruptedException e) {
			LOGGER.error("Could not insert PollResult to ResultHandler: "
					+ e.getMessage());
		}
	}

	/**
	 * @param reactions
	 */
	public void setReactions(List<Reaction> reactions) {
		mReactions = reactions;
	}
}
