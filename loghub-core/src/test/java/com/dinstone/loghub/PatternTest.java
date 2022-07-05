/*
 * Copyright (C) 2018-2022 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dinstone.loghub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {

    public static void main(String[] args) {
        case01();
        case02();

        case03();
        
        String input = "2018-12-22_9.log";
        String prefix = "";
        String suffix = "log";
        StringBuilder b = new StringBuilder("^");
        if (prefix != null && !prefix.isEmpty()) {
            b.append(Pattern.quote(prefix));
            b.append("_");
        }
        b.append("(\\d{4}-\\d{1,2}-\\d{1,2})_(\\d*)");
        if (suffix != null && !suffix.isEmpty()) {
            b.append("\\.");
            b.append(Pattern.quote(suffix));
        }
        b.append("$");

        Pattern pattern = Pattern.compile(b.toString());
        Matcher m = pattern.matcher(input);
        if (m.matches()) {
            System.out.println(input + " match " + pattern.pattern() + " ok");
        }
    }

    private static void case03() {
        String input = "2018-12-22_9";
        Pattern pattern = Pattern.compile("^(\\d{4}-\\d{1,2}-\\d{1,2})_(\\d*)$");
        Matcher m = pattern.matcher(input);
        if (m.matches()) {
            System.out.println(input + " match " + pattern.pattern() + " ok");
        }
    }

    private static void case02() {
        String input = "2018-12-22_909.log";

        String prefix = "";
        String suffix = "log";

        Pattern pattern = Pattern.compile(
            "^(" + Pattern.quote(prefix) + ")_*\\d{4}-\\d{1,2}-\\d{1,2}_\\d+\\.(" + Pattern.quote(suffix) + ")$");
        Matcher m = pattern.matcher(input);
        if (m.matches()) {
            System.out.println(input + " match " + pattern.pattern() + " ok");
        }
    }

    private static void case01() {
        String input = "jul_2018-12-22_909.log";

        String prefix = "jul";
        String suffix = "log";

        Pattern pattern = Pattern.compile(
            "^(" + Pattern.quote(prefix) + ")_\\d{4}-\\d{1,2}-\\d{1,2}_\\d+\\.(" + Pattern.quote(suffix) + ")$");
        Matcher m = pattern.matcher(input);
        if (m.matches()) {
            System.out.println(input + " match " + pattern.pattern() + " ok");
        }
    }

}
