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

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of the field types available in an xBase database. xBase types are mapped to Java
 * types as specified in the enumerated constant descriptions below.
 * <p>
 * <b>Note on NUMBER and FLOAT values:</b> In dBase III NUMBER and in dbBase IV FLOAT values are
 * represented internally as floating point values. When parsed by Java they may have slight
 * rounding errors.
 *
 * @author Jan van Mansum
 * @author Vesa Åkerman
 */
public enum Type
{
    /**
     * Numeric value, mapped to a {@link Number} subclass.
     */
    NUMBER('N'),
    /**
     * Float value, mapped to a {@link Number} subclass.
     */
    FLOAT('F'), 
    /**
     * String values, mapped to {@link String}.
     */
    CHARACTER('C'), 
    /**
     * Logical, or boolean value, mapped to {@link Boolean}
     */
    LOGICAL('L', 1), 
    /**
     * Date value, mapped to <code>java.util.Date</code>. Note that in xBase the date does
     * <em>not</em> have a time component. The time related fields of <code>java.util.Date</code>
     * are therefore set to 0.
     */
    DATE('D', 8), 
    /**
     * A String value (without length limitations), mapped to {@link String}
     */
    MEMO('M', 10), 
    /**
     * A binary value (without length limitations), mapped to <code>byte[]</code>.
     */
    GENERAL('G', 10), 
    /**
     * A binary value (without length limitations), mapped to <code>byte[]</code>.
     */
    PICTURE('P', 10), 
    /**
     * A binary value (without length limitations), mapped to <code>byte[]</code>.
     */
    BINARY('B', 10);
    // Fields
    private static final Map<Character, Type> typeMap = new HashMap<Character, Type>();
    private final char code;
    private final int length;

    static
    {
        /*
         * Maps the type characters from the .DBF file to type enum constants.
         */
        for (Type type : Type.values())
        {
            typeMap.put(type.getCode(),
                        type);
        }
    }

    Type(final char code)
    {
        this(code, -1);
    }

    Type(final char code, final int length)
    {
        this.code = code;
        this.length = length;
    }

    char getCode()
    {
        return code;
    }

    static Type getTypeByCode(final char code)
    {
        return typeMap.get(code);
    }

    int getLength()
    {
        return length;
    }
}
