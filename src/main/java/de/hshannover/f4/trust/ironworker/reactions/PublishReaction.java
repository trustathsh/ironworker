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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.identifier.Identifier;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.messages.PublishElement;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.messages.ResultItem;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import de.hshannover.f4.trust.ifmapj.metadata.StandardIfmapMetadataFactory;
import de.hshannover.f4.trust.ironworker.Main;
import de.hshannover.f4.trust.ironworker.ifmap.IfmapController;

/**
 * @author Bastian Hellmann
 *
 */
public class PublishReaction extends Reaction {

	private static final int MAXIMUM_COUNTER = 2;

	private final Logger LOGGER = LogManager.getLogger(PublishReaction.class);

	private IfmapController mController = Main.getIfmapController();

	private int mCounter;
	private boolean mNotify;

	public PublishReaction(boolean notify) {
		LOGGER.info(notify == true ? "Metadata will be published via 'publish notify'"
				: "Metadata will be published via 'publish update'");
		mNotify = notify;
		mCounter = 0;
	}

	@Override
	protected void handleSearchResult(SearchResult searchResult) {
		for (ResultItem resultItem : searchResult.getResultItems()) {
			if (!isEmptySearchResult(resultItem)) {
				if (mCounter < MAXIMUM_COUNTER) {
					mController.publish(createPublishRequest());
					LOGGER.info("Publish metadata while handling SearchResult; counter is "
							+ mCounter);
					mCounter++;
				} else {
					LOGGER.debug("Counter has reached maximum of "
							+ mCounter);
				}
			} else {
				LOGGER.debug("ResultItem was empty");
			}
		}
	}

	@Override
	protected void handleUpdateResult(SearchResult searchResult) {
		for (ResultItem resultItem : searchResult.getResultItems()) {
			if (isNotEmpty(resultItem)) {
				if (mCounter < MAXIMUM_COUNTER) {
					mController.publish(createPublishRequest());
					LOGGER.info("Publish metadata while handling UpdateResult; counter is "
							+ mCounter);
					mCounter++;
				} else {
					LOGGER.debug("Counter has reached maximum of "
							+ mCounter);
				}
			} else {
				LOGGER.debug("ResultItem was empty");
			}
		}
	}

	@Override
	protected void handleNotifyResult(SearchResult searchResult) {
		// do nothing
	}

	@Override
	protected void handleDeleteResult(SearchResult searchResult) {
		// do nothing
	}

	private PublishRequest createPublishRequest() {
		List<PublishElement> elements = new ArrayList<>();

		Identifier device = Identifiers.createDev("freeradius-pdp");
		Identifier ar = Identifiers.createAr("ar"
				+ mCounter);

		StandardIfmapMetadataFactory factory = IfmapJ.createStandardMetadataFactory();
		Document outgoingMetadata = factory.createArDev();

		if (mNotify) {
			elements.add(Requests.createPublishNotify(device, ar,
					outgoingMetadata));
		} else {
			elements.add(Requests.createPublishUpdate(device, ar,
					outgoingMetadata));
		}

		if (elements.size() > 0) {
			return Requests.createPublishReq(elements);
		} else {
			return null;
		}
	}

	private boolean isNotEmpty(ResultItem resultItem) {
		if (resultItem.getIdentifier()[0] != null
				|| resultItem.getIdentifier()[1] != null) {
			return true;
		}
		if (!resultItem.getMetadata().isEmpty()) {
			return true;
		}
		if (resultItem.holdsLink()) {
			return true;
		}
		return false;
	}

	private boolean isEmptySearchResult(ResultItem resultItem) {
		if (((resultItem.getIdentifier()[0] != null
				&& resultItem.getIdentifier()[1] == null)
				|| (resultItem.getIdentifier()[0] == null
						&& resultItem.getIdentifier()[1] != null))
				&& resultItem.getMetadata().isEmpty()
				&& !resultItem.holdsLink()) {
			return true;
		}
		return false;
	}

}
