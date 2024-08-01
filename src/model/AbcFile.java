package model;/*
 *model
 *Dell
 *2024 2024/7/23 16:51
 */

public class AbcFile {
    public byte[] content;
    public AbcHeader abcHeader;
    public AbcForeign[] abcForeigns;
    public AbcClass[] abcClasses;
    public AbcIndexHeader[] abcIndexHeaders;
}
