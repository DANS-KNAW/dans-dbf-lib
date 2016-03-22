dans-dbf-lib
============

DANS DBF Library - Java classes to read, write and update DBF database files.

DESCRIPTION
===========

DANS DBF is a Java library for reading, writing and updating
the contents of old [DBF] \(dBase) databases.
It was produced for the [MIXED] project.
It is no longer maintained. You are however free to fork it, if you wish. 

DANS DBF requires Java 1.5, or later, and has no dependencies.

The following program demonstrates how the library is used.

```java
import nl.knaw.dans.common.dbflib.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class DansDemo
{
  public static void main(String []args)
  {
    try
    {
      Table table = new Table(new File("/tmp", "cars.dbf"));
      table.open(IfNonExistent.ERROR);
      List<Field> fields = table.getFields();
      Iterator<Record> it = table.recordIterator();
      while (it.hasNext())
      {
        Record record = it.next();
        for (Field field: fields)
        {
          System.out.print(field.getName());
          System.out.print(": ");
          System.out.print(field.getType());
          System.out.print(": ");
          System.out.println(record.getTypedValue(field.getName()));
        }
      }
      table.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
```

The project does contain configuration for building a Maven site. However, it is currently broken. At sourceforge
there [may still be a version of this site](http://dans-dbf-lib.sourceforge.net/).


[DBF]: https://en.wikipedia.org/wiki/DBase
[MIXED]: http://www.dans.knaw.nl/en/projects/mixed
