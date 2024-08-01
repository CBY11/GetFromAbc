
import model.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Main {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        AbcFile abcFile = new AbcFile();
        String filePath = "F:\\Develop\\Java_code\\Huawei\\GetFromAbc\\batTest\\modules.abc";
        try {
            ReadUtil.parseAbcFile(filePath);
            abcFile = ReadUtil.abcFile;
            filePath += "";
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try {
        //     byte[] fileBytes = ReadUtil.readFileToByteArray(filePath);
        //     abcFile.content = fileBytes;
        //     System.out.println("Successfully read " + fileBytes.length + " bytes from file.");
        //     abcFile.abcHeader = ReadUtil.parseAbcHeader(fileBytes);
        //     System.out.println(abcFile.abcHeader);
        //
        //     // AbcLineNumberProgram[] lineNumberPrograms = ReadUtil.parseAbcLineNumberPrograms(fileBytes, abcFile.abcHeader);
        //
        //
        //     AbcClass[] abcClasses = ReadUtil.parseAbcClasses(fileBytes, abcFile.abcHeader);
        //     abcFile.abcClasses = abcClasses;
        //     for (AbcClass abcClass : abcClasses) {
        //         System.out.println(abcClass);
        //     }
        //
        //     AbcForeign[] abcForeigns = ReadUtil.parseAbcForeigns(fileBytes, abcFile.abcHeader);
        //     abcFile.abcForeigns = abcForeigns;
        //     for (AbcForeign abcForeign : abcForeigns) {
        //         System.out.println(abcForeign);
        //
        //     }
        //
        //
        //
        //     AbcIndexHeader[] indexHeaders = ReadUtil.parseAbcIndexHeaders(fileBytes, abcFile.abcHeader);
        //     abcFile.abcIndexHeaders = indexHeaders;
        //     for (AbcIndexHeader indexHeader : indexHeaders) {
        //         System.out.println(indexHeader);
        //     }
        //
        //
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}

