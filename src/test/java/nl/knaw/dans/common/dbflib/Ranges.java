/**
 * Copyright (C) 2009-2016 DANS - Data Archiving and  Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.common.dbflib;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a byte range in a file to ignore while comparing it to another file.
 *
 * @author Jan van Mansum
 */
class Ranges
{
    private static class Range
    {
        int first;
        int last;

        Range(final int aFirst, final int aLast)
        {
            first = aFirst;
            last = aLast;
        }
    }

    private List<Range> rangesList = new ArrayList<Range>();

    void addRange(final int aFirst, final int aLast)
    {
        rangesList.add(new Range(aFirst, aLast));
    }

    boolean inRanges(final long aOffset)
    {
        for (final Range range : rangesList)
        {
            if (aOffset >= range.first && aOffset <= range.last)
            {
                return true;
            }
        }

        return false;
    }
}
