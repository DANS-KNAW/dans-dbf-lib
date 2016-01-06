package nl.knaw.dans.common.dbflib;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;

public class TestBugPack
{
    @Test
    public void deleteAndPack()
                       throws Exception
    {
        UnitTestUtil.copyFile(new File("src/test/resources/bug_pack/test.DBF"),
                              new File("target/bug_pack"),
                              "test.DBF");

        Table table = new Table(new File("target/bug_pack/test.DBF"));

        try
        {
            table.open();
            table.deleteRecordAt(0);
            table.pack();

            Record r0 = table.getRecordAt(0);
            Record r1 = table.getRecordAt(1);
            assertEquals("0",
                         r0.getStringValue("NAZWA"));
            assertEquals("1",
                         r1.getStringValue("NAZWA"));
        }
        finally
        {
            table.close();
        }
    }
}
