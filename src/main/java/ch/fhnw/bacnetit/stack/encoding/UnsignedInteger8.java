/*******************************************************************************
 * Copyright (C) 2016 The Java BACnetITB Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.fhnw.bacnetit.stack.encoding;

public class UnsignedInteger8 extends UnsignedInteger31 {
    private static final long serialVersionUID = 3671427317129449189L;

    private static final int MAX = 0xff - 1;

    public UnsignedInteger8(final int value) {
        super(value);
        if (value > MAX) {
            throw new IllegalArgumentException(
                    "Value cannot be greater than " + MAX);
        }
    }

    public UnsignedInteger8(final _ByteQueue queue) {
        super(queue);
    }
}
