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
package de.hshannover.f4.trust.ironworker.reactions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hshannover.f4.trust.ifmapj.messages.SearchResult;

/**
 * @author Bastian Hellmann
 *
 */
public class RunScriptReaction extends Reaction {

	private final Logger LOGGER = LogManager.getLogger(RunScriptReaction.class);
	private String mScript = null;

	public RunScriptReaction(String script) {
		mScript = script;
	}

	@Override
	protected void handleSearchResult(SearchResult searchResult) {
		LOGGER.debug("Running script '"
				+ mScript + "' while handling SearchResult");
		runScript();
	}

	@Override
	protected void handleUpdateResult(SearchResult searchResult) {
		LOGGER.debug("Running script '"
				+ mScript + "' while handling UpdateResult");
		runScript();
	}

	@Override
	protected void handleNotifyResult(SearchResult searchResult) {
		// do nothing
	}

	@Override
	protected void handleDeleteResult(SearchResult searchResult) {
		// do nothing
	}

	private void runScript() {
		if (mScript != null
				&& !mScript.equals("")) {
			this.processCommand(mScript);
		}
	}

	private void processCommand(String commandWithParameter) {
		String[] commandParts = commandWithParameter.split(" ");
		String command = commandParts[0];

		List<String> commandList = new ArrayList<String>();
		for (int i = 0; i < commandParts.length; i++) {
			commandList.add(commandParts[i]);
		}

		ProcessBuilder pb = new ProcessBuilder(commandList);
		Map<String, String> environment = pb.environment();
		environment.put("foo", "bar");
		try {
			LOGGER.info("Trying to run command: "
					+ command + " in " + System.getProperty("user.dir"));
			Process p = pb.start();
			p.waitFor();
			printResult(p);
		} catch (IOException e) {
			LOGGER.error("Failed at running script: "
					+ e.getMessage());
		} catch (InterruptedException e) {
			LOGGER.info("Process was interupted: "
					+ e.getMessage());
		}
	}

	private void printResult(Process p) {
		LOGGER.info("Process exited "
				+ (p.exitValue() == 0 ? "successful" : "NOT successful"));
	}

}
