/*
 * Copyright 2010 The Lucene Interval Field Authors.
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

package com.greplin.interval;

import com.sun.tools.javac.util.Pair;
import org.apache.lucene.search.Searcher;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests for the in numeric range query.
 */
public class InNumericIntervalQueryTest extends BaseIntervalQueryTest {

  private static final Pair<Long, Long> EXAMPLE = Pair.of(1257642000L, 1257645600L);

  private static final Pair<Long, Long> EDGE = Pair.of(1257642240L, 1257645568L);

  private static final Pair<Long, Long> NEGATIVE = Pair.of(-100L, -50L);

  private static final Pair<Long, Long> ACROSS_ZERO = Pair.of(-80L, 80L);

  @Test
  public void testBasics() throws IOException {
    addDocument(1, EXAMPLE);

    Searcher searcher = getSearcher();
    assertSearch(searcher, 1257600000);
    assertSearch(searcher, 1257641999);
    assertSearch(searcher, 1257642000, 1);
    assertSearch(searcher, 1257644000, 1);
    assertSearch(searcher, 1257645600, 1);
    assertSearch(searcher, 1257645601);
  }

  @Test
  public void testEdge() throws IOException {
    addDocument(2, EDGE);

    Searcher searcher = getSearcher();
    assertSearch(searcher, 1257600000);
    assertSearch(searcher, 1257641999);
    assertSearch(searcher, 1257642000);
    assertSearch(searcher, 1257642239);
    assertSearch(searcher, 1257642240, 2);
    assertSearch(searcher, 1257644000, 2);
    assertSearch(searcher, 1257645568, 2);
    assertSearch(searcher, 1257645600);
    assertSearch(searcher, 1257645600);
    assertSearch(searcher, 1257645601);
  }

  @Test
  public void testNegative() throws IOException {
    addDocument(3, NEGATIVE);
    addDocument(4, ACROSS_ZERO);

    Searcher searcher = getSearcher();
    assertSearch(searcher, -1000);
    assertSearch(searcher, 1000);

    assertSearch(searcher, -101);
    assertSearch(searcher, -100, 3);
    assertSearch(searcher, -80, 3, 4);
    assertSearch(searcher, -50, 3, 4);
    assertSearch(searcher, -49, 4);
    assertSearch(searcher, -1, 4);
    assertSearch(searcher, 0, 4);
    assertSearch(searcher, 1, 4);
    assertSearch(searcher, 80, 4);
    assertSearch(searcher, 81);
  }

  @Test
  public void testMultiple() throws IOException {
    addDocument(1, EXAMPLE);
    addDocument(2, EDGE);

    Searcher searcher = getSearcher();
    assertSearch(searcher, 1257600000);
    assertSearch(searcher, 1257641999);
    assertSearch(searcher, 1257642000, 1);
    assertSearch(searcher, 1257642239, 1);
    assertSearch(searcher, 1257642240, 1, 2);
    assertSearch(searcher, 1257644000, 1, 2);
    assertSearch(searcher, 1257645568, 1, 2);
    assertSearch(searcher, 1257645600, 1);
    assertSearch(searcher, 1257645600, 1);
    assertSearch(searcher, 1257645601);
  }


  private void assertSearch(Searcher searcher, long value, Integer... expectedResults) throws IOException {
    assertSearch(searcher, new InNumericIntervalQuery("time", value), expectedResults);
  }
}
