/*
 * Copyright 2009-2010 Data Archiving and Networked Services (DANS), Netherlands.
 *
 * This file is part of DANS DBF Library.
 *
 * DANS DBF Library is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * DANS DBF Library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with DANS DBF Library. If
 * not, see <http://www.gnu.org/licenses/>.
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
