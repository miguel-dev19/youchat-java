/*
 * Copyright 2016 Google Inc. All rights reserved.
 *
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
 */

package com.google.android.flexbox;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import androidx.annotation.IntDef;

@IntDef({AlignContent.FLEX_START, AlignContent.FLEX_END, AlignContent.CENTER,
        AlignContent.SPACE_BETWEEN, AlignContent.SPACE_AROUND, AlignContent.STRETCH})
@Retention(RetentionPolicy.SOURCE)
public @interface AlignContent {
    int FLEX_START = 0;
    int FLEX_END = 1;
    int CENTER = 2;
    int SPACE_BETWEEN = 3;
    int SPACE_AROUND = 4;
    int STRETCH = 5;
}
