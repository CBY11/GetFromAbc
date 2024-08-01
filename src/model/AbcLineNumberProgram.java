package model;/*
 *model
 *Dell
 *2024 2024/7/25 17:31
 */

public class AbcLineNumberProgram {


    public static final byte END_SEQUENCE = 0x00;
    public static final byte ADVANCE_PC = 0x01;
    public static final byte ADVANCE_LINE = 0x02;
    public static final byte START_LOCAL = 0x03;
    public static final byte START_LOCAL_EXTENDED = 0x04;
    public static final byte END_LOCAL = 0x05;
    public static final byte SET_FILE = 0x09;
    public static final byte SET_SOURCE_CODE = 0x0a;
    public static final byte SET_COLUMN = 0x0b;
    public static final byte SPECIAL_OPCODE = 0x0c;


    public byte opcode;

    public long opParam;

    public long constPollParam1;
    public long constPollParam2;
    public long constPollParam3;


    public long source_idx;
    public String source_code;

}


