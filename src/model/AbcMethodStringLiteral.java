package model;/*
 *model
 *Dell
 *2024 2024/7/25 11:20
 */

public class AbcMethodStringLiteral {

    public static final int TYPE_METHOD = 1;
    public static final int TYPE_STRING = 2;
    public static final int TYPE_LITERAL = 3;


    public long offset;
    public int type;
    public String val_string;
    public AbcMethod val_method;
    public AbcLiteralArray val_literal;
}
