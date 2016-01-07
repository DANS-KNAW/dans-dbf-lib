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
 * Enumerates the supported table versions.
 * <p>
 * <b>Note:</b> Not all versions listed here are supported yet.
 *
 * @author Jan van Mansum
 * @author Vesa Åkerman
 */
public enum Version
{

    DBASE_3(254, 19, 1, 0, 0x1a1a, 2,
            getDbase3Types()),
    DBASE_4(254, 20, 1, 8, 0x00, 0,
            getDbase4Types()), 
    DBASE_5(254, 20, 1, 8, 0x00, 0,
            getDbase5Types()), 
    CLIPPER_5(1024, 19, 2, 0, 0x1a, 1,
              getClipper5Types()), 
    FOXPRO_26(254, 20, 1, 8, 0x00, 0,
              getFoxProTypes());
    // Trick Jalopy formatter into behaving.
    private static Type[] getDbase3Types()
    {
        return new Type[] { Type.CHARACTER, Type.NUMBER, Type.DATE, Type.LOGICAL, Type.MEMO };
    }

    private static Type[] getDbase4Types()
    {
        return new Type[] { Type.CHARACTER, Type.NUMBER, Type.DATE, Type.LOGICAL, Type.MEMO, Type.FLOAT };
    }

    private static Type[] getDbase5Types()
    {
        return new Type[]
               {
                   Type.CHARACTER, Type.NUMBER, Type.DATE, Type.LOGICAL, Type.MEMO, Type.FLOAT, Type.BINARY,
                   Type.GENERAL
               };
    }

    private static Type[] getClipper5Types()
    {
        return new Type[] { Type.CHARACTER, Type.NUMBER, Type.DATE, Type.LOGICAL, Type.MEMO };
    }

    private static Type[] getFoxProTypes()
    {
        return new Type[]
               {
                   Type.CHARACTER, Type.NUMBER, Type.DATE, Type.LOGICAL, Type.MEMO, Type.FLOAT, Type.GENERAL,
                   Type.PICTURE
               };
    }

    private final int maxLengthCharField;
    private final int maxLengthNumberField;
    private final int lengthHeaderTerminator;
    private final int memoDataOffset;
    private final int memoFieldEndMarker;
    private final int memoFieldEndMarkerLength;
    final List<Type> fieldTypes = new ArrayList<Type>();

    Version(final int maxLengthCharField, final int maxLengthNumberField, final int lengthHeaderTerminator,
            final int memoDataOffset, final int memoFieldEndMarker, final int memoFieldEndMarkerLength,
            final Type[] fieldTypes)
    {
        this.maxLengthCharField = maxLengthCharField;
        this.maxLengthNumberField = maxLengthNumberField;
        this.lengthHeaderTerminator = lengthHeaderTerminator;
        this.memoDataOffset = memoDataOffset;
        this.memoFieldEndMarker = memoFieldEndMarker;
        this.memoFieldEndMarkerLength = memoFieldEndMarkerLength;

        for (final Type type : fieldTypes)
        {
            this.fieldTypes.add(type);
        }
    }

    int getMaxLengthCharField()
    {
        return maxLengthCharField;
    }

    int getMaxLengthNumberField()
    {
        return maxLengthNumberField;
    }

    int getLengthHeaderTerminator()
    {
        return lengthHeaderTerminator;
    }

    int getMemoDataOffset()
    {
        return memoDataOffset;
    }

    int getMemoFieldEndMarker()
    {
        return memoFieldEndMarker;
    }

    int getMemoFieldEndMarkerLength()
    {
        return memoFieldEndMarkerLength;
    }

    static int getVersionByte(final Version version, final boolean hasMemo)
    {
        if (version == DBASE_3 || version == CLIPPER_5)
        {
            if (hasMemo)
            {
                return 0x83;
            }
            else
            {
                return 0x03;
            }
        }
        else if (version == DBASE_4 || version == DBASE_5)
        {
            if (hasMemo)
            {
                return 0x8B;
            }
            else
            {
                return 0x03;
            }
        }
        else if (version == FOXPRO_26)
        {
            if (hasMemo)
            {
                return 0xF5;
            }
            else
            {
                return 0x03;
            }
        }

        return 0;
    }

    static Version getVersion(final int versionByte, final int lengthHeaderTerminator)
    {
        if (lengthHeaderTerminator == 2)
        {
            return CLIPPER_5;
        }
        else if (versionByte == 0x83)
        {
            return DBASE_3;
        }
        else if (versionByte == 0xF5)
        {
            return FOXPRO_26;
        }
        else
        {
            return DBASE_5;
        }
    }
}
