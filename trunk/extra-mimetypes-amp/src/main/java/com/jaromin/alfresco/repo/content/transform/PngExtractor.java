/**
 * Copyright 2012 Patrick Jaromin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.jaromin.alfresco.repo.content.transform;

/**
 * 
 * @author pjaromin
 *
 */
public class PngExtractor extends AbstractDelimitedBitmapExtractor {

	public static final byte[] PNG_HEADER = { -119, 80, 78, 71,  13, 10, 26,   10 };
	public static final byte[] PNG_FOOTER = {   73, 69, 78, 68, -82, 66, 96, -126 };

	@Override
	byte[] getHeaderSequence() {
		return PNG_HEADER;
	}

	@Override
	byte[] getFooterSequence() {
		return PNG_FOOTER;
	}

}
