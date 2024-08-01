package model;/*
 *model
 *Dell
 *2024 2024/7/23 17:42
 */

public class AbcClassTag {
    public static final short NOTHING = 0x00; // 数量：1 格式：none 拥有此标记的TaggedValue，是其所在class_data的最后一项。
    public static final short SOURCE_LANG = 0x02;// 数量：0-1	格式：uint8_t	拥有此标记的TaggedValue的data是0，表示源码语言是ArkTS/TS/JS。
    public static final short SOURCE_FILE = 0x07;// 数量：0-1	格式：uint32_t	拥有此标记的TaggedValue的data是一个偏移量，指向字符串，表示源文件的名称。

}
