import model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ReadUtil {

    public static AbcFile abcFile = null;

    public static AbcClass[] parsedAbcClasses = null;
    public static long[] parsedLineNumberProgramIndex = null;
    public static AbcLineNumberProgram[] parsedLineNumberPrograms = null;

    public static final long INT2UINT32_SHIFT_NUM = 0x0FFFFFFFFL;
    public static final int INT2UINT16_SHIFT_NUM = 0XFF;

    public static ByteBuffer abcFileBytesBuffer = null;



    public static AbcFile parseAbcFile(String filePath) throws IOException {
        abcFile = new AbcFile();
        byte[] fileBytes = ReadUtil.readFileToByteArray(filePath);
        abcFileBytesBuffer = ByteBuffer.wrap(fileBytes);
        abcFileBytesBuffer.order(ByteOrder.LITTLE_ENDIAN);

        abcFile.content = fileBytes;
        abcFile.abcHeader = ReadUtil.parseAbcHeader(fileBytes);
        abcFile.abcClasses = ReadUtil.parseAbcClasses(fileBytes, abcFile.abcHeader);
        abcFile.abcForeigns = ReadUtil.parseAbcForeigns(fileBytes, abcFile.abcHeader);
        abcFile.abcIndexHeaders = ReadUtil.parseAbcIndexHeaders(fileBytes, abcFile.abcHeader);
        return abcFile;
    }

    public static String getStringByIndex(int index){
        AbcMethodStringLiteral[] offsets = abcFile.abcIndexHeaders[0].method_string_literals;
        // 多次尝试之后发现abcIndexHeaders内元素数量总是为1
        if(index>=abcFile.abcIndexHeaders[0].method_string_literal_region_idx_size || index < 0){
            return null; // index超出索引范围
        }else {
            long offset = offsets[index].offset;
            try {
                return getString(abcFileBytesBuffer, (int) offset);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public static byte[] readFileToByteArray(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        byte[] bytes = new byte[(int) fileSize];

        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            int offset = 0;
            int bytesRead;
            while (offset < bytes.length && (bytesRead = inputStream.read(bytes, offset, bytes.length - offset)) != -1) {
                offset += bytesRead;
            }
        }

        return bytes;
    }

    public static AbcHeader parseAbcHeader(byte[] bytes) {
        AbcHeader abcHeader = new AbcHeader();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节顺序，根据具体文件格式可能需要调整

        // 解析字段
        byte[] magicBytes = new byte[8];
        buffer.get(magicBytes, 0, 8);
        abcHeader.magic = new String(magicBytes); // 读取8个字节的文件头魔数
        long checksum = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的校验和
        abcHeader.version = String.format("%d.%d.%d.%d",
                buffer.get() & INT2UINT16_SHIFT_NUM,
                buffer.get() & INT2UINT16_SHIFT_NUM,
                buffer.get() & INT2UINT16_SHIFT_NUM,
                buffer.get() & INT2UINT16_SHIFT_NUM); // 读取4个字节的版本号

        abcHeader.fileSize = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的文件大小
        abcHeader.foreign_off = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的外部区域偏移量
        abcHeader.foreign_size = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的外部区域大小
        abcHeader.num_classes = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 ClassIndex 元素数量
        abcHeader.class_idx_off = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 ClassIndex 偏移量
        abcHeader.num_lnps = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 LineNumberProgramIndex 元素数量
        abcHeader.lnp_idx_off = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 LineNumberProgramIndex 偏移量
        abcHeader.reserved1 = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的保留字段1
        abcHeader.reserved2 = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的保留字段2
        abcHeader.num_index_regions = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 IndexSection 元素数量
        abcHeader.index_section_off = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 IndexSection 偏移量

        return abcHeader;
    }

    public static AbcLineNumberProgram[] parseAbcLineNumberPrograms(byte[] bytes, AbcHeader abcHeader) {
        AbcLineNumberProgram[] abcLineNumberPrograms = new AbcLineNumberProgram[(int) abcHeader.num_lnps];
        long[] lineNumberProgramIndex = new long[(int) abcHeader.num_lnps];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节顺序，根据具体文件格式可能需要调整

        // 解析 LineNumberProgramIndex 区域
        buffer.position((int) abcHeader.lnp_idx_off);
        for (int i = 0; i < lineNumberProgramIndex.length; i++) {
            lineNumberProgramIndex[i] = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 LineNumberProgramIndex 元素
        }

        abcHeader.lineNumberProgramIndex = lineNumberProgramIndex;
        parsedLineNumberProgramIndex = lineNumberProgramIndex;

        for (int i = 0; i < lineNumberProgramIndex.length; i++) {
            buffer.position((int) lineNumberProgramIndex[i]);
            AbcLineNumberProgram abcLineNumberProgram = new AbcLineNumberProgram();
            abcLineNumberProgram.opcode =  buffer.get(); // 读取1字节的操作码
            if (abcLineNumberProgram.opcode == AbcLineNumberProgram.END_SEQUENCE) {
                ;
            }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.ADVANCE_PC) {
                // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
            }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.ADVANCE_LINE) {
                // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
            }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.START_LOCAL) {
                abcLineNumberProgram.opParam = readULEB128(buffer);
                // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
                // abcLineNumberProgram.constPollParam2 = readULEB128(buffer);
            }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.START_LOCAL_EXTENDED) {
                abcLineNumberProgram.opParam = readULEB128(buffer);
                // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
                // abcLineNumberProgram.constPollParam2 = readULEB128(buffer);
                // abcLineNumberProgram.constPollParam3 = readULEB128(buffer);
            }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.END_LOCAL){
                abcLineNumberProgram.opParam = readULEB128(buffer);
            }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.SET_FILE) {
                // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
            }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.SET_SOURCE_CODE){
                // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
                // abcLineNumberProgram.source_idx = abcLineNumberProgram.constPollParam1;
                // abcLineNumberProgram.source_code = getString(buffer, (int) abcLineNumberProgram.source_idx);
            } else if(abcLineNumberProgram.opcode == AbcLineNumberProgram.SET_COLUMN){
                // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
            }else {
                ;
            }
            abcLineNumberPrograms[i] = abcLineNumberProgram;

        }
        parsedLineNumberPrograms = abcLineNumberPrograms;
        return abcLineNumberPrograms;
    }

    public static AbcIndexHeader[] parseAbcIndexHeaders(byte[] bytes, AbcHeader abcHeader) {
        AbcIndexHeader[] abcIndexHeaders = new AbcIndexHeader[(int) abcHeader.num_index_regions];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节顺序，根据具体文件格式可能需要调整

        // 解析 IndexSection 区域
        buffer.position((int) abcHeader.index_section_off);
        for (int i = 0; i < abcIndexHeaders.length; i++) {
            AbcIndexHeader abcIndexHeader = new AbcIndexHeader();
            abcIndexHeader.start_off = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的起始偏移量
            abcIndexHeader.end_off = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的结束偏移量
            abcIndexHeader.class_region_idx_size = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 ClassIndex 区域大小
            abcIndexHeader.class_region_idx_off = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 ClassIndex 区域偏移量
            abcIndexHeader.types = getAbcTypes(bytes, abcIndexHeader);

            abcIndexHeader.method_string_literal_region_idx_size = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 MethodStringLiteralIndex 区域大小
            abcIndexHeader.method_string_literal_region_idx_off = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 MethodStringLiteralIndex 区域偏移量
            abcIndexHeader.method_string_literals = getMethodStringLiterals(bytes, abcIndexHeader);
            abcIndexHeader.reserved1 = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的保留字段1
            abcIndexHeader.reserved2 = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的保留字段2
            abcIndexHeader.reserved3 = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的保留字段3
            abcIndexHeader.reserved4 = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的保留字段4

            abcIndexHeaders[i] = abcIndexHeader;
        }

        return abcIndexHeaders;
    }

    public static AbcMethodStringLiteral[] getMethodStringLiterals(byte[] bytes, AbcIndexHeader abcIndexHeader) {
        AbcMethodStringLiteral[] methodStringLiterals = new AbcMethodStringLiteral[(int) abcIndexHeader.method_string_literal_region_idx_size];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节顺序，根据具体文件格式可能需要调整

        // 解析 MethodStringLiteralIndex 区域
        buffer.position((int) abcIndexHeader.method_string_literal_region_idx_off);
        for (int i = 0; i < methodStringLiterals.length; i++) {
            AbcMethodStringLiteral methodStringLiteral = new AbcMethodStringLiteral();
            methodStringLiteral.offset = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的偏移量


            methodStringLiterals[i] = methodStringLiteral;
        }
        return methodStringLiterals;
    }

    public static AbcType[] getAbcTypes(byte[] bytes, AbcIndexHeader abcIndexHeader) {
        AbcType[] abcTypes = new AbcType[(int) abcIndexHeader.class_region_idx_size];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节顺序，根据具体文件格式可能需要调整

        // 解析 ClassIndex 区域
        buffer.position((int) abcIndexHeader.class_region_idx_off);
        for (int i = 0; i < abcTypes.length; i++) {
            AbcType abcType = new AbcType();

            abcType.code_offset = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的编码或偏移量
            if (abcType.code_offset > 0x0C) {
                abcType.isBase = false;
                abcType.abcClass = findAbcClass((int) abcType.code_offset);
            }

            abcTypes[i] = abcType;
        }
        return abcTypes;
    }

    public static AbcClass findAbcClass(int offset) {
        if (parsedAbcClasses == null) {
            return null;
        } else {
            for (AbcClass abcClass : parsedAbcClasses) {
                if (abcClass.offset == offset) {
                    return abcClass;
                }
            }
            return null;
        }
    }

    public static AbcForeign[] parseAbcForeigns(byte[] bytes, AbcHeader abcHeader) {
        ArrayList<AbcForeign> abcForeigns = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节顺序，根据具体文件格式可能需要调整
        int size = (int) abcHeader.foreign_size;
        // 解析外部区域
        buffer.position((int) abcHeader.foreign_off);

        String name = getString(buffer, buffer.position());
        byte tag1 = buffer.get();
        byte tag2 = buffer.get();
        byte tag3 = buffer.get();
        byte tag4 = buffer.get();
        byte tag5 = buffer.get();
        byte tag6 = buffer.get();

        return abcForeigns.toArray(new AbcForeign[0]);
    }

    public static AbcClass[] parseAbcClasses(byte[] bytes, AbcHeader abcHeader) {
        AbcClass[] abcClasses = new AbcClass[(int) abcHeader.num_classes];
        long[] offsets = new long[(int) abcHeader.num_classes];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节顺序，根据具体文件格式可能需要调整

        // 解析 ClassIndex 区域
        buffer.position((int) abcHeader.class_idx_off);
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的 ClassIndex 偏移量
        }
        for (int i = 0; i < offsets.length; i++) {
            buffer.position((int) offsets[i]);
            AbcClass abcClass = new AbcClass();
            abcClass.offset = offsets[i];
            abcClass.name = getString(buffer, buffer.position());
            while (buffer.get() != 0)
                continue;// 跳过类名字符串的结尾0
            abcClass.reserved = buffer.getInt() & INT2UINT32_SHIFT_NUM; // 读取4字节的保留字段
            abcClass.access_flags = readULEB128(buffer);
            abcClass.num_fields = readULEB128(buffer);
            abcClass.num_methods = readULEB128(buffer);
            abcClass.class_data = getClassData(buffer, abcClass);
            abcClass.fields = getAbcFields(buffer, (int) abcClass.num_fields);
            abcClass.methods = getAbcMethods(buffer, (int) abcClass.num_methods);

            abcClasses[i] = abcClass;
        }
        ReadUtil.parsedAbcClasses = abcClasses;
        return abcClasses;
    }

    public static AbcMethod[] getAbcMethods(ByteBuffer buffer, int num_methods) {
        AbcMethod[] abcMethods = new AbcMethod[num_methods];
        int tmpPos;
        for (int i = 0; i < num_methods; i++) {
            tmpPos = buffer.position();
            AbcMethod abcMethod = new AbcMethod();
            abcMethod.class_idx = buffer.getShort() & INT2UINT16_SHIFT_NUM;
            abcMethod.reserved = buffer.getShort() & INT2UINT16_SHIFT_NUM;
            abcMethod.name_off = buffer.getInt() & INT2UINT32_SHIFT_NUM;
            abcMethod.name = getString(buffer, (int) abcMethod.name_off);
            if(abcMethod.name.contains("@") || abcMethod.name.contains("^"))
                System.out.println(abcMethod.name);
            byte[] tmpBytes = readSLEB128Bytes(buffer);
            abcMethod.index_data = new byte[4];
            for (int i1 = 0; i1 < abcMethod.index_data.length; i1++) {
                if(i1>=tmpBytes.length){
                    abcMethod.index_data[i1] = 0x0;
                }else {
                    abcMethod.index_data[i1] = tmpBytes[i1];
                }
            }

            ByteBuffer tmpByteBuffer = ByteBuffer.wrap(abcMethod.index_data);
            tmpByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            abcMethod.header_index = tmpByteBuffer.getShort() & INT2UINT16_SHIFT_NUM;
            abcMethod.function_kind = tmpByteBuffer.get();
            abcMethod.index_data_reserved = tmpByteBuffer.get();
            abcMethod.method_data = getMethodData(buffer, abcMethod);
            abcMethods[i] = abcMethod;
            // System.out.printf("[method: offset:16jinzhi 0x%08x  10jinzhi:   %08d, name:%s]\n", tmpPos, tmpPos, abcMethod.name );
        }
        return abcMethods;
    }

    public static AbcTaggedValue[] getMethodData(ByteBuffer buffer, AbcMethod abcMethod) {
        ArrayList<AbcTaggedValue> taggedValues = new ArrayList<>();
        ArrayList<AbcAnnotation> annotations = new ArrayList<>();
        byte tag;
        do {
            tag = buffer.get();
            AbcTaggedValue taggedValue = new AbcTaggedValue();
            taggedValue.tag = tag;
            if (tag == AbcMethodTag.CODE) {
                byte[] tmpBytes = new byte[4];
                tmpBytes[0] = buffer.get();
                tmpBytes[1] = buffer.get();
                tmpBytes[2] = buffer.get();
                tmpBytes[3] = buffer.get();
                taggedValue.data = tmpBytes;
                abcMethod.code = getCode(buffer, taggedValue.data);
            } else if (tag == AbcMethodTag.SOURCE_LANG) {
                byte[] tmpBytes = new byte[1];
                tmpBytes[0] = buffer.get();
                abcMethod.src_lang = 0;
                taggedValue.data = tmpBytes;
            } else if (tag == AbcMethodTag.DEBUG_INFO) {
                byte[] tmpBytes = new byte[4];
                tmpBytes[0] = buffer.get();
                tmpBytes[1] = buffer.get();
                tmpBytes[2] = buffer.get();
                tmpBytes[3] = buffer.get();
                taggedValue.data = tmpBytes;
                abcMethod.debug_info = getDebugInfo(buffer, taggedValue.data);
            } else if (tag == AbcMethodTag.ANNOTATION) {
                byte[] tmpBytes = new byte[4];
                tmpBytes[0] = buffer.get();
                tmpBytes[1] = buffer.get();
                tmpBytes[2] = buffer.get();
                tmpBytes[3] = buffer.get();
                taggedValue.data = tmpBytes;
                annotations.add(getAnnotation(buffer, taggedValue.data));
            } else if (tag == AbcMethodTag.NOTHING) {
                taggedValue.data = null;
            }
            taggedValues.add(taggedValue);
        } while (tag != AbcMethodTag.NOTHING);
        abcMethod.abcAnnotations = annotations.toArray(new AbcAnnotation[0]);
        return taggedValues.toArray(new AbcTaggedValue[0]);
    }


    public static AbcAnnotation getAnnotation(ByteBuffer buffer, byte[] offset_bytes) {
        ByteBuffer tmpbuffer = ByteBuffer.wrap(offset_bytes);
        tmpbuffer.order(ByteOrder.LITTLE_ENDIAN);
        // 读取uint32_t类型数据
        long offset = tmpbuffer.getInt() & INT2UINT32_SHIFT_NUM;
        // 使用&运算符将int类型转换为无符号long
        int position = buffer.position();
        buffer.position((int) offset);
        AbcAnnotation abcAnnotation = new AbcAnnotation();

        buffer.position(position);
        return abcAnnotation;
    }

    public static AbcDebugInfo getDebugInfo(ByteBuffer buffer, byte[] offset_bytes) {
        ByteBuffer tmpbuffer = ByteBuffer.wrap(offset_bytes);
        tmpbuffer.order(ByteOrder.LITTLE_ENDIAN);
        // 读取uint32_t类型数据
        long offset = tmpbuffer.getInt() & INT2UINT32_SHIFT_NUM;
        // 使用&运算符将int类型转换为无符号long
        int position = buffer.position();
        buffer.position((int) offset);
        AbcDebugInfo abcDebugInfo = new AbcDebugInfo();
        abcDebugInfo.line_start = readULEB128(buffer);
        abcDebugInfo.num_parameters = readULEB128(buffer);
        long[] parameters = new long[(int) abcDebugInfo.num_parameters];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = readULEB128(buffer);
        }
        abcDebugInfo.parameters = parameters;
        abcDebugInfo.constant_pool_size = readULEB128(buffer);
        long[] constant_pool = new long[(int) abcDebugInfo.constant_pool_size];
        for (int i = 0; i < constant_pool.length; i++) {
            constant_pool[i] = readULEB128(buffer);
        }
        abcDebugInfo.constant_pool = constant_pool;
        abcDebugInfo.line_number_program_idx = readULEB128(buffer);
        // int constant_pool_ptr = 0;
        // ArrayList<AbcLineNumberProgram> methodDebugInfo_lnp_list = new ArrayList<>();
        // for (int i = (int) abcDebugInfo.line_number_program_idx; i < parsedLineNumberProgramIndex.length; i++) {
        //     AbcLineNumberProgram abcLineNumberProgram = parsedLineNumberPrograms[i];

        //     if (abcLineNumberProgram.opcode == AbcLineNumberProgram.END_SEQUENCE) {
        //         break;
        //     }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.ADVANCE_PC) {
        //         // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
        //         constant_pool_ptr++;
        //     }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.ADVANCE_LINE) {
        //         // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
        //         constant_pool_ptr++;
        //     }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.START_LOCAL) {
        //         abcLineNumberProgram.opParam = readULEB128(buffer);
        //         // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
        //         // abcLineNumberProgram.constPollParam2 = readULEB128(buffer);
        //         constant_pool_ptr++;
        //         constant_pool_ptr++;
        //     }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.START_LOCAL_EXTENDED) {
        //         abcLineNumberProgram.opParam = readULEB128(buffer);
        //         // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
        //         // abcLineNumberProgram.constPollParam2 = readULEB128(buffer);
        //         // abcLineNumberProgram.constPollParam3 = readULEB128(buffer);
        //         constant_pool_ptr++;
        //         constant_pool_ptr++;
        //         constant_pool_ptr++;
        //     }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.END_LOCAL){
        //         abcLineNumberProgram.opParam = readULEB128(buffer);
        //     }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.SET_FILE) {
        //         // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
        //         constant_pool_ptr++;
        //     }else if (abcLineNumberProgram.opcode == AbcLineNumberProgram.SET_SOURCE_CODE){
        //         // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
        //         abcLineNumberProgram.source_idx = constant_pool[constant_pool_ptr];
        //         abcLineNumberProgram.source_code = getString(buffer, (int) abcLineNumberProgram.source_idx);
        //         constant_pool_ptr++;
        //     } else if(abcLineNumberProgram.opcode == AbcLineNumberProgram.SET_COLUMN){
        //         // abcLineNumberProgram.constPollParam1 = readULEB128(buffer);
        //         constant_pool_ptr++;
        //     }else {
        //         ;
        //     }
        //     methodDebugInfo_lnp_list.add(abcLineNumberProgram);
        // }
        /*
         * 貌似不重要
         */
        buffer.position(position);
        return abcDebugInfo;
    }

    public static AbcCode getCode(ByteBuffer buffer, byte[] offset_bytes) {
        ByteBuffer tmpbuffer = ByteBuffer.wrap(offset_bytes);
        tmpbuffer.order(ByteOrder.LITTLE_ENDIAN);
        // 读取uint32_t类型数据
        long offset = tmpbuffer.getInt() & INT2UINT32_SHIFT_NUM;
        // 使用&运算符将int类型转换为无符号long
        int position = buffer.position();
        buffer.position((int) offset);
        AbcCode abcCode = new AbcCode();
        abcCode.num_vregs = readULEB128(buffer);
        abcCode.num_args = readULEB128(buffer);
        abcCode.code_size = readULEB128(buffer);
        abcCode.tries_size = readULEB128(buffer);
        abcCode.instructions = new byte[(int) abcCode.code_size];
        buffer.get(abcCode.instructions);
        abcCode.insns_code = new String(abcCode.instructions, 0, ((int) abcCode.code_size), StandardCharsets.UTF_8);
        abcCode.try_blocks = getTryBlocks(buffer, (int) abcCode.tries_size);

        buffer.position(position);
        return abcCode;
    }

    public static AbcTryBlock[] getTryBlocks(ByteBuffer buffer, int tries_size) {
        AbcTryBlock[] tryBlocks = new AbcTryBlock[tries_size];
        for (int i = 0; i < tries_size; i++) {
            AbcTryBlock tryBlock = new AbcTryBlock();
            tryBlock.start_pc = readULEB128(buffer);
            tryBlock.length = readULEB128(buffer);
            tryBlock.num_catches = readULEB128(buffer);
            tryBlock.catch_blocks = getCatchBlocks(buffer, (int) tryBlock.num_catches);
            tryBlocks[i] = tryBlock;
        }
        return tryBlocks;
    }

    public static AbcCatchBlock[] getCatchBlocks(ByteBuffer buffer, int num_catches) {
        AbcCatchBlock[] catchBlocks = new AbcCatchBlock[num_catches];
        for (int i = 0; i < num_catches; i++) {
            AbcCatchBlock catchBlock = new AbcCatchBlock();
            catchBlock.type_idx = readULEB128(buffer);
            catchBlock.handler_pc = readULEB128(buffer);
            catchBlock.code_size = readULEB128(buffer);
            catchBlocks[i] = catchBlock;
        }
        return catchBlocks;
    }

    public static AbcTaggedValue[] getClassData(ByteBuffer buffer, AbcClass abcClass) {
        ArrayList<AbcTaggedValue> taggedValues = new ArrayList<>();
        byte tag;
        do {
            tag = buffer.get();
            AbcTaggedValue taggedValue = new AbcTaggedValue();
            taggedValue.tag = tag;
            if (tag == AbcClassTag.SOURCE_LANG) {
                byte[] tmpBytes = new byte[1];
                tmpBytes[0] = buffer.get();
                taggedValue.data = tmpBytes;
                abcClass.src_lang = 0;
            } else if (tag == AbcClassTag.SOURCE_FILE) {
                byte[] tmpBytes = new byte[4];
                tmpBytes[0] = buffer.get();
                tmpBytes[1] = buffer.get();
                tmpBytes[2] = buffer.get();
                tmpBytes[3] = buffer.get();
                taggedValue.data = tmpBytes;
                abcClass.src_file_name = getString(buffer, taggedValue.data);
            } else if (tag == AbcClassTag.NOTHING) {
                taggedValue.data = null;
            }
            taggedValues.add(taggedValue);
        } while (tag != AbcClassTag.NOTHING);
        return taggedValues.toArray(new AbcTaggedValue[0]);
    }

    public static AbcField[] getAbcFields(ByteBuffer buffer, int num_fields) {
        AbcField[] abcFields = new AbcField[num_fields];
        for (int i = 0; i < num_fields; i++) {
            AbcField abcField = new AbcField();
            abcField.class_idx = buffer.getShort() & INT2UINT16_SHIFT_NUM;
            abcField.type_idx = buffer.getShort() & INT2UINT16_SHIFT_NUM;
            int tmp = buffer.getInt();
            abcField.name_off = tmp & INT2UINT32_SHIFT_NUM;
            abcField.name = getString(buffer, (int) abcField.name_off);
            abcField.reserved = readULEB128(buffer);
            abcField.field_data = getFieldData(buffer, abcField);
            abcFields[i] = abcField;
        }
        return abcFields;
    }

    public static AbcTaggedValue[] getFieldData(ByteBuffer buffer, AbcField abcField) {
        ArrayList<AbcTaggedValue> taggedValues = new ArrayList<>();
        byte tag;
        do {
            tag = buffer.get();
            AbcTaggedValue taggedValue = new AbcTaggedValue();
            taggedValue.tag = tag;
            if (tag == AbcFieldTag.INT_VALUE) {
                taggedValue.data = readSLEB128Bytes(buffer);
            } else if (tag == AbcFieldTag.VALUE) {
                byte[] tmpBytes = new byte[4];
                tmpBytes[0] = buffer.get();
                tmpBytes[1] = buffer.get();
                tmpBytes[2] = buffer.get();
                tmpBytes[3] = buffer.get();
                taggedValue.data = tmpBytes;
            } else if (tag == AbcFieldTag.NOTHING) {
                taggedValue.data = null;
            }
            taggedValues.add(taggedValue);
        } while (tag != AbcFieldTag.NOTHING);
        return taggedValues.toArray(new AbcTaggedValue[0]);
    }

    public static String getString(ByteBuffer buffer, int offset) {
        int position = buffer.position();
        buffer.position(offset);
        long utf16_length = readULEB128(buffer);
        int len = (int) (utf16_length >> 1);
        boolean is_ascii = utf16_length % 2 == 0;

        byte[] tmpBytes = new byte[1000];
        int i = 0;
        while (i <= len) {
            byte b = buffer.get();
            if (b == 0) {
                break;
            }
            tmpBytes[i] = b;
            i++;
        }
        buffer.position(position);
        try {
            String res = new String(tmpBytes, 0, len, is_ascii ? "ASCII" : "UTF-8");
            // System.out.printf("[offset:0x%08x, name:%s]\n", offset, res );
            return res;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getString(ByteBuffer buffer, byte[] bytes) {
        ByteBuffer tmpbuffer = ByteBuffer.wrap(bytes);
        tmpbuffer.order(ByteOrder.LITTLE_ENDIAN);
        // 读取uint32_t类型数据
        long unsignedInt = tmpbuffer.getInt() & INT2UINT32_SHIFT_NUM;
        // 使用&运算符将int类型转换为无符号long
        return getString(buffer, (int) unsignedInt);
    }

    public static long readULEB128(ByteBuffer buffer) {
        long result = 0;
        int shift = 0;
        byte byteRead;
        do {
            byteRead = buffer.get();
            result |= (long) (byteRead & 0x7F) << shift;
            shift += 7;
        } while ((byteRead & 0x80) != 0);
        return result;
    }

    public static byte[] readSLEB128Bytes(ByteBuffer buffer) {
        int result = 0;
        int shift = 0;
        byte byteRead;

        ArrayList<Byte> bytes = new ArrayList<>();
        do {
            byteRead = buffer.get();
            bytes.add(byteRead);
            result |= (byteRead & 0x7F) << shift;
            shift += 7;
        } while ((byteRead & 0x80) != 0);
        byte[] bytesArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            bytesArray[i] = bytes.get(i);
        }
        return bytesArray;
    }


}
