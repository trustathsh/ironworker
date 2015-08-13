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

import static de.hshannover.f4.trust.ifmapj.metadata.MetadataWrapper.metadata;

import org.w3c.dom.Document;

import de.hshannover.f4.trust.ifmapj.identifier.Identifier;
import de.hshannover.f4.trust.ifmapj.messages.ResultItem;
import de.hshannover.f4.trust.ifmapj.metadata.Metadata;

public class ResultItemHelper {

	private ResultItemHelper() {
	}

	/**
	 * @param resultItem
	 * @return
	 */
	public static String extractResultItemInformation(ResultItem resultItem) {
		StringBuilder sb = new StringBuilder();
		Identifier[] identifiers = resultItem.getIdentifier();
		if (identifiers[0] != null) {
			sb.append("Identifier: "
					+ identifiers[0] + "\n");
		}

		if (identifiers[1] != null) {
			sb.append("Identifier: "
					+ identifiers[1] + "\n");
		}

		for (Document metadata : resultItem.getMetadata()) {
			Metadata incomingMetadata = metadata(metadata);

			String publisherId = incomingMetadata.getPublisherId();
			String publishTimestamp = incomingMetadata
					.getPublishTimestamp();
			String cardinality = incomingMetadata.getCardinality();
			String localname = incomingMetadata.getLocalname();
			String typename = incomingMetadata.getTypename();

			sb.append("Publisher ID:      "
					+ publisherId + "\n");
			sb.append("Publish timestamp: "
					+ publishTimestamp + "\n");
			sb.append("Cardinality:       "
					+ cardinality + "\n");
			sb.append("Local name:        "
					+ localname + "\n");
			sb.append("Typename:          "
					+ typename + "\n");

			sb.append("Formatted XML:     "
					+ incomingMetadata.toFormattedString());
		}

		return sb.toString();
	}

}
